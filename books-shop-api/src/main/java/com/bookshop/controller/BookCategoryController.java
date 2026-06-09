package com.bookshop.controller;

import com.bookshop.common.Result;
import com.bookshop.entity.BookCategory;
import com.bookshop.service.BookCategoryService;
import com.bookshop.util.AuthHelper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public Result<Void> create(HttpServletRequest request, @RequestBody Map<String, String> body) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        categoryService.create(body.get("name"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Integer id) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        categoryService.delete(id);
        return Result.ok();
    }
}
