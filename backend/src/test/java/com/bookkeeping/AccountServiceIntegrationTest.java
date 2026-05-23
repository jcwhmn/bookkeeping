package com.bookkeeping;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.*;
import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.supporting.security.SecurityUtils;
import com.bookkeeping.supporting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceIntegrationTest {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private AccountMapper accountMapper;
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountMapper = mock(AccountMapper.class);
        securityUtils = mock(SecurityUtils.class);

        User mockUser = User.builder().build().withId(1L);
        when(securityUtils.requireCurrentUser()).thenReturn(mockUser);

        accountService = new AccountService(accountRepository, accountMapper, securityUtils);
    }

    private AccountDto dto(Long id, String name, AccountType type, String currency, Long balance) {
        return new AccountDto(id, name, type, currency, balance, 1L, null);
    }

    @Test
    @DisplayName("TC-ACC-AUTO-001: Create account stores $500.00 as 50000 cents")
    void createAccount_storesCorrectBalance() {
        Account saved = createAccount(1L, "My Wallet", AccountType.CASH, "USD", 50000L);

        when(accountRepository.existsByNameAndUserIdAndDeletedFalse("My Wallet", 1L)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);
        when(accountMapper.toDto(saved)).thenReturn(dto(1L, "My Wallet", AccountType.CASH, "USD", 50000L));

        CreateAccountRequest req = new CreateAccountRequest(
            "My Wallet", AccountType.CASH, "USD", 50000L, null);

        AccountDto result = accountService.createAccount(req);

        assertEquals(50000L, result.balance());
        assertEquals("My Wallet", result.name());
        assertEquals(AccountType.CASH, result.accountType());
    }

    @Test
    @DisplayName("TC-ACC-AUTO-002: Create account throws ACCOUNT_ALREADY_EXISTS for duplicate name")
    void createAccount_failsForDuplicateName() {
        when(accountRepository.existsByNameAndUserIdAndDeletedFalse("Wallet", 1L)).thenReturn(true);

        CreateAccountRequest req = new CreateAccountRequest("Wallet", AccountType.CASH, "USD", 0L, null);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.createAccount(req));
        assertEquals(ResultCode.ACCOUNT_ALREADY_EXISTS.getCode(), ex.getErrorCode());
        assertTrue(ex.getErrorMessage().contains("Wallet"));
    }

    @Test
    @DisplayName("TC-ACC-AUTO-003: Get accounts returns only current user (id=1) accounts")
    void getAccounts_returnsOnlyCurrentUserAccounts() {
        Account a1 = createAccount(1L, "Wallet", AccountType.CASH, "USD", 100L);
        Account a2 = createAccount(2L, "Bank", AccountType.CHECKING, "USD", 200L);

        when(accountRepository.findByUserIdAndDeletedFalse(1L)).thenReturn(List.of(a1, a2));
        when(accountMapper.toDto(a1)).thenReturn(dto(1L, "Wallet", AccountType.CASH, "USD", 100L));
        when(accountMapper.toDto(a2)).thenReturn(dto(2L, "Bank", AccountType.CHECKING, "USD", 200L));

        List<AccountDto> accounts = accountService.getCurrentUserAccounts();

        assertEquals(2, accounts.size());
        assertEquals("Wallet", accounts.get(0).name());
        assertEquals("Bank", accounts.get(1).name());
        verify(accountRepository).findByUserIdAndDeletedFalse(1L);
        verify(accountRepository, never()).findByUserIdAndDeletedFalse(2L);
    }

    @Test
    @DisplayName("TC-ACC-AUTO-004: Delete account sets deleted=true (soft delete)")
    void deleteAccount_marksAsDeleted() {
        Account acc = createAccount(5L, "ToDelete", AccountType.CASH, "USD", 0L);

        when(accountRepository.findByIdAndUserIdAndDeletedFalse(5L, 1L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        accountService.deleteAccount(5L);

        verify(accountRepository).save(argThat(a -> a.getDeleted() == Boolean.TRUE));
    }

    @Test
    @DisplayName("TC-ACC-AUTO-005: Update balance adds 5000 cents to account")
    void updateBalance_changesBalance() {
        Account acc = createAccount(10L, "BalanceTest", AccountType.CASH, "USD", 10000L);

        when(accountRepository.findById(10L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(accountMapper.toDto(any(Account.class))).thenReturn(dto(10L, "BalanceTest", AccountType.CASH, "USD", 15000L));

        accountService.updateBalance(10L, 5000L);

        verify(accountRepository).save(argThat(a -> a.getBalance() == 15000L));
    }

    @Test
    @DisplayName("TC-ACC-AUTO-005b: Update balance can make balance negative")
    void updateBalance_canGoNegative() {
        Account acc = createAccount(1L, "Credit", AccountType.CREDIT, "USD", 0L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        accountService.updateBalance(1L, -50000L);

        verify(accountRepository).save(argThat(a -> a.getBalance() == -50000L));
    }

    private Account createAccount(Long id, String name, AccountType type, String currency, Long balance) {
        return Account.builder()
                .name(name)
                .accountType(type)
                .currency(currency)
                .balance(balance)
                .userId(1L)
                .deleted(false)
                .build()
                .withId(id);
    }
}
