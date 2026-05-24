package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.AccountService;
import com.bookkeeping.core.category.CategoryService;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final SecurityUtils securityUtils;
    private final CategoryService categoryService;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              SecurityUtils securityUtils,
                              CategoryService categoryService,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.securityUtils = securityUtils;
        this.categoryService = categoryService;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getRecentTransactions(int limit) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return transactionRepository
                .findByUserIdOrderByTransactionTimeDesc(userId, PageRequest.of(0, limit))
                .stream().map(transactionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByMonth(int year, int month) {
        Long userId = securityUtils.requireCurrentUser().getId();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        return transactionRepository.findByUserIdAndMonth(userId, startTime, endTime)
                .stream().map(transactionMapper::toDto).toList();
    }

    /**
     * Cursor-based paginated list — use for all "load more" patterns.
     * @param cursor  transactionTime of the last item from previous page (exclusive upper bound)
     * @param limit   max items to return
     */
    @Transactional(readOnly = true)
    public TransactionPageResponse listByCursor(Long cursor, int limit) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdBeforeCursor(
                userId, cursor, PageRequest.of(0, limit + 1));
        Long nextCursor = null;
        if (txs.size() > limit) {
            nextCursor = txs.get(limit - 1).getTransactionTime();
            txs = txs.subList(0, limit);
        }
        return new TransactionPageResponse(
                txs.stream().map(transactionMapper::toDto).toList(),
                nextCursor);
    }

    /**
     * List all transactions for the user (unpaginated). Use for export.
     */
    @Transactional(readOnly = true)
    public List<TransactionDto> listAll() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId)
                .stream().map(transactionMapper::toDto).toList();
    }

    /**
     * Count transactions matching the given filters.
     */
    @Transactional(readOnly = true)
    public long countTransactions(TransactionSearchParams params) {
        Long userId = securityUtils.requireCurrentUser().getId();
        if (params.hasFilters()) {
            return transactionRepository.countWithFilters(
                    userId,
                    params.year(),
                    params.month(),
                    params.accountId() != null ? params.accountId().longValue() : null,
                    params.categoryId() != null ? params.categoryId().longValue() : null,
                    params.transactionType(),
                    params.search());
        }
        return transactionRepository.countByUserId(userId);
    }

    /**
     * Search/filter transactions with DB-level filtering (replaces in-memory approach).
     * All filters are applied at the database level via JPQL.
     */
    @Transactional(readOnly = true)
    public List<TransactionDto> searchTransactions(TransactionSearchParams params, int limit) {
        Long userId = securityUtils.requireCurrentUser().getId();
        if (params.hasFilters()) {
            return transactionRepository.findWithFilters(
                    userId,
                    params.year(),
                    params.month(),
                    params.accountId() != null ? params.accountId().longValue() : null,
                    params.categoryId() != null ? params.categoryId().longValue() : null,
                    params.transactionType(),
                    params.search(),
                    PageRequest.of(0, limit)
            ).stream().map(transactionMapper::toDto).toList();
        }
        return transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId, PageRequest.of(0, limit))
                .stream().map(transactionMapper::toDto).toList();
    }

    @Transactional
    public TransactionDto createTransaction(CreateTransactionRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Long now = System.currentTimeMillis() / 1000;
        Long transactionTime = request.transactionTime() != null ? request.transactionTime() : now;

        Transaction tx = Transaction.builder()
                .transactionType(request.transactionType())
                .accountId(request.accountId())
                .categoryId(request.categoryId())
                .amount(request.amount())
                .description(request.description())
                .transactionTime(transactionTime)
                .userId(userId)
                .tagIds(request.tagIds())
                .build();

        // Handle transfer (type 4 = TRANSFER_OUT)
        if (request.transactionType() == 4 && request.destinationAccountId() != null) {
            if (request.accountId().equals(request.destinationAccountId())) {
                throw new BusinessException(ResultCode.TRANSACTION_INVALID,
                        "Source and destination accounts must be different");
            }

            accountService.updateBalance(request.accountId(), -Math.abs(request.amount()));
            accountService.updateBalance(request.destinationAccountId(), request.amount());
            Transaction saved = transactionRepository.save(tx);

            Transaction transferIn = Transaction.builder()
                    .transactionType(5)
                    .accountId(request.destinationAccountId())
                    .categoryId(null)
                    .amount(request.amount())
                    .description(request.description())
                    .transactionTime(transactionTime)
                    .userId(userId)
                    .relatedId(saved.getId())
                    .build();
            transferIn = transactionRepository.save(transferIn);

            saved = transactionRepository.save(saved.toBuilder().relatedId(transferIn.getId()).build());
            return transactionMapper.toDto(saved);
        }

        switch (request.transactionType()) {
            case 1, 2 -> accountService.updateBalance(request.accountId(), request.amount());
            case 3 -> accountService.updateBalance(request.accountId(), -Math.abs(request.amount()));
            case 4 -> accountService.updateBalance(request.accountId(), -Math.abs(request.amount()));
            default -> throw new BusinessException(ResultCode.VALIDATION_ERROR, "Invalid transaction type");
        }

        return transactionMapper.toDto(transactionRepository.save(tx));
    }

    @Transactional
    public TransactionDto updateTransaction(Long id, UpdateTransactionRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Transaction existing = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.TRANSACTION_NOT_FOUND, "Transaction not found"));

        if (existing.getRelatedId() != null) {
            existing = existing.toBuilder()
                    .description(request.description())
                    .tagIds(request.tagIds())
                    .build();
            return transactionMapper.toDto(transactionRepository.save(existing));
        }

        Long oldAmount = existing.getAmount();
        Long oldAccountId = existing.getAccountId();
        Long oldChange = calculateBalanceChange(existing.getTransactionType(), oldAmount);
        Long newChange = calculateBalanceChange(request.transactionType(), request.amount());

        accountService.updateBalance(oldAccountId, -oldChange);

        if (request.accountId().equals(oldAccountId)) {
            accountService.updateBalance(request.accountId(), newChange);
        } else {
            accountService.updateBalance(request.accountId(), newChange);
        }

        Transaction.TransactionBuilder builder = existing.toBuilder()
                .transactionType(request.transactionType())
                .accountId(request.accountId())
                .categoryId(request.categoryId())
                .amount(request.amount())
                .description(request.description())
                .tagIds(request.tagIds());
        if (request.transactionTime() != null) {
            builder.transactionTime(request.transactionTime());
        }

        return transactionMapper.toDto(transactionRepository.save(builder.build()));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Transaction existing = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.TRANSACTION_NOT_FOUND, "Transaction not found"));

        if (existing.getRelatedId() != null) {
            transactionRepository.findById(existing.getRelatedId())
                    .ifPresent(related -> {
                        Long relatedChange = calculateBalanceChange(related.getTransactionType(), related.getAmount());
                        accountService.updateBalance(related.getAccountId(), -relatedChange);
                        transactionRepository.delete(related);
                    });
        }

        Long change = calculateBalanceChange(existing.getTransactionType(), existing.getAmount());
        accountService.updateBalance(existing.getAccountId(), -change);
        transactionRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public StatisticsDto getStatistics(int year, int month) {
        Long userId = securityUtils.requireCurrentUser().getId();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        List<Transaction> transactions = transactionRepository.findByUserIdAndMonth(userId, startTime, endTime);

        long totalIncome = 0, totalExpense = 0;
        List<Transaction> incomeTxs = new java.util.ArrayList<>();
        List<Transaction> expenseTxs = new java.util.ArrayList<>();

        for (Transaction tx : transactions) {
            switch (tx.getTransactionType()) {
                case 2 -> { totalIncome += tx.getAmount(); incomeTxs.add(tx); }
                case 3 -> { totalExpense += tx.getAmount(); expenseTxs.add(tx); }
            }
        }

        StatisticsDto.CategoryBreakdown[] incomeBreakdown = buildCategoryBreakdown(incomeTxs, totalIncome);
        StatisticsDto.CategoryBreakdown[] expenseBreakdown = buildCategoryBreakdown(expenseTxs, totalExpense);

        return new StatisticsDto(
            totalIncome,
            totalExpense,
            totalIncome - totalExpense,
            (long) transactions.size(),
            incomeBreakdown,
            expenseBreakdown
        );
    }

    private StatisticsDto.CategoryBreakdown[] buildCategoryBreakdown(List<Transaction> transactions, long total) {
        Map<Long, List<Transaction>> byCategory = transactions.stream()
            .filter(tx -> tx.getCategoryId() != null)
            .collect(Collectors.groupingBy(Transaction::getCategoryId));

        List<StatisticsDto.CategoryBreakdown> breakdowns = new java.util.ArrayList<>();
        for (Map.Entry<Long, List<Transaction>> entry : byCategory.entrySet()) {
            long amount = entry.getValue().stream().mapToLong(Transaction::getAmount).sum();
            long count = entry.getValue().size();
            double percentage = total > 0 ? (amount * 100.0 / total) : 0;
            String name = categoryService.getCategoryById(entry.getKey()).map(c -> c.name()).orElse("Unknown");
            breakdowns.add(new StatisticsDto.CategoryBreakdown(
                entry.getKey(), name, amount, count, percentage
            ));
        }
        breakdowns.sort((a, b) -> Long.compare(b.amount(), a.amount()));
        return breakdowns.toArray(new StatisticsDto.CategoryBreakdown[0]);
    }

    private Long calculateBalanceChange(Integer transactionType, Long amount) {
        return switch (transactionType) {
            case 1, 2, 5 -> amount;
            case 3, 4 -> -Math.abs(amount);
            default -> 0L;
        };
    }

    // === Batch Operations ===

    @Transactional
    public int batchUpdateCategory(List<Long> ids, Long categoryId) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdAndIds(userId, ids);
        for (Transaction tx : txs) {
            transactionRepository.save(tx.toBuilder().categoryId(categoryId).build());
        }
        return txs.size();
    }

    @Transactional
    public int batchUpdateAccount(List<Long> ids, Long accountId) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdAndIds(userId, ids);
        for (Transaction tx : txs) {
            // Revert balance on old account, apply to new
            Long change = calculateBalanceChange(tx.getTransactionType(), tx.getAmount());
            accountService.updateBalance(tx.getAccountId(), -change);
            accountService.updateBalance(accountId, change);
            transactionRepository.save(tx.toBuilder().accountId(accountId).build());
        }
        return txs.size();
    }

    @Transactional
    public int batchAddTags(List<Long> ids, List<Long> tagIdsToAdd) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdAndIds(userId, ids);
        for (Transaction tx : txs) {
            String existing = tx.getTagIds();
            List<Long> merged = parseTagIds(existing);
            for (Long tagId : tagIdsToAdd) {
                if (!merged.contains(tagId)) merged.add(tagId);
            }
            transactionRepository.save(tx.toBuilder().tagIds(serializeTagIds(merged)).build());
        }
        return txs.size();
    }

    @Transactional
    public int batchRemoveTags(List<Long> ids, List<Long> tagIdsToRemove) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdAndIds(userId, ids);
        for (Transaction tx : txs) {
            List<Long> current = parseTagIds(tx.getTagIds());
            current.removeAll(tagIdsToRemove);
            transactionRepository.save(tx.toBuilder().tagIds(serializeTagIds(current)).build());
        }
        return txs.size();
    }

    @Transactional
    public int batchClearTags(List<Long> ids) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdAndIds(userId, ids);
        for (Transaction tx : txs) {
            transactionRepository.save(tx.toBuilder().tagIds(null).build());
        }
        return txs.size();
    }

    @Transactional
    public int batchDelete(List<Long> ids) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> txs = transactionRepository.findByUserIdAndIds(userId, ids);
        for (Transaction tx : txs) {
            deleteTransaction(tx.getId());  // re-use existing logic
        }
        return txs.size();
    }

    // === Tag ID Helpers ===

    private List<Long> parseTagIds(String tagIds) {
        if (tagIds == null || tagIds.isBlank()) return new java.util.ArrayList<>();
        return java.util.Arrays.stream(tagIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

    private String serializeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return null;
        return String.join(",", tagIds.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList()));
    }
}