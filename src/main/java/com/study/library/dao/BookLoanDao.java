package com.study.library.dao;

import com.study.library.model.Book;
import com.study.library.model.BookLoan;
import com.study.library.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
