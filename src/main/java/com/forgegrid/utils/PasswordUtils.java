package com.forgegrid.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for secure password handling using SHA-256 hashing.
 * Provides methods for hashing passwords and verifying password matches.
 * 
 * This class uses SHA-256 with a salt to prevent rainbow table attacks.
 */
public class PasswordUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    
    /**
     * Hashes a password using SHA-256 with a random salt.
     * The salt is prepended to the password before hashing for additional security.
     * 
     * @param password The plain text password to hash
     * @return A string containing the salt and hash in the format: salt:hash
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Create the salted password
            String saltedPassword = password + Base64.getEncoder().encodeToString(salt);
            
            // Hash the salted password
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(saltedPassword.getBytes());
            
            // Encode both salt and hash to Base64 for storage
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            return saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            // This should never happen as SHA-256 is a standard algorithm
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash.
     * 
     * @param password The plain text password to verify
     * @param storedHash The stored hash in the format: salt:hash
     * @return true if the password matches, false otherwise
     * @throws IllegalArgumentException if password or storedHash is null/empty
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (storedHash == null || storedHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Stored hash cannot be null or empty");
        }
        
        try {
            // Split the stored hash into salt and hash parts
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid stored hash format");
            }
            
            String saltBase64 = parts[0];
            String hashBase64 = parts[1];
            
            // Decode the salt and hash
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            byte[] storedHashBytes = Base64.getDecoder().decode(hashBase64);
            
            // Create the salted password with the original salt
            String saltedPassword = password + Base64.getEncoder().encodeToString(salt);
            
            // Hash the password with the same salt
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(saltedPassword.getBytes());
            
            // Compare the hashes
            return MessageDigest.isEqual(hash, storedHashBytes);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        } catch (IllegalArgumentException e) {
            // Re-throw IllegalArgumentException with more context
            throw new IllegalArgumentException("Invalid stored hash: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates password strength requirements.
     * 
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        
        return hasLetter && hasNumber;
    }
    
    /**
     * Gets password strength requirements as a user-friendly message.
     * 
     * @return A string describing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must be at least 6 characters long and contain at least one letter and one number.";
    }
}
