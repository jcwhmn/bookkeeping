package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Reconciliation", description = "Account reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/reconciliation_statements.json")
    @Operation(summary = "Get reconciliation statement for an account")
    public ApiResponse<ReconciliationService.ReconciliationStatement> getStatement(
            @RequestParam("account_id") Long accountId,
            @RequestParam("start_time") Long startTime,
            @RequestParam("end_time") Long endTime) {
        return ApiResponse.success(
                reconciliationService.getStatement(accountId, startTime, endTime));
    }

    @PostMapping("/reconcile.json")
    @Operation(summary = "Reconcile account balance")
    public ApiResponse<ReconciliationService.ReconciliationResult> reconcile(
            @RequestBody ReconcileRequest request) {
        return ApiResponse.success(
                reconciliationService.reconcile(request.accountId(), request.endTime(), request.statementBalance()));
    }

    public record ReconcileRequest(Long accountId, Long endTime, Long statementBalance) {}
}