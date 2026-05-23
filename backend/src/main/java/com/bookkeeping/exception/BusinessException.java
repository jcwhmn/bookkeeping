package com.bookkeeping.exception;

import com.bookkeeping.common.ResultCode;

/**
 * Custom business exception with error code.
 */
public class BusinessException extends RuntimeException {
    
    private final int errorCode;
    private final String errorMessage;
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.errorCode = resultCode.getCode();
        this.errorMessage = resultCode.getMessage();
    }
    
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.errorCode = resultCode.getCode();
        this.errorMessage = message;
    }
    
    public BusinessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}