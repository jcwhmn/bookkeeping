package com.bookkeeping.core.transaction;

import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.account.AccountService;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionStatisticsService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;

    public TransactionStatisticsService(TransactionRepository transactionRepository,
                                         TransactionService transactionService,
                                         AccountRepository accountRepository,
                                         SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<TrendItem> getTrends(String startYearMonth, String endYearMonth, List<Long> tagIds, String keyword) {
        Long userId = securityUtils.requireCurrentUser().getId();
        DateTimeFormatter fmter = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate start = LocalDate.parse(startYearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate end = LocalDate.parse(endYearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<TrendItem> trends = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            long startTime = cursor.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            LocalDate nextMonth = cursor.plusMonths(1);
            long endTime = nextMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

            List<Transaction> txs = transactionRepository.searchTransactions(userId, startTime, endTime, null, tagIds, keyword);
            long income = txs.stream().filter(t -> t.getTransactionType() == 2).mapToLong(Transaction::getAmount).sum();
            long expense = txs.stream().filter(t -> t.getTransactionType() == 3).mapToLong(Transaction::getAmount).sum();

            trends.add(new TrendItem(
                    cursor.format(fmter),
                    cursor.getYear(),
                    cursor.getMonthValue(),
                    income,
                    expense,
                    income - expense
            ));
            cursor = nextMonth;
        }
        return trends;
    }

    @Transactional(readOnly = true)
    public List<AssetTrendItem> getAssetTrends(Long startTime, Long endTime) {
        Long userId = securityUtils.requireCurrentUser().getId();
        LocalDate start = startTime != null
                ? java.time.Instant.ofEpochSecond(startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now().minusMonths(12);
        LocalDate end = endTime != null
                ? java.time.Instant.ofEpochSecond(endTime).atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now();
        List<AssetTrendItem> trends = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            long time = cursor.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            long balance = transactionService.getAccountBalanceAt(time, userId);
            trends.add(new AssetTrendItem(time, balance));
            cursor = cursor.plusMonths(1);
        }
        return trends;
    }

    @Transactional(readOnly = true)
    public ReconciliationStatement getReconciliationStatement(Long accountId, Long startTime, Long endTime) {
        Long userId = securityUtils.requireCurrentUser().getId();
        var accountOpt = accountRepository.findByIdAndUserIdAndDeletedFalse(accountId, userId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        var transactions = transactionRepository.findByAccountIdAndTimeRange(accountId, startTime, endTime);
        long totalInflows = transactions.stream().filter(t -> t.getTransactionType() == 2).mapToLong(Transaction::getAmount).sum();
        long totalOutflows = transactions.stream().filter(t -> t.getTransactionType() == 3).mapToLong(Transaction::getAmount).sum();
        long openingBalance = transactionRepository.sumBalanceBefore(accountId, startTime);
        long closingBalance = openingBalance + totalInflows - totalOutflows;
        return new ReconciliationStatement(transactions, totalInflows, totalOutflows, openingBalance, closingBalance);
    }

    public record TrendItem(String yearMonth, int year, int month, long income, long expense, long net) {}
    public record AssetTrendItem(long time, long balance) {}
    public record ReconciliationStatement(
            List<Transaction> transactions,
            long totalInflows,
            long totalOutflows,
            long openingBalance,
            long closingBalance
    ) {}
}