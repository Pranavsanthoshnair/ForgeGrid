package com.forgegrid.services;

import com.forgegrid.model.PlayerProfile;
import com.forgegrid.utils.PasswordUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for handling offline authentication using local file storage.
 * Uses the existing password hashing system and stores profiles in JSON format.
 */
public class OfflineAuthService {
    
    private final String profilesFilePath;
    private final Gson gson;
    private final ReadWriteLock lock;
    private final ConcurrentHashMap<String, PlayerProfile> profiles;
    
    /**
     * Constructor for OfflineAuthService
     * 
     * @param profilesFilePath Path to store profiles (e.g., "players.json")
     */
    public OfflineAuthService(String profilesFilePath) {
        this.profilesFilePath = profilesFilePath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.lock = new ReentrantReadWriteLock();
        this.profiles = new ConcurrentHashMap<>();
        
        // Load existing profiles
        loadProfiles();
    }
    
    /**
     * Authenticates a user with email and password using local storage
     * 
     * @param email User's email address
     * @param password User's password
     * @return PlayerProfile if authentication successful, null otherwise
     * @throws OfflineAuthException if authentication fails
     */
    public PlayerProfile authenticateUser(String email, String password) throws OfflineAuthException {
        if (email == null || email.trim().isEmpty()) {
            throw new OfflineAuthException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new OfflineAuthException("Password cannot be null or empty");
        }
        
        email = email.trim().toLowerCase();
        
        lock.readLock().lock();
        try {
            // Find user by email
            PlayerProfile profile = null;
            for (PlayerProfile p : profiles.values()) {
                if (email.equals(p.getEmail())) {
                    profile = p;
                    break;
                }
            }
            
            if (profile == null) {
                throw new OfflineAuthException("User not found with email: " + email);
            }
            
            // Verify password
            if (profile.getLocalPasswordHash() == null || 
                !PasswordUtils.verifyPassword(password, profile.getLocalPasswordHash())) {
                throw new OfflineAuthException("Invalid password");
            }
            
            // Update last login
            profile.updateLastLogin();
            profile.setOnline(false); // Mark as offline profile
            profile.setLastSyncTime(LocalDateTime.now());
            
            // Save updated profile
            saveProfiles();
            
            return profile;
            
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Registers a new user with local storage
     * 
     * @param username The username
     * @param email The email address
     * @param password The plain text password
     * @param fullName The full name
     * @return PlayerProfile if registration successful, null if user already exists
     * @throws OfflineAuthException if registration fails
     */
    public PlayerProfile registerUser(String username, String email, String password, String fullName) throws OfflineAuthException {
        if (username == null || username.trim().isEmpty()) {
            throw new OfflineAuthException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new OfflineAuthException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new OfflineAuthException("Password cannot be null or empty");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new OfflineAuthException("Full name cannot be null or empty");
        }
        
        // Validate password strength
        if (!PasswordUtils.isValidPassword(password)) {
            throw new OfflineAuthException(PasswordUtils.getPasswordRequirements());
        }
        
        // Normalize inputs
        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();
        fullName = fullName.trim();
        
        lock.writeLock().lock();
        try {
            // Ensure username uniqueness by auto-uniquifying if taken (e.g., "john", "john1", "john2", ...)
            if (profiles.containsKey(username)) {
                String baseUsername = username;
                int suffix = 1;
                while (profiles.containsKey(username)) {
                    username = baseUsername + suffix;
                    suffix++;
                }
            }
            
            // Check if email already exists
            for (PlayerProfile profile : profiles.values()) {
                if (email.equals(profile.getEmail())) {
                    throw new OfflineAuthException("Email already exists: " + email);
                }
            }
            
            // Hash the password
            String hashedPassword = PasswordUtils.hashPassword(password);
            
            // Create new profile
            PlayerProfile newProfile = new PlayerProfile(username, email, fullName);
            newProfile.setLocalPasswordHash(hashedPassword);
            newProfile.setOnline(false);
            newProfile.setLastSyncTime(LocalDateTime.now());
            
            // Add to profiles
            profiles.put(username, newProfile);
            
            // Save to file
            saveProfiles();
            
            return newProfile;
            
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Updates a user profile
     * 
     * @param profile The updated profile
     * @return true if update successful, false otherwise
     */
    public boolean updateProfile(PlayerProfile profile) {
        if (profile == null || profile.getUsername() == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            profiles.put(profile.getUsername(), profile);
            saveProfiles();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets a user profile by username
     * 
     * @param username The username
     * @return PlayerProfile if found, null otherwise
     */
    public PlayerProfile getProfile(String username) {
        if (username == null) {
            return null;
        }
        
        lock.readLock().lock();
        try {
            return profiles.get(username.trim().toLowerCase());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets a user profile by email
     * 
     * @param email The email address
     * @return PlayerProfile if found, null otherwise
     */
    public PlayerProfile getProfileByEmail(String email) {
        if (email == null) {
            return null;
        }
        
        email = email.trim().toLowerCase();
        
        lock.readLock().lock();
        try {
            for (PlayerProfile profile : profiles.values()) {
                if (email.equals(profile.getEmail())) {
                    return profile;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all stored profiles
     * 
     * @return List of all profiles
     */
    public List<PlayerProfile> getAllProfiles() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(profiles.values());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Checks if a username exists
     * 
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    public boolean userExists(String username) {
        if (username == null) {
            return false;
        }
        
        lock.readLock().lock();
        try {
            return profiles.containsKey(username.trim().toLowerCase());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Checks if an email exists
     * 
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null) {
            return false;
        }
        
        email = email.trim().toLowerCase();
        
        lock.readLock().lock();
        try {
            for (PlayerProfile profile : profiles.values()) {
                if (email.equals(profile.getEmail())) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets the total number of stored profiles
     * 
     * @return Number of profiles
     */
    public int getProfileCount() {
        lock.readLock().lock();
        try {
            return profiles.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Loads profiles from the JSON file
     */
    private void loadProfiles() {
        File file = new File(profilesFilePath);
        if (!file.exists()) {
            return;
        }
        
        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonData = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (jsonData.has("profiles")) {
                JsonArray profilesArray = jsonData.getAsJsonArray("profiles");
                
                for (int i = 0; i < profilesArray.size(); i++) {
                    JsonObject profileJson = profilesArray.get(i).getAsJsonObject();
                    PlayerProfile profile = gson.fromJson(profileJson, PlayerProfile.class);
                    profiles.put(profile.getUsername(), profile);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error loading profiles: " + e.getMessage());
        }
    }
    
    /**
     * Saves profiles to the JSON file
     */
    private void saveProfiles() {
        try (FileWriter writer = new FileWriter(profilesFilePath)) {
            JsonObject jsonData = new JsonObject();
            JsonArray profilesArray = new JsonArray();
            
            for (PlayerProfile profile : profiles.values()) {
                JsonObject profileJson = JsonParser.parseString(profile.toJson()).getAsJsonObject();
                profilesArray.add(profileJson);
            }
            
            jsonData.add("profiles", profilesArray);
            jsonData.addProperty("last_updated", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            gson.toJson(jsonData, writer);
            
        } catch (IOException e) {
            System.err.println("Error saving profiles: " + e.getMessage());
        }
    }
    
    /**
     * Clears all profiles (for testing purposes)
     */
    public void clearAllProfiles() {
        lock.writeLock().lock();
        try {
            profiles.clear();
            File file = new File(profilesFilePath);
            if (file.exists()) {
                file.delete();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Exception class for offline authentication errors
     */
    public static class OfflineAuthException extends Exception {
        public OfflineAuthException(String message) {
            super(message);
        }
        
        public OfflineAuthException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
