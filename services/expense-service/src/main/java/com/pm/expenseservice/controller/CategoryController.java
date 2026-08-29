package com.pm.expenseservice.controller;

import com.pm.expenseservice.dto.request.CreateCategoryRequest;
import com.pm.expenseservice.dto.response.CategoryResponse;
import com.pm.expenseservice.security.UserPrincipal;
import com.pm.expenseservice.security.annotation.CurrentUser;
import com.pm.expenseservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Endpoints for retrieving system categories and creating user custom categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get available categories", description = "Returns system categories and custom categories created by the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public List<CategoryResponse> getAvailableCategories(@CurrentUser UserPrincipal principal) {
        return categoryService.getAvailableCategories(principal.getUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create custom category", description = "Creates a custom category for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or category name already exists")
    })
    public CategoryResponse createCustomCategory(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return categoryService.createCustomCategory(principal.getUserId(), request);
    }
}
