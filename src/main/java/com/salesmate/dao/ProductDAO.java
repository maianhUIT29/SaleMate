package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        // Mở connection 1 lần khi tạo DAO
        this.connection = DBConnection.getConnection();
    }

    // CREATE
    public boolean createProduct(Product p) {
        String sql = "INSERT INTO product (product_name, price, quantity, barcode, image) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setString(4, p.getBarcode());
            ps.setString(5, p.getImage());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, product_name, price, quantity, barcode, image FROM product";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setBarcode(rs.getString("barcode"));
                p.setImage(rs.getString("image"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ by ID
    public Product getProductById(int productId) {
        String sql = "SELECT product_id, product_name, price, quantity, barcode, image FROM product WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setBarcode(rs.getString("barcode"));
                p.setImage(rs.getString("image"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean updateProduct(Product p) {
        String sql = "UPDATE product SET product_name=?, price=?, quantity=?, barcode=?, image=? WHERE product_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setString(4, p.getBarcode());
            ps.setString(5, p.getImage());
            ps.setInt(6, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ Top Selling Products
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
