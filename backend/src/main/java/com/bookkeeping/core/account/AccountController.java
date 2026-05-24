package com.bookkeeping.core.account;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.core.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management APIs")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List accounts", description = "Get all accounts for the current user, sorted by sortOrder")
    public ApiResponse<List<AccountDto>> listAccounts(
            @RequestParam(defaultValue = "false") boolean visibleOnly) {
        return ApiResponse.success(accountService.getCurrentUserAccounts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account", description = "Get account by ID")
    public ApiResponse<AccountDto> getAccount(@PathVariable Long id) {
        return ApiResponse.success(accountService.getAccount(id));
    }

    @PostMapping
    @Operation(summary = "Create account", description = "Create a new account, optionally as sub-account")
    public ApiResponse<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ApiResponse.success(accountService.createAccount(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account", description = "Update an existing account")
    public ApiResponse<AccountDto> updateAccount(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateAccountRequest request) {
        return ApiResponse.success(accountService.updateAccount(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Delete an account and its sub-accounts (soft delete)")
    public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ApiResponse.success(null);
    }

    // === Season 3 — Account Enhancements ===

    @PostMapping("/hide.json")
    @Operation(summary = "Hide/unhide an account")
    public ApiResponse<Void> hideAccount(@RequestBody HideAccountRequest request) {
        accountService.hideAccount(request.id(), request.hidden());
        return ApiResponse.success(null);
    }

    @PostMapping("/move.json")
    @Operation(summary = "Reorder accounts (drag-to-sort)")
    public ApiResponse<Void> reorderAccounts(@Valid @RequestBody ReorderAccountsRequest request) {
        accountService.reorderAccounts(request.orderedIds());
        return ApiResponse.success(null);
    }

    @PostMapping("/move/transactions")
    @Operation(summary = "Move all transactions from one account to another")
    public ApiResponse<Void> moveAllTransactions(@RequestBody MoveTransactionsRequest request) {
        accountService.moveAllTransactions(request.fromAccountId(), request.toAccountId());
        return ApiResponse.success(null);
    }

    // === Season 3 — Batch Operations ===

    @PostMapping("/batch_update/category.json")
    @Operation(summary = "Batch update category for transactions")
    public ApiResponse<Integer> batchUpdateCategory(@RequestBody BatchUpdateCategoryRequest request) {
        return ApiResponse.success(transactionService.batchUpdateCategory(request.transactionIds(), request.categoryId()));
    }

    @PostMapping("/batch_update/account.json")
    @Operation(summary = "Batch update account for transactions")
    public ApiResponse<Integer> batchUpdateAccount(@RequestBody BatchUpdateAccountRequest request) {
        return ApiResponse.success(transactionService.batchUpdateAccount(request.transactionIds(), request.accountId()));
    }

    @PostMapping("/batch_update/tag/add.json")
    @Operation(summary = "Batch add tags to transactions")
    public ApiResponse<Integer> batchAddTags(@RequestBody BatchAddTagsRequest request) {
        return ApiResponse.success(transactionService.batchAddTags(request.transactionIds(), request.tagIds()));
    }

    @PostMapping("/batch_update/tag/remove.json")
    @Operation(summary = "Batch remove tags from transactions")
    public ApiResponse<Integer> batchRemoveTags(@RequestBody BatchRemoveTagsRequest request) {
        return ApiResponse.success(transactionService.batchRemoveTags(request.transactionIds(), request.tagIds()));
    }

    @PostMapping("/batch_update/tag/clear.json")
    @Operation(summary = "Batch clear all tags from transactions")
    public ApiResponse<Integer> batchClearTags(@RequestBody BatchClearTagsRequest request) {
        return ApiResponse.success(transactionService.batchClearTags(request.transactionIds()));
    }

    @PostMapping("/batch_delete.json")
    @Operation(summary = "Batch delete transactions")
    public ApiResponse<Integer> batchDelete(@RequestBody BatchDeleteRequest request) {
        return ApiResponse.success(transactionService.batchDelete(request.ids()));
    }

    // === Request DTOs ===

    public record HideAccountRequest(long id, boolean hidden) {}
    public record MoveTransactionsRequest(long fromAccountId, long toAccountId) {}
    public record BatchUpdateCategoryRequest(List<Long> transactionIds, Long categoryId) {}
    public record BatchUpdateAccountRequest(List<Long> transactionIds, Long accountId) {}
    public record BatchAddTagsRequest(List<Long> transactionIds, List<Long> tagIds) {}
    public record BatchRemoveTagsRequest(List<Long> transactionIds, List<Long> tagIds) {}
    public record BatchClearTagsRequest(List<Long> transactionIds) {}
    public record BatchDeleteRequest(List<Long> ids) {}
}