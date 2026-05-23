# Bookkeeping System — Development Guide

## Design Tool

| Tool | Purpose | URL |
|------|---------|-----|
| **Open Design** | AI-powered page design agent — generates design specs with wireframes, components, API contracts | https://open-design.ai/ |

### How to Use Open Design

Open Design is an AI agent that designs pages using skills. For this project:

**1. Open Open Design Desktop** → https://open-design.ai/

**2. Select the right skill** for each page type:

| Page Type | Skill | Trigger Words |
|-----------|-------|---------------|
| Dashboard / Analytics | `dashboard` | "dashboard", "admin", "analytics" |
| Mobile Screen | `mobile-app` | "mobile app", "phone screen" |
| General Web Page | `web-prototype` | "prototype", "mockup", "landing" |
| Hand-drawn Wireframe | `wireframe-sketch` | "wireframe", "sketch", "lo-fi" |

**3. Design each page with a prompt like:**

```
Design a bookkeeping transactions list page with:
- Fixed sidebar navigation (Dashboard, Accounts, Categories, Transactions, Profile)
- Transaction list showing: date, description, amount, account, category
- Filter bar: date range, account dropdown, category dropdown
- Add Transaction button
- Month/year picker in header
```

**4. Get the design** — Open Design generates an HTML mockup + design tokens

**5. Implement** — Backend API first, then frontend matching the design

### Project Design Specs Location

All Open Design outputs should be saved to:
```
docs/design/pages/
├── 01-login.md         # Already designed (v0.1)
├── 02-dashboard.md    # Already designed (v0.1)
├── 03-accounts.md      # Already designed (v0.1)
├── 04-transaction-form.md
├── 05-transactions-list.md
├── 06-categories.md
├── 07-transactions-edit.md     # Season 2 - design needed
├── 08-transactions-transfer.md # Season 2 - design needed
├── 09-tags.md                  # Season 2 - design needed
└── ... (more Season 2 pages)
```

### Season 2 Pages to Design (in priority order)

| # | Page | Skill | Priority |
|---|------|-------|----------|
| 1 | Transaction Edit/Delete | `dashboard` | P0 |
| 2 | Transaction Date Picker | `dashboard` | P0 |
| 3 | Transfer Support | `dashboard` | P0 |
| 4 | Month Navigation | `dashboard` | P0 |
| 5 | Transaction Search/Filter | `dashboard` | P1 |
| 6 | Tags Management | `dashboard` | P2 |
| 7 | Statistics/Charts | `dashboard` | P2 |
| 8 | Budgets Page | `dashboard` | P2 |
| 9 | Reports Page | `dashboard` | P2 |
| 10 | Settings Page | `dashboard` | P2 |

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Backend | Spring Boot | 4.0.6 |
| Build | Gradle (Kotlin DSL) | 9.3 |
| Java | OpenJDK | 25 |
| ORM | Spring Data JPA / Hibernate | 6.x |
| Database | PostgreSQL | 17+ |
| Migration | Flyway | 11.4.1 |
| Auth | JJWT | 0.12.6 |
| Boilerplate | Lombok | 1.18.38 |
| API Docs | SpringDoc OpenAPI | 2.8.8 |
| Caching | Caffeine | 3.x |
| Frontend | Nuxt 4 + Vue 3 + Vuetify 3 | latest |
| Charts | ECharts / vue-echarts | 5.x |
| i18n | @nuxtjs/i18n | 9.x |

## Prerequisites

- Java 25
- Docker + Docker Compose installed
- PostgreSQL container started: `scripts/start-db.sh`
- 3 databases created automatically:
  - `bookkeeping` (production)
  - `bookkeeping_dev` (development)
  - `bookkeeping_test` (testing)

## Database Connection

| Property | Value |
|----------|-------|
| Host | localhost:5432 |
| User | bookkeeping |
| Password | test123 |
| Driver | PostgreSQL 18 |

## Quick Start

```bash
# 1. Start PostgreSQL (creates 3 databases)
cd bookkeeping
./scripts/start-db.sh        # Linux/Mac
scripts\start-db.bat        # Windows

# 2. Run application (development profile)
cd backend
gradlew bootRun
# → http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html

# 3. Run tests
gradlew test                 # Uses bookkeeping_test database
```

## Test Account

After Flyway runs (auto on boot), login with:
- **Username**: `demo`
- **Password**: `demo123`

## Implementation Phases

The implementation is divided into **7 phases** with **gates** between each phase:

| Phase | Gate | Description | Key Files |
|-------|------|-------------|-----------|
| 0 | G0 | Spring Boot Starter | build.gradle.kts, BookkeepingApplication.java |
| 1 | G1 | Common Infrastructure | BaseEntity, ApiResponse, ResultCode, enums |
| 2 | G2 | OpenAPI Support | OpenApiConfig.java, Swagger UI |
| 3 | G3 | Spring Security | SecurityConfig, JWT filter |
| 4 | G4 | User Module | User entity, repository, service, controller |
| 5 | G5 | Authentication | Login, register, JWT tokens |
| 6 | G6 | Account Module | Account CRUD |
| 7 | G7 | Final Verification | Full integration test |

See [Implementation Plan](docs/superpowers/plans/2026-05-19-bookkeeping-v1-implementation.md) for details.

## Project Structure

```
bookkeeping/
├── backend/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradle.properties
│   └── src/
│       ├── main/
│       │   ├── java/com/bookkeeping/
│       │   │   ├── BookkeepingApplication.java
│       │   │   ├── common/           # BaseEntity, ApiResponse, ResultCode, enums
│       │   │   ├── exception/         # BusinessException, GlobalExceptionHandler
│       │   │   ├── infrastructure/   # Config (Security, OpenAPI, Cache)
│       │   │   ├── supporting/        # Auth, Security, User modules
│       │   │   └── core/              # Account, Category, Transaction (v2+)
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/
│       └── test/
│           └── java/com/bookkeeping/
└── frontend/                          # (future phase)
```

## API Conventions

- Response envelope: `{ "success": true|false, "result": {...}, "errorCode": 200001, "errorMessage": "..." }`
- Error codes: `category * 100000 + subCategory * 1000 + index`
- Auth: `Authorization: Bearer <jwt_token>`
- Amounts: stored as BIGINT (cents/fen), frontend displays divide by 100
- Timestamps: Unix epoch seconds (BIGINT)
- Soft delete: `deleted` flag + `deleted_unix_time`
- Transfer: two records (TRANSFER_OUT type=4 + TRANSFER_IN type=5) linked by `related_id`

## Transaction Types

| Frontend | DB Type | DB Value | Description |
|----------|---------|----------|-------------|
| Modify Balance | MODIFY_BALANCE | 1 | Direct balance adjustment |
| Income | INCOME | 2 | Positive amount |
| Expense | EXPENSE | 3 | Negative amount |
| Transfer | TRANSFER_OUT | 4 | Money leaving source |
| Transfer | TRANSFER_IN | 5 | Money entering destination |

## Build Commands

```bash
cd backend

# Phase 0: Verify starter compiles
gradle compileJava

# Phase 1+: Run tests after each phase
gradle test                    # Unit tests
gradle integrationTest         # Integration tests
gradle allTestsReport          # Combined report

# Full build
gradle clean build

# Run application
gradle bootRun

# Create new Flyway migration
# Add file: src/main/resources/db/migration/V<N>__<description>.sql
```

## Test Structure

### Unit Tests
Location: `src/test/java/`
```bash
gradlew test
```

### Integration Tests
Location: `src/integrationTest/java/`
```bash
gradle integrationTest
```

**Note:** Integration tests require running PostgreSQL container. Run `./scripts/start-db.sh` first.

## Key Design Decisions

1. **Amount as BIGINT**: stored in cents/fen to avoid floating point
2. **Unix timestamps**: all times stored as BIGINT Unix seconds
3. **Soft delete**: `deleted` flag + `deleted_unix_time`
4. **Transfer as two records**: TRANSFER_OUT + TRANSFER_IN linked by `related_id`
5. **Cursor pagination**: `transaction_time` for forward/backward pagination
6. **Standard response envelope**: `{isSuccess, result, errorCode, errorMessage}`
7. **DTOs as Java Records**: Immutable, no setters, no hierarchical structure
8. **Lombok on Entities**: Use `@Getter @Setter` ONLY - NEVER use `@Data` on JPA entities (breaks equals/hashCode with Hibernate proxies)
9. **Lombok on DTOs**: Records auto-generate constructors, getters; use `@Builder` for complex DTOs
10. **DTO Mapping**: Use `MapStructPlus` from `F:/code/mapstruct_plus` for entity↔DTO conversion
11. **Flat package structure**: `supporting/user/`, `core/account/` (no deep nesting)
12. **BCrypt password hashing**: Using Spring Security's BCryptPasswordEncoder

## Coding Rules

### Lombok Usage

```java
// ✅ Entities - Use @Builder(toBuilder = true) + @Getter + @AllArgsConstructor
// NEVER use @Setter, @Data, or public constructors on entities
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    private String username;
}

// ✅ Creating entities (services/test fixtures)
User user = User.builder()
        .username("demo")
        .email("demo@example.com")
        .build();

// ✅ Updating entities — use toBuilder() instead of setters
user = user.toBuilder()
        .nickname("New Nick")
        .language("zh-CN")
        .build();
userRepository.save(user);

// ✅ Setting entity ID in tests only — use withId()
User testUser = User.builder().username("test").build().withId(1L);
// withId() is the ONLY setter — restricted to test fixtures and JPA hydration

// ❌ NEVER use @Setter on entities
// ❌ NEVER use @Data on JPA entities (breaks Hibernate proxy equals/hashCode)
// ❌ NEVER call setters in production code

// ✅ DTOs - Use Java Records with @MapperAuto
@MapperAuto(sourceEntity = User.class, direction = Direction.From)
public record UserDto(Long id, String username, String email) {}
```

### Why No Setters on Entities?

All entities use `@Builder(toBuilder = true)` with `@NoArgsConstructor(access = PROTECTED)`:\n- **Creation**: `Entity.builder().field(value)...build()` — single expression, all fields visible\n- **Update**: `entity.toBuilder().field(newValue).build()` — immutable-like update via copy\n- **JPA**: Protected no-arg constructor satisfies Hibernate proxy requirements\n- **Test IDs**: `entity.withId(1L)` — only way to set id, clearly distinguished from production code\n- **No public setters**: prevents scattered mutation, ensures all field changes go through builder

### Java Records for DTOs

```java
// ✅ Simple DTO
public record LoginRequest(String username, String password) {}

// ✅ DTO with validation
public record CreateAccountRequest(
    @NotBlank String name,
    @NotNull AccountType type
) {}

// ✅ Response DTO
public record AccountResponse(
    Long id,
    String name,
    AccountType type,
    String balanceStr
) {}
```

### MapStructPlus for Entity↔DTO Mapping

We use a custom annotation processor `MapStructPlus` for entity↔DTO conversion.

**Repository**: `F:/code/mapstruct_plus`

#### Pattern: @MapperAuto on DTO (Preferred)

Place `@MapperAuto` directly on the DTO record. The processor auto-generates both the Mapper interface and the Converter implementation.

```java
// 1. Annotate the DTO record
@MapperAuto(sourceEntity = User.class, direction = Direction.From)
public record UserDto(Long id, String username, String email, ...) {}

// 2. Processor generates two files:
//    a) UserMapper.java — interface with toDto(User) method
//    b) UserDtoMapperConverter.java — @Component implementing UserMapper

// 3. Inject and use in services
@Service
public class UserService {
    private final UserMapper userMapper;  // Auto-injected via @Component
    
    public UserDto getUser(User user) {
        return userMapper.toDto(user);
    }
}
```

**Converter Naming**: `{DtoName}MapperConverter` (e.g., `UserDtoMapperConverter`, `AccountDtoMapperConverter`)

**Currently Applied To**: UserDto, AccountDto, CategoryDto, TransactionDto, TagDto (5 DTOs)

**Build MapStructPlus**:
```bash
cd F:/code/mapstruct_plus
./gradlew build publishToMavenLocal
```

## ADR References

See `docs/superpowers/adr/` for design decisions:
- ADR-001: Monolith Architecture
- ADR-002: Package Structure (Flat/DDD)
- ADR-003: DTO Mapping Strategy
- ADR-004: Database Design
- ADR-005: API Design
- ADR-006: Version Strategy
- ADR-007: Base Patterns
- ADR-008: Backend Dependencies