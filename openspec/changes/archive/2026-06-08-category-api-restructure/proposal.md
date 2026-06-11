# Category API Restructure Proposal

## Why

The current Category API uses non-RESTful naming conventions with `.json` suffixes and inconsistent endpoint patterns inherited from ezBookkeeping. This makes the API inconsistent with modern REST conventions and harder to consume.

## What Changes

### Endpoint Restructuring

| Old Endpoint | New Endpoint | Method | Description |
|-------------|--------------|--------|-------------|
| `/api/v1/transaction/categories/list.json` | `/api/v1/categories` | GET | List all categories |
| `/api/v1/transaction/categories/list.json?type=X` | `/api/v1/categories?type=X` | GET | List by type |
| `/api/v1/transaction/categories/search.json` | `/api/v1/categories/search` | GET | Search by name |
| `/api/v1/transaction/categories/get.json?id=X` | `/api/v1/categories/{id}` | GET | Get single category |
| `/api/v1/transaction/categories/add.json` | `/api/v1/categories` | POST | Create category |
| `/api/v1/transaction/categories/add_batch.json` | `/api/v1/categories` | POST | Batch create (array) |
| `/api/v1/transaction/categories/modify.json` | `/api/v1/categories/{id}` | PUT | Update category |
| `/api/v1/transaction/categories/hide.json` | `/api/v1/categories/{id}/hidden` | PATCH | Hide/unhide |
| `/api/v1/transaction/categories/move.json` | `/api/v1/categories/reorder` | PUT | Reorder categories |

### Breaking Changes

- **REMOVED**: All `.json` suffixes
- **REMOVED**: `/transaction` prefix from path
- **REMOVED**: `/get.json`, `/add.json`, `/modify.json`, `/hide.json`, `/move.json` suffixes
- **BREAKING**: No backward compatibility - all old endpoints removed

### Entity Enhancements

| Field | Type | Description | Notes |
|-------|------|-------------|-------|
| `icon` | String | Category icon identifier | New field |
| `color` | String (6-char hex) | Category color | New field, e.g., `#FF5722` |
| `comment` | String (255) | Category comment | New field |

### CategoryType Enum Enhancement

Add `TRANSFER` type:

```
INCOME("Income")     → type = 1
EXPENSE("Expense")   → type = 2
TRANSFER("Transfer") → type = 3  (NEW)
```

### Request/Response Format Changes

#### Create Category
```json
POST /api/v1/categories
{
  "name": "Salary",
  "type": 1,
  "icon": "mdi-cash",
  "color": "#4CAF50",
  "comment": "Monthly salary",
  "parentId": null
}
```

#### Batch Create Categories
```json
POST /api/v1/categories
[
  {
    "name": "Groceries",
    "type": 2,
    "icon": "mdi-cart",
    "color": "#FF5722",
    "comment": "Food shopping",
    "parentId": null
  },
  {
    "name": "Vegetables",
    "type": 2,
    "icon": "mdi-food-apple",
    "color": "#4CAF50",
    "parentId": 1
  }
]
```

#### Update Category
```json
PUT /api/v1/categories/{id}
{
  "name": "Updated Name",
  "type": 2,
  "icon": "mdi-new-icon",
  "color": "#2196F3",
  "comment": "Updated comment"
}
```

#### Hide/Unhide Category
```json
PATCH /api/v1/categories/{id}/hidden
{
  "hidden": true
}
```

#### Reorder Categories
```json
PUT /api/v1/categories/reorder
{
  "categoryIds": [3, 1, 2, 5, 4]
}
```

## Capabilities

### New Capabilities

- **`category-icon-color`**: Store and retrieve category icon and color
- **`category-transfer-type`**: Support TRANSFER category type
- **`category-search`**: Search categories by name with optional type filter

### Modified Capabilities

- **`category-crud`**: Changed from non-RESTful to RESTful endpoints

## Impact

### Affected Backend Files
- `CategoryController.java` - Endpoint changes
- `Category.java` - Entity with new fields
- `CategoryDto.java` - DTO with new fields
- `CategoryService.java` - Business logic updates
- `CategoryRepository.java` - Query updates
- `CategoryType.java` - Add TRANSFER enum
- `V14__category_enhancements.sql` - New migration

### Affected Frontend Files
- `pages/categories.vue` - API endpoint updates
- Any components using category API

### Database Migration
- New migration `V14__category_enhancements.sql`
- Add columns: `icon`, `color`, `comment`

### Dependencies
- Frontend must be updated to match new endpoints
