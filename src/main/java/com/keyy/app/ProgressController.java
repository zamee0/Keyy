package com.keyy.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgressController {

    @FXML
    private VBox rootVBox;

    @FXML
    private Button backBtn;

    @FXML
    private Button darkModeBtn;

    @FXML
    private LineChart<String, Number> wpmChart;

    @FXML
    private LineChart<String, Number> accuracyChart;

    @FXML
    private Label totalTestsLabel;

    @FXML
    private Label bestWpmLabel;

    @FXML
    private Label avgWpmLabel;

    @FXML
    private Label improvementLabel;

    @FXML
    private Label consistencyLabel;

    private String currentUsername;
    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        darkModeBtn.setText("🌙");
        darkModeBtn.setOnAction(e -> toggleDarkMode());
        backBtn.setOnAction(e -> goBackToDashboard());
    }

    public void setCurrentUser(String username) {
        this.currentUsername = username;
        loadProgress();
    }

    private void loadProgress() {
        List<ScoreRecord> scores = UserManager.getUserScores(currentUsername);

        if (scores.isEmpty()) {
            showEmptyState();
            return;
        }

        // Load WPM chart
        loadWPMChart(scores);

        // Load Accuracy chart
        loadAccuracyChart(scores);

        // Load statistics
        loadStatistics(scores);
    }

    private void loadWPMChart(List<ScoreRecord> scores) {
        wpmChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("WPM Progress");

        // Group by date and get average WPM per day
        Map<LocalDate, Double> dailyAvg = scores.stream()
                .collect(Collectors.groupingBy(
                        ScoreRecord::getDate,
                        Collectors.averagingDouble(ScoreRecord::getWpm)
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        dailyAvg.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String date = entry.getKey().format(formatter);
                    series.getData().add(new XYChart.Data<>(date, entry.getValue()));
                });

        wpmChart.getData().add(series);
    }

    private void loadAccuracyChart(List<ScoreRecord> scores) {
        accuracyChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Accuracy Progress");

        // Group by date and get average accuracy per day
        Map<LocalDate, Double> dailyAvg = scores.stream()
                .collect(Collectors.groupingBy(
                        ScoreRecord::getDate,
                        Collectors.averagingDouble(ScoreRecord::getAccuracy)
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        dailyAvg.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String date = entry.getKey().format(formatter);
                    series.getData().add(new XYChart.Data<>(date, entry.getValue()));
                });

        accuracyChart.getData().add(series);
    }

    private void loadStatistics(List<ScoreRecord> scores) {
        // Total tests
        totalTestsLabel.setText(String.valueOf(scores.size()));

        // Best WPM
        double bestWPM = scores.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .max()
                .orElse(0.0);
        bestWpmLabel.setText(String.format("%.1f WPM", bestWPM));

        // Average WPM
        double avgWPM = scores.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .average()
                .orElse(0.0);
        avgWpmLabel.setText(String.format("%.1f WPM", avgWPM));

        // Improvement rate
        double improvement = calculateImprovement(scores);
        improvementLabel.setText(String.format("%+.1f%% improvement", improvement));
        improvementLabel.setStyle(improvement >= 0
                ? "-fx-font-size: 18px; -fx-text-fill: #4ade80; -fx-font-weight: bold;"
                : "-fx-font-size: 18px; -fx-text-fill: #f87171; -fx-font-weight: bold;");

        // Consistency score
        double consistency = calculateConsistency(scores);
        consistencyLabel.setText(String.format("%.0f%% consistent", consistency));
    }

    private double calculateImprovement(List<ScoreRecord> scores) {
        if (scores.size() < 2) return 0.0;

        // Compare first 5 tests vs last 5 tests
        int compareSize = Math.min(5, scores.size() / 2);

        List<ScoreRecord> firstTests = scores.subList(0, Math.min(compareSize, scores.size()));
        List<ScoreRecord> lastTests = scores.subList(
                Math.max(0, scores.size() - compareSize),
                scores.size()
        );

        double firstAvg = firstTests.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .average()
                .orElse(0.0);

        double lastAvg = lastTests.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .average()
                .orElse(0.0);

        if (firstAvg == 0) return 0.0;

        return ((lastAvg - firstAvg) / firstAvg) * 100;
    }

    private double calculateConsistency(List<ScoreRecord> scores) {
        if (scores.isEmpty()) return 0.0;

        double avgWPM = scores.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .average()
                .orElse(0.0);

        // Count tests within 10% of average
        long consistentTests = scores.stream()
                .filter(s -> Math.abs(s.getWpm() - avgWPM) <= avgWPM * 0.1)
                .count();

        return (consistentTests * 100.0) / scores.size();
    }

    private void showEmptyState() {
        totalTestsLabel.setText("0");
        bestWpmLabel.setText("No data yet");
        avgWpmLabel.setText("Start practicing!");
        improvementLabel.setText("Take your first test");
        consistencyLabel.setText("-");
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

    private void goBackToDashboard() {
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