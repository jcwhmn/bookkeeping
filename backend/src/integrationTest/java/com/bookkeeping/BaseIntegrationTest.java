package com.bookkeeping;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Base class for integration tests.
 * Each test gets fresh test data that is cleaned up after the test.
 */
@SpringBootTest
@ActiveProfiles("integrationtest")
public abstract class BaseIntegrationTest {

    protected MockMvc mockMvc;
    protected String accessToken;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @BeforeEach
    protected void setUpBase() throws Exception {
        // Setup MockMvc with Security
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Clean up and insert fresh test data
        insertTestData();

        // Login to get token
        accessToken = loginAndGetToken("testuser", "password123");
    }

    @AfterEach
    protected void tearDownBase() {
        // Delete all test data
        cleanTestData();
    }

    protected abstract void insertTestData();

    protected abstract void cleanTestData();

    private String loginAndGetToken(String username, String password) throws Exception {
        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int tokenStart = response.indexOf("\"token\":\"") + 9;
        int tokenEnd = response.indexOf("\"", tokenStart);
        return response.substring(tokenStart, tokenEnd);
    }

    protected String authHeader() {
        return "Bearer " + accessToken;
    }

    // Helper method to generate unique test data names
    protected String uniqueName(String baseName) {
        return baseName + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}