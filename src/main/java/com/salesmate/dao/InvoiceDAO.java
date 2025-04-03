package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Invoice;


public class InvoiceDAO {
    public void saveInvoice(Invoice invoice) {
        Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String sql = "INSERT INTO invoice (users_id, total, created_at, status) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, invoice.getUsersId());
            preparedStatement.setBigDecimal(2, invoice.getTotal());
            preparedStatement.setDate(3, new java.sql.Date(invoice.getCreatedAt().getTime()));
            preparedStatement.setString(4, invoice.getStatus());
            
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Invoice saved successfully.");
            } else {
                System.out.println("Failed to save the invoice.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Saving invoice: " + invoice);
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
                invoice.setTotal(resultSet.getBigDecimal("total"));
                invoice.setCreatedAt(resultSet.getDate("created_at"));
                invoice.setStatus(resultSet.getString("status"));
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