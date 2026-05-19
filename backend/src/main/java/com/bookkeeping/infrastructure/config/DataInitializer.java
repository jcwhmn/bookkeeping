package com.bookkeeping.infrastructure.config;

import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) {
        // Create demo user if not exists
        if (userRepository.findByUsername("demo").isEmpty()) {
            log.info("Creating demo user...");
            User demo = new User();
            demo.setUsername("demo");
            demo.setEmail("demo@example.com");
            demo.setNickname("Demo User");
            demo.setPassword(passwordEncoder.encode("demo123"));
            demo.setSalt("demo-salt");
            demo.setDefaultCurrency("USD");
            demo.setLanguage("en-US");
            demo.setDisabled(false);
            demo = userRepository.save(demo);
            
            // Create default accounts
            Account cash = new Account();
            cash.setUserId(demo.getId());
            cash.setName("Cash");
            cash.setType(AccountType.CASH);
            cash.setCurrency("USD");
            cash.setBalance(250000L);
            cash.setIcon("wallet");
            cash.setColor("#4CAF50");
            cash.setIncludeInTotal(true);
            accountRepository.save(cash);
            
            Account checking = new Account();
            checking.setUserId(demo.getId());
            checking.setName("Checking (Chase)");
            checking.setType(AccountType.CHECKING);
            checking.setCurrency("USD");
            checking.setBalance(520050L);
            checking.setIcon("account_balance");
            checking.setColor("#2196F3");
            checking.setIncludeInTotal(true);
            accountRepository.save(checking);
            
            log.info("Demo user and accounts created with id={}", demo.getId());
        } else {
            log.info("Demo user already exists");
        }
    }
}