@echo off
echo Running ForgeGrid...

REM Check if bin directory exists
if not exist "bin" (
    echo Bin directory not found. Please run build.bat first.
    pause
    exit /b 1
)

REM Run the application
java -cp "bin;lib/*" com.forgegrid.app.Main

pause
