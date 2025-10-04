package com.forgegrid.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A simple offline indicator that shows when the app is offline.
 * Appears as a subtle bar at the top of the screen with a WiFi-off icon.
 */
public class OfflineIndicator extends JPanel {
    private final JLabel indicatorLabel;
    
    public OfflineIndicator() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(new Color(255, 243, 205)); // Light yellow background
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 223, 186)));
        
        // Create WiFi-off icon
        JLabel iconLabel = new JLabel("\uD83D\uDCF6"); // 📶 with slash (approximated with text)
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        indicatorLabel = new JLabel("You are currently offline");
        indicatorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        indicatorLabel.setForeground(new Color(133, 100, 4));
        
        add(iconLabel);
        add(indicatorLabel);
        
        // Initially hidden
        setVisible(false);
    }
    
    /**
     * Updates the visibility of the offline indicator
     * @param isOffline true to show the indicator, false to hide it
     */
    public void setOffline(boolean isOffline) {
        setVisible(isOffline);
    }
}
