package com.pm.authservice.service;

import com.pm.authservice.config.JwtProperties;
import com.pm.authservice.dto.request.ChangePasswordRequest;
import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.RegisterResponse;
import com.pm.authservice.entity.Account;
import com.pm.authservice.entity.RefreshToken;
import com.pm.authservice.event.AuthEventPublisher;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.ErrorCode;
import com.pm.authservice.exception.UnauthorizedException;
import com.pm.authservice.mapper.AuthMapper;
import com.pm.authservice.repository.AccountRepository;
import com.pm.authservice.security.RefreshTokenService;
import com.pm.authservice.security.jwt.JwtProvider;
import com.pm.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private AuthEventPublisher authEventPublisher;
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthServiceImpl authService;

    private Account sampleAccount;
    private UUID sampleAccountId;

    @BeforeEach
    void setUp() {
        sampleAccountId = UUID.randomUUID();
        sampleAccount = Account.builder()
                .id(sampleAccountId)
                .email("test@example.com")
                .passwordHash("hashed_password")
                .role("USER")
                .accountStatus("ACTIVE")
                .emailVerified(false)
                .build();
    }

    @Test
    @DisplayName("register() should create account, generate tokens, and publish event when valid")
    void register_success() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token("random_refresh_token")
                .build();

        when(accountRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashed_password");
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);
        when(jwtProvider.generateAccessToken(sampleAccount)).thenReturn("sample_access_token");
        when(refreshTokenService.createRefreshToken(sampleAccount)).thenReturn(refreshToken);
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(900L);

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("sample_access_token", response.getAccessToken());
        assertEquals("random_refresh_token", response.getRefreshToken());
        verify(authEventPublisher).publishUserRegistered(sampleAccountId, "test@example.com");
    }

    @Test
    @DisplayName("register() should throw BusinessException when email exists")
    void register_duplicateEmail_throwsException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        when(accountRepository.existsByEmail("test@example.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(request));
        assertEquals(ErrorCode.AUTH_004, ex.getErrorCode());
    }

    @Test
    @DisplayName("login() should return tokens when credentials are valid")
    void login_success() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token("random_refresh_token")
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("Password@123", "hashed_password")).thenReturn(true);
        when(jwtProvider.generateAccessToken(sampleAccount)).thenReturn("sample_access_token");
        when(refreshTokenService.createRefreshToken(sampleAccount)).thenReturn(refreshToken);
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(900L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("sample_access_token", response.getAccessToken());
        assertEquals("random_refresh_token", response.getRefreshToken());
    }

    @Test
    @DisplayName("login() should throw UnauthorizedException on invalid password")
    void login_invalidPassword_throwsUnauthorizedException() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword")
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("WrongPassword", "hashed_password")).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals(ErrorCode.AUTH_002, ex.getErrorCode());
    }

    @Test
    @DisplayName("login() should throw BusinessException when account is locked")
    void login_accountLocked_throwsBusinessException() {
        sampleAccount.setAccountStatus("LOCKED");
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("Password@123", "hashed_password")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
        assertEquals(ErrorCode.AUTH_003, ex.getErrorCode());
    }

    @Test
    @DisplayName("changePassword() should update password hash and revoke tokens")
    void changePassword_success() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Password@123")
                .newPassword("NewPassword@123")
                .confirmPassword("NewPassword@123")
                .build();

        when(accountRepository.findById(sampleAccountId)).thenReturn(Optional.of(sampleAccount));
        when(passwordEncoder.matches("Password@123", "hashed_password")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("new_hashed_password");

        authService.changePassword(sampleAccountId, request);

        verify(accountRepository).save(sampleAccount);
        verify(refreshTokenService).revokeAllByAccountId(sampleAccountId);
        verify(authEventPublisher).publishPasswordChanged(sampleAccountId);
    }
}
