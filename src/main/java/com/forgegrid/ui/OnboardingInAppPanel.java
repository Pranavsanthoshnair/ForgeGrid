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
	private final boolean isNewUser;
	private final String username;

	public OnboardingInAppPanel(CompletionListener listener) {
		this(listener, true, null);
	}

	public OnboardingInAppPanel(CompletionListener listener, boolean isNewUser, String username) {
		setLayout(new BorderLayout());
		setOpaque(false);
		this.isNewUser = isNewUser;
		this.username = username;
		flow = new CardLayout();
		root = new JPanel(flow);
		add(root, BorderLayout.CENTER);

		if (isNewUser) {
			// Show onboarding questions for new users
			root.add(buildQ1(() -> flow.show(root, "q2")), "q1");
			root.add(buildQ2(() -> flow.show(root, "q3")), "q2");
			root.add(buildQ3(() -> flow.show(root, "completion")), "q3");
			root.add(buildCompletionPanel(() -> { 
				if (listener != null) listener.onComplete(selectedGoal, selectedLanguage, selectedSkill); 
			}), "completion");
			flow.show(root, "q1");
		} else {
			// Show welcome back message for existing users
			root.add(buildWelcomeBackPanel(() -> { 
				if (listener != null) listener.onComplete("Welcome Back", "Returning User", "Existing"); 
			}), "welcome");
			flow.show(root, "welcome");
		}
	}

	private JPanel buildWelcomeBackPanel(Runnable continueAction) {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(new Color(25, 35, 55));
		
		JPanel center = new CardContainerPanel();
		center.setOpaque(false);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

		// Welcome back title
		GradientTextLabel welcomeTitle = new GradientTextLabel("Welcome back, " + (username != null ? username : "User") + "!");
		welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		welcomeTitle.setForeground(Theme.TEXT_PRIMARY);
		welcomeTitle.setGradient(Theme.BRAND_BLUE, Theme.BRAND_PINK);
		welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
		center.add(welcomeTitle);
		
		center.add(Box.createRigidArea(new Dimension(0, 20)));

		// Welcome message
		JLabel welcomeMessage = new JLabel("<html><div style='text-align: center;'>" +
			"<p style='font-size: 16px; color: #B4B8C8; margin: 10px 0;'>" +
			"Great to see you again! Your personalized dashboard is ready.</p>" +
			"<p style='font-size: 14px; color: #8A8FA3; margin: 10px 0;'>" +
			"Let's continue your journey on ForgeGrid.</p>" +
			"</div></html>");
		welcomeMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		center.add(welcomeMessage);
		
		center.add(Box.createRigidArea(new Dimension(0, 30)));

		// Continue button
		JButton continueBtn = createContinueButton();
		continueBtn.setText("Continue to Dashboard");
		continueBtn.addActionListener(e -> continueAction.run());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		JPanel continueWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
			@Override
			public Dimension getMaximumSize() {
				return getPreferredSize();
			}
		};
		continueWrap.setOpaque(false);
		continueBtn.setPreferredSize(new Dimension(280, 40));
		continueBtn.setMaximumSize(new Dimension(280, 40));
		continueBtn.setHorizontalAlignment(SwingConstants.CENTER);
		
        JComponent gradientButton = Theme.asGradientButton(continueBtn, Theme.BRAND_BLUE.darker(), Theme.BRAND_PINK.darker(), 20);
		Dimension contSize = new Dimension(280, 40);
		gradientButton.setPreferredSize(contSize);
		gradientButton.setMinimumSize(contSize);
		gradientButton.setMaximumSize(contSize);
		continueWrap.setPreferredSize(contSize);
		continueWrap.setMinimumSize(contSize);
		continueWrap.setMaximumSize(contSize);
		continueWrap.add(gradientButton);
		buttonPanel.add(continueWrap);
		
		center.add(buttonPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		bg.add(center, gbc);
		return bg;
	}

	private JPanel buildCompletionPanel(Runnable continueAction) {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(new Color(25, 35, 55));
		
		JPanel center = new CardContainerPanel();
		center.setOpaque(false);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

		// Completion title with username
		GradientTextLabel completionTitle = new GradientTextLabel("Hi " + (username != null ? username : "there") + "!");
		completionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		completionTitle.setForeground(Theme.TEXT_PRIMARY);
		completionTitle.setGradient(Theme.BRAND_BLUE, Theme.BRAND_PINK);
		completionTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
		center.add(completionTitle);
		
		center.add(Box.createRigidArea(new Dimension(0, 25)));

		// Motivational message
		JLabel motivationalMessage = new JLabel("<html><div style='text-align: center;'>" +
			"<p style='font-size: 18px; color: #B4B8C8; margin: 15px 0; font-weight: bold;'>" +
			"🎉 Welcome to ForgeGrid! 🎉</p>" +
			"<p style='font-size: 16px; color: #A0A8B8; margin: 12px 0;'>" +
			"Your journey to becoming a coding champion starts now!</p>" +
			"<p style='font-size: 15px; color: #8A8FA3; margin: 12px 0;'>" +
			"With your " + (selectedGoal != null ? selectedGoal.toLowerCase() : "chosen path") + " focus and " + 
			(selectedLanguage != null ? selectedLanguage : "programming") + " skills,</p>" +
			"<p style='font-size: 15px; color: #8A8FA3; margin: 12px 0;'>" +
			"you're ready to tackle any challenge that comes your way.</p>" +
			"<p style='font-size: 16px; color: #46C4B6; margin: 15px 0; font-weight: bold;'>" +
			"Let's build something amazing together! 💪</p>" +
			"</div></html>");
		motivationalMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		center.add(motivationalMessage);
		
		center.add(Box.createRigidArea(new Dimension(0, 35)));

		// Continue button
		JButton continueBtn = createContinueButton();
		continueBtn.setText("Continue to Dashboard");
		continueBtn.addActionListener(e -> continueAction.run());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		JPanel continueWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
			@Override
			public Dimension getMaximumSize() {
				return getPreferredSize();
			}
		};
		continueWrap.setOpaque(false);
		continueBtn.setPreferredSize(new Dimension(300, 45));
		continueBtn.setMaximumSize(new Dimension(300, 45));
		continueBtn.setHorizontalAlignment(SwingConstants.CENTER);
		
        JComponent gradientButton = Theme.asGradientButton(continueBtn, Theme.BRAND_BLUE.darker(), Theme.BRAND_PINK.darker(), 22);
		Dimension contSize = new Dimension(300, 45);
		gradientButton.setPreferredSize(contSize);
		gradientButton.setMinimumSize(contSize);
		gradientButton.setMaximumSize(contSize);
		continueWrap.setPreferredSize(contSize);
		continueWrap.setMinimumSize(contSize);
		continueWrap.setMaximumSize(contSize);
		continueWrap.add(gradientButton);
		buttonPanel.add(continueWrap);
		
		center.add(buttonPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		bg.add(center, gbc);
		return bg;
	}

	private JPanel buildQ1(Runnable next) {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(new Color(25, 35, 55));
		JPanel center = buildQuestionPanel(
			"Q1. What is your primary goal on ForgeGrid?",
			new String[]{
				"Learn programming fundamentals",
				"Prepare for technical interviews",
				"Practice competitive programming",
				"Build real-world projects"
			},
			value -> { selectedGoal = value; next.run(); },
			null
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
			"Q2. What's your preferred programming language?",
			new String[]{"Java", "Python", "C", "JavaScript"},
			value -> { selectedLanguage = value; next.run(); },
			() -> flow.show(root, "q1")
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
			value -> { selectedSkill = value; finish.run(); },
			() -> flow.show(root, "q2")
		);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
		bg.add(center, gbc);
		return bg;
	}

	private JPanel buildQuestionPanel(String title, String[] options, java.util.function.Consumer<String> onContinue, Runnable onBack) {
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
            btn.setMaximumSize(new Dimension(460, 36));
            btn.setPreferredSize(new Dimension(460, 36));
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
		
		// Button container with Continue and Back
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
        JPanel continueWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            public Dimension getMaximumSize() {
                // Prevent BoxLayout from stretching this row to full width
                return getPreferredSize();
            }
        };
		continueWrap.setOpaque(false);
        continueBtn.setPreferredSize(new Dimension(240, 34));
        continueBtn.setMaximumSize(new Dimension(240, 34));
		continueBtn.setHorizontalAlignment(SwingConstants.CENTER);
        // Use standardized theme gradient colors for consistency
        JComponent gradientButton = Theme.asGradientButton(continueBtn, Theme.BRAND_BLUE.darker(), Theme.BRAND_PINK.darker(), 18);
        Dimension contSize = new Dimension(240, 34);
        gradientButton.setPreferredSize(contSize);
        gradientButton.setMinimumSize(contSize);
        gradientButton.setMaximumSize(contSize);
        continueWrap.setPreferredSize(contSize);
        continueWrap.setMinimumSize(contSize);
        continueWrap.setMaximumSize(contSize);
        continueWrap.add(gradientButton);
		buttonPanel.add(continueWrap);
		
		// Add back button if onBack is provided
        if (onBack != null) {
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 12)));
            JButton backBtn = createBackButton();
			backBtn.addActionListener(e -> onBack.run());
			backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            JPanel backWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
                @Override
                public Dimension getMaximumSize() {
                    return getPreferredSize();
                }
            };
			backWrap.setOpaque(false);
            // Use the same approach as Continue button with Theme wrapper
            Dimension backSize = new Dimension(240, 34);
            backBtn.setPreferredSize(backSize);
            backBtn.setMaximumSize(backSize);
            JComponent backGradientButton = Theme.asGradientButton(backBtn, Theme.BRAND_BLUE.darker(), new Color(80, 90, 110), 18);
            backGradientButton.setPreferredSize(backSize);
            backGradientButton.setMinimumSize(backSize);
            backGradientButton.setMaximumSize(backSize);
            backWrap.setPreferredSize(backSize);
            backWrap.setMinimumSize(backSize);
            backWrap.setMaximumSize(backSize);
			backWrap.add(backGradientButton);
			buttonPanel.add(backWrap);
		}
		
		center.add(buttonPanel);
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
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(true);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        btn.setRolloverEnabled(false);
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
        b.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		b.setHorizontalAlignment(SwingConstants.CENTER);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return b;
	}
	
    private JButton createBackButton() {
        JButton b = new JButton("Back");
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}


