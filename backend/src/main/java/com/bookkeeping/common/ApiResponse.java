package com.bookkeeping.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T result;
    private Integer errorCode;
    private String errorMessage;
    private String path;

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, result, null, null, null);
    }

    public static <T> ApiResponse<T> error(int errorCode, String errorMessage, String path) {
        return new ApiResponse<>(false, null, errorCode, errorMessage, path);
    }
}