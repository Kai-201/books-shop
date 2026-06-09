package com.bookshop.interceptor;

import com.bookshop.common.BusinessException;
import com.bookshop.common.LoginUser;
import com.bookshop.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String LOGIN_USER_KEY = "loginUser";

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("🔥 请求进入拦截器: " + method + " " + uri);

        // ===== 1. 公开接口放行（GET books/categories）=====
        if ("GET".equalsIgnoreCase(method)
                && (uri.contains("/books") || uri.contains("/categories"))) {
            parseTokenIfPresent(request);
            return true;
        }

        // ===== 2. 获取 token =====
        String token = request.getHeader("Authorization");

        System.out.println("Authorization 原始值 = " + token);

        if (token == null || token.trim().isEmpty()) {
            System.out.println("❌ token 为空，抛出 401");
            throw new BusinessException(401, "未登录，请先登录");
        }

        // ===== 3. 去掉 Bearer =====
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        System.out.println("解析后 token = " + token);

        // ===== 4. 解析 JWT =====
        try {
            System.out.println("🔑 开始解析 JWT，secret 长度 = " + jwtUtil.getSecretLength());
            LoginUser loginUser = jwtUtil.parseToken(token);
            request.setAttribute(LOGIN_USER_KEY, loginUser);

            System.out.println("✅ 用户登录成功: " + loginUser.getLoginName());

            return true;

        } catch (Exception e) {

            // 🔥 关键：打印真实错误（否则你永远看不见问题）
            System.out.println("❌ JWT 解析失败:");
            e.printStackTrace();

            throw new BusinessException(401, "登录已过期或token无效: " + e.getMessage());
        }
    }

    private void parseTokenIfPresent(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || token.trim().isEmpty()) {
            return;
        }

        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            LoginUser loginUser = jwtUtil.parseToken(token);
            request.setAttribute(LOGIN_USER_KEY, loginUser);
        } catch (Exception e) {
            // 公开接口：忽略错误 token
        }
    }

    public static LoginUser getLoginUser(HttpServletRequest request) {
        return (LoginUser) request.getAttribute(LOGIN_USER_KEY);
    }
}