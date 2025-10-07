package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple fade-in wrapper for smooth screen transitions.
 */
public class FadeInPanel extends JPanel {

	private float alpha = 0f;
	private final Timer timer;

	public FadeInPanel(LayoutManager layout) {
		super(layout);
		setOpaque(false);
        timer = new Timer(16, e -> {
            alpha = Math.min(1f, alpha + 0.08f);
            if (alpha >= 1f) ((Timer) e.getSource()).stop();
            repaint();
        });
	}

	public void play() {
		alpha = 0f;
        if (!timer.isRunning()) timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
        if (alpha < 1f && !timer.isRunning()) timer.start();
        g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
		super.paintComponent(g2);
		g2.dispose();
	}
}


