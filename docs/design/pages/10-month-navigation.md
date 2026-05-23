# Month Navigation Component — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Main Component (Inline)

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   [◀]      May 2026 ▶       (click to open picker)            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Month Picker Dropdown (Open)

```
┌─────────────────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ◀  2025  ▶  (year navigation)                          │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │                                                         │   │
│  │   Jan ●      Feb       Mar       Apr                    │   │
│  │   12 txns    8 txns    15 txns   23 txns              │   │
│  │                                                         │   │
│  │   May ●      Jun       Jul       Aug                    │   │
│  │   47 txns    31 txns   28 txns   19 txns              │   │
│  │  [blue fill]                                              │   │
│  │                                                         │   │
│  │   Sep       Oct       Nov       Dec                    │   │
│  │   34 txns   22 txns   29 txns   41 txns               │   │
│  │                                                         │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │  [Today] ← blue accent button                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

● = dot indicator for current month (today)
[blue fill] = selected month
```

### Calendar Mini-View (Alternative)

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   ◀       May 2026        ▶                                  │
│   ──────────────────────────────────────                      │
│   Sun   Mon   Tue   Wed   Thu   Fri   Sat                      │
│   ──────────────────────────────────────                      │
│         1      2      3      4      5      6                  │
│    7      8      9     10     11     12     13               │
│   14     15     16     17     18     19     20               │
│   21     22     23     24     25     26     27               │
│   28     29     30     31                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | `#1976D2` | Selected month, Today button |
| `--primary-light` | `#E3F2FD` | Hover state, light fill |
| `--text-primary` | `#212121` | Month/Year text |
| `--text-secondary` | `#757575` | Disabled months, txns count |
| `--bg-surface` | `#FFFFFF` | Dropdown background |
| `--border` | `#E0E0E0` | Dropdown border |
| `--disabled` | `#BDBDBD` | Future months, disabled arrows |
| `--dot` | `#1976D2` | Today indicator dot |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Month/Year display |
| `--font-body` | sans-serif | Navigation arrows, counts |
| `--font-mono` | tabular-nums | Year number |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--nav-gap` | 8px | Between arrows and text |
| `--month-cell` | 80px | Each month cell width |
| `--month-grid-gap` | 4px | Gap between month cells |
| `--padding` | 16px | Dropdown padding |

### Dimensions
| Token | Value | Usage |
|-------|-------|-------|
| `--arrow-size` | 32px | Navigation arrow buttons |
| `--cell-height` | 64px | Month cell height |
| `--dropdown-width` | 340px | Picker dropdown width |

---

## Components

### 1. Left Arrow Button

| Element | Description |
|---------|-------------|
| Icon | ◀ (left chevron) |
| Size | 32px × 32px |
| Position | Left of center display |
| Action | Navigate to previous month |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray (#757575) |
| Hover | Blue (#1976D2) |
| Active | Darker blue, pressed effect |
| Disabled | Gray (#BDBDBD), no interaction |

### 2. Center Display

| Element | Description |
|---------|-------------|
| Format | "MMMM YYYY" (e.g., "May 2026") |
| Font | 18px serif |
| Cursor | pointer (clickable) |
| Icon | Small ▼ indicator |

**States**:
| State | Visual |
|-------|--------|
| Default | Black text, ▼ icon |
| Hover | Blue text, underline |
| Dropdown Open | Blue text, ▲ icon |

### 3. Right Arrow Button

| Element | Description |
|---------|-------------|
| Icon | ▶ (right chevron) |
| Size | 32px × 32px |
| Position | Right of center display |
| Action | Navigate to next month |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray (#757575) |
| Hover | Blue (#1976D2) |
| Active | Darker blue, pressed effect |
| Disabled | Gray (#BDBDBD), no interaction |

### 4. Month Picker Dropdown

| Element | Description |
|---------|-------------|
| Layout | 3×4 grid (12 months) |
| Header | Year with ◀ ▶ navigation |
| Footer | "Today" quick-jump button |

**Header**:
| Element | Description |
|---------|-------------|
| Year | "2025" centered, monospace font |
| Left Arrow | Navigate to previous year |
| Right Arrow | Navigate to next year |

### 5. Month Cell

| Element | Description |
|---------|-------------|
| Layout | Month name + transaction count |
| Format | "Jan" (full month name or abbr) |
| Count | "47 txns" below in small text |
| Size | 80px × 64px |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray text, transparent bg |
| Hover | Light blue background |
| Selected | Solid blue fill, white text |
| Current (Today) | Dot indicator (●) below name |
| Disabled (Future) | Gray text, disabled cursor |

### 6. Today Button

| Element | Description |
|---------|-------------|
| Text | "Today" |
| Position | Bottom of dropdown |
| Style | Blue outlined button |

**States**:
| State | Visual |
|-------|--------|
| Default | Blue outline, white bg |
| Hover | Light blue fill |
| Disabled | Gray outline (already at today) |

### 7. Transaction Count

| Element | Description |
|---------|-------------|
| Format | "N txns" or "N transactions" |
| Position | Below month name |
| Font | 12px, secondary color |
| Empty | Shows "0 txns" (not hidden) |

---

## Interactions

### Arrow Click (Previous)
1. Decrement month (or year if January)
2. Update center display
3. Fetch transactions for new month
4. Animate transition (fade or slide)

### Arrow Click (Next)
1. If at current month: Ignore click (disabled)
2. Else: Increment month
3. Update center display
4. Fetch transactions for new month

### Center Display Click
1. Toggle dropdown open/close
2. If open: Scroll to show current/selected month

### Month Cell Click
1. If disabled (future month): Ignore
2. Set selected month
3. Close dropdown
4. Update center display
5. Fetch transactions for new month

### Year Navigation
1. Click ◀: Decrement year
2. Click ▶: Increment year
3. Re-render month grid

### Today Button Click
1. Set selected month to current month
2. Close dropdown
3. Update center display
4. Scroll to Today position
5. Fetch current month transactions

### Click Outside Dropdown
1. Close dropdown
2. Keep selected month

### Escape Key
1. Close dropdown
2. Keep selected month

---

## Validation / Constraints

| Rule | Behavior |
|------|----------|
| Cannot go to future | Right arrow disabled at current month |
| Max past | 1 year back (or configurable) |
| Left arrow at limit | Disabled |
| Empty month | Shows "0 txns" |
| Today's month | Always selectable, shows dot |

---

## API Contract

### Get Transactions by Month
```
GET /api/v1/transactions?year=2026&month=5

Response:
{
  "success": true,
  "result": [
    { "id": 1, "amount": 8500, "transactionTime": 1717104000, ... },
    ...
  ]
}
```

### Get Month Counts (for picker)
```
GET /api/v1/transactions/month-counts

Response:
{
  "success": true,
  "result": {
    "2026": {
      "1": { "count": 12 },
      "2": { "count": 8 },
      "3": { "count": 15 },
      "4": { "count": 23 },
      "5": { "count": 47 },
      ...
    }
  }
}
```

### Get Current Date (for Today detection)
```
GET /api/v1/system/time

Response:
{
  "success": true,
  "result": {
    "timestamp": 1747862400,
    "timezone": "Asia/Shanghai",
    "year": 2026,
    "month": 5,
    "day": 22
  }
}
```

---

## Backend Implementation Tasks

### API
- [ ] `GET /api/v1/transactions` with year/month params (existing)
- [ ] `GET /api/v1/transactions/month-counts` — new endpoint
- [ ] `GET /api/v1/system/time` — current time endpoint

### Repository
- [ ] `TransactionRepository.countByUserIdAndYearAndMonth(userId, year, month)`

### Service
- [ ] `TransactionService.getMonthCounts(userId)` — return counts per month
- [ ] `SystemService.getCurrentTime()` — return server time

### Cache
- [ ] Cache month counts (invalidate on transaction CRUD)

---

## Frontend Implementation Tasks

### Components
- [ ] `MonthNavigation.vue` — main component
- [ ] `MonthPicker.vue` — dropdown picker
- [ ] `MonthCell.vue` — individual month cell
- [ ] `YearNavigation.vue` — year arrows

### Store
- [ ] `selectedMonth` state (year + month)
- [ ] `transactionCounts` state

### Composables
- [ ] `useMonthNavigation()` — navigation logic
- [ ] `useTransactionCounts()` — fetch and cache counts

### API
- [ ] `GET /api/v1/transactions?year&month` (existing)
- [ ] `GET /api/v1/transactions/month-counts` — new call
- [ ] `GET /api/v1/system/time` — current time

### i18n Keys
- [ ] `month.jan` = "January"
- [ ] `month.feb` = "February"
- [ ] ... (all months)
- [ ] `month.today` = "Today"
- [ ] `month.transactions` = "{count} transactions"
- [ ] `month.transactionsShort` = "{count} txns"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No transactions in month | Shows "0 txns", cell still clickable |
| Loading counts | Show skeleton loaders in cells |
| Future month | Cell disabled, gray text |
| Leap year February | 29 days shown |
| First day of month | Start calendar on correct weekday |
| Network error | Show cached data, retry button |

---

## States Summary

| State | Visual |
|-------|--------|
| Default | Current month displayed |
| Previous Month | Left arrow enabled |
| Next Month (Future) | Right arrow disabled |
| Picker Open | Dropdown visible |
| Selecting | Cell highlighted blue |
| Loading | Spinner on arrows |

---

## Design Variants

### Variant A: Month Names (3×4 Grid)
```
┌──────┬──────┬──────┬──────┐
│ Jan  │ Feb  │ Mar  │ Apr  │
│ 12tx │  8tx │ 15tx │ 23tx │
├──────┼──────┼──────┼──────┤
│ May  │ Jun  │ Jul  │ Aug  │
│ 47tx │ 31tx │ 28tx │ 19tx │
└──────┴──────┴──────┴──────┘
```

### Variant B: Mini Calendar
```
┌─────────────────────────────────┐
│  May 2026                       │
│  ┌──┬──┬──┬──┬──┬──┬──┐        │
│  │  │  │  │  │ 1 │ 2 │ 3 │      │
│  ├──┼──┼──┼──┼──┼──┼──┼──┤      │
│  │ 4│ 5│ 6│ 7│ 8 │ 9 │10 │      │
│  └──┴──┴──┴──┴──┴──┴──┴──┘      │
└─────────────────────────────────┘
```

---

## Reuse Across Pages

The Month Navigation component will be used on:
- [ ] Transactions page
- [ ] Dashboard (mini version)
- [ ] Reports page
- [ ] Budget page

**Props**:
```typescript
interface MonthNavigationProps {
  selectedYear: number
  selectedMonth: number
  minYear?: number        // Default: current year - 1
  maxYear?: number        // Default: current year
  showCounts?: boolean    // Default: true
  onMonthChange: (year: number, month: number) => void
}
```

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*