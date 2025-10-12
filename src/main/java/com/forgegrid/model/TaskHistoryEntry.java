package com.forgegrid.model;

/**
 * Represents a task history entry from the database
 */
public class TaskHistoryEntry {
    public String taskName;
    public int timeTaken;
    public int xpEarned;
    public String status; // "completed" or "skipped"
    public String timestamp;
    
    public TaskHistoryEntry(String taskName, int timeTaken, int xpEarned, String status, String timestamp) {
        this.taskName = taskName;
        this.timeTaken = timeTaken;
        this.xpEarned = xpEarned;
        this.status = status;
        this.timestamp = timestamp;
    }
}

