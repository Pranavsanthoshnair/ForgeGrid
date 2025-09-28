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
        properties.setProperty("supabase.url", "https://your-project.supabase.co");
        properties.setProperty("supabase.anon.key", "your-anon-key-here");
        properties.setProperty("app.name", "ForgeGrid");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.debug", "false");
        properties.setProperty("auth.offline.enabled", "true");
        properties.setProperty("auth.online.enabled", "true");
        properties.setProperty("auth.token.refresh.interval", "3600000");
        properties.setProperty("google.oauth.client.id", "your-google-client-id.apps.googleusercontent.com");
        properties.setProperty("google.oauth.client.secret", "your-google-client-secret");
        properties.setProperty("google.oauth.redirect.uri", "http://localhost:8080/callback");
        properties.setProperty("google.oauth.scope", "openid email profile");
    }
    
    /**
     * Get Supabase URL
     */
    public String getSupabaseUrl() {
        return properties.getProperty("supabase.url", "https://your-project.supabase.co");
    }
    
    /**
     * Get Supabase anonymous key
     */
    public String getSupabaseAnonKey() {
        return properties.getProperty("supabase.anon.key", "your-anon-key-here");
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
     * Check if offline authentication is enabled
     */
    public boolean isOfflineAuthEnabled() {
        return Boolean.parseBoolean(properties.getProperty("auth.offline.enabled", "true"));
    }
    
    /**
     * Check if online authentication is enabled
     */
    public boolean isOnlineAuthEnabled() {
        return Boolean.parseBoolean(properties.getProperty("auth.online.enabled", "true"));
    }
    
    /**
     * Get token refresh interval in milliseconds
     */
    public long getTokenRefreshInterval() {
        return Long.parseLong(properties.getProperty("auth.token.refresh.interval", "3600000"));
    }
    
    /**
     * Get Google OAuth client ID
     */
    public String getGoogleOAuthClientId() {
        return properties.getProperty("google.oauth.client.id", "your-google-client-id.apps.googleusercontent.com");
    }
    
    /**
     * Get Google OAuth client secret
     */
    public String getGoogleOAuthClientSecret() {
        return properties.getProperty("google.oauth.client.secret", "your-google-client-secret");
    }
    
    /**
     * Get Google OAuth redirect URI
     */
    public String getGoogleOAuthRedirectUri() {
        return properties.getProperty("google.oauth.redirect.uri", "http://localhost:8080/callback");
    }
    
    /**
     * Get Google OAuth scope
     */
    public String getGoogleOAuthScope() {
        return properties.getProperty("google.oauth.scope", "openid email profile");
    }
    
    /**
     * Check if Supabase is properly configured
     */
    public boolean isSupabaseConfigured() {
        String url = getSupabaseUrl();
        String key = getSupabaseAnonKey();
        
        return !url.equals("https://your-project.supabase.co") && 
               !key.equals("your-anon-key-here") &&
               !url.isEmpty() && !key.isEmpty();
    }
    
    /**
     * Print current configuration (without sensitive data)
     */
    public void printConfig() {
        System.out.println("🔧 Current Configuration:");
        System.out.println("========================");
        System.out.println("App Name: " + getAppName());
        System.out.println("App Version: " + getAppVersion());
        System.out.println("Debug Mode: " + isDebugEnabled());
        System.out.println("Offline Auth: " + isOfflineAuthEnabled());
        System.out.println("Online Auth: " + isOnlineAuthEnabled());
        System.out.println("Supabase URL: " + getSupabaseUrl());
        System.out.println("Supabase Key: " + (isSupabaseConfigured() ? "✅ Configured" : "❌ Not Configured"));
        System.out.println("========================\n");
    }
}
