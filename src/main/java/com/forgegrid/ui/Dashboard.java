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

            contentArea.add(placeholderLabel);
            contentArea.add(Box.createVerticalStrut(10));
            contentArea.add(infoLabel);
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20));
        panel.add(contentArea, BorderLayout.CENTER);
        
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
}
