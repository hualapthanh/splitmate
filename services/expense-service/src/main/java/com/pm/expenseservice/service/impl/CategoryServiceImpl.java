package com.pm.expenseservice.service.impl;

import com.pm.expenseservice.dto.request.CreateCategoryRequest;
import com.pm.expenseservice.dto.response.CategoryResponse;
import com.pm.expenseservice.entity.Category;
import com.pm.expenseservice.exception.BusinessException;
import com.pm.expenseservice.exception.ErrorCode;
import com.pm.expenseservice.mapper.CategoryMapper;
import com.pm.expenseservice.repository.CategoryRepository;
import com.pm.expenseservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAvailableCategories(UUID userId) {
        List<Category> categories = categoryRepository.findAllAvailableForUser(userId);
        return categoryMapper.toCategoryResponseList(categories);
    }

    @Override
    @Transactional
    public CategoryResponse createCustomCategory(UUID userId, CreateCategoryRequest request) {
        String categoryName = request.getName().trim();

        if (categoryRepository.existsByNameAndCreatedBy(categoryName, userId)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Custom category with name '" + categoryName + "' already exists");
        }

        Category category = categoryMapper.toCategory(request);
        category.setName(categoryName);
        category.setCreatedBy(userId);

        Category savedCategory = categoryRepository.save(category);
        log.info("Created custom category '{}' (id: {}) for userId: {}", savedCategory.getName(), savedCategory.getId(), userId);

        return categoryMapper.toCategoryResponse(savedCategory);
    }
}
