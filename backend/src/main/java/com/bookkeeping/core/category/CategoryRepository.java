package com.bookkeeping.core.category;

import com.bookkeeping.common.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrderBySortOrderAsc(Long userId);
    List<Category> findByUserIdAndCategoryTypeOrderBySortOrderAsc(Long userId, CategoryType categoryType);
    List<Category> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
    List<Category> findByUserIdAndNameContainingIgnoreCaseAndCategoryType(Long userId, String name, CategoryType categoryType);
    boolean existsByNameAndUserId(String name, Long userId);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    void deleteByUserId(Long userId);

    // Note: Removed @Modifying @Query methods.
    // All entity updates now follow the consistent pattern:
    //   1. fetch entity via findByIdAndUserId
    //   2. apply changes via toBuilder() in service layer
    //   3. persist via save()
    // This keeps the JPA lifecycle intact (@PreUpdate hooks, dirty checking)
    // and centralizes business rules in the service.
}
