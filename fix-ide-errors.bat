@echo off
echo Fixing IDE build errors...

REM Clean everything
if exist "bin" rmdir /s /q "bin"
if exist ".vscode" rmdir /s /q ".vscode"
if exist ".classpath" del ".classpath"
if exist ".project" del ".project"

REM Create fresh .vscode directory
mkdir .vscode

REM Create fresh VS Code settings
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

REM Create fresh launch configuration
echo {> .vscode\launch.json
echo     "version": "0.2.0",>> .vscode\launch.json
echo     "configurations": [>> .vscode\launch.json
echo         {>> .vscode\launch.json
echo             "type": "java",>> .vscode\launch.json
echo             "name": "Launch ForgeGrid",>> .vscode\launch.json
echo             "request": "launch",>> .vscode\launch.json
echo             "mainClass": "com.forgegrid.app.Main",>> .vscode\launch.json
echo             "projectName": "forgegrid",>> .vscode\launch.json
echo             "classPaths": [>> .vscode\launch.json
echo                 "${workspaceFolder}/lib/gson-2.10.1.jar",>> .vscode\launch.json
echo                 "${workspaceFolder}/lib/sqlite-jdbc-3.44.1.0.jar",>> .vscode\launch.json
echo                 "${workspaceFolder}/lib/slf4j-api-1.7.36.jar",>> .vscode\launch.json
echo                 "${workspaceFolder}/lib/slf4j-simple-1.7.36.jar">> .vscode\launch.json
echo             ],>> .vscode\launch.json
echo             "modulePaths": [],>> .vscode\launch.json
echo             "args": "",>> .vscode\launch.json
echo             "vmArgs": "",>> .vscode\launch.json
echo             "console": "integratedTerminal">> .vscode\launch.json
echo         }>> .vscode\launch.json
echo     ]>> .vscode\launch.json
echo }>> .vscode\launch.json

REM Create fresh Eclipse .classpath
echo ^<?xml version="1.0" encoding="UTF-8"?^> > .classpath
echo ^<classpath^> >> .classpath
echo 	^<classpathentry kind="src" path="src/main/java"/^> >> .classpath
echo 	^<classpathentry kind="src" path="src/main/resources"/^> >> .classpath
echo 	^<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17"/^> >> .classpath
echo 	^<classpathentry kind="lib" path="lib/gson-2.10.1.jar"/^> >> .classpath
echo 	^<classpathentry kind="lib" path="lib/sqlite-jdbc-3.44.1.0.jar"/^> >> .classpath
echo 	^<classpathentry kind="lib" path="lib/slf4j-api-1.7.36.jar"/^> >> .classpath
echo 	^<classpathentry kind="lib" path="lib/slf4j-simple-1.7.36.jar"/^> >> .classpath
echo 	^<classpathentry kind="output" path="bin"/^> >> .classpath
echo ^</classpath^> >> .classpath

REM Create fresh Eclipse .project
echo ^<?xml version="1.0" encoding="UTF-8"?^> > .project
echo ^<projectDescription^> >> .project
echo 	^<name^>ForgeGrid^</name^> >> .project
echo 	^<comment^>^</comment^> >> .project
echo 	^<projects^> >> .project
echo 	^</projects^> >> .project
echo 	^<buildSpec^> >> .project
echo 		^<buildCommand^> >> .project
echo 			^<name^>org.eclipse.jdt.core.javabuilder^</name^> >> .project
echo 			^<arguments^> >> .project
echo 			^</arguments^> >> .project
echo 		^</buildCommand^> >> .project
echo 	^</buildSpec^> >> .project
echo 	^<natures^> >> .project
echo 		^<nature^>org.eclipse.jdt.core.javanature^</nature^> >> .project
echo 	^</natures^> >> .project
echo 	^<filteredResources^> >> .project
echo 		^<filter^> >> .project
echo 			^<id^>1759632800475^</id^> >> .project
echo 			^<name^>^</name^> >> .project
echo 			^<type^>30^</type^> >> .project
echo 			^<matcher^> >> .project
echo 				^<id^>org.eclipse.core.resources.regexFilterMatcher^</id^> >> .project
echo 				^<arguments^>node_modules^.git^__CREATED_BY_JAVA_LANGUAGE_SERVER__^</arguments^> >> .project
echo 			^</matcher^> >> .project
echo 		^</filter^> >> .project
echo 	^</filteredResources^> >> .project
echo ^</projectDescription^> >> .project

REM Build the project
call build.bat

echo.
echo ✅ IDE configuration completely refreshed!
echo.
echo Please restart your IDE completely:
echo - Close the IDE
echo - Reopen the project
echo - The build errors should be gone
echo.
pause
