package com.study.library.controller;

import com.study.library.dao.UserDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Book;
import com.study.library.model.User;
import com.study.library.util.Gender;
import com.study.library.util.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private AnchorPane headerPane;
    @FXML
    private ComboBox<Status> cmbStatus;
    @FXML
    private ComboBox<Gender> cmbGender;
    @FXML
    private TableView<User> tableUser;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colGender;
    @FXML
    private TableColumn<User, String> colStatus;
    @FXML
    private TableColumn<User, Void> colActions;
    @FXML
    private Label totalUserLabel;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField txtSearch;

    private UserDAO userDao;

    private final  ObservableList<User> observableBooks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableUser.setItems(observableBooks);
        connectDao();
        setHeaderPane();
        setComboBoxSelect();
        setupButtonEvents();
        setupTableColumn();
        loadUserData(null);
    }

    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userDao = new UserDAO(conn);
    }

    private void setHeaderPane() {
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("QUẢN LÝ NGƯỜI MƯỢN");
        headerController.setIcon("fas-user");
        headerController.setIconColor("#3498db");
    }

    private void setComboBoxSelect() {
        cmbStatus.getItems().setAll(Status.values());
        cmbGender.getItems().setAll(Gender.values());
    }

    private void setupButtonEvents() {
        btnAdd.setOnAction(e -> showUserDialog(null));
    }

    private void setupTableColumn() {
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colGender.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGender()));
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        colId.setStyle("-fx-alignment: CENTER;");
        colName.setStyle("-fx-alignment: CENTER;");
        colGender.setStyle("-fx-alignment: CENTER;");
        colStatus.setStyle("-fx-alignment: CENTER;");

        // Tạo cột hành động với các nút
        colActions.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox actionBox = new HBox(3);

            {
                // Thiết lập icon cho các nút
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(12);
                editBtn.setGraphic(editIcon);
                editBtn.getStyleClass().add("edit-button");
                editBtn.setTooltip(new Tooltip("Chỉnh sửa"));

                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconSize(12);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.getStyleClass().add("delete-button");
                deleteBtn.setTooltip(new Tooltip("Xóa"));

                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(editBtn, deleteBtn);

                // Xử lý sự kiện
                editBtn.setOnAction(e -> {
                    User userModel = getTableView().getItems().get(getIndex());
                    showUserDialog(userModel);
                });

                deleteBtn.setOnAction(e -> {
                    User userModel = getTableView().getItems().get(getIndex());
                    //TODO: delete user
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void loadUserData(String data) {
        List<User> users = userDao.getAllUsers(data);
        observableBooks.clear();
        observableBooks.addAll(users);
        totalUserLabel.setText("Total users: " + users.size());

    }

    private void showUserDialog(User user) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/dialog-user.fxml"));
            BorderPane dialogContent = loader.load();

            // Get controller
            DialogUserController controller = loader.getController();
            controller.setUser(user); // null for create, User object for edit

            // Create custom dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle(user == null ? "Thêm độc giả" : "Sửa thông tin");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAdd.getScene().getWindow());

            Scene scene = new Scene(dialogContent);
            dialogStage.setScene(scene);
            controller.setDialogStage(dialogStage);
            dialogStage.setResizable(false);
            dialogStage.centerOnScreen();
            // Show dialog
            dialogStage.showAndWait();

            if (controller.isSaved()) {
                loadUserData(null);
            }


        } catch (IOException e) {
            showErrorAlert("Lỗi", "Không thể tải dialog người dùng: " + e.getMessage());
        }
    }

    private void showDeleteConfirmation(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + user.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteUser(user);
        }
    }

    private void deleteUser(User user) {

    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void search() {
        String data = txtSearch.getText();
        loadUserData(data);
    }

    public void clearSearch() {
        txtSearch.clear();
        loadUserData(null);
    }

}
