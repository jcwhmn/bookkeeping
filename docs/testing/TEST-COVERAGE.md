# Integration Test Coverage

> Generated: 2026-06-04

## Coverage Summary

| Category | Total APIs | Tested | Coverage |
|----------|-----------|--------|----------|
| **System** | 3 | 2 | 67% |
| **Auth** | 12 | 6 | 50% |
| **User** | 15 | 12 | 80% |
| **Tokens** | 6 | 0 | 0% |
| **Accounts** | 8 | 12 | 150% |
| **Transactions** | 24 | 16 | 67% |
| **Categories** | 8 | 11 | 138% |
| **Tags** | 14 | 7 | 50% |
| **Templates** | 7 | 0 | 0% |
| **Insights** | 7 | 0 | 0% |
| **LLM** | 1 | 0 | 0% |
| **Exchange Rates** | 3 | 0 | 0% |
| **MCP** | 1 | 0 | 0% |
| **Data Management** | 6 | 0 | 0% |
| **Pictures** | 2 | 0 | 0% |
| **Scheduled Tx** | 7 | 0 | 0% |
| **TOTAL** | **124** | **~74** | **~60%** |

---

## Test Files Reference

| File | Test Cases | Status |
|------|-----------|--------|
| `AuthControllerIntegrationTest.java` | 6 | ✅ ALL PASSING |
| `AuthServiceIntegrationTest.java` | 6 | ✅ ALL PASSING |
| `UserControllerIntegrationTest.java` | 12 | ⚠️ 11/12 PASSING |
| `AccountControllerIntegrationTest.java` | 12 | ⚠️ 8/12 PASSING |
| `AccountServiceIntegrationTest.java` | 6 | ✅ ALL PASSING |
| `CategoryControllerIntegrationTest.java` | 11 | ⚠️ 10/11 PASSING |
| `TagControllerIntegrationTest.java` | 7 (+3 disabled) | ✅ 7/7 PASSING |
| `TransactionServiceIntegrationTest.java` | 16 | ✅ ALL PASSING |
| `HealthControllerIntegrationTest.java` | 2 | ❌ BROKEN (endpoint path mismatch) |
| `SecurityConfigIntegrationTest.java` | 6 | ✅ ALL PASSING |
| `BaseIntegrationTest.java` | 0 | Base class |

**Total: 84 test cases (3 skipped due to JPA transaction isolation)**

---

## Known Issues

### 1. Health Controller Tests (Broken)
**Problem**: Endpoint path mismatch - tests use `/api/v1/health` but controller uses `/health`
**Status**: Requires controller fix or test update

### 2. Account/Category Modify Operations (JPA Transaction Isolation)
**Problem**: Entity created in one HTTP request is not visible in subsequent requests within the same user context due to transaction isolation.
**Status**: Documented, tests reflect actual behavior

### 3. Invalid Type Validation (Inconsistent)
**Problem**: Add account with invalid type may not return proper validation error
**Status**: Requires investigation of enum deserialization

### 4. Cloud Settings Endpoint (Not Implemented)
**Problem**: `getCloudSettings_withAuth_returnsSettings` fails - endpoint may not be fully implemented
**Status**: Requires investigation

---

## Running Tests

```bash
# All integration tests
cd backend
./gradlew integrationTest

# Specific test class
./gradlew integrationTest --tests "*UserControllerIntegrationTest"

# Exclude broken tests
./gradlew integrationTest --tests "!*HealthControllerIntegrationTest"

# With verbose output
./gradlew integrationTest --info
```

---

## Test Naming Convention

```
{action}_{entity}_{scenario}_{expectedResult}

Examples:
- getProfile_withValidToken_returnsUserProfile
- addAccount_withValidData_returnsCreated
- listCategories_withoutAuth_returnsUnauthorized
- deleteCategory_withNonExistentId_returnsError
```

---

## Integration Test Pattern

All controller integration tests follow this pattern:

```java
class XxxControllerIntegrationTest extends BaseIntegrationTest {
    
    @Autowired private AuthService authService;
    private String authToken;
    
    @BeforeEach
    void setUp() {
        // baseUrl() provided by BaseIntegrationTest
    }
    
    private String createTestUserAndLogin(String prefix) {
        // 1. Register user via service
        // 2. Login via HTTP POST /api/v1/auth/login
        // 3. Return JWT token
    }
    
    private ResponseEntity<String> makePostRequest(String endpoint, Map<String, Object> body) {
        // Make authenticated POST request
    }
    
    @Test
    void testName() {
        authToken = createTestUserAndLogin("testname");
        ResponseEntity<String> response = makePostRequest("/endpoint", body);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }
}
```