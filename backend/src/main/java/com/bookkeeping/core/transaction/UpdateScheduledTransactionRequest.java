package com.bookkeeping.core.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Update scheduled transaction request")
public record UpdateScheduledTransactionRequest(
        Integer transactionType,
        Long accountId,
        Long categoryId,
        Long destinationAccountId,
        Long amount,
        String description,
        String tagIds,
        String frequency,
        Integer intervalDays,
        Integer dayOfWeek,
        Integer dayOfMonth,
        Integer monthOfYear,
        Long startDate,
        Long endDate,
        Boolean active
) {}