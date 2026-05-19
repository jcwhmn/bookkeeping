# Page Design - Login

## Page Purpose
Entry point for the application. Users authenticate to access their financial data.

## Route
`/login`

## User Flow
```
┌─────────────────────────────────────────────┐
│  [Logo] Bookkeeping                         │
│                                             │
│  ┌─────────────────────────────────────┐   │
│  │ Username                             │   │
│  └─────────────────────────────────────┘   │
│  ┌─────────────────────────────────────┐   │
│  │ Password              [👁]          │   │
│  └─────────────────────────────────────┘   │
│                                             │
│  [    Sign In    ]                          │
│                                             │
│  ─── or ───                                 │
│                                             │
│  [  Register ]                              │
│                                             │
└─────────────────────────────────────────────┘
```

## Components

### Header
- App logo/icon
- App name "Bookkeeping"

### Login Form
| Field | Type | Validation | Notes |
|-------|------|------------|-------|
| Username | Text | Required, 3-32 chars | Email or username |
| Password | Password | Required, min 6 chars | Show/hide toggle |

### Actions
- **Sign In** button (primary, full width)
- **Register** link (text button, secondary)

### Footer
- Language selector (EN/中文)
- Version info (v0.1.0)

## States

### Default State
- Empty form, Sign In disabled until valid input

### Loading State
- Button shows spinner
- Form inputs disabled

### Error State
- Error message below form
- Inputs highlighted with red border

### Success State
- Redirect to dashboard

## Design Tokens
- Primary color: #1976D2 (blue)
- Background: #FAFAFA
- Card: white with shadow
- Border radius: 8px
- Input height: 48px
- Button height: 48px

## API Integration

### Endpoint
```
POST /api/v1/auth/login
Content-Type: application/json

{"username": "demo", "password": "demo123"}
```

### Success Response
```json
{
  "success": true,
  "result": {
    "token": "eyJ...",
    "refreshToken": "...",
    "expiresAt": 1717184000,
    "user": {
      "id": "1",
      "username": "demo",
      "nickname": "Demo User",
      "defaultCurrency": "USD"
    }
  }
}
```

### Error Response
```json
{
  "success": false,
  "errorCode": 201001,
  "errorMessage": "Invalid username or password"
}
```

## Responsive Behavior

| Breakpoint | Layout |
|------------|--------|
| Desktop | Centered card, max-width 400px |
| Mobile | Full width with padding 16px |

## i18n Keys
- `auth.login.title` = "Sign In"
- `auth.login.username` = "Username"
- `auth.login.password` = "Password"
- `auth.login.submit` = "Sign In"
- `auth.login.register` = "Create an account"
- `auth.login.forgot` = "Forgot password?"

## OpenDesign Reference
Create login page with the above specifications in OpenDesign.