package com.bookshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.BookRequest;
import com.bookshop.dto.BookVO;
import com.bookshop.security.SecurityUtils;
import com.bookshop.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
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
    @PreAuthorize("hasRole('user')")
    public Result<List<BookVO>> myBooks() {
        LoginUser user = SecurityUtils.getCurrentUser();
        return Result.ok(bookService.listByUploader(user.getId()));
    }

    @GetMapping("/{id}")
    public Result<BookVO> detail(@PathVariable Integer id) {
        return Result.ok(bookService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public Result<Void> create(@Validated @RequestBody BookRequest body) {
        LoginUser user = SecurityUtils.getCurrentUser();
        Integer uploaderId = "admin".equals(user.getRole()) ? null : user.getId();
        bookService.create(body, uploaderId);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public Result<Void> update(@PathVariable Integer id,
                               @Validated @RequestBody BookRequest body) {
        LoginUser user = SecurityUtils.getCurrentUser();
        body.setId(id);
        bookService.update(body, user.getId(), user.getRole());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public Result<Void> delete(@PathVariable Integer id) {
        LoginUser user = SecurityUtils.getCurrentUser();
        bookService.delete(id, user.getId(), user.getRole());
        log.info("删除图书: bookId={}, operator={}, role={}",id, user.getLoginName(), user.getRole());
        return Result.ok();
    }
}
