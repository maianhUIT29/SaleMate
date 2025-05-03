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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/*
Invoice Schema in oracle
    invoice_id INT PRIMARY KEY,
    users_id INT NOT NULL,
    total_amount DECIMAL(10,2),
    payment_status VARCHAR2(20) CHECK (payment_status IN ('Paid', 'Unpaid')),
    created_at DATE DEFAULT SYSDATE,
    FOREIGN KEY (users_id) REFERENCES USERS(users_id)
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

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date createdAt;

    // Constructors
    public Invoice() {
    }

    public Invoice(int invoiceId, int usersId, BigDecimal totalAmount, String paymentStatus, Date createdAt) {
        this.invoiceId = invoiceId;
        this.usersId = usersId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
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
        this.usersId = usersId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    // For backward compatibility with existing code
    public BigDecimal getTotal() {
        return totalAmount;
    }

    public void setTotal(BigDecimal total) {
        if (total != null && total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }
        this.totalAmount = total;
    }
}