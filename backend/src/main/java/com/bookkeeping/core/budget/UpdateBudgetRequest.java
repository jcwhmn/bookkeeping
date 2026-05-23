package com.bookkeeping.core.budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateBudgetRequest(
    @NotNull @Positive Long amount
) {}