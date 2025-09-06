# Habit RPG

**A gamified Java desktop application to track habits and motivate consistency using RPG-style rewards.**

---

## **Project Overview**

Habit RPG is a Java Swing-based desktop application that allows users to create and manage daily habits. Completing habits rewards the player with XP and coins, while missing habits decreases health. Users can also spend coins to recover health. The game motivates consistency and makes habit tracking fun by applying RPG mechanics.

---

## **Features**

* **Character System**: Name, Level, XP, Health, Coins.
* **Habit System**: Add, Edit, Delete habits; track completion status.
* **Progress Tracking**: XP, level, health, coins updated dynamically.
* **Health & Coins Interaction**: Health decreases for missed habits; coins can be spent to recover health.
* **Persistent Data**: Save and load progress using Java serialization.
* **Swing GUI**:

  * Top panel: character stats (JLabel + JProgressBar)
  * Center panel: habit list (JTable)
  * Bottom panel: action buttons (Add, Complete, Delete, Recover Health, Save & Exit)

---

## **Installation & Setup**

1. Make sure Java JDK is installed on your system.
2. Clone the repository or download the project files.
3. Open the project in an IDE (Eclipse, IntelliJ, or VS Code).
4. Compile and run the `Main.java` file.
5. The application will automatically load saved data if it exists.

---

## **Usage Instructions**

1. **Add Habit**: Enter a habit name and XP value.
2. **Complete Habit**: Marks a habit as completed, increasing XP and coins.
3. **Delete Habit**: Removes a habit from the list.
4. **Recover Health**: Spend coins to restore health.
5. **Save & Exit**: Saves all data and closes the application.

---

## **Class Structure**

* `Character`: Stores player stats and methods for updating XP, coins, and health.
* `Habit`: Stores habit details and completion status.
* `GameManager`: Manages habit operations, updates character stats, and handles save/load functionality.
* `Main`: Launches the GUI and initializes the application.

**Class Diagram:**

```
+----------------+       +----------------+
|   Character    |       |      Habit     |
+----------------+       +----------------+
| - name: String |       | - title: String|
| - level: int   |       | - xpValue: int |
| - xp: int      |       | - completed: boolean |
| - health: int  |       +----------------+
| - coins: int   |       | + getters/setters |
+----------------+       +----------------+
| + gainXp()     |
| + loseHealth() |
| + earnCoins()  |
| + checkLevelUp()|
| + recoverHealthWithCoins()|
+----------------+
        ^
        |
        | uses
        |
+------------------+
|   GameManager     |
+------------------+
| + completeHabit() |
| + addHabit()      |
| + deleteHabit()   |
| + saveData()      |
| + loadData()      |
+------------------+
```

---

## **Technology Used**

* Java (JDK 8 or above)
* Swing GUI components (JFrame, JTable, JButton, JLabel, JProgressBar)
* Java Serialization for data persistence

---

## **Future Enhancements**

* Daily streaks with bonus XP.
* Achievement badges.
* Sound effects or animations for level-ups.
* More interactive GUI elements (icons, color-coded bars).

---

## **License**

This project is open-source and free to use for educational purposes.

---

**Enjoy tracking your habits the fun way with Habit RPG!**
