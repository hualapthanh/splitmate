package com.pm.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account registration request payload")
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Schema(example = "john@example.com", description = "Unique user email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z\\d@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,64}$",
        message = "Password must be 8-64 characters and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character"
    )
    @Schema(example = "Password@123", description = "Password complying with complexity policy")
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Schema(example = "Password@123", description = "Confirmation password matching password field")
    private String confirmPassword;
}
