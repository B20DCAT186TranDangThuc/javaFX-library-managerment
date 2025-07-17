package com.study.library.dao;

import com.study.library.model.Author;
import com.study.library.model.Book;
import com.study.library.model.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    // Thêm sách
    public void addBook(Book book) throws SQLException {

    }

    // Cập nhật sách
    public void updateBook(Book book) throws SQLException {

    }

    // Xóa sách
    public void deleteBook(int id) throws SQLException {

    }

    // Tìm tất cả sách
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, a.id AS author_id, a.name AS author_name, " +
                     "c.id AS category_id, c.name AS category_name " +
                     "FROM books b " +
                     "LEFT JOIN authors a ON b.author_id = a.id " +
                     "LEFT JOIN categories c ON b.category_id = c.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Author
                Author author = null;
                int authorId = rs.getInt("author_id");
                if (!rs.wasNull()) {
                    author = new Author(authorId, rs.getString("author_name"));
                }

                // Category
                Category category = null;
                int categoryId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    category = new Category(categoryId, rs.getString("category_name"));
                }

                // Book
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        author,
                        category,
                        rs.getString("publisher"),
                        rs.getInt("year_published"),
                        rs.getInt("total_quantity"),
                        rs.getInt("available_quantity")
                );

                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Tìm sách theo ID
    public Book getBookById(int id) throws SQLException {
       return null;
    }
}