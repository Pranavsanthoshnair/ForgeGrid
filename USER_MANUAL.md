## ForgeGrid User Manual

### Overview
ForgeGrid is a minimalist, fast desktop app built with Swing to help you practice and track programming tasks. You authenticate, complete a lightweight onboarding (first‑time only), then use the Dashboard to start, complete, or skip tasks. All data persists to a Railway‑hosted MySQL database.

### System Requirements
- Windows 10 or later (recommended). macOS/Linux supported when running from source.
- Java 17+ runtime.
- Internet connectivity for login, tasks, and syncing.

### Launching ForgeGrid
- Windows: double‑click `run.bat` from the app folder (or run it in a terminal).
- macOS/Linux (from source): run `./build.sh` once, then start from your IDE or with `java -jar ForgeGrid.jar` (see `TECHNICAL_SETUP.md`).

---

### Welcome Screen
Purpose: A simple entry point to reach authentication.

Steps
1) Open the app; the title bar shows "ForgeGrid".
2) Click "Start" to proceed to the Authentication screen.
<img width="753" height="597" alt="image" src="https://github.com/user-attachments/assets/e406096d-b97d-4a25-8871-fe83a6231ff7" />
Tips
- You can resize the window; layout remains readable.
- If the app shows a brief loading state during transitions, please wait.

---

### Authentication (Login & Sign Up)
Use this screen to log in to your existing account or create a new one.

Login
1) Enter your Username (or Email) and Password.
2) Press Enter or click "Login".
3) If successful, first‑time users are taken to Onboarding; returning users go straight to the Dashboard.
<img width="991" height="940" alt="image" src="https://github.com/user-attachments/assets/498593a5-6f03-4d02-acde-b735d5867649" />


Sign Up
1) Click "New User? Sign Up".
2) Provide Username, Email, and Password.
3) Click "Sign Up" to create your account, then log in.
<img width="961" height="911" alt="image" src="https://github.com/user-attachments/assets/bb3d1b2c-ba18-4171-83d4-c4cc86d4599d" />


Convenience & Security
- Password field shows dots (•) by default. Click "Show" to reveal if needed.
- "Remember Me" can store the last successful username locally.
- "Forgot Password?" allows you to reset using your username or email.

Troubleshooting
- Invalid credentials: retry; check Caps Lock.
- Forgot password: use "Forgot Password?" and follow the prompt.
- No internet: reconnect and try again.

---

### Loading Screen
You may briefly see a loading screen during app startup or when switching major views (e.g., after login, before Dashboard). This is normal and prevents UI freezes while data is fetched.
<img width="836" height="444" alt="image" src="https://github.com/user-attachments/assets/c702406c-800a-4980-84d2-eae744d2bfd6" />


---

### Onboarding (First‑Time Only)
Purpose: Tailor the experience to your goals and skill level.

What you’ll answer (3 quick questions)
1) Your primary goal.
2) Preferred programming language (Java, Python, C, JavaScript).
3) Skill level (Beginner → Expert).

Steps
1) After your first successful login, the onboarding panel opens automatically.
2) Answer each question and proceed to the next.
3) Click "Finish" to save your preferences.

Result
- Preferences are saved and used to adapt tasks and recommendations.
- Returning users who have completed onboarding are sent directly to the Dashboard.
<img width="780" height="300" alt="image" src="https://github.com/user-attachments/assets/b2392498-5c51-44c3-8f6b-2d1734937da3" />
<img width="936" height="419" alt="image" src="https://github.com/user-attachments/assets/41695807-a8c2-4b77-b0ea-9407df21eac5" />
<img width="948" height="678" alt="image" src="https://github.com/user-attachments/assets/370940f2-adba-4b74-a5df-6bc6e359d2bc" />

---

### Dashboard
Your home screen for all task and profile actions. The layout typically includes:
- Sidebar (left): navigate between different views.
- Main content (center): shows the currently selected view (Tasks, History, Profile, etc.).

Core Actions
- Start Next Task: Fetches the next recommended task and opens it.
- Complete Task: Submits the current task, credits XP, and updates your history.
- Skip Task: Skips the current task with a small XP penalty.
- Manage Goated Tasks: Create and manage your own custom tasks.

Profile & Settings
- Update email, preferred programming language, skill level, and preferred time.
- Logout: Logs out and clears any Remember‑Me context.

Real‑Time Feedback
- XP, progress, and history update immediately after completing/skipping tasks.
- Motivational content and quick stats can appear in the main panel for context.

Navigation
- Click items in the sidebar to switch views.
- The window is resizable; the layout adapts to keep information readable.

Goated Tasks (Custom Tasks)
- Create custom tasks with a deadline in `HH.MM.SS` format (hours.minutes.seconds).
- Use the Task Popup for a streamlined create/edit experience.

Task Popup Dialog
- Focused interface to start, submit, or skip a task.
- Redundant buttons are removed for clarity; common actions are prominent.

Connectivity Notes
- If you go offline, some operations (fetching tasks, saving progress) may fail. Reconnect and retry.

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/39c030bb-a954-45a7-b3f5-b02a6e3b3f63" />
<img width="1519" height="1047" alt="image" src="https://github.com/user-attachments/assets/110737d5-e5f2-4cc6-9eba-24dd55eb2f9a" />
<img width="722" height="511" alt="image" src="https://github.com/user-attachments/assets/54661295-848a-4385-a4f1-20c59b97f336" />
<img width="909" height="673" alt="image" src="https://github.com/user-attachments/assets/a8e9f3ab-ac73-4197-9bc3-ba8a2b0c1646" />
<img width="1368" height="969" alt="image" src="https://github.com/user-attachments/assets/8af99fcc-ef4c-4197-81d4-43f905ce7eaa" />
<img width="1080" height="948" alt="image" src="https://github.com/user-attachments/assets/02d76220-6ab5-4a46-9a19-e0890fb92f9a" />
<img width="1456" height="1012" alt="image" src="https://github.com/user-attachments/assets/43eebc2e-5c36-42ee-94ac-7fda893ac2b1" />






---

### Shortcuts & Tips
- Press Enter on forms to submit quickly.
- Use the "Show" button on password fields only when needed, then hide it again.
- Keep the app updated to benefit from UI and performance improvements.

---

### Troubleshooting
- Can’t log in: verify credentials; try password reset; check network.
- Stuck on loading: wait a few seconds; if persistent, restart the app.
- UI looks cramped: resize the window to a larger size.
- Changes not saving: check internet connectivity and try again.
- Deadline format errors: for Goated Tasks, use `HH.MM.SS` (e.g., `02.15.00`).

### FAQs
- Do I have to redo onboarding? No. Once completed, you go straight to the Dashboard on future logins. You can adjust preferences in Profile.
- Where is my data stored? In a Railway‑hosted MySQL database connected by the app.
- Is my password visible? It’s masked by default. Use the "Show" toggle temporarily if needed.

---

### Support
If issues persist after following this guide, capture a screenshot and your steps, then contact the maintainer with your app version and a brief description of the problem.
