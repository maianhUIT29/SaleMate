package com.salesmate.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Product;

public class ProductDAO {

    // CREATE a new product using stored procedure
    public boolean createProduct(Product product) {
        String storedProcCall = "{CALL SP_ADD_PRODUCT(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection(); 
             CallableStatement stmt = conn.prepareCall(storedProcCall)) {

            // Set input parameters (8 IN parameters)
            stmt.setString(1, product.getProductName());      // p_product_name
            stmt.setBigDecimal(2, product.getPrice());        // p_price
            stmt.setInt(3, product.getQuantity());            // p_quantity
            stmt.setString(4, product.getBarcode());          // p_barcode
            stmt.setString(5, product.getCategory());         // p_category
            stmt.setString(6, product.getImage());            // p_image
            stmt.setString(7, product.getDescription());      // p_description
            stmt.setInt(8, 1);                                // p_created_by (default user ID, you may want to get this from session)
            
            // Register output parameters
            stmt.registerOutParameter(9, Types.INTEGER);      // p_product_id (OUT)
            stmt.registerOutParameter(10, Types.VARCHAR);     // p_result (OUT)

            // Execute the stored procedure
            stmt.execute();
            
            // Get the results from the stored procedure
            int generatedProductId = stmt.getInt(9);
            String result = stmt.getString(10);
            
            // Log the results for debugging
            System.out.println("SP_ADD_PRODUCT result: " + result);
            System.out.println("Generated Product ID: " + generatedProductId);
            
            // Check if the operation was successful
            boolean success = result != null && result.startsWith("Thêm sản phẩm thành công");
            
            if (success && generatedProductId > 0) {
                // Set the generated ID back to the product object
                product.setProductId(generatedProductId);
                return true;
            } else {
                System.err.println("SP_ADD_PRODUCT failed: " + result);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error executing stored procedure SP_ADD_PRODUCT: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(Product product) {
        String query = "UPDATE product SET product_name = ?, price = ?, quantity = ?, barcode = ?, image = ?, category = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getBarcode());
            stmt.setString(5, product.getImage());
            stmt.setString(6, product.getCategory()); // Add category
            stmt.setInt(7, product.getProductId());

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

            System.out.println("Executing update query for product ID: " + productId + 
                               ", reducing quantity by: " + soldQuantity);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Successfully updated quantity for product ID: " + productId);
                return true;
            } else {
                System.err.println("Failed to update quantity for product ID: " + productId + 
                                  " - No rows were updated. Insufficient stock or product not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error updating product quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setBarcode(rs.getString("barcode"));
                product.setImage(rs.getString("image"));
                product.setCategory(rs.getString("category"));
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

    // Lấy sản phẩm theo barcode
    public Product getProductByBarcode(String barcode) {
        Product product = null;
        String sql = "SELECT * FROM product WHERE barcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                product = mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

// Đếm số lượng sản phẩm
    public int countProduct() {
        String sql = "SELECT COUNT(*) FROM product";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Trả về số lượng sản phẩm
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Nếu có lỗi hoặc không tìm thấy dữ liệu, trả về 0
    }

// Lấy top 10 sản phẩm bán chạy nhất
public List<Map<String, Object>> getTopSellingProducts() throws SQLException {
    List<Map<String, Object>> products = new ArrayList<>();
    String sql = "SELECT p.product_id, " +
                 "p.product_name, " +
                 "p.price, " +
                 "p.quantity, " +
                 "p.barcode, " +
                 "p.image, " +
                 "COUNT(d.product_id) as total_sold " +  // Đếm số lượng bán ra từ bảng detail
                 "FROM product p " +
                 "LEFT JOIN detail d ON p.product_id = d.product_id " +  // Kết nối với bảng detail
                 "GROUP BY p.product_id, p.product_name, p.price, p.quantity, p.barcode, p.image " +
                 "ORDER BY total_sold DESC " +  // Sắp xếp theo số lượng bán ra từ cao đến thấp
                 "FETCH FIRST 10 ROWS ONLY";  // Giới hạn 10 sản phẩm bán chạy nhất

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            // Tạo một Map để chứa thông tin sản phẩm và tổng số lượng bán ra
            Map<String, Object> product = new HashMap<>();
            product.put("product_id", rs.getInt("product_id"));
            product.put("product_name", rs.getString("product_name"));
            product.put("price", rs.getBigDecimal("price"));
            product.put("quantity", rs.getInt("quantity"));
            product.put("barcode", rs.getString("barcode"));
            product.put("image", rs.getString("image"));
            product.put("total_sold", rs.getInt("total_sold"));  // Lưu tổng số lượng bán ra trực tiếp vào Map
            products.add(product);
        }
    }
    return products;
}
    // Xóa sản phẩm
    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // If rows are affected, the delete was successful
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes multiple products from the database
     * @param productIds Array of product IDs to be deleted
     * @return true if all deletes were successful, false otherwise
     */
    public boolean deleteMultipleProducts(int[] productIds) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int id : productIds) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                
                int[] results = ps.executeBatch();
                conn.commit();
                
                // Check if all deletes were successful
                for (int result : results) {
                    if (result != 1) {
                        return false;
                    }
                }
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error deleting multiple products: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all distinct categories from products in the database
     * @return List of category names
     */
    public List<String> getProductCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả"); // Add default "All" option first
        
        try (Connection conn = DBConnection.getConnection()) {
            // Modified query to handle null and empty categories properly
            String query = "SELECT DISTINCT category FROM product WHERE category IS NOT NULL AND category <> '' ORDER BY category";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    if (category != null && !category.trim().isEmpty()) {
                        categories.add(category.trim());
                        System.out.println("Added category from DB: " + category);
                    }
                }
                
                // Add "No category" option if not already in the list
                if (!categories.contains("Không có danh mục")) {
                    categories.add("Không có danh mục");
                    System.out.println("Added 'Không có danh mục' category");
                }
            }
            
            // If no categories found in database, add some defaults
            if (categories.size() <= 1) {
                categories.add("Nước giặt");
                categories.add("Dầu gội");
                categories.add("Nước xả");
                categories.add("Sữa tắm");
                categories.add("Không có danh mục");
                System.out.println("Added default categories since none found in DB");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product categories: " + e.getMessage());
            e.printStackTrace();
            
            // In case of error, ensure we have at least some categories
            if (categories.size() <= 1) {
                categories.add("Nước giặt");
                categories.add("Dầu gội");
                categories.add("Nước xả");
                categories.add("Sữa tắm");
                categories.add("Không có danh mục");
                System.out.println("Added fallback categories due to error");
            }
        }
        
        return categories;
    }

    public List<Map<String, Object>> getProductsLowStockPrediction(int thresholdDays) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = 
            "SELECT p.product_id, p.product_name, p.quantity, " +
            "  NVL(SUM(d.quantity), 0) / NULLIF(COUNT(DISTINCT TRUNC(i.created_at)), 0) AS avg_sold_per_day " +
            "FROM product p " +
            "LEFT JOIN detail d ON p.product_id = d.product_id " +
            "LEFT JOIN invoice i ON d.invoice_id = i.invoice_id AND i.payment_status = 'Paid' " +
            "GROUP BY p.product_id, p.product_name, p.quantity";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                double avgSold = rs.getDouble("avg_sold_per_day");
                int daysLeft = avgSold > 0 ? (int)Math.floor(quantity / avgSold) : Integer.MAX_VALUE;
                if (daysLeft < thresholdDays) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("product_id", rs.getInt("product_id"));
                    row.put("product_name", rs.getString("product_name"));
                    row.put("quantity", quantity);
                    row.put("days_left", daysLeft);
                    // Tính ngày hết hàng dự kiến
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.add(java.util.Calendar.DATE, daysLeft);
                    java.util.Date outOfStockDate = cal.getTime();
                    row.put("out_of_stock_date", new java.sql.Date(outOfStockDate.getTime()));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Add this method to get product name by ID
    public String getProductNameById(int productId) {
        String productName = null;
        String query = "SELECT product_name FROM product WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                productName = rs.getString("product_name");
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching product name: " + e.getMessage());
        }
        
        return productName;
    }

    // Get products with pagination and filtering
    public List<Product> getProducts(int page, int pageSize, String category, String search) {
        List<Product> products = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM product WHERE 1=1");
        
        if (category != null && !category.equals("Tất cả")) {
            if (category.equals("Không có danh mục")) {
                query.append(" AND (category IS NULL OR category = '')");
            } else {
                query.append(" AND category = ?");
            }
        }
        
        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND (product_name LIKE ? OR barcode LIKE ?)");
        }
        
        query.append(" ORDER BY product_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            
            if (category != null && !category.equals("Tất cả") && !category.equals("Không có danh mục")) {
                stmt.setString(paramIndex++, category);
            }
            
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            stmt.setInt(paramIndex++, (page - 1) * pageSize);
            stmt.setInt(paramIndex, pageSize);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setBarcode(rs.getString("barcode"));
                    product.setImage(rs.getString("image"));
                    product.setCategory(rs.getString("category"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Count products with filtering
    public int countProducts(String category, String search) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM product WHERE 1=1");
        
        if (category != null && !category.equals("Tất cả")) {
            if (category.equals("Không có danh mục")) {
                query.append(" AND (category IS NULL OR category = '')");
            } else {
                query.append(" AND category = ?");
            }
        }
        
        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND (product_name LIKE ? OR barcode LIKE ?)");
        }
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            
            if (category != null && !category.equals("Tất cả") && !category.equals("Không có danh mục")) {
                stmt.setString(paramIndex++, category);
            }
            
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateProductCategory(int productId, String newCategory) {
        String sql = "UPDATE product SET category = ? WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newCategory);
            ps.setInt(2, productId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating product category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get product stock quantity using Oracle function get_product_stock
     * @param productId The ID of the product
     * @return The stock quantity of the product, or 0 if product not found or error occurs
     */
    public int getProductStock(int productId) {
        String functionCall = "{? = CALL get_product_stock(?)}";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(functionCall)) {
            
            // Register the output parameter (return value)
            stmt.registerOutParameter(1, Types.INTEGER);
            
            // Set the input parameter
            stmt.setInt(2, productId);
            
            // Execute the function
            stmt.execute();
            
            // Get the result
            int stockQuantity = stmt.getInt(1);
            
            System.out.println("Product ID: " + productId + " - Stock Quantity: " + stockQuantity);
            
            return stockQuantity;
            
        } catch (SQLException e) {
            System.err.println("Error calling get_product_stock function for product ID " + productId + ": " + e.getMessage());
            e.printStackTrace();
            // Return 0 as fallback, consistent with the Oracle function behavior
            return 0;
        }
    }

    // Lấy số lượng đã bán của sản phẩm
    public int getSoldQuantity(int productId) {
        int sold = 0;
        String sql = "SELECT NVL(SUM(quantity),0) FROM detail WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sold = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sold;
    }

    // Tìm kiếm sản phẩm theo tên
    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE LOWER(product_name) LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setCategory(rs.getString("category"));
                p.setBarcode(rs.getString("barcode"));
                p.setQuantity(rs.getInt("quantity"));
                // ...set các trường khác nếu cần
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean markProductDamaged(int productId, String reason) {
        String sql = "UPDATE product SET status = 'Damaged', damaged_reason = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reason);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setBarcode(rs.getString("barcode"));
        product.setImage(rs.getString("image"));
        product.setCategory(rs.getString("category"));
        // Nếu có thêm các trường khác, set tiếp ở đây
        return product;
    }
}
