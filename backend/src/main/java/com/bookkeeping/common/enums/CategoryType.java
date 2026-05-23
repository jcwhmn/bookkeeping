package com.bookkeeping.common.enums;

/**
 * Transaction category types.
 */
public enum CategoryType {
    INCOME("Income"),
    EXPENSE("Expense");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
