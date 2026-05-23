# Bookkeeping System — Object-Oriented Design

> PlantUML diagrams for architecture, domain model, and interactions.

## 1. Domain Model (Class Diagram)

```plantuml
@startuml
!theme plain

' === BASE ===
abstract class BaseEntity {
    +Long id
    +Long createdAt
    +Long updatedAt
    +withId(Long): BaseEntity
}

interface Auditable {
    +Long getCreatedAt()
    +Long getUpdatedAt()
}

BaseEntity ..|> Auditable

' === USER ===
class User {
    +String username
    +String email
    +String nickname
    +String password
    +String salt
    +String defaultCurrency
    +Long defaultAccountId
    +String language
    +Boolean emailVerified
    +Boolean disabled
    +boolean isActive()
}
BaseEntity <|-- User

' === CORE — ACCOUNT ===
class Account {
    +String name
    +AccountType accountType
    +String currency
    +Long balance
    +Long userId
    +String description
    +Boolean deleted
}
BaseEntity <|-- Account

enum AccountType {
    CASH
    CHECKING
    SAVINGS
    CREDIT
    INVESTMENT
}
Account --> AccountType : accountType

' === CORE — CATEGORY ===
class Category {
    +String name
    +CategoryType categoryType
    +Long userId
    +Long parentId
    +Integer sortOrder
}
BaseEntity <|-- Category

enum CategoryType {
    INCOME
    EXPENSE
}
Category --> CategoryType : categoryType

' === CORE — TAG ===
class Tag {
    +Long id
    +Long userId
    +String name
    +String color
    +Long createdTime
    +Long updatedTime
    +Boolean deleted
}

' === CORE — BUDGET ===
class Budget {
    +Long id
    +Long userId
    +Long categoryId
    +Long amount
    +Integer year
    +Integer month
    +Long createdTime
    +Long updatedTime
}

' === CORE — TRANSACTION ===
class Transaction {
    +Integer transactionType
    +Long accountId
    +Long categoryId
    +Long amount
    +String description
    +Long transactionTime
    +Long relatedId
    +Long userId
    +String tagIds
}
BaseEntity <|-- Transaction

enum TransactionType {
    MODIFY_BALANCE = 1
    INCOME = 2
    EXPENSE = 3
    TRANSFER_OUT = 4
    TRANSFER_IN = 5
}
Transaction --> TransactionType : transactionType

' === RELATIONSHIPS ===
User "1" --> "n" Account : owns
User "1" --> "n" Category : owns
User "1" --> "n" Tag : owns
User "1" --> "n" Budget : owns
User "1" --> "n" Transaction : owns

Account "1" --> "n" Transaction : records
Category "1" --> "n" Transaction : categorizes
Category "1" --> "n" Budget : budgets

' Transfer link
Transaction "4\nTRANSFER_OUT" --o{ Transaction "5\nTRANSFER_IN" : relatedId

@enduml
```

---

## 2. Package Architecture (Component Diagram)

```plantuml
@startuml
!theme plain

title Package Architecture

package "common" {
    class BaseEntity
    interface Auditable
    class ApiResponse
    class ResultCode
    enum AccountType
    enum CategoryType
    enum TransactionType
}

package "supporting" {
    package "auth" {
        class AuthController
        class AuthService
        record LoginRequest
        record LoginResponse
        record RegisterRequest
    }
    package "security" {
        class JwtTokenProvider
        class JwtAuthenticationFilter
        class SecurityUtils
    }
    package "user" {
        class User
        class UserController
        class UserService
        record UserDto
        record UpdateUserRequest
    }
}

package "core" {
    package "account" {
        class Account
        class AccountController
        class AccountService
        record AccountDto
        record CreateAccountRequest
        record UpdateAccountRequest
        interface AccountMapper
    }
    package "category" {
        class Category
        class CategoryController
        class CategoryService
        record CategoryDto
    }
    package "transaction" {
        class Transaction
        class TransactionController
        class TransactionService
        record TransactionDto
        record CreateTransactionRequest
        record UpdateTransactionRequest
        record StatisticsDto
        record TransactionSearchParams
        interface TransactionMapper
    }
    package "tag" {
        class Tag
        class TagController
        class TagService
        record TagDto
    }
    package "budget" {
        class Budget
        class BudgetController
        class BudgetService
        record BudgetDto
    }
    package "dashboard" {
        class DashboardController
    }
}

package "config" {
    class SecurityConfig
    class DataInitializer
    class JwtAuthenticationEntryPoint
}

package "exception" {
    class BusinessException
    class GlobalExceptionHandler
}

' Intra-package dependencies
AuthService ..> User
SecurityUtils ..> UserRepository
AuthController --> SecurityConfig
AccountService ..> SecurityUtils
TransactionService ..> AccountService
TransactionService ..> SecurityUtils
TransactionService ..> TransactionMapper

@enduml
```

---

## 3. Service Layer — Transaction Flow (Sequence Diagram)

```plantuml
@startuml
!theme plain

title Transaction Create Flow

actor User
participant "AccountController" as AC
participant "TransactionController" as TC
participant "TransactionService" as TS
participant "AccountService" as AS
participant "TransactionRepository" as TR
participant "AccountRepository" as AR

User -> TC : POST /transactions (create)
TC -> TS : createTransaction(req)

alt Transfer (type = 4)
    TS -> AS : updateBalance(from, -amount)
    AS -> AR : findById
    AR --> AS : Account
    AS -> AR : save(updatedBalance)
    AS --> TS : Account

    TS -> TR : save(TRANSFER_OUT)
    TR --> TS : tx1

    TS -> TR : save(TRANSFER_IN, relatedId=tx1.id)
    TR --> TS : tx2

    TS -> TR : save(tx1, relatedId=tx2.id)
    TR --> TS : tx1

else Normal Transaction
    alt Income / Modify Balance (type 1, 2)
        TS -> AS : updateBalance(account, +amount)
    else Expense (type 3)
        TS -> AS : updateBalance(account, -amount)
    end

    AS -> AR : findById
    AR --> AS : Account
    AS -> AR : save(updatedBalance)
    AS --> TS : Account

    TS -> TR : save(transaction)
    TR --> TS : savedTx
end

TS --> TC : TransactionDto
TC --> User : 200 OK

@enduml
```

---

## 4. Authentication Flow (Sequence Diagram)

```plantuml
@startuml
!theme plain

title Login & Authenticated Request Flow

actor Client
participant "AuthController" as AC
participant "AuthService" as AS
participant "UserRepository" as UR
participant "JwtTokenProvider" as JTP
participant "JwtAuthenticationFilter" as JAF
participant "SecurityConfig" as SC

'=== Login ===
Client -> AC : POST /auth/login {username, password}
AC -> AS : login(req)
AS -> UR : findByUsername
UR --> AS : User
AS -> AS : verify password (MD5)
AS -> JTP : generateToken(userId, username)
JTP --> AS : jwtToken
AS --> AC : LoginResponse{jwtToken, user}
AC --> Client : 200 OK {token, user}

'=== Authenticated Request ===
Client -> JAF : Request + Bearer token
JAF -> JTP : validateToken
alt Token valid
    JTP --> JAF : userId
    JAF -> SC : SecurityContext.setAuthentication
    JAF -> SC : filterChain continue
else Token invalid/expired
    JAF --> Client : 401 Unauthorized
end

@enduml
```

---

## 5. Sequence Diagram — Delete Transaction (with Transfer Revert)

```plantuml
@startuml
!theme plain

title Delete Transaction (with Balance Revert)

actor User
participant "TransactionController" as TC
participant "TransactionService" as TS
participant "AccountService" as AS
participant "TransactionRepository" as TR
participant "AccountRepository" as AR

User -> TC : DELETE /transactions/{id}
TC -> TS : deleteTransaction(id)
TS -> TR : findByIdAndUserId(id, userId)
TR --> TS : Transaction

alt Has relatedId (Transfer pair)
    TS -> TR : findById(relatedId)
    TR --> TS : relatedTx
    TS -> AS : updateBalance(relatedTx.accountId, -change)
    AS -> AR : findById
    AR --> AS : Account
    AS -> AR : save(revertedBalance)
    AS --> TS : done
    TS -> TR : delete(relatedTx)
end

TS -> AS : updateBalance(oldAccountId, -change)
AS -> AR : findById
AR --> AS : Account
AS -> AR : save(revertedBalance)
AS --> TS : done

TS -> TR : delete(transaction)
TS --> TC : void
TC --> User : 204 No Content

@enduml
```

---

## 6. Use Case Diagram — System Functions

```plantuml
@startuml
!theme plain

title Use Case Diagram

left to right direction

actor User

package "Account Management" {
    usecase "UC1: View Accounts" as UC1
    usecase "UC2: Create Account" as UC2
    usecase "UC3: Update Account" as UC3
    usecase "UC4: Delete Account (soft)" as UC4
}

package "Category Management" {
    usecase "UC5: View Categories" as UC5
    usecase "UC6: Create Category" as UC6
    usecase "UC7: Delete Category" as UC7
}

package "Transaction Management" {
    usecase "UC8: Add Income" as UC8
    usecase "UC9: Add Expense" as UC9
    usecase "UC10: Transfer Between Accounts" as UC10
    usecase "UC11: Edit Transaction" as UC11
    usecase "UC12: Delete Transaction" as UC12
    usecase "UC13: Search/Filter Transactions" as UC13
    usecase "UC14: View Monthly Statistics" as UC14
}

package "Tag & Budget" {
    usecase "UC15: Manage Tags" as UC15
    usecase "UC16: Set Budget" as UC16
    usecase "UC17: View Budget vs Actual" as UC17
}

package "Reporting" {
    usecase "UC18: View Dashboard" as UC18
    usecase "UC19: View Reports" as UC19
    usecase "UC20: Export CSV" as UC20
}

package "Authentication" {
    usecase "UC21: Register" as UC21
    usecase "UC22: Login" as UC22
    usecase "UC23: Logout" as UC23
    usecase "UC24: View Profile" as UC24
    usecase "UC25: Update Profile" as UC25
}

User --> UC1
User --> UC2
User --> UC3
User --> UC4
User --> UC5
User --> UC6
User --> UC7
User --> UC8
User --> UC9
User --> UC10
User --> UC11
User --> UC12
User --> UC13
User --> UC14
User --> UC15
User --> UC16
User --> UC17
User --> UC18
User --> UC19
User --> UC20
User --> UC21
User --> UC22
User --> UC24
User --> UC25

@enduml
```

---

## 7. Entity Relationship Diagram (Database Schema)

```plantuml
@startuml
!theme plain

title Entity Relationship Diagram

' Tables
entity "users" as users {
    * id : BIGSERIAL PK
    * username : VARCHAR(32) UNIQUE
    * email : VARCHAR(100) UNIQUE
    * password : VARCHAR(100)
    * salt : VARCHAR(10)
    --
    nickname : VARCHAR(64)
    default_currency : VARCHAR(3)
    default_account_id : BIGINT FK
    language : VARCHAR(10)
    email_verified : BOOLEAN
    disabled : BOOLEAN
    * created_at : BIGINT
    * updated_at : BIGINT
}

entity "accounts" as accounts {
    * id : BIGSERIAL PK
    * name : VARCHAR(64)
    * account_type : VARCHAR(20)
    * currency : VARCHAR(3)
    * balance : BIGINT
    * user_id : BIGINT FK
    --
    description : VARCHAR(255)
    deleted : BOOLEAN
    * created_at : BIGINT
    * updated_at : BIGINT
}

entity "categories" as categories {
    * id : BIGSERIAL PK
    * name : VARCHAR(64)
    * category_type : VARCHAR(10)
    * user_id : BIGINT FK
    --
    parent_id : BIGINT FK
    sort_order : INT
    * created_at : BIGINT
    * updated_at : BIGINT
}

entity "transactions" as transactions {
    * id : BIGSERIAL PK
    * transaction_type : INT
    * account_id : BIGINT FK
    category_id : BIGINT FK
    * amount : BIGINT
    description : VARCHAR(255)
    * transaction_time : BIGINT
    related_id : BIGINT FK
    * user_id : BIGINT FK
    --
    tag_ids : TEXT
    * created_at : BIGINT
    * updated_at : BIGINT
}

entity "tags" as tags {
    * id : BIGSERIAL PK
    * user_id : BIGINT FK
    * name : VARCHAR
    color : VARCHAR(7)
    * created_unix_time : BIGINT
    updated_unix_time : BIGINT
    deleted : BOOLEAN
}

entity "budgets" as budgets {
    * id : BIGSERIAL PK
    * user_id : BIGINT FK
    * category_id : BIGINT FK
    * amount : BIGINT
    * year : INT
    * month : INT
    * created_unix_time : BIGINT
    updated_unix_time : BIGINT
}

' Relationships
users ||--o{ accounts : owns
users ||--o{ categories : owns
users ||--o{ transactions : owns
users ||--o{ tags : owns
users ||--o{ budgets : owns

accounts ||--o{ transactions : records
categories ||--o{ transactions : categorizes
categories ||--o{ budgets : budgets

transactions }o--|| transactions : transfer pair\n(related_id)

users ||--o| accounts : default account

@enduml
```

---

## 8. Mind Map — System Functionality Overview

```plantuml
@startuml
!theme plain

title Bookkeeping System — Functionality Mind Map

center footer Generated from OOD analysis. PlantUML mindmap support varies by renderer.

legend left
| **Color Legend** |
| <back:#E8F5E9>Green = Module/Feature</back> |
| <back:#E3F2FD>Blue = Sub-feature</back> |
| <back:#FFF3E0>Orange = Data Entity</back> |
| <back:#FCE4EC>Pink = API Endpoint</back> |
endlegend

leaf root {
  Bookkeeping System
}

branch {root} core {
  Account Management
  leaf {core} accounts {
    **Entity:** Account
    **Service:** AccountService
    **Repository:** AccountRepository
    leaf CRUD {
      Create Account
      Update Account
      Soft Delete
      List Accounts
    }
    leaf Balance {
      Initial Balance
      Auto-Update on Tx
    }
  }

  Category Management
  leaf {core} categories {
    **Entity:** Category
    **Service:** CategoryService
    **Repository:** CategoryRepository
    leaf CRUD {
      Create Category
      Delete Category
      List by Type
    }
    leaf Hierarchy {
      Parent Category
      Sort Order
    }
  }

  Transaction Management
  leaf {core} transactions {
    **Entity:** Transaction
    **Service:** TransactionService
    **Repository:** TransactionRepository
    leaf Types {
      INCOME (+amount)
      EXPENSE (-amount)
      TRANSFER (pair)
      MODIFY_BALANCE
    }
    leaf Features {
      Create Transaction
      Edit Transaction
      Delete Transaction
      Search & Filter
      Month Navigation
    }
  }

  Tag Management
  leaf {core} tags {
    **Entity:** Tag
    **Service:** TagService
    leaf CRUD {
      Create Tag
      Update Tag
      Delete Tag
      Tag Colors
    }
  }

  Budget Management
  leaf {core} budgets {
    **Entity:** Budget
    **Service:** BudgetService
    leaf Set Budget (amount/month/category)
    leaf Track Spent vs Budget
    leaf Alerts (over budget)
  }

  Dashboard & Reporting
  leaf {core} dashboard {
    **Controller:** DashboardController
    leaf KPI Cards {
      Total Balance
      Monthly Income
      Monthly Expense
      Net Change
    }
    leaf Statistics {
      Category Breakdown
      Income vs Expense
      Trend Charts
    }
    leaf Reports {
      Monthly Summary
      Category Report
      Budget vs Actual
    }
  }
}

branch {root} supporting {
  Authentication & User
  leaf {supporting} auth {
    **Entities:** User, Session
    **Services:** AuthService, UserService
    leaf Login (JWT)
    leaf Register
    leaf Profile Update
    leaf Password Hash (MD5+salt)
  }

  Security
  leaf {supporting} security {
    **Components:** JwtTokenProvider, JwtFilter
    leaf Stateless JWT
    leaf CORS Config
    leaf Public/Protected Routes
  }

  Infrastructure
  leaf {supporting} infra {
    leaf DataInitializer (Seed Data)
    leaf GlobalExceptionHandler
    leaf ResultCode (Error Codes)
    leaf OpenAPI / Swagger
  }
}

branch {root} data {
  Amount Storage
  leaf amount {
    BIGINT (cents/fen)
    No floating point
  }

  Time Storage
  leaf time {
    Unix Epoch (BIGINT seconds)
    JPA @PrePersist/@PreUpdate
  }

  Soft Delete
  leaf soft-delete {
    deleted flag
    updatedAt timestamp
  }
}

@enduml
```

---

## 9. State Machine — Transaction Lifecycle

```plantuml
@startuml
!theme plain

title Transaction State Machine

[*] --> Draft : createTransaction()

Draft --> Income : type = 2\namount > 0
Draft --> Expense : type = 3\namount > 0
Draft --> TransferOut : type = 4\n(source set)

TransferOut --> TransferIn : destination\naccount set
TransferIn --> [*] : saved

Income --> [*] : saved
Expense --> [*] : saved

Draft --> Editing : PUT /transactions/{id}

Editing --> [*] : updated

Draft --> Deleting : DELETE

Deleting --> [*] : deleted

note right of Draft
  Balance not yet changed
end note

note right of Income
  Account balance += amount
end note

note right of Expense
  Account balance -= amount
end note

note right of TransferOut
  Source balance -= amount
end note

note right of TransferIn
  Dest balance += amount
end note

note right of Deleting
  Balance reverted before delete
end note

@enduml
```

---

## 10. Deployment / Infrastructure View (Component Diagram)

```plantuml
@startuml
!theme plain

title Deployment Overview

node "Frontend (Nuxt 4)" {
    component "Vue 3 Pages"
    component "Vuetify 3 UI"
    component "Pinia Store"
    component "ECharts"
    component "i18n"
}

node "Backend (Spring Boot 4)" {
    component "Controllers" as C
    component "Services" as S
    component "Repositories" as R
    component "Security (JWT)" as SEC
    component "Flyway Migrations" as FLY

    database "PostgreSQL 17" as PG {
        table "users"
        table "accounts"
        table "categories"
        table "transactions"
        table "tags"
        table "budgets"
    }
}

node "Docker" {
    database "PostgreSQL Container" as PC
}

Frontend --> REST API : HTTPS
C --> S : @Service
S --> R : @Repository
R --> PG : JDBC
S --> SEC : Auth check
C --> SEC : Filter chain
FLY --> PG : migrate()

note bottom of PG
  3 databases:
  bookkeeping (prod)
  bookkeeping_dev
  bookkeeping_test
end note

@enduml
```

---

## 11. Controller & Service Dependency Graph

```plantuml
@startuml
!theme plain

title Service Dependency Graph

package "core" {
    class AccountService
    class CategoryService
    class TransactionService
    class TagService
    class BudgetService
    class DashboardController
}

package "supporting.security" {
    class SecurityUtils
}

package "supporting.auth" {
    class AuthService
}

package "common" {
    class BusinessException
}

TransactionService --> AccountService
TransactionService --> CategoryService
TransactionService --> SecurityUtils
TransactionService --> TransactionMapper
TransactionService --> BusinessException

AccountService --> SecurityUtils
AccountService --> AccountMapper
AccountService --> BusinessException

CategoryService --> SecurityUtils
CategoryService --> CategoryMapper

TagService --> SecurityUtils

BudgetService --> SecurityUtils
BudgetService --> BudgetMapper

DashboardController --> AccountRepository
DashboardController --> TransactionRepository
DashboardController --> SecurityUtils

AuthService --> UserRepository
AuthService --> JwtTokenProvider

note right of SecurityUtils
  Extracts userId from
  SecurityContext (JWT)
end note

@enduml
```