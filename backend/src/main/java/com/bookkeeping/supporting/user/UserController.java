package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.category.CategoryRepository;
import com.bookkeeping.core.tag.TagRepository;
import com.bookkeeping.core.transaction.TransactionRepository;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TransactionRepository transactionRepository;

    // === Profile ===

    @GetMapping("/profile/get.json")
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userId)));
    }

    @PostMapping("/profile/update.json")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(userId, request)));
    }

    // === Avatar ===

    @PostMapping("/avatar/update.json")
    @Operation(summary = "Upload user avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file) throws IOException {
        Long userId = securityUtils.requireCurrentUser().getId();

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Only image files are allowed");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Avatar file must be less than 2MB");
        }

        String filename = userId + "_" + System.currentTimeMillis() + ".jpg";
        Path uploadDir = Paths.get("backend/src/main/resources/static/avatars");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, file.getBytes());

        String avatarUrl = "/avatars/" + filename;
        userService.updateProfile(userId, new UpdateUserRequest(null, null, null, null, avatarUrl, null, null, null, null));
        return ResponseEntity.ok(ApiResponse.success(Map.of("avatar", avatarUrl)));
    }

    @PostMapping("/avatar/remove.json")
    @Operation(summary = "Remove user avatar")
    public ResponseEntity<ApiResponse<Void>> removeAvatar() {
        Long userId = securityUtils.requireCurrentUser().getId();
        userService.updateProfile(userId, new UpdateUserRequest(null, null, null, null, "", null, null, null, null));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // === Data Statistics ===

    @GetMapping("/data/statistics.json")
    @Operation(summary = "Get data statistics")
    public ResponseEntity<ApiResponse<DataStatisticsResponse>> getDataStatistics() {
        Long userId = securityUtils.requireCurrentUser().getId();
        long accountCount = accountRepository.countByUserId(userId);
        long categoryCount = categoryRepository.findByUserIdOrderBySortOrderAsc(userId).size();
        long tagCount = tagRepository.findByUserId(userId).size();
        long txCount = transactionRepository.countByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(new DataStatisticsResponse(
                String.valueOf(accountCount),
                String.valueOf(categoryCount),
                String.valueOf(tagCount),
                String.valueOf(txCount),
                "0", "0", "0", "0")));
    }

    public record DataStatisticsResponse(
            String totalAccountCount,
            String totalTransactionCategoryCount,
            String totalTransactionTagCount,
            String totalTransactionCount,
            String totalTransactionPictureCount,
            String totalInsightsExplorerCount,
            String totalTransactionTemplateCount,
            String totalScheduledTransactionCount
    ) {}
}