package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Full-surface animated neon background with floating programming symbols.
 * Dark blue gradient backdrop with cyan/green/magenta glowing glyphs drifting, rotating,
 * and occasionally sparkling. Subtle enough to keep foreground UI readable.
 */
public class NeonBackgroundPanel extends JPanel {
    private static class Glyph {
        String text;
        float x, y;      // position
        float vx, vy;    // velocity
        float angle;     // degrees
        float angVel;    // degrees/tick
        float size;      // base font size
        float alpha;     // 0..1 opacity factor
        Color color;     // neon base color
        float glowPhase; // for pulsing
        boolean sparkle;
        int sparkleTicks;
    }

    // Linear color interpolation helper
    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    private final List<Glyph> glyphs = new ArrayList<>();
    private final String[] symbols = new String[]{
            "}", ";", "%", "()", "<>", "&&", "||", "{}", "[]", "::"
    };
    private final Color[] neon = new Color[]{
            new Color(0, 255, 255),   // cyan
            new Color(0, 255, 170),   // greenish-cyan
            new Color(255, 0, 255)    // magenta
    };

    private final Timer updateTimer;
    private int pulseFrame = 0;

    public NeonBackgroundPanel() {
        setOpaque(false);
        // Seed some glyphs initially
        for (int i = 0; i < 26; i++) {
            glyphs.add(randomGlyph(true));
        }
        updateTimer = new Timer(40, (ActionEvent e) -> {
            step();
            repaint();
        });
        updateTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Solid background matching text-field blue (rgb 25,35,55)
        int H = Math.max(1, getHeight());
        int W = Math.max(1, getWidth());
        g2d.setColor(new Color(25, 35, 55));
        g2d.fillRect(0, 0, W, H);

        // Subtle drifting particles
        g2d.setColor(new Color(255, 215, 0, 22));
        int width = getWidth();
        int height = getHeight();
        for (int i = 0; i < 22; i++) {
            int x = (int) ((width * i / 22.0 + pulseFrame * 1.8) % Math.max(1, width));
            int y = (int) (height * 0.25 + Math.sin(pulseFrame * 0.05 + i) * 18);
            int size = 2 + (int) (Math.sin(pulseFrame * 0.12 + i) * 2);
            g2d.fillOval(x, y, size, size);
        }

        // Draw neon glyphs with glow and rotation
        for (Glyph s : glyphs) {
            float visibility = Math.max(0.05f, Math.min(1f, s.alpha));
            s.glowPhase += 0.08f;
            float glow = 0.5f + 0.5f * (float) Math.sin(s.glowPhase);
            if (s.sparkle && s.sparkleTicks > 0) {
                glow = 1.0f;
                s.sparkleTicks--;
                if (s.sparkleTicks == 0) s.sparkle = false;
            } else if (!s.sparkle && Math.random() < 0.003) {
                s.sparkle = true;
                s.sparkleTicks = 18 + (int) (Math.random() * 12);
            }

            AffineTransform old = g2d.getTransform();
            g2d.translate(s.x, s.y);
            g2d.rotate(Math.toRadians(s.angle));

            int layers = 3;
            for (int i = layers; i >= 1; i--) {
                float a = visibility * (0.12f * i) * (0.6f + 0.4f * glow);
                g2d.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, a)));
                g2d.setColor(s.color);
                g2d.setFont(new Font("Consolas", Font.BOLD, Math.max(12, (int) (s.size + i * 1.5))));
                g2d.drawString(s.text, -6, 6);
            }
            g2d.setComposite(AlphaComposite.SrcOver.derive(visibility));
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Consolas", Font.BOLD, Math.max(12, (int) (s.size))));
            g2d.drawString(s.text, -6, 6);

            g2d.setTransform(old);
        }

        g2d.dispose();
    }

    private void step() {
        pulseFrame++;
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Occasionally add a new glyph anywhere to maintain density
        if (glyphs.size() < 60 && Math.random() < 0.08) {
            glyphs.add(randomGlyph(true));
        }

        Iterator<Glyph> it = glyphs.iterator();
        while (it.hasNext()) {
            Glyph s = it.next();
            s.x += s.vx;
            s.y += s.vy;
            s.angle += s.angVel;
            s.alpha += (Math.random() - 0.5f) * 0.03f;
            if (s.alpha < 0.1f) s.alpha = 0.1f;
            if (s.alpha > 0.85f) s.alpha = 0.85f;
            // Wrap around edges to avoid originating from outside
            if (s.x < 0) s.x = w;
            if (s.x > w) s.x = 0;
            if (s.y < 0) s.y = h;
            if (s.y > h) s.y = 0;
        }
    }

    private Glyph randomGlyph(boolean anywhere) {
        Random rnd = new Random();
        Glyph s = new Glyph();
        s.text = symbols[rnd.nextInt(symbols.length)];
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        // Always spawn anywhere within bounds to keep icons omnipresent
        s.x = rnd.nextFloat() * w;
        s.y = rnd.nextFloat() * h;
        s.vx = -0.5f + rnd.nextFloat();     // -0.5..0.5
        s.vy = -0.3f + rnd.nextFloat() * 0.6f; // -0.3..0.3
        s.alpha = 0.15f + rnd.nextFloat() * 0.5f;
        s.size = 16 + rnd.nextFloat() * 22;
        s.color = neon[rnd.nextInt(neon.length)];
        s.angle = rnd.nextFloat() * 360f;
        s.angVel = -0.5f + rnd.nextFloat(); // -0.5..0.5
        s.glowPhase = rnd.nextFloat() * (float) Math.PI * 2f;
        s.sparkle = false;
        s.sparkleTicks = 0;
        return s;
    }
}
