package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.controller.Statistics;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Trả về đối tượng thống kê số lượng
    public Statistics getStatistics(String roleFilter, boolean onlyPaidInvoices) {
        int empCount = 0, prodCount = 0, invCount = 0;

        try (Connection conn = getConnection()) {
            // Đếm nhân viên có thể lọc theo vai trò
            String userSQL = "SELECT COUNT(*) FROM USERS" + (roleFilter != null ? " WHERE role = ?" : "");
            try (PreparedStatement ps = conn.prepareStatement(userSQL)) {
                if (roleFilter != null) ps.setString(1, roleFilter);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) empCount = rs.getInt(1);
            }

            // Đếm sản phẩm
            String prodSQL = "SELECT COUNT(*) FROM PRODUCT";
            try (PreparedStatement ps = conn.prepareStatement(prodSQL);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) prodCount = rs.getInt(1);
            }

            // Đếm hóa đơn (có lọc trạng thái thanh toán nếu cần)
            String invSQL = "SELECT COUNT(*) FROM INVOICE" + (onlyPaidInvoices ? " WHERE payment_status = 'Paid'" : "");
            try (PreparedStatement ps = conn.prepareStatement(invSQL);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) invCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Statistics(empCount, prodCount, invCount);
    }

    // Lấy danh sách tên người dùng (user)
    public List<String> getUserNames(String roleFilter) {
        List<String> list = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT username FROM USERS" + (roleFilter != null ? " WHERE role = ?" : "");
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (roleFilter != null) ps.setString(1, roleFilter);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách tên sản phẩm
    public List<String> getProductNames() {
        List<String> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT product_name FROM PRODUCT");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("product_name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách mã hóa đơn
    public List<Integer> getInvoiceIds(boolean onlyPaidInvoices) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT invoice_id FROM INVOICE" + (onlyPaidInvoices ? " WHERE payment_status = 'Paid'" : "");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getInt("invoice_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}