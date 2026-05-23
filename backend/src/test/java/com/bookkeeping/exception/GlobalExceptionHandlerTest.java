package com.bookkeeping.exception;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleBusinessException_returnsOkWithErrorResponse() {
        // Given
        BusinessException ex = new BusinessException(ResultCode.USER_NOT_FOUND);
        
        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), response.getBody().errorCode());
        assertEquals("User not found", response.getBody().errorMessage());
    }

    @Test
    void handleBusinessException_withCustomMessage() {
        // Given
        BusinessException ex = new BusinessException(ResultCode.BAD_REQUEST, "Custom error message");
        
        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResultCode.BAD_REQUEST.getCode(), response.getBody().errorCode());
        assertEquals("Custom error message", response.getBody().errorMessage());
    }

    @Test
    void handleValidationException_returnsAllFieldErrors() {
        // Given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("user", "username", "Username is required"));
        bindingResult.addError(new FieldError("user", "email", "Invalid email format"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleValidationException(ex);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResultCode.VALIDATION_ERROR.getCode(), response.getBody().errorCode());
        assertTrue(response.getBody().errorMessage().contains("Username is required"));
        assertTrue(response.getBody().errorMessage().contains("Invalid email format"));
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequest() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");
        
        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalArgumentException(ex);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResultCode.BAD_REQUEST.getCode(), response.getBody().errorCode());
        assertEquals("Invalid parameter", response.getBody().errorMessage());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        // Given
        Exception ex = new RuntimeException("Something went wrong");
        
        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(ex);
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ResultCode.INTERNAL_ERROR.getCode(), response.getBody().errorCode());
        assertEquals("An unexpected error occurred", response.getBody().errorMessage());
    }

    @Test
    void errorResponseFormat_matchesApiSpec() {
        // Given
        BusinessException ex = new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        
        // When
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);
        ApiResponse<Void> body = response.getBody();
        
        // Then - verify structure per API spec
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertNull(body.result());  // Error responses have null result
        assertEquals(4001, body.errorCode());
        assertNotNull(body.errorMessage());
    }
}