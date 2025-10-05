package com.forgegrid.app;

/**
 * IDE Launcher for ForgeGrid
 * This class provides a simple way to run the application from the IDE
 * without worrying about classpath configuration issues.
 */
public class IDE {
    public static void main(String[] args) {
        System.out.println("🚀 Starting ForgeGrid from IDE...");
        System.out.println("📁 Working Directory: " + System.getProperty("user.dir"));
        System.out.println("☕ Java Version: " + System.getProperty("java.version"));
        System.out.println("📚 Classpath: " + System.getProperty("java.class.path"));
        System.out.println();
        
        try {
            // Try to load SLF4J to verify it's available
            Class.forName("org.slf4j.LoggerFactory");
            System.out.println("✅ SLF4J logging library loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SLF4J logging library not found in classpath");
            System.err.println("Please ensure slf4j-api-1.7.36.jar and slf4j-simple-1.7.36.jar are in the lib folder");
            return;
        }
        
        try {
            // Try to load SQLite driver to verify it's available
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ SQLite JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found in classpath");
            System.err.println("Please ensure sqlite-jdbc-3.44.1.0.jar is in the lib folder");
            return;
        }
        
        try {
            // Try to load Gson to verify it's available
            Class.forName("com.google.gson.Gson");
            System.out.println("✅ Gson library loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Gson library not found in classpath");
            System.err.println("Please ensure gson-2.10.1.jar is in the lib folder");
            return;
        }
        
        System.out.println("🎯 Launching main application...");
        System.out.println();
        
        // Launch the main application
        Main.main(args);
    }
}
