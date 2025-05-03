package com.salesmate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
Shift Schema in oracle
    shift_id INT PRIMARY KEY,
    shift_name VARCHAR2(50) NOT NULL,
    start_time VARCHAR2(5) NOT NULL,
    end_time VARCHAR2(5) NOT NULL,
    break_time INT,
    description VARCHAR2(255),
    is_active NUMBER(1) DEFAULT 1
*/

@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_id")
    private int shiftId;

    @Column(name = "shift_name", nullable = false, length = 50)
    private String shiftName;

    @Column(name = "start_time", nullable = false, length = 5)
    private String startTime;

    @Column(name = "end_time", nullable = false, length = 5)
    private String endTime;

    @Column(name = "break_time")
    private Integer breakTime;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Constructors
    public Shift() {
    }

    public Shift(int shiftId, String shiftName, String startTime, String endTime, Integer breakTime, String description, boolean isActive) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakTime = breakTime;
        this.description = description;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(Integer breakTime) {
        this.breakTime = breakTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
