package com.bookkeeping.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HealthControllerIntegrationTest {

    @Autowired
    private HealthController healthController;

    @Test
    void healthEndpoint_returnsUp() {
        Map<String, String> response = healthController.health();
        
        assertEquals("bookkeeping", response.get("service"));
        assertEquals("UP", response.get("status"));
    }

    @Test
    void infoEndpoint_returnsApiInfo() {
        Map<String, Object> response = healthController.info();
        
        assertEquals("Bookkeeping API", response.get("name"));
        assertEquals("0.1.0", response.get("version"));
    }
}