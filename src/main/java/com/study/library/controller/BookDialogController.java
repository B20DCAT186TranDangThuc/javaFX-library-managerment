package com.study.library.controller;

import com.study.library.dao.AuthorDAO;
import com.study.library.dao.BookDAO;
import com.study.library.dao.CategoryDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Author;
import com.study.library.model.Book;
import com.study.library.model.Category;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class BookDialogController implements Initializable {

    @FXML
    public FontIcon headerIcon;
    @FXML
    public Label headerTitle;
    @FXML
    public TextField txtId;
    @FXML
    public TextField txtTitle;
    @FXML
    public Label lblTitleError;
    @FXML
    public Button btnAddAuthor;
    @FXML
    public Label lblAuthorError;
    @FXML
    public TextField txtPublisher;
    @FXML
    public Label lblPublisherError;
    @FXML
    public Spinner spnYear;
    @FXML
    public Label lblYearError;
    @FXML
    public Spinner spnTotalQuantity;
    @FXML
    public Label lblTotalQuantityError;
    @FXML
    public Spinner spnAvailableQuantity;
    @FXML
    public CheckBox chkSameAsTotal;
    @FXML
    public Label lblAvailableQuantityError;
    @FXML
    public TextArea txtDescription;
    @FXML
    public TextField txtLocation;
    @FXML
    public Button btnCancel;
    @FXML
    public Button btnSave;
    @FXML
    private ComboBox<Author> cmbAuthor;
    @FXML
    private ComboBox<Category> cmbCategory;

    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    private BookDAO bookDAO;
    private boolean isEditMode = false;
    private Book editingBook;

    @Getter
    private boolean saved = false;
    @Setter
    private Stage dialogStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectDao();
        loadListAuthor();
        loadListCategory();
        setupSpinners();
        bindSameAsTotalCheckbox();
    }

    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        authorDAO = new AuthorDAO(conn);
        categoryDAO = new CategoryDAO(conn);
        bookDAO = new BookDAO(conn);
    }

    private void loadListCategory() {
        List<Category> categories = categoryDAO.getAll();
        cmbCategory.getItems().addAll(categories);
        cmbCategory.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category author) {
                return author != null ? author.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                // Optional - thường không cần nhập tay
                return cmbCategory.getItems()
                        .stream()
                        .filter(a -> a.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadListAuthor() {
        List<Author> authors = authorDAO.getAllAuthors();
        cmbAuthor.getItems().setAll(authors);

        cmbAuthor.setConverter(new StringConverter<Author>() {
            @Override
            public String toString(Author author) {
                return author != null ? author.getName() : "";
            }

            @Override
            public Author fromString(String string) {
                // Optional - thường không cần nhập tay
                return cmbAuthor.getItems()
                        .stream()
                        .filter(a -> a.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

    }

    public void setEditMode(Book book) {
        if (book == null) return;

        isEditMode = true;
        editingBook = book;

        headerTitle.setText("Chỉnh sửa sách");
        headerIcon.setIconLiteral("fas-edit");

        txtId.setText(String.valueOf(book.getId()));
        txtTitle.setText(book.getTitle());
        cmbAuthor.setValue(book.getAuthor());
        cmbCategory.setValue(book.getCategory());
        txtPublisher.setText(book.getPublisher());
        spnYear.getValueFactory().setValue(book.getYearPublished());
        spnTotalQuantity.getValueFactory().setValue(book.getTotalQuantity());
        spnAvailableQuantity.getValueFactory().setValue(book.getAvailableQuantity());
        txtDescription.setText(book.getDescription());
        txtLocation.setText(book.getLocation());
    }

    private void setupSpinners() {
        spnYear.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1800, 2100, 2020));
        spnTotalQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        spnAvailableQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1));
    }

    private void bindSameAsTotalCheckbox() {
        chkSameAsTotal.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            spnAvailableQuantity.setDisable(isNowSelected);
            if (isNowSelected) {
                spnAvailableQuantity.getValueFactory().setValue(spnTotalQuantity.getValue());
            }
        });

        // Cập nhật available khi total thay đổi (nếu đang chọn same as total)
        spnTotalQuantity.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (chkSameAsTotal.isSelected()) {
                spnAvailableQuantity.getValueFactory().setValue(newVal);
            }
        });
    }

    @FXML
    private void onCancelClicked() {
        dialogStage.close();
    }

    @FXML
    private void onSaveClicked() {
        if (!validateForm()) {
            showAlert("Cảnh báo", "Vui lòng điền đầy đủ thông tin");
            return;
        };

        Book book = isEditMode ? editingBook : new Book();

        book.setTitle(txtTitle.getText());
        book.setAuthor(cmbAuthor.getValue());
        book.setCategory(cmbCategory.getValue());
        book.setPublisher(txtPublisher.getText());
        book.setYearPublished((Integer) spnYear.getValue());
        book.setTotalQuantity((Integer) spnTotalQuantity.getValue());
        book.setAvailableQuantity((Integer) spnAvailableQuantity.getValue());
        book.setDescription(txtDescription.getText());
        book.setLocation(txtLocation.getText());

        // TODO: Gọi DAO lưu vào DB nếu cần
        boolean success = bookDAO.saveOrUpdate(book);

        if (success) {
            saved = true;
            dialogStage.close();
        } else {
            showAlert("Lỗi", "Không thể lưu sách vào cơ sở dữ liệu.");
        }

        saved = true;
        dialogStage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateForm() {
        boolean isValid = true;
        clearErrors();

        if (txtTitle.getText().isEmpty()) {
            lblTitleError.setText("Tiêu đề không được để trống");
            isValid = false;
        }

        if (cmbAuthor.getValue() == null) {
            lblAuthorError.setText("Tác giả không được để trống");
            isValid = false;
        }

        if (txtPublisher.getText().isEmpty()) {
            lblPublisherError.setText("Nhà xuất bản không được để trống");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        lblTitleError.setText("");
        lblAuthorError.setText("");
        lblPublisherError.setText("");
    }

}
