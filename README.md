# CodeForge 🔥

**Forge your coding skills into legendary strength — with CodeForge!**

---

## **Project Overview**

CodeForge is a Java Swing-based desktop application that helps CSE students and self-learners grow their coding skills through a quest-like progression system. The app assigns meaningful tasks based on the player’s current level, each with a 24-hour deadline. Completing tasks on time rewards XP and levels, unlocking harder challenges, while missing deadlines causes XP loss. If a task is completed early, the next challenge is offered immediately. Players can also create custom “Goated Tasks” with self-set deadlines. CodeForge combines progression, time pressure, and learning content to keep users engaged daily.

---

## **Features**

- **Player System**: Name, Level, XP, Rank, Streak.
- **Task System**: Assign, Submit, Skip tasks; add custom “Goated Tasks.”
- **Timed Challenges**: Each task has a deadline; early completion unlocks the next task.
- **XP & Leveling**: Gain XP for completed tasks; lose XP for missed ones; level up to unlock more complex tasks.
- **Custom Tasks**: Users can add personal challenges with their own deadlines.
- **Persistent Data**: Save and load progress using Java serialization.
- **Swing GUI**:
  - **Top panel**: Player stats (Name, Level, XP bar, Rank, Deadline timer).
  - **Center panel**: Current task details and status.
  - **Bottom panel**: Action buttons (Submit Task, Skip Task, Add Custom Task, Save & Exit).

---

## **Installation & Setup**

1. **Make sure** Java JDK 8 or above is installed on your system.
2. **Clone** the repository or download the project files.
3. **Open** the project in an IDE (Eclipse, IntelliJ, or VS Code).
4. **Compile** and run the `Main.java` file.
5. The application will automatically **load saved data** if it exists.

---

## **Usage Instructions**

1. **Start App**: View your stats and the currently assigned task.
2. **Complete Task**: Submit a completed task to gain XP and unlock the next challenge.
3. **Skip Task**: Skip a task at the cost of XP.
4. **Add Custom Task**: Add a “Goated Task” with a self-set deadline.
5. **Save & Exit**: Saves all data and closes the application.

---

## **Class Structure**

- **Player**: Stores user stats and methods for updating XP, rank, and streaks.
- **Task**: Stores task details, deadlines, and completion status.
- **TaskManager**: Handles task assignment, progression, and deadline checks.
- **GameManager**: Manages interactions between Player and TaskManager; handles save/load functionality.
- **Main**: Launches the GUI and initializes the application.

**Class Diagram:**

```
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

## **Technology Used**

- **Java** (JDK 8 or above)
- **Swing GUI components** (`JFrame`, `JTable`, `JButton`, `JLabel`, `JProgressBar`)
- **Java Serialization** for data persistence
- **Collections** (`ArrayList`, `HashMap`) for task management

---

## **Future Enhancements**

- Online task pool integration via APIs.
- Achievements and badges for milestones.
- Leaderboards to encourage friendly competition.
- Multiple skill tracks (DSA, Web, AI, DBMS).
- Animations and sound effects for level-ups.

---

## **License**

This project is open-source and free to use for educational purposes.

---

**Forge your coding skills into legendary strength — with CodeForge!**
