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
        return categoryRepository.findByUserId(userId).stream()
                .map(categoryMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByType(CategoryType type) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return categoryRepository.findByUserIdAndCategoryType(userId, type).stream()
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
                .build();
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }
}
