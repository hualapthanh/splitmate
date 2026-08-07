package com.pm.userservice.dto.response;

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
@Schema(description = "Public profile response payload")
public class PublicProfileResponse {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "User unique identifier")
    private UUID userId;

    @Schema(example = "John Doe", description = "User full display name")
    private String fullName;

    @Schema(example = "https://example.com/avatars/john.jpg", description = "Avatar image URL")
    private String avatarUrl;

    @Schema(example = "Software Developer and SplitMate enthusiast.", description = "Public biography")
    private String bio;
}
