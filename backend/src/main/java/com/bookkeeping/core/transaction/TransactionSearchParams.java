package com.bookkeeping.core.transaction;

/**
 * Search parameters for transaction queries.
 */
public record TransactionSearchParams(
    Integer year,
    Integer month,
    Integer accountId,
    Integer categoryId,
    Integer transactionType,
    String search
) {
    /** Empty params with no filters — for count without filters. */
    public static final TransactionSearchParams NONE = new TransactionSearchParams(null, null, null, null, null, null);

    public boolean hasFilters() {
        return year != null || month != null || accountId != null ||
               categoryId != null || transactionType != null ||
               (search != null && !search.isBlank());
    }
}