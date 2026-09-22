package com.example.demo.dto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // 从 application.properties 读取密钥字符串
    @Value("${jwt.secret}")
    private String secretString;

    private SecretKey key;

    private final long EXPIRATION_MS = 1000L * 60 * 60 * 24 * 30; // 1小时过期

    // Bean 初始化时把字符串转换成固定的 SecretKey，之后一直复用，不会每次重启就变
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 token
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    // 从 token 里取出 email
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // 验证 token 是否有效（签名正确 + 没过期）
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}