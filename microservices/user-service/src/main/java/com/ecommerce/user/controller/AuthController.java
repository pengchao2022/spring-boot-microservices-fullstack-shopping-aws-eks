package com.ecommerce.user.controller;

import com.ecommerce.user.model.dto.LoginRequest;
import com.ecommerce.user.model.dto.RegisterRequest;
import com.ecommerce.user.model.dto.AuthResponse;
import com.ecommerce.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    // 添加业务异常处理
    @ExceptionHandler(AuthService.BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(AuthService.BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", e.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "error");
        
        return ResponseEntity.badRequest().body(response);
    }
    
    // 添加通用异常处理
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "系统繁忙，请稍后重试");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "error");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    // 原有的测试接口
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        log.info("🔍 测试接口被调用 - 验证路由是否正常");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Auth Service is working!");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "user-service");
        response.put("status", "healthy");
        response.put("endpoints", new String[] {
            "/auth/login", "/auth/register", "/auth/verification-code", 
            "/auth/health", "/auth/test", "/auth/alipay/callback"
        });
        
        log.info("✅ 测试接口响应: {}", response);
        return ResponseEntity.ok(response);
    }
    
    // 详细测试接口
    @GetMapping("/test/detailed")
    public ResponseEntity<Map<String, Object>> testDetailed() {
        log.info("🔍 详细测试接口被调用");
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "user-service");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("serverTime", System.currentTimeMillis());
        
        // 添加数据库连接状态
        try {
            response.put("database", "connected");
        } catch (Exception e) {
            response.put("database", "error: " + e.getMessage());
        }
        
        // 添加服务信息
        Map<String, Object> serviceInfo = new HashMap<>();
        serviceInfo.put("name", "user-service");
        serviceInfo.put("version", "1.0.0");
        serviceInfo.put("environment", "production");
        response.put("serviceInfo", serviceInfo);
        
        // 添加可用端点
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("健康检查", "GET /auth/health");
        endpoints.put("测试接口", "GET /auth/test");
        endpoints.put("用户登录", "POST /auth/login");
        endpoints.put("用户注册", "POST /auth/register");
        endpoints.put("发送验证码", "POST /auth/verification-code");
        endpoints.put("验证验证码", "POST /auth/verification-code/verify");
        endpoints.put("支付宝登录", "POST /auth/alipay/login");
        endpoints.put("支付宝回调", "GET /auth/alipay/callback");
        endpoints.put("淘宝登录", "POST /auth/taobao/login");
        endpoints.put("用户统计", "GET /auth/stats");
        response.put("availableEndpoints", endpoints);
        
        log.info("✅ 详细测试接口响应: {}", response);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求: 手机号={}, 登录类型={}", request.getMaskedPhone(), request.getLoginType());
        try {
            AuthResponse response = authService.login(request);
            // 构建成功的响应
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("token", response.getToken());
            successResponse.put("tokenType", response.getTokenType());
            successResponse.put("expiresIn", response.getExpiresIn());
            successResponse.put("user", response.getUser());
            successResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(successResponse);
        } catch (AuthService.BusinessException e) {
            // 业务异常会由 @ExceptionHandler 处理
            throw e;
        } catch (Exception e) {
            log.error("登录过程中发生系统异常", e);
            throw new AuthService.BusinessException("登录失败，请稍后重试");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到注册请求: 手机号={}", request.getMaskedPhone());
        try {
            AuthResponse response = authService.register(request);
            // 构建成功的响应
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("token", response.getToken());
            successResponse.put("tokenType", response.getTokenType());
            successResponse.put("expiresIn", response.getExpiresIn());
            successResponse.put("user", response.getUser());
            successResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(successResponse);
        } catch (AuthService.BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("注册过程中发生系统异常", e);
            throw new AuthService.BusinessException("注册失败，请稍后重试");
        }
    }
    
    @PostMapping("/verification-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestParam String phone,
            @RequestParam(required = false, defaultValue = "+86") String countryCode) {
        log.info("收到发送验证码请求: 国家区号={}, 手机号={}", countryCode, phone);
        try {
            authService.sendVerificationCode(countryCode, phone);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "验证码发送成功");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (AuthService.BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发送验证码过程中发生系统异常", e);
            throw new AuthService.BusinessException("发送验证码失败，请稍后重试");
        }
    }
    
    @PostMapping("/verification-code/verify")
    public ResponseEntity<?> verifyCode(
            @RequestParam String phone,
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "+86") String countryCode) {
        log.info("收到验证验证码请求: 国家区号={}, 手机号={}", countryCode, phone);
        try {
            boolean isValid = authService.verifyCode(countryCode, phone, code);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", isValid);
            response.put("message", isValid ? "验证码正确" : "验证码错误");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("验证验证码过程中发生异常", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("valid", false);
            response.put("error", "验证失败");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 支付宝回调接口 - 重定向到首页
    @GetMapping("/alipay/callback")
    public void alipayCallback(
            @RequestParam String app_id,
            @RequestParam String source,
            @RequestParam String scope,
            @RequestParam String auth_code,
            @RequestParam String state,
            HttpServletResponse httpResponse) throws IOException {
        
        log.info("收到支付宝回调: app_id={}, source={}, scope={}, auth_code={}, state={}", 
                 app_id, source, scope, auth_code, state);
        
        try {
            // 使用 auth_code 进行登录
            LoginRequest request = LoginRequest.createAlipayAuthCodeLogin(auth_code);
            request.setCountryCode("+86");
            
            AuthResponse authResponse = authService.login(request);
            
            log.info("支付宝登录成功: userId={}, alipayUserId={}", 
                    authResponse.getUser().getId(), authResponse.getUser().getAlipayUserId());
            
            // 重定向到首页，携带token信息
            String redirectUrl = "https://awsmpc.asia?" +
                    "token=" + authResponse.getToken() +
                    "&tokenType=" + authResponse.getTokenType() +
                    "&expiresIn=" + authResponse.getExpiresIn() +
                    "&userId=" + authResponse.getUser().getId() +
                    "&alipayUserId=" + authResponse.getUser().getAlipayUserId() +
                    "&userName=" + URLEncoder.encode(authResponse.getUser().getName(), StandardCharsets.UTF_8.toString()) +
                    "&loginSuccess=true" +
                    "&source=alipay" +
                    "&timestamp=" + System.currentTimeMillis();
            
            log.info("重定向到首页: {}", redirectUrl);
            
            // 执行重定向
            httpResponse.sendRedirect(redirectUrl);
            
        } catch (AuthService.BusinessException e) {
            log.error("支付宝回调业务异常: {}", e.getMessage());
            
            // 业务异常时重定向到首页并显示错误信息
            String errorRedirectUrl = "https://awsmpc.asia?" +
                    "loginError=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString()) +
                    "&source=alipay" +
                    "&loginSuccess=false" +
                    "&timestamp=" + System.currentTimeMillis();
            
            log.warn("支付宝登录失败，重定向到首页显示错误: {}", errorRedirectUrl);
            httpResponse.sendRedirect(errorRedirectUrl);
            
        } catch (Exception e) {
            log.error("支付宝回调系统异常: {}", e.getMessage(), e);
            
            // 系统异常时重定向到首页并显示错误信息
            String errorRedirectUrl = "https://awsmpc.asia?" +
                    "loginError=" + URLEncoder.encode("支付宝登录失败，请稍后重试", StandardCharsets.UTF_8.toString()) +
                    "&source=alipay" +
                    "&loginSuccess=false" +
                    "&timestamp=" + System.currentTimeMillis();
            
            log.error("支付宝登录系统异常，重定向到首页显示错误: {}", errorRedirectUrl);
            httpResponse.sendRedirect(errorRedirectUrl);
        }
    }
    
    // 支付宝回调接口的备用版本（返回JSON，用于调试）
    @GetMapping("/alipay/callback/json")
    public ResponseEntity<?> alipayCallbackJson(
            @RequestParam String app_id,
            @RequestParam String source,
            @RequestParam String scope,
            @RequestParam String auth_code,
            @RequestParam String state) {
        
        log.info("收到支付宝回调(JSON版本): app_id={}, source={}, scope={}, auth_code={}, state={}", 
                 app_id, source, scope, auth_code, state);
        
        try {
            // 使用 auth_code 进行登录
            LoginRequest request = LoginRequest.createAlipayAuthCodeLogin(auth_code);
            request.setCountryCode("+86");
            
            AuthResponse response = authService.login(request);
            
            // 构建成功的响应
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("token", response.getToken());
            successResponse.put("tokenType", response.getTokenType());
            successResponse.put("expiresIn", response.getExpiresIn());
            successResponse.put("user", response.getUser());
            successResponse.put("timestamp", LocalDateTime.now().toString());
            successResponse.put("redirectUrl", "https://awsmpc.asia?token=" + response.getToken() + "&loginSuccess=true");
            
            log.info("支付宝登录成功(JSON): userId={}", response.getUser().getId());
            
            return ResponseEntity.ok(successResponse);
            
        } catch (AuthService.BusinessException e) {
            log.error("支付宝回调业务异常(JSON): {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", "error");
            
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("支付宝回调系统异常(JSON): {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "支付宝登录失败，请稍后重试");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", "error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // 支付宝登录接口（手动调用）
    @PostMapping("/alipay/login")
    public ResponseEntity<?> alipayLogin(
            @RequestParam String authCode,
            @RequestParam(required = false, defaultValue = "alipay_login") String state) {
        log.info("收到支付宝登录请求: authCode={}, state={}", authCode, state);
        
        try {
            // 使用新的创建方法
            LoginRequest request = LoginRequest.createAlipayAuthCodeLogin(authCode);
            request.setCountryCode("+86");
            
            AuthResponse response = authService.login(request);
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("token", response.getToken());
            successResponse.put("tokenType", response.getTokenType());
            successResponse.put("expiresIn", response.getExpiresIn());
            successResponse.put("user", response.getUser());
            successResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(successResponse);
        } catch (AuthService.BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付宝登录过程中发生系统异常", e);
            throw new AuthService.BusinessException("支付宝登录失败，请稍后重试");
        }
    }
    
    @PostMapping("/taobao/login")
    public ResponseEntity<?> taobaoLogin(
            @RequestParam(required = false) String taobaoUserId,
            @RequestParam(required = false) String authCode) {
        log.info("收到淘宝登录请求: taobaoUserId={}, authCode={}", taobaoUserId, authCode);
        
        try {
            LoginRequest request;
            if (authCode != null && !authCode.trim().isEmpty()) {
                // 使用授权码登录
                request = LoginRequest.createTaobaoAuthCodeLogin(authCode);
            } else if (taobaoUserId != null && !taobaoUserId.trim().isEmpty()) {
                // 使用用户ID登录
                request = LoginRequest.createTaobaoUserIdLogin(taobaoUserId);
            } else {
                throw new AuthService.BusinessException("淘宝登录参数不完整，需要提供authCode或taobaoUserId");
            }
            
            request.setCountryCode("+86");
            
            AuthResponse response = authService.login(request);
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("token", response.getToken());
            successResponse.put("tokenType", response.getTokenType());
            successResponse.put("expiresIn", response.getExpiresIn());
            successResponse.put("user", response.getUser());
            successResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(successResponse);
        } catch (AuthService.BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("淘宝登录过程中发生系统异常", e);
            throw new AuthService.BusinessException("淘宝登录失败，请稍后重试");
        }
    }
    
    @PostMapping("/bind-phone")
    public ResponseEntity<?> bindPhone(
            @RequestParam Long userId,
            @RequestParam String countryCode,
            @RequestParam String phone,
            @RequestParam String verificationCode) {
        log.info("收到绑定手机号请求: userId={}, 国家区号={}, 手机号={}", userId, countryCode, phone);
        try {
            authService.bindPhone(userId, countryCode, phone, verificationCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "手机号绑定成功");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (AuthService.BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("绑定手机号过程中发生系统异常", e);
            throw new AuthService.BusinessException("绑定手机号失败，请稍后重试");
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        log.info("收到获取用户统计信息请求");
        try {
            AuthService.UserStats stats = authService.getUserStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取用户统计信息过程中发生异常", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "获取统计信息失败");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/stats/{countryCode}")
    public ResponseEntity<?> getUserStatsByCountryCode(
            @PathVariable String countryCode) {
        log.info("收到按国家区号获取用户统计信息请求: countryCode={}", countryCode);
        try {
            AuthService.UserStats stats = authService.getUserStatsByCountryCode(countryCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("按国家区号获取用户统计信息过程中发生异常", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "获取统计信息失败");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 健康检查接口
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("健康检查接口被调用");
        return ResponseEntity.ok("Auth Service is healthy - " + LocalDateTime.now());
    }
}