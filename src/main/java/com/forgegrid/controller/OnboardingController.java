package com.forgegrid.controller;

import com.forgegrid.service.UserService;

public class OnboardingController {

    private final UserService userService;

    public OnboardingController(UserService userService) {
        this.userService = userService;
    }

    public boolean hasCompletedOnboarding(String username) {
        return userService.hasCompletedOnboardingByUsername(username);
    }

    public String[] getOnboardingData(String username) {
        return userService.getOnboardingDataByUsername(username);
    }

    public boolean saveOnboardingData(String username, String goal, String language, String skill) {
        return userService.saveOnboardingDataByUsername(username, goal, language, skill);
    }
}


