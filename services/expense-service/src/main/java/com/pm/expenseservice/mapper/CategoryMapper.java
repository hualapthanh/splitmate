package com.pm.expenseservice.mapper;

import com.pm.expenseservice.dto.request.CreateCategoryRequest;
import com.pm.expenseservice.dto.response.CategoryResponse;
import com.pm.expenseservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Category toCategory(CreateCategoryRequest request);

    @Mapping(target = "isSystemCategory", expression = "java(category.getCreatedBy() == null)")
    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categories);
}
