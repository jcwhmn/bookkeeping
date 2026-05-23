package com.bookkeeping.core.category;

import com.bookkeeping.common.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    List<Category> findByUserIdAndCategoryType(Long userId, CategoryType categoryType);
    boolean existsByNameAndUserId(String name, Long userId);
}
