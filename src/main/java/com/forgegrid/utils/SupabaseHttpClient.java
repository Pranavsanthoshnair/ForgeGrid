package com.forgegrid.utils;

import com.forgegrid.model.PlayerProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP client utility for making API calls to Supabase.
 * Handles authentication, token management, and common API operations.
 */
public class SupabaseHttpClient {
    
    private final HttpClient httpClient;
    private final String supabaseUrl;
    private final String supabaseAnonKey;
    private final Gson gson;
    
    /**
     * Constructor for SupabaseHttpClient
     * 
     * @param supabaseUrl The base URL of your Supabase project (e.g., https://your-project.supabase.co)
     * @param supabaseAnonKey The anonymous key from your Supabase project settings
     */
    public SupabaseHttpClient(String supabaseUrl, String supabaseAnonKey) {
        // Ensure URL ends with a slash
        this.supabaseUrl = supabaseUrl.endsWith("/") ? supabaseUrl : supabaseUrl + "/";
        this.supabaseAnonKey = supabaseAnonKey;
        this.gson = new Gson();
        
        // Create HTTP client with timeout settings
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    /**
     * Authenticates a user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @return SupabaseAuthResponse containing tokens and user info
     * @throws SupabaseException if authentication fails
     */
    public SupabaseAuthResponse authenticateUser(String email, String password) throws SupabaseException {
        try {
            // Prepare request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("email", email);
            requestBody.addProperty("password", password);
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "auth/v1/token?grant_type=password"))
                    .header("Content-Type", "application/json")
                    .header("apikey", supabaseAnonKey)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Handle response
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), SupabaseAuthResponse.class);
            } else {
                JsonObject errorJson = JsonParser.parseString(response.body()).getAsJsonObject();
                String errorMessage = errorJson.has("error_description") 
                    ? errorJson.get("error_description").getAsString()
                    : "Authentication failed";
                throw new SupabaseException("Authentication failed: " + errorMessage, response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new SupabaseException("Network error during authentication: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Registers a new user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @param fullName User's full name
     * @return SupabaseAuthResponse containing tokens and user info
     * @throws SupabaseException if registration fails
     */
    public SupabaseAuthResponse registerUser(String email, String password, String fullName) throws SupabaseException {
        try {
            // Prepare request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("email", email);
            requestBody.addProperty("password", password);
            
            // Add user metadata
            JsonObject userMetadata = new JsonObject();
            userMetadata.addProperty("full_name", fullName);
            requestBody.add("data", userMetadata);
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "auth/v1/signup"))
                    .header("Content-Type", "application/json")
                    .header("apikey", supabaseAnonKey)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Handle response
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return gson.fromJson(response.body(), SupabaseAuthResponse.class);
            } else {
                JsonObject errorJson = JsonParser.parseString(response.body()).getAsJsonObject();
                String errorMessage = errorJson.has("msg") 
                    ? errorJson.get("msg").getAsString()
                    : "Registration failed";
                throw new SupabaseException("Registration failed: " + errorMessage, response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new SupabaseException("Network error during registration: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Refreshes an access token using a refresh token
     * 
     * @param refreshToken The refresh token
     * @return SupabaseAuthResponse containing new tokens
     * @throws SupabaseException if token refresh fails
     */
    public SupabaseAuthResponse refreshToken(String refreshToken) throws SupabaseException {
        try {
            // Prepare request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("refresh_token", refreshToken);
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "auth/v1/token?grant_type=refresh_token"))
                    .header("Content-Type", "application/json")
                    .header("apikey", supabaseAnonKey)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Handle response
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), SupabaseAuthResponse.class);
            } else {
                JsonObject errorJson = JsonParser.parseString(response.body()).getAsJsonObject();
                String errorMessage = errorJson.has("error_description") 
                    ? errorJson.get("error_description").getAsString()
                    : "Token refresh failed";
                throw new SupabaseException("Token refresh failed: " + errorMessage, response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new SupabaseException("Network error during token refresh: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Fetches a user profile from the profiles table
     * 
     * @param accessToken The JWT access token
     * @param userId The user ID to fetch profile for
     * @return PlayerProfile object
     * @throws SupabaseException if profile fetch fails
     */
    public PlayerProfile fetchUserProfile(String accessToken, String userId) throws SupabaseException {
        try {
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "rest/v1/profiles?id=eq." + userId))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("apikey", supabaseAnonKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Handle response
            if (response.statusCode() == 200) {
                // PostgREST returns a JSON array. Parse and read first element if present.
                var jsonElement = JsonParser.parseString(response.body());
                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                    JsonObject profileData = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
                    return gson.fromJson(profileData, PlayerProfile.class);
                } else {
                    throw new SupabaseException("Profile not found for user: " + userId, 404);
                }
            } else if (response.statusCode() == 404) {
                throw new SupabaseException("Profile not found for user: " + userId, 404);
            } else {
                throw new SupabaseException("Failed to fetch profile: " + response.body(), response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new SupabaseException("Network error during profile fetch: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Updates a user profile in the profiles table
     * 
     * @param accessToken The JWT access token
     * @param profile The profile data to update
     * @return true if update successful
     * @throws SupabaseException if profile update fails
     */
    public boolean updateUserProfile(String accessToken, PlayerProfile profile) throws SupabaseException {
        try {
            // Prepare request body (exclude local-only fields)
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", profile.getUsername());
            requestBody.addProperty("email", profile.getEmail());
            requestBody.addProperty("full_name", profile.getFullName());
            requestBody.addProperty("score", profile.getScore());
            requestBody.addProperty("level", profile.getLevel());
            requestBody.addProperty("achievements", profile.getAchievements());
            requestBody.addProperty("last_login", profile.getLastLogin());
            requestBody.addProperty("updated_at", profile.getUpdatedAt());
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "rest/v1/profiles?id=eq." + profile.getId()))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("apikey", supabaseAnonKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=minimal")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Handle response
            if (response.statusCode() == 204 || response.statusCode() == 200) {
                return true;
            } else {
                throw new SupabaseException("Failed to update profile: " + response.body(), response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new SupabaseException("Network error during profile update: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Creates a new user profile in the profiles table
     * 
     * @param accessToken The JWT access token
     * @param profile The profile data to create
     * @return true if creation successful
     * @throws SupabaseException if profile creation fails
     */
    public boolean createUserProfile(String accessToken, PlayerProfile profile) throws SupabaseException {
        try {
            // Prepare request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("id", profile.getId());
            requestBody.addProperty("username", profile.getUsername());
            requestBody.addProperty("email", profile.getEmail());
            requestBody.addProperty("full_name", profile.getFullName());
            requestBody.addProperty("score", profile.getScore());
            requestBody.addProperty("level", profile.getLevel());
            requestBody.addProperty("achievements", profile.getAchievements());
            requestBody.addProperty("last_login", profile.getLastLogin());
            requestBody.addProperty("created_at", profile.getCreatedAt());
            requestBody.addProperty("updated_at", profile.getUpdatedAt());
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "rest/v1/profiles"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("apikey", supabaseAnonKey)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=minimal")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Handle response
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                return true;
            } else {
                throw new SupabaseException("Failed to create profile: " + response.body(), response.statusCode());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new SupabaseException("Network error during profile creation: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Authenticates a user with Google OAuth using Supabase's token endpoint.
     * 
     * Why JSON body instead of form data?
     * - Supabase's auth API expects a JSON payload for OAuth token exchange
     * - JSON provides better structure for complex data and is more widely used in modern APIs
     * - It's the standard format for OAuth 2.0 token exchange in many implementations
     * 
     * Why grant_type as query parameter?
     * - The grant_type is part of the OAuth 2.0 specification for token endpoints
     * - Including it in the URL makes the endpoint more RESTful and explicit
     * - It helps with API versioning and documentation
     *
     * @param googleToken The Google ID token obtained from Google Sign-In
     * @return SupabaseAuthResponse containing tokens and user info if authentication was successful
     * @throws SupabaseException if authentication fails or network error occurs
     */
    public SupabaseAuthResponse authenticateWithGoogle(String googleToken) throws SupabaseException {
        HttpURLConnection connection = null;
        try {
            // Validate input
            if (googleToken == null || googleToken.trim().isEmpty()) {
                throw new SupabaseException("Google token cannot be empty", 400);
            }
            
            // Check internet connection
            if (!isOnline()) {
                throw new SupabaseException("No internet connection available", 0);
            }
            
            // Build the URL with grant_type as query parameter
            String endpoint = String.format("%sauth/v1/token?grant_type=id_token", supabaseUrl);
            URI uri = new URI(endpoint);
            
            // Set up the connection
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("apikey", supabaseAnonKey);
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            // Create JSON request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("provider", "google");
            requestBody.addProperty("id_token", googleToken);
            
            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Get response
            int responseCode = connection.getResponseCode();
            String responseBody;
            
            // Read response body (success or error)
            try (java.util.Scanner scanner = new java.util.Scanner(
                    responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(),
                    StandardCharsets.UTF_8)) {
                responseBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "{}";
            }
            
            // Log response for debugging
            System.out.println("Supabase response status: " + responseCode);
            // Don't log the full response as it may contain sensitive data
            
            // Handle response
            if (responseCode == 200) {
                // Success - parse and return the response
                return gson.fromJson(responseBody, SupabaseAuthResponse.class);
            } else {
                // Parse error response
                JsonObject errorJson = JsonParser.parseString(responseBody).getAsJsonObject();
                String errorCode = errorJson.has("error") ? errorJson.get("error").getAsString() : "unknown_error";
                String errorMessage = errorJson.has("error_description") 
                    ? errorJson.get("error_description").getAsString()
                    : "Authentication failed";
                    
                // Map common error types to more user-friendly messages
                switch (errorCode) {
                    case "invalid_grant":
                        errorMessage = "Invalid or expired token. Please sign in again.";
                        break;
                    case "unauthorized_client":
                        errorMessage = "This application is not authorized to use Google Sign-In.";
                        break;
                    case "invalid_request":
                        errorMessage = "Invalid authentication request. Please try again.";
                        break;
                }
                
                throw new SupabaseException(errorMessage, responseCode);
            }
            
        } catch (IOException | URISyntaxException e) {
            // Handle network and parsing errors
            String errorMessage = "Network error during authentication";
            if (e instanceof java.net.ConnectException) {
                errorMessage = "Could not connect to the authentication server";
            } else if (e instanceof java.net.SocketTimeoutException) {
                errorMessage = "Connection to authentication server timed out";
            } else if (e instanceof java.net.UnknownHostException) {
                errorMessage = "Could not resolve authentication server";
            }
            throw new SupabaseException(errorMessage + ": " + e.getMessage(), 0);
        } finally {
            // Ensure connection is closed
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Checks if the device is online by making a simple request to Supabase
     * 
     * @return true if online, false if offline
     */
    public boolean isOnline() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "rest/v1/"))
                    .header("apikey", supabaseAnonKey)
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
            
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
    
    /**
     * Exception class for Supabase API errors
     */
    public static class SupabaseException extends Exception {
        private final int statusCode;
        
        public SupabaseException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }
    
    /**
     * Response class for Supabase authentication
     */
    public static class SupabaseAuthResponse {
        @com.google.gson.annotations.SerializedName("access_token")
        private String accessToken;
        
        @com.google.gson.annotations.SerializedName("refresh_token")
        private String refreshToken;
        
        @com.google.gson.annotations.SerializedName("expires_in")
        private int expiresIn;
        
        @com.google.gson.annotations.SerializedName("token_type")
        private String tokenType;
        
        @com.google.gson.annotations.SerializedName("user")
        private SupabaseUser user;
        
        // Getters
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public int getExpiresIn() { return expiresIn; }
        public String getTokenType() { return tokenType; }
        public SupabaseUser getUser() { return user; }
    }
    
    /**
     * User class for Supabase authentication response
     */
    public static class SupabaseUser {
        @com.google.gson.annotations.SerializedName("id")
        private String id;
        
        @com.google.gson.annotations.SerializedName("email")
        private String email;
        
        @com.google.gson.annotations.SerializedName("user_metadata")
        private Map<String, Object> userMetadata;
        
        // Getters
        public String getId() { return id; }
        public String getEmail() { return email; }
        public Map<String, Object> getUserMetadata() { return userMetadata; }
    }
}
