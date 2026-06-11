package com.bookkeeping;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base integration test class.
 * Tests run against real PostgreSQL database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    protected String baseUrl() {
        return "http://localhost:" + port;
    }
}