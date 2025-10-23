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
        setLayout(new GridBagLayout());
        setBackground(new Color(238, 238, 238));

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        JLabel brand = new JLabel("ForgeGrid");
        brand.setFont(new Font("SansSerif", Font.BOLD, 24));
        brand.setForeground(Color.BLACK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel taglineLabel = new JLabel("ForgeGrid – Where coding challenges become milestones.");
        taglineLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        taglineLabel.setForeground(Color.DARK_GRAY);
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));

        statusLabel = new JLabel("Loading...", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(brand);
        card.add(taglineLabel);
        card.add(statusLabel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }
}

