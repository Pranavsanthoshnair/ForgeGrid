package com.forgegrid.controller;

import com.forgegrid.model.HardcodedTask;
import com.forgegrid.model.TaskHistoryEntry;
import com.forgegrid.service.HardcodedTaskService;
import com.forgegrid.service.LevelService;

import java.util.List;
import java.util.Set;

public class DashboardController {

    private final HardcodedTaskService taskService;
    private final LevelService levelService;

    public DashboardController(HardcodedTaskService taskService, LevelService levelService) {
        this.taskService = taskService;
        this.levelService = levelService;
    }

    public LevelService.LevelInfo getLevelInfo(String username) { return levelService.getLevelInfo(username); }
    public List<TaskHistoryEntry> getTaskHistory(String username, int limit) { return taskService.getTaskHistory(username, limit); }
    public List<HardcodedTask> getTasksFor(String language, String level) { return taskService.getTasksForUser(language, level); }
    public int getSkippedTaskCount(String username) { return taskService.getSkippedTaskCount(username); }
    public int getNetXP(String username) { return taskService.getNetXP(username); }
    public Set<String> getRecordedTaskNames(String username) { return taskService.getRecordedTaskNames(username); }
    public void recordAssignedTask(String username, String taskName) { taskService.recordAssignedTask(username, taskName); }
    public void autoSkipExpired(String username, String language, String level) { taskService.autoSkipExpiredAssignedTasks(username, language, level); }
    public java.util.List<com.forgegrid.model.GoatedTask> listGoatedTasks(String username) { return taskService.listGoatedTasks(username); }
    public boolean markGoatedTaskComplete(String username, int taskId) { return taskService.markGoatedTaskComplete(username, taskId); }
    public boolean createGoatedTask(String username, String title, String desc, java.time.LocalDateTime deadline, int xp) { return taskService.createGoatedTask(username, title, desc, deadline, xp); }
    public boolean saveCompletedTask(String username, String taskName, int timeTaken, int xpEarned) { return taskService.saveCompletedTask(username, taskName, timeTaken, xpEarned); }
    public int getCompletedTaskCount(String username) { return taskService.getCompletedTaskCount(username); }
}


