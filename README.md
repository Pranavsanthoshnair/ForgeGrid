# ForgeGrid

ForgeGrid is a minimal Java Swing desktop app that turns coding practice into a simple, XP‑based journey. The UI is intentionally basic (no gradients/animations), and responsibilities are cleanly separated using MVC: Views (Swing), Controllers (app logic), and Services (database operations).

## Overview
- UI: basic Swing screens using `JFrame`/`JPanel` with `CardLayout` for screen switching
- Controllers: thin layer invoked by UI; no DB logic in views
- Services: JDBC to Railway MySQL for auth, onboarding, tasks, and leveling

## Main Features
- Authentication (login/signup/reset)
- One‑time onboarding (goal, language, skill) → drives task sets
- Dashboard with simple task actions: Start Next Task, Complete, Skip
- Goated Tasks: create/list/update/delete custom tasks
- XP and Level progression

## Tech Stack
- Java 17+
- Swing (JFrame, JPanel, JLabel, JTextField, JPasswordField, JButton, JTable, JScrollPane)
- Railway MySQL via JDBC (MySQL Connector/J)

## Architecture
- Views: `ui/AuthUI`, `ui/WelcomeUI`, `ui/OnboardingInAppPanel`, `ui/Dashboard`, `ui/TaskPopupDialog`
- Controllers: `controller/AuthController`, `controller/OnboardingController`, `controller/DashboardController`
- Services: `auth/AuthService`, `service/UserService`, `service/LevelService`, `service/HardcodedTaskService`
- DB Helper: `db/DatabaseHelper`
- Models: `model/PlayerProfile`, `model/HardcodedTask`, `model/GoatedTask`, `model/TaskHistoryEntry`

## Build & Run
1) Ensure Java 17+ is installed
2) Provide Railway MySQL credentials via `.env` (see Technical Setup)
3) Build: `build.bat` (Windows) or `build.sh` (macOS/Linux)
4) Run: `run.bat` (Windows) or run `com.forgegrid.app.Main`

## How screens switch
- A single `JFrame` hosts a `CardLayout`; buttons switch cards like `LOGIN`, `SIGNUP`, `ONBOARDING_PROMPT`, `ONBOARDING`.
- After login/onboarding, the Dashboard’s content pane is embedded into the same frame (no new window).

## Notes
- UI is kept intentionally minimal for clarity and maintainability.
- All business logic lives in controllers/services; views only wire listeners and lay out components.

## License
ForgeGrid is open‑source and free for educational purposes.
