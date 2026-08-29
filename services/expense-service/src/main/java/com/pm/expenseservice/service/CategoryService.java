package com.pm.expenseservice.service;

import com.pm.expenseservice.dto.request.CreateCategoryRequest;
import com.pm.expenseservice.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> getAvailableCategories(UUID userId);
    CategoryResponse createCustomCategory(UUID userId, CreateCategoryRequest request);
}
