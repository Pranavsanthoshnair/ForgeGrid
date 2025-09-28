package com.forgegrid.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility class for creating subtle and professional animations
 */
public class AnimationUtils {
    
    /**
     * Creates a fade-in animation for a component
     */
    public static void fadeIn(JComponent component, int duration) {
        // Simple fade-in without hiding the component
        component.setVisible(true);
        component.setOpaque(true);
        
        Timer timer = new Timer(16, new ActionListener() {
            private int elapsed = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed += 16;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                
                if (progress >= 1.0f) {
                    ((Timer) e.getSource()).stop();
                }
                
                component.repaint();
            }
        });
        
        timer.start();
    }
    
    /**
     * Creates a gentle scale animation for a component
     */
    public static void scaleIn(JComponent component, int duration) {
        component.setVisible(false);
        
        Timer timer = new Timer(16, new ActionListener() {
            private float scale = 0.0f;
            private int elapsed = 0;
            private Dimension originalSize = component.getPreferredSize();
            
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed += 16;
                scale = Math.min(1.0f, (float) elapsed / duration);
                
                if (scale >= 1.0f) {
                    component.setVisible(true);
                    component.setSize(originalSize);
                    ((Timer) e.getSource()).stop();
                } else {
                    int newWidth = (int) (originalSize.width * scale);
                    int newHeight = (int) (originalSize.height * scale);
                    component.setSize(newWidth, newHeight);
                }
                
                component.repaint();
            }
        });
        
        timer.start();
    }
    
    /**
     * Creates a smooth hover effect for buttons
     */
    public static void addHoverEffect(JButton button, Color normalColor, Color hoverColor, int transitionDuration) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer hoverTimer;
            private Color currentColor = normalColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (hoverTimer != null) hoverTimer.stop();
                
                hoverTimer = new Timer(16, new ActionListener() {
                    private int elapsed = 0;
                    
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        elapsed += 16;
                        float progress = Math.min(1.0f, (float) elapsed / transitionDuration);
                        
                        currentColor = interpolateColor(normalColor, hoverColor, progress);
                        button.setBackground(currentColor);
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
                        float progress = Math.min(1.0f, (float) elapsed / transitionDuration);
                        
                        currentColor = interpolateColor(hoverColor, normalColor, progress);
                        button.setBackground(currentColor);
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
    
    /**
     * Creates a gentle pulse animation
     */
    public static void pulse(JComponent component, int duration, float intensity) {
        Timer timer = new Timer(16, new ActionListener() {
            private int elapsed = 0;
            private Dimension originalSize = component.getPreferredSize();
            
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed += 16;
                float progress = (float) elapsed / duration;
                float scale = 1.0f + (float) (Math.sin(progress * Math.PI * 2) * intensity);
                
                int newWidth = (int) (originalSize.width * scale);
                int newHeight = (int) (originalSize.height * scale);
                component.setSize(newWidth, newHeight);
                component.repaint();
                
                if (elapsed >= duration) {
                    component.setSize(originalSize);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Creates a smooth slide-in animation
     */
    public static void slideIn(JComponent component, int duration, int direction) {
        // direction: 0=from top, 1=from bottom, 2=from left, 3=from right
        component.setVisible(false);
        
        Timer timer = new Timer(16, new ActionListener() {
            private int elapsed = 0;
            private Point originalLocation = component.getLocation();
            private Point startLocation = new Point(originalLocation);
            
            {
                // Set initial position based on direction
                switch (direction) {
                    case 0: // from top
                        startLocation.y = -component.getHeight();
                        break;
                    case 1: // from bottom
                        startLocation.y = component.getParent().getHeight();
                        break;
                    case 2: // from left
                        startLocation.x = -component.getWidth();
                        break;
                    case 3: // from right
                        startLocation.x = component.getParent().getWidth();
                        break;
                }
                component.setLocation(startLocation);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed += 16;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                
                // Easing function for smooth animation
                progress = easeOutCubic(progress);
                
                int currentX = (int) (startLocation.x + (originalLocation.x - startLocation.x) * progress);
                int currentY = (int) (startLocation.y + (originalLocation.y - startLocation.y) * progress);
                
                component.setLocation(currentX, currentY);
                component.setVisible(true);
                component.repaint();
                
                if (progress >= 1.0f) {
                    component.setLocation(originalLocation);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Interpolates between two colors
     */
    private static Color interpolateColor(Color start, Color end, float progress) {
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * progress);
        
        return new Color(r, g, b, a);
    }
    
    /**
     * Easing function for smooth animations
     */
    private static float easeOutCubic(float t) {
        return 1.0f - (float) Math.pow(1.0f - t, 3);
    }
}
