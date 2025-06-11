package com.salesmate.controller;

import java.util.List;

import com.salesmate.dao.EmployeeDAO;
import com.salesmate.model.Employee;

public class EmployeeController {
    private final EmployeeDAO employeeDAO;

    public EmployeeController() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }

    public Employee getEmployeeByUserId(int userId) {
        return employeeDAO.getEmployeeByUserId(userId);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeDAO.updateEmployee(employee);
    }

    public boolean addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }

    public boolean deleteEmployee(int employeeId) {
        return employeeDAO.deleteEmployee(employeeId);
    }

    public List<Employee> getEmployee(int page, int pageSize, String search) {
        return employeeDAO.getEmployee(page, pageSize, search);
    }

    public int countEmployee(String search) {
        return employeeDAO.countEmployee(search);
    }
}