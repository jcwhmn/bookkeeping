package com.bookkeeping.core.category;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.common.enums.CategoryType;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final SecurityUtils securityUtils;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
                           SecurityUtils securityUtils) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.securityUtils = securityUtils;
    }

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

    @Transactional
    public CategoryDto createCategory(String name, CategoryType type, Long parentId) {
        Long userId = securityUtils.requireCurrentUser().getId();
        if (categoryRepository.existsByNameAndUserId(name, userId)) {
            throw new BusinessException(ResultCode.CATEGORY_ALREADY_EXISTS, "Category '" + name + "' already exists");
        }
        Category category = Category.builder()
                .name(name)
                .categoryType(type)
                .userId(userId)
                .parentId(parentId)
                .sortOrder(0)
                .hidden(false)
                .build();
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Transactional
    public void hideCategory(Long id, boolean hidden) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "Category not found"));
        categoryRepository.save(category.toBuilder().hidden(hidden).build());
    }

    @Transactional
    public void reorderCategories(List<Long> orderedIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < orderedIds.size(); i++) {
            categoryRepository.updateSortOrder(orderedIds.get(i), userId, i);
        }
    }

    @Transactional
    public List<CategoryDto> batchCreate(List<BatchCreateItem> items) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return items.stream().map(item -> {
            Category category = Category.builder()
                    .name(item.name())
                    .categoryType(item.categoryType())
                    .userId(userId)
                    .parentId(item.parentId())
                    .sortOrder(0)
                    .hidden(false)
                    .build();
            return categoryMapper.toDto(categoryRepository.save(category));
        }).toList();
    }

    public record BatchCreateItem(String name, CategoryType categoryType, Long parentId) {}
}