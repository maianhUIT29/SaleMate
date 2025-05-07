package com.salesmate.controller;

import com.salesmate.dao.SalaryDAO;
import com.salesmate.model.Salary;
import com.salesmate.model.ChartDataModel;
import java.util.*;
import java.math.BigDecimal;

public class SalaryController {
    private final SalaryDAO salaryDAO;
    
    public SalaryController() {
        this.salaryDAO = new SalaryDAO();
    }
    
    public List<ChartDataModel> getMonthlySalaryData() {
        return salaryDAO.getMonthlySalaryData();
    }
    
    public BigDecimal getTotalSalaryForCurrentMonth() {
        return salaryDAO.getTotalSalaryForCurrentMonth();
    }
    
    public List<Salary> getPendingSalaries() {
        return salaryDAO.getPendingSalaries();
    }
    
    public boolean processSalary(int salaryId) {
        return salaryDAO.processSalary(salaryId);
    }
    
    public boolean paySalary(int salaryId) {
        return salaryDAO.paySalary(salaryId);
    }
} 