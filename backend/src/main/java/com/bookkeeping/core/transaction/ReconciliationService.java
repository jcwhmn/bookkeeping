package com.bookkeeping.core.transaction;

import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReconciliationService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;

    public ReconciliationService(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository,
                                  SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Generate a reconciliation statement for an account within a date range.
     */
    @Transactional(readOnly = true)
    public ReconciliationStatement getStatement(Long accountId, Long startTime, Long endTime) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Verify account belongs to user
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(accountId, userId)
                .orElseThrow(() -> new BusinessException(
                        com.bookkeeping.common.ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        // Get opening balance (balance before start time)
        long openingBalance = transactionRepository.sumBalanceBefore(accountId, startTime);

        // Get transactions in range
        List<Transaction> transactions = transactionRepository.findByAccountIdAndTimeRange(accountId, startTime, endTime);

        // Calculate totals
        long totalIn = 0, totalOut = 0;
        for (Transaction tx : transactions) {
            if (tx.getTransactionType() == 2 || tx.getTransactionType() == 1) {
                totalIn += tx.getAmount();
            } else if (tx.getTransactionType() == 3 || tx.getTransactionType() == 4) {
                totalOut += tx.getAmount();
            }
        }

        long closingBalance = openingBalance + totalIn - totalOut;

        return new ReconciliationStatement(
                accountId,
                account.getName(),
                startTime,
                endTime,
                openingBalance,
                totalIn,
                totalOut,
                closingBalance,
                transactions.size()
        );
    }

    /**
     * Reconcile: mark account balance as verified.
     */
    @Transactional
    public ReconciliationResult reconcile(Long accountId, Long endTime, Long statementBalance) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Verify account
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(accountId, userId)
                .orElseThrow(() -> new BusinessException(
                        com.bookkeeping.common.ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        // Get actual balance from transactions
        long openingBalance = transactionRepository.sumBalanceBefore(accountId, endTime);
        List<Transaction> txs = transactionRepository.findByAccountIdAndTimeRange(accountId, 0L, endTime);

        long totalIn = 0, totalOut = 0;
        for (Transaction tx : txs) {
            if (tx.getTransactionType() == 2 || tx.getTransactionType() == 1) {
                totalIn += tx.getAmount();
            } else if (tx.getTransactionType() == 3 || tx.getTransactionType() == 4) {
                totalOut += tx.getAmount();
            }
        }
        long actualBalance = openingBalance + totalIn - totalOut;

        boolean matches = (actualBalance == statementBalance);

        return new ReconciliationResult(
                accountId,
                account.getName(),
                statementBalance,
                actualBalance,
                matches,
                matches ? "Reconciliation successful" : "Balance mismatch - check transactions"
        );
    }

    // === DTOs ===

    public record ReconciliationStatement(
            Long accountId,
            String accountName,
            Long startTime,
            Long endTime,
            long openingBalance,
            long totalIn,
            long totalOut,
            long closingBalance,
            int transactionCount
    ) {}

    public record ReconciliationResult(
            Long accountId,
            String accountName,
            long statementBalance,
            long actualBalance,
            boolean matches,
            String message
    ) {}
}