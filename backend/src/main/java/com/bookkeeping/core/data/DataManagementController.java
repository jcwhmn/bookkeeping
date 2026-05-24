package com.bookkeeping.core.data;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.transaction.TransactionSearchParams;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/data")
@Tag(name = "Data Management", description = "Data export and clear APIs")
public class DataManagementController {

    private final DataManagementService dataService;
    private final SecurityUtils securityUtils;

    public DataManagementController(DataManagementService dataService, SecurityUtils securityUtils) {
        this.dataService = dataService;
        this.securityUtils = securityUtils;
    }

    @GetMapping(value = "/export.csv", produces = "text/csv")
    @Operation(summary = "Export data as CSV")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) Integer transaction_type,
            @RequestParam(required = false) Long category_ids,
            @RequestParam(required = false) Long account_ids,
            @RequestParam(required = false) Long tag_ids,
            @RequestParam(required = false) Long min_amount,
            @RequestParam(required = false) Long max_amount,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long max_time,
            @RequestParam(required = false) Long min_time) {

        TransactionSearchParams params = new TransactionSearchParams(
                null, null,
                account_ids != null ? account_ids.intValue() : null,
                category_ids != null ? category_ids.intValue() : null,
                transaction_type,
                keyword, min_time, max_time, null, min_amount, max_amount);

        String csv = dataService.exportAsCsv(params);
        String filename = "transactions_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping(value = "/export.tsv", produces = "text/tab-separated-values")
    @Operation(summary = "Export data as TSV")
    public ResponseEntity<String> exportTsv(
            @RequestParam(required = false) Integer transaction_type,
            @RequestParam(required = false) Long category_ids,
            @RequestParam(required = false) Long account_ids,
            @RequestParam(required = false) Long tag_ids,
            @RequestParam(required = false) Long min_amount,
            @RequestParam(required = false) Long max_amount,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long max_time,
            @RequestParam(required = false) Long min_time) {

        TransactionSearchParams params = new TransactionSearchParams(
                null, null,
                account_ids != null ? account_ids.intValue() : null,
                category_ids != null ? category_ids.intValue() : null,
                transaction_type,
                keyword, min_time, max_time, null, min_amount, max_amount);

        String tsv = dataService.exportAsTsv(params);
        String filename = "transactions_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".tsv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/tab-separated-values"))
                .body(tsv);
    }

    @PostMapping("/clear/all.json")
    @Operation(summary = "Clear all user data")
    public ApiResponse<Void> clearAll(@RequestBody ClearDataRequest request) {
        securityUtils.requireCurrentUser(); // verify auth
        dataService.clearAll();
        return ApiResponse.success(null);
    }

    @PostMapping("/clear/transactions.json")
    @Operation(summary = "Clear all transactions")
    public ApiResponse<Void> clearTransactions(@RequestBody ClearDataRequest request) {
        securityUtils.requireCurrentUser();
        dataService.clearAllTransactions();
        return ApiResponse.success(null);
    }

    @PostMapping("/clear/transactions/by_account.json")
    @Operation(summary = "Clear transactions by account")
    public ApiResponse<Void> clearByAccount(@RequestBody ClearAccountTransactionsRequest request) {
        securityUtils.requireCurrentUser();
        dataService.clearTransactionsByAccount(request.accountId());
        return ApiResponse.success(null);
    }

    public record ClearDataRequest(String password) {}
    public record ClearAccountTransactionsRequest(Long accountId, String password) {}
}