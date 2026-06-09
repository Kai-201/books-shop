package com.bookshop.controller;

import com.bookshop.common.Result;
import com.bookshop.dto.LoginRequest;
import com.bookshop.dto.LoginResponse;
import com.bookshop.dto.RegisterRequest;
import com.bookshop.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/admin/login")
    public Result<LoginResponse> adminLogin(@Validated @RequestBody LoginRequest request) {
        return Result.ok(authService.adminLogin(request));
    }

    @PostMapping("/user/login")
    public Result<LoginResponse> userLogin(@Validated @RequestBody LoginRequest request) {
        return Result.ok(authService.userLogin(request));
    }

    @PostMapping("/user/register")
    public Result<LoginResponse> userRegister(@Validated @RequestBody RegisterRequest request) {
        return Result.ok(authService.userRegister(request, request.getUsername()));
    }
}
