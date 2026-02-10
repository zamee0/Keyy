package com.keyy.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML
    private VBox rootVBox;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label bestWpmLabel;

    @FXML
    private Label avgWpmLabel;

    @FXML
    private Label avgAccuracyLabel;

    @FXML
    private Label totalAttemptsLabel;

    @FXML
    private Label streakLabel;

    @FXML
    private Button newTestBtn;

    @FXML
    private Button progressBtn;

    @FXML
    private Button leaderboardBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button darkModeBtn;

    private String currentUsername;
    private boolean isDarkMode = false;

    // Color schemes
    private final String LIGHT_BG = "#f5f7fa";
    private final String LIGHT_CARD = "white";
    private final String LIGHT_TEXT = "#323437";

    private final String DARK_BG = "#323437";
    private final String DARK_CARD = "#2d2d2d";
    private final String DARK_TEXT = "#d1d0c5";

    @FXML
    public void initialize() {
        darkModeBtn.setText("🌙");
        darkModeBtn.setOnAction(e -> toggleDarkMode());

        newTestBtn.setOnAction(e -> startNewTest());
        progressBtn.setOnAction(e -> showProgress());
        leaderboardBtn.setOnAction(e -> showLeaderboard());
        settingsBtn.setOnAction(e -> showSettings());
        logoutBtn.setOnAction(e -> logout());
    }

    public void setCurrentUser(String username) {
        this.currentUsername = username;
        loadUserStats();
    }

    private void loadUserStats() {
        welcomeLabel.setText("Welcome back, " + currentUsername + "! 👋");

        // Load statistics
        double bestWPM = UserManager.getBestWPM(currentUsername);
        double avgWPM = UserManager.getAverageWPM(currentUsername);
        int totalAttempts = UserManager.getTotalAttempts(currentUsername);
        double avgAccuracy = calculateAverageAccuracy();
        int streak = calculateStreak();

        // Update labels
        bestWpmLabel.setText(String.format("%.1f", bestWPM));
        avgWpmLabel.setText(String.format("%.1f", avgWPM));
        avgAccuracyLabel.setText(String.format("%.1f%%", avgAccuracy));
        totalAttemptsLabel.setText(String.valueOf(totalAttempts));
        streakLabel.setText(streak + " days");
    }

    private double calculateAverageAccuracy() {
        List<ScoreRecord> scores = UserManager.getUserScores(currentUsername);
        if (scores.isEmpty()) return 0.0;

        return scores.stream()
                .mapToDouble(ScoreRecord::getAccuracy)
                .average()
                .orElse(0.0);
    }

    private int calculateStreak() {
        List<ScoreRecord> scores = UserManager.getUserScores(currentUsername);
        if (scores.isEmpty()) return 0;

        // Simple implementation: count unique practice days in last 30 days
        long uniqueDays = scores.stream()
                .map(ScoreRecord::getDate)
                .distinct()
                .count();

        return (int) uniqueDays;
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            darkModeBtn.setText("☀️");
            rootVBox.setStyle("-fx-background-color: " + DARK_BG + "; -fx-padding: 40;");
        } else {
            darkModeBtn.setText("🌙");
            rootVBox.setStyle("-fx-background-color: " + LIGHT_BG + "; -fx-padding: 40;");
        }
    }

    private void startNewTest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("test-mode-selection.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            TestModeSelectionController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Keyy - Select Test Mode");
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to direct typing test
            startDirectTest();
        }
    }

    private void startDirectTest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("keyy-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            KeyyController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Keyy - Typing Test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showProgress() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("progress-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            ProgressController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Keyy - My Progress");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("leaderboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            LeaderboardController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Keyy - Leaderboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            SettingsController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Keyy - Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        try {
            LoginController.setCurrentUsername(null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 700);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Keyy - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}