package com.forgegrid.managers;

import com.forgegrid.model.PlayerProfile;
import com.forgegrid.services.OfflineAuthService;
import com.forgegrid.services.SupabaseAuthService;
import com.forgegrid.utils.SupabaseHttpClient;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hybrid authentication manager that coordinates between online (Supabase) and offline authentication.
 * Automatically falls back to offline mode when online authentication is not available.
 * 
 * This class provides a unified interface for authentication that works both online and offline.
 */
public class HybridAuthManager {
    
    private final SupabaseAuthService supabaseAuth;
    private final OfflineAuthService offlineAuth;
    private final ExecutorService executor;
    private final String supabaseUrl;
    private final String supabaseAnonKey;
    
    // Current authentication state
    private PlayerProfile currentProfile;
    private boolean isOnlineMode;
    private boolean isAuthenticated;
    
    /**
     * Constructor for HybridAuthManager
     * 
     * @param supabaseUrl The base URL of your Supabase project
     * @param supabaseAnonKey The anonymous key from your Supabase project
     */
    public HybridAuthManager(String supabaseUrl, String supabaseAnonKey) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseAnonKey = supabaseAnonKey;
        this.supabaseAuth = new SupabaseAuthService(supabaseUrl, supabaseAnonKey, "supabase_tokens.json");
        this.offlineAuth = new OfflineAuthService("offline_profiles.json");
        this.executor = Executors.newCachedThreadPool();
        this.isOnlineMode = false;
        this.isAuthenticated = false;
        
        // Check if we have existing online authentication
        if (supabaseAuth.isAuthenticated()) {
            this.currentProfile = supabaseAuth.getCurrentProfile();
            this.isOnlineMode = true;
            this.isAuthenticated = true;
        }
    }
    
    /**
     * Authenticates a user with email and password.
     * Tries online authentication first, falls back to offline if needed.
     * 
     * @param email User's email address
     * @param password User's password
     * @return AuthenticationResult containing the profile and authentication mode
     */
    public CompletableFuture<AuthenticationResult> authenticateUser(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try online authentication
                if (isOnlineAvailable()) {
                    try {
                        PlayerProfile onlineProfile = supabaseAuth.authenticateUser(email, password);
                        if (onlineProfile != null) {
                            this.currentProfile = onlineProfile;
                            this.isOnlineMode = true;
                            this.isAuthenticated = true;
                            
                            // Sync profile to offline storage for fallback
                            syncProfileToOffline(onlineProfile);
                            
                            return new AuthenticationResult(onlineProfile, true, "Online authentication successful");
                        }
                    } catch (SupabaseAuthService.SupabaseAuthException e) {
                        System.out.println("Online authentication failed: " + e.getMessage());
                        // Continue to offline authentication
                    }
                }
                
                // Fall back to offline authentication
                try {
                    PlayerProfile offlineProfile = offlineAuth.authenticateUser(email, password);
                    if (offlineProfile != null) {
                        this.currentProfile = offlineProfile;
                        this.isOnlineMode = false;
                        this.isAuthenticated = true;
                        
                        // Try to sync with online if available
                        if (isOnlineAvailable()) {
                            syncOfflineProfileToOnline(offlineProfile);
                        }
                        
                        return new AuthenticationResult(offlineProfile, false, "Offline authentication successful");
                    }
                } catch (OfflineAuthService.OfflineAuthException e) {
                    return new AuthenticationResult(null, false, "Authentication failed: " + e.getMessage());
                }
                
                return new AuthenticationResult(null, false, "Authentication failed: Invalid credentials");
                
            } catch (Exception e) {
                return new AuthenticationResult(null, false, "Authentication error: " + e.getMessage());
            }
        }, executor);
    }
    
    /**
     * Registers a new user.
     * Tries to register online first, falls back to offline if needed.
     * 
     * @param username The username
     * @param email The email address
     * @param password The password
     * @param fullName The full name
     * @return RegistrationResult containing the profile and registration mode
     */
    public CompletableFuture<RegistrationResult> registerUser(String username, String email, String password, String fullName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try offline registration (always available)
                PlayerProfile offlineProfile;
                try {
                    offlineProfile = offlineAuth.registerUser(username, email, password, fullName);
                } catch (OfflineAuthService.OfflineAuthException e) {
                    return new RegistrationResult(null, false, "Registration failed: " + e.getMessage());
                }
                
                // Try to register online if available
                if (isOnlineAvailable()) {
                    try {
                        // Register user in Supabase
                        PlayerProfile onlineProfile = supabaseAuth.registerUser(email, password, fullName);
                        
                        // Sync offline profile with online data
                        offlineProfile.setScore(onlineProfile.getScore());
                        offlineProfile.setLevel(onlineProfile.getLevel());
                        offlineAuth.updateProfile(offlineProfile);
                        
                        return new RegistrationResult(onlineProfile, true, "Registration successful (online)");
                    } catch (Exception e) {
                        System.out.println("Online registration failed: " + e.getMessage());
                        return new RegistrationResult(offlineProfile, false, "Registration successful (offline only)");
                    }
                } else {
                    return new RegistrationResult(offlineProfile, false, "Registration successful (offline only)");
                }
                
            } catch (Exception e) {
                return new RegistrationResult(null, false, "Registration error: " + e.getMessage());
            }
        }, executor);
    }
    
    /**
     * Updates the current user's profile.
     * Syncs with online storage if available.
     * 
     * @param profile The updated profile
     * @return true if update successful, false otherwise
     */
    public CompletableFuture<Boolean> updateProfile(PlayerProfile profile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.currentProfile = profile;
                
                // Update offline storage
                boolean offlineSuccess = offlineAuth.updateProfile(profile);
                
                // Try to sync with online if available
                if (isOnlineMode && isOnlineAvailable()) {
                    try {
                        boolean onlineSuccess = supabaseAuth.updateProfile(profile);
                        return offlineSuccess && onlineSuccess;
                    } catch (Exception e) {
                        System.out.println("Online profile update failed: " + e.getMessage());
                        return offlineSuccess; // Return offline success even if online fails
                    }
                }
                
                return offlineSuccess;
                
            } catch (Exception e) {
                System.err.println("Profile update error: " + e.getMessage());
                return false;
            }
        }, executor);
    }
    
    /**
     * Gets the current authenticated profile
     * 
     * @return Current PlayerProfile or null if not authenticated
     */
    public PlayerProfile getCurrentProfile() {
        return currentProfile;
    }
    
    /**
     * Checks if the user is currently authenticated
     * 
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return isAuthenticated && currentProfile != null;
    }
    
    /**
     * Checks if currently in online mode
     * 
     * @return true if online mode, false if offline mode
     */
    public boolean isOnlineMode() {
        return isOnlineMode && isAuthenticated;
    }
    
    /**
     * Checks if online authentication is available
     * 
     * @return true if online, false if offline
     */
    public boolean isOnlineAvailable() {
        try {
            SupabaseHttpClient httpClient = new SupabaseHttpClient(supabaseUrl, supabaseAnonKey);
            return httpClient.isOnline();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Forces a sync with online storage if available
     * 
     * @return true if sync successful, false otherwise
     */
    public CompletableFuture<Boolean> forceSync() {
        return CompletableFuture.supplyAsync(() -> {
            if (!isAuthenticated() || !isOnlineAvailable()) {
                return false;
            }
            
            try {
                if (isOnlineMode) {
                    // Sync current profile to online
                    return supabaseAuth.syncProfileWithSupabase();
                } else {
                    // Try to sync offline profile to online
                    return syncOfflineProfileToOnline(currentProfile);
                }
            } catch (Exception e) {
                System.err.println("Sync error: " + e.getMessage());
                return false;
            }
        }, executor);
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        if (isOnlineMode) {
            supabaseAuth.logout();
        }
        
        this.currentProfile = null;
        this.isOnlineMode = false;
        this.isAuthenticated = false;
    }
    
    /**
     * Shuts down the authentication manager
     */
    public void shutdown() {
        supabaseAuth.shutdown();
        executor.shutdown();
    }
    
    /**
     * Syncs an online profile to offline storage
     */
    private void syncProfileToOffline(PlayerProfile onlineProfile) {
        try {
            PlayerProfile offlineCopy = onlineProfile.createLocalCopy();
            offlineAuth.updateProfile(offlineCopy);
        } catch (Exception e) {
            System.err.println("Error syncing online profile to offline: " + e.getMessage());
        }
    }
    
    /**
     * Syncs an offline profile to online storage
     */
    private boolean syncOfflineProfileToOnline(PlayerProfile offlineProfile) {
        try {
            if (isOnlineAvailable() && supabaseAuth.isAuthenticated()) {
                // Update the profile in Supabase
                return supabaseAuth.updateProfile(offlineProfile);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error syncing offline profile to online: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Result class for authentication operations
     */
    public static class AuthenticationResult {
        private final PlayerProfile profile;
        private final boolean isOnline;
        private final String message;
        
        public AuthenticationResult(PlayerProfile profile, boolean isOnline, String message) {
            this.profile = profile;
            this.isOnline = isOnline;
            this.message = message;
        }
        
        public PlayerProfile getProfile() { return profile; }
        public boolean isOnline() { return isOnline; }
        public String getMessage() { return message; }
        public boolean isSuccess() { return profile != null; }
    }
    
    /**
     * Result class for registration operations
     */
    public static class RegistrationResult {
        private final PlayerProfile profile;
        private final boolean isOnline;
        private final String message;
        
        public RegistrationResult(PlayerProfile profile, boolean isOnline, String message) {
            this.profile = profile;
            this.isOnline = isOnline;
            this.message = message;
        }
        
        public PlayerProfile getProfile() { return profile; }
        public boolean isOnline() { return isOnline; }
        public String getMessage() { return message; }
        public boolean isSuccess() { return profile != null; }
    }
}
