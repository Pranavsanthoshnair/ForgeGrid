package com.forgegrid.app;

import com.forgegrid.ui.AuthUI;

import javax.swing.*;

/**
 * Main entry point for the ForgeGrid application.
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("swing.bufferPerWindow", "true");
        System.setProperty("sun.java2d.noddraw", "true");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            AuthUI authUI = new AuthUI();
            authUI.setVisible(true);
        });
    }
}
