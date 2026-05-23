package com.bookkeeping.core.budget;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.category.CategoryService;
import com.bookkeeping.core.transaction.TransactionRepository;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    public BudgetService(BudgetRepository budgetRepository,
                        TransactionRepository transactionRepository,
                        CategoryService categoryService,
                        SecurityUtils securityUtils) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgets(int year, int month) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Budget> budgets = budgetRepository.findByUserIdAndYearAndMonth(userId, year, month);
        return budgets.stream().map(b -> toDtoWithSpent(b, userId)).toList();
    }

    @Transactional
    public BudgetDto createBudget(CreateBudgetRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Check for duplicate
        if (budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
                userId, request.categoryId(), request.year(), request.month()).isPresent()) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Budget already exists for this category and month");
        }

        Budget budget = Budget.builder()
                .userId(userId)
                .categoryId(request.categoryId())
                .amount(request.amount())
                .year(request.year())
                .month(request.month())
                .createdTime(System.currentTimeMillis() / 1000)
                .build();

        Budget saved = budgetRepository.save(budget);
        return toDtoWithSpent(saved, userId);
    }

    @Transactional
    public BudgetDto updateBudget(Long id, UpdateBudgetRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Budget not found"));

        Budget updated = budget.toBuilder()
                .amount(request.amount())
                .updatedTime(System.currentTimeMillis() / 1000)
                .build();

        Budget saved = budgetRepository.save(updated);
        return toDtoWithSpent(saved, userId);
    }

    @Transactional
    public void deleteBudget(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Budget not found"));

        budgetRepository.delete(budget);
    }

    private BudgetDto toDtoWithSpent(Budget budget, Long userId) {
        // Get category name
        String categoryName = categoryService.getCategoryById(budget.getCategoryId())
                .map(c -> c.name())
                .orElse("Unknown");

        // Calculate spent amount for this category in this month
        LocalDate startDate = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate endDate = startDate.plusMonths(1);
        long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        // Sum all expenses for this category in this month
        List<?> transactions = transactionRepository.findByUserIdAndMonth(userId, startTime, endTime);
        long spent = transactions.stream()
                .filter(tx -> tx instanceof com.bookkeeping.core.transaction.Transaction)
                .map(tx -> (com.bookkeeping.core.transaction.Transaction) tx)
                .filter(tx -> tx.getCategoryId() != null && 
                            tx.getCategoryId().equals(budget.getCategoryId()) && 
                            tx.getTransactionType() == 3)
                .mapToLong(com.bookkeeping.core.transaction.Transaction::getAmount)
                .sum();

        double percentUsed = budget.getAmount() > 0 ? (spent * 100.0 / budget.getAmount()) : 0;

        return new BudgetDto(budget.getId(), budget.getCategoryId(), categoryName, 
                budget.getAmount(), budget.getYear(), budget.getMonth(), spent, percentUsed);
    }
}