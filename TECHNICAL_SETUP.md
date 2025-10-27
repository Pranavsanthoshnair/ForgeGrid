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
- `src/main/java` — source code (views, controllers, services, models, db)
- `src/main/resources` — resources (icons, config)
- `lib/` — MySQL JDBC connector (`mysql-connector-j-8.0.33.jar`)
- `build.sh` and `build.bat` — compile helpers
- `run.bat` — Windows run helper

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
- Compiles sources under `src/main/java` to `bin/` with `--release 17`.
- Copies resources to `bin/` so classpath can resolve icons/config.
- Uses `lib/mysql-connector-j-8.0.33.jar` for JDBC driver.

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
- Railway MySQL (cloud-hosted)
- JDBC Driver: `lib/mysql-connector-j-8.0.33.jar`
- Configuration: `.env` or environment variables
- Connection: SSL-enabled; URL built from `.env` via `config/EnvironmentConfig` and `db/DatabaseHelper`

### Environment Configuration
- `.env` keys (examples):
  - `RAILWAY_MYSQL_URL` (preferred, e.g., `mysql://user:pass@host:port/db`)
  - or individual: `RAILWAY_MYSQL_HOST`, `RAILWAY_MYSQL_PORT`, `RAILWAY_MYSQL_DATABASE`, `RAILWAY_MYSQL_USERNAME`, `RAILWAY_MYSQL_PASSWORD`
- `EnvironmentConfig` parses these and `DatabaseHelper` constructs an SSL JDBC URL.

### Logging
- Console logs indicate table creation/migrations and connection success/fail.

### Packaging (optional)
Example:
```bash
jar --create --file forgegrid.jar -C bin .
java -cp "forgegrid.jar:lib/*" com.forgegrid.app.Main
```

### Common Issues
- Railway connection: verify service status and credentials
- Environment: `.env` exists with correct keys
- Classpath: ensure `lib/mysql-connector-j-8.0.33.jar` is present
- Java version: use JDK 17+
- SSL: connection string uses `useSSL=true`
- Performance: If experiencing UI lag, ensure you're using the latest build with performance optimizations
- Password field: Password always shows dots (`•`) by default for security, even with "Remember Me" enabled

### Development Tips
- Use an IDE with Java 17+ SDK.
- Mark `src/main/resources` as resources folder.
- Run `com.forgegrid.app.Main` directly from the IDE.
- The application now includes performance optimizations for smoother UI interactions.


