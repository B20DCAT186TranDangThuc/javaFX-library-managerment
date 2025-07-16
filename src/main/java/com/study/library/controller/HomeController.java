package com.study.library.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HomeController implements Initializable {

    @FXML
    private Label lblCurrentTime;
    @FXML private Label lblTotalBooks;
    @FXML private Label lblBorrowedBooks;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblOverdueBooks;
    @FXML private ListView<String> listRecentBooks;
    @FXML private ListView<String> listRecentLoans;

    private Timer timeTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startClock();
        loadStatistics();
        loadRecentActivities();
    }

    /**
     * Bắt đầu đồng hồ thời gian thực
     */
    private void startClock() {
        timeTimer = new Timer();
        timeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy - HH:mm:ss");
                    lblCurrentTime.setText(now.format(formatter));
                });
            }
        }, 0, 1000); // Cập nhật mỗi giây
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