package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.ChartDataModel;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class PaymentDAO {
    // Thống kê số lượng hóa đơn theo phương thức thanh toán (toàn thời gian)
    public List<ChartDataModel> getInvoiceCountByPaymentMethod() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT payment_method, COUNT(DISTINCT invoice_id) AS count FROM payment GROUP BY payment_method";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new ChartDataModel(rs.getString("payment_method"), BigDecimal.valueOf(rs.getInt("count"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Thống kê số lượng hóa đơn theo phương thức thanh toán trong 1 năm
    public List<ChartDataModel> getInvoiceCountByPaymentMethodForYear(int year) {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT payment_method, COUNT(DISTINCT invoice_id) AS count FROM payment WHERE EXTRACT(YEAR FROM payment_date) = ? GROUP BY payment_method";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ChartDataModel(rs.getString("payment_method"), BigDecimal.valueOf(rs.getInt("count"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Thống kê số lượng hóa đơn theo phương thức thanh toán trong 1 tháng của 1 năm
    public List<ChartDataModel> getInvoiceCountByPaymentMethodForMonth(int year, int month) {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT payment_method, COUNT(DISTINCT invoice_id) AS count FROM payment WHERE EXTRACT(YEAR FROM payment_date) = ? AND EXTRACT(MONTH FROM payment_date) = ? GROUP BY payment_method";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ChartDataModel(rs.getString("payment_method"), BigDecimal.valueOf(rs.getInt("count"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Thống kê số lượng hóa đơn theo phương thức thanh toán theo tuần của 1 tháng/năm
    public List<ChartDataModel> getInvoiceCountByPaymentMethodForWeek(int year, int month) {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT TO_CHAR(payment_date, 'IW') as week, payment_method, COUNT(DISTINCT invoice_id) AS count FROM payment WHERE EXTRACT(YEAR FROM payment_date) = ? AND EXTRACT(MONTH FROM payment_date) = ? GROUP BY TO_CHAR(payment_date, 'IW'), payment_method ORDER BY week";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String label = "Tuần " + rs.getString("week") + " - " + rs.getString("payment_method");
                    result.add(new ChartDataModel(label, BigDecimal.valueOf(rs.getInt("count"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
} 