package com.salesmate.controller;

import com.salesmate.dao.EmployeeDAO;
import com.salesmate.model.Employee;
import java.math.BigDecimal;
import java.util.Date;

public class EmployeeController {
    private final EmployeeDAO employeeDAO;
    
    public EmployeeController() {
        employeeDAO = new EmployeeDAO();
    }

    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }
    
    public String getEmployeePosition(int employeeId) {
        return employeeDAO.getEmployeePosition(employeeId);
    }
    
    public String getEmployeeDepartment(int employeeId) {
        return employeeDAO.getEmployeeDepartment(employeeId);
    }
    
    public BigDecimal getEmployeeBaseSalary(int employeeId) {
        return employeeDAO.getEmployeeBaseSalary(employeeId);
    }
    
    public Date getEmployeeHireDate(int employeeId) {
        return employeeDAO.getEmployeeHireDate(employeeId);
    }
    
    public String getEmployeePhone(int employeeId) {
        return employeeDAO.getEmployeePhone(employeeId);
    }
    
    public String getEmployeeAddress(int employeeId) {
        return employeeDAO.getEmployeeAddress(employeeId);
    }
}