package com.forgegrid.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for loading database-related environment variables from a .env file
 * and the system environment. Designed for simple, fail-safe configuration.
 */
public class EnvironmentConfig {
    
    private static final String ENV_FILE = ".env";
    private static Map<String, String> envVars = new HashMap<>();
    private static boolean envLoaded = false;
    
    /**
     * Load environment variables from .env file and system environment
     */
    private static void loadEnvironment() {
        if (envLoaded) return;
        
        // Load from .env file first
        loadFromEnvFile();
        loadFromSystemEnv();
        envLoaded = true;
    }
    
    /**
     * Load variables from .env file
     */
    private static void loadFromEnvFile() {
        File envFile = new File(ENV_FILE);
        if (!envFile.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    envVars.put(key, value);
                }
            }
        } catch (IOException e) {
            // .env is optional; ignore if missing or unreadable
        }
    }
    
    /**
     * Load variables from system environment
     */
    private static void loadFromSystemEnv() {
        String[] railwayKeys = {
            "RAILWAY_MYSQL_HOST", "RAILWAY_MYSQL_PORT", "RAILWAY_MYSQL_DATABASE",
            "RAILWAY_MYSQL_USERNAME", "RAILWAY_MYSQL_PASSWORD", "RAILWAY_MYSQL_URL"
        };
        
        for (String key : railwayKeys) {
            String value = System.getenv(key);
            if (value != null && !value.isEmpty()) {
                envVars.put(key, value);
            }
        }
    }
    
    /**
     * Get environment variable value
     * 
     * @param key Environment variable key
     * @param defaultValue Default value if not found
     * @return Environment variable value or default
     */
    public static String get(String key, String defaultValue) {
        loadEnvironment();
        return envVars.getOrDefault(key, defaultValue);
    }
    
    /**
     * Get environment variable value
     * 
     * @param key Environment variable key
     * @return Environment variable value or null
     */
    public static String get(String key) {
        loadEnvironment();
        return envVars.get(key);
    }
    
    /**
     * Check if environment variable exists
     * 
     * @param key Environment variable key
     * @return true if variable exists
     */
    public static boolean has(String key) {
        loadEnvironment();
        return envVars.containsKey(key) && envVars.get(key) != null && !envVars.get(key).isEmpty();
    }
    
    /**
     * Get Railway MySQL host
     */
    public static String getRailwayHost() {
        return get("RAILWAY_MYSQL_HOST", "localhost");
    }
    
    /**
     * Get Railway MySQL port
     */
    public static String getRailwayPort() {
        return get("RAILWAY_MYSQL_PORT", "3306");
    }
    
    /**
     * Get Railway MySQL database name
     */
    public static String getRailwayDatabase() {
        return get("RAILWAY_MYSQL_DATABASE", "railway");
    }
    
    /**
     * Get Railway MySQL username
     */
    public static String getRailwayUsername() {
        return get("RAILWAY_MYSQL_USERNAME", "root");
    }
    
    /**
     * Get Railway MySQL password
     */
    public static String getRailwayPassword() {
        return get("RAILWAY_MYSQL_PASSWORD", "");
    }
    
    /**
     * Get Railway MySQL connection URL
     */
    public static String getRailwayUrl() {
        String url = get("RAILWAY_MYSQL_URL");
        if (url != null && !url.isEmpty()) {
            if (url.startsWith("mysql://")) {
                return convertRailwayUrlToJdbc(url);
            }
            return url;
        }
        
        // Build URL from components
        String host = getRailwayHost();
        String port = getRailwayPort();
        String database = getRailwayDatabase();
        
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                           host, port, database);
    }
    
    /**
     * Convert Railway's mysql:// connection string to JDBC format
     * 
     * @param railwayUrl Railway connection string (mysql://user:pass@host:port/database)
     * @return JDBC connection string
     */
    private static String convertRailwayUrlToJdbc(String railwayUrl) {
        try {
            // Remove mysql:// prefix
            String url = railwayUrl.substring(8);
            
            // Find @ symbol to separate credentials from host
            int atIndex = url.indexOf('@');
            if (atIndex == -1) {
                throw new IllegalArgumentException("Invalid Railway URL format");
            }
            
            // Extract and validate credentials (user:pass); content not used in JDBC URL
            String credentials = url.substring(0, atIndex);
            int colonIndex = credentials.indexOf(':');
            if (colonIndex == -1) {
                throw new IllegalArgumentException("Invalid Railway URL format - no password");
            }
            String hostAndDb = url.substring(atIndex + 1);
            
            // Find : in hostAndDb to separate host and port
            int portIndex = hostAndDb.indexOf(':');
            if (portIndex == -1) {
                throw new IllegalArgumentException("Invalid Railway URL format - no port");
            }
            
            String host = hostAndDb.substring(0, portIndex);
            String portAndDb = hostAndDb.substring(portIndex + 1);
            
            // Find / to separate port and database
            int slashIndex = portAndDb.indexOf('/');
            if (slashIndex == -1) {
                throw new IllegalArgumentException("Invalid Railway URL format - no database");
            }
            
            String port = portAndDb.substring(0, slashIndex);
            String database = portAndDb.substring(slashIndex + 1);
            
            return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                               host, port, database);
        } catch (Exception e) {
            // Fallback to the original value if parsing fails
            return railwayUrl;
        }
    }
    
    /**
     * Check if Railway configuration is available
     */
    public static boolean isRailwayConfigured() {
        return has("RAILWAY_MYSQL_HOST") || has("RAILWAY_MYSQL_URL");
    }
}
