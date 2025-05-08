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
LeaveRequest Schema in oracle
    request_id INT PRIMARY KEY,
    employee_id INT NOT NULL,
    leave_type VARCHAR2(20) CHECK (leave_type IN ('Sick', 'Vacation', 'Personal')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR2(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected')),
    reason VARCHAR2(255),
    request_date DATE DEFAULT SYSDATE
*/

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int requestId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "leave_type", length = 20)
    private String leaveType;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "status", length = 20)
    private String status = "Pending";

    @Column(name = "reason", length = 255)
    private String reason;

    @Temporal(TemporalType.DATE)
    @Column(name = "request_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date requestDate;

    // Constructors
    public LeaveRequest() {
    }

    public LeaveRequest(int requestId, Employee employee, String leaveType, Date startDate, Date endDate,
                        String status, String reason, Date requestDate) {
        this.requestId = requestId;
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.reason = reason;
        this.requestDate = requestDate;
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        if (leaveType != null && !leaveType.equals("Sick") && !leaveType.equals("Vacation") && !leaveType.equals("Personal")) {
            throw new IllegalArgumentException("Leave type must be one of: Sick, Vacation, Personal");
        }
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.equals("Pending") && !status.equals("Approved") && !status.equals("Rejected")) {
            throw new IllegalArgumentException("Status must be one of: Pending, Approved, Rejected");
        }
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public int getEmployeeId() {
        return employee.getEmployeeId();
    }

    public void setEmployeeId(int employeeId) {
        // This method is not used in the current implementation
    }
}
