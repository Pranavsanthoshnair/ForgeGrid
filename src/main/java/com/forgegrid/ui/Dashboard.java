package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Random;

public class Dashboard extends JFrame {

    private final PlayerProfile profile;
    private CardLayout cardLayout;
    private JPanel root;
    // Simple state for demo
    private int problemsSolved = 0;
    private int problemsGoal = 100;
    private int currentStreak = 0;
    private java.util.List<String> badges = new java.util.ArrayList<>();

    // UI references to update dynamically
    private JLabel problemsLabel;
    private JProgressBar problemsProgress;
    private JLabel streakLabel;
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

    private static final List<String> QUOTES = java.util.Arrays.asList(
            "Every day is a chance to get better.",
            "Small steps add up to big results.",
            "Stay focused. Keep grinding. Forge your path.",
            "Your future is created by what you do today.",
            "Great things take time. Keep going."
    );

    public Dashboard(PlayerProfile profile) {
        this.profile = profile;
        setTitle("ForgeGrid - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 480));

        initUI();
    }

    private void initUI() {
        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);
        root.setBackground(new Color(246, 248, 252)); // soft light background
        setContentPane(root);

        JPanel welcomePanel = buildWelcomePanel();
        JPanel dashboardPanel = buildDashboardPanel();

        root.add(welcomePanel, "welcome");
        root.add(dashboardPanel, "dashboard");

        // Auto-switch to dashboard after ~5 seconds
        Timer timer = new Timer(5000, e -> cardLayout.show(root, "dashboard"));
        timer.setRepeats(false);
        timer.start();
    }

    private String getRandomQuote() {
        Random r = new Random();
        return QUOTES.get(r.nextInt(QUOTES.size()));
    }

    private JPanel buildWelcomePanel() {
        NeonBackgroundPanel bg = new NeonBackgroundPanel();
        bg.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(80, 40, 60, 40));

        JLabel heading = new JLabel("Welcome to ForgeGrid");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 36));

        String username = profile != null && profile.getUsername() != null ? profile.getUsername() : "Player";
        JLabel user = new JLabel(username);
        user.setAlignmentX(Component.CENTER_ALIGNMENT);
        user.setForeground(new Color(120, 200, 255));
        user.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel quote = new JLabel(getRandomQuote());
        quote.setAlignmentX(Component.CENTER_ALIGNMENT);
        quote.setForeground(new Color(210, 220, 235));
        quote.setFont(new Font("Segoe UI", Font.ITALIC, 16));

        center.add(heading);
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(user);
        center.add(Box.createRigidArea(new Dimension(0, 18)));
        center.add(quote);
        center.add(Box.createRigidArea(new Dimension(0, 28)));

        JProgressBar loading = new JProgressBar();
        loading.setIndeterminate(false);
        loading.setMinimum(0);
        loading.setMaximum(100);
        loading.setValue(0);
        loading.setStringPainted(false);
        loading.setBackground(new Color(40, 45, 60));
        loading.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        loading.setPreferredSize(new Dimension(420, 22));
        loading.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = progressBar.getWidth();
                int h = progressBar.getHeight();
                g2.setColor(new Color(30, 35, 50));
                g2.fillRoundRect(0, 0, w, h, h, h);
                int fill = (int) Math.round(((double) progressBar.getValue() / progressBar.getMaximum()) * w);
                GradientPaint gp = new GradientPaint(0, 0, new Color(250, 210, 80), w, 0, new Color(60, 190, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, fill, h, h, h);
                g2.dispose();
            }
        });
        center.add(loading);

        // Animate loading to complete in ~5 seconds
        Timer loadAnim = new Timer(50, e -> {
            int v = loading.getValue();
            if (v >= 100) {
                ((Timer) e.getSource()).stop();
            } else {
                loading.setValue(v + 2); // 50ms * 50 steps = ~2.5s; doubled timer is 5s with 2 increment
            }
        });
        loadAnim.start();

        bg.add(center, BorderLayout.CENTER);
        return bg;
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(250, 252, 255)); // soft white

        // Left blue sidebar
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(true);
        sidebar.setBackground(new Color(28, 72, 160)); // refined blue
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel menuTitle = new JLabel("Menu");
        menuTitle.setForeground(new Color(255, 235, 170));
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

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
        content.setOpaque(true);
        content.setBackground(new Color(250, 252, 255));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Removed global "Your Dashboard" heading to avoid showing it on all views

        // Profile card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // subtle shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 12, 18, 18);
                // card body
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        card.setPreferredSize(new Dimension(720, 300));

        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Avatar circle with initials
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                // shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new Ellipse2D.Double(x + 3, y + 4, size, size));
                // avatar circle
                g2.setColor(new Color(35, 90, 180));
                g2.fill(new Ellipse2D.Double(x, y, size, size));
                // Initials
                String initials = getInitials();
                g2.setColor(new Color(255, 230, 120));
                g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(28, size / 3)));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (size - fm.stringWidth(initials)) / 2;
                int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }
        };
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(160, 160));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        String username = profile != null && profile.getUsername() != null ? profile.getUsername() : "Player";
        JLabel nameLabel = new JLabel(username);
        nameLabel.setForeground(new Color(28, 32, 40));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        String skill = computeSkill(profile != null ? profile.getLevel() : 1);
        JLabel skillLabel = new JLabel("Skill: " + skill);
        skillLabel.setForeground(new Color(90, 100, 120));
        skillLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));

        int level = profile != null ? profile.getLevel() : 1;
        JLabel levelLabel = new JLabel("Level: " + level);
        levelLabel.setForeground(new Color(90, 100, 120));
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));

        center.add(nameLabel);
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(skillLabel);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(levelLabel);

        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        problemsLabel = new JLabel();
        problemsLabel.setForeground(new Color(90, 100, 120));
        problemsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateProblemsLabel();

        problemsProgress = new JProgressBar(0, problemsGoal);
        problemsProgress.setValue(problemsSolved);
        problemsProgress.setForeground(new Color(46, 196, 182));
        problemsProgress.setBackground(new Color(235, 240, 250));
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
        streakLabel.setForeground(new Color(90, 100, 120));
        streakLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateStreakLabel();

        JLabel badgesTitle = new JLabel("Badges / Achievements");
        badgesTitle.setForeground(new Color(90, 100, 120));
        badgesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badgesPanel = new JPanel();
        badgesPanel.setOpaque(false);
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
        mainContent.setOpaque(true);
        mainContent.setBackground(Color.WHITE);

        JPanel homeView = new JPanel(new BorderLayout(16, 16));
        homeView.setOpaque(false);

        // Top metrics row
        JPanel metricsRow = new JPanel();
        metricsRow.setOpaque(false);
        metricsRow.setLayout(new GridLayout(1, 4, 12, 12));
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
        // CTA button below profile card (yellow, per request)
        JButton startBtn = new JButton("Start your journey");
        startBtn.setFocusPainted(false);
        startBtn.setBackground(new Color(255, 205, 60));
        startBtn.setForeground(Color.WHITE);
        startBtn.setOpaque(true);
        startBtn.setContentAreaFilled(true);
        startBtn.setBorderPainted(false);
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { startBtn.setBackground(new Color(255, 192, 28)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { startBtn.setBackground(new Color(255, 205, 60)); }
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
        recommendationsPanel.setBackground(Color.WHITE);
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 235, 245))));
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
        label.setForeground(new Color(225, 238, 255));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // add simple vector icon
        label.setIcon(new SidebarIcon(text));
        label.setIconTextGap(10);
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
                l.setOpaque(true);
                l.setBackground(new Color(0, 0, 0, 40)); // dark overlay hover
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                JLabel l = (JLabel) e.getSource();
                if (l.getClientProperty("active") != Boolean.TRUE) {
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
                l.setOpaque(false);
                l.setBackground(new Color(0, 0, 0, 0));
                l.putClientProperty("active", Boolean.FALSE);
            }
        }
        active.setOpaque(true);
        active.setBackground(new Color(0, 0, 0, 60));
        active.putClientProperty("active", Boolean.TRUE);
        // In a larger app, we would switch content cards here
    }

    // Simple vector icon for sidebar labels
    private static class SidebarIcon implements Icon {
        private final String name;
        private final int size = 16;
        SidebarIcon(String name) { this.name = name; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(210, 230, 255));
            Shape shape;
            switch (name) {
                case "Dashboard" -> shape = new java.awt.geom.RoundRectangle2D.Float(x, y + 2, size, size - 4, 4, 4);
                case "Account" -> shape = new java.awt.geom.Ellipse2D.Float(x, y, size, size);
                case "Progress" -> shape = new java.awt.geom.Rectangle2D.Float(x, y + 2, size, size - 4);
                case "Settings" -> {
                    shape = new java.awt.geom.Ellipse2D.Float(x + 2, y + 2, size - 4, size - 4);
                    g2.draw(shape);
                    g2.dispose();
                    return;
                }
                default -> shape = new java.awt.geom.Rectangle2D.Float(x, y, size, size);
            }
            g2.fill(shape);
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
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 14, 14);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JLabel t = new JLabel(title);
        t.setForeground(new Color(100, 110, 130));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel v = new JLabel(value);
        v.setForeground(new Color(28, 32, 40));
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
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
                g2.setColor(Color.WHITE);
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
        t.setForeground(new Color(30, 35, 45));
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

    private void refreshBadges() {
        if (badgesPanel == null) return;
        badgesPanel.removeAll();
        if (badges.isEmpty()) {
            JLabel none = new JLabel("No badges yet");
            none.setForeground(new Color(160, 170, 190));
            badgesPanel.add(none);
        } else {
            for (String b : badges) {
                JLabel badge = new JLabel(b);
                badge.setOpaque(true);
                badge.setBackground(new Color(240, 245, 255));
                badge.setForeground(new Color(40, 80, 160));
                badge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                badgesPanel.add(badge);
            }
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
        JLabel title = new JLabel("Personalized recommendations");
        title.setForeground(new Color(30, 35, 45));
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        recommendationsPanel.add(title);
        recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        if (langs == null || langs.isEmpty()) {
            // No extra sentence; keep header only
        } else {
            JLabel based = new JLabel("Based on: " + String.join(", ", langs) + (goal != null ? ", goal: " + goal : "") + (time != null ? ", time: " + time : ""));
            based.setForeground(new Color(100, 110, 130));
            recommendationsPanel.add(based);
            recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

            // Improved placeholders
            for (String rec : new String[]{"Warm-up set for this week", "Core algorithms module", "Challenge of the week"}) {
                JLabel r = new JLabel("• " + rec);
                r.setForeground(new Color(60, 70, 90));
                recommendationsPanel.add(r);
            }
        }
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
    }

    // Styled option toggle (square, blue; hover lighter; selected green)
    private JToggleButton createOptionButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(230, 240, 255));
        btn.setForeground(new Color(30, 40, 60));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(28, 72, 160)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(new Color(46, 196, 182));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(230, 240, 255));
                btn.setForeground(new Color(30, 40, 60));
            }
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(new Color(214, 232, 255));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(new Color(230, 240, 255));
            }
        });
        return btn;
    }

    // Primary action button (yellowish)
    private JButton createPrimaryActionButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(255, 224, 120));
        b.setForeground(new Color(80, 60, 10));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 214, 100));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 224, 120));
            }
        });
        return b;
    }

    // Placeholder views for non-dashboard sections
    private JPanel buildPlaceholderView(String text) {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(Color.WHITE);
        p.setLayout(new GridBagLayout());
        JLabel l = new JLabel(text);
        l.setForeground(new Color(100, 110, 130));
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
        wizardRoot.setBackground(Color.WHITE);
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


