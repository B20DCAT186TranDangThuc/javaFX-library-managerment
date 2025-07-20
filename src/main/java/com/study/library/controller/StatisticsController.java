package com.study.library.controller;

import com.study.library.dao.BookLoanDao;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.BookStat;
import com.study.library.model.UserBorrowStats;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;

public class StatisticsController {

    @FXML
    private AnchorPane headerPane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private BarChart<String, Number> borrowChart;
    @FXML
    private TableView<BookStat> topBooksTable;
    @FXML
    private TableColumn<BookStat, String> colTitle;
    @FXML
    private TableColumn<BookStat, Integer> colCount;
    @FXML
    private TableView<UserBorrowStats> topUsersTable;

    @FXML
    private TableColumn<UserBorrowStats, String> colUserName;

    @FXML
    private TableColumn<UserBorrowStats, Integer> colUserBorrowCount;

    private BookLoanDao bookLoanDao;

    @FXML
    public void initialize() {
        setHeaderPane();
        connectDao();
        // Cấu hình bảng
        configTable();

        // Mặc định thống kê 10 ngày gần nhất
        fromDatePicker.setValue(LocalDate.now().minusDays(10));
        toDatePicker.setValue(LocalDate.now());
    }

    private void setHeaderPane() {
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("THỐNG KÊ");
        headerController.setIcon("fas-chart-bar");
        headerController.setIconColor("#3498db");
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

    private void configTable() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));
        colCount.setStyle("-fx-alignment: CENTER;");
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colUserBorrowCount.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));
        colUserBorrowCount.setStyle("-fx-alignment: CENTER;");
    }

    @FXML
    private void onGenerateStatistics() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        if (from == null || to == null || from.isAfter(to)) {
            showAlert("Ngày không hợp lệ", "Vui lòng chọn khoảng thời gian hợp lệ.");
            return;
        }

        loadBorrowChart(from, to);
        loadTopBooks(from, to);
        loadTopUsers(from, to);
    }

    private void loadBorrowChart(LocalDate from, LocalDate to) {
        borrowChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt mượn");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        // Lấy dữ liệu và parse thành LocalDate để sắp xếp
        Map<String, Integer> rawData = bookLoanDao.countBorrowsByDate(from, to);
        Map<LocalDate, Integer> sortedData = rawData.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> LocalDate.parse(e.getKey()),
                        Map.Entry::getValue,
                        (a, b) -> b,
                        TreeMap::new
                ));

        // Đưa dữ liệu vào chart
        for (Map.Entry<LocalDate, Integer> entry : sortedData.entrySet()) {
            String label = entry.getKey().format(formatter); // dd/MM
            series.getData().add(new XYChart.Data<>(label, entry.getValue()));
        }

        borrowChart.getData().add(series);
        borrowChart.setTitle("Lượt mượn theo ngày");

        // Cấu hình trục X và Y
        CategoryAxis xAxis = (CategoryAxis) borrowChart.getXAxis();
        xAxis.setLabel("Ngày");
        xAxis.setTickLabelRotation(-45); // Xoay để tránh đè label

        NumberAxis yAxis = (NumberAxis) borrowChart.getYAxis();
        yAxis.setLabel("Số lượt");
    }

    private void loadTopBooks(LocalDate from, LocalDate to) {
        List<BookStat> topBooks = bookLoanDao.getTopBorrowedBooks(from, to);
        topBooksTable.getItems().setAll(topBooks);
    }

    private void loadTopUsers(LocalDate from, LocalDate to) {
        List<UserBorrowStats> topUsers = bookLoanDao.getTopBorrowingUsers(from, to);
        topUsersTable.setItems(FXCollections.observableArrayList(topUsers));
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
