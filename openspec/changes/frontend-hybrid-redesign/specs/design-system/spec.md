# Design System Specification

This specification defines the visual design system for the bookkeeping frontend, including colors, typography, spacing, and component styling.

## ADDED Requirements

### Requirement: Color Palette

The system SHALL provide a cohesive color palette based on professional fintech aesthetics.

#### Scenario: Primary Colors Render Correctly
- **WHEN** a component uses primary color tokens
- **THEN** it SHALL render with indigo `#533afd` as the primary, `#4434d4` for hover states, and `#2e2b8c` for pressed states

#### Scenario: Canvas Background Colors
- **WHEN** a page loads with default background
- **THEN** it SHALL use white `#ffffff` for main canvas and soft off-white `#f6f9fc` for secondary surfaces

#### Scenario: Money/Financial Colors
- **WHEN** displaying financial amounts
- **THEN** income/positive values SHALL use green `#1aae39`
- **AND** expense/negative values SHALL use ruby `#ea2261`

#### Scenario: Text Color Hierarchy
- **WHEN** rendering text content
- **THEN** primary text SHALL use deep navy `#0d253d`
- **AND** secondary/muted text SHALL use `#64748d`

#### Scenario: Border/Divider Colors
- **WHEN** rendering borders or dividers
- **THEN** they SHALL use hairline color `#e3e8ee`

---

### Requirement: Typography System

The system SHALL provide consistent typography with tabular figures for financial data.

#### Scenario: Font Family
- **WHEN** rendering any text
- **THEN** it SHALL use 'Inter' as the primary font family with system fallbacks: `system-ui, -apple-system, sans-serif`

#### Scenario: Tabular Figures for Money
- **WHEN** rendering any monetary amounts
- **THEN** it SHALL enable tabular figures via `font-feature-settings: "tnum"` and `font-variant-numeric: tabular-nums`
- **AND** numbers SHALL align properly in columns regardless of digit count

#### Scenario: Type Scale
- **WHEN** rendering headings and body text
- **THEN** it SHALL use the following scale:
  - Display XL: 48px, weight 300, line-height 1.15
  - Heading LG: 22px, weight 300, line-height 1.1
  - Body LG: 16px, weight 400, line-height 1.4
  - Body MD: 15px, weight 400, line-height 1.4
  - Money/Numbers: 14px, weight 400, line-height 1.4, tabular
  - Caption: 13px, weight 400, line-height 1.4

---

### Requirement: Spacing System

The system SHALL provide consistent spacing values based on an 8px grid.

#### Scenario: Spacing Scale
- **WHEN** applying spacing between elements
- **THEN** it SHALL use the following scale:
  - XXS: 2px
  - XS: 4px
  - SM: 8px
  - MD: 12px
  - LG: 16px
  - XL: 24px
  - XXL: 32px
  - Huge: 64px

---

### Requirement: Border Radius

The system SHALL provide consistent border radius values.

#### Scenario: Component Radius
- **WHEN** applying border radius to components
- **THEN** it SHALL use:
  - XS: 4px (tags, table chrome)
  - SM: 6px (form inputs)
  - MD: 8px (compact cards)
  - LG: 12px (feature cards, main containers)
  - Pill: 9999px (all buttons - SIGNATURE)

---

### Requirement: Pill-Shaped Buttons

All buttons SHALL use pill-shaped styling as the default.

#### Scenario: Default Button Shape
- **WHEN** a VBtn is rendered without explicit rounding
- **THEN** it SHALL display with `border-radius: 9999px` (pill shape)

#### Scenario: Primary Button Styling
- **WHEN** a primary button is rendered
- **THEN** it SHALL have:
  - Background: `#533afd` (indigo)
  - Text: white
  - Pill shape
  - No border

#### Scenario: Secondary Button Styling
- **WHEN** a secondary/outlined button is rendered
- **THEN** it SHALL have:
  - Background: white
  - Border: 1px solid `#533afd`
  - Text: `#533afd`
  - Pill shape

---

### Requirement: Card Styling

Cards SHALL use Stripe-style appearance with hairline borders.

#### Scenario: Default Card Appearance
- **WHEN** a VCard is rendered
- **THEN** it SHALL have:
  - Background: white `#ffffff`
  - Border: 1px solid `#e3e8ee` (hairline)
  - Border-radius: 12px (LG)
  - Box-shadow: `0 1px 3px rgba(0, 55, 112, 0.08)`
  - No elevation (elevation: 0)

#### Scenario: Card Padding
- **WHEN** content is placed inside a card
- **THEN** it SHALL have consistent internal padding of 24px

---

### Requirement: Gradient Icon Circles

Icon containers SHALL support gradient backgrounds for visual interest.

#### Scenario: Gradient Icon Background
- **WHEN** an icon is wrapped in a gradient container
- **THEN** it SHALL use a gradient from purple to blue: `linear-gradient(135deg, #667eea, #764ba2)`

#### Scenario: Icon Circle Size
- **WHEN** rendering category/account icons
- **THEN** it SHALL be a 48px circular container with centered icon

---

### Requirement: Transaction Table Rows

Transaction lists SHALL use hairline-separated rows with tabular amounts.

#### Scenario: Table Row Borders
- **WHEN** rendering a transaction list item
- **THEN** it SHALL have `border-bottom: 1px solid #e3e8ee`

#### Scenario: Amount Column Alignment
- **WHEN** rendering monetary amounts in a table
- **THEN** it SHALL use tabular figures
- **AND** amounts SHALL be right-aligned
- **AND** positive amounts SHALL appear in green `#1aae39`
- **AND** negative amounts SHALL appear in ruby `#ea2261`

---

### Requirement: Navigation Sidebar

The system SHALL provide a collapsible sidebar navigation.

#### Scenario: Desktop Sidebar
- **WHEN** viewport width is >1024px
- **THEN** a fixed sidebar SHALL appear on the left
- **AND** expanded width SHALL be 240px
- **AND** collapsed width SHALL be 64px

#### Scenario: Active Nav Item
- **WHEN** a navigation item is the current route
- **THEN** it SHALL display with an indigo pill background `#533afd`
- **AND** text SHALL be white

#### Scenario: Mobile Navigation
- **WHEN** viewport width is <768px
- **THEN** sidebar SHALL be hidden by default
- **AND** a hamburger menu SHALL appear in the top bar
- **AND** tapping hamburger SHALL open a full-height drawer overlay

---

### Requirement: Form Inputs

Text inputs SHALL use Stripe-style appearance.

#### Scenario: Input Field Styling
- **WHEN** a text input is rendered
- **THEN** it SHALL have:
  - Background: white
  - Border: 1px solid `#e3e8ee`
  - Border-radius: 6px
  - Padding: 8px 12px
  - Focus state: border-color `#533afd` with subtle glow

#### Scenario: Input Focus State
- **WHEN** a text input receives focus
- **THEN** it SHALL display:
  - Border:1px solid `#533afd`
  - Box-shadow: `0 0 0 2px rgba(83, 58, 253, 0.2)`

---

### Requirement: Tag/Pill Badges

Tags and badges SHALL use pill styling with soft backgrounds.

#### Scenario: Category Tag
- **WHEN** a category tag is rendered
- **THEN** it SHALL have:
  - Background: soft indigo `#b9b9f9`
  - Text: darker indigo `#4434d4`
  - Border-radius: 9999px (pill)
  - Font-size: 10px, uppercase, letter-spacing 0.1px

---

### Requirement: CSS Custom Properties

Design tokens SHALL be exposed as CSS custom properties for flexibility.

#### Scenario: Available CSS Variables
- **WHEN** inspecting the computed styles
- **THEN** the following variables SHALL be available:
  - `--stripe-primary`, `--stripe-primary-deep`, `--stripe-primary-press`
  - `--stripe-canvas`, `--stripe-canvas-soft`
  - `--stripe-ink`, `--stripe-ink-mute`
  - `--stripe-positive`, `--stripe-negative`
  - `--stripe-hairline`
  - `--stripe-space-sm`, `--stripe-space-md`, `--stripe-space-lg`, `--stripe-space-xl`
  - `--stripe-radius-sm`, `--stripe-radius-md`, `--stripe-radius-lg`, `--stripe-radius-pill`