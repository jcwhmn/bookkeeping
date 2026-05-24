package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionStatisticsController {

    private final TransactionStatisticsService statisticsService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;

    public TransactionStatisticsController(TransactionStatisticsService statisticsService,
                                           TransactionService transactionService,
                                           AccountRepository accountRepository,
                                           SecurityUtils securityUtils) {
        this.statisticsService = statisticsService;
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/statistics/trends.json")
    @Operation(summary = "Get transaction trends")
    public ApiResponse<List<TransactionStatisticsService.TrendItem>> getTrends(
            @RequestParam(required = false) String start_year_month,
            @RequestParam(required = false) String end_year_month,
            @RequestParam(required = false) List<Long> tag_ids,
            @RequestParam(required = false) String keyword) {
        if (start_year_month == null) start_year_month = LocalDate.now().minusMonths(6).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        if (end_year_month == null) end_year_month = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        return ApiResponse.success(statisticsService.getTrends(start_year_month, end_year_month, tag_ids, keyword));
    }

    @GetMapping("/statistics/asset_trends.json")
    @Operation(summary = "Get asset trends")
    public ApiResponse<List<TransactionStatisticsService.AssetTrendItem>> getAssetTrends(
            @RequestParam(required = false) Long start_time,
            @RequestParam(required = false) Long end_time) {
        return ApiResponse.success(statisticsService.getAssetTrends(start_time, end_time));
    }

    @GetMapping("/reconciliation_statements.json")
    @Operation(summary = "Get reconciliation statement")
    public ApiResponse<TransactionStatisticsService.ReconciliationStatement> getReconciliationStatement(
            @RequestParam Long account_id,
            @RequestParam(required = false) Long start_time,
            @RequestParam(required = false) Long end_time) {
        return ApiResponse.success(statisticsService.getReconciliationStatement(account_id, start_time, end_time));
    }
}