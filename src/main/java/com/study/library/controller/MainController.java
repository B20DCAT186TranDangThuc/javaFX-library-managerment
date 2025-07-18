package com.study.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class MainController {
    @FXML
    private StackPane contentArea;

    private void setContent(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        showHome();
    }

    @FXML
    private void showHome() {
        setContent("/com/study/library/fxml/home-view.fxml");
    }

    @FXML
    private void showBooks() {
        setContent("/com/study/library/fxml/book-view.fxml");
    }

    @FXML
    private void showBorrowers() {
        setContent("/com/study/library/fxml/user-view.fxml");
    }

    @FXML
    private void showLoanReturn() {
        setContent("/com/study/library/fxml/book-loan-view.fxml");
    }

    @FXML
    private void showStatistics() {
        setContent("/com/study/library/fxml/statistics-view.fxml");
    }
}
