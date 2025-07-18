package com.study.library.controller;


import com.study.library.dao.BookDAO;
import com.study.library.dao.BookLoanDao;
import com.study.library.dao.UserDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Book;
import com.study.library.model.BookLoan;
import com.study.library.model.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class BookLoanController implements Initializable {
    @FXML
    private AnchorPane headerPane;


    // Main container
    @FXML
    private StackPane mainContainer;

    // Reader section
    @FXML
    private TextField txtReader;
    @FXML
    private Button btnSearchReader;
    @FXML
    private Button btnClearReader;
    @FXML
    private Label lblReaderName;
    @FXML
    private Label lblReaderEmail;
    @FXML
    private Label lblReaderActiveLoans;
    @FXML
    private Label lblReaderStatus;

    // Loan books table
    @FXML
    private TableView<BookLoan> tableLoanBooks;
    @FXML
    private TableColumn<BookLoan, Integer> colBookId;
    @FXML
    private TableColumn<BookLoan, String> colBookTitle;
    @FXML
    private TableColumn<BookLoan, String> colAuthor;
    @FXML
    private TableColumn<BookLoan, String> colLoanDate;
    @FXML
    private TableColumn<BookLoan, String> colDueDate;
    @FXML
    private TableColumn<BookLoan, String> colStatus;
    @FXML
    private TableColumn<BookLoan, Void> colActions;
    @FXML
    private Label lblLoanCount;

    // Add book section
    @FXML
    private TextField txtSearchBook;
    @FXML
    private Button btnSearchBook;
    @FXML
    private Button btnAddBookToLoan;
    @FXML
    private Label lblSelectedBookTitle;
    @FXML
    private Label lblSelectedBookAuthor;
    @FXML
    private Label lblSelectedBookAvailable;

    // Action buttons
    @FXML
    private Button btnSaveLoan;
    @FXML
    private Button btnReturnBooks;
    @FXML
    private Button btnRemoveBookFromLoan;
    @FXML
    private Button btnClearAll;

    // Data
    private User currentReader;
    private Book selectedBook;
    private ObservableList<BookLoan> currentLoans;

    // DAOs
    private UserDAO userDao;
    private BookDAO bookDao;
    private BookLoanDao loanDao;

    // Constants
    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectDao();
        setHeaderPane();
    }

    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bookDao = new BookDAO(conn);
        userDao = new UserDAO(conn);
        loanDao = new BookLoanDao(conn);
    }

    private void setHeaderPane() {
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("QUẢN LÝ MƯỢN TRẢ");
        headerController.setIcon("fas-exchange-alt");
        headerController.setIconColor("#3498db");
    }


    public void onSearchBook(ActionEvent actionEvent) {
    }

    public void onAddBookToLoan(ActionEvent actionEvent) {
    }

    public void onSaveLoan(ActionEvent actionEvent) {
    }

    public void onReturnBooks(ActionEvent actionEvent) {
    }

    public void onRemoveBookFromLoan(ActionEvent actionEvent) {
    }

    public void onClearAll(ActionEvent actionEvent) {
    }

    public void onClearReader(ActionEvent actionEvent) {
    }

    public void onSearchReader() {
        String keyword = txtReader.getText().trim();

        if (keyword.isEmpty()) {
            showAlert("Warning", "Vui lòng nhập mã hoặc tên độc giả.", Alert.AlertType.WARNING);
            return;
        }

        List<User> res = userDao.findUserByIdOrName(keyword);

        if (res.isEmpty()) {
            showAlert("Infomation", "Không tìm thấy độc giả nào.", Alert.AlertType.INFORMATION);
        } else {
            showReaderSelectionDialog(res); // nếu nhiều kết quả
        }
    }

    private void showReaderSelectionDialog(List<User> users) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/user-selection-dialog.fxml"));
            BorderPane pane = loader.load();

            UserSelectionController controller = loader.getController();
            controller.setUsers(users);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chọn độc giả");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(pane));
            controller.setDialogStage(dialogStage);

            // Nhận kết quả user từ dialog
            controller.setOnUserSelected(selectedUser -> {
                txtReader.setText(selectedUser.getName()); // hoặc gán ID/Email gì đó
                loadBorrowedBooksByUser(selectedUser.getId());
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooksByUser(int userId) {
        List<BookLoan> borrowedBooks = loanDao.getBorrowedBooksByUserId(userId);
        // Gán dữ liệu vào table mượn sách
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
