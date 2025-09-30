package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Welcome screen with logo + title + "Let's start" button,
 * using the same background and color theme as the app.
 */
public class WelcomeUI extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(0xffcc4d); // golden yellow
    private static final Color SECONDARY_COLOR = new Color(0x3a6ea5); // blue

    private final JButton startButton;

    public WelcomeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        NeonBackgroundPanel background = new NeonBackgroundPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel logoPanel = createLogoPanel(1.45);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, -8, 0));

        JLabel title = new JLabel("ForgeGrid");
        title.setFont(getStylishFont("Montserrat", Font.BOLD, 58, new String[]{"Poppins","Segoe UI","Trebuchet MS"}));
        title.setForeground(PRIMARY_COLOR);
        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(title);
        titleWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        startButton = new JButton("Let's start");
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setForeground(Color.WHITE);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        double s = calculateProportionalScale();
        startButton.setFont(getStylishFont("Montserrat", Font.BOLD, Math.max(18, (int)(22 * s)), new String[]{"Poppins","Segoe UI","Trebuchet MS"}));
        int buttonWidth = Math.max(250, (int)(520 * s));
        int buttonHeight = Math.max(50, (int)(70 * s));
        startButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        startButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        JPanel buttonWrap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                Color c1 = new Color(255, 193, 43);
                Color c2 = new Color(255, 140, 0);
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 26, 26);
                g2d.setColor(new Color(255, 255, 255, 28));
                g2d.fillRoundRect(2, 2, w - 4, h / 2, 22, 22);
                g2d.dispose();
            }
        };
        buttonWrap.setOpaque(false);
        buttonWrap.setLayout(new GridBagLayout());
        buttonWrap.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        buttonWrap.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonWrap.add(startButton);

        background.add(Box.createVerticalGlue());

        // Create a combined hero panel (logo + title + tagline)
        JPanel heroPanel = new JPanel(new GridBagLayout());
        heroPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0); // no gap above/below logo
        gbc.anchor = GridBagConstraints.CENTER;
        heroPanel.add(logoPanel, gbc);

        // Title: pull closer to logo using slight negative top inset
        gbc.gridy = 1;
        gbc.insets = new Insets(-8, 0, 0, 0);
        heroPanel.add(titleWrap, gbc);

        // Tagline: very small positive gap below title (tighter)
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 0, 0, 0);
        JLabel tagline = new JLabel("where coding challenges become milestones");
        tagline.setFont(getStylishFont("Segoe UI", Font.ITALIC, 20, new String[]{"Poppins","Trebuchet MS","SansSerif"}));
        tagline.setForeground(new Color(230, 230, 235));
        heroPanel.add(tagline, gbc);

        // Add the combined hero panel to the background
        background.add(heroPanel);

        // Increase gap between tagline and the CTA button for stronger hierarchy
        background.add(Box.createRigidArea(new Dimension(0, 56)));
        background.add(buttonWrap);
        background.add(Box.createVerticalGlue());

        add(background, BorderLayout.CENTER);
    }

    public void addStartActionListener(ActionListener l) {
        startButton.addActionListener(l);
    }

    private JPanel createLogoPanel(double multiplier) {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        try {
            java.net.URL logoUrl = getClass().getResource("/com/forgegrid/icon/logo2_transparent.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                double scale = calculateProportionalScale() * multiplier;
                int logoWidth = Math.max(260, (int)(360 * scale));
                int logoHeight = Math.max(160, (int)(220 * scale));
                Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledLogo);
                JLabel logoLabel = new JLabel(scaledIcon);
                logoLabel.setHorizontalAlignment(JLabel.CENTER);
                logoPanel.add(logoLabel, BorderLayout.CENTER);
                logoPanel.setPreferredSize(new Dimension(logoWidth, logoHeight));
                logoPanel.setMaximumSize(new Dimension(logoWidth, logoHeight));
            }
        } catch (Exception ignore) {}
        return logoPanel;
    }

    private Font getStylishFont(String primary, int style, int size, String[] fallbacks) {
        try {
            Font f = new Font(primary, style, size);
            if (f != null) return f;
        } catch (Exception ignore) {}
        if (fallbacks != null) {
            for (String name : fallbacks) {
                try {
                    Font f = new Font(name, style, size);
                    if (f != null) return f;
                } catch (Exception ignore) {}
            }
        }
        return new Font("SansSerif", style, size);
    }

    private double calculateProportionalScale() {
        double baseWidth = 800.0;
        double baseHeight = 900.0;
        double currentWidth = Math.max(1, getWidth());
        double currentHeight = Math.max(1, getHeight());
        double widthScale = Math.max(0.5, Math.min(1.5, currentWidth / baseWidth));
        double heightScale = Math.max(0.5, Math.min(1.5, currentHeight / baseHeight));
        return Math.min(widthScale, heightScale);
    }
}


