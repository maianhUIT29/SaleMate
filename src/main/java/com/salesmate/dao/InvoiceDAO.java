package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Invoice;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // Create
    public boolean createInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoice (users_id, total, status) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getUsersId());
            pstmt.setBigDecimal(2, invoice.getTotal());
            pstmt.setString(3, invoice.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read all invoices
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice";
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setStatus(rs.getString("status"));
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
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setStatus(rs.getString("status"));
                return invoice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoice SET users_id = ?, total = ?, status = ? WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getUsersId());
            pstmt.setBigDecimal(2, invoice.getTotal());
            pstmt.setString(3, invoice.getStatus());
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
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setStatus(rs.getString("status"));
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
        String sql = "SELECT * FROM invoice WHERE created_at >= SYSDATE - 7";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setStatus(rs.getString("status"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }
}
