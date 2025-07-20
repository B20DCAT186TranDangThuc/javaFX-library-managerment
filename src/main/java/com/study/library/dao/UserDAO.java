package com.study.library.dao;

import com.study.library.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public List<User> getAllUsers(String data) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM users "
        );

        boolean hasFilter = data != null && !data.trim().isEmpty();
        if (hasFilter) {
            sql.append("WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? OR address LIKE ? ");
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            if (hasFilter) {
                String keyword = "%" + data.trim() + "%";
                stmt.setString(1, keyword);
                stmt.setString(2, keyword);
                stmt.setString(3, keyword);
                stmt.setString(4, keyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setDob(rs.getString("dob"));
                    user.setAddress(rs.getString("address"));
                    user.setGender(rs.getString("gender"));
                    user.setStatus(rs.getString("status"));
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // hoặc dùng logger
        }

        return users;
    }

    public boolean saveOrUpdate(User user) throws SQLException {
        String insertSql = "INSERT INTO users (name, email, phone, dob, address, gender, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String updateSql = "UPDATE users SET name = ?, email = ?, phone = ?, dob = ?, address = ?, gender = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = (user.getId() == 0)
                ? connection.prepareStatement(insertSql)
                : connection.prepareStatement(updateSql)) {

            // Dùng chung phần binding
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getDob());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getStatus());

            if (user.getId() != 0) {
                stmt.setInt(8, user.getId()); // chỉ set nếu là update
            }

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }


    public List<User> findUserByIdOrName(String searchText) {
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users WHERE name LIKE ? ";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String keyword = "%" + searchText.trim() + "%";
            stmt.setString(1, keyword);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setDob(rs.getString("dob"));
                    user.setAddress(rs.getString("address"));
                    user.setGender(rs.getString("gender"));
                    user.setStatus(rs.getString("status"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // hoặc log bằng Logger
        }

        return users;
    }

    public Long getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
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
}
