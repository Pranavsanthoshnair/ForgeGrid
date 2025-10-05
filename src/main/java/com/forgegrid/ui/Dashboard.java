package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Dashboard extends JFrame {

    private final PlayerProfile profile;
    // Simple state for demo - all set to 0 for fresh start
    private int problemsSolved = 0;
    private int problemsGoal = 100;
    private int currentStreak = 0;
    private java.util.List<String> badges = new java.util.ArrayList<>();

    // UI references to update dynamically
    private JLabel problemsLabel;
    private JProgressBar problemsProgress;
    private JLabel streakLabel;
    // Profile labels to reflect onboarding selections
    private JLabel profileSkillLabel;
    private JLabel profileGoalLabel;
    private JLabel profileLanguageLabel;
    private JLabel profilePracticeLabel;
    private JPanel badgesPanel;
    private JPanel recommendationsPanel;

    // Main content switching (prevents overlapping text)
    private CardLayout mainContentLayout;
    private JPanel mainContent;
    private static final String VIEW_HOME = "home";
    private static final String VIEW_ACCOUNT = "account";
    private static final String VIEW_PROGRESS = "progress";
    private static final String VIEW_SETTINGS = "settings";
    private static final String VIEW_ONBOARDING = "onboarding";

    public Dashboard(PlayerProfile profile) {
        this(profile, false); // Default: show welcome screen
    }
    
    public Dashboard(PlayerProfile profile, boolean skipWelcome) {
        this.profile = profile;
        setTitle("ForgeGrid - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 480));

        initUI(skipWelcome);
    }

    private void initUI(boolean skipWelcome) {
        // Directly show dashboard without any welcome screen
        JPanel dashboardPanel = buildDashboardPanel();
        dashboardPanel.setBackground(new Color(25, 35, 55)); // Match AuthUI background
        setContentPane(dashboardPanel);
    }

    private JPanel buildDashboardPanel() {
        // Static solid background matching AuthUI
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Solid dark blue background matching AuthUI (25, 35, 55)
                g2.setColor(new Color(25, 35, 55));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        panel.setOpaque(true);
        panel.setLayout(new BorderLayout());

        // Left sidebar with dark blue theme styling
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dark blue gradient background
                GradientPaint sidebarGradient = new GradientPaint(
                    0, 0, new Color(20, 30, 55), // Darker blue
                    getWidth(), 0, new Color(25, 40, 70) // Slightly lighter blue
                );
                g2.setPaint(sidebarGradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Blue accent border on the right edge
                g2.setColor(new Color(60, 120, 200)); // Light blue accent
                g2.fillRect(getWidth() - 2, 0, 2, getHeight());
                
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0)); // Standard width for modern layout
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // 24px padding (3 * 8px base unit)

        JLabel menuTitle = new JLabel("Menu");
        menuTitle.setForeground(new Color(200, 220, 255)); // Light blue-white
        menuTitle.setFont(new Font("Inter", Font.BOLD, 16));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16))); // 16px spacing (2 * 8px base unit)

        JPanel navContainer = new JPanel();
        navContainer.setOpaque(false);
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));

        String[] items = {"Dashboard", "Account", "Progress", "Settings"};
        for (String item : items) {
            JLabel label = createNavLabel(item);
            navContainer.add(label);
        }
        sidebar.add(navContainer);

        // Main content area
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Removed global "Your Dashboard" heading to avoid showing it on all views

        // Dark Blue Theme Profile card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Enhanced shadow for depth
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(6, 10, getWidth() - 12, getHeight() - 12, 16, 16);
                
                // Dark blue gradient background
                GradientPaint cardGradient = new GradientPaint(
                    0, 0, new Color(25, 40, 70), // Darker blue
                    0, getHeight(), new Color(20, 30, 55) // Even darker blue
                );
                g2.setPaint(cardGradient);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 16, 16);
                
                // Blue accent border
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(60, 120, 200, 80)); // Light blue border
                g2.drawRoundRect(1, 1, getWidth() - 14, getHeight() - 14, 16, 16);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // 24px padding (3 * 8px base unit)
        card.setPreferredSize(new Dimension(720, 300));

        JPanel left = new JPanel() {
            private Timer pulseTimer;
            private float pulseIntensity = 0f;
            private boolean pulseDirection = true;
            
            {
                // Initialize pulsing glow animation
                pulseTimer = new Timer(100, e -> {
                    if (pulseDirection) {
                        pulseIntensity += 0.1f;
                        if (pulseIntensity >= 1.0f) {
                            pulseIntensity = 1.0f;
                            pulseDirection = false;
                        }
                    } else {
                        pulseIntensity -= 0.1f;
                        if (pulseIntensity <= 0.3f) {
                            pulseIntensity = 0.3f;
                            pulseDirection = true;
                        }
                    }
                    repaint();
                });
                pulseTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Animated Avatar with pulsing glow
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80; // 80px diameter as specified
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Animated outer glow with pulse effect
                int glowSize = (int)(size + 20 * pulseIntensity);
                int glowOffset = (glowSize - size) / 2;
                java.awt.RadialGradientPaint outerGlow = new java.awt.RadialGradientPaint(
                    x + size/2f, y + size/2f, glowSize/2f,
                    new float[]{0.0f, 0.7f, 1.0f},
                    new Color[]{
                        new Color(100, 180, 255, (int)(80 * pulseIntensity)),
                        new Color(60, 120, 200, (int)(40 * pulseIntensity)), 
                        new Color(60, 120, 200, 0)
                    }
                );
                g2.setPaint(outerGlow);
                g2.fillOval(x - glowOffset, y - glowOffset, glowSize, glowSize);
                
                // Blue gradient background for avatar
                GradientPaint avatarGradient = new GradientPaint(
                    x, y, new Color(60, 120, 200), // Light blue
                    x + size, y + size, new Color(30, 60, 120) // Darker blue
                );
                g2.setPaint(avatarGradient);
                g2.fill(new Ellipse2D.Double(x, y, size, size));
                
                // Animated blue glowing border
                g2.setStroke(new BasicStroke(2f + pulseIntensity));
                int borderAlpha = (int)(100 + 80 * pulseIntensity);
                g2.setColor(new Color(100, 180, 255, borderAlpha));
                g2.draw(new Ellipse2D.Double(x + 1.5, y + 1.5, size - 3, size - 3));
                
                // Initials text with subtle glow
                String initials = getInitials();
                g2.setFont(new Font("Inter", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (size - fm.stringWidth(initials)) / 2;
                int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                
                // Text glow effect
                g2.setColor(new Color(100, 180, 255, (int)(30 * pulseIntensity)));
                g2.drawString(initials, tx + 1, ty + 1);
                
                // Main text
                g2.setColor(new Color(240, 250, 255));
                g2.drawString(initials, tx, ty);
                
                g2.dispose();
            }
        };
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(120, 120)); // Smaller container for 80px avatar

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Username with blue theme styling
        String username = profile != null && profile.getUsername() != null ? profile.getUsername() : "lonelyammavan";
        JLabel nameLabel = new JLabel(username);
        nameLabel.setForeground(new Color(240, 250, 255)); // Very light blue-white
        nameLabel.setFont(new Font("Inter", Font.BOLD, 24)); // Font Size: 24px, Font Weight: 600 (Semi-Bold)

        // Skill with blue theme styling
        String skill = computeSkill(profile != null ? profile.getLevel() : 1);
        profileSkillLabel = new JLabel("Skill: " + skill);
        profileSkillLabel.setForeground(new Color(180, 220, 255)); // Light blue
        profileSkillLabel.setFont(new Font("Inter", Font.PLAIN, 14)); // Font Size: 14px, Font Weight: 400 (Regular)

        // Level with blue theme styling
        int level = profile != null ? profile.getLevel() : 1;
        JLabel levelLabel = new JLabel("Level: " + level);
        levelLabel.setForeground(new Color(180, 220, 255)); // Light blue
        levelLabel.setFont(new Font("Inter", Font.PLAIN, 14)); // Font Size: 14px, Font Weight: 400 (Regular)

        // XP Progress Bar with Modern Tech Dark Mode styling
        int currentXP = 0; // Start at 0 XP
        int nextLevelXP = level * 100;
        
        JLabel xpLabel = new JLabel("XP: " + currentXP + " / " + nextLevelXP);
        xpLabel.setForeground(new Color(160, 190, 230)); // Medium blue-grey
        xpLabel.setFont(new Font("Inter", Font.PLAIN, 12)); // Subtle Text: 12px, 400 weight
        
        JProgressBar xpBar = new JProgressBar(0, 100) {
            private float animatedValue = 0f;
            private Timer animationTimer;
            
            {
                // Initialize animation timer
                animationTimer = new Timer(50, e -> {
                    float targetValue = getValue();
                    if (animatedValue < targetValue) {
                        animatedValue = Math.min(targetValue, animatedValue + 2f);
                        repaint();
                    } else if (animatedValue > targetValue) {
                        animatedValue = Math.max(targetValue, animatedValue - 2f);
                        repaint();
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
                
                // Track background (dark blue)
                g2.setColor(new Color(30, 50, 80)); // Dark blue track
                g2.fillRoundRect(0, 0, w, h, h, h); // Pill shape (border-radius: 999px)
                
                // Animated progress fill with blue gradient
                int fill = (int) Math.round((animatedValue / getMaximum()) * w);
                if (fill > 0) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(60, 120, 200), // Light blue
                        w, 0, new Color(100, 180, 255)  // Brighter blue
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, fill, h, h, h); // Pill shape
                    
                    // Add shimmer effect
                    if (animatedValue > 0) {
                        float shimmerX = (System.currentTimeMillis() % 2000) / 2000f * w;
                        GradientPaint shimmer = new GradientPaint(
                            shimmerX - 10, 0, new Color(255, 255, 255, 0),
                            shimmerX + 10, 0, new Color(255, 255, 255, 80)
                        );
                        g2.setPaint(shimmer);
                        g2.fillRoundRect(0, 0, fill, h, h, h);
                    }
                }
                
                g2.dispose();
            }
        };
        xpBar.setValue(0); // Start at 0%
        xpBar.setStringPainted(false);
        xpBar.setBorderPainted(false);
        xpBar.setOpaque(false);
        xpBar.setPreferredSize(new Dimension(200, 10)); // 10px height as specified

        center.add(nameLabel);
        center.add(Box.createRigidArea(new Dimension(0, 16))); // 16px spacing (2 * 8px base unit)
        center.add(profileSkillLabel);
        center.add(Box.createRigidArea(new Dimension(0, 8))); // 8px spacing (1 * base unit)
        center.add(levelLabel);
        center.add(Box.createRigidArea(new Dimension(0, 16))); // 16px spacing (2 * 8px base unit)
        center.add(xpLabel);
        center.add(Box.createRigidArea(new Dimension(0, 8))); // 8px spacing (1 * base unit)
        center.add(xpBar);

        // Onboarding selections (hidden until provided)
        profileGoalLabel = new JLabel("Goal: ");
        profileGoalLabel.setForeground(new Color(200, 200, 220));
        profileGoalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        profileGoalLabel.setVisible(false);

        profileLanguageLabel = new JLabel("Language: ");
        profileLanguageLabel.setForeground(new Color(200, 200, 220));
        profileLanguageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        profileLanguageLabel.setVisible(false);

        profilePracticeLabel = new JLabel("Practice: ");
        profilePracticeLabel.setForeground(new Color(200, 200, 220));
        profilePracticeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        profilePracticeLabel.setVisible(false);

        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(profileGoalLabel);
        center.add(profileLanguageLabel);
        center.add(profilePracticeLabel);

        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        problemsLabel = new JLabel();
        problemsLabel.setForeground(Color.WHITE);
        problemsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateProblemsLabel();

        problemsProgress = new JProgressBar(0, problemsGoal);
        problemsProgress.setValue(problemsSolved);
        problemsProgress.setForeground(new Color(255, 215, 0)); // Gold/Yellow
        problemsProgress.setBackground(new Color(19, 38, 77)); // Slightly lighter navy
        problemsProgress.setPreferredSize(new Dimension(380, 20));
        problemsProgress.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        problemsProgress.setStringPainted(true);
        problemsProgress.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = progressBar.getWidth();
                int h = progressBar.getHeight();
                // track
                g2.setColor(new Color(235, 240, 250));
                g2.fillRoundRect(0, 0, w, h, h, h);
                // fill
                int fill = (int) Math.round(((double) progressBar.getValue() / progressBar.getMaximum()) * w);
                g2.setPaint(new GradientPaint(0, 0, new Color(46, 196, 182), w, 0, new Color(56, 120, 220)));
                g2.fillRoundRect(0, 0, fill, h, h, h);
                // text
                String s = progressBar.getString();
                if (s != null) {
                    g2.setColor(new Color(40, 50, 70));
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (w - fm.stringWidth(s)) / 2;
                    int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(s, tx, ty);
                }
                g2.dispose();
            }
        });

        streakLabel = new JLabel();
        streakLabel.setForeground(Color.WHITE);
        streakLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateStreakLabel();

        JLabel badgesTitle = new JLabel("Badges / Achievements");
        badgesTitle.setForeground(Color.WHITE);
        badgesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badgesPanel = new JPanel();
        badgesPanel.setOpaque(true);
        badgesPanel.setBackground(new Color(19, 38, 77)); // Slightly lighter navy #13264D
        badgesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        refreshBadges();

        progressPanel.add(problemsLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        progressPanel.add(problemsProgress);
        // Navigate to Progress view when clicking the bar
        problemsProgress.setCursor(new Cursor(Cursor.HAND_CURSOR));
        problemsProgress.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainContentLayout.show(mainContent, VIEW_PROGRESS);
                setActiveNav(findSidebarLabel("Progress"));
            }
        });
        progressPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        progressPanel.add(streakLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        progressPanel.add(badgesTitle);
        progressPanel.add(badgesPanel);

        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(progressPanel);

        card.add(left, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);

        // Main content area uses its own CardLayout (prevents overlapping)
        mainContentLayout = new CardLayout();
        mainContent = new JPanel(mainContentLayout);
        mainContent.setOpaque(false);

        JPanel homeView = new JPanel(new BorderLayout(16, 16));
        homeView.setOpaque(false);

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

        JPanel accountView = buildPlaceholderView("Account settings coming soon");
        JPanel progressView = buildProgressView();
        JPanel settingsView = buildPlaceholderView("Configure your preferences");

        mainContent.add(homeView, VIEW_HOME);
        mainContent.add(accountView, VIEW_ACCOUNT);
        mainContent.add(progressView, VIEW_PROGRESS);
        mainContent.add(settingsView, VIEW_SETTINGS);

        content.add(mainContent);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createNavLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(160, 180, 220)); // Light blue-grey (inactive)
        label.setFont(new Font("Inter", Font.PLAIN, 14)); // Inter font, medium weight (using PLAIN as closest)
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16)); // 12px vertical, 16px horizontal padding
        // add modern outline icon
        label.setIcon(new SidebarIcon(text));
        label.setIconTextGap(12); // 12px gap between icon and text
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                setActiveNav((JLabel) e.getSource());
                String t = ((JLabel) e.getSource()).getText();
                switch (t) {
                    case "Dashboard" -> mainContentLayout.show(mainContent, VIEW_HOME);
                    case "Account" -> mainContentLayout.show(mainContent, VIEW_ACCOUNT);
                    case "Progress" -> mainContentLayout.show(mainContent, VIEW_PROGRESS);
                    case "Settings" -> mainContentLayout.show(mainContent, VIEW_SETTINGS);
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                JLabel l = (JLabel) e.getSource();
                if (l.getClientProperty("active") != Boolean.TRUE) {
                    l.setForeground(new Color(100, 180, 255)); // Bright blue hover
                l.setOpaque(true);
                    l.setBackground(new Color(60, 120, 200, 30)); // Blue hover background
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                JLabel l = (JLabel) e.getSource();
                if (l.getClientProperty("active") != Boolean.TRUE) {
                    l.setForeground(new Color(160, 180, 220)); // Light blue-grey
                    l.setOpaque(false);
                    l.setBackground(new Color(0, 0, 0, 0));
                }
            }
        });
        return label;
    }

    private JLabel findSidebarLabel(String text) {
        // Sidebar is the WEST component; search for the label by text
        Container content = getContentPane();
        if (content.getComponentCount() == 0) return null;
        Component west = ((JPanel) content.getComponent(1)).getComponent(0); // fragile, but ok for this app
        if (west instanceof JPanel sidebar) {
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JPanel nav) {
                    for (Component cc : nav.getComponents()) {
                        if (cc instanceof JLabel l && text.equals(l.getText())) return l;
                    }
                }
            }
        }
        return null;
    }

    private void setActiveNav(JLabel active) {
        java.awt.Component parent = active.getParent();
        for (java.awt.Component c : ((JPanel) parent).getComponents()) {
            if (c instanceof JLabel l) {
                l.setForeground(new Color(160, 180, 220)); // Light blue-grey
                l.setOpaque(false);
                l.setBackground(new Color(0, 0, 0, 0));
                l.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                l.putClientProperty("active", Boolean.FALSE);
            }
        }
        active.setForeground(new Color(255, 255, 255)); // White for active
        active.setOpaque(true);
        active.setBackground(new Color(60, 120, 200, 50)); // Blue with transparency
        active.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        active.putClientProperty("active", Boolean.TRUE);
        // In a larger app, we would switch content cards here
    }

    // Modern outline icons for sidebar - Feather/Lucide style
    private static class SidebarIcon implements Icon {
        private final String name;
        private final int size = 20; // 20px as specified
        SidebarIcon(String name) { this.name = name; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Icons inherit the label's color (Medium Grey or Off-White when active)
            Color iconColor = c.getForeground();
            g2.setColor(iconColor);
            
            switch (name) {
                case "Dashboard" -> {
                    // Modern grid outline icon (3x3 grid)
                    int gridSize = size - 4;
                    int cellSize = gridSize / 3;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            int rectX = x + 2 + j * cellSize;
                            int rectY = y + 2 + i * cellSize;
                            g2.drawRoundRect(rectX, rectY, cellSize - 2, cellSize - 2, 2, 2);
                        }
                    }
                }
                case "Account" -> {
                    // User outline icon
                    int centerX = x + size/2;
                    int centerY = y + size/2;
                    // Head circle
                    g2.drawOval(centerX - 3, y + 3, 6, 6);
                    // Body outline
                    g2.drawArc(centerX - 6, centerY + 1, 12, 10, 0, 180);
                }
                case "Progress" -> {
                    // Trending up outline icon
                    int[] pointsX = {x + 3, x + 7, x + 11, x + size - 3};
                    int[] pointsY = {y + size - 3, y + 8, y + 6, y + 3};
                    for (int i = 0; i < pointsX.length - 1; i++) {
                        g2.drawLine(pointsX[i], pointsY[i], pointsX[i + 1], pointsY[i + 1]);
                    }
                    // Arrow head
                    g2.drawLine(pointsX[pointsX.length - 1] - 3, pointsY[pointsY.length - 1] + 2, 
                               pointsX[pointsX.length - 1], pointsY[pointsY.length - 1]);
                    g2.drawLine(pointsX[pointsX.length - 1], pointsY[pointsY.length - 1], 
                               pointsX[pointsX.length - 1] - 2, pointsY[pointsY.length - 1] + 3);
                }
                case "Settings" -> {
                    // Settings/gear outline icon
                    int centerX = x + size/2;
                    int centerY = y + size/2;
                    // Center circle
                    g2.drawOval(centerX - 3, centerY - 3, 6, 6);
                    // Gear teeth as lines
                    for (int i = 0; i < 8; i++) {
                        double angle = i * Math.PI / 4;
                        int x1 = (int)(centerX + Math.cos(angle) * 6);
                        int y1 = (int)(centerY + Math.sin(angle) * 6);
                        int x2 = (int)(centerX + Math.cos(angle) * 8);
                        int y2 = (int)(centerY + Math.sin(angle) * 8);
                        g2.drawLine(x1, y1, x2, y2);
                    }
                }
            }
            g2.dispose();
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    private String computeSkill(int level) {
        if (level <= 5) return "Beginner";
        if (level <= 10) return "Intermediate";
        if (level <= 20) return "Skilled";
        return "Expert";
    }

    private String getInitials() {
        String name = profile != null ? profile.getUsername() : null;
        if (name == null || name.isBlank()) return "FG";
        String[] parts = name.trim().split("[\\s_]+");
        if (parts.length == 1) {
            String p = parts[0];
            return p.length() >= 2 ? ("" + Character.toUpperCase(p.charAt(0)) + Character.toUpperCase(p.charAt(1))) : ("" + Character.toUpperCase(p.charAt(0)));
        }
        return ("" + Character.toUpperCase(parts[0].charAt(0)) + Character.toUpperCase(parts[1].charAt(0)));
    }

    private JPanel buildStatCard(String title, String value) {
        JPanel p = new JPanel() {
            private boolean isHovered = false;
            private float glowIntensity = 0.0f;
            private Timer glowTimer;
            
            {
                // Initialize glow animation timer
                glowTimer = new Timer(50, e -> {
                    if (isHovered && glowIntensity < 1.0f) {
                        glowIntensity = Math.min(1.0f, glowIntensity + 0.1f);
                        repaint();
                    } else if (!isHovered && glowIntensity > 0.0f) {
                        glowIntensity = Math.max(0.0f, glowIntensity - 0.1f);
                        repaint();
                    }
                });
                glowTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Check hover state from client property
                isHovered = getClientProperty("isHovered") == Boolean.TRUE;
                Float scaleValue = (Float) getClientProperty("scale");
                float scale = scaleValue != null ? scaleValue : 1.0f;
                
                // Apply scaling transform
                if (scale != 1.0f) {
                    g2.scale(scale, scale);
                }
                
                // Dynamic shadow based on hover state
                int shadowOffset = isHovered ? 12 : 8;
                int shadowAlpha = (int)(35 + glowIntensity * 15);
                g2.setColor(new Color(0, 0, 0, shadowAlpha));
                g2.fillRoundRect(4, shadowOffset, (int)(getWidth() / scale) - 8, (int)(getHeight() / scale) - shadowOffset, 16, 16);
                
                // Dark blue gradient background with hover enhancement
                Color color1 = new Color(25 + (int)(glowIntensity * 10), 40 + (int)(glowIntensity * 15), 70 + (int)(glowIntensity * 20));
                Color color2 = new Color(20 + (int)(glowIntensity * 8), 30 + (int)(glowIntensity * 12), 55 + (int)(glowIntensity * 15));
                GradientPaint cardGradient = new GradientPaint(0, 0, color1, 0, (int)(getHeight() / scale), color2);
                g2.setPaint(cardGradient);
                g2.fillRoundRect(0, 0, (int)(getWidth() / scale) - 8, (int)(getHeight() / scale) - 8, 16, 16);
                
                // Animated blue accent border with glow
                g2.setStroke(new BasicStroke(1f + glowIntensity));
                int borderAlpha = (int)(60 + glowIntensity * 120);
                g2.setColor(new Color(60, 120, 200, borderAlpha));
                g2.drawRoundRect(0, 0, (int)(getWidth() / scale) - 9, (int)(getHeight() / scale) - 9, 16, 16);
                
                // Add subtle glow effect when hovered
                if (glowIntensity > 0) {
                    g2.setStroke(new BasicStroke(3f));
                    g2.setColor(new Color(100, 180, 255, (int)(glowIntensity * 50)));
                    g2.drawRoundRect(-2, -2, (int)(getWidth() / scale) - 5, (int)(getHeight() / scale) - 5, 20, 20);
                }
                
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // 24px padding (3 * 8px base unit)
        
        // Card Titles with blue theme
        JLabel t = new JLabel(title);
        t.setForeground(new Color(160, 190, 230)); // Medium blue-grey
        t.setFont(new Font("Inter", Font.PLAIN, 16)); // Font Size: 16px, Font Weight: 500 (Medium - using PLAIN as closest)
        t.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Large Metrics with blue theme
        JLabel v = new JLabel(value);
        v.setForeground(new Color(240, 250, 255)); // Very light blue-white
        v.setFont(new Font("Inter", Font.BOLD, 48)); // Font Size: 48px, Font Weight: 700 (Bold)
        v.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Special color for "Solved" card (bright green to stand out)
        if ("Solved".equals(title)) {
            v.setForeground(new Color(100, 255, 150)); // Bright green for solved
        }
        
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(180, 120)); // Larger cards for better proportions
        
        // Add mouse listeners for hover animations
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                p.putClientProperty("isHovered", true);
                // Trigger bounce animation
                Timer bounceTimer = new Timer(30, null);
                final float[] scale = {1.0f};
                bounceTimer.addActionListener(bounceEvent -> {
                    scale[0] += 0.02f;
                    if (scale[0] >= 1.05f) {
                        scale[0] = 1.05f;
                        bounceTimer.stop();
                    }
                    p.putClientProperty("scale", scale[0]);
                    p.repaint();
                });
                bounceTimer.start();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                p.putClientProperty("isHovered", false);
                // Smooth return to normal size
                Timer returnTimer = new Timer(30, null);
                final float[] scale = {1.05f};
                returnTimer.addActionListener(returnEvent -> {
                    scale[0] -= 0.01f;
                    if (scale[0] <= 1.0f) {
                        scale[0] = 1.0f;
                        returnTimer.stop();
                    }
                    p.putClientProperty("scale", scale[0]);
                    p.repaint();
                });
                returnTimer.start();
            }
        });
        
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return p;
    }

    private JPanel buildBarChartPlaceholder() {
        return buildChartCard("Activity by Week", true);
    }

    private JPanel buildLineChartPlaceholder() {
        return buildChartCard("Progress Over Time", false);
    }

    private JPanel buildChartCard(String title, boolean bars) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 12, 16, 16);
                g2.setColor(new Color(19, 38, 77)); // Lighter navy #13264D
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 16, 16);
                g2.setColor(new Color(220, 225, 235));
                int margin = 36;
                int w = getWidth() - 12 - margin * 2;
                int h = getHeight() - 12 - margin * 2;
                int x = margin;
                int y = margin + h;
                g2.drawLine(x, y, x + w, y);
                g2.drawLine(x, y, x, y - h);
                if (bars) {
                    int columns = 6;
                    int gap = 12;
                    int barW = Math.max(12, (w - (columns - 1) * gap) / columns);
                    int[] vals = {40, 70, 55, 90, 65, 80};
                    for (int i = 0; i < columns; i++) {
                        int bx = x + i * (barW + gap);
                        int bh = (int) (h * (vals[i] / 100.0));
                        g2.setColor(new Color(56, 120, 220, 180));
                        g2.fillRoundRect(bx, y - bh, barW, bh, 6, 6);
                    }
                } else {
                    g2.setColor(new Color(46, 196, 182));
                    int points = 7;
                    int step = w / (points - 1);
                    int[] vals = {20, 30, 25, 45, 40, 60, 55};
                    int px = x;
                    int py = y - (int) (h * (vals[0] / 100.0));
                    for (int i = 1; i < points; i++) {
                        int cx = x + i * step;
                        int cy = y - (int) (h * (vals[i] / 100.0));
                        g2.drawLine(px, py, cx, cy);
                        px = cx; py = cy;
                    }
                    for (int i = 0; i < points; i++) {
                        int cx = x + i * step;
                        int cy = y - (int) (h * (vals[i] / 100.0));
                        g2.fillOval(cx - 3, cy - 3, 6, 6);
                    }
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    private void updateProblemsLabel() {
        if (problemsLabel != null) {
            problemsLabel.setText("Problems solved : " + problemsSolved + "/" + problemsGoal);
        }
    }

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
}


