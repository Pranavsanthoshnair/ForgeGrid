@echo off
setlocal enabledelayedexpansion

REM Change to the directory where the batch file is located
cd /d "%~dp0"

echo Building ForgeGrid...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Find all Java files and compile them
echo Compiling Java source files...
dir /s /b src\main\java\*.java > sources.txt
javac -cp "lib/*" -d bin --release 17 @sources.txt

REM Check compilation result
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    
    REM Copy resources to bin directory if they exist
    if exist "src\main\resources" (
        echo Copying resources...
        xcopy /E /I /Y "src\main\resources\*" "bin\" >nul 2>&1
    )
    
    echo.
    echo Build successful!
    echo.
    echo To run the application:
    echo   run.bat
    echo.
    echo Or from your IDE:
    echo   - Right-click on Main.java
    echo   - Select "Run Java"
) else (
    echo Build failed!
)

REM Clean up temporary file
if exist sources.txt del sources.txt

endlocal
