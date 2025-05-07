package com.salesmate.model;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "PROMOTION")
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Integer promotionId;
    
    @Column(name = "promotion_name", nullable = false)
    private String promotionName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, INACTIVE, EXPIRED
    
    @Column(name = "promotion_type")
    private String promotionType;
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    // Default constructor
    public Promotion() {
        this.createdAt = new Date();
        this.status = "ACTIVE";
    }
    
    // All-args constructor
    public Promotion(Integer promotionId, String promotionName, String description, 
                   Date startDate, Date endDate, String status, String promotionType, Date createdAt) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.promotionType = promotionType;
        this.createdAt = createdAt != null ? createdAt : new Date();
    }
    
    // Getters and Setters
    public Integer getPromotionId() {
        return promotionId;
    }
    
    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }
    
    public String getPromotionName() {
        return promotionName;
    }
    
    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPromotionType() {
        return promotionType;
    }
    
    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    // Check if promotion is active
    public boolean isActive() {
        Date now = new Date();
        return "ACTIVE".equals(status) && 
               startDate.before(now) && 
               endDate.after(now);
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", promotionType='" + promotionType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
