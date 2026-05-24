package com.bookkeeping.core.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndBudgetYearAndBudgetMonth(Long userId, Integer budgetYear, Integer budgetMonth);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(
            Long userId, Long categoryId, Integer budgetYear, Integer budgetMonth);

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.budgetYear = :year AND b.budgetMonth = :month AND b.categoryId = :categoryId AND b.id != :excludeId")
    Optional<Budget> findDuplicate(Long userId, Long categoryId, Integer year, Integer month, Long excludeId);
}