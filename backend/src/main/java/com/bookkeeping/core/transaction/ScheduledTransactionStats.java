package com.bookkeeping.core.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Scheduled transaction statistics")
public record ScheduledTransactionStats(
        long totalScheduled,
        long activeScheduled,
        long dailyCount,
        long weeklyCount,
        long monthlyCount,
        long yearlyCount,
        long next7Days,
        long next30Days
) {}