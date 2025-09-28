package com.forgegrid.services;

import com.forgegrid.model.PlayerProfile;
import com.forgegrid.utils.SupabaseHttpClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling online authentication with Supabase.
 * Manages tokens, profile synchronization, and automatic token refresh.
 */
public class SupabaseAuthService {
    
    private final SupabaseHttpClient httpClient;
    private final String tokenFilePath;
    private final Gson gson;
    private final ScheduledExecutorService scheduler;
    
    // Current session data
    private String currentAccessToken;
    private String currentRefreshToken;
    private LocalDateTime tokenExpiryTime;
    private PlayerProfile currentProfile;
    
    /**
     * Constructor for SupabaseAuthService
     * 
     * @param supabaseUrl The base URL of your Supabase project
     * @param supabaseAnonKey The anonymous key from your Supabase project
     * @param tokenFilePath Path to store tokens locally (e.g., "tokens.json")
     */
    public SupabaseAuthService(String supabaseUrl, String supabaseAnonKey, String tokenFilePath) {
        this.httpClient = new SupabaseHttpClient(supabaseUrl, supabaseAnonKey);
        this.tokenFilePath = tokenFilePath;
        this.gson = new Gson();
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        // Load existing tokens if available
        loadTokens();
        
        // Start token refresh scheduler
        startTokenRefreshScheduler();
    }

    // Derive a reasonable username if not provided
    private String deriveUsernameFromEmail(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }
    
    /**
     * Registers a new user in Supabase.
     * 
     * @param email User's email address
     * @param password User's password
     * @param fullName User's full name
     * @return PlayerProfile if registration successful
     * @throws SupabaseAuthException if registration fails
     */
    public PlayerProfile registerUser(String email, String password, String fullName) throws SupabaseAuthException {
        try {
            // Check if we're online
            if (!httpClient.isOnline()) {
                throw new SupabaseAuthException("No internet connection available", SupabaseAuthException.ErrorType.NETWORK_ERROR);
            }
            
            // Register with Supabase
            SupabaseHttpClient.SupabaseAuthResponse authResponse = httpClient.registerUser(email, password, fullName);
            
            // Store tokens
            this.currentAccessToken = authResponse.getAccessToken();
            this.currentRefreshToken = authResponse.getRefreshToken();
            this.tokenExpiryTime = LocalDateTime.now().plusSeconds(authResponse.getExpiresIn());
            
            // Save tokens to file
            saveTokens();
            
            // Fetch user profile; create if missing
            String userId = authResponse.getUser().getId();
            PlayerProfile profile;
            try {
                profile = httpClient.fetchUserProfile(currentAccessToken, userId);
            } catch (SupabaseHttpClient.SupabaseException e) {
                if (e.getStatusCode() == 404) {
                    // Create default profile in profiles table
                    PlayerProfile newProfile = new PlayerProfile();
                    newProfile.setId(userId);
                    newProfile.setEmail(email);
                    newProfile.setFullName(fullName);
                    newProfile.setUsername(deriveUsernameFromEmail(email));
                    newProfile.setScore(0);
                    newProfile.setLevel(1);
                    newProfile.setLastLogin(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    newProfile.setCreatedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    newProfile.setUpdatedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    httpClient.createUserProfile(currentAccessToken, newProfile);
                    profile = newProfile;
                } else {
                    throw e;
                }
            }
            this.currentProfile = profile;
            
            return profile;
            
        } catch (Exception e) {
            throw new SupabaseAuthException("Registration failed: " + e.getMessage(), SupabaseAuthException.ErrorType.AUTH_ERROR);
        }
    }
    
    /**
     * Authenticates a user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @return PlayerProfile if authentication successful, null otherwise
     * @throws SupabaseAuthException if authentication fails
     */
    public PlayerProfile authenticateUser(String email, String password) throws SupabaseAuthException {
        try {
            // Check if we're online
            if (!httpClient.isOnline()) {
                throw new SupabaseAuthException("No internet connection available", SupabaseAuthException.ErrorType.NETWORK_ERROR);
            }
            
            // Authenticate with Supabase
            SupabaseHttpClient.SupabaseAuthResponse authResponse = httpClient.authenticateUser(email, password);
            
            // Store tokens
            this.currentAccessToken = authResponse.getAccessToken();
            this.currentRefreshToken = authResponse.getRefreshToken();
            this.tokenExpiryTime = LocalDateTime.now().plusSeconds(authResponse.getExpiresIn());
            
            // Save tokens to file
            saveTokens();
            
            // Fetch user profile; create if missing
            String userId = authResponse.getUser().getId();
            PlayerProfile profile;
            try {
                profile = httpClient.fetchUserProfile(currentAccessToken, userId);
            } catch (SupabaseHttpClient.SupabaseException e) {
                if (e.getStatusCode() == 404) {
                    PlayerProfile newProfile = new PlayerProfile();
                    newProfile.setId(userId);
                    newProfile.setEmail(authResponse.getUser().getEmail());
                    String fullName = null;
                    java.util.Map<String, Object> meta = authResponse.getUser().getUserMetadata();
                    if (meta != null && meta.get("full_name") != null) {
                        fullName = String.valueOf(meta.get("full_name"));
                    }
                    newProfile.setFullName(fullName);
                    newProfile.setUsername(deriveUsernameFromEmail(authResponse.getUser().getEmail()));
                    newProfile.setScore(0);
                    newProfile.setLevel(1);
                    newProfile.setLastLogin(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    newProfile.setCreatedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    newProfile.setUpdatedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    httpClient.createUserProfile(currentAccessToken, newProfile);
                    profile = newProfile;
                } else {
                    throw e;
                }
            }
            
            // Mark as online profile
            profile.setOnline(true);
            profile.setLastSyncTime(LocalDateTime.now());
            this.currentProfile = profile;
            
            // Save profile locally for offline use
            saveProfileLocally(profile);
            
            return profile;
            
        } catch (SupabaseHttpClient.SupabaseException e) {
            throw new SupabaseAuthException("Authentication failed: " + e.getMessage(), SupabaseAuthException.ErrorType.AUTH_ERROR);
        } catch (Exception e) {
            throw new SupabaseAuthException("Unexpected error during authentication: " + e.getMessage(), SupabaseAuthException.ErrorType.UNKNOWN_ERROR);
        }
    }
    
    /**
     * Authenticates a user with Google OAuth
     * 
     * @param googleToken The Google OAuth token
     * @return PlayerProfile if authentication successful
     * @throws SupabaseAuthException if authentication fails
     */
    public PlayerProfile authenticateWithGoogle(String googleToken) throws SupabaseAuthException {
        try {
            // Check if we're online
            if (!httpClient.isOnline()) {
                throw new SupabaseAuthException("No internet connection available", SupabaseAuthException.ErrorType.NETWORK_ERROR);
            }
            
            // Authenticate with Supabase using Google token
            SupabaseHttpClient.SupabaseAuthResponse authResponse = httpClient.authenticateWithGoogle(googleToken);
            
            // Store tokens
            this.currentAccessToken = authResponse.getAccessToken();
            this.currentRefreshToken = authResponse.getRefreshToken();
            this.tokenExpiryTime = LocalDateTime.now().plusSeconds(authResponse.getExpiresIn());
            
            // Save tokens to file
            saveTokens();
            
            // Fetch user profile; create if missing
            String userId = authResponse.getUser().getId();
            PlayerProfile profile;
            try {
                profile = httpClient.fetchUserProfile(currentAccessToken, userId);
            } catch (SupabaseHttpClient.SupabaseException e) {
                if (e.getStatusCode() == 404) {
                    PlayerProfile newProfile = new PlayerProfile();
                    newProfile.setId(userId);
                    newProfile.setEmail(authResponse.getUser().getEmail());
                    java.util.Map<String, Object> meta = authResponse.getUser().getUserMetadata();
                    if (meta != null) {
                        Object name = meta.get("full_name");
                        if (name == null) name = meta.get("name");
                        if (name != null) newProfile.setFullName(String.valueOf(name));
                    }
                    newProfile.setUsername(deriveUsernameFromEmail(authResponse.getUser().getEmail()));
                    newProfile.setScore(0);
                    newProfile.setLevel(1);
                    newProfile.setLastLogin(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    newProfile.setCreatedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    newProfile.setUpdatedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    httpClient.createUserProfile(currentAccessToken, newProfile);
                    profile = newProfile;
                } else {
                    throw e;
                }
            }
            
            // Mark as online profile
            profile.setOnline(true);
            profile.setLastSyncTime(LocalDateTime.now());
            this.currentProfile = profile;
            
            // Save profile locally for offline use
            saveProfileLocally(profile);
            
            return profile;
            
        } catch (SupabaseHttpClient.SupabaseException e) {
            throw new SupabaseAuthException("Google authentication failed: " + e.getMessage(), SupabaseAuthException.ErrorType.AUTH_ERROR);
        } catch (Exception e) {
            throw new SupabaseAuthException("Unexpected error during Google authentication: " + e.getMessage(), SupabaseAuthException.ErrorType.UNKNOWN_ERROR);
        }
    }
    
    /**
     * Refreshes the current access token using the refresh token
     * 
     * @return true if refresh successful, false otherwise
     */
    public boolean refreshAccessToken() {
        if (currentRefreshToken == null) {
            return false;
        }
        
        try {
            SupabaseHttpClient.SupabaseAuthResponse authResponse = httpClient.refreshToken(currentRefreshToken);
            
            // Update tokens
            this.currentAccessToken = authResponse.getAccessToken();
            this.currentRefreshToken = authResponse.getRefreshToken();
            this.tokenExpiryTime = LocalDateTime.now().plusSeconds(authResponse.getExpiresIn());
            
            // Save updated tokens
            saveTokens();
            
            return true;
            
        } catch (SupabaseHttpClient.SupabaseException e) {
            System.err.println("Token refresh failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Syncs the current profile with Supabase
     * 
     * @return true if sync successful, false otherwise
     */
    public boolean syncProfileWithSupabase() {
        if (currentProfile == null || currentAccessToken == null) {
            return false;
        }
        
        try {
            // Check if we're online
            if (!httpClient.isOnline()) {
                return false;
            }
            
            // Update profile in Supabase
            boolean success = httpClient.updateUserProfile(currentAccessToken, currentProfile);
            
            if (success) {
                currentProfile.setLastSyncTime(LocalDateTime.now());
                saveProfileLocally(currentProfile);
            }
            
            return success;
            
        } catch (SupabaseHttpClient.SupabaseException e) {
            System.err.println("Profile sync failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates the current profile and syncs with Supabase
     * 
     * @param profile The updated profile
     * @return true if update and sync successful, false otherwise
     */
    public boolean updateProfile(PlayerProfile profile) {
        this.currentProfile = profile;
        
        // Try to sync with Supabase if online
        if (httpClient.isOnline() && currentAccessToken != null) {
            return syncProfileWithSupabase();
        } else {
            // Just save locally if offline
            saveProfileLocally(profile);
            return true;
        }
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
        return currentAccessToken != null && currentProfile != null;
    }
    
    /**
     * Checks if the current token is valid (not expired)
     * 
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid() {
        return currentAccessToken != null && 
               tokenExpiryTime != null && 
               LocalDateTime.now().isBefore(tokenExpiryTime);
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentAccessToken = null;
        this.currentRefreshToken = null;
        this.tokenExpiryTime = null;
        this.currentProfile = null;
        
        // Clear token file
        File tokenFile = new File(tokenFilePath);
        if (tokenFile.exists()) {
            tokenFile.delete();
        }
    }
    
    /**
     * Loads tokens from local file
     */
    private void loadTokens() {
        try {
            File tokenFile = new File(tokenFilePath);
            if (!tokenFile.exists()) {
                return;
            }
            
            String json = new String(java.nio.file.Files.readAllBytes(tokenFile.toPath()));
            JsonObject tokenData = JsonParser.parseString(json).getAsJsonObject();
            
            this.currentAccessToken = tokenData.get("access_token").getAsString();
            this.currentRefreshToken = tokenData.get("refresh_token").getAsString();
            this.tokenExpiryTime = LocalDateTime.parse(tokenData.get("expires_at").getAsString());
            
            // Check if token is still valid
            if (LocalDateTime.now().isAfter(tokenExpiryTime)) {
                // Try to refresh token
                if (!refreshAccessToken()) {
                    // If refresh fails, clear tokens
                    logout();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading tokens: " + e.getMessage());
            logout();
        }
    }
    
    /**
     * Saves tokens to local file
     */
    private void saveTokens() {
        try {
            JsonObject tokenData = new JsonObject();
            tokenData.addProperty("access_token", currentAccessToken);
            tokenData.addProperty("refresh_token", currentRefreshToken);
            tokenData.addProperty("expires_at", tokenExpiryTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            try (FileWriter writer = new FileWriter(tokenFilePath)) {
                writer.write(gson.toJson(tokenData));
            }
            
        } catch (IOException e) {
            System.err.println("Error saving tokens: " + e.getMessage());
        }
    }
    
    /**
     * Saves profile locally for offline use
     */
    private void saveProfileLocally(PlayerProfile profile) {
        try {
            String profileJson = profile.toJson();
            try (FileWriter writer = new FileWriter("player_profile.json")) {
                writer.write(profileJson);
            }
        } catch (IOException e) {
            System.err.println("Error saving profile locally: " + e.getMessage());
        }
    }
    
    /**
     * Starts the token refresh scheduler
     */
    private void startTokenRefreshScheduler() {
        // Check token validity every 5 minutes
        scheduler.scheduleAtFixedRate(() -> {
            if (isAuthenticated() && !isTokenValid()) {
                System.out.println("Token expired, attempting refresh...");
                if (!refreshAccessToken()) {
                    System.out.println("Token refresh failed, user needs to re-authenticate");
                    logout();
                }
            }
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * Shuts down the service and cleans up resources
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
    
    /**
     * Exception class for Supabase authentication errors
     */
    public static class SupabaseAuthException extends Exception {
        public enum ErrorType {
            NETWORK_ERROR,
            AUTH_ERROR,
            TOKEN_ERROR,
            UNKNOWN_ERROR
        }
        
        private final ErrorType errorType;
        
        public SupabaseAuthException(String message, ErrorType errorType) {
            super(message);
            this.errorType = errorType;
        }
        
        public ErrorType getErrorType() {
            return errorType;
        }
    }
}
