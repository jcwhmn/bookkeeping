package com.bookkeeping.supporting.security;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class for accessing current authenticated user.
 */
@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the username of the currently authenticated user.
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * Get the currently authenticated user entity.
     */
    public Optional<User> getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    /**
     * Get the currently authenticated user entity, throwing exception if not found.
     */
    public User requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "User not authenticated"));
    }

    /**
     * Check if the current user is authenticated.
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
