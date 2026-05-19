#!/bin/bash

# Create PostgreSQL databases for bookkeeping project
# Run with: ./scripts/create-databases.sh

echo "Creating databases..."

# Default values
HOST="${DB_HOST:-localhost}"
PORT="${DB_PORT:-5432}"
USER="${DB_USER:-bookkeeping}"
PASSWORD="${DB_PASSWORD:-123456}"

export PGPASSWORD="$PASSWORD"

# Create databases if they don't exist
for db in bookkeeping bookkeeping_dev bookkeeping_test; do
    # Check if database exists
    if psql -h "$HOST" -p "$PORT" -U "$USER" -lqt | cut -d \| -f 1 | grep -qw "$db"; then
        echo "  ✓ Database '$db' already exists"
    else
        echo "  Creating database '$db'..."
        psql -h "$HOST" -p "$PORT" -U "$USER" -tc "SELECT 1 FROM pg_database WHERE datname = '$db'" | grep -q 1 || \
        psql -h "$HOST" -p "$PORT" -U "$USER" -c "CREATE DATABASE $db"
        echo "  ✓ Database '$db' created"
    fi
done

unset PGPASSWORD
echo "Done!"