package com.pm.expenseservice.dto.request;

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
@Schema(description = "Create custom category request payload")
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    @Schema(example = "Coffee & Snacks", description = "Custom category display name")
    private String name;

    @Size(max = 255, message = "Icon cannot exceed 255 characters")
    @Schema(example = "☕", description = "Category icon emoji or URL")
    private String icon;

    @Size(max = 7, message = "Color code must be a hex color code (e.g. #FF5733)")
    @Schema(example = "#FF5733", description = "Category hex color code")
    private String color;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(example = "Coffee, tea, and quick snacks", description = "Category description")
    private String description;
}
