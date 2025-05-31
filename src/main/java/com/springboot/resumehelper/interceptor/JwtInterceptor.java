package com.springboot.resumehelper.interceptor;

import com.springboot.resumehelper.security.CustomUserDetails;
import com.springboot.resumehelper.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        System.out.println(requestUri);
        if (requestUri.startsWith("/api/auth/register") || requestUri.startsWith("/api/auth/login")||
                requestUri.startsWith("/api/error")) {
            logger.info("Request to {} is excluded from JWT validation", requestUri);
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader: "+authHeader);
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            logger.error("Missing or invalid Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            // 从 token 中提取用户名
            String username = jwtUtil.extractUsername(token);

            // 加载用户详细信息
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            // 使用两个参数的 validateToken 方法
            if (!jwtUtil.validateToken(token, userDetails)) {
                logger.error("Invalid token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return false;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,                        // 存储 CustomUserDetails 对象
                    null,
                    userDetails.getAuthorities()
            );
            logger.info("用户 {} (ID={}) 已认证", userDetails.getUsername(), userDetails.getUserId());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token expired", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return false;
        } catch (Exception e) {
            logger.error("Error validating token", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error validating token");
            return false;
        }
    }
}