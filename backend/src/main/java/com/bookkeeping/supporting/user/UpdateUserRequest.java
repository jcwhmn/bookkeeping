package com.bookkeeping.supporting.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(max = 64) String nickname,
    String defaultCurrency,
    @Size(min = 2, max = 10) String language,
    Long defaultAccountId,
    String avatar,
    @Min(0) @Max(6) Integer transactionEditScope,
    @Min(0) @Max(1) Integer firstDayOfWeek,
    @Min(1) @Max(12) Integer fiscalYearStart,
    String dateFormat
) {}