package com.bookkeeping.supporting.auth;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import com.bookkeeping.common.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Auth Controller Integration Tests")
public class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void insertTestData() {
        // Create test users
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setEmail("test@example.com");
        user1.setNickname("Test User");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setSalt("testsalt1");
        user1.setDefaultCurrency("USD");
        user1.setLanguage("en");
        user1.setEmailVerified(true);
        user1.setDisabled(false);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("anotheruser");
        user2.setEmail("another@example.com");
        user2.setNickname("Another User");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setSalt("anothersal");
        user2.setDefaultCurrency("EUR");
        user2.setLanguage("de");
        user2.setEmailVerified(true);
        user2.setDisabled(false);
        userRepository.save(user2);

        User disabledUser = new User();
        disabledUser.setUsername("disableduser");
        disabledUser.setEmail("disabled@example.com");
        disabledUser.setNickname("Disabled User");
        disabledUser.setPassword(passwordEncoder.encode("password123"));
        disabledUser.setSalt("disableds1");
        disabledUser.setDefaultCurrency("USD");
        disabledUser.setLanguage("en");
        disabledUser.setEmailVerified(true);
        disabledUser.setDisabled(true);
        userRepository.save(disabledUser);
    }

    @Override
    protected void cleanTestData() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class LoginTests {

        @Test
        @DisplayName("✓ Success: Login with valid credentials")
        void login_withValidCredentials_returnsToken() throws Exception {
            String json = "{\"username\":\"testuser\",\"password\":\"password123\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.token").exists())
                    .andExpect(jsonPath("$.result.refreshToken").exists())
                    .andExpect(jsonPath("$.result.expiresAtStr").exists())
                    .andExpect(jsonPath("$.result.user.username").value("testuser"))
                    .andExpect(jsonPath("$.result.user.nickname").value("Test User"))
                    .andExpect(jsonPath("$.result.user.defaultCurrency").value("USD"));
        }

        @Test
        @DisplayName("✓ Success: Login with another user")
        void login_withAnotherUser_returnsToken() throws Exception {
            String json = "{\"username\":\"anotheruser\",\"password\":\"password123\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.token").exists())
                    .andExpect(jsonPath("$.result.user.username").value("anotheruser"))
                    .andExpect(jsonPath("$.result.user.defaultCurrency").value("EUR"));
        }

        @Test
        @DisplayName("✗ Failure: Invalid password")
        void login_withInvalidPassword_returnsError() throws Exception {
            String json = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(201001))
                    .andExpect(jsonPath("$.errorMessage").value("Invalid username or password"));
        }

        @Test
        @DisplayName("✗ Failure: Invalid username")
        void login_withInvalidUsername_returnsError() throws Exception {
            String json = "{\"username\":\"nonexistent\",\"password\":\"password123\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(201001))
                    .andExpect(jsonPath("$.errorMessage").value("Invalid username or password"));
        }

        @Test
        @DisplayName("✗ Failure: Empty username")
        void login_withEmptyUsername_returnsBadRequest() throws Exception {
            String json = "{\"username\":\"\",\"password\":\"password123\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("✗ Failure: Empty password")
        void login_withEmptyPassword_returnsBadRequest() throws Exception {
            String json = "{\"username\":\"testuser\",\"password\":\"\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("✗ Failure: Missing username")
        void login_withMissingUsername_returnsBadRequest() throws Exception {
            String json = "{\"password\":\"password123\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("✗ Failure: Missing password")
        void login_withMissingPassword_returnsBadRequest() throws Exception {
            String json = "{\"username\":\"testuser\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("✗ Failure: Disabled user")
        void login_withDisabledUser_returnsError() throws Exception {
            String json = "{\"username\":\"disableduser\",\"password\":\"password123\"}";
            
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errorCode").value(201001));
        }
    }
}