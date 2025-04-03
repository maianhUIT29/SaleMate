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
Salary Schema in oracle
    salary_id INT PRIMARY KEY,
    users_id INT NOT NULL,
    salary_amount DECIMAL(10,2),
    payment_date DATE DEFAULT SYSDATE,
    FOREIGN KEY (users_id) REFERENCES USERS(users_id)
);
*/

@Entity
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private int salaryId;

    @Column(name = "users_id", nullable = false)
    private int userId;

    @Column(name = "salary_amount", precision = 10, scale = 2)
    private double salaryAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date paymentDate;

    // Constructors
    public Salary() {
    }

    public Salary(int salaryId, int userId, double salaryAmount, Date paymentDate) {
        this.salaryId = salaryId;
        this.userId = userId;
        this.salaryAmount = salaryAmount;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getSalaryAmount() {
        return salaryAmount;
    }

    public void setSalaryAmount(double salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
