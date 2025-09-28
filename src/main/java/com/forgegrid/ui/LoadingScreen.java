package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Loading screen component with spinner and status text
 */
public class LoadingScreen extends JPanel {
    
    private JLabel statusLabel;
    private JLabel spinnerLabel;
    private Timer spinnerTimer;
    private int spinnerFrame = 0;
    private final String[] spinnerFrames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
    
    public LoadingScreen() {
        initializeUI();
        startSpinner();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 25, 35));
        
        // Create main container
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Spinner label
        spinnerLabel = new JLabel("⠋", JLabel.CENTER);
        spinnerLabel.setFont(new Font("Consolas", Font.PLAIN, 48));
        spinnerLabel.setForeground(new Color(76, 175, 80));
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Status label
        statusLabel = new JLabel("Loading...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        statusLabel.setForeground(new Color(200, 200, 200));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Add components
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(spinnerLabel);
        centerPanel.add(statusLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Add subtle background pattern
        setBackground(new Color(20, 25, 35));
    }
    
    /**
     * Start the spinner animation
     */
    private void startSpinner() {
        spinnerTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerFrame = (spinnerFrame + 1) % spinnerFrames.length;
                spinnerLabel.setText(spinnerFrames[spinnerFrame]);
            }
        });
        spinnerTimer.start();
    }
    
    /**
     * Stop the spinner animation
     */
    public void stopSpinner() {
        if (spinnerTimer != null) {
            spinnerTimer.stop();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Draw subtle background pattern
        drawBackgroundPattern(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draw subtle background pattern
     */
    private void drawBackgroundPattern(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 255, 255, 5));
        
        int width = getWidth();
        int height = getHeight();
        int spacing = 60;
        
        for (int x = 0; x < width; x += spacing) {
            for (int y = 0; y < height; y += spacing) {
                g2d.drawOval(x, y, 4, 4);
            }
        }
    }
}
