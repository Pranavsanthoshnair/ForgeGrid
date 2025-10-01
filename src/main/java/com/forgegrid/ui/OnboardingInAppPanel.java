package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;

/**
 * In-app onboarding panel (same window) replicating OnboardingWizard steps.
 */
public class OnboardingInAppPanel extends JPanel {

	public interface CompletionListener {
		void onComplete(String goal, String language, String skill);
	}

	private final CardLayout flow;
	private final JPanel root;
	private String selectedGoal;
	private String selectedLanguage;
	private String selectedSkill;

	public OnboardingInAppPanel(CompletionListener listener) {
		setLayout(new BorderLayout());
		setOpaque(false);
		flow = new CardLayout();
		root = new JPanel(flow);
		add(root, BorderLayout.CENTER);

		root.add(buildQ1(() -> flow.show(root, "q2")), "q1");
		root.add(buildQ2(() -> flow.show(root, "q3")), "q2");
		root.add(buildQ3(() -> { if (listener != null) listener.onComplete(selectedGoal, selectedLanguage, selectedSkill); }), "q3");
		flow.show(root, "q1");
	}

	private JPanel buildQ1(Runnable next) {
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
			value -> { selectedGoal = value; next.run(); }
		);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		bg.add(center, gbc);
		return bg;
	}

	private JPanel buildQ2(Runnable next) {
		NeonBackgroundPanel bg = new NeonBackgroundPanel();
		bg.setLayout(new GridBagLayout());
		JPanel center = buildQuestionPanel(
			"Q2. What’s your preferred programming language?",
			new String[]{"Java", "Python", "C", "JavaScript"},
			value -> { selectedLanguage = value; next.run(); }
		);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		bg.add(center, gbc);
		return bg;
	}

	private JPanel buildQ3(Runnable finish) {
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
			value -> { selectedSkill = value; finish.run(); }
		);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		bg.add(center, gbc);
		return bg;
	}

	private JPanel buildQuestionPanel(String title, String[] options, java.util.function.Consumer<String> onContinue) {
		JPanel center = new CardContainerPanel();
		center.setOpaque(false);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

		GradientTextLabel heading = new GradientTextLabel(title);
		heading.setAlignmentX(Component.CENTER_ALIGNMENT);
		heading.setForeground(Theme.TEXT_PRIMARY);
		heading.setGradient(Theme.BRAND_BLUE, Theme.BRAND_PINK);
		heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
		center.add(heading);
		center.add(Box.createRigidArea(new Dimension(0, 18)));

		JPanel optionsWrap = new JPanel();
		optionsWrap.setOpaque(false);
		optionsWrap.setLayout(new BoxLayout(optionsWrap, BoxLayout.Y_AXIS));
		optionsWrap.setAlignmentX(Component.CENTER_ALIGNMENT);

		ButtonGroup group = new ButtonGroup();
		for (String opt : options) {
			JToggleButton btn = createOptionButton(opt);
			group.add(btn);
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			btn.setMaximumSize(new Dimension(520, 44));
			btn.setPreferredSize(new Dimension(520, 44));
			optionsWrap.add(btn);
			optionsWrap.add(Box.createRigidArea(new Dimension(0, 10)));
		}

		center.add(optionsWrap);
		center.add(Box.createRigidArea(new Dimension(0, 20)));

		JButton continueBtn = createContinueButton();
		continueBtn.setEnabled(false);
		for (Component c : optionsWrap.getComponents()) {
			if (c instanceof JToggleButton tb) {
				tb.addChangeListener(e -> {
					boolean anySelected = findSelectedToggle(optionsWrap) != null;
					continueBtn.setEnabled(anySelected);
					tb.repaint();
				});
			}
		}
		continueBtn.addActionListener(e -> {
			JToggleButton sel = findSelectedToggle(optionsWrap);
			if (sel != null) onContinue.accept(sel.getText());
		});
		JPanel continueWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
		continueWrap.setOpaque(false);
		continueWrap.add(Theme.asGradientButton(continueBtn, Theme.BRAND_YELLOW, Theme.BRAND_GOLD, 18));
		center.add(continueWrap);
		return center;
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

	private JToggleButton createOptionButton(String text) {
		JToggleButton btn = new JToggleButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int w = getWidth();
				int h = getHeight();
				Color fill;
				if (isSelected()) fill = new Color(46, 196, 182, 170);
				else if (getModel().isRollover()) fill = new Color(255, 255, 255, 28);
				else fill = new Color(255, 255, 255, 10);
				g2.setColor(fill);
				g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
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
		btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return btn;
	}

	private JButton createContinueButton() {
		JButton b = new JButton("Continue");
		b.setFocusPainted(false);
		b.setForeground(Color.WHITE);
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		b.setFont(new Font("Segoe UI", Font.BOLD, 13));
		b.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return b;
	}
}


