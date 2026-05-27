package com.bookkeeping.supporting.onboarding;

import com.bookkeeping.common.enums.CategoryType;
import com.bookkeeping.core.category.Category;
import com.bookkeeping.core.category.CategoryRepository;
import com.bookkeeping.supporting.security.SecurityUtils;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for onboarding and first-time setup.
 * Handles default category creation and onboarding status tracking.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static final List<DefaultCategoryTemplate> DEFAULT_EXPENSE = List.of(
            new DefaultCategoryTemplate("Food & Dining", "#FF9800"),
            new DefaultCategoryTemplate("Transportation", "#2196F3"),
            new DefaultCategoryTemplate("Shopping", "#E91E63"),
            new DefaultCategoryTemplate("Bills & Utilities", "#9C27B0"),
            new DefaultCategoryTemplate("Entertainment", "#00BCD4"),
            new DefaultCategoryTemplate("Healthcare", "#F44336"),
            new DefaultCategoryTemplate("Education", "#3F51B5"),
            new DefaultCategoryTemplate("Travel", "#009688"),
            new DefaultCategoryTemplate("Personal Care", "#795548"),
            new DefaultCategoryTemplate("Other Expenses", "#607D8B")
    );

    private static final List<DefaultCategoryTemplate> DEFAULT_INCOME = List.of(
            new DefaultCategoryTemplate("Salary", "#4CAF50"),
            new DefaultCategoryTemplate("Freelance", "#8BC34A"),
            new DefaultCategoryTemplate("Investment Returns", "#CDDC39"),
            new DefaultCategoryTemplate("Business Income", "#FFEB3B"),
            new DefaultCategoryTemplate("Gifts & Donations", "#FFC107"),
            new DefaultCategoryTemplate("Other Income", "#FF9800")
    );

    /**
     * Get current onboarding status for the logged-in user.
     */
    @Transactional(readOnly = true)
    public OnboardingDtos.OnboardingStatusResponse getOnboardingStatus() {
        Long userId = securityUtils.requireCurrentUser().getId();
        User user = userRepository.findById(userId).orElseThrow();

        long categoryCount = categoryRepository.findByUserIdOrderBySortOrderAsc(userId).size();
        boolean hasAccounts = user.getDefaultAccountId() != null;
        boolean hasCategories = categoryCount > 0;

        return new OnboardingDtos.OnboardingStatusResponse(
                Boolean.TRUE.equals(user.getOnboardingCompleted()),
                hasAccounts,
                hasCategories,
                List.of(
                        new OnboardingDtos.OnboardingStep("account", "Create Account", hasAccounts, true),
                        new OnboardingDtos.OnboardingStep("categories", "Add Categories", hasCategories, true),
                        new OnboardingDtos.OnboardingStep("preferences", "Set Preferences", true, true)
                )
        );
    }

    /**
     * Mark the onboarding process as complete.
     */
    public void markOnboardingComplete() {
        Long userId = securityUtils.requireCurrentUser().getId();
        User user = userRepository.findById(userId).orElseThrow();
        User updated = user.toBuilder().onboardingCompleted(true).build().withId(user.getId());
        userRepository.save(updated);
    }

    /**
     * Create default categories for new users.
     * @param type "income", "expense", or "all"
     */
    public OnboardingDtos.CreateDefaultsResponse createDefaultCategories(String type) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Long> createdIds = new ArrayList<>();

        if ("income".equals(type) || "all".equals(type)) {
            createdIds.addAll(createCategories(DEFAULT_INCOME, CategoryType.INCOME, userId));
        }
        if ("expense".equals(type) || "all".equals(type)) {
            createdIds.addAll(createCategories(DEFAULT_EXPENSE, CategoryType.EXPENSE, userId));
        }

        // Mark onboarding complete after creating defaults
        User user = userRepository.findById(userId).orElseThrow();
        User updated = user.toBuilder().onboardingCompleted(true).build().withId(user.getId());
        userRepository.save(updated);

        return new OnboardingDtos.CreateDefaultsResponse(createdIds.size(), createdIds);
    }

    private List<Long> createCategories(List<DefaultCategoryTemplate> templates,
                                        CategoryType type, Long userId) {
        List<Long> ids = new ArrayList<>();
        int sortOrder = 0;

        for (DefaultCategoryTemplate template : templates) {
            // Skip if category already exists
            if (categoryRepository.existsByNameAndUserId(template.name(), userId)) {
                continue;
            }
            Category category = Category.builder()
                    .name(template.name())
                    .categoryType(type)
                    .userId(userId)
                    .sortOrder(sortOrder++)
                    .hidden(false)
                    .build();
            ids.add(categoryRepository.save(category).getId());
        }
        return ids;
    }

    // Inner class for templates
    private record DefaultCategoryTemplate(String name, String color) {}
}