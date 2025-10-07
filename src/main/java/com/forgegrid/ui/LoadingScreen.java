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
    private JLabel taglineLabel;
    private PrizeWheelPanel spinnerContainer; // now a prize wheel
    private Timer spinnerTimer;
    private Timer fadeTimer;
    private final String[] loadingMessages = {
        "Initializing ForgeGrid...",
        "Loading your coding journey...",
        "Setting up achievements...",
        "Almost ready...",
        "Loading your coding journey..."
    };
    private int messageIndex = 0;
    
    // Fade overlay state (255 = fully covered, 0 = fully visible)
    private int overlayAlpha = 255;
    private boolean isFadingIn = true; // retained for clarity, currently not read

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
    // Confetti removed
    
    public LoadingScreen() {
        initializeUI();
        startAnimations();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 35, 55));
        
        // Main container with static background
        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Static neon blue background
                g2d.setColor(new Color(25, 35, 55));
                g2d.fillRect(0, 0, getWidth(), getHeight());

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
    
    /**
     * Start all animations
     */
    private void startAnimations() {
        startSpinner();
        startMessageAnimation();
    }
    
    /**
     * Start the spinner animation
     */
    private void startSpinner() {
        spinnerTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Drive prize wheel physics and repaint
                if (wheelSpinning) {
                    // Update angle by velocity
                    wheelAngle = (wheelAngle + wheelVelocity) % 360f;
                    // Decelerate with friction
                    wheelVelocity *= 0.965f;
                    // Ticking when passing slice boundaries
                    int idx = currentSliceIndex();
                    if (idx != lastTickIndex) {
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        lastTickIndex = idx;
                    }
                    // Stop condition
                    if (wheelVelocity < 0.5f) {
                        wheelVelocity = 0f;
                        wheelSpinning = false;
                        triggerWinEffects(idx);
                    }
                    if (spinnerContainer != null) spinnerContainer.repaint();
                } else {
                    // idle subtle wobble via pulse
                    if (spinnerContainer != null) spinnerContainer.repaint();
                }
            }
        });
        spinnerTimer.start();
    }
    
    /**
     * Start the pulse animation
     */
    // Pulse animation removed
    

    private int currentSliceIndex() {
        int slices = wheelIcons.length;
        float per = 360f / slices;
        float normalized = (360f - ((wheelAngle % 360f) + 360f) % 360f) % 360f; // pointer at 12 o'clock
        int idx = Math.round(normalized / per) % slices;
        return idx;
    }

    private void triggerWinEffects(int index) {
        statusLabel.setText("Ready!");
    }

    // Confetti removed
    
    
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
    }
    

    // Gradient/lerp helpers removed

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

            // Wheel slices
            int slices = wheelIcons.length;
            float per = 360f / slices;
            for (int i = 0; i < slices; i++) {
                float start = i * per + wheelAngle;
                g2d.setColor(wheelColors[i % wheelColors.length]);
                g2d.fillArc(cx - r, cy - r, r * 2, r * 2, Math.round(start), Math.round(per));
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

            // Hint text removed for brevity

            g2d.dispose();
        }
    }
}

