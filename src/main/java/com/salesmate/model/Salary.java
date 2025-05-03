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
Salary Schema in oracle
    salary_id INT PRIMARY KEY,
    employee_id INT NOT NULL,
    basic_salary DECIMAL(10,2) NOT NULL,
    payment_period VARCHAR2(7) NOT NULL,
    payment_date DATE,
    status VARCHAR2(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Processed', 'Paid', 'Cancelled')),
    total_salary DECIMAL(10,2),
    note VARCHAR2(255)
*/

@Entity
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private int salaryId;

    @Column(name = "employee_id", nullable = false)
    private int employeeId;

    @Column(name = "basic_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;
    
    @Column(name = "payment_period", nullable = false, length = 7)
    private String paymentPeriod;

    @Temporal(TemporalType.DATE)
    @Column(name = "payment_date")
    private Date paymentDate;
    
    @Column(name = "status", length = 20)
    private String status = "Pending";
    
    @Column(name = "total_salary", precision = 10, scale = 2)
    private BigDecimal totalSalary;
    
    @Column(name = "note", length = 255)
    private String note;

    // Constructors
    public Salary() {
    }

    public Salary(int salaryId, int employeeId, BigDecimal basicSalary, String paymentPeriod, 
                 Date paymentDate, String status, BigDecimal totalSalary, String note) {
        this.salaryId = salaryId;
        this.employeeId = employeeId;
        this.basicSalary = basicSalary;
        this.paymentPeriod = paymentPeriod;
        this.paymentDate = paymentDate;
        this.status = status;
        this.totalSalary = totalSalary;
        this.note = note;
    }
    
    // Constructor for backward compatibility
    public Salary(int salaryId, int employeeId, double salaryAmount, Date paymentDate) {
        this.salaryId = salaryId;
        this.employeeId = employeeId;
        this.basicSalary = BigDecimal.valueOf(salaryAmount);
        this.paymentDate = paymentDate;
        this.totalSalary = BigDecimal.valueOf(salaryAmount);
        // Default values
        this.paymentPeriod = String.format("%tY-%tm", paymentDate, paymentDate);
        this.status = "Pending";
    }

    // Getters and Setters
    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public String getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(String paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.equals("Pending") && !status.equals("Processed") 
                && !status.equals("Paid") && !status.equals("Cancelled")) {
            throw new IllegalArgumentException("Status must be one of: Pending, Processed, Paid, Cancelled");
        }
        this.status = status;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    // For backward compatibility
    public int getUserId() {
        return this.employeeId;
    }

    public void setUserId(int employeeId) {
        this.employeeId = employeeId;
    }
    
    public double getSalaryAmount() {
        return this.basicSalary != null ? this.basicSalary.doubleValue() : 0.0;
    }

    public void setSalaryAmount(double salaryAmount) {
        this.basicSalary = BigDecimal.valueOf(salaryAmount);
        // Also update total salary if it's null
        if (this.totalSalary == null) {
            this.totalSalary = this.basicSalary;
        }
    }
}
