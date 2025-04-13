package com.salesmate.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Invoice;


public class InvoiceDAO {
    public int saveInvoice(Invoice invoice) {
        // Validate payment status
        if (invoice.getPaymentStatus() == null || 
            (!invoice.getPaymentStatus().equals("Paid") && !invoice.getPaymentStatus().equals("Unpaid"))) {
            throw new IllegalArgumentException("Payment status must be either 'Paid' or 'Unpaid'");
        }

        // Validate total amount
        if (invoice.getTotal() == null || invoice.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }

        String insertSql = "INSERT INTO invoice (users_id, total_amount, created_at, payment_status) " +
                          "VALUES (?, ?, ?, ?)";
        
        // Sau đó lấy ID vừa được tạo
        String getIdSql = "SELECT invoice_seq.CURRVAL FROM dual";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertSql);
             PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)) {
            
            // Set các tham số cho câu insert
            insertStmt.setInt(1, invoice.getUsersId());
            insertStmt.setBigDecimal(2, invoice.getTotal());
            insertStmt.setDate(3, new java.sql.Date(invoice.getCreatedAt().getTime()));
            insertStmt.setString(4, invoice.getPaymentStatus());
            
            // Thực hiện insert
            insertStmt.executeUpdate();
            
            // Lấy ID vừa được tạo
            ResultSet rs = getIdStmt.executeQuery();
            if (rs.next()) {
                int newId = rs.getInt(1);
                invoice.setInvoiceId(newId);
                System.out.println("Invoice saved successfully with ID: " + newId);
                return newId;
            }
            
            throw new SQLException("Failed to get generated ID");
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save invoice: " + e.getMessage());
        }
    }

    public Invoice getInvoiceById(int id) {

        Connection conn = DBConnection.getConnection();
        Invoice invoice = null;
        try {
            String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                invoice = new Invoice();
                invoice.setInvoiceId(resultSet.getInt("invoice_id"));
                invoice.setUsersId(resultSet.getInt("users_id"));
                invoice.setTotal(resultSet.getBigDecimal("total_amount")); // Update column name here too
                invoice.setCreatedAt(resultSet.getDate("created_at"));
                invoice.setPaymentStatus(resultSet.getString("payment_status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Fetching invoice with ID: " + id);
        return invoice;
    }

    public void deleteInvoice(int id) {
        // Logic to delete an invoice by ID from the database
        System.out.println("Deleting invoice with ID: " + id);
    }
}