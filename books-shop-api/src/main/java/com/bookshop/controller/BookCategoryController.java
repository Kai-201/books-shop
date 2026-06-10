package com.bookshop.controller;

import com.bookshop.common.Result;
import com.bookshop.entity.BookCategory;
import com.bookshop.service.BookCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class BookCategoryController {

    private final BookCategoryService categoryService;

    public BookCategoryController(BookCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<BookCategory>> list() {
        return Result.ok(categoryService.listAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Void> create(@RequestBody Map<String, String> body) {
        categoryService.create(body.get("name"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
