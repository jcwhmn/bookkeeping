# Category Icon and Color Specification

This specification defines icon and color support for categories.

## ADDED Requirements

### Requirement: Category icon storage

The system SHALL store an icon identifier for each category.

#### Scenario: Create category with icon
- **WHEN** user creates a category with `"icon": "mdi-cash"`
- **THEN** system SHALL store the icon value
- **AND** icon SHALL be returned in all category responses

#### Scenario: Create category without icon
- **WHEN** user creates a category without specifying icon
- **THEN** system SHALL store NULL for icon
- **AND** icon SHALL be returned as null in responses

#### Scenario: Update category icon
- **WHEN** user updates a category with `"icon": "mdi-food-apple"`
- **THEN** system SHALL update the icon value
- **AND** updated icon SHALL be returned in response

### Requirement: Category color storage

The system SHALL store a hex color code for each category.

#### Scenario: Create category with valid color
- **WHEN** user creates a category with `"color": "#FF5722"`
- **THEN** system SHALL store the color value
- **AND** color SHALL be returned in all category responses

#### Scenario: Create category with invalid color format
- **WHEN** user creates a category with `"color": "red"` (invalid format)
- **THEN** system SHALL return 400 validation error
- **AND** error message SHALL indicate color must be 6-character hex with # prefix

#### Scenario: Create category without color
- **WHEN** user creates a category without specifying color
- **THEN** system SHALL store NULL for color
- **AND** color SHALL be returned as null in responses

#### Scenario: Update category color
- **WHEN** user updates a category with `"color": "#4CAF50"`
- **THEN** system SHALL update the color value
- **AND** updated color SHALL be returned in response

### Requirement: Category comment storage

The system SHALL store a comment/description for each category.

#### Scenario: Create category with comment
- **WHEN** user creates a category with `"comment": "Monthly salary income"`
- **THEN** system SHALL store the comment value
- **AND** comment SHALL be returned in all category responses

#### Scenario: Create category without comment
- **WHEN** user creates a category without specifying comment
- **THEN** system SHALL store NULL for comment
- **AND** comment SHALL be returned as null in responses

#### Scenario: Comment length validation
- **WHEN** user creates a category with comment exceeding 255 characters
- **THEN** system SHALL return 400 validation error
