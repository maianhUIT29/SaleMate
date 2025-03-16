package com.salesmate.configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Đọc cấu hình từ file config.properties
                Properties properties = new Properties();
                FileInputStream inputStream = new FileInputStream("config.properties");  // Đảm bảo đường dẫn chính xác
                properties.load(inputStream);

                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.username");
                String password = properties.getProperty("db.password");

                // Tải driver JDBC và kết nối đến DB
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("✅ Kết nối Oracle DB thành công!");
            } catch (ClassNotFoundException | SQLException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException("❌ Lỗi kết nối Oracle DB: " + e.getMessage());
            }
        }
        return connection;
    }
}

