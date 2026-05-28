package com.bookkeeping.supporting.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTOs for Two-Factor Authentication endpoints.
 */
public class TwoFactorDtos {

    // ============ Requests ============

    public record TwoFactorEnableRequest(
        @NotBlank(message = "Secret is required")
        String secret,
        
        @NotBlank(message = "Passcode is required")
        String passcode
    ) {}

    public record TwoFactorDisableRequest(
        @NotBlank(message = "Password is required")
        String password
    ) {}

    public record TwoFactorRegenerateRequest(
        @NotBlank(message = "Password is required")
        String password
    ) {}

    public record TwoFactorStatusResponse(
        boolean enabled,
        Long createdAt
    ) {}

    public record TwoFactorEnableResponse(
        String secret,
        String qrcode
    ) {}

    public record TwoFactorEnableConfirmResponse(
        String token,
        java.util.List<String> recoveryCodes
    ) {}
}