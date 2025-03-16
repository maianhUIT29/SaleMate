package com.salesmate.model;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;

/*
Invoice Schema in oracle
    invoice_id INT PRIMARY KEY,
    users_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    created_at DATE DEFAULT SYSDATE,
    status VARCHAR2(50)
 */

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private int invoiceId;

    @Column(name = "users_id", nullable = false)
    private int usersId;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date createdAt;

    @Column(name = "status", length = 50)
    private String status;

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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}