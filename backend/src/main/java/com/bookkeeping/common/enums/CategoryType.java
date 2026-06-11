package com.bookkeeping.common.enums;

/**
 * Transaction category types.
 * Type values match OpenAPI spec: 1=INCOME, 2=EXPENSE, 3=TRANSFER
 */
public enum CategoryType {
    INCOME("Income", 1),
    EXPENSE("Expense", 2),
    TRANSFER("Transfer", 3);

    private final String displayName;
    private final int value;

    CategoryType(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get CategoryType from integer value.
     * @param value the type value (1=INCOME, 2=EXPENSE, 3=TRANSFER)
     * @return the corresponding CategoryType
     * @throws IllegalArgumentException if value is not valid
     */
    public static CategoryType fromValue(int value) {
        for (CategoryType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid category type value: " + value);
    }
}
