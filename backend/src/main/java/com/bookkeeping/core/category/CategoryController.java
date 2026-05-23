package com.bookkeeping.core.category;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.enums.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List all categories")
    public ApiResponse<List<CategoryDto>> list(@RequestParam(required = false) CategoryType type) {
        if (type != null) return ApiResponse.success(categoryService.getCategoriesByType(type));
        return ApiResponse.success(categoryService.getCurrentUserCategories());
    }

    @PostMapping
    @Operation(summary = "Create category")
    public ApiResponse<CategoryDto> create(@RequestParam String name,
                                            @RequestParam CategoryType type,
                                            @RequestParam(required = false) Long parentId) {
        return ApiResponse.success(categoryService.createCategory(name, type, parentId));
    }
}
