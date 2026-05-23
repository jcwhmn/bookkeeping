---
name: bookkeeping-patterns
description: "Use when implementing or modifying Java code in the Bookkeeping project. Provides mandatory patterns for entity construction (Builder+toBuilder, no setters), DTO mapping (@MapperAuto on records), test fixtures (withId), and service design. Always consult this skill before writing any entity, DTO, service, or test class in this project."
---

# Bookkeeping Patterns

Mandatory patterns for the Bookkeeping application. All Java code must follow these.

## Patterns

### 1. Entity Construction (No Setters)

All entities use `@Builder(toBuilder = true)` with `@NoArgsConstructor(access = PROTECTED)`:

```java
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseEntity {
    private String name;
    private AccountType accountType;
    // ... all fields listed in @AllArgsConstructor
}
```

**Rules:**
- NEVER add `@Setter` to entities
- NEVER use `@Data` on entities
- Always use `AccessLevel.PROTECTED` for `@NoArgsConstructor` (JPA requirement)
- Include `@AllArgsConstructor` so builder works correctly
- BaseEntity fields (id, createdAt, updatedAt) are set by JPA lifecycle, NOT by builder

### 2. Creating Entities (Builder Pattern)

```java
// ✅ CORRECT — single builder expression
Account account = Account.builder()
        .name("Cash Wallet")
        .accountType(AccountType.CASH)
        .currency("USD")
        .balance(100000L)
        .userId(userId)
        .description("Main wallet")
        .deleted(false)
        .build();
accountRepository.save(account);

// ❌ WRONG — never use setters
Account account = new Account();
account.setName("Cash Wallet");  // setter doesn't exist!
```

### 3. Updating Entities (toBuilder Pattern)

```java
// ✅ CORRECT — toBuilder() creates a copy with modified fields
Account updated = account.toBuilder()
        .name(newName)
        .description(newDesc)
        .build();
return accountRepository.save(updated);

// For single field changes (e.g., soft delete):
accountRepository.save(account.toBuilder().deleted(true).build());

// For balance adjustments:
accountRepository.save(account.toBuilder()
        .balance(account.getBalance() + amountChange)
        .build());
```

### 4. Test Fixtures — Setting Entity IDs

`BaseEntity.withId(Long)` is the ONLY way to set an entity's ID:

```java
// ✅ CORRECT — use withId() for test fixtures
User testUser = User.builder()
        .username("testuser")
        .email("test@example.com")
        .build()
        .withId(1L);

Account testAccount = Account.builder()
        .name("Test")
        .accountType(AccountType.CASH)
        .currency("USD")
        .balance(100L)
        .userId(1L)
        .deleted(false)
        .build()
        .withId(10L);

// NEVER call withId() in production code — IDs are set by JPA
```

### 5. Mock Save in Tests with toBuilder

Since entities are immutable after construction, use `thenAnswer` for repository.save():

```java
// ✅ CORRECT — return the saved argument (with optional id via withId)
when(accountRepository.save(any(Account.class)))
    .thenAnswer(inv -> inv.getArgument(0));

// For mutations, capture with ArgumentCaptor:
ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
verify(accountRepository).save(captor.capture());
assertTrue(captor.getValue().getDeleted());

// ❌ WRONG — don't return a reference to a test entity that gets mutated
when(accountRepository.save(testAccount)).thenReturn(testAccount);
// testAccount won't be mutated since no setters exist
```

### 6. MapStructPlus DTO Mapping (@MapperAuto on DTO)

Place `@MapperAuto` on the DTO record, not on a separate interface:

```java
// ✅ CORRECT — annotation on DTO
@MapperAuto(sourceEntity = User.class, direction = Direction.From)
public record UserDto(Long id, String username, ...) {}

// Auto-generated: UserMapper interface + UserDtoMapperConverter @Component

// Inject in service:
@Service
public class UserService {
    private final UserMapper userMapper;  // injected via @Component
}
```

**Naming convention:**
- DTO `UserDto` → interface `UserMapper` → converter `UserDtoMapperConverter`
- DTO `AccountDto` → interface `AccountMapper` → converter `AccountDtoMapperConverter`

### 7. Service Constructor Injection

```java
// ✅ CORRECT — final fields + constructor injection
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final SecurityUtils securityUtils;

    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          SecurityUtils securityUtils) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.securityUtils = securityUtils;
    }
}
```

## Quick Reference

| Task | Pattern |
|------|---------|
| Create entity | `Entity.builder().field(v).build()` |
| Update entity | `entity.toBuilder().field(newV).build()` + `save()` |
| Soft delete | `entity.toBuilder().deleted(true).updatedTime(now).build()` + `save()` |
| Set entity ID (tests) | `.build().withId(1L)` |
| Map entity → DTO | `mapper.toDto(entity)` (mapper auto-generated by MapStruct+) |
| Mock save (tests) | `.thenAnswer(inv -> inv.getArgument(0))` |
| Verify save (tests) | `ArgumentCaptor` or `argThat(a -> a.getField().equals(x))` |
