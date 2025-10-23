package com.forgegrid.ui;

import com.forgegrid.auth.AuthService;
import com.forgegrid.config.UserPreferences;
import com.forgegrid.model.PlayerProfile;
import com.forgegrid.service.UserService;
import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused"})
public class AuthUI extends JFrame {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(0xffcc4d); // #ffcc4d - Golden yellow
    private static final Color SECONDARY_COLOR = new Color(0x3a6ea5); // #3a6ea5 - Blue
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckbox;
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
    
    // Panel references for dynamic onboarding creation
    private JPanel loginPanel;
    private JPanel signupPanel;
    private WelcomeUI welcomePanel;
    private JPanel onboardingPrompt;
    // placeholders removed; cards are managed via cardPanel
    
    public AuthUI() {
        this.authService = new AuthService();
        this.userService = new UserService();
        this.userPreferences = new UserPreferences();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("ForgeGrid");
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
        } catch (Exception e) { /* ignore icon load errors */ }
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { /* ignore look and feel issues */ }
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
		// Create loading screen
		loadingScreen = new LoadingScreen();
		addWithFade(loadingScreen, "LOADING");
        
        
		// Create login and signup panels
        loginPanel = createLoginPanel();
        signupPanel = createSignupPanel();
        
        addWithFade(loginPanel, "LOGIN");
        addWithFade(signupPanel, "SIGNUP");

		// Create landing cover panel (shown first)
        // Welcome screen inside the same window (instead of separate tab/window)
        welcomePanel = new WelcomeUI();
        welcomePanel.addStartActionListener(e -> showCard("LOGIN"));
        addWithFade(welcomePanel, "WELCOME");

        // In-app Onboarding panel (same window) - will be created dynamically based on user status
        addWithFade(new JPanel(), "ONBOARDING"); // Placeholder, will be replaced dynamically

        // Inline onboarding prompt card (asks Yes/No inside app UI)
        onboardingPrompt = createOnboardingPromptPanel();
        addWithFade(onboardingPrompt, "ONBOARDING_PROMPT");
        
        // Build a top header with a left-aligned back button (arrow only)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(new Color(238, 238, 238));
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        backButton = createBackArrowButton();
        header.add(backButton, BorderLayout.WEST);

        // Root container combines header and cards
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(header, BorderLayout.NORTH);
        root.add(cardPanel, BorderLayout.CENTER);
        add(root);
        
        // Show welcome first with fade-in
        cardLayout.show(cardPanel, "WELCOME");
        playFade("WELCOME");
        updateBackButtonVisibility("WELCOME");
        
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
        if (!"LOGIN".equals(name)) {
            playFade(name);
        }
        updateBackButtonVisibility(name);
    }

    private void openDashboardInCard(String goal, String language, String skill) {
        // Show loading screen immediately for better UX
        showCard("LOADING");
        
        // Create Dashboard asynchronously to avoid blocking the UI thread
        SwingWorker<Dashboard, Void> worker = new SwingWorker<Dashboard, Void>() {
            @Override
            protected Dashboard doInBackground() throws Exception {
                // Create Dashboard in background thread
                return new Dashboard(currentProfile, true); // skipWelcome = true
            }
            
            @Override
            protected void done() {
                try {
                    // Get the created Dashboard
                    Dashboard dashboard = get();
                    
                    // Get the dashboard content pane
                    Container dashboardContent = dashboard.getContentPane();
                    
                    // Replace this frame's content with the dashboard content
                    setContentPane(dashboardContent);
                    
                    // Keep the title as "ForgeGrid"
                    setTitle("ForgeGrid");
                    
                    // Refresh the frame
                    revalidate();
                    repaint();
                    
                    // Dispose the temporary dashboard frame (we only needed its content)
                    dashboard.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        AuthUI.this,
                        "Error loading dashboard: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }



    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(238, 238, 238));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Calculate proportional padding based on frame size
        double scale = calculateProportionalScale();
        int padding = (int) (80 * scale);
        padding = Math.max(30, Math.min(120, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(Math.max(10, padding - 80), padding, padding, padding));
        
        // Modern tagline with enhanced styling
        JLabel mainTagline = new JLabel("ForgeGrid – Where coding challenges become milestones.");
        mainTagline.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mainTagline.setForeground(Color.DARK_GRAY);
        mainTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create the logo panel
        JPanel logoPanel = createLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Modern title with enhanced styling
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // Increased height to avoid clipping
        
        JLabel welcomePart = new JLabel("ForgeGrid");
        welcomePart.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomePart.setForeground(Color.BLACK);
        
        // no gradient brand here; simple label already used
        
        // Create a container panel to hold both labels
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleContainer.setOpaque(false);
        titleContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        titleContainer.add(welcomePart);
        
        titleRow.add(titleContainer);
        
        
        // Form fields
        emailField = createModernTextField("Username or Email");
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
        
        loginButton = new JButton("Login");
        loginButton.setUI(new BasicButtonUI());
        loginButton.setBackground(Theme.BRAND_PINK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        // Ensure identical dimensions to signup button
        loginButton.setMaximumSize(new Dimension(520, 70));
        loginButton.setPreferredSize(new Dimension(520, 70));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComponent loginGradientWrap = loginButton; // use plain button for basic Swing look
        JButton switchToSignupButton = new JButton("New User? Sign Up");
        switchToSignupButton.setMaximumSize(new Dimension(520, 70));
        switchToSignupButton.setPreferredSize(new Dimension(520, 70));
        switchToSignupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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
            
        // Remove hover/animation effects
        dropdownPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    // no-op
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    // no-op
                }
                
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Fill the email field with the suggested username
                    emailField.setText(lastUsername);
                    emailField.setForeground(Color.BLACK);
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
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Add "Remember Me" checkbox
        rememberMeCheckbox = new JCheckBox("Remember Me");
        rememberMeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rememberMeCheckbox.setForeground(new Color(200, 210, 220));
        rememberMeCheckbox.setOpaque(false);
        rememberMeCheckbox.setFocusPainted(false);
        rememberMeCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        rememberMeCheckbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(rememberMeCheckbox);

        // Add "Forgot Password?" link
        JLabel forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPasswordLink.setForeground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 160)); // Lighter primary color
        forgotPasswordLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.addMouseListener(new java.awt.event.MouseAdapter() {
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // no-op
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // no-op
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showPasswordResetDialog();
            }
        });
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(forgotPasswordLink);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(loginGradientWrap);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(switchToSignupButton);

        // Layout with better spacing: center the card vertically (shifted upward via reduced top padding)
        panel.add(card);
        panel.add(Box.createVerticalGlue());
        
        // Auto-fill credentials if remember me was previously enabled
        if (userPreferences.isRememberMeEnabled()) {
            String savedUsername = userPreferences.getSavedUsername();
            String savedPassword = userPreferences.getSavedPassword();
            
            if (savedUsername != null && savedPassword != null) {
                emailField.setText(savedUsername);
                emailField.setForeground(Color.BLACK);
                emailField.putClientProperty("placeholderActive", Boolean.FALSE);
                
                passwordField.setText(savedPassword);
                passwordField.setForeground(Color.BLACK);
                passwordField.putClientProperty("placeholderActive", Boolean.FALSE);
                rememberMeCheckbox.setSelected(true);
            }
        }
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(238, 238, 238));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Calculate proportional padding based on frame size
        double scale = calculateProportionalScale();
        int padding = (int) (60 * scale);
        padding = Math.max(25, Math.min(100, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        JLabel titleLabel = new JLabel("ForgeGrid");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("ForgeGrid – Where coding challenges become milestones.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields
        nameField = createModernTextField("Username");
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
        
        signupButton = new JButton("Sign Up");
        signupButton.setUI(new BasicButtonUI());
        signupButton.setBackground(Theme.BRAND_PINK);
        signupButton.setForeground(Color.WHITE);
        signupButton.setBorderPainted(false);
        signupButton.setFocusPainted(false);
        // Ensure identical dimensions to login button
        signupButton.setMaximumSize(new Dimension(520, 70));
        signupButton.setPreferredSize(new Dimension(520, 70));
        JComponent signupGradientWrap = signupButton;
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
        signupCard.add(signupGradientWrap);
        signupCard.add(Box.createRigidArea(new Dimension(0, 15)));
        signupCard.add(switchToLoginButton);

        panel.add(signupCard);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        
        // Use fixed dimensions for consistent sizing
        int fieldWidth = 520;
        int fieldHeight = 70;
        
        field.setMaximumSize(new Dimension(fieldWidth, fieldHeight));
        field.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(220, 220, 240));
        field.setCaretColor(PRIMARY_COLOR);
        field.setOpaque(true);
        field.setText(placeholder);
        field.setForeground(Color.BLACK); // placeholder color as black

        // Placeholder behavior (clear only when typing begins)
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                if (Boolean.TRUE.equals(active) && field.getText().equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.BLACK);
                }
            }
        });
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Keep placeholder visible on focus; clear on first typing only
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.putClientProperty("placeholderActive", Boolean.TRUE);
                    field.setForeground(Color.BLACK);
                }
            }
        });

        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        
        // Use fixed dimensions for consistent sizing
        int fieldWidth = 520;
        int fieldHeight = 70;
        
        field.setMaximumSize(new Dimension(fieldWidth, fieldHeight));
        field.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(220, 220, 240));
        field.setCaretColor(PRIMARY_COLOR);
        field.setOpaque(true);
        field.setText(placeholder);
        field.setEchoChar((char) 0); // Show placeholder text initially
        field.setForeground(Color.BLACK); // placeholder color as black

        // Placeholder behavior
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        
        // Toggle password visibility with simple click region near right edge
        field.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int eyeX = field.getWidth() - 40;
                int eyeY = (field.getHeight() - 16) / 2;
                if (e.getX() >= eyeX - 5 && e.getX() <= eyeX + 25 && 
                    e.getY() >= eyeY - 5 && e.getY() <= eyeY + 21) {
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
                field.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
            }
        });
        
        // Remove hover motion changes over eye icon
        
        // Add key listener for placeholder behavior
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                Object active = field.getClientProperty("placeholderActive");
                String current = new String(field.getPassword());
                if (Boolean.TRUE.equals(active) && current.equals(placeholder)) {
                    field.setText("");
                    field.putClientProperty("placeholderActive", Boolean.FALSE);
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('•'); // Always hide password by default
                }
            }
        });
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Keep placeholder visible on focus; clear on first key press
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String current = new String(field.getPassword());
                if (current.trim().isEmpty()) {
                    field.setText(placeholder);
                    field.putClientProperty("placeholderActive", Boolean.TRUE);
                    field.setForeground(Color.BLACK);
                    field.setEchoChar((char) 0);
                }
            }
        });

        return field;
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
        
        // Use fixed dimensions for consistent button sizing
        int buttonWidth = 520;
        int buttonHeight = 70;
        
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
        button.setForeground(Color.BLACK);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Enable rollover for hover visuals
        button.setRolloverEnabled(true);
        // Disable hover effect
        
        return button;
    }

    // Google OAuth methods removed - using MySQL authentication only


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

        // Use fixed dimensions for consistent button sizing
        int buttonWidth = 520;
        int buttonHeight = 70;
        
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
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

    // Removed custom GradientPanel in favor of shared components

    // Removed inner GradientTextLabel; using shared com.forgegrid.ui.GradientTextLabel

    private void handleLogin() {
        // Normalize placeholders just in case flags are stale
        Object emailPA = emailField.getClientProperty("placeholderActive");
        if (Boolean.TRUE.equals(emailPA) && "Email".equalsIgnoreCase(emailField.getText().trim())) {
            emailField.setText("");
            emailField.putClientProperty("placeholderActive", Boolean.FALSE);
            emailField.setForeground(Color.BLACK);
        }
        Object passPA = passwordField.getClientProperty("placeholderActive");
        String passTextNow = new String(passwordField.getPassword());
        if (Boolean.TRUE.equals(passPA) && "Password".equalsIgnoreCase(passTextNow.trim())) {
            passwordField.setText("");
            passwordField.putClientProperty("placeholderActive", Boolean.FALSE);
            passwordField.setForeground(Color.BLACK);
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
        
        // Show loading state on button
        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");
        
        // Use MySQL authentication
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
                    
                    if (rememberMeCheckbox.isSelected()) {
                        userPreferences.saveRememberMeCredentials(username, password);
                    } else {
                        userPreferences.clearRememberMe();
                    }
                    
                    boolean hasCompletedOnboarding = userService.hasCompletedOnboardingByUsername(profile.getUsername());
                    
                        showCard("LOADING");
                    if (hasCompletedOnboarding) {
                            createWelcomeBackOnboarding(profile.getUsername());
                    } else {
                            showCard("ONBOARDING_PROMPT");
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
        if (Boolean.TRUE.equals(nPA2) && "Username".equalsIgnoreCase(nameFieldParam.getText().trim())) {
            nameFieldParam.setText("");
            nameFieldParam.putClientProperty("placeholderActive", Boolean.FALSE);
            nameFieldParam.setForeground(Color.BLACK);
        }
        Object ePA2 = emailField.getClientProperty("placeholderActive");
        if (Boolean.TRUE.equals(ePA2) && "Email".equalsIgnoreCase(emailField.getText().trim())) {
            emailField.setText("");
            emailField.putClientProperty("placeholderActive", Boolean.FALSE);
            emailField.setForeground(Color.BLACK);
        }
        Object pPA2 = passwordField.getClientProperty("placeholderActive");
        String passNow = new String(passwordField.getPassword());
        if (Boolean.TRUE.equals(pPA2) && "Password".equalsIgnoreCase(passNow.trim())) {
            passwordField.setText("");
            passwordField.putClientProperty("placeholderActive", Boolean.FALSE);
            passwordField.setForeground(Color.BLACK);
            passwordField.setEchoChar('•');
        }

        String name = nameFieldParam.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Force-deactivate placeholders if user has typed real input
        if (!name.isEmpty() && !"Username".equalsIgnoreCase(name)) {
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
        
        boolean emptyName = name.isEmpty() || (namePlaceholderActive && "Username".equalsIgnoreCase(name));
        boolean emptyEmail = email.isEmpty() || (emailPlaceholderActive && "Email".equalsIgnoreCase(email));
        boolean emptyPass = password.isEmpty() || (passPlaceholderActive && "Password".equalsIgnoreCase(password));

        if (emptyName || emptyEmail || emptyPass) {
            StringBuilder sb = new StringBuilder();
            if (emptyName) sb.append("Username");
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
        
        // Check if username and email are the same
        if (name.equalsIgnoreCase(email)) {
            JOptionPane.showMessageDialog(this, 
                "Username and Email cannot be the same.\nPlease use different values.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show loading state
        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");
        
        // Use MySQL authentication for registration with username, email, and password
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.register(name, email, password);
                
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Account created successfully!\nWelcome to ForgeGrid, " + name + "!\nPlease login with your credentials.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Switch to login panel
                    showLogin();
                    
                    // Reset signup fields to their placeholder state
                    nameFieldParam.setText("Username");
                    nameFieldParam.putClientProperty("placeholderActive", Boolean.TRUE);
                    nameFieldParam.setForeground(Color.BLACK);
                    
                    emailField.setText("Email");
                    emailField.putClientProperty("placeholderActive", Boolean.TRUE);
                    emailField.setForeground(Color.BLACK);
                    
                    passwordField.setText("Password");
                    passwordField.putClientProperty("placeholderActive", Boolean.TRUE);
                    passwordField.setForeground(Color.BLACK);
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
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                
                // Scale the logo proportionally (smaller size to fit screen)
                double scale = calculateProportionalScale();
                int logoWidth = (int) (200 * scale);
                int logoHeight = (int) (120 * scale);
                
                // Ensure reasonable sizes (smaller to prevent overflow)
                logoWidth = Math.max(150, Math.min(200, logoWidth));
                logoHeight = Math.max(90, Math.min(120, logoHeight));
                
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
        } catch (Exception e) { /* ignore L&F issues */ }
        
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
        // Use fixed sizes for consistent button dimensions
        int fieldWidth = 520;
        int fieldHeight = 70;
        int buttonHeight = 70;
        int glassButtonHeight = 70;
        
        // Use fixed font sizes
        int fieldFontSize = 20;
        int buttonFontSize = 22;
        int glassButtonFontSize = 20;
        
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

                // Compact left arrow (filled) to avoid looking like a separator line
                int arrowHeight = Math.min(h - 8, 18);
                int arrowWidth = Math.min(w - 12, 22);
                int topY = (h - arrowHeight) / 2;
                int centerY = h / 2;
                int leftX = 8;
                int rightX = leftX + arrowWidth;

                // Arrow shaft
                int shaftHeight = Math.max(2, arrowHeight / 4);
                int shaftY = centerY - shaftHeight / 2;
                int shaftRight = rightX - (arrowHeight / 2);
                g2d.fillRoundRect(leftX + (arrowHeight / 2) - 1, shaftY, Math.max(4, shaftRight - (leftX + (arrowHeight / 2))), shaftHeight, shaftHeight, shaftHeight);

                // Arrow head (triangle)
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
        // Hide on welcome, onboarding and dashboard; show on auth screens and prompt
        boolean shouldShow = !("WELCOME".equals(currentCard) || "ONBOARDING".equals(currentCard) || "ONBOARDING_PROMPT".equals(currentCard) || "DASHBOARD".equals(currentCard));
        backButton.setVisible(shouldShow);
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
    // Removed unused addLinkHoverAnimation helper
    
    /**
     * Simplified eye icon animation
     */
    
    
    
    
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
    // Removed unused initializeAuthentication helper
    
    
    /**
     * Show the SIGNUP card reliably and focus the first field.
     * Ensures the signup panel exists and triggers layout refresh.
     */
    private void showSignup() {
        SwingUtilities.invokeLater(() -> {
            // Use the standard showCard method like all other navigation
            showCard("SIGNUP");
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


    /**
     * Create onboarding panel for new users (shows questions)
     */
    private void createNewUserOnboarding() {
        OnboardingInAppPanel onboarding = new OnboardingInAppPanel((goal, language, skill) -> {
            // Save onboarding data to database
            if (currentProfile != null && currentProfile.getUsername() != null) {
                boolean saved = userService.saveOnboardingDataByUsername(
                    currentProfile.getUsername(), 
                    goal, 
                    language, 
                    skill
                );
                
                if (saved) {
                    // Update the current profile with onboarding data
                    currentProfile.setOnboardingCompleted(true);
                    currentProfile.setOnboardingGoal(goal);
                    currentProfile.setOnboardingLanguage(language);
                    currentProfile.setOnboardingSkill(skill);
                }
            }
            
            // After onboarding, open Dashboard within the same window
            openDashboardInCard(goal, language, skill);
        }, true, currentProfile != null ? currentProfile.getUsername() : null); // isNewUser = true, pass username
        
        // Replace the placeholder onboarding panel
        cardPanel.removeAll();
        addWithFade(loginPanel, "LOGIN");
        addWithFade(signupPanel, "SIGNUP");
        addWithFade(welcomePanel, "WELCOME");
        addWithFade(onboarding, "ONBOARDING");
        addWithFade(onboardingPrompt, "ONBOARDING_PROMPT");
        addWithFade(loadingScreen, "LOADING");
        
        showCard("ONBOARDING");
    }

    /**
     * Create onboarding panel for returning users (shows welcome back message)
     */
    private void createWelcomeBackOnboarding(String username) {
        OnboardingInAppPanel onboarding = new OnboardingInAppPanel((goal, language, skill) -> {
            // Load existing onboarding data from database
            String[] onboardingData = userService.getOnboardingDataByUsername(username);
            String existingGoal = onboardingData != null ? onboardingData[0] : null;
            String existingLanguage = onboardingData != null ? onboardingData[1] : null;
            String existingSkill = onboardingData != null ? onboardingData[2] : null;
            
            // Go directly to dashboard with existing data
            openDashboardInCard(existingGoal, existingLanguage, existingSkill);
        }, false, username); // isNewUser = false
        
        // Replace the placeholder onboarding panel
        cardPanel.removeAll();
        addWithFade(loginPanel, "LOGIN");
        addWithFade(signupPanel, "SIGNUP");
        addWithFade(welcomePanel, "WELCOME");
        addWithFade(onboarding, "ONBOARDING");
        addWithFade(onboardingPrompt, "ONBOARDING_PROMPT");
        addWithFade(loadingScreen, "LOADING");
        
        showCard("ONBOARDING");
    }

    private JPanel createOnboardingPromptPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(25, 35, 55));
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

        yesBtn.addActionListener(e -> {
            // Create new user onboarding panel
            createNewUserOnboarding();
        });
        skipBtn.addActionListener(e -> {
            // Mark onboarding as completed (skipped) in database
            if (currentProfile != null && currentProfile.getUsername() != null) {
                boolean saved = userService.saveOnboardingDataByUsername(
                    currentProfile.getUsername(), 
                    "Skipped", 
                    "Not specified", 
                    "Not specified"
                );
                
                if (saved) {
                    // Update the current profile
                    currentProfile.setOnboardingCompleted(true);
                    currentProfile.setOnboardingGoal("Skipped");
                    currentProfile.setOnboardingLanguage("Not specified");
                    currentProfile.setOnboardingSkill("Not specified");
                } else { /* no-op */ }
            }
            
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

}
