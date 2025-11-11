package com.ecommerce.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 设置键值对
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Redis设置成功: key={}", key);
        } catch (Exception e) {
            log.error("Redis设置失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 设置键值对并指定过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            log.debug("Redis设置成功: key={}, timeout={}{}", key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Redis设置失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 设置键值对并指定过期时间（秒）
     */
    public void set(String key, Object value, long timeoutSeconds) {
        set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * 获取值
     */
    public String get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Redis获取成功: key={}", key);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("Redis获取失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 获取值并转换为指定类型
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Redis获取成功: key={}", key);
            return value != null ? clazz.cast(value) : null;
        } catch (Exception e) {
            log.error("Redis获取失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 删除键
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Redis删除成功: key={}", key);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis删除失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 检查键是否存在
     */
    public boolean exists(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            log.debug("Redis检查存在: key={}", key);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis检查存在失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.expire(key, timeout, timeUnit);
            log.debug("Redis设置过期时间: key={}, timeout={}{}", key, timeout, timeUnit);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis设置过期时间失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 设置过期时间（秒）
     */
    public boolean expire(String key, long timeoutSeconds) {
        return expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * 获取剩余过期时间
     */
    public long getExpire(String key, TimeUnit timeUnit) {
        try {
            Long result = redisTemplate.getExpire(key, timeUnit);
            log.debug("Redis获取过期时间: key={}", key);
            return result != null ? result : -2;
        } catch (Exception e) {
            log.error("Redis获取过期时间失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 获取剩余过期时间（秒）
     */
    public long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }
    
    /**
     * 递增
     */
    public Long increment(String key) {
        try {
            Long result = redisTemplate.opsForValue().increment(key);
            log.debug("Redis递增: key={}", key);
            return result;
        } catch (Exception e) {
            log.error("Redis递增失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 递增指定值
     */
    public Long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            log.debug("Redis递增: key={}, delta={}", key, delta);
            return result;
        } catch (Exception e) {
            log.error("Redis递增失败: key={}, delta={}, error={}", key, delta, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 递减
     */
    public Long decrement(String key) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key);
            log.debug("Redis递减: key={}", key);
            return result;
        } catch (Exception e) {
            log.error("Redis递减失败: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 递减指定值
     */
    public Long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            log.debug("Redis递减: key={}, delta={}", key, delta);
            return result;
        } catch (Exception e) {
            log.error("Redis递减失败: key={}, delta={}, error={}", key, delta, e.getMessage());
            throw new RuntimeException("Redis操作失败");
        }
    }
    
    /**
     * 验证验证码
     */
    public boolean validateVerificationCode(String phone, String code) {
        String key = "verification_code:" + phone;
        String storedCode = get(key);
        if (storedCode != null && storedCode.equals(code)) {
            delete(key);
            return true;
        }
        return false;
    }
    
    /**
     * 存储验证码
     */
    public void storeVerificationCode(String phone, String code, long timeoutSeconds) {
        String key = "verification_code:" + phone;
        set(key, code, timeoutSeconds);
    }
    
    /**
     * 存储用户会话
     */
    public void storeUserSession(String token, Object userInfo, long timeoutSeconds) {
        String key = "user_session:" + token;
        set(key, userInfo, timeoutSeconds);
    }
    
    /**
     * 获取用户会话
     */
    public Object getUserSession(String token) {
        String key = "user_session:" + token;
        return get(key);
    }
    
    /**
     * 删除用户会话
     */
    public void deleteUserSession(String token) {
        String key = "user_session:" + token;
        delete(key);
    }
    
    /**
     * 清理所有验证码（用于测试）
     */
    public void cleanupAllVerificationCodes() {
        // 注意：在生产环境中慎用，这里只是示例
        // 实际应该使用更精确的键匹配
        log.info("清理所有验证码缓存");
    }
}