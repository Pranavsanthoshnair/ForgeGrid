package com.forgegrid.ui;

import com.forgegrid.managers.GameManager;
import com.forgegrid.utils.AnimationUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton exitButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private GameManager gameManager;
    
    public AuthUI() {
        this.gameManager = GameManager.getInstance();
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
        
        // Set the window icon to your custom logo
        try {
            java.net.URL iconUrl = getClass().getResource("/com/forgegrid/icon/logo.jpg");
            if (iconUrl == null) {
                iconUrl = getClass().getResource("/com/forgegrid/icon/logo.png");
            }
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                // Scale the icon to a smaller size for the title bar
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
        
        // Create login and signup panels
        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();
        
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(signupPanel, "SIGNUP");
        
        add(cardPanel);
        
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
    
    private JPanel createLoginPanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Calculate proportional padding based on frame size
        double scale = calculateProportionalScale();
        int padding = (int) (80 * scale);
        padding = Math.max(30, Math.min(120, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        // Modern tagline with enhanced styling
        JLabel mainTagline = new JLabel("Where coding challenges become milestones");
        mainTagline.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        mainTagline.setForeground(new Color(255, 215, 0)); // Golden yellow
        mainTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create the logo panel
        JPanel logoPanel = createLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
panel.add(Box.createVerticalGlue());
panel.add(logoPanel);
panel.add(Box.createRigidArea(new Dimension(0, 10)));
panel.add(mainTagline);
        // Modern title with enhanced styling
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Increased height
        
        JLabel welcomePart = new JLabel("Welcome to ");
        welcomePart.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomePart.setForeground(new Color(240, 240, 240));
        
        GradientTextLabel brandPart = new GradientTextLabel(
            "ForgeGrid",
            new Font("Segoe UI", Font.BOLD, 38),
            new Color(255, 215, 0), // golden yellow
            new Color(255, 105, 180)  // hot pink
        );
        
        // Create a container panel to hold both labels
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleContainer.setOpaque(false);
        titleContainer.add(welcomePart);
        titleContainer.add(brandPart);
        
        titleRow.add(titleContainer);
        
        
        // Form fields
        emailField = createModernTextField("Email");
        passwordField = createModernPasswordField("Password");
        
        loginButton = createGradientButton("Login", new Color(138, 43, 226), new Color(168, 85, 247));
        JButton switchToSignupButton = createSolidButton("New User? Sign Up", Color.WHITE, Color.BLACK);
        exitButton = createGradientButton("Exit", new Color(239, 68, 68), new Color(220, 38, 127));
        
        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        switchToSignupButton.addActionListener(e -> cardLayout.show(cardPanel, "SIGNUP"));
        exitButton.addActionListener(e -> System.exit(0));
        
        // Layout with better spacing
        panel.add(Box.createVerticalGlue());
        panel.add(logoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(titleRow);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(mainTagline);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(passwordField);
        
        // Add "Forgot Password?" link
        JLabel forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPasswordLink.setForeground(new Color(255, 215, 0));
        forgotPasswordLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect for forgot password link
        forgotPasswordLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                forgotPasswordLink.setForeground(new Color(255, 255, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                forgotPasswordLink.setForeground(new Color(255, 215, 0));
            }
        });
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(forgotPasswordLink);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(switchToSignupButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(exitButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Calculate proportional padding based on frame size
        double scale = calculateProportionalScale();
        int padding = (int) (60 * scale);
        padding = Math.max(25, Math.min(100, padding));
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        JLabel titleLabel = new JLabel("Join ForgeGrid");
        titleLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Start your productivity journey");
        subtitleLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields
        nameField = createModernTextField("Full Name");
        JTextField signupEmailField = createModernTextField("Email");
        JPasswordField signupPasswordField = createModernPasswordField("Password");
        
        signupButton = createGradientButton("Sign Up", new Color(34, 197, 94), new Color(59, 130, 246));
        JButton switchToLoginButton = createGlassButton("Already have an account? Login");
        
        // Add action listeners
        signupButton.addActionListener(e -> handleSignup(signupEmailField, signupPasswordField));
        switchToLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        // Layout
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 35)));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(signupEmailField);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(signupPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(signupButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(switchToLoginButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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
        field.setCaretColor(new Color(255, 215, 0));
        field.setOpaque(false);
        field.setText(placeholder);

        // Placeholder behavior
        field.putClientProperty("placeholderActive", Boolean.TRUE);
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
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
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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
        field.setCaretColor(new Color(255, 215, 0));
        field.setOpaque(false);
        field.setText(placeholder);
        field.setEchoChar((char) 0);

        // Placeholder behavior
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
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Modern gradient with enhanced colors
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Add subtle inner glow
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 2, 18, 18);
                
                // Text with shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 + 1;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + 1;
                g2d.drawString(getText(), x, y);
                
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x - 1, y - 1);
                
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
                g2d.setColor(new Color(255, 255, 255, 5));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border with gradient effect
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                
                // Inner highlight
                g2d.setColor(new Color(255, 255, 255, 10));
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
        
        // Add subtle hover effect
        addButtonHoverEffect(button);
        
        return button;
    }

    private JButton createSolidButton(String text, Color backgroundColor, Color foregroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

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
        
        // Add subtle hover effect
        addButtonHoverEffect(button);
        
        return button;
    }

    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Modern dark gradient with subtle depth
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(8, 20, 40), // deeper dark navy
                getWidth(), getHeight(), new Color(15, 30, 55) // slightly lighter with more blue
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Add subtle radial gradient overlay for depth
            RadialGradientPaint radialGradient = new RadialGradientPaint(
                getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{new Color(255, 255, 255, 5), new Color(255, 255, 255, 2), new Color(0, 0, 0, 0)}
            );
            g2d.setPaint(radialGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.dispose();
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
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty() || email.equals("Email") || email.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Placeholder for actual authentication
        boolean loginSuccess = true; // Replace with actual authentication
        
        if (loginSuccess) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Proceed to main application
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSignup(JTextField emailField, JPasswordField passwordField) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || 
            name.equals("Full Name") || email.equals("Email") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Placeholder for actual registration
        boolean registrationSuccess = true; // Replace with actual registration
        
        if (registrationSuccess) {
            JOptionPane.showMessageDialog(this, "Account created successfully! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(cardPanel, "LOGIN");
            // Clear signup fields
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "An account with this email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        
        try {
            // Try to load the logo from resources (try .jpg first, then .png)
            java.net.URL logoUrl = getClass().getResource("/com/forgegrid/icon/logo.jpg");
            System.out.println("Logo URL: " + logoUrl); // Debug output
            if (logoUrl == null) {
                logoUrl = getClass().getResource("/com/forgegrid/icon/logo.png");
                System.out.println("PNG Logo URL: " + logoUrl); // Debug output
            }
            if (logoUrl != null) {
                System.out.println("Logo found! Loading..."); // Debug output
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                
                // Scale the logo proportionally
                double scale = calculateProportionalScale();
                int logoWidth = (int) (120 * scale);
                int logoHeight = (int) (80 * scale);
                
                // Ensure minimum sizes
                logoWidth = Math.max(80, logoWidth);
                logoHeight = Math.max(50, logoHeight);
                
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
                // Fallback to gaming controller if logo not found
                logoPanel = new GamingControllerLogo();
            }
        } catch (Exception e) {
            // Fallback to gaming controller if there's any error loading the logo
            logoPanel = new GamingControllerLogo();
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
        int buttonHeight = (int) (70 * scale);
        int glassButtonHeight = (int) (65 * scale);
        
        // Ensure minimum sizes
        fieldWidth = Math.max(250, fieldWidth);
        fieldHeight = Math.max(50, fieldHeight);
        buttonHeight = Math.max(50, buttonHeight);
        glassButtonHeight = Math.max(45, glassButtonHeight);
        
        // Calculate proportional font sizes
        int fieldFontSize = Math.max(14, (int) (20 * scale));
        int buttonFontSize = Math.max(16, (int) (22 * scale));
        int glassButtonFontSize = Math.max(14, (int) (20 * scale));
        
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
        
        if (exitButton != null) {
            Dimension buttonSize = new Dimension(fieldWidth, buttonHeight);
            exitButton.setMaximumSize(buttonSize);
            exitButton.setPreferredSize(buttonSize);
            exitButton.setFont(new Font("Trebuchet MS", Font.BOLD, buttonFontSize));
        }
        
        // Update all other buttons in the panels
        updateAllButtonSizes(fieldWidth, buttonHeight, glassButtonHeight, buttonFontSize, glassButtonFontSize);
        
        // Refresh the layout
        revalidate();
        repaint();
    }
    
    private void updateAllButtonSizes(int buttonWidth, int buttonHeight, int glassButtonHeight, 
                                   int buttonFontSize, int glassButtonFontSize) {
        // Update all buttons in the card panel
        Component[] components = cardPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updateButtonsInPanel((JPanel) comp, buttonWidth, buttonHeight, glassButtonHeight, 
                                   buttonFontSize, glassButtonFontSize);
            }
        }
    }
    
    private void updateButtonsInPanel(JPanel panel, int buttonWidth, int buttonHeight, 
                                     int glassButtonHeight, int buttonFontSize, int glassButtonFontSize) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();
                
                // Determine button type and apply appropriate sizing
                if (text.contains("Sign Up") || text.contains("Login") || text.contains("Exit")) {
                    button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                    button.setFont(new Font("Trebuchet MS", Font.BOLD, buttonFontSize));
                } else {
                    // Glass buttons (like "New User? Sign Up", "Already have an account? Login")
                    button.setMaximumSize(new Dimension(buttonWidth, glassButtonHeight));
                    button.setPreferredSize(new Dimension(buttonWidth, glassButtonHeight));
                    button.setFont(new Font("Trebuchet MS", Font.PLAIN, glassButtonFontSize));
                }
            } else if (comp instanceof JPanel) {
                // Recursively update buttons in nested panels
                updateButtonsInPanel((JPanel) comp, buttonWidth, buttonHeight, glassButtonHeight, 
                                   buttonFontSize, glassButtonFontSize);
            }
        }
    }
    
    
    private void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer hoverTimer;
            private float scale = 1.0f;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (hoverTimer != null) hoverTimer.stop();
                
                hoverTimer = new Timer(16, new ActionListener() {
                    private int elapsed = 0;
                    
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        elapsed += 16;
                        float progress = Math.min(1.0f, (float) elapsed / 200); // 200ms transition
                        
                        scale = 1.0f + (progress * 0.05f); // 5% scale increase
                        button.repaint();
                        
                        if (progress >= 1.0f) {
                            hoverTimer.stop();
                        }
                    }
                });
                hoverTimer.start();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (hoverTimer != null) hoverTimer.stop();
                
                hoverTimer = new Timer(16, new ActionListener() {
                    private int elapsed = 0;
                    
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        elapsed += 16;
                        float progress = Math.min(1.0f, (float) elapsed / 200); // 200ms transition
                        
                        scale = 1.05f - (progress * 0.05f); // Return to normal scale
                        button.repaint();
                        
                        if (progress >= 1.0f) {
                            hoverTimer.stop();
                        }
                    }
                });
                hoverTimer.start();
            }
        });
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
    
    // Custom Gaming Controller Logo Component
    private static class GamingControllerLogo extends JPanel {
        private static final int LOGO_WIDTH = 80;
        private static final int LOGO_HEIGHT = 50;
        
        public GamingControllerLogo() {
            setPreferredSize(new Dimension(LOGO_WIDTH, LOGO_HEIGHT));
            setMaximumSize(new Dimension(LOGO_WIDTH, LOGO_HEIGHT));
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            // Draw controller body (yellow with texture)
            g2d.setColor(new Color(255, 215, 0)); // Bright yellow
            g2d.fillRoundRect(centerX - 80, centerY - 30, 160, 60, 20, 20);
            
            // Add texture strokes
            g2d.setColor(new Color(255, 182, 193, 100)); // Pink texture
            g2d.fillRoundRect(centerX - 75, centerY - 25, 150, 50, 15, 15);
            
            g2d.setColor(new Color(70, 130, 180, 100)); // Blue texture
            g2d.fillRoundRect(centerX - 70, centerY - 20, 140, 40, 10, 10);
            
            // Draw D-pad (left side - pink)
            g2d.setColor(new Color(255, 105, 180)); // Hot pink
            g2d.fillRoundRect(centerX - 65, centerY - 15, 25, 25, 5, 5);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("+", centerX - 58, centerY + 2);
            
            // Draw action buttons (right side - pink)
            g2d.setColor(new Color(255, 105, 180)); // Hot pink
            int buttonSize = 12;
            int buttonSpacing = 15;
            int startX = centerX + 25;
            int startY = centerY - 15;
            
            // Top button
            g2d.fillOval(startX, startY, buttonSize, buttonSize);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(startX + 4, startY + 4, 4, 4);
            
            // Right button
            g2d.setColor(new Color(255, 105, 180));
            g2d.fillOval(startX + buttonSpacing, startY + buttonSpacing, buttonSize, buttonSize);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(startX + buttonSpacing + 4, startY + buttonSpacing + 4, 4, 4);
            
            // Bottom button
            g2d.setColor(new Color(255, 105, 180));
            g2d.fillOval(startX, startY + buttonSpacing * 2, buttonSize, buttonSize);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(startX + 4, startY + buttonSpacing * 2 + 4, 4, 4);
            
            // Left button
            g2d.setColor(new Color(255, 105, 180));
            g2d.fillOval(startX - buttonSpacing, startY + buttonSpacing, buttonSize, buttonSize);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(startX - buttonSpacing + 4, startY + buttonSpacing + 4, 4, 4);
            
            // Draw center screen (dark blue)
            g2d.setColor(new Color(25, 25, 112)); // Dark blue
            g2d.fillRoundRect(centerX - 20, centerY - 25, 40, 15, 5, 5);
            
            // Draw center buttons (light blue)
            g2d.setColor(new Color(135, 206, 250)); // Light sky blue
            g2d.fillOval(centerX - 8, centerY + 5, 8, 8);
            g2d.fillOval(centerX + 2, centerY + 5, 8, 8);
            
            // Draw coding symbols above controller
            drawCodingSymbols(g2d, centerX, centerY - 50);
            
            g2d.dispose();
        }
        
        private void drawCodingSymbols(Graphics2D g2d, int centerX, int y) {
            int symbolSize = 20;
            int spacing = 25;
            int startX = centerX - (spacing * 2);
            
            // Symbol 1: {/}
            g2d.setColor(new Color(70, 130, 180)); // Steel blue
            g2d.fillOval(startX, y, symbolSize, symbolSize);
            g2d.setColor(new Color(255, 105, 180)); // Pink border
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(startX, y, symbolSize, symbolSize);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Courier New", Font.BOLD, 12));
            g2d.drawString("{/}", startX + 2, y + 14);
            
            // Symbol 2: </>  
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillOval(startX + spacing, y, symbolSize, symbolSize);
            g2d.setColor(new Color(255, 105, 180));
            g2d.drawOval(startX + spacing, y, symbolSize, symbolSize);
            g2d.setColor(Color.WHITE);
            g2d.drawString("</>", startX + spacing + 2, y + 14);
            
            // Symbol 3: >_
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillOval(startX + spacing * 2, y, symbolSize, symbolSize);
            g2d.setColor(new Color(255, 105, 180));
            g2d.drawOval(startX + spacing * 2, y, symbolSize, symbolSize);
            g2d.setColor(Color.WHITE);
            g2d.drawString(">_", startX + spacing * 2 + 2, y + 14);
            
            // Symbol 4: *
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillOval(startX + spacing * 3, y, symbolSize, symbolSize);
            g2d.setColor(new Color(255, 105, 180));
            g2d.drawOval(startX + spacing * 3, y, symbolSize, symbolSize);
            g2d.setColor(Color.WHITE);
            g2d.drawString("*", startX + spacing * 3 + 6, y + 14);
        }
    }
}
