package com.bookkeeping.core.category;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.enums.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management APIs")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * List all categories for current user.
     * Optionally filter by type.
     *
     * Use case: Main categories page - load all user categories, filter by tab.
     */
    @GetMapping
    @Operation(summary = "List categories")
    public ApiResponse<List<CategoryDto>> list(
            @RequestParam(required = false) Integer type) {
        if (type != null) {
            return ApiResponse.success(categoryService.getCategoriesByType(CategoryType.fromValue(type)));
        }
        return ApiResponse.success(categoryService.getCurrentUserCategories());
    }

    /**
     * Search categories by name with optional type filter.
     *
     * Use case: Typeahead search in transaction form, filter dropdowns.
     */
    @GetMapping("/search")
    @Operation(summary = "Search categories by name")
    public ApiResponse<List<CategoryDto>> search(
            @RequestParam String name,
            @RequestParam(required = false) Integer type) {
        CategoryType categoryType = type != null ? CategoryType.fromValue(type) : null;
        return ApiResponse.success(categoryService.searchByName(name, categoryType));
    }

    /**
     * Get a single category by ID.
     *
     * Use case: Edit dialog - fetch full category data (including icon, color, comment)
     * when user clicks edit. Also useful for verifying category exists before operations.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ApiResponse<CategoryDto> getById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(
                        com.bookkeeping.common.ResultCode.CATEGORY_NOT_FOUND.getCodeValue(),
                        com.bookkeeping.common.ResultCode.CATEGORY_NOT_FOUND.getMessage()));
    }

    /**
     * Create a single category.
     */
    @PostMapping
    @Operation(summary = "Create category")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryDto> create(@RequestBody CategoryCreateRequest request) {
        CategoryDto created = categoryService.createCategory(request);
        return ApiResponse.success(created);
    }

    /**
     * Update a category.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ApiResponse<CategoryDto> update(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request) {
        CategoryDto updated = categoryService.updateCategory(id, request);
        return ApiResponse.success(updated);
    }

    /**
     * Hide or unhide a category.
     */
    @PatchMapping("/{id}/hidden")
    @Operation(summary = "Hide or unhide category")
    public ApiResponse<Void> hide(
            @PathVariable Long id,
            @RequestBody CategoryHideRequest request) {
        categoryService.hideCategory(id, request.hidden());
        return ApiResponse.success(null);
    }

    /**
     * Reorder categories (drag-to-sort).
     * Sends complete new order as array of category IDs.
     */
    @PutMapping("/reorder")
    @Operation(summary = "Reorder categories")
    public ApiResponse<Void> reorder(@RequestBody CategoryReorderRequest request) {
        categoryService.reorderCategories(request.categoryIds());
        return ApiResponse.success(null);
    }

    // === Request DTOs ===
    // Note: MapStructPlus currently only supports Direction.From (Entity -> DTO).
    // Request -> Entity conversion is done explicitly in the service layer.

    public record CategoryCreateRequest(
            String name,
            Integer type,
            String icon,
            String color,
            String comment,
            Long parentId
    ) {}

    public record CategoryUpdateRequest(
            String name,
            Integer type,
            String icon,
            String color,
            String comment
    ) {}

    public record CategoryHideRequest(boolean hidden) {}

    public record CategoryReorderRequest(List<Long> categoryIds) {}
}
