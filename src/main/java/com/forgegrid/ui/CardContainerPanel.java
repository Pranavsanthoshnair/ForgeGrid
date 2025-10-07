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
    private final Color fill = new Color(25, 35, 55, 220);
    private final Color border = new Color(255, 255, 255, 35);
    private final Color shadow = new Color(0, 0, 0, 70);

    public CardContainerPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Static inner padding (no responsive/animated behavior)
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        if (w > 0 && h > 0) {
            // Shadow
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
