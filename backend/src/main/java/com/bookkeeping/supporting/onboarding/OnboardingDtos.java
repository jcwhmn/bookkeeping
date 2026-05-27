package com.bookkeeping.supporting.onboarding;

/**
 * Response DTOs for onboarding endpoints.
 */
public final class OnboardingDtos {

    private OnboardingDtos() {}

    public record OnboardingStatusResponse(
            boolean completed,
            boolean hasAccounts,
            boolean hasCategories,
            java.util.List<OnboardingStep> steps
    ) {}

    public record OnboardingStep(
            String id,
            String name,
            boolean completed,
            boolean available
    ) {}

    public record CreateDefaultsResponse(
            int created,
            java.util.List<Long> categoryIds
    ) {}

    public record CreateDefaultsRequest(String type) {}
}