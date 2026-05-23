# Refactoring Analysis: MapStruct+ for Setter Elimination

**Date**: 2026-05-22
**Goal**: Replace entity setters with MapStruct+ generated mapping code

---

## Current State

### Entities with Setters (Total: 70 setters across 4 entities need mappers)

| Entity | Setters Used | Mapper Exists | Status |
|--------|-------------|---------------|--------|
| `Budget` | 10 setters | ❌ | **Needs mapper** |
| `Tag` | 8 setters | ❌ | **Needs mapper** |
| `Transaction` | 20 setters | ❌ | **Needs mapper** |
| `Account` | 12 setters (partial) | ✅ | **Needs update** |

### Setters Count by File

```
TransactionService.java         20 setters  ⚠️ HIGH PRIORITY
BudgetService.java            10 setters  ⚠️ HIGH PRIORITY
TagService.java               8 setters  ⚠️ HIGH PRIORITY
AccountService.java          12 setters  ✅ Has AccountMapper
CategoryService.java         5 setters  ✅ Has CategoryMapper
```

---

## Detailed Analysis

### 1. BudgetService (10 setters) — HIGH PRIORITY

**Entity**: `Budget`
**Mapper**: Does not exist → **Create BudgetMapper**

**Current usage**:
```java
// createBudget()
Budget budget = new Budget();
budget.setUserId(userId);
budget.setCategoryId(request.categoryId());
budget.setAmount(request.amount());
budget.setYear(request.year());
budget.setMonth(request.month());
budget.setCreatedTime(now);

// updateBudget()
budget.setAmount(request.amount());
budget.setUpdatedTime(now);

// deleteBudget()
budget.setDeleted(true);  // Note: Budget doesn't have deleted field!
```

**DTOs available**:
- `CreateBudgetRequest` (4 fields)
- `UpdateBudgetRequest` (1 field)
- `BudgetDto` (8 fields, includes computed)

**Required mappers**:
1. `BudgetMapper.toEntity(CreateBudgetRequest, userId, now)` → Budget
2. `BudgetMapper.toEntity(Budget, UpdateBudgetRequest)` → Budget (update in place)
3. `BudgetMapper.toDtoWithSpent(Budget, categoryName, spent, percent)` → BudgetDto

**Note**: `BudgetDto` requires additional computed fields not in entity, so partial mapping needed.

---

### 2. TagService (8 setters) — HIGH PRIORITY

**Entity**: `Tag`
**Mapper**: Does not exist → **Create TagMapper**

**Current usage**:
```java
// createTag()
tag.setUserId(userId);
tag.setName(request.name());
tag.setColor(color);
tag.setCreatedTime(now);

// updateTag()
tag.setName(name);
tag.setColor(color);
tag.setUpdatedTime(now);

// deleteTag()
tag.setDeleted(true);
tag.setUpdatedTime(now);
```

**DTOs available**:
- `CreateTagRequest` (2 fields)
- `UpdateTagRequest` (2 fields)
- `TagDto` (4 fields)

**Required mappers**:
1. `TagMapper.toEntity(CreateTagRequest, userId, color, now)` → Tag
2. `TagMapper.toEntity(Tag, UpdateTagRequest, now)` → Tag (update in place)
3. `TagMapper.toDto(Tag)` → TagDto

---

### 3. TransactionService (20 setters) — HIGH PRIORITY

**Entity**: `Transaction`
**Mapper**: Does not exist → **Create TransactionMapper**

**Current usage**:
```java
// createTransaction()
tx.setTransactionType(...)
tx.setAccountId(...)
tx.setCategoryId(...)
tx.setAmount(...)
tx.setDescription(...)
tx.setTransactionTime(...)
tx.setUserId(...)
tx.setTagIds(...)

// transfer case
transferIn.setTransactionType(5);
transferIn.setAccountId(...);
transferIn.setCategoryId(null);
transferIn.setAmount(...);
transferIn.setDescription(...);
transferIn.setTransactionTime(...);
transferIn.setUserId(...);
transferIn.setRelatedId(...);

// updateTransaction (linked)
existing.setDescription(...);
existing.setTagIds(...);

// updateTransaction (full)
existing.setTransactionType(...);
existing.setAccountId(...);
existing.setCategoryId(...);
existing.setAmount(...);
existing.setDescription(...);
existing.setTagIds(...);
existing.setTransactionTime(...);
```

**DTOs available**:
- `CreateTransactionRequest` (8 fields)
- `UpdateTransactionRequest` (8 fields)
- `TransactionDto` (10 fields)
- `TransactionSearchParams` (for queries)
- `StatisticsDto` (for statistics)

**Required mappers**:
1. `TransactionMapper.toEntity(CreateTransactionRequest, userId, now)` → Transaction
2. `TransactionMapper.toEntity(Transaction, UpdateTransactionRequest)` → Transaction (update)
3. `TransactionMapper.toDto(Transaction)` → TransactionDto
4. `TransactionMapper.toDtoForTransfer(Transaction, relatedId)` → TransactionDto

**Complexity**: Transaction has special cases:
- Transfer creates two linked transactions
- RelatedId set after save
- Balance updates handled separately

---

### 4. AccountService (12 setters) — MEDIUM PRIORITY

**Entity**: `Account`
**Mapper**: ✅ Exists but **underutilized**

**Current usage**:
```java
// createAccount()
account.setName(...);
account.setAccountType(...);
account.setCurrency(...);
account.setBalance(...);
account.setUserId(userId);
account.setDescription(...);
account.setDeleted(false);

// updateAccount()
account.setName(...);
account.setDescription(...);

// softDelete()
account.setDeleted(true);

// updateBalance()
account.setBalance(...);  // Calculated field
```

**Required changes**:
1. Update `AccountMapper` to include all fields
2. Create partial update mapper
3. Replace setters with mapper calls

---

### 5. CategoryService (5 setters) — LOW PRIORITY (already good)

**Entity**: `Category`
**Mapper**: ✅ Exists but **limited**

**Current usage**:
```java
// createCategory()
category.setName(name);
category.setCategoryType(type);
category.setUserId(userId);
category.setParentId(parentId);
```

---

## Refactoring Plan

### Phase 1: Budget (10 setters)
1. Create `BudgetMapper` interface with `@MapperAuto`
2. Replace setters in `BudgetService`
3. Add computed field mapping for `BudgetDto`

### Phase 2: Tag (8 setters)
1. Create `TagMapper` interface with `@MapperAuto`
2. Replace setters in `TagService`
3. Update `TagDto` mapping

### Phase 3: Transaction (20 setters)
1. Create `TransactionMapper` interface with `@MapperAuto`
2. Handle special cases (transfers, relatedId)
3. Replace setters in `TransactionService`

### Phase 4: Account (12 setters)
1. Update existing `AccountMapper` with full coverage
2. Create partial update mapper
3. Replace setters in `AccountService`

---

## Summary

| Entity | Setters | Mapper Status | Effort |
|--------|---------|---------------|--------|
| Transaction | 20 | ❌ Missing | High |
| Budget | 10 | ❌ Missing | High |
| Account | 12 | ⚠️ Partial | Medium |
| Tag | 8 | ❌ Missing | Medium |
| **Total** | **50** | | **~3 hours** |

**Estimated time**: 3-4 hours for full refactoring
**Test impact**: All tests should pass unchanged

---

## Next Action

Start Phase 1: Create `BudgetMapper` and refactor `BudgetService`

```java
// Target: BudgetMapper.java
@MapperAuto
public interface BudgetMapper {
    // To entity (create)
    Budget toEntity(CreateBudgetRequest request, Long userId, Long createdTime);
    
    // Update existing entity
    void updateEntity(@MappingTarget Budget budget, UpdateBudgetRequest request);
    
    // To DTO (with computed fields)
    BudgetDto toDto(Budget budget, String categoryName, Long spent, Double percentUsed);
}
```