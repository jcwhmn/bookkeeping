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
| Mapping | MapStruct | 1.6.3 |
| Boilerplate | Lombok | 1.18.38 |
| API Docs | SpringDoc OpenAPI | 2.8.8 |
| Frontend | Nuxt 4 + Vue 3 + Vuetify 3 | latest |
| Charts | ECharts / vue-echarts | 5.x |
| i18n | @nuxtjs/i18n | 9.x |

## Prerequisites

- Java 25
- PostgreSQL 17+ running on `localhost:5432`
- Database `bookkeeping` created: `CREATE DATABASE bookkeeping;`
- Gradle 9.3 (or use `gradle wrapper`)

## Quick Start

```bash
# Backend
cd bookkeeping/backend
gradle bootRun
# → http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html

# Frontend
cd bookkeeping/frontend
npm install
npm run dev
# → http://localhost:3000
```

## Test Account

After Flyway runs (auto on boot), login with:
- **Username**: `demo`
- **Password**: `demo123`

## Project Structure

```
bookkeeping/
├── backend/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradle.properties
│   └── src/main/
│       ├── java/com/bookkeeping/
│       │   ├── BookkeepingApplication.java
│       │   ├── config/          # CORS, JWT entry point
│       │   ├── common/          # ApiResponse, ResultCode, enums
│       │   ├── controller/      # 8 REST controllers
│       │   ├── dto/             # Request/Response DTOs
│       │   ├── entity/          # 8 JPA entities
│       │   ├── exception/       # BusinessException, GlobalExceptionHandler
│       │   ├── repository/      # 7 Spring Data repositories
│       │   ├── security/        # JWT provider, filter, config
│       │   └── service/         # Business logic (4 interfaces + 4 impls)
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           └── db/migration/
│               ├── V1__init.sql        # Schema
│               └── V2__test_data.sql   # Test data
└── frontend/
    ├── nuxt.config.ts
    ├── pages/          # File-based routing (9 pages)
    ├── layouts/        # default.vue, empty.vue
    ├── stores/         # Pinia (auth.ts)
    ├── composables/    # useApi.ts
    ├── middleware/      # auth.ts
    ├── plugins/        # vuetify.ts, echarts.ts
    └── i18n/           # zh-CN.json, en-US.json
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

# Compile
gradle compileJava

# Full build (skip tests)
gradle build -x test

# Run
gradle bootRun

# Create new Flyway migration
# Add file: src/main/resources/db/migration/V<N>__<description>.sql
```

## Key Design Decisions

1. **Amount as BIGINT**: stored in cents/fen to avoid floating point
2. **Unix timestamps**: all times stored as BIGINT Unix seconds
3. **Soft delete**: `@SQLDelete` style, not physical deletion
4. **Transfer as two records**: TRANSFER_OUT + TRANSFER_IN linked by `related_id`
5. **Cursor pagination**: `transaction_time` for forward/backward pagination
6. **Standard response envelope**: `{success, result, errorCode, errorMessage}`
7. **Entity/DTO mapping**: MapStruct interfaces
8. **Lombok**: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` on all entities/DTOs
9. **Nuxt auto-imports**: Leverage Nuxt's auto-import for components, composables, `useFetch`/`$fetch`
10. **Password hashing**: MD5(salt + password) — following original ezBookkeeping design
