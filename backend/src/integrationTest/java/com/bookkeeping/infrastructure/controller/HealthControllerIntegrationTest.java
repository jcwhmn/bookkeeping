package com.bookkeeping.infrastructure.controller;

import com.bookkeeping.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Health Controller Integration Tests")
public class HealthControllerIntegrationTest extends BaseIntegrationTest {

    @Override
    protected void insertTestData() {
        // No data needed for health check
    }

    @Override
    protected void cleanTestData() {
        // No data to clean
    }

    @Test
    @DisplayName("✓ Success: Health check returns UP status")
    void health_returnsUpStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("bookkeeping"));
    }
}