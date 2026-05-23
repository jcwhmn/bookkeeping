package com.bookkeeping.core.budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBudgetRequest(
    @NotNull Long categoryId,
    @NotNull @Positive Long amount,
    @NotNull Integer year,
    @NotNull Integer month
) {}