package com.study.library.controller;

import com.study.library.dao.UserDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.User;
import com.study.library.util.Gender;
import com.study.library.util.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DialogUserController implements Initializable {
    @FXML
    private ComboBox<Status> cmbStatus;
    @FXML
    private ComboBox<Gender> cmbGender;
    @FXML
    private Label headerTitle;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea addressField;
    @FXML
    private DatePicker dobField;
    @FXML
    private Label validationMessage;
    @FXML
    private ButtonType saveButtonType;

    @Setter
    private Stage dialogStage;
    private User currentUser;
    @Getter
    private boolean isEditMode = false;
    @Getter
    private boolean saved = false;
    private UserDAO userDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectDao();
        setComboBoxSelect();
        setupValidation();
        setupDatePicker();
    }

    private void connectDao() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userDao = new UserDAO(conn);
    }

    private void setComboBoxSelect() {
        cmbStatus.getItems().setAll(Status.values());
        cmbGender.getItems().setAll(Gender.values());
    }

    private void setupValidation() {
        // Add validation listeners
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            hideValidationMessage();
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            hideValidationMessage();
        });
    }

    private void setupDatePicker() {
        // Set date format
        dobField.setConverter(new StringConverter<LocalDate>() {
            private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ?
                        LocalDate.parse(string, dateFormatter) : null;
            }
        });
    }

    public void setUser(User user) {
        if (user == null) {
            // Create mode
            isEditMode = false;
            headerTitle.setText("Create New User");
            clearFields();
        } else {
            // Edit mode
            isEditMode = true;
            currentUser = user;
            headerTitle.setText("Edit User");
            populateFields(user);
        }

    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        dobField.setValue(null);
        cmbStatus.setValue(Status.ACTIVE);
        cmbGender.setValue(Gender.MALE);
        hideValidationMessage();
    }

    private void populateFields(User user) {
        idField.setText(String.valueOf(user.getId()));
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        addressField.setText(user.getAddress());

        // Handle date of birth
        if (user.getDob() != null && !user.getDob().isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(user.getDob());
                dobField.setValue(dob);
            } catch (Exception e) {
                dobField.setValue(null);
            }
        }

        cmbGender.setValue(Gender.fromDisplayName(user.getGender()));
        cmbStatus.setValue(Status.fromDisplayName(user.getStatus()));


        hideValidationMessage();
    }

    public boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        // Validate required fields
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("Name is required.\n");
        }

        // Validate email format if provided
        if (emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
            String email = emailField.getText().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.append("Please enter a valid email address.\n");
            }
        }

        // Validate phone format if provided
        if (phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) {
            String phone = phoneField.getText().trim();
            if (!phone.matches("^[0-9+\\-\\s\\(\\)]+$")) {
                errors.append("Please enter a valid phone number.\n");
            }
        }

        if (!errors.isEmpty()) {
            showValidationMessage(errors.toString());
            return false;
        }

        return true;
    }

    public User getUser() {
        User user = isEditMode ? currentUser : new User();

        if (isEditMode) {
            user.setId(Integer.parseInt(idField.getText()));
        }

        user.setName(nameField.getText().trim());
        user.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
        user.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
        user.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());

        // Handle date of birth
        if (dobField.getValue() != null) {
            user.setDob(dobField.getValue().toString());
        } else {
            user.setDob(null);
        }

        user.setGender(cmbGender.getValue().toString());
        user.setStatus(cmbStatus.getValue().toString());

        return user;
    }

    private void showValidationMessage(String message) {
        validationMessage.setText(message);
        validationMessage.setVisible(true);
        validationMessage.setManaged(true);
    }

    private void hideValidationMessage() {
        validationMessage.setVisible(false);
        validationMessage.setManaged(false);
    }

    public void onCancelClicked(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void onSaveClicked() throws SQLException {
        if (!validateInput()) {
            return;
        }
        User user = getUser();

        // TODO: Gọi DAO lưu vào DB nếu cần
        boolean success = userDao.saveOrUpdate(user);

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
}
