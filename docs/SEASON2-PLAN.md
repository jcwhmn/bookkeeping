# Season 2 Implementation Plan

**Last Updated**: 2026-05-22

## ✅ Completed (P0 MVP)

| Feature | Backend | Frontend | Status |
|---------|---------|----------|--------|
| Transaction Edit | ✅ | ✅ | Complete |
| Transaction Delete | ✅ | ✅ | Complete |
| Date Picker | ✅ | ✅ | Complete |
| Transfer Support | ✅ | ✅ | Complete |
| Month Navigation | ✅ | ✅ | Complete |

---

## 📋 Remaining Work

### P1 - Should Have (In Order)

| # | Feature | Backend | Frontend | Est. |
|---|---------|---------|----------|------|
| 1 | **Transaction Search/Filter** | ✅ | 🔄 In Progress | 2 days |
| 2 | **Tags Management** | ❌ | ❌ | 3 days |
| 3 | **Statistics Dashboard** | ❌ | ❌ | 4 days |
| 4 | **Enhanced Charts** | ❌ | ❌ | 4 days |
| 5 | **Budget Management** | ❌ | ❌ | 3 days |
| 6 | **Reports Page** | ❌ | ❌ | 2 days |
| 7 | **CSV Export** | ❌ | ❌ | 2 days |

### P2 - Nice to Have

| # | Feature | Status |
|---|---------|--------|
| 8 | Settings Page | 📋 Todo |
| 9 | User Profile/Preferences | 📋 Todo |
| 10 | Recurring Transactions | 📋 Todo |

---

## Implementation Details

### P1.1: Transaction Search/Filter ✅ Already Implemented

The search and filter is already in the frontend:
- Filter by type (All/Income/Expense/Transfer)
- Filter by account
- Text search on description

**Still needed**: API-level search with query params (`?search=keyword&categoryId=1`)

---

### P1.2: Tags Management

**Backend Needed**:
```
GET/POST/DELETE /api/v1/tags
```

**Frontend**:
- Tags management page (CRUD)
- Tag selector in transaction form
- Tag filter in transactions list

---

### P1.3: Statistics Dashboard

**Backend Needed**:
```
GET /api/v1/transactions/statistics
GET /api/v1/transactions/statistics/trends?start=&end=
```

**Returns**:
- Income/Expense by month
- Category breakdown
- Account balances over time

**Frontend**:
- Statistics page with ECharts
- Bar chart: monthly income/expense
- Pie chart: category breakdown
- Line chart: balance over time

---

### P1.4: Enhanced Charts

**Dependencies**: P1.3 (Statistics API)

**Frontend**:
- Update dashboard with enhanced charts
- Interactive tooltips
- Responsive design
- Date range selector

---

### P1.5: Budget Management

**Backend Needed**:
```
GET/POST/PUT/DELETE /api/v1/budgets
```

**Entity**:
```
Budget {
  id, userId, categoryId, amount, period (monthly), year, month
}
```

**Frontend**:
- Budgets page (CRUD)
- Progress bars on transaction list
- Budget vs actual alerts

---

### P1.6: Reports Page

**Frontend**:
- Monthly summary report
- Cash flow statement
- Category comparison
- Print-friendly layout

---

### P1.7: CSV Export

**Backend Needed**:
```
GET /api/v1/transactions/export?format=csv&start=&end=
```

**Returns**: CSV file download

**Frontend**:
- Export button on transactions page
- Date range selector
- File download

---

## Next Steps

1. **Start**: P1.2 Tags Management (Backend + Frontend)
2. **Then**: P1.3 Statistics Dashboard
3. **Then**: P1.4 Enhanced Charts
4. **Then**: P1.5 Budget Management
5. **Then**: P1.6 Reports + P1.7 CSV Export

---

## Testing

```bash
# Run all tests
cd backend && ./gradlew test

# Expected: 110+ tests passing
```

---

## Running Services

```bash
# Backend: http://localhost:8080
cd backend && ./gradlew bootRun

# Frontend: http://localhost:3000
cd frontend && npm run dev
```

---

*Plan v1.0 - Season 2 MVP complete, moving to P1 features*