package com.salesmate.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/*
Invoice Schema in oracle
    invoice_id INT PRIMARY KEY,
    users_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at DATE DEFAULT SYSDATE,
    payment_status VARCHAR2(50)
 */

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_seq")
    @SequenceGenerator(name = "invoice_seq", sequenceName = "invoice_seq", allocationSize = 1)
    @Column(name = "invoice_id")
    private int invoiceId;

    @Column(name = "users_id", nullable = false)
    private int usersId;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date createdAt;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    // Constructors
    public Invoice() {
    }

    public Invoice(int invoiceId, int usersId, BigDecimal total, Date createdAt, String paymentStatus) {
        this.invoiceId = invoiceId;
        this.usersId = usersId;
        this.total = total;
        this.createdAt = createdAt;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        if (usersId <= 0) {
            throw new IllegalArgumentException("Users ID must be a positive number");
        }
        this.usersId = usersId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        if (total != null && total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }
        this.total = total;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        if (paymentStatus != null && !paymentStatus.equals("Paid") && !paymentStatus.equals("Unpaid")) {
            throw new IllegalArgumentException("Payment status must be either 'Paid' or 'Unpaid'");
        }
        this.paymentStatus = paymentStatus;
    }
}