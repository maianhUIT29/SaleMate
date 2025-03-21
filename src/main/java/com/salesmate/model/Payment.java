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
Payment Schema in oracle
    payment_id INT PRIMARY KEY,
    invoice_id INT NOT NULL,
    payment_method VARCHAR2(20) CHECK (payment_method IN ('Cash', 'QR Code', 'Bank Card')),
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE DEFAULT SYSDATE,
    FOREIGN KEY (invoice_id) REFERENCES INVOICE(invoice_id)
);
*/

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private double amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date paymentDate;

    // Constructors
    public Payment() {
    }

    public Payment(int paymentId, Invoice invoice, String paymentMethod, double amount, Date paymentDate) {
        this.paymentId = paymentId;
        this.invoice = invoice;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
