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

    public CardContainerPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Initial inner padding; will be adjusted responsively in paint
        setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        if (w > 0 && h > 0) {
            // Responsively adjust vertical padding based on available height
            // Aim for ~6% of height on top/bottom, clamped to reasonable bounds
            int desiredPad = Math.max(6, Math.min(80, (int) Math.round(h * 0.06)));
            Insets insets = getBorder() != null ? getBorder().getBorderInsets(this) : new Insets(0, 0, 0, 0);
            if (insets.top != desiredPad || insets.bottom != desiredPad || insets.left != 20 || insets.right != 20) {
                setBorder(BorderFactory.createEmptyBorder(desiredPad, 20, desiredPad, 20));
                revalidate();
            }

            // Shadow (offset)
            g2d.setColor(shadow);
            g2d.fillRoundRect(4, 6, w - 8, h - 8, arc + 6, arc + 6);

            // Card fill
            g2d.setColor(fill);
            g2d.fillRoundRect(0, 0, w, h, arc, arc);

            // Border
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setColor(border);
            g2d.drawRoundRect(1, 1, w - 2, h - 2, arc - 2, arc - 2);
        }

        g2d.dispose();
        super.paintComponent(g);
    }
}
