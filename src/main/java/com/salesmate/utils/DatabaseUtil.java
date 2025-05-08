package com.salesmate.utils;

import com.salesmate.configs.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
} 