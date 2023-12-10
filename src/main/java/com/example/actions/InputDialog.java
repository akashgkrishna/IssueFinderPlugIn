package com.example.actions;

import javax.swing.*;
import java.awt.*;

public class InputDialog extends JDialog {
    private final JTextField jiraBaseUrlField = new JTextField(30);
    private final JTextField slackTokenField = new JTextField(30);
    private final JTextField channelNameField = new JTextField(30);

    private boolean submitClicked = false;

    public InputDialog(Frame parent) {
        super(parent, "Enter Details", true);
        JPanel panel = new JPanel(new GridLayout(0, 1));

        panel.add(new JLabel("Enter your Jira base URL (e.g., https://yourcompany.atlassian.net/browse/):"));
        panel.add(jiraBaseUrlField);
        panel.add(new JLabel("Enter your Slack API token:"));
        panel.add(slackTokenField);
        panel.add(new JLabel("Enter the Slack channel name:"));
        panel.add(channelNameField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            submitClicked = true;
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    public String[] showDialog() {
        setVisible(true);

        if (submitClicked) {
            return new String[]{
                    jiraBaseUrlField.getText(),
                    slackTokenField.getText(),
                    channelNameField.getText()
            };
        }

        return null;
    }
}
