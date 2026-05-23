package com.bookkeeping.common.enums;

/**
 * Account types for bookkeeping.
 */
public enum AccountType {
    CASH("Cash"),
    CHECKING("Checking"),
    SAVINGS("Savings"),
    CREDIT("Credit"),
    INVESTMENT("Investment");
    
    private final String displayName;
    
    AccountType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}