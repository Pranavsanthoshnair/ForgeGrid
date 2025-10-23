package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Static container panel. No transitions or animations.
 */
public class FadeInPanel extends JPanel {

	public FadeInPanel(LayoutManager layout) {
		super(layout);
		setOpaque(false);
	}

	public void play() { /* no-op to preserve API */ }

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}


