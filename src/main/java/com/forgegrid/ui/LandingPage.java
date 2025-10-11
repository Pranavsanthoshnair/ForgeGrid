package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import javax.swing.*;
import java.awt.*;

public class LandingPage extends JFrame {
    // Colors
    private static final Color BG_COLOR = new Color(30, 35, 45);
    private static final Color TEXT_COLOR = new Color(220, 225, 235);
    private static final Color TEXT_SECONDARY = new Color(160, 170, 185);
    private static final Color BUTTON_COLOR = new Color(100, 180, 220);

    private final PlayerProfile profile;
    private final String goal;
    private final String language;
    private final String skill;

    public LandingPage(PlayerProfile profile, String goal, String language, String skill) {
        this.profile = profile;
        this.goal = goal;
        this.language = language;
        this.skill = skill;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("ForgeGrid - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setBackground(BG_COLOR);
        
        setLayout(new BorderLayout());
        add(createLandingContent(), BorderLayout.CENTER);
    }
    
    private JPanel createLandingContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Main title
        JLabel mainTitle = new JLabel("Welcome to ForgeGrid");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        mainTitle.setForeground(TEXT_COLOR);
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitle = new JLabel("Your minimalist productivity companion");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Features section
        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel featuresTitle = new JLabel("What you can do:");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        featuresTitle.setForeground(TEXT_COLOR);
        featuresTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Feature items
        String[] features = {
            "📋 Manage your tasks efficiently",
            "📊 Track your productivity progress", 
            "👤 Monitor your profile and achievements",
            "🎯 Stay focused with minimalist design"
        };
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            featureLabel.setForeground(TEXT_SECONDARY);
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createVerticalStrut(8));
        }
        
        // Get Started button
        JButton getStartedBtn = new JButton("Get Started");
        getStartedBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        getStartedBtn.setBackground(BUTTON_COLOR);
        getStartedBtn.setForeground(Color.WHITE);
        getStartedBtn.setBorderPainted(false);
        getStartedBtn.setPreferredSize(new Dimension(150, 50));
        getStartedBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        getStartedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        getStartedBtn.addActionListener(e -> openDashboard());
        
        // Layout components
        panel.add(Box.createVerticalGlue());
        panel.add(mainTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(featuresTitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(featuresPanel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(getStartedBtn);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void openDashboard() {
        // Close landing page
        dispose();
        
        // Open dashboard
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard(profile, true); // Skip welcome since we already showed it
            dashboard.setVisible(true);
        });
    }
}
