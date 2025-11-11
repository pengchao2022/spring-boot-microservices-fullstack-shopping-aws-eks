package com.ecommerce.user.service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.model.VerificationCode;
import com.ecommerce.user.model.dto.LoginRequest;
import com.ecommerce.user.model.dto.RegisterRequest;
import com.ecommerce.user.model.dto.AuthResponse;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.repository.VerificationCodeRepository;
import com.ecommerce.user.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // 自定义业务异常类
    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
    
    // 支付宝用户信息类
    public static class AlipayUserInfo {
        private String alipayUserId;
        private String name;
        private String phone;
        private String avatarUrl;
        
        public AlipayUserInfo(String alipayUserId, String name, String phone, String avatarUrl) {
            this.alipayUserId = alipayUserId;
            this.name = name;
            this.phone = phone;
            this.avatarUrl = avatarUrl;
        }
        
        // Getters
        public String getAlipayUserId() { return alipayUserId; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getAvatarUrl() { return avatarUrl; }
    }
    
    // 支付宝配置 - 请替换为你的实际配置
    private static final String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do";
    private static final String APP_ID = "2021006103655907"; // 你的APP_ID
    private static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDIj6uW9cZSMOb3uhxYTf0pf/J0IfFSUW6a2y+nQL8ZmuFHRIHJasVVH+/27ulzYlxlMp5+9SfmAWeeDkSb1ctgoslFJI32sKKs5Qw7uH6LMhoJHR6shIk40FKBLn1VvgeFggo+p6ODN1vm82JMNZYYUXTuHplPzRiCAPmYgJWoIWyDDPtC55rCo3PSUVtNHkjR9kzG+pODixlAdQ3pnfAaUVbs6zNgLotKDHDm0m6TjnWvvToxtA08r1YOtq6WAsXEr+bBQMFvhqMsDLEvF9g0iQwk3IQmma52DiJIqLQo9n7JZQeq1HYJqo8MG6D5GEeUSOVDWcINzoDylew8rDIrAgMBAAECggEAZpFzA2T145q2xAId9NsNmWehqtqg+6RBmFh36mUmVgXrJ/NVHJjKClqm8fYE1cl7zxUwEWV23h3hy+coFzojJGyb2gxzvbFfwGPy+afEr5MT1Y1a3od5VeDUENShrHPejNyQOLKq4LQy/82Ae9D7zbv6vLxRU4pj+jhdbSx1XOGydhZZikxWYxZAOQT+c4qyoy8l5TUw6CDZZ4a6K0Q58a30PXBhzDUHwVRRQ4K9oBPVU1CNZSdATlx/xGxaPbTNay3dJDyfCUcJWpsJ3zuyQ1VLEtAqjsXTDTcE8mGFhxR7Yy7jnHBltvlWlprCOiHvhZX+J2h1XKsWCDNiF+JPAQKBgQD2Vr6yyIedQt/B8ZeQRBUfpcoOL0Z+etLT9abQmMfsfY3bczZbUSGJrOSwPDcN9Swix/onJcPFRpAA9JV392TVmTvCB1QKMnaCG1GxIq/yCQeq/h4r/yiVH/0YT4pH0zCNdggapBKxjtTOqLZZPlikm8DhyWw7+RJklfqDoXjXIQKBgQDQbVDCvpWio99pSl4EaK/mbm1/He2JEAn8sklDA4zcrH+7jH3TGtKPPNKNXrbdtb7jLlrLBW0L8aAA7DVTwEM7+Ics3iWEwE0PusCmFZcXZcXEsNjSrZCN3vzEHobiFH9gwOBnHp5PYGoKsUtdgbSn1RQpA8LV/3d/6FArsKU7ywKBgApRXdFtNsjueSLNdLS1NVFLB2iKsGAx0szP+Dm6fH06UQvFCpzOjCIRHM8I5qFuCHg4ehDTsxx7NSLlG7GXqiCMN4WL+wgmTvJqJITP2CDCIhEWbbsYB+IfIeG3yynw/ZKfQ/2hq6rGOcGiLWkVhG74mx6Z6i+k4hFWetSymbYhAoGBAI1rDpgEXl0rGWREfEQ7j9Ym6Q6ODOSpyEz33zOkDgiQ1l43tgvtsB5WI3qeYC9QqMNWaW5FMTcga/MUUh6QXk4rk/RCimxnWiIpEZvfHFRYznZlk8hm0aUhPpoHKHfvdnn8hIYTRVEQVwMIRt2cKyqRLguiLKmsm7ViuDrcsVA/AoGBAOCRccO4LYdKpqOP4DuAMxXjxn9q3hXCSjsWqXyABhLJM63r6x7z8xgzRZ+RvxOShbZIZDwug0g6d3plGGR5uPIk7fTlqXtTyqRlmzmDwGVXvaztU4mszMpRQ8pT0a2Z7VLW+yJLK/36nSJFtLgzZbdmr3xm1JzRoqdKwV21Vwsv";
    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjZZrsTprAIrdZySGkFUOoWLvTWutG3PntcvssBZ1u5rm4l16maevIvicDR6u9fPyFDJjrpLVijcw8rZR10ET3xltP4vkz0EMQfM/trLu7wb1/pTI1t8pC6Lf10+JZ9sxx5qGtYk7QX9BGQHf1ACd17OQ+2xKPjqxKQxy6svoT4gyBEAI5Ist0MqgjvQwP/FDkUv9I+5UWBx9a05reMd5Gdul2ToS8fqo/zeoxQJ1p/3pPY4Ve1xsj3Gv2iNLYLNa7QjLhgi0zxjNloWUUqO0Yd9dJzYJeCict+BKHf5HuIdOn90mt6HXbIZYG0ALaAg+kZcDOlLLSQ1RGJ6/kqlaowIDAQAB";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";
    private static final String FORMAT = "json";
    
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisService redisService;
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("用户登录请求: 国家区号={}, 手机号={}, 登录类型={}", 
                request.getCountryCode(), request.getPhone(), request.getLoginType());
        
        // 数据清理和验证
        request.sanitize();
        if (!request.isValid()) {
            throw new BusinessException("登录请求参数不完整");
        }
        
        // 根据登录类型进行验证
        if (request.isVerificationLogin()) {
            // 验证码登录
            return handleVerificationLogin(request);
        } else if (request.isPasswordLogin()) {
            // 密码登录
            return handlePasswordLogin(request);
        } else if (request.isThirdPartyLogin()) {
            // 第三方登录
            return handleThirdPartyLogin(request);
        } else {
            throw new BusinessException("不支持的登录类型");
        }
    }
    
    @Transactional
    public AuthResponse handleVerificationLogin(LoginRequest request) {
        log.info("处理验证码登录: 国家区号={}, 手机号={}", request.getCountryCode(), request.getPhone());
        
        try {
            // 1. 首先验证验证码
            validateVerificationCodeForLogin(request.getCountryCode(), request.getPhone(), request.getVerificationCode());
            
            // 2. 查找用户，如果不存在则自动创建
            User user = userRepository.findByCountryCodeAndPhone(request.getCountryCode(), request.getPhone())
                    .orElseGet(() -> {
                        log.info("用户不存在，自动创建用户: 国家区号={}, 手机号={}", request.getCountryCode(), request.getPhone());
                        String defaultName = generateDefaultName(request.getPhone());
                        String tempPassword = passwordEncoder.encode("temp_" + System.currentTimeMillis());
                        User newUser = User.createPhoneUser(request.getCountryCode(), request.getPhone(), tempPassword, defaultName);
                        User savedUser = userRepository.save(newUser);
                        log.info("自动创建用户成功: ID={}, 完整手机号={}", savedUser.getId(), savedUser.getFullPhone());
                        return savedUser;
                    });
            
            // 3. 检查用户状态
            if (!user.isActive()) {
                log.warn("登录失败: 用户 {} 状态为 {}", user.getFullPhone(), user.getStatus());
                throw new BusinessException("用户账户不可用");
            }
            
            // 4. 更新登录信息
            user.updateLoginInfo(getClientIp());
            userRepository.save(user);
            
            // 5. 生成token - 使用 JwtTokenUtil
            String token = jwtTokenUtil.generateToken(user);
            
            log.info("验证码登录成功: ID={}, 完整手机号={}", user.getId(), user.getFullPhone());
            
            return buildAuthResponse(token, user);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证码登录过程中发生未知错误", e);
            throw new BusinessException("登录失败，请重试");
        }
    }
    
    @Transactional
    public AuthResponse handlePasswordLogin(LoginRequest request) {
        log.info("处理密码登录: 国家区号={}, 手机号={}", request.getCountryCode(), request.getPhone());
        
        try {
            // 查找用户
            User user = userRepository.findByCountryCodeAndPhone(request.getCountryCode(), request.getPhone())
                    .orElseThrow(() -> {
                        log.warn("登录失败: 手机号 {} 不存在", request.getFullPhone());
                        return new BusinessException("用户不存在");
                    });
            
            // 检查用户状态
            if (!user.isActive()) {
                log.warn("登录失败: 用户 {} 状态为 {}", user.getFullPhone(), user.getStatus());
                throw new BusinessException("用户账户不可用");
            }
            
            // 验证密码
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                log.warn("密码登录失败: 用户 {} 未设置密码", user.getFullPhone());
                throw new BusinessException("请先设置密码或使用验证码登录");
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("密码登录失败: 完整手机号={}", request.getFullPhone());
                throw new BusinessException("密码错误");
            }
            
            // 更新登录信息
            user.updateLoginInfo(getClientIp());
            userRepository.save(user);
            
            // 生成token - 使用 JwtTokenUtil
            String token = jwtTokenUtil.generateToken(user);
            
            log.info("密码登录成功: ID={}, 完整手机号={}", user.getId(), user.getFullPhone());
            
            return buildAuthResponse(token, user);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("密码登录过程中发生未知错误", e);
            throw new BusinessException("登录失败，请重试");
        }
    }
    
    @Transactional
    public AuthResponse handleThirdPartyLogin(LoginRequest request) {
        log.info("处理第三方登录: 类型={}, 第三方用户ID={}", request.getLoginType(), request.getThirdPartyUserId());
        
        if ("alipay".equalsIgnoreCase(request.getLoginType())) {
            return handleAlipayLogin(request);
        } else if ("taobao".equalsIgnoreCase(request.getLoginType())) {
            return handleTaobaoLogin(request);
        } else {
            throw new BusinessException("不支持的第三方登录类型");
        }
    }
    
    @Transactional
    public AuthResponse handleAlipayLogin(LoginRequest request) {
        log.info("支付宝登录请求: authCode={}", request.getAuthCode());
        
        try {
            // 调用支付宝API获取用户信息
            AlipayUserInfo userInfo = getAlipayUserInfo(request.getAuthCode());
            
            if (userInfo == null || userInfo.getAlipayUserId() == null) {
                throw new BusinessException("获取支付宝用户信息失败");
            }
            
            log.info("获取到支付宝用户信息: userId={}, name={}, phone={}", 
                    userInfo.getAlipayUserId(), userInfo.getName(), userInfo.getPhone());
            
            // 查找或创建用户
            User user = userRepository.findByAlipayUserId(userInfo.getAlipayUserId())
                    .orElseGet(() -> {
                        // 创建新用户，设置默认密码
                        String tempPassword = passwordEncoder.encode("temp_" + System.currentTimeMillis());
                        
                        // 使用从支付宝获取的真实手机号
                        String phone = userInfo.getPhone();
                        if (phone != null && phone.startsWith("+86")) {
                            phone = phone.substring(3); // 移除+86前缀
                        }
                        
                        // 如果支付宝没有返回手机号，使用虚拟手机号避免约束冲突
                        if (phone == null || phone.trim().isEmpty()) {
                            phone = "alipay_" + System.currentTimeMillis();
                            log.warn("支付宝未返回手机号，使用虚拟手机号: {}", phone);
                        }
                        
                        log.info("创建新支付宝用户: alipayUserId={}, phone={}", 
                                userInfo.getAlipayUserId(), phone);
                        
                        User newUser = User.createAlipayUser(
                            userInfo.getAlipayUserId(), 
                            userInfo.getName() != null ? userInfo.getName() : "支付宝用户",
                            userInfo.getAvatarUrl() != null ? userInfo.getAvatarUrl() : "",
                            "+86", 
                            phone
                        );
                        return userRepository.save(newUser);
                    });
            
            // 更新登录信息
            user.updateLoginInfo(getClientIp());
            userRepository.save(user);
            
            // 生成token - 使用 JwtTokenUtil
            String token = jwtTokenUtil.generateToken(user);
            
            log.info("支付宝登录成功: ID={}, alipayUserId={}, phone={}", 
                    user.getId(), user.getAlipayUserId(), user.getPhone());
            
            return buildAuthResponse(token, user);
                    
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付宝登录异常: {}", e.getMessage(), e);
            throw new BusinessException("支付宝登录失败: " + e.getMessage());
        }
    }
    
    @Transactional
    public AuthResponse handleTaobaoLogin(LoginRequest request) {
        log.info("淘宝登录请求: taobaoUserId={}", request.getThirdPartyUserId());
        
        try {
            // 安全处理字段长度
            String safeTaobaoUserId = request.getThirdPartyUserId() != null ? 
                request.getThirdPartyUserId().substring(0, Math.min(18, request.getThirdPartyUserId().length())) : "taobao_unknown";
            String safeName = "淘宝用户_" + safeTaobaoUserId.substring(Math.max(0, safeTaobaoUserId.length() - 6));
            String safeAvatarUrl = "";
            
            // 查找或创建用户
            User user = userRepository.findByTaobaoUserId(safeTaobaoUserId)
                    .orElseGet(() -> {
                        // 创建新用户
                        User newUser = User.createTaobaoUser(safeTaobaoUserId, safeName, safeAvatarUrl, 
                                                           request.getCountryCode(), request.getPhone());
                        log.info("创建新淘宝用户: taobaoUserId={} (长度: {})", safeTaobaoUserId, safeTaobaoUserId.length());
                        return userRepository.save(newUser);
                    });
            
            // 更新登录信息
            user.updateLoginInfo(getClientIp());
            userRepository.save(user);
            
            // 生成token - 使用 JwtTokenUtil
            String token = jwtTokenUtil.generateToken(user);
            
            log.info("淘宝登录成功: ID={}, taobaoUserId={}", user.getId(), user.getTaobaoUserId());
            
            return buildAuthResponse(token, user);
                    
        } catch (Exception e) {
            log.error("淘宝登录异常: {}", e.getMessage(), e);
            throw new BusinessException("淘宝登录失败: " + e.getMessage());
        }
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("用户注册请求: 国家区号={}, 手机号={}", request.getCountryCode(), request.getPhone());
        
        try {
            // 数据清理和验证
            request.sanitize();
            if (!request.isValid()) {
                throw new BusinessException("注册请求参数不完整");
            }
            
            // 验证国家区号格式
            if (!request.isValidCountryCode()) {
                throw new BusinessException("国家区号格式不正确");
            }
            
            // 验证手机号格式
            if (!request.isValidPhoneFormat()) {
                throw new BusinessException("手机号格式不正确");
            }
            
            // 验证手机号是否已存在
            if (userRepository.existsByCountryCodeAndPhone(request.getCountryCode(), request.getPhone())) {
                log.warn("注册失败: 手机号 {} 已存在", request.getFullPhone());
                throw new BusinessException("手机号已注册");
            }
            
            // 验证密码强度
            if (!isValidPassword(request.getPassword())) {
                throw new BusinessException("密码必须包含字母和数字，且长度在6-20位之间");
            }
            
            // 验证验证码
            validateVerificationCode(request.getCountryCode(), request.getPhone(), request.getVerificationCode());
            
            // 创建用户
            User user = User.createPhoneUser(
                request.getCountryCode(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                request.generateDefaultName()
            );
            
            User savedUser = userRepository.save(user);
            
            // 生成token - 使用 JwtTokenUtil
            String token = jwtTokenUtil.generateToken(savedUser);
            
            log.info("用户注册成功: ID={}, 完整手机号={}", savedUser.getId(), savedUser.getFullPhone());
            
            return buildAuthResponse(token, savedUser);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户注册过程中发生未知错误", e);
            throw new BusinessException("注册失败，请重试");
        }
    }
    
    // 支付宝相关方法保持不变
    private AlipayUserInfo getAlipayUserInfo(String authCode) {
        try {
            log.info("开始调用支付宝API，authCode: {}", authCode);
            
            // 1. 使用auth_code获取access_token
            String accessToken = getAlipayAccessToken(authCode);
            if (accessToken == null) {
                throw new BusinessException("获取支付宝访问令牌失败");
            }
            
            log.info("成功获取支付宝access_token: {}", accessToken);
            
            // 2. 使用access_token获取用户信息
            AlipayUserInfo userInfo = getAlipayUserInfoByToken(accessToken);
            if (userInfo == null) {
                throw new BusinessException("获取支付宝用户信息失败");
            }
            
            log.info("支付宝用户信息获取成功: userId={}, name={}, phone={}", 
                    userInfo.getAlipayUserId(), userInfo.getName(), userInfo.getPhone());
            
            return userInfo;
            
        } catch (Exception e) {
            log.error("获取支付宝用户信息失败", e);
            throw new BusinessException("支付宝授权失败: " + e.getMessage());
        }
    }
    
    private String getAlipayAccessToken(String authCode) {
        try {
            // 构建请求参数
            String bizContent = String.format(
                "{\"grant_type\":\"authorization_code\",\"code\":\"%s\"}",
                authCode
            );
            
            // 构建系统参数
            String systemParams = String.format(
                "app_id=%s&method=alipay.system.oauth.token&charset=%s&sign_type=%s&timestamp=%s&version=1.0&format=%s",
                APP_ID, CHARSET, SIGN_TYPE, 
                java.net.URLEncoder.encode(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), CHARSET),
                FORMAT
            );
            
            // 构建请求数据
            String requestData = systemParams + "&biz_content=" + java.net.URLEncoder.encode(bizContent, CHARSET);
            
            // 这里需要添加签名逻辑，但由于没有支付宝SDK，暂时使用简化版本
            // 实际生产环境应该使用支付宝SDK进行签名
            
            log.info("调用支付宝oauth.token接口，authCode: {}", authCode);
            
            // 简化实现 - 实际应该使用支付宝SDK
            // 这里返回模拟的access_token，实际应该从支付宝API响应中提取
            return "mock_access_token_" + System.currentTimeMillis();
            
        } catch (Exception e) {
            log.error("获取支付宝access_token失败", e);
            return null;
        }
    }
    
    private AlipayUserInfo getAlipayUserInfoByToken(String accessToken) {
        try {
            log.info("使用access_token获取用户信息: {}", accessToken);
            
            // 简化实现 - 实际应该调用 alipay.user.info.share 接口
            // 这里返回模拟数据，实际应该从支付宝API响应中提取真实数据
            
            // 模拟从支付宝获取的用户信息
            String alipayUserId = "2088" + String.format("%016d", System.currentTimeMillis() % 10000000000000000L);
            String name = "支付宝用户";
            String phone = "185" + String.format("%08d", Math.abs(alipayUserId.hashCode()) % 100000000);
            String avatarUrl = "";
            
            log.info("模拟支付宝用户信息: userId={}, name={}, phone={}", alipayUserId, name, phone);
            
            return new AlipayUserInfo(alipayUserId, name, phone, avatarUrl);
            
        } catch (Exception e) {
            log.error("获取支付宝用户信息失败", e);
            return null;
        }
    }
    
    // 验证码相关方法保持不变
    @Transactional
    public void sendVerificationCode(String countryCode, String phone) {
        log.info("发送验证码请求: 国家区号={}, 手机号={}", countryCode, phone);
        
        try {
            // 清理数据
            String cleanCountryCode = cleanCountryCode(countryCode);
            String cleanPhone = cleanPhoneNumber(phone);
            
            log.info("清理后: 国家区号={}, 手机号={}", cleanCountryCode, cleanPhone);
            
            // 验证国家区号格式
            if (!isValidCountryCode(cleanCountryCode)) {
                throw new BusinessException("国家区号格式不正确");
            }
            
            // 验证手机号格式
            if (!isValidPhone(cleanPhone)) {
                throw new BusinessException("手机号格式不正确");
            }
            
            // 检查发送频率（防止滥用）
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            long recentCount = verificationCodeRepository.countByCountryCodeAndPhoneAndCreatedAtAfter(
                cleanCountryCode, cleanPhone, oneMinuteAgo);
            
            if (recentCount >= 3) {
                log.warn("验证码发送过于频繁: 完整手机号={}", cleanCountryCode + cleanPhone);
                throw new BusinessException("验证码发送过于频繁，请稍后再试");
            }
            
            // 生成6位数字验证码
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            
            // 保存到数据库 - 使用 create 静态方法
            VerificationCode verificationCode = VerificationCode.create(cleanCountryCode, cleanPhone, code, 5);
            verificationCodeRepository.save(verificationCode);
            
            // 同时存储到Redis，设置5分钟过期（用于快速验证）
            String redisKey = "verification_code:" + cleanCountryCode + "_" + cleanPhone;
            redisService.set(redisKey, code, 300);
            
            // 开发环境下打印验证码到日志
            log.info("验证码已发送: 完整手机号={}, 验证码={} (有效期5分钟)", 
                    cleanCountryCode + cleanPhone, code);
                    
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发送验证码过程中发生未知错误", e);
            throw new BusinessException("发送验证码失败，请重试");
        }
    }
    
    @Transactional
    public void sendVerificationCode(String phone) {
        // 向后兼容的方法，使用默认国家区号
        sendVerificationCode("+86", phone);
    }
    
    public boolean verifyCode(String countryCode, String phone, String code) {
        try {
            log.info("验证验证码: 国家区号={}, 手机号={}", countryCode, phone);
            
            // 清理数据
            String cleanCountryCode = cleanCountryCode(countryCode);
            String cleanPhone = cleanPhoneNumber(phone);
            
            // 直接使用现有的验证逻辑
            validateVerificationCode(cleanCountryCode, cleanPhone, code);
            
            log.info("验证码验证成功: 完整手机号={}", cleanCountryCode + cleanPhone);
            return true;
            
        } catch (BusinessException e) {
            log.warn("验证码验证失败: 完整手机号={}, 错误={}", 
                    countryCode + phone, e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("验证码验证过程中发生未知错误: 完整手机号={}, 错误={}", 
                    countryCode + phone, e.getMessage());
            return false;
        }
    }
    
    public boolean verifyCode(String phone, String code) {
        // 向后兼容的方法，使用默认国家区号
        return verifyCode("+86", phone, code);
    }
    
    // 专门用于登录的验证码验证方法
    private void validateVerificationCodeForLogin(String countryCode, String phone, String code) {
        validateVerificationCode(countryCode, phone, code);
    }
    
    private void validateVerificationCode(String countryCode, String phone, String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("验证码不能为空");
        }
        
        // 清理数据
        String cleanCountryCode = cleanCountryCode(countryCode);
        String cleanPhone = cleanPhoneNumber(phone);
        
        log.debug("开始验证验证码 - 完整手机号: {}, 输入验证码: {}", 
                 cleanCountryCode + cleanPhone, code);
        
        // 首先检查Redis中的验证码（快速验证）
        String redisKey = "verification_code:" + cleanCountryCode + "_" + cleanPhone;
        String redisCode = redisService.get(redisKey);
        log.debug("从Redis获取的验证码: {}", redisCode);
        
        if (redisCode != null) {
            // 去掉Redis值中的引号（如果有）
            String cleanRedisCode = redisCode.replace("\"", "");
            log.debug("清理后的Redis验证码: {}, 输入验证码: {}, 是否相等: {}", 
                     cleanRedisCode, code, code.equals(cleanRedisCode));
            
            if (code.equals(cleanRedisCode)) {
                // Redis验证成功，但不删除Redis中的验证码，依靠Redis过期时间自动清理
                log.debug("Redis验证码验证成功，但不删除验证码，等待自动过期");
                // 标记数据库中的验证码为已使用
                verificationCodeRepository.markAsUsed(cleanCountryCode, cleanPhone, code);
                return;
            }
        }
        
        // Redis验证失败，检查数据库
        log.debug("Redis验证失败，检查数据库验证码");
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByCountryCodeAndPhoneAndCodeAndUsedFalseAndExpiresAtAfter(
                    cleanCountryCode, cleanPhone, code, LocalDateTime.now());
        
        if (verificationCodeOpt.isEmpty()) {
            log.debug("数据库验证码验证失败");
            throw new BusinessException("验证码错误或已过期");
        }
        
        // 验证成功，标记为已使用
        VerificationCode verificationCode = verificationCodeOpt.get();
        verificationCode.markAsUsed();
        verificationCodeRepository.save(verificationCode);
        log.debug("数据库验证码验证成功");
    }
    
    @Transactional
    public void bindPhone(Long userId, String countryCode, String phone, String verificationCode) {
        log.info("绑定手机号请求: userId={}, 国家区号={}, 手机号={}", userId, countryCode, phone);
        
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("用户不存在"));
            
            // 验证国家区号格式
            if (!isValidCountryCode(countryCode)) {
                throw new BusinessException("国家区号格式不正确");
            }
            
            // 验证手机号是否已被其他用户绑定
            if (userRepository.existsByCountryCodeAndPhone(countryCode, phone)) {
                throw new BusinessException("手机号已被其他用户绑定");
            }
            
            // 验证验证码
            validateVerificationCode(countryCode, phone, verificationCode);
            
            // 绑定手机号
            user.bindPhone(countryCode, phone);
            userRepository.save(user);
            
            log.info("手机号绑定成功: userId={}, 完整手机号={}", userId, countryCode + phone);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("绑定手机号过程中发生未知错误", e);
            throw new BusinessException("绑定手机号失败，请重试");
        }
    }
    
    // 辅助方法保持不变
    /**
     * 生成默认用户名
     */
    private String generateDefaultName(String phone) {
        if (phone != null && phone.length() >= 4) {
            return "用户_" + phone.substring(phone.length() - 4);
        }
        return "用户";
    }
    
    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(user)
                .phone(user.getPhone())
                .countryCode(user.getCountryCode())
                .name(user.getName())
                .userId(user.getId())
                .build();
    }
    
    private String cleanCountryCode(String countryCode) {
        if (countryCode == null) return "+86";
        String cleanCode = countryCode.trim();
        if (cleanCode.isEmpty()) {
            return "+86";
        }
        // 确保以+开头
        if (!cleanCode.startsWith("+")) {
            cleanCode = "+" + cleanCode;
        }
        return cleanCode;
    }
    
    private String cleanPhoneNumber(String phone) {
        if (phone == null) return null;
        // 移除所有非数字字符
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        log.debug("手机号清理: {} -> {}", phone, cleanPhone);
        return cleanPhone;
    }
    
    private boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) return false;
        return countryCode.matches("^\\+[1-9]\\d{0,3}$");
    }
    
    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        // 基础手机号格式验证，可根据具体国家调整
        // 中国手机号：1开头，11位数字
        if (phone.matches("^1[3-9]\\d{9}$")) {
            return true;
        }
        // 国际手机号：最少6位，最多15位数字
        return phone.matches("^[0-9]{6,15}$");
    }
    
    private boolean isValidPassword(String password) {
        // 密码必须包含字母和数字，长度6-20位
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$");
    }
    
    private String getClientIp() {
        // 实现获取客户端IP的逻辑
        // 在实际应用中，可以从HttpServletRequest中获取
        // 这里返回模拟IP
        return "127.0.0.1";
    }
    
    // 清理过期验证码的定时任务方法
    @Transactional
    public void cleanupExpiredVerificationCodes() {
        log.info("开始清理过期验证码");
        verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
        log.info("清理过期验证码完成");
    }
    
    // 获取用户统计信息
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long phoneUsers = userRepository.countByRegisterSource(User.RegisterSource.PHONE);
        long alipayUsers = userRepository.countByRegisterSource(User.RegisterSource.ALIPAY);
        long taobaoUsers = userRepository.countByRegisterSource(User.RegisterSource.TAOBAO);
        
        // 手动构建 UserStats
        UserStats stats = new UserStats();
        stats.setTotalUsers(totalUsers);
        stats.setPhoneUsers(phoneUsers);
        stats.setAlipayUsers(alipayUsers);
        stats.setTaobaoUsers(taobaoUsers);
        return stats;
    }
    
    // 按国家区号获取用户统计信息
    public UserStats getUserStatsByCountryCode(String countryCode) {
        long totalUsers = userRepository.countByCountryCode(countryCode);
        long phoneUsers = userRepository.countByCountryCodeAndRegisterSource(countryCode, User.RegisterSource.PHONE);
        long alipayUsers = userRepository.countByCountryCodeAndRegisterSource(countryCode, User.RegisterSource.ALIPAY);
        long taobaoUsers = userRepository.countByCountryCodeAndRegisterSource(countryCode, User.RegisterSource.TAOBAO);
        
        UserStats stats = new UserStats();
        stats.setTotalUsers(totalUsers);
        stats.setPhoneUsers(phoneUsers);
        stats.setAlipayUsers(alipayUsers);
        stats.setTaobaoUsers(taobaoUsers);
        return stats;
    }
    
    // 用户统计信息内部类
    public static class UserStats {
        private long totalUsers;
        private long phoneUsers;
        private long alipayUsers;
        private long taobaoUsers;
        
        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getPhoneUsers() { return phoneUsers; }
        public void setPhoneUsers(long phoneUsers) { this.phoneUsers = phoneUsers; }
        
        public long getAlipayUsers() { return alipayUsers; }
        public void setAlipayUsers(long alipayUsers) { this.alipayUsers = alipayUsers; }
        
        public long getTaobaoUsers() { return taobaoUsers; }
        public void setTaobaoUsers(long taobaoUsers) { this.taobaoUsers = taobaoUsers; }
    }
}