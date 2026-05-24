package com.bookkeeping.core.account;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Request to reorder accounts (drag-to-sort). */
public record ReorderAccountsRequest(
    @NotEmpty List<Long> orderedIds
) {}