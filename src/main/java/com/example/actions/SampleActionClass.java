package com.example.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
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
        findAndShowJiraIDs(filePath);
    }

    private void findAndShowJiraIDs(String filePath) {
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
            // Replace printStackTrace with more robust logging
            ex.printStackTrace();
            return;
        }

        // Display all found IDs together with a dialog including a button
        if (!foundIDs.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String id : foundIDs) {
                message.append("Found Jira ID in comment: ").append(id).append("\n");
            }

            int choice = Messages.showOkCancelDialog(message.toString() + "\nClick OK for an additional action.",
                    "Jira IDs", "OK", "Cancel", Messages.getInformationIcon());

            if (choice == Messages.OK) {
                // Perform additional action on OK button click
                Messages.showInfoMessage("Hello Clicked!", "Hello");
            }
        } else {
            Messages.showInfoMessage("No Jira IDs found.", "Jira IDs");
        }
    }
}
