package com.bookkeeping.core.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Scheduled transaction")
public record ScheduledTransactionDto(
        Long id,
        Integer transactionType,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        Long destinationAccountId,
        String destinationAccountName,
        Long amount,
        String amountStr,
        String description,
        String frequency,
        Integer intervalDays,
        Integer dayOfWeek,
        Integer dayOfMonth,
        Integer monthOfYear,
        Long startDate,
        Long endDate,
        Long nextRunTime,
        String nextRunTimeStr,
        Boolean active,
        Long lastRunTime,
        String lastRunResult,
        Integer runCount,
        Long createdAt
) {
    public static String formatAmount(Long amount) {
        return String.format("%.2f", amount / 100.0);
    }

    public static String formatTime(Long timestamp) {
        if (timestamp == null) return "Never";
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(timestamp * 1000));
    }
}