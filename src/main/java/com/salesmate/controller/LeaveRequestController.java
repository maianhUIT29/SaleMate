package com.salesmate.controller;

import com.salesmate.dao.LeaveRequestDAO;
import com.salesmate.model.LeaveRequest;
import java.util.*;

public class LeaveRequestController {
    private final LeaveRequestDAO leaveRequestDAO;
    
    public LeaveRequestController() {
        this.leaveRequestDAO = new LeaveRequestDAO();
    }
    
    public int getPendingLeaveRequestsCount() {
        return leaveRequestDAO.getPendingLeaveRequestsCount();
    }
    
    public Map<String, Integer> getLeaveRequestsByType() {
        return leaveRequestDAO.getLeaveRequestsByType();
    }
    
    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestDAO.getPendingLeaveRequests();
    }
    
    public boolean approveLeaveRequest(int requestId) {
        return leaveRequestDAO.approveLeaveRequest(requestId);
    }
    
    public boolean rejectLeaveRequest(int requestId) {
        return leaveRequestDAO.rejectLeaveRequest(requestId);
    }
      // tạo mới yêu cầu nghỉ
    public boolean createLeaveRequest(LeaveRequest lr) {
        return leaveRequestDAO.insertLeaveRequest(lr);
    }

    // lấy yêu cầu nghỉ theo nhân viên
    public List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId) {
        return leaveRequestDAO.getLeaveRequestsByEmployee(employeeId);
    }

    // lấy yêu cầu nghỉ theo ID
    public LeaveRequest getLeaveRequestById(int requestId) {
        return leaveRequestDAO.findById(requestId);
    }

} 