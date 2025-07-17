package com.study.library.dao;

import com.study.library.model.Author;
import com.study.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    // Thêm sách
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author_id, publisher, year_published, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId()); // Sử dụng ID của tác giả
            stmt.setString(3, book.getPublisher());
            stmt.setInt(4, book.getYearPublished());
            stmt.setInt(5, book.getTotalQuantity());
            stmt.setInt(6, book.getAvailableQuantity());
            stmt.executeUpdate();
        }
    }

    // Cập nhật sách
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author_id = ?, publisher = ?, year_published = ?, total_quantity = ?, available_quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId());
            stmt.setString(3, book.getPublisher());
            stmt.setInt(4, book.getYearPublished());
            stmt.setInt(5, book.getTotalQuantity());
            stmt.setInt(6, book.getAvailableQuantity());
            stmt.setInt(7, book.getId());
            stmt.executeUpdate();
        }
    }

    // Xóa sách
    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Tìm tất cả sách
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id, b.title, b.publisher, b.year_published, b.total_quantity, " +
                     "b.available_quantity, a.id AS author_id, a.name AS author_name " +
                     "FROM books b " +
                     "JOIN authors a ON b.author_id = a.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Author author = new Author(rs.getInt("author_id"), rs.getString("author_name"));
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        author,
                        rs.getString("publisher"),
                        rs.getInt("year_published"),
                        rs.getInt("total_quantity"),
                        rs.getInt("available_quantity")
                );
                books.add(book);
            }
        }
        return books;
    }

    // Tìm sách theo ID
    public Book getBookById(int id) throws SQLException {
        Book book = null;
        String sql = "SELECT " +
                     "b.id, b.title, b.publisher, b.year_published, b.total_quantity, " +
                     "b.available_quantity, a.id AS author_id, a.name AS author_name " +
                     "FROM books b " +
                     "JOIN authors a ON b.author_id = a.id " +
                     "WHERE b.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Author author = new Author(rs.getInt("author_id"), rs.getString("author_name"));
                    book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            author,
                            rs.getString("publisher"),
                            rs.getInt("year_published"),
                            rs.getInt("total_quantity"),
                            rs.getInt("available_quantity")
                    );
                }
            }
        }
        return book;
    }
}