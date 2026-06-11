# Frontend Hybrid Redesign - Technical Design

## Context

The bookkeeping frontend currently uses:
- **Vuetify 4** with default Material Design theme
- **Horizontal app bar** navigation (11+ nav items)
- **Inline styles** scattered throughout pages
- **Standard Material color palette** (blue primary)

This redesign adopts a **hybrid Stripe/Revolut** aesthetic while:
- Keeping Vuetify as the component framework
- Preserving all existing functionality
- Maintaining mobile responsiveness

### Current Pain Points
- Generic Material Design look
- Poor number alignment in transaction tables
- No sidebar navigation for large screens
- Inconsistent visual hierarchy

## Goals / Non-Goals

**Goals:**
- Professional fintech appearance (Stripe-inspired)
- Tabular figures for all money/number displays
- Collapsible sidebar navigation (Linear-inspired)
- Gradient icon circles (Revolut-inspired)
- Consistent pill-shaped buttons
- Hairline borders on cards and tables
- Design tokens as CSS variables

**Non-Goals:**
- Complete component library rewrite
- Dark mode (future phase)
- Custom font beyond Inter
- Backend changes
- API contract changes

## Decisions

### 1. Color Palette

**Decision:** Adopt Stripe's professional fintech palette

```
Primary:     #533afd (Indigo - CTAs, links, active states)
Primary Deep: #4434d4 (Hover states)
Canvas:      #ffffff (Main background)
Canvas Soft: #f6f9fc (Card backgrounds, sidebar)
Ink:         #0d253d (Primary text - deep navy)
Ink Mute:    #64748d (Secondary text, labels)
Hairline:    #e3e8ee (Borders, dividers)
Positive:    #1aae39 (Income)
Negative:    #ea2261 (Expenses)
```

**Rationale:** Stripe's palette is specifically designed for financial applications - trustworthy, clean, data-focused.

### 2. Typography

**Decision:** Inter font with tabular figures

```css
font-family: 'Inter', system-ui, -apple-system, sans-serif;
font-feature-settings: "tnum"; /* Tabular numbers for alignment */
```

**Implementation:** Add via Google Fonts CDN, enable tabular figures globally.

**Rationale:** Tabular figures are critical for financial tables - numbers must align regardless of digit count.

### 3. Navigation Layout

**Decision:** Replace horizontal bar with collapsible sidebar

```
Desktop (>1024px):  Fixed sidebar, 240px expanded, 64px collapsed
Tablet (768-1024): Collapsed sidebar by default
Mobile (<768px):    Hidden, hamburger trigger → drawer overlay
```

**Sidebar Structure:**
```
┌──────────────────────┐
│  📊 Bookkeeping      │  ← Logo + collapse toggle
├──────────────────────┤
│  🏠 Dashboard       │  ← Active: indigo pill
│  💰 Transactions    │
│  🏦 Accounts        │
│  📁 Categories      │
│  🏷️ Tags           │
│  📊 Statistics      │
│  💳 Budgets         │
│  📋 Templates      │
│  📈 Reports         │
│  💱 Exchange        │
│  🔮 Insights        │
├──────────────────────┤
│  ⚙️ Settings       │
│  ℹ️ About          │
├──────────────────────┤
│  [Avatar] Username  │  ← User section at bottom
│  Logout            │
└──────────────────────┘
```

**Rationale:** 
- Sidebar scales better with many nav items
- Linear and Notion set the standard for this pattern
- Vuetify's navigation drawer component supports this well

### 4. Button Styling

**Decision:** All buttons become pill-shaped (rounded-full)

```vue
<v-btn color="primary" rounded="pill">Action</v-btn>
```

**Override Vuetify default:**
```ts
VBtn: {
  rounded: 'pill'
}
```

**Rationale:** Pill buttons are Stripe's signature and signal "short, decisive action" - appropriate for financial CTAs.

### 5. Card Styling

**Decision:** Stripe-style cards with hairline borders

```css
.stripe-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e3e8ee;
  box-shadow: 0 1px 3px rgba(0, 55, 112, 0.08);
}
```

**Decision:** Gradient icon circles (Revolut style)

```css
.icon-gradient {
  background: linear-gradient(135deg, #667eea, #764ba2);
}
```

**Rationale:** Gradient icons add visual interest without being playful.

### 6. Transaction Table Rows

**Decision:** Hairline-separated rows with tabular amounts

```html
<tr class="stripe-table-row">
  <td class="stripe-money">-$1,234.56</td>
  <td>Office Supplies</td>
  <td class="text-muted">Jan 15</td>
</tr>

<style>
.stripe-table-row {
  border-bottom: 1px solid #e3e8ee;
}
.stripe-money {
  font-feature-settings: "tnum";
  text-align: right;
}
</style>
```

**Rationale:** Clean, scannable, professional.

### 7. Vuetify Theme Configuration

**Decision:** Extend Vuetify defaults via theme

```ts
// plugins/vuetify.ts
export default defineNuxtPlugin((nuxtApp) => {
  const vuetify = createVuetify({
    theme: {
      themes: {
        light: {
          colors: {
            primary: '#533afd',
            secondary: '#64748d',
            background: '#ffffff',
            surface: '#f6f9fc',
            error: '#ea2261',
            success: '#1aae39',
            // ... etc
          },
        },
      },
    },
    defaults: {
      VBtn: {
        rounded: 'pill',
      },
      VCard: {
        rounded: 'lg',
        elevation: 0,
      },
      VTextField: {
        variant: 'outlined',
        density: 'comfortable',
      },
    },
  })
})
```

**Rationale:** Centralized configuration is easier to maintain than scattered overrides.

## Implementation Phases

### Phase 1: Design System Foundation
1. Update Vuetify theme with Stripe colors
2. Load Inter font from Google Fonts
3. Create global CSS with design tokens
4. Configure default button/card styles

### Phase 2: Sidebar Navigation
1. Create sidebar layout component
2. Migrate all nav items
3. Handle collapse/expand behavior
4. Mobile drawer integration

### Phase 3: Dashboard Redesign
1. Balance cards with gradient icons
2. Chart container restyling
3. Transaction list preview styling
4. Summary cards

### Phase 4: Transactions Page
1. Table row styling with hairlines
2. Tabular number alignment
3. Filter bar styling
4. Dialog styling

### Phase 5-7: Remaining Pages
- Accounts, Categories, Tags
- Statistics, Reports, Budgets, Insights
- Templates, Exchange, Scheduled Tx
- Profile, Settings, About

## Migration Plan

### Before Starting
1. Backup/snapshot frontend code
2. Ensure tests cover critical paths
3. Document any inline style usage

### Incremental Changes
1. Theme updates first (low risk, visual only)
2. Layout changes second (may affect routing)
3. Page-by-page styling (can be done independently)

### Rollback Strategy
1. Git revert to previous commit
2. Theme is in single file - easy to revert
3. Layout changes tracked separately

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| Vuetify updates override customizations | Pin version, use defaults API |
| Inter font fails to load | Fallback to system fonts |
| Tabular figures break existing layouts | Test thoroughly, use sparingly |
| Sidebar breaks mobile UX | Test all breakpoints, use Vuetify drawer |
| Many pages = inconsistent styling | Create shared components, use linting |

## Open Questions

1. ~~Collapsed sidebar width?~~ → 64px (icon only)
2. ~~User avatar in sidebar or header?~~ → Sidebar, bottom section
3. ~~Should dark mode be included in this phase?~~ → Deferred to future
4. ~~Animation/transition preferences?~~ → Subtle fade for sidebar
5. **Settings location?** → Deferred to next task (full nav redesign)
6. **Onboarding page?** → Skipped, future task
7. **Gradient icons?** → Category-based when color available, else default gradient

---

## Files to Modify

| File | Changes |
|------|---------|
| `plugins/vuetify.ts` | Theme colors, defaults |
| `layouts/default.vue` | Sidebar navigation |
| `app.vue` | Global styles, font import |
| `assets/css/global.css` | Design tokens (new) |
| `pages/*.vue` | Component styling |

## New Components

| Component | Purpose |
|-----------|---------|
| `components/SidebarNav.vue` | Collapsible sidebar |
| `components/GradientIcon.vue` | Icon with gradient background |
| `components/StripeCard.vue` | Styled card wrapper |
| `components/MoneyDisplay.vue` | Tabular number formatting |
