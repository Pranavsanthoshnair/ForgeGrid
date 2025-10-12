package com.forgegrid.db;

import com.forgegrid.config.EnvironmentConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database helper class for managing MySQL database connections and initialization.
 * Creates and manages the forgegrid database with users table.
 * 
 * MIGRATION NOTE: This class has been updated to support Railway MySQL hosting
 * instead of local XAMPP MySQL. The connection parameters are now loaded from
 * environment variables (.env file or system environment) for security.
 * 
 * Changes made:
 * - Replaced hardcoded localhost connection with Railway MySQL connection
 * - Added environment variable support for credentials
 * - Updated connection URL format for Railway's SSL requirements
 * - Added comprehensive logging for migration tracking
 */
public class DatabaseHelper {
    
    private String dbUrl;
    private static DatabaseHelper instance;
    private Connection connection;
    
    // Railway MySQL connection parameters (loaded from environment)
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;
    
    /**
     * Private constructor for singleton pattern
     */
    private DatabaseHelper() {
        loadRailwayConfiguration();
        this.dbUrl = buildRailwayMySQLUrl();
        initializeDatabase();
    }
    
    /**
     * Load Railway MySQL configuration from environment variables
     */
    private void loadRailwayConfiguration() {
        this.dbHost = EnvironmentConfig.getRailwayHost();
        this.dbPort = EnvironmentConfig.getRailwayPort();
        this.dbName = EnvironmentConfig.getRailwayDatabase();
        this.dbUsername = EnvironmentConfig.getRailwayUsername();
        this.dbPassword = EnvironmentConfig.getRailwayPassword();
        extractCredentialsFromUrl();
    }
    
    /**
     * Extract credentials from Railway URL if needed
     */
    private void extractCredentialsFromUrl() {
        String railwayUrl = EnvironmentConfig.get("RAILWAY_MYSQL_URL");
        if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
            try {
                String url = railwayUrl.substring(8);
                int atIndex = url.indexOf('@');
                if (atIndex != -1) {
                    String credentials = url.substring(0, atIndex);
                    int colonIndex = credentials.indexOf(':');
                    if (colonIndex != -1) {
                        this.dbUsername = credentials.substring(0, colonIndex);
                        this.dbPassword = credentials.substring(colonIndex + 1);
                    }
                }
            } catch (Exception e) {
                // Silently fail - use environment variables instead
            }
        }
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
     * Build Railway MySQL JDBC URL
     * 
     * @return Railway MySQL JDBC URL
     */
    private String buildRailwayMySQLUrl() {
        String fullUrl = EnvironmentConfig.getRailwayUrl();
        if (fullUrl != null && !fullUrl.isEmpty()) {
            return fullUrl;
        }
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                           dbHost, dbPort, dbName);
    }
    
    /**
     * Get database connection
     * 
     * @return Connection to Railway MySQL database
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        }
        return connection;
    }
    
    /**
     * Initialize the Railway MySQL database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            createUsersTable();
            createUserPreferencesTable();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Test Railway MySQL database connection
     * 
     * @return true if database is accessible, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection testConn = getConnection();
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Create the users table with required columns including onboarding fields
     */
    private void createUsersTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                total_xp INT DEFAULT 0,
                level INT DEFAULT 1,
                onboarding_completed TINYINT(1) DEFAULT 0,
                onboarding_goal VARCHAR(255) NULL,
                onboarding_language VARCHAR(255) NULL,
                onboarding_skill VARCHAR(255) NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            migrateUsersTableForXP();
            createIndexes();
        }
    }
    
    /**
     * Migrate users table to add total_xp and level columns if they don't exist
     */
    private void migrateUsersTableForXP() {
        try (Statement statement = connection.createStatement()) {
            try {
                statement.execute("ALTER TABLE users ADD COLUMN total_xp INT DEFAULT 0");
                System.out.println("✓ Added total_xp column to users table");
            } catch (SQLException e) {
                // Column already exists
            }
            
            try {
                statement.execute("ALTER TABLE users ADD COLUMN level INT DEFAULT 1");
                System.out.println("✓ Added level column to users table");
            } catch (SQLException e) {
                // Column already exists
            }
        } catch (SQLException e) {
            System.err.println("Error migrating users table: " + e.getMessage());
        }
    }
    
    /**
     * Create the user_preferences table for post-dashboard customization data
     */
    private void createUserPreferencesTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS user_preferences (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                experience_level VARCHAR(255) NULL,
                work_style VARCHAR(255) NULL,
                productivity_goals VARCHAR(255) NULL,
                notification_preference VARCHAR(255) NULL,
                customize_completed BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        }
    }
    
    /**
     * Create indexes for better performance
     */
    private void createIndexes() {
        try (Statement statement = connection.createStatement()) {
            try { statement.execute("CREATE INDEX idx_users_username ON users(username)"); } catch (SQLException e) {}
            try { statement.execute("CREATE INDEX idx_users_email ON users(email)"); } catch (SQLException e) {}
            try { statement.execute("CREATE INDEX idx_users_onboarding_completed ON users(onboarding_completed)"); } catch (SQLException e) {}
            try { statement.execute("CREATE INDEX idx_user_preferences_username ON user_preferences(username)"); } catch (SQLException e) {}
        } catch (SQLException e) {
            // Silently fail - indexes not critical
        }
        migrateUserPreferencesTable();
    }
    
    /**
     * Migrate user_preferences table to add customize_completed column if it doesn't exist
     */
    private void migrateUserPreferencesTable() {
        try (Statement statement = connection.createStatement()) {
            // Check if customize_completed column exists
            try {
                statement.executeQuery("SELECT customize_completed FROM user_preferences LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                try {
                    statement.execute("ALTER TABLE user_preferences ADD COLUMN customize_completed BOOLEAN DEFAULT FALSE");
                } catch (SQLException ex) {
                    // Silently fail if already exists
                }
            }
        } catch (SQLException e) {
            // Silently fail
        }
    }
    
    /**
     * Close Railway MySQL database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Silently fail
        }
    }
    
}
