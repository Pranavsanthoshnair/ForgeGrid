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
