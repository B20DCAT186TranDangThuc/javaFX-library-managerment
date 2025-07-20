package com.study.library.dao;

import com.study.library.model.*;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public List<BookStat> getTopBorrowedBooks(LocalDate from, LocalDate to) {
        List<BookStat> result = new ArrayList<>();
        String sql = """
                    SELECT b.title, COUNT(*) AS cnt
                    FROM book_loans bl
                    JOIN books b ON bl.book_id = b.id
                    WHERE bl.borrow_date BETWEEN ? AND ?
                    GROUP BY b.id
                    ORDER BY cnt DESC LIMIT 2
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new BookStat(rs.getString("title"), rs.getInt("cnt")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Integer> countBorrowsByDate(LocalDate from, LocalDate to) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
                    SELECT DATE(borrow_date) AS borrow_day, COUNT(*) AS total
                    FROM book_loans
                    WHERE borrow_date BETWEEN ? AND ?
                    GROUP BY borrow_day
                    ORDER BY borrow_day
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getDate("borrow_day").toString(); // YYYY-MM-DD
                result.put(date, rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<UserBorrowStats> getTopBorrowingUsers(LocalDate from, LocalDate to) {
        List<UserBorrowStats> result = new ArrayList<>();
        String sql = """
                    SELECT u.name, COUNT(*) as borrow_count
                    FROM book_loans bl
                    JOIN users u ON bl.user_id = u.id
                    WHERE bl.borrow_date BETWEEN ? AND ?
                    GROUP BY bl.user_id
                    ORDER BY borrow_count DESC
                    LIMIT 2
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int count = rs.getInt("borrow_count");
                result.add(new UserBorrowStats(name, count));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Long getTotalBorrowingBooks() {
        String sql = "SELECT COUNT(*) FROM book_loans";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public Long getOverdueBooksCount() {
        String sql = """
                    SELECT COUNT(*)
                    FROM book_loans
                    WHERE returned = 0
                      AND DATE_ADD(borrow_date, INTERVAL 14 DAY) < CURRENT_DATE
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    public List<BookLoan> getRecentLoans(int limit) {
        List<BookLoan> loans = new ArrayList<>();

        String sql = """
                    SELECT bl.id, bl.borrow_date, bl.return_date, bl.returned,
                           u.id AS user_id, u.name,
                           b.id AS book_id, b.title
                    FROM book_loans bl
                    JOIN users u ON bl.user_id = u.id
                    JOIN books b ON bl.book_id = b.id
                    ORDER BY bl.borrow_date DESC
                    LIMIT ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo user
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));

                // Tạo book
                Book book = new Book();
                book.setId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));

                // Tạo loan
                BookLoan loan = new BookLoan();
                loan.setId(rs.getInt("id"));
                loan.setBorrowDate(rs.getString("borrow_date"));
                loan.setReturnDate(rs.getString("return_date"));
                loan.setReturned(rs.getBoolean("returned"));
                loan.setUser(user);
                loan.setBook(book);

                loans.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loans;
    }
}
