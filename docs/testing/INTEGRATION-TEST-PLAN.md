# Integration Test Implementation Plan

> Generated: 2026-06-01

## Priority Groups

### Group A: Critical (High Impact, Easy to Test)
Essential APIs that every user touches. Priority: **IMMEDIATE**

| Priority | Category | APIs | Effort |
|----------|----------|------|--------|
| P0 | **User Profile** | 16, 17, 18, 19, 20 | Low |
| P0 | **Accounts** | 37-44 (missing 38, 40-42) | Low |
| P0 | **Categories** | 69-76 | Low |
| P0 | **Tags** | 83-90, 77-82 | Low |

### Group B: Important (Core Features)
APIs that power core bookkeeping workflows. Priority: **NEXT SPRINT**

| Priority | Category | APIs | Effort |
|----------|----------|------|--------|
| P1 | **Transactions** | 45-68 (missing read ops, batch ops) | Medium |
| P1 | **Transaction Pictures** | 116, 117 | Medium |
| P1 | **Templates** | 91-97 | Medium |

### Group C: Nice to Have (Advanced Features)
APIs for advanced use cases. Priority: **FUTURE**

| Priority | Category | APIs | Effort |
|----------|----------|------|--------|
| P2 | **Data Management** | 110-115 | Medium |
| P2 | **Insights** | 98-104 | Medium |
| P2 | **Scheduled Transactions** | 118-124 | Medium |
| P2 | **Tokens** | 31-36 | Medium |
| P2 | **Exchange Rates** | 106-108 | Medium |

### Group D: Low Priority
Edge cases and less commonly used APIs. Priority: **BACKLOG**

| Priority | Category | APIs | Effort |
|----------|----------|------|--------|
| P3 | **2FA** | 26-30 | Medium |
| P3 | **OAuth2** | 9-11, 21-22 | Medium |
| P3 | **LLM** | 105 | Low |
| P3 | **MCP** | 109 | Low |
| P3 | **Auth (password reset)** | 14-15, 12-13 | Medium |

---

## Implementation Schedule

### Phase 1: User & Account APIs (This Session)
**Goal**: Test all User and Account endpoints at controller level

```
Files to create/modify:
├── src/integrationTest/java/com/bookkeeping/supporting/user/
│   └── UserControllerIntegrationTest.java    [ADD tests]
├── src/integrationTest/java/com/bookkeeping/core/account/
│   └── AccountControllerIntegrationTest.java [CREATE]
├── src/integrationTest/java/com/bookkeeping/core/category/
│   └── CategoryControllerIntegrationTest.java [CREATE]
└── src/integrationTest/java/com/bookkeeping/core/tag/
    └── TagControllerIntegrationTest.java     [CREATE]
```

### Phase 2: Transaction APIs (Next Session)
**Goal**: Test all Transaction endpoints at controller level

```
Files to create/modify:
├── src/integrationTest/java/com/bookkeeping/core/transaction/
│   └── TransactionControllerIntegrationTest.java  [CREATE]
├── src/integrationTest/java/com/bookkeeping/core/transaction/
│   └── TransactionBatchControllerIntegrationTest.java [CREATE]
└── src/integrationTest/java/com/bookkeeping/core/transaction/
    └── TransactionPictureControllerIntegrationTest.java [CREATE]
```

### Phase 3: Template, Insights, Data (Future Sessions)
**Goal**: Test Templates, Insights, Data Management APIs

### Phase 4: Advanced Features (Backlog)
**Goal**: Test 2FA, OAuth2, Tokens, Exchange Rates, MCP

---

## Test Naming Convention

```
{Controller}ControllerIntegrationTest.java

Test methods follow pattern:
{action}_{scenario}_{expectedResult}

Examples:
- getProfile_withValidToken_returnsUserProfile
- updateProfile_withInvalidCurrency_returnsError
- listAccounts_withNoAccounts_returnsEmptyList
- addAccount_withDuplicateName_returnsError
```

---

## Test Structure Template

```java
package com.bookkeeping.{module};

import com.bookkeeping.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class {Controller}ControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private {Service} {service};

    @Autowired
    private {Repository} {repository};

    // Helper methods
    private String getAuthToken() { ... }
    private void setupTestData() { ... }

    // === CRUD Tests ===

    @Test
    void list_{entity}_returnsList() throws Exception {
        // Setup
        createTestData();
        
        // Execute & Assert
        mockMvc.perform(get("/api/v1/{entity}/list.json")
                .header("Authorization", "Bearer " + getAuthToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void get_{entity}_withValidId_returnsEntity() throws Exception {
        // Setup
        var entity = createTestEntity();
        
        // Execute & Assert
        mockMvc.perform(get("/api/v1/{entity}/get.json")
                .param("id", entity.getId().toString())
                .header("Authorization", "Bearer " + getAuthToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.id").value(entity.getId()));
    }

    @Test
    void add_{entity}_withValidData_returnsCreated() throws Exception {
        // Execute & Assert
        mockMvc.perform(post("/api/v1/{entity}/add.json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"type\":\"CASH\"}")
                .header("Authorization", "Bearer " + getAuthToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.name").value("Test"));
    }

    // === Error Cases ===

    @Test
    void get_{entity}_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/{entity}/get.json")
                .param("id", "99999")
                .header("Authorization", "Bearer " + getAuthToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void add_{entity}_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/{entity}/add.json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }
}
```

---

## Authentication Helpers

Each integration test class should have:

```java
private String getAuthToken() {
    // Login and get token from /api/authorize.json
    // Cache token to avoid repeated logins
}
```

Or use Spring Security test support:

```java
@Test
@WithMockUser(username = "testuser")
void listAccounts_withAuth_returnsList() throws Exception {
    mockMvc.perform(get("/api/v1/accounts/list.json"))
            .andExpect(status().isOk());
}
```

---

## Next Steps

1. **Create UserControllerIntegrationTest.java** — 15 APIs
2. **Create AccountControllerIntegrationTest.java** — 8 APIs
3. **Create CategoryControllerIntegrationTest.java** — 8 APIs
4. **Create TagControllerIntegrationTest.java** — 14 APIs
5. **Create TransactionControllerIntegrationTest.java** — 24 APIs
6. Continue with remaining categories...