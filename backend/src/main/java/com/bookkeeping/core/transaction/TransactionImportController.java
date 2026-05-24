package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.core.transaction.TransactionPictureRepository;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction import APIs")
public class TransactionImportController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;
    private final TransactionPictureRepository pictureRepository;

    public TransactionImportController(TransactionService transactionService,
                                        SecurityUtils securityUtils,
                                        TransactionPictureRepository pictureRepository) {
        this.transactionService = transactionService;
        this.securityUtils = securityUtils;
        this.pictureRepository = pictureRepository;
    }

    @PostMapping("/import.json")
    @Operation(summary = "Import transactions (stub)")
    public ApiResponse<Map<String, Object>> importTransactions(
            @RequestBody TransactionImportRequest request) {
        securityUtils.requireCurrentUser();
        // Stub: return import job ID for async processing
        String jobId = "import_" + System.currentTimeMillis();
        return ApiResponse.success(Map.of(
                "jobId", jobId,
                "status", "pending",
                "totalRows", request.transactions() != null ? request.transactions().size() : 0
        ));
    }

    @GetMapping("/import/process.json")
    @Operation(summary = "Check import process status")
    public ApiResponse<ImportProcessStatus> checkImportStatus(
            @RequestParam String client_session_id) {
        // Stub: always return completed
        return ApiResponse.success(new ImportProcessStatus(
                client_session_id, 100, 0, "completed", 0));
    }

    public record TransactionImportRequest(
            String clientSessionId,
            String format,
            java.util.List<TransactionImportItem> transactions
    ) {}
    public record TransactionImportItem(
            Integer transactionType,
            Long accountId,
            Long categoryId,
            Long amount,
            String description,
            Long transactionTime,
            java.util.List<String> tags
    ) {}
    public record ImportProcessStatus(
            String clientSessionId,
            Integer progress,
            Integer totalRows,
            String status,
            Integer errors
    ) {}
}