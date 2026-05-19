package com.bookkeeping.supporting.auth;

public record LoginResponse(
    String token,
    String refreshToken,
    String expiresAtStr,
    UserInfo user
) {
    public record UserInfo(
        String idStr,
        String username,
        String nickname,
        String defaultCurrency
    ) {}
}