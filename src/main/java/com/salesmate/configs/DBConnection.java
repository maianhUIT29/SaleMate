package com.salesmate.configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() {
        Connection connection = null;
        FileInputStream inputStream = null;
        try {
            // Load configuration from config.properties
            Properties properties = new Properties();
            inputStream = new FileInputStream("config.properties"); // Ensure the path is correct
            properties.load(inputStream);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            // Load JDBC driver and establish connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Oracle DB Connected");
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to DB: " + e.getMessage());
        } finally {
            // Ensure FileInputStream is closed after use
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
