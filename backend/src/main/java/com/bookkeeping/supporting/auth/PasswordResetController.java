package com.bookkeeping.supporting.auth;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/forget_password")
@Tag(name = "Password Reset", description = "Password reset APIs")
public class PasswordResetController {

    @PostMapping("/request.json")
    @Operation(summary = "Request password reset email")
    public ApiResponse<Map<String, String>> requestReset(@RequestBody EmailRequest request) {
        return ApiResponse.success(Map.of(
                "message", "If an account exists with this email, a password reset link has been sent."
        ));
    }

    @PostMapping("/reset/by_token.json")
    @Operation(summary = "Reset password by token")
    public ApiResponse<Map<String, String>> resetByToken(@RequestBody ResetRequest request) {
        return ApiResponse.success(Map.of(
                "message", "Password has been reset successfully."
        ));
    }

    public record EmailRequest(String email) {}
    public record ResetRequest(String token, String newPassword) {}
}