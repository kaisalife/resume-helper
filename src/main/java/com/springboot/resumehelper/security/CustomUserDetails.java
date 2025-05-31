package com.springboot.resumehelper.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// 继承 Spring Security 的 User 类，添加用户 ID 字段
public class CustomUserDetails extends User {
    private final Long userId;

    public CustomUserDetails(Long userId, String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}