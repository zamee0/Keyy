package com.keyy.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private VBox rootVBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Label messageLabel;
    @FXML private Button darkModeBtn;

    private boolean isDarkMode = false;
    private static String currentUsername = null;

    @FXML
    public void initialize() {
        UserManager.initialize();

        darkModeBtn.setText("🌙");
        darkModeBtn.setOnAction(e -> toggleDarkMode());
        loginBtn.setOnAction(e -> handleLogin());
        registerBtn.setOnAction(e -> handleRegister());
        passwordField.setOnAction(e -> handleLogin());
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            darkModeBtn.setText("☀️");
            rootVBox.setStyle("-fx-background-color: #323437; -fx-padding: 40;");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #d1d0c5;");
        } else {
            darkModeBtn.setText("🌙");
            rootVBox.setStyle("-fx-background-color: #f5f7fa; -fx-padding: 40;");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #646669;");
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password!", "#f87171");
            return;
        }

        if (UserManager.loginUser(username, password)) {
            currentUsername = username;
            showMessage("Login successful! Loading...", "#4ade80");
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(this::loadTypingScreen);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showMessage("Invalid username or password!", "#f87171");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password!", "#f87171");
            return;
        }

        if (username.length() < 3) {
            showMessage("Username must be at least 3 characters!", "#f87171");
            return;
        }

        if (password.length() < 4) {
            showMessage("Password must be at least 4 characters!", "#f87171");
            return;
        }

        if (UserManager.registerUser(username, password)) {
            showMessage("Registration successful! Please login.", "#4ade80");
            passwordField.clear();
        } else {
            showMessage("Username already exists!", "#f87171");
        }
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    private void loadTypingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);

            // Pass username to DashboardController
            DashboardController controller = loader.getController();
            controller.setCurrentUser(currentUsername);

            Stage stage = (Stage) rootVBox.getScene().getWindow();

            // Save maximize state BEFORE changing scene
            boolean wasMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Keyy - Dashboard");

            // CRITICAL FIX: Use Platform.runLater to restore maximize
            javafx.application.Platform.runLater(() -> {
                if (wasMaximized) {
                    stage.setMaximized(false);  // First set to false
                    stage.setMaximized(true);   // Then set to true (forces refresh)
                }
                stage.centerOnScreen();  // Optional: center the window
            });

        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error loading dashboard!", "#f87171");
        }
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
}