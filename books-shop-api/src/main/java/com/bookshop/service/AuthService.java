package com.bookshop.service;

import com.bookshop.common.LoginUser;
import com.bookshop.dto.LoginRequest;
import com.bookshop.dto.LoginResponse;

public interface AuthService {

    LoginResponse adminLogin(LoginRequest request);

    LoginResponse userLogin(LoginRequest request);

    LoginResponse userRegister(LoginRequest request, String username);
}
