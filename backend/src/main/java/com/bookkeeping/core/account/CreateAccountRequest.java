package com.bookkeeping.core.account;

import com.bookkeeping.common.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new account.
 */
public record CreateAccountRequest(
    @NotBlank(message = "Account name is required")
    @Size(min = 1, max = 64, message = "Account name must be 1-64 characters")
    String name,

    @NotNull(message = "Account type is required")
    AccountType accountType,

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    String currency,

    Long initialBalance,

    @Size(max = 255, message = "Description must be at most 255 characters")
    String description,

    /** Parent account ID for sub-account creation; null for top-level accounts. */
    Long parentId
) {
    public CreateAccountRequest {
        if (initialBalance == null) initialBalance = 0L;
    }
}
