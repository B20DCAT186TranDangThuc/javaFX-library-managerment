package com.study.library.controller;

import com.study.library.dao.BookDAO;
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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import org.kordamp.ikonli.javafx.FontIcon;

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

    private UserDAO userDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        totalUserLabel.setText("Total users: " + users.size());
        ObservableList<User> observableBooks = FXCollections.observableArrayList(users);
        tableUser.setItems(observableBooks);
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
            Dialog<User> dialog = new Dialog<>();
            dialog.setDialogPane(new DialogPane());
            dialog.getDialogPane().setContent(dialogContent);
            dialog.getDialogPane().getButtonTypes().clear(); // Remove default buttons

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tableUser.getScene().getWindow());
            dialog.setResizable(true);

            // Set dialog title based on mode
            if (user == null) {
                dialog.setTitle("Thêm Người Dùng Mới");
            } else {
                dialog.setTitle("Chỉnh Sửa Người Dùng");
            }


            // Show dialog
            dialog.showAndWait();

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

}
