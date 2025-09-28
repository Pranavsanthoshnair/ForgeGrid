package com.forgegrid.demo;

import com.forgegrid.managers.UserManager;
import com.forgegrid.model.User;
import com.forgegrid.utils.PasswordUtils;

/**
 * Demonstration class showing the secure authentication system in action.
 * This class demonstrates:
 * - User registration with password hashing
 * - User authentication with password verification
 * - File-based storage and retrieval
 * - Error handling for various scenarios
 */
public class AuthDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ForgeGrid Secure Authentication Demo ===\n");
        
        // Get the UserManager instance
        UserManager userManager = UserManager.getInstance();
        
        // Demo 1: Register new users
        System.out.println("1. Registering new users...");
        try {
            // Register first user
            boolean success1 = userManager.registerUser("john_doe", "password123", "john@example.com", "John Doe");
            System.out.println("   ✓ Registered user 'john_doe': " + success1);
            
            // Register second user
            boolean success2 = userManager.registerUser("jane_smith", "mypassword456", "jane@example.com", "Jane Smith");
            System.out.println("   ✓ Registered user 'jane_smith': " + success2);
            
            // Try to register duplicate username (should fail)
            boolean success3 = userManager.registerUser("john_doe", "anotherpass", "john2@example.com", "John Doe Jr");
            System.out.println("   ✗ Duplicate username 'john_doe': " + success3 + " (expected: false)");
            
            // Try to register duplicate email (should fail)
            boolean success4 = userManager.registerUser("bob_wilson", "password789", "john@example.com", "Bob Wilson");
            System.out.println("   ✗ Duplicate email 'john@example.com': " + success4 + " (expected: false)");
            
        } catch (Exception e) {
            System.out.println("   ✗ Registration error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Demo 2: Authenticate users
        System.out.println("2. Authenticating users...");
        try {
            // Authenticate with correct credentials
            User user1 = userManager.authenticateUser("john_doe", "password123");
            System.out.println("   ✓ Login 'john_doe' with correct password: " + (user1 != null ? "SUCCESS" : "FAILED"));
            if (user1 != null) {
                System.out.println("      User details: " + user1);
            }
            
            // Authenticate with wrong password
            User user2 = userManager.authenticateUser("john_doe", "wrongpassword");
            System.out.println("   ✗ Login 'john_doe' with wrong password: " + (user2 != null ? "SUCCESS" : "FAILED") + " (expected: FAILED)");
            
            // Authenticate non-existent user
            User user3 = userManager.authenticateUser("nonexistent", "password123");
            System.out.println("   ✗ Login 'nonexistent' user: " + (user3 != null ? "SUCCESS" : "FAILED") + " (expected: FAILED)");
            
            // Authenticate by email
            User user4 = userManager.authenticateUserByEmail("jane@example.com", "mypassword456");
            System.out.println("   ✓ Login by email 'jane@example.com': " + (user4 != null ? "SUCCESS" : "FAILED"));
            if (user4 != null) {
                System.out.println("      User details: " + user4);
            }
            
        } catch (Exception e) {
            System.out.println("   ✗ Authentication error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Demo 3: Password hashing demonstration
        System.out.println("3. Password hashing demonstration...");
        try {
            String password = "mypassword123";
            String hash1 = PasswordUtils.hashPassword(password);
            String hash2 = PasswordUtils.hashPassword(password);
            
            System.out.println("   Original password: " + password);
            System.out.println("   Hash 1: " + hash1);
            System.out.println("   Hash 2: " + hash2);
            System.out.println("   Hashes are different (due to salt): " + !hash1.equals(hash2));
            
            // Verify both hashes work with the original password
            boolean verify1 = PasswordUtils.verifyPassword(password, hash1);
            boolean verify2 = PasswordUtils.verifyPassword(password, hash2);
            System.out.println("   Hash 1 verification: " + verify1);
            System.out.println("   Hash 2 verification: " + verify2);
            
            // Try wrong password
            boolean verifyWrong = PasswordUtils.verifyPassword("wrongpassword", hash1);
            System.out.println("   Wrong password verification: " + verifyWrong + " (expected: false)");
            
        } catch (Exception e) {
            System.out.println("   ✗ Password hashing error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Demo 4: User management
        System.out.println("4. User management...");
        try {
            System.out.println("   Total users registered: " + userManager.getUserCount());
            System.out.println("   User 'john_doe' exists: " + userManager.userExists("john_doe"));
            System.out.println("   User 'nonexistent' exists: " + userManager.userExists("nonexistent"));
            System.out.println("   Email 'jane@example.com' exists: " + userManager.emailExists("jane@example.com"));
            System.out.println("   Email 'bob@example.com' exists: " + userManager.emailExists("bob@example.com"));
            
        } catch (Exception e) {
            System.out.println("   ✗ User management error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Demo 5: Password validation
        System.out.println("5. Password validation...");
        String[] testPasswords = {"123", "password", "123456", "pass123", "Password123", "MySecurePass1"};
        for (String pwd : testPasswords) {
            boolean valid = PasswordUtils.isValidPassword(pwd);
            System.out.println("   Password '" + pwd + "': " + (valid ? "VALID" : "INVALID"));
        }
        System.out.println("   Requirements: " + PasswordUtils.getPasswordRequirements());
        
        System.out.println();
        System.out.println("=== Demo Complete ===");
        System.out.println("Check 'users.dat' file in the project root to see the stored user data.");
        System.out.println("Note: Passwords are stored as SHA-256 hashes with salt for security.");
    }
}
