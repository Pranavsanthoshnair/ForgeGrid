package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Centralized UI theme utilities and brand colors.
 */
public final class Theme {

	private Theme() {}

	// Brand colors
	public static final Color BRAND_YELLOW = new Color(0xffcc4d);
	public static final Color BRAND_BLUE = new Color(0x3a6ea5);
	public static final Color BRAND_PINK = new Color(0xe14c8c);
	public static final Color BRAND_GOLD = new Color(0xad8f37);
	public static final Color TEXT_PRIMARY = new Color(240, 242, 246);
	public static final Color TEXT_SECONDARY = new Color(208, 212, 220);

	public static void stylePrimaryButton(AbstractButton button) {
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, Math.max(14f, button.getFont().getSize2D())));
	}

    // Gradient helpers removed to keep Swing basic
}


