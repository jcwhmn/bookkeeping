-- V2__seed_data.sql: Seed data for demo user and default categories
-- Note: This file is kept for reference. Demo user is created by DataInitializer on startup.

-- Income categories
INSERT INTO categories (user_id, name, type, icon, color, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES 
    (1, 'Salary', 'INCOME', 'paid', '#4CAF50', 1, 1717104000, 1717104000, 1, 1),
    (1, 'Bonus', 'INCOME', 'card_giftcard', '#8BC34A', 2, 1717104000, 1717104000, 1, 1),
    (1, 'Investment', 'INCOME', 'trending_up', '#00BCD4', 3, 1717104000, 1717104000, 1, 1),
    (1, 'Gift', 'INCOME', 'volunteer_activism', '#E91E63', 4, 1717104000, 1717104000, 1, 1);

-- Expense categories
INSERT INTO categories (user_id, name, type, icon, color, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES
    (1, 'Food', 'EXPENSE', 'restaurant', '#FF9800', 10, 1717104000, 1717104000, 1, 1),
    (1, 'Transport', 'EXPENSE', 'directions_car', '#2196F3', 20, 1717104000, 1717104000, 1, 1),
    (1, 'Housing', 'EXPENSE', 'home', '#9C27B0', 30, 1717104000, 1717104000, 1, 1),
    (1, 'Utilities', 'EXPENSE', 'power', '#FFC107', 40, 1717104000, 1717104000, 1, 1),
    (1, 'Healthcare', 'EXPENSE', 'local_hospital', '#F44336', 50, 1717104000, 1717104000, 1, 1),
    (1, 'Entertainment', 'EXPENSE', 'movie', '#E91E63', 60, 1717104000, 1717104000, 1, 1),
    (1, 'Shopping', 'EXPENSE', 'shopping_bag', '#673AB7', 70, 1717104000, 1717104000, 1, 1);

-- Child categories (sub-categories)
INSERT INTO categories (user_id, parent_id, name, type, icon, color, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES
    (1, 5, 'Groceries', 'EXPENSE', 'shopping_cart', '#FF9800', 11, 1717104000, 1717104000, 1, 1),
    (1, 5, 'Restaurants', 'EXPENSE', 'fastfood', '#FF9800', 12, 1717104000, 1717104000, 1, 1),
    (1, 6, 'Taxi/Ride', 'EXPENSE', 'local_taxi', '#2196F3', 21, 1717104000, 1717104000, 1, 1),
    (1, 6, 'Gas/Fuel', 'EXPENSE', 'local_gas_station', '#2196F3', 22, 1717104000, 1717104000, 1, 1),
    (1, 7, 'Rent/Mortgage', 'EXPENSE', 'apartment', '#9C27B0', 31, 1717104000, 1717104000, 1, 1);

-- Default accounts for demo user
INSERT INTO accounts (user_id, name, type, currency, balance, icon, color, include_in_total, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES 
    (1, 'Savings', 'SAVING', 'USD', 1000000, 'savings', '#9C27B0', TRUE, 1717104000, 1717104000, 1, 1),
    (1, 'Credit Card', 'CREDIT', 'USD', -120000, 'credit_card', '#FF9800', FALSE, 1717104000, 1717104000, 1, 1);
