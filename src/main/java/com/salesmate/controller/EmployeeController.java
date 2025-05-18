package com.salesmate.controller;

import com.salesmate.dao.EmployeeDAO;
import com.salesmate.model.Employee;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    
  
    public Date getEmployeeHireDate(int employeeId) {
        return employeeDAO.getEmployeeHireDate(employeeId);
    }
    
    public String getEmployeePhone(int employeeId) {
        return employeeDAO.getEmployeePhone(employeeId);
    }
    
    public String getEmployeeAddress(int employeeId) {
        return employeeDAO.getEmployeeAddress(employeeId);
    }
   

    /**
     * Get paginated list of employees with optional search keyword.
     */
    public List<Employee> getEmployee(int page, int pageSize, String search) {
        return employeeDAO.getEmployee(page, pageSize, search);
    }

    /**
     * Count total employees matching the search keyword.
     */
    public int countEmployee(String search) {
        return employeeDAO.countEmployee(search);
    }

    /**
     * Retrieve all employees without pagination.
     */
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

   
    /**
     * Add a new employee.
     */
    public boolean addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }

    /**
     * Update an existing employee.
     */
    public boolean updateEmployee(Employee employee) {
        return employeeDAO.updateEmployee(employee);
    }

    /**
     * Delete an employee by ID.
     */
    public boolean deleteEmployee(int employeeId) {
        return employeeDAO.deleteEmployee(employeeId);
    }

}