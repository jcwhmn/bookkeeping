package com.bookkeeping.exception;

import com.bookkeeping.common.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }
    
    public static BusinessException notFound(ResultCode resultCode) {
        return new BusinessException(resultCode.getCode(), resultCode.getMessage());
    }
    
    public static BusinessException unauthorized(ResultCode resultCode, String message) {
        return new BusinessException(resultCode.getCode(), message);
    }
    
    public static BusinessException badRequest(ResultCode resultCode, String message) {
        return new BusinessException(resultCode.getCode(), message);
    }
    
    public static BusinessException conflict(ResultCode resultCode, String message) {
        return new BusinessException(resultCode.getCode(), message);
    }
}