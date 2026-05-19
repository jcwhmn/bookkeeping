package com.bookkeeping.core.account;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import com.bookkeeping.common.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Account Controller Integration Tests")
public class AccountControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;
    private Account testAccount1;
    private Account testAccount2;
    private User anotherUser;

    @Override
    protected void insertTestData() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setSalt("testsalt1");
        testUser.setDefaultCurrency("USD");
        testUser.setLanguage("en");
        testUser = userRepository.save(testUser);

        // Create another user
        anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setNickname("Another User");
        anotherUser.setPassword(passwordEncoder.encode("password123"));
        anotherUser.setSalt("anothersal");
        anotherUser.setDefaultCurrency("EUR");
        anotherUser.setLanguage("de");
        anotherUser = userRepository.save(anotherUser);

        // Create accounts for test user
        testAccount1 = new Account();
        testAccount1.setUserId(testUser.getId());
        testAccount1.setName("Cash");
        testAccount1.setType(AccountType.CASH);
        testAccount1.setCurrency("USD");
        testAccount1.setBalance(100000L);
        testAccount1.setIcon("wallet");
        testAccount1.setColor("#4CAF50");
        testAccount1.setIncludeInTotal(true);
        accountRepository.save(testAccount1);

        testAccount2 = new Account();
        testAccount2.setUserId(testUser.getId());
        testAccount2.setName("Checking Account");
        testAccount2.setType(AccountType.CHECKING);
        testAccount2.setCurrency("USD");
        testAccount2.setBalance(500000L);
        testAccount2.setIcon("account_balance");
        testAccount2.setColor("#2196F3");
        testAccount2.setIncludeInTotal(true);
        accountRepository.save(testAccount2);

        // Create account for another user
        Account anotherAccount = new Account();
        anotherAccount.setUserId(anotherUser.getId());
        anotherAccount.setName("Another Cash");
        anotherAccount.setType(AccountType.CASH);
        anotherAccount.setCurrency("EUR");
        anotherAccount.setBalance(200000L);
        anotherAccount.setIcon("wallet");
        anotherAccount.setColor("#4CAF50");
        anotherAccount.setIncludeInTotal(true);
        accountRepository.save(anotherAccount);
    }

    @Override
    protected void cleanTestData() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/accounts")
    class GetAccountsTests {

        @Test
        @DisplayName("✓ Success: Get all accounts for user")
        void getAccounts_returnsUserAccounts() throws Exception {
            mockMvc.perform(get("/api/v1/accounts")
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.length()").value(2))
                    .andExpect(jsonPath("$.result[0].name").exists());
        }

        @Test
        @DisplayName("✗ Failure: Get accounts without auth")
        void getAccounts_withoutAuth_returnsUnauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/accounts"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/accounts/{id}")
    class GetAccountByIdTests {

        @Test
        @DisplayName("✓ Success: Get account by valid ID")
        void getAccount_withValidId_returnsAccount() throws Exception {
            mockMvc.perform(get("/api/v1/accounts/" + testAccount1.getId())
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.name").value("Cash"));
        }

        @Test
        @DisplayName("✗ Failure: Get account by invalid ID")
        void getAccount_withInvalidId_returnsNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/accounts/99999")
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(204001));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/accounts")
    class CreateAccountTests {

        @Test
        @DisplayName("✓ Success: Create new account")
        void createAccount_withValidData_returnsCreated() throws Exception {
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String accountName = "Savings Account " + uniqueId;
            String json = String.format("""
                {
                    "name": "%s",
                    "type": "SAVINGS",
                    "currency": "USD",
                    "balanceStr": "50000",
                    "icon": "savings",
                    "color": "#FF9800",
                    "notes": "Test notes"
                }
                """, accountName);
            
            mockMvc.perform(post("/api/v1/accounts")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.name").value(accountName))
                    .andExpect(jsonPath("$.result.type").value("SAVINGS"));
        }

        @Test
        @DisplayName("✗ Failure: Create account with duplicate name")
        void createAccount_withDuplicateName_returnsError() throws Exception {
            String json = """
                {
                    "name": "Cash",
                    "type": "CASH",
                    "currency": "USD"
                }
                """;
            
            mockMvc.perform(post("/api/v1/accounts")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(204002));
        }

        @Test
        @DisplayName("✗ Failure: Create account without auth")
        void createAccount_withoutAuth_returnsUnauthorized() throws Exception {
            String json = """
                {
                    "name": "Unauthorized Account",
                    "type": "CASH",
                    "currency": "USD"
                }
                """;
            
            mockMvc.perform(post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/accounts/{id}")
    class UpdateAccountTests {

        @Test
        @DisplayName("✓ Success: Update existing account")
        void updateAccount_withValidData_returnsUpdated() throws Exception {
            String json = """
                {
                    "name": "Updated Cash",
                    "type": "CASH",
                    "currency": "USD",
                    "balanceStr": "200000",
                    "icon": "wallet",
                    "color": "#00BCD4",
                    "notes": "Updated notes"
                }
                """;
            
            mockMvc.perform(put("/api/v1/accounts/" + testAccount1.getId())
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.name").value("Updated Cash"))
                    .andExpect(jsonPath("$.result.balanceStr").value("200000"))
                    .andExpect(jsonPath("$.result.color").value("#00BCD4"));
        }

        @Test
        @DisplayName("✗ Failure: Update account with duplicate name")
        void updateAccount_withDuplicateName_returnsError() throws Exception {
            String json = """
                {
                    "name": "Checking Account",
                    "type": "CASH",
                    "currency": "USD"
                }
                """;
            
            mockMvc.perform(put("/api/v1/accounts/" + testAccount1.getId())
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(204002));
        }

        @Test
        @DisplayName("✗ Failure: Update non-existent account")
        void updateAccount_withInvalidId_returnsNotFound() throws Exception {
            String json = """
                {
                    "name": "Non Existent",
                    "type": "CASH",
                    "currency": "USD"
                }
                """;
            
            mockMvc.perform(put("/api/v1/accounts/99999")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(204001));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/accounts/{id}")
    class DeleteAccountTests {

        @Test
        @DisplayName("✓ Success: Delete account")
        void deleteAccount_withValidId_returnsSuccess() throws Exception {
            mockMvc.perform(delete("/api/v1/accounts/" + testAccount2.getId())
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
            
            // Verify account is deleted
            mockMvc.perform(get("/api/v1/accounts/" + testAccount2.getId())
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("✗ Failure: Delete non-existent account")
        void deleteAccount_withInvalidId_returnsNotFound() throws Exception {
            mockMvc.perform(delete("/api/v1/accounts/99999")
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(204001));
        }

        @Test
        @DisplayName("✗ Failure: Delete without auth")
        void deleteAccount_withoutAuth_returnsUnauthorized() throws Exception {
            mockMvc.perform(delete("/api/v1/accounts/" + testAccount1.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }
}