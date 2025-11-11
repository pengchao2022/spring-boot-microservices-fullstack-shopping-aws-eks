package com.ecommerce.user.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"country_code", "phone"})  // 修正：使用数据库列名 country_code
       })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "country_code", nullable = false, length = 10)
    @Builder.Default
    private String countryCode = "+86";
    
    @Column(nullable = false, length = 20)
    private String phone;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "alipay_user_id", unique = true, length = 100)
    private String alipayUserId;
    
    @Column(name = "taobao_user_id", unique = true, length = 100)
    private String taobaoUserId;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "register_source", nullable = false, length = 20)
    @Builder.Default
    private RegisterSource registerSource = RegisterSource.PHONE;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;
    
    @Column(name = "login_count")
    @Builder.Default
    private Integer loginCount = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 用户状态枚举
    public enum UserStatus {
        ACTIVE("活跃"),
        INACTIVE("未激活"),
        SUSPENDED("暂停"),
        DELETED("删除");
        
        private final String description;
        
        UserStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 注册来源枚举
    public enum RegisterSource {
        PHONE("手机号"),
        ALIPAY("支付宝"),
        TAOBAO("淘宝"),
        WECHAT("微信");
        
        private final String description;
        
        RegisterSource(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 业务方法
    
    /**
     * 获取完整手机号（包含国家区号）
     */
    public String getFullPhone() {
        return this.countryCode + this.phone;
    }
    
    /**
     * 更新用户登录信息
     */
    public void updateLoginInfo(String loginIp) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = loginIp;
        this.loginCount = (this.loginCount == null ? 0 : this.loginCount) + 1;
    }
    
    /**
     * 激活用户
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
    
    /**
     * 暂停用户
     */
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }
    
    /**
     * 检查用户是否活跃
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
    
    /**
     * 检查用户是否绑定了手机号
     */
    public boolean hasPhoneBound() {
        return this.phone != null && !this.phone.trim().isEmpty() && !this.phone.equals("unknown");
    }
    
    /**
     * 检查是否是第三方登录用户
     */
    public boolean isThirdPartyUser() {
        return this.registerSource == RegisterSource.ALIPAY || 
               this.registerSource == RegisterSource.TAOBAO;
    }
    
    /**
     * 绑定手机号
     */
    public void bindPhone(String countryCode, String phone) {
        this.countryCode = countryCode != null ? countryCode : "+86";
        this.phone = phone;
        if (this.registerSource == RegisterSource.ALIPAY || this.registerSource == RegisterSource.TAOBAO) {
            this.registerSource = RegisterSource.PHONE;
        }
    }
    
    /**
     * 检查手机号是否匹配（包含国家区号验证）
     */
    public boolean isPhoneMatch(String countryCode, String phone) {
        return (this.countryCode.equals(countryCode) && this.phone.equals(phone));
    }
    
    /**
     * 验证国家区号格式
     */
    public static boolean isValidCountryCode(String countryCode) {
        return countryCode != null && countryCode.matches("^\\+[1-9]\\d{0,3}$");
    }
    
    // 静态工厂方法
    
    /**
     * 创建手机号用户
     */
    public static User createPhoneUser(String countryCode, String phone, String password, String name) {
        User user = User.builder()
                .countryCode(countryCode != null ? countryCode : "+86")
                .phone(phone)
                .password(password)
                .registerSource(RegisterSource.PHONE)
                .status(UserStatus.ACTIVE)
                .loginCount(0)
                .build();
        
        // 设置用户名
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        } else if (phone != null && phone.length() >= 4) {
            user.setName("用户_" + phone.substring(phone.length() - 4));
        } else {
            user.setName("用户");
        }
        
        return user;
    }
    
    /**
     * 创建手机号用户（使用默认中国区号）
     */
    public static User createPhoneUser(String phone, String password, String name) {
        return createPhoneUser("+86", phone, password, name);
    }
    
    /**
     * 创建支付宝用户
     */
    public static User createAlipayUser(String alipayUserId, String name, String avatarUrl, String countryCode, String phone) {
        User user = User.builder()
                .alipayUserId(alipayUserId)
                .avatarUrl(avatarUrl)
                .countryCode(countryCode != null ? countryCode : "+86")
                .phone(phone != null ? phone : "")
                .password("") // 第三方登录用户不需要密码
                .registerSource(RegisterSource.ALIPAY)
                .status(UserStatus.ACTIVE)
                .loginCount(0)
                .build();
        
        // 设置用户名
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        } else {
            user.setName("支付宝用户");
        }
        
        return user;
    }
    
    /**
     * 创建支付宝用户（兼容旧版本）
     */
    public static User createAlipayUser(String alipayUserId, String name, String avatarUrl, String phone) {
        return createAlipayUser(alipayUserId, name, avatarUrl, "+86", phone);
    }
    
    /**
     * 创建淘宝用户
     */
    public static User createTaobaoUser(String taobaoUserId, String name, String avatarUrl, String countryCode, String phone) {
        User user = User.builder()
                .taobaoUserId(taobaoUserId)
                .avatarUrl(avatarUrl)
                .countryCode(countryCode != null ? countryCode : "+86")
                .phone(phone != null ? phone : "")
                .password("") // 第三方登录用户不需要密码
                .registerSource(RegisterSource.TAOBAO)
                .status(UserStatus.ACTIVE)
                .loginCount(0)
                .build();
        
        // 设置用户名
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        } else {
            user.setName("淘宝用户");
        }
        
        return user;
    }
    
    /**
     * 创建淘宝用户（兼容旧版本）
     */
    public static User createTaobaoUser(String taobaoUserId, String name, String avatarUrl, String phone) {
        return createTaobaoUser(taobaoUserId, name, avatarUrl, "+86", phone);
    }
    
    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return this.name != null ? this.name : 
               (this.phone != null ? "用户_" + this.phone.substring(Math.max(0, this.phone.length() - 4)) : "用户");
    }
    
    /**
     * 获取掩码手机号
     */
    public String getMaskedPhone() {
        if (this.phone == null || this.phone.length() < 7) {
            return "***";
        }
        return this.phone.substring(0, 3) + "****" + this.phone.substring(this.phone.length() - 4);
    }
    
    /**
     * 获取完整掩码手机号（包含国家区号）
     */
    public String getFullMaskedPhone() {
        return this.countryCode + " " + getMaskedPhone();
    }
    
    /**
     * 获取国家区号和手机号的组合标识
     */
    public String getPhoneIdentifier() {
        return this.countryCode + "_" + this.phone;
    }
}