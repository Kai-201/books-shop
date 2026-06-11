package com.bookshop.config;

import com.bookshop.common.Result;
import com.bookshop.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（JWT 无状态，不需要 CSRF 保护）
            .csrf().disable()

            // 无状态会话（JWT 自带身份信息）
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            // CORS 配置（取代旧的独立 CorsFilter）
            .cors().configurationSource(corsConfigurationSource())

            .and()
            // 异常处理：返回 JSON 而非默认的 302/403 页面
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) ->
                        writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                Result.fail(401, "未登录，请先登录")))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                                Result.fail(403, "权限不足")))

            .and()
            // URL 级别权限规则
            .authorizeRequests()
                // Knife4j 文档页面放行
                .antMatchers("/doc.html", "/swagger-resources/**", "/webjars/**", "/v2/api-docs/**", "/favicon.ico").permitAll()
                // 认证相关接口放行
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/error").permitAll()
                // 公开浏览接口
                .antMatchers(HttpMethod.GET, "/books", "/books/**").permitAll()
                .antMatchers(HttpMethod.GET, "/categories", "/categories/**").permitAll()
                // 当前用户信息（任意登录用户可访问）
                .antMatchers(HttpMethod.GET, "/users/me").authenticated()
                // 用户管理仅管理员可访问
                .antMatchers("/users/**").hasRole("admin")
                // 图书增删改需要登录
                .antMatchers(HttpMethod.POST, "/books").authenticated()
                .antMatchers(HttpMethod.PUT, "/books/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/books/**").authenticated()
                // 其余所有请求需要登录
                .anyRequest().authenticated()

            .and()
            // 禁用 Spring Security 自带的表单和 HTTP Basic 认证
            .formLogin().disable()
            .httpBasic().disable()

            // 在 UsernamePasswordAuthenticationFilter 之前插入 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 跨域配置（从 WebConfig 迁移至此，统一由 Spring Security 管理）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://127.0.0.1:5173");
        config.addAllowedOrigin("http://127.0.0.1:5174");
        config.addAllowedOrigin("http://127.0.0.1:3000");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 向响应写入 JSON
     */
    private static void writeJson(HttpServletResponse response, int status, Result<Void> result) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(result));
        writer.flush();
    }
}
