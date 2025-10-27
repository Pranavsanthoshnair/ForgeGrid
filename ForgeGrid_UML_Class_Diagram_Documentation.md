# ForgeGrid UML Class Diagram - Complete Breakdown

Here's exactly how your class diagram looks with all classes, variables, methods, and connections:

## 📦 Package Structure (7 Packages)

### 1. `com.forgegrid.app` Package
- **Main** (1 class)
  - `+main(String[] args)` - Entry point method

### 2. `com.forgegrid.ui` Package (9 classes)
- **AuthUI** - Main authentication interface
  - Variables: `emailField`, `passwordField`, `controller`
  - Methods: `AuthUI()` constructor

- **Dashboard** - Main application dashboard
  - Variables: `profile`, `controller`
  - Methods: `Dashboard(PlayerProfile profile)` constructor

- **WelcomeUI** - Welcome screen
  - Methods: `WelcomeUI()` constructor

- **OnboardingInAppPanel** - User onboarding interface
  - Variables: `controller`
  - Methods: `OnboardingInAppPanel(OnboardingController controller)` constructor

- **TaskPopupDialog** - Task completion dialog
  - Variables: `parent`, `profile`, `task`
  - Methods: `TaskPopupDialog(Dashboard parent, PlayerProfile profile, HardcodedTask task)` constructor

- **LoadingScreen** - Loading interface
  - Variables: `progressBar`, `statusLabel`
  - Methods: `LoadingScreen()` constructor

- **MotivationPanel** - Motivation display
  - Variables: `motivationText`
  - Methods: `MotivationPanel()` constructor

- **CardContainerPanel** - Card layout container
  - Variables: `cardLayout`, `cardPanel`
  - Methods: `CardContainerPanel()` constructor

- **Theme** - UI theme constants
  - Variables: `PRIMARY_COLOR`, `SECONDARY_COLOR` (public)
  - Methods: `getPrimaryColor(): Color`

### 3. `com.forgegrid.controller` Package (3 classes)
- **AuthController** - Authentication logic controller
  - Variables: `authService`, `userService`, `userPreferences`
  - Methods: `AuthController()` constructor, `login()`, `register()`

- **DashboardController** - Dashboard logic controller
  - Variables: `taskService`, `levelService`
  - Methods: `DashboardController()` constructor, `getLevelInfo()`, `getTaskHistory()`

- **OnboardingController** - Onboarding logic controller
  - Variables: `userService`
  - Methods: `OnboardingController()` constructor, `hasCompletedOnboarding()`

### 4. `com.forgegrid.service` Package (4 classes)
- **AuthService** - Authentication business logic
  - Variables: `dbHelper`
  - Methods: `AuthService()` constructor, `register()`, `login()`

- **UserService** - User management business logic
  - Variables: `dbHelper`
  - Methods: `UserService()` constructor, `hasCompletedOnboarding()`, `saveOnboardingData()`

- **LevelService** - XP/Level progression logic
  - Variables: `dbHelper`
  - Methods: `LevelService()` constructor, `getRequiredXPForLevel()`, `addXP()`

- **HardcodedTaskService** - Task management business logic
  - Variables: `dbHelper`
  - Methods: `HardcodedTaskService()` constructor, `getTasksForUser()`, `getTaskHistory()`

### 5. `com.forgegrid.model` Package (4 classes)
- **PlayerProfile** - User profile data model
  - Variables: `id`, `username`, `email`, `fullName`, `score`, `level`, `achievements`, `lastLogin`, `onboardingCompleted`, `onboardingGoal`, `onboardingLanguage`, `onboardingSkill`
  - Methods: `PlayerProfile()` constructors, `getId()`, `setId()`, `getUsername()`, `setUsername()`, `updateLastLogin()`, `updateScore()`

- **HardcodedTask** - Predefined task model
  - Variables: `taskName`, `description`, `language`, `level`, `xpReward`, `estimatedMinutes`
  - Methods: `HardcodedTask()` constructor, `getTaskName()`, `getDescription()`, `getLanguage()`, `getLevel()`, `getXpReward()`, `getEstimatedMinutes()`

- **GoatedTask** - User-created task model
  - Variables: `id`, `title`, `description`, `deadline`, `xp`, `isCompleted`, `createdAt`
  - Methods: `GoatedTask()` constructor, `getId()`, `getTitle()`, `getDescription()`, `getDeadline()`, `getXp()`, `isCompleted()`, `getCreatedAt()`

- **TaskHistoryEntry** - Task completion history model
  - Variables: `taskName`, `timeTaken`, `xpEarned`, `status`, `timestamp` (all public)
  - Methods: `TaskHistoryEntry()` constructor

### 6. `com.forgegrid.config` Package (2 classes)
- **EnvironmentConfig** - Environment variable management
  - Variables: `ENV_FILE`, `envVars`, `envLoaded`
  - Methods: `get()`, `getRailwayHost()`, `getRailwayUrl()`

- **UserPreferences** - Local user preferences
  - Variables: `PREFS_FILE_NAME`, `properties`, `prefsFile`
  - Methods: `UserPreferences()` constructor, `getLastUsername()`, `setLastUsername()`, `isRememberMeEnabled()`

### 7. `com.forgegrid.db` Package (1 class)
- **DatabaseHelper** - Database connection management
  - Variables: `dbUrl`, `instance`, `connection`, `dbHost`, `dbPort`, `dbName`, `dbUsername`, `dbPassword`
  - Methods: `DatabaseHelper()` constructor (private), `getInstance()`, `getConnection()`, `testConnection()`

## 🔗 Connection Relationships (27 arrows)

### UI Layer Connections:
1. `Main` → `AuthUI` (creates)
2. `AuthUI` → `AuthController` (uses)
3. `AuthUI` → `OnboardingController` (uses)
4. `AuthUI` → `PlayerProfile` (creates)
5. `AuthUI` → `WelcomeUI` (creates)
6. `AuthUI` → `OnboardingInAppPanel` (creates)
7. `AuthUI` → `LoadingScreen` (creates)
8. `Dashboard` → `DashboardController` (uses)
9. `Dashboard` → `PlayerProfile` (uses)
10. `Dashboard` → `TaskPopupDialog` (creates)
11. `Dashboard` → `HardcodedTask` (uses)
12. `Dashboard` → `TaskHistoryEntry` (uses)

### Controller Layer Connections:
13. `AuthController` → `AuthService` (uses)
14. `AuthController` → `UserService` (uses)
15. `AuthController` → `UserPreferences` (uses)
16. `DashboardController` → `HardcodedTaskService` (uses)
17. `DashboardController` → `LevelService` (uses)
18. `OnboardingController` → `UserService` (uses)

### Service Layer Connections:
19. `AuthService` → `DatabaseHelper` (uses)
20. `AuthService` → `PlayerProfile` (creates)
21. `UserService` → `DatabaseHelper` (uses)
22. `LevelService` → `DatabaseHelper` (uses)
23. `HardcodedTaskService` → `DatabaseHelper` (uses)
24. `HardcodedTaskService` → `HardcodedTask` (creates)
25. `HardcodedTaskService` → `GoatedTask` (creates)
26. `HardcodedTaskService` → `TaskHistoryEntry` (creates)

### Database Layer Connections:
27. `DatabaseHelper` → `EnvironmentConfig` (uses)

## 📊 Visual Layout
The diagram shows a **3-tier MVC architecture**:
- **Top**: UI Layer (Swing components)
- **Middle**: Controller Layer (business logic coordination)
- **Bottom**: Service Layer (business logic + data access)
- **Foundation**: Model Layer (data structures) + Config Layer (configuration) + DB Layer (database access)

The arrows show **dependency flow** from UI → Controllers → Services → Database, with models being created and used throughout the system.

## 🏗️ Architecture Summary

### Total Classes: 24
- **UI Classes**: 9 (Swing components)
- **Controller Classes**: 3 (MVC controllers)
- **Service Classes**: 4 (Business logic)
- **Model Classes**: 4 (Data structures)
- **Config Classes**: 2 (Configuration management)
- **DB Classes**: 1 (Database access)
- **App Classes**: 1 (Entry point)

### Key Design Patterns:
1. **MVC Pattern**: Clear separation between UI, Controllers, and Services
2. **Singleton Pattern**: DatabaseHelper uses singleton for connection management
3. **Dependency Injection**: Controllers receive services through constructors
4. **Repository Pattern**: Services act as repositories for data access
5. **Factory Pattern**: Services create model objects

### Data Flow:
1. **User Input** → UI Components
2. **UI Events** → Controllers
3. **Business Logic** → Services
4. **Data Access** → DatabaseHelper
5. **Data Storage** → MySQL Database
6. **Model Creation** → Services return model objects
7. **UI Updates** → Controllers update UI with model data

This architecture ensures clean separation of concerns, testability, and maintainability of the ForgeGrid application.
