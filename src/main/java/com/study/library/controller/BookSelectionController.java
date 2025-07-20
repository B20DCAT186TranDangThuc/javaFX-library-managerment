package com.study.library.controller;

import com.study.library.model.Book;
import com.study.library.model.SelectableBook;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookSelectionController {

    @FXML
    private TableView<SelectableBook> tableBooks;

    @FXML
    private TableColumn<SelectableBook, Boolean> colSelect;

    @FXML
    private TableColumn<SelectableBook, Integer> colBookId;

    @FXML
    private TableColumn<SelectableBook, String> colBookTitle;

    @FXML
    private TableColumn<SelectableBook, Integer> colAvailable;

    @FXML
    private TableColumn<SelectableBook, LocalDate> colReturnDate;

    private final ObservableList<SelectableBook> selectableBooks = FXCollections.observableArrayList();

    private BookSelectionHandler selectionHandler;
    @Getter
    private boolean canceled = true; // mặc định là huỷ

    public interface BookSelectionHandler {
        void onBooksSelected(List<SelectableBook> selectedBooks);
    }

    public void setBookSelectionHandler(BookSelectionHandler handler) {
        this.selectionHandler = handler;
    }

    @FXML
    public void initialize() {
        setupColumns();
        tableBooks.setItems(selectableBooks);
    }

    private void setupColumns() {
        // Cột checkbox
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));

        // Cột ID sách
        colBookId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getBook().getId()).asObject()
        );

        // Cột tiêu đề sách
        colBookTitle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBook().getTitle())
        );

        // Cột số lượng còn lại
        colAvailable.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getBook().getAvailableQuantity()).asObject()
        );

        // Cột ngày trả (DatePicker trong cell)
        colReturnDate.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());
        colReturnDate.setCellFactory(column -> new TableCell<>() {
            private final DatePicker datePicker = new DatePicker();

            {
                datePicker.setPrefWidth(120);
                datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
                    SelectableBook item = getTableRow().getItem();
                    if (item != null) {
                        item.setReturnDate(newDate);
                    }
                });
            }

            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    datePicker.setValue(date);
                    setGraphic(datePicker);
                }
            }
        });
        tableBooks.setEditable(true);
        colSelect.setEditable(true);
    }

    public void setBookList(List<Book> books) {
        List<SelectableBook> selectableList = books.stream()
                .map(SelectableBook::new)
                .collect(Collectors.toList());

        // Lắng nghe sự kiện chọn/bỏ chọn chỉ 1 lần duy nhất
        for (SelectableBook item : selectableList) {
            item.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    System.out.println("✅ Đã chọn: " + item.getBook().getTitle());
                } else {
                    System.out.println("❌ Bỏ chọn: " + item.getBook().getTitle());
                }
            });
        }

        selectableBooks.setAll(selectableList);
    }

    @FXML
    private void onConfirm() {
        List<SelectableBook> selected = selectableBooks.stream()
                .filter(SelectableBook::isSelected)
                .collect(Collectors.toList());

        if (selectionHandler != null) {
            selectionHandler.onBooksSelected(selected);
        }

        closeDialog();
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) tableBooks.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
