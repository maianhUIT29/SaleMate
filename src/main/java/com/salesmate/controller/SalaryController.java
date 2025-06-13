package com.salesmate.controller;

import com.salesmate.dao.SalaryDAO;
import com.salesmate.model.Salary;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

public class SalaryController {
    private SalaryDAO salaryDAO;

    public SalaryController() {
        salaryDAO = new SalaryDAO();
    }

    // Lấy danh sách tất cả lương
    public List<Salary> getAllSalaries() {
        return salaryDAO.getAllSalaries();
    }

    // Thêm mới lương: nhận vào tất cả field cần thiết
    public boolean addSalary(int employeeId, BigDecimal basicSalary, String paymentPeriod, Date paymentDate,
                             BigDecimal totalSalary, String note) {
        Salary s = new Salary();
        s.setEmployeeId(employeeId);
        s.setBasicSalary(basicSalary);
        s.setPaymentPeriod(paymentPeriod);
        s.setPaymentDate(paymentDate);
        s.setStatus("Pending"); // mặc định
        s.setTotalSalary(totalSalary);
        s.setNote(note);
        return salaryDAO.addSalary(s);
    }

    // Xóa lương
    public boolean deleteSalary(int salaryId) {
        return salaryDAO.deleteSalary(salaryId);
    }

    // Process lương (chuyển trạng thái)
    public boolean processSalary(int salaryId) {
        return salaryDAO.processSalary(salaryId);
    }

    // Pay lương
    public boolean paySalary(int salaryId) {
        return salaryDAO.paySalary(salaryId);
    }
    /**
     * Lấy danh sách salaries kèm tên nhân viên, phân trang + tìm kiếm + lọc trạng thái.
     */
    public List<Object[]> getSalariesWithEmployeeNameRaw(int offset, int limit, String keyword, String statusFilter) {
        return salaryDAO.getSalariesWithEmployeeNameRaw(offset, limit, keyword, statusFilter);
    }

    /**
     * Đếm tổng bản ghi thoả điều kiện tìm kiếm + lọc trạng thái.
     */
    public int countSalariesWithEmployeeNameRaw(String keyword, String statusFilter) {
        return salaryDAO.countSalariesWithEmployeeNameRaw(keyword, statusFilter);
    }

    public Object[][] getSalaryInfo(int employeeId) {
        return salaryDAO.getSalaryInfo(employeeId);
    }
}
