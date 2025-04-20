package com.salesmate.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.dao.ProductDAO;
import com.salesmate.model.Product;

public class ProductController {

    private ProductDAO productDAO;

    public ProductController() {
        productDAO = new ProductDAO();
    }

    // CREATE
    public boolean addProduct(Product product) {
        try {
            return productDAO.createProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ by ID
    public Product getProductById(int productId) {
        try {
            return productDAO.getProductById(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public boolean updateProduct(Product product) {
        try {
            return productDAO.updateProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the product quantity after a sale by delegating to ProductDAO
     *
     * @param productId The ID of the product to update
     * @param soldQuantity The quantity that was sold
     * @return true if the update was successful, false otherwise
     */
    public boolean updateProductQuantity(int productId, int soldQuantity) {
        try {
            return productDAO.updateProductQuantity(productId, soldQuantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product findProductByBarcode(String barcode) {
        String query = "SELECT * FROM product WHERE barcode = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setBarcode(rs.getString("barcode"));
                    product.setImage(rs.getString("image"));
                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
