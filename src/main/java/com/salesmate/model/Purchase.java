package com.salesmate.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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

    @Column(name = "supplier_id", nullable = false)
    private int supplierId;

    @Column(name = "users_id", nullable = false)
    private int usersId;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private double totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date purchaseDate;

    // Constructors
    public Purchase() {
    }

    public Purchase(int purchaseId, int supplierId, int usersId, double totalAmount, Date purchaseDate) {
        this.purchaseId = purchaseId;
        this.supplierId = supplierId;
        this.usersId = usersId;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
    }

    // Getters and Setters
    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
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
