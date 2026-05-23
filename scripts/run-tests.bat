@echo off
REM Run tests against test database

echo Running Bookkeeping Tests...
cd /d "%~dp0..\backend"
gradlew test
pause