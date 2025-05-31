package com.springboot.resumehelper.controller.auth;

import com.springboot.resumehelper.model.dto.auth.LoginDTO;
import com.springboot.resumehelper.model.dto.auth.RegisterDTO;
import com.springboot.resumehelper.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = authService.login(loginDTO);
            System.out.println("ok--token");
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            // 密码错误，返回 401 状态码
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        } catch (UsernameNotFoundException e) {
            // 用户不存在，返回 404 状态码
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        } catch (Exception e) {
            // 其他认证异常，返回 500 状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("认证失败: " + e.getMessage());
        }
    }
}    