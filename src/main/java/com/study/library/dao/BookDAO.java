package com.study.library.dao;

import com.study.library.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
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
    public List<Book> getAllBooks() throws SQLException {
        return null;
    }

    // Tìm sách theo ID
    public Book getBookById(int id) throws SQLException {
       return null;
    }
}