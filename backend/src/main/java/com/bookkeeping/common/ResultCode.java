package com.bookkeeping.common;

/**
 * Error codes for the application.
 * Format: category * 100000 + subCategory * 1000 + index
 */
public enum ResultCode {
    
    // Common errors (0xxx)
    SUCCESS(0, "Success"),
    BAD_REQUEST(1001, "Bad request"),
    UNAUTHORIZED(1002, "Unauthorized"),
    FORBIDDEN(1003, "Forbidden"),
    NOT_FOUND(1004, "Resource not found"),
    INTERNAL_ERROR(1005, "Internal server error"),
    VALIDATION_ERROR(1006, "Validation error"),
    
    // Auth errors (2xxx)
    AUTHENTICATION_FAILED(2001, "Authentication failed"),
    AUTH_TOKEN_EXPIRED(2002, "Token has expired"),
    AUTH_TOKEN_INVALID(2003, "Invalid token"),
    AUTH_USER_DISABLED(2004, "User account is disabled"),
    
    // User errors (3xxx)
    USER_NOT_FOUND(3001, "User not found"),
    USER_ALREADY_EXISTS(3002, "User already exists"),
    USER_INVALID_PASSWORD(3003, "Invalid password"),
    USER_DISABLED(3004, "User is disabled"),
    USERNAME_ALREADY_EXISTS(3005, "Username already exists"),
    EMAIL_ALREADY_EXISTS(3006, "Email already exists"),
    
    // Account errors (4xxx)
    ACCOUNT_NOT_FOUND(4001, "Account not found"),
    ACCOUNT_ALREADY_EXISTS(4002, "Account already exists"),
    ACCOUNT_INVALID_BALANCE(4003, "Invalid balance"),
    
    // Category errors (5xxx)
    CATEGORY_NOT_FOUND(5001, "Category not found"),
    CATEGORY_ALREADY_EXISTS(5002, "Category already exists"),
    CATEGORY_TYPE_INVALID(5003, "Invalid category type"),
    
    // Transaction errors (6xxx)
    TRANSACTION_NOT_FOUND(6001, "Transaction not found"),
    TRANSACTION_INSUFFICIENT_BALANCE(6002, "Insufficient balance"),
    TRANSACTION_INVALID(6003, "Invalid transaction"),
    
    // 2FA errors (7xxx)
    INVALID_TOTP_PASSCODE(7001, "Invalid TOTP passcode"),
    INVALID_RECOVERY_CODE(7002, "Invalid recovery code"),
    TWO_FACTOR_NOT_ENABLED(7003, "Two-factor authentication is not enabled"),
    
    // Password errors (8xxx)
    PASSWORD_INCORRECT(8001, "Password is incorrect"),
    PASSWORD_TOO_SHORT(8002, "Password must be at least 6 characters");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getCodeValue() {
        return code;
    }
}
