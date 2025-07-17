package com.study.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Label lblTotalBooks;
    @FXML private Label lblBorrowedBooks;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblOverdueBooks;
    @FXML private ListView<String> listRecentBooks;
    @FXML private ListView<String> listRecentLoans;

    @FXML private AnchorPane headerPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lấy HeaderController
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("TRANG CHỦ");
        headerController.setIcon("fas-home");
        headerController.setIconColor("#3498db");
        loadStatistics();
        loadRecentActivities();
    }

    /**
     * Tải thống kê tổng quan
     */
    private void loadStatistics() {

    }

    /**
     * Tải hoạt động gần đây
     */
    private void loadRecentActivities() {
        loadRecentBooks();
        loadRecentLoans();
    }

    /**
     * Tải danh sách sách mới thêm (5 sách gần nhất)
     */
    private void loadRecentBooks() {

    }

    /**
     * Tải danh sách mượn sách gần đây (5 lần mượn gần nhất)
     */
    private void loadRecentLoans() {
    }

    /**
     * Thao tác nhanh: Thêm sách mới
     */
    @FXML
    private void addNewBook() {

    }

    /**
     * Thao tác nhanh: Thêm người mượn mới
     */
    @FXML
    private void addNewBorrower() {

    }

    /**
     * Thao tác nhanh: Mượn sách
     */
    @FXML
    private void borrowBook() {

    }

    /**
     * Thao tác nhanh: Xem thống kê
     */
    @FXML
    private void viewStatistics() {

    }

}