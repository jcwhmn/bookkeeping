package com.bookkeeping.common;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("✓ BusinessException returns 200 OK with success=false")
    void handleBusinessException_returnsOkWithError() {
        BusinessException ex = new BusinessException(204001, "Account not found");

        ResponseEntity<ApiResponse<Void>> result = handler.handleBusinessException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isFalse();
        assertThat(result.getBody().getErrorCode()).isEqualTo(204001);
        assertThat(result.getBody().getErrorMessage()).isEqualTo("Account not found");
    }

    @Test
    @DisplayName("✓ MethodArgumentNotValidException returns 400 Bad Request")
    void handleValidationException_returnsBadRequest() throws Exception {
        // Create BindingResult manually
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "field", "must not be null"));
        
        // Create MethodArgumentNotValidException using reflection
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null, bindingResult);

        ResponseEntity<ApiResponse<Void>> result = handler.handleValidationException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("✓ Generic Exception returns 500 Internal Server Error")
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<ApiResponse<Void>> result = handler.handleGenericException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isFalse();
        assertThat(result.getBody().getErrorMessage()).isEqualTo("Something went wrong");
    }
}