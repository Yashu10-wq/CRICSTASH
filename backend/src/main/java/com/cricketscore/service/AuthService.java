package com.cricketscore.service;

import com.cricketscore.dto.request.LoginRequest;
import com.cricketscore.dto.request.RegisterRequest;
import com.cricketscore.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
