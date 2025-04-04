package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Product;

public class ProductDAO {

    // Lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT product_id, product_name, price, quantity, barcode, image FROM product";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Executing query: " + query);

            while (rs.next()) {
                System.out.println("Found product: " + rs.getString("product_name"));
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantity"),
                        rs.getString("barcode"),
                        rs.getString("image")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Total products retrieved: " + products.size());
        return products;
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int productId) {
        String query = "SELECT product_id, product_name, price, quantity, barcode, image FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            System.out.println("Executing query: " + query + " with productId = " + productId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantity"),
                        rs.getString("barcode"),
                        rs.getString("image")
                );
            } else {
                System.out.println("No product found with productId = " + productId);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
        return null;  // Return null if no product is found
    }
}
