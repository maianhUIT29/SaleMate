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
EmployeeShift Schema in oracle
    employee_shift_id INT PRIMARY KEY,
    employee_id INT NOT NULL,
    shift_id INT NOT NULL,
    assigned_date DATE NOT NULL,
    status VARCHAR2(20) DEFAULT 'Scheduled' CHECK (status IN ('Scheduled', 'Completed', 'Absent', 'Late'))
*/

@Entity
@Table(name = "employee_shift")
public class EmployeeShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_shift_id")
    private int employeeShiftId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Temporal(TemporalType.DATE)
    @Column(name = "assigned_date", nullable = false)
    private Date assignedDate;

    @Column(name = "status", length = 20)
    private String status = "Scheduled";

    // Constructors
    public EmployeeShift() {
    }

    public EmployeeShift(int employeeShiftId, Employee employee, Shift shift, Date assignedDate, String status) {
        this.employeeShiftId = employeeShiftId;
        this.employee = employee;
        this.shift = shift;
        this.assignedDate = assignedDate;
        this.status = status;
    }

    // Getters and Setters
    public int getEmployeeShiftId() {
        return employeeShiftId;
    }

    public void setEmployeeShiftId(int employeeShiftId) {
        this.employeeShiftId = employeeShiftId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.equals("Scheduled") && !status.equals("Completed") 
                && !status.equals("Absent") && !status.equals("Late")) {
            throw new IllegalArgumentException("Status must be one of: Scheduled, Completed, Absent, Late");
        }
        this.status = status;
    }
}
