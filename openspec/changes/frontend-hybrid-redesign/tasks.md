# Frontend Hybrid Redesign - Implementation Tasks

## 1. Design System Foundation

- [ ] 1.1 Update `plugins/vuetify.ts` with Stripe color palette
  - Primary: #533afd, Primary Deep: #4434d4, Primary Press: #2e2b8c
  - Canvas: #ffffff, Canvas Soft: #f6f9fc
  - Ink: #0d253d, Ink Mute: #64748d
  - Positive: #1aae39, Negative: #ea2261
  - Hairline: #e3e8ee

- [ ] 1.2 Configure Vuetify defaults for pill buttons and card styling
  - VBtn: rounded = 'pill'
  - VCard: rounded = 'lg', elevation = 0

- [ ] 1.3 Load Inter font from Google Fonts in `app.vue`
  - Add `<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">`

- [ ] 1.4 Create `assets/css/design-tokens.css` with CSS custom properties
  - All Stripe color variables
  - Spacing scale (--stripe-space-*)
  - Border radius (--stripe-radius-*)

- [ ] 1.5 Create `assets/css/global.css` with global styles
  - Tabular figures for `.money`, `.amount` classes
  - Global font-family with Inter fallback
  - Type scale utilities

## 2. Sidebar Navigation

- [ ] 2.1 Create `components/SidebarNav.vue` component
  - Vuetify Navigation Drawer
  - Logo + app name header
  - Collapse/expand toggle button

- [ ] 2.2 Add navigation items with icons
  - Dashboard, Transactions, Accounts, Categories, Tags
  - Statistics, Budgets, Templates, Reports, Exchange, Insights
  - Settings, About

- [ ] 2.3 Implement active state styling
  - Indigo pill background for current route
  - White text on active item

- [ ] 2.4 Add collapse/expand functionality
  - Expanded: 240px with icon + label
  - Collapsed: 64px with icon only
  - Smooth transition animation

- [ ] 2.5 Handle responsive behavior
  - Desktop (>1024px): Fixed sidebar
  - Tablet (768-1024px): Collapsed by default
  - Mobile (<768px): Hidden, hamburger triggers drawer

- [ ] 2.6 Add user section at bottom of sidebar
  - Avatar circle with gradient
  - Username display
  - Logout button

- [ ] 2.7 Update `layouts/default.vue` to use SidebarNav

## 3. Dashboard Redesign

- [ ] 3.1 Restyle balance cards with gradient icons
  - Create `components/BalanceCard.vue`
  - Gradient circle icon (Revolut style)
  - Large balance display with tabular figures
  - Trend indicator (if applicable)

- [ ] 3.2 Update chart containers
  - Card styling with hairline borders
  - Consistent header/titles
  - Chart background: #ffffff with subtle border

- [ ] 3.3 Restyle transaction list preview
  - Hairline row separators
  - Tabular amounts
  - Income in green, expense in ruby

- [ ] 3.4 Update summary/stat cards
  - Consistent card styling
  - Icon + value + label layout
  - Pill badges for status

- [ ] 3.5 Apply design to `pages/index.vue`

## 4. Transactions Page

- [ ] 4.1 Restyle transaction table
  - Hairline row borders
  - Alternating row backgrounds (subtle)
  - Header row with hairline bottom border

- [ ] 4.2 Style amount column
  - Tabular figures enabled
  - Right-aligned
  - Color-coded: green for income, ruby for expense

- [ ] 4.3 Restyle filter bar
  - Card container with hairline border
  - Input fields with Stripe styling
  - Filter buttons as pill buttons

- [ ] 4.4 Update dialog/modal styling
  - Clean card appearance
  - Pill-styled action buttons
  - Form input styling

- [ ] 4.5 Apply design to `pages/transactions.vue`

## 5. Accounts/Categories/Tags Pages

- [ ] 5.1 Restyle account cards
  - Gradient icon circle header
  - Balance display with tabular figures
  - Card with hairline border

- [ ] 5.2 Restyle category cards
  - Icon with gradient background
  - Name and type display
  - Color indicator for INCOME/EXPENSE

- [ ] 5.3 Restyle tag components
  - Soft purple pill badges
  - Tag group cards with hairline borders

- [ ] 5.4 Apply design to `pages/accounts.vue`
- [ ] 5.5 Apply design to `pages/categories.vue`
- [ ] 5.6 Apply design to `pages/tags.vue`

## 6. Statistics/Reports/Budgets Pages

- [ ] 6.1 Restyle chart containers
  - Consistent card styling
  - Title header with hairline bottom
  - Chart area with subtle background

- [ ] 6.2 Update summary widgets
  - Balance cards with gradient icons
  - Period selector pill buttons

- [ ] 6.3 Apply design to `pages/statistics.vue`
- [ ] 6.4 Apply design to `pages/reports.vue`
- [ ] 6.5 Apply design to `pages/budgets.vue`

## 7. Remaining Pages

- [ ] 7.1 Apply design to `pages/templates.vue`
- [ ] 7.2 Apply design to `pages/exchange.vue`
- [ ] 7.3 Apply design to `pages/scheduled-transactions.vue`
- [ ] 7.4 Apply design to `pages/insights.vue`

## 8. Auth/Profile Pages

- [ ] 8.1 Restyle login page
  - Clean card with logo
  - Pill-styled form inputs
  - Primary CTA as pill button

- [ ] 8.2 Restyle register page
  - Consistent with login styling

- [ ] 8.3 Restyle profile page
  - Avatar with gradient background
  - Form styling
  - Card containers

- [ ] 8.4 Apply design to `pages/login.vue`
- [ ] 8.5 Apply design to `pages/register.vue`
- [ ] 8.6 Apply design to `pages/profile.vue`

## 9. Settings/About Pages

- [ ] 9.1 Restyle settings page
  - Card-based sections
  - Toggle switches
  - Form inputs consistent styling

- [ ] 9.2 Restyle about page
  - Clean card with app info
  - Version info styling

- [ ] 9.3 Apply design to `pages/settings.vue`
- [ ] 9.4 Apply design to `pages/about.vue`

**NOTE:** Onboarding page (`/onboarding`) is skipped - should be a separate future task.

## 10. Shared Components (New)

- [ ] 10.1 Create `components/GradientIcon.vue`
  - Props: icon, gradient (optional)
  - Default gradient: #667eea → #764ba2
  - Size variants: sm (32px), md (48px), lg (64px)

- [ ] 10.2 Create `components/StripeCard.vue`
  - Wrapper for VCard with Stripe styling
  - Props: title, subtitle, padding

- [ ] 10.3 Create `components/MoneyDisplay.vue`
  - Props: amount, showSign, color
  - Tabular figures
  - Color-coded by sign

- [ ] 10.4 Create `components/StatCard.vue`
  - Icon + value + label layout
  - Optional trend indicator
  - Gradient icon background

## 11. Testing & Polish

- [ ] 11.1 Test all pages in responsive modes
  - Desktop (1920px, 1366px)
  - Tablet (768px, 1024px)
  - Mobile (375px, 428px)

- [ ] 11.2 Verify tabular figures work correctly
  - Check number alignment in tables
  - Verify money displays

- [ ] 11.3 Test sidebar collapse/expand
  - Smooth transitions
  - Active state updates

- [ ] 11.4 Check all interactive elements
  - Button hover/active states
  - Input focus states
  - Card hover effects

- [ ] 11.5 Test mobile hamburger menu
  - Opens drawer correctly
  - Close on outside click
  - Swipe to close (if supported)

- [ ] 11.6 Update documentation
  - Add design system section to AGENTS.md
  - Document any custom CSS classes used