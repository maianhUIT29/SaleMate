package com.salesmate.model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal originalPrice; // Added field for original price
    private int quantity;
    private String barcode;
    private String image;
    private int maxQuantity; // Max quantity available
    private String category; // New field for category
    private String description; // New field for product description
    private double discountPercent; // New field for discount percent
    private String description;

    // Default constructor
    public Product() {
    }

    // Constructor without originalPrice
    public Product(int productId, String productName, BigDecimal price, int quantity, String barcode, String image) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.image = image;
    }

    // Constructor with originalPrice
    public Product(int productId, String productName, BigDecimal originalPrice, BigDecimal price, int quantity, String barcode, String image) {
        this.productId = productId;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.image = image;
    }

    // Getters and setters
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

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


