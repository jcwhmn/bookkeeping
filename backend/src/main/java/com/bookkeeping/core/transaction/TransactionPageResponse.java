package com.bookkeeping.core.transaction;

import java.util.List;

/**
 * Cursor-based paginated response for transactions.
 * Contains the list of transactions and the cursor for the next page.
 */
public record TransactionPageResponse(
    List<TransactionDto> transactions,
    Long nextCursor,
    Long totalCount
) {
    public TransactionPageResponse(List<TransactionDto> transactions, Long nextCursor) {
        this(transactions, nextCursor, null);
    }
}