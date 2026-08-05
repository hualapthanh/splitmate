package com.pm.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current account details response payload")
public class AccountResponse {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Account unique identifier")
    private UUID id;

    @Schema(example = "john@example.com", description = "Account email address")
    private String email;

    @Schema(example = "USER", description = "Assigned user role (USER, ADMIN)")
    private String role;

    @Schema(example = "ACTIVE", description = "Account status (ACTIVE, LOCKED, DISABLED)")
    private String status;

    @Schema(example = "2026-08-04T12:00:00Z", description = "Account creation timestamp")
    private OffsetDateTime createdAt;
}
