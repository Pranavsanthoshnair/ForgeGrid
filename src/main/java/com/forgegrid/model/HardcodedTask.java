package com.forgegrid.model;

/**
 * Simple POJO representing a hardcoded task
 */
public class HardcodedTask {
    private String taskName;
    private String description;
    private String language;
    private String level;
    private int xpReward;
    private int estimatedMinutes;
    
    public HardcodedTask(String taskName, String description, String language, 
                         String level, int xpReward, int estimatedMinutes) {
        this.taskName = taskName;
        this.description = description;
        this.language = language;
        this.level = level;
        this.xpReward = xpReward;
        this.estimatedMinutes = estimatedMinutes;
    }
    
    // Getters
    public String getTaskName() {
        return taskName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public String getLevel() {
        return level;
    }
    
    public int getXpReward() {
        return xpReward;
    }
    
    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }
    
    @Override
    public String toString() {
        return taskName + " (" + language + " - " + level + ")";
    }
}

