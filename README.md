
# IssueTagTracker IntelliJ Plugin

## Description

The IssueTagTracker plugin allows you to search for Jira issue IDs within your Java files and send notifications to Slack with the identified Jira IDs. This plugin provides a convenient way to streamline Jira ID tracking and notification within IntelliJ IDEA.

## Features

-   **Jira ID Finder:** Search for Jira IDs within Java files.
-   **Slack Integration:** Send Slack notifications containing found Jira IDs.
-   **Convenient UI:** Integrated dialog for entering Jira and Slack details.

## Installation

To install the plugin:

1. Download the plugin JAR file from the [Releases](https://github.com/TV-hackathon-2023/nc-rockers//releases) page.
2. Open IntelliJ IDEA.
3. Go to `File` > `Settings` > `Plugins`.
4. Click on the gear icon and select `Install Plugin from Disk...`.
5. Choose the downloaded JAR file and click `OK`.
6. Restart IntelliJ IDEA to activate the plugin.

## Creating a Slack app
**Here are the steps to obtain a Slack API token:**

1.  Go to the Slack API website: [https://api.slack.com/](https://api.slack.com/)
2.  Sign in to your Slack workspace.
3.  Create a new Slack app or use an existing one.
4.  Navigate to "OAuth & Permissions" in your app settings.
5.  Add the required scopes (e.g., `chat:write`) under "Bot Token Scopes".
6.  Install the app to your workspace.
7.  Copy the generated API token from the "OAuth & Permissions" section or the "Install App" page.
8. Add the app to the Slack channel where you want to get notification


## Usage

1.  Open a project in IntelliJ IDEA.
2.  Navigate to the menu `Tools` -> `IssueTagTracker`.
3.  Click on `IssueTagTracker` to initiate the Jira ID search process.
4.  If Jira IDs are found, a dialog prompts you to send a Slack notification.
5.  Enter your Jira base URL, Slack API token, and Slack channel name in the dialog.
6.  Click `Submit` to send the Slack notification or `Cancel` to abort..

## License

This project is licensed under the [MIT License]().