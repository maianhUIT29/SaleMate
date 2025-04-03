package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        this.connection = DBConnection.getConnection();
    }

    public List<Product> getTopSellingProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, COUNT(d.product_id) as total_sold " +
                    "FROM product p " +
                    "LEFT JOIN detail d ON p.product_id = d.product_id " +
                    "GROUP BY p.product_id, p.product_name, p.price, p.quantity, p.barcode, p.image " +
                    "ORDER BY total_sold DESC " +
                    "FETCH FIRST ? ROWS ONLY";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setBarcode(rs.getString("barcode"));
                product.setImage(rs.getString("image"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
} 