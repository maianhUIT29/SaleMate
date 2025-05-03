package com.salesmate.model;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity representing the PROMOTION_DETAIL table
 * This table stores the details of promotions applied to products
 */
@Entity
@Table(name = "PROMOTION_DETAIL")
public class PromotionDetail {
    /**
     * Primary key for the promotion detail
     */
    @Id
    @Column(name = "promotion_detail_id")
    private Integer promotionDetailId;

    /**
     * Foreign key reference to the promotion
     */
    @Column(name = "promotion_id")
    private Integer promotionId;

    /**
     * Foreign key reference to the product
     */
    @Column(name = "product_id")
    private Integer productId;

    /**
     * Type of discount (e.g., percentage, fixed amount)
     */
    @Column(name = "discount_type")
    private String discountType;

    /**
     * Value of the discount
     */
    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    /**
     * Maximum amount that can be discounted
     */
    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    // Default constructor
    public PromotionDetail() {
    }

    // All-args constructor
    public PromotionDetail(Integer promotionDetailId, Integer promotionId, Integer productId,
                         String discountType, BigDecimal discountValue, BigDecimal maxDiscountAmount) {
        this.promotionDetailId = promotionDetailId;
        this.promotionId = promotionId;
        this.productId = productId;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.maxDiscountAmount = maxDiscountAmount;
    }

    // Getters and Setters
    public Integer getPromotionDetailId() {
        return promotionDetailId;
    }

    public void setPromotionDetailId(Integer promotionDetailId) {
        this.promotionDetailId = promotionDetailId;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }
}