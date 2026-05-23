@echo off
REM Start Bookkeeping backend with development profile

echo Starting Bookkeeping Backend (Development Profile)...
cd /d "%~dp0..\backend"
gradlew bootRun -Dspring-boot.run.profiles=dev
pause