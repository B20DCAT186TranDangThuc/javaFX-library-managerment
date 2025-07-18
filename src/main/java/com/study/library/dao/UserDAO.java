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
}
