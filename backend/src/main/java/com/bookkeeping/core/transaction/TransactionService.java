package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.AccountService;
import com.bookkeeping.core.category.CategoryService;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
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
        
        // Calculate start and end timestamps for the month
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        
        long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        
        return transactionRepository.findByUserIdAndMonth(userId, startTime, endTime)
                .stream().map(transactionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> searchTransactions(TransactionSearchParams params, int limit) {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<Transaction> results;
        
        if (params.hasFilters()) {
            // Get all user transactions (up to limit * 10 for filtering)
            results = transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId, PageRequest.of(0, limit * 10));
            
            // Apply filters
            results = results.stream().filter(tx -> {
                // Year filter
                if (params.year() != null && params.month() != null) {
                    LocalDate txDate = LocalDate.ofEpochDay(tx.getTransactionTime() / 86400);
                    if (txDate.getYear() != params.year() || txDate.getMonthValue() != params.month()) {
                        return false;
                    }
                }
                // Account filter
                if (params.accountId() != null && !tx.getAccountId().equals(params.accountId().longValue())) {
                    return false;
                }
                // Category filter
                if (params.categoryId() != null && (tx.getCategoryId() == null || !tx.getCategoryId().equals(params.categoryId().longValue()))) {
                    return false;
                }
                // Type filter
                if (params.transactionType() != null && tx.getTransactionType() != params.transactionType()) {
                    return false;
                }
                // Search filter
                if (params.search() != null && !params.search().isBlank()) {
                    String searchLower = params.search().toLowerCase();
                    boolean matches = tx.getDescription() != null && tx.getDescription().toLowerCase().contains(searchLower);
                    if (!matches) return false;
                }
                return true;
            }).toList();
            
            // Limit results
            if (results.size() > limit) {
                results = results.subList(0, limit);
            }
        } else {
            results = transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId, PageRequest.of(0, limit));
        }
        
        return results.stream().map(transactionMapper::toDto).toList();
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
            // Validate accounts are different
            if (request.accountId().equals(request.destinationAccountId())) {
                throw new BusinessException(ResultCode.TRANSACTION_INVALID, 
                        "Source and destination accounts must be different");
            }

            // Create TRANSFER_OUT record
            tx = tx.toBuilder().transactionType(4).build();
            accountService.updateBalance(request.accountId(), -Math.abs(request.amount()));
            accountService.updateBalance(request.destinationAccountId(), request.amount());
            Transaction saved = transactionRepository.save(tx);

            // Create TRANSFER_IN record
            Transaction transferIn = Transaction.builder()
                    .transactionType(5) // TRANSFER_IN
                    .accountId(request.destinationAccountId())
                    .categoryId(null)
                    .amount(request.amount())
                    .description(request.description())
                    .transactionTime(transactionTime)
                    .userId(userId)
                    .relatedId(saved.getId())
                    .build();
            transferIn = transactionRepository.save(transferIn);

            // Link back
            saved = transactionRepository.save(saved.toBuilder().relatedId(transferIn.getId()).build());

            return transactionMapper.toDto(saved);
        }

        // Handle other transaction types
        switch (request.transactionType()) {
            case 1, 2 -> {
                // MODIFY_BALANCE or INCOME -> add
                accountService.updateBalance(request.accountId(), request.amount());
            }
            case 3 -> {
                // EXPENSE -> subtract
                accountService.updateBalance(request.accountId(), -Math.abs(request.amount()));
            }
            case 4 -> {
                // TRANSFER_OUT (without destination means just balance adjustment)
                accountService.updateBalance(request.accountId(), -Math.abs(request.amount()));
            }
            default -> throw new BusinessException(ResultCode.VALIDATION_ERROR, "Invalid transaction type");
        }

        return transactionMapper.toDto(transactionRepository.save(tx));
    }

    @Transactional
    public TransactionDto updateTransaction(Long id, UpdateTransactionRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Transaction existing = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.TRANSACTION_NOT_FOUND, "Transaction not found"));

        // Handle linked transfers (TRANSFER_OUT/TRANSFER_IN pairs)
        if (existing.getRelatedId() != null) {
            // For transfers, only allow editing notes/description
            existing = existing.toBuilder()
                    .description(request.description())
                    .tagIds(request.tagIds())
                    .build();
            return transactionMapper.toDto(transactionRepository.save(existing));
        }

        // Calculate balance change from old transaction
        Long oldAmount = existing.getAmount();
        Long oldAccountId = existing.getAccountId();
        Long oldChange = calculateBalanceChange(existing.getTransactionType(), oldAmount);

        // Calculate new balance change
        Long newChange = calculateBalanceChange(request.transactionType(), request.amount());

        // Revert old balance change
        accountService.updateBalance(oldAccountId, -oldChange);

        // Apply new balance change (if account changed, apply to new account too)
        if (request.accountId().equals(oldAccountId)) {
            // Same account: apply net change
            accountService.updateBalance(request.accountId(), newChange);
        } else {
            // Different account: apply full change to new account
            accountService.updateBalance(request.accountId(), newChange);
        }

        // Update transaction
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

        // Handle linked transfers
        if (existing.getRelatedId() != null) {
            // Delete the related transaction
            transactionRepository.findById(existing.getRelatedId())
                    .ifPresent(related -> {
                        // Revert balance for related transaction
                        Long relatedChange = calculateBalanceChange(related.getTransactionType(), related.getAmount());
                        accountService.updateBalance(related.getAccountId(), -relatedChange);
                        transactionRepository.delete(related);
                    });
        }

        // Revert balance change
        Long change = calculateBalanceChange(existing.getTransactionType(), existing.getAmount());
        accountService.updateBalance(existing.getAccountId(), -change);

        // Delete transaction
        transactionRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public StatisticsDto getStatistics(int year, int month) {
        Long userId = securityUtils.requireCurrentUser().getId();
        
        // Calculate start and end timestamps
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        
        // Get transactions for the month
        List<Transaction> transactions = transactionRepository.findByUserIdAndMonth(userId, startTime, endTime);
        
        // Calculate totals
        long totalIncome = 0, totalExpense = 0;
        List<Transaction> incomeTxs = new java.util.ArrayList<>();
        List<Transaction> expenseTxs = new java.util.ArrayList<>();
        
        for (Transaction tx : transactions) {
            switch (tx.getTransactionType()) {
                case 2 -> { totalIncome += tx.getAmount(); incomeTxs.add(tx); }
                case 3 -> { totalExpense += tx.getAmount(); expenseTxs.add(tx); }
            }
        }
        
        // Build category breakdowns
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
        // Group by category
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
        // Sort by amount descending
        breakdowns.sort((a, b) -> Long.compare(b.amount(), a.amount()));
        return breakdowns.toArray(new StatisticsDto.CategoryBreakdown[0]);
    }

    /**
     * Calculate the balance change for an account based on transaction type.
     * Returns positive for additions, negative for subtractions.
     */
    private Long calculateBalanceChange(Integer transactionType, Long amount) {
        return switch (transactionType) {
            case 1, 2, 5 -> amount; // MODIFY_BALANCE, INCOME, TRANSFER_IN: add
            case 3, 4 -> -Math.abs(amount); // EXPENSE, TRANSFER_OUT: subtract
            default -> 0L;
        };
    }

}