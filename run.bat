@echo off
setlocal

REM Change to the directory where this script is located
cd /d "%~dp0"

REM Ensure build output exists
if not exist "bin" (
  echo bin directory not found. Please run build.bat first.
  exit /b 1
)

REM Prefer our freshly built classes and local libs
set "CP=bin;lib\*"

echo Running ForgeGrid...
java -cp "%CP%" com.forgegrid.app.Main

endlocal
@echo off
setlocal enabledelayedexpansion

REM Change to the directory where the batch file is located
cd /d "%~dp0"

echo Running ForgeGrid...
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Error: bin directory not found!
    echo Please run build.bat first to compile the application.
    exit /b 1
)

REM Run the application
java -cp "bin;lib/*" com.forgegrid.app.Main

endlocal
