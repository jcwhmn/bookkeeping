package com.bookkeeping.core.transaction;

import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionAmountsService {

    private final TransactionRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public TransactionAmountsService(TransactionRepository transactionRepository, SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<AmountsItem> getAmounts(String query, List<Long> excludeAccountIds, List<Long> excludeCategoryIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<AmountsItem> results = new ArrayList<>();

        if (query == null || query.isBlank()) return results;

        for (String item : query.split("\\|")) {
            String[] parts = item.trim().split("_");
            if (parts.length < 3) continue;
            String name = parts[0];
            long startTime = Long.parseLong(parts[1]);
            long endTime = Long.parseLong(parts[2]);

            List<Transaction> txs = transactionRepository.searchTransactions(
                    userId, startTime, endTime, null, null, null);
            if (excludeAccountIds != null && !excludeAccountIds.isEmpty()) {
                txs = txs.stream().filter(t -> !excludeAccountIds.contains(t.getAccountId())).toList();
            }
            if (excludeCategoryIds != null && !excludeCategoryIds.isEmpty()) {
                txs = txs.stream().filter(t -> !excludeCategoryIds.contains(t.getCategoryId())).toList();
            }

            long income = txs.stream().filter(t -> t.getTransactionType() == 2).mapToLong(Transaction::getAmount).sum();
            long expense = txs.stream().filter(t -> t.getTransactionType() == 3).mapToLong(Transaction::getAmount).sum();

            results.add(new AmountsItem(startTime, endTime, name, income, expense, income - expense));
        }
        return results;
    }

    public record AmountsItem(
            long startTime,
            long endTime,
            String name,
            long incomeAmount,
            long expenseAmount,
            long netAmount
    ) {}
}