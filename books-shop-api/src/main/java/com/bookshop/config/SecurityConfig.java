package com.bookshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（使用 JWT，不需要 CSRF）
            .csrf().disable()
            // 无状态会话（使用 JWT）
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            // 放行所有请求（由自定义 JWT 拦截器控制权限）
            .authorizeRequests()
                .antMatchers("/**").permitAll()
            .and()
            // 禁用 Spring Security 自带的登录表单
            .formLogin().disable()
            .httpBasic().disable();

        return http.build();
    }
}
