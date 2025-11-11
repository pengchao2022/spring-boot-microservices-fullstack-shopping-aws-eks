package com.ecommerce.user.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_codes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "country_code", nullable = false, length = 10)
    @Builder.Default
    private String countryCode = "+86";
    
    @Column(nullable = false, length = 20)
    private String phone;
    
    @Column(nullable = false, length = 10)
    private String code;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // 设置默认国家区号
        if (this.countryCode == null) {
            this.countryCode = "+86";
        }
        // 设置默认使用状态
        if (this.used == null) {
            this.used = false;
        }
        // 默认5分钟后过期
        if (this.expiresAt == null) {
            this.expiresAt = LocalDateTime.now().plusMinutes(5);
        }
    }
    
    public boolean isValid() {
        return !this.used && LocalDateTime.now().isBefore(this.expiresAt);
    }
    
    public void markAsUsed() {
        this.used = true;
    }
    
    // 业务方法
    
    /**
     * 获取完整手机号（包含国家区号）
     */
    public String getFullPhone() {
        return (countryCode != null ? countryCode : "+86") + phone;
    }
    
    /**
     * 验证国家区号格式
     */
    public boolean isValidCountryCode() {
        return countryCode != null && countryCode.matches("^\\+[1-9]\\d{0,3}$");
    }
    
    /**
     * 获取掩码手机号（用于日志记录）
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
     * 创建验证码对象
     */
    public static VerificationCode create(String countryCode, String phone, String code, int expireMinutes) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCountryCode(countryCode);
        verificationCode.setPhone(phone);
        verificationCode.setCode(code);
        verificationCode.setUsed(false);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(expireMinutes));
        return verificationCode;
    }
    
    /**
     * 创建验证码对象（使用默认国家区号）
     */
    public static VerificationCode create(String phone, String code, int expireMinutes) {
        return create("+86", phone, code, expireMinutes);
    }
    
    /**
     * 检查验证码是否匹配
     */
    public boolean isCodeMatch(String inputCode) {
        return this.code != null && this.code.equals(inputCode);
    }
    
    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    /**
     * 获取剩余有效时间（秒）
     */
    public long getRemainingSeconds() {
        return java.time.Duration.between(LocalDateTime.now(), this.expiresAt).getSeconds();
    }
}