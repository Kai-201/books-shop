package com.bookshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.Result;
import com.bookshop.common.validation.CreateGroup;
import com.bookshop.common.validation.UpdateGroup;
import com.bookshop.dto.UserRequest;
import com.bookshop.entity.User;
import com.bookshop.security.SecurityUtils;
import com.bookshop.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<User> me() {
        return Result.ok(userService.getById(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public Result<List<User>> list() {
        List<User> users = userService.listAll();
        users.forEach(u -> u.setPassword(null));
        return Result.ok(users);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('admin')")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        Page<User> result = userService.page(page, size, keyword);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<User> detail(@PathVariable @Min(value = 1, message = "用户ID无效")Integer id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Void> create(@Validated(CreateGroup.class) @RequestBody UserRequest body) {
        userService.create(body);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> update(@PathVariable @Min(value = 1, message = "用户ID无效") Integer id,
                               @Validated(UpdateGroup.class) @RequestBody UserRequest body) {
        body.setId(id);
        userService.update(body);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.ok();
    }
}
