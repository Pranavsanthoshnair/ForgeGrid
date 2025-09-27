package com.forgegrid.ui;

import com.forgegrid.managers.GameManager;
import javax.swing.*;
import java.awt.*;

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
        setSize(800, 900);  // Increased frame size
        setLocationRelativeTo(null);
        setResizable(true);  // Allow resizing
        
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
        
        // Apply custom styling
        applyCustomStyling();
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 100, 80, 100));  // Increased padding
        
        // Title with gradient text
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel welcomePart = new JLabel("Welcome to ");
        welcomePart.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomePart.setForeground(Color.WHITE);
        
        GradientTextLabel brandPart = new GradientTextLabel(
            "ForgeGrid",
            new Font("Serif", Font.BOLD, 42),
            new Color(255, 221, 0), // yellow
            new Color(236, 72, 153)  // pink
        );
        
        // Create a container panel to hold both labels
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleContainer.setOpaque(false);
        titleContainer.add(welcomePart);
        titleContainer.add(brandPart);
        
        titleRow.add(titleContainer);
        
        JLabel subtitleLabel = new JLabel("Level up your productivity");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));  // Larger font
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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
        panel.add(titleRow);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
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
        panel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        
        JLabel titleLabel = new JLabel("Join ForgeGrid");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Start your productivity journey");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(520, 70));  // Larger field
        field.setPreferredSize(new Dimension(520, 70));  // Larger field
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20));  // Larger font
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setBackground(new Color(255, 255, 255, 20));
        field.setForeground(new Color(200, 200, 220));
        field.setCaretColor(Color.WHITE);
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
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(520, 70));  // Larger field
        field.setPreferredSize(new Dimension(520, 70));  // Larger field
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20));  // Larger font
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setBackground(new Color(255, 255, 255, 20));
        field.setForeground(new Color(200, 200, 220));
        field.setCaretColor(Color.WHITE);
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
                
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
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
        
        button.setMaximumSize(new Dimension(520, 70));  // Larger button
        button.setPreferredSize(new Dimension(520, 70));  // Larger button
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));  // Larger font
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }
    
    private JButton createGlassButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setMaximumSize(new Dimension(520, 65));  // Larger button
        button.setPreferredSize(new Dimension(520, 65));  // Larger button
        button.setFont(new Font("Segoe UI", Font.PLAIN, 20));  // Larger font
        button.setForeground(new Color(200, 200, 220));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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

        button.setMaximumSize(new Dimension(320, 45));
        button.setPreferredSize(new Dimension(320, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(foregroundColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dark blue gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(10, 25, 47), // dark navy
                getWidth(), getHeight(), new Color(17, 34, 64) // slightly lighter dark blue
            );
            g2d.setPaint(gradient);
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
    
    private void applyCustomStyling() {
        // Set larger default font for all components
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 18);
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
}
