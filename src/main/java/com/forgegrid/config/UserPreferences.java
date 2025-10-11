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
    private static final String KEY_REMEMBER_ME = "remember.me";
    private static final String KEY_SAVED_USERNAME = "saved.username";
    private static final String KEY_SAVED_PASSWORD = "saved.password";
    
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
            } catch (IOException e) {
                // Silently fail - will use defaults
            }
        }
    }
    
    /**
     * Save preferences to file
     */
    private void savePreferences() {
        try (FileOutputStream fos = new FileOutputStream(prefsFile)) {
            properties.store(fos, "ForgeGrid User Preferences - Device Specific");
        } catch (IOException e) {
            // Silently fail
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
    
    /**
     * Check if remember me is enabled
     * 
     * @return true if remember me is enabled
     */
    public boolean isRememberMeEnabled() {
        return Boolean.parseBoolean(properties.getProperty(KEY_REMEMBER_ME, "false"));
    }
    
    /**
     * Get saved username for remember me
     * 
     * @return Saved username, or null if not found
     */
    public String getSavedUsername() {
        if (isRememberMeEnabled()) {
            return properties.getProperty(KEY_SAVED_USERNAME);
        }
        return null;
    }
    
    /**
     * Get saved password for remember me (Base64 encoded)
     * 
     * @return Saved password, or null if not found
     */
    public String getSavedPassword() {
        if (isRememberMeEnabled()) {
            String encoded = properties.getProperty(KEY_SAVED_PASSWORD);
            if (encoded != null) {
                try {
                    return new String(java.util.Base64.getDecoder().decode(encoded));
                } catch (Exception e) {
                    System.err.println("Error decoding password: " + e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Save credentials for remember me
     * 
     * @param username Username to save
     * @param password Password to save (will be Base64 encoded)
     */
    public void saveRememberMeCredentials(String username, String password) {
        if (username != null && !username.trim().isEmpty() && password != null) {
            properties.setProperty(KEY_REMEMBER_ME, "true");
            properties.setProperty(KEY_SAVED_USERNAME, username.trim());
            String encoded = java.util.Base64.getEncoder().encodeToString(password.getBytes());
            properties.setProperty(KEY_SAVED_PASSWORD, encoded);
            savePreferences();
        }
    }
    
    /**
     * Clear remember me credentials
     */
    public void clearRememberMe() {
        properties.remove(KEY_REMEMBER_ME);
        properties.remove(KEY_SAVED_USERNAME);
        properties.remove(KEY_SAVED_PASSWORD);
        savePreferences();
    }
}
