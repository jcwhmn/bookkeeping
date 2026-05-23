package com.bookkeeping.core.account;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for account management.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management APIs")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "List accounts", description = "Get all accounts for the current user")
    public ApiResponse<List<AccountDto>> listAccounts() {
        return ApiResponse.success(accountService.getCurrentUserAccounts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account", description = "Get account by ID")
    public ApiResponse<AccountDto> getAccount(@PathVariable Long id) {
        return ApiResponse.success(accountService.getAccount(id));
    }

    @PostMapping
    @Operation(summary = "Create account", description = "Create a new account")
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
    @Operation(summary = "Delete account", description = "Delete an account (soft delete)")
    public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ApiResponse.success(null);
    }
}
