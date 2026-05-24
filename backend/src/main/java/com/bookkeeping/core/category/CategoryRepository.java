package com.bookkeeping.core.category;

import com.bookkeeping.common.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrderBySortOrderAsc(Long userId);
    List<Category> findByUserIdAndCategoryTypeOrderBySortOrderAsc(Long userId, CategoryType categoryType);
    boolean existsByNameAndUserId(String name, Long userId);
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE Category c SET c.sortOrder = :sortOrder WHERE c.id = :id AND c.userId = :userId")
    int updateSortOrder(@Param("id") Long id, @Param("userId") Long userId, @Param("sortOrder") Integer sortOrder);

    @Modifying
    @Query("UPDATE Category c SET c.hidden = :hidden WHERE c.id = :id AND c.userId = :userId")
    int updateHidden(@Param("id") Long id, @Param("userId") Long userId, @Param("hidden") Boolean hidden);
}