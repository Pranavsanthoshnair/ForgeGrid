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
    private PrizeWheelPanel spinnerContainer; // now a prize wheel
    private Timer spinnerTimer, pulseTimer, fadeTimer;
    private int pulseFrame = 0;
    private final String[] loadingMessages = {
        "Initializing ForgeGrid...",
        "Loading your coding journey...",
        "Setting up achievements...",
        "Almost ready...",
        "Loading your coding journey..."
    };
    private int messageIndex = 0;
    
    // Optional background image
    private Image backgroundImage;
    
    // Fade overlay state (255 = fully covered, 0 = fully visible)
    private int overlayAlpha = 255;
    private boolean isFadingIn = true;

    // Prize wheel state
    private final String[] wheelIcons = new String[] {"★","💰","🎁","😺","⚡","🍀","🎮","🧩"};
    private final Color[] wheelColors = new Color[] {
        new Color(255, 99, 132), new Color(54, 162, 235), new Color(255, 206, 86), new Color(75, 192, 192),
        new Color(153, 102, 255), new Color(255, 159, 64), new Color(46, 196, 182), new Color(233, 196, 106)
    };
    private int lastTickIndex = -1;
    private float wheelAngle = 0f;          // degrees
    private float wheelVelocity = 0f;       // degrees per tick
    private boolean wheelSpinning = false;
    private java.util.List<Confetti> confetti = new java.util.ArrayList<>();
    
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
                
                // Draw background (image if available, else dark blue gradient)
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
                        // Darken with translucent overlay for readability
                        g2d.setColor(new Color(0, 0, 0, 120));
                        g2d.fillRect(0, 0, w, h);
                    }
                } else {
                    int w = getWidth();
                    int h = getHeight();
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 28, 48), 0, Math.max(1, h), new Color(10, 14, 24));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, Math.max(1, w), Math.max(1, h));
                }
                
                // Background animations removed for cleaner look

                                // Fade overlay (for fade-in/out transitions)
                if (overlayAlpha > 0) {
                    g2d.setComposite(AlphaComposite.SrcOver.derive((float)Math.min(1, overlayAlpha / 255)));
                    g2d.setColor(getBackground());
                }
                
                g2d.dispose();
            }
        };
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        
        // Brand label using shared GradientTextLabel for consistency
        GradientTextLabel brand = new GradientTextLabel("ForgeGrid");
        brand.setFont(new Font("Trebuchet MS", Font.BOLD, 48));
        brand.setGradient(Theme.BRAND_YELLOW, Theme.BRAND_GOLD);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Tagline label
        taglineLabel = new JLabel("where coding challenges become milestones", JLabel.CENTER);
        taglineLabel.setFont(new Font("Trebuchet MS", Font.ITALIC, 16));
        taglineLabel.setForeground(new Color(135, 206, 250)); // Light blue
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));
        
        // Interactive prize wheel container
        spinnerContainer = new PrizeWheelPanel();
        spinnerContainer.setOpaque(false);
        spinnerContainer.setPreferredSize(new Dimension(200, 200));
        spinnerContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Status label with typewriter effect
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        statusLabel.setForeground(new Color(200, 200, 200));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Add components
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(brand);
        centerPanel.add(taglineLabel);
        centerPanel.add(spinnerContainer);
        centerPanel.add(statusLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Start fade-in
        startFadeIn();
    }
    
    private void startAnimations() {
        startSpinner();
        startPulseAnimation();
        startMessageAnimation();
    }
    
    /**
     * Start the spinner animation
     */
    private void startSpinner() {
        spinnerTimer = new Timer(100, e -> {
            if (wheelSpinning) {
                wheelAngle = (wheelAngle + wheelVelocity) % 360f;
                wheelVelocity *= 0.965f;
                int idx = currentSliceIndex();
                if (idx != lastTickIndex) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    lastTickIndex = idx;
                }
                if (wheelVelocity < 0.5f) {
                    wheelVelocity = 0f;
                    wheelSpinning = false;
                    triggerWinEffects(idx);
                }
            }
            if (spinnerContainer != null) spinnerContainer.repaint();
        });
        spinnerTimer.start();
    }
    
    private void startPulseAnimation() {
        pulseTimer = new Timer(50, e -> { pulseFrame++; repaint(); });
        pulseTimer.start();
    }
    

    private int currentSliceIndex() {
        int slices = wheelIcons.length;
        float per = 360f / slices;
        float normalized = (360f - ((wheelAngle % 360f) + 360f) % 360f) % 360f; // pointer at 12 o'clock
        int idx = Math.round(normalized / per) % slices;
        return idx;
    }

    private void triggerWinEffects(int index) {
        // Update status and jingle via quick beeps
        String icon = wheelIcons[index];
        statusLabel.setText("Lucky spin: " + icon + "!");
        // simple jingle
        new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    Thread.sleep(80);
                }
            } catch (InterruptedException ignored) {}
        }).start();
        // confetti burst
        spawnConfettiBurst();
    }

    private void spawnConfettiBurst() {
        confetti.clear();
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 120; i++) {
            Confetti c = new Confetti();
            c.x = w / 2f;
            c.y = h / 2f - 20;
            double ang = rnd.nextDouble() * Math.PI * 2;
            double spd = 2 + rnd.nextDouble() * 6;
            c.vx = (float) (Math.cos(ang) * spd);
            c.vy = (float) (Math.sin(ang) * spd - 2);
            c.color = wheelColors[i % wheelColors.length];
            c.life = 60 + rnd.nextInt(40);
            confetti.add(c);
        }
    }
    
    
    private void startFadeIn() {
        startFade(true, 255, -20, null);
    }
    
    public void startFadeOut(Runnable onComplete) {
        startFade(false, 0, 15, onComplete);
    }
    
    private void startFade(boolean fadeIn, int startAlpha, int delta, Runnable onComplete) {
        isFadingIn = fadeIn;
        overlayAlpha = startAlpha;
        if (fadeTimer != null) fadeTimer.stop();
        int endAlpha = (delta < 0) ? 0 : 255;
        fadeTimer = AnimationUtils.createFadeTimer(
            startAlpha,
            endAlpha,
            Math.abs(delta),
            alpha -> { overlayAlpha = alpha; repaint(); },
            onComplete
        );
        fadeTimer.start();
    }
    
    private void startMessageAnimation() {
        new Timer(2000, e -> {
            messageIndex = (messageIndex + 1) % loadingMessages.length;
            statusLabel.setText(loadingMessages[messageIndex]);
        }).start();
    }
    
    public void stopSpinner() {
        if (spinnerTimer != null) spinnerTimer.stop();
        if (pulseTimer != null) pulseTimer.stop();
    }
    

    private static class Confetti {
        float x, y, vx, vy;
        Color color;
        int life;
    }

    private static Color lerpColor(Color a, Color b, float t) {
        return AnimationUtils.lerpColor(a, b, t);
    }

    private class PrizeWheelPanel extends JPanel {
        public PrizeWheelPanel() {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText("Tap to spin");
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!wheelSpinning) {
                        statusLabel.setText("Spinning...");
                        wheelSpinning = true;
                        lastTickIndex = -1;
                        // random initial velocity based on click location
                        wheelVelocity = 18f + (float)(Math.random() * 12f); // deg per tick
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int r = Math.min(w, h) / 2 - 8;
            int cx = w / 2;
            int cy = h / 2;

            // Glow
            g2d.setColor(new Color(255, 204, 77, 30));
            g2d.fillOval(cx - r - 12, cy - r - 12, (r + 12) * 2, (r + 12) * 2);

            // Wheel slices
            int slices = wheelIcons.length;
            float per = 360f / slices;
            for (int i = 0; i < slices; i++) {
                float start = i * per + wheelAngle;
                g2d.setColor(wheelColors[i % wheelColors.length]);
                g2d.fillArc(cx - r, cy - r, r * 2, r * 2, Math.round(start), Math.round(per));
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawArc(cx - r, cy - r, r * 2, r * 2, Math.round(start), Math.round(per));
            }

            // Center hub
            g2d.setColor(new Color(25, 35, 55));
            g2d.fillOval(cx - 28, cy - 28, 56, 56);
            g2d.setColor(new Color(255, 215, 0));
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawOval(cx - 28, cy - 28, 56, 56);

            // Icons on slices
            g2d.setFont(new Font("Segoe UI Emoji", Font.BOLD, Math.max(16, r / 6)));
            for (int i = 0; i < slices; i++) {
                float mid = (i + 0.5f) * per + wheelAngle;
                double rad = Math.toRadians(mid);
                int tx = cx + (int) (Math.cos(rad) * (r * 0.6));
                int ty = cy + (int) (Math.sin(rad) * (r * 0.6));
                String icon = wheelIcons[i];
                FontMetrics fm = g2d.getFontMetrics();
                int tw = fm.stringWidth(icon);
                int th = fm.getAscent();
                g2d.setColor(Color.WHITE);
                g2d.drawString(icon, tx - tw / 2, ty + th / 2 - 6);
            }

            // Pointer at top
            int pointerW = 18, pointerH = 24;
            Polygon pointer = new Polygon();
            pointer.addPoint(cx, cy - r - 6);
            pointer.addPoint(cx - pointerW / 2, cy - r + pointerH);
            pointer.addPoint(cx + pointerW / 2, cy - r + pointerH);
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillPolygon(pointer);
            g2d.setColor(new Color(255, 215, 0));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawPolygon(pointer);

            // Hint text
            g2d.setColor(new Color(220, 220, 230, 200));
            g2d.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
            String hint = wheelSpinning ? "Spinning..." : "Tap to spin";
            int tw = g2d.getFontMetrics().stringWidth(hint);
            g2d.drawString(hint, cx - tw / 2, cy + r + 20);

            g2d.dispose();
        }
    }
}

