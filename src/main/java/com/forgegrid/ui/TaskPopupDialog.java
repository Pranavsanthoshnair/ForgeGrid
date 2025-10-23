package com.forgegrid.ui;

import com.forgegrid.model.HardcodedTask;
import com.forgegrid.controller.DashboardController;
import com.forgegrid.model.PlayerProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Dialog that shows a single task with timer and completion options
 */
public class TaskPopupDialog extends JDialog {
    
    // Basic colors removed; rely on defaults
    
    private HardcodedTask task;
    private long startTime;
    private javax.swing.Timer uiTimer;
    
    private JLabel timerLabel;
    private Dashboard parent;
    
    public TaskPopupDialog(Dashboard parent, HardcodedTask task, long startTime) {
        super(parent, true);
        this.parent = parent;
        this.task = task;
        this.startTime = startTime;
        
        setUndecorated(false);
        setSize(520, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(238, 238, 238));
        
        setupUI();
        startUITimer();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(238, 238, 238));
        
        // Simple title via dialog title
        setTitle("Task");
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header with timer
        JLabel titleLabel = new JLabel("Task");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timerLabel.setForeground(Color.DARK_GRAY);
        timerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Task details card
        JPanel taskCard = new JPanel();
        taskCard.setLayout(new BoxLayout(taskCard, BoxLayout.Y_AXIS));
        taskCard.setBackground(Color.WHITE);
        taskCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Task name
        JLabel nameLabel = new JLabel(task.getTaskName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskCard.add(nameLabel);
        
        taskCard.add(Box.createVerticalStrut(15));
        
        // Description
        JTextArea descArea = new JTextArea(task.getDescription());
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea.setForeground(Color.DARK_GRAY);
        descArea.setBackground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskCard.add(descArea);
        
        taskCard.add(Box.createVerticalStrut(20));
        
        // Metadata panel
        JPanel metaPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaPanel.setMaximumSize(new Dimension(500, 80));
        
        metaPanel.add(createMetaItem("Language", task.getLanguage(), "💻"));
        metaPanel.add(createMetaItem("Level", task.getLevel(), "📚"));
        metaPanel.add(createMetaItem("XP Reward", task.getXpReward() + " XP", "⭐"));
        metaPanel.add(createMetaItem("Est. Time", task.getEstimatedMinutes() + " min", "⏱"));
        
        taskCard.add(metaPanel);
        
        // Warning label
        taskCard.add(Box.createVerticalStrut(15));
        JLabel warningLabel = new JLabel("Timer is tracking your work. Submit only after completing the task.");
        warningLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        warningLabel.setForeground(Color.GRAY);
        warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskCard.add(warningLabel);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        
        JButton submitBtn = new JButton("Submit Task");
        submitBtn.setUI(new BasicButtonUI());
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setBackground(new Color(34, 197, 94));
        submitBtn.setBorderPainted(false);
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(140, 36));
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
        
        JButton skipBtn = new JButton("Skip Task");
        skipBtn.setUI(new BasicButtonUI());
        skipBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        skipBtn.setForeground(Color.WHITE);
        skipBtn.setBackground(new Color(251, 146, 60));
        skipBtn.setBorderPainted(false);
        skipBtn.setFocusPainted(false);
        skipBtn.setPreferredSize(new Dimension(140, 36));
        skipBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSkip();
            }
        });
        
        buttonsPanel.add(submitBtn);
        buttonsPanel.add(skipBtn);
        
        // Assemble content (vertical order)
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(timerLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(taskCard);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(buttonsPanel);
        
        // Assemble main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(contentPanel, gbc);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createMetaItem(String label, String value, String emoji) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        labelText.setForeground(Color.GRAY);
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("SansSerif", Font.BOLD, 13));
        valueText.setForeground(Color.BLACK);
        
        textPanel.add(labelText);
        textPanel.add(valueText);
        
        panel.add(emojiLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void startUITimer() {
        uiTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerDisplay();
            }
        });
        uiTimer.start();
    }
    
    private void updateTimerDisplay() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000; // seconds
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        timerLabel.setText(String.format("⏱ %02d:%02d", minutes, seconds));
    }
    
    
    
    private void handleSubmit() {
        if (uiTimer != null) uiTimer.stop();
        long elapsedMillis = System.currentTimeMillis() - startTime;
        int elapsedMinutes = (int) (elapsedMillis / 60000);
        
        // Check if submitted too quickly (less than 10% of estimated time)
        int minTime = Math.max(1, task.getEstimatedMinutes() / 10);
        
        if (elapsedMinutes < minTime) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "You completed this task very quickly (" + elapsedMinutes + " min)!\n" +
                "Expected time: " + task.getEstimatedMinutes() + " min.\n\n" +
                "Are you sure you want to submit?",
                "Quick Completion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Save completed task
        DashboardController ctrl = new DashboardController(new com.forgegrid.service.HardcodedTaskService(), new com.forgegrid.service.LevelService());
        PlayerProfile profile = parent.profile;
        
        boolean success = ctrl.saveCompletedTask(
            profile.getUsername(),
            task.getTaskName(),
            Math.max(1, elapsedMinutes),
            task.getXpReward()
        );
        
                        if (success) {
            // Add XP and check for level up
            com.forgegrid.service.LevelService.LevelUpResult result = new com.forgegrid.service.LevelService().addXP(profile.getUsername(), task.getXpReward());
            
            // Refresh parent dashboard
            parent.completedTaskNames.add(task.getTaskName());
            parent.loadedViews.put(Dashboard.VIEW_TASKS, false);
            parent.loadedViews.put(Dashboard.VIEW_DASHBOARD, false);
            
            // Update parent's display
            parent.refreshHeaderAfterXPChange();
            
            dispose();
            
            // Show level up notification if leveled up
            String message;
            if (result != null && result.leveledUp) {
                message = "🎉 LEVEL UP! 🎉\n\n" +
                         "Level " + result.oldLevel + " → Level " + result.newLevel + "\n\n" +
                         "Task completed: +" + task.getXpReward() + " XP\n" +
                         "Time taken: " + elapsedMinutes + " min\n" +
                         "Total XP: " + result.totalXP + "\n\n" +
                         "Start next task?";
            } else {
                message = "🎉 Task completed!\n\n" +
                         "You earned " + task.getXpReward() + " XP!\n" +
                         "Time taken: " + elapsedMinutes + " min\n\n" +
                         "Start next task?";
            }
            
            // Show success and ask if they want to continue
            int choice = JOptionPane.showOptionDialog(
                parent,
                message,
                result != null && result.leveledUp ? "LEVEL UP!" : "Task Completed",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Next Task", "Return to Dashboard"},
                "Next Task"
            );
            
            if (choice == 0) {
                // Refresh view and show next task
                parent.switchView(Dashboard.VIEW_TASKS);
                SwingUtilities.invokeLater(() -> parent.showTaskPopup());
            } else {
                parent.switchView(Dashboard.VIEW_TASKS);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save task. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            
        }
    }
    
    private void handleSkip() {
        if (uiTimer != null) uiTimer.stop();
        int result = JOptionPane.showConfirmDialog(
            this,
            "Skip this task?\n\n" +
            "⚠️ You will lose 50% of the XP (" + (task.getXpReward() / 2) + " XP penalty)\n\n" +
            "Are you sure?",
            "Skip Task",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result != JOptionPane.YES_OPTION) {
            if (uiTimer != null) uiTimer.start();
            return;
        }
        
        long elapsedMillis = System.currentTimeMillis() - startTime;
        int elapsedMinutes = Math.max(1, (int) (elapsedMillis / 60000));
        
        // Save skipped task with negative XP
        DashboardController ctrl = new DashboardController(new com.forgegrid.service.HardcodedTaskService(), new com.forgegrid.service.LevelService());
        PlayerProfile profile = parent.profile;
        
        int xpPenalty = -(task.getXpReward() / 2);
        
        boolean success = new com.forgegrid.service.HardcodedTaskService().saveSkippedTask(
            profile.getUsername(),
            task.getTaskName(),
            elapsedMinutes,
            xpPenalty
        );
        
        if (success) {
            // Apply XP penalty
            new com.forgegrid.service.LevelService().addXP(profile.getUsername(), xpPenalty);
            
            // Mark as completed so it doesn't show again
            parent.completedTaskNames.add(task.getTaskName());
            parent.loadedViews.put(Dashboard.VIEW_TASKS, false);
            parent.loadedViews.put(Dashboard.VIEW_DASHBOARD, false);
            
            // Update parent's display
            parent.refreshHeaderAfterXPChange();
            
            dispose();
            
            // Show result and ask if they want to continue
            int choice = JOptionPane.showOptionDialog(
                parent,
                "⏭ Task skipped\n\n" +
                "XP penalty: " + xpPenalty + " XP\n" +
                "Time spent: " + elapsedMinutes + " min\n\n" +
                "Start next task?",
                "Task Skipped",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"Next Task", "Return to Dashboard"},
                "Next Task"
            );
            
            if (choice == 0) {
                // Refresh view and show next task
                parent.switchView(Dashboard.VIEW_TASKS);
                SwingUtilities.invokeLater(() -> parent.showTaskPopup());
            } else {
                parent.switchView(Dashboard.VIEW_TASKS);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to skip task. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            
        }
    }
    
    @Override
    public void dispose() {
        if (uiTimer != null) uiTimer.stop();
        super.dispose();
    }
}

