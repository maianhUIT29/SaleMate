package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.SalaryDetail;

public class SalaryDetailDAO {
    
    public List<SalaryDetail> getAllSalaryDetailsBySalaryId(int salaryId) {
        List<SalaryDetail> salaryDetails = new ArrayList<>();
        String sql = "SELECT * FROM salary_detail WHERE salary_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, salaryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SalaryDetail detail = new SalaryDetail();
                detail.setSalaryId(rs.getInt("salary_id"));
                detail.setEmployeeId(rs.getInt("employee_id"));
                detail.setBasicSalary(rs.getBigDecimal("basic_salary"));
                detail.setPaymentPeriod(rs.getString("payment_period"));
                detail.setPaymentDate(rs.getDate("payment_date"));
                detail.setStatus(rs.getString("status"));
                detail.setTotalSalary(rs.getBigDecimal("total_salary"));
                detail.setNote(rs.getString("note"));
                
                salaryDetails.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return salaryDetails;
    }
}
