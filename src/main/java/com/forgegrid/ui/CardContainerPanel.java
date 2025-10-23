package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple container panel with a basic border; no custom painting.
 */
public class CardContainerPanel extends JPanel {
    

    public CardContainerPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
