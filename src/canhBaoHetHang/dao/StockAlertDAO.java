package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Product;

// Lớp StockAlertDAO để truy vấn các sản phẩm có số lượng thấp trong cơ sở dữ liệu
public class StockAlertDAO {

    // Phương thức lấy danh sách các sản phẩm có số lượng thấp (<= 10)
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();  // Danh sách lưu các sản phẩm
        String sql = "SELECT product_id, product_name, quantity FROM PRODUCT WHERE quantity <= 10";  // Câu lệnh SQL

        try (Connection conn = DatabaseConnection.getConnection();  // Kết nối cơ sở dữ liệu
             PreparedStatement stmt = conn.prepareStatement(sql);  // Chuẩn bị câu lệnh SQL
             ResultSet rs = stmt.executeQuery()) {  // Thực thi câu lệnh và nhận kết quả

            // Duyệt qua các sản phẩm và thêm vào danh sách
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                products.add(new Product(id, name, quantity));  // Thêm sản phẩm vào danh sách
            }
        } catch (SQLException e) {
            e.printStackTrace();  // In lỗi nếu có vấn đề khi truy vấn
        }

        return products;  // Trả về danh sách các sản phẩm
    }
}
