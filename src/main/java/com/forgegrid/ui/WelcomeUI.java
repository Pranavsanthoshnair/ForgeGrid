package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Welcome screen with logo + title + "Let's start" button,
 */
public class WelcomeUI extends JPanel {

    // Colors centralized in Theme
    
    private final String[] quotes = {
        "Every bug is a step closer to mastery.",
        "Code. Compile. Conquer.",
        "Small commits, big milestones.",
        "One challenge at a time.",
        "Debugging is where real learning happens.",
        "Your next line of code is a new achievement.",
        "Consistency beats intensity.",
        "Dream in code, live in milestones.",
        "Challenges are opportunities in disguise.",
        "Forge your skills, one problem at a time."
    };

    private final JButton startButton;

    public WelcomeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        NeonBackgroundPanel background = new NeonBackgroundPanel();
        background.setLayout(new GridBagLayout());
        background.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel logoPanel = createLogoPanel(1.45);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, -8, 0));

        GradientTextLabel title = new GradientTextLabel("ForgeGrid");
        title.setFont(getStylishFont("Montserrat", Font.BOLD, 58, new String[]{"Poppins","Segoe UI","Trebuchet MS"}));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setGradient(Theme.BRAND_YELLOW, Theme.BRAND_PINK);
        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(title);
        titleWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        startButton = new JButton("Let's start");
        Theme.stylePrimaryButton(startButton);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        double s = calculateProportionalScale();
        startButton.setFont(getStylishFont("Montserrat", Font.BOLD, Math.max(18, (int)(22 * s)), new String[]{"Poppins","Segoe UI","Trebuchet MS"}));
        int buttonWidth = Math.max(250, (int)(520 * s));
        int buttonHeight = Math.max(50, (int)(70 * s));
        startButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        startButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        JComponent buttonWrap = Theme.asGradientButton(startButton, Theme.BRAND_YELLOW, Theme.BRAND_GOLD, 26);
        buttonWrap.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        buttonWrap.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        // Create a combined hero panel (logo + title + tagline) inside a glass card
        JPanel heroPanel = new CardContainerPanel();
        heroPanel.setOpaque(false);
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.add(logoPanel);
        titleWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(titleWrap);

        JLabel tagline = new JLabel("where coding challenges become milestones");
        tagline.setFont(getStylishFont("Segoe UI", Font.ITALIC, 20, new String[]{"Poppins","Trebuchet MS","SansSerif"}));
        tagline.setForeground(Theme.TEXT_SECONDARY);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        heroPanel.add(tagline);

        heroPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        heroPanel.add(buttonWrap);

        // Create rotating quotes panel
        RotatingQuotesPanel quotesPanel = new RotatingQuotesPanel();
        
        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.weightx = 1;
        rootGbc.weighty = 1;
        rootGbc.anchor = GridBagConstraints.CENTER;
        background.add(heroPanel, rootGbc);
        
        // Create a container for the quotes panel positioned as overlay at bottom
        JPanel quotesContainer = new JPanel(new BorderLayout());
        quotesContainer.setOpaque(false);
        quotesContainer.add(quotesPanel, BorderLayout.CENTER);
        quotesContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0)); // Bottom padding
        
        GridBagConstraints quotesGbc = new GridBagConstraints();
        quotesGbc.gridx = 0;
        quotesGbc.gridy = 1;
        quotesGbc.weightx = 1;
        quotesGbc.weighty = 0;
        quotesGbc.anchor = GridBagConstraints.CENTER;
        quotesGbc.fill = GridBagConstraints.HORIZONTAL;
        background.add(quotesContainer, quotesGbc);

        FadeInPanel fade = new FadeInPanel(new BorderLayout());
        fade.add(background, BorderLayout.CENTER);
        add(fade, BorderLayout.CENTER);
        SwingUtilities.invokeLater(fade::play);
    }

    public void addStartActionListener(ActionListener l) {
        startButton.addActionListener(l);
    }

    private JPanel createLogoPanel(double multiplier) {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        try {
            java.net.URL logoUrl = getClass().getResource("/com/forgegrid/icon/logo2_transparent.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                double scale = calculateProportionalScale() * multiplier;
                int logoWidth = Math.max(260, (int)(360 * scale));
                int logoHeight = Math.max(160, (int)(220 * scale));
                Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledLogo);
                JLabel logoLabel = new JLabel(scaledIcon);
                logoLabel.setHorizontalAlignment(JLabel.CENTER);
                logoPanel.add(logoLabel, BorderLayout.CENTER);
                logoPanel.setPreferredSize(new Dimension(logoWidth, logoHeight));
                logoPanel.setMaximumSize(new Dimension(logoWidth, logoHeight));
            }
        } catch (Exception ignore) {}
        return logoPanel;
    }

    private Font getStylishFont(String primary, int style, int size, String[] fallbacks) {
        try {
            Font f = new Font(primary, style, size);
            if (f != null) return f;
        } catch (Exception ignore) {}
        if (fallbacks != null) {
            for (String name : fallbacks) {
                try {
                    Font f = new Font(name, style, size);
                    if (f != null) return f;
                } catch (Exception ignore) {}
            }
        }
        return new Font("SansSerif", style, size);
    }

    private double calculateProportionalScale() {
        double baseWidth = 800.0;
        double baseHeight = 900.0;
        double currentWidth = Math.max(1, getWidth());
        double currentHeight = Math.max(1, getHeight());
        double widthScale = Math.max(0.5, Math.min(1.5, currentWidth / baseWidth));
        double heightScale = Math.max(0.5, Math.min(1.5, currentHeight / baseHeight));
        return Math.min(widthScale, heightScale);
    }
    
    /**
     * Inner class for displaying rotating motivational quotes with fade animations
     */
    private class RotatingQuotesPanel extends JPanel {
        private JLabel quoteLabel;
        private Timer quoteTimer;
        private int currentQuoteIndex = 0;
        
        public RotatingQuotesPanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            
            // Create quote label with subtle styling
            quoteLabel = new JLabel(quotes[0], JLabel.CENTER);
            quoteLabel.setFont(getStylishFont("Segoe UI", Font.ITALIC, 16, new String[]{"Poppins","Trebuchet MS","SansSerif"}));
            quoteLabel.setForeground(new Color(Theme.TEXT_SECONDARY.getRed(), Theme.TEXT_SECONDARY.getGreen(), Theme.TEXT_SECONDARY.getBlue(), 180)); // Semi-transparent
            
            // Apply subtle glow effect using Theme colors
            quoteLabel.putClientProperty("FlatLaf.style", "color: $Text.accentForeground");
            
            add(quoteLabel, BorderLayout.CENTER);
            
            // Start the rotation timer
            startQuoteRotation();
        }
        
        private void startQuoteRotation() {
            quoteTimer = new Timer(true); // Daemon timer
            
            quoteTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        // Fade out
                        fadeOut(() -> {
                            // Change quote
                            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.length;
                            quoteLabel.setText(quotes[currentQuoteIndex]);
                            
                            // Fade in
                            fadeIn();
                        });
                    });
                }
            }, 4000, 4000); // Start after 4 seconds, repeat every 4 seconds
        }
        
        private void fadeOut(Runnable onComplete) {
            Timer fadeTimer = new Timer(true);
            float[] alpha = {1.0f};
            
            fadeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        alpha[0] -= 0.05f;
                        if (alpha[0] <= 0) {
                            alpha[0] = 0;
                            fadeTimer.cancel();
                            onComplete.run();
                        }
                        
                        Color currentColor = quoteLabel.getForeground();
                        Color newColor = new Color(
                            currentColor.getRed(), 
                            currentColor.getGreen(), 
                            currentColor.getBlue(), 
                            (int)(180 * alpha[0])
                        );
                        quoteLabel.setForeground(newColor);
                    });
                }
            }, 16, 16); // Approximately 60 FPS
        }
        
        private void fadeIn() {
            Timer fadeTimer = new Timer(true);
            float[] alpha = {0.0f};
            
            fadeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        alpha[0] += 0.05f;
                        if (alpha[0] >= 1) {
                            alpha[0] = 1;
                            fadeTimer.cancel();
                        }
                        
                        Color currentColor = quoteLabel.getForeground();
                        Color newColor = new Color(
                            currentColor.getRed(), 
                            currentColor.getGreen(), 
                            currentColor.getBlue(), 
                            (int)(180 * alpha[0])
                        );
                        quoteLabel.setForeground(newColor);
                    });
                }
            }, 16, 16); // Approximately 60 FPS
        }
        
        // Cleanup method to stop timer when panel is removed
        public void cleanup() {
            if (quoteTimer != null) {
                quoteTimer.cancel();
            }
        }
    }
}


