-- Initialize databases on first container start
-- This runs automatically when the container is first created

-- Create bookkeeping database
SELECT 'Creating bookkeeping database...' AS status;
CREATE DATABASE bookkeeping;

-- Create bookkeeping_test database  
SELECT 'Creating bookkeeping_test database...' AS status;
CREATE DATABASE bookkeeping_test;

SELECT 'Databases initialized successfully!' AS status;