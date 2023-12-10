package com.example.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SampleActionClass extends AnAction {
    private static final Logger logger = LogManager.getLogger(SampleActionClass.class);
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
                logger.error("An error occurred", ex);
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
                        logger.error("An error occurred", ex);
                        Messages.showMessageDialog("Failed to send Slack notification", "Error", Messages.getErrorIcon());
                    } catch (Exception ex) {
                        logger.error("An error occurred ", ex);
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
            logger.error("An error occurred", ex);
        }

        return foundIDs;
    }

    private void sendSlackNotification(List<String> jiraIDs) throws IOException {

        InputDialog inputDialog = new InputDialog(null);

        String[] input = inputDialog.showDialog();

        if (input == null || input.length != 3) {
            Messages.showMessageDialog("Invalid input provided.", "Error", Messages.getErrorIcon());
            return;
        }

        String jiraBaseUrl = input[0];
        String slackToken = input[1];
        String channelName = input[2];

        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(slackToken);

        StringBuilder jiraIDMessage = new StringBuilder();
        for (String id : jiraIDs) {
            jiraIDMessage.append("<").append(jiraBaseUrl).append(id).append("|").append(id).append(">").append("\n");
        }

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelName)
                .text("Found Jira IDs:\n" + jiraIDMessage)
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
