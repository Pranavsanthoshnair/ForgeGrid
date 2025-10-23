package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

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

    public static JComponent asGradientButton(AbstractButton button, Color left, Color right, int arc) {
        stylePrimaryButton(button);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        final Dimension size = button.getPreferredSize();

        JPanel wrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, left, w, 0, right);
                Shape rr = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc);
                g2.setPaint(gp);
                g2.fill(rr);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.draw(rr);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);
        wrapper.add(button);
        wrapper.setPreferredSize(new Dimension(size));
        wrapper.setMinimumSize(new Dimension(size));
        wrapper.setMaximumSize(new Dimension(size));
        return wrapper;
    }
}


