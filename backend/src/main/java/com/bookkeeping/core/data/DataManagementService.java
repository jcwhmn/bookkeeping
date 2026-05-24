package com.bookkeeping.core.data;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.account.AccountService;
import com.bookkeeping.core.category.CategoryRepository;
import com.bookkeeping.core.tag.TagRepository;
import com.bookkeeping.core.transaction.Transaction;
import com.bookkeeping.core.transaction.TransactionRepository;
import com.bookkeeping.core.transaction.TransactionSearchParams;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DataManagementService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final SecurityUtils securityUtils;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public DataManagementService(TransactionRepository transactionRepository,
                                 AccountRepository accountRepository,
                                 AccountService accountService,
                                 SecurityUtils securityUtils,
                                 CategoryRepository categoryRepository,
                                 TagRepository tagRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.securityUtils = securityUtils;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    // === Export ===

    public String exportAsCsv(TransactionSearchParams params) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> transactions = transactionRepository.searchWithFilters(userId, params);
        return buildCsv(transactions);
    }

    public String exportAsTsv(TransactionSearchParams params) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> transactions = transactionRepository.searchWithFilters(userId, params);
        return buildTsv(transactions);
    }

    private String buildCsv(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Date,Type,Account,Category,Amount,Description,Tags\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Transaction tx : transactions) {
            String date = tx.getTransactionTime() != null
                    ? java.time.Instant.ofEpochSecond(tx.getTransactionTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(fmt)
                    : "";
            String type = switch (tx.getTransactionType()) {
                case 1 -> "Modify";
                case 2 -> "Income";
                case 3 -> "Expense";
                case 4 -> "Transfer Out";
                case 5 -> "Transfer In";
                default -> String.valueOf(tx.getTransactionType());
            };
            String tags = tx.getTagIds() != null ? tx.getTagIds().replace(",", ";") : "";
            sb.append(String.format("%d,%s,%s,%d,%d,%d,%s,%s\n",
                    tx.getId(), date, type,
                    tx.getAccountId(), tx.getCategoryId(),
                    tx.getAmount(),
                    escapeCsv(tx.getDescription()),
                    escapeCsv(tags)));
        }
        return sb.toString();
    }

    private String buildTsv(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID\tDate\tType\tAccount\tCategory\tAmount\tDescription\tTags\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Transaction tx : transactions) {
            String date = tx.getTransactionTime() != null
                    ? java.time.Instant.ofEpochSecond(tx.getTransactionTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(fmt)
                    : "";
            String type = switch (tx.getTransactionType()) {
                case 1 -> "Modify";
                case 2 -> "Income";
                case 3 -> "Expense";
                case 4 -> "Transfer Out";
                case 5 -> "Transfer In";
                default -> String.valueOf(tx.getTransactionType());
            };
            String tags = tx.getTagIds() != null ? tx.getTagIds().replace(",", "\t") : "";
            sb.append(String.format("%d\t%s\t%s\t%d\t%d\t%d\t%s\t%s\n",
                    tx.getId(), date, type,
                    tx.getAccountId(), tx.getCategoryId(),
                    tx.getAmount(),
                    escapeCsv(tx.getDescription()),
                    escapeCsv(tags)));
        }
        return sb.toString();
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // === Clear ===

    @Transactional
    public void clearAll() {
        Long userId = securityUtils.requireCurrentUser().getId();
        transactionRepository.deleteByUserId(userId);
        accountRepository.deleteByUserId(userId);
        categoryRepository.deleteByUserId(userId);
        tagRepository.deleteByUserId(userId);
    }

    @Transactional
    public void clearAllTransactions() {
        Long userId = securityUtils.requireCurrentUser().getId();
        transactionRepository.deleteByUserId(userId);
    }

    @Transactional
    public void clearTransactionsByAccount(Long accountId) {
        Long userId = securityUtils.requireCurrentUser().getId();
        transactionRepository.deleteByAccountIdAndUserId(accountId, userId);
    }
}