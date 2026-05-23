package com.bookkeeping;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base integration test class.
 * Tests run against real PostgreSQL database.
 */
@SpringBootTest
@ActiveProfiles("integrationtest")
public abstract class BaseIntegrationTest {
    // Shared configuration for all integration tests
}