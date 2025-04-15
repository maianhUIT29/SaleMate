package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Detail;

public class DetailDAO {

    // Thêm một Detail mới vào database
    public boolean addDetail(Detail detail) {
        String sql = "INSERT INTO detail (invoice_id, product_id, quantity, price, total) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getInvoiceId());
            stmt.setInt(2, detail.getProductId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setBigDecimal(4, detail.getPrice());
            stmt.setBigDecimal(5, detail.getTotal());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách tất cả các chi tiết hóa đơn
    public List<Detail> getAllDetails() {
        List<Detail> details = new ArrayList<>();
        String sql = "SELECT * FROM detail";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Detail detail = new Detail(
                    rs.getInt("detail_id"),
                    rs.getInt("invoice_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("price"),
                    rs.getBigDecimal("total")
                );
                details.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    // Lấy Detail theo ID
    public Detail getDetailById(int detailId) {
        String sql = "SELECT * FROM detail WHERE detail_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detailId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Detail(
                        rs.getInt("detail_id"),
                        rs.getInt("invoice_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price"),
                        rs.getBigDecimal("total")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật Detail
    public boolean updateDetail(Detail detail) {
        String sql = "UPDATE detail SET invoice_id = ?, product_id = ?, quantity = ?, price = ?, total = ? WHERE detail_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getInvoiceId());
            stmt.setInt(2, detail.getProductId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setBigDecimal(4, detail.getPrice());
            stmt.setBigDecimal(5, detail.getTotal());
            stmt.setInt(6, detail.getDetailId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa Detail theo ID
    public boolean deleteDetail(int detailId) {
        String sql = "DELETE FROM detail WHERE detail_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detailId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // lấy tất cả sản phẩm của một hóa đơn
    public List<Detail> getDetailsByInvoiceId(int invoiceId) {
        List<Detail> details = new ArrayList<>();
        String sql = "SELECT * FROM detail WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Detail detail = new Detail(
                        rs.getInt("detail_id"),
                        rs.getInt("invoice_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price"),
                        rs.getBigDecimal("total")
                    );
                    details.add(detail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }
}
