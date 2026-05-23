package com.bookkeeping.core.account;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing account.
 */
public record UpdateAccountRequest(
    @Size(min = 1, max = 64, message = "Account name must be 1-64 characters")
    String name,

    @Size(max = 255, message = "Description must be at most 255 characters")
    String description
) {}
