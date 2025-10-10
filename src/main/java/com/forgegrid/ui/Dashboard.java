package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
// removed unused: import java.awt.geom.*;

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
    private boolean onboardingCompleted = false;
    private static final String ONBOARDING_FILE = "onboarding.json";
    private int currentLevel = 1;
    private String playerRank = "Novice";
    
    // Color scheme - subtle attractive theme
    private static final Color BG_COLOR = new Color(25, 30, 40);
    private static final Color SIDEBAR_COLOR = new Color(20, 25, 35);
    private static final Color PANEL_COLOR = new Color(40, 50, 65);
    private static final Color ACCENT_COLOR = new Color(100, 180, 220);
    private static final Color TEXT_COLOR = new Color(220, 225, 235);
    private static final Color TEXT_SECONDARY = new Color(160, 170, 185);
    private static final Color HOVER_COLOR = new Color(55, 65, 80);
    
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
    private static final String VIEW_EXIT = "Exit";

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
        
        // Check onboarding after UI is fully set up
        SwingUtilities.invokeLater(() -> {
            loadOnboardingStatus();
            if (!onboardingCompleted) {
                showOnboardingModal();
            }
        });
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
        
        // Collapse all nodes initially
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.collapseRow(i);
        }
        
        // Add mouse listener for single-click expand/collapse on categories
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tree.getRowForLocation(e.getX(), e.getY());
                if (row != -1) {
                    TreePath path = tree.getPathForRow(row);
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        
                        // Toggle expand/collapse for category nodes (non-leaf) on single click
                        if (!node.isLeaf() && e.getClickCount() == 1) {
                            if (tree.isExpanded(path)) {
                                tree.collapsePath(path);
                            } else {
                                tree.expandPath(path);
                            }
                        }
                    }
                }
            }
        });
        
        // Add selection listener for leaf nodes (actual menu items)
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
        panel.setBorder(new EmptyBorder(20, 30, 20, 30)); // Less space for smaller windows
        
        // Title using shared GradientTextLabel for consistency
        GradientTextLabel titleLabel = new GradientTextLabel(viewName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setGradient(new Color(255, 215, 0), ACCENT_COLOR);
        
        // Player Info Bar
        JPanel playerInfoBar = createPlayerInfoBar();
        add(playerInfoBar, BorderLayout.NORTH);
        
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
        } else if (VIEW_DASHBOARD.equals(viewName)) {
            // Center the content area
            JPanel centeredContent = new JPanel();
            centeredContent.setOpaque(false);
            centeredContent.setLayout(new BoxLayout(centeredContent, BoxLayout.Y_AXIS));
            centeredContent.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            centeredContent.add(buildOverallProgressWidgetZero());
            centeredContent.add(Box.createVerticalStrut(16)); // Less spacing for smaller windows
            centeredContent.add(buildDailyGoalWidgetZero());
            centeredContent.add(Box.createVerticalStrut(24));
            centeredContent.add(buildWeeklyProductivityWidgetZero());
            
            contentArea.add(centeredContent);
        } else if (VIEW_ASSIGNED.equals(viewName)) {
            contentArea.add(buildAssignedTasksView());
        } else if (VIEW_COMPLETED.equals(viewName)) {
            contentArea.add(buildCompletedTasksView());
        } else if (VIEW_SKIPPED.equals(viewName)) {
            contentArea.add(buildMissedTasksView());
        } else if (VIEW_GOATED.equals(viewName)) {
            contentArea.add(buildGoatedTasksView());
        } else if (VIEW_PROFILE.equals(viewName)) {
            contentArea.add(buildPlayerProfileView());
        } else if (VIEW_ACHIEVEMENTS.equals(viewName)) {
            contentArea.add(buildAchievementsView());
        } else if (VIEW_PROGRESS.equals(viewName)) {
            contentArea.add(buildProgressTrackerView());
        } else if (VIEW_DEADLINE.equals(viewName)) {
            contentArea.add(buildDeadlineTrackerView());
        } else if (VIEW_SAVE_LOAD.equals(viewName)) {
            contentArea.add(buildSaveLoadView());
        } else if (VIEW_SETTINGS.equals(viewName)) {
            contentArea.add(buildSettingsView());
        } else if (VIEW_HELP.equals(viewName)) {
            contentArea.add(buildHelpDocsView());
        } else if (VIEW_EXIT.equals(viewName)) {
            contentArea.add(buildExitView());
        } else {
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
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20));
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }

    
    // Zero-state Dashboard Widgets
    private JComponent buildOverallProgressWidgetZero() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1), // Subtle blue border
            new EmptyBorder(12, 12, 12, 12) // Less padding for smaller windows
        ));

        JLabel title = new JLabel("Overall Progress");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        panel.add(title, BorderLayout.NORTH);

        final int totalTasks = 0;
        final int completedTasks = 0;
        final int percent = 0;

        JPanel donut = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Background ring
                g2.setColor(new Color(50, 60, 75));
                g2.fillOval(x, y, size, size);

                // Progress arc at 0%
                g2.setColor(new Color(80, 200, 120)); // Subtle green for progress
                g2.fillArc(x, y, size, size, 90, 0);

                // Inner cutout
                int thickness = (int)(size * 0.28);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
                g2.fillOval(x + thickness, y + thickness, size - 2*thickness, size - 2*thickness);
                g2.dispose();

                // Center labels
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                String text = percent + "%";
                g3.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g3.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 4;
                g3.setColor(TEXT_COLOR);
                g3.drawString(text, tx, ty);
                g3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                String sub = "Overall Progress";
                int sx = (getWidth() - g3.getFontMetrics().stringWidth(sub)) / 2;
                g3.setColor(TEXT_SECONDARY);
                g3.drawString(sub, sx, ty + 16);
                g3.dispose();
            }
        };
        donut.setPreferredSize(new Dimension(100, 80)); // Smaller for non-maximized windows
        donut.setMinimumSize(new Dimension(80, 60)); // Allow smaller minimum
        donut.setMaximumSize(new Dimension(150, 120)); // Allow larger when maximized
        donut.setOpaque(false);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel summary = new JLabel(completedTasks + "/" + totalTasks + " tasks completed");
        summary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        summary.setForeground(TEXT_SECONDARY);
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(summary);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(donut, BorderLayout.CENTER); // Center the donut instead of WEST
        center.add(info, BorderLayout.SOUTH); // Move info below
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildDailyGoalWidgetZero() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(true);
        container.setBackground(PANEL_COLOR);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 58, 63)),
            new EmptyBorder(20, 20, 20, 20) // 20px inside card padding
        ));

        JLabel title = new JLabel("Daily Goal Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        container.add(title, BorderLayout.NORTH);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setAlignmentX(Component.CENTER_ALIGNMENT);

        int goal = 0;
        int todayDone = 0;
        int safeMax = Math.max(goal, 1);
        JProgressBar bar = new JProgressBar(0, safeMax);
        bar.setValue(0);
        bar.setForeground(new Color(80, 200, 120)); // Subtle green to match donut
        bar.setBackground(SIDEBAR_COLOR);
        bar.setPreferredSize(new Dimension(200, 16)); // Smaller for non-maximized windows
        bar.setMaximumSize(new Dimension(300, 20)); // Allow larger when maximized
        bar.setBorderPainted(false);
        bar.setStringPainted(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(Box.createVerticalStrut(8)); // More space above bar
        inner.add(bar);
        inner.add(Box.createVerticalStrut(8)); // Space below bar
        JLabel caption = new JLabel(todayDone + "/" + goal + " tasks done");
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        caption.setForeground(TEXT_SECONDARY);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(caption);

        container.add(inner, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildWeeklyProductivityWidgetZero() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1), // Subtle blue border
            new EmptyBorder(12, 12, 12, 12) // Less padding for smaller windows
        ));

        JLabel title = new JLabel("Weekly Productivity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        panel.add(title, BorderLayout.NORTH);

        final int[] completedPerDay = new int[] {0, 0, 0, 0, 0, 0, 0};
        final String[] days = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final int maxVal = 1; // keep bars at zero height but avoid divide-by-zero

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int pad = 24;
                int barW = Math.max(10, (w - pad*2) / (completedPerDay.length * 2));
                int gap = barW;

                for (int i = 0; i < completedPerDay.length; i++) {
                    double ratio = completedPerDay[i] / (double) maxVal; // always 0
                    int bh = (int) Math.round((h - pad*2 - 20) * ratio);
                    int bx = pad + i * (barW + gap);
                    int by = h - pad - bh;
                    g2.setColor(new Color(180, 120, 200)); // Subtle purple for bars
                    g2.fillRoundRect(bx, by, barW, Math.max(0, bh), 6, 6);
                    g2.setColor(TEXT_SECONDARY);
                    String d = days[i];
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = bx + (barW - fm.stringWidth(d)) / 2;
                    g2.drawString(d, tx, h - 6);
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(300, 100)); // Smaller for non-maximized windows
        chart.setMinimumSize(new Dimension(250, 80)); // Allow smaller minimum
        chart.setMaximumSize(new Dimension(500, 150)); // Allow larger when maximized
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildUpcomingDeadlinesWidgetZero() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1), // Subtle blue border
            new EmptyBorder(12, 12, 12, 12) // Less padding for smaller windows
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Upcoming Deadlines (next 2 days)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        JLabel empty = new JLabel("No upcoming deadlines");
        empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        empty.setForeground(TEXT_SECONDARY);
        empty.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(empty);

        // Wrap in container to reduce width to 90% and center
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 0, 0, 0)); // 20px margin-top
        
        JPanel centeredPanel = new JPanel();
        centeredPanel.setOpaque(false);
        centeredPanel.setLayout(new BoxLayout(centeredPanel, BoxLayout.X_AXIS));
        centeredPanel.add(Box.createHorizontalGlue());
        
        JPanel sizedPanel = new JPanel(new BorderLayout());
        sizedPanel.setOpaque(false);
        sizedPanel.add(panel, BorderLayout.CENTER);
        sizedPanel.setPreferredSize(new Dimension((int)(panel.getPreferredSize().width * 0.9), panel.getPreferredSize().height));
        
        centeredPanel.add(sizedPanel);
        centeredPanel.add(Box.createHorizontalGlue());
        
        wrapper.add(centeredPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // Task Management Views
    private JComponent buildAssignedTasksView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Header with buttons
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Assigned Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        header.add(title, BorderLayout.WEST);
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        
        JButton addTaskBtn = new JButton("+ Add New Task");
        styleTaskButton(addTaskBtn, new Color(80, 200, 120));
        
        JButton sortBtn = new JButton("Sort by Deadline");
        styleTaskButton(sortBtn, ACCENT_COLOR);
        
        buttons.add(addTaskBtn);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(sortBtn);
        header.add(buttons, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20));
        
        // Task table
        String[] columns = {"Task Name", "Deadline", "Priority", "Status"};
        Object[][] data = {
            {"Complete Java Assignment", "2024-01-15", "HIGH", "In Progress"},
            {"Review Code Documentation", "2024-01-18", "MEDIUM", "Pending"},
            {"Update Project README", "2024-01-20", "LOW", "Pending"},
            {"Fix Bug #123", "2024-01-12", "HIGH", "In Progress"},
            {"Write Unit Tests", "2024-01-25", "MEDIUM", "Pending"}
        };
        
        JTable table = new JTable(data, columns);
        table.setBackground(PANEL_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(new Color(60, 70, 85));
        table.setSelectionBackground(ACCENT_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        
        // Custom cell renderer for priority colors
        table.getColumnModel().getColumn(2).setCellRenderer(new PriorityCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 180, 220), 1));
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JComponent buildCompletedTasksView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Completed Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Progress ring
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        
        JPanel progressRing = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Background ring
                g2.setColor(new Color(50, 60, 75));
                g2.fillOval(x, y, size, size);
                
                // Progress arc (0% completion - not initialized)
                g2.setColor(new Color(80, 200, 120));
                g2.fillArc(x, y, size, size, 90, 0);
                
                // Inner cutout
                int thickness = 15;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
                g2.fillOval(x + thickness, y + thickness, size - 2*thickness, size - 2*thickness);
                g2.dispose();
                
                // Center text
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g3.setColor(TEXT_COLOR);
                g3.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g3.getFontMetrics();
                String text = "0%";
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2;
                g3.drawString(text, tx, ty);
                g3.dispose();
            }
        };
        progressRing.setPreferredSize(new Dimension(100, 100));
        progressRing.setOpaque(false);
        
        JLabel progressLabel = new JLabel("Daily Completion Rate (Not Initialized)");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressLabel.setForeground(TEXT_SECONDARY);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel progressInfo = new JPanel();
        progressInfo.setOpaque(false);
        progressInfo.setLayout(new BoxLayout(progressInfo, BoxLayout.Y_AXIS));
        progressInfo.add(progressRing);
        progressInfo.add(Box.createVerticalStrut(5));
        progressInfo.add(progressLabel);
        
        progressPanel.add(progressInfo, BorderLayout.WEST);
        
        // Completed tasks list
        JPanel tasksList = new JPanel();
        tasksList.setOpaque(false);
        tasksList.setLayout(new BoxLayout(tasksList, BoxLayout.Y_AXIS));
        
        String[] completedTasks = {
            "No completed tasks yet - system not initialized"
        };
        
        for (String task : completedTasks) {
            JLabel taskLabel = new JLabel(task);
            taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            taskLabel.setForeground(new Color(80, 200, 120));
            taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            taskLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
            tasksList.add(taskLabel);
        }
        
        progressPanel.add(tasksList, BorderLayout.CENTER);
        panel.add(progressPanel);
        
        return panel;
    }
    
    private JComponent buildMissedTasksView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Missed Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Missed tasks with warning icons
        String[] missedTasks = {
            "No missed tasks - system not initialized"
        };
        
        for (String task : missedTasks) {
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setOpaque(true);
            taskPanel.setBackground(new Color(60, 40, 40)); // Dark red background
            taskPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 100, 100), 1),
                new EmptyBorder(10, 15, 10, 15)
            ));
            
            JLabel taskLabel = new JLabel(task);
            taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            taskLabel.setForeground(new Color(255, 150, 150)); // Light red text
            taskPanel.add(taskLabel, BorderLayout.WEST);
            
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.setOpaque(false);
            
            JButton rescheduleBtn = new JButton("Reschedule");
            styleTaskButton(rescheduleBtn, new Color(200, 150, 100));
            
            JButton reasonBtn = new JButton("Mark Reason");
            styleTaskButton(reasonBtn, new Color(200, 100, 100));
            
            buttons.add(rescheduleBtn);
            buttons.add(Box.createHorizontalStrut(5));
            buttons.add(reasonBtn);
            
            taskPanel.add(buttons, BorderLayout.EAST);
            panel.add(taskPanel);
            panel.add(Box.createVerticalStrut(10));
        }
        
        return panel;
    }
    
    private JComponent buildGoatedTasksView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Goated Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Motivational note
        JPanel notePanel = new JPanel();
        notePanel.setOpaque(true);
        notePanel.setBackground(new Color(40, 60, 40)); // Dark green background
        notePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 100), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel noteLabel = new JLabel("🚀 Ready to begin your productivity journey! Complete tasks to unlock achievements.");
        noteLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        noteLabel.setForeground(new Color(150, 255, 150));
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        notePanel.add(noteLabel);
        
        panel.add(notePanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Goated tasks showcase
        String[][] goatedTasks = {
            {"", "No achievements yet - system not initialized", "Start completing tasks to unlock achievements", "Begin your journey to greatness!"}
        };
        
        for (String[] task : goatedTasks) {
            JPanel taskCard = new JPanel(new BorderLayout());
            taskCard.setOpaque(true);
            taskCard.setBackground(new Color(50, 45, 60)); // Dark purple background
            taskCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 120, 200), 1),
                new EmptyBorder(15, 20, 15, 20)
            ));
            
            JPanel leftPanel = new JPanel();
            leftPanel.setOpaque(false);
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            
            JLabel starsLabel = new JLabel(task[0]);
            starsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            starsLabel.setForeground(new Color(255, 215, 0)); // Gold color
            leftPanel.add(starsLabel);
            
            JLabel taskNameLabel = new JLabel(task[1]);
            taskNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            taskNameLabel.setForeground(TEXT_COLOR);
            leftPanel.add(taskNameLabel);
            
            JLabel difficultyLabel = new JLabel(task[2]);
            difficultyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            difficultyLabel.setForeground(TEXT_SECONDARY);
            leftPanel.add(difficultyLabel);
            
            JLabel impactLabel = new JLabel(task[3]);
            impactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            impactLabel.setForeground(new Color(100, 200, 220));
            leftPanel.add(impactLabel);
            
            taskCard.add(leftPanel, BorderLayout.WEST);
            
            // Trophy icon
            JLabel trophyLabel = new JLabel("🏆");
            trophyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            taskCard.add(trophyLabel, BorderLayout.EAST);
            
            panel.add(taskCard);
            panel.add(Box.createVerticalStrut(15));
        }
        
        return panel;
    }
    
    private void styleTaskButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
    }
    
    // Custom cell renderer for priority colors
    private class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 2) { // Priority column
                String priority = value.toString();
                switch (priority) {
                    case "HIGH":
                        c.setBackground(new Color(200, 100, 100));
                        c.setForeground(Color.WHITE);
                        break;
                    case "MEDIUM":
                        c.setBackground(new Color(200, 200, 100));
                        c.setForeground(Color.BLACK);
                        break;
                    case "LOW":
                        c.setBackground(new Color(100, 200, 100));
                        c.setForeground(Color.WHITE);
                        break;
                }
            }
            
        return c;
    }
    }

    // Player Profile Views
    private JComponent buildPlayerProfileView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Profile Card
        panel.add(buildProfileCard());
        panel.add(Box.createVerticalStrut(20));
        
        // Statistics Panel
        panel.add(buildStatisticsPanel());
        panel.add(Box.createVerticalStrut(20));
        
        return panel;
    }
    
    private JComponent buildProfileCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(new Color(45, 55, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Avatar with glowing border
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Glowing border
                g2.setColor(new Color(100, 180, 220));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x-2, y-2, size+4, size+4);
                
                // Avatar circle
                g2.setColor(new Color(60, 70, 85));
                g2.fillOval(x, y, size, size);
                
                // Avatar icon
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "👤";
                int tx = x + (size - fm.stringWidth(icon)) / 2;
                int ty = y + (size + fm.getAscent()) / 2;
                g2.drawString(icon, tx, ty);
                
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(100, 100));
        avatarPanel.setOpaque(false);
        
        // User info
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        String username = (profile != null && profile.getUsername() != null) ? profile.getUsername() : "Player";
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Task Champion");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel streakLabel = new JLabel("🔥 7-Day Streak");
        streakLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        streakLabel.setForeground(new Color(255, 150, 100));
        streakLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(streakLabel);
        
        // XP/Level bar
        JPanel xpPanel = new JPanel();
        xpPanel.setOpaque(false);
        xpPanel.setLayout(new BoxLayout(xpPanel, BoxLayout.Y_AXIS));
        
        JLabel xpLabel = new JLabel("Level " + currentLevel + " • " + currentXP + "/" + maxXP + " XP");
        xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        xpLabel.setForeground(TEXT_SECONDARY);
        xpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JProgressBar xpBar = new JProgressBar(0, maxXP);
        xpBar.setValue(currentXP);
        xpBar.setForeground(new Color(80, 200, 120));
        xpBar.setBackground(new Color(40, 50, 65));
        xpBar.setPreferredSize(new Dimension(200, 8));
        xpBar.setMaximumSize(new Dimension(200, 8));
        xpBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        xpPanel.add(xpLabel);
        xpPanel.add(Box.createVerticalStrut(5));
        xpPanel.add(xpBar);
        
        card.add(avatarPanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(xpPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JComponent buildStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(40, 50, 65));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel("Statistics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        panel.add(title, BorderLayout.NORTH);
        
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        statsGrid.setOpaque(false);
        
        // Total tasks completed
        JPanel stat1 = createStatCard("Total Tasks", "0", "Not initialized", new Color(80, 200, 120));
        statsGrid.add(stat1);
        
        // Average time per task
        JPanel stat2 = createStatCard("Avg Time", "0 min", "No data yet", new Color(100, 180, 220));
        statsGrid.add(stat2);
        
        // Success rate
        JPanel stat3 = createStatCard("Success Rate", "0%", "Start tracking", new Color(255, 150, 100));
        statsGrid.add(stat3);
        
        // Rank comparison
        JPanel stat4 = createStatCard("Weekly Rank", "Unranked", "Begin journey", new Color(180, 120, 200));
        statsGrid.add(stat4);
        
        panel.add(statsGrid, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(35, 45, 60));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(subtitleLabel);
        
        return card;
    }
    
    private JComponent buildAchievementsView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Achievements Gallery");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Achievement badges
        String[][] achievements = {
            {"🏆", "Consistency King", "Complete 7 tasks in a row", "true"},
            {"⚡", "Deadline Hero", "Never miss a deadline for a week", "false"},
            {"🚀", "Fast Finisher", "Complete 5 tasks in one day", "false"},
            {"💎", "Perfectionist", "100% success rate for a month", "false"},
            {"🔥", "Streak Master", "30-day completion streak", "false"},
            {"⭐", "Task Legend", "Complete 100 total tasks", "false"}
        };
        
        JPanel badgesGrid = new JPanel(new GridLayout(2, 3, 15, 15));
        badgesGrid.setOpaque(false);
        
        for (String[] achievement : achievements) {
            badgesGrid.add(createAchievementBadge(achievement[0], achievement[1], achievement[2], Boolean.parseBoolean(achievement[3])));
        }
        
        panel.add(badgesGrid);
        return panel;
    }
    
    private JPanel createAchievementBadge(String icon, String title, String description, boolean unlocked) {
        JPanel badge = new JPanel();
        badge.setOpaque(true);
        badge.setBackground(unlocked ? new Color(45, 55, 70) : new Color(30, 35, 45));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(unlocked ? new Color(255, 215, 0) : new Color(60, 70, 85), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(unlocked ? new Color(255, 215, 0) : new Color(80, 85, 90));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(unlocked ? TEXT_COLOR : new Color(100, 105, 110));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(unlocked ? TEXT_SECONDARY : new Color(80, 85, 90));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        badge.add(iconLabel);
        badge.add(Box.createVerticalStrut(5));
        badge.add(titleLabel);
        badge.add(Box.createVerticalStrut(3));
        badge.add(descLabel);
        
        return badge;
    }
    
    private JComponent buildProgressTrackerView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(40, 50, 65));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel("Progress Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        panel.add(title, BorderLayout.NORTH);
        
        // Progress chart
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int pad = 30;
                
                // Chart area
                int chartW = w - pad * 2;
                int chartH = h - pad * 2;
                int chartX = pad;
                int chartY = pad;
                
                // Background
                g2.setColor(new Color(30, 40, 55));
                g2.fillRect(chartX, chartY, chartW, chartH);
                
                // Grid lines
                g2.setColor(new Color(50, 60, 75));
                for (int i = 0; i <= 7; i++) {
                    int x = chartX + (chartW * i / 7);
                    g2.drawLine(x, chartY, x, chartY + chartH);
                }
                for (int i = 0; i <= 5; i++) {
                    int y = chartY + (chartH * i / 5);
                    g2.drawLine(chartX, y, chartX + chartW, y);
                }
                
                // Progress line (flat at 0 - not initialized)
                g2.setColor(new Color(100, 180, 220));
                g2.setStroke(new BasicStroke(3));
                int baseY = chartY + chartH - 20;
                g2.drawLine(chartX, baseY, chartX + chartW, baseY);
                
                // Milestone markers
                g2.setColor(new Color(255, 150, 100));
                g2.fillOval(chartX + chartW/2 - 5, baseY - 5, 10, 10);
                
                // Labels
                g2.setColor(TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                for (int i = 0; i < days.length; i++) {
                    int x = chartX + (chartW * i / 7) + chartW/14;
                    g2.drawString(days[i], x - 10, chartY + chartH + 15);
                }
                
                // No data message
                g2.setColor(TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                String message = "No progress data yet - system not initialized";
                FontMetrics fm = g2.getFontMetrics();
                int msgX = (w - fm.stringWidth(message)) / 2;
                int msgY = h / 2;
                g2.drawString(message, msgX, msgY);
                
                g2.dispose();
            }
        };
        chartPanel.setPreferredSize(new Dimension(500, 200));
        chartPanel.setOpaque(false);
        
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    // Components Section Views
    private JComponent buildDeadlineTrackerView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Deadline Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Mini calendar
        JPanel calendarPanel = buildMiniCalendar();
        contentPanel.add(calendarPanel, BorderLayout.WEST);
        
        // Countdown timer and reminders
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        
        rightPanel.add(buildCountdownTimer());
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(buildRemindersPanel());
        
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        panel.add(contentPanel);
        
        return panel;
    }
    
    private JPanel buildMiniCalendar() {
        JPanel calendar = new JPanel();
        calendar.setOpaque(true);
        calendar.setBackground(new Color(40, 50, 65));
        calendar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        calendar.setPreferredSize(new Dimension(250, 200));
        
        // Calendar header
        JLabel monthLabel = new JLabel("January 2024");
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        monthLabel.setForeground(TEXT_COLOR);
        monthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Calendar grid
        JPanel gridPanel = new JPanel(new GridLayout(7, 7, 2, 2));
        gridPanel.setOpaque(false);
        
        // Day headers
        String[] dayHeaders = {"S", "M", "T", "W", "T", "F", "S"};
        for (String day : dayHeaders) {
            JLabel dayLabel = new JLabel(day, JLabel.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            dayLabel.setForeground(TEXT_SECONDARY);
            gridPanel.add(dayLabel);
        }
        
        // Calendar days with deadline dots
        for (int day = 1; day <= 31; day++) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setOpaque(false);
            dayPanel.setPreferredSize(new Dimension(25, 20));
            
            JLabel dayLabel = new JLabel(String.valueOf(day), JLabel.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            dayLabel.setForeground(TEXT_COLOR);
            dayPanel.add(dayLabel, BorderLayout.CENTER);
            
            // No deadline dots - system not initialized
            
            gridPanel.add(dayPanel);
        }
        
        calendar.setLayout(new BoxLayout(calendar, BoxLayout.Y_AXIS));
        calendar.add(monthLabel);
        calendar.add(Box.createVerticalStrut(10));
        calendar.add(gridPanel);
        
        return calendar;
    }
    
    private JComponent buildCountdownTimer() {
        JPanel timerPanel = new JPanel();
        timerPanel.setOpaque(true);
        timerPanel.setBackground(new Color(45, 55, 70));
        timerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 150, 100), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        
        JLabel timerTitle = new JLabel("⏰ Next Deadline");
        timerTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timerTitle.setForeground(TEXT_COLOR);
        timerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel timerValue = new JLabel("No deadlines");
        timerValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        timerValue.setForeground(new Color(255, 150, 100));
        timerValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel timerTask = new JLabel("System not initialized");
        timerTask.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timerTask.setForeground(TEXT_SECONDARY);
        timerTask.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        timerPanel.add(timerTitle);
        timerPanel.add(Box.createVerticalStrut(5));
        timerPanel.add(timerValue);
        timerPanel.add(Box.createVerticalStrut(5));
        timerPanel.add(timerTask);
        
        return timerPanel;
    }
    
    private JComponent buildRemindersPanel() {
        JPanel remindersPanel = new JPanel();
        remindersPanel.setOpaque(true);
        remindersPanel.setBackground(new Color(40, 50, 65));
        remindersPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        remindersPanel.setLayout(new BoxLayout(remindersPanel, BoxLayout.Y_AXIS));
        
        JLabel remindersTitle = new JLabel("🔔 Reminders");
        remindersTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        remindersTitle.setForeground(TEXT_COLOR);
        remindersTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        remindersPanel.add(remindersTitle);
        remindersPanel.add(Box.createVerticalStrut(10));
        
        // Reminder cards
        String[][] reminders = {
            {"No reminders yet", "System not initialized", "gray"}
        };
        
        for (String[] reminder : reminders) {
            JPanel reminderCard = new JPanel(new BorderLayout());
            reminderCard.setOpaque(true);
            Color bgColor = switch (reminder[2]) {
                case "red" -> new Color(60, 40, 40);
                case "yellow" -> new Color(60, 55, 40);
                case "green" -> new Color(40, 60, 40);
                case "gray" -> new Color(50, 55, 60);
                default -> new Color(50, 55, 60);
            };
            reminderCard.setBackground(bgColor);
            reminderCard.setBorder(new EmptyBorder(8, 10, 8, 10));
            
            JLabel taskLabel = new JLabel(reminder[0]);
            taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            taskLabel.setForeground(TEXT_COLOR);
            
            JLabel timeLabel = new JLabel(reminder[1]);
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            timeLabel.setForeground(TEXT_SECONDARY);
            
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);
            
            JButton snoozeBtn = new JButton("⏰");
            snoozeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            snoozeBtn.setPreferredSize(new Dimension(25, 20));
            snoozeBtn.setBackground(new Color(100, 180, 220));
            snoozeBtn.setForeground(Color.WHITE);
            snoozeBtn.setBorderPainted(false);
            
            rightPanel.add(snoozeBtn);
            
            reminderCard.add(taskLabel, BorderLayout.WEST);
            reminderCard.add(timeLabel, BorderLayout.CENTER);
            reminderCard.add(rightPanel, BorderLayout.EAST);
            
            remindersPanel.add(reminderCard);
            remindersPanel.add(Box.createVerticalStrut(5));
        }
        
        return remindersPanel;
    }
    
    private JComponent buildSaveLoadView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Save/Load Progress");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Save/Load buttons panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setOpaque(false);
        
        // Save button
        JPanel savePanel = new JPanel();
        savePanel.setOpaque(false);
        savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.Y_AXIS));
        
        JButton saveBtn = createGlowingButton("💾 Save Progress", new Color(80, 200, 120));
        savePanel.add(saveBtn);
        savePanel.add(Box.createVerticalStrut(10));
        
        JLabel lastSavedLabel = new JLabel("Never saved");
        lastSavedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastSavedLabel.setForeground(TEXT_SECONDARY);
        lastSavedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        savePanel.add(lastSavedLabel);
        
        // Load button
        JPanel loadPanel = new JPanel();
        loadPanel.setOpaque(false);
        loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.Y_AXIS));
        
        JButton loadBtn = createGlowingButton("🔄 Load Progress", new Color(100, 180, 220));
        loadPanel.add(loadBtn);
        loadPanel.add(Box.createVerticalStrut(10));
        
        JLabel lastLoadedLabel = new JLabel("Last loaded: Never");
        lastLoadedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastLoadedLabel.setForeground(TEXT_SECONDARY);
        lastLoadedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadPanel.add(lastLoadedLabel);
        
        buttonsPanel.add(savePanel, BorderLayout.WEST);
        buttonsPanel.add(loadPanel, BorderLayout.EAST);
        
        panel.add(buttonsPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Sync indicator
        panel.add(buildSyncIndicator());
        
        return panel;
    }
    
    private JButton createGlowingButton(String text, Color glowColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(glowColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add glow effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(glowColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(glowColor);
            }
        });
        
        return button;
    }
    
    private JComponent buildSyncIndicator() {
        JPanel syncPanel = new JPanel(new BorderLayout());
        syncPanel.setOpaque(true);
        syncPanel.setBackground(new Color(40, 50, 65));
        syncPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel syncIcon = new JLabel("☁️");
        syncIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        syncIcon.setForeground(new Color(100, 180, 220));
        
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        
        JLabel statusLabel = new JLabel("Sync Status");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusValue = new JLabel("Not synced");
        statusValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusValue.setForeground(new Color(255, 150, 100));
        statusValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(statusValue);
        
        syncPanel.add(syncIcon, BorderLayout.WEST);
        syncPanel.add(statusPanel, BorderLayout.CENTER);
        
        return syncPanel;
    }

    // Other Section Views
    private JComponent buildSettingsView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Settings grid
        JPanel settingsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        settingsGrid.setOpaque(false);
        
        // Theme setting
        settingsGrid.add(createSettingCard("🎨", "Theme", "Dark/Light", "Dark", new String[]{"Dark", "Light"}));
        
        // Sound setting
        settingsGrid.add(createSettingCard("🔊", "Sound", "Notifications", "On", new String[]{"On", "Off"}));
        
        // Notifications setting
        settingsGrid.add(createSettingCard("🔔", "Notifications", "Alerts & Reminders", "Enabled", new String[]{"Enabled", "Disabled"}));
        
        // Language setting
        settingsGrid.add(createSettingCard("🌐", "Language", "Interface Language", "English", new String[]{"English", "Spanish", "French", "German"}));
        
        panel.add(settingsGrid);
        panel.add(Box.createVerticalStrut(20));
        
        // Additional settings
        JPanel additionalSettings = new JPanel();
        additionalSettings.setOpaque(true);
        additionalSettings.setBackground(new Color(40, 50, 65));
        additionalSettings.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        additionalSettings.setLayout(new BoxLayout(additionalSettings, BoxLayout.Y_AXIS));
        
        JLabel additionalTitle = new JLabel("Additional Settings");
        additionalTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        additionalTitle.setForeground(TEXT_COLOR);
        additionalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        additionalSettings.add(additionalTitle);
        additionalSettings.add(Box.createVerticalStrut(10));
        
        // Auto-save setting
        JPanel autoSavePanel = createToggleSetting("💾", "Auto-save", "Automatically save progress every 5 minutes", true);
        additionalSettings.add(autoSavePanel);
        additionalSettings.add(Box.createVerticalStrut(10));
        
        // Analytics setting
        JPanel analyticsPanel = createToggleSetting("📊", "Analytics", "Help improve the app by sharing usage data", false);
        additionalSettings.add(analyticsPanel);
        
        panel.add(additionalSettings);
        
        return panel;
    }
    
    private JPanel createSettingCard(String icon, String title, String description, String currentValue, String[] options) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(40, 50, 65));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        // Icon and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(new Color(100, 180, 220));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Dropdown
        JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.setSelectedItem(currentValue);
        dropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dropdown.setBackground(new Color(50, 60, 75));
        dropdown.setForeground(TEXT_COLOR);
        dropdown.setBorder(new EmptyBorder(5, 10, 5, 10));
        dropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(dropdown);
        
        return card;
    }
    
    private JPanel createToggleSetting(String icon, String title, String description, boolean enabled) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(new Color(100, 180, 220));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(iconLabel);
        leftPanel.add(Box.createVerticalStrut(2));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(2));
        leftPanel.add(descLabel);
        
        // Toggle switch
        JPanel togglePanel = new JPanel();
        togglePanel.setOpaque(false);
        togglePanel.setLayout(new BoxLayout(togglePanel, BoxLayout.Y_AXIS));
        
        JLabel toggleLabel = new JLabel(enabled ? "ON" : "OFF");
        toggleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toggleLabel.setForeground(enabled ? new Color(80, 200, 120) : new Color(150, 150, 150));
        toggleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton toggleBtn = new JButton(enabled ? "●" : "○");
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        toggleBtn.setForeground(enabled ? new Color(80, 200, 120) : new Color(150, 150, 150));
        toggleBtn.setBackground(new Color(50, 60, 75));
        toggleBtn.setBorderPainted(false);
        toggleBtn.setPreferredSize(new Dimension(40, 20));
        toggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        togglePanel.add(toggleLabel);
        togglePanel.add(Box.createVerticalStrut(5));
        togglePanel.add(toggleBtn);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(togglePanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JComponent buildHelpDocsView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Help & Documentation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(true);
        searchPanel.setBackground(new Color(40, 50, 65));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JTextField searchField = new JTextField("Search Help Topics...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setBackground(new Color(50, 60, 75));
        searchField.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        JButton searchBtn = new JButton("🔍");
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBtn.setBackground(new Color(100, 180, 220));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setPreferredSize(new Dimension(40, 35));
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        
        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Help sections
        JPanel helpGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        helpGrid.setOpaque(false);
        
        helpGrid.add(createHelpCard("📚", "Documentation", "User guides and tutorials", "View Docs"));
        helpGrid.add(createHelpCard("💬", "Live Chat", "Get instant support", "Start Chat"));
        helpGrid.add(createHelpCard("❓", "FAQs", "Frequently asked questions", "Browse FAQs"));
        helpGrid.add(createHelpCard("📧", "Contact Support", "Email support team", "Send Message"));
        
        panel.add(helpGrid);
        panel.add(Box.createVerticalStrut(20));
        
        // FAQ section
        JPanel faqPanel = new JPanel();
        faqPanel.setOpaque(true);
        faqPanel.setBackground(new Color(40, 50, 65));
        faqPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        faqPanel.setLayout(new BoxLayout(faqPanel, BoxLayout.Y_AXIS));
        
        JLabel faqTitle = new JLabel("Frequently Asked Questions");
        faqTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        faqTitle.setForeground(TEXT_COLOR);
        faqTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        faqPanel.add(faqTitle);
        faqPanel.add(Box.createVerticalStrut(10));
        
        String[][] faqs = {
            {"How do I create a new task?", "Click the 'Add New Task' button in the Assigned Tasks section."},
            {"Can I change the theme?", "Yes, go to Settings and select your preferred theme."},
            {"How do I save my progress?", "Use the Save/Load Progress section or enable auto-save in Settings."},
            {"What are achievements?", "Achievements are badges you earn by completing tasks and maintaining streaks."}
        };
        
        for (String[] faq : faqs) {
            JPanel faqItem = createFAQItem(faq[0], faq[1]);
            faqPanel.add(faqItem);
            faqPanel.add(Box.createVerticalStrut(8));
        }
        
        panel.add(faqPanel);
        
        return panel;
    }
    
    private JPanel createHelpCard(String icon, String title, String description, String buttonText) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(40, 50, 65));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(new Color(100, 180, 220));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton actionBtn = new JButton(buttonText);
        actionBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actionBtn.setBackground(new Color(100, 180, 220));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setBorderPainted(false);
        actionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionBtn.setPreferredSize(new Dimension(120, 30));
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(actionBtn);
        
        return card;
    }
    
    private JPanel createFAQItem(String question, String answer) {
        JPanel faqItem = new JPanel();
        faqItem.setOpaque(true);
        faqItem.setBackground(new Color(50, 60, 75));
        faqItem.setBorder(new EmptyBorder(10, 15, 10, 15));
        faqItem.setLayout(new BoxLayout(faqItem, BoxLayout.Y_AXIS));
        
        JLabel questionLabel = new JLabel("Q: " + question);
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel answerLabel = new JLabel("A: " + answer);
        answerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        answerLabel.setForeground(TEXT_SECONDARY);
        answerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        faqItem.add(questionLabel);
        faqItem.add(Box.createVerticalStrut(3));
        faqItem.add(answerLabel);
        
        return faqItem;
    }
    
    private JComponent buildExitView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Centered exit confirmation
        JPanel exitPanel = new JPanel();
        exitPanel.setOpaque(true);
        exitPanel.setBackground(new Color(45, 55, 70));
        exitPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
            new EmptyBorder(40, 40, 40, 40)
        ));
        exitPanel.setLayout(new BoxLayout(exitPanel, BoxLayout.Y_AXIS));
        exitPanel.setPreferredSize(new Dimension(400, 250));
        
        // Exit icon and title
        JLabel exitIcon = new JLabel("⚠️");
        exitIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        exitIcon.setForeground(new Color(255, 150, 100));
        exitIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel exitTitle = new JLabel("Exit Application");
        exitTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        exitTitle.setForeground(new Color(255, 100, 100));
        exitTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel exitMessage = new JLabel("Are you sure you want to exit?");
        exitMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitMessage.setForeground(TEXT_COLOR);
        exitMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setOpaque(false);
        
        JButton saveExitBtn = new JButton("💾 Save & Exit");
        saveExitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveExitBtn.setBackground(new Color(80, 200, 120));
        saveExitBtn.setForeground(Color.WHITE);
        saveExitBtn.setBorderPainted(false);
        saveExitBtn.setPreferredSize(new Dimension(140, 40));
        saveExitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(100, 110, 120));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonsPanel.add(saveExitBtn);
        buttonsPanel.add(cancelBtn);
        
        exitPanel.add(exitIcon);
        exitPanel.add(Box.createVerticalStrut(15));
        exitPanel.add(exitTitle);
        exitPanel.add(Box.createVerticalStrut(10));
        exitPanel.add(exitMessage);
        exitPanel.add(Box.createVerticalStrut(20));
        exitPanel.add(buttonsPanel);
        
        // Center the exit panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(exitPanel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPlayerInfoBar() {
        JPanel infoBar = new JPanel(new BorderLayout());
        infoBar.setOpaque(true);
        infoBar.setBackground(new Color(25, 30, 40)); // Darker, more modern background
        infoBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        infoBar.setPreferredSize(new Dimension(0, 65));
        
        // Add subtle gradient effect
        infoBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 30, 40),
                    0, getHeight(), new Color(20, 25, 35)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle top border
                g2.setColor(new Color(100, 180, 220, 30));
                g2.fillRect(0, 0, getWidth(), 1);
                
                g2.dispose();
            }
        };
        infoBar.setPreferredSize(new Dimension(0, 65));
        
        // Left side - Username and Level with modern styling
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        String username = (profile != null && profile.getUsername() != null) ? profile.getUsername() : "Player";
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(255, 255, 255)); // Pure white for emphasis
        usernameLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        // Level badge
        JPanel levelBadge = new JPanel();
        levelBadge.setOpaque(true);
        levelBadge.setBackground(new Color(100, 180, 220));
        levelBadge.setBorder(new EmptyBorder(4, 8, 4, 8));
        levelBadge.setLayout(new BoxLayout(levelBadge, BoxLayout.X_AXIS));
        
        JLabel levelIcon = new JLabel("⭐");
        levelIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        levelIcon.setForeground(Color.WHITE);
        
        JLabel levelLabel = new JLabel("Level " + currentLevel);
        levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        levelLabel.setForeground(Color.WHITE);
        
        levelBadge.add(levelIcon);
        levelBadge.add(Box.createHorizontalStrut(4));
        levelBadge.add(levelLabel);
        
        leftPanel.add(usernameLabel);
        leftPanel.add(levelBadge);
        
        // Center - Enhanced XP Progress Bar
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 30, 0, 30));
        
        // XP container with modern styling
        JPanel xpContainer = new JPanel(new BorderLayout());
        xpContainer.setOpaque(false);
        
        JProgressBar xpBar = new JProgressBar(0, maxXP);
        xpBar.setValue(currentXP);
        xpBar.setStringPainted(false);
        xpBar.setForeground(new Color(80, 200, 120)); // Green for progress
        xpBar.setBackground(new Color(40, 50, 65));
        xpBar.setBorderPainted(false);
        xpBar.setPreferredSize(new Dimension(250, 8));
        xpBar.setMaximumSize(new Dimension(250, 8));
        
        // Add rounded corners to progress bar
        xpBar = new JProgressBar(0, maxXP) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(new Color(40, 50, 65));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                
                // Progress
                if (getValue() > 0) {
                    g2.setColor(new Color(80, 200, 120));
                    int width = (int) ((getWidth() * getValue()) / getMaximum());
                    g2.fillRoundRect(0, 0, width, getHeight(), 4, 4);
                }
                
                g2.dispose();
            }
        };
        xpBar.setValue(currentXP);
        xpBar.setPreferredSize(new Dimension(250, 8));
        xpBar.setMaximumSize(new Dimension(250, 8));
        
        // XP text with better styling
        JLabel xpLabel = new JLabel(currentXP + " / " + maxXP + " XP");
        xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        xpLabel.setForeground(new Color(180, 190, 200));
        xpLabel.setHorizontalAlignment(JLabel.CENTER);
        xpLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        
        xpContainer.add(xpBar, BorderLayout.CENTER);
        xpContainer.add(xpLabel, BorderLayout.SOUTH);
        centerPanel.add(xpContainer, BorderLayout.CENTER);
        
        // Right side - Rank and Streak with modern cards
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        
        // Rank card
        JPanel rankCard = new JPanel();
        rankCard.setOpaque(true);
        rankCard.setBackground(new Color(45, 55, 70));
        rankCard.setBorder(new EmptyBorder(6, 12, 6, 12));
        rankCard.setLayout(new BoxLayout(rankCard, BoxLayout.X_AXIS));
        
        JLabel rankIcon = new JLabel("🏆");
        rankIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel rankLabel = new JLabel("Novice");
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rankLabel.setForeground(new Color(255, 215, 0)); // Gold color
        rankLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        
        rankCard.add(rankIcon);
        rankCard.add(rankLabel);
        
        // Streak card
        JPanel streakCard = new JPanel();
        streakCard.setOpaque(true);
        streakCard.setBackground(new Color(60, 40, 50));
        streakCard.setBorder(new EmptyBorder(6, 12, 6, 12));
        streakCard.setLayout(new BoxLayout(streakCard, BoxLayout.X_AXIS));
        
        JLabel streakIcon = new JLabel("🔥");
        streakIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel streakLabel = new JLabel("0");
        streakLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        streakLabel.setForeground(new Color(255, 150, 100));
        streakLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        
        streakCard.add(streakIcon);
        streakCard.add(streakLabel);
        
        rightPanel.add(rankCard);
        rightPanel.add(streakCard);
        
        infoBar.add(leftPanel, BorderLayout.WEST);
        infoBar.add(centerPanel, BorderLayout.CENTER);
        infoBar.add(rightPanel, BorderLayout.EAST);
        
        return infoBar;
    }
    
    private void showOnboardingModal() {
        JDialog onboardingDialog = new JDialog(this, "Welcome to ForgeGrid!", true);
        onboardingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        onboardingDialog.setSize(600, 500);
        onboardingDialog.setLocationRelativeTo(this);
        onboardingDialog.setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(true);
        mainPanel.setBackground(new Color(30, 35, 45));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Welcome header
        JLabel welcomeLabel = new JLabel("🎉 Welcome to ForgeGrid!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(100, 180, 220));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel subtitleLabel = new JLabel("Let's get you set up for maximum productivity!");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(180, 190, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Onboarding questions
        JPanel questionsPanel = new JPanel();
        questionsPanel.setOpaque(false);
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        
        // Question 1: Experience Level
        JPanel q1Panel = createQuestionPanel(
            "1. What's your experience with task management?",
            new String[]{"Beginner", "Intermediate", "Advanced", "Expert"},
            "experience"
        );
        questionsPanel.add(q1Panel);
        questionsPanel.add(Box.createVerticalStrut(20));
        
        // Question 2: Work Style
        JPanel q2Panel = createQuestionPanel(
            "2. How do you prefer to work?",
            new String[]{"Focused blocks", "Flexible timing", "Deadline-driven", "Collaborative"},
            "workStyle"
        );
        questionsPanel.add(q2Panel);
        questionsPanel.add(Box.createVerticalStrut(20));
        
        // Question 3: Goals
        JPanel q3Panel = createQuestionPanel(
            "3. What's your main productivity goal?",
            new String[]{"Complete more tasks", "Better time management", "Reduce stress", "Track progress"},
            "goals"
        );
        questionsPanel.add(q3Panel);
        questionsPanel.add(Box.createVerticalStrut(20));
        
        // Question 4: Notifications
        JPanel q4Panel = createQuestionPanel(
            "4. How often would you like reminders?",
            new String[]{"Frequent (every hour)", "Regular (every 2-3 hours)", "Minimal (daily)", "None"},
            "notifications"
        );
        questionsPanel.add(q4Panel);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        JButton skipBtn = new JButton("Skip for now");
        skipBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        skipBtn.setBackground(new Color(100, 110, 120));
        skipBtn.setForeground(Color.WHITE);
        skipBtn.setBorderPainted(false);
        skipBtn.setPreferredSize(new Dimension(120, 35));
        skipBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        skipBtn.addActionListener(e -> {
            onboardingCompleted = true;
            saveOnboardingStatus();
            onboardingDialog.dispose();
        });
        
        JButton completeBtn = new JButton("Complete Setup");
        completeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        completeBtn.setBackground(new Color(80, 200, 120));
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setBorderPainted(false);
        completeBtn.setPreferredSize(new Dimension(140, 35));
        completeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        completeBtn.addActionListener(e -> {
            saveOnboardingAnswers();
            onboardingCompleted = true;
            saveOnboardingStatus();
            onboardingDialog.dispose();
        });
        
        buttonsPanel.add(skipBtn);
        buttonsPanel.add(completeBtn);
        
        mainPanel.add(welcomeLabel);
        mainPanel.add(subtitleLabel);
        mainPanel.add(questionsPanel);
        mainPanel.add(buttonsPanel);
        
        onboardingDialog.add(mainPanel);
        onboardingDialog.setVisible(true);
    }
    
    private JPanel createQuestionPanel(String question, String[] options, String key) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel questionLabel = new JLabel(question);
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionLabel.setForeground(new Color(220, 230, 240));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        optionsPanel.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        for (String option : options) {
            JRadioButton radioBtn = new JRadioButton(option);
            radioBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            radioBtn.setForeground(new Color(180, 190, 200));
            radioBtn.setBackground(new Color(30, 35, 45));
            radioBtn.setOpaque(false);
            radioBtn.setFocusPainted(false);
            radioBtn.setActionCommand(option);
            radioBtn.setName(key); // Store the question key
            group.add(radioBtn);
            optionsPanel.add(radioBtn);
        }
        
        panel.add(questionLabel);
        panel.add(optionsPanel);
        
        return panel;
    }
    
    private void loadOnboardingStatus() {
        try {
            java.io.File file = new java.io.File(ONBOARDING_FILE);
            if (file.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(file);
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("completed=true")) {
                        onboardingCompleted = true;
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("Error loading onboarding status: " + e.getMessage());
        }
    }
    
    private void saveOnboardingStatus() {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(ONBOARDING_FILE);
            writer.write("completed=true\n");
            writer.write("timestamp=" + System.currentTimeMillis() + "\n");
            writer.close();
            System.out.println("Onboarding status saved!");
        } catch (Exception e) {
            System.out.println("Error saving onboarding status: " + e.getMessage());
        }
    }
    
    private void saveOnboardingAnswers() {
        // Collect answers from the dialog
        java.util.Map<String, String> answers = new java.util.HashMap<>();
        
        // In a real implementation, you would collect the actual radio button selections
        // For now, we'll save placeholder data
        answers.put("experience", "Not specified");
        answers.put("workStyle", "Not specified");
        answers.put("goals", "Not specified");
        answers.put("notifications", "Not specified");
        
        try {
            java.io.FileWriter writer = new java.io.FileWriter("user_preferences.json");
            writer.write("{\n");
            writer.write("  \"onboarding_completed\": true,\n");
            writer.write("  \"experience_level\": \"" + answers.get("experience") + "\",\n");
            writer.write("  \"work_style\": \"" + answers.get("workStyle") + "\",\n");
            writer.write("  \"productivity_goals\": \"" + answers.get("goals") + "\",\n");
            writer.write("  \"notification_preference\": \"" + answers.get("notifications") + "\",\n");
            writer.write("  \"completed_at\": " + System.currentTimeMillis() + "\n");
            writer.write("}\n");
            writer.close();
            System.out.println("Onboarding answers saved to user_preferences.json!");
        } catch (Exception e) {
            System.out.println("Error saving onboarding answers: " + e.getMessage());
        }
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
}
