package com.pm.userservice.dto.response;

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
@Schema(description = "Private profile response payload")
public class ProfileResponse {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "User unique identifier")
    private UUID userId;

    @Schema(example = "John Doe", description = "User full display name")
    private String fullName;

    @Schema(example = "https://example.com/avatars/john.jpg", description = "Avatar image URL")
    private String avatarUrl;

    @Schema(example = "+1234567890", description = "User phone number")
    private String phoneNumber;

    @Schema(example = "Software Developer and SplitMate enthusiast.", description = "User biography")
    private String bio;

    @Schema(example = "Asia/Ho_Chi_Minh", description = "Preferred timezone")
    private String timezone;

    @Schema(example = "en", description = "Preferred language/locale")
    private String locale;

    @Schema(example = "2026-08-04T12:00:00Z", description = "Profile creation timestamp")
    private OffsetDateTime createdAt;

    @Schema(example = "2026-08-05T10:00:00Z", description = "Profile last update timestamp")
    private OffsetDateTime updatedAt;
}
