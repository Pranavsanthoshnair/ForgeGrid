package com.forgegrid.service;

import com.forgegrid.db.DatabaseHelper;
import com.forgegrid.model.PlayerProfile;
import java.sql.*;

/**
 * Service for handling XP and level progression
 */
public class LevelService {
    
    private DatabaseHelper dbHelper;
    
    public LevelService() {
        this.dbHelper = DatabaseHelper.getInstance();
    }
    
    /**
     * Calculate required XP to reach the next level
     * Formula: XP for Level N = 100 * (1.5^(N-1))
     * 
     * Level 1 → Level 2: 100 XP
     * Level 2 → Level 3: 150 XP
     * Level 3 → Level 4: 225 XP
     * Level 4 → Level 5: 337.5 ≈ 338 XP
     * etc.
     */
    public static int getRequiredXPForLevel(int level) {
        if (level <= 1) return 0;
        // XP to go from level (N-1) to level N
        return (int) Math.round(100 * Math.pow(1.5, level - 2));
    }
    
    /**
     * Get total XP required to reach a specific level from level 1
     */
    public static int getTotalXPForLevel(int level) {
        if (level <= 1) return 0;
        
        int total = 0;
        for (int i = 2; i <= level; i++) {
            total += getRequiredXPForLevel(i);
        }
        return total;
    }
    
    /**
     * Calculate current level from total XP
     */
    public static int calculateLevelFromXP(int totalXP) {
        if (totalXP <= 0) return 1;
        
        int level = 1;
        int xpAccumulated = 0;
        
        // Keep checking if user has enough XP for next level
        while (true) {
            int xpForNextLevel = getRequiredXPForLevel(level + 1);
            if (xpAccumulated + xpForNextLevel > totalXP) {
                // Not enough XP for next level
                break;
            }
            xpAccumulated += xpForNextLevel;
            level++;
            
            // Safety check to prevent infinite loop
            if (level > 100) break;
        }
        
        return level;
    }
    
    /**
     * Get current XP progress within current level
     * (XP earned towards the next level)
     */
    public static int getCurrentLevelXP(int totalXP) {
        int level = calculateLevelFromXP(totalXP);
        int xpForCurrentLevel = getTotalXPForLevel(level);
        return totalXP - xpForCurrentLevel;
    }
    
    /**
     * Add XP and check for level up
     * Returns new level if leveled up, otherwise returns current level
     */
    public LevelUpResult addXP(String username, int xpToAdd) {
        try (Connection conn = dbHelper.getConnection()) {
            // Get current XP and level
            String selectSQL = "SELECT total_xp, level FROM users WHERE username = ?";
            int currentTotalXP = 0;
            int currentLevel = 1;
            
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentTotalXP = rs.getInt("total_xp");
                    currentLevel = rs.getInt("level");
                }
            }
            
            // Add new XP
            int newTotalXP = Math.max(0, currentTotalXP + xpToAdd);
            int newLevel = calculateLevelFromXP(newTotalXP);
            boolean leveledUp = newLevel > currentLevel;
            
            // Update database
            String updateSQL = "UPDATE users SET total_xp = ?, level = ?, updated_at = ? WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, newTotalXP);
                pstmt.setInt(2, newLevel);
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(4, username);
                pstmt.executeUpdate();
            }
            
            // Calculate XP within current level
            int currentLevelXP = getCurrentLevelXP(newTotalXP);
            int requiredForNextLevel = getRequiredXPForLevel(newLevel + 1);
            
            return new LevelUpResult(
                leveledUp,
                currentLevel,
                newLevel,
                newTotalXP,
                currentLevelXP,
                requiredForNextLevel
            );
            
        } catch (SQLException e) {
            System.err.println("Error adding XP: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get user's current level info
     */
    public LevelInfo getLevelInfo(String username) {
        try (Connection conn = dbHelper.getConnection()) {
            String selectSQL = "SELECT total_xp, level FROM users WHERE username = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int totalXP = rs.getInt("total_xp");
                    int level = rs.getInt("level");
                    int currentLevelXP = getCurrentLevelXP(totalXP);
                    int requiredForNextLevel = getRequiredXPForLevel(level + 1);
                    
                    return new LevelInfo(level, totalXP, currentLevelXP, requiredForNextLevel);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting level info: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new LevelInfo(1, 0, 0, getRequiredXPForLevel(2));
    }
    
    /**
     * Result of adding XP (contains level up info)
     */
    public static class LevelUpResult {
        public final boolean leveledUp;
        public final int oldLevel;
        public final int newLevel;
        public final int totalXP;
        public final int currentLevelXP;
        public final int requiredForNextLevel;
        
        public LevelUpResult(boolean leveledUp, int oldLevel, int newLevel, int totalXP, 
                            int currentLevelXP, int requiredForNextLevel) {
            this.leveledUp = leveledUp;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
            this.totalXP = totalXP;
            this.currentLevelXP = currentLevelXP;
            this.requiredForNextLevel = requiredForNextLevel;
        }
    }
    
    /**
     * Current level information
     */
    public static class LevelInfo {
        public final int level;
        public final int totalXP;
        public final int currentLevelXP;
        public final int requiredForNextLevel;
        
        public LevelInfo(int level, int totalXP, int currentLevelXP, int requiredForNextLevel) {
            this.level = level;
            this.totalXP = totalXP;
            this.currentLevelXP = currentLevelXP;
            this.requiredForNextLevel = requiredForNextLevel;
        }
        
        public int getProgressPercentage() {
            if (requiredForNextLevel == 0) return 100;
            return (int) ((currentLevelXP * 100.0) / requiredForNextLevel);
        }
    }
}

