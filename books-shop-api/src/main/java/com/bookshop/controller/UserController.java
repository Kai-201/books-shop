package com.bookshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.UserRequest;
import com.bookshop.entity.User;
import com.bookshop.service.UserService;
import com.bookshop.util.AuthHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<User> me(HttpServletRequest request) {
        LoginUser loginUser = AuthHelper.currentUser(request);
        return Result.ok(userService.getById(loginUser.getId()));
    }

    @GetMapping
    public Result<List<User>> list(HttpServletRequest request) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        List<User> users = userService.listAll();
        users.forEach(u -> u.setPassword(null));
        return Result.ok(users);
    }

    @GetMapping("/page")
    public Result<Page<User>> page(HttpServletRequest request,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        Page<User> result = userService.page(page, size, keyword);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(result);
    }

    @GetMapping("/{id}")
    public Result<User> detail(HttpServletRequest request, @PathVariable Integer id) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    public Result<Void> create(HttpServletRequest request, @Validated @RequestBody UserRequest body) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        userService.create(body);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest request, @PathVariable Integer id,
                               @Validated @RequestBody UserRequest body) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        body.setId(id);
        userService.update(body);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Integer id) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        userService.delete(id);
        return Result.ok();
    }
}
