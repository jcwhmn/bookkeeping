package com.bookkeeping.core.dashboard;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.transaction.TransactionRepository;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Dashboard summary APIs")
public class DashboardController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public DashboardController(AccountRepository accountRepository,
                                TransactionRepository transactionRepository,
                                SecurityUtils securityUtils) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/stats")
    @Operation(summary = "Dashboard statistics")
    public ApiResponse<Map<String, Object>> stats() {
        Long userId = securityUtils.requireCurrentUser().getId();

        var accounts = accountRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(userId);
        long totalBalance = accounts.stream().mapToLong(a -> a.getBalance()).sum();
        long accountCount = accounts.size();

        long now = System.currentTimeMillis() / 1000;
        long monthAgo = now - 30 * 86400;
        var monthTxs = transactionRepository.findByUserIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(userId, monthAgo, now);
        long monthlyIncome = monthTxs.stream().filter(t -> t.getTransactionType() == 2).mapToLong(t -> t.getAmount()).sum();
        long monthlyExpense = monthTxs.stream().filter(t -> t.getTransactionType() == 3).mapToLong(t -> Math.abs(t.getAmount())).sum();

        return ApiResponse.success(Map.of(
            "totalBalance", totalBalance,
            "accountCount", accountCount,
            "monthlyIncome", monthlyIncome,
            "monthlyExpense", monthlyExpense,
            "transactionCount", monthTxs.size()
        ));
    }
}
