package com.bookkeeping.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    // System errors: 1xxxxx
    SYSTEM_ERROR(100001, "System error"),
    UNAUTHORIZED(100002, "Unauthorized"),
    FORBIDDEN(100003, "Forbidden"),
    NOT_FOUND(100004, "Resource not found"),
    METHOD_NOT_ALLOWED(100005, "Method not allowed"),
    VALIDATION_ERROR(100006, "Validation error"),

    // User errors: 2x1xxx
    USER_NOT_FOUND(201001, "User not found"),
    USER_ALREADY_EXISTS(201002, "Username or email already exists"),
    USER_DISABLED(201003, "User is disabled"),
    USER_NOT_VERIFIED(201004, "Email not verified"),

    // Login errors
    INVALID_CREDENTIALS(201005, "Invalid username or password"),
    OLD_PASSWORD_INCORRECT(201006, "Old password is incorrect"),

    // Token errors: 2x2xxx
    TOKEN_EXPIRED(202001, "Token has expired"),
    TOKEN_INVALID(202002, "Token is invalid"),
    TOKEN_REVOKED(202003, "Token has been revoked"),
    TOKEN_NOT_FOUND(202004, "Token not found"),

    // Account errors: 2x4xxx
    ACCOUNT_NOT_FOUND(204001, "Account not found"),
    ACCOUNT_DUPLICATE_NAME(204002, "Account with this name already exists"),
    ACCOUNT_HAS_SUB_ACCOUNTS(204003, "Account has sub-accounts, delete them first"),
    ACCOUNT_HAS_TRANSACTIONS(204004, "Account has associated transactions"),

    // Transaction errors: 2x5xxx
    TRANSACTION_NOT_FOUND(205001, "Transaction not found"),
    TRANSACTION_EDIT_NOT_ALLOWED(205002, "Transaction edit not allowed due to edit scope"),
    TRANSFER_REQUIRES_TWO_ACCOUNTS(205003, "Transfer requires source and target accounts"),
    TRANSACTION_AMOUNT_OUT_OF_RANGE(205004, "Transaction amount out of valid range"),
    TOO_MANY_TAGS(205005, "Maximum 10 tags per transaction"),
    DUPLICATE_TRANSACTION(205006, "Duplicate transaction detected"),

    // Category errors: 2x6xxx
    CATEGORY_NOT_FOUND(206001, "Category not found"),
    CATEGORY_HAS_SUB_CATEGORIES(206002, "Category has sub-categories, delete them first"),
    CATEGORY_HAS_TRANSACTIONS(206003, "Category has associated transactions"),

    // Tag errors: 2x7xxx
    TAG_NOT_FOUND(207001, "Tag not found"),

    // Data errors: 2x8xxx
    DATA_CLEAR_PASSWORD_REQUIRED(208001, "Password required for data clear operation"),

    // Password reset errors: 2x9xxx
    PASSWORD_RESET_TOKEN_INVALID(209001, "Password reset token is invalid"),
    PASSWORD_RESET_TOKEN_EXPIRED(209002, "Password reset token has expired");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}