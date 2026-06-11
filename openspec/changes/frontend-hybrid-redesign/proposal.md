# Frontend Hybrid Redesign Proposal

## Why

The current bookkeeping frontend uses basic Vuetify Material Design styling with the default blue theme. While functional, it lacks the professional polish expected in financial applications. A **hybrid Stripe/Revolut redesign** will:

1. **Build trust** - Professional fintech aesthetics signal reliability for handling money
2. **Improve usability** - Better visual hierarchy makes financial data easier to scan
3. **Modernize the app** - Current UI feels dated compared to Linear, Stripe, Revolut

## What Changes

### Visual Design
- **New color palette** based on Stripe's professional fintech design
  - Primary: Indigo `#533afd` for CTAs and highlights
  - Canvas: Clean white `#ffffff` with soft off-white `#f6f9fc`
  - Money colors: Green `#1aae39` for income, Ruby `#ea2261` for expenses
- **Typography upgrade**
  - Inter font family with tabular figures for perfect number alignment
  - Consistent type scale (display, heading, body, caption)
- **Component restyling**
  - Pill-shaped buttons (Stripe signature)
  - Hairline borders on cards and tables
  - Gradient icon circles (Revolut style)

### Navigation
- **Replace horizontal app bar with collapsible sidebar**
  - Left-aligned sidebar with icon + label for each page
  - Collapses to icon-only mode on smaller screens
  - Mobile: Hamburger menu opens full-height drawer
  - Active state: Indigo pill highlight

### Page-by-Page Redesign
- **Dashboard**: Balance cards with gradient icons, cleaner chart containers
- **Transactions**: Stripe-style table rows with hairline borders, tabular amounts
- **Accounts/Categories/Tags**: Card-based layouts with Revolut-style headers
- **Statistics/Reports/Budgets**: Professional chart containers with subtle shadows
- **All other pages**: Consistent card styling, pill buttons, proper spacing

### Technical
- **Design tokens as CSS variables** in Vuetify theme
- **Global styles** for custom components
- **Inter font** loaded from Google Fonts
- **Tabular figures** enabled via `font-feature-settings: "tnum"`

## Capabilities

### New Capabilities

- **`design-system`**: Design tokens, typography, spacing, and global component styles
  - CSS custom properties for colors, spacing, radius, shadows
  - Inter font integration with tabular figures
  - Global utility classes for common patterns
  - Tabular number styling for financial data

### Modified Capabilities

- *(None - this is a pure visual redesign, no API or behavior changes)*

## Impact

### Affected Code
- `frontend/plugins/vuetify.ts` - Theme configuration
- `frontend/layouts/default.vue` - Sidebar navigation
- `frontend/pages/*.vue` - Component styling updates
- `frontend/app.vue` - Global styles, font loading
- `frontend/assets/` - Any new CSS files

### Dependencies
- Google Fonts (Inter) - CDN import
- Existing Vuetify 4 components - Reused with custom theme

### No Breaking Changes
- All API contracts unchanged
- All functionality preserved
- Responsive behavior maintained
