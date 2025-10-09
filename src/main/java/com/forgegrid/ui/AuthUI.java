package com.forgegrid.ui;

import com.forgegrid.auth.AuthService;
import com.forgegrid.config.UserPreferences;
import com.forgegrid.model.PlayerProfile;
import com.forgegrid.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AuthUI extends JFrame {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(0xffcc4d);
    private static final Color SECONDARY_COLOR = new Color(0x3a6ea5);
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton backButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private final Map<String, FadeInPanel> cardFades = new HashMap<>();
    private AuthService authService;
    private UserService userService;
    private UserPreferences userPreferences;
    private LoadingScreen loadingScreen;
    private PlayerProfile currentProfile;
    
    public AuthUI() {
        this.authService = new AuthService();
        this.userService = new UserService();
        this.userPreferences = new UserPreferences();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("ForgeGrid");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    AuthUI.this,
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
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = Math.max(650, Math.min(screenSize.width * 3 / 4, 1200));
        int frameHeight = Math.max(750, Math.min(screenSize.height * 3 / 4, 1000));
        
        setSize(frameWidth, frameHeight);
        setMinimumSize(new Dimension(650, 750));
        setLocationRelativeTo(null);
        setResizable(true);
        
        try {
            java.net.URL iconUrl = getClass().getResource("/com/forgegrid/icon/logo2_transparent.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image iconImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                setIconImage(iconImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading window icon: " + e.getMessage());
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        loadingScreen = new LoadingScreen();
        addWithFade(loadingScreen, "LOADING");
        
        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();
        
        addWithFade(loginPanel, "LOGIN");
        addWithFade(signupPanel, "SIGNUP");

        WelcomeUI welcomePanel = new WelcomeUI();
        welcomePanel.addStartActionListener(e -> showCard("LOGIN"));
        addWithFade(welcomePanel, "WELCOME");

        OnboardingInAppPanel onboarding = new OnboardingInAppPanel((goal, language, skill) -> {
            System.out.println("=== ONBOARDING COMPLETION CALLBACK ===");
            System.out.println("Current Profile: " + (currentProfile != null ? currentProfile.getUsername() : "NULL"));
            System.out.println("Goal: " + goal);
            System.out.println("Language: " + language);
            System.out.println("Skill: " + skill);
            
            if (currentProfile != null && currentProfile.getUsername() != null) {
                boolean saved = userService.saveOnboardingDataByUsername(
                    currentProfile.getUsername(), 
                    goal, 
                    language, 
                    skill
                );
                
                if (saved) {
                    System.out.println("✓ Onboarding data saved successfully to database!");
                    currentProfile.setOnboardingCompleted(true);
                    currentProfile.setOnboardingGoal(goal);
                    currentProfile.setOnboardingLanguage(language);
                    currentProfile.setOnboardingSkill(skill);
                } else {
                    System.err.println("✗ Failed to save onboarding data to database");
                }
            } else {
                System.err.println("✗ Cannot save onboarding: currentProfile is null or has no username");
            }
            
            openDashboardInCard(goal, language, skill);
        });
        addWithFade(onboarding, "ONBOARDING");

        JPanel onboardingPrompt = createOnboardingPromptPanel();
        addWithFade(onboardingPrompt, "ONBOARDING_PROMPT");
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(new Color(25, 35, 55));
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        backButton = createBackArrowButton();
        header.add(backButton, BorderLayout.WEST);

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(header, BorderLayout.NORTH);
        root.add(cardPanel, BorderLayout.CENTER);
        add(root);
        
        cardLayout.show(cardPanel, "WELCOME");
        playFade("WELCOME");
        updateBackButtonVisibility("WELCOME");
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                refreshComponentSizes();
            }
        });
        
        applyCustomStyling();
    }

    private void addWithFade(JComponent comp, String name) {
        FadeInPanel wrapper = new FadeInPanel(new BorderLayout());
        wrapper.add(comp, BorderLayout.CENTER);
        cardPanel.add(wrapper, name);
        cardFades.put(name, wrapper);
    }

    private void playFade(String name) {
        FadeInPanel f = cardFades.get(name);
        if (f != null) f.play();
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
        if (!"LOGIN".equals(name)) {
            playFade(name);
        }
        updateBackButtonVisibility(name);
    }

    private void openDashboardInCard(String goal, String language, String skill) {
        boolean skipWelcome = currentProfile != null && currentProfile.isOnboardingCompleted();
        
        JPanel dashboardHost = new JPanel(new BorderLayout());
        dashboardHost.setOpaque(false);
        Dashboard dashFrame = new Dashboard(currentProfile, skipWelcome);
        dashFrame.applyOnboardingSelections(goal, language, skill, null);
        Component content = dashFrame.getContentPane();
        dashFrame.setVisible(false);
        dashFrame.dispose();
        dashboardHost.add(content);
        addWithFade(dashboardHost, "DASHBOARD");
        showCard("DASHBOARD");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new NeonBackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        double scale = calculateProportionalScale();
        int padding = (int) (80 * scale);
        padding = Math.max(30, Math.min(120, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(Math.max(10, padding - 80), padding, padding, padding));
        
        JLabel mainTagline = new JLabel("ForgeGrid – Your coding journey starts here.");
        mainTagline.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        mainTagline.setForeground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 180));
        mainTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel logoPanel = createLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        JLabel welcomePart = new JLabel("Welcome to ");
        welcomePart.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomePart.setForeground(new Color(240, 240, 240));
        
        GradientTextLabel brandPart = new GradientTextLabel("ForgeGrid");
        brandPart.setFont(new Font("Segoe UI", Font.BOLD, 34));
        brandPart.setGradient(PRIMARY_COLOR, SECONDARY_COLOR);
        
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleContainer.setOpaque(false);
        titleContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        titleContainer.add(welcomePart);
        titleContainer.add(brandPart);
        
        titleRow.add(titleContainer);
        
        emailField = createModernTextField("Username or Email");
        passwordField = createModernPasswordField("Password");
        
        addKeyboardNavigation(emailField, passwordField, null, this::handleLogin);
        addKeyboardNavigation(passwordField, null, loginButton, this::handleLogin);
        
        loginButton = createPrimaryButton("Login");
        JButton switchToSignupButton = createSolidButton("New User? Sign Up", SECONDARY_COLOR, Color.WHITE);
        switchToSignupButton.setRolloverEnabled(false);
        for (java.awt.event.MouseListener ml : switchToSignupButton.getMouseListeners()) {
            switchToSignupButton.removeMouseListener(ml);
        }
        
        addKeyboardNavigation(loginButton, null, null, this::handleLogin);
        
        loginButton.addActionListener(e -> handleLogin());
        switchToSignupButton.addActionListener(e -> showSignup());
        
        CardContainerPanel card = new CardContainerPanel();
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        int cardMaxW = Math.max(600, (int)(600 * calculateProportionalScale()));
        card.setMaximumSize(new Dimension(cardMaxW, Integer.MAX_VALUE));

        card.add(logoPanel);
        card.add(titleRow);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(mainTagline);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel emailContainer = createEmailContainer(emailField);
        card.add(emailContainer);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(passwordField);

        JLabel forgotPasswordLink = createForgotPasswordLink();
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(forgotPasswordLink);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(switchToSignupButton);

        panel.add(card);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new NeonBackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        double scale = calculateProportionalScale();
        int padding = (int) (60 * scale);
        padding = Math.max(25, Math.min(100, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        JLabel titleLabel = new JLabel("Join ForgeGrid");
        titleLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join the coding adventure");
        subtitleLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(SECONDARY_COLOR.getRed(), SECONDARY_COLOR.getGreen(), SECONDARY_COLOR.getBlue(), 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameField = createModernTextField("Username");
        JTextField signupEmailField = createModernTextField("Email");
        JPasswordField signupPasswordField = createModernPasswordField("Password");
        
        addKeyboardNavigation(nameField, signupEmailField, null, null);
        addKeyboardNavigation(signupEmailField, signupPasswordField, null, null);
        addKeyboardNavigation(signupPasswordField, null, null, () -> handleSignup(nameField, signupEmailField, signupPasswordField));
        
        signupButton = createSecondaryButton("Sign Up");
        JButton switchToLoginButton = createGlassButton("Already have an account? Login");
        
        addKeyboardNavigation(signupButton, null, null, () -> handleSignup(nameField, signupEmailField, signupPasswordField));
        
        signupButton.addActionListener(e -> handleSignup(nameField, signupEmailField, signupPasswordField));
        switchToLoginButton.addActionListener(e -> showLogin());
        
        CardContainerPanel signupCard = new CardContainerPanel();
        signupCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        int signupCardMaxW = Math.max(520, (int)(520 * calculateProportionalScale()));
        signupCard.setMaximumSize(new Dimension(signupCardMaxW, Integer.MAX_VALUE));

        signupCard.add(titleLabel);
        signupCard.add(Box.createRigidArea(new Dimension(0, 8)));
        signupCard.add(subtitleLabel);
        signupCard.add(Box.createRigidArea(new Dimension(0, 35)));
        signupCard.add(nameField);
        signupCard.add(Box.createRigidArea(new Dimension(0, 18)));
        signupCard.add(signupEmailField);
        signupCard.add(Box.createRigidArea(new Dimension(0, 18)));
        signupCard.add(signupPasswordField);
        signupCard.add(Box.createRigidArea(new Dimension(0, 30)));
        signupCard.add(signupButton);
        signupCard.add(Box.createRigidArea(new Dimension(0, 15)));
        signupCard.add(switchToLoginButton);

        panel.add(signupCard);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            private boolean isFocused = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isFocused) {
                    g2d.setColor(new Color(255, 215, 0, 30));
                    g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 19, 19);
                }
                
                g2d.setColor(new Color(25, 35, 55));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRoundRect(1, 1, getWidth() - 2, 3, 15, 15);
                
                g2d.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public void setFocusable(boolean focusable) {
                super.setFocusable(focusable);
                addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        isFocused = true;
                        repaint();
                    }
                    
                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        isFocused = false;
                        repaint();
                    }
                });
            }
        };
        
        configureField(field, placeholder);
        addPlaceholderBehavior(field, placeholder);
        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            private boolean isFocused = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isFocused) {
                    g2d.setColor(new Color(255, 215, 0, 30));
                    g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 19, 19);
                }
                
                g2d.setColor(new Color(25, 35, 55));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRoundRect(1, 1, getWidth() - 2, 3, 15, 15);
                
                drawEyeIcon(g2d);
                
                Boolean hoveringEye = (Boolean) getClientProperty("isHoveringEye");
                if (Boolean.TRUE.equals(hoveringEye)) {
                    int eyeX = getWidth() - 40;
                    int eyeY = (getHeight() - 16) / 2;
                    g2d.setColor(new Color(255, 215, 0, 20));
                    g2d.fillOval(eyeX - 5, eyeY - 5, 30, 26);
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
            
            private void drawEyeIcon(Graphics2D g2d) {
                int eyeX = getWidth() - 40;
                int eyeY = (getHeight() - 16) / 2;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Object scaleObj = getClientProperty("eyeScale");
                float eyeScale = scaleObj instanceof Float ? (Float) scaleObj : 1.0f;

                int iconW = 20, iconH = 16;
                int cx = eyeX + iconW / 2, cy = eyeY + iconH / 2;

                java.awt.geom.AffineTransform oldTx = g2d.getTransform();
                g2d.translate(cx, cy);
                g2d.scale(eyeScale, eyeScale);
                g2d.translate(-cx, -cy);

                Boolean showPasswordProperty = (Boolean) getClientProperty("showPassword");
                boolean showPassword = showPasswordProperty != null && showPasswordProperty;

                if (showPassword) {
                    g2d.setColor(new Color(255, 255, 255, 200));
                    g2d.fillOval(eyeX, eyeY, iconW, iconH);
                    g2d.setColor(new Color(50, 50, 50));
                    g2d.fillOval(eyeX + 6, eyeY + 4, 8, 8);
                    g2d.setColor(new Color(255, 255, 255, 150));
                    g2d.fillOval(eyeX + 7, eyeY + 5, 3, 3);
                    g2d.setColor(new Color(200, 200, 200, 100));
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawArc(eyeX + 1, eyeY + 1, iconW - 2, iconH - 2, 0, 180);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 60));
                    g2d.setStroke(new BasicStroke(1.2f));
                    g2d.drawArc(eyeX + 1, eyeY + 3, iconW - 2, iconH - 6, 200, 140);
                    g2d.setColor(new Color(90, 90, 95));
                    g2d.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawArc(eyeX + 1, eyeY + 1, iconW - 2, iconH - 4, 0, 180);
                    g2d.setColor(new Color(70, 70, 75));
                    g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    for (int i = 0; i < 9; i++) {
                        float t = (float) i / 8;
                        float px = eyeX + 3 + t * (iconW - 6);
                        float curve = (float) Math.sin(t * Math.PI) * 3.5f;
                        float py = eyeY + 4 + (3.5f - curve);
                        float angleDeg = -35f - t * 110f;
                        double ang = Math.toRadians(angleDeg);
                        float len = 5.0f + (float) Math.sin(t * Math.PI) * 2.0f;
                        float dx = (float) (Math.cos(ang) * len);
                        float dy = (float) (Math.sin(ang) * len);
                        g2d.drawLine(Math.round(px), Math.round(py), Math.round(px + dx), Math.round(py + dy));
                    }

                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.setStroke(new BasicStroke(1f));
                    g2d.drawArc(eyeX + 2, eyeY + 2, iconW - 4, iconH - 6, 10, 160);
                }
                g2d.setTransform(oldTx);
            }
            
            @Override
            public void setFocusable(boolean focusable) {
                super.setFocusable(focusable);
                addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        isFocused = true;
                        repaint();
                    }
                    
                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        isFocused = false;
                        repaint();
                    }
                });
            }
        };
        
        configureField(field, placeholder);
        field.setEchoChar((char) 0);
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        
        field.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int eyeX = field.getWidth() - 40;
                int eyeY = (field.getHeight() - 16) / 2;
                if (e.getX() >= eyeX - 5 && e.getX() <= eyeX + 25 && 
                    e.getY() >= eyeY - 5 && e.getY() <= eyeY + 21) {
                    
                    addEyeIconAnimation(field);
                    
                    Boolean showPasswordProperty = (Boolean) field.getClientProperty("showPassword");
                    boolean currentShowPassword = showPasswordProperty != null && showPasswordProperty;
                    field.putClientProperty("showPassword", !currentShowPassword);
                    field.setEchoChar(!currentShowPassword ? (char) 0 : '•');
                    field.repaint();
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                field.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
            }
        });
        
        addPasswordPlaceholderBehavior(field, placeholder);
        return field;
    }
    
    private JButton createGlassButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Color bg = getModel().isRollover() ? new Color(255, 255, 255, 12) : new Color(255, 255, 255, 5);
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(getModel().isRollover() ? new Color(255, 255, 255, 70) : new Color(255, 255, 255, 40));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                
                g2d.setColor(getModel().isRollover() ? new Color(255, 255, 255, 20) : new Color(255, 255, 255, 10));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);
                
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setMaximumSize(new Dimension(520, 70));
        button.setPreferredSize(new Dimension(520, 70));
        button.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
        button.setForeground(new Color(200, 200, 220));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setRolloverEnabled(true);
        
        return button;
    }

    private JButton createSolidButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base;
                boolean isWhiteBtn = Boolean.TRUE.equals(getClientProperty("isWhiteButton"));
                if (isWhiteBtn) {
                    base = getModel().isRollover() ? new Color(242, 242, 242) : Color.WHITE;
                } else {
                    base = backgroundColor;
                    if (getModel().isRollover()) {
                        base = new Color(
                            Math.min(255, backgroundColor.getRed() + 25),
                            Math.min(255, backgroundColor.getGreen() + 25),
                            Math.min(255, backgroundColor.getBlue() + 25)
                        );
                    }
                }
                g2d.setColor(base);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                Color textColor = getForeground();
                Object forceWhite = getClientProperty("forceWhiteText");
                if (Boolean.TRUE.equals(forceWhite)) {
                    textColor = Color.WHITE;
                }
                g2d.setColor(textColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int centerX = getWidth() / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                Object iconObj = getClientProperty("leftIcon");
                int gap = 8;
                Object gapObj = getClientProperty("leftIconGap");
                if (gapObj instanceof Integer) gap = (Integer) gapObj;
                int leftIconW = 0;
                if (iconObj instanceof Icon) {
                    Icon icon = (Icon) iconObj;
                    leftIconW = icon.getIconWidth() + gap;
                    int iconX = centerX - (textW + leftIconW) / 2;
                    int iconY = (getHeight() - icon.getIconHeight()) / 2;
                    
                    if (!isWhiteBtn) {
                        int d = Math.max(icon.getIconWidth(), icon.getIconHeight()) + 6;
                        int cx = iconX + icon.getIconWidth() / 2;
                        int cy = iconY + icon.getIconHeight() / 2;
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(cx - d / 2, cy - d / 2, d, d);
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.setStroke(new BasicStroke(1f));
                        g2d.drawOval(cx - d / 2, cy - d / 2, d, d);
                    }
                    icon.paintIcon(this, g2d, iconX, iconY);
                }
                int textX = centerX - (textW - (leftIconW > 0 ? -leftIconW : 0)) / 2;
                if (leftIconW > 0) {
                    textX = centerX - (textW + leftIconW) / 2 + leftIconW;
                }
                g2d.drawString(getText(), textX, y);

                g2d.dispose();
            }
        };

        button.setMaximumSize(new Dimension(520, 70));
        button.setPreferredSize(new Dimension(520, 70));
        button.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        button.setForeground(foregroundColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setRolloverEnabled(true);
        
        addButtonHoverEffect(button);
        
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        Theme.stylePrimaryButton(button);
        button.setMaximumSize(new Dimension(520, 70));
        button.setPreferredSize(new Dimension(520, 70));
        
        JComponent gradientWrap = Theme.asGradientButton(
            button,
            PRIMARY_COLOR,
            new Color(PRIMARY_COLOR.getRed() - 20, PRIMARY_COLOR.getGreen() - 20, PRIMARY_COLOR.getBlue() - 20),
            20
        );
        
        return (JButton) gradientWrap;
    }
    
    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        Theme.stylePrimaryButton(button);
        button.setMaximumSize(new Dimension(520, 70));
        button.setPreferredSize(new Dimension(520, 70));
        
        JComponent gradientWrap = Theme.asGradientButton(
            button,
            SECONDARY_COLOR,
            new Color(SECONDARY_COLOR.getRed() - 20, SECONDARY_COLOR.getGreen() - 20, SECONDARY_COLOR.getBlue() - 20),
            20
        );
        
        return (JButton) gradientWrap;
    }

    private void handleLogin() {
        String username = normalizeField(emailField, "Username or Email");
        String password = normalizePassword(passwordField, "Password");
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                PlayerProfile profile = authService.login(username, password);
                
                if (profile != null) {
                    this.currentProfile = profile;
                    userPreferences.setLastUsername(username);
                    
                    System.out.println("=== LOGIN SUCCESS ===");
                    System.out.println("Username: " + profile.getUsername());
                    System.out.println("Onboarding Completed: " + profile.isOnboardingCompleted());
                    
                    showCard("LOADING");
                    new javax.swing.Timer(3500, e2 -> {
                        ((javax.swing.Timer) e2.getSource()).stop();
                        showCard("ONBOARDING_PROMPT");
                    }).start();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    showCard("LOGIN");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Authentication error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showCard("LOGIN");
            } finally {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }
        });
    }
    
    private void handleSignup(JTextField nameFieldParam, JTextField emailField, JPasswordField passwordField) {
        String name = normalizeField(nameFieldParam, "Username");
        String email = normalizeField(emailField, "Email");
        String password = normalizePassword(passwordField, "Password");
        
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (name.isEmpty()) sb.append("Username");
            if (email.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("Email");
            }
            if (password.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("Password");
            }
            JOptionPane.showMessageDialog(this, "Please fill in all fields: " + sb.toString() + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (name.equalsIgnoreCase(email)) {
            JOptionPane.showMessageDialog(this, 
                "Username and Email cannot be the same.\nPlease use different values.", 
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.register(name, email, password);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Account created successfully!\nWelcome to ForgeGrid, " + name + "!\nPlease login with your credentials.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    showLogin();
                    resetSignupFields(nameFieldParam, emailField, passwordField);
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different email.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Registration error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
            }
        });
    }
    
    private String normalizeField(JTextField field, String placeholder) {
        Object active = field.getClientProperty("placeholderActive");
        if (Boolean.TRUE.equals(active) && placeholder.equalsIgnoreCase(field.getText().trim())) {
            field.setText("");
            field.putClientProperty("placeholderActive", Boolean.FALSE);
            field.setForeground(Color.WHITE);
        }
        return field.getText().trim();
    }
    
    private String normalizePassword(JPasswordField field, String placeholder) {
        Object active = field.getClientProperty("placeholderActive");
        String current = new String(field.getPassword());
        if (Boolean.TRUE.equals(active) && placeholder.equalsIgnoreCase(current.trim())) {
            field.setText("");
            field.putClientProperty("placeholderActive", Boolean.FALSE);
            field.setForeground(Color.WHITE);
            field.setEchoChar('•');
        }
        return new String(field.getPassword());
    }
    
    private void resetSignupFields(JTextField nameField, JTextField emailField, JPasswordField passwordField) {
        nameField.setText("Username");
        nameField.putClientProperty("placeholderActive", Boolean.TRUE);
        nameField.setForeground(new Color(200, 200, 220));
        
        emailField.setText("Email");
        emailField.putClientProperty("placeholderActive", Boolean.TRUE);
        emailField.setForeground(new Color(200, 200, 220));
        
        passwordField.setText("Password");
        passwordField.putClientProperty("placeholderActive", Boolean.TRUE);
        passwordField.setForeground(new Color(200, 200, 220));
        passwordField.setEchoChar((char) 0);
    }
    
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        
        try {
            java.net.URL logoUrl = getClass().getResource("/com/forgegrid/icon/logo2_transparent.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                
                double scale = calculateProportionalScale();
                int logoWidth = (int) (360 * scale);
                int logoHeight = (int) (220 * scale);
                
                logoWidth = Math.max(240, logoWidth);
                logoHeight = Math.max(140, logoHeight);
                
                Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledLogo);
                
                JLabel logoLabel = new JLabel(scaledIcon);
                logoLabel.setHorizontalAlignment(JLabel.CENTER);
                logoPanel.add(logoLabel, BorderLayout.CENTER);
                
                logoPanel.setPreferredSize(new Dimension(logoWidth, logoHeight));
                logoPanel.setMaximumSize(new Dimension(logoWidth, logoHeight));
            } else {
                JLabel fallbackLabel = new JLabel("ForgeGrid");
                fallbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
                fallbackLabel.setForeground(PRIMARY_COLOR);
                fallbackLabel.setHorizontalAlignment(JLabel.CENTER);
                logoPanel.add(fallbackLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel fallbackLabel = new JLabel("ForgeGrid");
            fallbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackLabel.setForeground(PRIMARY_COLOR);
            fallbackLabel.setHorizontalAlignment(JLabel.CENTER);
            logoPanel.add(fallbackLabel, BorderLayout.CENTER);
        }
        
        return logoPanel;
    }
    
    private double calculateProportionalScale() {
        double baseWidth = 800.0;
        double baseHeight = 900.0;
        double currentWidth = getWidth();
        double currentHeight = getHeight();
        
        double widthScale = Math.max(0.5, Math.min(1.5, currentWidth / baseWidth));
        double heightScale = Math.max(0.5, Math.min(1.5, currentHeight / baseHeight));
        return Math.min(widthScale, heightScale);
    }
    
    private void refreshComponentSizes() {
        Dimension fieldSize = new Dimension(520, 70);
        Dimension buttonSize = new Dimension(520, 70);
        Font fieldFont = new Font("Trebuchet MS", Font.PLAIN, 20);
        Font buttonFont = new Font("Trebuchet MS", Font.BOLD, 22);
        
        if (emailField != null) {
            emailField.setMaximumSize(fieldSize);
            emailField.setPreferredSize(fieldSize);
            emailField.setFont(fieldFont);
        }
        if (passwordField != null) {
            passwordField.setMaximumSize(fieldSize);
            passwordField.setPreferredSize(fieldSize);
            passwordField.setFont(fieldFont);
        }
        if (nameField != null) {
            nameField.setMaximumSize(fieldSize);
            nameField.setPreferredSize(fieldSize);
            nameField.setFont(fieldFont);
        }
        
        if (loginButton != null) {
            loginButton.setMaximumSize(buttonSize);
            loginButton.setPreferredSize(buttonSize);
            loginButton.setFont(buttonFont);
        }
        if (signupButton != null) {
            signupButton.setMaximumSize(buttonSize);
            signupButton.setPreferredSize(buttonSize);
            signupButton.setFont(buttonFont);
        }
        
        revalidate();
        repaint();
    }

    private JButton createBackArrowButton() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2d.setColor(Color.WHITE);

                int arrowHeight = Math.min(h - 8, 18);
                int arrowWidth = Math.min(w - 12, 22);
                int topY = (h - arrowHeight) / 2;
                int centerY = h / 2;
                int leftX = 8;
                int rightX = leftX + arrowWidth;

                int shaftHeight = Math.max(2, arrowHeight / 4);
                int shaftY = centerY - shaftHeight / 2;
                int shaftRight = rightX - (arrowHeight / 2);
                g2d.fillRoundRect(leftX + (arrowHeight / 2) - 1, shaftY, Math.max(4, shaftRight - (leftX + (arrowHeight / 2))), shaftHeight, shaftHeight, shaftHeight);

                int[] xs = new int[] { leftX, leftX + (arrowHeight / 2), leftX + (arrowHeight / 2) };
                int[] ys = new int[] { centerY, topY, topY + arrowHeight };
                g2d.fillPolygon(xs, ys, 3);
                g2d.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(44, 36));
        btn.setMinimumSize(new Dimension(44, 36));
        btn.setMaximumSize(new Dimension(44, 36));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showCard("WELCOME"));
        return btn;
    }

    private void updateBackButtonVisibility(String currentCard) {
        if (backButton == null) return;
        boolean shouldShow = !("WELCOME".equals(currentCard) || "ONBOARDING".equals(currentCard) || "ONBOARDING_PROMPT".equals(currentCard) || "DASHBOARD".equals(currentCard));
        backButton.setVisible(shouldShow);
    }
    
    private void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.getClass().getSimpleName().contains("$")) {
                    try {
                        java.lang.reflect.Method setHovered = button.getClass().getMethod("setHovered", boolean.class);
                        setHovered.invoke(button, true);
                    } catch (Exception ex) {
                        Color currentColor = button.getBackground();
                        if (currentColor != null) {
                            Color hoverColor = new Color(
                                Math.min(255, currentColor.getRed() + 30),
                                Math.min(255, currentColor.getGreen() + 30),
                                Math.min(255, currentColor.getBlue() + 30)
                            );
                            button.setBackground(hoverColor);
                        }
                    }
                }
                button.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.getClass().getSimpleName().contains("$")) {
                    try {
                        java.lang.reflect.Method setHovered = button.getClass().getMethod("setHovered", boolean.class);
                        setHovered.invoke(button, false);
                    } catch (Exception ex) {
                        Color currentColor = button.getBackground();
                        if (currentColor != null) {
                            Color originalColor = new Color(
                                Math.max(0, currentColor.getRed() - 30),
                                Math.max(0, currentColor.getGreen() - 30),
                                Math.max(0, currentColor.getBlue() - 30)
                            );
                            button.setBackground(originalColor);
                        }
                    }
                }
                button.repaint();
            }
        });
    }
    
    private void addEyeIconAnimation(JPasswordField field) {
        field.putClientProperty("eyeScale", 1.1f);
        field.repaint();
        
        Timer resetTimer = new Timer(100, e -> {
            field.putClientProperty("eyeScale", 1.0f);
            field.repaint();
            ((Timer) e.getSource()).stop();
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
    
    private void applyCustomStyling() {
        Font defaultFont = new Font("Trebuchet MS", Font.PLAIN, 18);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showPasswordResetDialog() {
        String username = JOptionPane.showInputDialog(this, "Enter your username (email):", "Reset Password", JOptionPane.QUESTION_MESSAGE);
        
        if (username != null && !username.trim().isEmpty()) {
            if (authService.usernameExists(username.trim())) {
                String newPassword = JOptionPane.showInputDialog(this, "Enter your new password:", "Reset Password", JOptionPane.QUESTION_MESSAGE);
                
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    if (newPassword.length() < 6) {
                        JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    boolean success = authService.resetPassword(username.trim(), newPassword);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Password reset successfully!\nYou can now login with your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to reset password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username not found. Please check your username and try again.", "User Not Found", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showSignup() {
        SwingUtilities.invokeLater(() -> {
            JPanel signupPanel = createSignupPanel();
            cardPanel.add(signupPanel, "SIGNUP");
            cardLayout.show(cardPanel, "SIGNUP");
            cardPanel.revalidate();
            cardPanel.repaint();
            if (nameField != null) nameField.requestFocusInWindow();
        });
    }
    
    private void showLogin() {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(cardPanel, "LOGIN");
            cardPanel.revalidate();
            cardPanel.repaint();
            if (emailField != null) emailField.requestFocusInWindow();
        });
    }

    private JPanel createOnboardingPromptPanel() {
        JPanel panel = new NeonBackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        CardContainerPanel card = new CardContainerPanel();
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        int cardMaxW = Math.max(600, (int)(600 * calculateProportionalScale()));
        card.setMaximumSize(new Dimension(cardMaxW, Integer.MAX_VALUE));

        JLabel title = new JLabel("Do onboarding now?");
        title.setFont(new Font("Trebuchet MS", Font.BOLD, 28));
        title.setForeground(new Color(240, 240, 240));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Answer a few quick questions to personalize your dashboard.");
        subtitle.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        subtitle.setForeground(new Color(200, 200, 220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton yesBtn = createSolidButton("Yes, start onboarding", PRIMARY_COLOR, Color.BLACK);
        JButton skipBtn = createGlassButton("Skip for now – go to dashboard");

        yesBtn.addActionListener(e -> showCard("ONBOARDING"));
        skipBtn.addActionListener(e -> {
            String goal = currentProfile != null ? currentProfile.getOnboardingGoal() : null;
            String language = currentProfile != null ? currentProfile.getOnboardingLanguage() : null;
            String skill = currentProfile != null ? currentProfile.getOnboardingSkill() : null;
            openDashboardInCard(goal, language, skill);
        });

        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(yesBtn);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(skipBtn);

        panel.add(card);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // Helper methods for field configuration and behavior
    private void configureField(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(520, 70));
        field.setPreferredSize(new Dimension(520, 70));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        field.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        field.setBackground(new Color(25, 35, 55));
        field.setForeground(new Color(220, 220, 240));
        field.setOpaque(false);
        field.setText(placeholder);
        field.setForeground(new Color(200, 200, 220));
        
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setCaretColor(PRIMARY_COLOR);
        }
    }
    
    private void addPlaceholderBehavior(JTextField field, String placeholder) {
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                if (Boolean.TRUE.equals(active) && field.getText().equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.WHITE);
                }
            }
        });
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                if (Boolean.TRUE.equals(active) && field.getText().equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.putClientProperty("placeholderActive", Boolean.TRUE);
                    field.setForeground(new Color(200, 200, 220));
                }
            }
        });
    }
    
    private void addPasswordPlaceholderBehavior(JPasswordField field, String placeholder) {
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                String current = new String(field.getPassword());
                if (Boolean.TRUE.equals(active) && current.equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.WHITE);
                    field.setEchoChar('•');
                }
            }
        });
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                String current = new String(field.getPassword());
                if (Boolean.TRUE.equals(active) && current.equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.WHITE);
                    field.setEchoChar('•');
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String current = new String(field.getPassword());
                if (current.trim().isEmpty()) {
                    field.setText(placeholder);
                    field.putClientProperty("placeholderActive", Boolean.TRUE);
                    field.setForeground(new Color(200, 200, 220));
                    field.setEchoChar((char) 0);
                }
            }
        });
    }
    
    private void addKeyboardNavigation(JComponent from, JComponent toNext, JComponent toButton, Runnable onEnter) {
        if (from instanceof JTextField || from instanceof JPasswordField) {
            from.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        if (toNext != null) {
                            toNext.requestFocus();
                        } else if (onEnter != null) {
                            onEnter.run();
                        }
                    } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN && toNext != null) {
                        toNext.requestFocus();
                    } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP && toButton != null) {
                        toButton.requestFocus();
                    }
                }
            });
        } else if (from instanceof JButton) {
            from.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if ((e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER || e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) && onEnter != null) {
                        onEnter.run();
                    } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP && toNext != null) {
                        toNext.requestFocus();
                    }
                }
            });
        }
    }
    
    private JPanel createEmailContainer(JTextField emailField) {
        JPanel emailContainer = new JPanel();
        emailContainer.setOpaque(false);
        emailContainer.setLayout(new BoxLayout(emailContainer, BoxLayout.Y_AXIS));
        emailContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailContainer.add(emailField);
        
        String lastUsername = userPreferences.getLastUsername();
        if (lastUsername != null && !lastUsername.isEmpty()) {
            JPanel dropdownPanel = createUsernameDropdown(lastUsername, emailField);
            emailContainer.add(Box.createRigidArea(new Dimension(0, 4)));
            emailContainer.add(dropdownPanel);
        }
        
        return emailContainer;
    }
    
    private JPanel createUsernameDropdown(String lastUsername, JTextField emailField) {
        JPanel dropdownPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(30, 40, 60));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 100));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                
                g2d.dispose();
            }
        };
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new BorderLayout());
        dropdownPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        dropdownPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dropdownPanel.setVisible(false);
        
        JLabel suggestionLabel = new JLabel(lastUsername);
        suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        suggestionLabel.setForeground(new Color(220, 220, 240));
        suggestionLabel.setIcon(new javax.swing.ImageIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 150));
                g2.fillOval(x, y + 2, 8, 8);
                g2.dispose();
            }
            @Override
            public int getIconWidth() { return 8; }
            @Override
            public int getIconHeight() { return 12; }
        });
        suggestionLabel.setIconTextGap(8);
        
        dropdownPanel.add(suggestionLabel, BorderLayout.CENTER);
        
        double dropdownScale = calculateProportionalScale();
        int fieldWidth = (int) (520 * dropdownScale);
        fieldWidth = Math.max(250, fieldWidth);
        dropdownPanel.setMaximumSize(new Dimension(fieldWidth, 40));
        dropdownPanel.setPreferredSize(new Dimension(fieldWidth, 40));
        dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        dropdownPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                emailField.setText(lastUsername);
                emailField.setForeground(Color.WHITE);
                emailField.putClientProperty("placeholderActive", Boolean.FALSE);
                dropdownPanel.setVisible(false);
            }
        });
        
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                Object placeholderActive = emailField.getClientProperty("placeholderActive");
                if (Boolean.TRUE.equals(placeholderActive) || emailField.getText().trim().isEmpty()) {
                    dropdownPanel.setVisible(true);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                Timer hideTimer = new Timer(150, evt -> {
                    if (!dropdownPanel.isAncestorOf(e.getOppositeComponent())) {
                        dropdownPanel.setVisible(false);
                    }
                });
                hideTimer.setRepeats(false);
                hideTimer.start();
            }
        });
        
        return dropdownPanel;
    }
    
    private JLabel createForgotPasswordLink() {
        JLabel forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPasswordLink.setForeground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 160));
        forgotPasswordLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showPasswordResetDialog();
            }
        });
        return forgotPasswordLink;
    }
}