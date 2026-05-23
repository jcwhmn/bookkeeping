package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    
    /**
     * Get current user profile.
     * For demo purposes, returns user with ID 1.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get the profile of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        // TODO: Get actual user from security context (Phase 4)
        UserDto user = userService.findById(1L)
            .map(userMapper::toDto)
            .orElseThrow(() -> new RuntimeException("Demo user not found"));
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Update current user profile.
     * For demo purposes, updates user with ID 1.
     */
    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update the profile of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request) {
        // TODO: Get actual user from security context (Phase 4)
        UserDto user = userService.updateProfile(1L, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get a user by their ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userService.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}