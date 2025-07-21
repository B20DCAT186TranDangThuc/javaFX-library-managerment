package com.study.library.controller;


import com.study.library.dao.BookDAO;
import com.study.library.dao.BookLoanDao;
import com.study.library.dao.UserDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Book;
import com.study.library.model.BookLoan;
import com.study.library.model.SelectableBook;
import com.study.library.model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private List<SelectableBook> selectableBookList = new ArrayList<>();
    private ObservableList<BookLoan> currentLoans;

    // DAOs
    private UserDAO userDao;
    private BookDAO bookDao;
    private BookLoanDao loanDao;

    private ObservableList<BookLoan> loanList = FXCollections.observableArrayList();

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


    public void onSearchBook() {
        String keyword = txtSearchBook.getText();
        if (keyword.isEmpty()) {
            showAlert("Warning", "Vui lòng nhập mã hoặc tên sách.", Alert.AlertType.WARNING);
            return;
        }

        List<Book> books = bookDao.findBookByIdOrTitle(keyword);
        if (books.isEmpty()) {
            showAlert("Infomation", "Không tìm thấy sách.", Alert.AlertType.INFORMATION);
        } else {
            showBookSelectionDialog(books); // nếu nhiều kết quả
        }

    }

    public void showBookSelectionDialog(List<Book> books) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/book-selection-view.fxml"));
            Parent root = loader.load();

            BookSelectionController controller = loader.getController();

            // Truyền danh sách sách có thể mượn
            controller.setBookList(books);

            controller.setBookSelectionHandler(selectedBooks -> {
                selectableBookList.clear();
                // xử lý danh sách sách được chọn tại đây
                selectableBookList.addAll(selectedBooks);

                System.out.println(selectableBookList.size());
                System.out.println(currentReader);
            });

            Stage stage = new Stage();
            stage.setTitle("Chọn sách để mượn");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // 👉 Sau khi dialog chọn sách đóng, mở cửa sổ xác nhận
            if (controller.isCanceled()) {
                return;
            }
            if (!selectableBookList.isEmpty() && currentReader != null) {
                showLoanConfirmationDialog(currentReader, selectableBookList);
            } else {
                showAlert("Cảnh báo", "Thiếu thông tin độc giả hoặc chưa chọn sách", Alert.AlertType.WARNING);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoanConfirmationDialog(User reader, List<SelectableBook> selectedBooks) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/loan-confirmation-view.fxml"));
            Parent root = loader.load();

            LoanConfirmationController controller = loader.getController();
            controller.setReader(reader);
            controller.setBookList(selectedBooks);

            // Truyền callback để reload danh sách sách mượn sau khi mượn thành công
            controller.setOnLoanSuccess(() -> {
                loadBorrowedBooksByUser(currentReader.getId());
            });

            Stage stage = new Stage();
            stage.setTitle("Xác nhận mượn sách");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClearReader(ActionEvent actionEvent) {
        txtReader.clear();
        tableLoanBooks.getItems().clear();
        lblLoanCount.setText("0 cuốn");
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
                currentReader = selectedUser;
                txtReader.setText(selectedUser.getName()); // hoặc gán ID/Email gì đó
                loadBorrowedBooksByUser(selectedUser.getId());
                System.out.println(currentReader);
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooksByUser(int userId) {
        loanList.clear();

        List<BookLoan> borrowedBooks = loanDao.getBorrowedBooksByUserId(userId);
        lblLoanCount.setText(borrowedBooks.size() + " cuốn");
        loanList.addAll(borrowedBooks);
        tableLoanBooks.setItems(loanList);

        setupColumns(); // cấu hình column nếu chưa setup
    }

    private void setupColumns() {
        colBookId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBook().getId()).asObject());
        colBookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        colLoanDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowDate()));
        colDueDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnDate()));

        colStatus.setCellValueFactory(cellData -> {
            boolean returned = cellData.getValue().isReturned();
            return new SimpleStringProperty(returned ? "Đã trả" : "Đang mượn");
        });

        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button returnBtn = new Button("Trả sách");

            {
                returnBtn.setOnAction(event -> {
                    BookLoan loan = getTableView().getItems().get(getIndex());

                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Xác nhận trả sách");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Bạn có chắc chắn muốn trả sách \"" + loan.getBook().getTitle() + "\" không?");

                    Optional<ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        handleReturnBook(loan);
                    }
                });
                returnBtn.getStyleClass().add("table-return-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(returnBtn);
                }
            }
        });

    }

    private void handleReturnBook(BookLoan loan) {
        loan.setReturned(true);
        loanDao.updateReturnStatus(loan.getId(), true);
        loadBorrowedBooksByUser(loan.getUser().getId());
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
