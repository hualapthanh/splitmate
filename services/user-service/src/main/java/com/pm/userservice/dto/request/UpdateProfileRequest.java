package com.pm.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "User profile update request payload")
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    @Schema(example = "John Doe", description = "User full display name")
    private String fullName;

    @Pattern(regexp = "^$|^\\+?[0-9]{8,15}$", message = "Invalid phone number format. E.g. +1234567890")
    @Schema(example = "+1234567890", description = "Contact phone number")
    private String phoneNumber;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    @Schema(example = "Software Developer and SplitMate enthusiast.", description = "User biography/description")
    private String bio;

    @Size(max = 50, message = "Timezone cannot exceed 50 characters")
    @Schema(example = "Asia/Ho_Chi_Minh", description = "User preferred timezone identifier")
    private String timezone;

    @Size(max = 10, message = "Locale cannot exceed 10 characters")
    @Schema(example = "en", description = "User preferred language/locale code")
    private String locale;
}
