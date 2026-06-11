# Category Transfer Type Specification

This specification defines TRANSFER category type support.

## ADDED Requirements

### Requirement: TRANSFER category type

The system SHALL support TRANSFER as a valid category type.

#### Scenario: Create TRANSFER category
- **WHEN** user creates a category with `"type": 3`
- **THEN** system SHALL create the category with type TRANSFER
- **AND** category SHALL be returned with `"type": 3`

#### Scenario: List TRANSFER categories
- **WHEN** user sends GET request to `/api/v1/categories?type=3`
- **THEN** system SHALL return only TRANSFER categories

#### Scenario: Search TRANSFER categories
- **WHEN** user sends GET request to `/api/v1/categories/search?name=transfer&type=3`
- **THEN** system SHALL return TRANSFER categories matching the name search

#### Scenario: Update category to TRANSFER
- **WHEN** user updates a category with `"type": 3`
- **THEN** system SHALL change the category type to TRANSFER

### Requirement: Category type enum

The system SHALL use the following type values:

| Type Value | Name | Description |
|------------|------|-------------|
| 1 | INCOME | Money coming in |
| 2 | EXPENSE | Money going out |
| 3 | TRANSFER | Money moving between accounts |

#### Scenario: All type values are valid
- **WHEN** user creates categories with types 1, 2, and 3
- **THEN** all categories SHALL be created successfully
