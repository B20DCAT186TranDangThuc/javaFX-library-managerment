package com.study.library.dao;

import com.study.library.model.Book;
import com.study.library.model.BookLoan;
import com.study.library.model.SelectableBook;
import com.study.library.model.User;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookLoanDao {
    private final Connection connection;

    public BookLoanDao(Connection connection) {
        this.connection = connection;
    }

    public List<BookLoan> getBorrowedBooksByUserId(int userId) {
        List<BookLoan> loans = new ArrayList<>();
        String sql = """
                    SELECT bl.*, 
                           u.id AS user_id, u.name AS user_name, u.email AS user_email,
                           b.id AS book_id, b.title AS book_title
                    FROM book_loans bl
                    JOIN users u ON bl.user_id = u.id
                    JOIN books b ON bl.book_id = b.id
                    WHERE bl.user_id = ? AND bl.returned = FALSE
                    ORDER BY bl.borrow_date DESC
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookLoan loan = new BookLoan();
                    loan.setId(rs.getInt("id"));
                    loan.setBorrowDate(rs.getString("borrow_date"));
                    loan.setReturnDate(rs.getString("return_date"));
                    loan.setReturned(rs.getBoolean("returned"));

                    // Set User
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setName(rs.getString("user_name"));
                    user.setEmail(rs.getString("user_email"));
                    loan.setUser(user);

                    // Set Book
                    Book book = new Book();
                    book.setId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("book_title"));
                    loan.setBook(book);

                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // hoặc logger
        }

        return loans;
    }

    public void updateReturnStatus(int id, boolean returned) {
        String sql = "UPDATE book_loans SET returned = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBoolean(1, returned);
            stmt.setInt(2, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createLoans(User user, List<SelectableBook> items) {
        String sql = "INSERT INTO book_loans (user_id, book_id, borrow_date, return_date, returned) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false); // bắt đầu transaction

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                LocalDate today = LocalDate.now();

                for (SelectableBook sb : items) {
                    if (!sb.isSelected()) continue;

                    stmt.setInt(1, user.getId());
                    stmt.setInt(2, sb.getBook().getId());
                    stmt.setDate(3, Date.valueOf(today));
                    stmt.setDate(4, Date.valueOf(sb.getReturnDate()));
                    stmt.setBoolean(5, false); // chưa trả
                    stmt.addBatch();
                }

                int[] result = stmt.executeBatch();
                for (int count : result) {
                    if (count == Statement.EXECUTE_FAILED) {
                        connection.rollback();
                        return false;
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
