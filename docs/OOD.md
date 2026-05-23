# Bookkeeping System — Object-Oriented Design

> PlantUML diagrams for architecture, domain model, and interactions.

## 1. Domain Model (Class Diagram)

```plantuml
@startuml
!theme plain
hide empty members

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

' === CORE - ACCOUNT ===
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

' === CORE - CATEGORY ===
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

' === CORE - TAG ===
class Tag {
    +Long id
    +Long userId
    +String name
    +String color
    +Long createdTime
    +Long updatedTime
    +Boolean deleted
}

' === CORE - BUDGET ===
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

' === CORE - TRANSACTION ===
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

' Transfer link -- TRANSFER_OUT (type=4) and TRANSFER_IN (type=5) are linked by relatedId
Transaction }o--o{ Transaction : relatedId

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

## 3. Transaction Create Flow (Sequence Diagram)

```plantuml
@startuml
!theme plain

title Transaction Create Flow

actor User
participant "TransactionController" as TC
participant "TransactionService" as TS
participant "AccountService" as AS
participant "TransactionRepository" as TR
participant "AccountRepository" as AR

User -> TC : POST /api/v1/transactions (createTransaction)
TC -> TS : createTransaction(request)

alt Transfer (type = 4, destinationAccountId set)
    TS -> AS : updateBalance(fromAccountId, -amount)
    AS -> AR : findById
    AR --> AS : Account
    AS -> AR : save(toBuilder().balance(newBalance).build())
    AS --> TS : Account

    TS -> TR : save(TRANSFER_OUT record)
    TR --> TS : tx1

    TS -> TR : save(TRANSFER_IN record, relatedId=tx1.id)
    TR --> TS : tx2

    TS -> TR : save(tx1, relatedId=tx2.id)
    TR --> TS : tx1 (updated)

else Normal Transaction
    alt Income / Modify Balance (type 1 or 2)
        TS -> AS : updateBalance(accountId, +amount)
    else Expense (type 3)
        TS -> AS : updateBalance(accountId, -amount)
    end

    AS -> AR : findById
    AR --> AS : Account
    AS -> AR : save(toBuilder().balance(newBalance).build())
    AS --> TS : Account

    TS -> TR : save(transaction)
    TR --> TS : savedTx
end

TS --> TC : TransactionDto
TC --> User : 200 OK ApiResponse

@enduml
```

---

## 4. Login & Authenticated Request (Sequence Diagram)

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

' Login flow
Client -> AC : POST /api/v1/auth/login {username, password}
AC -> AS : login(request)
AS -> UR : findByUsername
UR --> AS : User
AS -> AS : verify password (MD5 hash)
AS -> JTP : generateToken(userId, username)
JTP --> AS : jwtToken
AS --> AC : LoginResponse{jwtToken, user}
AC --> Client : 200 OK {token, user}

' Authenticated request flow
Client -> JAF : Request + Authorization: Bearer <token>
JAF -> JTP : validateToken(token)
alt Token valid
    JTP --> JAF : userId
    JAF -> SC : SecurityContext.setAuthentication(userId)
    JAF -> SC : filterChain proceeds
else Token invalid / expired
    JAF --> Client : 401 Unauthorized
end

@enduml
```

---

## 5. Delete Transaction with Balance Revert (Sequence Diagram)

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

User -> TC : DELETE /api/v1/transactions/{id}
TC -> TS : deleteTransaction(id)
TS -> TR : findByIdAndUserId(id, userId)
TR --> TS : Transaction

alt Has relatedId (part of transfer pair)
    TS -> TR : findById(relatedId)
    TR --> TS : relatedTx
    TS -> AS : updateBalance(relatedTx.accountId, -calculateBalanceChange(relatedTx))
    AS -> AR : findById
    AR --> AS : Account
    AS -> AR : save(revertedBalance)
    AS --> TS : done
    TS -> TR : delete(relatedTx)
end

TS -> AS : updateBalance(oldAccountId, -calculateBalanceChange(existingTx))
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

## 6. Use Case Diagram

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
    usecase "UC13: Search and Filter" as UC13
    usecase "UC14: View Monthly Statistics" as UC14
}

package "Tag and Budget" {
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
    usecase "UC23: View Profile" as UC23
    usecase "UC24: Update Profile" as UC24
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
User --> UC23
User --> UC24

@enduml
```

---

## 7. Entity Relationship Diagram (Database Schema)

```plantuml
@startuml
!theme plain

title Entity Relationship Diagram

' users table
entity "users" as users {
    * id : BIGSERIAL PK
    * username : VARCHAR(32) UNIQUE NOT NULL
    * email : VARCHAR(100) UNIQUE NOT NULL
    * password : VARCHAR(100) NOT NULL
    * salt : VARCHAR(10) NOT NULL
    --
    nickname : VARCHAR(64)
    default_currency : VARCHAR(3)
    default_account_id : BIGINT
    language : VARCHAR(10)
    email_verified : BOOLEAN
    disabled : BOOLEAN
    * created_at : BIGINT
    * updated_at : BIGINT
}

' accounts table
entity "accounts" as accounts {
    * id : BIGSERIAL PK
    * name : VARCHAR(64) NOT NULL
    * account_type : VARCHAR(20) NOT NULL
    * currency : VARCHAR(3) NOT NULL
    * balance : BIGINT NOT NULL
    * user_id : BIGINT NOT NULL FK
    --
    description : VARCHAR(255)
    deleted : BOOLEAN
    * created_at : BIGINT
    * updated_at : BIGINT
}

' categories table
entity "categories" as categories {
    * id : BIGSERIAL PK
    * name : VARCHAR(64) NOT NULL
    * category_type : VARCHAR(10) NOT NULL
    * user_id : BIGINT NOT NULL FK
    --
    parent_id : BIGINT
    sort_order : INT
    * created_at : BIGINT
    * updated_at : BIGINT
}

' transactions table
entity "transactions" as transactions {
    * id : BIGSERIAL PK
    * transaction_type : INT NOT NULL
    * account_id : BIGINT NOT NULL FK
    category_id : BIGINT FK
    * amount : BIGINT NOT NULL
    description : VARCHAR(255)
    * transaction_time : BIGINT NOT NULL
    related_id : BIGINT FK
    * user_id : BIGINT NOT NULL FK
    --
    tag_ids : TEXT
    * created_at : BIGINT
    * updated_at : BIGINT
}

' tags table
entity "tags" as tags {
    * id : BIGSERIAL PK
    * user_id : BIGINT NOT NULL FK
    * name : VARCHAR NOT NULL
    color : VARCHAR(7)
    * created_unix_time : BIGINT NOT NULL
    updated_unix_time : BIGINT
    deleted : BOOLEAN
}

' budgets table
entity "budgets" as budgets {
    * id : BIGSERIAL PK
    * user_id : BIGINT NOT NULL FK
    * category_id : BIGINT NOT NULL FK
    * amount : BIGINT NOT NULL
    * year : INT NOT NULL
    * month : INT NOT NULL
    * created_unix_time : BIGINT NOT NULL
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

' Transfer pair self-reference
transactions }o--|| transactions : related_id (transfer pair)

users ||--o| accounts : default account

@enduml
```

---

## 8. System Mind Map

```plantuml
@startuml
!theme plain

title Bookkeeping System - Functionality Mind Map

' Core Modules
rectangle "Account Management" #LightGreen {
  card "Account entity\n(name, balance, currency)"
  card "AccountService"
  card "AccountRepository"
  card "AccountController"
  card "AccountDto + @MapperAuto"
  note bottom #White
    CRUD + soft delete
    Balance auto-update on tx
  end note
}

rectangle "Category Management" #LightGreen {
  card "Category entity\n(name, categoryType, parentId)"
  card "CategoryService"
  card "CategoryRepository"
  card "CategoryController"
  card "CategoryDto + @MapperAuto"
  note bottom #White
    INCOME / EXPENSE types
    Optional parent hierarchy
  end note
}

rectangle "Transaction Management" #LightGreen {
  card "Transaction entity"
  card "TransactionService"
  card "TransactionRepository"
  card "TransactionController"
  card "StatisticsDto"
  note bottom #White
    5 types: INCOME, EXPENSE,\nTRANSFER_OUT, TRANSFER_IN, MODIFY_BALANCE
    Auto balance update
    relatedId for transfers
  end note
}

rectangle "Tag Management" #LightGreen {
  card "Tag entity\n(name, color)"
  card "TagService"
  card "TagRepository"
  card "TagController"
  card "TagDto + @MapperAuto"
  note bottom #White
    CRUD + soft delete
    Tag colors (hex)
  end note
}

rectangle "Budget Management" #LightGreen {
  card "Budget entity\n(amount, year, month)"
  card "BudgetService"
  card "BudgetRepository"
  card "BudgetController"
  card "BudgetDto (calculated spent)"
  note bottom #White
    Per category / month
    Spent vs budget tracking
  end note
}

rectangle "Dashboard & Reporting" #LightGreen {
  card "DashboardController"
  card "Statistics (by category)"
  card "Charts (ECharts)"
  card "Reports page"
  card "CSV Export"
  note bottom #White
    KPI: income, expense, net
    Monthly trend analysis
  end note
}

' Supporting
rectangle "Authentication" #LightYellow {
  card "User entity"
  card "AuthService"
  card "AuthController"
  card "Login / Register / JWT"
  card "UserService"
  note bottom #White
    MD5 + salt password hash
    JWT stateless auth
  end note
}

rectangle "Security" #LightYellow {
  card "JwtTokenProvider\n(token gen/validate)"
  card "JwtAuthenticationFilter"
  card "SecurityUtils\n(requireCurrentUser)"
  card "SecurityConfig\n(CORS, routes)"
  note bottom #White
    Public: /auth/*, /health
    Protected: /api/v1/*
  end note
}

rectangle "Infrastructure" #LightYellow {
  card "DataInitializer\n(seed demo data)"
  card "GlobalExceptionHandler"
  card "ResultCode (error codes)"
  card "OpenApiConfig (Swagger)"
  card "Flyway migrations"
  note bottom #White
    Flyway: V1-V5 migrations
    Demo user: demo/demo123
  end note
}

' Common
rectangle "Common" #LightGray {
  card "BaseEntity (id, createdAt, updatedAt)"
  card "Auditable interface"
  card "ApiResponse<T>"
  card "AccountType enum"
  card "CategoryType enum"
  card "TransactionType enum"
  note bottom #White
    @Builder(toBuilder=true)
    @NoArgsConstructor(PROTECTED)
    No setters in production code
  end note
}

' Key Design Rules
rectangle "Key Design Rules" #Lavender {
  card "Amount: BIGINT cents (no float)"
  card "Time: Unix epoch BIGINT sec"
  card "Soft delete: deleted flag"
  card "Transfer: 2 records + relatedId"
  card "Response: ApiResponse<T>"
  card "Builder pattern (no setters)"
  card "MapStructPlus @MapperAuto on DTO"
}

@enduml
```

---

## 9. Transaction State Machine

```plantuml
@startuml
!theme plain

title Transaction State Machine

[*] --> Draft : createTransaction()

Draft --> Income : type = 2 (INCOME)
Draft --> Expense : type = 3 (EXPENSE)
Draft --> TransferOut : type = 4 (TRANSFER_OUT)

TransferOut --> TransferIn : destinationAccount\nset
TransferIn --> [*] : saved

Income --> [*] : saved
Expense --> [*] : saved

Draft --> Editing : PUT /transactions/{id}
Editing --> [*] : updated

Draft --> Deleting : DELETE /transactions/{id}
Deleting --> [*] : deleted\n(balance reverted)

note right of Draft
  Account balance not yet changed
end note

note right of Income
  Account balance += amount
end note

note right of Expense
  Account balance -= amount
end note

note right of TransferOut
  Source account -= amount
end note

note right of TransferIn
  Dest account += amount\nrelatedId links the pair
end note

note right of Deleting
  Revert balance change\nthen delete record
end note

@enduml
```

---

## 10. Deployment Overview

```plantuml
@startuml
!theme plain

title Deployment Overview

skinparam componentStyle uml2

package "Frontend" {
    [Vue Pages]
    [Vuetify 3]
    [Pinia Store]
    [ECharts]
    [i18n locales]
}


package "Backend (Spring Boot 4)" {
    [Controllers]
    [Services]
    [Repositories]
    [Security (JWT)]
    [Flyway Migrations]
}

database "PostgreSQL 17" {
    [users]
    [accounts]
    [categories]
    [transactions]
    [tags]
    [budgets]
    [flyway_schema_history]
}

database "Docker Container" {
    [PostgreSQL Container]
}

package "External" {
    [REST /api/v1/*]
}


[Vue Pages] --> [REST /api/v1/*]
[Controllers] --> [Services]
[Services] --> [Repositories]
[Repositories] --> [PostgreSQL 17]
[Security (JWT)] --> [Services]
[Flyway Migrations] --> [PostgreSQL 17] : migrate on boot


@enduml
```

---

## 11. Service Dependency Graph

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
    class JwtTokenProvider
}

package "supporting.auth" {
    class AuthService
}

package "exception" {
    class BusinessException
}

TransactionService --> AccountService
TransactionService --> CategoryService
TransactionService --> SecurityUtils
TransactionService ..> BusinessException

AccountService --> SecurityUtils
AccountService ..> BusinessException

CategoryService --> SecurityUtils

TagService --> SecurityUtils

BudgetService --> SecurityUtils

DashboardController --> AccountRepository
DashboardController --> TransactionRepository
DashboardController --> SecurityUtils

AuthService --> UserRepository
AuthService --> JwtTokenProvider

note right of SecurityUtils
  Extracts userId from\nSecurityContext (JWT filter)
end note

note right of BusinessException
  Used by all services for\nerror handling (ResultCode)
end note

@enduml
```