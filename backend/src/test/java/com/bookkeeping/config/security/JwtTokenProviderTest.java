package com.bookkeeping.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider.
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Use a test secret key (must be at least 32 bytes for HMAC-SHA256)
        String testSecret = "testSecretKeyForUnitTestingOnly123456789012345678901234567890";
        long testExpiration = 86400000L; // 24 hours
        jwtTokenProvider = new JwtTokenProvider(testSecret, testExpiration);
    }

    @Test
    void generateToken_withValidUsername_returnsToken() {
        String username = "testuser";

        String token = jwtTokenProvider.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT format: header.payload.signature
    }

    @Test
    void getUsernameFromToken_withValidToken_returnsUsername() {
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);

        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        String token = jwtTokenProvider.generateToken("testuser");

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_withEmptyToken_returnsFalse() {
        boolean isValid = jwtTokenProvider.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void validateToken_withMalformedToken_returnsFalse() {
        String malformedToken = "not-a-jwt-token";

        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        assertFalse(isValid);
    }

    @Test
    void getExpirationDateFromToken_withValidToken_returnsExpirationDate() {
        String token = jwtTokenProvider.generateToken("testuser");

        java.util.Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new java.util.Date()));
    }

    @Test
    void isTokenExpired_withFreshToken_returnsFalse() {
        String token = jwtTokenProvider.generateToken("testuser");

        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_withExpiredToken_throwsException() {
        // Create provider with very short expiration
        String testSecret = "testSecretKeyForUnitTestingOnly123456789012345678901234567890";
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(testSecret, 1); // 1ms expiration
        String token = shortLivedProvider.generateToken("testuser");

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // getExpirationDateFromToken throws ExpiredJwtException for expired tokens
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            shortLivedProvider.getExpirationDateFromToken(token);
        });
    }

    @Test
    void validateToken_withExpiredToken_returnsFalse() throws InterruptedException {
        // Create provider with very short expiration
        String testSecret = "testSecretKeyForUnitTestingOnly123456789012345678901234567890";
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(testSecret, 1); // 1ms expiration
        String token = shortLivedProvider.generateToken("testuser");

        // Wait for token to expire
        Thread.sleep(10);

        boolean isValid = shortLivedProvider.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void validateToken_withNullToken_returnsFalse() {
        boolean isValid = jwtTokenProvider.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    void generateToken_withShortSecret_throwsWeakKeyException() {
        // Secret must be at least 32 bytes for HMAC-SHA256
        // WeakKeyException is thrown in the constructor, not during token generation
        assertThrows(io.jsonwebtoken.security.WeakKeyException.class, () -> {
            new JwtTokenProvider("short", 86400000L);
        });
    }

    @Test
    void generateToken_withNullSecret_throwsException() {
        // Should throw an exception for null secret (exception thrown during construction)
        assertThrows(Exception.class, () -> {
            new JwtTokenProvider(null, 86400000L);
        });
    }

    @Test
    void generateToken_withEmptyUsername_generatesToken() {
        String token = jwtTokenProvider.generateToken("");

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateToken_withSpecialCharactersInUsername_works() {
        String username = "user+special@example.com";

        String token = jwtTokenProvider.generateToken(username);
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_withTamperedToken_returnsFalse() {
        String token = jwtTokenProvider.generateToken("testuser");
        // Tamper with the token by changing a character
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    void generateToken_createsWellFormedJwt() {
        String token = jwtTokenProvider.generateToken("testuser");
        String[] parts = token.split("\\.");

        // JWT should have 3 parts: header.payload.signature
        assertEquals(3, parts.length);
    }
}
