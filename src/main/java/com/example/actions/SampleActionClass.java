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

public class SampleActionClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String filePath = "/Users/testvagrant/Desktop/Better/LearnFramework/src/test/java/web/test/SampleTest.java";
        findAndSendSlackNotification(filePath);
    }

    private void findAndSendSlackNotification(String filePath) {
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
            Messages.showInfoMessage("No Jira IDs found.", "Jira IDs");
        }
    }

    private void sendSlackNotification(List<String> jiraIDs) throws IOException {
        String slackToken = "xoxb-10189127591-6318842254149-qJT53zuncqjgovxiFCTu9Jr7";

        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(slackToken);

        String jiraIDMessage = String.join(", ", jiraIDs);

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel("jira_issues")
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
