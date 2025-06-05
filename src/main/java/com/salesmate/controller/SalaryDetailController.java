package com.salesmate.controller;

import com.salesmate.dao.SalaryDetailDAO;
import com.salesmate.model.SalaryDetail;

import java.util.List;

public class SalaryDetailController {
    private final SalaryDetailDAO salaryDetailDAO;

    public SalaryDetailController() {
        this.salaryDetailDAO = new SalaryDetailDAO();
    }

    /**
     * Trả về toàn bộ danh sách SalaryDetail từ cơ sở dữ liệu.
     */
    public List<SalaryDetail> getAllDetails() {
        return salaryDetailDAO.getAllDetails();
    }

    /**
     * Trả về một SalaryDetail theo ID chi tiết lương.
     */
    public SalaryDetail getDetailById(int salaryDetailId) {
        return salaryDetailDAO.getDetailById(salaryDetailId);
    }

    /**
     * Thêm một chi tiết lương mới vào cơ sở dữ liệu.
     */
    public boolean addDetail(SalaryDetail detail) {
        return salaryDetailDAO.addDetail(detail);
    }

    /**
     * Cập nhật một chi tiết lương đã có.
     */
    public boolean updateDetail(SalaryDetail detail) {
        return salaryDetailDAO.updateDetail(detail);
    }

    /**
     * Xoá cứng một chi tiết lương khỏi cơ sở dữ liệu.
     */
    public boolean deleteDetail(int salaryDetailId) {
        return salaryDetailDAO.deleteDetail(salaryDetailId);
    }

    /**
     * Trả về danh sách các chi tiết lương theo salary_id.
     */
    public List<SalaryDetail> getDetailsBySalaryId(int salaryId) {
        return salaryDetailDAO.getDetailsBySalaryId(salaryId);
    }
}
