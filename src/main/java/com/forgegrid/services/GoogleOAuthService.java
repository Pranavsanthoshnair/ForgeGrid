package com.forgegrid.services;

import com.forgegrid.config.AppConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * Service for handling Google OAuth authentication with automatic callback handling
 */
public class GoogleOAuthService {
    private static final int LOCAL_SERVER_PORT = 8080;
    private static final String LOCAL_REDIRECT_URI = "http://localhost:" + LOCAL_SERVER_PORT + "/callback";
    
    private final AppConfig config;
    private final ExecutorService serverExecutor;
    
    public GoogleOAuthService() {
        this.config = AppConfig.getInstance();
        this.serverExecutor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * Handles Google OAuth flow with a local server to capture the callback
     */
    public CompletableFuture<String> authenticateWithGoogle() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create OAuth URL
                String authUrl = String.format(
                    "https://accounts.google.com/o/oauth2/v2/auth?" +
                    "client_id=%s&" +
                    "redirect_uri=%s&" +
                    "response_type=code&" +
                    "scope=%s&" +
                    "access_type=offline",
                    URLEncoder.encode(config.getGoogleOAuthClientId(), StandardCharsets.UTF_8),
                    URLEncoder.encode(LOCAL_REDIRECT_URI, StandardCharsets.UTF_8),
                    URLEncoder.encode(getGoogleOAuthScope(), StandardCharsets.UTF_8)
                );
                
                System.out.println("Opening Google OAuth URL in browser...");
                System.out.println("Make sure your Google OAuth credentials have " + LOCAL_REDIRECT_URI + " as an authorized redirect URI");
                
                // Start local server to handle the callback
                CompletableFuture<String> authFuture = new CompletableFuture<>();
                
                try {
                    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
                        new InetSocketAddress(LOCAL_SERVER_PORT), 0);
                    
                    server.createContext("/callback", exchange -> {
                        try {
                            // Parse the authorization code from the request
                            String query = exchange.getRequestURI().getQuery();
                            String code = parseCodeFromQuery(query);
                            
                            if (code != null) {
                                // Send success response
                                String response = "<html><body><h1>Success!</h1><p>You can close this window and return to the application.</p></body></html>";
                                exchange.sendResponseHeaders(200, response.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                }
                                
                                // Complete the future with the auth code
                                authFuture.complete(code);
                            } else {
                                // Send error response
                                String error = "No authorization code received";
                                String response = "<html><body><h1>Error</h1><p>" + error + "</p></body></html>";
                                exchange.sendResponseHeaders(400, response.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                }
                                authFuture.completeExceptionally(new RuntimeException(error));
                            }
                        } catch (Exception e) {
                            authFuture.completeExceptionally(e);
                        } finally {
                            // Stop the server after handling the request
                            server.stop(0);
                        }
                    });
                    
                    server.setExecutor(serverExecutor);
                    server.start();
                    System.out.println("Local server started on port " + LOCAL_SERVER_PORT);
                    
                    // Open browser for user to log in
                    Desktop.getDesktop().browse(URI.create(authUrl));
                    
                    // Wait for the auth code with a timeout
                    try {
                        String code = authFuture.get(5, TimeUnit.MINUTES);
                        System.out.println("Successfully received authorization code");
                        
                        // Exchange code for ID token
                        return exchangeCodeForToken(code);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to complete Google OAuth: " + e.getMessage(), e);
                    }
                    
                } catch (IOException e) {
                    throw new RuntimeException("Failed to start local server: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                throw new RuntimeException("Google OAuth authentication failed: " + e.getMessage(), e);
            }
        });
    }
    
    private String parseCodeFromQuery(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && "code".equals(pair.substring(0, idx))) {
                return pair.substring(idx + 1);
            }
        }
        return null;
    }
    
    /**
     * Exchanges authorization code for ID token
     */
    private String exchangeCodeForToken(String authCode) throws Exception {
        System.out.println("Exchanging authorization code for token...");
        
        String tokenUrl = "https://oauth2.googleapis.com/token";
        
        // Prepare request body
        String requestBody = String.format(
            "client_id=%s&" +
            "client_secret=%s&" +
            "code=%s&" +
            "grant_type=authorization_code&" +
            "redirect_uri=%s",
            URLEncoder.encode(config.getGoogleOAuthClientId(), StandardCharsets.UTF_8),
            URLEncoder.encode(config.getGoogleOAuthClientSecret(), StandardCharsets.UTF_8),
            URLEncoder.encode(authCode, StandardCharsets.UTF_8),
            URLEncoder.encode(LOCAL_REDIRECT_URI, StandardCharsets.UTF_8)
        );
        
        System.out.println("Token exchange request to Google...");
        
        // Make HTTP request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tokenUrl))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Token exchange response status: " + response.statusCode());
        
        if (response.statusCode() == 200) {
            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
            // Return the id_token for Supabase authentication
            String idToken = responseJson.get("id_token").getAsString();
            System.out.println("Successfully obtained id_token for Supabase authentication");
            return idToken;
        } else {
            throw new RuntimeException("Failed to exchange code for token: " + response.body());
        }
    }
    
    private String getGoogleOAuthScope() {
        return "email profile openid";
    }
    
    public void shutdown() {
        serverExecutor.shutdown();
        try {
            if (!serverExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                serverExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            serverExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}