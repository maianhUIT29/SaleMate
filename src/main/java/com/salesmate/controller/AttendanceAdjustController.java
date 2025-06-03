/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesmate.controller;

import com.salesmate.dao.AttendanceAdjustmentDAO;
import com.salesmate.model.AttendanceAdjustment;

import java.util.List;

/**
 *
 * @author meiln
 */
public class AttendanceAdjustController {
    private final AttendanceAdjustmentDAO dao;

    public AttendanceAdjustController() {
        this.dao = new AttendanceAdjustmentDAO();
    }

  
    public List<AttendanceAdjustment> getAdjustmentsByAttendanceId(int attendanceId) {
        return dao.findByAttendanceId(attendanceId);
    }

  
}
