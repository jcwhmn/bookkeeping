package com.bookkeeping;

import com.bookkeeping.core.transaction.*;
import com.bookkeeping.core.account.*;
import com.bookkeeping.core.category.*;
import com.bookkeeping.supporting.security.SecurityUtils;
import com.bookkeeping.supporting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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

        User mockUser = User.builder().build();
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
