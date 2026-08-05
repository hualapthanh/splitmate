package com.pm.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.RegisterResponse;
import com.pm.authservice.exception.GlobalExceptionHandler;
import com.pm.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register should return 201 Created on valid input")
    void register_shouldReturn201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("john@example.com")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        RegisterResponse response = RegisterResponse.builder()
                .accountId(UUID.randomUUID())
                .email("john@example.com")
                .accessToken("mock_access_token")
                .refreshToken("mock_refresh_token")
                .expiresIn(900)
                .tokenType("Bearer")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.accessToken").value("mock_access_token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login should return 200 OK on valid credentials")
    void login_shouldReturn200() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("Password@123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("mock_access_token")
                .refreshToken("mock_refresh_token")
                .expiresIn(900)
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("mock_refresh_token"));
    }
}
