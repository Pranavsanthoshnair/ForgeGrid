@echo off
echo Refreshing IDE project configuration...

REM Clean the project
if exist "bin" rmdir /s /q "bin"
if exist ".vscode\settings.json" del ".vscode\settings.json"

REM Recreate VS Code settings
echo {> .vscode\settings.json
echo     "java.project.sourcePaths": [>> .vscode\settings.json
echo         "src/main/java">> .vscode\settings.json
echo     ],>> .vscode\settings.json
echo     "java.project.resourceFilters": [>> .vscode\settings.json
echo         "node_modules",>> .vscode\settings.json
echo         ".git">> .vscode\settings.json
echo     ],>> .vscode\settings.json
echo     "java.project.referencedLibraries": [>> .vscode\settings.json
echo         "lib/gson-2.10.1.jar",>> .vscode\settings.json
echo         "lib/sqlite-jdbc-3.44.1.0.jar",>> .vscode\settings.json
echo         "lib/slf4j-api-1.7.36.jar",>> .vscode\settings.json
echo         "lib/slf4j-simple-1.7.36.jar">> .vscode\settings.json
echo     ],>> .vscode\settings.json
echo     "java.compile.nullAnalysis.mode": "automatic",>> .vscode\settings.json
echo     "java.configuration.updateBuildConfiguration": "automatic",>> .vscode\settings.json
echo     "java.project.outputPath": "bin">> .vscode\settings.json
echo }>> .vscode\settings.json

echo ✅ IDE configuration refreshed!
echo.
echo Please restart your IDE or reload the window:
echo - VS Code: Ctrl+Shift+P → "Developer: Reload Window"
echo - IntelliJ: File → Reload Gradle Project
echo - Eclipse: Right-click project → Refresh
echo.
pause

