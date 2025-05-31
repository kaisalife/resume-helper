package com.springboot.resumehelper.service.impl;

import com.springboot.resumehelper.model.entity.User;
import com.springboot.resumehelper.repository.UserRepository;
import com.springboot.resumehelper.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("用户不存在: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("加载用户: {}, 密码长度: {}", user.getUsername(), user.getPassword().length());
        log.info("加载id: {}", user.getId());
        return new CustomUserDetails(
                user.getId(),           // 用户 ID
                user.getUsername(),     // 用户名
                user.getPassword(),     // 密码
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))          // 权限列表
        );
    }
}