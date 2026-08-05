package com.pm.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registration success response payload")
public class RegisterResponse {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Newly created account ID")
    private UUID accountId;

    @Schema(example = "john@example.com", description = "User email address")
    private String email;

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "JWT Access Token")
    private String accessToken;

    @Schema(example = "d8f1b2a3-c4e5-4f6a-8b9c-0d1e2f3a4b5c", description = "Opaque Refresh Token")
    private String refreshToken;

    @Schema(example = "900", description = "Access token validity duration in seconds (15 mins)")
    private long expiresIn;

    @Builder.Default
    @Schema(example = "Bearer", description = "Token Authorization type")
    private String tokenType = "Bearer";
}
