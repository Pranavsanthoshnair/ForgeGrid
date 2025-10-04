package com.forgegrid.app;

import com.forgegrid.ui.AuthUI;

import javax.swing.*;

/**
 * Main entry point for the ForgeGrid application.
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            AuthUI authUI = new AuthUI();
            authUI.setVisible(true);
        });
    }
}
