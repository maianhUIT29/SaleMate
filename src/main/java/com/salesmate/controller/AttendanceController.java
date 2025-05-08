package com.salesmate.controller;

import com.salesmate.dao.AttendanceDAO;
import com.salesmate.model.Attendance;
import java.util.*;

public class AttendanceController {
    private final AttendanceDAO attendanceDAO;
    
    public AttendanceController() {
        this.attendanceDAO = new AttendanceDAO();
    }
    
    public int getTodayAttendanceCount() {
        return attendanceDAO.getTodayAttendanceCount();
    }
    
    public Map<String, Integer> getAttendanceByStatus() {
        return attendanceDAO.getAttendanceByStatus();
    }
    
    public List<Attendance> getTodayAttendance() {
        return attendanceDAO.getTodayAttendance();
    }
    
    public boolean checkIn(int employeeId) {
        return attendanceDAO.checkIn(employeeId);
    }
    
    public boolean checkOut(int employeeId) {
        return attendanceDAO.checkOut(employeeId);
    }
} 