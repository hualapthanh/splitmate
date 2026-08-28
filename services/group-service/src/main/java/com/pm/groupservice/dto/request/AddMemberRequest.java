package com.pm.groupservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Add group member request payload")
public class AddMemberRequest {

    @NotNull(message = "User ID is required")
    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "User unique identifier to add to group")
    private UUID userId;
}
