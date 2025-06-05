package com.salesmate.controller;

import com.salesmate.dao.ShiftDAO;
import com.salesmate.model.Shift;

import java.util.List;

public class ShiftController {

    private final ShiftDAO shiftDAO;

    public ShiftController() {
        this.shiftDAO = new ShiftDAO();
    }

    // Lấy tất cả ca (bao gồm inactive)
    public List<Shift> getAllShifts() {
        return shiftDAO.findAll();
    }

    // Lấy ca đang active
    public List<Shift> getActiveShifts() {
        return shiftDAO.findAllActive();
    }

    // Lấy ca theo ID
    public Shift getShiftById(int shiftId) {
        return shiftDAO.findById(shiftId);
    }

    // Tạo ca mới
    public boolean createShift(Shift shift) {
        return shiftDAO.insert(shift);
    }

    // Cập nhật ca
    public boolean updateShift(Shift shift) {
        return shiftDAO.update(shift);
    }

    // Vô hiệu hóa ca
    public boolean disableShift(int shiftId) {
        return shiftDAO.disable(shiftId);
    }
}
