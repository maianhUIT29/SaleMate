package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Promotion;
import com.salesmate.model.ChartDataModel;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class PromotionDAO {
    public int getActivePromotionsCount() {
        String sql = "SELECT COUNT(*) FROM PROMOTION " +
                    "WHERE status = 'ACTIVE' AND start_date <= SYSDATE AND end_date >= SYSDATE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<ChartDataModel> getActivePromotionsByType() {
        List<ChartDataModel> data = new ArrayList<>();
        String sql = "SELECT promotion_type, COUNT(*) as count FROM PROMOTION " +
                    "WHERE status = 'ACTIVE' AND start_date <= SYSDATE AND end_date >= SYSDATE " +
                    "GROUP BY promotion_type";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new ChartDataModel(
                    rs.getString("promotion_type"), 
                    BigDecimal.valueOf(rs.getInt("count"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM PROMOTION " +
                    "WHERE status = 'ACTIVE' AND start_date <= SYSDATE AND end_date >= SYSDATE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setPromotionName(rs.getString("promotion_name"));
                promotion.setDescription(rs.getString("description"));
                promotion.setStartDate(rs.getDate("start_date"));
                promotion.setEndDate(rs.getDate("end_date"));
                promotion.setStatus(rs.getString("status"));
                promotion.setPromotionType(rs.getString("promotion_type"));
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }
    
    public boolean createPromotion(Promotion promotion) {
        String sql = "INSERT INTO PROMOTION (promotion_name, description, start_date, end_date, status, promotion_type) " +
                    "VALUES (?, ?, ?, ?, 'ACTIVE', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, promotion.getPromotionName());
            stmt.setString(2, promotion.getDescription());
            stmt.setDate(3, new java.sql.Date(promotion.getStartDate().getTime()));
            stmt.setDate(4, new java.sql.Date(promotion.getEndDate().getTime()));
            stmt.setString(5, promotion.getPromotionType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePromotion(Promotion promotion) {
        String sql = "UPDATE PROMOTION SET promotion_name = ?, description = ?, " +
                    "start_date = ?, end_date = ?, promotion_type = ? WHERE promotion_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, promotion.getPromotionName());
            stmt.setString(2, promotion.getDescription());
            stmt.setDate(3, new java.sql.Date(promotion.getStartDate().getTime()));
            stmt.setDate(4, new java.sql.Date(promotion.getEndDate().getTime()));
            stmt.setString(5, promotion.getPromotionType());
            stmt.setInt(6, promotion.getPromotionId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deactivatePromotion(int promotionId) {
        String sql = "UPDATE PROMOTION SET status = 'INACTIVE' WHERE promotion_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 