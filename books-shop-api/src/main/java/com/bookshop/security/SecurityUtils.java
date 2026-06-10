package com.bookshop.security;

import com.bookshop.common.BusinessException;
import com.bookshop.common.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 上下文工具类。
 * 替代旧的 AuthHelper，从 SecurityContextHolder 获取当前登录用户。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户，未登录时抛出 401
     */
    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        return (LoginUser) authentication.getPrincipal();
    }

    /**
     * 获取当前登录用户 ID
     */
    public static Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        return "admin".equals(getCurrentUserRole());
    }

    /**
     * 判断当前用户是否为普通用户
     */
    public static boolean isUser() {
        return "user".equals(getCurrentUserRole());
    }
}
