package com.bookkeeping.supporting.auth;

import com.bookkeeping.supporting.user.UserDto;

/**
 * Login response DTO containing JWT token and user info.
 */
public record LoginResponse(
    String token,
    UserDto user
) {}
