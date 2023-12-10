package com.example.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SampleActionClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String basePath = e.getProject().getBasePath();
        if (basePath != null) {
            List<String> foundIDs = new ArrayList<>();

            try {
                Files.walk(Paths.get(basePath))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> foundIDs.addAll(findJiraIDsInFile(path.toString())));
            } catch (IOException ex) {
                ex.printStackTrace(); // Replace with proper logging
                return;
            }

            if (!foundIDs.isEmpty()) {
                StringBuilder message = new StringBuilder();
                for (String id : foundIDs) {
                    message.append("Found Jira ID in comment: ").append(id).append("\n");
                }

                int choice = Messages.showOkCancelDialog(message + "\nClick OK to send a Slack notification.",
                        "Jira IDs", "OK", "Cancel", Messages.getInformationIcon());

                if (choice == Messages.OK) {
                    try {
                        sendSlackNotification(foundIDs);
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Replace with proper logging
                        Messages.showMessageDialog("Failed to send Slack notification", "Error", Messages.getErrorIcon());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Messages.showMessageDialog("Failed to send Slack notification", "Error", Messages.getErrorIcon());
                    }
                }
            } else {
                Messages.showInfoMessage("No Jira IDs found in Java files.", "Jira IDs");
            }
        }
    }

    private List<String> findJiraIDsInFile(String filePath) {
        List<String> foundIDs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Pattern commentPattern = Pattern.compile("//.*|/\\*(.|\\n)*?\\*/");
            Pattern jiraIDPattern = Pattern.compile("\\b[A-Z]{2,}-\\d+\\b");

            while ((line = br.readLine()) != null) {
                Matcher commentMatcher = commentPattern.matcher(line);
                while (commentMatcher.find()) {
                    String comment = commentMatcher.group();

                    Matcher jiraIDMatcher = jiraIDPattern.matcher(comment);
                    while (jiraIDMatcher.find()) {
                        String jiraID = jiraIDMatcher.group();
                        foundIDs.add(jiraID);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(); // Replace with proper logging
        }

        return foundIDs;
    }

    private void sendSlackNotification(List<String> jiraIDs) throws IOException {
        String slackToken = Messages.showInputDialog("Enter your Slack API token:", "Slack Token", Messages.getQuestionIcon());
        if (slackToken == null || slackToken.isEmpty()) {
            Messages.showMessageDialog("Slack token cannot be empty.", "Error", Messages.getErrorIcon());
            return;
        }

        String channelName = Messages.showInputDialog("Enter the Slack channel name:", "Slack Channel", Messages.getQuestionIcon());
        if (channelName == null || channelName.isEmpty()) {
            Messages.showMessageDialog("Channel name cannot be empty.", "Error", Messages.getErrorIcon());
            return;
        }

        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(slackToken);

        String jiraIDMessage = String.join(", ", jiraIDs);

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelName)
                .text("Found Jira IDs: " + jiraIDMessage)
                .build();

        try {
            ChatPostMessageResponse response = methods.chatPostMessage(request);
            if (!response.isOk()) {
                throw new IOException("Failed to send Slack notification: " + response.getError());
            }

            Messages.showMessageDialog("Slack notification sent!", "Success", Messages.getInformationIcon());
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException("Failed to send Slack notification", ex);
        }
    }

}
