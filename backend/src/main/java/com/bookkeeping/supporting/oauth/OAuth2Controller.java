package com.bookkeeping.supporting.oauth;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@Tag(name = "External Auth", description = "OAuth2/OIDC integration APIs")
public class OAuth2Controller {

    @GetMapping("/login")
    @Operation(summary = "Initiate OAuth2 login")
    public ResponseEntity<Void> initiateLogin(
            @RequestParam String platform,
            @RequestParam String client_session_id,
            @RequestParam(required = false) String token) {
        // Stub: redirect to OAuth2 provider (Google/GitHub/etc)
        // In production, would redirect to provider's authorization URL
        String redirectUrl = switch (platform.toLowerCase()) {
            case "google" -> "https://accounts.google.com/o/oauth2/auth?client_id=STUB";
            case "github" -> "https://github.com/login/oauth/authorize?client_id=STUB";
            default -> "/";
        };
        return ResponseEntity.status(302).header("Location", redirectUrl).build();
    }

    @GetMapping("/callback")
    @Operation(summary = "OAuth2 callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description) {
        if (error != null) {
            return ResponseEntity.status(302).header("Location", "/login?error=" + error).build();
        }
        // Stub: redirect to frontend with success (in production, would exchange code for tokens)
        return ResponseEntity.status(302).header("Location", "/?oauth=success&state=" + (state != null ? state : "")).build();
    }
}