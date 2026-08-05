package com.pm.authservice.controller;

import com.pm.authservice.dto.request.ChangePasswordRequest;
import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.LogoutRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.AccountResponse;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.dto.response.RegisterResponse;
import com.pm.authservice.security.AccountPrincipal;
import com.pm.authservice.security.annotation.CurrentUser;
import com.pm.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Management", description = "Endpoints for User Authentication, Registration, and Token Management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account", description = "Creates a new user account and publishes UserRegistered event to Kafka.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or password mismatch"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Authenticate user credentials", description = "Verifies email and password, returning JWT access token and refresh token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account locked or disabled")
    })
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refresh access token", description = "Performs Refresh Token Rotation: revokes old refresh token and issues new token pair.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Refresh token expired or revoked")
    })
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logout user", description = "Revokes the provided refresh token.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully logged out"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change password", description = "Updates user password and revokes all existing active refresh tokens.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or incorrect password"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void changePassword(
            @CurrentUser AccountPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(principal.getId(), request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get current authenticated account", description = "Returns details of the currently logged-in user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account details retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public AccountResponse getCurrentAccount(@CurrentUser AccountPrincipal principal) {
        return authService.getCurrentAccount(principal.getId());
    }
}
