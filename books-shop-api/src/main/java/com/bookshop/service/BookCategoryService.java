package com.bookshop.service;

import com.bookshop.entity.BookCategory;

import java.util.List;

public interface BookCategoryService {

    List<BookCategory> listAll();

    void create(String name);

    void delete(Integer id);
}
