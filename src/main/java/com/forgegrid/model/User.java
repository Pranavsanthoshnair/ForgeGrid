package com.forgegrid.model;

import java.io.Serializable;

/**
 * User class representing a user account in the ForgeGrid system.
 * Stores username and hashed password for secure authentication.
 * 
 * This class implements Serializable to allow storing user data in files.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String hashedPassword;
    private String email;
    private String fullName;
    
    /**
     * Default constructor for serialization
     */
    public User() {
    }
    
    /**
     * Constructor to create a new user with all required fields
     * 
     * @param username The unique username for the user
     * @param hashedPassword The SHA-256 hashed password
     * @param email The user's email address
     * @param fullName The user's full name
     */
    public User(String username, String hashedPassword, String email, String fullName) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
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
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username != null ? username.equals(user.username) : user.username == null;
    }
    
    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
