package com.study.library.controller;

import com.study.library.dao.BookDAO;
import com.study.library.dao.BookLoanDao;
import com.study.library.dao.UserDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Book;
import com.study.library.model.BookLoan;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeController implements Initializable {

    @FXML
    private Label lblTotalBooks;
    @FXML
    private Label lblBorrowedBooks;
    @FXML
    private Label lblTotalUsers;
    @FXML
    private Label lblOverdueBooks;
    @FXML
    private ListView<String> listRecentBooks;
    @FXML
    private ListView<String> listRecentLoans;
    @FXML
    private AnchorPane headerPane;

    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BookLoanDao bookLoanDao;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectDao();
        // Lấy HeaderController
        setHeaderPane();
        loadStatistics();
        loadRecentActivities();
    }

    private void setHeaderPane() {
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("TRANG CHỦ");
        headerController.setIcon("fas-home");
        headerController.setIconColor("#3498db");
    }

    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bookDAO = new BookDAO(conn);
        userDAO = new UserDAO(conn);
        bookLoanDao = new BookLoanDao(conn);
    }

    /**
     * Tải thống kê tổng quan
     */
    private void loadStatistics() {
        Long totalBooks = bookDAO.getTotalBooks();
        Long borrowedBooks = bookLoanDao.getTotalBorrowingBooks();
        Long totalUsers = userDAO.getTotalUsers();
        Long overdueBooks = bookLoanDao.getOverdueBooksCount();

        lblTotalBooks.setText(String.valueOf(totalBooks));
        lblBorrowedBooks.setText(String.valueOf(borrowedBooks));
        lblTotalUsers.setText(String.valueOf(totalUsers));
        lblOverdueBooks.setText(String.valueOf(overdueBooks));
    }

    /**
     * Tải hoạt động gần đây
     */
    private void loadRecentActivities() {
        loadRecentBooks();
        loadRecentLoans();
    }

    private void loadRecentBooks() {
        List<Book> recentBooks = bookDAO.getLatestBooks(5);

        List<String> titles = recentBooks.stream()
                .map(book -> {
                    return book.getTitle() + " - " + book.getCreateAt();
                })
                .collect(Collectors.toList());

        listRecentBooks.setItems(FXCollections.observableArrayList(titles));
    }

    private void loadRecentLoans() {
        List<BookLoan> recentLoans = bookLoanDao.getRecentLoans(5);

        List<String> loanInfo = recentLoans.stream()
                .map(loan -> String.format("%s - \"%s\" - %s",
                        loan.getUser().getName(),
                        loan.getBook().getTitle(),
                        loan.getBorrowDate()))
                .collect(Collectors.toList());

        listRecentLoans.setItems(FXCollections.observableArrayList(loanInfo));
    }
}