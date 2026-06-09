package com.bookshop.controller;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.bookshop.common.LoginUser;

import com.bookshop.common.Result;

import com.bookshop.dto.BookRequest;

import com.bookshop.dto.BookVO;

import com.bookshop.service.BookService;

import com.bookshop.util.AuthHelper;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;



import javax.servlet.http.HttpServletRequest;

import java.util.List;



@RestController

@RequestMapping("/books")

public class BookController {



    private final BookService bookService;



    public BookController(BookService bookService) {

        this.bookService = bookService;

    }



    @GetMapping

    public Result<List<BookVO>> list() {

        return Result.ok(bookService.listAll());

    }



    @GetMapping("/page")

    public Result<Page<BookVO>> page(@RequestParam(defaultValue = "1") int page,

                                     @RequestParam(defaultValue = "10") int size,

                                     @RequestParam(required = false) String keyword,

                                     @RequestParam(required = false) Integer categoryId,

                                     @RequestParam(required = false) String uploaderType) {

        return Result.ok(bookService.page(page, size, keyword, categoryId, uploaderType));

    }



    @GetMapping("/my")

    public Result<List<BookVO>> myBooks(HttpServletRequest request) {

        LoginUser user = AuthHelper.currentUser(request);

        AuthHelper.requireUser(user);

        return Result.ok(bookService.listByUploader(user.getId()));

    }



    @GetMapping("/{id}")

    public Result<BookVO> detail(@PathVariable Integer id) {

        return Result.ok(bookService.getById(id));

    }



    @PostMapping

    public Result<Void> create(HttpServletRequest request, @Validated @RequestBody BookRequest body) {

        LoginUser user = AuthHelper.currentUser(request);

        Integer uploaderId = "admin".equals(user.getRole()) ? null : user.getId();

        bookService.create(body, uploaderId);

        return Result.ok();

    }



    @PutMapping("/{id}")

    public Result<Void> update(HttpServletRequest request, @PathVariable Integer id,

                               @Validated @RequestBody BookRequest body) {

        LoginUser user = AuthHelper.currentUser(request);

        body.setId(id);

        bookService.update(body, user.getId(), user.getRole());

        return Result.ok();

    }



    @DeleteMapping("/{id}")

    public Result<Void> delete(HttpServletRequest request, @PathVariable Integer id) {

        LoginUser user = AuthHelper.currentUser(request);

        bookService.delete(id, user.getId(), user.getRole());

        return Result.ok();

    }

}

