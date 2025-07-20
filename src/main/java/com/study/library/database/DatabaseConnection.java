package com.study.library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:p6spy:mysql://localhost:3306/library_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.p6spy.engine.spy.P6SpyDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Kết nối thành công MySQL!");
            } catch (ClassNotFoundException e) {
                System.err.println("Không tìm thấy MySQL JDBC Driver");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Kết nối MySQL thất bại!");
                throw e;
            }
        }
        return connection;
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Đã kết nối tới database: " + conn.getMetaData().getURL());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi test kết nối: " + e.getMessage());
        }
        return false;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Đã đóng kết nối MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
