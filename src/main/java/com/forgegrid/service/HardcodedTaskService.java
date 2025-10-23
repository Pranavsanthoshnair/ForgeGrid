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
        
        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("✓ user_tasks table initialized");
            migrateUserTasksForGoated(stmt);
        } catch (SQLException e) {
            System.err.println("Error creating user_tasks table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add columns for Goated Tasks if not present.
     */
    private void migrateUserTasksForGoated(Statement stmt) {
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN type VARCHAR(20) DEFAULT 'regular'"); } catch (SQLException ignored) {}
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN title VARCHAR(255) NULL"); } catch (SQLException ignored) {}
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN description TEXT NULL"); } catch (SQLException ignored) {}
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN deadline DATETIME NULL"); } catch (SQLException ignored) {}
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN xp INT NULL"); } catch (SQLException ignored) {}
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN is_completed TINYINT(1) DEFAULT 0"); } catch (SQLException ignored) {}
        try { stmt.execute("ALTER TABLE user_tasks ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"); } catch (SQLException ignored) {}
        try { stmt.execute("CREATE INDEX idx_user_tasks_type ON user_tasks(type)"); } catch (SQLException ignored) {}
    }

    /**
     * Create a Goated Task for the user.
     */
    public boolean createGoatedTask(String username, String title, String description, java.time.LocalDateTime deadline, int xp) {
        if (xp < 0) xp = 0;
        if (xp > 500) xp = 500;
        String sql = "INSERT INTO user_tasks (username, task_name, title, description, deadline, xp, status, type, is_completed, created_at) VALUES (?, ?, ?, ?, ?, ?, 'assigned', 'goated', 0, ?)";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String taskName = title != null && !title.isBlank() ? title : "Custom Task";
            ps.setString(1, username);
            ps.setString(2, taskName);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setTimestamp(5, deadline != null ? java.sql.Timestamp.valueOf(deadline) : null);
            ps.setInt(6, xp);
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating goated task: " + e.getMessage());
            return false;
        }
    }

    /**
     * List all Goated Tasks for a user.
     */
    public java.util.List<com.forgegrid.model.GoatedTask> listGoatedTasks(String username) {
        java.util.List<com.forgegrid.model.GoatedTask> list = new java.util.ArrayList<>();
        String sql = "SELECT id, title, description, deadline, xp, is_completed, created_at FROM user_tasks WHERE username = ? AND type = 'goated' ORDER BY is_completed ASC, deadline IS NULL ASC, deadline ASC";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                com.forgegrid.model.GoatedTask t = new com.forgegrid.model.GoatedTask(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getTimestamp("deadline"),
                    rs.getInt("xp"),
                    rs.getBoolean("is_completed"),
                    rs.getTimestamp("created_at")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error listing goated tasks: " + e.getMessage());
        }
        return list;
    }

    /**
     * Mark a Goated Task as completed and credit XP.
     */
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

    /**
     * Update a Goated Task.
     */
    public boolean updateGoatedTask(String username, int taskId, String title, String description, java.time.LocalDateTime deadline, Integer xp) {
        String sql = "UPDATE user_tasks SET title = ?, description = ?, deadline = ?, xp = ? WHERE id = ? AND username = ? AND type = 'goated'";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setTimestamp(3, deadline != null ? java.sql.Timestamp.valueOf(deadline) : null);
            ps.setInt(4, xp != null ? Math.max(0, Math.min(500, xp)) : 0);
            ps.setInt(5, taskId);
            ps.setString(6, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating goated task: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a Goated Task.
     */
    public boolean deleteGoatedTask(String username, int taskId) {
        String sql = "DELETE FROM user_tasks WHERE id = ? AND username = ? AND type = 'goated'";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting goated task: " + e.getMessage());
            return false;
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
            // Java Basics Beginner Tasks
            tasks.add(new HardcodedTask(
                "Hello World Program",
                "Write a Java program to print 'Hello, World!'",
                "Java", "Beginner", 10, 10
            ));
            tasks.add(new HardcodedTask(
                "Personal Information Display",
                "Write a program to print your name, age, and city",
                "Java", "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Basic Calculator",
                "Write a program to perform addition, subtraction, multiplication, and division of two numbers",
                "Java", "Beginner", 20, 25
            ));
            tasks.add(new HardcodedTask(
                "Number Swapping with Third Variable",
                "Write a program to swap two numbers using a third variable",
                "Java", "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Largest of Two Numbers",
                "Write a program to find the largest of two numbers",
                "Java", "Beginner", 15, 15
            ));
            // Java Control Statements Beginner Tasks
            tasks.add(new HardcodedTask(
                "Print Numbers 1-10",
                "Print numbers from 1 to 10 using a for loop",
                "Java", "Beginner", 10, 10
            ));
            tasks.add(new HardcodedTask(
                "Even Numbers 1-50",
                "Print all even numbers between 1 and 50",
                "Java", "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Sum of Natural Numbers",
                "Calculate the sum of first n natural numbers",
                "Java", "Beginner", 20, 20
            ));
            tasks.add(new HardcodedTask(
                "Multiplication Table",
                "Display multiplication table of a given number",
                "Java", "Beginner", 20, 20
            ));
            // Java Arrays Beginner Tasks
            tasks.add(new HardcodedTask(
                "Array Read and Display",
                "Read and display elements of an array",
                "Java", "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Array Largest and Smallest",
                "Find the largest and smallest element in an array",
                "Java", "Beginner", 20, 20
            ));
            tasks.add(new HardcodedTask(
                "Array Sum and Average",
                "Calculate sum and average of array elements",
                "Java", "Beginner", 20, 20
            ));
            // Java Strings Beginner Tasks
            tasks.add(new HardcodedTask(
                "String Input and Display",
                "Read a string and print it",
                "Java", "Beginner", 10, 10
            ));
            tasks.add(new HardcodedTask(
                "String Length Calculator",
                "Find length of a string without using length() method",
                "Java", "Beginner", 20, 20
            ));
            tasks.add(new HardcodedTask(
                "Vowel and Consonant Counter",
                "Count vowels and consonants in a string",
                "Java", "Beginner", 25, 25
            ));
            // Java Methods Beginner Tasks
            tasks.add(new HardcodedTask(
                "Add Two Numbers Function",
                "Write a method to add two numbers",
                "Java", "Beginner", 15, 15
            ));
            tasks.add(new HardcodedTask(
                "Factorial Function",
                "Write a method to calculate factorial",
                "Java", "Beginner", 20, 20
            ));
            // Java OOP Beginner Tasks
            tasks.add(new HardcodedTask(
                "Student Class",
                "Create a class Student with data members name, rollNo, and a method to display details",
                "Java", "Beginner", 30, 30
            ));
            tasks.add(new HardcodedTask(
                "Rectangle Class",
                "Create a class Rectangle to calculate area and perimeter",
                "Java", "Beginner", 25, 25
            ));
        } else if (language.contains("c")) {
            // Comprehensive C Programming Beginner Tasks
            tasks.add(new HardcodedTask(
                "Hello World Program",
                "Write a C program to print 'Hello, World!'",
                "C", "Beginner", 10, 5
            ));
            tasks.add(new HardcodedTask(
                "Personal Information Display",
                "Write a program to display your name, age, and city",
                "C", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Basic Calculator",
                "Write a program to perform addition, subtraction, multiplication, and division of two numbers",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Number Swapping with Third Variable",
                "Write a program to swap two numbers using a third variable",
                "C", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Largest of Two Numbers",
                "Write a program to find the largest of two numbers",
                "C", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Numbers 1 to 10 Loop",
                "Print numbers from 1 to 10 using a for loop",
                "C", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Even Numbers 1-50",
                "Print all even numbers between 1 and 50",
                "C", "Beginner", 15, 12
            ));
            tasks.add(new HardcodedTask(
                "Sum of Natural Numbers",
                "Find the sum of the first n natural numbers",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Multiplication Table",
                "Display the multiplication table of a given number",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Array Read and Display",
                "Read and display elements of an array",
                "C", "Beginner", 15, 12
            ));
            tasks.add(new HardcodedTask(
                "Array Largest and Smallest",
                "Find the largest and smallest elements in an array",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Array Sum and Average",
                "Calculate sum and average of array elements",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "String Input and Display",
                "Read a string and print it",
                "C", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "String Length Calculator",
                "Find the length of a string without using strlen()",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Vowel and Consonant Counter",
                "Count vowels and consonants in a string",
                "C", "Beginner", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Add Two Numbers Function",
                "Write a function to add two numbers",
                "C", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Factorial Function",
                "Write a function to find factorial of a number",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Pointer Declaration and Initialization",
                "Demonstrate pointer declaration and initialization",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Swap Numbers Using Pointers",
                "Swap two numbers using pointers",
                "C", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Sum and Average Using Pointers",
                "Find sum and average of elements using pointers",
                "C", "Beginner", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Student Structure Definition",
                "Define a structure named Student with name, roll number, and marks",
                "C", "Beginner", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Multiple Students Input/Display",
                "Input and display details of multiple students",
                "C", "Beginner", 30, 25
            ));
        } else if (language.contains("python")) {
            // Comprehensive Python Beginner Tasks
            tasks.add(new HardcodedTask(
                "Hello World Program",
                "Write a Python program to print 'Hello, World!'",
                "Python", "Beginner", 10, 5
            ));
            tasks.add(new HardcodedTask(
                "Personal Information Display",
                "Write a program to input and display your name, age, and city",
                "Python", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Basic Calculator",
                "Write a program to perform addition, subtraction, multiplication, and division of two numbers",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Number Swapping",
                "Write a program to swap two numbers",
                "Python", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Largest of Two Numbers",
                "Write a program to find the largest of two numbers",
                "Python", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Numbers 1 to 10 Loop",
                "Print numbers from 1 to 10 using a for loop",
                "Python", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Even Numbers 1-50",
                "Print all even numbers between 1 and 50",
                "Python", "Beginner", 15, 12
            ));
            tasks.add(new HardcodedTask(
                "Sum of Natural Numbers",
                "Calculate the sum of first n natural numbers",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Multiplication Table",
                "Display the multiplication table of a given number",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "List Creation and Display",
                "Create a list and display its elements",
                "Python", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Largest and Smallest in List",
                "Find the largest and smallest elements in a list",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "List Sum and Average",
                "Calculate sum and average of list elements",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "String Input and Display",
                "Read a string and print it",
                "Python", "Beginner", 5, 5
            ));
            tasks.add(new HardcodedTask(
                "String Length Calculator",
                "Find length of a string without using len()",
                "Python", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Vowel and Consonant Counter",
                "Count vowels and consonants in a string",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Add Two Numbers Function",
                "Write a function to add two numbers",
                "Python", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Factorial Function",
                "Write a function to calculate factorial",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Tuple Creation and Access",
                "Create a tuple and access its elements",
                "Python", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "List to Tuple Conversion",
                "Convert a list into a tuple",
                "Python", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Tuple Packing and Unpacking",
                "Demonstrate tuple packing and unpacking",
                "Python", "Beginner", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Dictionary Creation and Display",
                "Create a dictionary and display its elements",
                "Python", "Beginner", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Dictionary CRUD Operations",
                "Access, add, and remove elements from a dictionary",
                "Python", "Beginner", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Student Class",
                "Create a class Student with attributes name and roll number, and a method to display details",
                "Python", "Beginner", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Rectangle Class",
                "Create a class Rectangle to calculate area and perimeter",
                "Python", "Beginner", 25, 20
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
            // Java Basics Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Even or Odd Checker",
                "Write a program to check whether a number is even or odd",
                "Java", "Intermediate", 15, 20
            ));
            tasks.add(new HardcodedTask(
                "Largest of Three Numbers",
                "Write a program to find the largest of three numbers",
                "Java", "Intermediate", 20, 25
            ));
            tasks.add(new HardcodedTask(
                "Leap Year Checker",
                "Write a program to check whether a given year is a leap year",
                "Java", "Intermediate", 25, 30
            ));
            tasks.add(new HardcodedTask(
                "ASCII Value Finder",
                "Write a program to find the ASCII value of a character",
                "Java", "Intermediate", 15, 20
            ));
            tasks.add(new HardcodedTask(
                "Quadratic Equation Solver",
                "Write a program to find the roots of a quadratic equation",
                "Java", "Intermediate", 35, 40
            ));
            // Java Control Statements Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Prime Number Checker",
                "Check whether a number is prime",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "Factorial Calculator",
                "Find factorial of a number",
                "Java", "Intermediate", 25, 30
            ));
            tasks.add(new HardcodedTask(
                "Number Reverser",
                "Reverse a number",
                "Java", "Intermediate", 25, 30
            ));
            tasks.add(new HardcodedTask(
                "Palindrome Checker",
                "Check whether a number is palindrome",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "Fibonacci Series Generator",
                "Generate Fibonacci series up to n terms",
                "Java", "Intermediate", 35, 40
            ));
            // Java Arrays Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Array Sorting",
                "Sort an array in ascending/descending order",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "Linear Search in Array",
                "Search an element in an array (linear search)",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "Matrix Addition and Subtraction",
                "Perform matrix addition and subtraction",
                "Java", "Intermediate", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "Count Even and Odd Numbers",
                "Count even and odd numbers in an array",
                "Java", "Intermediate", 25, 30
            ));
            // Java Strings Intermediate Tasks
            tasks.add(new HardcodedTask(
                "String Palindrome Checker",
                "Check whether a string is palindrome",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "String Reverser",
                "Reverse a string",
                "Java", "Intermediate", 25, 30
            ));
            tasks.add(new HardcodedTask(
                "String Comparison",
                "Compare two strings without using equals()",
                "Java", "Intermediate", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "Case Conversion",
                "Convert string to uppercase and lowercase",
                "Java", "Intermediate", 25, 30
            ));
            // Java Methods Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Prime Number Function",
                "Write a method to check prime number",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "String Reverse Function",
                "Write a method to reverse a string",
                "Java", "Intermediate", 25, 30
            ));
            tasks.add(new HardcodedTask(
                "Recursive Fibonacci",
                "Write a recursive method to calculate Fibonacci",
                "Java", "Intermediate", 35, 40
            ));
            // Java OOP Intermediate Tasks
            tasks.add(new HardcodedTask(
                "BankAccount Class",
                "Create a class BankAccount with deposit and withdraw methods",
                "Java", "Intermediate", 40, 50
            ));
            tasks.add(new HardcodedTask(
                "Employee Class",
                "Create a class Employee with constructors and method overloading",
                "Java", "Intermediate", 45, 55
            ));
            tasks.add(new HardcodedTask(
                "Inheritance Demo",
                "Demonstrate inheritance using Animal → Dog example",
                "Java", "Intermediate", 50, 60
            ));
            // Java Exception Handling Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Try-Catch-Finally Demo",
                "Demonstrate try-catch-finally block",
                "Java", "Intermediate", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "Exception Handling",
                "Handle ArrayIndexOutOfBoundsException and ArithmeticException",
                "Java", "Intermediate", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "Multiple Catch Blocks",
                "Use multiple catch blocks",
                "Java", "Intermediate", 30, 35
            ));
            // Java File Handling Intermediate Tasks
            tasks.add(new HardcodedTask(
                "File Read/Write",
                "Write a program to read and write text files using FileReader and FileWriter",
                "Java", "Intermediate", 40, 50
            ));
            tasks.add(new HardcodedTask(
                "File Statistics Counter",
                "Count number of words, lines, and characters in a file",
                "Java", "Intermediate", 45, 55
            ));
            // Java Collections Intermediate Tasks
            tasks.add(new HardcodedTask(
                "ArrayList Student Names",
                "Use ArrayList to store and print student names",
                "Java", "Intermediate", 35, 45
            ));
            tasks.add(new HardcodedTask(
                "HashMap Student Records",
                "Use HashMap to store and display student roll numbers and names",
                "Java", "Intermediate", 40, 50
            ));
            // Java Multithreading Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Thread Class Demo",
                "Create and start a thread using Thread class",
                "Java", "Intermediate", 40, 50
            ));
            tasks.add(new HardcodedTask(
                "Runnable Interface Demo",
                "Create and start a thread using Runnable interface",
                "Java", "Intermediate", 40, 50
            ));
        } else if (language.contains("c")) {
            // Comprehensive C Programming Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Even or Odd Checker",
                "Write a program to check whether a number is even or odd",
                "C", "Intermediate", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Largest of Three Numbers",
                "Write a program to find the largest among three numbers",
                "C", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Leap Year Checker",
                "Write a program to check whether a year is a leap year",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "ASCII Value Finder",
                "Write a program to find the ASCII value of a character",
                "C", "Intermediate", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Quadratic Equation Solver",
                "Write a program to find the roots of a quadratic equation",
                "C", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Prime Number Checker",
                "Check whether a number is prime",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Factorial Calculator",
                "Find the factorial of a number",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Number Reverser",
                "Reverse a number",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Palindrome Checker",
                "Check whether a number is palindrome",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Fibonacci Series Generator",
                "Generate the Fibonacci series up to n terms",
                "C", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Array Sorting",
                "Sort an array in ascending order",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Linear Search in Array",
                "Search an element in an array (Linear Search)",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Count Even and Odd Elements",
                "Count even and odd elements in an array",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Matrix Addition and Subtraction",
                "Perform addition and subtraction of two matrices",
                "C", "Intermediate", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "String Palindrome Checker",
                "Check whether a string is palindrome",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "String Reverser",
                "Reverse a string",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "String Comparison",
                "Compare two strings without using strcmp()",
                "C", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Case Conversion",
                "Convert a string to uppercase and lowercase",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Prime Number Function",
                "Write a function to check if a number is prime",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Number Reverse Function",
                "Write a function to reverse a number",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Recursive Fibonacci",
                "Write a recursive function to generate Fibonacci series",
                "C", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Array Elements Using Pointers",
                "Access array elements using pointers",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "String Length Using Pointers",
                "Find length of a string using pointers",
                "C", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Student Marks Calculation",
                "Calculate total and average marks of students using structures",
                "C", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Union Demonstration",
                "Demonstrate use of a union",
                "C", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Text File Read/Write",
                "Write a program to read and write data to a text file",
                "C", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "File Statistics Counter",
                "Count the number of lines, words, and characters in a file",
                "C", "Intermediate", 40, 35
            ));
        } else if (language.contains("python")) {
            // Comprehensive Python Intermediate Tasks
            tasks.add(new HardcodedTask(
                "Even or Odd Checker",
                "Check whether a number is even or odd",
                "Python", "Intermediate", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Largest of Three Numbers",
                "Find the largest among three numbers",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Leap Year Checker",
                "Check whether a given year is a leap year",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "ASCII Value Finder",
                "Find the ASCII value of a character",
                "Python", "Intermediate", 10, 8
            ));
            tasks.add(new HardcodedTask(
                "Quadratic Equation Solver",
                "Find the roots of a quadratic equation",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Prime Number Checker",
                "Check whether a number is prime",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Factorial Calculator",
                "Find factorial of a number",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Number Reverser",
                "Reverse a number",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Palindrome Checker",
                "Check whether a number is palindrome",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Fibonacci Series Generator",
                "Generate Fibonacci series up to n terms",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "List Sorting",
                "Sort a list in ascending or descending order",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Linear Search in List",
                "Search for an element in a list (linear search)",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Count Even and Odd Numbers",
                "Count even and odd numbers in a list",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Remove Duplicates from List",
                "Remove duplicates from a list",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "String Palindrome Checker",
                "Check whether a string is palindrome",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "String Reverser",
                "Reverse a string",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "String Comparison",
                "Compare two strings without using built-in functions",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Case Conversion",
                "Convert a string to uppercase and lowercase",
                "Python", "Intermediate", 15, 10
            ));
            tasks.add(new HardcodedTask(
                "Prime Number Function",
                "Write a function to check whether a number is prime",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "String Reverse Function",
                "Write a function to reverse a string",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Recursive Fibonacci",
                "Write a recursive function to generate Fibonacci series",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Set Operations",
                "Find union, intersection, and difference between two sets",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Set Relationships",
                "Check if two sets are disjoint or subsets",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Element Frequency Counter",
                "Count frequency of elements using a dictionary",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Merge Two Dictionaries",
                "Merge two dictionaries",
                "Python", "Intermediate", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Dictionary Sorting",
                "Sort a dictionary by keys or values",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Text File Read/Write",
                "Read and write text files",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "File Statistics Counter",
                "Count the number of words, lines, and characters in a file",
                "Python", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Try-Except-Finally Demo",
                "Demonstrate try-except-finally block",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Specific Exception Handling",
                "Handle ZeroDivisionError and FileNotFoundError",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Multiple Exception Blocks",
                "Use multiple except blocks",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Bank Account Class",
                "Create a class BankAccount with deposit and withdraw methods",
                "Python", "Intermediate", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Inheritance Demo",
                "Demonstrate inheritance using Animal → Dog example",
                "Python", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Constructor Overloading",
                "Show constructor overloading using default arguments",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Built-in Modules Usage",
                "Import built-in modules like math and random and use their functions",
                "Python", "Intermediate", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Custom Module Creation",
                "Create and import your own module",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Stack Implementation",
                "Implement stack using list",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Queue Implementation",
                "Implement queue using list or collections.deque",
                "Python", "Intermediate", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Graph Representation and Traversal",
                "Use dictionary to represent a graph and perform DFS or BFS",
                "Python", "Intermediate", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "List and Dictionary Comprehensions",
                "Use list comprehension and dictionary comprehension",
                "Python", "Intermediate", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Functional Programming",
                "Demonstrate use of lambda, map, filter, and reduce",
                "Python", "Intermediate", 35, 30
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
            // Java Control Statements Expert Tasks
            tasks.add(new HardcodedTask(
                "Prime Numbers in Range",
                "Display all prime numbers between two intervals",
                "Java", "Expert", 45, 50
            ));
            tasks.add(new HardcodedTask(
                "Sum of Digits Calculator",
                "Find the sum of digits of a number using while loop",
                "Java", "Expert", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "GCD and LCM Calculator",
                "Find GCD and LCM of two numbers",
                "Java", "Expert", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "Pattern Programs",
                "Print pattern programs (triangle, pyramid, diamond)",
                "Java", "Expert", 50, 60
            ));
            // Java Arrays Expert Tasks
            tasks.add(new HardcodedTask(
                "Merge Two Arrays",
                "Merge two arrays",
                "Java", "Expert", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "Second Largest Element",
                "Find the second largest element in an array",
                "Java", "Expert", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "Matrix Multiplication",
                "Perform matrix multiplication",
                "Java", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "Binary Search Implementation",
                "Implement binary search",
                "Java", "Expert", 45, 50
            ));
            // Java Strings Expert Tasks
            tasks.add(new HardcodedTask(
                "Character Frequency Counter",
                "Count occurrences of each character in a string",
                "Java", "Expert", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "Remove Duplicate Characters",
                "Remove duplicate characters from a string",
                "Java", "Expert", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "First Non-Repeated Character",
                "Find the first non-repeated character",
                "Java", "Expert", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "Anagram Checker",
                "Check whether two strings are anagrams",
                "Java", "Expert", 45, 50
            ));
            // Java Methods Expert Tasks
            tasks.add(new HardcodedTask(
                "Recursive GCD Function",
                "Write a recursive method to find GCD",
                "Java", "Expert", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "Armstrong Number Checker",
                "Write a method to check Armstrong number",
                "Java", "Expert", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "Recursive Power Function",
                "Write a method to calculate power (x^n) using recursion",
                "Java", "Expert", 30, 35
            ));
            // Java OOP Expert Tasks
            tasks.add(new HardcodedTask(
                "Method Overriding Demo",
                "Demonstrate method overriding and dynamic method dispatch",
                "Java", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "Abstract Class Implementation",
                "Implement an abstract class and interface example",
                "Java", "Expert", 60, 70
            ));
            tasks.add(new HardcodedTask(
                "Multiple Inheritance with Interfaces",
                "Implement multiple inheritance using interfaces",
                "Java", "Expert", 55, 65
            ));
            tasks.add(new HardcodedTask(
                "Encapsulation Demo",
                "Demonstrate encapsulation and data hiding",
                "Java", "Expert", 45, 55
            ));
            // Java Exception Handling Expert Tasks
            tasks.add(new HardcodedTask(
                "Custom Exception Class",
                "Create a custom exception class",
                "Java", "Expert", 45, 55
            ));
            tasks.add(new HardcodedTask(
                "Exception Chaining Demo",
                "Demonstrate exception chaining",
                "Java", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "Throw and Rethrow Exceptions",
                "Throw and rethrow exceptions between methods",
                "Java", "Expert", 45, 55
            ));
            // Java File Handling Expert Tasks
            tasks.add(new HardcodedTask(
                "File Copy Program",
                "Copy contents from one file to another",
                "Java", "Expert", 30, 35
            ));
            tasks.add(new HardcodedTask(
                "File Append Operations",
                "Append data to a file",
                "Java", "Expert", 25, 30
            ));
            tasks.add(new HardcodedTask(
                "Object Serialization",
                "Serialize and deserialize an object using ObjectOutputStream and ObjectInputStream",
                "Java", "Expert", 60, 70
            ));
            // Java Collections Expert Tasks
            tasks.add(new HardcodedTask(
                "List Sorting with Comparator",
                "Sort a list using Comparator and Comparable",
                "Java", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "HashSet Remove Duplicates",
                "Use HashSet to remove duplicates",
                "Java", "Expert", 35, 40
            ));
            tasks.add(new HardcodedTask(
                "Student Record Management",
                "Implement a mini project using Collections (e.g., student record management)",
                "Java", "Expert", 80, 100
            ));
            // Java Multithreading Expert Tasks
            tasks.add(new HardcodedTask(
                "Thread Synchronization",
                "Synchronize multiple threads accessing shared data",
                "Java", "Expert", 60, 70
            ));
            tasks.add(new HardcodedTask(
                "Inter-Thread Communication",
                "Demonstrate inter-thread communication using wait() and notify()",
                "Java", "Expert", 65, 75
            ));
            tasks.add(new HardcodedTask(
                "Producer-Consumer Problem",
                "Create a producer-consumer problem using threads",
                "Java", "Expert", 70, 80
            ));
            // Java Advanced Expert Tasks
            tasks.add(new HardcodedTask(
                "JDBC CRUD Operations",
                "Implement simple CRUD operations using JDBC",
                "Java", "Expert", 80, 100
            ));
            tasks.add(new HardcodedTask(
                "Swing Calculator",
                "Create a GUI calculator using Swing",
                "Java", "Expert", 90, 120
            ));
            tasks.add(new HardcodedTask(
                "Socket Chat System",
                "Implement a chat system using Sockets",
                "Java", "Expert", 100, 150
            ));
            tasks.add(new HardcodedTask(
                "Library Management System",
                "Build a mini project (Library Management System)",
                "Java", "Expert", 120, 180
            ));
            tasks.add(new HardcodedTask(
                "ATM Simulation",
                "Build a mini project (ATM Simulation)",
                "Java", "Expert", 100, 150
            ));
            tasks.add(new HardcodedTask(
                "Inventory Management System",
                "Build a mini project (Inventory System)",
                "Java", "Expert", 110, 160
            ));
        } else if (language.contains("c")) {
            // Comprehensive C Programming Expert/Advanced Tasks
            tasks.add(new HardcodedTask(
                "Prime Numbers in Range",
                "Display all prime numbers between two intervals",
                "C", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Sum of Digits Calculator",
                "Find the sum of digits of a number using while loop",
                "C", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "GCD and LCM Calculator",
                "Find GCD and LCM of two numbers",
                "C", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Pattern Programs",
                "Print pattern programs such as right triangle, pyramid, inverted pyramid, and diamond",
                "C", "Expert", 50, 45
            ));
            tasks.add(new HardcodedTask(
                "Merge Two Arrays",
                "Merge two arrays",
                "C", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Second Largest Element",
                "Find the second largest element in an array",
                "C", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Matrix Multiplication",
                "Perform matrix multiplication",
                "C", "Expert", 50, 45
            ));
            tasks.add(new HardcodedTask(
                "Binary Search Implementation",
                "Implement Binary Search",
                "C", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Character Frequency Counter",
                "Count occurrences of each character in a string",
                "C", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Remove Duplicate Characters",
                "Remove duplicate characters from a string",
                "C", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "First Non-Repeated Character",
                "Find the first non-repeated character",
                "C", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Anagram Checker",
                "Check whether two strings are anagrams",
                "C", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Recursive GCD Function",
                "Write a recursive function to find GCD of two numbers",
                "C", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Armstrong Number Checker",
                "Write a function to check Armstrong number",
                "C", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Recursive Power Function",
                "Write a recursive function to calculate power (xⁿ)",
                "C", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Pointer to Pointer",
                "Demonstrate pointer to pointer",
                "C", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Dynamic Memory Allocation",
                "Perform dynamic memory allocation using malloc() and free()",
                "C", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Employee Records Array",
                "Create an array of structures for employee records",
                "C", "Expert", 50, 45
            ));
            tasks.add(new HardcodedTask(
                "Structure to Function",
                "Pass a structure to a function and display its data",
                "C", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "File Copy Program",
                "Copy contents from one file to another",
                "C", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "File Append Operations",
                "Append data to a file",
                "C", "Expert", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Stack Operations Implementation",
                "Implement stack operations using arrays",
                "C", "Expert", 50, 45
            ));
            tasks.add(new HardcodedTask(
                "Queue Operations Implementation",
                "Implement queue operations using arrays",
                "C", "Expert", 50, 45
            ));
            tasks.add(new HardcodedTask(
                "Linked List Operations",
                "Implement linked list operations (insert, delete, display)",
                "C", "Expert", 60, 60
            ));
            tasks.add(new HardcodedTask(
                "Decimal to Binary Recursion",
                "Convert decimal to binary using recursion",
                "C", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Student Record System",
                "Create a mini project such as Student Record System",
                "C", "Expert", 80, 120
            ));
            tasks.add(new HardcodedTask(
                "Bank Management System",
                "Create a mini project such as Bank Management System",
                "C", "Expert", 90, 150
            ));
        } else if (language.contains("python")) {
            // Comprehensive Python Expert/Advanced Tasks
            tasks.add(new HardcodedTask(
                "Prime Numbers in Range",
                "Display all prime numbers between two intervals",
                "Python", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Sum of Digits Calculator",
                "Find the sum of digits of a number using a while loop",
                "Python", "Expert", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "GCD and LCM Calculator",
                "Find GCD and LCM of two numbers",
                "Python", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Pattern Programs",
                "Print pattern programs (triangle, pyramid, diamond)",
                "Python", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Merge Two Lists",
                "Merge two lists",
                "Python", "Expert", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Second Largest Element",
                "Find the second largest element in a list",
                "Python", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Binary Search Implementation",
                "Implement binary search using lists",
                "Python", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Matrix Operations",
                "Perform matrix addition and subtraction using nested lists",
                "Python", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Character Frequency Counter",
                "Count occurrences of each character in a string",
                "Python", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Remove Duplicate Characters",
                "Remove duplicate characters from a string",
                "Python", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "First Non-Repeated Character",
                "Find the first non-repeated character",
                "Python", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Anagram Checker",
                "Check whether two strings are anagrams",
                "Python", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Recursive GCD Function",
                "Write a recursive function to find GCD of two numbers",
                "Python", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Armstrong Number Checker",
                "Write a function to check Armstrong number",
                "Python", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Recursive Power Function",
                "Write a recursive function to calculate power (xⁿ)",
                "Python", "Expert", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "Remove Duplicates Using Sets",
                "Remove duplicates from a list using sets",
                "Python", "Expert", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "Mathematical Set Operations",
                "Perform mathematical operations on multiple sets",
                "Python", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Student Record Management",
                "Implement a simple student record management system using dictionaries",
                "Python", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "Word Count Program",
                "Create a word-count program for a text",
                "Python", "Expert", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "File Copy Program",
                "Copy contents from one file to another",
                "Python", "Expert", 25, 20
            ));
            tasks.add(new HardcodedTask(
                "File Append Operations",
                "Append data to a file",
                "Python", "Expert", 20, 15
            ));
            tasks.add(new HardcodedTask(
                "CSV File Operations",
                "Read and write CSV files using the csv module",
                "Python", "Expert", 40, 45
            ));
            tasks.add(new HardcodedTask(
                "Custom Exception Class",
                "Create a custom exception class",
                "Python", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Exception Raising and Re-raising",
                "Raise and re-raise exceptions",
                "Python", "Expert", 30, 25
            ));
            tasks.add(new HardcodedTask(
                "Method Overriding and Polymorphism",
                "Demonstrate method overriding and polymorphism",
                "Python", "Expert", 45, 40
            ));
            tasks.add(new HardcodedTask(
                "Abstract Class Implementation",
                "Implement an abstract class using abc module",
                "Python", "Expert", 50, 45
            ));
            tasks.add(new HardcodedTask(
                "Encapsulation and Data Hiding",
                "Demonstrate encapsulation and data hiding",
                "Python", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Package Creation",
                "Create a package containing multiple modules",
                "Python", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Package Import Demo",
                "Use __init__.py and demonstrate package import",
                "Python", "Expert", 35, 30
            ));
            tasks.add(new HardcodedTask(
                "Linked List Implementation",
                "Implement linked list using classes",
                "Python", "Expert", 60, 75
            ));
            tasks.add(new HardcodedTask(
                "OOP Stack and Queue",
                "Implement stack and queue using OOP concepts",
                "Python", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "JSON File Operations",
                "Work with JSON files (json module)",
                "Python", "Expert", 40, 35
            ));
            tasks.add(new HardcodedTask(
                "Database Connection",
                "Connect Python with a database using sqlite3",
                "Python", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "CRUD Operations",
                "Implement a simple CRUD operation (Create, Read, Update, Delete)",
                "Python", "Expert", 60, 75
            ));
            tasks.add(new HardcodedTask(
                "Calculator using Functions",
                "Build a calculator using functions",
                "Python", "Expert", 50, 60
            ));
            tasks.add(new HardcodedTask(
                "To-Do List with File Handling",
                "Create a to-do list using file handling",
                "Python", "Expert", 60, 90
            ));
            tasks.add(new HardcodedTask(
                "Student Management System",
                "Build a complete student management system",
                "Python", "Expert", 80, 120
            ));
            tasks.add(new HardcodedTask(
                "Contact Book using Dictionary",
                "Create a contact book using dictionary",
                "Python", "Expert", 60, 90
            ));
            tasks.add(new HardcodedTask(
                "Simple ATM Simulation",
                "Build a simple ATM simulation",
                "Python", "Expert", 70, 105
            ));
            tasks.add(new HardcodedTask(
                "Quiz Game using Random Module",
                "Create a quiz game using random module",
                "Python", "Expert", 60, 90
            ));
            tasks.add(new HardcodedTask(
                "Library Management System",
                "Build a library management system",
                "Python", "Expert", 90, 150
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

