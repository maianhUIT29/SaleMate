package com.salesmate.model;

import java.util.Date;
import jakarta.persistence.*;

/*
Purchase Schema in oracle
    purchase_id INT PRIMARY KEY,
    supplier_id INT NOT NULL,
    users_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    purchase_date DATE DEFAULT SYSDATE,
    FOREIGN KEY (supplier_id) REFERENCES SUPPLIER(supplier_id),
    FOREIGN KEY (users_id) REFERENCES USERS(users_id)
);
*/

@Entity
@Table(name = "purchase")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private int purchaseId;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private double totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date purchaseDate;

    // Getters and Setters
    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
