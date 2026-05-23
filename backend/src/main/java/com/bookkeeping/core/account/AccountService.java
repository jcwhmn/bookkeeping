package com.bookkeeping.core.account;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for account management.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final SecurityUtils securityUtils;

    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          SecurityUtils securityUtils) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.securityUtils = securityUtils;
    }

    /**
     * Get all accounts for the current user.
     */
    @Transactional(readOnly = true)
    public List<AccountDto> getCurrentUserAccounts() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return accountRepository.findByUserIdAndDeletedFalse(userId).stream()
                .map(accountMapper::toDto)
                .toList();
    }

    /**
     * Get account by ID for the current user.
     */
    @Transactional(readOnly = true)
    public AccountDto getAccount(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));
        return accountMapper.toDto(account);
    }

    /**
     * Create a new account for the current user.
     */
    @Transactional
    public AccountDto createAccount(CreateAccountRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Check if account with same name already exists
        if (accountRepository.existsByNameAndUserIdAndDeletedFalse(request.name(), userId)) {
            throw new BusinessException(ResultCode.ACCOUNT_ALREADY_EXISTS, 
                    "Account with name '" + request.name() + "' already exists");
        }

        Account account = Account.builder()
                .name(request.name())
                .accountType(request.accountType())
                .currency(request.currency())
                .balance(request.initialBalance())
                .userId(userId)
                .description(request.description())
                .deleted(false)
                .build();

        Account saved = accountRepository.save(account);
        return accountMapper.toDto(saved);
    }

    /**
     * Update an existing account.
     */
    @Transactional
    public AccountDto updateAccount(Long id, UpdateAccountRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        Account.AccountBuilder builder = account.toBuilder();
        if (request.name() != null) {
            // Check if another account has the same name
            if (!request.name().equals(account.getName()) 
                    && accountRepository.existsByNameAndUserIdAndDeletedFalse(request.name(), userId)) {
                throw new BusinessException(ResultCode.ACCOUNT_ALREADY_EXISTS,
                        "Account with name '" + request.name() + "' already exists");
            }
            builder.name(request.name());
        }
        if (request.description() != null) {
            builder.description(request.description());
        }

        return accountMapper.toDto(accountRepository.save(builder.build()));
    }

    /**
     * Delete an account (soft delete).
     */
    @Transactional
    public void deleteAccount(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        accountRepository.save(account.toBuilder().deleted(true).build());
    }

    /**
     * Update account balance (used by transaction module).
     */
    @Transactional
    public void updateBalance(Long accountId, Long amountChange) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));
        accountRepository.save(account.toBuilder().balance(account.getBalance() + amountChange).build());
    }

    /**
     * Check if account exists and belongs to user.
     */
    @Transactional(readOnly = true)
    public boolean accountExists(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserIdAndDeletedFalse(accountId, userId).isPresent();
    }
}
