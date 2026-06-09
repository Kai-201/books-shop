package com.bookshop.util;

import com.bookshop.common.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-hours}")
    private int expireHours;

    /**
     * 获取签名 Key（新版本必须用 Key，不再用 String）
     */
    private Key getSigningKey() {
        System.out.println("🔑 JwtUtil.getSigningKey() - secret = '" + secret + "', 长度 = " + (secret != null ? secret.length() : "null"));
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取 secret 长度（用于调试）
     */
    public int getSecretLength() {
        return secret != null ? secret.length() : -1;
    }

    /**
     * 生成 Token
     */
    public String generateToken(LoginUser user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("loginName", user.getLoginName());
        claims.put("role", user.getRole());

        long expireMillis = expireHours * 3600L * 1000L;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     */
    public LoginUser parseToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        LoginUser user = new LoginUser();
        user.setId(((Number) claims.get("id")).intValue());
        user.setLoginName((String) claims.get("loginName"));
        user.setRole((String) claims.get("role"));

        return user;
    }
}