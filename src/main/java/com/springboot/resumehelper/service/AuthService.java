package com.springboot.resumehelper.service;

import com.springboot.resumehelper.model.dto.auth.LoginDTO;
import com.springboot.resumehelper.model.dto.auth.RegisterDTO;

public interface AuthService {

    void register(RegisterDTO registerDTO);

    String login(LoginDTO loginDTO);
}    