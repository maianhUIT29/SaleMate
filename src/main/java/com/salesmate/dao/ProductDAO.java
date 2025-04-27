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

    // CREATE a new product
    public boolean createProduct(Product product) {
        String query = "INSERT INTO product (product_name, price, quantity, barcode, image) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getBarcode());
            stmt.setString(5, product.getImage());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // If rows are affected, the insert was successful
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(Product product) {
        String query = "UPDATE product SET product_name = ?, price = ?, quantity = ?, barcode = ?, image = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getBarcode());
            stmt.setString(5, product.getImage());
            stmt.setInt(6, product.getProductId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // If rows are affected, the update was successful
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the product quantity after a sale
     * 
     * @param productId The ID of the product to update
     * @param soldQuantity The quantity that was sold
     * @return true if the update was successful, false otherwise
     */
    public boolean updateProductQuantity(int productId, int soldQuantity) {
        String query = "UPDATE product SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, soldQuantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, soldQuantity); // Ensure we have enough stock
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products; // Trả về danh sách rỗng nếu không có dữ liệu
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int productId) {
        String query = "SELECT product_id, product_name, price, quantity, barcode, image FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

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

    public boolean deleteProduct(int productId) {
        String deleteDetailQuery = "DELETE FROM detail WHERE product_id = ?";
        String deletePurchaseDetailQuery = "DELETE FROM purchase_detail WHERE product_id = ?";
        String deleteStockQuery = "DELETE FROM stock WHERE product_id = ?";
        String deleteProductPromotionQuery = "DELETE FROM product_promotion WHERE product_id = ?";
        String deleteProductQuery = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Xóa các bản ghi liên quan
            try (PreparedStatement stmt1 = conn.prepareStatement(deleteDetailQuery)) {
                stmt1.setInt(1, productId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deletePurchaseDetailQuery)) {
                stmt2.setInt(1, productId);
                stmt2.executeUpdate();
            }

            try (PreparedStatement stmt3 = conn.prepareStatement(deleteStockQuery)) {
                stmt3.setInt(1, productId);
                stmt3.executeUpdate();
            }

            try (PreparedStatement stmt4 = conn.prepareStatement(deleteProductPromotionQuery)) {
                stmt4.setInt(1, productId);
                stmt4.executeUpdate();
            }

            // Xóa sản phẩm
            try (PreparedStatement stmt5 = conn.prepareStatement(deleteProductQuery)) {
                stmt5.setInt(1, productId);
                int rowsAffected = stmt5.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction nếu thành công
                    return true;
                } else {
                    conn.rollback(); // Rollback nếu không xóa được sản phẩm
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
