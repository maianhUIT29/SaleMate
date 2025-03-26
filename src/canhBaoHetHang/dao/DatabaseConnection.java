package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Lớp DatabaseConnection cung cấp phương thức kết nối cơ sở dữ liệu Oracle
public class DatabaseConnection {
    // Phương thức getConnection trả về kết nối đến cơ sở dữ liệu Oracle
    public static Connection getConnection() {
        try {
            // Kết nối tới cơ sở dữ liệu Oracle với JDBC
            return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1523:orcl1",  // Địa chỉ cơ sở dữ liệu
                "salemate",  // Tên người dùng
                "Thien1029"   // Mật khẩu
            );
        } catch (SQLException e) {
            e.printStackTrace();  // In ra lỗi nếu có sự cố khi kết nối
            return null;  // Trả về null nếu không thể kết nối
        }
    }
}
