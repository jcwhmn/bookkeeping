package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;

    public TransactionController(TransactionService transactionService, SecurityUtils securityUtils) {
        this.transactionService = transactionService;
        this.securityUtils = securityUtils;
    }

    // === List & Query ===

    @GetMapping
    @Operation(summary = "Get transactions with filters (DB-level filtering)")
    public ApiResponse<List<TransactionDto>> list(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer transactionType,
            @RequestParam(required = false) String search) {
        return ApiResponse.success(transactionService.searchTransactions(
                new TransactionSearchParams(year, month, accountId, categoryId, transactionType, search), limit));
    }

    /**
     * GET /api/v1/transactions/list.json
     * Cursor-based paginated list — for "load more" / infinite scroll.
     *
     * @param cursor  transactionTime of last item (exclusive), omit for first page
     * @param limit   page size (default 50)
     * @param count   if true, return totalCount in response
     */
    @GetMapping("/list.json")
    @Operation(summary = "List transactions (cursor pagination, optional total count)")
    public ApiResponse<TransactionPageResponse> listPaginated(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "false") boolean count) {
        TransactionPageResponse response = transactionService.listByCursor(cursor, limit);
        if (count) {
            long total = transactionService.countTransactions(TransactionSearchParams.NONE);
            return ApiResponse.success(new TransactionPageResponse(
                    response.transactions(), response.nextCursor(), total));
        }
        return ApiResponse.success(response);
    }

    /**
     * GET /api/v1/transactions/list/by_month.json
     * List transactions for a specific year+month.
     */
    @GetMapping("/list/by_month.json")
    @Operation(summary = "List transactions by specific year+month")
    public ApiResponse<List<TransactionDto>> listByMonth(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(transactionService.getTransactionsByMonth(year, month));
    }

    /**
     * GET /api/v1/transactions/list/all.json
     * List all transactions (unpaginated). For export/backup.
     */
    @GetMapping("/list/all.json")
    @Operation(summary = "List all transactions (unpaginated, for export)")
    public ApiResponse<List<TransactionDto>> listAll() {
        return ApiResponse.success(transactionService.listAll());
    }

    /**
     * GET /api/v1/transactions/count.json
     * Count transactions with optional filters.
     */
    @GetMapping("/count.json")
    @Operation(summary = "Count transactions matching filters")
    public ApiResponse<Long> count(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer transactionType,
            @RequestParam(required = false) String search) {
        return ApiResponse.success(transactionService.countTransactions(
                new TransactionSearchParams(year, month, accountId, categoryId, transactionType, search)));
    }

    // === Statistics ===

    @GetMapping("/statistics")
    @Operation(summary = "Get transaction statistics for a month")
    public ApiResponse<StatisticsDto> statistics(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(transactionService.getStatistics(year, month));
    }

    // === Export ===

    @GetMapping("/export.csv")
    @Operation(summary = "Export transactions as CSV")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer accountId) {
        List<TransactionDto> transactions = transactionService.searchTransactions(
                new TransactionSearchParams(year, month, accountId, null, null, null), 10000);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Type,Account,Category,Amount,Description,Date,Tags\n");
        for (TransactionDto tx : transactions) {
            csv.append(tx.id()).append(",");
            csv.append(switch (tx.transactionType()) {
                case 1 -> "Modify";
                case 2 -> "Income";
                case 3 -> "Expense";
                case 4 -> "Transfer Out";
                case 5 -> "Transfer In";
                default -> "Unknown";
            }).append(",");
            csv.append(tx.accountId()).append(",");
            csv.append(tx.categoryId() != null ? tx.categoryId() : "").append(",");
            csv.append(tx.amount()).append(",");
            csv.append("\"").append(tx.description() != null ? tx.description().replace("\"", "\"\"") : "").append("\",");
            csv.append(tx.transactionTime()).append(",");
            csv.append(tx.tagIds() != null ? tx.tagIds() : "").append("\n");
        }

        String filename = "transactions-" +
            (year != null ? year : "*") + "-" +
            (month != null ? String.format("%02d", month) : "*") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    // === CRUD ===

    @PostMapping
    @Operation(summary = "Create transaction")
    public ApiResponse<TransactionDto> create(@Valid @RequestBody CreateTransactionRequest request) {
        return ApiResponse.success(transactionService.createTransaction(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction")
    public ApiResponse<TransactionDto> update(@PathVariable Long id,
                                              @Valid @RequestBody UpdateTransactionRequest request) {
        return ApiResponse.success(transactionService.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ApiResponse.success(null);
    }
}