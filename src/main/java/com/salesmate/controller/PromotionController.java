package com.salesmate.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Promotion;
import com.salesmate.model.PromotionDetail;

public class PromotionController {
    
    /**
     * Get active promotion for a specific product
     * @param productId The product ID to check for promotions
     * @return PromotionDetail if an active promotion exists, null otherwise
     */
    public PromotionDetail getActivePromotionForProduct(int productId) {
        String sql = "SELECT pd.*, p.promotion_name, p.start_date, p.end_date " +
                    "FROM PROMOTION_DETAIL pd " +
                    "JOIN PROMOTION p ON pd.promotion_id = p.promotion_id " +
                    "WHERE pd.product_id = ? " +
                    "AND p.start_date <= SYSDATE " +
                    "AND p.end_date >= SYSDATE " +
                    "AND p.status = 'ACTIVE' " +
                    "ORDER BY pd.discount_value DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PromotionDetail detail = new PromotionDetail();
                    detail.setPromotionDetailId(rs.getInt("promotion_detail_id"));
                    detail.setPromotionId(rs.getInt("promotion_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setDiscountType(rs.getString("discount_type"));
                    detail.setDiscountValue(rs.getBigDecimal("discount_value"));
                    detail.setMaxDiscountAmount(rs.getBigDecimal("max_discount_amount"));
                    return detail;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // No active promotion found
    }
    
    /**
     * Gets all active promotions
     * @return List of active promotions
     */
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM PROMOTION WHERE status = 'ACTIVE' AND start_date <= SYSDATE AND end_date >= SYSDATE";
        
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
                promotion.setCreatedAt(rs.getTimestamp("created_at"));
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return promotions;
    }
    
    /**
     * Gets promotion details for a specific promotion
     * @param promotionId The promotion ID
     * @return List of promotion details
     */
    public List<PromotionDetail> getPromotionDetails(int promotionId) {
        List<PromotionDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM PROMOTION_DETAIL WHERE promotion_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, promotionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PromotionDetail detail = new PromotionDetail();
                    detail.setPromotionDetailId(rs.getInt("promotion_detail_id"));
                    detail.setPromotionId(rs.getInt("promotion_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setDiscountType(rs.getString("discount_type"));
                    detail.setDiscountValue(rs.getBigDecimal("discount_value"));
                    detail.setMaxDiscountAmount(rs.getBigDecimal("max_discount_amount"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return details;
    }
}
