package com.bookkeeping.supporting.user;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.supporting.auth.AuthService;
import com.bookkeeping.supporting.auth.LoginRequest;
import com.bookkeeping.supporting.auth.LoginResponse;
import com.bookkeeping.supporting.auth.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserController.
 * Tests HTTP endpoints using real HTTP calls via RestTemplate.
 */
class UserControllerIntegrationTest extends BaseIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthService authService;

    private String authToken;

    @BeforeEach
    void setUp() {
        // baseUrl is provided by BaseIntegrationTest
    }

    private String loginAndGetToken(String username, String password) {
        String url = baseUrl() + "/api/v1/auth/login";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode json = objectMapper.readTree(response.getBody());
                return json.path("result").path("token").asText();
            } catch (Exception e) {
                fail("Failed to parse login response: " + e.getMessage());
            }
        }
        fail("Login failed for user: " + username);
        return null;
    }

    private String createTestUserAndLogin(String prefix) {
        String username = prefix + "_" + (System.currentTimeMillis() % 100000);
        String password = "password123";
        
        authService.register(new RegisterRequest(username, username + "@example.com", password));
        
        return loginAndGetToken(username, password);
    }

    // ========================================================================
    // Profile GET Tests
    // ========================================================================

    @Test
    void getProfile_withValidToken_returnsUserProfile() {
        authToken = createTestUserAndLogin("getprofile");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/api/v1/users/profile/get.json",
                HttpMethod.GET,
                request,
                String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("username"));
    }

    @Test
    void getProfile_withoutToken_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/profile/get.json",
                    HttpMethod.GET,
                    request,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void getProfile_withInvalidToken_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.token.here");
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/profile/get.json",
                    HttpMethod.GET,
                    request,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    // ========================================================================
    // Profile UPDATE Tests
    // ========================================================================

    @Test
    void updateProfile_withValidData_updatesAndReturnsProfile() {
        authToken = createTestUserAndLogin("updateprofile");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        
        Map<String, Object> body = new HashMap<>();
        body.put("nickname", "UpdatedNickname_" + (System.currentTimeMillis() % 100000));
        body.put("defaultCurrency", "EUR");
        body.put("language", "de-DE");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/api/v1/users/profile/update.json",
                HttpMethod.POST,
                request,
                String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("EUR"));
    }

    @Test
    void updateProfile_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = new HashMap<>();
        body.put("nickname", "NewNickname");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/profile/update.json",
                    HttpMethod.POST,
                    request,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    // ========================================================================
    // Avatar Tests
    // ========================================================================

    @Test
    void removeAvatar_withAuth_removesAvatar() {
        authToken = createTestUserAndLogin("removeavatar");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/api/v1/users/avatar/remove.json",
                HttpMethod.POST,
                request,
                String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void removeAvatar_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/avatar/remove.json",
                    HttpMethod.POST,
                    request,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    // ========================================================================
    // Data Statistics Tests
    // ========================================================================

    @Test
    void getDataStatistics_withAuth_returnsStatistics() {
        authToken = createTestUserAndLogin("datastats");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/api/v1/users/data/statistics.json",
                HttpMethod.GET,
                request,
                String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void getDataStatistics_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/data/statistics.json",
                    HttpMethod.GET,
                    request,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    // ========================================================================
    // Cloud Settings Tests
    // ========================================================================

    @Test
    void getCloudSettings_withAuth_returnsSettings() {
        authToken = createTestUserAndLogin("cloudsets");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/api/v1/users/settings/cloud/get.json",
                HttpMethod.GET,
                request,
                String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void getCloudSettings_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/settings/cloud/get.json",
                    HttpMethod.GET,
                    request,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }
}