package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Utility class for common UI animations and transitions
 */
public class AnimationUtils {
    
    /**
     * Linear color interpolation
     */
    public static Color lerpColor(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return new Color(
            (int)(a.getRed() + (b.getRed() - a.getRed()) * t),
            (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int)(a.getBlue() + (b.getBlue() - a.getBlue()) * t)
        );
    }
    
    /**
     * Create a smooth color transition timer
     * @param startColor Initial color
     * @param endColor Target color
     * @param durationMs Transition duration in milliseconds
     * @param onUpdate Callback with interpolated color
     * @param onComplete Callback when animation completes
     */
    public static Timer createColorTransition(Color startColor, Color endColor, int durationMs, 
                                             Consumer<Color> onUpdate, Runnable onComplete) {
        Timer timer = new Timer(16, null);
        final int[] elapsed = {0};
        
        timer.addActionListener(e -> {
            elapsed[0] += 16;
            float progress = Math.min(1.0f, (float) elapsed[0] / durationMs);
            float easedProgress = 1.0f - (float) Math.pow(1.0f - progress, 2); // Ease-out
            
            Color current = lerpColor(startColor, endColor, easedProgress);
            onUpdate.accept(current);
            
            if (progress >= 1.0f) {
                timer.stop();
                if (onComplete != null) onComplete.run();
            }
        });
        
        return timer;
    }
    
    /**
     * Create a fade animation timer
     * @param startAlpha Starting alpha value (0-255)
     * @param endAlpha Ending alpha value (0-255)
     * @param step Change per frame
     * @param onUpdate Callback with current alpha
     * @param onComplete Callback when fade completes
     */
    public static Timer createFadeTimer(int startAlpha, int endAlpha, int step, 
                                       Consumer<Integer> onUpdate, Runnable onComplete) {
        final int[] alpha = {startAlpha};
        boolean increasing = endAlpha > startAlpha;
        
        Timer timer = new Timer(16, e -> {
            alpha[0] += step;
            boolean done = increasing ? alpha[0] >= endAlpha : alpha[0] <= endAlpha;
            
            if (done) {
                alpha[0] = endAlpha;
                ((Timer)e.getSource()).stop();
                if (onComplete != null) onComplete.run();
            }
            
            onUpdate.accept(alpha[0]);
        });
        
        return timer;
    }
    
    /**
     * Create a scale animation timer
     * @param startScale Starting scale (1.0 = normal)
     * @param endScale Target scale
     * @param durationMs Animation duration
     * @param onUpdate Callback with current scale
     */
    public static Timer createScaleAnimation(float startScale, float endScale, int durationMs,
                                            Consumer<Float> onUpdate) {
        Timer timer = new Timer(30, null);
        final int[] elapsed = {0};
        
        timer.addActionListener(e -> {
            elapsed[0] += 30;
            float progress = Math.min(1.0f, (float) elapsed[0] / durationMs);
            float currentScale = startScale + (endScale - startScale) * progress;
            
            onUpdate.accept(currentScale);
            
            if (progress >= 1.0f) {
                timer.stop();
            }
        });
        
        return timer;
    }
    
    /**
     * Create a pulsing glow animation
     * @param minIntensity Minimum glow intensity (0.0-1.0)
     * @param maxIntensity Maximum glow intensity (0.0-1.0)
     * @param speed Speed multiplier
     * @param onUpdate Callback with current intensity
     */
    public static Timer createPulseAnimation(float minIntensity, float maxIntensity, float speed,
                                            Consumer<Float> onUpdate) {
        final float[] intensity = {minIntensity};
        final boolean[] increasing = {true};
        
        Timer timer = new Timer(100, e -> {
            if (increasing[0]) {
                intensity[0] += 0.1f * speed;
                if (intensity[0] >= maxIntensity) {
                    intensity[0] = maxIntensity;
                    increasing[0] = false;
                }
            } else {
                intensity[0] -= 0.1f * speed;
                if (intensity[0] <= minIntensity) {
                    intensity[0] = minIntensity;
                    increasing[0] = true;
                }
            }
            
            onUpdate.accept(intensity[0]);
        });
        
        return timer;
    }
    
    /**
     * Paint a rounded rectangle with gradient and shadow
     */
    public static void paintGradientCard(Graphics2D g2, int width, int height, 
                                        Color topColor, Color bottomColor,
                                        int arc, int shadowOffset) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Shadow
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(shadowOffset, shadowOffset + 2, width - shadowOffset * 2, 
                        height - shadowOffset * 2, arc, arc);
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, width - shadowOffset, height - shadowOffset, arc, arc);
    }
    
    /**
     * Paint a neumorphic field background
     */
    public static void paintNeumorphicField(Graphics2D g2, int width, int height, 
                                           boolean focused, Color baseColor) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Focus glow
        if (focused) {
            g2.setColor(new Color(255, 215, 0, 30));
            g2.fillRoundRect(-2, -2, width + 4, height + 4, 19, 19);
        }
        
        // Base
        g2.setColor(baseColor);
        g2.fillRoundRect(0, 0, width, height, 15, 15);
        
        // Inner shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(2, 2, width - 4, height - 4, 13, 13);
        
        // Highlight
        g2.setColor(new Color(255, 255, 255, 10));
        g2.fillRoundRect(1, 1, width - 2, 3, 15, 15);
    }
}
