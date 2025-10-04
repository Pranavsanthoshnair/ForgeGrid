package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
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
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
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

        // Center the hero panel
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(heroPanel);
        
        // Create rotating quotes panel
        RotatingQuotesPanel quotesPanel = new RotatingQuotesPanel();
        JPanel quotesWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        quotesWrapper.setOpaque(false);
        quotesWrapper.add(quotesPanel);
        
        // Add all components to background using vertical BoxLayout
        background.add(Box.createVerticalGlue());
        centerWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        background.add(centerWrapper);
        background.add(Box.createRigidArea(new Dimension(0, 30)));
        quotesWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        background.add(quotesWrapper);
        background.add(Box.createVerticalGlue());

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
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            setOpaque(false);
            
            // Create quote label with enhanced visibility and subtle glow
            quoteLabel = new GlowingQuotesLabel(quotes[0], JLabel.CENTER);
            quoteLabel.setFont(getStylishFont("Segoe UI", Font.BOLD | Font.ITALIC, 18, new String[]{"Poppins","Trebuchet MS","SansSerif"}));
            
            // Use brand colors for better visibility
            quoteLabel.setForeground(Theme.BRAND_YELLOW);
            quoteLabel.setPreferredSize(new Dimension(600, 30));
            quoteLabel.setHorizontalAlignment(JLabel.CENTER);
            
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
    
    /**
     * Custom label with subtle glow effect for quotes
     */
    private static class GlowingQuotesLabel extends JLabel {
        
        public GlowingQuotesLabel(String text, int alignment) {
            super(text, alignment);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            String text = getText();
            if (text == null || text.isEmpty()) {
                g2d.dispose();
                return;
            }
            
            // Get actual text bounds for accurate centering
            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D textBounds = fm.getStringBounds(text, g2d);
            g2d.setFont(getFont());
            
            Dimension size = getSize();
            int textWidth = (int) textBounds.getWidth();
            int textHeight = (int) textBounds.getHeight();
            
            // Calculate centered position
            int textX = (size.width - textWidth) / 2;
            int textY = (size.height - fm.getDescent()) / 2 + fm.getAscent() / 2;
            
            // Draw subtle text shadow for glow effect
            Color shadowColor = new Color(0, 0, 0, 80);
            g2d.setColor(shadowColor);
            g2d.drawString(text, textX + 1, textY + 1);
            
            // Draw main text
            g2d.setColor(getForeground());
            g2d.drawString(text, textX, textY);
            
            g2d.dispose();
        }
    }
}


