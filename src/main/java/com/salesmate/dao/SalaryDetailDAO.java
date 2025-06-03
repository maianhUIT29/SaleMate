package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.SalaryDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class SalaryDetailDAO {

    public List<SalaryDetail> getAllDetails() {
        List<SalaryDetail> list = new ArrayList<>();
        String sql = "SELECT salary_detail_id, salary_id, amount, calculation_base, note, component_name, component_type, calculation_type, value, description, is_taxable FROM salary_detail";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SalaryDetail d = new SalaryDetail();
                d.setSalaryDetailId(rs.getInt("salary_detail_id"));
                d.setSalaryId(rs.getInt("salary_id"));
                d.setAmount(rs.getBigDecimal("amount"));
                d.setCalculationBase(rs.getBigDecimal("calculation_base"));
                d.setNote(rs.getString("note"));
                d.setComponentName(rs.getString("component_name"));
                d.setComponentType(rs.getString("component_type"));
                d.setCalculationType(rs.getString("calculation_type"));
                d.setValue(rs.getBigDecimal("value"));
                d.setDescription(rs.getString("description"));
                d.setIsTaxable(rs.getInt("is_taxable"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public SalaryDetail getDetailById(int salaryDetailId) {
        String sql = "SELECT salary_detail_id, salary_id, amount, calculation_base, note, component_name, component_type, calculation_type, value, description, is_taxable FROM salary_detail WHERE salary_detail_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryDetailId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalaryDetail d = new SalaryDetail();
                    d.setSalaryDetailId(rs.getInt("salary_detail_id"));
                    d.setSalaryId(rs.getInt("salary_id"));
                    d.setAmount(rs.getBigDecimal("amount"));
                    d.setCalculationBase(rs.getBigDecimal("calculation_base"));
                    d.setNote(rs.getString("note"));
                    d.setComponentName(rs.getString("component_name"));
                    d.setComponentType(rs.getString("component_type"));
                    d.setCalculationType(rs.getString("calculation_type"));
                    d.setValue(rs.getBigDecimal("value"));
                    d.setDescription(rs.getString("description"));
                    d.setIsTaxable(rs.getInt("is_taxable"));
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDetail(SalaryDetail detail) {
        String sql = "INSERT INTO salary_detail (salary_id, amount, calculation_base, note, component_name, component_type, calculation_type, value, description, is_taxable) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getSalaryId());
            stmt.setBigDecimal(2, detail.getAmount());
            stmt.setBigDecimal(3, detail.getCalculationBase());
            stmt.setString(4, detail.getNote());
            stmt.setString(5, detail.getComponentName());
            stmt.setString(6, detail.getComponentType());
            stmt.setString(7, detail.getCalculationType());
            stmt.setBigDecimal(8, detail.getValue());
            stmt.setString(9, detail.getDescription());
            stmt.setInt(10, detail.getIsTaxable());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDetail(SalaryDetail detail) {
        String sql = "UPDATE salary_detail SET salary_id = ?, amount = ?, calculation_base = ?, note = ?, component_name = ?, component_type = ?, calculation_type = ?, value = ?, description = ?, is_taxable = ? "
                   + "WHERE salary_detail_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getSalaryId());
            stmt.setBigDecimal(2, detail.getAmount());
            stmt.setBigDecimal(3, detail.getCalculationBase());
            stmt.setString(4, detail.getNote());
            stmt.setString(5, detail.getComponentName());
            stmt.setString(6, detail.getComponentType());
            stmt.setString(7, detail.getCalculationType());
            stmt.setBigDecimal(8, detail.getValue());
            stmt.setString(9, detail.getDescription());
            stmt.setInt(10, detail.getIsTaxable());
            stmt.setInt(11, detail.getSalaryDetailId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDetail(int salaryDetailId) {
        String sql = "DELETE FROM salary_detail WHERE salary_detail_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryDetailId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SalaryDetail> getDetailsBySalaryId(int salaryId) {
        List<SalaryDetail> list = new ArrayList<>();
        String sql = "SELECT salary_detail_id, salary_id, amount, calculation_base, note, component_name, component_type, calculation_type, value, description, is_taxable FROM salary_detail WHERE salary_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalaryDetail d = new SalaryDetail();
                    d.setSalaryDetailId(rs.getInt("salary_detail_id"));
                    d.setSalaryId(rs.getInt("salary_id"));
                    d.setAmount(rs.getBigDecimal("amount"));
                    d.setCalculationBase(rs.getBigDecimal("calculation_base"));
                    d.setNote(rs.getString("note"));
                    d.setComponentName(rs.getString("component_name"));
                    d.setComponentType(rs.getString("component_type"));
                    d.setCalculationType(rs.getString("calculation_type"));
                    d.setValue(rs.getBigDecimal("value"));
                    d.setDescription(rs.getString("description"));
                    d.setIsTaxable(rs.getInt("is_taxable"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
