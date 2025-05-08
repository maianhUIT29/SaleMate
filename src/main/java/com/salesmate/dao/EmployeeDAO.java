package com.salesmate.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Employee;

public class EmployeeDAO {

    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM EMPLOYEE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("EMPLOYEE_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getDate("BIRTH_DATE"),
                    rs.getDate("HIRE_DATE"),
                    rs.getString("PHONE"),
                    rs.getString("ADDRESS"),
                    rs.getString("EMERGENCY_CONTACT"),
                    rs.getString("EMERGENCY_PHONE"),
                    rs.getString("ROLE")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
    
 
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Employee(
                    rs.getInt("EMPLOYEE_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getDate("BIRTH_DATE"),
                    rs.getDate("HIRE_DATE"),
                    rs.getString("PHONE"),
                    rs.getString("ADDRESS"),
                    rs.getString("EMERGENCY_CONTACT"),
                    rs.getString("EMERGENCY_PHONE"),
                    rs.getString("ROLE")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE EMPLOYEE SET FIRST_NAME = ?, LAST_NAME = ?, BIRTH_DATE = ?, HIRE_DATE = ?, " +
                     "PHONE = ?, ADDRESS = ?, EMERGENCY_CONTACT = ?, EMERGENCY_PHONE = ?, ROLE = ? " +
                     "WHERE EMPLOYEE_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setDate(3, new java.sql.Date(employee.getBirthDate().getTime()));
            stmt.setDate(4, new java.sql.Date(employee.getHireDate().getTime()));
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getAddress());
            stmt.setString(7, employee.getEmergencyContact());
            stmt.setString(8, employee.getEmergencyPhone());
            stmt.setString(9, employee.getRole());
            stmt.setInt(10, employee.getEmployeeId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getEmployeePosition(int employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return employee != null ? employee.getRole() : null;
    }

    public String getEmployeeDepartment(int employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return employee != null ? employee.getRole() : null;
    }

    public BigDecimal getEmployeeBaseSalary(int employeeId) {
        String sql = "SELECT BASE_SALARY FROM EMPLOYEE WHERE EMPLOYEE_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("BASE_SALARY");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getEmployeeHireDate(int employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return employee != null ? employee.getHireDate() : null;
    }

    public String getEmployeePhone(int employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return employee != null ? employee.getPhone() : null;
    }

    public String getEmployeeAddress(int employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return employee != null ? employee.getAddress() : null;
    }

}
