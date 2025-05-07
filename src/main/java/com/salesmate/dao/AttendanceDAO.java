package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Attendance;
import java.sql.*;
import java.util.*;

public class AttendanceDAO {
    public int getTodayAttendanceCount() {
        String sql = "SELECT COUNT(*) FROM ATTENDANCE WHERE TRUNC(check_in_time) = TRUNC(SYSDATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public Map<String, Integer> getAttendanceByStatus() {
        Map<String, Integer> statusCount = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM ATTENDANCE " +
                    "WHERE TRUNC(check_in_time) = TRUNC(SYSDATE) " +
                    "GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statusCount.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statusCount;
    }
    
    public List<Attendance> getTodayAttendance() {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM ATTENDANCE WHERE TRUNC(check_in_time) = TRUNC(SYSDATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setEmployeeId(rs.getInt("employee_id"));
                attendance.setCheckInTime(rs.getTimestamp("check_in_time"));
                attendance.setCheckOutTime(rs.getTimestamp("check_out_time"));
                attendance.setStatus(rs.getString("status"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }
    
    public boolean checkIn(int employeeId) {
        String sql = "INSERT INTO ATTENDANCE (employee_id, check_in_time, status) VALUES (?, SYSDATE, 'Present')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean checkOut(int employeeId) {
        String sql = "UPDATE ATTENDANCE SET check_out_time = SYSDATE " +
                    "WHERE employee_id = ? AND TRUNC(check_in_time) = TRUNC(SYSDATE) " +
                    "AND check_out_time IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 