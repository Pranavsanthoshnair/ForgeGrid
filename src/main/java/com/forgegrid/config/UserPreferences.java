package com.forgegrid.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages user preferences stored locally per device.
 * Stores non-sensitive data like last logged-in username in user's home directory.
 */
public class UserPreferences {
    
    private static final String PREFS_FILE_NAME = "forgegrid.prefs";
    private static final String KEY_LAST_USERNAME = "last.username";
    
    private Properties properties;
    private File prefsFile;
    
    public UserPreferences() {
        properties = new Properties();
        // Save preferences in user's home directory (device-specific)
        String userHome = System.getProperty("user.home");
        prefsFile = new File(userHome, PREFS_FILE_NAME);
        loadPreferences();
    }
    
    /**
     * Load preferences from file
     */
    private void loadPreferences() {
        if (prefsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(prefsFile)) {
                properties.load(fis);
                System.out.println("User preferences loaded from: " + prefsFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error loading preferences: " + e.getMessage());
            }
        } else {
            System.out.println("No existing preferences file found. Will create new one at: " + prefsFile.getAbsolutePath());
        }
    }
    
    /**
     * Save preferences to file
     */
    private void savePreferences() {
        try (FileOutputStream fos = new FileOutputStream(prefsFile)) {
            properties.store(fos, "ForgeGrid User Preferences - Device Specific");
            System.out.println("User preferences saved to: " + prefsFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving preferences: " + e.getMessage());
        }
    }
    
    /**
     * Get the last logged-in username
     * 
     * @return Last username, or null if not found
     */
    public String getLastUsername() {
        return properties.getProperty(KEY_LAST_USERNAME);
    }
    
    /**
     * Save the last logged-in username
     * 
     * @param username Username to save
     */
    public void setLastUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            properties.setProperty(KEY_LAST_USERNAME, username.trim());
            savePreferences();
        }
    }
    
    /**
     * Clear the last username
     */
    public void clearLastUsername() {
        properties.remove(KEY_LAST_USERNAME);
        savePreferences();
    }
    
    /**
     * Clear all preferences
     */
    public void clearAll() {
        properties.clear();
        savePreferences();
    }
}
