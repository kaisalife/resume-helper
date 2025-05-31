package com.springboot.resumehelper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用所有默认的安全配置
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 允许所有请求，不进行认证
                )
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF 保护
                .formLogin(AbstractHttpConfigurer::disable) // 禁用表单登录
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用 HTTP 基本认证
                .logout(AbstractHttpConfigurer::disable) // 禁用注销功能
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS) // 使用无状态会话
                );
        return http.build();
    }
}