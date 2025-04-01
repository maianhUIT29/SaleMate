package com.salesmate.dao;

import com.salesmate.model.Sale;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    private Connection connection;

    public SaleDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Sale> getTopSellingProducts(Date startDate, Date endDate) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT p.product_name, SUM(d.quantity) AS total_quantity_sold " +
                     "FROM DETAIL d " +
                     "JOIN PRODUCT p ON d.product_id = p.product_id " +
                     "JOIN INVOICE i ON d.invoice_id = i.invoice_id " +
                     "WHERE i.created_at BETWEEN ? AND ? " +
                     "GROUP BY p.product_name " +
                     "ORDER BY total_quantity_sold DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int totalQuantitySold = rs.getInt("total_quantity_sold");
                    sales.add(new Sale(productName, totalQuantitySold));
                }
            }
        }
        return sales;
    }
}
