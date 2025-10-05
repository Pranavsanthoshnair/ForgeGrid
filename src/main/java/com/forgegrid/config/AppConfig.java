package com.forgegrid.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration manager
 * Loads settings from config.properties file
 */
public class AppConfig {
    
    private static final String CONFIG_FILE = "config.properties";
    private static AppConfig instance;
    private Properties properties;
    
    private AppConfig() {
        loadConfig();
    }
    
    /**
     * Get singleton instance
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    /**
     * Load configuration from properties file
     */
    private void loadConfig() {
        properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("⚠️  Config file not found: " + CONFIG_FILE);
                System.err.println("📝 Please create config.properties with your Supabase credentials");
                createDefaultConfig();
                return;
            }
            
            properties.load(input);
            System.out.println("✅ Configuration loaded successfully");
            
        } catch (IOException e) {
            System.err.println("❌ Error loading configuration: " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    /**
     * Create default configuration if file doesn't exist
     */
    private void createDefaultConfig() {
        properties.setProperty("app.name", "ForgeGrid");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.debug", "false");
    }
    
    /**
     * Get application name
     */
    public String getAppName() {
        return properties.getProperty("app.name", "ForgeGrid");
    }
    
    /**
     * Get application version
     */
    public String getAppVersion() {
        return properties.getProperty("app.version", "1.0.0");
    }
    
    /**
     * Check if debug mode is enabled
     */
    public boolean isDebugEnabled() {
        return Boolean.parseBoolean(properties.getProperty("app.debug", "false"));
    }
    
    /**
     * Print current configuration
     */
    public void printConfig() {
        System.out.println("🔧 Current Configuration:");
        System.out.println("========================");
        System.out.println("App Name: " + getAppName());
        System.out.println("App Version: " + getAppVersion());
        System.out.println("Debug Mode: " + isDebugEnabled());
        System.out.println("Authentication: SQLite Database");
        System.out.println("========================\n");
    }
}
