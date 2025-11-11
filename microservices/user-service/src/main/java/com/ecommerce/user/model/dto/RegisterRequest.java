package com.ecommerce.user.model.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @Pattern(regexp = "^\\+[1-9]\\d{0,3}$", message = "国家区号格式不正确")
    private String countryCode = "+86";  // 国家区号，默认中国
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    private String name;             // 用户名（可选）
    
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度必须在4-6位之间")
    private String verificationCode;
    
    // 注册来源
    private String registerSource = "PHONE"; // 默认手机注册
    
    // 邀请码（可选）
    private String inviteCode;
    
    // 设备信息
    private String deviceId;          // 设备ID
    private String userAgent;         // 用户代理
    private String clientType;        // 客户端类型：WEB, APP, H5等
    
    // 第三方注册相关字段
    private String thirdPartyUserId;  // 第三方用户ID（支付宝、淘宝等）
    private String thirdPartyToken;   // 第三方访问令牌
    private String avatarUrl;         // 头像URL（第三方注册时使用）
    
    // 业务方法
    
    /**
     * 获取完整手机号（包含国家区号）
     */
    public String getFullPhone() {
        return (countryCode != null ? countryCode : "+86") + phone;
    }
    
    /**
     * 检查是否是手机号注册
     */
    public boolean isPhoneRegister() {
        return "PHONE".equalsIgnoreCase(registerSource);
    }
    
    /**
     * 检查是否是第三方注册
     */
    public boolean isThirdPartyRegister() {
        return ("ALIPAY".equalsIgnoreCase(registerSource) || "TAOBAO".equalsIgnoreCase(registerSource)) 
                && thirdPartyUserId != null && !thirdPartyUserId.trim().isEmpty();
    }
    
    /**
     * 验证国家区号格式
     */
    public boolean isValidCountryCode() {
        return countryCode != null && countryCode.matches("^\\+[1-9]\\d{0,3}$");
    }
    
    /**
     * 验证手机号格式（基础验证）
     */
    public boolean isValidPhoneFormat() {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // 基础手机号格式验证，可根据具体需求调整
        return phone.matches("^1[3-9]\\d{9}$") || phone.matches("^[0-9]{6,15}$");
    }
    
    /**
     * 清理数据
     */
    public void sanitize() {
        if (phone != null) {
            phone = phone.trim().replaceAll("\\s+", "");
        }
        if (countryCode != null) {
            countryCode = countryCode.trim();
        } else {
            countryCode = "+86";
        }
        if (password != null) {
            password = password.trim();
        }
        if (verificationCode != null) {
            verificationCode = verificationCode.trim();
        }
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }
        if (registerSource != null) {
            registerSource = registerSource.trim().toUpperCase();
        } else {
            registerSource = "PHONE";
        }
    }
    
    /**
     * 生成默认用户名
     */
    public String generateDefaultName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        
        if (phone != null && phone.length() >= 4) {
            return "用户_" + phone.substring(phone.length() - 4);
        }
        
        return "用户";
    }
    
    /**
     * 创建手机号注册请求
     */
    public static RegisterRequest createPhoneRegister(String countryCode, String phone, String password, 
                                                     String verificationCode, String name) {
        RegisterRequest request = new RegisterRequest();
        request.setCountryCode(countryCode);
        request.setPhone(phone);
        request.setPassword(password);
        request.setVerificationCode(verificationCode);
        request.setName(name);
        request.setRegisterSource("PHONE");
        return request;
    }
    
    /**
     * 创建手机号注册请求（使用默认国家区号）
     */
    public static RegisterRequest createPhoneRegister(String phone, String password, String verificationCode, String name) {
        return createPhoneRegister("+86", phone, password, verificationCode, name);
    }
    
    /**
     * 创建支付宝注册请求
     */
    public static RegisterRequest createAlipayRegister(String alipayUserId, String name, String avatarUrl, String phone) {
        RegisterRequest request = new RegisterRequest();
        request.setThirdPartyUserId(alipayUserId);
        request.setName(name);
        request.setAvatarUrl(avatarUrl);
        request.setPhone(phone);
        request.setCountryCode("+86"); // 支付宝默认中国
        request.setRegisterSource("ALIPAY");
        request.setPassword(""); // 第三方用户不需要密码
        return request;
    }
    
    /**
     * 创建淘宝注册请求
     */
    public static RegisterRequest createTaobaoRegister(String taobaoUserId, String name, String avatarUrl, String phone) {
        RegisterRequest request = new RegisterRequest();
        request.setThirdPartyUserId(taobaoUserId);
        request.setName(name);
        request.setAvatarUrl(avatarUrl);
        request.setPhone(phone);
        request.setCountryCode("+86"); // 淘宝默认中国
        request.setRegisterSource("TAOBAO");
        request.setPassword(""); // 第三方用户不需要密码
        return request;
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
     * 验证注册请求是否完整
     */
    public boolean isValid() {
        sanitize();
        
        if (isPhoneRegister()) {
            return phone != null && !phone.trim().isEmpty() &&
                   password != null && !password.trim().isEmpty() &&
                   verificationCode != null && !verificationCode.trim().isEmpty() &&
                   isValidCountryCode();
        } else if (isThirdPartyRegister()) {
            return thirdPartyUserId != null && !thirdPartyUserId.trim().isEmpty();
        }
        
        return false;
    }
    
    /**
     * 获取注册来源描述
     */
    public String getRegisterSourceDescription() {
        if (registerSource == null) return "未知";
        
        switch (registerSource.toUpperCase()) {
            case "PHONE":
                return "手机号注册";
            case "ALIPAY":
                return "支付宝注册";
            case "TAOBAO":
                return "淘宝注册";
            case "WECHAT":
                return "微信注册";
            default:
                return "未知来源";
        }
    }
}