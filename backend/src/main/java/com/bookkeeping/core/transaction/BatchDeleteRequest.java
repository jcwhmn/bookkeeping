package com.bookkeeping.core.transaction;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Request to batch-delete transactions. */
public record BatchDeleteRequest(
    @NotEmpty List<Long> ids
) {}