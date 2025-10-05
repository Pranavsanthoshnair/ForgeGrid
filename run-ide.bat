@echo off
echo Running ForgeGrid from IDE...

REM Check if bin directory exists
if not exist "bin" (
    echo Building project first...
    call build.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed!
        pause
        exit /b 1
    )
)

REM Run the application with explicit classpath
java -cp "bin;lib/sqlite-jdbc-3.44.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-simple-1.7.36.jar" com.forgegrid.app.Main

pause
