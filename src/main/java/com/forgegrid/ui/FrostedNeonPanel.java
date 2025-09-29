package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Neon background with an additional frosted-glass style rounded box
 * auto-sized to the area occupied by child components. This avoids
 * restructuring layouts while providing a blurred/transparent container look.
 */
public class FrostedNeonPanel extends NeonBackgroundPanel {

    // Appearance controls
    private final int padding = 16;       // extra padding around content bounds
    private final int arc = 24;           // corner radius
    private final Color glassFill = new Color(0, 0, 0, 120);      // translucent dark
    private final Color glassStroke = new Color(255, 255, 255, 28); // subtle outline
    private final Color sheenTop = new Color(255, 255, 255, 20);
    private final Color sheenBottom = new Color(255, 255, 255, 0);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Compute bounding box of visible child components
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Component c : getComponents()) {
            if (!c.isVisible()) continue;
            Rectangle r = c.getBounds();
            minX = Math.min(minX, r.x);
            minY = Math.min(minY, r.y);
            maxX = Math.max(maxX, r.x + r.width);
            maxY = Math.max(maxY, r.y + r.height);
        }

        if (minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE) {
            int x = Math.max(0, minX - padding);
            int y = Math.max(0, minY - padding);
            int w = Math.min(getWidth() - x, (maxX - minX) + padding * 2);
            int h = Math.min(getHeight() - y, (maxY - minY) + padding * 2);

            // Glass background
            g2d.setColor(glassFill);
            g2d.fillRoundRect(x, y, w, h, arc, arc);

            // Outline
            g2d.setColor(glassStroke);
            g2d.drawRoundRect(x + 1, y + 1, Math.max(0, w - 2), Math.max(0, h - 2), arc - 2, arc - 2);

            // Top sheen
            Paint old = g2d.getPaint();
            GradientPaint sheen = new GradientPaint(x, y, sheenTop, x, y + h / 3, sheenBottom);
            g2d.setPaint(sheen);
            g2d.fillRoundRect(x + 2, y + 2, Math.max(0, w - 4), Math.max(0, h / 3), arc - 4, arc - 4);
            g2d.setPaint(old);
        }

        g2d.dispose();
    }
}
