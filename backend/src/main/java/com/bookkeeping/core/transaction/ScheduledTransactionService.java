package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.category.Category;
import com.bookkeeping.core.category.CategoryRepository;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduledTransactionService {

    private final ScheduledTransactionRepository repository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;

    public ScheduledTransactionService(
            ScheduledTransactionRepository repository,
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            SecurityUtils securityUtils) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.securityUtils = securityUtils;
    }

    // === CRUD Operations ===

    @Transactional(readOnly = true)
    public List<ScheduledTransactionDto> listByUser() {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<ScheduledTransaction> scheduled = repository.findByUserIdAndDeletedFalse(userId);
        return scheduled.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ScheduledTransactionDto getById(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        ScheduledTransaction st = repository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Scheduled transaction not found"));
        return toDto(st);
    }

    @Transactional
    public ScheduledTransactionDto create(CreateScheduledTransactionRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Validate account exists
        Account account = accountRepository.findByIdAndUserIdAndDeletedFalse(request.accountId(), userId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Account not found"));

        // Validate destination account for transfers
        if (request.transactionType() == 4 && request.destinationAccountId() != null) {
            accountRepository.findByIdAndUserIdAndDeletedFalse(request.destinationAccountId(), userId)
                    .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND, "Destination account not found"));
        }

        // Calculate next run time
        Long nextRunTime = calculateNextRunTime(request.frequency(), request.intervalDays(),
                request.dayOfWeek(), request.dayOfMonth(), request.monthOfYear(), request.startDate());

        ScheduledTransaction st = ScheduledTransaction.builder()
                .userId(userId)
                .transactionType(request.transactionType())
                .accountId(request.accountId())
                .categoryId(request.categoryId())
                .destinationAccountId(request.destinationAccountId())
                .amount(request.amount())
                .description(request.description())
                .tagIds(request.tagIds())
                .frequency(request.frequency())
                .intervalDays(request.intervalDays() != null ? request.intervalDays() : 1)
                .dayOfWeek(request.dayOfWeek())
                .dayOfMonth(request.dayOfMonth())
                .monthOfYear(request.monthOfYear())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .nextRunTime(nextRunTime)
                .active(true)
                .runCount(0)
                .build();

        st = repository.save(st);
        return toDto(st);
    }

    @Transactional
    public ScheduledTransactionDto update(Long id, UpdateScheduledTransactionRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        ScheduledTransaction st = repository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Scheduled transaction not found"));

        // Update fields if provided
        if (request.transactionType() != null) st.setTransactionType(request.transactionType());
        if (request.accountId() != null) st.setAccountId(request.accountId());
        if (request.categoryId() != null) st.setCategoryId(request.categoryId());
        if (request.destinationAccountId() != null) st.setDestinationAccountId(request.destinationAccountId());
        if (request.amount() != null) st.setAmount(request.amount());
        if (request.description() != null) st.setDescription(request.description());
        if (request.tagIds() != null) st.setTagIds(request.tagIds());
        if (request.frequency() != null) st.setFrequency(request.frequency());
        if (request.intervalDays() != null) st.setIntervalDays(request.intervalDays());
        if (request.dayOfWeek() != null) st.setDayOfWeek(request.dayOfWeek());
        if (request.dayOfMonth() != null) st.setDayOfMonth(request.dayOfMonth());
        if (request.monthOfYear() != null) st.setMonthOfYear(request.monthOfYear());
        if (request.startDate() != null) st.setStartDate(request.startDate());
        if (request.endDate() != null) st.setEndDate(request.endDate());

        // Recalculate next run time if schedule changed
        if (request.frequency() != null || request.intervalDays() != null || request.startDate() != null) {
            Long nextRunTime = calculateNextRunTime(
                    st.getFrequency(), st.getIntervalDays(),
                    st.getDayOfWeek(), st.getDayOfMonth(), st.getMonthOfYear(),
                    st.getStartDate());
            st.setNextRunTime(nextRunTime);
        }

        if (request.active() != null) st.setActive(request.active());

        st = repository.save(st);
        return toDto(st);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        ScheduledTransaction st = repository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Scheduled transaction not found"));
        st.setDeleted(true);
        st.setDeletedAt(System.currentTimeMillis() / 1000);
        repository.save(st);
    }

    @Transactional
    public void toggleActive(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        ScheduledTransaction st = repository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Scheduled transaction not found"));
        st.setActive(!st.getActive());
        repository.save(st);
    }

    @Transactional(readOnly = true)
    public ScheduledTransactionStats getStats() {
        Long userId = securityUtils.requireCurrentUser().getId();
        long total = repository.countActiveByUserId(userId);
        List<Object[]> freqCounts = repository.countByFrequency(userId);

        long daily = 0, weekly = 0, monthly = 0, yearly = 0;
        for (Object[] row : freqCounts) {
            String freq = (String) row[0];
            long count = (Long) row[1];
            switch (freq) {
                case "daily" -> daily = count;
                case "weekly" -> weekly = count;
                case "monthly" -> monthly = count;
                case "yearly" -> yearly = count;
            }
        }

        // Count upcoming
        LocalDate now = LocalDate.now();
        long next7 = 0, next30 = 0;
        // Simplified - would need actual implementation

        return new ScheduledTransactionStats(total, total, daily, weekly, monthly, yearly, next7, next30);
    }

    // === Scheduled Execution ===

    /**
     * Run every 5 minutes to process due scheduled transactions.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void processDueTransactions() {
        Long now = System.currentTimeMillis() / 1000;
        List<ScheduledTransaction> due = repository.findDueForExecution(now);

        for (ScheduledTransaction st : due) {
            try {
                executeTransaction(st);
            } catch (Exception e) {
                st.setLastRunResult("failed");
                st.setActive(false); // Pause on failure
                repository.save(st);
            }
        }
    }

    private void executeTransaction(ScheduledTransaction st) {
        // Create the actual transaction
        Transaction tx = Transaction.builder()
                .transactionType(st.getTransactionType())
                .accountId(st.getAccountId())
                .categoryId(st.getCategoryId())
                .amount(st.getAmount())
                .description(st.getDescription())
                .transactionTime(st.getNextRunTime())
                .userId(st.getUserId())
                .tagIds(st.getTagIds())
                .build();

        // Handle transfer
        if (st.getTransactionType() == 4 && st.getDestinationAccountId() != null) {
            // Create TRANSFER_OUT
            tx = transactionRepository.save(tx);

            // Create TRANSFER_IN
            Transaction transferIn = Transaction.builder()
                    .transactionType(5)
                    .accountId(st.getDestinationAccountId())
                    .categoryId(null)
                    .amount(st.getAmount())
                    .description(st.getDescription())
                    .transactionTime(st.getNextRunTime())
                    .userId(st.getUserId())
                    .relatedId(tx.getId())
                    .build();
            transferIn = transactionRepository.save(transferIn);

            tx = tx.toBuilder().relatedId(transferIn.getId()).build();
            tx = transactionRepository.save(tx);
        } else {
            // Regular transaction
            tx = transactionRepository.save(tx);

            // Update account balance
            switch (st.getTransactionType()) {
                case 2 -> accountRepository.findById(st.getAccountId())
                        .ifPresent(acc -> acc.setBalance(acc.getBalance() + st.getAmount()));
                case 3 -> accountRepository.findById(st.getAccountId())
                        .ifPresent(acc -> acc.setBalance(acc.getBalance() - st.getAmount()));
            }
        }

        // Update scheduled transaction
        st.setLastRunTime(st.getNextRunTime());
        st.setLastRunResult("success");
        st.incrementRunCount();
        st.setNextRunTime(calculateNextRunTime(st.getFrequency(), st.getIntervalDays(),
                st.getDayOfWeek(), st.getDayOfMonth(), st.getMonthOfYear(),
                st.getNextRunTime()));

        // Check if expired
        if (st.getEndDate() != null && st.getNextRunTime() > st.getEndDate()) {
            st.setActive(false);
        }

        repository.save(st);
    }

    // === Helpers ===

    private Long calculateNextRunTime(String frequency, Integer intervalDays,
                                       Integer dayOfWeek, Integer dayOfMonth, Integer monthOfYear,
                                       Long startDate) {
        LocalDate now = LocalDate.now();
        LocalDate start = LocalDate.ofEpochDay(startDate / 86400);
        LocalDate next = start.isAfter(now) ? start : now;

        int interval = intervalDays != null ? intervalDays : 1;

        switch (frequency) {
            case "daily" -> next = next.plusDays(interval);
            case "weekly" -> {
                if (dayOfWeek != null) {
                    // Find next occurrence of this day of week
                    DayOfWeek targetDay = DayOfWeek.of(dayOfWeek + 1);
                    while (next.getDayOfWeek() != targetDay || next.isBefore(now)) {
                        next = next.plusDays(1);
                    }
                } else {
                    next = next.plusWeeks(interval);
                }
            }
            case "monthly" -> {
                if (dayOfMonth != null) {
                    // Set to specific day of month
                    next = next.withDayOfMonth(Math.min(dayOfMonth, next.lengthOfMonth()));
                    if (next.isBefore(now)) {
                        next = next.plusMonths(interval);
                        next = next.withDayOfMonth(Math.min(dayOfMonth, next.lengthOfMonth()));
                    }
                } else {
                    next = next.plusMonths(interval);
                }
            }
            case "yearly" -> {
                if (monthOfYear != null) {
                    next = next.withMonth(monthOfYear);
                    if (dayOfMonth != null) {
                        next = next.withDayOfMonth(Math.min(dayOfMonth, next.lengthOfMonth()));
                    }
                    if (next.isBefore(now)) {
                        next = next.plusYears(1);
                    }
                } else {
                    next = next.plusYears(interval);
                }
            }
            default -> throw new BusinessException(ResultCode.VALIDATION_ERROR, "Invalid frequency: " + frequency);
        }

        return next.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    private ScheduledTransactionDto toDto(ScheduledTransaction st) {
        // Get account names
        String accountName = accountRepository.findById(st.getAccountId())
                .map(Account::getName).orElse("Unknown");
        String destAccountName = null;
        if (st.getDestinationAccountId() != null) {
            destAccountName = accountRepository.findById(st.getDestinationAccountId())
                    .map(Account::getName).orElse("Unknown");
        }
        String categoryName = null;
        if (st.getCategoryId() != null) {
            categoryName = categoryRepository.findById(st.getCategoryId())
                    .map(Category::getName).orElse("Unknown");
        }

        return new ScheduledTransactionDto(
                st.getId(),
                st.getTransactionType(),
                st.getAccountId(),
                accountName,
                st.getCategoryId(),
                categoryName,
                st.getDestinationAccountId(),
                destAccountName,
                st.getAmount(),
                ScheduledTransactionDto.formatAmount(st.getAmount()),
                st.getDescription(),
                st.getFrequency(),
                st.getIntervalDays(),
                st.getDayOfWeek(),
                st.getDayOfMonth(),
                st.getMonthOfYear(),
                st.getStartDate(),
                st.getEndDate(),
                st.getNextRunTime(),
                ScheduledTransactionDto.formatTime(st.getNextRunTime()),
                st.getActive(),
                st.getLastRunTime(),
                st.getLastRunResult(),
                st.getRunCount(),
                st.getCreatedAt()
        );
    }
}