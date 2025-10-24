package com.forgegrid.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * In-app onboarding panel that asks three quick questions (goal, language,
 * skill) for new users, or shows a concise welcome-back screen for returning
 * users. Emits selections via {@link CompletionListener}.
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
        // Basic light background like other screens
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(UIManager.getColor("Panel.background"));

        // Simple white card container
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        // Title
        JLabel title = new JLabel("Welcome back, " + (username != null ? username : "User") + "!");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Message
        JLabel msg1 = new JLabel("Great to see you again! Your personalized dashboard is ready.");
        msg1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msg1.setForeground(Color.DARK_GRAY);
        msg1.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(msg1);

        JLabel msg2 = new JLabel("Let's continue your journey on ForgeGrid.");
        msg2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        msg2.setForeground(Color.GRAY);
        msg2.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(msg2);

        card.add(Box.createRigidArea(new Dimension(0, 18)));

        // Continue button (plain pink)
        JButton continueBtn = new JButton("Continue to Dashboard");
        continueBtn.setUI(new BasicButtonUI());
        continueBtn.setBackground(Theme.BRAND_PINK);
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setBorderPainted(false);
        continueBtn.setFocusPainted(false);
        Dimension btnSize = new Dimension(320, 40);
        continueBtn.setPreferredSize(btnSize);
        continueBtn.setMaximumSize(btnSize);
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.addActionListener(e -> continueAction.run());
        card.add(continueBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        bg.add(card, gbc);
        return bg;
    }

	private JPanel buildCompletionPanel(Runnable continueAction) {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(UIManager.getColor("Panel.background"));
		
        JPanel center = new JPanel();
        center.setOpaque(true);
        center.setBackground(Color.WHITE);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

		// Completion title with username
        JLabel completionTitle = new JLabel("Hi " + (username != null ? username : "there") + "!");
        completionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        completionTitle.setForeground(Color.BLACK);
        completionTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        center.add(completionTitle);
		
		center.add(Box.createRigidArea(new Dimension(0, 25)));

		// Motivational message
        JLabel motivationalMessage = new JLabel("Welcome to ForgeGrid! Let's get started.", SwingConstants.CENTER);
        motivationalMessage.setFont(new Font("SansSerif", Font.PLAIN, 14));
        motivationalMessage.setForeground(Color.DARK_GRAY);
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
		
        continueBtn.setBackground(Theme.BRAND_PINK);
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setBorderPainted(false);
        continueBtn.setFocusPainted(false);
        Dimension contSize = new Dimension(300, 45);
        continueWrap.setPreferredSize(contSize);
        continueWrap.setMinimumSize(contSize);
        continueWrap.setMaximumSize(contSize);
        continueWrap.add(continueBtn);
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
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(UIManager.getColor("Panel.background"));
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
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setOpaque(true);
        bg.setBackground(new Color(238, 238, 238));
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

        JLabel heading = new JLabel(title);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setForeground(Color.BLACK);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
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
        continueBtn.setBackground(Theme.BRAND_PINK);
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setBorderPainted(false);
        continueBtn.setFocusPainted(false);
        Dimension contSize = new Dimension(240, 34);
        continueWrap.setPreferredSize(contSize);
        continueWrap.setMinimumSize(contSize);
        continueWrap.setMaximumSize(contSize);
        continueWrap.add(continueBtn);
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
            Dimension backSize = new Dimension(240, 34);
            backBtn.setPreferredSize(backSize);
            backBtn.setMaximumSize(backSize);
            backBtn.setBackground(new Color(80, 90, 110));
            backBtn.setForeground(Color.WHITE);
            backBtn.setBorderPainted(false);
            backBtn.setFocusPainted(false);
            backWrap.setPreferredSize(backSize);
            backWrap.setMinimumSize(backSize);
            backWrap.setMaximumSize(backSize);
            backWrap.add(backBtn);
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
        return b;
    }
	
    private JButton createBackButton() {
        JButton b = new JButton("Back");
        b.setFocusPainted(false);
        return b;
    }
}


