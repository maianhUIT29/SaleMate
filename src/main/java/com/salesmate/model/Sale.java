package com.salesmate.model;

public class Sale {
    private String productName;
    private int totalQuantitySold;

    // Constructor
    public Sale(String productName, int totalQuantitySold) {
        this.productName = productName;
        this.totalQuantitySold = totalQuantitySold;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(int totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }
}
