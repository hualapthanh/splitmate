package com.pm.userservice.controller;

import com.pm.userservice.dto.request.UpdateAvatarRequest;
import com.pm.userservice.dto.request.UpdateProfileRequest;
import com.pm.userservice.dto.response.ProfileResponse;
import com.pm.userservice.dto.response.PublicProfileResponse;
import com.pm.userservice.security.UserPrincipal;
import com.pm.userservice.security.annotation.CurrentUser;
import com.pm.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Endpoints for user profile retrieval, updates, and public profiles")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get current user profile", description = "Returns full profile details of the currently authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ProfileResponse getCurrentUserProfile(@CurrentUser UserPrincipal principal) {
        return userService.getCurrentUserProfile(principal.getUserId());
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update current user profile", description = "Updates full name, phone number, bio, timezone, or locale.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or invalid timezone"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ProfileResponse updateProfile(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(principal.getUserId(), request);
    }

    @PutMapping("/avatar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user avatar URL", description = "Updates avatar image URL for the current user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Avatar updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or invalid URL"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ProfileResponse updateAvatar(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody UpdateAvatarRequest request
    ) {
        return userService.updateAvatar(principal.getUserId(), request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get public user profile", description = "Returns public profile (name, avatar, bio) for any user without authentication.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Public profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public PublicProfileResponse getPublicProfile(@PathVariable("id") UUID userId) {
        return userService.getPublicProfile(userId);
    }
}
