package com.ecommerce.user.util;

import com.ecommerce.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {

    private SecretKey secretKey;
    
    @Value("${jwt.secret:defaultSecretKeyForJWTTokenGenerationInEcommerceApp}")
    private String jwtSecret;
    
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24小时

    @PostConstruct
    public void init() {
        // 使用配置的密钥，而不是随机生成
        if (jwtSecret != null && !jwtSecret.isEmpty()) {
            try {
                // 确保密钥长度足够（HS256需要至少256位）
                byte[] keyBytes = jwtSecret.getBytes();
                if (keyBytes.length < 32) {
                    // 如果密钥太短，进行填充
                    byte[] paddedKey = new byte[32];
                    System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
                    this.secretKey = Keys.hmacShaKeyFor(paddedKey);
                } else {
                    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
                }
                log.info("JWT密钥初始化成功");
            } catch (Exception e) {
                log.error("JWT密钥初始化失败，使用默认密钥", e);
                this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        } else {
            log.warn("未配置JWT密钥，使用随机生成的密钥（重启后token将失效）");
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    // 从 token 中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从 token 中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有声明
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析JWT token失败: {}", e.getMessage());
            throw e;
        }
    }

    // 检查 token 是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 生成 token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("name", user.getName());
        claims.put("countryCode", user.getCountryCode());
        
        return createToken(claims, user.getPhone()); // 使用手机号作为subject
    }

    // 创建 token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // subject 设置为手机号
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证 token（无用户参数）- 只验证token本身的有效性
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    // 验证 token 和用户匹配 - 新增的重载方法
    public Boolean validateToken(String token, User user) {
        try {
            final String username = extractUsername(token);
            final Long userIdFromToken = extractUserId(token);
            
            // 验证 token 中的用户信息与提供的用户是否匹配
            boolean isUserMatch = (username != null && username.equals(user.getPhone())) ||
                                 (userIdFromToken != null && userIdFromToken.equals(user.getId()));
            
            return isUserMatch && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token与用户验证失败: {}", e.getMessage());
            return false;
        }
    }

    // 从 token 中提取用户ID
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("从token提取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    // 新增：直接从token获取用户ID（用于Controller）
    public Long getUserIdFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return extractUserId(token);
        } catch (Exception e) {
            log.error("从token获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    // 新增：从token中提取手机号
    public String extractPhone(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("phone", String.class);
        } catch (Exception e) {
            log.error("从token提取手机号失败: {}", e.getMessage());
            return null;
        }
    }
    
    // 新增：从token中提取国家区号
    public String extractCountryCode(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("countryCode", String.class);
        } catch (Exception e) {
            log.error("从token提取国家区号失败: {}", e.getMessage());
            return null;
        }
    }
}