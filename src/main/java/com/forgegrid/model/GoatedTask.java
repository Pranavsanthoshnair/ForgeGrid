package com.forgegrid.model;

public class GoatedTask {
    private final int id;
    private final String title;
    private final String description;
    private final java.sql.Timestamp deadline;
    private final int xp;
    private final boolean isCompleted;
    private final java.sql.Timestamp createdAt;

    public GoatedTask(int id, String title, String description, java.sql.Timestamp deadline, int xp, boolean isCompleted, java.sql.Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.xp = xp;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public java.sql.Timestamp getDeadline() { return deadline; }
    public int getXp() { return xp; }
    public boolean isCompleted() { return isCompleted; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
}


