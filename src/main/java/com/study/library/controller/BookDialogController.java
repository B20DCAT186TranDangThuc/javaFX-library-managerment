package com.study.library.controller;

import com.study.library.dao.AuthorDAO;
import com.study.library.dao.CategoryDAO;
import com.study.library.database.DatabaseConnection;
import com.study.library.model.Author;
import com.study.library.model.Category;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class BookDialogController implements Initializable {

    @FXML
    private ComboBox<Author> cmbAuthor;
    @FXML
    private ComboBox<Category> cmbCategory;

    private  AuthorDAO authorDAO;

    private CategoryDAO categoryDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectDao();
        loadListAuthor();
        loadListCategory();
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
    }

    private void loadListCategory() {
        List<Category> categories = categoryDAO.getAll();
        cmbCategory.getItems().addAll(categories);
    }

    private void loadListAuthor(){
        List<Author> authors = authorDAO.getAllAuthors();
        cmbAuthor.getItems().setAll(authors);

    }
}
