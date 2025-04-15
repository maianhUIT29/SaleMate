package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Salary;

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
}