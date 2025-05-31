package com.springboot.resumehelper.service.impl;

import com.springboot.resumehelper.model.dto.auth.LoginDTO;
import com.springboot.resumehelper.model.dto.auth.RegisterDTO;
import com.springboot.resumehelper.model.entity.User;
import com.springboot.resumehelper.repository.UserRepository;
import com.springboot.resumehelper.service.AuthService;
import com.springboot.resumehelper.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        userRepository.save(user);
    }
    @Override
    public String login(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            return jwtUtil.generateToken(authentication.getName());
        } catch (BadCredentialsException e) {
            // 密码错误
            throw new BadCredentialsException("用户名或密码错误");
        } catch (UsernameNotFoundException e) {
            // 用户不存在
            throw new UsernameNotFoundException("用户不存在");
        } catch (Exception e) {
            // 其他认证异常
            throw new RuntimeException("认证失败: " + e.getMessage(), e);
        }
    }
}    