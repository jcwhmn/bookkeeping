package com.bookkeeping.supporting.auth;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import com.bookkeeping.supporting.user.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AuthService.
 * Tests auth logic using direct service injection.
 */
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private String hashPassword(String password, String salt) {
        try {
            String saltedPassword = salt + password;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User createTestUser(String username, String password) {
        String salt = "salt123";
        User user = User.builder()
                .username(username)
                .email(username + "@example.com")
                .nickname("Test User")
                .salt(salt)
                .password(hashPassword(password, salt))
                .defaultCurrency("USD")
                .language("en-US")
                .emailVerified(true)
                .disabled(false)
                .build();
        return userRepository.save(user);
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        String username = "loginuser_" + System.currentTimeMillis();
        String password = "password123";
        createTestUser(username, password);

        LoginRequest request = new LoginRequest(username, password);
        var response = authService.login(request);

        assertNotNull(response.token());
        assertEquals(username, response.user().username());
    }

    @Test
    void login_withInvalidUsername_returnsError() {
        LoginRequest request = new LoginRequest("nonexistent_" + System.currentTimeMillis(), "password123");
        
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
        assertNotNull(ex.getMessage());
    }

    @Test
    void login_withInvalidPassword_returnsError() {
        String username = "wrongpw_" + System.currentTimeMillis();
        String password = "correctPassword";
        createTestUser(username, password);

        LoginRequest request = new LoginRequest(username, "wrongPassword");
        
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
        assertNotNull(ex.getMessage());
    }

    @Test
    void login_withDisabledUser_returnsError() {
        String username = "disabled_" + System.currentTimeMillis();
        String password = "password123";
        User user = createTestUser(username, password);
        user.setDisabled(true);
        userRepository.save(user);

        LoginRequest request = new LoginRequest(username, password);
        
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().contains("disabled"));
    }

    @Test
    void register_withNewUser_createsUserAndReturnsDto() {
        String username = "newuser_" + System.currentTimeMillis();
        RegisterRequest request = new RegisterRequest(username, username + "@example.com", "password123");
        UserDto result = authService.register(request);

        assertNotNull(result);
        assertEquals(username, result.username());
        
        // Verify user exists
        var user = userRepository.findByUsername(username);
        assertTrue(user.isPresent());
    }

    @Test
    void register_withExistingUsername_returnsError() {
        String username = "existing_" + System.currentTimeMillis();
        createTestUser(username, "password123");

        RegisterRequest request = new RegisterRequest(username, username + "@example.com", "password123");
        
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("exists") || ex.getMessage().contains("already"));
    }
}