package com.seateam.secondhand_system.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT工具类
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String userId) {
        return generateToken(userId, expiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String userId) {
        // 刷新令牌有效期设为7天
        long refreshExpiration = 7 * 24 * 60 * 60 * 1000;
        String refreshToken = generateToken(userId, refreshExpiration);
        // 存储刷新令牌到Redis
        redisTemplate.opsForValue().set("refresh_token:" + userId, refreshToken, refreshExpiration, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    /**
     * 生成JWT令牌
     */
    private String generateToken(String userId, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从JWT令牌中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        if (isTokenBlacklisted(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求头中获取JWT令牌
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    /**
     * 将令牌加入黑名单（登出用）
     */
    public void blacklistToken(String token) {
        String userId = getUserIdFromToken(token);
        long ttl = getTokenRemainingTime(token);
        redisTemplate.opsForValue().set("blacklist:" + token, userId, ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查令牌是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }

    /**
     * 获取令牌剩余时间
     */
    private long getTokenRemainingTime(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public Long getCurrentUserId() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String token = getTokenFromRequest(request);
        if (token == null) {
            return null;
        }
        String userIdStr = getUserIdFromToken(token);
        return Long.parseLong(userIdStr);
    }
}