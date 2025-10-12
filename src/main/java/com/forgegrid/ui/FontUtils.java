package com.forgegrid.ui;

import java.awt.*;
import javax.swing.*;

/**
 * Utility class for handling fonts, especially emoji-compatible fonts
 */
public class FontUtils {
    
    private static Font emojiFont;
    private static Font fallbackFont;
    private static boolean fontsInitialized = false;
    
    /**
     * Initialize emoji fonts for the application
     */
    public static void initializeEmojiSupport() {
        if (fontsInitialized) return;
        
        // Try to find an emoji-compatible font
        String[] emojiFontNames = {
            "Segoe UI Emoji",    // Windows 10+
            "Apple Color Emoji", // macOS
            "Noto Color Emoji",  // Linux
            "Segoe UI Symbol",   // Windows fallback
            "Symbola"            // Cross-platform fallback
        };
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // Find first available emoji font
        for (String fontName : emojiFontNames) {
            for (String available : availableFonts) {
                if (available.equalsIgnoreCase(fontName)) {
                    emojiFont = new Font(fontName, Font.PLAIN, 14);
                    System.out.println("✓ Using emoji font: " + fontName);
                    fontsInitialized = true;
                    break;
                }
            }
            if (fontsInitialized) break;
        }
        
        // Fallback to Segoe UI (no emoji, but won't crash)
        if (!fontsInitialized) {
            emojiFont = new Font("Segoe UI", Font.PLAIN, 14);
            System.out.println("⚠ No emoji font found, using Segoe UI fallback");
            fontsInitialized = true;
        }
        
        fallbackFont = new Font("Segoe UI", Font.PLAIN, 14);
    }
    
    /**
     * Get emoji-compatible font with specific size and style
     */
    public static Font getEmojiFont(int style, int size) {
        initializeEmojiSupport();
        return emojiFont.deriveFont(style, (float) size);
    }
    
    /**
     * Get emoji-compatible font with default style and size
     */
    public static Font getEmojiFont() {
        return getEmojiFont(Font.PLAIN, 14);
    }
    
    /**
     * Get emoji-compatible font with specific size
     */
    public static Font getEmojiFont(int size) {
        return getEmojiFont(Font.PLAIN, size);
    }
    
    /**
     * Configure global UI fonts to support emojis
     */
    public static void configureGlobalFonts() {
        initializeEmojiSupport();
        
        // Set default fonts for Swing components
        Font defaultFont = getEmojiFont(Font.PLAIN, 14);
        Font boldFont = getEmojiFont(Font.BOLD, 14);
        
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("List.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("TitledBorder.font", boldFont);
        
        System.out.println("✓ Global emoji font support configured");
    }
    
    /**
     * Create a label with emoji support
     */
    public static JLabel createEmojiLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(getEmojiFont());
        return label;
    }
    
    /**
     * Create a label with emoji support and custom size
     */
    public static JLabel createEmojiLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(getEmojiFont(size));
        return label;
    }
    
    /**
     * Create a label with emoji support, custom size and style
     */
    public static JLabel createEmojiLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(getEmojiFont(style, size));
        return label;
    }
    
    /**
     * Apply emoji font to an existing component
     */
    public static void applyEmojiFont(JComponent component) {
        component.setFont(getEmojiFont());
    }
    
    /**
     * Apply emoji font with specific size to an existing component
     */
    public static void applyEmojiFont(JComponent component, int size) {
        component.setFont(getEmojiFont(size));
    }
    
    /**
     * Apply emoji font with specific style and size to an existing component
     */
    public static void applyEmojiFont(JComponent component, int style, int size) {
        component.setFont(getEmojiFont(style, size));
    }
}

