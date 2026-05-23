# Transaction Search & Filter Bar — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Collapsed State (Default)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│  [🔍 Search transactions...]                          [Filter ▼  3]  [Clear]  │
│                                                                                  │
│                                              ▼ (click to expand)                   │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Expanded State

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  [🔍 Search transactions...]                          [Filter ▲  3]  [Clear]  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                           │  │
│  │  Date Range                    Amount Range                              │  │
│  │  ┌──────────────────┐  ─  ┌──────────────────┐   ┌────────┐  ─  ┌────────┐│  │
│  │  │ From   📅        ]│     │ Min   $         ]│   │ Max $ ]│     │        ││  │
│  │  └──────────────────┘     └──────────────────┘   └────────┘     └────────┘│  │
│  │                                                                           │  │
│  │  Account                     Category                     Sort           │  │
│  │  ┌──────────────────┐       ┌──────────────────┐    ┌────────────────┐│  │
│  │  │ 3 selected     ▼ ]│       │ 2 selected     ▼ ]│    │ Date         ▼ ]│  │
│  │  └──────────────────┘       └──────────────────┘    └────────────────┘│  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Search Input (Focused)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │ 🔍  Search transactions...                                              │  │
│  │     (placeholder text, gray)                                            │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │ 📄 Lunch at restaurant         -$35.00    Cash → Food & Dining         │  │
│  │ 📄 Gas station                  -$45.00    Chase → Transportation       │  │
│  │ 📄 Online shopping              -$129.00    Visa → Shopping             │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Results Count

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│  Showing 47 of 156 transactions                           [+ Add Transaction]  │
│                                                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │ May 15   Lunch at restaurant         -$35.00    Cash → Food & Dining   │  │
│  │ May 14   Gas station                  -$45.00    Chase → Transportation │  │
│  │ May 13   Online shopping              -$129.00    Visa → Shopping       │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Multi-Select Dropdown (Account)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│  Account ▼                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  ☑ 💵 Cash                $1,234.56                                    │  │
│  │  ☑ 🏦 Chase Checking      $2,847.50                                    │  │
│  │  ☐ 💳 Visa Credit Card   -$432.10                                     │  │
│  │  ☐ 🏠 Savings            $8,500.00                                      │  │
│  │  ☐ 🎁 Gift Card          $100.00                                       │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | `#1976D2` | Active filter badge, selected items |
| `--primary-light` | `#E3F2FD` | Hover states |
| `--bg-surface` | `#FFFFFF` | Input backgrounds |
| `--bg-section` | `#F5F5F5` | Filter section background |
| `--text-primary` | `#212121` | Labels, input text |
| `--text-secondary` | `#757575` | Placeholders, counts |
| `--border` | `#E0E0E0` | Input borders |
| `--checkbox-checked` | `#1976D2` | Checkbox fill |
| `--badge` | `#D32F2F` | Filter count badge (red) |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-body` | sans-serif | All text |
| `--font-mono` | tabular-nums | Amount values |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--input-height` | 40px | Search, dropdowns |
| `--input-padding` | 12px | Inner padding |
| `--field-gap` | 16px | Between fields |
| `--row-gap` | 12px | Between filter rows |
| `--section-padding` | 16px | Filter section padding |

### Animation
| Token | Value | Usage |
|-------|-------|-------|
| `--expand-duration` | 200ms | Section expand/collapse |
| `--expand-ease` | ease-out | Animation curve |

---

## Components

### 1. Search Input

| Element | Description |
|---------|-------------|
| Icon | 🔍 magnifying glass (left) |
| Placeholder | "Search transactions..." |
| Width | Full width, grows |
| Debounce | 300ms delay before search |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray border, placeholder text |
| Focus | Blue border, clear button appears |
| Filled | Black text, clear button visible |
| Loading | Spinner inside input |

### 2. Filter Toggle Button

| Element | Description |
|---------|-------------|
| Text | "Filter" |
| Icon | ▼ (collapsed) / ▲ (expanded) |
| Badge | Red circle with count (when > 0) |

**States**:
| State | Visual |
|-------|--------|
| Default | "Filter ▼" no badge |
| Active | "Filter ▼  3" with red badge |
| Expanded | "Filter ▲  3" with badge |

### 3. Clear All Button

| Element | Description |
|---------|-------------|
| Icon | ✕ (X) |
| Text | "Clear" |
| Visibility | Hidden when no filters active |

**States**:
| State | Visual |
|-------|--------|
| Hidden | No filters = not visible |
| Visible | Filters active = shows up |
| Hover | Red background |

### 4. Filter Section

| Element | Description |
|---------|-------------|
| Background | Light gray (#F5F5F5) |
| Layout | Grid of filter rows |
| Animation | Smooth expand/collapse |

### 5. Date Range Inputs

| Element | Description |
|---------|-------------|
| Label | "From" and "To" |
| Format | "MMM DD, YYYY" |
| Picker | Native calendar |
| Clear | ✕ button when populated |

**Layout**: Two inputs side by side with "—" between

### 6. Amount Range Inputs

| Element | Description |
|---------|-------------|
| Label | "Min" and "Max" |
| Prefix | "$" symbol |
| Format | Tabular-numeric |
| Type | number (allows decimals) |

**Layout**: Two inputs side by side with "—" between

### 7. Multi-Select Dropdown (Account/Category)

| Element | Description |
|---------|-------------|
| Button | Shows selected count |
| Dropdown | Checkbox list |
| Checkbox | ☑ / ☐ with label |
| Format | "💵 Cash — $1,234.56" |

**Button States**:
| State | Visual |
|-------|--------|
| Default | "Select..." placeholder |
| Partial | "3 selected ▼" |
| All | "All (5) ▼" |
| Open | Blue border |

**Dropdown Item States**:
| State | Visual |
|-------|--------|
| Unchecked | ☐ gray text |
| Checked | ☑ blue text, blue checkbox |
| Hover | Light blue background |

### 8. Sort Dropdown

| Element | Description |
|---------|-------------|
| Options | Date (default), Amount ↑, Amount ↓ |
| Default | "Date" selected |

**Options**:
| Option | Description |
|--------|-------------|
| Date | Newest first |
| Amount (High → Low) | Largest expense first |
| Amount (Low → High) | Smallest expense first |

### 9. Results Count

| Element | Description |
|---------|-------------|
| Format | "Showing X of Y transactions" |
| Update | Updates when filters change |
| Position | Above transaction list |

---

## Interactions

### Search Input
1. User types query
2. Debounce 300ms
3. Send search request
4. Update results count
5. Update list

### Filter Toggle Click
1. Toggle expanded/collapsed state
2. Smooth animation
3. Persist state (optional)

### Checkbox Toggle
1. Click checkbox
2. Update selected items
3. Update button count
4. Apply filter
5. Update results

### Clear All Click
1. Reset all filter states
2. Clear search input
3. Apply (show all)
4. Update results count
5. Hide Clear button

### Date/Clear Click
1. Clear that date field
2. Apply filter
3. Update results

### Sort Change
1. Select new sort option
2. Re-sort list
3. Update display

### Click Outside Dropdown
1. Close dropdown
2. Keep selections

### Escape Key
1. Close all dropdowns
2. Keep selections

---

## Validation & Constraints

| Rule | Behavior |
|------|----------|
| Min ≤ Max | Validate when both filled |
| From ≤ To | Validate when both filled |
| Max amount | ≤ 999,999,999 |
| Search length | ≤ 100 characters |

---

## API Contract

### Search Transactions
```
GET /api/v1/transactions?keyword=lunch&min_amount=1000&max_amount=50000&account_ids=1,2&category_ids=5,6&start_time=1747267200&end_time=1749868800&sort=amount_desc&limit=50&offset=0

Parameters:
- keyword: string (searches description)
- min_amount: number (in cents)
- max_amount: number (in cents)
- account_ids: string (comma-separated IDs)
- category_ids: string (comma-separated IDs)
- start_time: number (Unix timestamp)
- end_time: number (Unix timestamp)
- sort: string (date_asc|date_desc|amount_asc|amount_desc)
- limit: number (default 50)
- offset: number (pagination)

Response:
{
  "success": true,
  "result": {
    "transactions": [ ... ],
    "total": 156,
    "filtered": 47
  }
}
```

### Get Filter Options
```
GET /api/v1/accounts (existing)
GET /api/v1/categories (existing)
```

---

## Backend Implementation Tasks

### Controller
- [ ] Update `GET /api/v1/transactions` with filter params

### Repository
- [ ] Add `findByFilters()` with dynamic query
- [ ] Support keyword search (LIKE)
- [ ] Support amount range (BETWEEN)
- [ ] Support multiple account IDs (IN)
- [ ] Support multiple category IDs (IN)
- [ ] Support date range (BETWEEN)
- [ ] Support sort options

### Service
- [ ] `TransactionService.searchTransactions(filters)`
- [ ] `TransactionService.countFiltered(filters)`

### DTO
- [ ] `TransactionSearchRequest` record with all filter fields
- [ ] `TransactionSearchResponse` with total/filtered counts

---

## Frontend Implementation Tasks

### Components
- [ ] `SearchFilterBar.vue` — main container
- [ ] `SearchInput.vue` — search field
- [ ] `DateRangeFilter.vue` — from/to dates
- [ ] `AmountRangeFilter.vue` — min/max amount
- [ ] `MultiSelectDropdown.vue` — reusable dropdown
- [ ] `AccountFilter.vue` — account multi-select
- [ ] `CategoryFilter.vue` — category multi-select
- [ ] `SortDropdown.vue` — sort selector
- [ ] `ResultsCount.vue` — count display

### Composables
- [ ] `useSearchFilters()` — filter state management
- [ ] `useDebounce()` — debounce search

### Store
- [ ] `filters` state (reactive)
- [ ] `resultsCount` state
- [ ] `totalCount` state

### API
- [ ] `GET /api/v1/transactions` with query params

### i18n Keys
- [ ] `search.placeholder` = "Search transactions..."
- [ ] `filter.label` = "Filter"
- [ ] `filter.clear` = "Clear"
- [ ] `filter.from` = "From"
- [ ] `filter.to` = "To"
- [ ] `filter.min` = "Min"
- [ ] `filter.max` = "Max"
- [ ] `filter.account` = "Account"
- [ ] `filter.category` = "Category"
- [ ] `filter.sort` = "Sort"
- [ ] `filter.sortDate` = "Date"
- [ ] `filter.sortAmountHigh` = "Amount (High → Low)"
- [ ] `filter.sortAmountLow` = "Amount (Low → High)"
- [ ] `search.showing` = "Showing {filtered} of {total} transactions"
- [ ] `search.noResults` = "No transactions match your filters"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No results | Show empty state with "Clear filters" link |
| All filters cleared | Show "Showing X of X transactions" |
| Invalid date range | Show validation error |
| Network error | Show error toast, retry button |
| Very long search | Truncate to 100 chars |
| Special characters | Escape in search query |

---

## States Summary

| State | Visual |
|-------|--------|
| Collapsed | Only search input visible |
| Expanded | All filters visible |
| Filtering | Spinner on search |
| Results | List updated, count shown |
| No Results | Empty state message |
| Error | Error toast |

---

## Responsive Behavior

| Breakpoint | Behavior |
|------------|----------|
| Desktop (>1024px) | Filters in 2 columns |
| Tablet (768-1024px) | Filters in 2 columns, smaller inputs |
| Mobile (<768px) | Filters stack vertically |

---

## Performance Considerations

| Optimization | Implementation |
|-------------|---------------|
| Debounce | 300ms delay on search input |
| Virtual Scroll | For lists > 100 items |
| Cache | Cache filter options (accounts, categories) |
| Pagination | Limit results, load more |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*