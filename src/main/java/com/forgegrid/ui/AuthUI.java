package com.forgegrid.ui;

import com.forgegrid.auth.AuthService;
import com.forgegrid.config.UserPreferences;
import com.forgegrid.model.PlayerProfile;
import com.forgegrid.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class AuthUI extends JFrame {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(0xffcc4d); // #ffcc4d - Golden yellow
    private static final Color SECONDARY_COLOR = new Color(0x3a6ea5); // #3a6ea5 - Blue
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JButton loginButton;
    private JButton signupButton;
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
        setTitle("ForgeGrid - Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        
        // Calculate responsive frame size (60-80% of screen, with minimums)
        int frameWidth = Math.max(650, Math.min(screenWidth * 3 / 4, 1200));
        int frameHeight = Math.max(750, Math.min(screenHeight * 3 / 4, 1000));
        
        setSize(frameWidth, frameHeight);
        setMinimumSize(new Dimension(650, 750)); // Increased minimum size to prevent title cutoff
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set the window icon to the existing logo image (single source)
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
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
		// Create loading screen
		loadingScreen = new LoadingScreen();
		addWithFade(loadingScreen, "LOADING");
        
        
		// Create login and signup panels
        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();
        
        addWithFade(loginPanel, "LOGIN");
        addWithFade(signupPanel, "SIGNUP");

		// Create landing cover panel (shown first)
        // Welcome screen inside the same window (instead of separate tab/window)
        WelcomeUI welcomePanel = new WelcomeUI();
        welcomePanel.addStartActionListener(e -> showCard("LOGIN"));
        addWithFade(welcomePanel, "WELCOME");

        // In-app Onboarding panel (same window)
        OnboardingInAppPanel onboarding = new OnboardingInAppPanel((goal, language, skill) -> {
            // Save onboarding data to database
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
                    // Update the current profile with onboarding data
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
            
            // After onboarding, open Dashboard within the same window
            openDashboardInCard(goal, language, skill);
        });
        addWithFade(onboarding, "ONBOARDING");
        
        add(cardPanel);
        
        // Show welcome first with fade-in
        cardLayout.show(cardPanel, "WELCOME");
        playFade("WELCOME");
        
        // Add component listener to handle window resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                refreshComponentSizes();
            }
        });
        
        // Apply custom styling
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
        playFade(name);
    }

    private void openDashboardInCard(String goal, String language, String skill) {
        JPanel dashboardHost = new JPanel(new BorderLayout());
        dashboardHost.setOpaque(false);
        // Reuse existing Dashboard panel building by instantiating and extracting its content pane
        Dashboard dashFrame = new Dashboard(currentProfile);
        // Apply onboarding selections to dashboard UI
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
        
        // Calculate proportional padding based on frame size
        double scale = calculateProportionalScale();
        int padding = (int) (80 * scale);
        padding = Math.max(30, Math.min(120, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(Math.max(10, padding - 80), padding, padding, padding));
        
        // Modern tagline with enhanced styling
        JLabel mainTagline = new JLabel("ForgeGrid – Your coding journey starts here.");
        mainTagline.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        mainTagline.setForeground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 180)); // Lighter primary color
        mainTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create the logo panel
        JPanel logoPanel = createLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Modern title with enhanced styling
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // Increased height to avoid clipping
        
        JLabel welcomePart = new JLabel("Welcome to ");
        welcomePart.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomePart.setForeground(new Color(240, 240, 240));
        
        GradientTextLabel brandPart = new GradientTextLabel(
            "ForgeGrid",
            new Font("Segoe UI", Font.BOLD, 34),
            PRIMARY_COLOR, // primary golden yellow
            SECONDARY_COLOR  // secondary blue
        );
        
        // Create a container panel to hold both labels
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleContainer.setOpaque(false);
        titleContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        titleContainer.add(welcomePart);
        titleContainer.add(brandPart);
        
        titleRow.add(titleContainer);
        
        
        // Form fields
        emailField = createModernTextField("Email");
        passwordField = createModernPasswordField("Password");
        
        // Add keyboard navigation
        emailField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    passwordField.requestFocus();
                }
            }
        });
        
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    handleLogin();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    loginButton.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    emailField.requestFocus();
                }
            }
        });
        
        loginButton = createGradientButton("Login", PRIMARY_COLOR, new Color(PRIMARY_COLOR.getRed() - 20, PRIMARY_COLOR.getGreen() - 20, PRIMARY_COLOR.getBlue() - 20));
        JButton switchToSignupButton = createSolidButton("New User? Sign Up", SECONDARY_COLOR, Color.WHITE);
        
        // Add arrow key navigation to login button
        loginButton.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER || e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    handleLogin();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    passwordField.requestFocus();
                }
            }
        });
        
        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        switchToSignupButton.addActionListener(e -> showSignup());
        // Build card container to hold form content
        CardContainerPanel card = new CardContainerPanel();
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        int cardMaxW = Math.max(600, (int)(600 * calculateProportionalScale()));
        card.setMaximumSize(new Dimension(cardMaxW, Integer.MAX_VALUE));

        // Add components inside the card
        card.add(logoPanel);
        card.add(titleRow);
        card.add(Box.createRigidArea(new Dimension(0, 8))); // tighter spacing before tagline
        card.add(mainTagline);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Create container for email field and dropdown
        JPanel emailContainer = new JPanel();
        emailContainer.setOpaque(false);
        emailContainer.setLayout(new BoxLayout(emailContainer, BoxLayout.Y_AXIS));
        emailContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailContainer.add(emailField);
        
        // Add dropdown suggestion for last username if available
        String lastUsername = userPreferences.getLastUsername();
        if (lastUsername != null && !lastUsername.isEmpty()) {
            // Create dropdown panel
            JPanel dropdownPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Dropdown background
                    g2d.setColor(new Color(30, 40, 60));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    // Border
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
            dropdownPanel.setVisible(false); // Hidden by default
            
            // Suggestion label inside dropdown
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
            
            // Calculate dropdown size to match email field
            double dropdownScale = calculateProportionalScale();
            int fieldWidth = (int) (520 * dropdownScale);
            fieldWidth = Math.max(250, fieldWidth);
            dropdownPanel.setMaximumSize(new Dimension(fieldWidth, 40));
            dropdownPanel.setPreferredSize(new Dimension(fieldWidth, 40));
            dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add hover effect to dropdown
            dropdownPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    suggestionLabel.setForeground(PRIMARY_COLOR);
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    suggestionLabel.setForeground(new Color(220, 220, 240));
                }
                
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Fill the email field with the suggested username
                    emailField.setText(lastUsername);
                    emailField.setForeground(Color.WHITE);
                    emailField.putClientProperty("placeholderActive", Boolean.FALSE);
                    // Hide the dropdown after clicking
                    dropdownPanel.setVisible(false);
                    // Focus on password field
                    passwordField.requestFocus();
                }
            });
            
            // Show dropdown when email field is focused or clicked
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
                    // Hide dropdown when focus is lost (with delay to allow clicking)
                    Timer hideTimer = new Timer(150, evt -> {
                        if (!dropdownPanel.isAncestorOf(e.getOppositeComponent())) {
                            dropdownPanel.setVisible(false);
                        }
                    });
                    hideTimer.setRepeats(false);
                    hideTimer.start();
                }
            });
            
            emailContainer.add(Box.createRigidArea(new Dimension(0, 4)));
            emailContainer.add(dropdownPanel);
        }
        
        card.add(emailContainer);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(passwordField);

        // Add "Forgot Password?" link
        JLabel forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPasswordLink.setForeground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 160)); // Lighter primary color
        forgotPasswordLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer colorTimer;
            private Color startColor = new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 160);
            private Color endColor = new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 255);
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (colorTimer != null) colorTimer.stop();
                
                colorTimer = new Timer(16, new ActionListener() {
                    private int elapsed = 0;
                    
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        elapsed += 16;
                        float progress = Math.min(1.0f, (float) elapsed / 200);
                        float easedProgress = 1.0f - (float) Math.pow(1.0f - progress, 2);
                        
                        int red = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * easedProgress);
                        int green = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * easedProgress);
                        int blue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * easedProgress);
                        
                        forgotPasswordLink.setForeground(new Color(red, green, blue));
                        
                        if (progress >= 1.0f) {
                            colorTimer.stop();
                        }
                    }
                });
                colorTimer.start();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (colorTimer != null) colorTimer.stop();
                
                colorTimer = new Timer(16, new ActionListener() {
                    private int elapsed = 0;
                    
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        elapsed += 16;
                        float progress = Math.min(1.0f, (float) elapsed / 200);
                        float easedProgress = 1.0f - (float) Math.pow(1.0f - progress, 2);
                        
                        int red = (int) (endColor.getRed() + (startColor.getRed() - endColor.getRed()) * easedProgress);
                        int green = (int) (endColor.getGreen() + (startColor.getGreen() - endColor.getGreen()) * easedProgress);
                        int blue = (int) (endColor.getBlue() + (startColor.getBlue() - endColor.getBlue()) * easedProgress);
                        
                        forgotPasswordLink.setForeground(new Color(red, green, blue));
                        
                        if (progress >= 1.0f) {
                            colorTimer.stop();
                        }
                    }
                });
                colorTimer.start();
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showPasswordResetDialog();
            }
        });
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(forgotPasswordLink);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(switchToSignupButton);

        // Layout with better spacing: center the card vertically (shifted upward via reduced top padding)
        panel.add(card);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new NeonBackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Calculate proportional padding based on frame size
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
        subtitleLabel.setForeground(new Color(SECONDARY_COLOR.getRed(), SECONDARY_COLOR.getGreen(), SECONDARY_COLOR.getBlue(), 200)); // Lighter secondary color
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields
        nameField = createModernTextField("Full Name");
        JTextField signupEmailField = createModernTextField("Email");
        JPasswordField signupPasswordField = createModernPasswordField("Password");
        
        // Add keyboard navigation for signup form
        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    signupEmailField.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    signupEmailField.requestFocus();
                }
            }
        });
        
        signupEmailField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    signupPasswordField.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    signupPasswordField.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    nameField.requestFocus();
                }
            }
        });
        
        signupPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    handleSignup(nameField, signupEmailField, signupPasswordField);
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    signupButton.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    signupEmailField.requestFocus();
                }
            }
        });
        
        signupButton = createGradientButton("Sign Up", SECONDARY_COLOR, new Color(SECONDARY_COLOR.getRed() - 20, SECONDARY_COLOR.getGreen() - 20, SECONDARY_COLOR.getBlue() - 20));
        JButton switchToLoginButton = createGlassButton("Already have an account? Login");
        
        // Add arrow key navigation to signup button
        signupButton.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER || e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    handleSignup(nameField, signupEmailField, signupPasswordField);
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    signupPasswordField.requestFocus();
                }
            }
        });
        
        // Add action listeners
        signupButton.addActionListener(e -> handleSignup(nameField, signupEmailField, signupPasswordField));
        switchToLoginButton.addActionListener(e -> showLogin());
        
        // Layout inside a card container
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
                
                // Focus glow effect
                if (isFocused) {
                    g2d.setColor(new Color(255, 215, 0, 30));
                    g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 19, 19);
                }
                
                // Neumorphic background
                g2d.setColor(new Color(25, 35, 55));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Inner shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);
                
                // Highlight effect
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
        
        // Calculate proportional scaling based on current frame size
        double scale = calculateProportionalScale();
        int fieldWidth = (int) (520 * scale);
        int fieldHeight = (int) (70 * scale);
        
        // Ensure minimum sizes
        fieldWidth = Math.max(250, fieldWidth);
        fieldHeight = Math.max(50, fieldHeight);
        
        field.setMaximumSize(new Dimension(fieldWidth, fieldHeight));
        field.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        field.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(16, (int) (20 * scale))));
        field.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        field.setBackground(new Color(25, 35, 55));
        field.setForeground(new Color(220, 220, 240));
        field.setCaretColor(PRIMARY_COLOR);
        field.setOpaque(false);
        field.setText(placeholder);
        field.setForeground(new Color(200, 200, 220)); // visible placeholder color

        // Placeholder behavior (clear only when typing begins)
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

        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            private boolean isFocused = false;
            private boolean isHoveringEye = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Focus glow effect
                if (isFocused) {
                    g2d.setColor(new Color(255, 215, 0, 30));
                    g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 19, 19);
                }
                
                // Neumorphic background
                g2d.setColor(new Color(25, 35, 55));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Inner shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);
                
                // Highlight effect
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRoundRect(1, 1, getWidth() - 2, 3, 15, 15);
                
                // Draw eye icon with hover effect
                drawEyeIcon(g2d);
                
                // Add hover effect for eye icon
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

				// More realistic eye icon
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Apply micro scale animation if present
				Object scaleObj = getClientProperty("eyeScale");
				float eyeScale = 1.0f;
				if (scaleObj instanceof Float) {
					eyeScale = (Float) scaleObj;
				}

				int iconW = 20;
				int iconH = 16;
				int cx = eyeX + iconW / 2;
				int cy = eyeY + iconH / 2;

				java.awt.geom.AffineTransform oldTx = g2d.getTransform();
				g2d.translate(cx, cy);
				g2d.scale(eyeScale, eyeScale);
				g2d.translate(-cx, -cy);

				Boolean showPasswordProperty = (Boolean) getClientProperty("showPassword");
				boolean showPassword = (showPasswordProperty != null) ? showPasswordProperty : false;

				if (showPassword) {
					// Open eye - more realistic with proper shading
					g2d.setColor(new Color(255, 255, 255, 200)); // White eye
					g2d.fillOval(eyeX, eyeY, iconW, iconH); // Eye white

					g2d.setColor(new Color(50, 50, 50)); // Dark pupil
					g2d.fillOval(eyeX + 6, eyeY + 4, 8, 8); // Pupil

					g2d.setColor(new Color(255, 255, 255, 150)); // Highlight
					g2d.fillOval(eyeX + 7, eyeY + 5, 3, 3); // Eye highlight

					g2d.setColor(new Color(200, 200, 200, 100)); // Eyelid shadow
					g2d.setStroke(new BasicStroke(1.5f));
					g2d.drawArc(eyeX + 1, eyeY + 1, iconW - 2, iconH - 2, 0, 180); // Top eyelid
				} else {
					// Closed eye with visible eyelashes (more realistic)
					// Almond-shaped outline (subtle) under the lid for form
					g2d.setColor(new Color(255, 255, 255, 60));
					g2d.setStroke(new BasicStroke(1.2f));
					g2d.drawArc(eyeX + 1, eyeY + 3, iconW - 2, iconH - 6, 200, 140); // lower curve hint

					// Upper eyelid (thicker stroke)
					g2d.setColor(new Color(90, 90, 95));
					g2d.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					g2d.drawArc(eyeX + 1, eyeY + 1, iconW - 2, iconH - 4, 0, 180); // closed lid

					// Eyelashes along the upper lid
					g2d.setColor(new Color(70, 70, 75));
					g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

					int lashCount = 9;
					for (int i = 0; i < lashCount; i++) {
						float t = (float) i / (lashCount - 1);
						// Position along the lid (avoid extreme ends slightly)
						float px = eyeX + 3 + t * (iconW - 6);
						// Slight curve offset to follow the arc
						float curve = (float) Math.sin(t * Math.PI) * 3.5f;
						float py = eyeY + 4 + (3.5f - curve);

						// Angle fan outward (from -35° to -145°)
						float angleDeg = -35f - t * 110f;
						double ang = Math.toRadians(angleDeg);

						// Lash length varies slightly (longer near center)
						float len = 5.0f + (float) Math.sin(t * Math.PI) * 2.0f;

						float dx = (float) (Math.cos(ang) * len);
						float dy = (float) (Math.sin(ang) * len);

						g2d.drawLine(Math.round(px), Math.round(py), Math.round(px + dx), Math.round(py + dy));
					}

					// Soft lid highlight
					g2d.setColor(new Color(255, 255, 255, 40));
					g2d.setStroke(new BasicStroke(1f));
					g2d.drawArc(eyeX + 2, eyeY + 2, iconW - 4, iconH - 6, 10, 160);
				}

				// restore transform
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
        
        // Calculate proportional scaling based on current frame size
        double scale = calculateProportionalScale();
        int fieldWidth = (int) (520 * scale);
        int fieldHeight = (int) (70 * scale);
        
        // Ensure minimum sizes
        fieldWidth = Math.max(250, fieldWidth);
        fieldHeight = Math.max(50, fieldHeight);
        
        field.setMaximumSize(new Dimension(fieldWidth, fieldHeight));
        field.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        field.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(16, (int) (20 * scale))));
        field.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        field.setBackground(new Color(25, 35, 55));
        field.setForeground(new Color(220, 220, 240));
        field.setCaretColor(PRIMARY_COLOR);
        field.setOpaque(false);
        field.setText(placeholder);
        field.setEchoChar((char) 0); // Show placeholder text initially

        // Placeholder behavior
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        
        // Add click listener for eye icon with cursor changes
        field.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int eyeX = field.getWidth() - 40;
                int eyeY = (field.getHeight() - 16) / 2;
                // Larger click area for better usability
                if (e.getX() >= eyeX - 5 && e.getX() <= eyeX + 25 && 
                    e.getY() >= eyeY - 5 && e.getY() <= eyeY + 21) {
                    
                    // Add micro-animation for eye icon click
                    addEyeIconAnimation(field);
                    
                    // Toggle password visibility
                    Boolean showPasswordProperty = (Boolean) field.getClientProperty("showPassword");
                    boolean currentShowPassword = (showPasswordProperty != null) ? showPasswordProperty : false;
                    boolean newShowPassword = !currentShowPassword;
                    
                    field.putClientProperty("showPassword", newShowPassword);
                    field.setEchoChar(newShowPassword ? (char) 0 : '•');
                    field.repaint();
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Reset cursor when mouse leaves the field
                field.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
            }
        });
        
        // Add separate mouse motion listener for cursor changes
        field.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int eyeX = field.getWidth() - 40;
                int eyeY = (field.getHeight() - 16) / 2;
                // Change cursor when hovering over eye icon
                if (e.getX() >= eyeX - 5 && e.getX() <= eyeX + 25 && 
                    e.getY() >= eyeY - 5 && e.getY() <= eyeY + 21) {
                    field.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    // Set hover state for visual feedback
                    field.putClientProperty("isHoveringEye", Boolean.TRUE);
                    field.repaint();
                } else {
                    field.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
                    // Clear hover state
                    field.putClientProperty("isHoveringEye", Boolean.FALSE);
                    field.repaint();
                }
            }
        });
        
        // Add key listener for placeholder behavior
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                String current = new String(field.getPassword());
                if (Boolean.TRUE.equals(active) && current.equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.WHITE);
                    field.setEchoChar('•'); // Always hide password by default
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

        return field;
    }
    
    private JButton createGradientButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Enhanced colors based on state
                Color hoverColor1 = isHovered ? 
                    new Color(Math.min(255, color1.getRed() + 40), Math.min(255, color1.getGreen() + 40), Math.min(255, color1.getBlue() + 40)) : 
                    color1;
                Color hoverColor2 = isHovered ? 
                    new Color(Math.min(255, color2.getRed() + 40), Math.min(255, color2.getGreen() + 40), Math.min(255, color2.getBlue() + 40)) : 
                    color2;
                
                // Scale effect when hovered
                float scale = isHovered ? 1.02f : 1.0f; // Reduced scale effect
                int offsetX = (int)((getWidth() * (scale - 1)) / 2);
                int offsetY = (int)((getHeight() * (scale - 1)) / 2);
                
                // Diagonal gradient for login button
                GradientPaint gradient = new GradientPaint(0, 0, hoverColor1, getWidth(), getHeight(), hoverColor2);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(offsetX, offsetY, (int)(getWidth() * scale), (int)(getHeight() * scale), 20, 20);
                
                // Enhanced inner glow when hovered
                int glowAlpha = isHovered ? 40 : 20;
                g2d.setColor(new Color(255, 255, 255, glowAlpha));
                g2d.fillRoundRect(offsetX + 2, offsetY + 2, (int)((getWidth() - 4) * scale), (int)((getHeight() / 2) * scale), 18, 18);
                
                
                // Text with enhanced shadow when hovered
                g2d.setColor(new Color(0, 0, 0, isHovered ? 50 : 30));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 + 1;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + 1;
                g2d.drawString(getText(), x, y);
                
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x - 1, y - 1);
                
                g2d.dispose();
            }
            
            public void setHovered(boolean hovered) {
                this.isHovered = hovered;
                repaint();
            }
            
            public void setPressed(boolean pressed) {
                this.isPressed = pressed;
                repaint();
            }
            
            // Ensure button is focusable and can receive key events
            @Override
            public boolean isFocusable() {
                return true;
            }
            
            @Override
            public void processMouseEvent(java.awt.event.MouseEvent e) {
                super.processMouseEvent(e);
                if (e.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED) {
                    setPressed(true);
                } else if (e.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED) {
                    setPressed(false);
                }
            }
            
            @Override
            protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {
                super.processMouseMotionEvent(e);
                if (e.getID() == java.awt.event.MouseEvent.MOUSE_ENTERED) {
                    setHovered(true);
                } else if (e.getID() == java.awt.event.MouseEvent.MOUSE_EXITED) {
                    setHovered(false);
                }
            }
        };
        
        // Calculate proportional scaling based on current frame size
        double scale = calculateProportionalScale();
        int buttonWidth = (int) (520 * scale);
        int buttonHeight = (int) (70 * scale);
        
        // Ensure minimum sizes
        buttonWidth = Math.max(250, buttonWidth);
        buttonHeight = Math.max(50, buttonHeight);
        
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setFont(new Font("Trebuchet MS", Font.BOLD, Math.max(16, (int) (22 * scale))));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add subtle hover effect
        addButtonHoverEffect(button);
        
        return button;
    }
    
    private JButton createGlassButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Modern outlined button with subtle background
                Color bg = getModel().isRollover() ? new Color(255, 255, 255, 12) : new Color(255, 255, 255, 5);
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border with gradient effect
                g2d.setColor(getModel().isRollover() ? new Color(255, 255, 255, 70) : new Color(255, 255, 255, 40));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                
                // Inner highlight
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
        
        // Calculate proportional scaling based on current frame size
        double scale = calculateProportionalScale();
        int buttonWidth = (int) (520 * scale);
        int buttonHeight = (int) (65 * scale);
        
        // Ensure minimum sizes
        buttonWidth = Math.max(250, buttonWidth);
        buttonHeight = Math.max(45, buttonHeight);
        
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setFont(new Font("Trebuchet MS", Font.PLAIN, Math.max(14, (int) (20 * scale))));
        button.setForeground(new Color(200, 200, 220));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Enable rollover for hover visuals
        button.setRolloverEnabled(true);
        // Add subtle hover effect
        addButtonHoverEffect(button);
        
        return button;
    }

    // Google OAuth methods removed - using SQLite authentication only


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

                // Respect per-button override for text color when requested
                Color textColor = getForeground();
                Object forceWhite = getClientProperty("forceWhiteText");
                if (Boolean.TRUE.equals(forceWhite)) {
                    textColor = Color.WHITE;
                }
                g2d.setColor(textColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent() - fm.getDescent();
                int centerX = getWidth() / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                // Optional left icon
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
                    // Backdrop circle for icon visibility on colored backgrounds
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
                    // Paint the actual icon on top
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

        // Calculate proportional scaling based on current frame size
        double scale = calculateProportionalScale();
        int buttonWidth = (int) (520 * scale);
        int buttonHeight = (int) (70 * scale);
        
        // Ensure minimum sizes
        buttonWidth = Math.max(250, buttonWidth);
        buttonHeight = Math.max(50, buttonHeight);
        
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setFont(new Font("Trebuchet MS", Font.BOLD, Math.max(16, (int) (22 * scale))));
        button.setForeground(foregroundColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Enable rollover for hover visuals
        button.setRolloverEnabled(true);
        // Add subtle hover effect
        addButtonHoverEffect(button);
        
        return button;
    }

    private class GradientPanel extends JPanel {
        private Timer animationTimer;
        private float animationPhase = 0.0f;
        
        public GradientPanel() {
            // Start animation timer
            animationTimer = new Timer(50, e -> {
                animationPhase += 0.02f;
                if (animationPhase > 2 * Math.PI) {
                    animationPhase = 0.0f;
                }
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Enhanced dark gradient with smoother transitions (primary background)
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(6, 15, 35), // deeper dark navy
                getWidth(), getHeight(), new Color(12, 25, 50) // slightly lighter with more blue
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Animated radial gradient with subtle color hints
            float centerX = getWidth() / 2.0f + (float)(Math.sin(animationPhase) * 30);
            float centerY = getHeight() / 2.0f + (float)(Math.cos(animationPhase * 0.7) * 20);
            
            RadialGradientPaint radialGradient = new RadialGradientPaint(
                centerX, centerY, Math.max(getWidth(), getHeight()) / 1.5f,
                new float[]{0.0f, 0.4f, 0.8f, 1.0f},
                new Color[]{
                    new Color(255, 255, 255, 8), // brighter center
                    new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 15), // subtle primary color
                    new Color(SECONDARY_COLOR.getRed(), SECONDARY_COLOR.getGreen(), SECONDARY_COLOR.getBlue(), 10), // subtle secondary color
                    new Color(0, 0, 0, 0)
                }
            );
            g2d.setPaint(radialGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Add subtle geometric pattern overlay
            drawAnimatedGeometricPattern(g2d);

            g2d.dispose();
        }
        
        private void drawAnimatedGeometricPattern(Graphics2D g2d) {
            // Animated opacity based on animation phase - very subtle
            int alpha = (int)(2 + 1 * Math.sin(animationPhase * 2));
            g2d.setColor(new Color(255, 255, 255, alpha)); // Use white for subtle pattern
            g2d.setStroke(new BasicStroke(1));
            
            int spacing = 60;
            int patternSize = 20;
            
            // Animate pattern position
            float offsetX = (float)(Math.sin(animationPhase * 0.3) * 10);
            float offsetY = (float)(Math.cos(animationPhase * 0.4) * 8);
            
            for (int x = 0; x < getWidth(); x += spacing) {
                for (int y = 0; y < getHeight(); y += spacing) {
                    // Draw subtle coding-themed patterns with animation
                    if ((x + y) % (spacing * 2) == 0) {
                        // Draw small squares (like code blocks) with animated position
                        g2d.drawRect((int)(x + offsetX), (int)(y + offsetY), patternSize, patternSize);
                    } else {
                        // Draw small circles (like code elements) with animated position
                        g2d.drawOval((int)(x + offsetX), (int)(y + offsetY), patternSize, patternSize);
                    }
                }
            }
        }
    }

    private static class GradientTextLabel extends JComponent {
        private final String text;
        private final Font font;
        private final Color startColor;
        private final Color endColor;

        GradientTextLabel(String text, Font font, Color startColor, Color endColor) {
            this.text = text;
            this.font = font;
            this.startColor = startColor;
            this.endColor = endColor;
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            Graphics g = getGraphics();
            if (g == null) {
                return new Dimension(100, 30);
            }
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(text);
            int h = fm.getAscent() + fm.getDescent();
            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = 0;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            GradientPaint paint = new GradientPaint(0, 0, startColor, Math.max(textWidth, 1), 0, endColor);
            g2d.setPaint(paint);
            g2d.drawString(text, x, y);
            g2d.dispose();
        }
    }

    private void handleLogin() {
        // Normalize placeholders just in case flags are stale
        Object emailPA = emailField.getClientProperty("placeholderActive");
        if (Boolean.TRUE.equals(emailPA) && "Email".equalsIgnoreCase(emailField.getText().trim())) {
            emailField.setText("");
            emailField.putClientProperty("placeholderActive", Boolean.FALSE);
            emailField.setForeground(Color.WHITE);
        }
        Object passPA = passwordField.getClientProperty("placeholderActive");
        String passTextNow = new String(passwordField.getPassword());
        if (Boolean.TRUE.equals(passPA) && "Password".equalsIgnoreCase(passTextNow.trim())) {
            passwordField.setText("");
            passwordField.putClientProperty("placeholderActive", Boolean.FALSE);
            passwordField.setForeground(Color.WHITE);
            passwordField.setEchoChar('•');
        }

        String username = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Consider placeholders only if active AND the text equals the placeholder label
        boolean emailPlaceholderActive = Boolean.TRUE.equals(emailField.getClientProperty("placeholderActive"));
        boolean passPlaceholderActive = Boolean.TRUE.equals(passwordField.getClientProperty("placeholderActive"));
        boolean emailEffectivelyEmpty = username.isEmpty() || (emailPlaceholderActive && "Email".equalsIgnoreCase(username));
        boolean passEffectivelyEmpty = password.isEmpty() || (passPlaceholderActive && "Password".equalsIgnoreCase(password));
        if (emailEffectivelyEmpty || passEffectivelyEmpty) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading state and switch to loading card before authentication
        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");
        showCard("LOADING");
        
        // Use SQLite authentication
        SwingUtilities.invokeLater(() -> {
            try {
                PlayerProfile profile = authService.login(username, password);
                
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                
                if (profile != null) {
                    // Store current profile
                    this.currentProfile = profile;
                    
                    // Save username for auto-fill on next login
                    userPreferences.setLastUsername(username);
                    
                    // Debug logging
                    System.out.println("=== LOGIN SUCCESS ===");
                    System.out.println("Username: " + profile.getUsername());
                    System.out.println("Onboarding Completed: " + profile.isOnboardingCompleted());
                    System.out.println("Onboarding Goal: " + profile.getOnboardingGoal());
                    System.out.println("Onboarding Language: " + profile.getOnboardingLanguage());
                    System.out.println("Onboarding Skill: " + profile.getOnboardingSkill());
                    
                    // Check if user has completed onboarding
                    if (profile.isOnboardingCompleted()) {
                        System.out.println("→ Skipping onboarding, going to dashboard");
                        // Skip onboarding, go directly to dashboard
                        new javax.swing.Timer(600, e2 -> {
                            ((javax.swing.Timer) e2.getSource()).stop();
                            // Load onboarding data and go to dashboard
                            String goal = profile.getOnboardingGoal();
                            String language = profile.getOnboardingLanguage();
                            String skill = profile.getOnboardingSkill();
                            openDashboardInCard(goal, language, skill);
                        }).start();
                    } else {
                        System.out.println("→ Showing onboarding questions");
                        // Navigate to in-app onboarding within same window
                        new javax.swing.Timer(600, e2 -> {
                            ((javax.swing.Timer) e2.getSource()).stop();
                            showCard("ONBOARDING");
                        }).start();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    showCard("LOGIN");
                }
            } catch (Exception e) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                JOptionPane.showMessageDialog(this, "Authentication error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showCard("LOGIN");
            }
        });
    }
    
    private void handleSignup(JTextField nameFieldParam, JTextField emailField, JPasswordField passwordField) {
        // Normalize placeholders just before reading values
        Object nPA2 = nameFieldParam.getClientProperty("placeholderActive");
        if (Boolean.TRUE.equals(nPA2) && "Full Name".equalsIgnoreCase(nameFieldParam.getText().trim())) {
            nameFieldParam.setText("");
            nameFieldParam.putClientProperty("placeholderActive", Boolean.FALSE);
            nameFieldParam.setForeground(Color.WHITE);
        }
        Object ePA2 = emailField.getClientProperty("placeholderActive");
        if (Boolean.TRUE.equals(ePA2) && "Email".equalsIgnoreCase(emailField.getText().trim())) {
            emailField.setText("");
            emailField.putClientProperty("placeholderActive", Boolean.FALSE);
            emailField.setForeground(Color.WHITE);
        }
        Object pPA2 = passwordField.getClientProperty("placeholderActive");
        String passNow = new String(passwordField.getPassword());
        if (Boolean.TRUE.equals(pPA2) && "Password".equalsIgnoreCase(passNow.trim())) {
            passwordField.setText("");
            passwordField.putClientProperty("placeholderActive", Boolean.FALSE);
            passwordField.setForeground(Color.WHITE);
            passwordField.setEchoChar('•');
        }

        String name = nameFieldParam.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Force-deactivate placeholders if user has typed real input
        if (!name.isEmpty() && !"Full Name".equalsIgnoreCase(name)) {
            nameFieldParam.putClientProperty("placeholderActive", Boolean.FALSE);
        }
        if (!email.isEmpty() && !"Email".equalsIgnoreCase(email)) {
            emailField.putClientProperty("placeholderActive", Boolean.FALSE);
        }
        if (!password.isEmpty() && !"Password".equalsIgnoreCase(password)) {
            passwordField.putClientProperty("placeholderActive", Boolean.FALSE);
            passwordField.setEchoChar('•');
        }
        
        // Consider placeholders only if active AND the text equals the placeholder label
        boolean namePlaceholderActive = Boolean.TRUE.equals(nameFieldParam.getClientProperty("placeholderActive"));
        boolean emailPlaceholderActive = Boolean.TRUE.equals(emailField.getClientProperty("placeholderActive"));
        boolean passPlaceholderActive = Boolean.TRUE.equals(passwordField.getClientProperty("placeholderActive"));
        
        boolean emptyName = name.isEmpty() || (namePlaceholderActive && "Full Name".equalsIgnoreCase(name));
        boolean emptyEmail = email.isEmpty() || (emailPlaceholderActive && "Email".equalsIgnoreCase(email));
        boolean emptyPass = password.isEmpty() || (passPlaceholderActive && "Password".equalsIgnoreCase(password));

        if (emptyName || emptyEmail || emptyPass) {
            StringBuilder sb = new StringBuilder();
            if (emptyName) sb.append("Name");
            if (emptyEmail) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("Email");
            }
            if (emptyPass) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("Password");
            }
            JOptionPane.showMessageDialog(this, "Please fill in all fields: " + sb.toString() + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show loading state
        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");
        
        // Use email as username for simplicity
        String username = email;
        
        // Use SQLite authentication for registration
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.register(username, password);
                
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Account created successfully!\nWelcome to ForgeGrid, " + name + "!\nPlease login with your credentials.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Switch to login panel
                    showLogin();
                    
                    // Reset signup fields to their placeholder state
                    nameFieldParam.setText("Full Name");
                    nameFieldParam.putClientProperty("placeholderActive", Boolean.TRUE);
                    nameFieldParam.setForeground(new Color(200, 200, 220));
                    
                    emailField.setText("Email");
                    emailField.putClientProperty("placeholderActive", Boolean.TRUE);
                    emailField.setForeground(new Color(200, 200, 220));
                    
                    passwordField.setText("Password");
                    passwordField.putClientProperty("placeholderActive", Boolean.TRUE);
                    passwordField.setForeground(new Color(200, 200, 220));
                    passwordField.setEchoChar((char) 0);
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different email.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                JOptionPane.showMessageDialog(this, "Registration error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        
        try {
            // Use the single existing logo image
            java.net.URL logoUrl = getClass().getResource("/com/forgegrid/icon/logo2_transparent.png");
            if (logoUrl != null) {
                System.out.println("Logo found! Loading..."); // Debug output
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                
                // Scale the logo proportionally (much larger base size)
                double scale = calculateProportionalScale();
                int logoWidth = (int) (360 * scale);
                int logoHeight = (int) (220 * scale);
                
                // Ensure minimum sizes (much larger)
                logoWidth = Math.max(240, logoWidth);
                logoHeight = Math.max(140, logoHeight);
                
                // Scale the image
                Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledLogo);
                
                JLabel logoLabel = new JLabel(scaledIcon);
                logoLabel.setHorizontalAlignment(JLabel.CENTER);
                logoPanel.add(logoLabel, BorderLayout.CENTER);
                
                // Set preferred size
                logoPanel.setPreferredSize(new Dimension(logoWidth, logoHeight));
                logoPanel.setMaximumSize(new Dimension(logoWidth, logoHeight));
            } else {
                // Fallback to text if logo not found
                JLabel fallbackLabel = new JLabel("ForgeGrid");
                fallbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
                fallbackLabel.setForeground(PRIMARY_COLOR);
                fallbackLabel.setHorizontalAlignment(JLabel.CENTER);
                logoPanel.add(fallbackLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Fallback to text if there's any error loading the logo
            JLabel fallbackLabel = new JLabel("ForgeGrid");
            fallbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackLabel.setForeground(PRIMARY_COLOR);
            fallbackLabel.setHorizontalAlignment(JLabel.CENTER);
            logoPanel.add(fallbackLabel, BorderLayout.CENTER);
        }
        
        return logoPanel;
    }
    
    private double calculateProportionalScale() {
        // Calculate proportional scaling factor based on current frame size
        // Base frame size is 800x900, so we calculate scaling factor
        double baseWidth = 800.0;
        double baseHeight = 900.0;
        double currentWidth = getWidth();
        double currentHeight = getHeight();
        
        // Use the smaller scaling factor to ensure components fit
        double widthScale = Math.max(0.5, Math.min(1.5, currentWidth / baseWidth));
        double heightScale = Math.max(0.5, Math.min(1.5, currentHeight / baseHeight));
        return Math.min(widthScale, heightScale);
    }
    
    private void refreshComponentSizes() {
        // Calculate proportional scaling factor based on current frame size
        double scale = calculateProportionalScale();
        
        // Calculate proportional sizes
        int fieldWidth = (int) (520 * scale);
        int fieldHeight = (int) (70 * scale);
        int buttonHeight = (int) (168 * scale);
        int glassButtonHeight = (int) (156 * scale);
        
        // Ensure minimum sizes
        fieldWidth = Math.max(250, fieldWidth);
        fieldHeight = Math.max(50, fieldHeight);
        buttonHeight = Math.max(112, buttonHeight);
        glassButtonHeight = Math.max(104, glassButtonHeight);
        
        // Calculate proportional font sizes
        int fieldFontSize = Math.max(14, (int) (20 * scale));
        int buttonFontSize = Math.max(18, (int) (26 * scale));
        int glassButtonFontSize = Math.max(16, (int) (22 * scale));
        
        // Update field sizes and fonts
        if (emailField != null) {
            Dimension fieldSize = new Dimension(fieldWidth, fieldHeight);
            emailField.setMaximumSize(fieldSize);
            emailField.setPreferredSize(fieldSize);
            emailField.setFont(new Font("Trebuchet MS", Font.PLAIN, fieldFontSize));
        }
        
        if (passwordField != null) {
            Dimension fieldSize = new Dimension(fieldWidth, fieldHeight);
            passwordField.setMaximumSize(fieldSize);
            passwordField.setPreferredSize(fieldSize);
            passwordField.setFont(new Font("Trebuchet MS", Font.PLAIN, fieldFontSize));
        }
        
        if (nameField != null) {
            Dimension fieldSize = new Dimension(fieldWidth, fieldHeight);
            nameField.setMaximumSize(fieldSize);
            nameField.setPreferredSize(fieldSize);
            nameField.setFont(new Font("Trebuchet MS", Font.PLAIN, fieldFontSize));
        }
        
        // Update button sizes and fonts
        if (loginButton != null) {
            Dimension buttonSize = new Dimension(fieldWidth, buttonHeight);
            loginButton.setMaximumSize(buttonSize);
            loginButton.setPreferredSize(buttonSize);
            loginButton.setFont(new Font("Trebuchet MS", Font.BOLD, buttonFontSize));
        }
        
        if (signupButton != null) {
            Dimension buttonSize = new Dimension(fieldWidth, buttonHeight);
            signupButton.setMaximumSize(buttonSize);
            signupButton.setPreferredSize(buttonSize);
            signupButton.setFont(new Font("Trebuchet MS", Font.BOLD, buttonFontSize));
        }
        
        
        
        // Update all other buttons in the panels
        updateAllButtonSizes(fieldWidth, buttonHeight, glassButtonHeight, buttonFontSize, glassButtonFontSize);
        
        // Refresh the layout
        revalidate();
        repaint();
    }
    
    private void updateAllButtonSizes(int buttonWidth, int buttonHeight, int glassButtonHeight, 
                                   int buttonFontSize, int glassButtonFontSize) {
        // Simplified button size updates
        if (loginButton != null) {
            loginButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
            loginButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            loginButton.setFont(new Font("Trebuchet MS", Font.BOLD, buttonFontSize));
        }
        if (signupButton != null) {
            signupButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
            signupButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            signupButton.setFont(new Font("Trebuchet MS", Font.BOLD, buttonFontSize));
        }
    }
    
    
    /**
     * Simplified button hover effect
     */
    private void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Set hover state for gradient buttons
                if (button.getClass().getSimpleName().contains("$")) {
                    try {
                        java.lang.reflect.Method setHovered = button.getClass().getMethod("setHovered", boolean.class);
                        setHovered.invoke(button, true);
                    } catch (Exception ex) {
                        // Fallback for regular buttons
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
                // Reset hover state for gradient buttons
                if (button.getClass().getSimpleName().contains("$")) {
                    try {
                        java.lang.reflect.Method setHovered = button.getClass().getMethod("setHovered", boolean.class);
                        setHovered.invoke(button, false);
                    } catch (Exception ex) {
                        // Fallback for regular buttons
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
    
    /**
     * Simplified link hover animation
     */
    private void addLinkHoverAnimation(JLabel link) {
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = link.getForeground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                link.setForeground(new Color(255, 165, 0)); // Orange
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                link.setForeground(originalColor);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                link.setForeground(new Color(PRIMARY_COLOR.getRed() - 40, PRIMARY_COLOR.getGreen() - 40, PRIMARY_COLOR.getBlue() - 40));
            }
        });
    }
    
    /**
     * Simplified eye icon animation
     */
    private void addEyeIconAnimation(JPasswordField field) {
        // Simple scale animation
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
        // Set larger default font for all components
        Font defaultFont = new Font("Trebuchet MS", Font.PLAIN, 18);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        
        // Make sure the look and feel is set
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Main method removed since we're using Main.java
    
    
    /**
     * Show password reset dialog
     */
    private void showPasswordResetDialog() {
        // Create a simple input dialog for username
        String username = JOptionPane.showInputDialog(
            this, 
            "Enter your username (email):", 
            "Reset Password", 
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (username != null && !username.trim().isEmpty()) {
            // Check if user exists
            if (authService.usernameExists(username.trim())) {
                // Get new password
                String newPassword = JOptionPane.showInputDialog(
                    this, 
                    "Enter your new password:", 
                    "Reset Password", 
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    if (newPassword.length() < 6) {
                        JOptionPane.showMessageDialog(
                            this, 
                            "Password must be at least 6 characters long.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                    
                    // Reset password
                    boolean success = authService.resetPassword(username.trim(), newPassword);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(
                            this, 
                            "Password reset successfully!\nYou can now login with your new password.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            this, 
                            "Failed to reset password. Please try again.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                    this, 
                    "Username not found. Please check your username and try again.", 
                    "User Not Found", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Initialize authentication system
     */
    private void initializeAuthentication() {
        // SQLite authentication is ready immediately
        Timer showAuthTimer = new Timer(5000, e -> {
            if (loadingScreen != null) {
                loadingScreen.startFadeOut(() -> showLogin());
            } else {
                showLogin();
            }
        });
        showAuthTimer.setRepeats(false);
        showAuthTimer.start();
    }
    
    
    /**
     * Show the SIGNUP card reliably and focus the first field.
     * Ensures the signup panel exists and triggers layout refresh.
     */
    private void showSignup() {
        SwingUtilities.invokeLater(() -> {
            // Ensure a fresh SIGNUP panel is present and visible
            JPanel signupPanel = createSignupPanel();
            cardPanel.add(signupPanel, "SIGNUP");
            cardLayout.show(cardPanel, "SIGNUP");
            cardPanel.revalidate();
            cardPanel.repaint();
            if (nameField != null) nameField.requestFocusInWindow();
        });
    }
    
    /**
     * Show the LOGIN card reliably and focus the first field.
     * Ensures the login panel exists and triggers layout refresh.
     */
    private void showLogin() {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(cardPanel, "LOGIN");
            cardPanel.revalidate();
            cardPanel.repaint();
            if (emailField != null) emailField.requestFocusInWindow();
        });
    }

}
