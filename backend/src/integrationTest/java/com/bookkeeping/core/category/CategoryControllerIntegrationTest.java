package com.bookkeeping.core.category;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.supporting.auth.AuthService;
import com.bookkeeping.supporting.auth.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CategoryController REST API.
 * Tests HTTP endpoints using real HTTP calls via RestTemplate.
 * 
 * Endpoints tested:
 * - GET /api/v1/categories - List categories
 * - GET /api/v1/categories/search - Search by name
 * - GET /api/v1/categories/{id} - Get single category
 * - POST /api/v1/categories - Create category
 * - PUT /api/v1/categories/{id} - Update category
 * - PATCH /api/v1/categories/{id}/hidden - Hide/unhide
 * - PUT /api/v1/categories/reorder - Reorder
 */
class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final org.springframework.web.client.RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    private String authToken;
    private String username;

    CategoryControllerIntegrationTest() {
        // Use Apache HttpClient as the request factory because the JDK's
        // HttpURLConnection (used by SimpleClientHttpRequestFactory) does NOT
        // support the PATCH HTTP method - it throws java.net.ProtocolException.
        // Apache HttpClient5 (added as integrationTestImplementation) supports
        // PATCH natively.
        this.restTemplate = new org.springframework.web.client.RestTemplate(
                new org.springframework.http.client.HttpComponentsClientHttpRequestFactory());
    }

    @BeforeEach
    void setUp() {
        username = "test_" + (System.currentTimeMillis() % 100000);
        String password = "password123";
        
        authService.register(new RegisterRequest(
                username, username + "@example.com", password));
        
        authToken = login(username, password);
    }

    private String login(String username, String password) {
        String url = baseUrl() + "/api/v1/auth/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> body = Map.of(
                "username", username,
                "password", password
        );
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.path("result").path("token").asText();
        } catch (Exception e) {
            fail("Failed to parse login response: " + e.getMessage());
            return null;
        }
    }

    // ========================================================================
    // LIST Tests
    // ========================================================================

    @Test
    void listCategories_withAuth_returnsCategoryList() {
        ResponseEntity<String> response = makeGetRequest("/api/v1/categories");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("\"result\":"));
    }

    @Test
    void listCategories_withTypeFilter_returnsFilteredList() {
        // Create an EXPENSE category first
        createCategory("Expense_Cat_" + username, 2);
        
        ResponseEntity<String> response = makeGetRequest("/api/v1/categories?type=2");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("Expense_Cat_"));
    }

    @Test
    void listCategories_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/categories",
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
    // SEARCH Tests
    // ========================================================================

    @Test
    void searchCategories_searchByName_returnsMatches() {
        // Create a searchable category
        String uniqueName = "Searchable_" + username + "_" + System.currentTimeMillis();
        createCategory(uniqueName, 2);
        
        // Search for it
        ResponseEntity<String> response = makeGetRequest(
                "/api/v1/categories/search?name=" + uniqueName);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains(uniqueName));
    }

    @Test
    void searchCategories_searchByNameAndType_returnsMatches() {
        // Create an EXPENSE category
        String uniqueName = "SearchType_" + username + "_" + System.currentTimeMillis();
        createCategory(uniqueName, 2);
        
        // Search with type filter
        ResponseEntity<String> response = makeGetRequest(
                "/api/v1/categories/search?name=" + uniqueName + "&type=2");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains(uniqueName));
    }

    // ========================================================================
    // GET BY ID Tests
    // ========================================================================

    @Test
    void getCategory_withValidId_returnsCategory() {
        // Create a category
        Long categoryId = createCategoryReturnId("GetTest_" + username, 1);
        
        // Get it
        ResponseEntity<String> response = makeGetRequest("/api/v1/categories/" + categoryId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("GetTest_"));
    }

    @Test
    void getCategory_withInvalidId_returnsNotFound() {
        ResponseEntity<String> response = makeGetRequest("/api/v1/categories/999999");
        
        assertEquals(HttpStatus.OK, response.getStatusCode()); // API returns 200 with error in body
        assertTrue(response.getBody().contains("\"success\":false"));
    }

    // ========================================================================
    // CREATE Tests
    // ========================================================================

    @Test
    void createCategory_withValidData_returnsCreated() {
        Map<String, Object> body = Map.of(
                "name", "NewCategory_" + username,
                "type", 2,
                "icon", "mdi-cart",
                "color", "#FF5722",
                "comment", "Test comment"
        );
        
        ResponseEntity<String> response = makePostRequest("/api/v1/categories", body);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("NewCategory_"));
        assertTrue(response.getBody().contains("mdi-cart"));
        assertTrue(response.getBody().contains("#FF5722"));
    }

    @Test
    void createCategory_withInvalidType_returnsError() {
        Map<String, Object> body = Map.of(
                "name", "InvalidType_" + username,
                "type", 99  // Invalid type
        );
        
        ResponseEntity<String> response = makePostRequest("/api/v1/categories", body);
        
        assertTrue(response.getBody().contains("\"success\":false"));
    }

    @Test
    void createCategory_withDuplicateName_returnsError() {
        String name = "Duplicate_" + username;
        
        // Create first category
        createCategory(name, 2);
        
        // Try to create duplicate
        Map<String, Object> body = Map.of(
                "name", name,
                "type", 1
        );
        
        ResponseEntity<String> response = makePostRequest("/api/v1/categories", body);
        
        assertTrue(response.getBody().contains("\"success\":false"));
    }

    @Test
    void createCategory_withoutAuth_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
                "name", "TestCategory",
                "type", 2
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/categories",
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
    // UPDATE Tests
    // ========================================================================

    @Test
    void updateCategory_withValidData_returnsUpdated() {
        // Create a category
        Long categoryId = createCategoryReturnId("UpdateTest_" + username, 2);
        
        // Update it
        Map<String, Object> body = Map.of(
                "name", "UpdatedName_" + username,
                "type", 1,
                "icon", "mdi-star",
                "color", "#4CAF50"
        );
        
        ResponseEntity<String> response = makePutRequest("/api/v1/categories/" + categoryId, body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("UpdatedName_"));
    }

    // ========================================================================
    // HIDE/UNHIDE Tests
    // ========================================================================

    // Note: PATCH is supported because we use Apache HttpClient5 as the
    // request factory in the RestTemplate constructor.

    @Test
    void hideCategory_withValidId_hidesCategory() {
        // Create a category
        Long categoryId = createCategoryReturnId("HideTest_" + username, 2);
        
        // Hide the category
        Map<String, Object> body = Map.of("hidden", true);
        
        ResponseEntity<String> response = makePatchRequest(
                "/api/v1/categories/" + categoryId + "/hidden", body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void unhideCategory_withValidId_unhidesCategory() {
        // Create a hidden category
        Long categoryId = createCategoryReturnId("UnhideTest_" + username, 2);
        
        // First hide
        makePatchRequest("/api/v1/categories/" + categoryId + "/hidden", 
                Map.of("hidden", true));
        
        // Then unhide
        Map<String, Object> body = Map.of("hidden", false);
        ResponseEntity<String> response = makePatchRequest(
                "/api/v1/categories/" + categoryId + "/hidden", body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    // ========================================================================
    // REORDER Tests
    // ========================================================================

    @Test
    void reorderCategories_withValidOrder_reordersCategories() {
        // Create multiple categories
        Long id1 = createCategoryReturnId("ReorderA_" + username, 2);
        Long id2 = createCategoryReturnId("ReorderB_" + username, 2);
        
        // Reorder: id2 first, then id1
        Map<String, Object> body = Map.of(
                "categoryIds", List.of(id2, id1)
        );
        
        ResponseEntity<String> response = makePutRequest("/api/v1/categories/reorder", body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    // ========================================================================
    // TRANSFER TYPE Tests
    // ========================================================================

    @Test
    void createCategory_withTransferType_createsSuccessfully() {
        Map<String, Object> body = Map.of(
                "name", "Transfer_" + username,
                "type", 3  // TRANSFER
        );
        
        ResponseEntity<String> response = makePostRequest("/api/v1/categories", body);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void listCategories_withTransferTypeFilter_returnsTransferCategories() {
        // Create a TRANSFER category
        createCategory("TransferCat_" + username, 3);
        
        ResponseEntity<String> response = makeGetRequest("/api/v1/categories?type=3");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
        assertTrue(response.getBody().contains("TransferCat_"));
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private ResponseEntity<String> makeGetRequest(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(baseUrl() + endpoint, HttpMethod.GET, request, String.class);
    }

    private ResponseEntity<String> makePostRequest(String endpoint, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(baseUrl() + endpoint, request, String.class);
    }

    private ResponseEntity<String> makePutRequest(String endpoint, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(baseUrl() + endpoint, HttpMethod.PUT, request, String.class);
    }

    private ResponseEntity<String> makePatchRequest(String endpoint, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(baseUrl() + endpoint, HttpMethod.PATCH, request, String.class);
    }

    private Long createCategoryReturnId(String name, int type) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("type", type);
        
        ResponseEntity<String> response = makePostRequest("/api/v1/categories", body);
        
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.path("result").path("id").asLong();
        } catch (Exception e) {
            fail("Failed to extract category ID: " + e.getMessage());
            return null;
        }
    }

    private void createCategory(String name, int type) {
        createCategoryReturnId(name, type);
    }
}
