package com.bookshop.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.bookshop.common.BusinessException;

import com.bookshop.dto.BookRequest;

import com.bookshop.dto.BookVO;

import com.bookshop.entity.Book;

import com.bookshop.entity.User;

import com.bookshop.mapper.BookMapper;

import com.bookshop.mapper.UserMapper;

import com.bookshop.service.BookService;

import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;



import java.util.List;

import java.util.stream.Collectors;



@Service

public class BookServiceImpl implements BookService {



    private final BookMapper bookMapper;

    private final UserMapper userMapper;



    public BookServiceImpl(BookMapper bookMapper, UserMapper userMapper) {

        this.bookMapper = bookMapper;

        this.userMapper = userMapper;

    }



    @Override

    public List<BookVO> listAll() {

        return bookMapper.selectList(new LambdaQueryWrapper<Book>().orderByDesc(Book::getId))

                .stream().map(this::toVO).collect(Collectors.toList());

    }



    @Override

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

        voPage.setRecords(bookPage.getRecords().stream().map(this::toVO).collect(Collectors.toList()));

        return voPage;

    }



    @Override

    public List<BookVO> listByUploader(Integer uploaderId) {

        return bookMapper.selectList(new LambdaQueryWrapper<Book>()

                        .eq(Book::getUploaderId, uploaderId)

                        .orderByDesc(Book::getId))

                .stream().map(this::toVO).collect(Collectors.toList());

    }



    @Override

    public BookVO getById(Integer id) {

        Book book = bookMapper.selectById(id);

        if (book == null) {

            throw new BusinessException("图书不存在");

        }

        return toVO(book);

    }



    @Override

    public void create(BookRequest request, Integer uploaderId) {

        Book book = new Book();

        copyProperties(request, book);

        book.setUploaderId(uploaderId);

        if (!StringUtils.hasText(book.getBookAuthor())) {

            book.setBookAuthor("未知作者");

        }

        bookMapper.insert(book);

    }



    @Override

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

    }



    @Override

    public void delete(Integer id, Integer operatorId, String role) {

        Book existing = bookMapper.selectById(id);

        if (existing == null) {

            throw new BusinessException("图书不存在");

        }

        checkPermission(existing, operatorId, role);

        bookMapper.deleteById(id);

    }



    private void checkPermission(Book book, Integer operatorId, String role) {

        if ("admin".equals(role)) {

            return;

        }

        if (book.getUploaderId() == null || !book.getUploaderId().equals(operatorId)) {

            throw new BusinessException(403, "无权操作该图书");

        }

    }



    private BookVO toVO(Book book) {

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

            User user = userMapper.selectById(book.getUploaderId());

            vo.setUploaderName(user != null ? user.getUsername() : "用户" + book.getUploaderId());

        }

        return vo;

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

