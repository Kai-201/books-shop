package com.bookshop.util;

import com.bookshop.common.BusinessException;
import com.bookshop.common.LoginUser;
import com.bookshop.interceptor.AuthInterceptor;

import javax.servlet.http.HttpServletRequest;

public final class AuthHelper {

    private AuthHelper() {
    }

    public static LoginUser currentUser(HttpServletRequest request) {
        return AuthInterceptor.getLoginUser(request);
    }

    public static void requireAdmin(LoginUser user) {
        if (!"admin".equals(user.getRole())) {
            throw new BusinessException(403, "需要管理员权限");
        }
    }

    public static void requireUser(LoginUser user) {
        if (!"user".equals(user.getRole())) {
            throw new BusinessException(403, "需要用户权限");
        }
    }
}
