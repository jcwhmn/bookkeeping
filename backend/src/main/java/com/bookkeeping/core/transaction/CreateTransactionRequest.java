package com.bookkeeping.core.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionRequest(
    @NotNull Integer transactionType,
    @NotNull Long accountId,
    Long categoryId,
    Long destinationAccountId,
    @NotNull Long amount,
    @NotBlank String description,
    Long transactionTime,
    String tagIds
) {}