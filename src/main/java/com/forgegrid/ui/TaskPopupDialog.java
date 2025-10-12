package com.forgegrid.ui;

import com.forgegrid.model.HardcodedTask;
import com.forgegrid.service.HardcodedTaskService;
import com.forgegrid.service.LevelService;
import com.forgegrid.model.PlayerProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog that shows a single task with timer and completion options
 */
public class TaskPopupDialog extends JDialog {
    
    private static final Color BG_COLOR = new Color(25, 30, 40);
    private static final Color PANEL_COLOR = new Color(40, 50, 65);
    private static final Color ACCENT_COLOR = new Color(100, 180, 220);
    private static final Color TEXT_COLOR = new Color(220, 225, 235);
    private static final Color TEXT_SECONDARY = new Color(160, 170, 185);
    
    private HardcodedTask task;
    private long startTime;
    private Timer uiTimer;
    private JLabel timerLabel;
    private Dashboard parent;
    
    public TaskPopupDialog(Dashboard parent, HardcodedTask task, long startTime) {
        super(parent, true);
        this.parent = parent;
        this.task = task;
        this.startTime = startTime;
        
        setUndecorated(true); // Remove default white title bar
        setSize(600, 550); // Slightly taller for custom title bar
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        setupUI();
        startUITimer();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 110, 130), 2));
        
        // Custom title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(30, 35, 45));
        titleBar.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleBarLabel = new JLabel("📋 Current Task");
        titleBarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleBarLabel.setForeground(TEXT_COLOR);
        
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(TEXT_COLOR);
        closeButton.setBackground(new Color(30, 35, 45));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(40, 30));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            if (uiTimer != null) uiTimer.stop();
            dispose();
        });
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(220, 60, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(30, 35, 45));
            }
        });
        
        titleBar.add(titleBarLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);
        
        // Make title bar draggable
        final Point[] dragPoint = {null};
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    Point location = getLocation();
                    setLocation(
                        location.x + e.getX() - dragPoint[0].x,
                        location.y + e.getY() - dragPoint[0].y
                    );
                }
            }
        });
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Header with timer
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📋 Your Next Task");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ACCENT_COLOR);
        
        timerLabel = new JLabel("⏱ 00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(new Color(251, 191, 36));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(timerLabel, BorderLayout.EAST);
        
        // Task details card
        JPanel taskCard = new JPanel();
        taskCard.setLayout(new BoxLayout(taskCard, BoxLayout.Y_AXIS));
        taskCard.setBackground(PANEL_COLOR);
        taskCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 110, 130), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Task name
        JLabel nameLabel = new JLabel(task.getTaskName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskCard.add(nameLabel);
        
        taskCard.add(Box.createVerticalStrut(15));
        
        // Description
        JTextArea descArea = new JTextArea(task.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setBackground(PANEL_COLOR);
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
        JLabel warningLabel = new JLabel("<html><i>⚠️ Timer is tracking your work. Submit only after completing the task!</i></html>");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        warningLabel.setForeground(new Color(251, 191, 36));
        warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskCard.add(warningLabel);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        
        JButton submitBtn = new JButton("✓ Submit Task");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setBackground(new Color(34, 197, 94));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setPreferredSize(new Dimension(160, 45));
        submitBtn.addActionListener(e -> handleSubmit());
        
        JButton skipBtn = new JButton("⏭ Skip Task");
        skipBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        skipBtn.setForeground(Color.WHITE);
        skipBtn.setBackground(new Color(251, 146, 60));
        skipBtn.setFocusPainted(false);
        skipBtn.setBorderPainted(false);
        skipBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        skipBtn.setPreferredSize(new Dimension(160, 45));
        skipBtn.addActionListener(e -> handleSkip());
        
        buttonsPanel.add(submitBtn);
        buttonsPanel.add(skipBtn);
        
        // Assemble content
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(taskCard, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Assemble main panel with title bar
        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createMetaItem(String label, String value, String emoji) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        
        JLabel emojiLabel = new JLabel(emoji);
        try {
            emojiLabel.setFont(com.forgegrid.ui.FontUtils.getEmojiFont().deriveFont(16f));
        } catch (Exception e) {
            emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        }
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelText.setForeground(TEXT_SECONDARY);
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueText.setForeground(TEXT_COLOR);
        
        textPanel.add(labelText);
        textPanel.add(valueText);
        
        panel.add(emojiLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void startUITimer() {
        uiTimer = new Timer(1000, e -> updateTimerDisplay());
        uiTimer.start();
    }
    
    private void updateTimerDisplay() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000; // seconds
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        timerLabel.setText(String.format("⏱ %02d:%02d", minutes, seconds));
    }
    
    private void handleSubmit() {
        uiTimer.stop();
        
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
                uiTimer.start();
                return;
            }
        }
        
        // Save completed task
        HardcodedTaskService taskService = new HardcodedTaskService();
        LevelService levelService = new LevelService();
        PlayerProfile profile = parent.profile;
        
        boolean success = taskService.saveCompletedTask(
            profile.getUsername(),
            task.getTaskName(),
            Math.max(1, elapsedMinutes),
            task.getXpReward()
        );
        
        if (success) {
            // Add XP and check for level up
            LevelService.LevelUpResult result = levelService.addXP(profile.getUsername(), task.getXpReward());
            
            // Refresh parent dashboard
            parent.completedTaskNames.add(task.getTaskName());
            parent.loadedViews.put(parent.VIEW_TASKS, false);
            parent.loadedViews.put(parent.VIEW_DASHBOARD, false);
            
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
                parent.switchView(parent.VIEW_TASKS);
                SwingUtilities.invokeLater(() -> parent.showTaskPopup());
            } else {
                parent.switchView(parent.VIEW_TASKS);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save task. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            uiTimer.start();
        }
    }
    
    private void handleSkip() {
        uiTimer.stop();
        
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
            uiTimer.start();
            return;
        }
        
        long elapsedMillis = System.currentTimeMillis() - startTime;
        int elapsedMinutes = Math.max(1, (int) (elapsedMillis / 60000));
        
        // Save skipped task with negative XP
        HardcodedTaskService taskService = new HardcodedTaskService();
        LevelService levelService = new LevelService();
        PlayerProfile profile = parent.profile;
        
        int xpPenalty = -(task.getXpReward() / 2);
        
        boolean success = taskService.saveSkippedTask(
            profile.getUsername(),
            task.getTaskName(),
            elapsedMinutes,
            xpPenalty
        );
        
        if (success) {
            // Apply XP penalty
            LevelService.LevelUpResult levelResult = levelService.addXP(profile.getUsername(), xpPenalty);
            
            // Mark as completed so it doesn't show again
            parent.completedTaskNames.add(task.getTaskName());
            parent.loadedViews.put(parent.VIEW_TASKS, false);
            parent.loadedViews.put(parent.VIEW_DASHBOARD, false);
            
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
                parent.switchView(parent.VIEW_TASKS);
                SwingUtilities.invokeLater(() -> parent.showTaskPopup());
            } else {
                parent.switchView(parent.VIEW_TASKS);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to skip task. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            uiTimer.start();
        }
    }
    
    @Override
    public void dispose() {
        if (uiTimer != null) {
            uiTimer.stop();
        }
        super.dispose();
    }
}

