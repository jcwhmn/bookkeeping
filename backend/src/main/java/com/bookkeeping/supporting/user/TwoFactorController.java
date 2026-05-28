package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.supporting.user.TwoFactorDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Two-Factor Authentication (TOTP).
 */
@RestController
@RequestMapping("/api/v1/users/2fa")
@RequiredArgsConstructor
@Tag(name = "Two-Factor Auth", description = "Two-factor authentication management")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;

    @GetMapping("/status.json")
    @Operation(summary = "Get 2FA status")
    public ApiResponse<TwoFactorStatusResponse> getStatus() {
        return ApiResponse.success(twoFactorService.getStatus());
    }

    @PostMapping("/enable/request.json")
    @Operation(summary = "Request 2FA enable", description = "Returns TOTP secret and QR code for setup")
    public ApiResponse<TwoFactorEnableResponse> requestEnable() {
        return ApiResponse.success(twoFactorService.requestEnable());
    }

    @PostMapping("/enable/confirm.json")
    @Operation(summary = "Confirm 2FA enable", description = "Confirms setup with TOTP passcode")
    public ApiResponse<TwoFactorEnableConfirmResponse> confirmEnable(
            @Valid @RequestBody TwoFactorEnableRequest request) {
        return ApiResponse.success(twoFactorService.confirmEnable(
                request.secret(), request.passcode()));
    }

    @PostMapping("/disable.json")
    @Operation(summary = "Disable 2FA", description = "Disables 2FA with password verification")
    public ApiResponse<Void> disable(@Valid @RequestBody TwoFactorDisableRequest request) {
        twoFactorService.disable(request.password());
        return ApiResponse.success();
    }

    @PostMapping("/recovery/regenerate.json")
    @Operation(summary = "Regenerate recovery codes")
    public ApiResponse<java.util.List<String>> regenerateRecoveryCodes(
            @Valid @RequestBody TwoFactorRegenerateRequest request) {
        return ApiResponse.success(
                twoFactorService.regenerateRecoveryCodes(request.password()));
    }
}