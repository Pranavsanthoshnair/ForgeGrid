package com.forgegrid.managers;

import com.forgegrid.model.User;
import com.forgegrid.utils.PasswordUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UserManager class handles user registration, authentication, and file-based storage.
 * Uses serialization to store user data securely with SHA-256 hashed passwords.
 * 
 * This class is thread-safe and provides methods for:
 * - User registration with duplicate checking
 * - User authentication with password verification
 * - Loading and saving users from/to file
 * - User management operations
 */
public class UserManager {
    
    private static final String USERS_FILE = "users.dat";
    private static UserManager instance;
    private Map<String, User> users;
    private final Object lock = new Object();
    
    /**
     * Private constructor for singleton pattern
     */
    private UserManager() {
        this.users = new ConcurrentHashMap<>();
        loadUsers();
    }
    
    /**
     * Gets the singleton instance of UserManager
     * 
     * @return The UserManager instance
     */
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    /**
     * Registers a new user with the system.
     * 
     * @param username The unique username
     * @param password The plain text password (will be hashed)
     * @param email The user's email address
     * @param fullName The user's full name
     * @return true if registration successful, false if username already exists
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public boolean registerUser(String username, String password, String email, String fullName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
        
        // Validate password strength
        if (!PasswordUtils.isValidPassword(password)) {
            throw new IllegalArgumentException(PasswordUtils.getPasswordRequirements());
        }
        
        // Normalize inputs
        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();
        fullName = fullName.trim();
        
        synchronized (lock) {
            // Check if user already exists
            if (users.containsKey(username)) {
                return false; // Username already exists
            }
            
            // Check if email already exists
            for (User user : users.values()) {
                if (email.equals(user.getEmail())) {
                    throw new IllegalArgumentException("An account with this email already exists");
                }
            }
            
            // Hash the password
            String hashedPassword = PasswordUtils.hashPassword(password);
            
            // Create new user
            User newUser = new User(username, hashedPassword, email, fullName);
            users.put(username, newUser);
            
            // Save to file
            saveUsers();
            
            return true;
        }
    }
    
    /**
     * Authenticates a user with username and password.
     * 
     * @param username The username to authenticate
     * @param password The plain text password to verify
     * @return The authenticated User object if successful, null if authentication fails
     * @throws IllegalArgumentException if username or password is null/empty
     */
    public User authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        username = username.trim().toLowerCase();
        
        synchronized (lock) {
            User user = users.get(username);
            if (user == null) {
                return null; // User not found
            }
            
            // Verify password
            if (PasswordUtils.verifyPassword(password, user.getHashedPassword())) {
                return user; // Authentication successful
            } else {
                return null; // Invalid password
            }
        }
    }
    
    /**
     * Authenticates a user with email and password.
     * 
     * @param email The email to authenticate
     * @param password The plain text password to verify
     * @return The authenticated User object if successful, null if authentication fails
     * @throws IllegalArgumentException if email or password is null/empty
     */
    public User authenticateUserByEmail(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        email = email.trim().toLowerCase();
        
        synchronized (lock) {
            // Find user by email
            User user = null;
            for (User u : users.values()) {
                if (email.equals(u.getEmail())) {
                    user = u;
                    break;
                }
            }
            
            if (user == null) {
                return null; // User not found
            }
            
            // Verify password
            if (PasswordUtils.verifyPassword(password, user.getHashedPassword())) {
                return user; // Authentication successful
            } else {
                return null; // Invalid password
            }
        }
    }
    
    /**
     * Checks if a username already exists.
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        synchronized (lock) {
            return users.containsKey(username.trim().toLowerCase());
        }
    }
    
    /**
     * Checks if an email already exists.
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        email = email.trim().toLowerCase();
        
        synchronized (lock) {
            for (User user : users.values()) {
                if (email.equals(user.getEmail())) {
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     * Gets a user by username.
     * 
     * @param username The username to look up
     * @return The User object if found, null otherwise
     */
    public User getUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        synchronized (lock) {
            return users.get(username.trim().toLowerCase());
        }
    }
    
    /**
     * Gets all registered users (for administrative purposes).
     * 
     * @return A collection of all users
     */
    public Collection<User> getAllUsers() {
        synchronized (lock) {
            return new ArrayList<>(users.values());
        }
    }
    
    /**
     * Gets the total number of registered users.
     * 
     * @return The number of users
     */
    public int getUserCount() {
        synchronized (lock) {
            return users.size();
        }
    }
    
    /**
     * Loads users from the file system.
     * This method is called during initialization.
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // No users file exists yet, start with empty map
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                users = (Map<String, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            // Start with empty map if loading fails
            users = new ConcurrentHashMap<>();
        }
    }
    
    /**
     * Saves users to the file system.
     * This method is called after each user registration.
     */
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            throw new RuntimeException("Failed to save user data", e);
        }
    }
    
    /**
     * Clears all user data (for testing purposes).
     * This method should only be used in test environments.
     */
    public void clearAllUsers() {
        synchronized (lock) {
            users.clear();
            File file = new File(USERS_FILE);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
