package com.ecommerce.user.service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.model.UserProfile;
import com.ecommerce.user.repository.UserProfileRepository;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    
    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        log.info("根据ID查询用户: id={}", id);
        return userRepository.findById(id);
    }
    
    @Cacheable(value = "users", key = "#phone")
    public Optional<User> getUserByPhone(String phone) {
        log.info("根据手机号查询用户: phone={}", phone);
        return userRepository.findByPhone(phone);
    }
    
    @Cacheable(value = "users", key = "#alipayUserId")
    public Optional<User> getUserByAlipayUserId(String alipayUserId) {
        log.info("根据支付宝用户ID查询用户: alipayUserId={}", alipayUserId);
        return userRepository.findByAlipayUserId(alipayUserId);
    }
    
    @Cacheable(value = "users", key = "#taobaoUserId")
    public Optional<User> getUserByTaobaoUserId(String taobaoUserId) {
        log.info("根据淘宝用户ID查询用户: taobaoUserId={}", taobaoUserId);
        return userRepository.findByTaobaoUserId(taobaoUserId);
    }
    
    // 修改：由于 User 类没有 email 字段，这里只按手机号查找
    public Optional<User> findByPhoneOrEmail(String username) {
        log.info("根据用户名查找用户: username={}", username);
        // 只按手机号查找，因为 User 类没有 email 字段
        return userRepository.findByPhone(username);
    }
    
    @CacheEvict(value = "users", key = "#user.id")
    public User saveUser(User user) {
        log.info("保存用户: id={}, phone={}", user.getId(), user.getPhone());
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("删除用户: id={}", id);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(User.UserStatus.DELETED);
            userRepository.save(user);
            log.info("用户标记为删除: id={}", id);
        } else {
            log.warn("删除用户失败: 用户不存在, id={}", id);
            throw new RuntimeException("用户不存在");
        }
    }
    
    public List<User> getAllUsers() {
        log.info("查询所有用户");
        return userRepository.findAll();
    }
    
    public List<User> getActiveUsers() {
        log.info("查询所有活跃用户");
        return userRepository.findAllActiveUsers();
    }
    
    public List<User> getUsersByPhone(String phone) {
        log.info("根据手机号模糊查询用户: phone={}", phone);
        return userRepository.findByPhoneContaining(phone);
    }
    
    public List<User> getUsersByName(String name) {
        log.info("根据姓名模糊查询用户: name={}", name);
        return userRepository.findByNameContaining(name);
    }
    
    public List<User> getUsersByStatus(User.UserStatus status) {
        log.info("根据状态查询用户: status={}", status);
        return userRepository.findByStatus(status);
    }
    
    public boolean existsByPhone(String phone) {
        log.info("检查手机号是否存在: phone={}", phone);
        return userRepository.existsByPhone(phone);
    }
    
    public boolean existsByAlipayUserId(String alipayUserId) {
        log.info("检查支付宝用户ID是否存在: alipayUserId={}", alipayUserId);
        return userRepository.existsByAlipayUserId(alipayUserId);
    }
    
    public boolean existsByTaobaoUserId(String taobaoUserId) {
        log.info("检查淘宝用户ID是否存在: taobaoUserId={}", taobaoUserId);
        return userRepository.existsByTaobaoUserId(taobaoUserId);
    }
    
    // 原有的简单更新方法（保持兼容）
    @CacheEvict(value = "users", key = "#id")
    public User updateUserProfile(Long id, String name, String avatarUrl) {
        log.info("更新用户资料: id={}, name={}", id, name);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    // 修改：通用的用户资料更新方法 - 添加 UserProfile 处理
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public User updateUserProfile(Long id, Map<String, Object> profileData) {
        log.info("更新用户资料: id={}, data={}", id, profileData);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        boolean userUpdated = false;
        
        // 1. 更新 users 表中的基本信息
        if (profileData.containsKey("name")) {
            String name = (String) profileData.get("name");
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name);
                userUpdated = true;
                log.info("更新用户名: {}", name);
            }
        }
        if (profileData.containsKey("phone")) {
            String phone = (String) profileData.get("phone");
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone);
                userUpdated = true;
                log.info("更新手机号: {}", phone);
            }
        }
        if (profileData.containsKey("avatar") || profileData.containsKey("avatarUrl")) {
            String avatar = (String) profileData.get("avatar");
            if (avatar == null) {
                avatar = (String) profileData.get("avatarUrl");
            }
            if (avatar != null && !avatar.trim().isEmpty()) {
                user.setAvatarUrl(avatar);
                userUpdated = true;
                log.info("更新头像: {}", avatar);
            }
        }
        
        // 2. 处理 user_profiles 表中的扩展信息（如邮箱）
        if (profileData.containsKey("email")) {
            String email = (String) profileData.get("email");
            if (email != null && !email.trim().isEmpty()) {
                // 修复：创建 final 变量用于 lambda 表达式
                final User finalUser = user;
                
                // 获取或创建 UserProfile
                UserProfile userProfile = userProfileRepository.findByUserId(id)
                        .orElseGet(() -> {
                            UserProfile newProfile = new UserProfile();
                            newProfile.setUser(finalUser); // 使用 final 变量
                            log.info("创建新的用户资料记录: userId={}", id);
                            return newProfile;
                        });
                
                userProfile.setEmail(email);
                userProfileRepository.save(userProfile);
                log.info("更新用户邮箱: userId={}, email={}", id, email);
            }
        }
        
        // 3. 更新用户更新时间
        if (userUpdated) {
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }
        
        log.info("用户资料更新完成: id={}", id);
        return user;
    }
    
    @CacheEvict(value = "users", key = "#id")
    public User updateUserStatus(Long id, User.UserStatus status) {
        log.info("更新用户状态: id={}, status={}", id, status);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public User updateUserPassword(Long id, String newPassword) {
        log.info("更新用户密码: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 在实际应用中，这里应该对密码进行加密
        // user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword); // 注意：这里需要密码加密
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    public long getTodayNewUserCount() {
        return userRepository.countTodayNewUsers();
    }
    
    public long getUserCountBySource(User.RegisterSource source) {
        return userRepository.countByRegisterSource(source);
    }
    
    public List<User> getRecentActiveUsers() {
        log.info("查询最近活跃用户");
        return userRepository.findRecentActiveUsers();
    }
    
    public List<User> getUsersByLoginCountDesc() {
        log.info("查询用户按登录次数降序");
        return userRepository.findByLoginCountDesc();
    }
    
    public void cleanupInactiveUsers(int days) {
        log.info("清理 {} 天未登录的非活跃用户", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<User> inactiveUsers = userRepository.findInactiveUsers(cutoffDate);
        
        for (User user : inactiveUsers) {
            user.setStatus(User.UserStatus.INACTIVE);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("标记用户为非活跃: id={}, phone={}, lastLoginAt={}", 
                    user.getId(), user.getPhone(), user.getLastLoginAt());
        }
        
        log.info("清理完成: 共标记 {} 个非活跃用户", inactiveUsers.size());
    }
    
    // 新增方法：获取用户资料（包含邮箱等信息）
    public Optional<UserProfile> getUserProfile(Long userId) {
        log.info("获取用户资料: userId={}", userId);
        return userProfileRepository.findByUserId(userId);
    }
}