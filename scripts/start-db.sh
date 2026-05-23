#!/bin/bash
# Start PostgreSQL and initialize databases for Bookkeeping application

cd "$(dirname "$0")/.."

echo "========================================"
echo " Bookkeeping - Database Setup"
echo "========================================"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker is not running. Please start Docker first."
    exit 1
fi

echo "[1/3] Stopping existing container if any..."
docker-compose -f docker-compose.yml stop bookkeeping-db 2>/dev/null

echo "[2/3] Removing old container..."
docker-compose -f docker-compose.yml rm -f bookkeeping-db 2>/dev/null

echo "[3/3] Starting PostgreSQL container..."
docker-compose -f docker-compose.yml up -d

echo ""
echo "Waiting for PostgreSQL to be ready..."
sleep 10

# Check if databases were created
echo ""
echo "Databases created:"
docker exec bookkeeping-db psql -U bookkeeping -d postgres -c "SELECT datname FROM pg_database WHERE datname LIKE 'bookkeeping%';" 2>/dev/null

echo ""
echo "========================================"
echo " Database Setup Complete!"
echo "========================================"
echo ""
echo "Databases available:"
echo "  - bookkeeping     (production)"
echo "  - bookkeeping_dev  (development)"
echo "  - bookkeeping_test (testing)"
echo ""
echo "Connection info:"
echo "  Host: localhost:5432"
echo "  User: bookkeeping"
echo "  Password: test123"
echo "  Databases: bookkeeping, bookkeeping_dev, bookkeeping_test"
echo ""