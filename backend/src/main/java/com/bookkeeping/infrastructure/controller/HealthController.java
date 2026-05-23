package com.bookkeeping.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check controller for OpenAPI verification.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @Operation(summary = "Health check", description = "Returns application status")
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "service", "bookkeeping",
            "status", "UP",
            "version", "0.1.0"
        );
    }

    @Operation(summary = "API info", description = "Returns API information")
    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "name", "Bookkeeping API",
            "version", "0.1.0",
            "description", "Personal bookkeeping application API"
        );
    }
}