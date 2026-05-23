# User Settings Page — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Settings Page Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Settings                                                                     │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Profile                                                                  │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  ┌───────────┐                                                            │  │
│  │  │           │   Upload Photo                                             │  │
│  │  │  (avatar) │   (hover overlay)                                          │  │
│  │  │           │                                                            │  │
│  │  └───────────┘                                                            │  │
│  │                                                                           │  │
│  │  Username                                                               │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ demo                                              (display only)      │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Email                                                                  │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ demo@example.com                                   (display only)      │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Nickname                                                               │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ Demo User                                              [Save]        │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Preferences                                                              │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  Language                                                                │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ English (US)                                              [▼]       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Currency                                                                │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ USD ($)                                                       [▼]  │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Date Format                                                             │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ MM/DD/YYYY                                                    [▼]  │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Time Zone                                                               │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ Asia/Shanghai (UTC+8)                           (read-only)         │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Security                                                    [▲]          │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  Last password change: Never                                            │  │
│  │                                                                           │  │
│  │  Current Password                                                       │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ •••••••••••                                                        │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  New Password                                                           │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ •••••••••••                          [ strength bar ████░░░░░░ ]    │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Confirm Password                                                       │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ •••••••••••                                                        │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │                           [ Update Password ]                           │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  🔴 Danger Zone                                                          │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  Deleting your account will permanently remove all your data.            │  │
│  │  This action cannot be undone.                                           │  │
│  │                                                                           │  │
│  │                         [ Delete Account ]                              │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Avatar Hover State

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Profile                                                                  │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  ┌─────────────────────────────────┐                                     │  │
│  │  │ ╭─────────────────────────────╮ │                                     │  │
│  │  │ │                             │ │                                     │  │
│  │  │ │     📷 Upload Photo         │ │  (dark overlay)                      │  │
│  │  │ │                             │ │                                     │  │
│  │  │ ╰─────────────────────────────╯ │                                     │  │
│  │  └─────────────────────────────────┘                                     │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Password Strength Indicator

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│  New Password                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │ ••••••••••••••••                                                         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  Strength:  ████████░░░░░░░░░░░░░  Strong                                         │
│            (green bar)                                                           │
│                                                                                  │
│  Requirements:                                                                    │
│  ✓ At least 8 characters                                                         │
│  ✓ Contains uppercase letter                                                    │
│  ✓ Contains number                                                              │
│  ✗ Contains special character                                                   │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Delete Account Confirmation Dialog

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Delete Account                                        [✕]              │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │                          🔴                                              │  │
│  │                     (red warning icon)                                  │  │
│  │                                                                           │  │
│  │                 Delete Account?                                          │  │
│  │                                                                           │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ⚠️ This will permanently delete:                                 │   │  │
│  │  │                                                                       │   │  │
│  │  │ • All your accounts and transaction history                       │   │  │
│  │  │ • All your categories                                             │   │  │
│  │  │ • All your budgets and tags                                      │   │  │
│  │  │ • Your profile information                                        │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Type "demo" to confirm:                                                │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │                                                                       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │              [ Cancel ]                          [ Delete Account ]     │  │
│  │               (secondary)                      (red, disabled)           │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | `#1976D2` | Save buttons, links |
| `--danger` | `#D32F2F` | Delete button, danger zone |
| `--danger-bg` | `#FFEBEE` | Danger zone background |
| `--danger-border` | `#EF5350` | Danger zone border |
| `--warning-bg` | `#FFF3E0` | Warning box background |
| `--bg-surface` | `#FFFFFF` | Section backgrounds |
| `--bg-page` | `#FAFAFA` | Page background |
| `--text-primary` | `#212121` | Labels, values |
| `--text-secondary` | `#9E9E9E` | Display-only fields, hints |
| `--border` | `#E0E0E0` | Section borders, inputs |
| `--strength-weak` | `#D32F2F` | Weak password |
| `--strength-medium` | `#FF9800` | Medium password |
| `--strength-strong` | `#4CAF50` | Strong password |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Page title |
| `--font-body` | sans-serif | Body text |
| `--font-mono` | monospace | Passwords, username display |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--section-gap` | 24px | Between sections |
| `--field-gap` | 16px | Between fields |
| `--avatar-size` | 80px | Avatar dimensions |
| `--button-height` | 40px | Action buttons |

---

## Components

### 1. Page Header

| Element | Description |
|---------|-------------|
| Title | "Settings" (serif, large) |

### 2. Section Card

| Element | Description |
|---------|-------------|
| Title | Section name (uppercase, small, gray) |
| Border | Light gray border |
| Padding | 24px |

### 3. Avatar

| Element | Description |
|---------|-------------|
| Shape | Circle, 80px |
| Default | Placeholder image or initials |
| Hover | Dark overlay with upload icon |
| Click | Opens file picker |

**States**:
| State | Visual |
|-------|--------|
| Default | Avatar or placeholder |
| Hover | Overlay with 📷 icon |
| Uploading | Spinner on avatar |
| Error | Error icon, retry |

### 4. Display-Only Field

| Element | Description |
|---------|-------------|
| Style | Grayed out text, disabled |
| Border | None or subtle |

### 5. Editable Field with Save

| Element | Description |
|---------|-------------|
| Layout | Input + Save button inline |
| Button | Blue, appears when changed |
| Save | Saves on click |

### 6. Dropdown Select

| Element | Description |
|---------|-------------|
| Style | Standard dropdown |
| Arrow | ▼ indicator |

### 7. Read-Only Field

| Element | Description |
|---------|-------------|
| Style | Gray text, no focus state |
| Note | "(read-only)" label |

### 8. Password Panel

| Element | Description |
|---------|-------------|
| Header | "Security" with expand/collapse chevron |
| Content | Password fields (collapsed by default) |
| Animation | Smooth expand/collapse |

### 9. Password Strength Indicator

| Element | Description |
|---------|-------------|
| Bar | Horizontal fill bar |
| Colors | Red (weak), Yellow (medium), Green (strong) |
| Text | "Weak", "Medium", "Strong" label |
| Checklist | Requirements with ✓/✗ icons |

**Strength Levels**:
| Level | Score | Color |
|-------|-------|-------|
| Weak | 1-2 | Red |
| Medium | 3-4 | Yellow |
| Strong | 5 | Green |

### 10. Danger Zone

| Element | Description |
|---------|-------------|
| Border | Red border, light red background |
| Icon | 🔴 (red dot) |
| Title | "Danger Zone" in red |

### 11. Delete Confirmation Modal

| Element | Description |
|---------|-------------|
| Icon | 🔴 red circle |
| Title | "Delete Account?" |
| Warning Box | Yellow with consequences list |
| Confirm Input | Text field to type username |
| Delete Button | Red, disabled until name matches |

---

## Interactions

### Page Load
1. Fetch current user profile
2. Fetch user preferences
3. Render all sections

### Avatar Upload Click
1. Open file picker
2. User selects image
3. Show preview
4. User confirms
5. Upload to server
6. Show success/error

### Nickname Change
1. User edits nickname
2. Save button appears
3. Click Save
4. Show loading
5. Call API: PUT /api/v1/users/profile
6. Show toast

### Preference Dropdown Change
1. User selects option
2. Auto-save (or save button)
3. Call API: PUT /api/v1/users/preferences
4. Show toast

### Security Panel Toggle
1. Click header chevron
2. Smooth expand/collapse animation
3. Persist state (optional)

### Password Field Change
1. User types new password
2. Strength indicator updates in real-time
3. Requirements checklist updates
4. Validate match on blur

### Update Password Click
1. Validate all fields filled
2. Validate passwords match
3. If error: Show message
4. If valid: Show loading
5. Call API: PUT /api/v1/users/password
6. On success: Clear fields, show toast
7. On error: Show error

### Delete Account Click
1. Open confirmation dialog
2. Show warning with consequences
3. User types username to confirm
4. Delete button enabled when matches
5. Click Delete
6. Show final confirmation
7. Call API: DELETE /api/v1/users/account
8. Redirect to login

### Escape / Click Outside
1. Close dialogs
2. Discard changes

---

## Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| Nickname | Max 64 chars | "Nickname too long" |
| Password | Min 8 chars | "Password must be at least 8 characters" |
| Password | Uppercase required | "Must contain an uppercase letter" |
| Password | Number required | "Must contain a number" |
| Password | Special char required | "Must contain a special character" |
| Confirm | Must match | "Passwords do not match" |
| Delete | Username must match | "Username does not match" |

---

## API Contract

### Get Profile
```
GET /api/v1/users/me

Response:
{
  "success": true,
  "result": {
    "id": 1,
    "username": "demo",
    "email": "demo@example.com",
    "nickname": "Demo User",
    "avatarUrl": null,
    "defaultCurrency": "USD",
    "language": "en-US",
    "dateFormat": "MM/DD/YYYY",
    "timezone": "Asia/Shanghai",
    "lastPasswordChange": null
  }
}
```

### Update Profile
```
PUT /api/v1/users/profile

Request:
{ "nickname": "New Nickname" }

Response:
{ "success": true, "result": { ... updated user ... } }
```

### Update Preferences
```
PUT /api/v1/users/preferences

Request:
{ 
  "language": "zh-CN",
  "currency": "CNY",
  "dateFormat": "YYYY-MM-DD"
}

Response:
{ "success": true, "result": { ... } }
```

### Change Password
```
PUT /api/v1/users/password

Request:
{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword"
}

Response:
{ "success": true, "result": { "lastPasswordChange": 1747862400 } }
```

### Upload Avatar
```
POST /api/v1/users/avatar

Request: multipart/form-data with image file

Response:
{ "success": true, "result": { "avatarUrl": "/uploads/avatar/xxx.jpg" } }
```

### Delete Account
```
DELETE /api/v1/users/account

Request: (requires special header or token)

Response:
{ "success": true, "result": null }
```

---

## Backend Implementation Tasks

### Entity
- [ ] `User` — add avatar_url, date_format, timezone fields (migration)

### Migration
- [ ] `V4__add_user_settings.sql` — avatar_url, date_format, timezone

### Service
- [ ] `UserService.updateProfile(nickname)`
- [ ] `UserService.updatePreferences(prefs)`
- [ ] `UserService.changePassword(current, new)`
- [ ] `UserService.uploadAvatar(file)`
- [ ] `UserService.deleteAccount()`

### Controller
- [ ] `PUT /api/v1/users/profile`
- [ ] `PUT /api/v1/users/preferences`
- [ ] `PUT /api/v1/users/password`
- [ ] `POST /api/v1/users/avatar`
- [ ] `DELETE /api/v1/users/account`

### DTO
- [ ] `UpdateProfileRequest`
- [ ] `UpdatePreferencesRequest`
- [ ] `ChangePasswordRequest`
- [ ] `UserPreferencesDto`

### Storage
- [ ] Avatar file upload handling
- [ ] Image resizing (optional)
- [ ] File storage location

### Tests
- [ ] Unit tests for password strength
- [ ] Integration tests for all endpoints

---

## Frontend Implementation Tasks

### Pages
- [ ] `settings.vue` — main settings page

### Components
- [ ] `ProfileSection.vue` — profile fields
- [ ] `AvatarUpload.vue` — avatar with upload
- [ ] `EditableField.vue` — reusable editable field
- [ ] `PreferencesSection.vue` — preference dropdowns
- [ ] `SecuritySection.vue` — password panel
- [ ] `PasswordStrength.vue` — strength indicator
- [ ] `DangerZoneSection.vue` — delete account
- [ ] `DeleteConfirmDialog.vue` — confirmation modal

### Composables
- [ ] `useProfile()` — profile state
- [ ] `usePassword()` — password change logic
- [ ] `useDeleteAccount()` — deletion flow

### Store
- [ ] `useUserStore()` — user preferences

### API
- [ ] GET /api/v1/users/me
- [ ] PUT /api/v1/users/profile
- [ ] PUT /api/v1/users/preferences
- [ ] PUT /api/v1/users/password
- [ ] POST /api/v1/users/avatar
- [ ] DELETE /api/v1/users/account

### i18n Keys
- [ ] `settings.title` = "Settings"
- [ ] `settings.profile` = "Profile"
- [ ] `settings.uploadPhoto` = "Upload Photo"
- [ ] `settings.username` = "Username"
- [ ] `settings.email` = "Email"
- [ ] `settings.nickname` = "Nickname"
- [ ] `settings.preferences` = "Preferences"
- [ ] `settings.language` = "Language"
- [ ] `settings.currency` = "Currency"
- [ ] `settings.dateFormat` = "Date Format"
- [ ] `settings.timeZone` = "Time Zone"
- [ ] `settings.security` = "Security"
- [ ] `settings.changePassword` = "Change Password"
- [ ] `settings.currentPassword` = "Current Password"
- [ ] `settings.newPassword` = "New Password"
- [ ] `settings.confirmPassword` = "Confirm Password"
- [ ] `settings.updatePassword` = "Update Password"
- [ ] `settings.lastPasswordChange` = "Last password change"
- [ ] `settings.dangerZone` = "Danger Zone"
- [ ] `settings.deleteAccount` = "Delete Account"
- [ ] `settings.strength` = "Strength"
- [ ] `settings.weak` = "Weak"
- [ ] `settings.medium` = "Medium"
- [ ] `settings.strong` = "Strong"
- [ ] `settings.cancel` = "Cancel"
- [ ] `settings.save` = "Save"
- [ ] `settings.confirmDelete` = 'Type "{username}" to confirm'

---

## Edge Cases

| Case | Handling |
|------|----------|
| Avatar upload fails | Show error, retry option |
| Password wrong | Show "Current password incorrect" |
| Username not matched | Delete button disabled |
| Already deleting | Prevent double-click |
| Preferences save fails | Revert to previous, show error |

---

## States Summary

| State | Visual |
|-------|--------|
| Loading | Skeleton loaders |
| Loaded | All sections populated |
| Saving | Spinner on Save button |
| Password Panel Closed | Chevron pointing right |
| Password Panel Open | Chevron pointing down |
| Password Strength | Colored bar + label |
| Delete Dialog Open | Modal overlay |
| Deleting | Full page loading |
| Deleted | Redirect to login |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*