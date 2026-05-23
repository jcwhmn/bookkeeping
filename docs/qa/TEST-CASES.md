# QA Test Cases - Bookkeeping App v0.1.0

## Test Environment

| Component | Value |
|-----------|-------|
| Backend | http://localhost:8080 |
| Frontend | http://localhost:3002 (or next available) |
| Database | PostgreSQL bookkeeping_dev |
| Test Account | demo / demo123 |
| Browser | Chrome latest |

---

## 1. Authentication & Authorization

### 1.1 Manual Test Cases

#### TC-AUTH-001: Register new account
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-001 |
| Title | Register new account with valid data |
| Priority | P0 |
| Precondition | User is on `/register` page |
| Steps | 1. Fill username: `testuser001` 2. Fill email: `test001@example.com` 3. Fill password: `Test1234` 4. Click Register |
| Expected | Success message, redirect to `/login` |
| Actual | |
| Status | |

#### TC-AUTH-002: Login with new account
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-002 |
| Title | Login with newly registered account |
| Priority | P0 |
| Precondition | TC-AUTH-001 passed |
| Steps | 1. Fill username: `testuser001` 2. Fill password: `Test1234` 3. Click Sign In |
| Expected | Redirect to Dashboard, token saved in cookie |
| Actual | |
| Status | |

#### TC-AUTH-003: Login with wrong password
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-003 |
| Title | Login fails with incorrect password |
| Priority | P1 |
| Precondition | User exists |
| Steps | 1. Fill username: `demo` 2. Fill password: `wrongpassword` 3. Click Sign In |
| Expected | Error: "Invalid username or password" |
| Actual | |
| Status | |

#### TC-AUTH-004: Login with non-existent user
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-004 |
| Title | Login fails with non-existent username |
| Priority | P1 |
| Precondition | None |
| Steps | 1. Fill username: `nonexistent` 2. Fill password: `anypassword` 3. Click Sign In |
| Expected | Error: "Invalid username or password" |
| Actual | |
| Status | |

#### TC-AUTH-005: Duplicate username registration
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-005 |
| Title | Cannot register with existing username |
| Priority | P1 |
| Precondition | User `testuser001` exists |
| Steps | 1. Go to `/register` 2. Fill username: `testuser001` 3. Fill email: `another@example.com` 4. Fill password: `Test1234` 5. Click Register |
| Expected | Error: "Username already exists" |
| Actual | |
| Status | |

#### TC-AUTH-006: JWT token authentication
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-006 |
| Title | Subsequent requests use JWT token |
| Priority | P0 |
| Precondition | Logged in, token stored in cookie |
| Steps | 1. Refresh page 2. Navigate to `/accounts` |
| Expected | Page loads successfully with user data |
| Actual | |
| Status | |

#### TC-AUTH-007: Logout clears token
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-007 |
| Title | Logout removes token and redirects to login |
| Priority | P1 |
| Precondition | Logged in |
| Steps | 1. Click user avatar 2. Click Sign Out |
| Expected | Redirect to `/login`, token cookie cleared |
| Actual | |
| Status | |

#### TC-AUTH-008: Unauthenticated access to protected page
| Field | Value |
|-------|-------|
| Test ID | TC-AUTH-008 |
| Title | Redirect to login when accessing protected page without token |
| Priority | P0 |
| Precondition | No token cookie |
| Steps | 1. Navigate to `http://localhost:3002/` directly |
| Expected | Redirect to `/login` |
| Actual | |
| Status | |

### 1.2 Automated Test Cases

```java
// src/test/java/com/bookkeeping/supporting/auth/AuthServiceTest.java
// TC-AUTH-AUTO-001: Register creates user with correct defaults
@Test
void register_createsUserWithCorrectDefaults() {
    RegisterRequest req = new RegisterRequest("newuser", "new@test.com", "pass123");
    UserDto result = authService.register(req);
    
    assertNotNull(result.getId());
    assertEquals("newuser", result.getUsername());
    assertEquals("new@test.com", result.getEmail());
    assertTrue(result.isEmailVerified()); // After fix
    assertFalse(result.isDisabled());
}

// TC-AUTH-AUTO-002: Login returns token for valid credentials
@Test
void login_returnsTokenForValidCredentials() {
    LoginRequest req = new LoginRequest("demo", "demo123");
    LoginResponse resp = authService.login(req);
    
    assertNotNull(resp.token());
    assertNotNull(resp.token());
    assertEquals("demo", resp.user().getUsername());
}

// TC-AUTH-AUTO-003: Login fails with wrong password
@Test
void login_throwsForWrongPassword() {
    LoginRequest req = new LoginRequest("demo", "wrongpassword");
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
    assertEquals(ResultCode.AUTHENTICATION_FAILED, ex.getResultCode());
}

// TC-AUTH-AUTO-004: Login fails for disabled user
@Test
void login_throwsForDisabledUser() {
    User disabled = createUser();
    disabled.setDisabled(true);
    userRepository.save(disabled);
    
    LoginRequest req = new LoginRequest(disabled.getUsername(), "pass123");
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
    assertEquals(ResultCode.USER_DISABLED, ex.getResultCode());
}

// TC-AUTH-AUTO-005: Register fails for duplicate username
@Test
void register_failsForDuplicateUsername() {
    RegisterRequest req = new RegisterRequest("demo", "another@test.com", "pass");
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(req));
    assertEquals(ResultCode.USERNAME_ALREADY_EXISTS, ex.getResultCode());
}

// TC-AUTH-AUTO-006: Register fails for duplicate email
@Test
void register_failsForDuplicateEmail() {
    RegisterRequest req = new RegisterRequest("newuser", "demo@example.com", "pass");
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(req));
    assertEquals(ResultCode.EMAIL_ALREADY_EXISTS, ex.getResultCode());
}
```

---

## 2. Account Management

### 2.1 Manual Test Cases

#### TC-ACC-001: Create new account
| Field | Value |
|-------|-------|
| Test ID | TC-ACC-001 |
| Title | Create CASH account |
| Priority | P0 |
| Precondition | Logged in |
| Steps | 1. Go to `/accounts` 2. Click "Add Account" 3. Fill name: `My Wallet` 4. Select Type: `CASH` 5. Fill Currency: `USD` 6. Fill Initial Balance: `500` 7. Click "Create" |
| Expected | Account appears in list, balance = $500.00 |
| Actual | |
| Status | |

#### TC-ACC-002: Create account with negative balance (credit card)
| Field | Value |
|-------|-------|
| Test ID | TC-ACC-002 |
| Title | Create CREDIT account with negative balance |
| Priority | P0 |
| Precondition | Logged in |
| Steps | 1. Go to `/accounts` 2. Click "Add Account" 3. Fill name: `Credit Card` 4. Select Type: `CREDIT` 5. Fill Currency: `USD` 6. Fill Initial Balance: `-1000` 7. Click "Create" |
| Expected | Account created with balance = -$1000.00, shown in red |
| Actual | |
| Status | |

#### TC-ACC-003: Edit account name
| Field | Value |
|-------|-------|
| Test ID | TC-ACC-003 |
| Title | Update account name |
| Priority | P1 |
| Precondition | Account exists |
| Steps | 1. Go to `/accounts` 2. Click edit icon on account card 3. Change name to `Updated Wallet` 4. Click "Update" |
| Expected | Account name updated |
| Actual | |
| Status | |

#### TC-ACC-004: Archive account (soft delete)
| Field | Value |
|-------|-------|
| Test ID | TC-ACC-004 |
| Title | Archive account removes from list |
| Priority | P1 |
| Precondition | Account has no transactions |
| Steps | 1. Go to `/accounts` 2. Click archive icon on account card 3. Confirm |
| Expected | Account no longer appears in list |
| Actual | |
| Status | |

#### TC-ACC-005: Filter accounts by type
| Field | Value |
|-------|-------|
| Test ID | TC-ACC-005 |
| Title | Filter shows only matching account types |
| Priority | P2 |
| Precondition | Multiple account types exist |
| Steps | 1. Go to `/accounts` 2. Click "CREDIT" tab |
| Expected | Only Credit accounts shown |
| Actual | |
| Status | |

#### TC-ACC-006: Total balance calculation
| Field | Value |
|-------|-------|
| Test ID | TC-ACC-006 |
| Title | Total balance sums all account balances |
| Priority | P1 |
| Precondition | Multiple accounts with varying balances |
| Steps | 1. Go to `/accounts` 2. Check "Total Balance" bar |
| Expected | Total = sum of all account balances |
| Actual | |
| Status | |

### 2.2 Automated Test Cases

```java
// src/test/java/com/bookkeeping/core/account/AccountServiceTest.java
// TC-ACC-AUTO-001: Create account stores correct balance
@Test
void createAccount_storesCorrectBalance() {
    CreateAccountRequest req = new CreateAccountRequest("Test", AccountType.CASH, "USD", 50000L);
    AccountDto result = accountService.createAccount(req);
    
    assertEquals(50000L, result.balance()); // $500.00 in cents
    assertEquals("Test", result.name());
    assertEquals(AccountType.CASH, result.accountType());
}

// TC-ACC-AUTO-002: Create duplicate account name fails
@Test
void createAccount_failsForDuplicateName() {
    createAccount("Wallet", AccountType.CASH);
    
    CreateAccountRequest req = new CreateAccountRequest("Wallet", AccountType.CASH, "USD", 0L);
    BusinessException ex = assertThrows(BusinessException.class, 
        () -> accountService.createAccount(req));
    assertEquals(ResultCode.ACCOUNT_ALREADY_EXISTS, ex.getResultCode());
}

// TC-ACC-AUTO-003: Get accounts returns only current user
@Test
void getAccounts_returnsOnlyCurrentUserAccounts() {
    // Create accounts for current user
    accountService.createAccount(new CreateAccountRequest("A1", AccountType.CASH, "USD", 100L));
    accountService.createAccount(new CreateAccountRequest("A2", AccountType.CHECKING, "USD", 200L));
    
    List<AccountDto> accounts = accountService.getCurrentUserAccounts();
    
    assertEquals(2, accounts.size());
    assertTrue(accounts.stream().allMatch(a -> a.name().equals("A1") || a.name().equals("A2")));
}

// TC-ACC-AUTO-004: Soft delete marks account as deleted
@Test
void deleteAccount_marksAsDeleted() {
    AccountDto account = accountService.createAccount(
        new CreateAccountRequest("ToDelete", AccountType.CASH, "USD", 0L));
    
    accountService.deleteAccount(account.id());
    
    List<AccountDto> accounts = accountService.getCurrentUserAccounts();
    assertTrue(accounts.stream().noneMatch(a -> a.id().equals(account.id())));
}

// TC-ACC-AUTO-005: Update balance changes account balance
@Test
void updateBalance_changesBalance() {
    AccountDto account = accountService.createAccount(
        new CreateAccountRequest("BalanceTest", AccountType.CASH, "USD", 10000L));
    
    accountService.updateBalance(account.id(), 5000L); // Add $50
    
    AccountDto updated = accountService.getAccount(account.id());
    assertEquals(15000L, updated.balance());
}
```

---

## 3. Transaction Management

### 3.1 Manual Test Cases

#### TC-TXN-001: Create income transaction
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-001 |
| Title | Create income adds to account balance |
| Priority | P0 |
| Precondition | Account exists with balance $0 |
| Steps | 1. Go to `/transactions` 2. Click "Add Transaction" 3. Select "Income" 4. Fill amount: `1000` 5. Select account 6. Fill notes: `Salary` 7. Click "Save Transaction" |
| Expected | Account balance increases by $1000. Transaction appears in list. |
| Actual | |
| Status | |

#### TC-TXN-002: Create expense transaction
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-002 |
| Title | Create expense subtracts from account balance |
| Priority | P0 |
| Precondition | Account has balance > $100 |
| Steps | 1. Go to `/transactions` 2. Click "Add Transaction" 3. Select "Expense" 4. Fill amount: `50` 5. Select account 6. Select category 7. Fill notes: `Lunch` 8. Click "Save Transaction" |
| Expected | Account balance decreases by $50. Transaction appears in list with negative sign. |
| Actual | |
| Status | |

#### TC-TXN-003: Income transaction amount displayed correctly
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-003 |
| Title | Income amount shows green with + sign |
| Priority | P1 |
| Precondition | Income transaction exists |
| Steps | 1. Go to `/transactions` 2. Find income transaction |
| Expected | Amount displayed as +$XX.XX in green color |
| Actual | |
| Status | |

#### TC-TXN-004: Expense transaction amount displayed correctly
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-004 |
| Title | Expense amount shows red with - sign |
| Priority | P1 |
| Precondition | Expense transaction exists |
| Steps | 1. Go to `/transactions` 2. Find expense transaction |
| Expected | Amount displayed as -$XX.XX in red color |
| Actual | |
| Status | |

#### TC-TXN-005: Transaction shows correct account name
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-005 |
| Title | Transaction subtitle shows associated account |
| Priority | P0 |
| Precondition | Transactions exist for multiple accounts |
| Steps | 1. Go to `/transactions` 2. Check each transaction's subtitle |
| Expected | Transaction subtitle shows "AccountName · HH:MM" |
| Actual | |
| Status | |

#### TC-TXN-006: Filter transactions by type
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-006 |
| Title | Type filter shows only matching transactions |
| Priority | P1 |
| Precondition | Both income and expense transactions exist |
| Steps | 1. Go to `/transactions` 2. Click "Expense" tab |
| Expected | Only expense transactions shown |
| Actual | |
| Status | |

#### TC-TXN-007: Grouped by date
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-007 |
| Title | Transactions grouped under date headers |
| Priority | P1 |
| Precondition | Transactions on multiple dates |
| Steps | 1. Go to `/transactions` |
| Expected | Today/Yesterday/Mon DD YYYY headers above transactions |
| Actual | |
| Status | |

#### TC-TXN-008: Search transactions
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-008 |
| Title | Search finds matching transactions |
| Priority | P2 |
| Precondition | Multiple transactions with different notes |
| Steps | 1. Go to `/transactions` 2. Type "Lunch" in search |
| Expected | Only transactions containing "Lunch" shown |
| Actual | |
| Status | |

#### TC-TXN-009: Amount input accepts decimal
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-009 |
| Title | Amount field accepts and converts decimal input |
| Priority | P1 |
| Precondition | Create transaction dialog open |
| Steps | 1. Fill amount: `12.50` |
| Expected | Stored as 1250 cents ($12.50) |
| Actual | |
| Status | |

#### TC-TXN-010: Account balance updates after transaction
| Field | Value |
|-------|-------|
| Test ID | TC-TXN-010 |
| Title | Account balance on `/accounts` reflects transactions |
| Priority | P0 |
| Precondition | Account has existing transactions |
| Steps | 1. Create expense of $100 on account 2. Go to `/accounts` 3. Check that account's balance |
| Expected | Balance decreased by $100 |
| Actual | |
| Status | |

### 3.2 Automated Test Cases

```java
// src/test/java/com/bookkeeping/core/transaction/TransactionServiceTest.java
// TC-TXN-AUTO-001: Create income adds to account balance
@Test
void createTransaction_incomeAddsToBalance() {
    AccountDto account = accountService.createAccount(
        new CreateAccountRequest("Test", AccountType.CASH, "USD", 10000L));
    
    transactionService.createTransaction(
        new CreateTransactionRequest(2, account.id(), null, 5000L, "Bonus"));
    
    AccountDto updated = accountService.getAccount(account.id());
    assertEquals(15000L, updated.balance()); // 100 + 50
}

// TC-TXN-AUTO-002: Create expense subtracts from balance
@Test
void createTransaction_expenseSubtractsBalance() {
    AccountDto account = accountService.createAccount(
        new CreateAccountRequest("Test", AccountType.CASH, "USD", 10000L));
    
    transactionService.createTransaction(
        new CreateTransactionRequest(3, account.id(), categoryId, 3000L, "Lunch"));
    
    AccountDto updated = accountService.getAccount(account.id());
    assertEquals(7000L, updated.balance()); // 100 - 30
}

// TC-TXN-AUTO-003: Get recent transactions returns sorted list
@Test
void getRecentTransactions_returnsSortedByTimeDesc() {
    transactionService.createTransaction(req(3, a1, 100L, "Tx1", now - 1000));
    transactionService.createTransaction(req(2, a1, 200L, "Tx2", now - 500));
    transactionService.createTransaction(req(3, a1, 300L, "Tx3", now - 100));
    
    List<TransactionDto> txs = transactionService.getRecentTransactions(10);
    
    assertEquals(3, txs.size());
    assertTrue(txs.get(0).transactionTime() >= txs.get(1).transactionTime());
}

// TC-TXN-AUTO-004: Amount stored as cents (BIGINT)
@Test
void createTransaction_storesAmountAsCents() {
    // Input $123.45 = 12345 cents
    transactionService.createTransaction(
        new CreateTransactionRequest(2, accountId, null, 12345L, "Test"));
    
    TransactionDto tx = transactionService.getRecentTransactions(1).get(0);
    assertEquals(12345L, tx.amount());
}
```

---

## 4. Category Management

### 4.1 Manual Test Cases

#### TC-CAT-001: View income categories
| Field | Value |
|-------|-------|
| Test ID | TC-CAT-001 |
| Title | Income tab shows income categories |
| Priority | P1 |
| Precondition | Income categories exist |
| Steps | 1. Go to `/categories` 2. Click "Income" tab |
| Expected | Only INCOME type categories listed |
| Actual | |
| Status | |

#### TC-CAT-002: View expense categories
| Field | Value |
|-------|-------|
| Test ID | TC-CAT-002 |
| Title | Expense tab shows expense categories |
| Priority | P1 |
| Precondition | Expense categories exist |
| Steps | 1. Go to `/categories` 2. Click "Expense" tab |
| Expected | Only EXPENSE type categories listed |
| Actual | |
| Status | |

#### TC-CAT-003: Create new income category
| Field | Value |
|-------|-------|
| Test ID | TC-CAT-003 |
| Title | Create income category |
| Priority | P2 |
| Precondition | Logged in |
| Steps | 1. Go to `/categories` 2. Click "Add Category" 3. Fill name: `Freelance` 4. Select type: `Income` 5. Click "Save" |
| Expected | Category appears in Income list |
| Actual | |
| Status | |

#### TC-CAT-004: Create new expense category
| Field | Value |
|-------|-------|
| Test ID | TC-CAT-004 |
| Title | Create expense category |
| Priority | P2 |
| Precondition | Logged in |
| Steps | 1. Go to `/categories` 2. Click "Add Category" 3. Fill name: `Gym` 4. Select type: `Expense` 5. Click "Save" |
| Expected | Category appears in Expense list |
| Actual | |
| Status | |

#### TC-CAT-005: Cannot create category without name
| Field | Value |
|-------|-------|
| Test ID | TC-CAT-005 |
| Title | Form validation for empty name |
| Priority | P2 |
| Precondition | Create dialog open |
| Steps | 1. Click "Add Category" 2. Leave name empty 3. Click "Save" |
| Expected | Validation error, category not created |
| Actual | |
| Status | |

### 4.2 Automated Test Cases

```java
// TC-CAT-AUTO-001: Create income category
@Test
void createCategory_incomeType() {
    CategoryDto cat = categoryService.createCategory("Freelance", CategoryType.INCOME);
    assertEquals("Freelance", cat.name());
    assertEquals(CategoryType.INCOME, cat.categoryType());
}

// TC-CAT-AUTO-002: Create expense category
@Test
void createCategory_expenseType() {
    CategoryDto cat = categoryService.createCategory("Gym", CategoryType.EXPENSE);
    assertEquals("Gym", cat.name());
    assertEquals(CategoryType.EXPENSE, cat.categoryType());
}

// TC-CAT-AUTO-003: Get categories filtered by type
@Test
void getCategories_filtersByType() {
    categoryService.createCategory("Salary", CategoryType.INCOME);
    categoryService.createCategory("Food", CategoryType.EXPENSE);
    
    List<CategoryDto> incomeCats = categoryService.getCategoriesByType(CategoryType.INCOME);
    assertTrue(incomeCats.stream().allMatch(c -> c.categoryType() == CategoryType.INCOME));
}
```

---

## 5. Dashboard

### 5.1 Manual Test Cases

#### TC-DASH-001: Dashboard displays summary cards
| Field | Value |
|-------|-------|
| Test ID | TC-DASH-001 |
| Title | Dashboard shows 4 summary metric cards |
| Priority | P0 |
| Precondition | Logged in, accounts and transactions exist |
| Steps | 1. Navigate to `/` |
| Expected | 4 cards: Assets, Liabilities, Net Worth, This Month |
| Actual | |
| Status | |

#### TC-DASH-002: Assets card shows total positive balance
| Field | Value |
|-------|-------|
| Test ID | TC-DASH-002 |
| Title | Assets = sum of all account balances >= 0 |
| Priority | P1 |
| Precondition | Multiple accounts |
| Steps | 1. Check Assets card value |
| Expected | Assets = sum of accounts with balance >= 0 |
| Actual | |
| Status | |

#### TC-DASH-003: Liabilities card shows total negative balance
| Field | Value |
|-------|-------|
| Test ID | TC-DASH-003 |
| Title | Liabilities = sum of negative account balances |
| Priority | P1 |
| Precondition | Credit account exists with negative balance |
| Steps | 1. Check Liabilities card value |
| Expected | Liabilities = sum of accounts with negative balance |
| Actual | |
| Status | |

#### TC-DASH-004: Recent transactions section
| Field | Value |
|-------|-------|
| Test ID | TC-DASH-004 |
| Title | Dashboard shows last 5 transactions |
| Priority | P1 |
| Precondition | Transactions exist |
| Steps | 1. Scroll to "Recent Transactions" section |
| Expected | Up to 5 transactions with amounts and accounts |
| Actual | |
| Status | |

#### TC-DASH-005: View all link navigates to transactions
| Field | Value |
|-------|-------|
| Test ID | TC-DASH-005 |
| Title | View All link goes to transactions page |
| Priority | P2 |
| Precondition | On dashboard |
| Steps | 1. Click "View All →" in Recent Transactions |
| Expected | Navigate to `/transactions` |
| Actual | |
| Status | |

---

## 6. Profile Management

### 6.1 Manual Test Cases

#### TC-PROF-001: View profile page
| Field | Value |
|-------|-------|
| Test ID | TC-PROF-001 |
| Title | Profile page shows user info |
| Priority | P1 |
| Precondition | Logged in |
| Steps | 1. Click user avatar 2. Select "Profile" |
| Expected | Shows username, email, currency, language |
| Actual | |
| Status | |

#### TC-PROF-002: Update email
| Field | Value |
|-------|-------|
| Test ID | TC-PROF-002 |
| Title | Update user email |
| Priority | P2 |
| Precondition | On profile page |
| Steps | 1. Change email field 2. Click Save |
| Expected | Email updated, success message |
| Actual | |
| Status | |

---

## 7. UI/UX & Performance

### 7.1 Manual Test Cases

#### TC-UI-001: Page width constrained to 1200px
| Field | Value |
|-------|-------|
| Test ID | TC-UI-001 |
| Title | Content not full screen |
| Priority | P1 |
| Precondition | On any page |
| Steps | 1. Resize browser to 1920px wide |
| Expected | Content centered with margins on sides |
| Actual | |
| Status | |

#### TC-UI-002: Login page centered with max-width
| Field | Value |
|-------|-------|
| Test ID | TC-UI-002 |
| Title | Login card centered, not full width |
| Priority | P1 |
| Precondition | On `/login` |
| Steps | 1. View login page at full screen |
| Expected | Card width max 420px, centered |
| Actual | |
| Status | |

#### TC-UI-003: Loading states visible
| Field | Value |
|-------|-------|
| Test ID | TC-UI-003 |
| Title | Loading spinner when fetching data |
| Priority | P2 |
| Precondition | On transactions page |
| Steps | 1. Hard refresh page (Ctrl+F5) |
| Expected | Loading bar visible while data loads |
| Actual | |
| Status | |

#### TC-UI-004: Mobile responsive layout
| Field | Value |
|-------|-------|
| Test ID | TC-UI-004 |
| Title | Pages render correctly on mobile |
| Priority | P2 |
| Precondition | Use mobile device or resize to 375px |
| Steps | 1. Resize browser to 375px wide 2. Navigate to accounts |
| Expected | Cards stack vertically, readable text |
| Actual | |
| Status | |

---

## 8. API Integration Tests

```java
// src/integrationTest/java/com/bookkeeping/supporting/auth/AuthControllerIntegrationTest.java
// TC-API-AUTH-001: Login returns correct response structure
@Test
void login_returnsSuccessWithTokenAndUser() {
    HttpRequest request = HttpRequest.POST("/api/v1/auth/login",
        "{\"username\":\"demo\",\"password\":\"demo123\"}");
    
    HttpResponse response = client.execute(request);
    
    assertEquals(HttpStatus.OK, response.statusCode());
    assertTrue(response.body().contains("\"success\":true"));
    assertTrue(response.body().contains("\"token\":"));
    assertTrue(response.body().contains("\"user\":"));
}

// TC-API-AUTH-002: Unauthenticated request returns 401
@Test
void accounts_returns401WithoutToken() {
    HttpRequest request = HttpRequest.GET("/api/v1/accounts");
    
    HttpResponse response = client.execute(request);
    
    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
}

// TC-API-AUTH-003: Invalid token returns 401
@Test
void accounts_returns401WithInvalidToken() {
    HttpRequest request = HttpRequest.GET("/api/v1/accounts")
        .header("Authorization", "Bearer invalid.token.here");
    
    HttpResponse response = client.execute(request);
    
    assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
}
```

---

## 9. Data Integrity Tests

### 9.1 Amount Precision

```java
// TC-AMOUNT-001: Amount in cents stored correctly
@Test
void createTransaction_preservesCentsPrecision() {
    // 12345 cents = $123.45
    transactionService.createTransaction(req(2, accId, null, 12345L, "Test"));
    
    TransactionDto tx = transactionService.getRecentTransactions(1).get(0);
    assertEquals(12345L, tx.amount());
    assertEquals("$123.45", format(tx.amount())); // Display check
}

// TC-AMOUNT-002: Large amounts stored correctly
@Test
void createTransaction_handlesLargeAmounts() {
    // $999,999.99 = 99999999 cents
    transactionService.createTransaction(req(2, accId, null, 99999999L, "Large"));
    
    TransactionDto tx = transactionService.getRecentTransactions(1).get(0);
    assertEquals(99999999L, tx.amount());
}

// TC-AMOUNT-003: Zero amount
@Test
void createTransaction_zeroAmount() {
    transactionService.createTransaction(req(2, accId, null, 0L, "Zero"));
    
    TransactionDto tx = transactionService.getRecentTransactions(1).get(0);
    assertEquals(0L, tx.amount());
}
```

### 9.2 Soft Delete

```java
// TC-SOFTDELETE-001: Deleted account not returned in list
@Test
void getAccounts_excludesDeletedAccounts() {
    AccountDto acc = accountService.createAccount(req("DeleteMe", CASH, "USD", 0L));
    accountService.deleteAccount(acc.id());
    
    List<AccountDto> accounts = accountService.getCurrentUserAccounts();
    assertFalse(accounts.stream().anyMatch(a -> a.id() == acc.id()));
}

// TC-SOFTDELETE-002: Deleted account not accessible by ID
@Test
void getAccount_throwsForDeletedAccount() {
    AccountDto acc = accountService.createAccount(req("Delete", CASH, "USD", 0L));
    accountService.deleteAccount(acc.id());
    
    BusinessException ex = assertThrows(BusinessException.class,
        () -> accountService.getAccount(acc.id()));
    assertEquals(ResultCode.ACCOUNT_NOT_FOUND, ex.getResultCode());
}
```

---

## 10. Edge Cases & Error Handling

### 10.1 Manual Test Cases

#### TC-ERR-001: Network error shows retry option
| Field | Value |
|-------|-------|
| Test ID | TC-ERR-001 |
| Title | API failure shows user-friendly message |
| Priority | P1 |
| Steps | 1. Disconnect network 2. Try to load accounts |
| Expected | Error message displayed |
| Actual | |
| Status | |

#### TC-ERR-002: Empty transactions list
| Field | Value |
|-------|-------|
| Test ID | TC-ERR-002 |
| Title | Empty state message when no transactions |
| Priority | P2 |
| Precondition | New user with no transactions |
| Steps | 1. Create new account 2. Go to transactions |
| Expected | Message: "No transactions yet" |
| Actual | |
| Status | |

#### TC-ERR-003: Empty accounts list
| Field | Value |
|-------|-------|
| Test ID | TC-ERR-003 |
| Title | Empty state with create button |
| Priority | P2 |
| Precondition | New user |
| Steps | 1. Go to `/accounts` |
| Expected | "No accounts" message with "Create one" button |
| Actual | |
| Status | |

### 10.2 Automated Test Cases

```java
// TC-ERR-AUTO-001: Create transaction on deleted account fails
@Test
void createTransaction_throwsForDeletedAccount() {
    AccountDto acc = accountService.createAccount(req("Del", CASH, "USD", 0L));
    accountService.deleteAccount(acc.id());
    
    BusinessException ex = assertThrows(BusinessException.class,
        () -> transactionService.createTransaction(req(2, acc.id(), null, 100L, "Test")));
    assertEquals(ResultCode.ACCOUNT_NOT_FOUND, ex.getResultCode());
}

// TC-ERR-AUTO-002: Update balance on non-existent account fails
@Test
void updateBalance_throwsForNonExistentAccount() {
    BusinessException ex = assertThrows(BusinessException.class,
        () -> accountService.updateBalance(99999L, 100L));
    assertEquals(ResultCode.ACCOUNT_NOT_FOUND, ex.getResultCode());
}

// TC-ERR-AUTO-003: Invalid transaction type handled
@Test
void createTransaction_validatesTransactionType() {
    // Transaction type must be 1-5
    assertThrows(BusinessException.class,
        () -> transactionService.createTransaction(
            new CreateTransactionRequest(99, accId, null, 100L, "Invalid")));
}
```

---

## Test Results Summary

| Category | Manual TC Count | Automated TC Count |
|----------|-----------------|-------------------|
| Authentication | 8 | 6 |
| Accounts | 6 | 5 |
| Transactions | 10 | 4 |
| Categories | 5 | 3 |
| Dashboard | 5 | 0 |
| Profile | 2 | 0 |
| UI/UX | 4 | 0 |
| API Integration | 0 | 3 |
| Data Integrity | 0 | 5 |
| Error Handling | 3 | 3 |
| **Total** | **43** | **29** |

## Sign-off

| Role | Name | Date | Signature |
|------|------|------|-----------|
| QA Engineer | | | |
| Developer | | | |
| Product Owner | | | |