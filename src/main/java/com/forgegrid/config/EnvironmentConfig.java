package com.forgegrid.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment configuration utility for loading database credentials.
 * Supports both system environment variables and .env file loading.
 * 
 * Migration Note: This class was added to support Railway MySQL integration,
 * replacing the hardcoded XAMPP localhost configuration.
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
        
        // Override with system environment variables if they exist
        loadFromSystemEnv();
        
        envLoaded = true;
        System.out.println("Environment configuration loaded successfully");
    }
    
    /**
     * Load variables from .env file
     */
    private static void loadFromEnvFile() {
        File envFile = new File(ENV_FILE);
        if (!envFile.exists()) {
            System.out.println("No .env file found. Using system environment variables or defaults.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse KEY=VALUE format
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    // Remove quotes if present
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    
                    envVars.put(key, value);
                }
            }
            System.out.println("Loaded " + envVars.size() + " variables from .env file");
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        }
    }
    
    /**
     * Load variables from system environment
     */
    private static void loadFromSystemEnv() {
        String[] railwayKeys = {
            "RAILWAY_MYSQL_HOST",
            "RAILWAY_MYSQL_PORT", 
            "RAILWAY_MYSQL_DATABASE",
            "RAILWAY_MYSQL_USERNAME",
            "RAILWAY_MYSQL_PASSWORD",
            "RAILWAY_MYSQL_URL"
        };
        
        for (String key : railwayKeys) {
            String value = System.getenv(key);
            if (value != null && !value.isEmpty()) {
                envVars.put(key, value);
                System.out.println("Loaded " + key + " from system environment");
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
            // Convert Railway's mysql:// format to JDBC format
            if (url.startsWith("mysql://")) {
                // Parse Railway connection string: mysql://user:pass@host:port/database
                String jdbcUrl = convertRailwayUrlToJdbc(url);
                System.out.println("Converted Railway URL to JDBC format");
                return jdbcUrl;
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
            
            // Extract credentials (user:pass)
            String credentials = url.substring(0, atIndex);
            String hostAndDb = url.substring(atIndex + 1);
            
            // Find : in credentials to separate user and password
            int colonIndex = credentials.indexOf(':');
            if (colonIndex == -1) {
                throw new IllegalArgumentException("Invalid Railway URL format - no password");
            }
            
            String username = credentials.substring(0, colonIndex);
            String password = credentials.substring(colonIndex + 1);
            
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
            
            // Build JDBC URL
            String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                                         host, port, database);
            
            System.out.println("Converted Railway URL:");
            System.out.println("  From: " + railwayUrl.replace(password, "[PASSWORD]"));
            System.out.println("  To:   " + jdbcUrl);
            System.out.println("  Host: " + host);
            System.out.println("  Port: " + port);
            System.out.println("  Database: " + database);
            System.out.println("  Username: " + username);
            
            return jdbcUrl;
            
        } catch (Exception e) {
            System.err.println("Error converting Railway URL: " + e.getMessage());
            return railwayUrl; // Return original if conversion fails
        }
    }
    
    /**
     * Check if Railway configuration is available
     */
    public static boolean isRailwayConfigured() {
        return has("RAILWAY_MYSQL_HOST") || has("RAILWAY_MYSQL_URL");
    }
}
