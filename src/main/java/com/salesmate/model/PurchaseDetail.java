package com.salesmate.model;

import jakarta.persistence.*;

/*
PurchaseDetail Schema in oracle
    purchase_detail_id INT PRIMARY KEY,
    purchase_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (purchase_id) REFERENCES PURCHASE(purchase_id),
    FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id)
);
*/

@Entity
@Table(name = "purchase_detail")
public class PurchaseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_detail_id")
    private int purchaseDetailId;

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private double price;

    // Getters and Setters
    public int getPurchaseDetailId() {
        return purchaseDetailId;
    }

    public void setPurchaseDetailId(int purchaseDetailId) {
        this.purchaseDetailId = purchaseDetailId;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
