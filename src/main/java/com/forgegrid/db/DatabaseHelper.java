package com.forgegrid.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database helper class for managing MySQL database connections and initialization.
 * Creates and manages the forgegrid database with users table.
 */
public class DatabaseHelper {
    
    private String dbUrl;
    private static DatabaseHelper instance;
    private Connection connection;
    
    // MySQL connection parameters
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "forgegrid";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    
    /**
     * Private constructor for singleton pattern
     */
    private DatabaseHelper() {
        this.dbUrl = buildMySQLUrl();
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
     * Build MySQL JDBC URL
     * 
     * @return MySQL JDBC URL
     */
    private String buildMySQLUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                           DB_HOST, DB_PORT, DB_NAME);
    }
    
    /**
     * Get database connection
     * 
     * @return Connection to MySQL database
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, DB_USERNAME, DB_PASSWORD);
        }
        return connection;
    }
    
    /**
     * Initialize the database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection
            connection = DriverManager.getConnection(dbUrl, DB_USERNAME, DB_PASSWORD);
            
            // Create users table if it doesn't exist
            createUsersTable();
            
            System.out.println("MySQL database initialized successfully: forgegrid");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error initializing MySQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test database connection
     * 
     * @return true if database is accessible, false otherwise
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
            System.out.println("Users table created/verified successfully");
            
            // Create indexes for better performance
            createIndexes();
        }
    }
    
    /**
     * Create indexes for better performance
     */
    private void createIndexes() {
        try (Statement statement = connection.createStatement()) {
            // Create indexes if they don't exist
            statement.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_users_onboarding_completed ON users(onboarding_completed)");
            System.out.println("Database indexes created/verified successfully");
        } catch (SQLException e) {
            System.err.println("Error creating indexes: " + e.getMessage());
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
    
}
