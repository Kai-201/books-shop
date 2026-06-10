package com.bookshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 * JWT 认证已迁移至 Spring Security（SecurityConfig + JwtAuthenticationFilter），
 * CORS 已迁移至 SecurityConfig.corsConfigurationSource()。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 保留此文件以备后续 MVC 配置扩展
}
