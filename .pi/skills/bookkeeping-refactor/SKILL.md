---
name: bookkeeping-refactor
description: Use when refactoring a Bookkeeping model to the canonical RESTful pattern. Applies to all entity modules that extend BaseEntity (Account, Tag, Transaction, etc.). Always use this skill before refactoring any service/controller/DTO/model to ensure the canonical pattern is followed and critical bugs (especially the toBuilder() inherited-id bug) are avoided.
---

# Bookkeeping Model Refactor Skill

The canonical pattern for refactoring a Bookkeeping model (e.g. Category, Account, Tag, etc.) to the standardized RESTful + DTO-mapped + bug-free style demonstrated in the Category module.

## When to Use

Use this skill whenever you are:
- Adding a new model (entity + DTO + service + controller)
- Refactoring an existing model to the canonical pattern
- Investigating duplicate-row or "update doesn't persist" bugs (see [Critical Bug: toBuilder() Loses Inherited id](#critical-bug-tobuilder-loses-inherited-id))
- Writing tests for a service or controller
- Verifying frontend ↔ backend API contract for a model

## The Canonical Refactor Pattern

### Step 1: Entity (e.g. `Category.java`)

**Rules:**
- `@Entity` + `@Table(name = "<plural_snake_case>")`
- `@Getter` ONLY (no `@Setter`, no `@Data`)
- `@Builder(toBuilder = true)` + `@NoArgsConstructor(access = PROTECTED)` + `@AllArgsConstructor`
- All `Boolean`/`Integer` fields with defaults use `@Builder.Default` on the FIELD
- **NO public setters** — all updates use `toBuilder()`
- Fields declared in BaseEntity (`id`, `createdAt`, `updatedAt`) are NOT duplicated here

**Template:**
```java
@Entity
@Table(name = "<table_name>")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Xxx extends BaseEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hidden = false;

    // ... other fields, NO setters
}
```

### Step 2: Enum Updates (if needed)

If adding a new enum value:
- Add the new constant with explicit integer value (matches OpenAPI/DB)
- Add `getValue()` and `fromValue(int)` methods for round-trip conversion
- Frontend gets the enum as JSON string (Jackson serializes by name)

```java
public enum CategoryType {
    INCOME("Income", 1),
    EXPENSE("Expense", 2),
    TRANSFER("Transfer", 3);

    private final String displayName;
    private final int value;

    public int getValue() { return value; }

    public static CategoryType fromValue(int value) {
        for (CategoryType t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
```

### Step 3: Database Migration (Flyway V<N>__<description>.sql)

```sql
-- V<N>: <description>
-- Add new columns (idempotent with IF NOT EXISTS)
ALTER TABLE <table> ADD COLUMN IF NOT EXISTS <col> <TYPE> <CONSTRAINTS>;

-- Add constraints (drop and re-create to be idempotent)
ALTER TABLE <table> DROP CONSTRAINT IF EXISTS <constraint_name>;
ALTER TABLE <table> ADD CONSTRAINT <constraint_name> CHECK (...);
```

### Step 4: DTO (record + `@MapperAuto`)

**Rules:**
- Use Java `record` (immutable)
- Annotate with `@MapperAuto(sourceEntity = Xxx.class, direction = Direction.From)`
- This auto-generates `XxxDtoMapperConverter` via MapStructPlus
- The DTO must include all fields that should be exposed in the response (id, userId, audit fields, etc.)

```java
@MapperAuto(sourceEntity = Xxx.class, direction = Direction.From)
public record XxxDto(
    Long id,
    String name,
    Long userId,
    Integer sortOrder,
    Boolean hidden,
    // ... other fields
) {}
```

### Step 5: Service Layer

**Rules:**
- `@Service` + `@RequiredArgsConstructor` (Lombok)
- Inject `Repository`, `Mapper`, `SecurityUtils` as `final` fields
- **CRITICAL**: Use `existing.applyUpdate(c -> c.toBuilder()...build())` instead of bare `toBuilder()` to preserve `id`
- Use per-user queries (e.g. `findByIdAndUserId`, not `findById`)
- Validate with `BusinessException(ResultCode.X, "message")` and specific error codes

**Template:**
```java
@Service
@RequiredArgsConstructor
public class XxxService {

    private final XxxRepository xxxRepository;
    private final XxxMapper xxxMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public XxxDto createXxx(XxxController.XxxCreateRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        // ... validation
        Xxx xxx = Xxx.builder()
                .name(request.name())
                .userId(userId)
                .build();
        return xxxMapper.toDto(xxxRepository.save(xxx));
    }

    @Transactional
    public XxxDto updateXxx(Long id, XxxController.XxxUpdateRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Xxx existing = xxxRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.XXX_NOT_FOUND, "Not found"));

        // CRITICAL: Use applyUpdate() to preserve id (which lives in BaseEntity)
        Xxx updated = existing.applyUpdate(e -> e.toBuilder()
                .name(request.name())
                .build());

        return xxxMapper.toDto(xxxRepository.save(updated));
    }
}
```

### Step 6: Controller (RESTful)

**Rules:**
- `@RestController` + `@RequestMapping("/api/v1/<plural>")` + `@RequiredArgsConstructor` (Lombok)
- Use REST verbs: `GET`/`POST`/`PUT`/`PATCH`/`DELETE` — **NO `.json` suffix**
- Return `ApiResponse<T>` (envelope already wrapped)
- For non-2xx status codes (e.g. 201 Created), use `@ResponseStatus`
- Define request DTOs as nested `record`s (e.g. `XxxCreateRequest`, `XxxUpdateRequest`)

**Template:**
```java
@RestController
@RequestMapping("/api/v1/xxxs")
@Tag(name = "Xxxs", description = "Xxx management APIs")
@RequiredArgsConstructor
public class XxxController {

    private final XxxService xxxService;

    @GetMapping
    @Operation(summary = "List xxxs")
    public ApiResponse<List<XxxDto>> list() {
        return ApiResponse.success(xxxService.getCurrentUserXxxs());
    }

    @PostMapping
    @Operation(summary = "Create xxx")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<XxxDto> create(@RequestBody XxxCreateRequest request) {
        return ApiResponse.success(xxxService.createXxx(request));
    }

    // Request DTOs as nested records
    public record XxxCreateRequest(String name, /* ... */) {}
}
```

### Step 7: Unit Tests (Service)

**Rules:**
- `@ExtendWith(MockitoExtension.class)`
- Mock `Repository`, `SecurityUtils` (NOT the mapper — use the real one!)
- Use the real `XxxDtoMapperConverter` to verify auto-generated mapping
- Use `@Nested` classes for organization
- Use `ArgumentCaptor` to verify entity state on save

**Template:**
```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {

    @Mock private XxxRepository xxxRepository;
    @Mock private SecurityUtils securityUtils;
    private final XxxMapper xxxMapper = new XxxDtoMapperConverter();  // real

    private XxxService xxxService;
    private User testUser;

    @BeforeEach
    void setUp() {
        xxxService = new XxxService(xxxRepository, xxxMapper, securityUtils);
        testUser = User.builder().username("test").build().withId(1L);
    }

    @Nested
    @DisplayName("createXxx")
    class CreateXxx {
        @Test
        @DisplayName("creates with all fields")
        void creates_with_all_fields() {
            when(securityUtils.requireCurrentUser()).thenReturn(testUser);
            when(xxxRepository.save(any(Xxx.class))).thenAnswer(inv -> inv.getArgument(0));

            XxxController.XxxCreateRequest req = new XxxController.XxxCreateRequest("name", ...);
            XxxDto result = xxxService.createXxx(req);

            assertNotNull(result);
            ArgumentCaptor<Xxx> captor = ArgumentCaptor.forClass(Xxx.class);
            verify(xxxRepository).save(captor.capture());
            assertEquals("name", captor.getValue().getName());
        }
    }
}
```

### Step 8: Integration Tests (Controller)

**Rules:**
- Extend `BaseIntegrationTest`
- Use `RestTemplate` with `HttpComponentsClientHttpRequestFactory` (Apache HttpClient5) for PATCH support
- Each test creates a unique user via `authService.register(...)`
- Use helpers for `makeGetRequest`, `makePostRequest`, `makePutRequest`, `makePatchRequest`

**Add dependency to `build.gradle.kts`:**
```kotlin
dependencies {
    "integrationTestImplementation"("org.apache.httpcomponents.client5:httpclient5")
}
```

**Template:**
```java
class XxxControllerIntegrationTest extends BaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Autowired private AuthService authService;
    private String authToken;

    XxxControllerIntegrationTest() {
        // Use Apache HttpClient (required for PATCH method support)
        this.restTemplate = new RestTemplate(
            new HttpComponentsClientHttpRequestFactory());
    }

    @BeforeEach
    void setUp() {
        String username = "test_" + (System.currentTimeMillis() % 100000);
        authService.register(new RegisterRequest(username, username + "@example.com", "password123"));
        authToken = login(username, "password123");
    }

    @Test
    void listXxxs_returnsOk() {
        ResponseEntity<String> response = makeGetRequest("/api/v1/xxxs");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"success\":true"));
    }
}
```

### Step 9: Frontend Integration

**Rules:**
- Use the canonical `useApi` composable (it unwraps the `ApiResponse` envelope)
- Type the result as `T` directly, NOT `{ result: T }`

**Correct (✅):**
```typescript
const res = await api.get<Category[]>('/categories')
categories.value = res || []
```

**Wrong (❌):**
```typescript
const res = await api.get<{ result: Category[] }>('/categories')
categories.value = res.result || []  // BUG: res is already Category[]
```

**Request body format:**
- Match the backend's request DTO field names exactly
- Use the same field types (string for enum, number for int, etc.)

---

## Critical Bug: toBuilder() Loses Inherited id

**This is the #1 bug to avoid in any entity refactor.**

### The Problem

Lombok's `@Builder(toBuilder = true)` generates a `toBuilder()` method that copies **only the fields declared in the current class**. Fields inherited from a parent class (like `BaseEntity.id`) are NOT included.

```java
@Entity
@Builder(toBuilder = true)
public class Category extends BaseEntity {
    private String name;  // ✅ copied by toBuilder()
    // ... other fields
    // id, createdAt, updatedAt are in BaseEntity — NOT copied!
}
```

When you do `entity.toBuilder().name("new").build()`:
- The new entity has `id = null`
- Hibernate sees a transient entity (no id) → **executes INSERT** instead of UPDATE
- Result: **Duplicate rows** instead of in-place updates

### Evidence (Real Bug Found in Category)

User reported: "I modified Shopping, but it remains no change; there are two Transportation items."

Investigation revealed: 50 categories in dev DB for userId=1, with duplicate names (Shopping×2, Transportation×2, etc.). The new entries (IDs 119-143) had the same names as the originals (IDs 1-13) but new IDs — classic INSERT behavior caused by the `toBuilder()` bug.

### The Fix

`BaseEntity.applyUpdate(Function)` is provided in `common/BaseEntity.java`:

```java
/**
 * Apply changes to a copy of this entity, preserving the id and createdAt
 * fields that Lombok's toBuilder() does not copy.
 */
public <T extends BaseEntity> T applyUpdate(Function<T, T> updater) {
    @SuppressWarnings("unchecked")
    T self = (T) this;
    T built = updater.apply(self);
    built.id = this.id;
    built.createdAt = this.createdAt;
    return built;
}
```

### Usage in Service Layer

**Always use `applyUpdate()` for entity updates:**

```java
// ✅ CORRECT — preserves id, performs UPDATE
Xxx updated = existing.applyUpdate(e -> e.toBuilder()
    .name("new name")
    .build());
xxxRepository.save(updated);

// ❌ WRONG — loses id, performs INSERT (creates duplicate!)
Xxx updated = existing.toBuilder()
    .name("new name")
    .build();
xxxRepository.save(updated);
```

### Verification

After refactoring, **always verify with a live test** that the update path does NOT create duplicates:

```bash
# Count categories before
curl -s -X GET "/api/v1/categories" -H "Authorization: Bearer $TOKEN" | grep -oP '"id":' | wc -l
# Should be N

# Update or reorder
curl -s -X PUT "/api/v1/categories/reorder" -d "..."

# Count after
curl -s -X GET "/api/v1/categories" -H "Authorization: Bearer $TOKEN" | grep -oP '"id":' | wc -l
# Should still be N (not N+something)
```

---

## Other Critical Patterns to Apply

### 1. No Setters on Entities

Per `AGENTS.md`, entities should NEVER have public setters. Use `toBuilder()` for all updates.

If you need a "with X" helper, prefer the canonical pattern:
```java
// In service
Xxx updated = existing.applyUpdate(e -> e.toBuilder().name(newName).build());
```

### 2. Service Constructor Pattern

Use Lombok's `@RequiredArgsConstructor` (NOT manual constructors):
```java
@Service
@RequiredArgsConstructor
public class XxxService {
    private final XxxRepository xxxRepository;
    private final XxxMapper xxxMapper;
    // ...
}
```

### 3. Request DTOs as Nested Records in Controller

```java
@RestController
public class XxxController {
    public record XxxCreateRequest(String name, /* ... */) {}
    public record XxxUpdateRequest(String name, /* ... */) {}
}
```

### 4. Per-User Authorization

Always filter by current user:
```java
Long userId = securityUtils.requireCurrentUser().getId();
Xxx xxx = xxxRepository.findByIdAndUserId(id, userId)
    .orElseThrow(() -> new BusinessException(ResultCode.XXX_NOT_FOUND, "Not found"));
```

### 5. DataInitializer Per-User Check

If seeding demo data, use **per-user** checks:
```java
// ❌ WRONG — counts ALL categories globally
if (categoryRepository.count() > 0) { return; }

// ✅ CORRECT — per-user
if (categoryRepository.findByUserIdOrderBySortOrderAsc(demoUser.getId()).size() > 0) { return; }
```

### 6. Frontend Response Unwrapping

`useApi` already unwraps the `ApiResponse` envelope — don't double-unwrap:
```typescript
// ❌ WRONG
const res = await api.get<{ result: T[] }>('/path')
return res.result

// ✅ CORRECT
const res = await api.get<T[]>('/path')
return res
```

---

## Pre-Refactor Checklist

Before starting, run these checks:

- [ ] Read the current entity, service, controller, DTO files
- [ ] Identify all `toBuilder()` usages in the service (each one is a potential bug)
- [ ] Check if the model has any `XxxRepository.@Modifying` queries (prefer `find` + `save` pattern)
- [ ] List frontend pages that call this model's API (verify they need updates)
- [ ] Note any tests that exist for the current model

## Post-Refactor Checklist

- [ ] Entity: No setters, no `@Data`, all `Boolean`/`Integer` with `@Builder.Default`
- [ ] Service: All updates use `applyUpdate()`, not bare `toBuilder()`
- [ ] Controller: RESTful paths (no `.json`), `@RequiredArgsConstructor`
- [ ] Unit tests pass (use real `XxxDtoMapperConverter`)
- [ ] Integration tests pass (with Apache HttpClient5 for PATCH)
- [ ] Live test: count entities before/after update — should be equal
- [ ] Frontend updated with new API paths and field names
- [ ] No regressions in dependent models (check what uses this model)

## Refactor Order (Dependency-Based)

When refactoring multiple models, do them in this order to minimize cascading changes:

1. **Token** — depends on nothing
2. **ExchangeRate** — depends on nothing
3. **InsightsExplorer** — depends on nothing
4. **TagGroup** — depends on nothing
5. **Tag** — depends on TagGroup
6. **Category** — already done ✅
7. **Account** — depends on nothing, but used by many
8. **Budget** — depends on Category
9. **TransactionTemplate** — depends on Category, Account, Tag
10. **TransactionPicture** — depends on Transaction
11. **ScheduledTransaction** — depends on Category, Account, Tag
12. **Transaction** — depends on Category, Account, Tag, TagGroup

---

## Quick Reference: Files to Create/Modify Per Refactor

For each model, the canonical file structure is:

```
src/main/java/com/bookkeeping/<package>/
├── Xxx.java                # Entity
├── XxxDto.java             # Response DTO (@MapperAuto on record)
├── XxxMapper.java          # (AUTO-GENERATED by MapStructPlus — don't write)
├── XxxRepository.java      # Data access
├── XxxService.java         # Business logic
└── XxxController.java      # REST API

src/test/java/com/bookkeeping/<package>/
└── XxxServiceTest.java     # Unit tests (mockito + real mapper)

src/integrationTest/java/com/bookkeeping/<package>/
└── XxxControllerIntegrationTest.java  # E2E tests (RestTemplate)
```

Database migration:
```
src/main/resources/db/migration/V<N>__<description>.sql
```

---

## Real-World Example: Category Refactor

The Category module was the first to be refactored. The PR included:

| File | Type | Change |
|------|------|--------|
| `V14__category_enhancements.sql` | NEW | Added `icon`, `color`, `comment` columns |
| `Category.java` | MODIFY | Added fields, removed setters |
| `CategoryType.java` | MODIFY | Added `TRANSFER` value, `getValue()`, `fromValue()` |
| `CategoryDto.java` | MODIFY | Added new fields, kept `@MapperAuto` |
| `CategoryController.java` | REWRITE | RESTful endpoints, `@RequiredArgsConstructor` |
| `CategoryService.java` | REWRITE | Cleaner methods, uses `applyUpdate()` |
| `CategoryControllerIntegrationTest.java` | REWRITE | RESTful paths, 17 tests |
| `CategoryServiceTest.java` | NEW | 28 unit tests |
| `DataInitializer.java` | FIX | Per-user check, seed for existing user |
| `pages/categories.vue` | REWRITE | New API, icon/color/comment UI |
| `useApi.ts` | MODIFY | Added `patch` method |
| `nuxt.config.ts` | FIX | Disabled PWA devOptions for workbox-build compat |

**Result:**
- 50+ duplicate categories in dev DB (data pollution) — **fixed**
- "Update doesn't persist" bug — **fixed**
- RESTful API design — **consistent**
- 100% test coverage (unit + integration) — **achieved**

---

## When In Doubt

If you're unsure whether a pattern is correct:
1. Read the Category module — it has the canonical pattern
2. Check `AGENTS.md` for project-wide conventions
3. Look at the latest refactor PRs for examples
4. Run the existing tests to verify your changes don't regress
