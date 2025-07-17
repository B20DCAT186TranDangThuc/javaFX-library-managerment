package com.study.library.controller;

import com.study.library.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML
    private AnchorPane headerPane;

    @FXML private TableView<Book> bookTable;

    @FXML private TableColumn<Book, Integer> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, Integer> yearColumn;
    @FXML private TableColumn<Book, Integer> availableQuantityColumn;
    @FXML private TableColumn<Book, String> statusColumn;
    @FXML private TableColumn<Book, Void> actionColumn;

    @FXML private Button btnAdd;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setHeaderPane();

        setupButtonEvents();
    }

    private void setupButtonEvents() {
        btnAdd.setOnAction(e -> showAddBookDialog());
    }

    private void showAddBookDialog() {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/book-dialog.fxml"));
            Parent root = loader.load();

            // Create stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm Sách Mới");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAdd.getScene().getWindow());

            // Set scene
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Configure dialog
            dialogStage.setResizable(false);
            dialogStage.centerOnScreen();

            // Show dialog
            dialogStage.showAndWait();

            // Refresh table after dialog closes
            // refreshBookList();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể mở dialog thêm sách: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setHeaderPane() {
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("QUẢN LÝ SÁCH");
        headerController.setIcon("fas-book");
        headerController.setIconColor("#3498db");
    }

}
