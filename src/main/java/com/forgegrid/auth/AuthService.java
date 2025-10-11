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
 * Authentication service using MySQL database for user management.
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
     * Register a new user with username, email and password
     * 
     * @param username User's username (must be unique)
     * @param email User's email (must be unique)
     * @param password User's password (will be hashed with SHA-256)
     * @return true if registration successful, false if username/email already exists
     */
    public boolean register(String username, String email, String password) {
        if (username == null || username.trim().isEmpty() || 
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Check if username matches any existing email OR email matches any existing username
        if (usernameOrEmailExists(username) || usernameOrEmailExists(email)) {
            System.out.println("Username or email already exists in the system");
            return false;
        }
        
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return false;
        }
        
        String insertSQL = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, email.trim());
            pstmt.setString(3, hashedPassword);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Check if it's a unique constraint violation (username or email already exists)
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                if (e.getMessage().contains("username")) {
                    System.out.println("Username already exists: " + username);
                } else if (e.getMessage().contains("email")) {
                    System.out.println("Email already exists: " + email);
                }
                return false;
            }
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Login a user with username/email and password
     * 
     * @param usernameOrEmail User's username or email
     * @param password User's password
     * @return PlayerProfile if login successful, null if credentials are invalid
     */
    public PlayerProfile login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return null;
        }
        
        String selectSQL = """
            SELECT id, username, email, onboarding_completed, onboarding_goal, 
                   onboarding_language, onboarding_skill 
            FROM users 
            WHERE (username = ? OR email = ?) AND password = ?
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, usernameOrEmail.trim());
            pstmt.setString(2, usernameOrEmail.trim());
            pstmt.setString(3, hashedPassword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String dbEmail = rs.getString("email");
                    boolean onboardingCompleted = rs.getInt("onboarding_completed") == 1;
                    String onboardingGoal = rs.getString("onboarding_goal");
                    String onboardingLanguage = rs.getString("onboarding_language");
                    String onboardingSkill = rs.getString("onboarding_skill");
                    
                    // Create and return a PlayerProfile for the authenticated user
                    PlayerProfile profile = createPlayerProfile(userId, dbUsername, dbEmail);
                    profile.setOnboardingCompleted(onboardingCompleted);
                    profile.setOnboardingGoal(onboardingGoal);
                    profile.setOnboardingLanguage(onboardingLanguage);
                    profile.setOnboardingSkill(onboardingSkill);
                    
                    return profile;
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
     * Check if a value exists as either a username OR email
     * This ensures that usernames and emails are globally unique
     * (e.g., if "john@email.com" is a username, it can't also be someone's email)
     * 
     * @param value Value to check (could be username or email)
     * @return true if value exists as username OR email, false otherwise
     */
    public boolean usernameOrEmailExists(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        String selectSQL = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, value.trim());
            pstmt.setString(2, value.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username/email existence: " + e.getMessage());
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
     * @param email Email
     * @return PlayerProfile with default values
     */
    private PlayerProfile createPlayerProfile(int userId, String username, String email) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUsername(username);
        profile.setEmail(email);
        profile.setLevel(1);
        profile.setScore(0); // Use score instead of XP
        profile.setLastLogin(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return profile;
    }
    
    /**
     * Reset password for a user
     * 
     * @param username Username to reset password for
     * @param newPassword New password
     * @return true if password reset successful, false if user not found
     */
    public boolean resetPassword(String username, String newPassword) {
        if (username == null || username.trim().isEmpty() || 
            newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            return false;
        }
        
        String updateSQL = "UPDATE users SET password = ? WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, username.trim());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
