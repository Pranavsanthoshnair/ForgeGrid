package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {

    private final PlayerProfile profile;
    
    // UI Components
    private JPanel centerPanel;
    private CardLayout centerLayout;
    private JLabel currentViewLabel;
    
    // Player stats (placeholders for now)
    private int currentXP = 0;
    private int maxXP = 100;
    private int currentStreak = 0;
    private int currentLevel = 1;
    private String playerRank = "Novice";
    
    // Color scheme - minimalistic dark theme
    private static final Color BG_COLOR = new Color(30, 33, 38);
    private static final Color SIDEBAR_COLOR = new Color(24, 26, 31);
    private static final Color PANEL_COLOR = new Color(40, 43, 48);
    private static final Color ACCENT_COLOR = new Color(88, 166, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 160);
    private static final Color HOVER_COLOR = new Color(50, 53, 58);
    
    // View constants
    private static final String VIEW_DASHBOARD = "Dashboard";
    private static final String VIEW_ASSIGNED = "Assigned Tasks";
    private static final String VIEW_COMPLETED = "Completed Tasks";
    private static final String VIEW_SKIPPED = "Skipped/Missed";
    private static final String VIEW_GOATED = "Goated Tasks";
    private static final String VIEW_PROFILE = "Profile";
    private static final String VIEW_ACHIEVEMENTS = "Achievements";
    private static final String VIEW_PROGRESS = "Progress Tracker";
    private static final String VIEW_DEADLINE = "Deadline Tracker";
    private static final String VIEW_SAVE_LOAD = "Save/Load Progress";
    private static final String VIEW_SETTINGS = "Settings";
    private static final String VIEW_HELP = "Help & Docs";

    public Dashboard(PlayerProfile profile) {
        this(profile, false);
    }
    
    public Dashboard(PlayerProfile profile, boolean skipWelcome) {
        this.profile = profile;
        
        // Initialize player stats from profile
        if (profile != null) {
            this.currentLevel = profile.getLevel();
            this.currentXP = profile.getScore() % 100; // XP is remainder of score divided by 100
            this.maxXP = currentLevel * 100;
        }
        
        setTitle("ForgeGrid - Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Add window listener for close confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    Dashboard.this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        initUI();
    }

    private void initUI() {
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        
        // Create the three main sections
        JPanel topPanel = createTopPanel();
        JPanel sidebarPanel = createSidebarPanel();
        JPanel centerContainer = createCenterContainer();
        JPanel bottomPanel = createBottomPanel();
        
        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Creates the top panel with player stats
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Player Name
        String playerName = (profile != null && profile.getUsername() != null) ? profile.getUsername() : "Player";
        JLabel nameLabel = new JLabel(playerName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(TEXT_COLOR);
        
        topPanel.add(nameLabel);
        topPanel.add(Box.createHorizontalStrut(30));
        
        // Level
        JLabel levelLabel = new JLabel("Level: " + currentLevel);
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        levelLabel.setForeground(TEXT_SECONDARY);
        
        topPanel.add(levelLabel);
        topPanel.add(Box.createHorizontalStrut(30));
        
        // XP Progress Bar
        JPanel xpPanel = new JPanel();
        xpPanel.setLayout(new BoxLayout(xpPanel, BoxLayout.Y_AXIS));
        xpPanel.setOpaque(false);
        
        JLabel xpLabel = new JLabel("XP: " + currentXP + " / " + maxXP);
        xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        xpLabel.setForeground(TEXT_SECONDARY);
        xpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JProgressBar xpBar = new JProgressBar(0, maxXP);
        xpBar.setValue(currentXP);
        xpBar.setPreferredSize(new Dimension(200, 8));
        xpBar.setMaximumSize(new Dimension(200, 8));
        xpBar.setForeground(ACCENT_COLOR);
        xpBar.setBackground(SIDEBAR_COLOR);
        xpBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        xpPanel.add(xpLabel);
        xpPanel.add(Box.createVerticalStrut(5));
        xpPanel.add(xpBar);
        
        topPanel.add(xpPanel);
        topPanel.add(Box.createHorizontalStrut(30));
        
        // Rank
        JLabel rankLabel = new JLabel("Rank: " + playerRank);
        rankLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rankLabel.setForeground(TEXT_SECONDARY);
        
        topPanel.add(rankLabel);
        topPanel.add(Box.createHorizontalStrut(30));
        
        // Streak
        JLabel streakLabel = new JLabel("🔥 Streak: " + currentStreak);
        streakLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        streakLabel.setForeground(TEXT_SECONDARY);
        
        topPanel.add(streakLabel);
        topPanel.add(Box.createHorizontalGlue());
        
        return topPanel;
    }
    
    /**
     * Creates the sidebar with tree menu
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        
        // Title
        JLabel titleLabel = new JLabel("ForgeGrid");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        sidebarPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Menu tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu");
        
        // MAIN
        DefaultMutableTreeNode main = new DefaultMutableTreeNode("MAIN");
        main.add(new DefaultMutableTreeNode(VIEW_DASHBOARD));
        root.add(main);
        
        // TASKS
        DefaultMutableTreeNode tasks = new DefaultMutableTreeNode("TASKS");
        tasks.add(new DefaultMutableTreeNode(VIEW_ASSIGNED));
        tasks.add(new DefaultMutableTreeNode(VIEW_COMPLETED));
        tasks.add(new DefaultMutableTreeNode(VIEW_SKIPPED));
        tasks.add(new DefaultMutableTreeNode(VIEW_GOATED));
        root.add(tasks);
        
        // PLAYER
        DefaultMutableTreeNode player = new DefaultMutableTreeNode("PLAYER");
        player.add(new DefaultMutableTreeNode(VIEW_PROFILE));
        player.add(new DefaultMutableTreeNode(VIEW_ACHIEVEMENTS));
        player.add(new DefaultMutableTreeNode(VIEW_PROGRESS));
        root.add(player);
        
        // COMPONENTS
        DefaultMutableTreeNode components = new DefaultMutableTreeNode("COMPONENTS");
        components.add(new DefaultMutableTreeNode(VIEW_DEADLINE));
        components.add(new DefaultMutableTreeNode(VIEW_SAVE_LOAD));
        root.add(components);
        
        // OTHER
        DefaultMutableTreeNode other = new DefaultMutableTreeNode("OTHER");
        other.add(new DefaultMutableTreeNode(VIEW_SETTINGS));
        other.add(new DefaultMutableTreeNode(VIEW_HELP));
        other.add(new DefaultMutableTreeNode("Exit"));
        root.add(other);
        
        JTree tree = new JTree(new DefaultTreeModel(root));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setBackground(SIDEBAR_COLOR);
        tree.setForeground(TEXT_COLOR);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tree.setBorder(new EmptyBorder(10, 20, 10, 10));
        
        // Expand all nodes
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        
        // Add selection listener
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null || node.isRoot() || !node.isLeaf()) return;
            
            String selectedView = node.getUserObject().toString();
            
            if ("Exit".equals(selectedView)) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
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
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SIDEBAR_COLOR);
        
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);
        
        return sidebarPanel;
    }
    
    /**
     * Creates the center container with CardLayout for switching views
     */
    private JPanel createCenterContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);
        
        // Center panel with CardLayout
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(BG_COLOR);
        
        // Add all views
        centerPanel.add(createViewPanel(VIEW_DASHBOARD), VIEW_DASHBOARD);
        centerPanel.add(createViewPanel(VIEW_ASSIGNED), VIEW_ASSIGNED);
        centerPanel.add(createViewPanel(VIEW_COMPLETED), VIEW_COMPLETED);
        centerPanel.add(createViewPanel(VIEW_SKIPPED), VIEW_SKIPPED);
        centerPanel.add(createViewPanel(VIEW_GOATED), VIEW_GOATED);
        centerPanel.add(createViewPanel(VIEW_PROFILE), VIEW_PROFILE);
        centerPanel.add(createViewPanel(VIEW_ACHIEVEMENTS), VIEW_ACHIEVEMENTS);
        centerPanel.add(createViewPanel(VIEW_PROGRESS), VIEW_PROGRESS);
        centerPanel.add(createViewPanel(VIEW_DEADLINE), VIEW_DEADLINE);
        centerPanel.add(createViewPanel(VIEW_SAVE_LOAD), VIEW_SAVE_LOAD);
        centerPanel.add(createViewPanel(VIEW_SETTINGS), VIEW_SETTINGS);
        centerPanel.add(createViewPanel(VIEW_HELP), VIEW_HELP);
        
        container.add(centerPanel, BorderLayout.CENTER);
        
        return container;
    }
    
    /**
     * Creates a placeholder view panel
     */
    private JPanel createViewPanel(String viewName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel(viewName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Content area
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(PANEL_COLOR);
        contentArea.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel placeholderLabel = new JLabel("This is the " + viewName + " view.");
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        placeholderLabel.setForeground(TEXT_SECONDARY);
        placeholderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel = new JLabel("Content and functionality will be added here.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(TEXT_SECONDARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentArea.add(placeholderLabel);
        contentArea.add(Box.createVerticalStrut(10));
        contentArea.add(infoLabel);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20));
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the bottom panel with action buttons
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(PANEL_COLOR);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Action buttons
        JButton submitBtn = createActionButton("Submit Task", ACCENT_COLOR);
        JButton skipBtn = createActionButton("Skip Task", new Color(200, 80, 80));
        JButton addBtn = createActionButton("Add Custom Task", new Color(100, 180, 100));
        JButton saveExitBtn = createActionButton("Save & Exit", new Color(150, 120, 200));
        
        // Add action listeners (placeholders for now)
        submitBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Submit Task clicked"));
        skipBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Skip Task clicked"));
        addBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add Custom Task clicked"));
        saveExitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Save and exit?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        bottomPanel.add(submitBtn);
        bottomPanel.add(skipBtn);
        bottomPanel.add(addBtn);
        bottomPanel.add(saveExitBtn);
        
        return bottomPanel;
    }
    
    /**
     * Creates a styled action button
     */
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Switches to a different view
     */
    private void switchView(String viewName) {
        centerLayout.show(centerPanel, viewName);
    }
    
    /**
     * Applies onboarding selections to the dashboard (for compatibility with AuthUI)
     * This is a placeholder method - full implementation will be added later
     */
    public void applyOnboardingSelections(String goal, String language, String skill, String practice) {
        // Placeholder for onboarding data integration
        // This will be implemented when we add database functionality
        // For now, just store the values if needed
        System.out.println("Onboarding applied - Goal: " + goal + ", Language: " + language + ", Skill: " + skill);
    }
}
