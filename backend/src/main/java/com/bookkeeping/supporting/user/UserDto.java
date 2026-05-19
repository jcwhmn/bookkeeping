package com.bookkeeping.supporting.user;

public record UserDto(
    String idStr,
    String username,
    String email,
    String nickname,
    String defaultCurrency,
    String defaultAccountIdStr,
    String language,
    String emailVerifiedStr,
    String disabledStr
) {
    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId() != null ? user.getId().toString() : null,
            user.getUsername(),
            user.getEmail(),
            user.getNickname(),
            user.getDefaultCurrency(),
            user.getDefaultAccountId() != null ? user.getDefaultAccountId().toString() : null,
            user.getLanguage(),
            user.getEmailVerified() != null ? user.getEmailVerified().toString() : "false",
            user.getDisabled() != null ? user.getDisabled().toString() : "false"
        );
    }
}