package com.keyy.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LeaderboardController {

    @FXML
    private VBox rootVBox;

    @FXML
    private VBox leaderboardContainer;

    @FXML
    private Button backBtn;

    @FXML
    private Button darkModeBtn;

    @FXML
    private Label userStatsLabel;

    private boolean isDarkMode = false;
    private String currentUsername;

    @FXML
    public void initialize() {
        darkModeBtn.setText("🌙");
        darkModeBtn.setOnAction(e -> toggleDarkMode());
        backBtn.setOnAction(e -> goBackToTyping());
    }

    public void setCurrentUser(String username) {
        this.currentUsername = username;
        loadLeaderboard();
        loadUserStats();
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            darkModeBtn.setText("☀️");
            rootVBox.setStyle("-fx-background-color: #323437; -fx-padding: 40;");
        } else {
            darkModeBtn.setText("🌙");
            rootVBox.setStyle("-fx-background-color: #f5f7fa; -fx-padding: 40;");
        }

        loadLeaderboard(); // Reload with new colors
    }

    private void loadLeaderboard() {
        leaderboardContainer.getChildren().clear();

        List<Map.Entry<String, Double>> leaderboard = UserManager.getLeaderboard();

        int rank = 1;
        for (Map.Entry<String, Double> entry : leaderboard) {
            String username = entry.getKey();
            double wpm = entry.getValue();

            HBox row = createLeaderboardRow(rank, username, wpm);
            leaderboardContainer.getChildren().add(row);
            rank++;
        }

        if (leaderboard.isEmpty()) {
            Label emptyLabel = new Label("No players yet. Be the first!");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #646669;");
            leaderboardContainer.getChildren().add(emptyLabel);
        }
    }

    private HBox createLeaderboardRow(int rank, String username, double wpm) {
        HBox row = new HBox(30);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPrefWidth(600);

        String bgColor = isDarkMode ? "#2d2d2d" : "white";
        String textColor = isDarkMode ? "#d1d0c5" : "#323437";
        String rankColor = getRankColor(rank);

        // Highlight current user
        if (username.equals(currentUsername)) {
            bgColor = isDarkMode ? "#3d3d3d" : "#fff9e6";
        }

        row.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 15; -fx-background-radius: 10; -fx-border-radius: 10;");

        // Rank
        Label rankLabel = new Label(getRankIcon(rank) + " #" + rank);
        rankLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + rankColor + "; -fx-min-width: 80;");

        // Username
        Label usernameLabel = new Label(username + (username.equals(currentUsername) ? " (You)" : ""));
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-min-width: 200;");

        // WPM
        Label wpmLabel = new Label(String.format("%.1f WPM", wpm));
        wpmLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #e2b714; -fx-font-weight: bold;");

        // Attempts
        int attempts = UserManager.getTotalAttempts(username);
        Label attemptsLabel = new Label(attempts + " attempts");
        attemptsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #646669;");

        row.getChildren().addAll(rankLabel, usernameLabel, wpmLabel, attemptsLabel);

        return row;
    }

    private String getRankIcon(int rank) {
        switch (rank) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return "";
        }
    }

    private String getRankColor(int rank) {
        switch (rank) {
            case 1: return "#FFD700"; // Gold
            case 2: return "#C0C0C0"; // Silver
            case 3: return "#CD7F32"; // Bronze
            default: return "#646669";
        }
    }

    private void loadUserStats() {
        if (currentUsername == null) return;

        double bestWPM = UserManager.getBestWPM(currentUsername);
        double avgWPM = UserManager.getAverageWPM(currentUsername);
        int attempts = UserManager.getTotalAttempts(currentUsername);

        String textColor = isDarkMode ? "#d1d0c5" : "#323437";

        String stats = String.format(
                "Your Stats: Best WPM: %.1f | Average WPM: %.1f | Total Attempts: %d",
                bestWPM, avgWPM, attempts
        );

        userStatsLabel.setText(stats);
        userStatsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + textColor + "; -fx-font-weight: bold;");
    }

    private void goBackToTyping() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Keyy - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}