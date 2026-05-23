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
    public boolean hasFilters() {
        return year != null || month != null || accountId != null || 
               categoryId != null || transactionType != null || 
               (search != null && !search.isBlank());
    }
}