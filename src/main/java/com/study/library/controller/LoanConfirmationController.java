package com.study.library.controller;

import com.study.library.dao.BookLoanDao;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.SelectableBook;
import com.study.library.model.User;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LoanConfirmationController {

    @FXML
    private Label lblReaderInfo;
    @FXML
    private TableView<SelectableBook> tableBooks;
    @FXML
    private TableColumn<SelectableBook, Boolean> colSelect;
    @FXML
    private TableColumn<SelectableBook, Integer> colBookId;
    @FXML
    private TableColumn<SelectableBook, String> colBookTitle;
    @FXML
    private TableColumn<SelectableBook, LocalDate> colReturnDate;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnConfirm;

    @Setter
    private Runnable onLoanSuccess;

    private BookLoanDao bookLoanDao;

    private User currentReader;

    @FXML
    public void initialize() {
        connectDao();
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);

        colBookId.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getBook().getId()));
        colBookTitle.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getBook().getTitle()));
        colReturnDate.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());

        tableBooks.setEditable(true);
    }


    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bookLoanDao = new BookLoanDao(conn);
    }

    public void setReader(User reader) {
        this.currentReader = reader;
        lblReaderInfo.setText("Độc giả: " + reader.getName() + " (Mã: " + reader.getId() + ")");
    }

    public void setBookList(List<SelectableBook> books) {
        tableBooks.getItems().setAll(books);
    }

    @FXML
    private void onCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    @FXML
    private void onConfirm() {
        boolean isSaved = bookLoanDao.createLoans(currentReader, tableBooks.getItems());

        if (isSaved) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Xác nhận mượn");
            alert.setHeaderText(null);
            alert.setContentText("Đã xác nhận mượn " + tableBooks.getItems().size() + " sách cho độc giả.");
            alert.showAndWait();

            // Gọi callback nếu có
            if (onLoanSuccess != null) {
                onLoanSuccess.run();
            }

            // Đóng cửa sổ xác nhận
            ((Stage) btnConfirm.getScene().getWindow()).close();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu thông tin mượn sách");
            alert.setContentText("Đã xảy ra lỗi khi lưu vào cơ sở dữ liệu. Vui lòng thử lại.");
            alert.showAndWait();
        }
    }
}