package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions/import")
@Tag(name = "Transaction Import", description = "Import transactions from CSV/OFX")
public class TransactionImportController {

    private final TransactionImportService importService;

    public TransactionImportController(TransactionImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/parse_custom.json")
    @Operation(summary = "Parse CSV/TSV with custom column mapping")
    public ApiResponse<TransactionImportService.ParseResult> parseCustom(
            @RequestBody TransactionImportService.ParseRequest request) {
        return ApiResponse.success(importService.parseCustom(request.rawData(), request));
    }

    @PostMapping("/parse_standard.json")
    @Operation(summary = "Parse standard format (OFX/QFX/QIF) - stub")
    public ApiResponse<TransactionImportService.ParseResult> parseStandard(
            @RequestBody ParseStandardRequest request) {
        // Stub: Return empty result
        return ApiResponse.success(new TransactionImportService.ParseResult(
                java.util.List.of(), "Standard format parsing not implemented yet", 0));
    }

    @PostMapping("/import.json")
    @Operation(summary = "Execute import from parsed session")
    public ApiResponse<TransactionImportService.ImportResult> executeImport(
            @RequestBody ExecuteImportRequest request) {
        return ApiResponse.success(importService.executeImport(request.sessionId(), request.rowCategoryMap()));
    }

    @GetMapping("/process.json")
    @Operation(summary = "Check import process status")
    public ApiResponse<TransactionImportService.ImportProcessResult> checkProcess(
            @RequestParam("session_id") String sessionId) {
        return ApiResponse.success(importService.checkProcess(sessionId));
    }

    // === Request DTOs ===

    public record ParseStandardRequest(String format, String rawData, Long accountId) {}

    public record ExecuteImportRequest(String sessionId, Map<Integer, Long> rowCategoryMap) {}
}