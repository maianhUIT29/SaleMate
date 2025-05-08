package com.salesmate.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<Product> products = productDAO.getAllProducts();
            return products != null ? products : new ArrayList<>(); // Trả về danh sách rỗng nếu null
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
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

    // Add this method to get product name by ID
    public String getProductNameById(int productId) {
        try {
            return productDAO.getProductNameById(productId);
        } catch (Exception e) {
            System.err.println("Error fetching product name: " + e.getMessage());
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
            System.out.println("ProductController: Updating product " + productId + 
                               " quantity by -" + soldQuantity);
            boolean result = productDAO.updateProductQuantity(productId, soldQuantity);
            if (result) {
                System.out.println("ProductController: Successfully updated product quantity");
            } else {
                System.err.println("ProductController: Failed to update product quantity");
            }
            return result;
        } catch (Exception e) {
            System.err.println("ProductController: Error updating product quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteProduct(int productId) {
        try {
            return productDAO.deleteProduct(productId);
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
    
     // Đếm số lượng sảnpham
    public int countProduct() {
        try {
            return productDAO.countProduct();
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an error
        }
    }
    
 // Lấy danh sách sản phẩm bán chạy nhất với số thứ tự từ 1 đến 10
    public List<Map<String, Object>> getTopSellingProducts() {
        try {
            return productDAO.getTopSellingProducts(); // Không cần tham số nữa
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy danh mục sản phẩm
    public List<String> getProductCategories() {
        try {
            return productDAO.getProductCategories();
        } catch (Exception e) {
            System.err.println("Error getting product categories: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getProductsLowStockPrediction(int thresholdDays) {
        return productDAO.getProductsLowStockPrediction(thresholdDays);
    }

    public List<Product> getProducts(int page, int pageSize, String category, String search) {
        return productDAO.getProducts(page, pageSize, category, search);
    }

    public int countProducts(String category, String search) {
        return productDAO.countProducts(category, search);
    }

}
