package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

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
        
        // Create the main sections
        JPanel topPanel = createTopPanel();
        JPanel sidebarPanel = createSidebarPanel();
        JPanel centerContainer = createCenterContainer();
        
        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        
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
     * Creates the sidebar with enhanced tree menu
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        
        // Title
        JLabel titleLabel = new JLabel("ForgeGrid");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(new EmptyBorder(20, 20, 15, 20));
        
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
        tree.setBorder(new EmptyBorder(5, 15, 10, 10));
        tree.setRowHeight(28); // Increased row height for better readability
        
        // Custom cell renderer for better appearance
        tree.setCellRenderer(new CustomTreeCellRenderer());
        
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
        
        // Title using shared GradientTextLabel for consistency
        GradientTextLabel titleLabel = new GradientTextLabel(viewName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setGradient(new Color(255, 215, 0), ACCENT_COLOR);
        
        // Content area wrapped in reusable CardContainerPanel
        JPanel contentArea = new CardContainerPanel();
        contentArea.setOpaque(false);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        
        if (VIEW_HELP.equals(viewName)) {
            // Help view: wire to open local markdown docs in the system viewer
            JLabel intro = new JLabel("Open documentation:");
            intro.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            intro.setForeground(TEXT_SECONDARY);
            intro.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton userManualBtn = new JButton("Open User Manual (USER_MANUAL.md)");
            userManualBtn.addActionListener(e -> openDoc("USER_MANUAL.md"));
            styleLinkishButton(userManualBtn);

            JButton techSetupBtn = new JButton("Open Technical Setup (TECHNICAL_SETUP.md)");
            techSetupBtn.addActionListener(e -> openDoc("TECHNICAL_SETUP.md"));
            styleLinkishButton(techSetupBtn);

            contentArea.add(intro);
            contentArea.add(Box.createVerticalStrut(10));
            contentArea.add(userManualBtn);
            contentArea.add(Box.createVerticalStrut(8));
            contentArea.add(techSetupBtn);
        } else {
            JLabel placeholderLabel = new JLabel("This is the " + viewName + " view.");
            placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            placeholderLabel.setForeground(TEXT_SECONDARY);
            placeholderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel infoLabel = new JLabel("Content and functionality will be added here.");
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            infoLabel.setForeground(TEXT_SECONDARY);
            infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

<<<<<<< HEAD
        // Top metrics row with 16px spacing between cards
        JPanel metricsRow = new JPanel();
        metricsRow.setOpaque(false);
        metricsRow.setLayout(new GridLayout(1, 4, 16, 16)); // 16px margin between cards
        metricsRow.add(buildStatCard("Total Problems", String.valueOf(problemsGoal)));
        metricsRow.add(buildStatCard("Solved", String.valueOf(problemsSolved)));
        metricsRow.add(buildStatCard("Current Streak", String.valueOf(currentStreak)));
        metricsRow.add(buildStatCard("Level", String.valueOf(profile != null ? profile.getLevel() : 1)));
        homeView.add(metricsRow, BorderLayout.NORTH);

        // Right column (profile card with enhanced colors)
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new BorderLayout());
        rightCol.add(card, BorderLayout.NORTH);
        // Dynamic CTA button with smooth animations
        JButton startBtn = new JButton("Start your journey") {
            private float animationProgress = 0f;
            private Timer animationTimer;
            private boolean isAnimating = false;
            
            {
                animationTimer = new Timer(30, e -> {
                    Boolean animatingProperty = (Boolean) getClientProperty("isAnimating");
                    isAnimating = animatingProperty != null && animatingProperty;
                    
                    if (isAnimating) {
                        boolean isHovered = getClientProperty("hovered") == Boolean.TRUE;
                        if (isHovered && animationProgress < 1.0f) {
                            animationProgress = Math.min(1.0f, animationProgress + 0.1f);
                        } else if (!isHovered && animationProgress > 0.0f) {
                            animationProgress = Math.max(0.0f, animationProgress - 0.1f);
                        }
                        repaint();
                        
                        if ((isHovered && animationProgress >= 1.0f) || (!isHovered && animationProgress <= 0.0f)) {
                            putClientProperty("isAnimating", false);
                        }
                    }
                });
                animationTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Smooth lift animation
                int liftOffset = (int)(animationProgress * 3);
                g2.translate(0, -liftOffset);
                
                // Animated glow effect
                if (animationProgress > 0) {
                    g2.setColor(new Color(100, 180, 255, (int)(animationProgress * 50)));
                    g2.fillRoundRect(-2, -2, w + 4, h + 4, 16, 16);
                }
                
                // Smooth color transition
                int r = (int)(60 + animationProgress * 20);
                int g_ = (int)(120 + animationProgress * 30);
                int b = (int)(200 + animationProgress * 20);
                g2.setColor(new Color(r, g_, b));
                
                // Pill shape (12px border radius)
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                
                // Animated highlight
                if (animationProgress > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(animationProgress * 60)));
                    g2.fillRoundRect(2, 2, w - 4, h/2 - 2, 10, 10);
                }
                
                g2.dispose();
                
                // Draw text
                super.paintComponent(g);
            }
        };
        startBtn.setFocusPainted(false);
        startBtn.setForeground(new Color(255, 255, 255)); // Pure white text
        startBtn.setOpaque(false);
        startBtn.setContentAreaFilled(false);
        startBtn.setBorderPainted(false);
        startBtn.setFont(new Font("Inter", Font.BOLD, 16)); // 16px, 600 weight
        startBtn.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28)); // 14px vertical, 28px horizontal padding
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                startBtn.putClientProperty("hovered", true);
                startBtn.putClientProperty("isAnimating", true);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { 
                startBtn.putClientProperty("hovered", false);
                startBtn.putClientProperty("isAnimating", true);
            }
        });
        JPanel startWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startWrap.setOpaque(false);
        startWrap.setBorder(BorderFactory.createEmptyBorder(8, 6, 0, 0));
        startWrap.add(startBtn);
        rightCol.add(startWrap, BorderLayout.SOUTH);

        // Only show the profile column on dashboard; charts moved to Progress view
        homeView.add(rightCol, BorderLayout.CENTER);

        // Recommendations appear above the start button on the right column
        recommendationsPanel = new JPanel();
        recommendationsPanel.setOpaque(true);
        recommendationsPanel.setBackground(new Color(19, 38, 77)); // Slightly lighter navy #13264D
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(50, 70, 110))));
        rightCol.add(recommendationsPanel, BorderLayout.CENTER);
        updateRecommendations(java.util.Collections.emptyList(), null, null);

        JPanel accountView = buildAccountView();
        JPanel progressView = buildProgressView();
        JPanel settingsView = buildSettingsView();

        mainContent.add(homeView, VIEW_HOME);
        mainContent.add(accountView, VIEW_ACCOUNT);
        mainContent.add(progressView, VIEW_PROGRESS);
        mainContent.add(settingsView, VIEW_SETTINGS);

        content.add(mainContent);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);
=======
            contentArea.add(placeholderLabel);
            contentArea.add(Box.createVerticalStrut(10));
            contentArea.add(infoLabel);
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20));
        panel.add(contentArea, BorderLayout.CENTER);
        
>>>>>>> 900e92a45150a61f3e48121550a458a6ff8a9990
        return panel;
    }

    private void openDoc(String filename) {
        try {
            java.io.File f = new java.io.File(filename).getAbsoluteFile();
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "File not found: " + f.getPath(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(f);
            } else {
                JOptionPane.showMessageDialog(this, "Desktop open is not supported on this system.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to open document: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleLinkishButton(AbstractButton b) {
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setForeground(ACCENT_COLOR);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
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
    
    /**
     * Custom tree cell renderer for enhanced appearance
     */
    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        
        public CustomTreeCellRenderer() {
            setOpaque(false);
            setBackgroundNonSelectionColor(SIDEBAR_COLOR);
            setBackgroundSelectionColor(ACCENT_COLOR);
            setBorderSelectionColor(null);
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            String text = node.getUserObject().toString();
            
            // Style category nodes (non-leaf) with bold font
            if (!leaf) {
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(TEXT_SECONDARY);
                setIcon(createFolderIcon());
            } else {
                // Style leaf nodes with regular font
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                if (selected) {
                    setForeground(Color.WHITE);
                    setBackground(ACCENT_COLOR);
                } else {
                    setForeground(TEXT_COLOR);
                    setBackground(SIDEBAR_COLOR);
                }
                // Set custom icons based on item type
                setIcon(getIconForItem(text));
            }
            
            setOpaque(selected);
            setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            
            return this;
        }
        
        /**
         * Returns appropriate icon for menu items
         */
        private Icon getIconForItem(String itemName) {
            return switch (itemName) {
                case VIEW_DASHBOARD -> createDashboardIcon();
                case VIEW_ASSIGNED, VIEW_COMPLETED, VIEW_SKIPPED, VIEW_GOATED -> createTaskIcon();
                case VIEW_PROFILE -> createProfileIcon();
                case VIEW_ACHIEVEMENTS -> createAchievementIcon();
                case VIEW_PROGRESS -> createProgressIcon();
                case VIEW_DEADLINE -> createDeadlineIcon();
                case VIEW_SAVE_LOAD -> createSaveIcon();
                case VIEW_SETTINGS -> createSettingsIcon();
                case VIEW_HELP -> createHelpIcon();
                case "Exit" -> createExitIcon();
                default -> createDefaultIcon();
            };
        }
        
        // Simple icon creation methods using shapes
        private Icon createFolderIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 200, 100));
                    g2.fillRect(x + 2, y + 4, 12, 10);
                    g2.dispose();
                }
                public int getIconWidth() { return 16; }
                public int getIconHeight() { return 16; }
            };
        }
        
        private Icon createDashboardIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ACCENT_COLOR);
                    g2.fillRect(x + 2, y + 2, 6, 6);
                    g2.fillRect(x + 10, y + 2, 6, 6);
                    g2.fillRect(x + 2, y + 10, 6, 6);
                    g2.fillRect(x + 10, y + 10, 6, 6);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createTaskIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(100, 180, 100));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(x + 3, y + 3, 12, 12);
                    g2.drawLine(x + 6, y + 9, x + 8, y + 11);
                    g2.drawLine(x + 8, y + 11, x + 12, y + 7);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createProfileIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(88, 166, 255));
                    g2.fillOval(x + 5, y + 3, 8, 8);
                    g2.fillArc(x + 3, y + 10, 12, 8, 0, 180);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createAchievementIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 200, 50));
                    int[] xPoints = {x + 9, x + 11, x + 16, x + 12, x + 14, x + 9, x + 4, x + 6, x + 2, x + 7};
                    int[] yPoints = {y + 2, y + 7, y + 7, y + 11, y + 16, y + 13, y + 16, y + 11, y + 7, y + 7};
                    g2.fillPolygon(xPoints, yPoints, 10);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createProgressIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ACCENT_COLOR);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(x + 3, y + 14, x + 6, y + 10);
                    g2.drawLine(x + 6, y + 10, x + 9, y + 7);
                    g2.drawLine(x + 9, y + 7, x + 15, y + 3);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createDeadlineIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(220, 90, 90));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(x + 3, y + 3, 12, 12);
                    g2.drawLine(x + 9, y + 6, x + 9, y + 9);
                    g2.drawLine(x + 9, y + 9, x + 12, y + 9);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createSaveIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(160, 130, 210));
                    g2.fillRect(x + 3, y + 3, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x + 5, y + 11, 8, 4);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createSettingsIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_SECONDARY);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(x + 6, y + 6, 6, 6);
                    for (int i = 0; i < 4; i++) {
                        double angle = i * Math.PI / 2;
                        int x1 = (int)(x + 9 + Math.cos(angle) * 3);
                        int y1 = (int)(y + 9 + Math.sin(angle) * 3);
                        int x2 = (int)(x + 9 + Math.cos(angle) * 6);
                        int y2 = (int)(y + 9 + Math.sin(angle) * 6);
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createHelpIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ACCENT_COLOR);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(x + 3, y + 3, 12, 12);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("?", x + 7, y + 13);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createExitIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(220, 90, 90));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(x + 4, y + 4, x + 14, y + 14);
                    g2.drawLine(x + 14, y + 4, x + 4, y + 14);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
        
        private Icon createDefaultIcon() {
            return new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_SECONDARY);
                    g2.fillOval(x + 6, y + 6, 6, 6);
                    g2.dispose();
                }
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
    }
<<<<<<< HEAD

    private void updateStreakLabel() {
        if (streakLabel != null) {
            String suffix = currentStreak > 0 ? ("" + currentStreak) : "0";
            streakLabel.setText("Current streak: " + suffix);
        }
    }

    // Called after onboarding to apply user's selections to the profile card
    public void applyOnboardingSelections(String goal, String language, String skill, String practice) {
        if (skill != null && !skill.isBlank()) {
            profileSkillLabel.setText("Skill: " + skill);
        }
        if (goal != null && !goal.isBlank()) {
            profileGoalLabel.setText("Goal: " + goal);
            profileGoalLabel.setVisible(true);
        }
        if (language != null && !language.isBlank()) {
            profileLanguageLabel.setText("Language: " + language);
            profileLanguageLabel.setVisible(true);
        }
        if (practice != null && !practice.isBlank()) {
            profilePracticeLabel.setText("Practice: " + practice);
            profilePracticeLabel.setVisible(true);
        }
        revalidate();
        repaint();
    }

    private void refreshBadges() {
        if (badgesPanel == null) return;
        badgesPanel.removeAll();
        if (badges.isEmpty()) {
            // Show nothing if no badges per request
            badgesPanel.setPreferredSize(new Dimension(1, 1));
        } else {
            for (String b : badges) {
                JLabel badge = new JLabel(b);
                badge.setOpaque(true);
                badge.setBackground(new Color(255, 215, 0)); // Gold/Yellow
                badge.setForeground(new Color(20, 30, 50)); // Dark text on gold
                badge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                badgesPanel.add(badge);
            }
            // Reset size when badges exist
            badgesPanel.setPreferredSize(null);
        }
        badgesPanel.revalidate();
        badgesPanel.repaint();
    }

    private void openPreferencesDialog() {
        JDialog dialog = new JDialog(this, "Preferences", true);
        dialog.setLayout(new BorderLayout());
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(6,6,6,6);

        form.add(new JLabel("Languages to practice:"), gbc);
        gbc.gridx = 1;
        JCheckBox java = new JCheckBox("Java", true);
        JCheckBox js = new JCheckBox("JavaScript");
        JCheckBox py = new JCheckBox("Python");
        JPanel langs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        langs.add(java); langs.add(js); langs.add(py);
        form.add(langs, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Coding goal:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> goal = new JComboBox<>(new String[]{"Interview prep", "Competitive programming", "Projects"});
        form.add(goal, gbc);

        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Weekly time commitment:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> time = new JComboBox<>(new String[]{"2 hrs", "5 hrs", "10+ hrs"});
        form.add(time, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save");
        actions.add(cancel); actions.add(save);

        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            java.util.List<String> langsSelected = new java.util.ArrayList<>();
            if (java.isSelected()) langsSelected.add("Java");
            if (js.isSelected()) langsSelected.add("JavaScript");
            if (py.isSelected()) langsSelected.add("Python");
            updateRecommendations(langsSelected, (String) goal.getSelectedItem(), (String) time.getSelectedItem());
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateRecommendations(java.util.List<String> langs, String goal, String time) {
        recommendationsPanel.removeAll();
        
        // Current Quest Card - Modern Tech Dark Mode
        JPanel questCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtle shadow for depth
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, 16, 16);
                
                // Deep Charcoal background (#161B22)
                g2.setColor(new Color(22, 27, 34)); // #161B22
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 16, 16);
                
                // Subtle border (#30363D)
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(48, 54, 61)); // #30363D
                g2.drawRoundRect(0, 0, getWidth() - 9, getHeight() - 9, 16, 16);
                
                g2.dispose();
            }
        };
        questCard.setOpaque(false);
        questCard.setLayout(new BorderLayout());
        questCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // 24px padding
        
        JLabel questTitle = new JLabel("Current Quest");
        questTitle.setForeground(new Color(139, 148, 158)); // #8B949E Medium Grey
        questTitle.setFont(new Font("Inter", Font.PLAIN, 16)); // Card title styling (using PLAIN as closest to medium)
        
        JLabel questDesc = new JLabel("<html>Solve 5 problems today<br/>Reward: +50 XP</html>");
        questDesc.setForeground(new Color(230, 237, 243)); // #E6EDF3 Off-White
        questDesc.setFont(new Font("Inter", Font.PLAIN, 14)); // Body text styling
        
        JProgressBar questProgress = new JProgressBar(0, 5) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Track background (dark blue)
                g2.setColor(new Color(30, 50, 80)); // Dark blue track
                g2.fillRoundRect(0, 0, w, h, h, h); // Pill shape
                
                // Progress fill with blue gradient
                int fill = (int) Math.round(((double) getValue() / getMaximum()) * w);
                if (fill > 0) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(60, 120, 200), // Light blue
                        w, 0, new Color(100, 180, 255)  // Brighter blue
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, fill, h, h, h); // Pill shape
                }
                
                g2.dispose();
            }
        };
        questProgress.setValue(0); // Start quest at 0
        questProgress.setStringPainted(true);
        questProgress.setString("0/5 completed");
        questProgress.setForeground(new Color(160, 190, 230)); // Medium blue-grey for text
        questProgress.setFont(new Font("Inter", Font.PLAIN, 12)); // Subtle text
        questProgress.setBorderPainted(false);
        questProgress.setOpaque(false);
        
        JPanel questTop = new JPanel(new BorderLayout());
        questTop.setOpaque(false);
        questTop.add(questTitle, BorderLayout.WEST);
        
        questCard.add(questTop, BorderLayout.NORTH);
        questCard.add(questDesc, BorderLayout.CENTER);
        questCard.add(questProgress, BorderLayout.SOUTH);
        questCard.setPreferredSize(new Dimension(0, 90));
        
        recommendationsPanel.add(questCard);
        recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Regular recommendations
        JLabel title = new JLabel("📋 Personalized Recommendations");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        recommendationsPanel.add(title);
        recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        if (langs == null || langs.isEmpty()) {
            // Default recommendations
            for (String rec : new String[]{"🔥 Daily coding challenge", "📚 Algorithm fundamentals", "⚔️ Weekly contest prep"}) {
                JLabel r = new JLabel(rec);
                r.setForeground(new Color(200, 220, 255));
                r.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                recommendationsPanel.add(r);
                recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            }
        } else {
            JLabel based = new JLabel("Based on: " + String.join(", ", langs) + (goal != null ? ", goal: " + goal : "") + (time != null ? ", time: " + time : ""));
            based.setForeground(new Color(200, 200, 220));
            recommendationsPanel.add(based);
            recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

            // Enhanced recommendations with icons
            for (String rec : new String[]{"🔥 Warm-up set for this week", "⚙️ Core algorithms module", "🏆 Challenge of the week"}) {
                JLabel r = new JLabel(rec);
                r.setForeground(new Color(200, 220, 255));
                r.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                recommendationsPanel.add(r);
                recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            }
        }
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
    }

    // Styled option toggle (square, blue; hover lighter; selected green)
    private JToggleButton createOptionButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(19, 38, 77)); // Lighter navy
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(28, 72, 160)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(new Color(255, 215, 0)); // Gold/Yellow
                btn.setForeground(new Color(20, 30, 50)); // Dark text
            } else {
                btn.setBackground(new Color(19, 38, 77)); // Lighter navy
                btn.setForeground(Color.WHITE);
            }
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(new Color(30, 50, 90));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(new Color(19, 38, 77));
            }
        });
        return btn;
    }

    // Primary action button (yellowish)
    private JButton createPrimaryActionButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(255, 215, 0)); // Gold/Yellow
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 223, 50));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 215, 0));
            }
        });
        return b;
    }

    // Placeholder views for non-dashboard sections
    private JPanel buildPlaceholderView(String text) {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(new Color(19, 38, 77)); // Lighter navy #13264D
        p.setLayout(new GridBagLayout());
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        p.add(l);
        return p;
    }

    // Progress view with charts
    private JPanel buildProgressView() {
        JPanel p = new JPanel(new BorderLayout(16, 16));
        p.setOpaque(false);
        JLabel t = new JLabel("Progress");
        t.setForeground(new Color(30, 35, 45));
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(t, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(1, 1, 16, 16));
        grid.setOpaque(false);
        grid.add(buildLineChartPlaceholder());
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    // Account view with profile information
    private JPanel buildAccountView() {
        JPanel p = new JPanel(new BorderLayout(16, 16));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        // Title
        JLabel title = new JLabel("Account Settings");
        title.setForeground(new Color(255, 255, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        p.add(title, BorderLayout.NORTH);
        
        // Content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        
        // Profile section
        JPanel profileSection = createSettingsSection("Profile Information");
        profileSection.add(createSettingRow("Username:", profile != null ? profile.getUsername() : "Guest"));
        profileSection.add(createSettingRow("Email:", profile != null ? profile.getUsername() : "Not set"));
        profileSection.add(createSettingRow("Member since:", "2025"));
        content.add(profileSection);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Preferences section
        JPanel prefsSection = createSettingsSection("Preferences");
        prefsSection.add(createSettingRow("Goal:", profile != null ? profile.getOnboardingGoal() : "Not set"));
        prefsSection.add(createSettingRow("Language:", profile != null ? profile.getOnboardingLanguage() : "Not set"));
        prefsSection.add(createSettingRow("Skill Level:", profile != null ? profile.getOnboardingSkill() : "Not set"));
        JButton editPrefsBtn = new JButton("Edit Preferences");
        editPrefsBtn.setBackground(new Color(255, 215, 0));
        editPrefsBtn.setForeground(new Color(20, 30, 50));
        editPrefsBtn.setFocusPainted(false);
        editPrefsBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        editPrefsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editPrefsBtn.addActionListener(e -> showOnboarding());
        prefsSection.add(Box.createRigidArea(new Dimension(0, 12)));
        prefsSection.add(editPrefsBtn);
        content.add(prefsSection);
        
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // Settings view with app configuration
    private JPanel buildSettingsView() {
        JPanel p = new JPanel(new BorderLayout(16, 16));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        // Title
        JLabel title = new JLabel("Settings");
        title.setForeground(new Color(255, 255, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        p.add(title, BorderLayout.NORTH);
        
        // Content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        
        // Appearance section
        JPanel appearanceSection = createSettingsSection("Appearance");
        JCheckBox darkModeCheck = new JCheckBox("Dark Mode (Always On)");
        darkModeCheck.setSelected(true);
        darkModeCheck.setEnabled(false);
        darkModeCheck.setOpaque(false);
        darkModeCheck.setForeground(new Color(200, 200, 220));
        darkModeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appearanceSection.add(darkModeCheck);
        content.add(appearanceSection);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Notifications section
        JPanel notifSection = createSettingsSection("Notifications");
        JCheckBox achievementNotif = new JCheckBox("Achievement Notifications");
        achievementNotif.setSelected(true);
        achievementNotif.setOpaque(false);
        achievementNotif.setForeground(new Color(200, 200, 220));
        achievementNotif.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JCheckBox streakNotif = new JCheckBox("Streak Reminders");
        streakNotif.setSelected(true);
        streakNotif.setOpaque(false);
        streakNotif.setForeground(new Color(200, 200, 220));
        streakNotif.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notifSection.add(achievementNotif);
        notifSection.add(Box.createRigidArea(new Dimension(0, 8)));
        notifSection.add(streakNotif);
        content.add(notifSection);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Privacy section
        JPanel privacySection = createSettingsSection("Privacy & Data");
        JButton clearDataBtn = new JButton("Clear Local Preferences");
        clearDataBtn.setBackground(new Color(200, 50, 50));
        clearDataBtn.setForeground(Color.WHITE);
        clearDataBtn.setFocusPainted(false);
        clearDataBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        clearDataBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearDataBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "This will clear your saved email and preferences. Continue?",
                "Clear Data",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                // Clear preferences logic here
                JOptionPane.showMessageDialog(this, "Preferences cleared successfully!");
            }
        });
        privacySection.add(clearDataBtn);
        content.add(privacySection);
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // About section
        JPanel aboutSection = createSettingsSection("About");
        aboutSection.add(createSettingRow("Version:", "1.0.0"));
        aboutSection.add(createSettingRow("Build:", "2025.01"));
        JButton aboutBtn = new JButton("View Licenses");
        aboutBtn.setBackground(new Color(60, 120, 200));
        aboutBtn.setForeground(Color.WHITE);
        aboutBtn.setFocusPainted(false);
        aboutBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        aboutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        aboutSection.add(Box.createRigidArea(new Dimension(0, 12)));
        aboutSection.add(aboutBtn);
        content.add(aboutSection);
        
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // Helper method to create settings sections
    private JPanel createSettingsSection(String sectionTitle) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(true);
        section.setBackground(new Color(30, 40, 60));
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 70, 110), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel title = new JLabel(sectionTitle);
        title.setForeground(new Color(255, 215, 0));
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(title);
        section.add(Box.createRigidArea(new Dimension(0, 12)));
        
        return section;
    }

    // Helper method to create setting rows
    private JPanel createSettingRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(new Color(160, 180, 220));
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setPreferredSize(new Dimension(150, 20));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(new Color(255, 255, 255));
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        row.add(labelComp);
        row.add(valueComp);
        
        return row;
    }

    // Full-screen onboarding wizard inside main content
    private void showOnboarding() {
        JPanel wizard = buildOnboardingWizard();
        mainContent.add(wizard, VIEW_ONBOARDING);
        mainContentLayout.show(mainContent, VIEW_ONBOARDING);
    }

    private JPanel buildOnboardingWizard() {
        CardLayout flow = new CardLayout();
        JPanel wizardRoot = new JPanel(flow);
        wizardRoot.setOpaque(true);
        wizardRoot.setBackground(new Color(19, 38, 77)); // Lighter navy #13264D
        wizardRoot.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // State
        java.util.List<String> langs = new java.util.ArrayList<>();
        final String[] goalSel = {null};
        final String[] timeSel = {null};
        final String[] skillSel = {null};

        // Step 1: Skill level
        JPanel step1 = new JPanel();
        step1.setOpaque(false);
        step1.setLayout(new BoxLayout(step1, BoxLayout.Y_AXIS));
        JLabel q1 = new JLabel("What is your current skill level?");
        q1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        q1.setForeground(new Color(30, 35, 45));
        ButtonGroup g1 = new ButtonGroup();
        JToggleButton beginner = createOptionButton("Beginner");
        JToggleButton intermediate = createOptionButton("Intermediate");
        JToggleButton advanced = createOptionButton("Advanced");
        ButtonGroup group1 = new ButtonGroup();
        group1.add(beginner); group1.add(intermediate); group1.add(advanced);

        JPanel opts1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        opts1.setOpaque(false);
        opts1.add(beginner); opts1.add(intermediate); opts1.add(advanced);
        JButton next1 = createPrimaryActionButton("Continue");
        next1.addActionListener(e -> {
            skillSel[0] = beginner.isSelected() ? "Beginner" : intermediate.isSelected() ? "Intermediate" : advanced.isSelected() ? "Advanced" : null;
            flow.show(wizardRoot, "step2");
        });
        step1.add(q1); step1.add(Box.createRigidArea(new Dimension(0, 12))); step1.add(opts1); step1.add(Box.createRigidArea(new Dimension(0, 20))); step1.add(next1);

        // Step 2: Languages
        JPanel step2 = new JPanel();
        step2.setOpaque(false);
        step2.setLayout(new BoxLayout(step2, BoxLayout.Y_AXIS));
        JLabel q2 = new JLabel("Which languages do you want to practice?");
        q2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        q2.setForeground(new Color(30, 35, 45));
        JToggleButton java = createOptionButton("Java");
        java.setSelected(true);
        JToggleButton js = createOptionButton("JavaScript");
        JToggleButton py = createOptionButton("Python");
        JToggleButton c = createOptionButton("C");

        JPanel opts2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        opts2.setOpaque(false);
        opts2.add(java); opts2.add(js); opts2.add(py); opts2.add(c);
        JButton next2 = createPrimaryActionButton("Continue");
        next2.addActionListener(e -> {
            langs.clear();
            if (java.isSelected()) langs.add("Java");
            if (js.isSelected()) langs.add("JavaScript");
            if (py.isSelected()) langs.add("Python");
            if (c.isSelected()) langs.add("C");
            flow.show(wizardRoot, "step3");
        });
        step2.add(q2); step2.add(Box.createRigidArea(new Dimension(0, 12))); step2.add(opts2); step2.add(Box.createRigidArea(new Dimension(0, 20))); step2.add(next2);

        // Step 3: Goal
        JPanel step3 = new JPanel();
        step3.setOpaque(false);
        step3.setLayout(new BoxLayout(step3, BoxLayout.Y_AXIS));
        JLabel q3 = new JLabel("What’s your coding goal?");
        q3.setFont(new Font("Segoe UI", Font.BOLD, 18));
        q3.setForeground(new Color(30, 35, 45));
        JComboBox<String> goal = new JComboBox<>(new String[]{"Interview prep", "Competitive programming", "Projects"});
        goal.setMaximumSize(new Dimension(260, 28));
        JButton next3 = createPrimaryActionButton("Continue");
        next3.addActionListener(e -> { goalSel[0] = (String) goal.getSelectedItem(); flow.show(wizardRoot, "step4"); });
        step3.add(q3); step3.add(Box.createRigidArea(new Dimension(0, 12))); step3.add(goal); step3.add(Box.createRigidArea(new Dimension(0, 20))); step3.add(next3);

        // Step 4: Time commitment
        JPanel step4 = new JPanel();
        step4.setOpaque(false);
        step4.setLayout(new BoxLayout(step4, BoxLayout.Y_AXIS));
        JLabel q4 = new JLabel("Weekly time commitment");
        q4.setFont(new Font("Segoe UI", Font.BOLD, 18));
        q4.setForeground(new Color(30, 35, 45));
        JComboBox<String> time = new JComboBox<>(new String[]{"2 hrs", "5 hrs", "10+ hrs"});
        time.setMaximumSize(new Dimension(260, 28));
        JButton submit = createPrimaryActionButton("Submit");
        submit.addActionListener(e -> {
            timeSel[0] = (String) time.getSelectedItem();
            // Simple updates to state (simulate progress boost on submit)
            currentStreak = Math.max(currentStreak, 1);
            updateStreakLabel();
            updateRecommendations(langs, goalSel[0], timeSel[0]);
            mainContentLayout.show(mainContent, VIEW_HOME);
        });
        step4.add(q4); step4.add(Box.createRigidArea(new Dimension(0, 12))); step4.add(time); step4.add(Box.createRigidArea(new Dimension(0, 20))); step4.add(submit);

        wizardRoot.add(step1, "step1");
        wizardRoot.add(step2, "step2");
        wizardRoot.add(step3, "step3");
        wizardRoot.add(step4, "step4");
        flow.show(wizardRoot, "step1");

        return wizardRoot;
    }
=======
>>>>>>> 900e92a45150a61f3e48121550a458a6ff8a9990
}
