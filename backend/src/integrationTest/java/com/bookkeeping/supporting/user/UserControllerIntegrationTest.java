package com.bookkeeping.supporting.user;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.common.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("User Controller Integration Tests")
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;

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
        testUser.setEmailVerified(true);
        testUser.setDisabled(false);
        testUser = userRepository.save(testUser);

        // Create a test account
        Account account = new Account();
        account.setUserId(testUser.getId());
        account.setName("Test Cash");
        account.setType(AccountType.CASH);
        account.setCurrency("USD");
        account.setBalance(100000L);
        account.setIcon("wallet");
        account.setColor("#4CAF50");
        account.setIncludeInTotal(true);
        accountRepository.save(account);
    }

    @Override
    protected void cleanTestData() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/users/me")
    class GetCurrentUserTests {

        @Test
        @DisplayName("✓ Success: Get current user info")
        void getCurrentUser_returnsUserInfo() throws Exception {
            mockMvc.perform(get("/api/v1/users/me")
                    .header("Authorization", authHeader()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.username").value("testuser"))
                    .andExpect(jsonPath("$.result.nickname").value("Test User"))
                    .andExpect(jsonPath("$.result.email").value("test@example.com"))
                    .andExpect(jsonPath("$.result.defaultCurrency").value("USD"));
        }

        @Test
        @DisplayName("✗ Failure: Get current user without auth")
        void getCurrentUser_withoutAuth_returnsUnauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/me")
    class UpdateCurrentUserTests {

        @Test
        @DisplayName("✓ Success: Update all user fields")
        void updateCurrentUser_withAllFields_returnsUpdated() throws Exception {
            String json = """
                {
                    "nickname": "Updated Nickname",
                    "defaultCurrency": "EUR",
                    "language": "zh-CN"
                }
                """;
            
            mockMvc.perform(put("/api/v1/users/me")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.nickname").value("Updated Nickname"))
                    .andExpect(jsonPath("$.result.defaultCurrency").value("EUR"))
                    .andExpect(jsonPath("$.result.language").value("zh-CN"));
        }

        @Test
        @DisplayName("✓ Success: Update partial data")
        void updateCurrentUser_withPartialData_returnsUpdated() throws Exception {
            String json = """
                {
                    "nickname": "Partial Update"
                }
                """;
            
            mockMvc.perform(put("/api/v1/users/me")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.nickname").value("Partial Update"))
                    .andExpect(jsonPath("$.result.defaultCurrency").value("USD")); // unchanged
        }

        @Test
        @DisplayName("✓ Success: Update currency only")
        void updateCurrentUser_withCurrencyOnly_returnsUpdated() throws Exception {
            String json = """
                {
                    "defaultCurrency": "GBP"
                }
                """;
            
            mockMvc.perform(put("/api/v1/users/me")
                    .header("Authorization", authHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.defaultCurrency").value("GBP"));
        }

        @Test
        @DisplayName("✗ Failure: Update without auth")
        void updateCurrentUser_withoutAuth_returnsUnauthorized() throws Exception {
            String json = """
                {
                    "nickname": "Hacked"
                }
                """;
            
            mockMvc.perform(put("/api/v1/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isUnauthorized());
        }
    }
}