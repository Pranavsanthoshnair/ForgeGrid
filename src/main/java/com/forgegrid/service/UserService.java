package com.forgegrid.service;

import com.forgegrid.db.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service class for managing user-related database operations.
 * Handles onboarding data persistence and retrieval.
 */
public class UserService {
    
    private final DatabaseHelper dbHelper;
    
    /**
     * Constructor for UserService
     */
    public UserService() {
        this.dbHelper = DatabaseHelper.getInstance();
    }
    
    /**
     * Check if a user has completed onboarding
     * 
     * @param userId User ID
     * @return true if onboarding is completed, false otherwise
     */
    public boolean hasCompletedOnboarding(int userId) {
        String selectSQL = "SELECT onboarding_completed FROM users WHERE id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("onboarding_completed") == 1;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking onboarding status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Save onboarding data for a user
     * 
     * @param userId User ID
     * @param goal Primary goal selected during onboarding
     * @param language Preferred programming language
     * @param skill Current skill level
     * @return true if save successful, false otherwise
     */
    public boolean saveOnboardingData(int userId, String goal, String language, String skill) {
        String updateSQL = """
            UPDATE users 
            SET onboarding_completed = 1,
                onboarding_goal = ?,
                onboarding_language = ?,
                onboarding_skill = ?,
                updated_at = ?
            WHERE id = ?
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setString(1, goal);
            pstmt.setString(2, language);
            pstmt.setString(3, skill);
            pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setInt(5, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Onboarding data saved successfully for user ID: " + userId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving onboarding data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Save onboarding data using username
     * 
     * @param username Username
     * @param goal Primary goal selected during onboarding
     * @param language Preferred programming language
     * @param skill Current skill level
     * @return true if save successful, false otherwise
     */
    public boolean saveOnboardingDataByUsername(String username, String goal, String language, String skill) {
        String updateSQL = """
            UPDATE users 
            SET onboarding_completed = 1,
                onboarding_goal = ?,
                onboarding_language = ?,
                onboarding_skill = ?,
                updated_at = ?
            WHERE username = ?
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setString(1, goal);
            pstmt.setString(2, language);
            pstmt.setString(3, skill);
            pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(5, username);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Onboarding data saved successfully for username: " + username);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving onboarding data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get onboarding data for a user
     * 
     * @param userId User ID
     * @return Array containing [goal, language, skill], or null if not found
     */
    public String[] getOnboardingData(int userId) {
        String selectSQL = """
            SELECT onboarding_goal, onboarding_language, onboarding_skill 
            FROM users 
            WHERE id = ?
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("onboarding_goal"),
                        rs.getString("onboarding_language"),
                        rs.getString("onboarding_skill")
                    };
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving onboarding data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get onboarding data by username
     * 
     * @param username Username
     * @return Array containing [goal, language, skill], or null if not found
     */
    public String[] getOnboardingDataByUsername(String username) {
        String selectSQL = """
            SELECT onboarding_goal, onboarding_language, onboarding_skill 
            FROM users 
            WHERE username = ?
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("onboarding_goal"),
                        rs.getString("onboarding_language"),
                        rs.getString("onboarding_skill")
                    };
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving onboarding data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Check if a user has completed onboarding by username
     * 
     * @param username Username
     * @return true if onboarding is completed, false otherwise
     */
    public boolean hasCompletedOnboardingByUsername(String username) {
        String selectSQL = "SELECT onboarding_completed FROM users WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("onboarding_completed") == 1;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking onboarding status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update user's last login time
     * 
     * @param username Username
     * @return true if update successful, false otherwise
     */
    public boolean updateLastLogin(String username) {
        String updateSQL = "UPDATE users SET updated_at = ? WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get user ID by username
     * 
     * @param username Username
     * @return User ID, or -1 if not found
     */
    public int getUserIdByUsername(String username) {
        String selectSQL = "SELECT id FROM users WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
}
