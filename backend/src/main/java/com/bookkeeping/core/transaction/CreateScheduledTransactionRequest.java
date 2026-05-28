package com.bookkeeping.core.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Create scheduled transaction request")
public record CreateScheduledTransactionRequest(
        @Schema(description = "Transaction type: 2=income, 3=expense, 4=transfer", example = "2")
        @NotNull Integer transactionType,
        @Schema(description = "Source account ID", example = "1")
        @NotNull Long accountId,
        Long categoryId,
        Long destinationAccountId,
        @Schema(description = "Amount in cents", example = "5000")
        @NotNull Long amount,
        String description,
        String tagIds,
        @Schema(description = "Frequency: daily, weekly, monthly, yearly", example = "monthly")
        @NotBlank String frequency,
        Integer intervalDays,
        Integer dayOfWeek,
        Integer dayOfMonth,
        Integer monthOfYear,
        @Schema(description = "Start date as Unix timestamp", example = "1717200000")
        @NotNull Long startDate,
        Long endDate
) {}