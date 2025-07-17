package com.study.library.dao;

import com.study.library.model.Author;
import com.study.library.model.Book;
import com.study.library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean saveOrUpdate(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        } else {
            return update(book);
        }
    }


    private boolean insert(Book book) {
        String sql = "INSERT INTO books (title, author_id, category_id, publisher, year_published, total_quantity, available_quantity, description, location) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId());
            stmt.setInt(3, book.getCategory().getId());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYearPublished());
            stmt.setInt(6, book.getTotalQuantity());
            stmt.setInt(7, book.getAvailableQuantity());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getLocation());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Thêm sách thất bại, không có dòng nào được chèn.");
            }

            // Lấy ID tự sinh
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Không thể lấy ID sau khi thêm sách.");
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean update(Book book) {
        String sql = "UPDATE books SET title=?, author_id=?, category_id=?, publisher=?, year_published=?, total_quantity=?, available_quantity=?, description=?, location=? " +
                     "WHERE id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthor().getId());
            stmt.setInt(3, book.getCategory().getId());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYearPublished());
            stmt.setInt(6, book.getTotalQuantity());
            stmt.setInt(7, book.getAvailableQuantity());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getLocation());
            stmt.setInt(10, book.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa sách
    public boolean deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm tất cả sách
    public List<Book> getAllBooks(String data) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT b.*, a.id AS author_id, a.name AS author_name, " +
                "c.id AS category_id, c.name AS category_name " +
                "FROM books b " +
                "LEFT JOIN authors a ON b.author_id = a.id " +
                "LEFT JOIN categories c ON b.category_id = c.id "
        );

        boolean hasFilter = data != null && !data.trim().isEmpty();
        if (hasFilter) {
            sql.append("WHERE b.title LIKE ? OR a.name LIKE ? OR c.name LIKE ?");
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            if (hasFilter) {
                String keyword = "%" + data.trim() + "%";
                stmt.setString(1, keyword);
                stmt.setString(2, keyword);
                stmt.setString(3, keyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author(
                            rs.getInt("author_id"),
                            rs.getString("author_name")
                    );
                    Category category = new Category(
                            rs.getInt("category_id"),
                            rs.getString("category_name")
                    );

                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(author);
                    book.setCategory(category);
                    book.setPublisher(rs.getString("publisher"));
                    book.setYearPublished(rs.getInt("year_published"));
                    book.setTotalQuantity(rs.getInt("total_quantity"));
                    book.setAvailableQuantity(rs.getInt("available_quantity"));
                    book.setDescription(rs.getString("description"));
                    book.setLocation(rs.getString("location"));

                    books.add(book);
                }
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