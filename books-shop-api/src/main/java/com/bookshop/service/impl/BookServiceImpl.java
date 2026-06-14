package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.BusinessException;
import com.bookshop.dto.BookRequest;
import com.bookshop.dto.BookVO;
import com.bookshop.entity.Book;
import com.bookshop.entity.User;
import com.bookshop.dto.BookDocument;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.UserMapper;
import com.bookshop.service.BookSearchService;
import com.bookshop.service.BookService;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BookSearchService bookSearchService;
    /**
     * 布隆过滤器：缓存 id 是否存在，防止缓存穿透
     * 预计 10000 条数据，误判率 1%（误判时 DB 兜底）
     */
    private final BloomFilter<Integer> bloomFilter =
            BloomFilter.create(Funnels.integerFunnel(), 10000, 0.01);

    /**
     * 批量查询上传者信息，避免 N+1
     */
    private Map<Integer, User> loadUploaders(List<Book> books) {
        Set<Integer> uploaderIds = books.stream()
                .map(Book::getUploaderId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (uploaderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> users = userMapper.selectBatchIds(uploaderIds);  // 1 条 SQL
        return users.stream().collect(Collectors.toMap(User::getId, u -> u));
    }

    public BookServiceImpl(BookMapper bookMapper, UserMapper userMapper,
                            BookSearchService bookSearchService) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        this.bookSearchService = bookSearchService;
    }
//    初始化方法，启动时把现有图书 id 全部加载进去
    @PostConstruct
    public void initBloomFilter() {
        List<Book> allBooks = bookMapper.selectList(null);
        for (Book book : allBooks) {
            bloomFilter.put(book.getId());
        }
        log.info("布隆过滤器初始化完成，共加载 {} 条图书 id", allBooks.size());
    }
    @Override
    @Cacheable(cacheNames = "books", key = "'all'", sync = true)
    public List<BookVO> listAll() {
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().orderByDesc(Book::getId));
        Map<Integer, User> userMap = loadUploaders(books);
        return books.stream().map(b -> toVO(b, userMap)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "books", key = "'page:' + #page + ':' + #size + ':' + #keyword + ':' + #categoryId + ':' + #uploaderType", sync = true)
    public Page<BookVO> page(int page, int size, String keyword, Integer categoryId, String uploaderType) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getBookName, keyword)
                    .or().like(Book::getBookAuthor, keyword)
                    .or().like(Book::getBookSn, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        if ("admin".equalsIgnoreCase(uploaderType)) {
            wrapper.isNull(Book::getUploaderId);
        } else if ("user".equalsIgnoreCase(uploaderType)) {
            wrapper.isNotNull(Book::getUploaderId);
        }
        wrapper.orderByDesc(Book::getId);

        Page<Book> bookPage = bookMapper.selectPage(new Page<>(page, size), wrapper);
        Page<BookVO> voPage = new Page<>(page, size, bookPage.getTotal());
        Map<Integer, User> userMap = loadUploaders(bookPage.getRecords());
        voPage.setRecords(bookPage.getRecords().stream()
                .map(b -> toVO(b, userMap)).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Cacheable(cacheNames = "books", key = "'uploader:' + #uploaderId", sync = true)
    public List<BookVO> listByUploader(Integer uploaderId) {
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>()
                        .eq(Book::getUploaderId, uploaderId)
                        .orderByDesc(Book::getId));
        Map<Integer, User> userMap = loadUploaders(books);
        return books.stream().map(b -> toVO(b, userMap)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "book", key = "#id", sync = true)
    public BookVO getById(Integer id) {
        // 布隆拦截：id 绝对不存在，直接返回，不查 DB
        if (!bloomFilter.mightContain(id)) {
            return null;
        }
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return null;  // ← 返回 null，Spring 会把 null 也缓存
        }
        return toVO(book, loadUploaders(Collections.singletonList(book)));
    }

    @Override
    @CacheEvict(cacheNames = {"books", "book"}, allEntries = true)
    public void create(BookRequest request, Integer uploaderId) {
        Book book = new Book();
        copyProperties(request, book);
        book.setUploaderId(uploaderId);
        if (!StringUtils.hasText(book.getBookAuthor())) {
            book.setBookAuthor("未知作者");
        }
        bookMapper.insert(book);
        bloomFilter.put(book.getId());
        // 同步到 ES
        bookSearchService.sync(new BookDocument(book, uploaderId == null ? "管理员" : null));
    }

    @Override
    @CacheEvict(cacheNames = {"books", "book"}, allEntries = true)
    public void update(BookRequest request, Integer operatorId, String role) {
        if (request.getId() == null) {
            throw new BusinessException("图书ID不能为空");
        }
        Book existing = bookMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException("图书不存在");
        }
        checkPermission(existing, operatorId, role);

        Book book = new Book();
        copyProperties(request, book);
        book.setId(request.getId());
        book.setUploaderId(existing.getUploaderId());
        bookMapper.updateById(book);
        // 同步到 ES
        Book updated = bookMapper.selectById(book.getId());
        if (updated != null) {
            bookSearchService.sync(new BookDocument(updated,
                    updated.getUploaderId() == null ? "管理员" : null));
        }
    }

    @Override
    @CacheEvict(cacheNames = {"books", "book"}, allEntries = true)
    public void delete(Integer id, Integer operatorId, String role) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("图书不存在");
        }
        checkPermission(existing, operatorId, role);
        bookMapper.deleteById(id);
        // 从 ES 删除
        bookSearchService.remove(id);
    }

    private void checkPermission(Book book, Integer operatorId, String role) {
        if ("admin".equals(role)) {
            return;
        }
        if (book.getUploaderId() == null || !book.getUploaderId().equals(operatorId)) {
            throw new BusinessException(403, "无权操作该图书");
        }
    }

    private BookVO toVO(Book book,Map<Integer, User> userMap) {
        BookVO vo = new BookVO();
        vo.setId(book.getId());
        vo.setBookSn(book.getBookSn());
        vo.setBookName(book.getBookName());
        vo.setBookAuthor(book.getBookAuthor());
        vo.setCategoryId(book.getCategoryId());
        vo.setBookPrice(book.getBookPrice());
        vo.setBooksNum(book.getBooksNum());
        vo.setBookCover(book.getBookCover());
        vo.setUploaderId(book.getUploaderId());
        vo.setCreateTime(book.getCreateTime());

        if (book.getUploaderId() == null) {
            vo.setUploaderType("admin");
            vo.setUploaderName("管理员");
        } else {
            vo.setUploaderType("user");
            User user = userMap.get(book.getUploaderId());
            vo.setUploaderName(user != null ? user.getUsername() : "用户" + book.getUploaderId());
        }
        return vo;
    }
    @Override
    public boolean deductStock(Integer bookId, int quantity) {
        // 最多重试 3 次
        for (int retry = 0; retry < 3; retry++) {
            // ① 读：查当前库存和版本号（不加锁）
            Book book = bookMapper.selectById(bookId);
            if (book == null || book.getBooksNum() < quantity) {
                return false;
            }
            
            // ② 写：改库存，@Version 自动拼 WHERE version = 旧值
            book.setBooksNum(book.getBooksNum() - quantity);
            int rows = bookMapper.updateById(book);
            
            if (rows > 0) {
                return true;  // 成功
            }
            // ③ 冲突：版本号变了，重试
        }
        return false;  // 重试耗尽
    }
    private void copyProperties(BookRequest request, Book book) {
        book.setBookSn(request.getBookSn());
        book.setBookName(request.getBookName());
        book.setBookAuthor(request.getBookAuthor());
        book.setCategoryId(request.getCategoryId());
        book.setBookPrice(request.getBookPrice());
        book.setBooksNum(request.getBooksNum());
        book.setBookCover(request.getBookCover());
    }

}
