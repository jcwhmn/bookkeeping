package com.bookkeeping.config;

import com.bookkeeping.common.enums.AccountType;
import com.bookkeeping.common.enums.CategoryType;
import com.bookkeeping.core.account.Account;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.category.Category;
import com.bookkeeping.core.category.CategoryRepository;
import com.bookkeeping.core.transaction.Transaction;
import com.bookkeeping.core.transaction.TransactionRepository;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Creates demo data for development/testing.
 * Disabled during integration tests.
 */
@Component
@Profile("!integrationtest")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public DataInitializer(UserRepository userRepository, AccountRepository accountRepository,
                           CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            User demoUser = createDemoUserIfNeeded();
            // If user is new, createDemoUserIfNeeded() returns the new user.
            // If user already exists, fetch them so we can still seed their data
            // (accounts/categories/transactions) when missing.
            if (demoUser == null) {
                demoUser = userRepository.findByUsername("demo").orElse(null);
            }
            if (demoUser != null) {
                List<Account> accounts = createDemoAccounts(demoUser);
                List<Category> categories = createDemoCategories(demoUser);
                if (categories != null && !categories.isEmpty()) {
                    createDemoTransactions(demoUser, accounts, categories);
                }
            }
        } catch (Exception e) {
            log.warn("DataInitializer skipped (likely running in test mode): {}", e.getMessage());
        }
    }

    private User createDemoUserIfNeeded() {
        if (userRepository.existsByUsername("demo")) {
            log.info("Demo user already exists, skipping seed data");
            return null;
        }
        User demoUser = User.builder()
                .username("demo")
                .email("demo@example.com")
                .nickname("Demo User")
                .salt("salt123")
                .password(hashPassword("demo123", "salt123"))
                .defaultCurrency("USD")
                .language("en-US")
                .emailVerified(true)
                .disabled(false)
                .build();
        demoUser = userRepository.save(demoUser);
        log.info("✓ Demo user created: username=demo, password=demo123");
        return demoUser;
    }

    private List<Account> createDemoAccounts(User demoUser) {
        if (accountRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(demoUser.getId()).size() > 0) {
            log.info("Accounts already exist for demo user");
            return accountRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(demoUser.getId());
        }
        createAccount(demoUser, "Cash", AccountType.CASH, "USD", 150000L, "Daily cash expenses");
        createAccount(demoUser, "Bank Checking", AccountType.CHECKING, "USD", 8500000L, "Primary checking");
        createAccount(demoUser, "Savings", AccountType.SAVINGS, "USD", 25000000L, "Emergency fund");
        createAccount(demoUser, "Credit Card", AccountType.CREDIT, "USD", -350000L, "Monthly credit");
        createAccount(demoUser, "Stock Portfolio", AccountType.INVESTMENT, "USD", 50000000L, "Long-term stock");
        log.info("✓ 5 demo accounts created");
        return accountRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(demoUser.getId());
    }

    private List<Category> createDemoCategories(User demoUser) {
        if (categoryRepository.findByUserIdOrderBySortOrderAsc(demoUser.getId()).size() > 0) {
            log.info("Categories already exist for demo user");
            return categoryRepository.findByUserIdOrderBySortOrderAsc(demoUser.getId());
        }
        // Income categories
        createCategory(demoUser, "Salary", CategoryType.INCOME);
        createCategory(demoUser, "Freelance", CategoryType.INCOME);
        createCategory(demoUser, "Investment", CategoryType.INCOME);
        createCategory(demoUser, "Other Income", CategoryType.INCOME);
        // Expense categories
        createCategory(demoUser, "Food & Dining", CategoryType.EXPENSE);
        createCategory(demoUser, "Transportation", CategoryType.EXPENSE);
        createCategory(demoUser, "Shopping", CategoryType.EXPENSE);
        createCategory(demoUser, "Housing", CategoryType.EXPENSE);
        createCategory(demoUser, "Entertainment", CategoryType.EXPENSE);
        createCategory(demoUser, "Healthcare", CategoryType.EXPENSE);
        createCategory(demoUser, "Utilities", CategoryType.EXPENSE);
        createCategory(demoUser, "Other Expense", CategoryType.EXPENSE);
        log.info("✓ 12 demo categories created");
        return categoryRepository.findByUserIdOrderBySortOrderAsc(demoUser.getId());
    }

    private void createDemoTransactions(User demoUser, List<Account> accounts, List<Category> categories) {
        if (transactionRepository.count() > 0) {
            log.info("Transactions already exist");
            return;
        }
        Long cashId = accounts.get(0).getId();
        Long checkingId = accounts.get(1).getId();
        Long now = System.currentTimeMillis() / 1000;

        List<Category> incomeCategories = categories.stream().filter(c -> c.getCategoryType() == CategoryType.INCOME).toList();
        List<Category> expenseCategories = categories.stream().filter(c -> c.getCategoryType() == CategoryType.EXPENSE).toList();

        // This month: salary income
        createTx(demoUser, 2, checkingId, incomeCategories.get(0).getId(), 9000000L, "Monthly salary", now - 86400);

        // Expense transactions (last 7 days)
        createTx(demoUser, 3, cashId, expenseCategories.get(0).getId(), 3500L, "Lunch at restaurant", now - 86400);
        createTx(demoUser, 3, cashId, expenseCategories.get(1).getId(), 1500L, "Bus fare", now - 2*86400);
        createTx(demoUser, 3, cashId, expenseCategories.get(2).getId(), 8900L, "New shoes", now - 3*86400);
        createTx(demoUser, 3, checkingId, expenseCategories.get(3).getId(), 200000L, "Monthly rent", now - 5*86400);
        createTx(demoUser, 3, cashId, expenseCategories.get(4).getId(), 4500L, "Movie tickets", now - 6*86400);
        createTx(demoUser, 3, cashId, expenseCategories.get(5).getId(), 12000L, "Pharmacy", now - 7*86400);
        createTx(demoUser, 3, checkingId, expenseCategories.get(6).getId(), 15000L, "Electricity bill", now - 10*86400);

        // Freelance income
        createTx(demoUser, 2, checkingId, incomeCategories.get(1).getId(), 300000L, "Freelance project", now - 4*86400);

        log.info("✓ 9 demo transactions created");
    }

    private void createAccount(User user, String name, AccountType type, String currency, Long balance, String desc) {
        Account a = Account.builder()
                .name(name)
                .accountType(type)
                .currency(currency)
                .balance(balance)
                .userId(user.getId())
                .description(desc)
                .deleted(false)
                .sortOrder(0)
                .hidden(false)
                .parentId(null)
                .build();
        accountRepository.save(a);
    }

    private void createCategory(User user, String name, CategoryType type) {
        Category c = Category.builder()
                .name(name)
                .categoryType(type)
                .userId(user.getId())
                .sortOrder(0)
                .hidden(false)
                .build();
        categoryRepository.save(c);
    }

    private void createTx(User user, int type, Long accountId, Long categoryId, Long amount, String desc, Long time) {
        Transaction tx = Transaction.builder()
                .transactionType(type)
                .accountId(accountId)
                .categoryId(categoryId)
                .amount(amount)
                .description(desc)
                .transactionTime(time)
                .userId(user.getId())
                .build();
        transactionRepository.save(tx);
    }

    private String hashPassword(String password, String salt) {
        try {
            String s = salt + password;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) { sb.append(String.format("%02x", b)); }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
