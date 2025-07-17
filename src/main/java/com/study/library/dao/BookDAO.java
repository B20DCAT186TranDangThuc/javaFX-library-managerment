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
                        rs.getInt("available_quantity"),
                        rs.getString("description"),
                        rs.getString("location")
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