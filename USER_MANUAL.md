## ForgeGrid User Manual

### Overview
ForgeGrid is a minimal Swing desktop app. You authenticate, complete a quick onboarding (first‑time users), and use a simple dashboard to start, complete, or skip tasks. All data persists to a Railway MySQL database.

### Launching the App
- Windows: double‑click `run.bat` (or run it from a terminal)
- macOS/Linux: run `./build.sh` once, then start from your IDE or `java` (see technical guide)

### Authentication
1. Open ForgeGrid. The title bar shows “ForgeGrid”.
2. Welcome screen → click “Start” to go to Login.
3. Login
   - Enter username/email and password, then press Enter or click “Login”.
   - “Forgot Password?” lets you reset using your username.
4. Sign Up
   - Click “New User? Sign Up”.
   - Provide Username, Email, and Password, then click “Sign Up”.

Tips
- Last successful username can be remembered (optional).
- Placeholders clear on first typing.

### Onboarding (first‑time users)
After first login, answer 3 quick questions:
- Primary goal
- Preferred language (Java, Python, C, JavaScript)
- Skill level (Beginner → Expert)

On completion, choices are saved and applied to tasks. Returning users who already onboarded go straight to the Dashboard.

### Dashboard (simplified)
- Sidebar on the left to switch views.
- Center area shows the current view (tasks, history, profile, etc.).
- Common actions:
  - Start Next Task
  - Complete Task (submits and credits XP)
  - Skip Task (applies XP penalty)
  - Manage Goated Tasks (custom tasks)

How to Navigate
- Click items in the sidebar to change the center view.
- Resize the window as needed; layout remains readable.

### Loading Screen
- A short loading screen appears during transitions.

### Connectivity
- If offline, DB operations may fail; try again after reconnecting.

### Troubleshooting
- Incorrect credentials → retry login.
- Password reset → use “Forgot Password?” on login.
- Window too small → resize.


