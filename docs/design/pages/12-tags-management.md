# Tags Management Page — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Tags Page (With Data)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Tags                                                [+ Add Tag]            │
│                                                                                  │
│   [🔍 Filter tags...]                                                         │
│                                                                                  │
│   ┌─────────────────────────────────────────────────────────────────────────┐  │
│   │                                                                         │  │
│   │  ●  Food                             #FF5722      47 transactions    │  │
│   │                                                    [✏️] [🗑️]           │  │
│   │                                                                         │  │
│   ├─────────────────────────────────────────────────────────────────────────┤  │
│   │                                                                         │  │
│   │  ●  Travel                          #4CAF50      12 transactions    │  │
│   │                                                    [✏️] [🗑️]           │  │
│   │                                                                         │  │
│   ├─────────────────────────────────────────────────────────────────────────┤  │
│   │                                                                         │  │
│   │  ●  Business                       #2196F3       8 transactions     │  │
│   │                                                    [✏️] [🗑️]           │  │
│   │                                                                         │  │
│   ├─────────────────────────────────────────────────────────────────────────┤  │
│   │                                                                         │  │
│   │  ●  Subscriptions                   #9C27B0      24 transactions    │  │
│   │                                                    [✏️] [🗑️]           │  │
│   │                                                                         │  │
│   └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Tags Page (Empty State)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Tags                                                [+ Add Tag]            │
│                                                                                  │
│   [🔍 Filter tags...]                                                         │
│                                                                                  │
│                                                                                  │
│                        ┌───────────────────────────────┐                       │
│                        │                               │                       │
│                        │     [illustration placeholder] │                       │
│                        │                               │                       │
│                        │       No tags yet             │                       │
│                        │                               │                       │
│                        │  Create your first tag to     │                       │
│                        │  organize your transactions   │                       │
│                        │                               │                       │
│                        │    [ Create Tag ]             │                       │
│                        │                               │                       │
│                        └───────────────────────────────┘                       │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Add/Edit Tag Dialog

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Create Tag                                              [✕]           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  Name                                                                       │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ Food                                                             │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Color                                                                       │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ │   │  │
│  │  │ │ # │ │ # │ │ # │ │ # │ │ # │ │ # │ │ # │ │ # │ │ # │ │ # │ │ # │ │   │  │
│  │  │ │ F5 │ │ 4C │ │ 21 │ │ 9C │ │ FF │ │ 00 │ │ 60 │ │ E9 │ │ 79 │ │ 8C │ │   │  │
│  │  │ │ F5 │ │ AF │ │ 96 │ │ 27 │ │ 57 │ │ 0D │ │ 57 │ │ 91 │ │ 73 │ │ BE │ │   │  │
│  │  │ │ 72 │ │ 50 │ │ F3 │ │ B0 │ │ 22 │ │ BC │ │ C0 │ │ E4 │ │ 6E │ │ 0F │ │   │  │
│  │  │ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ └───┘ │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Preview                                                                   │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ [● Food]                                                             │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │              [ Cancel ]                          [ Save ]              │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

### Delete Confirmation Dialog

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Delete Tag?                                          [✕]              │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │                          ⚠️                                              │  │
│  │                     (amber warning icon)                                │  │
│  │                                                                           │  │
│  │                    Delete "Food" tag?                                   │  │
│  │                                                                           │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ⚠️ This tag is used in 47 transactions.                           │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │              [ Keep Tag ]                        [ Delete ]              │  │
│  │               (secondary)                      (red button)             │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | `#1976D2` | Add button, Save button |
| `--danger` | `#D32F2F` | Delete button |
| `--warning` | `#FF9800` | Delete warning icon |
| `--warning-bg` | `#FFF3E0` | Warning detail box |
| `--bg-surface` | `#FFFFFF` | Dialog background |
| `--bg-page` | `#FAFAFA` | Page background |
| `--bg-tag-card` | `#FFFFFF` | Tag card background |
| `--text-primary` | `#212121` | Tag name |
| `--text-secondary` | `#757575` | Hex code, count |
| `--border` | `#E0E0E0` | Card borders, inputs |

### Preset Colors (for picker)
| Name | Hex |
|------|-----|
| Red | #F44336 |
| Green | #4CAF50 |
| Blue | #2196F3 |
| Purple | #9C27B0 |
| Orange | #FF5722 |
| Cyan | #00BCD4 |
| Pink | #E91E63 |
| Amber | #FFC107 |
| Lime | #CDDC39 |
| Teal | #009688 |
| Indigo | #3F51B5 |
| Gray | #607D8B |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Page title |
| `--font-body` | sans-serif | Body text |
| `--font-mono` | monospace | Hex codes |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--card-padding` | 16px | Tag card padding |
| `--color-dot` | 16px | Color dot size |
| `--swatch-size` | 32px | Color swatch size |
| `--swatch-gap` | 8px | Gap between swatches |
| `--dialog-padding` | 24px | Dialog padding |
| `--field-gap` | 16px | Between form fields |

---

## Components

### 1. Page Header

| Element | Description |
|---------|-------------|
| Title | "Tags" (serif, large) |
| Add Button | Blue, right side, "+ Add Tag" |

### 2. Search Input

| Element | Description |
|---------|-------------|
| Icon | 🔍 magnifying glass |
| Placeholder | "Filter tags..." |
| Behavior | Real-time filtering |

### 3. Tag Card

| Element | Description |
|---------|-------------|
| Layout | Horizontal row |
| Color Dot | 16px circle with tag color |
| Name | Bold text |
| Hex Code | Monospace, secondary color |
| Count | "N transactions" |
| Actions | Edit/Delete icons (hover reveal) |

**States**:
| State | Visual |
|-------|--------|
| Default | All elements visible, no actions |
| Hover | Edit/Delete icons appear |
| Loading | Skeleton loader |

### 4. Edit Icon Button

| Element | Description |
|---------|-------------|
| Icon | ✏️ (pencil) |
| Visibility | Show on hover |
| Action | Open Edit dialog |

### 5. Delete Icon Button

| Element | Description |
|---------|-------------|
| Icon | 🗑️ (trash) |
| Visibility | Show on hover |
| Action | Open Delete confirmation |

### 6. Add/Edit Dialog

| Element | Description |
|---------|-------------|
| Title | "Create Tag" or "Edit Tag" |
| Name Input | Text input for tag name |
| Color Picker | Grid of 12 preset swatches |
| Preview | Live tag chip preview |
| Footer | Cancel + Save buttons |

### 7. Color Swatch

| Element | Description |
|---------|-------------|
| Shape | Square with rounded corners |
| Size | 32px × 32px |
| Selected | Blue border ring |

**States**:
| State | Visual |
|-------|--------|
| Default | Square with color fill |
| Hover | Slight scale up (1.1) |
| Selected | Blue ring border |

### 8. Tag Preview Chip

| Element | Description |
|---------|-------------|
| Layout | Pill shape with dot + text |
| Color Dot | 8px circle |
| Text | Tag name |
| Update | Real-time as user types/picks |

### 9. Delete Confirmation Dialog

| Element | Description |
|---------|-------------|
| Icon | ⚠️ amber circle |
| Title | "Delete Tag?" |
| Tag Name | Shows tag being deleted |
| Warning Box | Yellow with transaction count |
| Keep Button | Secondary (gray) |
| Delete Button | Red |

### 10. Empty State

| Element | Description |
|---------|-------------|
| Illustration | Placeholder image |
| Heading | "No tags yet" |
| Description | "Create your first tag to organize your transactions" |
| Button | "Create Tag" primary button |

---

## Interactions

### Page Load
1. Fetch all tags for user
2. Fetch transaction counts per tag
3. Render tag list
4. Show empty state if no tags

### Search Input
1. User types query
2. Filter tags in real-time (no debounce needed for small list)
3. Show filtered results
4. Show "No tags match your search" if empty

### Add Tag Click
1. Open Create dialog
2. Clear any previous values
3. Focus on name input
4. Default color: first swatch selected

### Name Input
1. User types tag name
2. Update preview chip in real-time
3. Max 64 characters

### Color Swatch Click
1. Select swatch (blue ring)
2. Update preview chip color
3. Update hex code display

### Preview Chip
1. Shows live preview
2. Updates instantly on name/color change
3. Format: [● Tag Name]

### Save Tag Click
1. Validate: name required, unique
2. If error: Show error message
3. If valid: Show loading
4. Call API: POST or PUT
5. On success: Close dialog, refresh list
6. On error: Show error message

### Edit Tag Click
1. Open Edit dialog with tag data
2. Pre-fill name and color
3. Focus on name input

### Delete Tag Click
1. Open confirmation dialog
2. Show warning with transaction count
3. If "Keep Tag": Close dialog
4. If "Delete": Show loading
5. Call API: DELETE
6. On success: Close dialog, refresh list, toast "Tag deleted"
7. On error: Show error message

### Cancel/Close Click
1. Close dialog
2. Discard changes

### Escape Key
1. Close any open dialog

### Click Outside Dialog
1. Close dialog
2. Discard changes

---

## Validation Rules

| Rule | Error Message |
|------|---------------|
| Name required | "Tag name is required" |
| Name unique | "A tag with this name already exists" |
| Name max length | "Tag name must be 64 characters or less" |
| Color required | Default to first swatch |

---

## API Contract

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

### Get Tag Stats
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

### Create Tag
```
POST /api/v1/tags

Request:
{ "name": "Food", "color": "#FF5722" }

Response:
{
  "success": true,
  "result": { "id": 1, "name": "Food", "color": "#FF5722", "sortOrder": 1 }
}
```

### Update Tag
```
PUT /api/v1/tags/{id}

Request:
{ "name": "Groceries", "color": "#4CAF50" }

Response:
{
  "success": true,
  "result": { "id": 1, "name": "Groceries", "color": "#4CAF50", "sortOrder": 1 }
}
```

### Delete Tag
```
DELETE /api/v1/tags/{id}

Response:
{ "success": true, "result": null }
```

---

## Database Schema

```sql
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(64) NOT NULL,
    color VARCHAR(7) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    UNIQUE(user_id, name)
);

CREATE TABLE transaction_tags (
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (transaction_id, tag_id)
);
```

---

## Backend Implementation Tasks

### Entity
- [ ] `Tag` entity class
- [ ] `TransactionTag` join table (no entity needed)

### Migration
- [ ] `V4__add_tags.sql` — create tables + indexes
- [ ] `V5__add_sort_order.sql` — add sort_order to existing tables

### Repository
- [ ] `TagRepository`
- [ ] `TransactionTagRepository`

### Service
- [ ] `TagService` — CRUD operations
- [ ] `TagService.getTagStats()` — transaction counts
- [ ] Link/unlink tags in `TransactionService`

### Controller
- [ ] `GET /api/v1/tags`
- [ ] `POST /api/v1/tags`
- [ ] `PUT /api/v1/tags/{id}`
- [ ] `DELETE /api/v1/tags/{id}`
- [ ] `GET /api/v1/tags/stats`

### DTO
- [ ] `TagDto`
- [ ] `CreateTagRequest`
- [ ] `UpdateTagRequest`
- [ ] `TagStatsDto`

### Tests
- [ ] Unit tests for TagService
- [ ] Integration tests for endpoints

---

## Frontend Implementation Tasks

### Pages
- [ ] `tags.vue` — main tags page

### Components
- [ ] `TagCard.vue` — tag list item
- [ ] `TagDialog.vue` — create/edit dialog
- [ ] `TagDeleteDialog.vue` — delete confirmation
- [ ] `ColorPicker.vue` — color swatch picker
- [ ] `TagPreview.vue` — preview chip

### Store
- [ ] `useTagsStore()` — tags state

### API
- [ ] GET /api/v1/tags
- [ ] GET /api/v1/tags/stats
- [ ] POST /api/v1/tags
- [ ] PUT /api/v1/tags/{id}
- [ ] DELETE /api/v1/tags/{id}

### i18n Keys
- [ ] `tags.title` = "Tags"
- [ ] `tags.add` = "Add Tag"
- [ ] `tags.edit` = "Edit Tag"
- [ ] `tags.delete` = "Delete Tag"
- [ ] `tags.name` = "Name"
- [ ] `tags.color` = "Color"
- [ ] `tags.preview` = "Preview"
- [ ] `tags.search` = "Filter tags..."
- [ ] `tags.noTags` = "No tags yet"
- [ ] `tags.createFirst` = "Create your first tag to organize your transactions"
- [ ] `tags.transactions` = "{count} transactions"
- [ ] `tags.deleteWarning` = "This tag is used in {count} transactions."
- [ ] `tags.keep` = "Keep Tag"
- [ ] `tags.save` = "Save"
- [ ] `tags.cancel` = "Cancel"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No tags | Show empty state |
| Many tags | Virtual scroll if > 50 |
| Tag name duplicate | Show validation error |
| Tag with transactions deleted | Confirm dialog shows count |
| Color picker on mobile | Tap to select, no hover |
| Long tag name | Truncate with ellipsis |

---

## States Summary

| State | Visual |
|-------|--------|
| Loading | Skeleton loaders |
| Empty | Empty state with illustration |
| With Tags | List of tag cards |
| Filtering | Filtered list |
| Dialog Open | Modal overlay |
| Saving | Spinner on Save button |
| Deleting | Spinner on Delete button |
| Error | Error message in dialog |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*