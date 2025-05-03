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
Attendance Schema in oracle
    attendance_id INT PRIMARY KEY,
    employee_id INT NOT NULL,
    attendance_date DATE DEFAULT SYSDATE,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    shift_id INT,
    status VARCHAR2(20) DEFAULT 'Present' CHECK (status IN ('Present', 'Absent', 'Late', 'Early Leave')),
    late_minutes INT DEFAULT 0,
    early_leave_minutes INT DEFAULT 0,
    total_working_hours DECIMAL(5,2),
    note VARCHAR2(255)
*/

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private int attendanceId;

    @Column(name = "employee_id", nullable = false)
    private int employeeId;

    @Temporal(TemporalType.DATE)
    @Column(name = "attendance_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date attendanceDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_in")
    private Date checkIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_out")
    private Date checkOut;

    @Column(name = "shift_id")
    private Integer shiftId;

    @Column(name = "status", length = 20)
    private String status = "Present";

    @Column(name = "late_minutes")
    private int lateMinutes = 0;

    @Column(name = "early_leave_minutes")
    private int earlyLeaveMinutes = 0;

    @Column(name = "total_working_hours", precision = 5, scale = 2)
    private double totalWorkingHours;

    @Column(name = "note", length = 255)
    private String note;

    // Constructors
    public Attendance() {
    }

    public Attendance(int attendanceId, int employeeId, Date attendanceDate, Date checkIn, Date checkOut, 
                      Integer shiftId, String status, int lateMinutes, int earlyLeaveMinutes, 
                      double totalWorkingHours, String note) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.attendanceDate = attendanceDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.shiftId = shiftId;
        this.status = status;
        this.lateMinutes = lateMinutes;
        this.earlyLeaveMinutes = earlyLeaveMinutes;
        this.totalWorkingHours = totalWorkingHours;
        this.note = note;
    }

    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.equals("Present") && !status.equals("Absent") 
                && !status.equals("Late") && !status.equals("Early Leave")) {
            throw new IllegalArgumentException("Status must be one of: Present, Absent, Late, Early Leave");
        }
        this.status = status;
    }

    public int getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(int lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public int getEarlyLeaveMinutes() {
        return earlyLeaveMinutes;
    }

    public void setEarlyLeaveMinutes(int earlyLeaveMinutes) {
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    public double getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public void setTotalWorkingHours(double totalWorkingHours) {
        this.totalWorkingHours = totalWorkingHours;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
