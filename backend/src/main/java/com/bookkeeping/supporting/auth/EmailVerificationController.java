package com.bookkeeping.supporting.auth;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Auth", description = "Authentication APIs")
public class EmailVerificationController {

    @PostMapping("/verify_email/resend.json")
    @Operation(summary = "Resend email verification")
    public ApiResponse<Map<String, String>> resendVerification(
            @RequestBody ResendVerifyRequest request) {
        // Stub: would send verification email
        return ApiResponse.success(Map.of(
                "message", "Verification email sent. Please check your inbox.",
                "email", request.email() != null ? maskEmail(request.email()) : "unknown@example.com"
        ));
    }

    @PostMapping("/verify_email/by_token.json")
    @Operation(summary = "Verify email by token")
    public ApiResponse<Map<String, Object>> verifyByToken(
            @RequestBody VerifyEmailRequest request) {
        // Stub: would verify token and update user email_verified
        return ApiResponse.success(Map.of(
                "verified", true,
                "email", "user@example.com"
        ));
    }

    @PostMapping("/users/verify_email/resend.json")
    @Operation(summary = "Resend user email verification (authenticated)")
    public ApiResponse<Map<String, String>> resendUserVerification() {
        // Stub: would send verification email to authenticated user
        return ApiResponse.success(Map.of("message", "Verification email sent"));
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***@***.com";
        int at = email.indexOf("@");
        String name = email.substring(0, at);
        String domain = email.substring(at);
        return name.substring(0, 2) + "***" + domain;
    }

    public record ResendVerifyRequest(String email) {}
    public record VerifyEmailRequest(String token) {}
}