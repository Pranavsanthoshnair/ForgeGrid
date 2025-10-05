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
- `lib/` — third‑party jars (SQLite JDBC, SLF4J)
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
- Ensures the required `lib/*.jar` (SQLite JDBC, SLF4J) are available.

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
- Embedded SQLite file: `forgegrid.db` at project root.
- JDBC driver provided via `lib/sqlite-jdbc-3.44.1.0.jar`.
- Configuration defaults are in `src/main/resources/config.properties` and `bin/config.properties` after build.

Backups
- To back up user data, copy `forgegrid.db` while the app is closed.

### Logging
- SLF4J Simple is included: `lib/slf4j-api-1.7.36.jar` and `lib/slf4j-simple-1.7.36.jar`.
- Configure logging via system properties if needed, e.g.:
```bash
java -Dorg.slf4j.simpleLogger.defaultLogLevel=info -cp "bin:lib/*" com.forgegrid.app.Main
```

### Common Issues
- Classpath errors: ensure the OS‑specific separator (`;` on Windows, `:` on macOS/Linux).
- Java version: use JDK 17+; older JDKs may fail to compile.
- Locked DB: if `forgegrid.db` is locked, close running instances before retrying.

### Packaging (optional)
If you want a runnable fat‑jar (manual example):
```bash
jar --create --file forgegrid.jar -C bin .
# run with external libs on classpath
java -cp "forgegrid.jar:lib/*" com.forgegrid.app.Main
```

### Development Tips
- Use an IDE (IntelliJ/Eclipse/VS Code) and set the project SDK to Java 17+.
- Mark `src/main/resources` as resources folder to include icons/config on classpath.
- Run `com.forgegrid.app.Main` directly from the IDE.


