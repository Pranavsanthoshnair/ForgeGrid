package com.forgegrid.ui;

import com.forgegrid.model.PlayerProfile;

import javax.swing.*;
import java.awt.*;

/**
 * Full-screen onboarding wizard shown after login, before opening the dashboard.
 */
public class OnboardingWizard extends JFrame {

    private final PlayerProfile profile;
    private final CardLayout cardLayout;
    private final JPanel root;

    // Collected answers
    private String selectedGoal;
    private String selectedLanguage;
    private String selectedSkill;
    // Removed Q4 per request

    public OnboardingWizard(PlayerProfile profile) {
        this.profile = profile;
        setTitle("ForgeGrid - Getting Started");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 480));

        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);
        setContentPane(root);

        JPanel q1 = buildQ1();
        JPanel q2 = buildQ2();
        JPanel q3 = buildQ3();

        root.add(q1, "q1");
        root.add(q2, "q2");
        root.add(q3, "q3");

        cardLayout.show(root, "q1");
        triggerFade("q1");
    }

    private JPanel buildQ1() {
        NeonBackgroundPanel bg = new NeonBackgroundPanel();
        bg.setLayout(new GridBagLayout());
        JPanel center = buildQuestionPanel(
                "Q1. What is your main coding goal on ForgeGrid?",
                new String[]{
                        "Learn programming fundamentals",
                        "Prepare for coding interviews",
                        "Practice competitive programming",
                        "Build real-world projects"
                },
                e -> {
                    this.selectedGoal = getSelectedTextFrom((JButton) e.getSource());
                    cardLayout.show(root, "q2");
                    triggerFade("q2");
                }
        );
        FadeInPanel fade = new FadeInPanel(new GridBagLayout());
        GridBagConstraints inner = new GridBagConstraints();
        inner.gridx = 0; inner.gridy = 0; inner.anchor = GridBagConstraints.CENTER;
        fade.add(center, inner);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.weightx = 1; gbc.weighty = 1;
        bg.add(fade, gbc);
        return bg;
    }

    private JPanel buildQ2() {
        NeonBackgroundPanel bg = new NeonBackgroundPanel();
        bg.setLayout(new GridBagLayout());
        JPanel center = buildQuestionPanel(
                "Q2. What’s your preferred programming language?",
                new String[]{"Java", "Python", "C", "JavaScript"},
                e -> {
                    this.selectedLanguage = getSelectedTextFrom((JButton) e.getSource());
                    cardLayout.show(root, "q3");
                    triggerFade("q3");
                }
        );
        FadeInPanel fade = new FadeInPanel(new GridBagLayout());
        GridBagConstraints inner = new GridBagConstraints();
        inner.gridx = 0; inner.gridy = 0; inner.anchor = GridBagConstraints.CENTER;
        fade.add(center, inner);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.weightx = 1; gbc.weighty = 1;
        bg.add(fade, gbc);
        return bg;
    }

    private JPanel buildQ3() {
        NeonBackgroundPanel bg = new NeonBackgroundPanel();
        bg.setLayout(new GridBagLayout());
        JPanel center = buildQuestionPanel(
                "Q3. What best describes your current coding skill level?",
                new String[]{
                        "Beginner (just starting out)",
                        "Intermediate (comfortable with basics + OOP)",
                        "Advanced (algorithms, contests, frameworks)",
                        "Expert (mentor/competitive level)"
                },
                e -> {
                    this.selectedSkill = getSelectedTextFrom((JButton) e.getSource());
                    finishOnboarding();
                }
        );
        FadeInPanel fade = new FadeInPanel(new GridBagLayout());
        GridBagConstraints inner = new GridBagConstraints();
        inner.gridx = 0; inner.gridy = 0; inner.anchor = GridBagConstraints.CENTER;
        fade.add(center, inner);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.weightx = 1; gbc.weighty = 1;
        bg.add(fade, gbc);
        return bg;
    }

    private void finishOnboarding() {
        dispose();
        Dashboard dashboard = new Dashboard(profile);
        dashboard.applyOnboardingSelections(
                selectedGoal,
                selectedLanguage,
                selectedSkill,
                null
        );
        dashboard.setVisible(true);
    }

    private JPanel buildQuestionPanel(String title, String[] options, java.awt.event.ActionListener onContinue) {
        JPanel center = new CardContainerPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        GradientTextLabel heading = new GradientTextLabel(title);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setForeground(Theme.TEXT_PRIMARY);
        heading.setGradient(Theme.BRAND_BLUE, Theme.BRAND_PINK);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        center.add(heading);
        center.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel optionsWrap = new JPanel();
        optionsWrap.setOpaque(false);
        optionsWrap.setLayout(new BoxLayout(optionsWrap, BoxLayout.Y_AXIS));

        ButtonGroup group = new ButtonGroup();
        for (String opt : options) {
            JToggleButton btn = createOptionButton(opt);
            group.add(btn);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(520, 44));
            btn.setPreferredSize(new Dimension(520, 44));
            optionsWrap.add(btn);
            optionsWrap.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        center.add(optionsWrap);
        center.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton continueBtn = createContinueButton();
        continueBtn.setEnabled(false);
        // Enable Continue when a selection is made and tint selected green
        for (Component c : optionsWrap.getComponents()) {
            if (c instanceof JToggleButton tb) {
                tb.addChangeListener(e -> {
                    boolean anySelected = findSelectedToggle(optionsWrap) != null;
                    continueBtn.setEnabled(anySelected);
                    tb.repaint();
                });
            }
        }
        continueBtn.addActionListener(onContinue);
        JPanel continueWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        continueWrap.setOpaque(false);
        continueWrap.add(continueBtn);
        center.add(continueWrap);

        return center;
    }

    private void triggerFade(String ignoredCardName) {
        // Best-effort: play fade on the currently showing card's FadeInPanel.
        for (Component c : root.getComponents()) {
            if (c.isShowing()) {
                FadeInPanel fade = findFadeInPanel(c);
                if (fade != null) fade.play();
            }
        }
    }

    private FadeInPanel findFadeInPanel(Component c) {
        if (c instanceof FadeInPanel) return (FadeInPanel) c;
        if (c instanceof Container) {
            for (Component ch : ((Container) c).getComponents()) {
                FadeInPanel f = findFadeInPanel(ch);
                if (f != null) return f;
            }
        }
        return null;
    }

    private JToggleButton createOptionButton(String text) {
        JToggleButton btn = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                // Fill: green when selected, slight white tint on hover, transparent otherwise
                Color fill;
                if (isSelected()) {
                    fill = new Color(46, 196, 182, 170);
                } else if (getModel().isRollover()) {
                    fill = new Color(255, 255, 255, 28);
                } else {
                    fill = new Color(255, 255, 255, 10);
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                // Outline: green when selected, white otherwise
                g2.setColor(isSelected() ? new Color(46, 196, 182) : new Color(255,255,255,200));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createContinueButton() {
        JButton b = new JButton("Continue");
        b.setFocusPainted(false);
        b.setBackground(new Color(255, 205, 60));
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(255, 196, 48)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(new Color(255, 205, 60)); }
        });
        return b;
    }

    private String getSelectedTextFrom(JButton continueButton) {
        // Find any selected toggle button within the current page
        Container parent = continueButton.getParent();
        while (parent != null && !(parent instanceof JPanel)) parent = parent.getParent();
        if (parent instanceof JPanel center) {
            JToggleButton selected = findSelectedToggle(center);
            return selected != null ? selected.getText() : null;
        }
        return null;
    }

    private JToggleButton findSelectedToggle(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JToggleButton tb && tb.isSelected()) return tb;
            if (c instanceof Container child) {
                JToggleButton found = findSelectedToggle(child);
                if (found != null) return found;
            }
        }
        return null;
    }
}


