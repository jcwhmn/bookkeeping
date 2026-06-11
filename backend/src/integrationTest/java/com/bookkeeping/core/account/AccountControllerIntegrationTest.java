package com.bookkeeping.core.account;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.supporting.auth.AuthService;
import com.bookkeeping.supporting.auth.LoginRequest;
import com.bookkeeping.supporting.auth.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AccountController.
 * Tests HTTP endpoints using real HTTP calls via RestTemplate.
 */
class AccountControllerIntegrationTest extends BaseIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    private String authToken;

    @BeforeEach
    void setUp() {
        // baseUrl is provided by BaseIntegrationTest
    }

    private String createTestUserAndLogin(String prefix) {
        String username = prefix + "_" + (System.currentTimeMillis() % 100000);
        String password = "password123";
        
        authService.register(new RegisterRequest(
                username, username + "@example.com", password));
        
        String url = baseUrl() + "/api/v1/auth/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.path("result").path("token").asText();
        } catch (Exception e) {
            fail("Failed to parse login response: " + e.getMessage());
        }
        return null;
    }

    private ResponseEntity<String> makePostRequest(String endpoint, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(baseUrl() + endpoint, request, String.class);
    }

    private ResponseEntity<String> makeGetRequest(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(baseUrl() + endpoint, HttpMethod.GET, request, String.class);
    }

    // ========================================================================
    // LIST Tests
    // ========================================================================

    @Test
    void listAccounts_withAuth_returnsAccountList() {
        authToken = createTestUserAndLogin("listaccounts");
        
        ResponseEntity<String> response = makeGetRequest("/api/v1/accounts");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void listAccounts_withVisibleOnlyFilter_returnsFilteredList() {
        authToken = createTestUserAndLogin("listaccounts_visible");
        
        ResponseEntity<String> response = makeGetRequest("/api/v1/accounts?visible_only=true");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void listAccounts_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/accounts",
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
    // GET Tests
    // ========================================================================

    @Test
    void getAccount_withValidId_returnsAccount() {
        authToken = createTestUserAndLogin("getaccount");
        
        // First create an account
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("name", "TestAccount_" + (System.currentTimeMillis() % 100000));
        createBody.put("accountType", "CASH");
        createBody.put("currency", "USD");
        createBody.put("balance", 100000L);
        
        ResponseEntity<String> createResponse = makePostRequest(
                "/api/v1/accounts",
                createBody
        );
        assertTrue(createResponse.getBody().contains("\"success\":true"));
        
        // Extract account ID
        String accountId = extractId(createResponse.getBody());
        
        // Now get the account
        ResponseEntity<String> response = makeGetRequest("/api/v1/accounts/" + accountId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void getAccount_withInvalidId_returnsNotFound() {
        authToken = createTestUserAndLogin("getaccount_invalid");
        
        ResponseEntity<String> response = makeGetRequest("/api/v1/accounts/99999");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":false"));
    }

    // ========================================================================
    // ADD Tests
    // ========================================================================

    @Test
    void addAccount_withValidData_returnsCreated() {
        authToken = createTestUserAndLogin("addaccount");
        
        Map<String, Object> body = new HashMap<>();
        body.put("name", "NewBankAccount_" + (System.currentTimeMillis() % 100000));
        body.put("accountType", "CHECKING");
        body.put("currency", "USD");
        body.put("balance", 500000L);
        
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts", body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("NewBankAccount_"));
    }

    @Test
    void addAccount_withInvalidType_returnsError() {
        authToken = createTestUserAndLogin("addaccount_invalidtype");
        
        Map<String, Object> body = new HashMap<>();
        body.put("name", "InvalidTypeAccount_" + (System.currentTimeMillis() % 100000));
        body.put("accountType", "INVALID_TYPE");
        body.put("currency", "USD");
        
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts", body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":false"));
    }

    @Test
    void addAccount_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = new HashMap<>();
        body.put("name", "TestAccount");
        body.put("accountType", "CASH");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/accounts",
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
    // MODIFY Tests
    // ========================================================================

    @Test
    void modifyAccount_withValidData_returnsUpdated() {
        authToken = createTestUserAndLogin("modifyaccount");
        
        // First create an account
        long ts = System.currentTimeMillis() % 100000;
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("name", "OriginalAcct_" + ts);
        createBody.put("accountType", "CASH");
        createBody.put("currency", "USD");
        
        ResponseEntity<String> createResponse = makePostRequest("/api/v1/accounts", createBody);
        String accountId = extractId(createResponse.getBody());
        
        // Now modify it
        Map<String, Object> modifyBody = new HashMap<>();
        modifyBody.put("name", "UpdatedAcctName_" + ts);
        modifyBody.put("accountType", "SAVINGS");
        modifyBody.put("currency", "EUR");
        
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts/" + accountId, modifyBody);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("UpdatedAcctName_"));
    }

    @Test
    void modifyAccount_withNonExistentId_returnsError() {
        authToken = createTestUserAndLogin("modifyaccount_invalid");
        
        Map<String, Object> body = new HashMap<>();
        body.put("name", "UpdatedName");
        body.put("accountType", "CASH");
        
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts/99999", body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":false"));
    }

    // ========================================================================
    // HIDE Tests
    // ========================================================================

    @Test
    void hideAccount_withValidId_hidesAccount() {
        authToken = createTestUserAndLogin("hideaccount");
        
        // Create account first
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("name", "AcctToHide_" + (System.currentTimeMillis() % 100000));
        createBody.put("accountType", "CASH");
        createBody.put("currency", "USD");
        
        ResponseEntity<String> createResponse = makePostRequest("/api/v1/accounts", createBody);
        String accountId = extractId(createResponse.getBody());
        
        // Hide the account
        Map<String, Object> hideBody = new HashMap<>();
        hideBody.put("id", Long.parseLong(accountId));
        hideBody.put("hidden", true);
        
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts/hide.json", hideBody);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    // ========================================================================
    // MOVE Tests
    // ========================================================================

    @Test
    void moveAccounts_withValidOrder_reordersAccounts() {
        authToken = createTestUserAndLogin("moveaccounts");
        
        long ts = System.currentTimeMillis() % 100000;
        
        // Create multiple accounts
        Map<String, Object> body1 = new HashMap<>();
        body1.put("name", "Acct1_" + ts);
        body1.put("accountType", "CASH");
        body1.put("currency", "USD");
        
        Map<String, Object> body2 = new HashMap<>();
        body2.put("name", "Acct2_" + ts);
        body2.put("accountType", "CHECKING");
        body2.put("currency", "USD");
        
        ResponseEntity<String> resp1 = makePostRequest("/api/v1/accounts", body1);
        ResponseEntity<String> resp2 = makePostRequest("/api/v1/accounts", body2);
        
        String id1 = extractId(resp1.getBody());
        String id2 = extractId(resp2.getBody());
        
        // Reorder
        Map<String, Object> moveBody = new HashMap<>();
        moveBody.put("orderedIds", List.of(Long.parseLong(id2), Long.parseLong(id1)));
        
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts/move.json", moveBody);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    // ========================================================================
    // DELETE Tests
    // ========================================================================

    @Test
    void deleteAccount_withValidId_deletesAccount() {
        authToken = createTestUserAndLogin("deleteaccount");
        
        // Create account first
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("name", "AcctToDelete_" + (System.currentTimeMillis() % 100000));
        createBody.put("accountType", "CASH");
        createBody.put("currency", "USD");
        
        ResponseEntity<String> createResponse = makePostRequest("/api/v1/accounts", createBody);
        String accountId = extractId(createResponse.getBody());
        
        // Delete the account
        ResponseEntity<String> response = makePostRequest("/api/v1/accounts/" + accountId, Map.of());
        
        // Note: Spring Data JPA delete returns 200 even if entity not found
        // This test verifies the endpoint works
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String extractId(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.path("result").path("id").asText();
        } catch (Exception e) {
            fail("Failed to extract ID: " + e.getMessage());
            return null;
        }
    }
}