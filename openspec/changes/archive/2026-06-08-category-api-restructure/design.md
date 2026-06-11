# Category API Restructure - Technical Design

## Context

The bookkeeping backend inherited ezBookkeeping's non-RESTful API patterns:
- `.json` suffixes on all endpoints
- `/transaction` prefix for category endpoints
- Action-based naming (`/add.json`, `/modify.json`, `/hide.json`, `/move.json`)

This inconsistency makes the API harder to consume and doesn't follow modern REST conventions.

## Goals / Non-Goals

**Goals:**
- Convert Category API to RESTful conventions
- Remove all `.json` suffixes
- Use proper HTTP methods (GET, POST, PUT, PATCH, DELETE)
- Add icon/color/comment fields to support UI enhancements
- Add TRANSFER category type
- No backward compatibility (break and fix all)

**Non-Goals:**
- Restructuring other APIs (accounts, tags, etc.)
- Frontend changes (handled separately in frontend-hybrid-redesign)
- Data migration for existing categories (defaults applied)

## Decisions

### 1. Endpoint Structure

**Decision:** Flat `/api/v1/categories` hierarchy

```
/api/v1/categories              - List, Create, Batch Create
/api/v1/categories/{id}         - Get, Update, Delete
/api/v1/categories/search       - Search by name
/api/v1/categories/reorder      - Batch reorder
/api/v1/categories/{id}/hidden - PATCH hide/unhide
```

**Rationale:** Simple, consistent, follows REST conventions. Nested resources not needed since categories are flat.

### 2. Batch Create with Array Body

**Decision:** POST `/api/v1/categories` accepts either single object or array

```java
@PostMapping
public ApiResponse<?> create(@RequestBody Object data) {
    if (data instanceof List) {
        return ApiResponse.success(categoryService.batchCreate((List)data));
    }
    return ApiResponse.success(categoryService.create((CategoryRequest)data));
}
```

**Rationale:** Clean API design. Single create vs batch create on same endpoint based on payload type.

### 3. Category ID in Response

**Decision:** Use `Long` for category IDs (not String)

```java
public record CategoryDto(Long id, String name, Integer type, ...) {}
```

**Rationale:** Consistent with other entities (User, Account). Frontend can convert to string if needed.

### 4. Color Field Format

**Decision:** Store as 6-character hex string with `#` prefix

```java
@Column(length = 7)
private String color;  // "#FF5722"
```

**Validation:** Regex pattern `^#[0-9A-Fa-f]{6}$`

**Rationale:** Standard hex color format, includes `#` for clarity, matches CSS conventions.

### 5. Icon Field Format

**Decision:** Store icon identifier as string

```java
@Column(length = 64)
private String icon;  // "mdi-cash", "mdi-food", etc.
```

**Rationale:** Supports Material Design Icons (MDI) naming convention. Frontend renders based on this string.

### 6. Category Type Mapping

**Decision:** Use integers in API, enum in backend

```java
// API Request: { "type": 1 }  // 1=INCOME, 2=EXPENSE, 3=TRANSFER
// Backend: CategoryType.INCOME
```

**Rationale:** Matches OpenAPI spec. Enum provides type safety internally.

### 7. Reorder Endpoint

**Decision:** `PUT /api/v1/categories/reorder` with category ID array

```json
{
  "categoryIds": [3, 1, 2, 5, 4]
}
```

**Rationale:** Frontend sends complete new order after drag-drop. Simple, atomic operation.

## Database Schema Changes

```sql
-- V14__category_enhancements.sql

ALTER TABLE categories ADD COLUMN IF NOT EXISTS icon VARCHAR(64);
ALTER TABLE categories ADD COLUMN IF NOT EXISTS color VARCHAR(7);
ALTER TABLE categories ADD COLUMN IF NOT EXISTS comment VARCHAR(255);

-- Add CHECK constraint for color format
ALTER TABLE categories ADD CONSTRAINT chk_color_format 
    CHECK (color IS NULL OR color ~ '^#[0-9A-Fa-f]{6}$');
```

## API Response Examples

### List Categories
```json
GET /api/v1/categories

{
  "success": true,
  "result": [
    {
      "id": 1,
      "name": "Salary",
      "type": 1,
      "icon": "mdi-cash",
      "color": "#4CAF50",
      "comment": "Monthly salary",
      "parentId": null,
      "sortOrder": 0,
      "hidden": false
    }
  ]
}
```

### Search Categories
```json
GET /api/v1/categories/search?name=sal&type=1

{
  "success": true,
  "result": [
    {
      "id": 1,
      "name": "Salary",
      "type": 1,
      ...
    }
  ]
}
```

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| Frontend breaks with new endpoints | Update frontend as part of same change |
| Existing data needs defaults | Migration provides NULL defaults |
| Type conversion errors | Validate integer type 1-3 |
| Color format validation | Frontend validates before send |

## Migration Plan

1. Create `V14__category_enhancements.sql` migration
2. Update `CategoryType` enum with TRANSFER
3. Update `Category` entity with new fields
4. Update `CategoryDto` with new fields
5. Rewrite `CategoryController` with new endpoints
6. Update `CategoryService` business logic
7. Update integration tests
8. Deploy backend
9. Update frontend to match new API
10. Verify end-to-end functionality

## Rollback Strategy

- Revert migration V14
- Revert controller/service/entity changes
- No data loss (new columns nullable)
