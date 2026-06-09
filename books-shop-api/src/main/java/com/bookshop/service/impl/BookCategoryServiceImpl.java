package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookshop.common.BusinessException;
import com.bookshop.entity.Book;
import com.bookshop.entity.BookCategory;
import com.bookshop.mapper.BookCategoryMapper;
import com.bookshop.mapper.BookMapper;
import com.bookshop.service.BookCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    public BookCategoryServiceImpl(BookCategoryMapper categoryMapper, BookMapper bookMapper) {
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public List<BookCategory> listAll() {
        return categoryMapper.selectList(new LambdaQueryWrapper<BookCategory>().orderByAsc(BookCategory::getId));
    }

    @Override
    public void create(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("分类名称不能为空");
        }
        Long count = categoryMapper.selectCount(new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getName, name));
        if (count > 0) {
            throw new BusinessException("分类已存在");
        }
        BookCategory category = new BookCategory();
        category.setName(name);
        categoryMapper.insert(category);
    }

    @Override
    public void delete(Integer id) {
        Long bookCount = bookMapper.selectCount(new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, id));
        if (bookCount > 0) {
            throw new BusinessException("该分类下仍有图书，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
