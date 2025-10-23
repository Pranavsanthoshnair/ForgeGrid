package com.forgegrid.app;

import com.forgegrid.ui.AuthUI;

import javax.swing.*;

/**
 * Main entry point for the ForgeGrid application.
 */
public class Main {
    public static void main(String[] args) {
        // Reduce white flicker on some Windows/GPU combos
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("swing.bufferPerWindow", "true");
        // If flicker persists on some devices, uncomment next line to disable old DirectDraw pipeline
        System.setProperty("sun.java2d.noddraw", "true");
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize emoji font support BEFORE creating any UI
        // Use default Swing fonts
        
        // Run the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            AuthUI authUI = new AuthUI();
            authUI.setVisible(true);
        });
    }
}
