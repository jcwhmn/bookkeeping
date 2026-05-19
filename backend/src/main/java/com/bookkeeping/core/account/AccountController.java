package com.bookkeeping.core.account;

import com.bookkeeping.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;
    
    @GetMapping
    public ApiResponse<List<AccountDto>> getAccounts() {
        Long userId = getCurrentUserId();
        List<AccountDto> accounts = accountService.getAccountsByUser(userId);
        return ApiResponse.success(accounts);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<AccountDto> getAccount(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        AccountDto account = accountService.getAccountById(userId, id);
        return ApiResponse.success(account);
    }
    
    @PostMapping
    public ApiResponse<AccountDto> createAccount(@RequestBody AccountService.CreateAccountRequest request) {
        Long userId = getCurrentUserId();
        AccountDto account = accountService.createAccount(userId, request);
        return ApiResponse.success(account);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<AccountDto> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountService.CreateAccountRequest request) {
        Long userId = getCurrentUserId();
        AccountDto account = accountService.updateAccount(userId, id, request);
        return ApiResponse.success(account);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        accountService.deleteAccount(userId, id);
        return ApiResponse.success(null);
    }
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found in SecurityContext");
        }
        return Long.parseLong(auth.getName());
    }
}