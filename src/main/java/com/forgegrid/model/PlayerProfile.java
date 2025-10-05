package com.forgegrid.model;

// Gson imports removed - using SQLite instead of JSON

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PlayerProfile represents a user's game profile data.
 * This class handles both online (Supabase) and offline (local) data storage.
 * 
 * Fields are mapped to match Supabase profiles table structure.
 */
public class PlayerProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // User profile fields
    private String id; // User ID
    private String username;
    private String email;
    private String fullName;
    private int score;
    private int level;
    private String achievements; // JSON string of achievements
    private String lastLogin; // ISO 8601 format
    private String createdAt;
    private String updatedAt;
    
    // Onboarding fields
    private boolean onboardingCompleted;
    private String onboardingGoal;
    private String onboardingLanguage;
    private String onboardingSkill;
    
    // Local-only fields
    private String localPasswordHash; // Stored locally for offline auth
    private transient boolean isOnline; // Whether this profile is from online sync
    private transient LocalDateTime lastSyncTime; // When this was last synced with Supabase
    
    /**
     * Default constructor for JSON deserialization
     */
    public PlayerProfile() {
        this.score = 0;
        this.level = 1;
        this.achievements = "[]";
        this.isOnline = false;
        this.lastSyncTime = LocalDateTime.now();
    }
    
    /**
     * Constructor for creating a new profile
     */
    public PlayerProfile(String username, String email, String fullName) {
        this();
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public String getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getLocalPasswordHash() {
        return localPasswordHash;
    }
    
    public void setLocalPasswordHash(String localPasswordHash) {
        this.localPasswordHash = localPasswordHash;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public void setOnline(boolean online) {
        isOnline = online;
    }
    
    public LocalDateTime getLastSyncTime() {
        return lastSyncTime;
    }
    
    public void setLastSyncTime(LocalDateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
    
    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }
    
    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }
    
    public String getOnboardingGoal() {
        return onboardingGoal;
    }
    
    public void setOnboardingGoal(String onboardingGoal) {
        this.onboardingGoal = onboardingGoal;
    }
    
    public String getOnboardingLanguage() {
        return onboardingLanguage;
    }
    
    public void setOnboardingLanguage(String onboardingLanguage) {
        this.onboardingLanguage = onboardingLanguage;
    }
    
    public String getOnboardingSkill() {
        return onboardingSkill;
    }
    
    public void setOnboardingSkill(String onboardingSkill) {
        this.onboardingSkill = onboardingSkill;
    }
    
    /**
     * Updates the last login time to now
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.updatedAt = this.lastLogin;
    }
    
    /**
     * Updates the score and level based on game progress
     */
    public void updateScore(int newScore) {
        this.score = newScore;
        this.level = calculateLevel(newScore);
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * Calculates level based on score (simple formula: level = score / 1000 + 1)
     */
    private int calculateLevel(int score) {
        return Math.max(1, score / 1000 + 1);
    }
    
    // JSON methods removed - using SQLite instead of JSON files
    
    /**
     * Creates a copy of this profile for local storage
     * (excludes online-only fields)
     */
    public PlayerProfile createLocalCopy() {
        PlayerProfile localCopy = new PlayerProfile();
        localCopy.username = this.username;
        localCopy.email = this.email;
        localCopy.fullName = this.fullName;
        localCopy.score = this.score;
        localCopy.level = this.level;
        localCopy.achievements = this.achievements;
        localCopy.lastLogin = this.lastLogin;
        localCopy.createdAt = this.createdAt;
        localCopy.updatedAt = this.updatedAt;
        localCopy.localPasswordHash = this.localPasswordHash;
        localCopy.isOnline = false;
        localCopy.lastSyncTime = LocalDateTime.now();
        return localCopy;
    }
    
    @Override
    public String toString() {
        return "PlayerProfile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", score=" + score +
                ", level=" + level +
                ", isOnline=" + isOnline +
                ", lastSyncTime=" + lastSyncTime +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerProfile that = (PlayerProfile) obj;
        return username != null ? username.equals(that.username) : that.username == null;
    }
    
    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
