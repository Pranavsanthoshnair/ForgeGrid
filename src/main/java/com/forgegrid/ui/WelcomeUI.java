package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.event.ActionListener;
 
 

/**
 * Welcome screen with logo + title + "Let's start" button,
 */
public class WelcomeUI extends JPanel {

    // Colors centralized in Theme
    
    private final String[] quotes = {"Welcome to ForgeGrid"};

    private final JButton startButton;

    public WelcomeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setOpaque(true);
        background.setBackground(Color.LIGHT_GRAY);
        background.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel logoPanel = new JPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        logoPanel.setOpaque(false);
        // Load logo from resources and center it
        try {
            URL logoUrl = WelcomeUI.class.getResource("/com/forgegrid/icon/logo2_transparent.png");
            if (logoUrl != null) {
                ImageIcon rawIcon = new ImageIcon(logoUrl);
                int rawW = rawIcon.getIconWidth();
                int rawH = rawIcon.getIconHeight();
                int targetH = 96; // scale to a pleasant height
                int targetW = (rawW > 0 && rawH > 0) ? (int) Math.round((double) rawW * targetH / rawH) : 96;
                Image scaled = rawIcon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaled));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                logoPanel.add(logoLabel);
            }
        } catch (Exception ignored) { }

        JLabel title = new JLabel("ForgeGrid");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.BLACK);
        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(title);
        titleWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        startButton = new JButton("Start");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        startButton.setMaximumSize(new Dimension(200, 40));
        startButton.setPreferredSize(new Dimension(200, 40));

        // Create a combined hero panel (logo + title + tagline + start + welcome)
        JPanel heroPanel = new JPanel();
        heroPanel.setOpaque(false);
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(logoPanel);
        titleWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(titleWrap);

        JLabel tagline = new JLabel("ForgeGrid – Where coding challenges become milestones.");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tagline.setForeground(Color.DARK_GRAY);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        heroPanel.add(tagline);

        heroPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        startButton.setUI(new BasicButtonUI());
        startButton.setBackground(Theme.BRAND_PINK);
        startButton.setForeground(Color.WHITE);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(startButton);

        // Center the hero panel
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(heroPanel);
        
        // Create welcome label (previously below card) and place it inside the card
        JLabel quotesPanel = new JLabel(quotes[0]);
        quotesPanel.setForeground(Color.DARK_GRAY);
        quotesPanel.setHorizontalAlignment(SwingConstants.CENTER);
        quotesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        heroPanel.add(quotesPanel);
        
        // Add all components to background using vertical BoxLayout
        background.add(Box.createVerticalGlue());
        centerWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        background.add(centerWrapper);
        background.add(Box.createVerticalGlue());

        // Static landing page (no fade/animations)
        add(background, BorderLayout.CENTER);
    }

    public void addStartActionListener(ActionListener l) {
        startButton.addActionListener(l);
    }

    // No extra helpers
}


