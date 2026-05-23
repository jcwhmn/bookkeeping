# Bookkeeping Implementation Plan v1.0

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan phase-by-phase. Each phase has a GATE that must pass verification before proceeding.

**Goal:** Build a working bookkeeping application with user authentication and account management

**Starting Version:** 0.0.1-SNAPSHOT

**Target Version:** 0.1.0 (M1 - User Auth + Accounts)

**Architecture:** Monolith (Spring Boot + Nuxt), PostgreSQL 18+, JWT Auth

---

## 🔑 Gate Definition

Each phase ends with a **GATE** - verification checkpoint:

| Gate | Check | Command |
|------|-------|---------|
| **G0** | Project compiles | `cd backend && gradle compileJava` |
| **G1** | App starts | `curl http://localhost:8080/actuator/health` → `{"status":"UP"}` |
| **G2** | OpenAPI works | `curl http://localhost:8080/api-docs` → valid JSON |
| **G3** | Security works | `curl http://localhost:8080/api/v1/auth/login` → 400/401 (valid response) |
| **G4** | Unit tests pass | `cd backend && gradle test` |
| **G5** | Integration tests pass | `cd backend && gradle integrationTest` |

**Gate Rules:**
- Must pass ALL checks in a gate before proceeding
- If gate fails, fix before continuing
- Document any issues in commit message

---

## Phase 0: Spring Boot Starter ⛩️ ✅ COMPLETE

**Objective:** Create minimal Spring Boot project with all dependencies, verify it compiles and runs

### Files created:

```
backend/
├── build.gradle.kts          ✅
├── settings.gradle.kts         ✅
├── gradle.properties           ✅
├── gradlew                      ✅
└── src/main/
    ├── java/com/bookkeeping/
    │   └── BookkeepingApplication.java  ✅
    └── resources/
        ├── application.yml      ✅
        └── db/migration/
            └── V1__init.sql      ✅
```

### Verification Results:
- ✅ Compilation successful
- ✅ App starts without errors
- ✅ Health endpoint returns UP
- ✅ Unit test passes (context loads)

---

## Phase 1: Common Infrastructure ✅ COMPLETE

**Objective:** Add base classes, enums, and utilities used across the application

### Files created:

```
backend/src/main/java/com/bookkeeping/
├── common/
│   ├── Auditable.java           ✅
│   ├── BaseEntity.java          ✅
│   ├── ApiResponse.java         ✅
│   ├── ResultCode.java          ✅
│   └── enums/
│       ├── AccountType.java     ✅
│       └── TransactionType.java ✅
├── exception/
│   ├── BusinessException.java   ✅
│   └── GlobalExceptionHandler.java ✅
└── config/
    └── CacheConfig.java         ✅
```

### Verification Results:
- ✅ Compilation successful
- ✅ 6 GlobalExceptionHandler tests pass
- ✅ App starts without errors
- ✅ Health endpoint returns UP

---

## Phase 2: OpenAPI Support ✅ COMPLETE

**Objective:** Configure SpringDoc OpenAPI with proper documentation

### Files created:

```
backend/src/main/java/com/bookkeeping/
├── config/
│   ├── OpenApiConfig.java       ✅ # Swagger UI, JWT security, API info
│   └── SecurityConfig.java      ✅ # Permit all (Phase 3 will configure)
└── infrastructure/controller/
    └── HealthController.java    ✅ # /api/v1/health, /api/v1/info
```

### Verification Results:
- ✅ OpenAPI JSON: `curl http://localhost:8080/api-docs` → valid JSON
- ✅ Swagger UI: `curl http://localhost:8080/swagger-ui/index.html` → HTML
- ✅ Health endpoint: `curl http://localhost:8080/api/v1/health` → `{"status":"UP"}`
- ✅ All tests pass (9 tests)

### OpenAPI Features:
- Title: "Bookkeeping API"
- Version: 0.1.0
- JWT Bearer auth configured
- Health and Info endpoints documented

---

## Phase 3: User Module (CRUD) ✅ COMPLETE

**Objective:** Complete user management with entity, repository, service, controller

### Files created:

```
backend/src/main/java/com/bookkeeping/supporting/user/
├── User.java                   ✅ Entity with @Getter @Setter
├── UserRepository.java         ✅ Spring Data JPA interface
├── UserDto.java                ✅ Response DTO (Java Record)
├── UpdateUserRequest.java      ✅ Request DTO (Java Record)
├── UserService.java            ✅ Business logic
└── UserController.java          ✅ REST endpoints

backend/src/test/java/com/bookkeeping/supporting/user/
└── UserServiceTest.java        ✅ 16 unit tests

backend/src/integrationTest/java/com/bookkeeping/supporting/user/
├── BaseIntegrationTest.java    ✅ Base class
└── UserControllerIntegrationTest.java  ✅ 15 integration tests
```

### Test Results:
- ✅ **Unit Tests**: 24 passed (GlobalExceptionHandler: 6, HealthController: 2, UserService: 16)
- ✅ **Integration Tests**: 15 passed (UserRepository/Service)
- ✅ **Total**: 39 tests all passing

### API Endpoints (visible in Swagger):
- `GET /api/v1/users/me` - Get current user profile
- `PUT /api/v1/users/me` - Update current user profile
- `GET /api/v1/users/{id}` - Get user by ID

---

## Phase 4: Spring Security Setup

**Objective:** Configure Spring Security with public and authenticated endpoints

### Files to create/update:

```
backend/src/main/java/com/bookkeeping/
├── infrastructure/config/
│   ├── SecurityConfig.java      # Security configuration
│   └── JwtAuthenticationEntryPoint.java
└── supporting/security/
    ├── JwtTokenProvider.java   # JWT generation/validation
    └── JwtAuthenticationFilter.java
```

### Tasks:

- [ ] **Task 3.1:** Create `SecurityConfig.java` (permit /actuator/**, /api-docs, /swagger-ui, deny all else)
- [ ] **Task 3.2:** Create `JwtAuthenticationEntryPoint.java`
- [ ] **Task 3.3:** Create `JwtTokenProvider.java` (stub - just generate/validate JWT)
- [ ] **Task 3.4:** Create `JwtAuthenticationFilter.java`
- [ ] **Task 3.5:** Update `SecurityConfig.java` to add JWT filter
- [ ] **Task 3.6:** Write unit tests for JWT utilities
- [ ] **Task 3.7:** Write integration tests for security config

### 🚪 GATE 3: Security Verification

```bash
# G3.1: Compile
cd backend && gradle clean compileJava

# G3.2: Start app
cd backend && gradle bootRun &
sleep 15

# G3.3: Public endpoints should work
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}

# G3.4: Auth endpoint should require auth
curl http://localhost:8080/api/v1/users/me
# Expected: 401 Unauthorized

# G3.5: Run all tests
cd backend && gradle test integrationTest
# Expected: All tests pass

# G3.6: Cleanup
pkill -f 'bootRun' || true
```

**Gate 3 Pass Criteria:**
- ✅ Health endpoint publicly accessible
- ✅ User endpoint returns 401 (auth required)
- ✅ JWT filter loads without errors
- ✅ All tests pass

---

## Phase 4: User Module (CRUD)

**Objective:** Complete user management with entity, repository, service, controller

### Files to create:

```
backend/src/main/java/com/bookkeeping/supporting/user/
├── User.java                   # Entity
├── UserRepository.java         # Spring Data JPA
├── UserDto.java                # Response DTO (record)
├── UpdateUserRequest.java      # Request DTO (record)
├── UserService.java            # Business logic
└── UserController.java         # REST endpoints
```

### Tasks:

- [ ] **Task 4.1:** Create `User.java` extending BaseEntity
- [ ] **Task 4.2:** Create `UserRepository.java` interface
- [ ] **Task 4.3:** Create `UserDto.java` (record for response)
- [ ] **Task 4.4:** Create `UpdateUserRequest.java` (record for request)
- [ ] **Task 4.5:** Create `UserService.java` with business logic
- [ ] **Task 4.6:** Create `UserController.java` with endpoints
- [ ] **Task 4.7:** Write unit tests for `UserService`
- [ ] **Task 4.8:** Write integration tests for `UserController`

### 🚪 GATE 4: User Module Verification

```bash
# G4.1: Compile
cd backend && gradle clean compileJava

# G4.2: Start app
cd backend && gradle bootRun &
sleep 15

# G4.3: Get current user (with auth)
# First need a user in DB - check if demo exists
# If not, create via DataInitializer or direct SQL

# G4.4: Run all tests
cd backend && gradle test integrationTest
# Expected: All tests pass

# G4.5: Cleanup
pkill -f 'bootRun' || true
```

**Gate 4 Pass Criteria:**
- ✅ User entity compiles and persists
- ✅ GET /api/v1/users/me works with auth
- ✅ PUT /api/v1/users/me works with auth
- ✅ All unit and integration tests pass

---

## Phase 5: Authentication Module

**Objective:** Complete JWT authentication with login/logout/refresh

### Files to create:

```
backend/src/main/java/com/bookkeeping/supporting/
├── auth/
│   ├── LoginRequest.java       # Request DTO
│   ├── LoginResponse.java      # Response DTO
│   ├── RegisterRequest.java    # Request DTO
│   ├── AuthService.java        # Business logic
│   └── AuthController.java     # REST endpoints
└── security/
    └── SecurityUtils.java      # Helper to get current user
```

### Tasks:

- [ ] **Task 5.1:** Create `LoginRequest.java`
- [ ] **Task 5.2:** Create `LoginResponse.java`
- [ ] **Task 5.3:** Create `RegisterRequest.java`
- [ ] **Task 5.4:** Create `AuthService.java` (password encoding, JWT generation)
- [ ] **Task 5.5:** Create `AuthController.java` (POST /login, POST /register)
- [ ] **Task 5.6:** Create `SecurityUtils.java`
- [ ] **Task 5.7:** Update `JwtTokenProvider.java` with real implementation
- [ ] **Task 5.8:** Update `SecurityConfig.java` for proper CORS
- [ ] **Task 5.9:** Create `DataInitializer.java` for demo user
- [ ] **Task 5.10:** Write unit tests for `AuthService`
- [ ] **Task 5.11:** Write integration tests for `AuthController`

### 🚪 GATE 5: Auth Module Verification

```bash
# G5.1: Compile
cd backend && gradle clean compileJava

# G5.2: Start app
cd backend && gradle bootRun &
sleep 15

# G5.3: Login with demo user
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
# Expected: {"success":true,"result":{"token":"...","user":{...}}}

# G5.4: Use token to access protected endpoint
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.result.token')
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/users/me
# Expected: User details

# G5.5: Run all tests
cd backend && gradle test integrationTest
# Expected: All tests pass

# G5.6: Cleanup
pkill -f 'bootRun' || true
```

**Gate 5 Pass Criteria:**
- ✅ Login returns JWT token
- ✅ Token works for authenticated endpoints
- ✅ Invalid credentials return 401
- ✅ All unit and integration tests pass

---

## Phase 6: Account Module (Core Domain)

**Objective:** Account CRUD operations

### Files to create:

```
backend/src/main/java/com/bookkeeping/core/account/
├── Account.java               # Entity
├── AccountRepository.java     # Spring Data JPA
├── AccountDto.java            # Response DTO (record)
├── CreateAccountRequest.java  # Request DTO (record)
├── UpdateAccountRequest.java  # Request DTO (record)
├── AccountService.java        # Business logic
└── AccountController.java     # REST endpoints
```

### Tasks:

- [ ] **Task 6.1:** Create `Account.java` extending BaseEntity
- [ ] **Task 6.2:** Create `AccountRepository.java` interface
- [ ] **Task 6.3:** Create `AccountDto.java`, `CreateAccountRequest.java`, `UpdateAccountRequest.java`
- [ ] **Task 6.4:** Create `AccountService.java` with business logic
- [ ] **Task 6.5:** Create `AccountController.java` with endpoints
- [ ] **Task 6.6:** Write unit tests for `AccountService`
- [ ] **Task 6.7:** Write integration tests for `AccountController`

### 🚪 GATE 6: Account Module Verification

```bash
# G6.1: Compile
cd backend && gradle clean compileJava

# G6.2: Get auth token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.result.token')

# G6.3: Create account
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Cash","type":"CASH","currency":"USD","balanceStr":"1000"}'

# G6.4: List accounts
curl http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN"

# G6.5: Get account
curl http://localhost:8080/api/v1/accounts/1 \
  -H "Authorization: Bearer $TOKEN"

# G6.6: Update account
curl -X PUT http://localhost:8080/api/v1/accounts/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Cash Wallet"}'

# G6.7: Run all tests
cd backend && gradle test integrationTest
```

**Gate 6 Pass Criteria:**
- ✅ Create account succeeds
- ✅ List accounts returns data
- ✅ Get account by ID works
- ✅ Update account works
- ✅ All tests pass

---

## Phase 7: Final Verification

**Objective:** Full integration test and documentation

### Tasks:

- [ ] **Task 7.1:** Run full test suite
- [ ] **Task 7.2:** Update API documentation
- [ ] **Task 7.3:** Verify all ADR decisions implemented

### 🚪 GATE 7: Final Verification

```bash
# G7.1: Clean and build
cd backend && gradle clean build

# G7.2: Run all tests with report
cd backend && gradle allTestsReport

# G7.3: Start app and do smoke tests
cd backend && gradle bootRun &
sleep 15

# Full auth flow
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r '.result.token')

# Create account
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","type":"CASH","currency":"USD"}'

# Get user
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer $TOKEN"

pkill -f 'bootRun' || true
```

**Gate 7 Pass Criteria:**
- ✅ Full build succeeds
- ✅ All 50+ tests pass
- ✅ Complete auth flow works
- ✅ Account CRUD works
- ✅ Swagger UI shows all endpoints

---

## Verification Summary

| Phase | Gate | Key Verification |
|-------|------|-------------------|
| 0 | G0 | Spring Boot starter compiles and runs |
| 1 | G1 | Common infrastructure in place |
| 2 | G2 | OpenAPI documentation works |
| 3 | G3 | Spring Security blocks unauthorized |
| 4 | G4 | User module complete |
| 5 | G5 | JWT authentication works |
| 6 | G6 | Account CRUD works |
| 7 | G7 | Full integration verified |

---

## File Summary (Final State)

```
bookkeeping/backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── src/
    ├── main/
    │   ├── java/com/bookkeeping/
    │   │   ├── BookkeepingApplication.java
    │   │   ├── common/
    │   │   │   ├── Auditable.java
    │   │   │   ├── BaseEntity.java
    │   │   │   ├── ApiResponse.java
    │   │   │   ├── ResultCode.java
    │   │   │   └── enums/
    │   │   ├── exception/
    │   │   │   ├── BusinessException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── infrastructure/
    │   │   │   └── config/
    │   │   │       ├── CacheConfig.java
    │   │   │       ├── OpenApiConfig.java
    │   │   │       └── SecurityConfig.java
    │   │   └── supporting/
    │   │       ├── auth/
    │   │       ├── security/
    │   │       └── user/
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/
    └── test/
        └── java/com/bookkeeping/
```

---

## Notes

- All entities extend `BaseEntity` for consistent fields
- DTOs are Java Records for immutability
- Tests use H2 in-memory database for isolation
- Each gate must pass before proceeding to next phase
- Commit after each successful gate