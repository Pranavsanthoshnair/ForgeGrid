@echo off
echo Building ForgeGrid...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile Java source files with Java 17 compatibility
javac -cp "lib/*" -d bin --release 17 src/main/java/com/forgegrid/app/*.java src/main/java/com/forgegrid/auth/*.java src/main/java/com/forgegrid/config/*.java src/main/java/com/forgegrid/db/*.java src/main/java/com/forgegrid/model/*.java src/main/java/com/forgegrid/ui/*.java src/main/java/com/forgegrid/ui/components/*.java

REM Copy resources to bin directory
xcopy /E /I /Y "src\main\resources\*" "bin\"

if %ERRORLEVEL% EQU 0 (
    echo Build successful!
    echo.
    echo To run the application, use: run.bat
) else (
    echo Build failed!
    exit /b 1
)
