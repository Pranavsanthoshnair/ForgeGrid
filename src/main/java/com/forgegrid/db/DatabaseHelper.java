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
        System.out.println("=== RAILWAY MYSQL MIGRATION ===");
        System.out.println("Loading Railway MySQL configuration...");
        
        // Load configuration from environment variables
        this.dbHost = EnvironmentConfig.getRailwayHost();
        this.dbPort = EnvironmentConfig.getRailwayPort();
        this.dbName = EnvironmentConfig.getRailwayDatabase();
        this.dbUsername = EnvironmentConfig.getRailwayUsername();
        this.dbPassword = EnvironmentConfig.getRailwayPassword();
        
        // Log configuration (without sensitive data)
        System.out.println("Railway Host: " + dbHost);
        System.out.println("Railway Port: " + dbPort);
        System.out.println("Railway Database: " + dbName);
        System.out.println("Railway Username: " + dbUsername);
        System.out.println("Railway Password: " + (dbPassword.isEmpty() ? "[NOT SET]" : "[SET]"));
        
        if (EnvironmentConfig.isRailwayConfigured()) {
            System.out.println("✅ Railway configuration detected");
        } else {
            System.out.println("⚠️  No Railway configuration found, using defaults");
        }
        
        // Extract credentials from Railway URL if available
        extractCredentialsFromUrl();
        
        System.out.println("=== END RAILWAY MIGRATION ===");
    }
    
    /**
     * Extract credentials from Railway URL if needed
     */
    private void extractCredentialsFromUrl() {
        String railwayUrl = EnvironmentConfig.get("RAILWAY_MYSQL_URL");
        if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
            try {
                // Parse Railway URL: mysql://user:pass@host:port/database
                String url = railwayUrl.substring(8); // Remove mysql://
                int atIndex = url.indexOf('@');
                if (atIndex != -1) {
                    String credentials = url.substring(0, atIndex);
                    int colonIndex = credentials.indexOf(':');
                    if (colonIndex != -1) {
                        this.dbUsername = credentials.substring(0, colonIndex);
                        this.dbPassword = credentials.substring(colonIndex + 1);
                        System.out.println("Extracted credentials from Railway URL");
                        System.out.println("Username: " + dbUsername);
                        System.out.println("Password: [EXTRACTED]");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error extracting credentials from Railway URL: " + e.getMessage());
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
        // Try to get the full URL from environment first
        String fullUrl = EnvironmentConfig.getRailwayUrl();
        if (fullUrl != null && !fullUrl.isEmpty()) {
            System.out.println("Using Railway connection URL from environment");
            return fullUrl;
        }
        
        // Build URL from components
        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                                 dbHost, dbPort, dbName);
        System.out.println("Built Railway connection URL: " + url.replaceAll("password=[^&]*", "password=[PASSWORD]"));
        return url;
    }
    
    /**
     * Get database connection
     * 
     * @return Connection to Railway MySQL database
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Establishing connection to Railway MySQL...");
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("✅ Successfully connected to Railway MySQL");
        }
        return connection;
    }
    
    /**
     * Initialize the Railway MySQL database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            System.out.println("=== INITIALIZING RAILWAY MYSQL DATABASE ===");
            
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC driver loaded");
            
            // Create connection to Railway MySQL
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("✅ Connected to Railway MySQL database: " + dbName);
            
            // Create users table if it doesn't exist
            createUsersTable();
            
            // Create user_preferences table if it doesn't exist
            createUserPreferencesTable();
            
            System.out.println("✅ Railway MySQL database initialized successfully");
            System.out.println("=== END DATABASE INITIALIZATION ===");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error initializing Railway MySQL database: " + e.getMessage());
            System.err.println("Connection URL: " + dbUrl.replace(dbPassword, "[PASSWORD]"));
            e.printStackTrace();
        }
    }

    /**
     * Test Railway MySQL database connection
     * 
     * @return true if database is accessible, false otherwise
     */
    public boolean testConnection() {
        try {
            System.out.println("Testing Railway MySQL connection...");
            Connection testConn = getConnection();
            boolean isConnected = testConn != null && !testConn.isClosed();
            
            if (isConnected) {
                System.out.println("✅ Railway MySQL connection test successful");
            } else {
                System.out.println("❌ Railway MySQL connection test failed");
            }
            
            return isConnected;
        } catch (SQLException e) {
            System.err.println("❌ Railway MySQL connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create the users table with required columns including onboarding fields
     */
    private void createUsersTable() throws SQLException {
        System.out.println("Creating/verifying users table in Railway MySQL...");
        
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
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
            System.out.println("✅ Users table created/verified successfully in Railway MySQL");
            
            // Create indexes for better performance
            createIndexes();
        }
    }
    
    /**
     * Create the user_preferences table for post-dashboard customization data
     */
    private void createUserPreferencesTable() throws SQLException {
        System.out.println("Creating/verifying user_preferences table in Railway MySQL...");
        
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS user_preferences (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                experience_level VARCHAR(255) NULL,
                work_style VARCHAR(255) NULL,
                productivity_goals VARCHAR(255) NULL,
                notification_preference VARCHAR(255) NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("✅ User preferences table created/verified successfully in Railway MySQL");
        }
    }
    
    /**
     * Create indexes for better performance
     */
    private void createIndexes() {
        try (Statement statement = connection.createStatement()) {
            System.out.println("Creating/verifying database indexes in Railway MySQL...");
            
            // Create indexes if they don't exist (MySQL syntax)
            try {
                statement.execute("CREATE INDEX idx_users_username ON users(username)");
            } catch (SQLException e) {
                // Index might already exist, ignore error
            }
            
            try {
                statement.execute("CREATE INDEX idx_users_email ON users(email)");
            } catch (SQLException e) {
                // Index might already exist, ignore error
            }
            
            try {
                statement.execute("CREATE INDEX idx_users_onboarding_completed ON users(onboarding_completed)");
            } catch (SQLException e) {
                // Index might already exist, ignore error
            }
            
            try {
                statement.execute("CREATE INDEX idx_user_preferences_username ON user_preferences(username)");
            } catch (SQLException e) {
                // Index might already exist, ignore error
            }
            
            System.out.println("✅ Database indexes created/verified successfully in Railway MySQL");
        } catch (SQLException e) {
            System.err.println("❌ Error creating indexes in Railway MySQL: " + e.getMessage());
        }
    }
    
    /**
     * Close Railway MySQL database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Railway MySQL database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing Railway MySQL database connection: " + e.getMessage());
        }
    }
    
}
