package com.ecommerce.user.controller;

import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserProfile;
import com.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData) {
        log.info("收到更新用户资料请求: {}", profileData);
        try {
            // 从JWT token中获取用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.warn("用户未认证，无法更新资料");
                return buildErrorResponse("用户未认证，请重新登录");
            }
            
            log.info("获取到的用户ID: {}", userId);
            
            // 验证用户是否存在
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                log.warn("用户不存在: userId={}", userId);
                return buildErrorResponse("用户不存在");
            }
            
            User updatedUser = userService.updateUserProfile(userId, profileData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", updatedUser);
            response.put("message", "用户资料更新成功");
            response.put("timestamp", LocalDateTime.now().toString());
            
            log.info("用户资料更新成功: userId={}", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新用户资料失败", e);
            return buildErrorResponse("更新资料失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.warn("用户未认证，无法获取资料");
                return buildErrorResponse("用户未认证，请重新登录");
            }
            
            log.info("获取用户资料请求，用户ID: {}", userId);
            
            // 1. 获取用户基本信息（users表）
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 2. 获取用户完整资料（user_profiles表）
            Optional<UserProfile> userProfileOpt = userService.getUserProfile(userId);
            
            // 3. 构建完整的响应数据
            Map<String, Object> profileResponse = new HashMap<>();
            
            // 基本用户信息（来自 users 表）
            profileResponse.put("id", user.getId());
            profileResponse.put("name", user.getName());
            profileResponse.put("phone", user.getPhone());
            profileResponse.put("countryCode", user.getCountryCode());
            profileResponse.put("avatarUrl", user.getAvatarUrl());
            profileResponse.put("registerSource", user.getRegisterSource());
            profileResponse.put("lastLoginAt", user.getLastLoginAt());
            profileResponse.put("loginCount", user.getLoginCount());
            profileResponse.put("status", user.getStatus());
            profileResponse.put("createdAt", user.getCreatedAt());
            profileResponse.put("updatedAt", user.getUpdatedAt());
            
            // 用户资料信息（从 user_profiles 表获取）
            if (userProfileOpt.isPresent()) {
                UserProfile userProfile = userProfileOpt.get();
                profileResponse.put("email", userProfile.getEmail());
                profileResponse.put("gender", userProfile.getGender());
                profileResponse.put("birthday", userProfile.getBirthday());
                profileResponse.put("personalSignature", userProfile.getPersonalSignature());
                profileResponse.put("wechatId", userProfile.getWechatId());
                profileResponse.put("qqNumber", userProfile.getQqNumber());
                profileResponse.put("locationProvince", userProfile.getLocationProvince());
                profileResponse.put("locationCity", userProfile.getLocationCity());
                profileResponse.put("locationDistrict", userProfile.getLocationDistrict());
                profileResponse.put("profileCreatedAt", userProfile.getCreatedAt());
                profileResponse.put("profileUpdatedAt", userProfile.getUpdatedAt());
            } else {
                // 如果没有用户资料记录，设置默认值
                profileResponse.put("email", null);
                profileResponse.put("gender", null);
                profileResponse.put("birthday", null);
                profileResponse.put("personalSignature", null);
                profileResponse.put("wechatId", null);
                profileResponse.put("qqNumber", null);
                profileResponse.put("locationProvince", null);
                profileResponse.put("locationCity", null);
                profileResponse.put("locationDistrict", null);
                profileResponse.put("profileCreatedAt", null);
                profileResponse.put("profileUpdatedAt", null);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("profile", profileResponse);  // 改为返回完整的 profile 对象
            response.put("timestamp", LocalDateTime.now().toString());
            
            log.info("获取用户资料成功: userId={}, hasProfile={}", userId, userProfileOpt.isPresent());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取用户资料失败", e);
            return buildErrorResponse("获取资料失败: " + e.getMessage());
        }
    }
    
    // 修复的方法 - 从JWT token中获取当前用户ID
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                log.info("Authentication principal: {}, type: {}", principal, principal.getClass().getSimpleName());
                
                String username = authentication.getName();
                log.info("从SecurityContext获取用户名: {}", username);
                
                // 如果是匿名用户，返回null
                if (username.equals("anonymousUser")) {
                    log.warn("用户未认证，返回null");
                    return null;
                }
                
                // 尝试从用户名解析用户ID（JWT中应该直接存储用户ID）
                try {
                    Long userId = Long.parseLong(username);
                    log.info("从用户名解析出用户ID: {}", userId);
                    return userId;
                } catch (NumberFormatException e) {
                    // 如果用户名不是数字，可能是手机号，需要查询用户
                    log.info("用户名不是数字格式，尝试按手机号查询用户: {}", username);
                    Optional<User> userOpt = userService.getUserByPhone(username);
                    if (userOpt.isPresent()) {
                        Long userId = userOpt.get().getId();
                        log.info("通过手机号找到用户ID: {}", userId);
                        return userId;
                    } else {
                        log.warn("未找到手机号对应的用户: {}", username);
                        return null;
                    }
                }
            } else {
                log.warn("SecurityContext中没有认证信息或用户未认证");
                return null;
            }
        } catch (Exception e) {
            log.error("获取当前用户ID失败", e);
            return null;
        }
    }
    
    private ResponseEntity<?> buildErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", errorMessage);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(response);
    }
}