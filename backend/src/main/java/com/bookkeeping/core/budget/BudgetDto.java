package com.bookkeeping.core.budget;

public record BudgetDto(
    Long id,
    Long categoryId,
    String categoryName,
    Long amount,
    Integer year,
    Integer month,
    Long spent,
    Double percentUsed
) {}