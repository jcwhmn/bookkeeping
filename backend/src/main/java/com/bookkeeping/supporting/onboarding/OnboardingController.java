package com.bookkeeping.supporting.onboarding;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for onboarding and first-time setup.
 * Provides endpoints to track and complete the onboarding flow.
 */
@RestController
@RequestMapping("/api/v1/onboarding")
@Tag(name = "Onboarding", description = "First-time setup and onboarding APIs")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/status.json")
    @Operation(summary = "Get onboarding status", description = "Check if user has completed onboarding setup")
    public ApiResponse<OnboardingDtos.OnboardingStatusResponse> getStatus() {
        return ApiResponse.success(onboardingService.getOnboardingStatus());
    }

    @PostMapping("/complete.json")
    @Operation(summary = "Mark onboarding complete", description = "Mark the onboarding process as completed")
    public ApiResponse<Void> complete() {
        onboardingService.markOnboardingComplete();
        return ApiResponse.success(null);
    }

    @PostMapping("/create_defaults.json")
    @Operation(summary = "Create default categories", description = "Create default income/expense categories for new users")
    public ApiResponse<OnboardingDtos.CreateDefaultsResponse> createDefaults(@RequestBody OnboardingDtos.CreateDefaultsRequest request) {
        return ApiResponse.success(onboardingService.createDefaultCategories(request.type()));
    }
}