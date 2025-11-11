package com.ecommerce.user.model.dto;

import com.ecommerce.user.model.User;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;           // JWT token (原来的 accessToken)
    private String tokenType = "Bearer";
    private Long expiresIn = 3600L; // token 过期时间（秒）
    private User user;              // 完整的用户信息
    
    // 为了兼容性，保留一些常用字段
    private String phone;           // 用户手机号
    private String countryCode;     // 国家区号（新增）
    private String name;            // 用户姓名
    private Long userId;            // 用户ID
    
    // 构造方法 - 使用 User 对象
    public AuthResponse(String token, User user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = 3600L;
        this.user = user;
        if (user != null) {
            this.phone = user.getPhone();
            this.countryCode = user.getCountryCode();
            this.name = user.getName();
            this.userId = user.getId();
        }
    }
    
    // 构造方法 - 包含所有字段
    public AuthResponse(String token, String tokenType, Long expiresIn, User user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
        if (user != null) {
            this.phone = user.getPhone();
            this.countryCode = user.getCountryCode();
            this.name = user.getName();
            this.userId = user.getId();
        }
    }
    
    // 快速创建成功响应的方法
    public static AuthResponse success(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(user)
                .phone(user != null ? user.getPhone() : null)
                .countryCode(user != null ? user.getCountryCode() : "+86")
                .name(user != null ? user.getName() : null)
                .userId(user != null ? user.getId() : null)
                .build();
    }
    
    // 创建包含国家区号的响应方法
    public static AuthResponse successWithCountryCode(String token, User user, String countryCode) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(user)
                .phone(user != null ? user.getPhone() : null)
                .countryCode(countryCode != null ? countryCode : "+86")
                .name(user != null ? user.getName() : null)
                .userId(user != null ? user.getId() : null)
                .build();
    }
    
    // 创建失败响应的方法（可选）
    public static AuthResponse error(String message) {
        // 这里可以返回一个包含错误信息的 AuthResponse
        // 或者使用不同的响应结构
        return null; // 通常错误响应会使用不同的 DTO
    }
    
    // 业务方法
    
    /**
     * 获取完整手机号（包含国家区号）
     */
    public String getFullPhone() {
        return (countryCode != null ? countryCode : "+86") + phone;
    }
    
    /**
     * 获取掩码手机号
     */
    public String getMaskedPhone() {
        if (phone == null || phone.length() < 7) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    /**
     * 获取完整掩码手机号（包含国家区号）
     */
    public String getFullMaskedPhone() {
        return (countryCode != null ? countryCode : "+86") + " " + getMaskedPhone();
    }
    
    /**
     * 检查响应是否包含有效的用户信息
     */
    public boolean hasValidUser() {
        return user != null && userId != null;
    }
    
    /**
     * 获取用户显示名称
     */
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return getMaskedPhone();
    }
}