package com.bookkeeping.core.account;

import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUserId(1L);
        testAccount.setName("Cash");
        testAccount.setType(AccountType.CASH);
        testAccount.setCurrency("USD");
        testAccount.setBalance(100000L);
        testAccount.setIcon("wallet");
        testAccount.setColor("#4CAF50");
        testAccount.setIncludeInTotal(true);
        testAccount.setArchived(false);
    }

    @Nested
    @DisplayName("getAccountsByUser()")
    class GetAccountsTests {

        @Test
        @DisplayName("✓ Success: get all accounts for user")
        void getAccountsByUser_returnsAccountDtos() {
            Account account2 = new Account();
            account2.setId(2L);
            account2.setUserId(1L);
            account2.setName("Bank");
            account2.setType(AccountType.CHECKING);
            account2.setCurrency("USD");
            account2.setBalance(500000L);

            when(accountRepository.findAllByUser(1L))
                    .thenReturn(Arrays.asList(testAccount, account2));

            List<AccountDto> result = accountService.getAccountsByUser(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Cash");
            assertThat(result.get(1).name()).isEqualTo("Bank");
        }
    }

    @Nested
    @DisplayName("getAccountById()")
    class GetAccountTests {

        @Test
        @DisplayName("✓ Success: get account by id")
        void getAccountById_withValidId_returnsAccountDto() {
            when(accountRepository.findByUserAndId(1L, 1L)).thenReturn(Optional.of(testAccount));

            AccountDto result = accountService.getAccountById(1L, 1L);

            assertThat(result.idStr()).isEqualTo("1");
            assertThat(result.name()).isEqualTo("Cash");
        }

        @Test
        @DisplayName("✗ Failure: account not found")
        void getAccountById_notFound_throwsException() {
            when(accountRepository.findByUserAndId(1L, 999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccountById(1L, 999L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("createAccount()")
    class CreateAccountTests {

        @Test
        @DisplayName("✓ Success: create new account")
        void createAccount_withValidData_returnsAccountDto() {
            AccountService.CreateAccountRequest request = new AccountService.CreateAccountRequest(
                    "Savings",
                    "SAVINGS",
                    "USD",
                    "200000",
                    "savings",
                    "#FF9800",
                    "Test notes",
                    "true"
            );

            when(accountRepository.findByUserAndName(1L, "Savings")).thenReturn(Optional.empty());
            when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
                Account saved = inv.getArgument(0);
                saved.setId(2L);
                return saved;
            });

            AccountDto result = accountService.createAccount(1L, request);

            assertThat(result.name()).isEqualTo("Savings");
            assertThat(result.type()).isEqualTo("SAVINGS");
        }

        @Test
        @DisplayName("✗ Failure: duplicate account name")
        void createAccount_duplicateName_throwsException() {
            AccountService.CreateAccountRequest request = new AccountService.CreateAccountRequest(
                    "Cash",
                    "CASH",
                    "USD",
                    "100000",
                    null,
                    null,
                    null,
                    null
            );

            when(accountRepository.findByUserAndName(1L, "Cash")).thenReturn(Optional.of(testAccount));

            assertThatThrownBy(() -> accountService.createAccount(1L, request))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("deleteAccount()")
    class DeleteAccountTests {

        @Test
        @DisplayName("✓ Success: delete account")
        void deleteAccount_deletesSuccessfully() {
            when(accountRepository.findByUserAndId(1L, 1L)).thenReturn(Optional.of(testAccount));

            accountService.deleteAccount(1L, 1L);

            verify(accountRepository).delete(testAccount);
        }

        @Test
        @DisplayName("✗ Failure: delete non-existent account")
        void deleteAccount_notFound_throwsException() {
            when(accountRepository.findByUserAndId(1L, 999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.deleteAccount(1L, 999L))
                    .isInstanceOf(BusinessException.class);
        }
    }
}