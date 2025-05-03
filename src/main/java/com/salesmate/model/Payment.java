package com.salesmate.model;

import java.math.BigDecimal;
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
Payment Schema in oracle
    payment_id INT PRIMARY KEY,
    invoice_id INT NOT NULL,
    payment_method VARCHAR2(20) CHECK (payment_method IN ('Cash', 'QR Code', 'Bank Card')),
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE DEFAULT SYSDATE,
    FOREIGN KEY (invoice_id) REFERENCES INVOICE(invoice_id)
*/

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @Column(name = "invoice_id", nullable = false)
    private int invoiceId;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date paymentDate;

    // Constructors
    public Payment() {
    }

    public Payment(int paymentId, int invoiceId, String paymentMethod, BigDecimal amount, Date paymentDate) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
    
    // Constructor for backward compatibility
    public Payment(int paymentId, int invoiceId, String paymentMethod, double amount, Date paymentDate) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.paymentMethod = paymentMethod;
        this.amount = BigDecimal.valueOf(amount);
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod != null && !paymentMethod.equals("Cash") && !paymentMethod.equals("QR Code") && !paymentMethod.equals("Bank Card")) {
            throw new IllegalArgumentException("Payment method must be one of: Cash, QR Code, Bank Card");
        }
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    // For backward compatibility
    public void setAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
