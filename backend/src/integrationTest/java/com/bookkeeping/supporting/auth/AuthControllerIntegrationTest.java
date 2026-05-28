package com.bookkeeping.supporting.auth;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AuthController.
 */
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String baseUrl() {
        return "http://localhost:8080";
    }

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
        String password = "password123";
        createTestUser("testuser", password);

        LoginRequest request = new LoginRequest("testuser", password);
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl() + "/api/v1/auth/login", request, String.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("\"token\":"));
            assertTrue(response.getBody().contains("\"success\":true"));
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            fail("Expected 200 OK but got: " + e.getStatusCode());
        }
    }

    @Test
    void login_withInvalidUsername_returnsUnauthorized() {
        LoginRequest request = new LoginRequest("nonexistent", "password123");
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.postForEntity(baseUrl() + "/api/v1/auth/login", request, String.class);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void login_withInvalidPassword_returnsUnauthorized() {
        String password = "correctPassword";
        createTestUser("testuser", password);

        LoginRequest request = new LoginRequest("testuser", "wrongPassword");
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.postForEntity(baseUrl() + "/api/v1/auth/login", request, String.class);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void login_withDisabledUser_returnsUnauthorized() {
        String password = "password123";
        User user = createTestUser("testuser", password);
        user.setDisabled(true);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("testuser", password);
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.postForEntity(baseUrl() + "/api/v1/auth/login", request, String.class);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void register_withNewUser_createsUser() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123");
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl() + "/api/v1/auth/register", request, String.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("\"success\":true"));
            assertTrue(response.getBody().contains("newuser"));
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            fail("Expected 200 OK but got: " + e.getStatusCode());
        }
    }

    @Test
    void register_withExistingUsername_returnsBadRequest() {
        createTestUser("existinguser", "password123");

        RegisterRequest request = new RegisterRequest("existinguser", "new@example.com", "password123");
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.postForEntity(baseUrl() + "/api/v1/auth/register", request, String.class);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    void getCurrentUser_withValidToken_returnsUser() {
        String password = "password123";
        createTestUser("testuser", password);

        RestTemplate restTemplate = new RestTemplate();

        // Login to get token
        LoginRequest loginRequest = new LoginRequest("testuser", password);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl() + "/api/v1/auth/login", loginRequest, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String token = extractToken(loginResponse.getBody());

        // Get current user with auth
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl() + "/api/v1/auth/me",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().contains("testuser"));
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            fail("Expected 200 OK but got: " + e.getStatusCode());
        }
    }

    @Test
    void getCurrentUser_withoutToken_returnsUnauthorized() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/auth/me",
                    HttpMethod.GET,
                    request,
                    String.class
            );
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    private String extractToken(String responseBody) {
        // Simple extraction - in real test use JSON parser
        int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
        int tokenEnd = responseBody.indexOf("\"", tokenStart);
        return responseBody.substring(tokenStart, tokenEnd);
    }
}