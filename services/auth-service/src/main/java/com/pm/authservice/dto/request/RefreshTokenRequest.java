package com.pm.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Refresh token request payload")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(example = "d8f1b2a3-c4e5-4f6a-8b9c-0d1e2f3a4b5c", description = "Valid refresh token string")
    private String refreshToken;
}
