package com.salesmate.controller;

import com.salesmate.dao.AttendanceDAO;
import com.salesmate.dao.AttendanceAdjustmentDAO;
import com.salesmate.dao.LeaveRequestDAO;
import com.salesmate.model.Attendance;
import com.salesmate.model.LeaveRequest;
import com.salesmate.model.AttendanceAdjustment;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttendanceController {
    private final AttendanceDAO attendanceDAO;
    private final LeaveRequestDAO leaveRequestDAO;
    private final AttendanceAdjustmentDAO adjustmentDAO;

    public AttendanceController() {
        attendanceDAO   = new AttendanceDAO();
        leaveRequestDAO = new LeaveRequestDAO();
        adjustmentDAO   = new AttendanceAdjustmentDAO();
    }

    /** Lấy toàn bộ các bản ghi Attendance để hiển thị. */
    public List<Attendance> getAllAttendance() {
        return attendanceDAO.getAllAttendance();
    }

    /** Lấy danh sách Attendance của ngày hôm nay. */
    public List<Attendance> getTodayAttendance() {
        return attendanceDAO.getTodayAttendance();
    }

    /** Tìm Attendance theo bộ lọc (nếu có dùng). */
    public List<Attendance> findAttendanceByFilter(Map<String, Object> filters) {
        return attendanceDAO.findAttendanceByFilter(filters);
    }

    /** Truy xuất một bản ghi Attendance theo ngày và employeeId. */
    public Attendance getAttendanceByEmployeeAndDate(Date attendanceDate, int employeeId) {
        return attendanceDAO.getAttendanceByEmployeeAndDate(attendanceDate, employeeId);
    }

    /** Cập nhật một bản ghi Attendance (check-in, check-out, status). */
    public boolean updateAttendance(Attendance a) {
        return attendanceDAO.updateAttendance(a);
    }

    /** Lấy số lượng Attendance của ngày hôm nay (thống kê). */
    public int getTodayAttendanceCount() {
        return attendanceDAO.getTodayAttendanceCount();
    }

    /** Lấy thống kê theo trạng thái của ngày hôm nay. */
    public Map<String, Integer> getTodayStatusStats() {
        return attendanceDAO.getAttendanceByStatus();
    }

    /** Lấy danh sách LeaveRequest của một employee theo ID. */
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(int employeeId) {
        return leaveRequestDAO.getLeaveRequestsByEmployeeId(employeeId);
    }

    /** Lấy tập tất cả employee_id có ít nhất một đơn nghỉ phép. */
    public Set<Integer> getAllEmployeesWithLeave() {
        return leaveRequestDAO.getAllEmployeesWithLeave();
    }

    /** Lấy tập tất cả attendance_id đã có ít nhất một record điều chỉnh. */
    public Set<Integer> getAllAdjustedAttendanceIds() {
        return adjustmentDAO.getAllAdjustedAttendanceIds();
    }

    /** Lấy danh sách các bản ghi điều chỉnh theo attendanceId. */
    public List<AttendanceAdjustment> getAdjustmentsByAttendanceId(int attendanceId) {
        return adjustmentDAO.findByAttendanceId(attendanceId);
    }
}
