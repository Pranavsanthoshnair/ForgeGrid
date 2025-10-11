package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import com.forgegrid.service.UserService;
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
    private final UserService userService;
    
    // UI Components
    private JPanel centerPanel;
    private CardLayout centerLayout;
    private JLabel currentViewLabel;
    
    // Player stats (placeholders for now)
    private int currentXP = 0;
    private int maxXP = 100;
    private int currentStreak = 0;
    private boolean onboardingCompleted = false;
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
    private static final String VIEW_DASHBOARD = "Home";
    private static final String VIEW_TASKS = "Tasks";
    private static final String VIEW_PROFILE = "Profile";
    private static final String VIEW_SETTINGS = "Settings";
    private static final String VIEW_HELP = "Help";
    private static final String VIEW_ASSIGNED = "Assigned Tasks";
    private static final String VIEW_COMPLETED = "Completed Tasks";
    private static final String VIEW_SKIPPED = "Missed Tasks";
    private static final String VIEW_GOATED = "Goated Tasks";
    private static final String VIEW_ACHIEVEMENTS = "Achievements";
    private static final String VIEW_PROGRESS = "Progress Tracker";

    public Dashboard(PlayerProfile profile) {
        this(profile, false);
    }
    
    public Dashboard(PlayerProfile profile, boolean skipWelcome) {
        this.profile = profile;
        this.userService = new UserService();
        
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
                    "Are you sure you want to exit ForgeGrid?",
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
            // Always show customization option in dashboard
            addCustomizationOption();
        });
    }
    
    /**
     * Creates the top panel with enhanced player stats
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // LEFT: User Info Section with Level Badge
        JPanel leftPanel = createUserInfoSection();
        
        // CENTER: XP Section with Rank and Streak
        JPanel centerPanel = createXPSection();
        
        // RIGHT: Customize Experience Button
        JPanel rightPanel = createCustomizeSection();
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    /**
     * Create user info card (matching sidebar footer style)
     */
    private JPanel createUserInfoSection() {
        JPanel userCard = new JPanel(new BorderLayout(10, 0));
        userCard.setOpaque(true);
        userCard.setBackground(new Color(30, 38, 50));
        userCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 60, 75), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        userCard.setPreferredSize(new Dimension(220, 65));
        
        // User icon
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userIcon.setPreferredSize(new Dimension(30, 30));
        userIcon.setHorizontalAlignment(SwingConstants.CENTER);
        userIcon.setVerticalAlignment(SwingConstants.CENTER);
        
        // User details
        JPanel userDetails = new JPanel();
        userDetails.setOpaque(false);
        userDetails.setLayout(new BoxLayout(userDetails, BoxLayout.Y_AXIS));
        
        JLabel userName = new JLabel(profile != null ? profile.getUsername() : "Guest");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userName.setForeground(TEXT_COLOR);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel userLevel = new JLabel("Level " + (profile != null ? profile.getLevel() : "1") + " • " + playerRank);
        userLevel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userLevel.setForeground(TEXT_SECONDARY);
        userLevel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userDetails.add(userName);
        userDetails.add(Box.createVerticalStrut(2));
        userDetails.add(userLevel);
        
        userCard.add(userIcon, BorderLayout.WEST);
        userCard.add(userDetails, BorderLayout.CENTER);
        
        return userCard;
    }
    
    /**
     * Create XP section with centered progress bar
     */
    private JPanel createXPSection() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 40, 0, 40));
        
        // LEFT: Rank label
        JLabel rankLabel = new JLabel("Rank: " + playerRank);
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rankLabel.setForeground(new Color(255, 215, 0));
        
        // CENTER: Enhanced XP Progress Bar
        JPanel xpBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Background
                g2.setColor(SIDEBAR_COLOR);
                g2.fillRoundRect(0, 0, width, height, 10, 10);
                
                // Progress
                if (maxXP > 0) {
                    int progressWidth = (int) ((width * currentXP) / maxXP);
                    g2.setColor(new Color(80, 200, 120));
                    g2.fillRoundRect(0, 0, progressWidth, height, 10, 10);
                }
                
                // XP Text inside bar
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String xpText = "XP: " + currentXP + " / " + maxXP;
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(xpText)) / 2;
                int textY = (height + fm.getAscent()) / 2 - 2;
                g2.drawString(xpText, textX, textY);
                
                g2.dispose();
            }
        };
        xpBarPanel.setPreferredSize(new Dimension(300, 24));
        xpBarPanel.setOpaque(false);
        
        // RIGHT: Streak with fire icon
        JLabel streakLabel = new JLabel("🔥 Streak: " + currentStreak);
        streakLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        streakLabel.setForeground(new Color(255, 150, 100));
        
        panel.add(rankLabel, BorderLayout.WEST);
        panel.add(xpBarPanel, BorderLayout.CENTER);
        panel.add(streakLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create customize section with red dot indicator
     */
    private JPanel createCustomizeSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        panel.setOpaque(false);
        
        // Check if customization is completed
        final boolean[] showRedDot = {true};
        if (profile != null && profile.getUsername() != null) {
            showRedDot[0] = !userService.hasCompletedCustomization(profile.getUsername());
        }
        
        // Customization label
        JLabel customizeLabel = new JLabel("Customize your experience");
        customizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customizeLabel.setForeground(ACCENT_COLOR);
        customizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        customizeLabel.setOpaque(false);
        
        // Red dot indicator (only show if not completed) - using a custom painted component
        JPanel redDotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (showRedDot[0]) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 60, 60));
                    g2.fillOval(2, 5, 8, 8);
                    g2.dispose();
                }
            }
        };
        redDotPanel.setPreferredSize(new Dimension(12, 18));
        redDotPanel.setOpaque(false);
        
        // Add hover effect
        customizeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCustomizationPanel();
                // Hide red dot after clicking
                showRedDot[0] = false;
                redDotPanel.repaint();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                customizeLabel.setForeground(ACCENT_COLOR.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                customizeLabel.setForeground(ACCENT_COLOR);
            }
        });
        
        panel.add(redDotPanel);
        panel.add(customizeLabel);
        
        return panel;
    }
    
    /**
     * Creates the ultra-modern professional sidebar menu
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(45, 55, 70)));
        
        // === HEADER SECTION ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        // Logo and Title Container
        JPanel logoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoTitlePanel.setOpaque(false);
        
        // Load ForgeGrid logo with better sizing
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(40, 40));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/com/forgegrid/icon/logo2_transparent.png"));
            Image scaledLogo = logoIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        } catch (Exception e) {
            logoLabel.setText("🏗️");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        }
        
        // Title with better styling
        JLabel titleLabel = new JLabel("ForgeGrid");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLabel.setForeground(new Color(255, 255, 255));
        
        logoTitlePanel.add(logoLabel);
        logoTitlePanel.add(titleLabel);
        headerPanel.add(logoTitlePanel, BorderLayout.CENTER);
        
        // === MENU SECTION ===
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(5, 12, 20, 12));
        
        // Add section label (centered)
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        labelPanel.setOpaque(false);
        labelPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
        labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel mainLabel = new JLabel("MAIN MENU");
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        mainLabel.setForeground(new Color(120, 130, 150));
        
        labelPanel.add(mainLabel);
        menuPanel.add(labelPanel);
        
        // Add menu items with icons
        menuPanel.add(createModernMenuItem("🏠", VIEW_DASHBOARD, true));
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(createModernMenuItem("📋", VIEW_TASKS, false));
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(createModernMenuItem("👤", VIEW_PROFILE, false));
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(createModernMenuItem("⚙️", VIEW_SETTINGS, false));
        
        // Add some spacing
        menuPanel.add(Box.createVerticalStrut(20));
        
        // Wrap menu in scroll pane
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        
        // Assemble sidebar (no footer needed - user info is in top header now)
        sidebarPanel.add(headerPanel, BorderLayout.NORTH);
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);
        
        return sidebarPanel;
    }
    
    /**
     * Track the currently selected menu item
     */
    private JPanel currentSelectedMenuItem = null;
    
    /**
     * Create an ultra-modern menu item with smooth animations
     */
    private JPanel createModernMenuItem(String icon, String text, boolean selected) {
        JPanel item = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                if (getBackground().getRGB() != SIDEBAR_COLOR.getRGB()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                
                g2.dispose();
            }
        };
        
        item.setOpaque(false);
        item.setBackground(selected ? new Color(40, 50, 65) : SIDEBAR_COLOR);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, selected ? ACCENT_COLOR : new Color(0, 0, 0, 0)),
            new EmptyBorder(14, 16, 14, 16)
        ));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        
        // Icon with better styling
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setForeground(selected ? ACCENT_COLOR : new Color(140, 150, 170));
        iconLabel.setPreferredSize(new Dimension(25, 25));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Text with better styling
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(selected ? new Color(240, 245, 250) : new Color(160, 170, 185));
        
        item.add(iconLabel, BorderLayout.WEST);
        item.add(textLabel, BorderLayout.CENTER);
        
        // Track if this is the first selected item
        if (selected) {
            currentSelectedMenuItem = item;
        }
        
        // Add smooth hover and click effects with timer for animation
        final Timer[] hoverTimer = {null};
        
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (item != currentSelectedMenuItem) {
                    // Cancel any existing timer
                    if (hoverTimer[0] != null && hoverTimer[0].isRunning()) {
                        hoverTimer[0].stop();
                    }
                    
                    // Smooth transition
                    hoverTimer[0] = new Timer(10, new ActionListener() {
                        float alpha = 0f;
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            alpha += 0.15f;
                            if (alpha >= 1f) {
                                alpha = 1f;
                                ((Timer) e.getSource()).stop();
                            }
                            item.setBackground(new Color(35, 45, 60));
                            textLabel.setForeground(new Color(240, 245, 250));
                            iconLabel.setForeground(ACCENT_COLOR);
                            item.repaint();
                        }
                    });
                    hoverTimer[0].start();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (item != currentSelectedMenuItem) {
                    // Cancel timer
                    if (hoverTimer[0] != null && hoverTimer[0].isRunning()) {
                        hoverTimer[0].stop();
                    }
                    
                    item.setBackground(SIDEBAR_COLOR);
                    textLabel.setForeground(new Color(160, 170, 185));
                    iconLabel.setForeground(new Color(140, 150, 170));
                    item.repaint();
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Deselect previous item
                if (currentSelectedMenuItem != null && currentSelectedMenuItem != item) {
                    currentSelectedMenuItem.setBackground(SIDEBAR_COLOR);
                    currentSelectedMenuItem.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(0, 0, 0, 0)),
                        new EmptyBorder(14, 16, 14, 16)
                    ));
                    Component[] components = currentSelectedMenuItem.getComponents();
                    if (components.length >= 2) {
                        ((JLabel) components[0]).setForeground(new Color(140, 150, 170));
                        ((JLabel) components[1]).setForeground(new Color(160, 170, 185));
                    }
                    currentSelectedMenuItem.repaint();
                }
                
                // Select new item
                item.setBackground(new Color(40, 50, 65));
                item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT_COLOR),
                    new EmptyBorder(14, 16, 14, 16)
                ));
                textLabel.setForeground(new Color(240, 245, 250));
                iconLabel.setForeground(ACCENT_COLOR);
                currentSelectedMenuItem = item;
                item.repaint();
                
                // Switch view
                switchView(text);
            }
        });
        
        return item;
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
        centerPanel.add(createViewPanel(VIEW_TASKS), VIEW_TASKS);
        centerPanel.add(createViewPanel(VIEW_PROFILE), VIEW_PROFILE);
        centerPanel.add(createViewPanel(VIEW_SETTINGS), VIEW_SETTINGS);
        
        container.add(centerPanel, BorderLayout.CENTER);
        
        return container;
    }
    
    /**
     * Creates a placeholder view panel
     */
    private JPanel createViewPanel(String viewName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
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
            contentArea.add(buildSimpleDashboardView());
        } else if (VIEW_TASKS.equals(viewName)) {
            contentArea.add(buildSimpleTasksView());
        } else if (VIEW_PROFILE.equals(viewName)) {
            contentArea.add(buildSimpleProfileView());
        } else if (VIEW_SETTINGS.equals(viewName)) {
            contentArea.add(buildSettingsView());
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
        
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    
    // Simplified Views
    private JComponent buildSimpleDashboardView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 20));
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Welcome Section
        JPanel welcomeSection = new JPanel();
        welcomeSection.setOpaque(false);
        welcomeSection.setLayout(new BoxLayout(welcomeSection, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Home");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeSection.add(title);
        
        // Stats Cards Section
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Stat Card 1: Total Tasks
        statsPanel.add(createModernStatCard("Total Tasks", "24", "📋", new Color(147, 51, 234), 75));
        
        // Stat Card 2: Completed
        statsPanel.add(createModernStatCard("Completed", "18", "✅", new Color(34, 139, 230), 75));
        
        // Stat Card 3: In Progress
        statsPanel.add(createModernStatCard("In Progress", "6", "⏳", new Color(251, 191, 36), 25));
        
        // Stat Card 4: Streak
        statsPanel.add(createModernStatCard("Streak", currentStreak + " days", "🔥", new Color(74, 222, 128), 90));
        
        welcomeSection.add(statsPanel);
        
        // Main Content Section
        JPanel contentSection = new JPanel(new GridLayout(1, 2, 20, 0));
        contentSection.setOpaque(false);
        contentSection.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Recent Activity Card
        JPanel activityCard = createModernCard("Recent Activity");
        JPanel activityContent = new JPanel();
        activityContent.setOpaque(false);
        activityContent.setLayout(new BoxLayout(activityContent, BoxLayout.Y_AXIS));
        activityContent.add(createActivityItem("Completed task: Setup Environment", "2 hours ago"));
        activityContent.add(Box.createVerticalStrut(10));
        activityContent.add(createActivityItem("Started task: Build Dashboard", "3 hours ago"));
        activityContent.add(Box.createVerticalStrut(10));
        activityContent.add(createActivityItem("Achieved level 1", "1 day ago"));
        activityCard.add(activityContent, BorderLayout.CENTER);
        contentSection.add(activityCard);
        
        // Quick Actions Card
        JPanel quickActionsCard = createModernCard("Quick Actions");
        JPanel actionsContent = new JPanel();
        actionsContent.setOpaque(false);
        actionsContent.setLayout(new BoxLayout(actionsContent, BoxLayout.Y_AXIS));
        actionsContent.add(createActionButton("➕ Create New Task", new Color(100, 180, 220)));
        actionsContent.add(Box.createVerticalStrut(12));
        actionsContent.add(createActionButton("📊 View Progress", new Color(100, 180, 220)));
        actionsContent.add(Box.createVerticalStrut(12));
        actionsContent.add(createActionButton("⚙️ Customize Settings", new Color(100, 180, 220)));
        quickActionsCard.add(actionsContent, BorderLayout.CENTER);
        contentSection.add(quickActionsCard);
        
        panel.add(welcomeSection, BorderLayout.NORTH);
        panel.add(contentSection, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a modern stat card with progress indicator
     */
    private JPanel createModernStatCard(String title, String value, String icon, Color accentColor, int progress) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
                BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor.darker())
            ),
            new EmptyBorder(18, 18, 18, 18)
        ));
        
        // Icon and Value Section
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(accentColor);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topSection.add(iconLabel, BorderLayout.WEST);
        topSection.add(valueLabel, BorderLayout.EAST);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        // Progress Bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setBackground(new Color(40, 50, 65));
        progressBar.setForeground(accentColor);
        progressBar.setBorderPainted(false);
        
        JPanel bottomSection = new JPanel(new BorderLayout(0, 8));
        bottomSection.setOpaque(false);
        bottomSection.add(titleLabel, BorderLayout.NORTH);
        bottomSection.add(progressBar, BorderLayout.SOUTH);
        
        card.add(topSection, BorderLayout.NORTH);
        card.add(bottomSection, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Create a modern card panel
     */
    private JPanel createModernCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        card.add(titleLabel, BorderLayout.NORTH);
        
        return card;
    }
    
    /**
     * Create an activity item
     */
    private JPanel createActivityItem(String activity, String time) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activityLabel.setForeground(TEXT_COLOR);
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(TEXT_SECONDARY);
        
        item.add(activityLabel, BorderLayout.CENTER);
        item.add(timeLabel, BorderLayout.EAST);
        
        return item;
    }
    
    /**
     * Create an action button
     */
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setPreferredSize(new Dimension(0, 40));
        
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
    
    private JComponent buildSimpleTasksView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 20));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ACCENT_COLOR);
        
        JButton addTaskBtn = new JButton("+ New Task");
        addTaskBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addTaskBtn.setForeground(Color.WHITE);
        addTaskBtn.setBackground(ACCENT_COLOR);
        addTaskBtn.setFocusPainted(false);
        addTaskBtn.setBorderPainted(false);
        addTaskBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTaskBtn.setPreferredSize(new Dimension(120, 35));
        
        header.add(title, BorderLayout.WEST);
        header.add(addTaskBtn, BorderLayout.EAST);
        
        // Tasks Table Card
        JPanel tasksCard = createModernCard("All Tasks");
        
        // Create sample task list
        JPanel tasksList = new JPanel();
        tasksList.setLayout(new BoxLayout(tasksList, BoxLayout.Y_AXIS));
        tasksList.setOpaque(false);
        
        tasksList.add(createTaskRow("Complete project documentation", "High", "In Progress", new Color(251, 191, 36)));
        tasksList.add(Box.createVerticalStrut(8));
        tasksList.add(createTaskRow("Review code changes", "Medium", "Pending", new Color(100, 180, 220)));
        tasksList.add(Box.createVerticalStrut(8));
        tasksList.add(createTaskRow("Setup testing environment", "High", "In Progress", new Color(251, 191, 36)));
        tasksList.add(Box.createVerticalStrut(8));
        tasksList.add(createTaskRow("Update dependencies", "Low", "Completed", new Color(74, 222, 128)));
        tasksList.add(Box.createVerticalStrut(8));
        tasksList.add(createTaskRow("Fix UI bugs", "Medium", "Pending", new Color(100, 180, 220)));
        
        tasksCard.add(tasksList, BorderLayout.CENTER);
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(tasksCard, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a task row
     */
    private JPanel createTaskRow(String taskName, String priority, String status, Color statusColor) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(true);
        row.setBackground(new Color(35, 45, 60));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 60, 75), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        // Left: Task name
        JLabel nameLabel = new JLabel(taskName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_COLOR);
        
        // Center: Priority badge
        JLabel priorityLabel = new JLabel(priority);
        priorityLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        priorityLabel.setForeground(TEXT_COLOR);
        priorityLabel.setOpaque(true);
        priorityLabel.setBackground(new Color(50, 60, 75));
        priorityLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        
        // Right: Status badge
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(statusColor);
        statusLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(priorityLabel);
        rightPanel.add(statusLabel);
        
        row.add(nameLabel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);
        
        // Add hover effect
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(45, 55, 70));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(new Color(35, 45, 60));
            }
        });
        
        return row;
    }
    
    private JComponent buildSimpleProfileView() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 20));
        
        // Header
        JLabel title = new JLabel("Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ACCENT_COLOR);
        
        // Get user profile details
        java.util.Map<String, String> profileDetails = userService.getUserProfileDetails(profile.getUsername());
        
        // Main content panel (will be scrollable)
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Profile Stats Cards (Top Section)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Level Card
        JPanel levelCard = createProfileStatCard("Level", String.valueOf(currentLevel), "🏆", ACCENT_COLOR);
        statsPanel.add(levelCard);
        
        // XP Card
        JPanel xpCard = createProfileStatCard("Total XP", String.valueOf(currentXP) + " / " + maxXP, "⭐", new Color(255, 215, 0));
        statsPanel.add(xpCard);
        
        // Rank Card
        JPanel rankCard = createProfileStatCard("Rank", playerRank, "🎖️", new Color(147, 51, 234));
        statsPanel.add(rankCard);
        
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Profile Information Card
        JPanel profilePanel = new JPanel();
        profilePanel.setOpaque(true);
        profilePanel.setBackground(PANEL_COLOR);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Card title
        JLabel cardTitle = new JLabel("Account Information");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(TEXT_COLOR);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        profilePanel.add(cardTitle);
        
        // Username
        JPanel usernamePanel = createProfileField("Username", profile.getUsername(), false, null);
        profilePanel.add(usernamePanel);
        profilePanel.add(Box.createVerticalStrut(15));
        
        // Level
        JPanel levelPanel = createProfileField("Level", String.valueOf(profile.getLevel()), false, null);
        profilePanel.add(levelPanel);
        profilePanel.add(Box.createVerticalStrut(15));
        
        // Email
        JTextField emailField = new JTextField(profileDetails.getOrDefault("email", ""));
        JPanel emailPanel = createProfileField("Email", null, true, emailField);
        profilePanel.add(emailPanel);
        profilePanel.add(Box.createVerticalStrut(15));
        
        // Programming Language
        String[] languages = {"Java", "Python", "JavaScript", "C++", "C#", "Go", "Rust", "Ruby", "PHP", "Swift"};
        JComboBox<String> languageBox = new JComboBox<>(languages);
        languageBox.setSelectedItem(profileDetails.getOrDefault("onboarding_language", "Java"));
        languageBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        languageBox.setBackground(Color.WHITE);
        languageBox.setForeground(Color.BLACK);
        JPanel languagePanel = createProfileFieldWithComponent("Preferred Language", languageBox);
        profilePanel.add(languagePanel);
        profilePanel.add(Box.createVerticalStrut(15));
        
        // Skill Level
        String[] skillLevels = {"Beginner", "Intermediate", "Advanced", "Expert"};
        JComboBox<String> skillBox = new JComboBox<>(skillLevels);
        skillBox.setSelectedItem(profileDetails.getOrDefault("onboarding_skill", "Beginner"));
        skillBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        skillBox.setBackground(Color.WHITE);
        skillBox.setForeground(Color.BLACK);
        JPanel skillPanel = createProfileFieldWithComponent("Skill Level", skillBox);
        profilePanel.add(skillPanel);
        profilePanel.add(Box.createVerticalStrut(15));
        
        // Preferred Time for Tasks
        String[] times = {"Morning (6 AM - 12 PM)", "Afternoon (12 PM - 6 PM)", "Evening (6 PM - 12 AM)", "Night (12 AM - 6 AM)"};
        JComboBox<String> timeBox = new JComboBox<>(times);
        timeBox.setSelectedItem(profileDetails.getOrDefault("notification_preference", "Morning (6 AM - 12 PM)"));
        timeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeBox.setBackground(Color.WHITE);
        timeBox.setForeground(Color.BLACK);
        JPanel timePanel = createProfileFieldWithComponent("Preferred Time for Tasks", timeBox);
        profilePanel.add(timePanel);
        profilePanel.add(Box.createVerticalStrut(20));
        
        // Save button
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setForeground(TEXT_COLOR);
        saveButton.setBackground(ACCENT_COLOR);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setMaximumSize(new Dimension(150, 40));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                saveButton.setBackground(ACCENT_COLOR.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                saveButton.setBackground(ACCENT_COLOR);
            }
        });
        
        saveButton.addActionListener(e -> {
            String email = emailField.getText();
            String language = (String) languageBox.getSelectedItem();
            String skill = (String) skillBox.getSelectedItem();
            String time = (String) timeBox.getSelectedItem();
            
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = userService.updateUserProfileDetails(profile.getUsername(), email, language, skill, time);
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        profilePanel.add(saveButton);
        
        contentPanel.add(profilePanel);
        
        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a profile stat card
     */
    private JPanel createProfileStatCard(String label, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setForeground(accentColor);
        
        // Right panel with label and value
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueText.setForeground(TEXT_COLOR);
        valueText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rightPanel.add(labelText);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(valueText);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createProfileField(String label, String value, boolean editable, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(ACCENT_COLOR);
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (editable && textField != null) {
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setBackground(Color.WHITE);
            textField.setForeground(Color.BLACK);
            textField.setCaretColor(Color.BLACK);
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 95), 1),
                new EmptyBorder(8, 10, 8, 10)
            ));
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            textField.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            panel.add(labelComponent);
            panel.add(Box.createVerticalStrut(5));
            panel.add(textField);
        } else {
            JLabel valueComponent = new JLabel(value);
            valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            valueComponent.setForeground(TEXT_COLOR);
            valueComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            panel.add(labelComponent);
            panel.add(Box.createVerticalStrut(5));
            panel.add(valueComponent);
        }
        
        return panel;
    }
    
    private JPanel createProfileFieldWithComponent(String label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(ACCENT_COLOR);
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(labelComponent);
        panel.add(Box.createVerticalStrut(5));
        panel.add(component);
        
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
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Progress Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        // Center the content area with dashboard components
        JPanel centeredContent = new JPanel();
        centeredContent.setOpaque(false);
        centeredContent.setLayout(new BoxLayout(centeredContent, BoxLayout.Y_AXIS));
        centeredContent.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add the dashboard components to progress tracker
        centeredContent.add(buildOverallProgressWidgetZero());
        centeredContent.add(Box.createVerticalStrut(16));
        centeredContent.add(buildDailyGoalWidgetZero());
        centeredContent.add(Box.createVerticalStrut(24));
        centeredContent.add(buildWeeklyProductivityWidgetZero());
        
        panel.add(centeredContent);
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
        panel.setLayout(new BorderLayout(0, 20));
        
        // Header
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ACCENT_COLOR);
        
        // Main content with GridLayout for two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        // Left Column - Preferences Card
        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        
        JPanel preferencesCard = new JPanel();
        preferencesCard.setOpaque(true);
        preferencesCard.setBackground(PANEL_COLOR);
        preferencesCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        preferencesCard.setLayout(new BoxLayout(preferencesCard, BoxLayout.Y_AXIS));
        
        JLabel preferencesTitle = new JLabel("General Preferences");
        preferencesTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        preferencesTitle.setForeground(TEXT_COLOR);
        preferencesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        preferencesCard.add(preferencesTitle);
        preferencesCard.add(Box.createVerticalStrut(20));
        
        // Sound setting
        JPanel soundPanel = createEnhancedToggleSetting("🔊", "Sound Effects", "Enable sound effects and notifications", true);
        preferencesCard.add(soundPanel);
        preferencesCard.add(Box.createVerticalStrut(15));
        
        // Notifications setting
        JPanel notificationsPanel = createEnhancedToggleSetting("🔔", "Notifications", "Show alerts and reminders", true);
        preferencesCard.add(notificationsPanel);
        preferencesCard.add(Box.createVerticalStrut(15));
        
        // Auto-save setting
        JPanel autoSavePanel = createEnhancedToggleSetting("💾", "Auto-Save", "Automatically save progress every 5 minutes", true);
        preferencesCard.add(autoSavePanel);
        
        leftColumn.add(preferencesCard);
        
        // Right Column - Account & Security Card
        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        
        JPanel accountCard = new JPanel();
        accountCard.setOpaque(true);
        accountCard.setBackground(PANEL_COLOR);
        accountCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        accountCard.setLayout(new BoxLayout(accountCard, BoxLayout.Y_AXIS));
        
        JLabel accountLabel = new JLabel("Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        accountLabel.setForeground(TEXT_COLOR);
        accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountCard.add(accountLabel);
        accountCard.add(Box.createVerticalStrut(20));
        
        // Account info display
        JPanel accountInfo = new JPanel();
        accountInfo.setOpaque(true);
        accountInfo.setBackground(new Color(35, 45, 60));
        accountInfo.setBorder(new EmptyBorder(15, 15, 15, 15));
        accountInfo.setLayout(new BoxLayout(accountInfo, BoxLayout.Y_AXIS));
        
        JLabel usernameInfo = new JLabel("👤 " + profile.getUsername());
        usernameInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameInfo.setForeground(TEXT_COLOR);
        usernameInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel levelInfo = new JLabel("🏆 Level " + currentLevel);
        levelInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        levelInfo.setForeground(TEXT_COLOR);
        levelInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        accountInfo.add(usernameInfo);
        accountInfo.add(Box.createVerticalStrut(8));
        accountInfo.add(levelInfo);
        
        accountCard.add(accountInfo);
        accountCard.add(Box.createVerticalStrut(20));
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(220, 60, 60));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.setPreferredSize(new Dimension(0, 40));
        
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(255, 80, 80));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 60, 60));
            }
        });
        
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                Dashboard.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                handleLogout();
            }
        });
        
        accountCard.add(logoutButton);
        
        rightColumn.add(accountCard);
        
        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create an enhanced toggle setting
     */
    private JPanel createEnhancedToggleSetting(String icon, String title, String description, boolean enabled) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(true);
        panel.setBackground(new Color(35, 45, 60));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(ACCENT_COLOR);
        
        // Middle panel with title and description
        JPanel middlePanel = new JPanel();
        middlePanel.setOpaque(false);
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        middlePanel.add(titleLabel);
        middlePanel.add(Box.createVerticalStrut(4));
        middlePanel.add(descLabel);
        
        // Toggle switch (simple checkbox styled as toggle)
        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(enabled);
        toggle.setOpaque(false);
        toggle.setFocusPainted(false);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(middlePanel, BorderLayout.CENTER);
        panel.add(toggle, BorderLayout.EAST);
        
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
        
        JLabel exitTitle = new JLabel("Logout Application");
        exitTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        exitTitle.setForeground(new Color(255, 100, 100));
        exitTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel exitMessage = new JLabel("Are you sure you want to logout?");
        exitMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitMessage.setForeground(TEXT_COLOR);
        exitMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setOpaque(false);
        
        JButton saveExitBtn = new JButton("💾 Save & Logout");
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
    
    
    
    
    
    
    private void loadOnboardingStatus() {
        // Load onboarding status from database instead of JSON files
        if (profile != null && profile.getUsername() != null) {
            onboardingCompleted = userService.hasCompletedOnboardingByUsername(profile.getUsername());
        }
    }
    
    
    
    
    
    /**
     * Add customization option to the dashboard
     */
    private void addCustomizationOption() {
        // This method is called after UI initialization to ensure customization option is always available
        // The actual customization option is added in createTopPanel()
    }
    
    /**
     * Show customization panel in the center area of the dashboard
     */
    private void showCustomizationPanel() {
        // Create a new panel for customization questions
        JPanel customizationPanel = new JPanel();
        customizationPanel.setBackground(BG_COLOR);
        customizationPanel.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("🎯 Customize Your Experience");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Help us personalize your ForgeGrid experience");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descLabel);
        
        // Questions panel
        JPanel questionsPanel = new JPanel();
        questionsPanel.setBackground(BG_COLOR);
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        // Store responses
        String[] responses = new String[4];
        
        // Question 1: Experience Level
        JPanel q1Panel = createCustomizationQuestion(
            "1. What's your experience with task management?",
            new String[]{"Beginner", "Intermediate", "Advanced", "Expert"},
            responses, 0
        );
        questionsPanel.add(q1Panel);
        questionsPanel.add(Box.createVerticalStrut(20));
        
        // Question 2: Work Style
        JPanel q2Panel = createCustomizationQuestion(
            "2. How do you prefer to work?",
            new String[]{"Focused blocks", "Flexible timing", "Deadline-driven", "Collaborative"},
            responses, 1
        );
        questionsPanel.add(q2Panel);
        questionsPanel.add(Box.createVerticalStrut(20));
        
        // Question 3: Goals
        JPanel q3Panel = createCustomizationQuestion(
            "3. What's your main productivity goal?",
            new String[]{"Complete more tasks", "Better time management", "Reduce stress", "Track progress"},
            responses, 2
        );
        questionsPanel.add(q3Panel);
        questionsPanel.add(Box.createVerticalStrut(20));
        
        // Question 4: Notifications
        JPanel q4Panel = createCustomizationQuestion(
            "4. How often would you like reminders?",
            new String[]{"Frequent (every hour)", "Regular (every 2-3 hours)", "Minimal (daily)", "None"},
            responses, 3
        );
        questionsPanel.add(q4Panel);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(30, 0, 20, 0));
        
        JButton saveBtn = new JButton("💾 Save Preferences");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(80, 200, 120));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorderPainted(false);
        saveBtn.setPreferredSize(new Dimension(180, 40));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> {
            saveCustomizationData(responses);
            // Switch back to dashboard view
            centerLayout.show(centerPanel, VIEW_DASHBOARD);
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.setBackground(new Color(100, 110, 120));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> {
            // Switch back to dashboard view
            centerLayout.show(centerPanel, VIEW_DASHBOARD);
        });
        
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);
        
        // Wrap questions panel in a scroll pane for better UX
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        
        // Add components to main panel
        customizationPanel.add(headerPanel, BorderLayout.NORTH);
        customizationPanel.add(scrollPane, BorderLayout.CENTER);
        customizationPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add to center panel and show
        centerPanel.add(customizationPanel, "CUSTOMIZATION");
        centerLayout.show(centerPanel, "CUSTOMIZATION");
    }
    
    /**
     * Create a customization question panel
     */
    private JPanel createCustomizationQuestion(String question, String[] options, String[] responses, int index) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel questionLabel = new JLabel(question);
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        ButtonGroup group = new ButtonGroup();
        for (String option : options) {
            JRadioButton radio = new JRadioButton(option);
            radio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            radio.setForeground(TEXT_SECONDARY);
            radio.setBackground(PANEL_COLOR);
            radio.setOpaque(false);
            radio.addActionListener(e -> {
                responses[index] = option;
            });
            group.add(radio);
            panel.add(radio);
        }
        
        panel.add(questionLabel, 0); // Add question at the top
        
        return panel;
    }
    
    /**
     * Save customization data to MySQL database (separate from onboarding data)
     */
    private void saveCustomizationData(String[] responses) {
        if (profile != null && profile.getUsername() != null) {
            String experience = responses[0] != null ? responses[0] : "Not specified";
            String workStyle = responses[1] != null ? responses[1] : "Not specified";
            String goals = responses[2] != null ? responses[2] : "Not specified";
            String notifications = responses[3] != null ? responses[3] : "Not specified";
            
            // Save to user_preferences table (separate from onboarding data)
            boolean saved = userService.saveUserPreferences(
                profile.getUsername(),
                experience,
                workStyle,
                goals,
                notifications
            );
            
            if (saved) {
                JOptionPane.showMessageDialog(this, 
                    "Preferences saved successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save preferences. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
     * Handle logout - return to AuthUI screen
     */
    private void handleLogout() {
        com.forgegrid.config.UserPreferences userPrefs = new com.forgegrid.config.UserPreferences();
        userPrefs.clearRememberMe();
        setVisible(false);
        dispose();
        SwingUtilities.invokeLater(() -> {
            AuthUI authUI = new AuthUI();
            authUI.setVisible(true);
        });
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
