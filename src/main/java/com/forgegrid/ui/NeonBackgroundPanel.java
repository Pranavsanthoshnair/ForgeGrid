package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple static gradient background panel.
 * Dark blue gradient backdrop without any animations or symbols.
 */
public class NeonBackgroundPanel extends JPanel {
    
    public NeonBackgroundPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Simple solid background
        int H = Math.max(1, getHeight());
        int W = Math.max(1, getWidth());
        g2d.setColor(new Color(25, 35, 55));
        g2d.fillRect(0, 0, W, H);

        g2d.dispose();
    }
}
