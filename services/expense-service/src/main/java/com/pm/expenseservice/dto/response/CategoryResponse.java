package com.pm.expenseservice.dto.response;

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
@Schema(description = "Category response payload")
public class CategoryResponse {

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Category unique identifier")
    private UUID id;

    @Schema(example = "Food & Dining", description = "Category name")
    private String name;

    @Schema(example = "🍽️", description = "Category icon")
    private String icon;

    @Schema(example = "#FF6B6B", description = "Category color hex code")
    private String color;

    @Schema(example = "Restaurants, groceries, and coffee", description = "Category description")
    private String description;

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Creator user ID (NULL for system categories)")
    private UUID createdBy;

    @Schema(example = "true", description = "True if system category, false if user custom category")
    private boolean isSystemCategory;
}
