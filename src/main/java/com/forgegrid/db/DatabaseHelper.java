package com.forgegrid.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database helper class for managing SQLite database connections and initialization.
 * Creates and manages the forgegrid.db database with users table.
 */
public class DatabaseHelper {
    
    private static final String DB_URL = "jdbc:sqlite:forgegrid.db";
    private static DatabaseHelper instance;
    private Connection connection;
    
    /**
     * Private constructor for singleton pattern
     */
    private DatabaseHelper() {
        initializeDatabase();
    }
    
    /**
     * Get singleton instance of DatabaseHelper
     * 
     * @return DatabaseHelper instance
     */
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }
    
    /**
     * Get database connection
     * 
     * @return Connection to SQLite database
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }
    
    /**
     * Initialize the database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection
            connection = DriverManager.getConnection(DB_URL);
            
            // Create users table if it doesn't exist
            createUsersTable();
            
            System.out.println("Database initialized successfully: forgegrid.db");
            
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create the users table with required columns including onboarding fields
     */
    private void createUsersTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                onboarding_completed INTEGER DEFAULT 0,
                onboarding_goal TEXT,
                onboarding_language TEXT,
                onboarding_skill TEXT,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                updated_at TEXT DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Users table created/verified successfully");
            
            // Add onboarding columns to existing tables (migration)
            migrateOnboardingColumns();
        }
    }
    
    /**
     * Migrate existing users table to add onboarding columns if they don't exist
     */
    private void migrateOnboardingColumns() {
        try (Statement statement = connection.createStatement()) {
            // Check if onboarding_completed column exists
            try {
                statement.execute("SELECT onboarding_completed FROM users LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("Adding onboarding columns to existing users table...");
                statement.execute("ALTER TABLE users ADD COLUMN onboarding_completed INTEGER DEFAULT 0");
                statement.execute("ALTER TABLE users ADD COLUMN onboarding_goal TEXT");
                statement.execute("ALTER TABLE users ADD COLUMN onboarding_language TEXT");
                statement.execute("ALTER TABLE users ADD COLUMN onboarding_skill TEXT");
                System.out.println("Onboarding columns added successfully");
            }
            
            // Check if created_at column exists (separate check for timestamp columns)
            try {
                statement.execute("SELECT created_at FROM users LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add timestamp columns
                System.out.println("Adding timestamp columns to existing users table...");
                statement.execute("ALTER TABLE users ADD COLUMN created_at TEXT");
                statement.execute("ALTER TABLE users ADD COLUMN updated_at TEXT");
                System.out.println("Timestamp columns added successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error during migration: " + e.getMessage());
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Test database connection
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection testConn = getConnection();
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
