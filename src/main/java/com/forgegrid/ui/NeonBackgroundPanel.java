package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Basic panel; use standard background color.
 */
public class NeonBackgroundPanel extends JPanel {
    public NeonBackgroundPanel() {
        setOpaque(true);
        setBackground(new Color(25, 35, 55));
    }
}
