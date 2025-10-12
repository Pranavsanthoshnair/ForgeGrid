# Quick Task System Reference

## 🎯 How to Use

### 1. **First Time Setup**
- Run the application
- Complete onboarding (select language and skill level)
- Tasks are automatically loaded based on your preferences

### 2. **View Your Tasks**
- Click "Tasks" in the sidebar
- See all available tasks for your language and skill level
- Completed tasks appear dimmed with a ✓ checkmark

### 3. **Complete a Task**
- Click "Mark Complete" on any task
- Enter the time you spent (in minutes)
- Receive XP reward immediately
- Task is saved to database permanently

### 4. **Track Progress**
- Go to "Home" to see your statistics:
  - Total Tasks Available
  - Completed Count
  - Available Tasks
  - Total XP Earned

## 📊 Task Breakdown by Language

### Java Tasks
- **Beginner**: 5 tasks (10-20 XP, 10-30 min)
- **Intermediate**: 5 tasks (30-40 XP, 45-90 min)
- **Advanced**: 5 tasks (60-100 XP, 120-240 min)

### Python Tasks
- **Beginner**: 5 tasks (10-20 XP, 10-30 min)
- **Intermediate**: 5 tasks (30-40 XP, 45-90 min)
- **Advanced**: 5 tasks (60-80 XP, 120-180 min)

### Other Languages
- Generic tasks that apply to any programming language

## 💾 Database

### Table: `user_tasks`
```sql
id              INT (auto-increment)
username        VARCHAR(100)
task_name       VARCHAR(255)
time_taken      INT (minutes)
xp_earned       INT
status          VARCHAR(50) default 'assigned'
completed_at    TIMESTAMP
```

### Automatic Features
- Table created automatically on first run
- All completions persisted permanently
- XP calculated from database, not hardcoded
- Secure with prepared statements

## 🎨 UI Features

### Task Card Shows:
- ✓ Task name (with checkmark if completed)
- 📝 Description
- ⭐ XP reward (gold color)
- ⏱ Estimated time (blue color)
- 📚 Skill level (purple color)
- 🟢 "Mark Complete" button (or "✓ Done" if completed)

### Visual States:
- **Available**: Bright colors, hover effect, clickable
- **Completed**: Dimmed, italic text, green border, disabled button

## 🔧 Adding More Tasks

To add tasks, edit `HardcodedTaskService.java`:

```java
private List<HardcodedTask> getBeginnerTasks(String language) {
    // Add your new task here
    tasks.add(new HardcodedTask(
        "Task Name",
        "Task Description",
        "Java",
        "Beginner",
        xpReward,    // e.g., 20
        minutes      // e.g., 30
    ));
}
```

Then rebuild with `.\build.bat`

## 🚀 Commands

```bash
# Build
.\build.bat

# Run
.\run.bat
```

## ✅ Success Indicators

- ✓ Tasks load on Dashboard startup
- ✓ Stats show real numbers (not hardcoded)
- ✓ Marking complete updates database
- ✓ XP increases with each completion
- ✓ Completed tasks persist after restart

## 🆘 Troubleshooting

**No tasks showing?**
- Check if onboarding is completed
- Verify language and skill level are set
- Check console for database errors

**Tasks not saving?**
- Verify MySQL is running
- Check `.env` file has correct database credentials
- Check console for SQL errors

**Wrong tasks showing?**
- Update your language/skill in Profile > Edit Profile
- Restart the application
- New tasks will load based on updated preferences

---

**Implementation Date**: Oct 2025  
**No AI Required**: All tasks are hardcoded in Java  
**Database**: MySQL with `user_tasks` table  
**Status**: ✅ Production Ready

