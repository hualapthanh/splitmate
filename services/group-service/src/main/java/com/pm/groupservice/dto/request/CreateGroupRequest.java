package com.pm.groupservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create new group request payload")
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 255, message = "Group name cannot exceed 255 characters")
    @Schema(example = "Summer Trip 2026", description = "Group display name")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(example = "Beach vacation expense tracking group", description = "Group description")
    private String description;

    @NotBlank(message = "Group type is required")
    @Size(max = 50, message = "Group type cannot exceed 50 characters")
    @Schema(example = "TRIP", description = "Group type (e.g. TRIP, ROOMMATES, HOME, COUPLE, OTHER)")
    private String groupType;

    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code (e.g. VND, USD)")
    @Schema(example = "VND", description = "Default group currency ISO code")
    private String currency;
}
