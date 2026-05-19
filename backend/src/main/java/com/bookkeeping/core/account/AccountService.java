package com.bookkeeping.core.account;

import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByUser(Long userId) {
        return accountRepository.findAllByUserNotDeleted(userId)
            .stream()
            .map(AccountDto::fromEntity)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long userId, Long accountId) {
        Account account = accountRepository.findByUserAndIdNotDeleted(userId, accountId)
            .orElseThrow(() -> BusinessException.notFound(ResultCode.ACCOUNT_NOT_FOUND));
        return AccountDto.fromEntity(account);
    }
    
    @Transactional
    public AccountDto createAccount(Long userId, CreateAccountRequest request) {
        // Check for duplicate name
        if (accountRepository.findByUserAndName(userId, request.name()).isPresent()) {
            throw BusinessException.conflict(ResultCode.ACCOUNT_DUPLICATE_NAME, "Account with this name already exists");
        }
        
        Account account = new Account();
        account.setUserId(userId);
        account.setName(request.name());
        account.setType(AccountType.valueOf(request.type()));
        account.setCurrency(request.currency() != null ? request.currency() : "USD");
        account.setBalance(request.balanceStr() != null ? Long.parseLong(request.balanceStr()) : 0L);
        account.setIcon(request.icon());
        account.setColor(request.color());
        account.setNotes(request.notes());
        account.setIncludeInTotal(request.includeInTotalStr() != null ? Boolean.parseBoolean(request.includeInTotalStr()) : true);
        
        Account saved = accountRepository.save(account);
        log.info("Account created: {} for user {}", saved.getId(), userId);
        
        return AccountDto.fromEntity(saved);
    }
    
    @Transactional
    public AccountDto updateAccount(Long userId, Long accountId, CreateAccountRequest request) {
        Account account = accountRepository.findByUserAndIdNotDeleted(userId, accountId)
            .orElseThrow(() -> BusinessException.notFound(ResultCode.ACCOUNT_NOT_FOUND));
        
        // Check for duplicate name (if name changed)
        if (!account.getName().equals(request.name())) {
            if (accountRepository.findByUserAndName(userId, request.name()).isPresent()) {
                throw BusinessException.conflict(ResultCode.ACCOUNT_DUPLICATE_NAME, "Account with this name already exists");
            }
        }
        
        account.setName(request.name());
        account.setType(AccountType.valueOf(request.type()));
        account.setCurrency(request.currency() != null ? request.currency() : "USD");
        if (request.balanceStr() != null) {
            account.setBalance(Long.parseLong(request.balanceStr()));
        }
        account.setIcon(request.icon());
        account.setColor(request.color());
        account.setNotes(request.notes());
        if (request.includeInTotalStr() != null) {
            account.setIncludeInTotal(Boolean.parseBoolean(request.includeInTotalStr()));
        }
        
        Account saved = accountRepository.save(account);
        log.info("Account updated: {}", accountId);
        
        return AccountDto.fromEntity(saved);
    }
    
    @Transactional
    public void deleteAccount(Long userId, Long accountId) {
        Account account = accountRepository.findByUserAndIdNotDeleted(userId, accountId)
            .orElseThrow(() -> BusinessException.notFound(ResultCode.ACCOUNT_NOT_FOUND));
        
        account.setDeleted(true);
        account.setDeletedUnixTime(System.currentTimeMillis() / 1000);
        accountRepository.save(account);
        
        log.info("Account deleted: {}", accountId);
    }
    
    public record CreateAccountRequest(
        String name,
        String type,
        String currency,
        String balanceStr,
        String icon,
        String color,
        String notes,
        String includeInTotalStr
    ) {}
}