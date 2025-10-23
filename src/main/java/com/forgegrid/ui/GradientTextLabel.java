package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple heading label using standard Swing rendering.
 */
public class GradientTextLabel extends JLabel {

    

	public GradientTextLabel(String text) {
		super(text);
		setForeground(Theme.TEXT_PRIMARY);
		setFont(getFont().deriveFont(Font.BOLD, Math.max(28f, getFont().getSize2D())));
		setOpaque(false);
	}

    public void setGradient(Color start, Color end) { /* no-op for basic Swing */ }

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}


