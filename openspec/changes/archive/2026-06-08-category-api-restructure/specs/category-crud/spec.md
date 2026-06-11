# Category CRUD Specification

This specification defines RESTful CRUD operations for categories.

## ADDED Requirements

### Requirement: List categories

The system SHALL return a list of all categories for the authenticated user.

#### Scenario: List all categories
- **WHEN** user sends GET request to `/api/v1/categories`
- **THEN** system SHALL return all non-hidden categories for the current user
- **AND** response SHALL include id, name, type, icon, color, comment, parentId, sortOrder, hidden

#### Scenario: List categories filtered by type
- **WHEN** user sends GET request to `/api/v1/categories?type=1`
- **THEN** system SHALL return only categories with matching type
- **AND** type 1 = INCOME, 2 = EXPENSE, 3 = TRANSFER

### Requirement: Search categories by name

The system SHALL allow searching categories by name with optional type filter.

#### Scenario: Search with name only
- **WHEN** user sends GET request to `/api/v1/categories/search?name=sal`
- **THEN** system SHALL return categories where name contains "sal" (case-insensitive)

#### Scenario: Search with name and type
- **WHEN** user sends GET request to `/api/v1/categories/search?name=gro&type=2`
- **THEN** system SHALL return categories where name contains "gro" AND type equals 2 (EXPENSE)

### Requirement: Get single category

The system SHALL return a single category by ID.

#### Scenario: Get existing category
- **WHEN** user sends GET request to `/api/v1/categories/{id}`
- **THEN** system SHALL return the category with matching ID
- **AND** category SHALL belong to the current user

#### Scenario: Get non-existent category
- **WHEN** user sends GET request to `/api/v1/categories/99999`
- **THEN** system SHALL return 404 error with Category NOT_FOUND error code

### Requirement: Create category

The system SHALL create a new category for the authenticated user.

#### Scenario: Create category with valid data
- **WHEN** user sends POST request to `/api/v1/categories` with valid category data
- **THEN** system SHALL create the category
- **AND** system SHALL return 201 Created with the created category

#### Scenario: Create category with duplicate name
- **WHEN** user sends POST request to `/api/v1/categories` with a name that already exists
- **THEN** system SHALL return 400 error with Category ALREADY_EXISTS error code

#### Scenario: Create category with invalid type
- **WHEN** user sends POST request to `/api/v1/categories` with type not in [1, 2, 3]
- **THEN** system SHALL return 400 error with validation error

### Requirement: Batch create categories

The system SHALL create multiple categories in a single request.

#### Scenario: Batch create with valid data
- **WHEN** user sends POST request to `/api/v1/categories` with array of categories
- **THEN** system SHALL create all categories
- **AND** system SHALL return 201 Created with array of created categories

### Requirement: Update category

The system SHALL update an existing category.

#### Scenario: Update category with valid data
- **WHEN** user sends PUT request to `/api/v1/categories/{id}` with updated data
- **THEN** system SHALL update the category
- **AND** system SHALL return 200 OK with the updated category

#### Scenario: Update non-existent category
- **WHEN** user sends PUT request to `/api/v1/categories/99999`
- **THEN** system SHALL return 404 error

### Requirement: Hide/unhide category

The system SHALL allow hiding and unhiding categories.

#### Scenario: Hide category
- **WHEN** user sends PATCH request to `/api/v1/categories/{id}/hidden` with `{"hidden": true}`
- **THEN** system SHALL mark the category as hidden
- **AND** system SHALL return 200 OK

#### Scenario: Unhide category
- **WHEN** user sends PATCH request to `/api/v1/categories/{id}/hidden` with `{"hidden": false}`
- **THEN** system SHALL mark the category as not hidden
- **AND** system SHALL return 200 OK

### Requirement: Reorder categories

The system SHALL allow reordering all categories in a single operation.

#### Scenario: Reorder categories
- **WHEN** user sends PUT request to `/api/v1/categories/reorder` with `{"categoryIds": [3, 1, 2, 5, 4]}`
- **THEN** system SHALL update sortOrder for all listed categories
- **AND** first category in array SHALL have sortOrder=0, second=1, etc.
- **AND** system SHALL return 200 OK

## REMOVED Requirements

### Requirement: Legacy endpoint list.json

**Reason**: Replaced by RESTful `/api/v1/categories` GET endpoint

**Migration**: Use `GET /api/v1/categories`

### Requirement: Legacy endpoint add.json

**Reason**: Replaced by RESTful `/api/v1/categories` POST endpoint

**Migration**: Use `POST /api/v1/categories`

### Requirement: Legacy endpoint modify.json

**Reason**: Replaced by RESTful `/api/v1/categories/{id}` PUT endpoint

**Migration**: Use `PUT /api/v1/categories/{id}`

### Requirement: Legacy endpoint hide.json

**Reason**: Replaced by RESTful `/api/v1/categories/{id}/hidden` PATCH endpoint

**Migration**: Use `PATCH /api/v1/categories/{id}/hidden`

### Requirement: Legacy endpoint move.json

**Reason**: Replaced by RESTful `/api/v1/categories/reorder` PUT endpoint

**Migration**: Use `PUT /api/v1/categories/reorder`

### Requirement: Legacy /transaction prefix

**Reason**: Removed from path structure

**Migration**: Use `/api/v1/categories` instead of `/api/v1/transaction/categories`
