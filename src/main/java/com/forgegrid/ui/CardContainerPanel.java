package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A slightly transparent, rounded, card-like container panel with
 * subtle border and shadow to make content feel "present" over
 * a dynamic neon background.
 */
public class CardContainerPanel extends JPanel {
    private final int arc = 20;
    // Match background color (25,35,55) with slight translucency to keep presence
    private final Color fill = new Color(25, 35, 55, 220);
    private final Color border = new Color(255, 255, 255, 35); // subtle light border
    private final Color shadow = new Color(0, 0, 0, 70);    // soft shadow
    private float hoverScale = 1.0f;
    private Timer hoverTimer;

    public CardContainerPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Initial inner padding; will be adjusted responsively in paint
        setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

        // Smooth hover scale (1.0 -> 1.02)
        enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
        hoverTimer = new Timer(16, e -> {
            boolean hovered = getMousePosition() != null;
            float target = hovered ? 1.02f : 1.0f;
            float delta = (target - hoverScale) * 0.18f; // easing
            if (Math.abs(delta) < 0.001f) {
                hoverScale = target;
                hoverTimer.stop();
            } else {
                hoverScale += delta;
            }
            repaint();
        });
        hoverTimer.setRepeats(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { if (!hoverTimer.isRunning()) hoverTimer.start(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { if (!hoverTimer.isRunning()) hoverTimer.start(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        if (w > 0 && h > 0) {
            Graphics2D gScaled = (Graphics2D) g2d.create();
            gScaled.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = w / 2;
            int cy = h / 2;
            gScaled.translate(cx, cy);
            gScaled.scale(hoverScale, hoverScale);
            gScaled.translate(-cx, -cy);
            // Responsively adjust vertical padding based on available height
            // Aim for ~6% of height on top/bottom, clamped to reasonable bounds
            int desiredPad = Math.max(6, Math.min(80, (int) Math.round(h * 0.06)));
            Insets insets = getBorder() != null ? getBorder().getBorderInsets(this) : new Insets(0, 0, 0, 0);
            if (insets.top != desiredPad || insets.bottom != desiredPad || insets.left != 20 || insets.right != 20) {
                setBorder(BorderFactory.createEmptyBorder(desiredPad, 20, desiredPad, 20));
                revalidate();
            }

            // Shadow (offset)
            gScaled.setColor(shadow);
            gScaled.fillRoundRect(4, 6, w - 8, h - 8, arc + 6, arc + 6);

            // Card fill
            gScaled.setColor(fill);
            gScaled.fillRoundRect(0, 0, w, h, arc, arc);

            // Border
            gScaled.setStroke(new BasicStroke(1.5f));
            gScaled.setColor(border);
            gScaled.drawRoundRect(1, 1, w - 2, h - 2, arc - 2, arc - 2);
            gScaled.dispose();
        }

        g2d.dispose();
        super.paintComponent(g);
    }
}
