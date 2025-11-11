package com.ecommerce.user.filter;

import com.ecommerce.user.service.UserService;
import com.ecommerce.user.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwtToken = null;

            // 检查 Authorization 头
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);
                log.info("JWT token found in request to: {}", request.getRequestURI());

                try {
                    // 直接从 JWT token 中提取用户ID
                    Long userId = jwtTokenUtil.extractUserId(jwtToken);
                    log.info("从JWT token中提取的用户ID: {}", userId);
                    
                    // 验证 token 是否有效
                    if (userId != null && jwtTokenUtil.validateToken(jwtToken)) {
                        // 根据用户ID查找用户
                        var userOpt = userService.getUserById(userId);
                        if (userOpt.isPresent()) {
                            var user = userOpt.get();
                            log.info("找到用户: id={}, name={}, phone={}", user.getId(), user.getName(), user.getPhone());

                            // 验证 token 和用户是否匹配
                            if (jwtTokenUtil.validateToken(jwtToken, user)) {
                                // 创建认证对象 - 使用用户ID作为 principal
                                UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        user.getId().toString(),  // 使用用户ID作为 principal
                                        null,                    // 密码为 null
                                        java.util.Collections.emptyList()  // 权限列表
                                    );
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                
                                // 设置认证信息到 SecurityContext
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.info("用户认证成功: userId={}, phone={}", user.getId(), user.getPhone());
                            } else {
                                log.warn("JWT token与用户不匹配");
                            }
                        } else {
                            log.warn("未找到用户: userId={}", userId);
                        }
                    } else {
                        log.warn("JWT token无效或用户ID为空");
                    }
                } catch (Exception e) {
                    log.error("JWT token解析失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("JWT过滤器处理过程中发生错误: {}", e.getMessage());
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}