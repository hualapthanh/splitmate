package com.pm.authservice.service;

import com.pm.authservice.dto.request.ChangePasswordRequest;
import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.LogoutRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.AccountResponse;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.RegisterResponse;

import java.util.UUID;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(LogoutRequest request);
    void changePassword(UUID accountId, ChangePasswordRequest request);
    AccountResponse getCurrentAccount(UUID accountId);
}
