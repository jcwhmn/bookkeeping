package com.bookkeeping.supporting.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user profile.
 */
public record UpdateUserRequest(
    @Size(max = 64, message = "Nickname must be at most 64 characters")
    String nickname,
    
    @Email(message = "Invalid email format")
    String email,
    
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    String defaultCurrency,
    
    @Size(min = 2, max = 10, message = "Language code must be 2-10 characters")
    String language
) {}