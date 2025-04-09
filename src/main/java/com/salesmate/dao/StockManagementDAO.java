package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockManagementDAO {
    private Connection connection;

    public StockManagementDAO() {
        connection = DBConnection.getConnection();
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, s.quantity as stock_quantity FROM product p " +
                    "LEFT JOIN stock s ON p.product_id = s.product_id " +
                    "ORDER BY p.product_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setQuantity(rs.getInt("stock_quantity"));
                product.setBarcode(rs.getString("barcode"));
                product.setImage(rs.getString("image"));
                
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getLowStockProducts() {
        List<Product> lowStockList = new ArrayList<>();
        String sql = "SELECT p.*, s.quantity as stock_quantity FROM product p " +
                    "LEFT JOIN stock s ON p.product_id = s.product_id " +
                    "WHERE s.quantity <= 10 OR s.quantity IS NULL " +
                    "ORDER BY s.quantity";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setQuantity(rs.getInt("stock_quantity"));
                product.setBarcode(rs.getString("barcode"));
                product.setImage(rs.getString("image"));
                
                lowStockList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockList;
    }

    public boolean updateStockQuantity(int productId, int newQuantity) {
        String sql = "UPDATE stock SET quantity = ?, last_updated = SYSDATE " +
                    "WHERE product_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 