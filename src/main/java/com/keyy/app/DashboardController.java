package com.keyy.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private VBox rootVBox;
    @FXML private Label welcomeLabel;
    @FXML private Label bestWpmLabel;
    @FXML private Label avgWpmLabel;
    @FXML private Label avgAccuracyLabel;
    @FXML private Label totalAttemptsLabel;
    @FXML private Label streakLabel;
    @FXML private Button newTestBtn;
    @FXML private Button progressBtn;
    @FXML private Button leaderboardBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    @FXML private Button darkModeBtn;

    private String currentUsername;
    private boolean isDarkMode = false;

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

        double bestWPM = UserManager.getBestWPM(currentUsername);
        double avgWPM = UserManager.getAverageWPM(currentUsername);
        int totalAttempts = UserManager.getTotalAttempts(currentUsername);
        double avgAccuracy = calculateAverageAccuracy();
        int streak = calculateStreak();

        bestWpmLabel.setText(String.format("%.1f", bestWPM));
        avgWpmLabel.setText(String.format("%.1f", avgWPM));
        avgAccuracyLabel.setText(String.format("%.1f%%", avgAccuracy));
        totalAttemptsLabel.setText(String.valueOf(totalAttempts));
        streakLabel.setText(streak + " days");
    }

    private double calculateAverageAccuracy() {
        List<ScoreRecord> scores = UserManager.getUserScores(currentUsername);
        if (scores.isEmpty()) return 0.0;
        return scores.stream().mapToDouble(ScoreRecord::getAccuracy).average().orElse(0.0);
    }

    private int calculateStreak() {
        List<ScoreRecord> scores = UserManager.getUserScores(currentUsername);
        if (scores.isEmpty()) return 0;
        return (int) scores.stream().map(ScoreRecord::getDate).distinct().count();
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
    }

    // ========== NAVIGATION METHODS WITH MAXIMIZE FIX ==========

    private void startNewTest() {
        loadScene("keyy-view.fxml", "Keyy - Typing Test", KeyyController.class);
    }

    private void showProgress() {
        loadScene("progress-view.fxml", "Keyy - My Progress", ProgressController.class);
    }

    private void showLeaderboard() {
        loadScene("leaderboard-view.fxml", "Keyy - Leaderboard", LeaderboardController.class);
    }

    private void showSettings() {
        loadScene("settings-view.fxml", "Keyy - Settings", SettingsController.class);
    }

    private void logout() {
        try {
            LoginController.setCurrentUsername(null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 700);

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Keyy - Login");

            // CRITICAL FIX: Platform.runLater with toggle
            final boolean shouldMaximize = wasMaximized;
            javafx.application.Platform.runLater(() -> {
                if (shouldMaximize) {
                    stage.setMaximized(false);
                    stage.setMaximized(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HELPER METHOD - Handles all scene loading with maximize fix
    private <T> void loadScene(String fxmlFile, String title, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Set username if controller supports it
            Object controller = loader.getController();

            if (controller instanceof KeyyController) {
                ((KeyyController) controller).setCurrentUser(currentUsername);
            } else if (controller instanceof ProgressController) {
                ((ProgressController) controller).setCurrentUser(currentUsername);
            } else if (controller instanceof LeaderboardController) {
                ((LeaderboardController) controller).setCurrentUser(currentUsername);
            } else if (controller instanceof SettingsController) {
                ((SettingsController) controller).setCurrentUser(currentUsername);
            }

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle(title);

            // CRITICAL FIX: Platform.runLater with toggle
            final boolean shouldMaximize = wasMaximized;
            javafx.application.Platform.runLater(() -> {
                if (shouldMaximize) {
                    stage.setMaximized(false);  // Force toggle
                    stage.setMaximized(true);   // Then maximize
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}