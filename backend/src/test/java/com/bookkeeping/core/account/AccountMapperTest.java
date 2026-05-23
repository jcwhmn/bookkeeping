package com.bookkeeping.core.account;

import com.bookkeeping.common.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AccountMapper (MapStructPlus generated converter).
 * Plain unit test - no Spring context needed.
 */
class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountDtoMapperConverter();
    }

    @Test
    void toDto_withAllFields_mapsCorrectly() {
        Account account = createTestAccount();

        AccountDto dto = accountMapper.toDto(account);

        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals("Cash Wallet", dto.name());
        assertEquals(AccountType.CASH, dto.accountType());
        assertEquals("USD", dto.currency());
        assertEquals(100000L, dto.balance());
        assertEquals(1L, dto.userId());
        assertEquals("My main cash wallet", dto.description());
    }

    @Test
    void toDto_withNullAccount_returnsNull() {
        assertNull(accountMapper.toDto(null));
    }

    @Test
    void toDto_withDifferentAccountTypes_mapsAll() {
        AccountType[] types = {AccountType.CASH, AccountType.CHECKING, AccountType.SAVINGS, 
                               AccountType.CREDIT, AccountType.INVESTMENT};
        
        for (AccountType type : types) {
            Account account = createTestAccount().toBuilder().accountType(type).build().withId(10L);
            AccountDto dto = accountMapper.toDto(account);
            assertEquals(type, dto.accountType());
        }
    }

    @Test
    void toDto_withZeroBalance_mapsCorrectly() {
        Account account = createTestAccount().toBuilder().balance(0L).build().withId(10L);

        AccountDto dto = accountMapper.toDto(account);

        assertEquals(0L, dto.balance());
    }

    @Test
    void toDto_withNegativeBalance_mapsCorrectly() {
        Account account = createTestAccount().toBuilder().balance(-50000L).build().withId(10L);

        AccountDto dto = accountMapper.toDto(account);

        assertEquals(-50000L, dto.balance());
    }

    @Test
    void toDto_withNullFields_handlesGracefully() {
        Account account = createTestAccount().toBuilder().description(null).userId(null).build().withId(10L);

        AccountDto dto = accountMapper.toDto(account);

        assertNotNull(dto);
        assertNull(dto.description());
        assertNull(dto.userId());
    }

    @Test
    void toDto_withEmptyStrings_mapsCorrectly() {
        Account account = createTestAccount().toBuilder().name("").description("").build().withId(10L);

        AccountDto dto = accountMapper.toDto(account);

        assertEquals("", dto.name());
        assertEquals("", dto.description());
    }

    @Test
    void toDto_preservesCurrencyCode() {
        String[] currencies = {"USD", "EUR", "CNY", "JPY", "GBP"};

        for (String currency : currencies) {
            Account account = createTestAccount().toBuilder().currency(currency).build().withId(10L);
            AccountDto dto = accountMapper.toDto(account);
            assertEquals(currency, dto.currency());
        }
    }

    private Account createTestAccount() {
        return Account.builder()
                
                .name("Cash Wallet")
                .accountType(AccountType.CASH)
                .currency("USD")
                .balance(100000L)
                .userId(1L)
                .description("My main cash wallet")
                .deleted(false)
                .build().withId(10L);
    }
}
