package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.LeaveRequest;
import java.sql.*;
import java.util.*;

public class LeaveRequestDAO {
    public int getPendingLeaveRequestsCount() {
        String sql = "SELECT COUNT(*) FROM LEAVE_REQUEST WHERE status = 'PENDING'";
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
    
    public Map<String, Integer> getLeaveRequestsByType() {
        Map<String, Integer> typeCount = new HashMap<>();
        String sql = "SELECT leave_type, COUNT(*) as count FROM LEAVE_REQUEST " +
                    "WHERE status = 'PENDING' GROUP BY leave_type";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                typeCount.put(rs.getString("leave_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeCount;
    }
    
    public List<LeaveRequest> getPendingLeaveRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM LEAVE_REQUEST WHERE status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LeaveRequest request = new LeaveRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setEmployeeId(rs.getInt("employee_id"));
                request.setLeaveType(rs.getString("leave_type"));
                request.setStartDate(rs.getDate("start_date"));
                request.setEndDate(rs.getDate("end_date"));
                request.setReason(rs.getString("reason"));
                request.setStatus(rs.getString("status"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public boolean approveLeaveRequest(int requestId) {
        String sql = "UPDATE LEAVE_REQUEST SET status = 'APPROVED' WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean rejectLeaveRequest(int requestId) {
        String sql = "UPDATE LEAVE_REQUEST SET status = 'REJECTED' WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 