package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.LeaveRequest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeaveRequestDAO {

    // Đếm số yêu cầu nghỉ đang chờ duyệt
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

    // Thống kê số lượng yêu cầu nghỉ theo loại (leave_type)
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

    // Lấy danh sách yêu cầu nghỉ đang chờ duyệt
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

    // Duyệt yêu cầu nghỉ phép
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

    // Từ chối yêu cầu nghỉ phép
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

    // Tạo mới yêu cầu nghỉ phép (trạng thái mặc định: PENDING)
    public boolean insertLeaveRequest(LeaveRequest lr) {
        String sql = "INSERT INTO LEAVE_REQUEST "
                   + "(employee_id, leave_type, start_date, end_date, reason, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lr.getEmployeeId());
            stmt.setString(2, lr.getLeaveType());
            stmt.setDate(3, new Date(lr.getStartDate().getTime()));
            stmt.setDate(4, new Date(lr.getEndDate().getTime()));
            stmt.setString(5, lr.getReason());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả yêu cầu nghỉ theo ID nhân viên
    public List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId) {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT request_id, employee_id, leave_type, start_date, end_date, reason, status "
                   + "FROM LEAVE_REQUEST WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveRequest lr = new LeaveRequest();
                    lr.setRequestId(rs.getInt("request_id"));
                    lr.setEmployeeId(rs.getInt("employee_id"));
                    lr.setLeaveType(rs.getString("leave_type"));
                    lr.setStartDate(rs.getDate("start_date"));
                    lr.setEndDate(rs.getDate("end_date"));
                    lr.setReason(rs.getString("reason"));
                    lr.setStatus(rs.getString("status"));
                    list.add(lr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm yêu cầu nghỉ theo request_id
    public LeaveRequest findById(int requestId) {
        String sql = "SELECT request_id, employee_id, leave_type, start_date, end_date, reason, status "
                   + "FROM LEAVE_REQUEST WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LeaveRequest lr = new LeaveRequest();
                    lr.setRequestId(rs.getInt("request_id"));
                    lr.setEmployeeId(rs.getInt("employee_id"));
                    lr.setLeaveType(rs.getString("leave_type"));
                    lr.setStartDate(rs.getDate("start_date"));
                    lr.setEndDate(rs.getDate("end_date"));
                    lr.setReason(rs.getString("reason"));
                    lr.setStatus(rs.getString("status"));
                    return lr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
 public Set<Integer> getAllEmployeesWithLeave() {
        Set<Integer> result = new HashSet<>();
        String sql = "SELECT DISTINCT employee_id FROM LEAVE_REQUEST";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getInt("employee_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Phương thức rút gọn để gọi từ controller hoặc component
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(int employeeId) {
        return getLeaveRequestsByEmployee(employeeId);
    }
}
