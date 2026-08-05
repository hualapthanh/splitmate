package com.pm.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change password request payload")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Schema(example = "Password@123", description = "User's current password")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z\\d@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,64}$",
        message = "New password must be 8-64 characters and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character"
    )
    @Schema(example = "Password@456", description = "New password complying with complexity policy")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    @Schema(example = "Password@456", description = "Confirmation matching new password")
    private String confirmPassword;
}
