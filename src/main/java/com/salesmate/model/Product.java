package com.salesmate.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/*
Product Schema in oracle
    product_id INT PRIMARY KEY,
    product_name VARCHAR2(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    barcode VARCHAR2(20),
    category VARCHAR2(50),
    image VARCHAR2(255),
    description VARCHAR2(255),
    created_at DATE DEFAULT SYSDATE,
    created_by INT,
    last_updated DATE DEFAULT SYSDATE,
    last_updated_by INT
 */

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "barcode", length = 20)
    private String barcode;
    
    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "image", length = 255)
    private String image;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date createdAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date lastUpdated;
    
    @ManyToOne
    @JoinColumn(name = "last_updated_by")
    private User lastUpdatedBy;

    private int maxQuantity; // Non-database field to track maximum quantity for UI

    // Constructors
    public Product() {
    }

    public Product(int productId, String productName, BigDecimal price, int quantity, String barcode, String category,
                  String image, String description, Date createdAt, User createdBy, Date lastUpdated, User lastUpdatedBy) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.category = category;
        this.image = image;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    // Basic constructor for essential fields
    public Product(int productId, String productName, BigDecimal price, int quantity, String barcode, String image) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.image = image;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public User getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(User lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity; 
    }

    public int getMaxQuantity() {
        return this.maxQuantity;
    }
}


