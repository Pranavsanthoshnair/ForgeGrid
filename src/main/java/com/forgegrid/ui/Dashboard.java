package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import com.forgegrid.service.UserService;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    // Colors
    private static final Color BG_COLOR = new Color(30, 35, 45);
    private static final Color PANEL_COLOR = new Color(40, 50, 65);
    private static final Color TEXT_COLOR = new Color(220, 225, 235);
    private static final Color TEXT_SECONDARY = new Color(160, 170, 185);
    
    // View constants
    private static final String VIEW_LANDING = "Landing";
    private static final String VIEW_DASHBOARD = "Dashboard";
    private static final String VIEW_TASKS = "Tasks";
    private static final String VIEW_PROFILE = "Profile";
    private static final String VIEW_EXIT = "Logout";

    private final PlayerProfile profile;
    private final UserService userService;
    private CardLayout centerLayout;
    private JPanel centerPanel;

    public Dashboard(PlayerProfile profile) {
        this(profile, false);
    }
    
    public Dashboard(PlayerProfile profile, boolean skipWelcome) {
        this.profile = profile;
        this.userService = new UserService();
        
        initializeUI();
        
        if (!skipWelcome) {
            showWelcomeDialog();
            // Show landing page after welcome dialog
            centerLayout.show(centerPanel, VIEW_LANDING);
        } else {
            // If skipping welcome, go directly to dashboard
            centerLayout.show(centerPanel, VIEW_DASHBOARD);
        }
    }
    
    private void initializeUI() {
        setTitle("ForgeGrid - Minimalist Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setBackground(BG_COLOR);
        
        // Add window listener for close confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    Dashboard.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(createSidebarPanel(), BorderLayout.WEST);
        add(createCenterContainer(), BorderLayout.CENTER);
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(25, 30, 40));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("ForgeGrid");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        sidebarPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Menu tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu");
        
        // MAIN
        DefaultMutableTreeNode main = new DefaultMutableTreeNode("MAIN");
        main.add(new DefaultMutableTreeNode(VIEW_DASHBOARD));
        main.add(new DefaultMutableTreeNode(VIEW_TASKS));
        main.add(new DefaultMutableTreeNode(VIEW_PROFILE));
        main.add(new DefaultMutableTreeNode("Logout"));
        root.add(main);
        
        JTree tree = new JTree(new DefaultTreeModel(root));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setBackground(new Color(25, 30, 40));
        tree.setForeground(TEXT_COLOR);
        tree.setCellRenderer(new CustomTreeCellRenderer());
        
        // Tree selection listener
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null || node.isRoot() || !node.isLeaf()) return;
            
            String selectedView = node.getUserObject().toString();
            
            if ("Logout".equals(selectedView)) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else {
                switchView(selectedView);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBackground(new Color(25, 30, 40));
        scrollPane.setBorder(null);
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);
        
        return sidebarPanel;
    }
    
    private JPanel createCenterContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);
        
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(BG_COLOR);
        
        // Add all views
        centerPanel.add(createViewPanel(VIEW_LANDING), VIEW_LANDING);
        centerPanel.add(createViewPanel(VIEW_DASHBOARD), VIEW_DASHBOARD);
        centerPanel.add(createViewPanel(VIEW_TASKS), VIEW_TASKS);
        centerPanel.add(createViewPanel(VIEW_PROFILE), VIEW_PROFILE);
        
        container.add(centerPanel, BorderLayout.CENTER);
        
        return container;
    }
    
    private JPanel createViewPanel(String viewName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel(viewName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentArea = new JPanel();
        contentArea.setOpaque(false);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        
        if (VIEW_LANDING.equals(viewName)) {
            contentArea.add(buildLandingPageView());
        } else if (VIEW_DASHBOARD.equals(viewName)) {
            contentArea.add(buildSimpleDashboardView());
        } else if (VIEW_TASKS.equals(viewName)) {
            contentArea.add(buildSimpleTasksView());
        } else if (VIEW_PROFILE.equals(viewName)) {
            contentArea.add(buildSimpleProfileView());
        } else if (VIEW_EXIT.equals(viewName)) {
            contentArea.add(buildExitView());
        }
        
        panel.add(contentArea, BorderLayout.CENTER);
        return panel;
    }
    
    private JComponent buildLandingPageView() {
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
        getStartedBtn.setBackground(new Color(100, 180, 220));
        getStartedBtn.setForeground(Color.WHITE);
        getStartedBtn.setBorderPainted(false);
        getStartedBtn.setPreferredSize(new Dimension(150, 50));
        getStartedBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        getStartedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        getStartedBtn.addActionListener(e -> {
            centerLayout.show(centerPanel, VIEW_DASHBOARD);
        });
        
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
    
    private JComponent buildSimpleDashboardView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Welcome to ForgeGrid");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Your minimalist productivity companion");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JComponent buildSimpleTasksView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        JLabel message = new JLabel("Task management features coming soon...");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(TEXT_SECONDARY);
        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(message);
        
        return panel;
    }
    
    private JComponent buildSimpleProfileView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        JLabel nameLabel = new JLabel("Player: " + (profile != null ? profile.getUsername() : "Guest"));
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        
        JLabel levelLabel = new JLabel("Level: " + (profile != null ? profile.getLevel() : "1"));
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(levelLabel);
        
        return panel;
    }
    
    private JComponent buildExitView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Centered logout confirmation
        JPanel logoutPanel = new JPanel();
        logoutPanel.setOpaque(true);
        logoutPanel.setBackground(new Color(45, 55, 70));
        logoutPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
            new EmptyBorder(40, 40, 40, 40)
        ));
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        
        JLabel logoutIcon = new JLabel("⚠️");
        logoutIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoutIcon.setForeground(new Color(255, 150, 100));
        logoutIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoutTitle = new JLabel("Logout Application");
        logoutTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoutTitle.setForeground(new Color(255, 100, 100));
        logoutTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoutMessage = new JLabel("Are you sure you want to logout?");
        logoutMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutMessage.setForeground(TEXT_COLOR);
        logoutMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setOpaque(false);
        
        JButton saveLogoutBtn = new JButton("💾 Save & Logout");
        saveLogoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveLogoutBtn.setBackground(new Color(80, 200, 120));
        saveLogoutBtn.setForeground(Color.WHITE);
        saveLogoutBtn.setBorderPainted(false);
        saveLogoutBtn.setPreferredSize(new Dimension(140, 40));
        saveLogoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveLogoutBtn.addActionListener(e -> System.exit(0));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.setBackground(new Color(100, 110, 120));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> centerLayout.show(centerPanel, VIEW_DASHBOARD));
        
        buttonsPanel.add(saveLogoutBtn);
        buttonsPanel.add(cancelBtn);
        
        logoutPanel.add(Box.createVerticalGlue());
        logoutPanel.add(logoutIcon);
        logoutPanel.add(Box.createVerticalStrut(10));
        logoutPanel.add(logoutTitle);
        logoutPanel.add(Box.createVerticalStrut(5));
        logoutPanel.add(logoutMessage);
        logoutPanel.add(Box.createVerticalStrut(20));
        logoutPanel.add(buttonsPanel);
        logoutPanel.add(Box.createVerticalGlue());
        
        panel.add(logoutPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void switchView(String viewName) {
        centerLayout.show(centerPanel, viewName);
    }
    
    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(
            this,
            "Welcome to ForgeGrid!\nYour minimalist productivity companion.",
            "Welcome",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Custom tree cell renderer
    private static class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            setBackground(selected ? new Color(55, 65, 80) : new Color(25, 30, 40));
            setForeground(selected ? new Color(220, 225, 235) : new Color(160, 170, 185));
            
            return this;
        }
    }
}