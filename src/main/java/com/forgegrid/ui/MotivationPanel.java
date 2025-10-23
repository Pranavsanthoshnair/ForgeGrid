package com.forgegrid.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Minimal, dark-themed panel that displays random motivational tips/quotes
 * related to coding and learning. Designed to be used inside a CardLayout:
 *
 * mainPanel.add(new MotivationPanel(), "Motivation");
 * cardLayout.show(mainPanel, "Motivation");
 */
public class MotivationPanel extends JPanel {

    // Palette aligned with the rest of ForgeGrid
    // Using default Swing colors; keep minimal

    private final JLabel quoteLabel;
    private final JButton nextButton;
    private final List<String> quotes;

    public MotivationPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(238, 238, 238));

        // White card container
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 24, 20, 24));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("Motivation");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Quote label (centered, wrapped via HTML)
        quoteLabel = new JLabel("", SwingConstants.CENTER);
        quoteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quoteLabel.setForeground(Color.DARK_GRAY);
        quoteLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        // Use app's emoji-capable font if available
        quoteLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Next button (hand cursor on hover)
        nextButton = new JButton("Next Tip");
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setUI(new BasicButtonUI());
        nextButton.setForeground(Color.WHITE);
        nextButton.setBackground(Theme.BRAND_PINK);
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        Dimension btn = new Dimension(140, 36);
        nextButton.setPreferredSize(btn);
        nextButton.setMaximumSize(btn);
        nextButton.addActionListener(this::handleNext);

        // Build vertical layout
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(Box.createVerticalGlue());
        card.add(quoteLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nextButton);
        card.add(Box.createVerticalGlue());

        // Slight outer padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);

        quotes = defaultQuotes();
        showRandomQuote();
    }

    private void handleNext(ActionEvent e) {
        showRandomQuote();
    }

    /**
     * Chooses and displays a random quote from the list.
     */
    private void showRandomQuote() {
        if (quotes.isEmpty()) {
            quoteLabel.setText("No tips available.");
            return;
        }
        int index = ThreadLocalRandom.current().nextInt(quotes.size());
        String text = quotes.get(index);
        // Sanitize emojis to prevent hollow boxes on older environments
        // No emoji sanitization needed in basic Swing mode
        // HTML centers and wraps the text nicely inside a JLabel
        quoteLabel.setText("<html><div style='text-align:center;'>" + escapeHtml(text) + "</div></html>");
    }

    /**
     * Minimal HTML escape for display inside JLabel HTML.
     */
    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Curated, short quotes aligned with coding, learning and consistency.
     */
    private static List<String> defaultQuotes() {
        return Arrays.asList(
            "Small commits every day beat big plans someday.",
            "Practice beats talent when talent doesn’t practice.",
            "Read code, write code, repeat.",
            "Shipping is a feature. Finish something today.",
            "Debugging is learning. Every bug teaches.",
            "Consistency compounds—twenty focused minutes count.",
            "First make it work, then make it clean, then fast.",
            "Tests are notes to your future self.",
            "The best time to start was yesterday. The next best is now.",
            "Tiny improvements daily > massive improvement rarely.",
            "You don’t need motivation—just a tiny next step.",
            "Clarity comes from shipping, not from thinking about shipping.",
            "Keyboard time beats tutorial time.",
            "Delete code bravely. Simple wins.",
            "If it’s hard to test, it’s too complex.",
            "Your code mirrors your thinking—keep both clear.",
            "Repetition builds intuition. Keep going.",
            "Ask: what is the smallest valuable thing I can do now?",
            "You improve by doing the work you avoid.",
            "Momentum > motivation. Start for five minutes."
        );
    }
}


