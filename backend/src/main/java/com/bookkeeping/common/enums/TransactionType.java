package com.bookkeeping.common.enums;

/**
 * Transaction types for bookkeeping.
 */
public enum TransactionType {
    MODIFY_BALANCE(1, "Modify Balance"),
    INCOME(2, "Income"),
    EXPENSE(3, "Expense"),
    TRANSFER_OUT(4, "Transfer Out"),
    TRANSFER_IN(5, "Transfer In");
    
    private final int code;
    private final String displayName;
    
    TransactionType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static TransactionType fromCode(int code) {
        for (TransactionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type code: " + code);
    }
}