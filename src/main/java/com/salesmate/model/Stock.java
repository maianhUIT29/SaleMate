package com.salesmate.model;

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
Stock Schema in oracle
    stock_id INT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    storage_location VARCHAR2(100),
    last_updated DATE DEFAULT SYSDATE,
    FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id)
);
*/

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int stockId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date lastUpdated;

    // Constructors
    public Stock() {
    }

    public Stock(int stockId, Product product, int quantity, String storageLocation, Date lastUpdated) {
        this.stockId = stockId;
        this.product = product;
        this.quantity = quantity;
        this.storageLocation = storageLocation;
        this.lastUpdated = lastUpdated;
    }
    

    // Getters and Setters
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
