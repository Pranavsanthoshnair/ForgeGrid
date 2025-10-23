package com.forgegrid.controller;

import com.forgegrid.auth.AuthService;
import com.forgegrid.config.UserPreferences;
import com.forgegrid.model.PlayerProfile;
import com.forgegrid.service.UserService;

public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserPreferences userPreferences;

    public AuthController(AuthService authService, UserService userService, UserPreferences userPreferences) {
        this.authService = authService;
        this.userService = userService;
        this.userPreferences = userPreferences;
    }

    public PlayerProfile login(String username, String password) { return authService.login(username, password); }
    public boolean register(String name, String email, String password) { return authService.register(name, email, password); }
    public boolean usernameExists(String username) { return authService.usernameExists(username); }
    public boolean resetPassword(String username, String newPassword) { return authService.resetPassword(username, newPassword); }
    public boolean hasCompletedOnboarding(String username) { return userService.hasCompletedOnboardingByUsername(username); }
    public boolean saveOnboardingData(String username, String goal, String language, String skill) { return userService.saveOnboardingDataByUsername(username, goal, language, skill); }
    public String getLastUsername() { return userPreferences.getLastUsername(); }
    public boolean isRememberMeEnabled() { return userPreferences.isRememberMeEnabled(); }
    public String getSavedUsername() { return userPreferences.getSavedUsername(); }
    public String getSavedPassword() { return userPreferences.getSavedPassword(); }
    public void setLastUsername(String username) { userPreferences.setLastUsername(username); }
    public void saveRememberMeCredentials(String username, String password) { userPreferences.saveRememberMeCredentials(username, password); }
    public void clearRememberMe() { userPreferences.clearRememberMe(); }
}


