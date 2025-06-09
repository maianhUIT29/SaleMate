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
// 1. Lấy danh sách phân trang & tìm kiếm (Oracle ROWNUM)
public List<Employee> getEmployee(int page, int pageSize, String search) {
    List<Employee> list = new ArrayList<>();
    int offset = (page - 1) * pageSize;
    String base =
        "SELECT * FROM ( " +
        "  SELECT a.*, ROWNUM rnum FROM ( " +
        "    SELECT * FROM EMPLOYEE " +
        (search != null && !search.isEmpty()
            ? " WHERE UPPER(FIRST_NAME) LIKE ? OR UPPER(LAST_NAME) LIKE ? OR UPPER(ROLE) LIKE ?"
            : "") +
        "    ORDER BY EMPLOYEE_ID" +
        "  ) a WHERE ROWNUM <= ? " +
        ") WHERE rnum > ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(base)) {
        int idx = 1;
        if (search != null && !search.isEmpty()) {
            String kw = "%" + search.toUpperCase() + "%";
            stmt.setString(idx++, kw);
            stmt.setString(idx++, kw);
            stmt.setString(idx++, kw);
        }
        stmt.setInt(idx++, offset + pageSize);
        stmt.setInt(idx, offset);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Employee(
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
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

// 2. Đếm tổng nhân viên (có tìm kiếm)
public int countEmployee(String search) {
    String sql = "SELECT COUNT(*) FROM EMPLOYEE" +
                 (search != null && !search.isEmpty()
                     ? " WHERE UPPER(FIRST_NAME) LIKE ? OR UPPER(LAST_NAME) LIKE ? OR UPPER(ROLE) LIKE ?"
                     : "");
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        if (search != null && !search.isEmpty()) {
            String kw = "%" + search.toUpperCase() + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
            stmt.setString(3, kw);
        }
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}


// 4. Thêm nhân viên mới (Oracle sequence)
public boolean addEmployee(Employee e) {
    String sql = "INSERT INTO EMPLOYEE " +
                 "(EMPLOYEE_ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, HIRE_DATE, PHONE, ADDRESS, EMERGENCY_CONTACT, EMERGENCY_PHONE, ROLE) " +
                 "VALUES (EMPLOYEE_SEQ.NEXTVAL, ?,?,?,?,?,?,?,?,?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, e.getFirstName());
        stmt.setString(2, e.getLastName());
        stmt.setDate(3, new java.sql.Date(e.getBirthDate().getTime()));
        stmt.setDate(4, new java.sql.Date(e.getHireDate().getTime()));
        stmt.setString(5, e.getPhone());
        stmt.setString(6, e.getAddress());
        stmt.setString(7, e.getEmergencyContact());
        stmt.setString(8, e.getEmergencyPhone());
        stmt.setString(9, e.getRole());
        return stmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}

// 5. Xóa nhân viên
public boolean deleteEmployee(int employeeId) {
    String sql = "DELETE FROM EMPLOYEE WHERE EMPLOYEE_ID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, employeeId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}

// 6. Lấy employee theo user_id (thông qua users table)
public Employee getEmployeeByUserId(int userId) {
    String sql = "SELECT e.* FROM EMPLOYEE e " +
                 "JOIN USERS u ON e.EMPLOYEE_ID = u.EMPLOYEE_ID " +
                 "WHERE u.USERS_ID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
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


}
