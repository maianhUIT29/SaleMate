package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.AttendanceAdjustment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceAdjustmentDAO {

    /**
     * Lấy tất cả attendance_id đã có ít nhất một bản ghi điều chỉnh.
     * Dùng để filter nhanh “Có/Những bản ghi Điều chỉnh” mà không phải query hàng loạt.
     */
    public Set<Integer> getAllAdjustedAttendanceIds() {
        Set<Integer> result = new HashSet<>();
        String sql = "SELECT DISTINCT attendance_id FROM ATTENDANCE_ADJUSTMENT";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getInt("attendance_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy tất cả các yêu cầu điều chỉnh của một attendance cụ thể (đang sử dụng khi bấm Xem Điều Chỉnh).
     */
    public List<AttendanceAdjustment> findByAttendanceId(int attendanceId) {
        List<AttendanceAdjustment> list = new ArrayList<>();
        String sql = "SELECT adjustment_id, attendance_id, old_status, new_status, reason, "
                   + "requested_by, approved_by, request_date, approval_date, status "
                   + "FROM ATTENDANCE_ADJUSTMENT WHERE attendance_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceAdjustment a = new AttendanceAdjustment();
                    a.setAdjustmentId(rs.getInt("adjustment_id"));
                    a.setAttendanceId(rs.getInt("attendance_id"));
                    a.setOldStatus(rs.getString("old_status"));
                    a.setNewStatus(rs.getString("new_status"));
                    a.setReason(rs.getString("reason"));
                    a.setRequestedBy(rs.getInt("requested_by"));
                    int ap = rs.getInt("approved_by");
                    if (!rs.wasNull()) {
                        a.setApprovedBy(ap);
                    }
                    a.setRequestDate(rs.getDate("request_date"));
                    a.setApprovalDate(rs.getDate("approval_date"));
                    a.setStatus(rs.getString("status"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
