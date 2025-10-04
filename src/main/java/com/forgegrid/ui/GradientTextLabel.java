package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Bold gradient-filled text label for headings like the ForgeGrid title.
 */
public class GradientTextLabel extends JLabel {

	private Color start = Theme.BRAND_YELLOW;
	private Color end = Theme.BRAND_PINK;

	public GradientTextLabel(String text) {
		super(text);
		setForeground(Theme.TEXT_PRIMARY);
		setFont(getFont().deriveFont(Font.BOLD, Math.max(28f, getFont().getSize2D())));
		setOpaque(false);
	}

	public void setGradient(Color start, Color end) {
		this.start = start;
		this.end = end;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fm = g2.getFontMetrics(getFont());
		String text = getText();
		int textWidth = fm.stringWidth(text);
		int x = 0;
		int y = fm.getAscent();
		GradientPaint gp = new GradientPaint(0, 0, start, Math.max(1, textWidth), 0, end);
		g2.setPaint(gp);
		g2.setFont(getFont());
		g2.drawString(text, x, y);
		g2.dispose();
	}
}


