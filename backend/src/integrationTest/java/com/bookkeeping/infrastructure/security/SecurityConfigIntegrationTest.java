package com.bookkeeping.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Spring Security configuration.
 * Verifies that public endpoints are accessible and protected endpoints require authentication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityConfigIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void healthEndpoint_isPubliclyAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/api/v1/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void infoEndpoint_isPubliclyAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/api/v1/info", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void usersMeEndpoint_requiresAuthentication() {
        // Without auth, should get 401
        try {
            restTemplate.getForEntity(baseUrl() + "/api/v1/users/me", String.class);
            fail("Expected HttpClientErrorException for 401 response");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void usersMeEndpoint_withInvalidToken_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalid.token.here");
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            restTemplate.exchange(
                    baseUrl() + "/api/v1/users/me",
                    HttpMethod.GET,
                    request,
                    String.class
            );
            fail("Expected HttpClientErrorException for 401 response");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void swaggerUiEndpoint_isPubliclyAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/swagger-ui/index.html", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void apiDocsEndpoint_isPubliclyAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/api-docs", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
