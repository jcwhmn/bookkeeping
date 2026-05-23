package com.bookkeeping.core.account;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import com.bookkeeping.supporting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private SecurityUtils securityUtils;

    private AccountService accountService;
    private User testUser;
    private Account testAccount;
    private AccountDto testAccountDto;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, accountMapper, securityUtils);

        testUser = User.builder()
                
                .username("testuser")
                .build().withId(1L);

        testAccount = Account.builder()
                
                .name("Cash Wallet")
                .accountType(AccountType.CASH)
                .currency("USD")
                .balance(100000L)
                .userId(1L)
                .description("Main wallet")
                .deleted(false)
                .build().withId(10L);

        testAccountDto = new AccountDto(10L, "Cash Wallet", AccountType.CASH, "USD", 100000L, 1L, "Main wallet");
    }

    @Test
    void getCurrentUserAccounts_returnsAccountList() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByUserIdAndDeletedFalse(1L)).thenReturn(List.of(testAccount));
        when(accountMapper.toDto(testAccount)).thenReturn(testAccountDto);

        List<AccountDto> result = accountService.getCurrentUserAccounts();

        assertEquals(1, result.size());
        assertEquals("Cash Wallet", result.get(0).name());
        verify(accountRepository).findByUserIdAndDeletedFalse(1L);
    }

    @Test
    void getCurrentUserAccounts_emptyList_returnsEmpty() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByUserIdAndDeletedFalse(1L)).thenReturn(List.of());

        List<AccountDto> result = accountService.getCurrentUserAccounts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAccount_existingAccount_returnsDto() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.of(testAccount));
        when(accountMapper.toDto(testAccount)).thenReturn(testAccountDto);

        AccountDto result = accountService.getAccount(10L);

        assertEquals("Cash Wallet", result.name());
        assertEquals(100000L, result.balance());
    }

    @Test
    void getAccount_nonExisting_throwsException() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.getAccount(10L));
        assertEquals(ResultCode.ACCOUNT_NOT_FOUND.getCode(), ex.getErrorCode());
    }

    @Test
    void createAccount_withValidRequest_createsAccount() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.existsByNameAndUserIdAndDeletedFalse("Cash Wallet", 1L)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountMapper.toDto(any(Account.class))).thenReturn(testAccountDto);

        CreateAccountRequest request = new CreateAccountRequest("Cash Wallet", AccountType.CASH, "USD", 100000L, "Main wallet");
        AccountDto result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals("Cash Wallet", result.name());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_withDuplicateName_throwsException() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.existsByNameAndUserIdAndDeletedFalse("Cash Wallet", 1L)).thenReturn(true);

        CreateAccountRequest request = new CreateAccountRequest("Cash Wallet", AccountType.CASH, "USD", 0L, null);
        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.createAccount(request));

        assertEquals(ResultCode.ACCOUNT_ALREADY_EXISTS.getCode(), ex.getErrorCode());
    }

    @Test
    void createAccount_withNullBalance_defaultsToZero() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.existsByNameAndUserIdAndDeletedFalse(any(), any())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountMapper.toDto(any())).thenReturn(testAccountDto);

        CreateAccountRequest request = new CreateAccountRequest("Cash Wallet", AccountType.CASH, "USD", null, null);
        accountService.createAccount(request);

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_nameOnly_updatesName() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountMapper.toDto(any(Account.class))).thenReturn(
                new AccountDto(10L, "Updated Wallet", AccountType.CASH, "USD", 100000L, 1L, "Main wallet"));

        UpdateAccountRequest request = new UpdateAccountRequest("Updated Wallet", null);
        AccountDto result = accountService.updateAccount(10L, request);

        assertEquals("Updated Wallet", result.name());
        assertEquals("Main wallet", result.description());
    }

    @Test
    void updateAccount_nonExisting_throwsException() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.empty());

        UpdateAccountRequest request = new UpdateAccountRequest("New Name", null);
        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.updateAccount(10L, request));

        assertEquals(ResultCode.ACCOUNT_NOT_FOUND.getCode(), ex.getErrorCode());
    }

    @Test
    void updateAccount_duplicateName_throwsException() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.existsByNameAndUserIdAndDeletedFalse("Existing", 1L)).thenReturn(true);

        UpdateAccountRequest request = new UpdateAccountRequest("Existing", null);
        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.updateAccount(10L, request));

        assertEquals(ResultCode.ACCOUNT_ALREADY_EXISTS.getCode(), ex.getErrorCode());
    }

    @Test
    void deleteAccount_softDeletes() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.deleteAccount(10L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
    }

    @Test
    void deleteAccount_nonExisting_throwsException() {
        when(securityUtils.requireCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.deleteAccount(10L));

        assertEquals(ResultCode.ACCOUNT_NOT_FOUND.getCode(), ex.getErrorCode());
    }

    @Test
    void updateBalance_positiveChange_increases() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.updateBalance(10L, 50000L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(150000L, captor.getValue().getBalance());
    }

    @Test
    void updateBalance_negativeChange_decreases() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.updateBalance(10L, -30000L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(70000L, captor.getValue().getBalance());
    }
}
