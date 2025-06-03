package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Attendance;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AttendanceDAO {

    public int getTodayAttendanceCount() {
        String sql = "SELECT COUNT(*) FROM ATTENDANCE WHERE TRUNC(attendance_date) = TRUNC(SYSDATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getAttendanceByStatus() {
        Map<String, Integer> statusCount = new HashMap<>();
        String sql = "SELECT status, COUNT(*) FROM ATTENDANCE " +
                     "WHERE TRUNC(attendance_date) = TRUNC(SYSDATE) GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statusCount.put(rs.getString("status"), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statusCount;
    }

    public List<Attendance> getTodayAttendance() {
        String sql = "SELECT * FROM ATTENDANCE WHERE TRUNC(attendance_date) = TRUNC(SYSDATE)";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Attendance> getAllAttendance() {
        String sql = "SELECT * FROM ATTENDANCE ORDER BY attendance_date DESC, check_in DESC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

 

    public Attendance getAttendanceByEmployeeAndDate(java.util.Date attendanceDate, int employeeId) {
        String sql = "SELECT * FROM ATTENDANCE WHERE employee_id = ? AND TRUNC(attendance_date) = TRUNC(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setDate(2, new Date(attendanceDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Attendance> findAttendanceByFilter(Map<String, Object> filters) {
        List<Attendance> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ATTENDANCE WHERE 1=1 ");
        if (filters.containsKey("employeeId")) sql.append(" AND employee_id = ? ");
        if (filters.containsKey("shiftId")) sql.append(" AND shift_id = ? ");
        if (filters.containsKey("status")) sql.append(" AND status = ? ");
        if (filters.containsKey("dateFrom")) sql.append(" AND TRUNC(attendance_date) >= TRUNC(?) ");
        if (filters.containsKey("dateTo")) sql.append(" AND TRUNC(attendance_date) <= TRUNC(?) ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (filters.containsKey("employeeId")) stmt.setInt(idx++, (Integer) filters.get("employeeId"));
            if (filters.containsKey("shiftId")) stmt.setInt(idx++, (Integer) filters.get("shiftId"));
            if (filters.containsKey("status")) stmt.setString(idx++, (String) filters.get("status"));
            if (filters.containsKey("dateFrom")) stmt.setDate(idx++, new Date(((java.util.Date) filters.get("dateFrom")).getTime()));
            if (filters.containsKey("dateTo")) stmt.setDate(idx++, new Date(((java.util.Date) filters.get("dateTo")).getTime()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    
 public boolean updateAttendance(Attendance a) {
        String sql = "UPDATE ATTENDANCE " +
                     "SET check_in = ?, check_out = ?, status = ? " +
                     "WHERE attendance_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Nếu muốn cho phép bỏ trống check_in / check_out (NULL), ta kiểm tra:
            if (a.getCheckInTime() != null) {
                stmt.setTimestamp(1, new Timestamp(a.getCheckInTime().getTime()));
            } else {
                stmt.setTimestamp(1, null);
            }

            if (a.getCheckOutTime() != null) {
                stmt.setTimestamp(2, new Timestamp(a.getCheckOutTime().getTime()));
            } else {
                stmt.setTimestamp(2, null);
            }

            stmt.setString(3, a.getStatus());
            stmt.setInt(4, a.getAttendanceId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
     private Attendance mapResultSet(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setEmployeeId(rs.getInt("employee_id"));
        a.setShiftId(rs.getInt("shift_id"));
        a.setAttendanceDate(rs.getDate("attendance_date"));
        a.setCheckInTime(rs.getTimestamp("check_in"));
        a.setCheckOutTime(rs.getTimestamp("check_out"));
        a.setStatus(rs.getString("status"));
        a.setLateMinutes(rs.getInt("late_minutes"));
        a.setEarlyLeaveMinutes(rs.getInt("early_leave_minutes"));
        BigDecimal totalHrBD = rs.getBigDecimal("total_working_hours");
        a.setTotalWorkingHours(totalHrBD != null ? totalHrBD.doubleValue() : 0.0);
        a.setNote(rs.getString("note"));
        return a;
    }
}
