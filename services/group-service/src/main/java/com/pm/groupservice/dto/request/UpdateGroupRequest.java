package com.pm.groupservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update group details request payload")
public class UpdateGroupRequest {

    @Size(max = 255, message = "Group name cannot exceed 255 characters")
    @Schema(example = "Summer Trip 2026 Updated", description = "Updated group display name")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(example = "Updated description", description = "Updated group description")
    private String description;

    @Size(max = 50, message = "Group type cannot exceed 50 characters")
    @Schema(example = "TRIP", description = "Updated group type")
    private String groupType;

    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @Schema(example = "USD", description = "Updated group currency ISO code")
    private String currency;
}
