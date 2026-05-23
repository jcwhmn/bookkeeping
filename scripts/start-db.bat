@echo off
REM Start PostgreSQL and initialize databases for Bookkeeping application

echo ========================================
echo  Bookkeeping - Database Setup
echo ========================================
echo.

cd /d "%~dp0.."

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [1/3] Stopping existing container if any...
docker-compose -f docker-compose.yml stop bookkeeping-db 2>nul

echo [2/3] Removing old container...
docker-compose -f docker-compose.yml rm -f bookkeeping-db 2>nul

echo [3/3] Starting PostgreSQL container...
docker-compose -f docker-compose.yml up -d

echo.
echo Waiting for PostgreSQL to be ready...
timeout /t 10 /nobreak >nul

REM Check if databases were created
docker exec bookkeeping-db psql -U bookkeeping -d postgres -c "SELECT datname FROM pg_database WHERE datname LIKE 'bookkeeping%';" 2>nul

echo.
echo ========================================
echo  Database Setup Complete!
echo ========================================
echo.
echo Databases available:
echo   - bookkeeping     (production)
echo   - bookkeeping_dev  (development)
echo   - bookkeeping_test (testing)
echo.
echo Connection info:
echo   Host: localhost:5432
echo   User: bookkeeping
echo   Password: test123
echo   Databases: bookkeeping, bookkeeping_dev, bookkeeping_test
echo.
pause