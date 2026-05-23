-- Initialize databases for bookkeeping application
-- This script is auto-run by PostgreSQL container on first start

-- Create development database
SELECT 'Creating bookkeeping_dev database...' AS status;
CREATE DATABASE bookkeeping_dev;

-- Create test database
SELECT 'Creating bookkeeping_test database...' AS status;
CREATE DATABASE bookkeeping_test;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE bookkeeping_dev TO bookkeeping;
GRANT ALL PRIVILEGES ON DATABASE bookkeeping_test TO bookkeeping;

SELECT 'Databases initialized successfully!' AS status;

-- Switch to bookkeeping (default/prod)
\c bookkeeping

-- Create superuser if needed
SELECT 'Current database: bookkeeping' AS status;
SELECT datname AS database, pg_size_pretty(pg_database_size(datname)) AS size 
FROM pg_database 
WHERE datname IN ('postgres', 'bookkeeping', 'bookkeeping_dev', 'bookkeeping_test')
ORDER BY datname;