@echo off
REM Stop PostgreSQL container

echo Stopping PostgreSQL...
cd /d "%~dp0.."
docker-compose stop bookkeeping-db
echo PostgreSQL stopped.
pause