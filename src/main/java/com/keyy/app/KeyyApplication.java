package com.keyy.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KeyyApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize UserManager
        UserManager.initialize();

        // Load login screen
        FXMLLoader fxmlLoader = new FXMLLoader(KeyyApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);
        stage.setTitle("Keyy - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}