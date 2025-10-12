package com.forgegrid.service;

import com.forgegrid.model.HardcodedTask;
import com.forgegrid.db.DatabaseHelper;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing hardcoded tasks based on user preferences
 */
public class HardcodedTaskService {
    
    private DatabaseHelper dbHelper;
    
    public HardcodedTaskService() {
        this.dbHelper = DatabaseHelper.getInstance();
        createUserTasksTable();
    }
    
    /**
     * Create user_tasks table if it doesn't exist
     */
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
            "INDEX idx_username (username), " +
            "INDEX idx_status (status)" +
            ")";
        
        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("✓ user_tasks table initialized");
        } catch (SQLException e) {
            System.err.println("Error creating user_tasks table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get hardcoded tasks based on language and skill level
     */
    public List<HardcodedTask> getTasksForUser(String language, String level) {
        List<HardcodedTask> tasks = new ArrayList<>();
        
        // Normalize inputs
        String lang = language != null ? language.toLowerCase() : "java";
        String lvl = level != null ? level.toLowerCase() : "beginner";
        
        // Get appropriate task list
        if (lvl.contains("beginner")) {
            tasks = getBeginnerTasks(lang);
        } else if (lvl.contains("intermediate")) {
            tasks = getIntermediateTasks(lang);
        } else if (lvl.contains("advanced") || lvl.contains("expert")) {
            tasks = getAdvancedTasks(lang);
        } else {
            // Default to beginner if level not recognized
            tasks = getBeginnerTasks(lang);
        }
        
        return tasks;
    }
    
    /**
     * Beginner level tasks
     */
    private List<HardcodedTask> getBeginnerTasks(String language) {
        List<HardcodedTask> tasks = new ArrayList<>();
        
        if (language.contains("java")) {
            tasks.add(new HardcodedTask(
                "Hello World Program",
                "Write a simple Java program that prints 'Hello World' to the console",
                "Java", "Beginner", 10, 10
            ));
            tasks.add(new HardcodedTask(
                "Print Numbers 1-10",
                "Use a loop to print numbers from 1 to 10",
                "Java", "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Simple Calculator",
                "Create a calculator that can add, subtract, multiply, and divide two numbers",
                "Java", "Beginner", 20, 30
            ));
            tasks.add(new HardcodedTask(
                "Temperature Converter",
                "Write a program to convert Celsius to Fahrenheit",
                "Java", "Beginner", 15, 20
            ));
            tasks.add(new HardcodedTask(
                "Even or Odd Checker",
                "Create a program that checks if a number is even or odd",
                "Java", "Beginner", 10, 15
            ));
        } else if (language.contains("python")) {
            tasks.add(new HardcodedTask(
                "Add Two Numbers",
                "Write a function that adds two numbers and returns the result",
                "Python", "Beginner", 10, 10
            ));
            tasks.add(new HardcodedTask(
                "Simple Calculator",
                "Create a basic calculator with add, subtract, multiply, divide functions",
                "Python", "Beginner", 20, 30
            ));
            tasks.add(new HardcodedTask(
                "List Operations",
                "Create a list of numbers and find the sum, average, and maximum",
                "Python", "Beginner", 15, 20
            ));
            tasks.add(new HardcodedTask(
                "String Reverser",
                "Write a function to reverse a string",
                "Python", "Beginner", 10, 15
            ));
            tasks.add(new HardcodedTask(
                "Palindrome Checker",
                "Create a program to check if a word is a palindrome",
                "Python", "Beginner", 15, 20
            ));
        } else {
            // Default generic tasks
            tasks.add(new HardcodedTask(
                "Hello World",
                "Write a program that prints 'Hello World'",
                language, "Beginner", 10, 10
            ));
            tasks.add(new HardcodedTask(
                "Basic Variables",
                "Declare and use variables of different types",
                language, "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Simple Loop",
                "Use a loop to print numbers 1 to 10",
                language, "Beginner", 15, 20
            ));
        }
        
        return tasks;
    }
    
    /**
     * Intermediate level tasks
     */
    private List<HardcodedTask> getIntermediateTasks(String language) {
        List<HardcodedTask> tasks = new ArrayList<>();
        
        if (language.contains("java")) {
            tasks.add(new HardcodedTask(
                "OOP Class Structure",
                "Implement a basic class with constructors, getters, and setters",
                "Java", "Intermediate", 30, 45
            ));
            tasks.add(new HardcodedTask(
                "ArrayList Student Database",
                "Use ArrayList to store and manage student data",
                "Java", "Intermediate", 35, 60
            ));
            tasks.add(new HardcodedTask(
                "File Read/Write",
                "Read from and write to a text file using Java I/O",
                "Java", "Intermediate", 40, 60
            ));
            tasks.add(new HardcodedTask(
                "Exception Handling",
                "Implement proper try-catch blocks and custom exceptions",
                "Java", "Intermediate", 30, 45
            ));
            tasks.add(new HardcodedTask(
                "Collections Framework",
                "Use HashMap and TreeSet to solve a practical problem",
                "Java", "Intermediate", 35, 60
            ));
        } else if (language.contains("python")) {
            tasks.add(new HardcodedTask(
                "CLI To-Do App",
                "Build a command-line interface for a to-do list application",
                "Python", "Intermediate", 40, 90
            ));
            tasks.add(new HardcodedTask(
                "JSON Data Handling",
                "Read, modify, and write JSON data using Python",
                "Python", "Intermediate", 30, 60
            ));
            tasks.add(new HardcodedTask(
                "File Operations",
                "Read a CSV file and perform data analysis",
                "Python", "Intermediate", 35, 60
            ));
            tasks.add(new HardcodedTask(
                "Dictionary and Sets",
                "Use dictionaries and sets to solve a word frequency problem",
                "Python", "Intermediate", 30, 45
            ));
            tasks.add(new HardcodedTask(
                "Class and Objects",
                "Create a class hierarchy for a library management system",
                "Python", "Intermediate", 40, 90
            ));
        } else {
            tasks.add(new HardcodedTask(
                "Data Structures",
                "Implement basic data structures (arrays, lists)",
                language, "Intermediate", 30, 60
            ));
            tasks.add(new HardcodedTask(
                "Functions and Methods",
                "Create reusable functions with parameters and return values",
                language, "Intermediate", 30, 45
            ));
            tasks.add(new HardcodedTask(
                "File Handling",
                "Read and write data to files",
                language, "Intermediate", 35, 60
            ));
        }
        
        return tasks;
    }
    
    /**
     * Advanced level tasks
     */
    private List<HardcodedTask> getAdvancedTasks(String language) {
        List<HardcodedTask> tasks = new ArrayList<>();
        
        if (language.contains("java")) {
            tasks.add(new HardcodedTask(
                "Multithreading Application",
                "Create a producer-consumer application using threads",
                "Java", "Advanced", 60, 120
            ));
            tasks.add(new HardcodedTask(
                "REST API Client",
                "Build a client that consumes a REST API using HttpURLConnection",
                "Java", "Advanced", 70, 150
            ));
            tasks.add(new HardcodedTask(
                "Design Patterns",
                "Implement Singleton, Factory, and Observer patterns",
                "Java", "Advanced", 80, 180
            ));
            tasks.add(new HardcodedTask(
                "JDBC Database App",
                "Create a full CRUD application with MySQL integration",
                "Java", "Advanced", 100, 240
            ));
            tasks.add(new HardcodedTask(
                "Lambda and Streams",
                "Use Java 8+ features for functional programming",
                "Java", "Advanced", 60, 120
            ));
        } else if (language.contains("python")) {
            tasks.add(new HardcodedTask(
                "Web Scraper",
                "Build a web scraper using BeautifulSoup or Scrapy",
                "Python", "Advanced", 70, 150
            ));
            tasks.add(new HardcodedTask(
                "REST API with Flask",
                "Create a RESTful API using Flask framework",
                "Python", "Advanced", 80, 180
            ));
            tasks.add(new HardcodedTask(
                "Data Analysis",
                "Perform data analysis using Pandas and NumPy",
                "Python", "Advanced", 70, 150
            ));
            tasks.add(new HardcodedTask(
                "Decorator Pattern",
                "Implement custom decorators and context managers",
                "Python", "Advanced", 60, 120
            ));
            tasks.add(new HardcodedTask(
                "Async Programming",
                "Use asyncio for concurrent programming",
                "Python", "Advanced", 80, 180
            ));
        } else {
            tasks.add(new HardcodedTask(
                "Complex Algorithm",
                "Implement a sorting or searching algorithm",
                language, "Advanced", 60, 120
            ));
            tasks.add(new HardcodedTask(
                "Design Patterns",
                "Implement common design patterns",
                language, "Advanced", 70, 150
            ));
            tasks.add(new HardcodedTask(
                "Full Application",
                "Build a complete application with multiple modules",
                language, "Advanced", 100, 240
            ));
        }
        
        return tasks;
    }
    
    /**
     * Save completed task to database
     */
    public boolean saveCompletedTask(String username, String taskName, int timeTaken, int xpEarned) {
        String insertSQL = 
            "INSERT INTO user_tasks (username, task_name, time_taken, xp_earned, status, completed_at) " +
            "VALUES (?, ?, ?, ?, 'completed', ?)";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, taskName);
            pstmt.setInt(3, timeTaken);
            pstmt.setInt(4, xpEarned);
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving completed task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get completed tasks for a user
     */
    public List<String> getCompletedTasks(String username) {
        List<String> completedTasks = new ArrayList<>();
        String selectSQL = "SELECT task_name FROM user_tasks WHERE username = ? AND status = 'completed'";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                completedTasks.add(rs.getString("task_name"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting completed tasks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return completedTasks;
    }
    
    /**
     * Get total XP earned by user
     */
    public int getTotalXP(String username) {
        String selectSQL = "SELECT SUM(xp_earned) as total FROM user_tasks WHERE username = ? AND status = 'completed'";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total XP: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Get net XP across all tasks (completed positive, skipped negative)
     */
    public int getNetXP(String username) {
        String selectSQL = "SELECT COALESCE(SUM(xp_earned), 0) as total FROM user_tasks WHERE username = ?";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting net XP: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of skipped tasks for a user
     */
    public int getSkippedTaskCount(String username) {
        String selectSQL = "SELECT COUNT(*) as count FROM user_tasks WHERE username = ? AND status = 'skipped'";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting skipped task count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Record that a task has been assigned to the user now, if not already recorded.
     * Uses status 'assigned' and stores the assigned time in completed_at column.
     */
    public void recordAssignedTask(String username, String taskName) {
        String existsSQL = "SELECT 1 FROM user_tasks WHERE username = ? AND task_name = ? LIMIT 1";
        String insertSQL = "INSERT INTO user_tasks (username, task_name, time_taken, xp_earned, status, completed_at) VALUES (?, ?, NULL, 0, 'assigned', ?)";
        try (Connection conn = dbHelper.getConnection()) {
            try (PreparedStatement check = conn.prepareStatement(existsSQL)) {
                check.setString(1, username);
                check.setString(2, taskName);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    return; // already recorded (assigned/completed/skipped)
                }
            }
            try (PreparedStatement ins = conn.prepareStatement(insertSQL)) {
                ins.setString(1, username);
                ins.setString(2, taskName);
                ins.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
                ins.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error recording assigned task: " + e.getMessage());
        }
    }

    /**
     * Convert any 'assigned' tasks older than 24 hours into 'skipped' with XP penalty.
     * Penalty is 50% of the task XP (negative).
     */
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

    private int getXpRewardForTaskName(String taskName, String language, String level) {
        List<HardcodedTask> list = getTasksForUser(language, level);
        for (HardcodedTask t : list) {
            if (t.getTaskName().equalsIgnoreCase(taskName)) return t.getXpReward();
        }
        return 10; // sensible default
    }

    /**
     * Get names of all tasks recorded (completed or skipped) for the given user.
     * Used to compute remaining available tasks from the current hardcoded list.
     */
    public java.util.Set<String> getRecordedTaskNames(String username) {
        java.util.Set<String> names = new java.util.HashSet<>();
        String selectSQL = "SELECT task_name FROM user_tasks WHERE username = ?";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("task_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recorded task names: " + e.getMessage());
            e.printStackTrace();
        }
        return names;
    }
    
    /**
     * Get task completion count
     */
    public int getCompletedTaskCount(String username) {
        String selectSQL = "SELECT COUNT(*) as count FROM user_tasks WHERE username = ? AND status = 'completed'";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting task count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Save skipped task to database
     */
    public boolean saveSkippedTask(String username, String taskName, int timeTaken, int xpLost) {
        String insertSQL = 
            "INSERT INTO user_tasks (username, task_name, time_taken, xp_earned, status, completed_at) " +
            "VALUES (?, ?, ?, ?, 'skipped', ?)";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, taskName);
            pstmt.setInt(3, timeTaken);
            pstmt.setInt(4, xpLost); // Negative value for XP loss
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving skipped task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get task history for a user (recent tasks)
     */
    public List<com.forgegrid.model.TaskHistoryEntry> getTaskHistory(String username, int limit) {
        List<com.forgegrid.model.TaskHistoryEntry> history = new ArrayList<>();
        String selectSQL = 
            "SELECT task_name, time_taken, xp_earned, status, completed_at " +
            "FROM user_tasks " +
            "WHERE username = ? " +
            "ORDER BY completed_at DESC " +
            "LIMIT ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String taskName = rs.getString("task_name");
                int timeTaken = rs.getInt("time_taken");
                int xpEarned = rs.getInt("xp_earned");
                String status = rs.getString("status");
                Timestamp timestamp = rs.getTimestamp("completed_at");
                
                String timeStr = formatTimestamp(timestamp);
                
                history.add(new com.forgegrid.model.TaskHistoryEntry(
                    taskName, timeTaken, xpEarned, status, timeStr
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting task history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return history;
    }
    
    /**
     * Format timestamp to relative time
     */
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "Unknown";
        
        long now = System.currentTimeMillis();
        long then = timestamp.getTime();
        long diff = now - then;
        
        long minutes = diff / 60000;
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        
        long days = hours / 24;
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";
        
        return timestamp.toString().substring(0, 16);
    }
}

