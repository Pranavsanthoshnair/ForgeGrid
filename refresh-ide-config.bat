@echo off
echo Refreshing IDE configuration to fix ClassNotFoundException...

REM Clean and rebuild
if exist "bin" rmdir /s /q "bin"
call build.bat

REM Force VS Code to reload Java project
if exist ".vscode" (
    echo Forcing VS Code Java extension to reload...
    echo Please restart VS Code or reload the window:
    echo - Press Ctrl+Shift+P
    echo - Type "Developer: Reload Window"
    echo - Press Enter
)

echo.
echo ✅ IDE configuration refreshed!
echo.
echo Now try running Main.java from your IDE:
echo - Right-click on Main.java
echo - Select "Run Java" or "Debug Java"
echo - Or use the Run/Debug button in VS Code
echo.
pause
