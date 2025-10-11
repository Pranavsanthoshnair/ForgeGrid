# ForgeGrid 🔥

**ForgeGrid** is a **gamified Java desktop application** that transforms coding practice into an RPG-style, task-based leveling journey for learners. Users complete challenges, earn XP, level up, and track their skill progression — all while staying engaged with daily tasks.

---

## Project Overview

ForgeGrid is built with **Java Swing** and follows a modular architecture to provide a smooth user experience. The app assigns coding tasks based on the player’s current level and enforces **timed challenges**. Completing tasks rewards XP, while skipping or missing deadlines may reduce XP. Users can also create custom challenges (“Goated Tasks”) with self-set deadlines.

ForgeGrid combines **progression, gamification, and engagement** into a single platform that encourages daily learning.

---

## Key Features

### Player System
- Tracks: **Name, Level, XP, Rank, Streak**
- Automatically updates stats as tasks are completed

### Task System
- **Assign Tasks**: Auto-assigned based on level
- **Submit / Skip Tasks**: Track progress and XP changes
- **Custom Tasks**: Add personal “Goated Tasks” with deadlines

### Timed Challenges
- Each task has a **deadline**
- Early completion unlocks the next challenge immediately

### XP & Leveling
- Gain XP for completed tasks
- Lose XP for skipped/missed tasks
- Level up to unlock harder challenges

### Persistent Data
- Save/load player progress using **MySQL Database**

### GUI (Swing)
- **Top Panel**: Player stats (Name, Level, XP bar, Rank, Deadline timer)
- **Center Panel**: Current task details and status
- **Bottom Panel**: Action buttons (Submit Task, Skip Task, Add Custom Task, Save & Exit)

---

## Class Structure

```text
+---------------+         +-------------+
|    Player     |         |    Task     |
+---------------+         +-------------+
| - name: String|         | - title: String
| - level: int  |         | - description: String
| - xp: int     |         | - deadline: LocalDateTime
| - rank: String|         | - completed: boolean
| - streak: int |         +-------------+
+---------------+         | + getters/setters |
| + gainXp()    |         +-------------+
| + loseXp()    |
| + levelUp()   |
| + updateStreak()|
+---------------+
        ^
        |
        | uses
        |
+-----------------+
|   TaskManager   |
+-----------------+
| + getNextTask() |
| + addTask()     |
| + skipTask()    |
| + checkDeadline()|
+-----------------+
        ^
        |
        | manages
        |
+-----------------+
|   GameManager   |
+-----------------+
| + saveData()    |
| + loadData()    |
| + updateState() |
+-----------------+
```

---

## Technology Stack
- **Java JDK 17+**
- **Swing GUI**: JFrame, JTable, JButton, JLabel, JProgressBar
- **MySQL Database** for data persistence
- **Collections**: ArrayList, HashMap for task management

---

## Installation & Setup
1. Install **Java JDK 17 or higher** (recommended: Java 17 LTS for best compatibility)
2. Install **XAMPP** and start MySQL service
3. **Setup Database**: Run `database/forgegrid_schema.sql` in phpMyAdmin to create the database before launching the app
4. Clone or download the project repository
5. **Build the project**: Run `build.bat` (Windows) or `build.sh` (Linux/Mac)
6. Open in an IDE (**VS Code / IntelliJ / Eclipse**)
7. **Run the application**: Right-click on `Main.java` → "Run Java" or use the IDE's run button

**Note:** The project uses MySQL for data persistence and requires the MySQL connector JAR in the `lib/` folder to be included in the classpath. Most IDEs will handle this automatically.
8. Progress is automatically saved to the MySQL database (`forgegrid`)

---

## Usage Instructions
1. **Start App** → View player stats and current task
2. **Complete Task** → Submit task to gain XP and unlock the next challenge
3. **Skip Task** → Lose XP but move to next task
4. **Add Custom Task** → Create personal “Goated Task” with a custom deadline
5. **Save & Exit** → Save all progress and close the app

---

## Future Enhancements
- **AI Screenshot Verification**: Validate task completion automatically
- **Online Task Pool**: Download community challenges via API
- **Achievements & Badges**: Reward milestones
- **Leaderboards**: Compare progress with other users
- **Multiple Skill Tracks**: DSA, Web, AI, DBMS
- **Animations & Sounds**: Level-up effects for engagement

---

## License
ForgeGrid is **open-source** and free for educational purposes.

---

**Forge your coding skills into legendary strength — with ForgeGrid!**
