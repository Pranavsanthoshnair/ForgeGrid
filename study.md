# ForgeGrid Study Guide

## 1) What is ForgeGrid?
ForgeGrid is a gamified Java desktop app (Swing/AWT) that turns coding practice into an RPG‑style journey. Users register/login, complete tasks, gain XP, level up, and can add personal “Goated Tasks.” Data persists in a Railway‑hosted MySQL database via JDBC.

Tech essentials:
- Java 17+
- Swing/AWT for UI
- JDBC (mysql-connector-j) for persistence
- No JavaFX/SWT; only Swing/AWT

Directory highlights:
- `src/main/java/com/forgegrid/app` → app entry
- `.../ui` → all Swing UI frames/panels and shared UI utilities
- `.../auth`, `.../service`, `.../db` → backend/auth/services and DB helpers
- `.../model` → simple POJOs
- `.../config` → env and local user prefs
- `src/main/resources` → icons/config
- `build.bat`, `build.sh`, `run.bat` → build/run scripts

---

## 2) High-level flow
1. App starts at `com.forgegrid.app.Main` → sets system Look & Feel and shows `AuthUI` on EDT.
2. `AuthUI` handles login/signup and onboarding prompt. After success, it opens `Dashboard` in the same window.
3. `Dashboard` renders the main app: navigation, player stats (level/XP), hardcoded tasks, “Goated Tasks,” history, and customization entry.
4. Services (`AuthService`, `UserService`, `LevelService`, `HardcodedTaskService`) talk to MySQL through `DatabaseHelper`.
5. Tables are auto-created/migrated at startup.

---

## 3) UI architecture (Swing)
- Entry: `AuthUI` extends `JFrame`
  - Card-based navigation (`CardLayout`) for: WELCOME, LOGIN, SIGNUP, ONBOARDING_PROMPT, LOADING.
  - Uses custom panels/components:
    - `WelcomeUI`, `LoadingScreen`, `OnboardingInAppPanel` (new/returning flows)
    - (Removed legacy custom background components)
    - `CardContainerPanel`: layout helper for consistent card look
    - `Theme`: brand colors (no gradients)
  - Login/Signup fields are basic Swing inputs (simple borders, placeholders handled inline).
  - After login, either:
    - Shows `ONBOARDING_PROMPT` → in-app onboarding (`OnboardingInAppPanel`) → saves onboarding to DB → opens `Dashboard` in-card; or
    - If already onboarded → shows welcome-back → `Dashboard`.

- Main app: `Dashboard` extends `JFrame`
  - Layout:
    - Sidebar (navigation): Home, Tasks (Assigned/Completed/Skipped/Goated), Profile, Settings, etc.
    - Center area (`CardLayout`) swaps views. XP bar and streak are displayed; level/XP pulled from `LevelService`.
  - Data-driven sections via services:
    - Hardcoded task list based on onboarding language/skill using `HardcodedTaskService#getTasksForUser`
    - Completion/skip log writes into `user_tasks`, and XP updates via `LevelService#addXP`
    - Goated Tasks: CRUD UI hooks that call `HardcodedTaskService` methods
  - Overlays removed; plain card switches (CardLayout).

- Shared UI utilities
- `Theme`: brand colors; gradients removed
 
- `TaskPopupDialog`, `LoadingScreen` (simplified), `CardContainerPanel`

Layouts used (selection):
- `BorderLayout`, `BoxLayout`, `CardLayout`, `FlowLayout`, and custom-painted panels.

---

## 4) Backend services and responsibilities
- `DatabaseHelper`
  - Loads Railway config from `.env` or system env via `EnvironmentConfig`.
  - Builds JDBC URL, opens connections, and initializes tables and indexes.
  - Tables created/migrated:
    - `users`: id, username, email, password (SHA‑256), total_xp, level, onboarding_* fields, created/updated timestamps.
    - `user_preferences`: per-username customization (experience_level, work_style, productivity_goals, notification_preference, customize_completed), timestamps.
    - `user_tasks`: assigned/completed/skipped + Goated fields (title/description/deadline/xp/is_completed), timestamps/indexes.

- `AuthService`
  - register(username, email, password): SHA-256 hash; insert into `users` if unique.
  - login(usernameOrEmail, password): verifies credentials; returns `PlayerProfile` with onboarding fields from `users`.
  - usernameExists / usernameOrEmailExists
  - resetPassword(username, newPassword)

- `UserService`
  - hasCompletedOnboarding / by username
  - saveOnboardingData / by username
  - getOnboardingData / by username
  - updateLastLogin
  - Preferences: save/get/exists (`user_preferences`), hasCompletedCustomization
  - getUserProfileDetails / updateUserProfileDetails
  - updateUserScore (legacy score field)

- `LevelService`
  - XP curve: XP(N) = 100 * 1.5^(N-1) for each level increment
  - calculateLevelFromXP, getCurrentLevelXP, getRequiredXPForLevel
  - addXP(username, delta): updates `users.total_xp` and `users.level` atomically; returns `LevelUpResult`
  - getLevelInfo(username): returns level, totalXP, currentLevelXP, requiredForNextLevel

- `HardcodedTaskService`
  - Initializes `user_tasks` and migrates Goated columns.
  - Provides hardcoded task catalogs by language+level (Beginner/Intermediate/Advanced).
  - Records assigned/completed/skipped tasks with timestamps and XP.
  - Net/total XP, counts, recorded names; auto-skip overdue assigned tasks with XP penalty.
  - Goated Tasks: create/list/update/delete; completion credits XP via `LevelService`.

---

## 5) Models
- `PlayerProfile`: runtime user profile (username, email, level, score, onboarding fields, timestamps).
- `HardcodedTask`: name, description, language, level, xpReward, estimatedMinutes.
- `GoatedTask`: id/title/description/deadline/xp/isCompleted/createdAt.
- `TaskHistoryEntry`: simple row projection for history lists.

---

## 6) Configuration and persistence
- `EnvironmentConfig`: loads `.env` and system env; supports `RAILWAY_MYSQL_URL` or component variables:
  - `RAILWAY_MYSQL_HOST`, `RAILWAY_MYSQL_PORT`, `RAILWAY_MYSQL_DATABASE`, `RAILWAY_MYSQL_USERNAME`, `RAILWAY_MYSQL_PASSWORD`
- `.env` (not in repo) can supply the above. Example keys:
  - RAILWAY_MYSQL_URL=mysql://user:pass@host:port/db
- `UserPreferences`: local per-device preferences file in user home (`forgegrid.prefs`), used for last username and “remember me”.

---

## 7) Database schema (created at runtime)
- `users`
  - id PK, username UNIQUE, email UNIQUE
  - password (SHA‑256 hex), total_xp INT DEFAULT 0, level INT DEFAULT 1
  - onboarding_completed TINYINT(1), onboarding_goal/lang/skill VARCHAR(255)
  - created_at/updated_at timestamps
  - Indexes on username, email, onboarding_completed

- `user_preferences`
  - id PK, username FK→users(username)
  - experience_level, work_style, productivity_goals, notification_preference
  - customize_completed BOOLEAN, created_at/updated_at
  - Index on username

- `user_tasks`
  - id PK, username, task_name, time_taken, xp_earned, status, completed_at
  - Goated: type('goated'|'regular'), title, description, deadline, xp, is_completed, created_at
  - Indexes on username, status, type

---

## 8) Build and run scripts
- Windows: `build.bat`
  - Recursively lists all Java files to `sources.txt`, compiles with Java 17 (`--release 17`) to `bin` with classpath `lib/*`.
  - Copies `src/main/resources/**` into `bin/` (ensures icons are present).
  - Run after successful build: `run.bat`.

- Windows: `run.bat`
  - Sets classpath: `bin;lib\*` and runs `com.forgegrid.app.Main`.

- Linux/macOS: `build.sh`
  - Compiles with `javac -cp "lib/*" -d bin --release 17` using explicit source globs for packages.
  - Copies `src/main/resources/*` into `bin/`.

Notes:
- Ensure `lib/mysql-connector-j-8.0.33.jar` exists.
- Provide `.env` or system env vars for Railway credentials before running.

---

## 9) End-to-end user journey
1. Launch app (`run.bat` or IDE). `Main` sets L&F and shows `AuthUI`.
2. Register/Login in `AuthUI`. Credentials are hashed with SHA‑256 and verified via `AuthService`/`users` table.
3. If new user or not onboarded: `OnboardingInAppPanel` collects goal/language/skill, and `UserService` saves to `users`.
4. `AuthUI` switches content to `Dashboard` (same frame). `Dashboard` loads:
   - Level/XP via `LevelService#getLevelInfo`
   - Tasks via `HardcodedTaskService#getTasksForUser`
   - Completed list via `HardcodedTaskService#getCompletedTasks`/history queries
5. Completing a task → `HardcodedTaskService#saveCompletedTask` and `LevelService#addXP`.
6. Skipping a task → `saveSkippedTask` with negative XP; `LevelService#addXP` reflects penalties (auto-skip handles overdue assigned tasks too).
7. Goated Tasks → CRUD via `HardcodedTaskService`, completion credits XP.
8. Customization/preferences → `UserService.saveUserPreferences` stored in `user_preferences`.

---

## 10) Notable design choices
- All UI is Swing/AWT; no JavaFX.
- DB schema auto-creation/migration is done at startup; safe on re-run.
- XP system is centralized in `LevelService` with a scalable curve.
- Local `UserPreferences` keeps UX conveniences device-specific; sensitive data stays in MySQL.

---

## 11) Quick file map
- Entry: `app/Main.java`
- Auth/UI: `ui/AuthUI.java`, `ui/WelcomeUI.java`, `ui/LoadingScreen.java`, `ui/OnboardingInAppPanel.java`
- Main UI: `ui/Dashboard.java`, shared: `ui/Theme.java`, `ui/CardContainerPanel.java`
- Services: `auth/AuthService.java`, `service/UserService.java`, `service/LevelService.java`, `service/HardcodedTaskService.java`
- DB: `db/DatabaseHelper.java`
- Models: `model/PlayerProfile.java`, `model/HardcodedTask.java`, `model/GoatedTask.java`, `model/TaskHistoryEntry.java`
- Config: `config/EnvironmentConfig.java`, `config/UserPreferences.java`
- Resources: `resources/com/forgegrid/icon/logo2_transparent.png`, `resources/config.properties`
- Scripts: `build.bat`, `build.sh`, `run.bat`

---

## 12) How to run locally
1. Install JDK 17+.
2. Place `mysql-connector-j-8.0.33.jar` in `lib/` (already in repo).
3. Provide Railway credentials via `.env` or environment variables.
4. On Windows: double-click `build.bat`, then `run.bat`.
   On macOS/Linux: `bash build.sh` then run from IDE or `java -cp "bin:lib/*" com.forgegrid.app.Main`.

That’s the complete picture of ForgeGrid as it stands: UI, services, database, scripts, and flows.

---

## 13) Deep Technical Breakdown

### 13.1 Authentication: hashing, queries, and control flow
- Where: `auth/AuthService.java`, UI in `ui/AuthUI.java`
- Hashing: Passwords are hashed using SHA‑256 before storage and verification.
  - Method: `AuthService#hashPassword(String)` → `MessageDigest.getInstance("SHA-256")`, hex-encodes hash bytes.
- Registration flow:
  1) UI validates fields (non-empty) → `AuthService.register(username, email, password)`.
  2) Guard: `usernameOrEmailExists(value)` ensures global uniqueness (username and email cannot collide cross-field).
  3) Hash password → `INSERT INTO users (username, email, password)`.
  4) DB constraints: UNIQUE on `username` and `email` enforce uniqueness at SQL layer too.
- Login flow:
  1) UI collects `usernameOrEmail`, `password` → hash entered password.
  2) Query:
     ```sql
     SELECT id, username, email, onboarding_completed, onboarding_goal,
            onboarding_language, onboarding_skill
     FROM users
     WHERE (username = ? OR email = ?) AND password = ?
     ```
  3) On success, build `PlayerProfile` and populate onboarding fields.
  4) UI decides next step: either onboarding prompt or straight to dashboard.
- Reset password: `UPDATE users SET password = ? WHERE username = ?` (hashed).

Security notes:
- Hash-only (no salt/pepper). For production hardening, add per-user salt and stronger KDF (bcrypt/Argon2/PBKDF2) and avoid Base64-storing plaintext for remember-me (see 13.7).

### 13.2 Database connection, initialization, and migrations
- Where: `db/DatabaseHelper.java`, `config/EnvironmentConfig.java`
- Configuration loading:
  - `EnvironmentConfig` merges `.env` and system env. Supports either a full `RAILWAY_MYSQL_URL` (converted to JDBC if `mysql://`) or individual parts (`RAILWAY_MYSQL_HOST`, `..._PORT`, `..._DATABASE`, `..._USERNAME`, `..._PASSWORD`).
- Connection:
  - Driver: `com.mysql.cj.jdbc.Driver` (loaded via `Class.forName`).
  - JDBC URL: built from env or converted from `mysql://...` to `jdbc:mysql://...?...` with `useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true`.
  - `getConnection()` lazily opens and reuses a single `Connection` instance when needed.
- Initialization:
  - On helper construction: open connection → `createUsersTable()` → `createUserPreferencesTable()` → `createIndexes()`.
  - Migrations are idempotent: `CREATE TABLE IF NOT EXISTS ...`; `ALTER TABLE` guarded by try/catch to ignore “already exists.”
  - Indexes created in try/catch blocks to be safe across repeated runs.

Tables auto-created:
- `users` with `total_xp`, `level`, and onboarding fields; created/updated timestamps with `ON UPDATE CURRENT_TIMESTAMP`.
- `user_preferences` with `customize_completed` and FK to `users(username)`.
- `user_tasks` (created in `HardcodedTaskService`) with status tracking and Goated columns; indexes on `username`, `status`, `type`.

### 13.3 Onboarding: prompt, capture, persistence, reuse
- Where: `ui/AuthUI.java`, `ui/OnboardingInAppPanel.java`, `service/UserService.java`
- New users after login:
  - If `hasCompletedOnboardingByUsername(username)` is false, `AuthUI` shows `ONBOARDING_PROMPT` → user chooses to proceed now or skip.
  - Proceed: `OnboardingInAppPanel` (new-user mode) asks Q1/Q2/Q3 via a `CardLayout` flow.
  - On completion: `UserService.saveOnboardingDataByUsername(username, goal, language, skill)` sets `onboarding_completed=1` and stores choices on `users`.
  - Skip: `saveOnboardingDataByUsername(username, "Skipped", "Not specified", "Not specified")` and move on.
- Returning users:
  - `OnboardingInAppPanel` in returning mode shows “Welcome back” and immediately continues; `UserService.getOnboardingDataByUsername(username)` can preload values.
- Dashboard task selection uses onboarding `language` and `skill` to load appropriate hardcoded tasks.

### 13.4 Tasks engine: assignment, completion, skipping, history
- Where: `service/HardcodedTaskService.java`, `ui/Dashboard.java`
- Hardcoded catalogs:
  - Methods: `getBeginnerTasks(lang)`, `getIntermediateTasks(lang)`, `getAdvancedTasks(lang)` return `List<HardcodedTask>` per language+level.
  - `getTasksForUser(language, level)` picks the correct list.
- Assignment tracking:
  - When a task is surfaced, `recordAssignedTask(username, taskName)` writes an `assigned` row (if not already present), storing assigned time in `completed_at` for aging checks.
- Completion:
  - `saveCompletedTask(username, taskName, timeTaken, xpEarned)` inserts a `completed` row with XP.
  - After save, `LevelService.addXP(username, xpEarned)` updates `users.total_xp` and `users.level`.
- Skipping:
  - `saveSkippedTask(username, taskName, timeTaken, xpLost)` inserts a `skipped` row with negative XP.
  - `LevelService.addXP(username, xpLost)` applies penalty to total XP.
- Auto-skip expired assigned tasks:
  - `autoSkipExpiredAssignedTasks(username, language, level)` converts `assigned` rows older than 24h to `skipped` with XP penalty of 50% of the task’s reward.
  - Penalty is credited through `LevelService.addXP` to keep UI in sync with net XP.
- History and stats:
  - `getCompletedTasks(username)` and `getTaskHistory(username, limit)` read back rows.
  - `getTotalXP`, `getNetXP`, `getCompletedTaskCount`, `getSkippedTaskCount` provide aggregates for UI.

### 13.5 Goated Tasks (custom user tasks)
- Where: `service/HardcodedTaskService.java`, `model/GoatedTask.java`, `ui/Dashboard.java`
- Schema additions in `user_tasks` (migrated if missing): `type`, `title`, `description`, `deadline`, `xp`, `is_completed`, `created_at`, index on `type`.
- Create: `createGoatedTask(username, title, description, deadline, xp)` → inserts a row with `type='goated'` and `status='assigned'`.
- List: `listGoatedTasks(username)` returns ordered list (unfinished first, then by deadline).
- Update/Delete: `updateGoatedTask(...)`, `deleteGoatedTask(...)` operate on the same table.
- Complete: `markGoatedTaskComplete(username, taskId)` sets `is_completed=1`, `status='completed'`, writes `xp_earned=xp`, timestamps completion, then calls `LevelService.addXP(username, xp)` to update level progression.

### 13.6 XP and Leveling mechanics
- Where: `service/LevelService.java`
- XP curve per level step: `getRequiredXPForLevel(N) = round(100 * 1.5^(N-2))` for N≥2.
- Given `total_xp`:
  - `calculateLevelFromXP(totalXP)` iteratively finds the highest level where total XP covers all prior steps.
  - `getCurrentLevelXP(totalXP)` subtracts XP for prior levels to show in-level progress.
- Adding XP:
  - `addXP(username, xpDelta)` reads `users.total_xp`, adds delta, recalculates level, and writes back `total_xp`, `level`, and `updated_at`.
  - Returns `LevelUpResult` (leveledUp flag, old/new levels, totals, progress) for UI.
- Reading level info: `getLevelInfo(username)` returns snapshot for UI (current progress and requirement for next level).

### 13.7 Preferences & Remember Me (local-only)
- Where: `config/UserPreferences.java`, used by `AuthUI`
- Stored file: `${user.home}/forgegrid.prefs` as Java `Properties`.
- Keys: `last.username`, `remember.me`, `saved.username`, `saved.password`.
- Saved password encoding: Base64 (for convenience only; not secure). If “Remember Me” is unchecked, credentials are cleared.
- Security recommendation: Prefer storing tokens/secrets encrypted or avoid password storage entirely.

### 13.8 Dashboard rendering and service integrations
- Where: `ui/Dashboard.java`
- On open:
  - Reads level/XP via `LevelService.getLevelInfo(username)` and sets `currentLevel`, `currentXP`, `maxXP`.
  - Loads hardcoded tasks based on onboarding `language/skill` via `getTasksForUser`.
  - Loads completed tasks via `getCompletedTasks` and history via `getTaskHistory`.
  - Renders views via `CardLayout` in the center panel; sidebar navigation drives card switching.
  - XP bar is a custom-painted `JPanel` that reads `currentXP/maxXP` and repaints on updates.
- Actions (complete/skip/customize) call into corresponding service methods; after writes, UI refreshes by re-querying services.

### 13.9 Build/Run and resources handling
- `build.bat` and `build.sh` compile with `--release 17` and classpath `lib/*`, output to `bin/`.
- Resource copy ensures `src/main/resources/**` ends up in `bin/`; images (e.g., `logo2_transparent.png`) are loaded via `getResource`.
- `run.bat` sets CP=`bin;lib\*` then starts `com.forgegrid.app.Main`.

### 13.10 Environment setup for Railway MySQL
- Provide either:
  - `RAILWAY_MYSQL_URL=mysql://user:pass@host:port/database` (recommended), or
  - `RAILWAY_MYSQL_HOST`, `RAILWAY_MYSQL_PORT`, `RAILWAY_MYSQL_DATABASE`, `RAILWAY_MYSQL_USERNAME`, `RAILWAY_MYSQL_PASSWORD`.
- `.env` can hold these values locally; `EnvironmentConfig` reads `.env` and system env and builds a JDBC URL.

---

## 14) Implementation Trace (file pointers)
- Entry/UI boot:
  - `app/Main.java` → `ui/AuthUI.java`
- Auth:
  - `auth/AuthService.java` (hashing, register/login queries)
  - `config/UserPreferences.java` (remember me)
- Onboarding:
  - `ui/AuthUI.java` (flows), `ui/OnboardingInAppPanel.java` (Q1/2/3 UI)
  - `service/UserService.java` (save/get onboarding)
- Dashboard:
  - `ui/Dashboard.java` (views, calls to controllers/services)
  - `ui/Theme.java`
- Tasks and XP:
  - `service/HardcodedTaskService.java` (assignment, complete/skip, goated, history)
  - `service/LevelService.java` (XP curve and updates)
- Database & Config:
  - `db/DatabaseHelper.java` (init/migrations/indices)
  - `config/EnvironmentConfig.java` (env/.env merge)
- Models:
  - `model/PlayerProfile.java`, `model/HardcodedTask.java`, `model/GoatedTask.java`, `model/TaskHistoryEntry.java`
- Scripts & Resources:
  - `build.bat`, `build.sh`, `run.bat`
  - `src/main/resources/com/forgegrid/icon/logo2_transparent.png`


### 13.11 Representative inline code references

Authentication (SHA-256 hashing):
```218:240:src/main/java/com/forgegrid/auth/AuthService.java
private String hashPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        
        // Convert byte array to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
        
    } catch (NoSuchAlgorithmException e) {
        System.err.println("SHA-256 algorithm not available: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
```

Authentication (login query with username/email OR match):
```102:108:src/main/java/com/forgegrid/auth/AuthService.java
String selectSQL = """
    SELECT id, username, email, onboarding_completed, onboarding_goal, 
           onboarding_language, onboarding_skill 
    FROM users 
    WHERE (username = ? OR email = ?) AND password = ?
    """;
```

Authentication (registration insert):
```56:66:src/main/java/com/forgegrid/auth/AuthService.java
String insertSQL = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        
try (Connection conn = dbHelper.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
    
    pstmt.setString(1, username.trim());
    pstmt.setString(2, email.trim());
    pstmt.setString(3, hashedPassword);
    
    int rowsAffected = pstmt.executeUpdate();
    return rowsAffected > 0;
```

Environment config (convert mysql:// URL to JDBC):
```178:225:src/main/java/com/forgegrid/config/EnvironmentConfig.java
private static String convertRailwayUrlToJdbc(String railwayUrl) {
    try {
        // Remove mysql:// prefix
        String url = railwayUrl.substring(8);
        
        // Find @ symbol to separate credentials from host
        int atIndex = url.indexOf('@');
        if (atIndex == -1) {
            throw new IllegalArgumentException("Invalid Railway URL format");
        }
        
        // Extract credentials (user:pass)
        String credentials = url.substring(0, atIndex);
        String hostAndDb = url.substring(atIndex + 1);
        
        // Find : in credentials to separate user and password
        int colonIndex = credentials.indexOf(':');
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Invalid Railway URL format - no password");
        }
        
        String username = credentials.substring(0, colonIndex);
        String password = credentials.substring(colonIndex + 1);
        
        // Find : in hostAndDb to separate host and port
        int portIndex = hostAndDb.indexOf(':');
        if (portIndex == -1) {
            throw new IllegalArgumentException("Invalid Railway URL format - no port");
        }
        
        String host = hostAndDb.substring(0, portIndex);
        String portAndDb = hostAndDb.substring(portIndex + 1);
        
        // Find / to separate port and database
        int slashIndex = portAndDb.indexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException("Invalid Railway URL format - no database");
        }
        
        String port = portAndDb.substring(0, slashIndex);
        String database = portAndDb.substring(slashIndex + 1);
        
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                           host, port, database);
    } catch (Exception e) {
        return railwayUrl;
    }
}
```

Database helper (build URL and connect):
```98:105:src/main/java/com/forgegrid/db/DatabaseHelper.java
private String buildRailwayMySQLUrl() {
    String fullUrl = EnvironmentConfig.getRailwayUrl();
    if (fullUrl != null && !fullUrl.isEmpty()) {
        return fullUrl;
    }
    return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                       dbHost, dbPort, dbName);
}
```
```113:118:src/main/java/com/forgegrid/db/DatabaseHelper.java
public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    return connection;
}
```

Database helper (users table DDL):
```151:168:src/main/java/com/forgegrid/db/DatabaseHelper.java
String createTableSQL = """
    CREATE TABLE IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        total_xp INT DEFAULT 0,
        level INT DEFAULT 1,
        onboarding_completed TINYINT(1) DEFAULT 0,
        onboarding_goal VARCHAR(255) NULL,
        onboarding_language VARCHAR(255) NULL,
        onboarding_skill VARCHAR(255) NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """;
```

Onboarding (save by username):
```108:117:src/main/java/com/forgegrid/service/UserService.java
public boolean saveOnboardingDataByUsername(String username, String goal, String language, String skill) {
    String updateSQL = """
        UPDATE users 
        SET onboarding_completed = 1,
            onboarding_goal = ?,
            onboarding_language = ?,
            onboarding_skill = ?,
            updated_at = ?
        WHERE username = ?
        """;
```
```119:131:src/main/java/com/forgegrid/service/UserService.java
try (Connection conn = dbHelper.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
    
    pstmt.setString(1, goal);
    pstmt.setString(2, language);
    pstmt.setString(3, skill);
    pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    pstmt.setString(5, username);
    
    int rowsAffected = pstmt.executeUpdate();
    
    if (rowsAffected > 0) {
        System.out.println("Onboarding data saved successfully for username: " + username);
        return true;
    }
```

Tasks (create user_tasks DDL):
```26:46:src/main/java/com/forgegrid/service/HardcodedTaskService.java
private void createUserTasksTable() {
    String createTableSQL = 
        "CREATE TABLE IF NOT EXISTS user_tasks (" +
        "id INT AUTO_INCREMENT PRIMARY KEY, " +
        "username VARCHAR(100) NOT NULL, " +
        "task_name VARCHAR(255) NOT NULL, " +
        "time_taken INT, " +
        "xp_earned INT, " +
        "status VARCHAR(50) DEFAULT 'assigned', " +
        "completed_at TIMESTAMP NULL, " +
        // Goated tasks support
        "type VARCHAR(20) DEFAULT 'regular', " +
        "title VARCHAR(255) NULL, " +
        "description TEXT NULL, " +
        "deadline DATETIME NULL, " +
        "xp INT NULL, " +
        "is_completed TINYINT(1) DEFAULT 0, " +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "INDEX idx_username (username), " +
        "INDEX idx_status (status)" +
        ")";
```

Tasks (auto-skip expired assigned tasks with penalty):
```599:627:src/main/java/com/forgegrid/service/HardcodedTaskService.java
public void autoSkipExpiredAssignedTasks(String username, String language, String level) {
    String selectExpired = "SELECT task_name FROM user_tasks WHERE username = ? AND status = 'assigned' AND completed_at < (NOW() - INTERVAL 24 HOUR)";
    String updateSQL = "UPDATE user_tasks SET status='skipped', xp_earned=?, time_taken=?, completed_at=? WHERE username=? AND task_name=? AND status='assigned'";
    try (Connection conn = dbHelper.getConnection();
         PreparedStatement sel = conn.prepareStatement(selectExpired);
         PreparedStatement upd = conn.prepareStatement(updateSQL)) {
        sel.setString(1, username);
        ResultSet rs = sel.executeQuery();
        while (rs.next()) {
            String taskName = rs.getString("task_name");
            int reward = getXpRewardForTaskName(taskName, language, level);
            int penalty = -(Math.max(1, reward / 2));
            upd.setInt(1, penalty);
            upd.setInt(2, 1440); // 24h in minutes
            upd.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            upd.setString(4, username);
            upd.setString(5, taskName);
            upd.executeUpdate();

            // Reflect penalty to user's total XP so UI progress matches net history
            try {
                new com.forgegrid.service.LevelService().addXP(username, penalty);
            } catch (Exception ignored) {}
        }
    } catch (SQLException e) {
        System.err.println("Error auto-skipping expired tasks: " + e.getMessage());
        e.printStackTrace();
    }
}
```

Goated task completion (credit XP):
```127:149:src/main/java/com/forgegrid/service/HardcodedTaskService.java
public boolean markGoatedTaskComplete(String username, int taskId) {
    String select = "SELECT xp, is_completed FROM user_tasks WHERE id = ? AND username = ? AND type = 'goated'";
    String update = "UPDATE user_tasks SET is_completed = 1, status = 'completed', xp_earned = COALESCE(xp, 0), completed_at = ? WHERE id = ? AND username = ?";
    try (Connection conn = dbHelper.getConnection();
         PreparedStatement sel = conn.prepareStatement(select);
         PreparedStatement upd = conn.prepareStatement(update)) {
        sel.setInt(1, taskId);
        sel.setString(2, username);
        ResultSet rs = sel.executeQuery();
        if (!rs.next()) return false;
        if (rs.getBoolean("is_completed")) return true;
        int xp = rs.getInt("xp");
        upd.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        upd.setInt(2, taskId);
        upd.setString(3, username);
        int ok = upd.executeUpdate();
        if (ok > 0) {
            try {
                new com.forgegrid.service.LevelService().addXP(username, xp);
            } catch (Exception ignored) {}
            return true;
        }
    } catch (SQLException e) {
        System.err.println("Error completing goated task: " + e.getMessage());
    }
    return false;
}
```

XP/Leveling (required XP per level step):
```28:32:src/main/java/com/forgegrid/service/LevelService.java
public static int getRequiredXPForLevel(int level) {
    if (level <= 1) return 0;
    // XP to go from level (N-1) to level N
    return (int) Math.round(100 * Math.pow(1.5, level - 2));
}
```

XP/Leveling (add XP and update level):
```87:116:src/main/java/com/forgegrid/service/LevelService.java
public LevelUpResult addXP(String username, int xpToAdd) {
    try (Connection conn = dbHelper.getConnection()) {
        // Get current XP and level
        String selectSQL = "SELECT total_xp, level FROM users WHERE username = ?";
        int currentTotalXP = 0;
        int currentLevel = 1;
        
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentTotalXP = rs.getInt("total_xp");
                currentLevel = rs.getInt("level");
            }
        }
        
        // Add new XP
        int newTotalXP = Math.max(0, currentTotalXP + xpToAdd);
        int newLevel = calculateLevelFromXP(newTotalXP);
        boolean leveledUp = newLevel > currentLevel;
        
        // Update database
        String updateSQL = "UPDATE users SET total_xp = ?, level = ?, updated_at = ? WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, newTotalXP);
            pstmt.setInt(2, newLevel);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, username);
            pstmt.executeUpdate();
        }
```

Dashboard (XP bar paint logic):
```236:266:src/main/java/com/forgegrid/ui/Dashboard.java
xpProgressBar = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Background
        g2.setColor(SIDEBAR_COLOR);
        g2.fillRoundRect(0, 0, width, height, 10, 10);
        
        // Progress
        if (maxXP > 0) {
            int progressWidth = (int) ((width * currentXP) / maxXP);
            g2.setColor(new Color(80, 200, 120));
            g2.fillRoundRect(0, 0, progressWidth, height, 10, 10);
        }
        
        // XP Text inside bar
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String xpText = "XP: " + currentXP + " / " + maxXP;
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(xpText)) / 2;
        int textY = (height + fm.getAscent()) / 2 - 2;
        g2.drawString(xpText, textX, textY);
        
        g2.dispose();
    }
};
```

Entry point (EDT and AuthUI):
```25:33:src/main/java/com/forgegrid/app/Main.java
// Run the application on the Event Dispatch Thread (EDT)
SwingUtilities.invokeLater(() -> {
    AuthUI authUI = new AuthUI();
    authUI.setVisible(true);
});
```

## 15) Component inventory (Swing) by file [post-simplification]
- **ui/WelcomeUI.java**: `JPanel`, `JLabel`, `JButton`, `FlowLayout`, `BoxLayout`, `BorderLayout`, `BasicButtonUI`.
- **ui/AuthUI.java**: `JFrame`, `JPanel`, `JLabel`, `JTextField`, `JPasswordField`, `JCheckBox`, `JButton`, `CardLayout`, `BoxLayout`, `FlowLayout`, `BorderLayout`, `BasicButtonUI`, `SwingWorker`, `JOptionPane`.
- **ui/OnboardingInAppPanel.java**: `JPanel`, `CardLayout`, `JLabel`, `JToggleButton`, `ButtonGroup`, `JButton`, `BoxLayout`, `FlowLayout`, `BorderLayout`, `GridBagLayout`, `BasicButtonUI`.
- **ui/Dashboard.java**: `JFrame`, `JPanel`, `JLabel`, `JButton`, `JList`, `JTable`, `JScrollPane`, `JProgressBar` (custom-painted/standard), layouts: `BorderLayout`, `FlowLayout`, `BoxLayout`, plus basic borders. All text uses black; backgrounds use default `UIManager` panel color.
- **ui/TaskPopupDialog.java**: `JDialog`, `JPanel`, `JLabel`, `JButton`, `JTextArea`, `JScrollPane`.
- **ui/CardContainerPanel.java**: `JPanel` with simple line border; basic white background.
- **ui/LoadingScreen.java**: `JPanel`, `JLabel`; simple white card.
- **ui/Theme.java**: Brand color constants.

## 16) Button click flows (what happens)
- **WelcomeUI → Start**: Triggers listener → `AuthUI` shows `LOGIN` card.
- **AuthUI → Login**: Placeholder normalize → disable → `AuthController.login` → on success, check onboarding via controller → show `LOADING` → either show onboarding prompt or open `Dashboard` (embedding its content pane in same frame).
- **AuthUI → Sign Up**: Validate → `AuthController.register` → on success, show success dialog and switch to `LOGIN`.
- **Onboarding Prompt (Yes)**: Build `OnboardingInAppPanel` and show it.
- **Onboarding Prompt (Skip)**: `AuthController.saveOnboardingData("Skipped", "Not specified", "Not specified")` → open dashboard.
- **OnboardingInAppPanel → Continue**: Store selections (goal, language, skill) → final Continue calls listener → `OnboardingController.saveOnboardingData(username, ...)` → `AuthUI` opens dashboard.
- **Dashboard actions**: Start/Done/Skip wire to services via controller: `recordAssignedTask`, `saveCompletedTask` + `LevelService.addXP(+xp)`, `saveSkippedTask` + `LevelService.addXP(-xp)`.

## 17) Railway MySQL integration and JDBC steps (where located)
- **Register driver & open connection**:
```125:126:src/main/java/com/forgegrid/db/DatabaseHelper.java
Class.forName("com.mysql.cj.jdbc.Driver");
connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
```
- **Central connection getter**:
```113:117:src/main/java/com/forgegrid/db/DatabaseHelper.java
public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    return connection;
}
```
- **Create statement / prepared statements**:
```47:53:src/main/java/com/forgegrid/service/HardcodedTaskService.java
try (Connection conn = dbHelper.getConnection();
     Statement stmt = conn.createStatement()) {
    stmt.execute(createTableSQL);
}
```
```109:116:src/main/java/com/forgegrid/auth/AuthService.java
try (Connection conn = dbHelper.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
    // set params ...
}
```
- **Execute query/update examples**:
```115:121:src/main/java/com/forgegrid/auth/AuthService.java
pstmt.setString(1, usernameOrEmail.trim());
pstmt.setString(2, usernameOrEmail.trim());
pstmt.setString(3, hashedPassword);
try (ResultSet rs = pstmt.executeQuery()) {
    if (rs.next()) { /* build profile */ }
}
```
- **Close connection**: try-with-resources auto-closes; optional explicit:
```262:270:src/main/java/com/forgegrid/db/DatabaseHelper.java
public void closeConnection() {
    try {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    } catch (SQLException e) { }
}
```

### Where Railway settings are configured
- `config/EnvironmentConfig.java`: Reads `.env`/env, can convert `RAILWAY_MYSQL_URL` to JDBC.
- `db/DatabaseHelper.java`: Builds JDBC URL, registers driver, initializes tables and indexes.

## 18) Query map (SQL locations)
- **auth/AuthService.java**: `INSERT users` (register), `SELECT users` (login), `UPDATE users` (reset password), existence checks (`COUNT(*)`).
- **service/UserService.java**: `SELECT/UPDATE users` for onboarding; `INSERT/UPSERT user_preferences`; reads preferences/profile details; existence checks.
- **service/HardcodedTaskService.java**: `CREATE TABLE user_tasks`, migrations (`ALTER`/`CREATE INDEX`), task writes (`INSERT completed|skipped|assigned`), reads (`SELECT history/aggregates`), `UPDATE` for auto-skip, CRUD for goated.
- **service/LevelService.java**: Reads XP/level (`SELECT`), writes updated totals (`UPDATE`).
- **db/DatabaseHelper.java**: `CREATE TABLE users`, `CREATE TABLE user_preferences`, index creation.

## 19) GUI file responsibilities (concise)
- **app/Main.java**: Starts UI on EDT.
- **ui/WelcomeUI.java**: Landing card; Start → login.
- **ui/AuthUI.java**: Manages cards: WELCOME, LOGIN, SIGNUP, ONBOARDING_PROMPT, LOADING; delegates to controllers/services; swaps in `Dashboard` content.
- **ui/OnboardingInAppPanel.java**: Q1/Q2/Q3 or Welcome Back; emits completion to caller.
- **ui/Dashboard.java**: Simplified main UI; sidebar, center cards, task actions, stats; basic Swing colors; text black.
- **ui/TaskPopupDialog.java**: Minimal dialog for task actions wired to services.
- **ui/CardContainerPanel.java**: Simple white bordered card.
- **ui/LoadingScreen.java**: Minimal loading view.
- **ui/Theme.java**: Brand colors (pink/yellow/blue/gold).

## 20) Tasks according to onboarding
- Onboarding fields stored on `users` table drive catalogs.
- Flow: `AuthUI` → (maybe) `OnboardingInAppPanel` → persist via `UserService` → `Dashboard` loads tasks using `HardcodedTaskService.getTasksForUser(language, level)` selecting beginner/intermediate/advanced lists; penalties for auto-skip use same catalog to compute XP.

### Code references
```194:214:src/main/java/com/forgegrid/service/HardcodedTaskService.java
public List<HardcodedTask> getTasksForUser(String language, String level) { /* ... */ }
```
```599:627:src/main/java/com/forgegrid/service/HardcodedTaskService.java
public void autoSkipExpiredAssignedTasks(String username, String language, String level) { /* ... */ }
```

## 21) End-to-end button→DB trace (Login → Dashboard)
1) `ui/AuthUI` Login button → `AuthController.login` → `auth/AuthService.login` (SELECT with username/email OR + hashed password).
2) On success, `UserService.hasCompletedOnboardingByUsername` check.
3) If new user → `OnboardingInAppPanel` → `UserService.saveOnboardingDataByUsername`.
4) `AuthUI` embeds `Dashboard` content; `Dashboard` uses `HardcodedTaskService.getTasksForUser` per onboarding.

## 22) Code walkthrough snippets (concise)

- AuthUI: Login button wiring
```299:301:src/main/java/com/forgegrid/ui/AuthUI.java
loginButton.addActionListener(e -> handleLogin());
```

- AuthUI: Embed Dashboard content
```166:173:src/main/java/com/forgegrid/ui/AuthUI.java
Container dashboardContent = dashboard.getContentPane();
setContentPane(dashboardContent);
setTitle("ForgeGrid");
```

- AuthUI: Onboarding prompt Skip → save + open dashboard
```1269:1277:src/main/java/com/forgegrid/ui/AuthUI.java
skipBtn.addActionListener(e -> {
    if (currentProfile != null) {
        controller.saveOnboardingData(currentProfile.getUsername(), "Skipped", "Not specified", "Not specified");
    }
});
```

- WelcomeUI: Start button hook
```115:117:src/main/java/com/forgegrid/ui/WelcomeUI.java
public void addStartActionListener(ActionListener l) { startButton.addActionListener(l); }
```

- DatabaseHelper: Driver registration
```125:126:src/main/java/com/forgegrid/db/DatabaseHelper.java
Class.forName("com.mysql.cj.jdbc.Driver");
connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
```

- AuthService: Login WHERE clause
```102:107:src/main/java/com/forgegrid/auth/AuthService.java
WHERE (username = ? OR email = ?) AND password = ?
```

- UserService: Save onboarding by username
```108:117:src/main/java/com/forgegrid/service/UserService.java
UPDATE users SET onboarding_completed = 1, onboarding_goal = ?,
onboarding_language = ?, onboarding_skill = ?, updated_at = ? WHERE username = ?
```

- HardcodedTaskService: Select tasks based on onboarding
```198:205:src/main/java/com/forgegrid/service/HardcodedTaskService.java
if (lvl.contains("beginner")) tasks = getBeginnerTasks(lang);
else if (lvl.contains("intermediate")) tasks = getIntermediateTasks(lang);
else if (lvl.contains("advanced") || lvl.contains("expert")) tasks = getAdvancedTasks(lang);
```

- HardcodedTaskService: Auto-skip penalty outline
```611:616:src/main/java/com/forgegrid/service/HardcodedTaskService.java
int reward = getXpRewardForTaskName(taskName, language, level);
int penalty = -(Math.max(1, reward / 2));
upd.setInt(1, penalty);
```

- LevelService: Update XP/level
```109:116:src/main/java/com/forgegrid/service/LevelService.java
String updateSQL = "UPDATE users SET total_xp = ?, level = ?, updated_at = ? WHERE username = ?";
pstmt.executeUpdate();
```

- Dashboard: Start Next Task button wiring
```1051:1059:src/main/java/com/forgegrid/ui/Dashboard.java
JButton startTaskBtn = new JButton("Start Next Task");
startTaskBtn.setBackground(Theme.BRAND_PINK);
startTaskBtn.addActionListener(e -> showTaskPopup());
```

## 23) Controllers (aligned with simplified GUI)

- Purpose: Move logic out of UI (Swing) into a thin controller layer that delegates to services and preferences. The facelift kept controllers unchanged; only UI styling/layout was simplified.

- AuthController (`controller/AuthController.java`)
  - Responsibilities: registration, login, password reset, onboarding checks/saves, remember‑me.
  - Used by: `ui/AuthUI.java` for all auth/onboarding actions.
  - Key APIs:
```20:26:src/main/java/com/forgegrid/controller/AuthController.java
public PlayerProfile login(String u, String p) { return authService.login(u, p); }
public boolean register(String n, String e, String p) { return authService.register(n, e, p); }
public boolean hasCompletedOnboarding(String u) { return userService.hasCompletedOnboardingByUsername(u); }
public boolean saveOnboardingData(String u, String g, String l, String s) { return userService.saveOnboardingDataByUsername(u, g, l, s); }
```

- OnboardingController (`controller/OnboardingController.java`)
  - Responsibilities: read/write onboarding fields by username.
  - Used by: `ui/AuthUI.java`, `ui/OnboardingInAppPanel.java`.
  - Key APIs:
```17:23:src/main/java/com/forgegrid/controller/OnboardingController.java
public String[] getOnboardingData(String username) { return userService.getOnboardingDataByUsername(username); }
public boolean saveOnboardingData(String u, String g, String l, String s) { return userService.saveOnboardingDataByUsername(u, g, l, s); }
```

- DashboardController (`controller/DashboardController.java`)
  - Responsibilities: glue for tasks and levels; fetch lists/stats, record assigned/completed/skipped, Goated CRUD.
  - Used by: `ui/Dashboard.java`, `ui/TaskPopupDialog.java`.
  - Key APIs:
```21:33:src/main/java/com/forgegrid/controller/DashboardController.java
public LevelService.LevelInfo getLevelInfo(String username)
public List<TaskHistoryEntry> getTaskHistory(String username, int limit)
public List<HardcodedTask> getTasksFor(String language, String level)
public void recordAssignedTask(String username, String taskName)
public boolean saveCompletedTask(String username, String taskName, int timeTaken, int xpEarned)
```

- Facelift alignment notes:
  - UI files only layout Swing and wire listeners; they no longer reach directly into services or DB.
  - Controllers encapsulate all business operations; the simplified visuals (black text, default backgrounds, plain buttons) do not affect controller contracts.
  - Dashboard actions (Start/Done/Skip/Goated) invoke controller methods; tables/labels are populated from controller data.

## 24) Scenario flows and expected behavior (test cases)

- Invalid login (wrong username/password)
  - Flow: AuthUI → controller.login returns null → show error dialog → stay on LOGIN.
  - Expected: No DB writes; no controller state changes; fields remain.
  - Snippet:
```736:738:src/main/java/com/forgegrid/ui/AuthUI.java
JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
showCard("LOGIN");
```

- Login error (DB/connection issue)
  - Flow: Exception thrown in login path → show error dialog → remain on LOGIN.
  - Snippet:
```742:744:src/main/java/com/forgegrid/ui/AuthUI.java
JOptionPane.showMessageDialog(this, "Authentication error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
showCard("LOGIN");
```

- Valid login + onboarding already completed
  - Flow: controller.login → profile; `hasCompletedOnboarding=true` → show LOADING → show welcome back → embed Dashboard.
  - Snippet:
```727:733:src/main/java/com/forgegrid/ui/AuthUI.java
boolean hasCompletedOnboarding = controller.hasCompletedOnboarding(profile.getUsername());
if (hasCompletedOnboarding) { createWelcomeBackOnboarding(profile.getUsername()); }
else { showCard("ONBOARDING_PROMPT"); }
```

- Valid login + new user (not onboarded)
  - Flow: controller.login → profile; `hasCompletedOnboarding=false` → show `ONBOARDING_PROMPT`.
  - Expected: No Dashboard until onboarding is saved or skipped.

- Onboarding: choose Yes (complete onboarding)
  - Flow: Build `OnboardingInAppPanel` → after Q1/Q2/Q3 → `OnboardingController.saveOnboardingData` → open Dashboard.

- Onboarding: Skip
  - Flow: Save “Skipped/Not specified/Not specified” for user → open Dashboard.
  - Snippet:
```1269:1277:src/main/java/com/forgegrid/ui/AuthUI.java
controller.saveOnboardingData(currentProfile.getUsername(), "Skipped", "Not specified", "Not specified");
openDashboardInCard(goal, language, skill);
```

- Signup validation: empty fields
  - Flow: Any field empty/placeholder → error dialog → remain on SIGNUP.
  - Snippet:
```807:810:src/main/java/com/forgegrid/ui/AuthUI.java
JOptionPane.showMessageDialog(this, "Please fill in all fields: " + sb.toString() + ".", "Error", JOptionPane.ERROR_MESSAGE);
```

- Signup validation: username equals email
  - Flow: Show error dialog; no registration attempt.
  - Snippet:
```812:818:src/main/java/com/forgegrid/ui/AuthUI.java
"Username and Email cannot be the same.\nPlease use different values."
```

- Signup success
  - Flow: controller.register returns true → success dialog → switch to LOGIN; placeholders reset.
  - Snippet:
```832:839:src/main/java/com/forgegrid/ui/AuthUI.java
JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
showLogin();
```

- Dashboard: Start Next Task
  - Flow: Click button → controller.recordAssignedTask(username, taskName) → open task popup.
  - Snippet:
```1051:1059:src/main/java/com/forgegrid/ui/Dashboard.java
JButton startTaskBtn = new JButton("Start Next Task");
startTaskBtn.addActionListener(e -> showTaskPopup());
```

- Task completion
  - Flow: Submit in dialog → controller.saveCompletedTask(...) → LevelService.addXP(+xp) internally via service → UI refresh.

- Task skip
  - Flow: Skip in dialog → controller.saveSkippedTask(...) (via service) → LevelService.addXP(-xp) → UI refresh.

- Auto-skip expired assigned tasks
  - Flow: On dashboard refresh/tick → controller.autoSkipExpired(username, language, level) → converts to skipped and applies penalty.
  - Snippet:
```599:616:src/main/java/com/forgegrid/service/HardcodedTaskService.java
int reward = getXpRewardForTaskName(taskName, language, level);
int penalty = -(Math.max(1, reward / 2));
```

## 25) HardcodedTaskService – core snippets

- Select tasks by onboarding (language + level)
```198:205:src/main/java/com/forgegrid/service/HardcodedTaskService.java
if (lvl.contains("beginner")) tasks = getBeginnerTasks(lang);
else if (lvl.contains("intermediate")) tasks = getIntermediateTasks(lang);
else if (lvl.contains("advanced") || lvl.contains("expert")) tasks = getAdvancedTasks(lang);
```

- Record a task as assigned (first time only)
```2391:2396:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String existsSQL = "SELECT 1 FROM user_tasks WHERE username = ? AND task_name = ? LIMIT 1";
String insertSQL = "INSERT INTO user_tasks (username, task_name, time_taken, xp_earned, status, completed_at) VALUES (?, ?, NULL, 0, 'assigned', ?)";
```

- Save completed task
```2275:2281:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String insertSQL =
    "INSERT INTO user_tasks (username, task_name, time_taken, xp_earned, status, completed_at) " +
    "VALUES (?, ?, ?, ?, 'completed', ?)";
```

- Save skipped task (negative XP)
```2503:2511:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String insertSQL =
    "INSERT INTO user_tasks (username, task_name, time_taken, xp_earned, status, completed_at) " +
    "VALUES (?, ?, ?, ?, 'skipped', ?)";
```

- Auto‑skip expired assigned tasks (24h) with 50% penalty
```2417:2421:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String selectExpired = "SELECT task_name FROM user_tasks WHERE username = ? AND status = 'assigned' AND completed_at < (NOW() - INTERVAL 24 HOUR)";
String updateSQL = "UPDATE user_tasks SET status='skipped', xp_earned=?, time_taken=?, completed_at=? WHERE username=? AND task_name=? AND status='assigned'";
```
```2427:2431:src/main/java/com/forgegrid/service/HardcodedTaskService.java
int reward = getXpRewardForTaskName(taskName, language, level);
int penalty = -(Math.max(1, reward / 2));
upd.setInt(1, penalty);
```

- History and aggregates
```2530:2537:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String selectSQL =
    "SELECT task_name, time_taken, xp_earned, status, completed_at " +
    "FROM user_tasks WHERE username = ? ORDER BY completed_at DESC LIMIT ?";
```
```2351:2356:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String selectSQL = "SELECT COALESCE(SUM(xp_earned), 0) as total FROM user_tasks WHERE username = ?";
```

- Goated tasks – create example
```78:86:src/main/java/com/forgegrid/service/HardcodedTaskService.java
String sql = "INSERT INTO user_tasks (username, task_name, title, description, deadline, xp, status, type, is_completed, created_at) VALUES (?, ?, ?, ?, ?, ?, 'assigned', 'goated', 0, ?)";
```

### XP updates (where addXP is invoked)
- After completing a regular task (UI applies XP)
```254:255:src/main/java/com/forgegrid/ui/TaskPopupDialog.java
com.forgegrid.service.LevelService.LevelUpResult result = new com.forgegrid.service.LevelService().addXP(profile.getUsername(), task.getXpReward());
```

- After skipping a task (UI applies XP penalty)
```347:347:src/main/java/com/forgegrid/ui/TaskPopupDialog.java
new com.forgegrid.service.LevelService().addXP(profile.getUsername(), xpPenalty);
```

- Auto‑skip expired (service applies penalty)
```2438:2438:src/main/java/com/forgegrid/service/HardcodedTaskService.java
new com.forgegrid.service.LevelService().addXP(username, penalty);
```

- Goated task completion (service credits XP)
```1445:145:src/main/java/com/forgegrid/service/HardcodedTaskService.java
new com.forgegrid.service.LevelService().addXP(username, xp);
```

---

## 26) Settings UI improvements
- **Problem**: Settings section had dark backgrounds making black text unreadable
- **Solution**: Removed dark backgrounds from setting cards and account panels, kept black text for readability
- **Enhancement**: Added rose gradient backgrounds to profile stat cards with white text for contrast
- **Layout**: Improved profile section with horizontal layout using `BorderLayout`
- **Result**: Clean, readable settings interface with consistent pink theme

---

## 27) Home Page Card Styling Updates
- **Problem**: Home page stat cards had pink gradient backgrounds that didn't match settings page style
- **Solution**: Changed card backgrounds from pink gradient to `PANEL_COLOR` (settings-style gray background) with pink borders
- **Enhancement**: Updated Profile view layout to center Account Information panel using horizontal `BorderLayout`
- **Result**: Consistent styling across home page and settings with improved visual balance


