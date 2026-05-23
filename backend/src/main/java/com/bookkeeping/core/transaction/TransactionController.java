package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
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

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "Get transactions with filters")
    public ApiResponse<List<TransactionDto>> recent(
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

    @GetMapping("/statistics")
    @Operation(summary = "Get transaction statistics for a month")
    public ApiResponse<StatisticsDto> statistics(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(transactionService.getStatistics(year, month));
    }

    @GetMapping("/export")
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