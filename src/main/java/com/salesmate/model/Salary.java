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

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @Column(name = "salary_amount", precision = 10, scale = 2)
    private double salaryAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date paymentDate;

    // Constructors
    public Salary() {
    }

    public Salary(int salaryId, User user, double salaryAmount, Date paymentDate) {
        this.salaryId = salaryId;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
