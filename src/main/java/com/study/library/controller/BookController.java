package com.study.library.controller;

import com.study.library.dao.BookDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Book;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML
    private AnchorPane headerPane;

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    @FXML
    private TableColumn<Book, Integer> availableQuantityColumn;
    @FXML
    private TableColumn<Book, String> statusColumn;
    @FXML
    private TableColumn<Book, Void> actionColumn;
    @FXML
    Label totalBooksLabel;

    private BookDAO bookDAO;

    @FXML
    private Button btnAdd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectDao();
        setHeaderPane();
        setupButtonEvents();

        setupTableColumns();
        loadBookData();
    }

    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bookDAO = new BookDAO(conn);
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

    private void loadBookData() {
        List<Book> books = bookDAO.getAllBooks();
        ObservableList<Book> observableBooks = FXCollections.observableArrayList(books);
        bookTable.setItems(observableBooks);
        totalBooksLabel.setText("Tổng: " + books.size() + " sách");
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("yearPublished"));
        availableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        statusColumn.setCellValueFactory(cellData -> {
            int available = cellData.getValue().getAvailableQuantity();
            String status = available > 0 ? "Có sẵn" : "Hết";
            return new ReadOnlyStringWrapper(status);
        });

        // Căn giữa các cột số
        idColumn.setStyle("-fx-alignment: CENTER;");
        yearColumn.setStyle("-fx-alignment: CENTER;");
        availableQuantityColumn.setStyle("-fx-alignment: CENTER;");
        statusColumn.setStyle("-fx-alignment: CENTER;");

        // Tùy chỉnh hiển thị cho cột trạng thái
        statusColumn.setCellFactory(param -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if ("Còn hàng".equals(item)) {
                        getStyleClass().removeAll("status-out-of-stock");
                        getStyleClass().add("status-available");
                    } else if ("Hết hàng".equals(item)) {
                        getStyleClass().removeAll("status-available");
                        getStyleClass().add("status-out-of-stock");
                    }
                }
            }
        });

        // Tạo cột hành động với các nút
        actionColumn.setCellFactory(param -> new TableCell<Book, Void>() {
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
                    Book bookModel = getTableView().getItems().get(getIndex());

                });

                deleteBtn.setOnAction(e -> {
                    Book bookModel = getTableView().getItems().get(getIndex());

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

}
