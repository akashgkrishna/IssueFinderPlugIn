package com.example.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleActionClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String filePath = "/Users/testvagrant/Desktop/Better/LearnFramework/src/test/java/web/test/SampleTest.java";
        findAndShowJiraIDs(filePath);
    }

    private void findAndShowJiraIDs(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;
            Pattern commentPattern = Pattern.compile("//.*|/\\*(.|\\n)*?\\*/");
            Pattern jiraIDPattern = Pattern.compile("\\b[A-Z]{2,}-\\d+\\b");

            while ((line = br.readLine()) != null) {
                Matcher commentMatcher = commentPattern.matcher(line);
                while (commentMatcher.find()) {
                    String comment = commentMatcher.group();

                    Matcher jiraIDMatcher = jiraIDPattern.matcher(comment);
                    while (jiraIDMatcher.find()) {
                        String jiraID = jiraIDMatcher.group();
                        Messages.showInfoMessage("Found Jira ID in comment: " + jiraID, "Jira ID");
                        // You can perform other actions with the Jira ID here
                    }
                }
                lineNumber++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
