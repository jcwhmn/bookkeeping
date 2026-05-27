# Bookkeeping System — Development Guide

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

## Documentation Update Rule

> **IMPORTANT**: When adding new features or making updates, always update the documentation after confirming the changes.

### Required Documentation Updates

| File | Description | When to Update |
|------|-------------|----------------|
| `docs/USER-GUIDE.md` | User-facing documentation with API examples | Add/modify any API endpoint or feature |
| `docs/ADMIN-GUIDE.md` | Admin guide for deployment, config, troubleshooting | Add/modify configuration, deployment, security |
| `AGENTS.md` | Development guide (this file) | Add new patterns, conventions, or tech stack changes |
| `FRONTEND_PAGES.md` | Frontend page reference | Add new UI pages or modify existing ones |

### Update Checklist

After confirming any feature modification:
1. [ ] Identify affected API endpoints → Update `USER-GUIDE.md`
2. [ ] Check if configuration changes → Update `ADMIN-GUIDE.md`
3. [ ] Check if new patterns/conventions → Update `AGENTS.md`
4. [ ] Check frontend changes → Update `FRONTEND_PAGES.md`
5. [ ] Add/update related docs in `docs/` if needed
6. [ ] Commit all documentation changes with the feature

### Example: Adding a New API Endpoint

```markdown
// In USER-GUIDE.md, add:

### X.X New Feature

**接口**: `POST /api/v1/new-feature`

**请求示例**:
```json
{
  "name": "example",
  "value": 100
}
```

**响应示例**:
```json
{
  "success": true,
  "result": {
    "id": 1,
    "name": "example",
    "value": 100
  }
}
```

// In ADMIN-GUIDE.md, add to relevant section if config needed
```