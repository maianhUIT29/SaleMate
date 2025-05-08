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

/* SalaryDetail Schema in oracle
"SALARY_ID" NUMBER(*,0), 
	"EMPLOYEE_ID" NUMBER(*,0) NOT NULL ENABLE, 
	"BASIC_SALARY" NUMBER(10,2) NOT NULL ENABLE, 
	"PAYMENT_PERIOD" VARCHAR2(7 BYTE) NOT NULL ENABLE, 
	"PAYMENT_DATE" DATE, 
	"STATUS" VARCHAR2(20 BYTE) DEFAULT 'Pending', 
	"TOTAL_SALARY" NUMBER(10,2), 
	"NOTE" VARCHAR2(255 BYTE), 
	 CHECK (status IN ('Pending', 'Processed', 'Paid', 'Cancelled')) ENABLE, 
	 PRIMARY KEY ("SALARY_ID")
*/

@Entity
@Table(name = "salary_detail")
public class SalaryDetail {
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
    public SalaryDetail() {
    }

    public SalaryDetail(int salaryId, int employeeId, BigDecimal basicSalary, String paymentPeriod,
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
}