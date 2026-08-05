package com.pm.authservice.service.impl;

import com.pm.authservice.config.JwtProperties;
import com.pm.authservice.dto.request.ChangePasswordRequest;
import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.LogoutRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.AccountResponse;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.RegisterResponse;
import com.pm.authservice.entity.Account;
import com.pm.authservice.entity.RefreshToken;
import com.pm.authservice.event.AuthEventPublisher;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.ErrorCode;
import com.pm.authservice.exception.ResourceNotFoundException;
import com.pm.authservice.exception.UnauthorizedException;
import com.pm.authservice.mapper.AuthMapper;
import com.pm.authservice.repository.AccountRepository;
import com.pm.authservice.security.RefreshTokenService;
import com.pm.authservice.security.jwt.JwtProvider;
import com.pm.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthMapper authMapper;
    private final AuthEventPublisher authEventPublisher;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.AUTH_001, "Confirm password does not match password");
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.AUTH_004, "Email already exists");
        }

        Account account = Account.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .accountStatus("ACTIVE")
                .emailVerified(false)
                .build();

        Account savedAccount = accountRepository.save(account);

        String accessToken = jwtProvider.generateAccessToken(savedAccount);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedAccount);

        authEventPublisher.publishUserRegistered(savedAccount.getId(), savedAccount.getEmail());

        return RegisterResponse.builder()
                .accountId(savedAccount.getId())
                .email(savedAccount.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_002, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new UnauthorizedException(ErrorCode.AUTH_002, "Invalid credentials");
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getAccountStatus())) {
            throw new BusinessException(ErrorCode.AUTH_003, "Account is locked or inactive");
        }

        String accessToken = jwtProvider.generateAccessToken(account);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(account);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.rotateRefreshToken(request.getRefreshToken());
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }

    @Override
    @Transactional
    public void changePassword(UUID accountId, ChangePasswordRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPasswordHash())) {
            throw new UnauthorizedException(ErrorCode.AUTH_002, "Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.AUTH_001, "Confirm password does not match new password");
        }

        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new BusinessException(ErrorCode.AUTH_001, "New password must be different from current password");
        }

        account.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        // Revoke all active refresh tokens for this account
        refreshTokenService.revokeAllByAccountId(accountId);

        // Publish PasswordChanged Event
        authEventPublisher.publishPasswordChanged(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getCurrentAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        return authMapper.toAccountResponse(account);
    }
}
