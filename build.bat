@echo off
setlocal enabledelayedexpansion

REM Change to the directory where the batch file is located
cd /d "%~dp0"

echo Building ForgeGrid...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Find all Java files and compile them
echo Compiling Java source files...
javac -cp "lib/*" -d bin --release 17 -J-Dfile.encoding=UTF-8 src\main\java\com\forgegrid\app\*.java src\main\java\com\forgegrid\auth\*.java src\main\java\com\forgegrid\config\*.java src\main\java\com\forgegrid\controller\*.java src\main\java\com\forgegrid\db\*.java src\main\java\com\forgegrid\model\*.java src\main\java\com\forgegrid\service\*.java src\main\java\com\forgegrid\ui\*.java

REM Check compilation result
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    
    REM Copy resources to bin directory if they exist
    if exist "src\main\resources" (
        echo Copying resources...
        xcopy /E /I /Y "src\main\resources\*" "bin\" >nul 2>&1
        
        REM Ensure the icon directory structure is properly copied
        if exist "src\main\resources\com\forgegrid\icon" (
            echo Copying icon resources...
            if not exist "bin\com\forgegrid\icon" mkdir "bin\com\forgegrid\icon"
            xcopy /Y "src\main\resources\com\forgegrid\icon\*" "bin\com\forgegrid\icon\" >nul 2>&1
            echo Icon resources copied to: bin\com\forgegrid\icon\
        )
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

REM Clean up completed

endlocal
