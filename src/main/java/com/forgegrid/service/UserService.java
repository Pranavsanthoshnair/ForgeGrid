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
     * Save user preferences (post-dashboard customization data) to user_preferences table
     * 
     * @param username Username
     * @param experienceLevel Experience level preference
     * @param workStyle Work style preference
     * @param productivityGoals Productivity goals preference
     * @param notificationPreference Notification preference
     * @return true if save successful, false otherwise
     */
    public boolean saveUserPreferences(String username, String experienceLevel, String workStyle, 
                                     String productivityGoals, String notificationPreference) {
        String insertOrUpdateSQL = """
            INSERT INTO user_preferences (username, experience_level, work_style, productivity_goals, notification_preference, customize_completed, updated_at)
            VALUES (?, ?, ?, ?, ?, TRUE, ?)
            ON DUPLICATE KEY UPDATE
                experience_level = VALUES(experience_level),
                work_style = VALUES(work_style),
                productivity_goals = VALUES(productivity_goals),
                notification_preference = VALUES(notification_preference),
                customize_completed = TRUE,
                updated_at = VALUES(updated_at)
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertOrUpdateSQL)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, experienceLevel);
            pstmt.setString(3, workStyle);
            pstmt.setString(4, productivityGoals);
            pstmt.setString(5, notificationPreference);
            pstmt.setString(6, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if user has completed customization
     * 
     * @param username Username to check
     * @return true if customization completed, false otherwise
     */
    public boolean hasCompletedCustomization(String username) {
        String query = "SELECT customize_completed FROM user_preferences WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("customize_completed");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get user preferences (post-dashboard customization data) by username
     * 
     * @param username Username
     * @return Array containing [experience_level, work_style, productivity_goals, notification_preference], or null if not found
     */
    public String[] getUserPreferences(String username) {
        String selectSQL = """
            SELECT experience_level, work_style, productivity_goals, notification_preference 
            FROM user_preferences 
            WHERE username = ?
            """;
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("experience_level"),
                        rs.getString("work_style"),
                        rs.getString("productivity_goals"),
                        rs.getString("notification_preference")
                    };
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user preferences: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Check if user has saved preferences (post-dashboard customization data)
     * 
     * @param username Username
     * @return true if preferences exist, false otherwise
     */
    public boolean hasUserPreferences(String username) {
        String selectSQL = "SELECT COUNT(*) FROM user_preferences WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking user preferences: " + e.getMessage());
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
    
    /**
     * Get user profile details including onboarding information
     * 
     * @param username Username
     * @return Map with keys: email, onboarding_language, onboarding_skill, notification_preference
     */
    public java.util.Map<String, String> getUserProfileDetails(String username) {
        java.util.Map<String, String> details = new java.util.HashMap<>();
        
        // Get user data from users table
        String userQuery = "SELECT email, onboarding_language, onboarding_skill FROM users WHERE username = ?";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(userQuery)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                details.put("email", rs.getString("email"));
                details.put("onboarding_language", rs.getString("onboarding_language"));
                details.put("onboarding_skill", rs.getString("onboarding_skill"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Get notification preference from user_preferences table
        String prefQuery = "SELECT notification_preference FROM user_preferences WHERE username = ?";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(prefQuery)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                details.put("notification_preference", rs.getString("notification_preference"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return details;
    }
    
    /**
     * Update user profile details
     * 
     * @param username Username
     * @param email Email address
     * @param language Preferred programming language
     * @param skillLevel Skill level
     * @param notificationTime Preferred notification time
     * @return true if update successful, false otherwise
     */
    public boolean updateUserProfileDetails(String username, String email, String language, String skillLevel, String notificationTime) {
        // Update users table
        String updateUserSQL = "UPDATE users SET email = ?, onboarding_language = ?, onboarding_skill = ? WHERE username = ?";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateUserSQL)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, language);
            pstmt.setString(3, skillLevel);
            pstmt.setString(4, username);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // Update user_preferences table
        String updatePrefSQL = """
            INSERT INTO user_preferences (username, notification_preference) 
            VALUES (?, ?) 
            ON DUPLICATE KEY UPDATE notification_preference = ?
            """;
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updatePrefSQL)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, notificationTime);
            pstmt.setString(3, notificationTime);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * Update user's score
     * 
     * @param username Username
     * @param newScore New score value
     * @return true if update successful, false otherwise
     */
    public boolean updateUserScore(String username, int newScore) {
        String updateSQL = "UPDATE users SET score = ?, updated_at = ? WHERE username = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setInt(1, newScore);
            pstmt.setString(2, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(3, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user score: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
