package com.bookkeeping.core.token;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tokens")
@Tag(name = "Tokens", description = "Token management APIs")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/list.json")
    @Operation(summary = "List all tokens")
    public ApiResponse<List<TokenService.TokenInfo>> list() {
        return ApiResponse.success(tokenService.listTokens());
    }

    @PostMapping("/generate/api.json")
    @Operation(summary = "Generate API token")
    public ApiResponse<TokenService.GenerateApiTokenResponse> generateApi(
            @RequestBody TokenService.GenerateRequest request) {
        return ApiResponse.success(tokenService.generateApiToken(
                request.expiresInSeconds() != null ? request.expiresInSeconds() : 31536000L,
                request.password()));
    }

    @PostMapping("/generate/mcp.json")
    @Operation(summary = "Generate MCP token")
    public ApiResponse<TokenService.GenerateMcpTokenResponse> generateMcp(
            @RequestBody TokenService.GenerateRequest request) {
        return ApiResponse.success(tokenService.generateMcpToken(
                request.expiresInSeconds() != null ? request.expiresInSeconds() : 31536000L,
                request.password()));
    }

    @PostMapping("/revoke.json")
    @Operation(summary = "Revoke a token")
    public ApiResponse<Void> revoke(@RequestBody TokenService.RevokeRequest request) {
        tokenService.revokeToken(request.tokenId(), request.password());
        return ApiResponse.success(null);
    }

    @PostMapping("/revoke_all.json")
    @Operation(summary = "Revoke all tokens")
    public ApiResponse<Void> revokeAll(@RequestBody TokenService.GenerateRequest request) {
        tokenService.revokeAllTokens(request.password());
        return ApiResponse.success(null);
    }

    @PostMapping("/refresh.json")
    @Operation(summary = "Refresh current token")
    public ApiResponse<TokenService.RefreshTokenResponse> refresh(
            @RequestBody(required = false) TokenService.RefreshRequest request) {
        return ApiResponse.success(tokenService.refreshToken(
                request != null ? request.tokenId() : null));
    }
}