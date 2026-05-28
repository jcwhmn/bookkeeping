package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduled_transactions")
@Tag(name = "Scheduled Transactions", description = "Recurring scheduled transactions")
public class ScheduledTransactionController {

    private final ScheduledTransactionService service;

    public ScheduledTransactionController(ScheduledTransactionService service) {
        this.service = service;
    }

    @GetMapping("/list.json")
    @Operation(summary = "List all scheduled transactions")
    public ApiResponse<List<ScheduledTransactionDto>> list() {
        return ApiResponse.success(service.listByUser());
    }

    @GetMapping("/get.json")
    @Operation(summary = "Get scheduled transaction by ID")
    public ApiResponse<ScheduledTransactionDto> get(@RequestParam("id") Long id) {
        return ApiResponse.success(service.getById(id));
    }

    @PostMapping("/add.json")
    @Operation(summary = "Create new scheduled transaction")
    public ApiResponse<ScheduledTransactionDto> create(@RequestBody CreateScheduledTransactionRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PostMapping("/modify.json")
    @Operation(summary = "Update scheduled transaction")
    public ApiResponse<ScheduledTransactionDto> update(
            @RequestParam("id") Long id,
            @RequestBody UpdateScheduledTransactionRequest request) {
        return ApiResponse.success(service.update(id, request));
    }

    @PostMapping("/delete.json")
    @Operation(summary = "Delete scheduled transaction")
    public ApiResponse<Void> delete(@RequestParam("id") Long id) {
        service.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/toggle_active.json")
    @Operation(summary = "Toggle scheduled transaction active status")
    public ApiResponse<Void> toggleActive(@RequestParam("id") Long id) {
        service.toggleActive(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/statistics.json")
    @Operation(summary = "Get scheduled transaction statistics")
    public ApiResponse<ScheduledTransactionStats> stats() {
        return ApiResponse.success(service.getStats());
    }
}