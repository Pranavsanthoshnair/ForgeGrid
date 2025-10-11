## ForgeGrid Technical Setup

### Prerequisites
- Java 17+ (JDK). Verify with:
```bash
java -version
javac -version
```
- Git (optional, for source control)
- On Windows, PowerShell/CMD; on macOS/Linux, a shell.

### Project Layout (key paths)
- `src/main/java` — source code
- `src/main/resources` — resources (icons, config)
- `lib/` — third‑party jars (MySQL JDBC connector)
- `build.sh` and `build.bat` — helpers to compile
- `run.bat` — run helper for Windows

### Build (from source)
Windows (PowerShell/CMD):
```bat
build.bat
```

macOS/Linux:
```bash
chmod +x build.sh
./build.sh
```

What the build does
- Compiles sources under `src/main/java` to `bin/`.
- Copies resources to the classpath.
- Ensures the required `lib/*.jar` (MySQL JDBC connector) are available.

### Run
Windows:
```bat
run.bat
```

macOS/Linux (from project root):
```bash
java -cp "bin;lib/*" com.forgegrid.app.Main   # On Windows
java -cp "bin:lib/*" com.forgegrid.app.Main   # On macOS/Linux
```

Entry Point
- Main class: `com.forgegrid.app.Main`

### Database
- **Railway MySQL Database**: Cloud-hosted MySQL database
- **JDBC Driver**: `lib/mysql-connector-j-8.0.33.jar` for MySQL connectivity
- **Configuration**: Railway credentials stored in `.env` file (gitignored)
- **Connection**: Automatic SSL-enabled connection to Railway MySQL

### Environment Configuration
- **Credentials**: Stored securely in `.env` file
- **Fallback**: Defaults to localhost for development (if no Railway config)
- **Security**: All sensitive data excluded from version control

### Logging
- **Console Logging**: Comprehensive migration and connection logging
- **Success Indicators**: Clear ✅/❌ status messages for Railway connection
- **Debug Info**: Detailed connection URL parsing and credential extraction

### Packaging (optional)
If you want a runnable fat‑jar (manual example):
```bash
jar --create --file forgegrid.jar -C bin .
# run with external libs on classpath
java -cp "forgegrid.jar:lib/*" com.forgegrid.app.Main
```

### Common Issues
- **Railway Connection**: Ensure Railway MySQL service is running and credentials are correct
- **Environment Variables**: Check `.env` file exists and contains valid Railway credentials
- **Classpath**: Ensure `lib/mysql-connector-j-8.0.33.jar` is included in classpath
- **Java Version**: Use JDK 17+; older JDKs may fail to compile
- **SSL Issues**: Railway requires SSL connections; ensure `useSSL=true` in connection string

### Development Tips
- Use an IDE (IntelliJ/Eclipse/VS Code) and set the project SDK to Java 17+.
- Mark `src/main/resources` as resources folder to include icons/config on classpath.
- Run `com.forgegrid.app.Main` directly from the IDE.


