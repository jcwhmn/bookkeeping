# Page Design - Categories

## Page Purpose
Manage income and expense categories with hierarchical structure.

## Route
`/categories`

## Layout Structure

```
┌─────────────────────────────────────────────────────────────────┐
│ [☰]  Categories                      [+ Add]                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [ Income ] [ Expense ]                       [🔍 Search...]    │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🍔 Food                              [drag] [✏️] [🗑️]   │   │
│  │    ├── 🍎 Groceries                  [drag] [✏️] [🗑️]   │   │
│  │    └── 🍕 Restaurants                [drag] [✏️] [🗑️]   │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 🚗 Transport                         [drag] [✏️] [🗑️]   │   │
│  │    ├── 🚕 Taxi                       [drag] [✏️] [🗑️]   │   │
│  │    ├── 🚌 Bus                        [drag] [✏️] [🗑️]   │   │
│  │    └── ⛽ Gas                        [drag] [✏️] [🗑️]   │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 🏠 Housing                           [drag] [✏️] [🗑️]   │   │
│  │    ├── 🏡 Rent                       [drag] [✏️] [🗑️]   │   │
│  │    └── 💡 Utilities                  [drag] [✏️] [🗑️]   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ─────────────────────────────────────────────────────────────  │
│  INCOME                                                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 💰 Salary                            [drag] [✏️] [🗑️]   │   │
│  │ 💵 Bonus                            [drag] [✏️] [🗑️]   │   │
│  │ 📈 Investment                       [drag] [✏️] [🗑️]   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└───────────────────────────────────────────────┘
```

## Components

### Page Header
| Element | Description |
|---------|-------------|
| Title | "Categories" |
| Add Button | Create new category |
| Search | Filter categories |

### Type Tabs
| Tab | Description |
|-----|-------------|
| Income | Income categories only |
| Expense | Expense categories only |

### Category Tree
Hierarchical display with:
- Expand/collapse parent categories
- Drag handle for reordering
- Edit/Delete actions on hover
- Icon + name display
- Indentation for children

## Category Structure

### Default Categories (from seed data)

**Income:**
- Salary
- Bonus
- Investment
- Gift

**Expense:**
- Food
  - Groceries
  - Restaurants
- Transport
  - Taxi/Ride-share
  - Bus/Train
  - Gas/Fuel
- Housing
  - Rent/Mortgage
  - Utilities
- Healthcare
- Entertainment
- Shopping

## Data Model

### Category Entity
```javascript
{
  id: "1",
  name: "Food",
  type: "EXPENSE",           // EXPENSE | INCOME
  parentId: null,            // null for root categories
  icon: "restaurant",
  color: "#FF9800",
  sortOrder: 10,
  children: [                // Populated for parent categories
    { id: "5", name: "Groceries" },
    { id: "6", name: "Restaurants" }
  ]
}
```

### API Endpoints
```javascript
// GET /api/v1/categories?type=EXPENSE
{
  categories: [
    {
      id: "4",
      name: "Food",
      icon: "restaurant",
      color: "#FF9800",
      sortOrder: 10,
      children: [
        { id: "5", name: "Groceries", icon: "shopping_basket" },
        { id: "6", name: "Restaurants", icon: "local_pizza" }
      ]
    }
  ]
}

// POST /api/v1/categories
{ name: "New Category", type: "EXPENSE", parentId: null }

// PATCH /api/v1/categories/5
{ name: "Updated Name", color: "#F44336" }
```

## Modal: Create/Edit Category

```
┌─────────────────────────────────────┐
│ Create Category                 ✕   │
├─────────────────────────────────────┤
│                                     │
│  Name:                              │
│  ┌─────────────────────────────┐   │
│  │ Food                        │   │
│  └─────────────────────────────┘   │
│                                     │
│  Type:                              │
│  ○ Expense  ● Income                │
│                                     │
│  Parent Category (optional):        │
│  ┌─────────────────────────────┐   │
│  │ [None ▼]                   │   │
│  └─────────────────────────────┘   │
│                                     │
│  Icon: [🍔]                        │
│  Color: [#FF9800]                  │
│                                     │
│  [ Cancel ]        [ Save ]        │
│                                     │
└─────────────────────────────────────┘
```

## Drag & Drop Reorder

- Drag handle on left side of each row
- Visual feedback during drag (highlight drop zone)
- Update sortOrder on drop
- Persist order via API

```javascript
// PATCH /api/v1/categories/reorder
{
  order: ["4", "5", "6", "7", "8"]  // IDs in new order
}
```

## Actions

| Action | Trigger | Result |
|--------|---------|--------|
| Add | + button | Opens create modal |
| Edit | Pencil icon | Opens edit modal |
| Delete | Trash icon | Confirmation dialog |
| Reorder | Drag handle | Updates sortOrder |

## Delete Behavior

- Cannot delete category with existing transactions
- Prompt to reassign transactions first
- Can delete empty parent (cascades to children)

## Design Tokens
- Tree indent: 24px per level
- Row height: 48px
- Drag handle: 6 dots icon
- Action icons appear on hover
- Color picker: 12 preset colors

## i18n Keys
- `categories.title` = "Categories"
- `categories.add` = "Add Category"
- `categories.income` = "Income"
- `categories.expense` = "Expense"
- `categories.name` = "Name"
- `categories.icon` = "Icon"
- `categories.color` = "Color"
- `categories.parent` = "Parent Category"
- `categories.delete` = "Delete Category"
- `categories.deleteWarning` = "This category has transactions. Delete anyway?"

## OpenDesign Reference
Create categories page with:
- Income/Expense tabs
- Tree view with children
- Drag handles for reordering
- Action buttons on hover
- Create/Edit modal
- Delete confirmation