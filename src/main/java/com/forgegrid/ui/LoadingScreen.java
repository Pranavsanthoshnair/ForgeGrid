package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modern loading screen component with animated spinner and gradient effects
 */
public class LoadingScreen extends JPanel {
    
    private JLabel statusLabel;
    private JLabel brandLabel;
    private JLabel taglineLabel;
    private Timer spinnerTimer;
    private Timer pulseTimer;
    private Timer symbolTimer;
    private Timer fadeTimer;
    private int spinnerFrame = 0;
    private int pulseFrame = 0;
    private final String[] spinnerFrames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
    private final String[] loadingMessages = {
        "Initializing ForgeGrid...",
        "Loading your coding journey...",
        "Preparing challenges...",
        "Setting up achievements...",
        "Almost ready..."
    };
    private int messageIndex = 0;
    
    // Floating symbols state
    private static class FloatingSymbol {
        String text;
        float x;
        float y;
        float vx;
        float vy;
        float alpha; // 0..1
        float size;  // font size
    }
    private java.util.List<FloatingSymbol> symbols = new java.util.ArrayList<>();
    private final String[] javaSymbols = {";", ",", ":", "{", "}", "(", ")", "<", ">", "%", "&"};
    
    // Optional background image
    private Image backgroundImage;
    
    // Fade overlay state (255 = fully covered, 0 = fully visible)
    private int overlayAlpha = 255;
    private boolean isFadingIn = true;
    
    public LoadingScreen() {
        initializeUI();
        startAnimations();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 20, 30));
        
        // Try to load optional background image (jpg/png)
        try {
            java.net.URL bgUrl = getClass().getResource("/com/forgegrid/icon/splash_bg.jpg");
            if (bgUrl == null) {
                bgUrl = getClass().getResource("/com/forgegrid/icon/splash_bg.png");
            }
            if (bgUrl != null) {
                backgroundImage = new ImageIcon(bgUrl).getImage();
            }
        } catch (Exception ignore) { }
        
        // Create main container with gradient background
        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background (image if available, else gradient)
                if (backgroundImage != null) {
                    // Cover entire area preserving aspect ratio
                    int w = getWidth();
                    int h = getHeight();
                    if (w > 0 && h > 0) {
                        double sx = (double) w / backgroundImage.getWidth(null);
                        double sy = (double) h / backgroundImage.getHeight(null);
                        double scale = Math.max(sx, sy);
                        int drawW = (int) (backgroundImage.getWidth(null) * scale);
                        int drawH = (int) (backgroundImage.getHeight(null) * scale);
                        int x = (w - drawW) / 2;
                        int y = (h - drawH) / 2;
                        g2d.drawImage(backgroundImage, x, y, drawW, drawH, null);
                        // Darken with translucent overlay for readability
                        g2d.setColor(new Color(0, 0, 0, 120));
                        g2d.fillRect(0, 0, w, h);
                    }
                } else {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(15, 20, 30),
                        0, getHeight(), new Color(25, 35, 55)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Add animated background elements
                drawAnimatedBackground(g2d);
                
                // Fade overlay (for fade-in/out transitions)
                if (overlayAlpha > 0) {
                    g2d.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, overlayAlpha / 255f)));
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                
                g2d.dispose();
            }
        };
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        
        // Brand label with subtle glow
        brandLabel = new JLabel("ForgeGrid", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                String text = getText();
                Font font = getFont();
                g2d.setFont(font);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 6;
                // Glow layers
                for (int i = 8; i >= 1; i--) {
                    g2d.setColor(new Color(255, 215, 0, 12));
                    g2d.setStroke(new BasicStroke(i));
                    g2d.drawString(text, x, y);
                }
                // Main text
                g2d.setColor(new Color(255, 215, 0));
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
        };
        brandLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 48));
        brandLabel.setForeground(new Color(255, 215, 0));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Tagline label
        taglineLabel = new JLabel("Where coding challenges become milestones", JLabel.CENTER);
        taglineLabel.setFont(new Font("Trebuchet MS", Font.ITALIC, 16));
        taglineLabel.setForeground(new Color(135, 206, 250)); // Light blue
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        
        // Animated spinner container
        JPanel spinnerContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw pulsing circle around spinner
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = 30 + (int)(Math.sin(pulseFrame * 0.1) * 5);
                
                // Outer glow
                g2d.setColor(new Color(255, 215, 0, 30));
                g2d.fillOval(centerX - radius - 10, centerY - radius - 10, 
                           (radius + 10) * 2, (radius + 10) * 2);
                
                // Inner circle
                g2d.setColor(new Color(255, 215, 0, 60));
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                
                g2d.dispose();
            }
        };
        spinnerContainer.setOpaque(false);
        spinnerContainer.setPreferredSize(new Dimension(100, 100));
        spinnerContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Spinner label (rotating icon style)
        JLabel spinnerLabel = new JLabel("⠋", JLabel.CENTER);
        spinnerLabel.setFont(new Font("Consolas", Font.PLAIN, 36));
        spinnerLabel.setForeground(new Color(255, 255, 255));
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinnerContainer.add(spinnerLabel);
        
        // Status label with typewriter effect
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        statusLabel.setForeground(new Color(200, 200, 200));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Add components
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(brandLabel);
        centerPanel.add(taglineLabel);
        centerPanel.add(spinnerContainer);
        centerPanel.add(statusLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Start fade-in
        startFadeIn();
    }
    
    /**
     * Start all animations
     */
    private void startAnimations() {
        startSpinner();
        startPulseAnimation();
        startMessageAnimation();
        startSymbols();
    }
    
    /**
     * Start the spinner animation
     */
    private void startSpinner() {
        spinnerTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerFrame = (spinnerFrame + 1) % spinnerFrames.length;
                // Update spinner in the container
                Component[] components = ((JPanel) getComponent(0)).getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel spinnerContainer = (JPanel) comp;
                        Component[] spinnerComponents = spinnerContainer.getComponents();
                        for (Component spinnerComp : spinnerComponents) {
                            if (spinnerComp instanceof JLabel) {
                                ((JLabel) spinnerComp).setText(spinnerFrames[spinnerFrame]);
                                break;
                            }
                        }
                        spinnerContainer.repaint();
                        break;
                    }
                }
            }
        });
        spinnerTimer.start();
    }
    
    /**
     * Start the pulse animation
     */
    private void startPulseAnimation() {
        pulseTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pulseFrame++;
                repaint();
            }
        });
        pulseTimer.start();
    }
    
    /**
     * Spawn and animate floating Java-related symbols
     */
    private void startSymbols() {
        // seed initial symbols
        for (int i = 0; i < 22; i++) {
            symbols.add(randomSymbol(true));
        }
        symbolTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return;
                // Occasionally add a new symbol
                if (symbols.size() < 40 && Math.random() < 0.08) {
                    symbols.add(randomSymbol(false));
                }
                // Update
                java.util.Iterator<FloatingSymbol> it = symbols.iterator();
                while (it.hasNext()) {
                    FloatingSymbol s = it.next();
                    s.x += s.vx;
                    s.y += s.vy;
                    // slight alpha breathing
                    s.alpha += (Math.random() - 0.5f) * 0.03f;
                    if (s.alpha < 0.1f) s.alpha = 0.1f;
                    if (s.alpha > 0.8f) s.alpha = 0.8f;
                    // remove if out of bounds
                    if (s.x < -100 || s.x > w + 100 || s.y < -100 || s.y > h + 100) {
                        it.remove();
                    }
                }
                repaint();
            }
        });
        symbolTimer.start();
    }
    
    private FloatingSymbol randomSymbol(boolean anywhere) {
        FloatingSymbol s = new FloatingSymbol();
        s.text = javaSymbols[(int)(Math.random() * javaSymbols.length)];
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        if (anywhere) {
            s.x = (float)(Math.random() * w);
            s.y = (float)(Math.random() * h);
        } else {
            // spawn near edges to drift across
            if (Math.random() < 0.5) {
                s.x = Math.random() < 0.5 ? -50 : w + 50;
                s.y = (float)(Math.random() * h);
            } else {
                s.y = Math.random() < 0.5 ? -50 : h + 50;
                s.x = (float)(Math.random() * w);
            }
        }
        s.vx = (float)(-0.6 + Math.random() * 1.2); // -0.6..0.6
        s.vy = (float)(-0.4 + Math.random() * 0.8); // -0.4..0.4
        s.alpha = (float)(0.2 + Math.random() * 0.5);
        s.size = (float)(14 + Math.random() * 20);
        return s;
    }
    
    /**
     * Fade-in effect when showing the loading screen
     */
    private void startFadeIn() {
        isFadingIn = true;
        overlayAlpha = 255;
        if (fadeTimer != null) fadeTimer.stop();
        fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                overlayAlpha -= 20; // ~200ms total
                if (overlayAlpha <= 0) {
                    overlayAlpha = 0;
                    fadeTimer.stop();
                }
                repaint();
            }
        });
        fadeTimer.start();
    }
    
    /**
     * Fade-out effect before switching away
     */
    public void startFadeOut(Runnable onComplete) {
        isFadingIn = false;
        overlayAlpha = 0;
        if (fadeTimer != null) fadeTimer.stop();
        fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                overlayAlpha += 15; // ~270ms total
                if (overlayAlpha >= 255) {
                    overlayAlpha = 255;
                    fadeTimer.stop();
                    if (onComplete != null) onComplete.run();
                }
                repaint();
            }
        });
        fadeTimer.start();
    }
    
    /**
     * Start the message animation
     */
    private void startMessageAnimation() {
        Timer messageTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageIndex = (messageIndex + 1) % loadingMessages.length;
                statusLabel.setText(loadingMessages[messageIndex]);
            }
        });
        messageTimer.start();
    }
    
    /**
     * Stop all animations
     */
    public void stopSpinner() {
        if (spinnerTimer != null) {
            spinnerTimer.stop();
        }
        if (pulseTimer != null) {
            pulseTimer.stop();
        }
    }
    
    /**
     * Draw animated background elements
     */
    private void drawAnimatedBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        // Animated floating particles / sparkles
        g2d.setColor(new Color(255, 215, 0, 22));
        for (int i = 0; i < 22; i++) {
            int x = (int) ((width * i / 22.0 + pulseFrame * 2) % width);
            int y = (int) (height * 0.3 + Math.sin(pulseFrame * 0.05 + i) * 20);
            int size = 2 + (int)(Math.sin(pulseFrame * 0.12 + i) * 2);
            g2d.fillOval(x, y, size, size);
        }
        
        // Floating Java-related symbols
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        for (FloatingSymbol s : symbols) {
            g2d.setComposite(AlphaComposite.SrcOver.derive(Math.max(0.05f, Math.min(1f, s.alpha))));
            g2d.setColor(new Color(135, 206, 250));
            g2d.setFont(g2d.getFont().deriveFont(s.size));
            g2d.drawString(s.text, (int) s.x, (int) s.y);
        }
        g2d.setComposite(AlphaComposite.SrcOver);
        
        // Subtle grid pattern
        g2d.setColor(new Color(255, 255, 255, 3));
        int spacing = 40;
        for (int x = 0; x < width; x += spacing) {
            g2d.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += spacing) {
            g2d.drawLine(0, y, width, y);
        }
    }
}
