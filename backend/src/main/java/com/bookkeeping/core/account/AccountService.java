package com.bookkeeping.core.account;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.transaction.TransactionRepository;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Business logic for account management.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final SecurityUtils securityUtils;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          SecurityUtils securityUtils,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.securityUtils = securityUtils;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getCurrentUserAccounts() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return accountRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(userId).stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountDto getAccount(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto createAccount(CreateAccountRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
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
                .sortOrder(0)
                .hidden(false)
                .parentId(request.parentId())
                .build();

        Account saved = accountRepository.save(account);
        return accountMapper.toDto(saved);
    }

    @Transactional
    public AccountDto updateAccount(Long id, UpdateAccountRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        Account.AccountBuilder builder = account.toBuilder();
        if (request.name() != null) {
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

    @Transactional
    public void deleteAccount(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        // Soft-delete account and all sub-accounts
        accountRepository.save(account.toBuilder().deleted(true).build());
        List<Account> allAccounts = accountRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(userId);
        for (Account sub : allAccounts) {
            if (id.equals(sub.getParentId())) {
                accountRepository.save(sub.toBuilder().deleted(true).build());
            }
        }
    }

    @Transactional
    public void hideAccount(Long id, boolean hidden) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));
        accountRepository.save(account.toBuilder().hidden(hidden).build());
    }

    @Transactional
    public void reorderAccounts(List<Long> orderedIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < orderedIds.size(); i++) {
            accountRepository.updateSortOrder(orderedIds.get(i), userId, i);
        }
    }

    /**
     * Move all transactions from one account to another.
     * Transfers both balance and transaction references atomically.
     * Source account balance becomes 0 after move.
     */
    @Transactional
    public void moveAllTransactions(Long fromAccountId, Long toAccountId) {
        Long userId = securityUtils.requireCurrentUser().getId();

        Account from = accountRepository.findByIdAndUserIdAndDeletedFalse(fromAccountId, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Source account not found"));
        Account to = accountRepository.findByIdAndUserIdAndDeletedFalse(toAccountId, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Target account not found"));

        if (fromAccountId.equals(toAccountId)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Source and target accounts must be different");
        }

        Long fromBalance = from.getBalance();
        Long toBalance = to.getBalance();

        // Move all transactions to target account
        transactionRepository.moveAllTransactions(fromAccountId, toAccountId, userId);

        // Zero out source, add to target
        accountRepository.save(from.toBuilder().balance(0L).build());
        accountRepository.save(to.toBuilder().balance(toBalance + fromBalance).build());
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

    @Transactional(readOnly = true)
    public boolean accountExists(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserIdAndDeletedFalse(accountId, userId).isPresent();
    }
}