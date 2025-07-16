package com.study.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MailApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load giao diện chính (menu điều hướng)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);

        // Load icon
        Image appIcon = new Image(getClass().getResourceAsStream("/com/study/library/images/library-icon.jpg"));
        stage.getIcons().add(appIcon);

        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
