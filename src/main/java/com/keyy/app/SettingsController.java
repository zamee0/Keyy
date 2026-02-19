
package com.keyy.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    private VBox rootVBox;

    @FXML
    private Button backBtn;

    @FXML
    private Button darkModeBtn;

    @FXML
    private Label usernameLabel;

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
        usernameLabel.setText("Username: " + username);
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