package com.salesmate.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Invoice;

public class InvoiceDAO {

    // Tạo invoice mới
    public boolean createInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoice (users_id, total_amount, payment_status) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getUsersId());
            pstmt.setBigDecimal(2, invoice.getTotal());
            pstmt.setString(3, invoice.getPaymentStatus()); // Fixed: paymentStatus instead of status
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả hóa đơn
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (Connection connection = DBConnection.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status")); // Fixed: payment_status instead of status
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    // Read invoice by ID
    public Invoice getInvoiceById(int id) {
        String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status")); // Fixed: payment_status instead of status
                return invoice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoice SET users_id = ?, total_amount = ?, payment_status = ? WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getUsersId());
            pstmt.setBigDecimal(2, invoice.getTotal());
            pstmt.setString(3, invoice.getPaymentStatus()); // Fixed: paymentStatus instead of status
            pstmt.setInt(4, invoice.getInvoiceId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete
    public boolean deleteInvoice(int id) {
        String sql = "DELETE FROM invoice WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get invoices by user ID
    public List<Invoice> getInvoicesByUserId(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE users_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status")); // Fixed: payment_status instead of status
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    // Get invoices from the last 7 days
    public List<Invoice> getInvoicesLast7Days() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE created_at >= CURRENT_DATE - 7"; // Fixed: Using CURRENT_DATE for consistency with Oracle
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status")); // Fixed: payment_status instead of status
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public int saveInvoice(Invoice invoice) {
        // Validate payment status
        if (invoice.getPaymentStatus() == null
                || (!invoice.getPaymentStatus().equals("Paid") && !invoice.getPaymentStatus().equals("Unpaid"))) {
            throw new IllegalArgumentException("Payment status must be either 'Paid' or 'Unpaid'");
        }

        // Validate total amount
        if (invoice.getTotal() == null || invoice.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }

        String insertSql = "INSERT INTO invoice (users_id, total_amount, created_at, payment_status) "
                + "VALUES (?, ?, ?, ?)";

        // Sau đó lấy ID vừa được tạo
        String getIdSql = "SELECT invoice_seq.CURRVAL FROM dual";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement insertStmt = connection.prepareStatement(insertSql); PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)) {

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
// Đếm số lượng hóa đơn
public int countInvoices() {
    String sql = "SELECT COUNT(*) FROM invoice";
    try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
            return rs.getInt(1); // Trả về số lượng hóa đơn
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0; // Nếu có lỗi hoặc không tìm thấy dữ liệu, trả về 0
}

}
