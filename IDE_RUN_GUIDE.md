# 🚀 Running ForgeGrid from IDE

## Quick Solutions

### Option 1: Use the IDE Launcher (Recommended)
1. Open `src/main/java/com/forgegrid/app/IDE.java` in your IDE
2. Right-click on the file and select "Run IDE.main()"
3. This will check dependencies and launch the application

### Option 2: Use the Launch Configuration
1. Press `F5` or go to Run → Start Debugging
2. Select "Launch ForgeGrid" from the dropdown
3. The application will start with proper classpath

### Option 3: Use the Run Script
1. Double-click `run-ide.bat` (Windows) or `run-ide.sh` (Linux/Mac)
2. This will build and run the application automatically

## Troubleshooting

### If you get "ClassNotFoundException" errors:

1. **Check JAR files exist:**
   - `lib/gson-2.10.1.jar`
   - `lib/sqlite-jdbc-3.44.1.0.jar`

2. **Refresh IDE project:**
   - VS Code: `Ctrl+Shift+P` → "Java: Reload Projects"
   - IntelliJ: File → Reload Gradle Project
   - Eclipse: Right-click project → Refresh

3. **Manual classpath setup:**
   - Add JAR files to project build path
   - Or use the explicit launch configurations provided

### If the application doesn't start:

1. **Build first:** Run `.\build.bat` (Windows) or `./build.sh` (Linux/Mac)
2. **Check Java version:** Ensure you're using Java 17 or higher
3. **Verify resources:** Make sure `logo2_transparent.png` is in `src/main/resources/com/forgegrid/icon/`

## Available Run Methods

| Method | Description | Best For |
|--------|-------------|----------|
| `IDE.java` | IDE launcher with dependency checks | Development |
| Launch Config | VS Code/IntelliJ configuration | IDE integration |
| `run-ide.bat/sh` | Command line script | Quick testing |
| `run.bat/sh` | Standard run script | Production |

## Dependencies

The application requires:
- **Java 17+** (JDK or JRE)
- **Gson 2.10.1** (for JSON handling)
- **SQLite JDBC 3.44.1.0** (for database)
- **SLF4J API 1.7.36** (logging interface)
- **SLF4J Simple 1.7.36** (logging implementation)

All dependencies are included in the `lib/` folder.

### Required JAR Files:
- `lib/gson-2.10.1.jar`
- `lib/sqlite-jdbc-3.44.1.0.jar`
- `lib/slf4j-api-1.7.36.jar`
- `lib/slf4j-simple-1.7.36.jar`
