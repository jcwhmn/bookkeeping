# ADR-008: Backend Dependencies

## Status
✅ Accepted

## Context

We need to select and document all backend dependencies for the Spring Boot application. The goal is cutting-edge versions with stability.

## Decision

### Core Stack

| Dependency | Purpose | Version |
|------------|---------|---------|
| Spring Boot | Framework | 4.0.6 |
| Spring Data JPA | ORM | (with Spring Boot) |
| Hibernate | JPA implementation | 6.x |
| PostgreSQL Driver | Database | 42.7.x |
| Flyway | Database migration | 11.x |
| Lombok | Boilerplate reduction | 1.18.x |
| Jakarta Bean Validation | Input validation | 3.x |

### API & Documentation

| Dependency | Purpose | Version |
|------------|---------|---------|
| SpringDoc OpenAPI | API docs (Swagger) | 2.9.x |
| Jackson | JSON processing | 2.18.x |

### Security

| Dependency | Purpose | Version |
|------------|---------|---------|
| Spring Security | Security framework | (with Spring Boot) |
| JJWT | JWT token handling | 0.12.x |

### Caching

| Dependency | Purpose | Version |
|------------|---------|---------|
| Caffeine | In-memory cache | 3.x |
| Spring Cache | Cache abstraction | (with Spring Boot) |

### Logging

| Dependency | Purpose | Version |
|------------|---------|---------|
| SLF4j | Logging API | 2.x |
| Logback | Logging implementation | (Spring Boot default) |

### Testing

| Dependency | Purpose | Version |
|------------|---------|---------|
| JUnit 5 | Unit testing | 5.12.x |
| Mockito | Mocking | 5.x |
| AssertJ | Fluent assertions | 3.26.x |
| H2 Database | In-memory testing | 2.x |

### Utilities

| Dependency | Purpose | Version |
|------------|---------|---------|
| Apache Commons Lang3 | String/object utils | 3.15.x |
| commons-codec | MD5, Base64, etc. | 1.16.x |

## Optional / Future

| Dependency | Purpose | Version |
|------------|---------|---------|
| mapstruct-ext | DTO mapping | 1.0.x |
| Testcontainers | Integration tests | 11.x |

## Build Configuration Example

```kotlin
// build.gradle.kts
plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.0.0"  // Lombok plugin
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.9.0")
    
    // Security
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine")
    
    // Utilities
    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("commons-codec:commons-codec:1.16.1")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

## Library Rationale

| Library | Why |
|---------|-----|
| Lombok | Reduces boilerplate (getters, setters, builder, etc.) |
| Caffeine | Fast in-memory cache, better than EhCache |
| JJWT | Standard JWT library, no need to code JWT manually |
| SpringDoc | Auto-generates OpenAPI 3.0+ docs |
| Apache Commons | Fill gaps in standard library |
| H2 | In-memory database for unit tests |

## Not Included

| Library | Reason |
|---------|--------|
| Redis | Not needed for v1.0 |
| Elasticsearch | No search requirements |
| Kafka | No async messaging needs |
| Resilience4j | No circuit breaker needed yet |

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted