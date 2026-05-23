package com.bookkeeping.core.budget;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@Tag(name = "Budgets", description = "Budget management APIs")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    @Operation(summary = "Get budgets for a month")
    public ApiResponse<List<BudgetDto>> getBudgets(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(budgetService.getBudgets(year, month));
    }

    @PostMapping
    @Operation(summary = "Create budget")
    public ApiResponse<BudgetDto> create(@Valid @RequestBody CreateBudgetRequest request) {
        return ApiResponse.success(budgetService.createBudget(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget")
    public ApiResponse<BudgetDto> update(@PathVariable Long id,
                                         @Valid @RequestBody UpdateBudgetRequest request) {
        return ApiResponse.success(budgetService.updateBudget(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ApiResponse.success(null);
    }
}