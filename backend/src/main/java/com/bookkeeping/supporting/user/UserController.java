package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ApiResponse<UserDto> getCurrentUser() {
        Long userId = getCurrentUserId();
        UserDto dto = userService.getCurrentUserDto(userId);
        return ApiResponse.success(dto);
    }
    
    @PutMapping("/me")
    public ApiResponse<UserDto> updateCurrentUser(@RequestBody UpdateUserRequest request) {
        Long userId = getCurrentUserId();
        UserDto dto = userService.updateUser(userId, request);
        return ApiResponse.success(dto);
    }
    
    /**
     * Extract user ID from JWT token in SecurityContext
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found in SecurityContext");
        }
        // The principal is the username from JWT, which is the user ID
        String userIdStr = auth.getName();
        return Long.parseLong(userIdStr);
    }
    
    public record UpdateUserRequest(
        String nickname,
        String defaultCurrency,
        String language
    ) {}
}