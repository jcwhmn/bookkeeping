package com.bookkeeping.core.category;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.common.enums.CategoryType;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<CategoryDto> getCurrentUserCategories() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return categoryRepository.findByUserIdOrderBySortOrderAsc(userId).stream()
                .map(categoryMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByType(CategoryType type) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return categoryRepository.findByUserIdAndCategoryTypeOrderBySortOrderAsc(userId, type).stream()
                .map(categoryMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> searchByName(String name, CategoryType type) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Category> categories;
        if (type != null) {
            categories = categoryRepository.findByUserIdAndNameContainingIgnoreCaseAndCategoryType(userId, name, type);
        } else {
            categories = categoryRepository.findByUserIdAndNameContainingIgnoreCase(userId, name);
        }
        return categories.stream().map(categoryMapper::toDto).toList();
    }

    @Transactional
    public CategoryDto createCategory(CategoryController.CategoryCreateRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        CategoryType type = request.type() != null ? CategoryType.fromValue(request.type()) : null;

        if (type == null) {
            throw new BusinessException(ResultCode.CATEGORY_TYPE_INVALID, "Category type is required");
        }
        if (categoryRepository.existsByNameAndUserId(request.name(), userId)) {
            throw new BusinessException(ResultCode.CATEGORY_ALREADY_EXISTS,
                    "Category '" + request.name() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.name())
                .categoryType(type)
                .userId(userId)
                .parentId(request.parentId())
                .icon(request.icon())
                .color(request.color())
                .comment(request.comment())
                .sortOrder(0)
                .hidden(false)
                .build();
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return categoryRepository.findByIdAndUserId(id, userId)
                .map(categoryMapper::toDto);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryController.CategoryUpdateRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Category existing = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "Category not found"));

        CategoryType newType = request.type() != null ? CategoryType.fromValue(request.type()) : existing.getCategoryType();

        // Use applyUpdate() to preserve id (which lives in BaseEntity and is
        // NOT included in Lombok's toBuilder). Without this, the save() would
        // INSERT a new row instead of UPDATE the existing one.
        Category updated = existing.applyUpdate(c -> c.toBuilder()
                .name(request.name() != null ? request.name() : existing.getName())
                .categoryType(newType)
                .icon(request.icon())
                .color(request.color())
                .comment(request.comment())
                .build());

        return categoryMapper.toDto(categoryRepository.save(updated));
    }

    @Transactional
    public void hideCategory(Long id, boolean hidden) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "Category not found"));
        // Use applyUpdate() to preserve id (see comment in updateCategory)
        categoryRepository.save(category.applyUpdate(c -> c.toBuilder().hidden(hidden).build()));
    }

    @Transactional
    public void reorderCategories(List<Long> categoryIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < categoryIds.size(); i++) {
            Long categoryId = categoryIds.get(i);
            Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                    .orElseThrow(() -> new BusinessException(
                            ResultCode.CATEGORY_NOT_FOUND,
                            "Category " + categoryId + " not found or not owned by user"));
            final int newSortOrder = i;
            // Use applyUpdate() to preserve id (see comment in updateCategory)
            categoryRepository.save(category.applyUpdate(c -> c.toBuilder().sortOrder(newSortOrder).build()));
        }
    }

    @Transactional
    public List<CategoryDto> batchCreate(List<BatchCreateItem> items) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return items.stream().map(item -> {
            if (item.categoryType() == null) {
                throw new BusinessException(ResultCode.CATEGORY_TYPE_INVALID, "Category type is required");
            }
            Category category = Category.builder()
                    .name(item.name())
                    .categoryType(item.categoryType())
                    .userId(userId)
                    .parentId(item.parentId())
                    .icon(item.icon())
                    .color(item.color())
                    .comment(item.comment())
                    .sortOrder(0)
                    .hidden(false)
                    .build();
            return categoryMapper.toDto(categoryRepository.save(category));
        }).toList();
    }

    public record BatchCreateItem(String name, CategoryType categoryType, Long parentId,
                                  String icon, String color, String comment) {}
}
