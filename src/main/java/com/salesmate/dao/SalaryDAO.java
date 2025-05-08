package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Salary;
import com.salesmate.model.ChartDataModel;

public class SalaryDAO {

    // Method to retrieve all salaries
    public List<Salary> getAllSalaries() {
        List<Salary> salaries = new ArrayList<>();
        String query = "SELECT * FROM salary";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Salary salary = new Salary();
                salary.setSalaryId(resultSet.getInt("salary_id"));
                salary.setUserId(resultSet.getInt("users_id")); 
                salary.setSalaryAmount(resultSet.getDouble("salary_amount"));
                salary.setPaymentDate(resultSet.getDate("payment_date"));
                salaries.add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salaries;
    }

    // Method to add a new salary
    public boolean addSalary(Salary salary) {
        String query = "INSERT INTO salary (users_id, salary_amount, payment_date) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, salary.getUserId());
            statement.setDouble(2, salary.getSalaryAmount());
            statement.setDate(3, new java.sql.Date(salary.getPaymentDate().getTime()));

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Method to delete a salary by ID
    public boolean deleteSalary(int id) {
        String query = "DELETE FROM salary WHERE salary_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<ChartDataModel> getMonthlySalaryData() {
        List<ChartDataModel> data = new ArrayList<>();
        String sql = "SELECT TO_CHAR(month_year, 'YYYY-MM') as month, SUM(amount) as total " +
                    "FROM SALARY GROUP BY month_year ORDER BY month_year";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new ChartDataModel(rs.getString("month"), rs.getBigDecimal("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public BigDecimal getTotalSalaryForCurrentMonth() {
        String sql = "SELECT SUM(amount) FROM SALARY " +
                    "WHERE TO_CHAR(month_year, 'YYYY-MM') = TO_CHAR(SYSDATE, 'YYYY-MM')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public List<Salary> getPendingSalaries() {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT * FROM SALARY WHERE status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Salary salary = new Salary();
                salary.setSalaryId(rs.getInt("salary_id"));
                salary.setEmployeeId(rs.getInt("employee_id"));
                salary.setMonthYear(rs.getDate("month_year"));
                salary.setAmount(rs.getBigDecimal("amount"));
                salary.setStatus(rs.getString("status"));
                salaries.add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }
    
    public boolean processSalary(int salaryId) {
        String sql = "UPDATE SALARY SET status = 'PROCESSED' WHERE salary_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean paySalary(int salaryId) {
        String sql = "UPDATE SALARY SET status = 'PAID' WHERE salary_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}