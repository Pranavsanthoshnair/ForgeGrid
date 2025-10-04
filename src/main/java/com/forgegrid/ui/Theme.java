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
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setForeground(Color.WHITE);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setFont(button.getFont().deriveFont(Font.BOLD, Math.max(14f, button.getFont().getSize2D())));
	}

	public static JComponent asGradientButton(AbstractButton button, Color left, Color right, int arc) {
		JPanel wrapper = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int w = getWidth();
				int h = getHeight();
				GradientPaint gp = new GradientPaint(0, 0, left, w, h, right);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, w, h, arc, arc);
				g2d.setColor(new Color(255, 255, 255, 36));
				g2d.fillRoundRect(2, 2, Math.max(0, w - 4), Math.max(0, h / 2), Math.max(0, arc - 4), Math.max(0, arc - 4));
				g2d.dispose();
			}
		};
		wrapper.setOpaque(false);
		wrapper.setLayout(new GridBagLayout());
		wrapper.add(button);
		return wrapper;
	}
}


