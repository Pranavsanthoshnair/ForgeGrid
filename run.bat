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
