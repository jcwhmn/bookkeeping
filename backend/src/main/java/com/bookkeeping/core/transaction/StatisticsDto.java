package com.bookkeeping.core.transaction;

import java.math.BigDecimal;

public record StatisticsDto(
    Long totalIncome,
    Long totalExpense,
    Long netBalance,
    Long transactionCount,
    CategoryBreakdown[] incomeBreakdown,
    CategoryBreakdown[] expenseBreakdown
) {
    public record CategoryBreakdown(
        Long categoryId,
        String categoryName,
        Long amount,
        Long count,
        Double percentage
    ) {}
}