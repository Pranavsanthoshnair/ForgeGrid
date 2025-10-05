package com.forgegrid.auth;

import com.forgegrid.db.DatabaseHelper;
import com.forgegrid.model.PlayerProfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Authentication service using SQLite database for user management.
 * Provides registration and login functionality with SHA-256 password hashing.
 */
public class AuthService {
    
    private final DatabaseHelper dbHelper;
    
    /**
     * Constructor for AuthService
     */
    public AuthService() {
        this.dbHelper = DatabaseHelper.getInstance();
    }
    
    /**
     * Register a new user with username and password
     * 
     * @param username User's username (must be unique)
     * @param password User's password (will be hashed with SHA-256)
     * @return true if registration successful, false if username already exists
     */
    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return false;
        }
        
        String insertSQL = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, hashedPassword);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Check if it's a unique constraint violation (username already exists)
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Username already exists: " + username);
                return false;
            }
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Login a user with username and password
     * 
     * @param username User's username
     * @param password User's password
     * @return PlayerProfile if login successful, null if credentials are invalid
     */
    public PlayerProfile login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return null;
        }
        
        String selectSQL = "SELECT id, username FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, hashedPassword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    
                    // Create and return a PlayerProfile for the authenticated user
                    return createPlayerProfile(userId, dbUsername);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Check if a username already exists
     * 
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        String selectSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Hash password using SHA-256 algorithm
     * 
     * @param password Plain text password
     * @return Hashed password as hex string, or null if hashing fails
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not available: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a PlayerProfile for the authenticated user
     * 
     * @param userId User ID from database
     * @param username Username
     * @return PlayerProfile with default values
     */
    private PlayerProfile createPlayerProfile(int userId, String username) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUsername(username);
        profile.setEmail(username); // Use username as email for simplicity
        profile.setLevel(1);
        profile.setScore(0); // Use score instead of XP
        profile.setLastLogin(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return profile;
    }
    
    /**
     * Test database connection
     * 
     * @return true if database is accessible, false otherwise
     */
    public boolean testConnection() {
        return dbHelper.testConnection();
    }
}
