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
			alpha += 0.08f;
			if (alpha >= 1f) {
				alpha = 1f;
				((Timer) e.getSource()).stop();
			}
			repaint();
		});
	}

	public void play() {
		alpha = 0f;
		if (!timer.isRunning()) timer.start();
		else repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
        if (alpha < 1f && !timer.isRunning()) {
            // Auto-play on first render when not yet animated
            timer.start();
        }
        g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alpha))));
		super.paintComponent(g2);
		g2.dispose();
	}
}


