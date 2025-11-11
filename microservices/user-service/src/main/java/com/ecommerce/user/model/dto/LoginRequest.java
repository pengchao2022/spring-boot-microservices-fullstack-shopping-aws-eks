package com.ecommerce.user.model.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;        // 手机号
    
    @Pattern(regexp = "^\\+[1-9]\\d{0,3}$", message = "国家区号格式不正确")
    private String countryCode = "+86";  // 国家区号，默认中国
    
    private String password;     // 密码（可选，用于密码登录）
    
    private String verificationCode; // 验证码（可选，用于验证码登录）
    
    private String loginType;        // 登录类型：password, verification, alipay, taobao
    
    // 第三方登录相关字段
    private String thirdPartyUserId;  // 第三方用户ID（支付宝、淘宝等）
    private String thirdPartyToken;   // 第三方访问令牌
    private String authCode;          // 第三方授权码
    
    // 设备信息
    private String deviceId;          // 设备ID
    private String userAgent;         // 用户代理
    private String clientType;        // 客户端类型：WEB, APP, H5等
    
    // 验证分组接口
    public interface PasswordLogin {}
    public interface VerificationLogin {}
    public interface ThirdPartyLogin {}
    
    // 业务方法
    
    /**
     * 获取完整手机号（包含国家区号）
     */
    public String getFullPhone() {
        return (countryCode != null ? countryCode : "+86") + phone;
    }
    
    /**
     * 检查是否是密码登录
     */
    public boolean isPasswordLogin() {
        return "password".equalsIgnoreCase(loginType) && 
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * 检查是否是验证码登录
     */
    public boolean isVerificationLogin() {
        return "verification".equalsIgnoreCase(loginType) && 
               verificationCode != null && !verificationCode.trim().isEmpty();
    }
    
    /**
     * 检查是否是第三方登录
     */
    public boolean isThirdPartyLogin() {
        if ("alipay".equalsIgnoreCase(loginType) || "taobao".equalsIgnoreCase(loginType)) {
            // 第三方登录：需要 authCode 或 thirdPartyUserId 之一
            return (authCode != null && !authCode.trim().isEmpty()) || 
                   (thirdPartyUserId != null && !thirdPartyUserId.trim().isEmpty());
        }
        return false;
    }
    
    /**
     * 检查是否是支付宝授权码登录
     */
    public boolean isAlipayAuthCodeLogin() {
        return "alipay".equalsIgnoreCase(loginType) && 
               authCode != null && !authCode.trim().isEmpty();
    }
    
    /**
     * 检查是否是淘宝授权码登录  
     */
    public boolean isTaobaoAuthCodeLogin() {
        return "taobao".equalsIgnoreCase(loginType) && 
               authCode != null && !authCode.trim().isEmpty();
    }
    
    /**
     * 检查是否是支付宝用户ID登录
     */
    public boolean isAlipayUserIdLogin() {
        return "alipay".equalsIgnoreCase(loginType) && 
               thirdPartyUserId != null && !thirdPartyUserId.trim().isEmpty();
    }
    
    /**
     * 检查是否是淘宝用户ID登录
     */
    public boolean isTaobaoUserIdLogin() {
        return "taobao".equalsIgnoreCase(loginType) && 
               thirdPartyUserId != null && !thirdPartyUserId.trim().isEmpty();
    }
    
    /**
     * 检查登录请求是否有效
     */
    public boolean isValid() {
        if (loginType == null) {
            return false;
        }
        
        switch (loginType.toLowerCase()) {
            case "password":
                return isPasswordLogin();
            case "verification":
                return isVerificationLogin();
            case "alipay":
                // 支付宝登录：支持 authCode 或 thirdPartyUserId
                return isAlipayAuthCodeLogin() || isAlipayUserIdLogin();
            case "taobao":
                // 淘宝登录：支持 authCode 或 thirdPartyUserId
                return isTaobaoAuthCodeLogin() || isTaobaoUserIdLogin();
            default:
                return false;
        }
    }
    
    /**
     * 获取登录类型描述
     */
    public String getLoginTypeDescription() {
        if (loginType == null) return "未知";
        
        switch (loginType.toLowerCase()) {
            case "password":
                return "密码登录";
            case "verification":
                return "验证码登录";
            case "alipay":
                return "支付宝登录";
            case "taobao":
                return "淘宝登录";
            default:
                return "未知类型";
        }
    }
    
    /**
     * 验证国家区号格式
     */
    public boolean isValidCountryCode() {
        return countryCode != null && countryCode.matches("^\\+[1-9]\\d{0,3}$");
    }
    
    /**
     * 验证手机号格式
     */
    public boolean isValidPhoneFormat() {
        if (phone == null) return false;
        // 中国手机号：1开头，11位数字
        if (phone.matches("^1[3-9]\\d{9}$")) {
            return true;
        }
        // 国际手机号：最少6位，最多15位数字
        return phone.matches("^[0-9]{6,15}$");
    }
    
    /**
     * 清理数据
     */
    public void sanitize() {
        if (phone != null) {
            phone = phone.trim();
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
        if (loginType != null) {
            loginType = loginType.trim().toLowerCase();
        }
        if (authCode != null) {
            authCode = authCode.trim();
        }
        if (thirdPartyUserId != null) {
            thirdPartyUserId = thirdPartyUserId.trim();
        }
    }
    
    /**
     * 创建密码登录请求
     */
    public static LoginRequest createPasswordLogin(String countryCode, String phone, String password) {
        LoginRequest request = new LoginRequest();
        request.setCountryCode(countryCode);
        request.setPhone(phone);
        request.setPassword(password);
        request.setLoginType("password");
        return request;
    }
    
    /**
     * 创建密码登录请求（使用默认国家区号）
     */
    public static LoginRequest createPasswordLogin(String phone, String password) {
        return createPasswordLogin("+86", phone, password);
    }
    
    /**
     * 创建验证码登录请求
     */
    public static LoginRequest createVerificationLogin(String countryCode, String phone, String verificationCode) {
        LoginRequest request = new LoginRequest();
        request.setCountryCode(countryCode);
        request.setPhone(phone);
        request.setVerificationCode(verificationCode);
        request.setLoginType("verification");
        return request;
    }
    
    /**
     * 创建验证码登录请求（使用默认国家区号）
     */
    public static LoginRequest createVerificationLogin(String phone, String verificationCode) {
        return createVerificationLogin("+86", phone, verificationCode);
    }
    
    /**
     * 创建支付宝授权码登录请求
     */
    public static LoginRequest createAlipayAuthCodeLogin(String authCode) {
        LoginRequest request = new LoginRequest();
        request.setAuthCode(authCode);
        request.setLoginType("alipay");
        return request;
    }
    
    /**
     * 创建支付宝用户ID登录请求
     */
    public static LoginRequest createAlipayUserIdLogin(String thirdPartyUserId) {
        LoginRequest request = new LoginRequest();
        request.setThirdPartyUserId(thirdPartyUserId);
        request.setLoginType("alipay");
        return request;
    }
    
    /**
     * 创建淘宝授权码登录请求
     */
    public static LoginRequest createTaobaoAuthCodeLogin(String authCode) {
        LoginRequest request = new LoginRequest();
        request.setAuthCode(authCode);
        request.setLoginType("taobao");
        return request;
    }
    
    /**
     * 创建淘宝用户ID登录请求
     */
    public static LoginRequest createTaobaoUserIdLogin(String thirdPartyUserId) {
        LoginRequest request = new LoginRequest();
        request.setThirdPartyUserId(thirdPartyUserId);
        request.setLoginType("taobao");
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
     * 获取掩码授权码（用于日志记录）
     */
    public String getMaskedAuthCode() {
        if (authCode == null || authCode.length() < 8) {
            return "***";
        }
        return authCode.substring(0, 4) + "****" + authCode.substring(authCode.length() - 4);
    }
    
    /**
     * 获取掩码第三方用户ID（用于日志记录）
     */
    public String getMaskedThirdPartyUserId() {
        if (thirdPartyUserId == null || thirdPartyUserId.length() < 8) {
            return "***";
        }
        return thirdPartyUserId.substring(0, 4) + "****" + thirdPartyUserId.substring(thirdPartyUserId.length() - 4);
    }
}