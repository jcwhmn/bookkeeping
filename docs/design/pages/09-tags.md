# Page Design - Tags Management

## Page Purpose
Create, edit, and manage transaction tags. Tags are user-defined labels for additional categorization beyond categories.

## Route
`/tags` — accessed from main navigation

## Layout Structure

### Tags Page
```
┌─────────────────────────────────────────────────────────────────┐
│ [☰]  Tags                           [+ Add Tag]                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Search: [🔍 Filter tags...                    ]               │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ● Food                          #FF5722    [✏️] [🗑️]     │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ ● Travel                       #4CAF50    [✏️] [🗑️]     │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ ● Business                     #2196F3    [✏️] [🗑️]     │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ ● Subscription                  #9C27B0    [✏️] [🗑️]     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ───────────────────────────────────────────────────────────── │
│  Tag Usage Stats:                                               │
│  • Food: 47 transactions                                        │
│  • Travel: 12 transactions                                     │
│  • Business: 8 transactions                                    │
│  • Subscription: 24 transactions                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Create/Edit Tag Dialog
```
┌─────────────────────────────────────┐
│ Create Tag                      ✕   │
├─────────────────────────────────────┤
│                                     │
│  Name:                              │
│  ┌─────────────────────────────┐   │
│  │ Food                        │   │
│  └─────────────────────────────┘   │
│                                     │
│  Color:                             │
│  ┌─────────────────────────────┐   │
│  │ [#FF5722]                   │   │
│  └─────────────────────────────┘   │
│                                     │
│  Preview:                          │
│  ┌─────────────────────────────┐   │
│  │ [● Food]                   │   │
│  └─────────────────────────────┘   │
│                                     │
│  [ Cancel ]        [ Save ]        │
│                                     │
└─────────────────────────────────────┘
```

### Delete Confirmation
```
┌─────────────────────────────────────┐
│ Delete Tag?                      ✕   │
├─────────────────────────────────────┤
│                                     │
│  Delete "Food" tag?                 │
│                                     │
│  ⚠️ This tag is used in 47          │
│     transactions.                   │
│                                     │
│  [ Cancel ]        [ Delete ]       │
│                                     │
└─────────────────────────────────────┘
```

## Components

### Tag List Item
- Color dot indicator
- Tag name
- Color hex code
- Edit button (hover)
- Delete button (hover)

### Search/Filter
- Text input to filter tags by name
- Instant filtering (debounced)

### Color Picker
- Text input with # prefix
- Preset color swatches (12 colors)
- Click swatch to select

### Preview
- Shows how tag chip will look
- Updates as user types

### Tag Usage Stats
- List of tags with transaction count
- Helps identify unused tags

## Data Model

### Tag Entity
```javascript
{
  id: 1,
  name: "Food",
  color: "#FF5722",
  sortOrder: 10,
  userId: 1,
  createdAt: 1717104000,
  updatedAt: 1717104000
}
```

### TransactionTag (Join Table)
```javascript
{
  transactionId: 123,
  tagId: 1
}
```

## API Endpoints

### List Tags
```
GET /api/v1/tags

Response:
{
  "success": true,
  "result": [
    { "id": 1, "name": "Food", "color": "#FF5722", "sortOrder": 1 },
    { "id": 2, "name": "Travel", "color": "#4CAF50", "sortOrder": 2 }
  ]
}
```

### Create Tag
```
POST /api/v1/tags

Request:
{ "name": "Food", "color": "#FF5722" }

Response:
{ "success": true, "result": { "id": 1, "name": "Food", "color": "#FF5722", "sortOrder": 1 } }
```

### Update Tag
```
PUT /api/v1/tags/{id}

Request:
{ "name": "Groceries", "color": "#4CAF50" }

Response:
{ "success": true, "result": { "id": 1, "name": "Groceries", "color": "#4CAF50", "sortOrder": 1 } }
```

### Delete Tag
```
DELETE /api/v1/tags/{id}

Response:
{ "success": true, "result": null }
```

### Tag Usage Stats
```
GET /api/v1/tags/stats

Response:
{
  "success": true,
  "result": [
    { "tagId": 1, "tagName": "Food", "transactionCount": 47 },
    { "tagId": 2, "tagName": "Travel", "transactionCount": 12 }
  ]
}
```

## Transaction Integration

### Add Tags to Transaction (in create/edit dialog)
```
┌──────────────────────────────────────────────┐
│  Add Transaction                        [✕]  │
├──────────────────────────────────────────────┤
│  ... (existing fields) ...                   │
│                                              │
│  Tags:                                       │
│  ┌──────────────────────────────────────┐   │
│  │ [● Food] [● Travel] [+ Add]        │   │
│  └──────────────────────────────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

### Filter Transactions by Tag
```
┌──────────────────────────────────────────────┐
│ Transactions              [+ Add] [Filter ▼]│
├──────────────────────────────────────────────┤
│ [ All ] [ Food ] [ Travel ] [ Business ]    │
│                                              │
│  🔵 Lunch at restaurant    -$35.00   [food] │
│  🟢 Flight to NYC           -$450.00  [travel]│
│                                              │
└──────────────────────────────────────────────┘
```

## States

### Default (empty)
- "No tags yet" message
- "Create your first tag" button

### With Tags
- List of all tags
- Search/filter available

### Creating/Editing
- Dialog with name + color inputs
- Preview chip
- Save/Cancel buttons

### Deleting (with usage)
- Warning about transaction count
- Confirm/Delete buttons

### Loading
- Skeleton loaders for list

## Design Tokens
- Tag chip: 8px border-radius, colored left border
- Color dot: 12px circle
- Row height: 48px
- Color swatches: 24px circles

## i18n Keys
- `tags.title` = "Tags"
- `tags.add` = "Add Tag"
- `tags.edit` = "Edit Tag"
- `tags.delete` = "Delete Tag"
- `tags.name` = "Name"
- `tags.color` = "Color"
- `tags.preview` = "Preview"
- `tags.search` = "Filter tags..."
- `tags.noTags` = "No tags yet"
- `tags.createFirst` = "Create your first tag"
- `tags.usedIn` = "Used in {count} transactions"
- `tags.deleteWarning` = "This tag is used in {count} transactions. Delete anyway?"