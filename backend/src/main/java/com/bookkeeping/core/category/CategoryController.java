package com.bookkeeping.core.category;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.enums.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction/categories")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list.json")
    @Operation(summary = "List all categories for current user")
    public ApiResponse<List<CategoryDto>> list(
            @RequestParam(required = false) CategoryType type) {
        if (type != null) {
            return ApiResponse.success(categoryService.getCategoriesByType(type));
        }
        return ApiResponse.success(categoryService.getCurrentUserCategories());
    }

    @PostMapping("/add.json")
    @Operation(summary = "Create category")
    public ApiResponse<CategoryDto> create(@RequestBody CategoryCreateRequest request) {
        return ApiResponse.success(categoryService.createCategory(
                request.name(), request.categoryType(), request.parentId()));
    }

    @PostMapping("/add_batch.json")
    @Operation(summary = "Batch create categories")
    public ApiResponse<List<CategoryDto>> batchCreate(@RequestBody List<CategoryService.BatchCreateItem> items) {
        return ApiResponse.success(categoryService.batchCreate(items));
    }

    @PostMapping("/hide.json")
    @Operation(summary = "Hide/unhide category")
    public ApiResponse<Void> hide(@RequestBody CategoryHideRequest request) {
        categoryService.hideCategory(request.id(), request.hidden());
        return ApiResponse.success(null);
    }

    @PostMapping("/move.json")
    @Operation(summary = "Reorder categories (drag-to-sort)")
    public ApiResponse<Void> reorder(@RequestBody CategoryReorderRequest request) {
        categoryService.reorderCategories(request.orderedIds());
        return ApiResponse.success(null);
    }

    // === Request DTOs ===

    public record CategoryCreateRequest(String name, CategoryType categoryType, Long parentId) {}
    public record CategoryHideRequest(long id, boolean hidden) {}
    public record CategoryReorderRequest(List<Long> orderedIds) {}
}