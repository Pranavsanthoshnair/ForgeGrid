package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Static loading screen with brand and tagline. No animations or sounds.
 */
public class LoadingScreen extends JPanel {

    private JLabel statusLabel;

    public LoadingScreen() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 35, 55));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));

        GradientTextLabel brand = new GradientTextLabel("ForgeGrid");
        brand.setFont(new Font("Trebuchet MS", Font.BOLD, 48));
        brand.setGradient(Theme.BRAND_YELLOW, Theme.BRAND_GOLD);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel taglineLabel = new JLabel("where coding challenges become milestones", JLabel.CENTER);
        taglineLabel.setFont(new Font("Trebuchet MS", Font.ITALIC, 16));
        taglineLabel.setForeground(new Color(135, 206, 250));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        statusLabel = new JLabel("Initializing ForgeGrid...", JLabel.CENTER);
        statusLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        statusLabel.setForeground(new Color(200, 200, 200));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(brand);
        centerPanel.add(taglineLabel);
        centerPanel.add(statusLabel);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }
}

