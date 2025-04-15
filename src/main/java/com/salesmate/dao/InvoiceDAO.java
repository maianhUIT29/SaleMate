package com.salesmate.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Invoice;
import java.util.ArrayList;
import java.util.List;


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


    // Lấy tất cả hoá đơn của một người dùng
    public List<Invoice> getInvoicesByUserId(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE users_id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(resultSet.getInt("invoice_id"));
                invoice.setUsersId(resultSet.getInt("users_id"));
                invoice.setTotal(resultSet.getBigDecimal("total_amount"));
                invoice.setCreatedAt(resultSet.getDate("created_at"));
                invoice.setPaymentStatus(resultSet.getString("payment_status"));
                invoices.add(invoice);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch invoices for user ID: " + userId, e);
        }
        
        System.out.println("Fetching invoices for user with ID: " + userId);
        return invoices;
    }

    // Lấy tất cả hoá đơn trong 7 ngày gần đây
    public List<Invoice> getInvoicesLast7Days() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE created_at >= SYSDATE - 7";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(resultSet.getInt("invoice_id"));
                invoice.setUsersId(resultSet.getInt("users_id"));
                invoice.setTotal(resultSet.getBigDecimal("total_amount"));
                invoice.setCreatedAt(resultSet.getDate("created_at"));
                invoice.setPaymentStatus(resultSet.getString("payment_status"));
                invoices.add(invoice);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch invoices from the last 7 days", e);
        }
        
        System.out.println("Fetching invoices from the last 7 days");
        return invoices;
    }
}