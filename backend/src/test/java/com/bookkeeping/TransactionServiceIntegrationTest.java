package com.bookkeeping;

import com.bookkeeping.core.transaction.*;
import com.bookkeeping.core.account.*;
import com.bookkeeping.core.category.*;
import com.bookkeeping.supporting.security.SecurityUtils;
import com.bookkeeping.supporting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceIntegrationTest {

    private TransactionService transactionService;
    private TransactionRepository transactionRepository;
    private AccountService accountService;
    private SecurityUtils securityUtils;
    private CategoryService categoryService;
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountService = mock(AccountService.class);
        securityUtils = mock(SecurityUtils.class);
        categoryService = mock(CategoryService.class);
        transactionMapper = new TransactionDtoMapperConverter();

        User mockUser = User.builder().build().withId(1L);
        when(securityUtils.requireCurrentUser()).thenReturn(mockUser);

        transactionService = new TransactionService(
            transactionRepository, accountService, securityUtils, categoryService, transactionMapper);
    }

    @Test
    @DisplayName("TC-TXN-AUTO-001: Income transaction (+5000 cents) adds to account balance")
    void createTransaction_incomeAddsToBalance() {
        Long accountId = 1L;
        Long amount = 5000L;

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv ->
            inv.<Transaction>getArgument(0).toBuilder().build().withId(10L));

        CreateTransactionRequest req = new CreateTransactionRequest(
            2, accountId, 1L, null, amount, "Bonus", null, null);

        TransactionDto result = transactionService.createTransaction(req);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(2, result.transactionType());
        verify(accountService).updateBalance(eq(accountId), eq(amount));
    }

    @Test
    @DisplayName("TC-TXN-AUTO-002: Expense transaction (-3000 cents) subtracts from account balance")
    void createTransaction_expenseSubtractsFromBalance() {
        Long accountId = 1L;
        Long amount = 3000L;

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv ->
            inv.<Transaction>getArgument(0).toBuilder().build().withId(20L));

        CreateTransactionRequest req = new CreateTransactionRequest(
            3, accountId, 2L, null, amount, "Coffee", null, null);

        TransactionDto result = transactionService.createTransaction(req);

        assertNotNull(result);
        assertEquals(3, result.transactionType());
        verify(accountService).updateBalance(eq(accountId), eq(-3000L));
    }

    @Test
    @DisplayName("TC-TXN-AUTO-003: Invalid transaction type throws exception")
    void createTransaction_missingFields_throwsException() {
        CreateTransactionRequest req = new CreateTransactionRequest(
            99, 1L, 1L, null, 5000L, "Bad type", null, null);

        assertThrows(Exception.class, () -> transactionService.createTransaction(req));
    }

    @Test
    @DisplayName("TC-TXN-AUTO-004: Transfer creates linked pair (type 4 + type 5)")
    void createTransaction_transferCreatesLinkedPair() {
        Long sourceAccountId = 1L;
        Long destAccountId = 2L;
        Long amount = 10000L;

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            return t.toBuilder().build();
        });

        CreateTransactionRequest req = new CreateTransactionRequest(
            4, sourceAccountId, null, destAccountId, amount, "Transfer to savings", null, null);

        TransactionDto result = transactionService.createTransaction(req);

        assertNotNull(result);
        assertEquals(4, result.transactionType());

        // Verify: source account decreased
        verify(accountService).updateBalance(eq(sourceAccountId), eq(-amount));
        // Verify: destination account increased
        verify(accountService).updateBalance(eq(destAccountId), eq(amount));
    }

    @Test
    @DisplayName("TC-TXN-AUTO-004b: Transfer OUT (type=4) subtracts from source account")
    void createTransaction_transferOutSubtracts() {
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv ->
            inv.<Transaction>getArgument(0).toBuilder().build().withId(10L));

        CreateTransactionRequest req = new CreateTransactionRequest(
            4, 1L, null, null, 10000L, "Transfer to savings", null, null);

        TransactionDto result = transactionService.createTransaction(req);

        assertEquals(4, result.transactionType());
        verify(accountService).updateBalance(eq(1L), eq(-10000L));
    }

    @Test
    @DisplayName("TC-TXN-SEARCH-001: searchTransactions uses DB-level filtering when filters present")
    void searchTransactions_withFilters_usesFindWithFilters() {
        TransactionSearchParams params = new TransactionSearchParams(2026, 5, 1, 2, 3, "coffee");
        List<Transaction> mockResults = List.of(
            createTx(1L, 3, 1L, 500L, "coffee beans", 1750000000L));

        when(transactionRepository.findWithFilters(
                eq(1L), eq(2026), eq(5), eq(1L), eq(2L), eq(3), eq("coffee"), any(Pageable.class)))
                .thenReturn(mockResults);

        List<TransactionDto> result = transactionService.searchTransactions(params, 50);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    @DisplayName("TC-TXN-SEARCH-002: searchTransactions falls back to default repo method when no filters")
    void searchTransactions_noFilters_usesDefaultMethod() {
        TransactionSearchParams params = TransactionSearchParams.NONE;
        List<Transaction> mockResults = List.of(
            createTx(1L, 2, 1L, 10000L, "Salary", 1750000000L),
            createTx(2L, 3, 1L, 2000L, "Lunch", 1750001000L));

        when(transactionRepository.findByUserIdOrderByTransactionTimeDesc(eq(1L), any(Pageable.class)))
                .thenReturn(mockResults);

        List<TransactionDto> result = transactionService.searchTransactions(params, 100);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("TC-TXN-CURSOR-001: listByCursor returns page with nextCursor when more results exist")
    void listByCursor_hasMorePages_returnsNextCursor() {
        // Build 51 transactions so limit=50 returns 50 with a nextCursor
        List<Transaction> txs = java.util.stream.IntStream.rangeClosed(1, 51)
                .mapToObj(i -> createTx((long) i, 2, 1L, 1000L, "tx " + i, (long) (1750000000L - i * 60)))
                .toList();

        when(transactionRepository.findByUserIdBeforeCursor(eq(1L), eq(999999L), any(Pageable.class)))
                .thenReturn(txs);

        TransactionPageResponse response = transactionService.listByCursor(999999L, 50);

        assertEquals(50, response.transactions().size());
        assertNotNull(response.nextCursor());
    }

    @Test
    @DisplayName("TC-TXN-CURSOR-002: listByCursor returns null nextCursor when on last page")
    void listByCursor_lastPage_noNextCursor() {
        List<Transaction> txs = java.util.stream.IntStream.rangeClosed(1, 30)
                .mapToObj(i -> createTx((long) i, 2, 1L, 1000L, "tx " + i, (long) (1750000000L - i * 60)))
                .toList();

        when(transactionRepository.findByUserIdBeforeCursor(eq(1L), eq(999999L), any(Pageable.class)))
                .thenReturn(txs);

        TransactionPageResponse response = transactionService.listByCursor(999999L, 50);

        assertEquals(30, response.transactions().size());
        assertNull(response.nextCursor());
    }

    @Test
    @DisplayName("TC-TXN-COUNT-001: countTransactions with filters uses countWithFilters")
    void countTransactions_withFilters_usesCountWithFilters() {
        TransactionSearchParams params = new TransactionSearchParams(2026, 5, 1, null, null, null);

        when(transactionRepository.countWithFilters(
                eq(1L), eq(2026), eq(5), eq(1L), isNull(), isNull(), isNull()))
                .thenReturn(42L);

        long count = transactionService.countTransactions(params);

        assertEquals(42L, count);
    }

    @Test
    @DisplayName("TC-TXN-COUNT-002: countTransactions without filters uses countByUserId")
    void countTransactions_noFilters_usesCountByUserId() {
        TransactionSearchParams params = TransactionSearchParams.NONE;

        when(transactionRepository.countByUserId(1L)).thenReturn(123L);

        long count = transactionService.countTransactions(params);

        assertEquals(123L, count);
    }

    @Test
    @DisplayName("TC-TXN-LISTALL-001: listAll returns all transactions for user")
    void listAll_returnsAllTransactions() {
        List<Transaction> mockResults = List.of(
            createTx(1L, 2, 1L, 10000L, "Salary", 1750000000L),
            createTx(2L, 3, 1L, 2000L, "Lunch", 1750001000L),
            createTx(3L, 4, 1L, 5000L, "Transfer", 1750002000L));

        when(transactionRepository.findByUserIdOrderByTransactionTimeDesc(1L))
                .thenReturn(mockResults);

        List<TransactionDto> result = transactionService.listAll();

        assertEquals(3, result.size());
    }

    private Transaction createTx(Long id, int type, Long accountId, Long amount, String desc, Long time) {
        return Transaction.builder()
                .transactionType(type)
                .accountId(accountId)
                .amount(amount)
                .description(desc)
                .transactionTime(time)
                .userId(1L)
                .build()
                .withId(id);
    }
}
