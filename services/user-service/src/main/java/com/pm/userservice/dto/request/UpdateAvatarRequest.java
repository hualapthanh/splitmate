package com.pm.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Avatar update request payload")
public class UpdateAvatarRequest {

    @NotBlank(message = "Avatar URL is required")
    @URL(message = "Avatar URL must be a valid URL")
    @Size(max = 512, message = "Avatar URL cannot exceed 512 characters")
    @Schema(example = "https://example.com/avatars/user123.jpg", description = "Public URL of avatar image")
    private String avatarUrl;
}
