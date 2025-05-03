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
AttendanceAdjustment Schema in oracle
    adjustment_id INT PRIMARY KEY,
    attendance_id INT NOT NULL,
    old_status VARCHAR2(20),
    new_status VARCHAR2(20),
    reason VARCHAR2(255) NOT NULL,
    requested_by INT NOT NULL,
    approved_by INT,
    request_date DATE DEFAULT SYSDATE,
    approval_date DATE,
    status VARCHAR2(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected'))
*/

@Entity
@Table(name = "attendance_adjustment")
public class AttendanceAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_id")
    private int adjustmentId;

    @Column(name = "attendance_id", nullable = false)
    private int attendanceId;

    @Column(name = "old_status", length = 20)
    private String oldStatus;

    @Column(name = "new_status", length = 20)
    private String newStatus;

    @Column(name = "reason", length = 255, nullable = false)
    private String reason;

    @Column(name = "requested_by", nullable = false)
    private int requestedBy;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "request_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date requestDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "approval_date")
    private Date approvalDate;

    @Column(name = "status", length = 20)
    private String status = "Pending";

    // Constructors
    public AttendanceAdjustment() {
    }

    public AttendanceAdjustment(int adjustmentId, int attendanceId, String oldStatus, String newStatus,
                              String reason, int requestedBy, Integer approvedBy,
                              Date requestDate, Date approvalDate, String status) {
        this.adjustmentId = adjustmentId;
        this.attendanceId = attendanceId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.requestDate = requestDate;
        this.approvalDate = approvalDate;
        this.status = status;
    }

    // Getters and Setters
    public int getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(int adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(int requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
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
}
