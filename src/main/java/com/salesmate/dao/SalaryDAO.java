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

    /**
     * Lấy tất cả Salary (không bao gồm những bản ghi đã bị xóa mềm, tức status = 'Cancelled').
     */
    public List<Salary> getAllSalaries() {
        List<Salary> salaries = new ArrayList<>();
        String query = ""
            + "SELECT salary_id, employee_id, basic_salary, payment_period, payment_date, status, total_salary, note "
            + "FROM salary "
            + "WHERE status <> 'Cancelled'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Salary s = new Salary();
                s.setSalaryId(rs.getInt("salary_id"));
                s.setEmployeeId(rs.getInt("employee_id"));
                s.setBasicSalary(rs.getBigDecimal("basic_salary"));
                s.setPaymentPeriod(rs.getString("payment_period"));
                s.setPaymentDate(rs.getDate("payment_date"));
                s.setStatus(rs.getString("status"));
                s.setTotalSalary(rs.getBigDecimal("total_salary"));
                s.setNote(rs.getString("note"));
                salaries.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }

    /**
     * Thêm mới Salary (với trạng thái mặc định do model gán sẵn, thường là 'Pending').
     */
    public boolean addSalary(Salary salary) {
        String sql = ""
            + "INSERT INTO salary "
            + "  (employee_id, basic_salary, payment_period, payment_date, status, total_salary, note) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setBigDecimal(2, salary.getBasicSalary());
            stmt.setString(3, salary.getPaymentPeriod());
            stmt.setDate(4, new java.sql.Date(salary.getPaymentDate().getTime()));
            stmt.setString(5, salary.getStatus());       // ví dụ 'Pending'
            stmt.setBigDecimal(6, salary.getTotalSalary());
            stmt.setString(7, salary.getNote());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa mềm: chỉ cập nhật status = 'Cancelled', không xóa bản ghi vật lý.
     */
    public boolean deleteSalary(int id) {
        String sql = "UPDATE salary SET status = 'Cancelled' WHERE salary_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy số liệu biểu đồ hàng tháng (giữ nguyên logic, không liên quan đến xóa mềm).
     */
    public List<ChartDataModel> getMonthlySalaryData() {
        List<ChartDataModel> data = new ArrayList<>();
        String sql = ""
            + "SELECT TO_CHAR(month_year, 'YYYY-MM') AS month, SUM(amount) AS total "
            + "FROM salary "
            + "WHERE status <> 'Cancelled' "                     // bỏ bản ghi đã bị hủy
            + "GROUP BY month_year "
            + "ORDER BY month_year";

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

    /**
     * Tổng lương của tháng hiện tại (bỏ qua record đã được hủy).
     */
    public BigDecimal getTotalSalaryForCurrentMonth() {
        String sql = ""
            + "SELECT SUM(amount) "
            + "FROM salary "
            + "WHERE TO_CHAR(month_year, 'YYYY-MM') = TO_CHAR(SYSDATE, 'YYYY-MM') "
            + "  AND status <> 'Cancelled'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Lấy danh sách những Salary có status = 'PENDING' (không quan tâm tới 'Cancelled').
     */
    public List<Salary> getPendingSalaries() {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT * FROM salary WHERE status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Salary salary = new Salary();
                salary.setSalaryId(rs.getInt("salary_id"));
                salary.setEmployeeId(rs.getInt("employee_id"));
                salary.setBasicSalary(rs.getBigDecimal("basic_salary"));
                salary.setPaymentPeriod(rs.getString("payment_period"));
                salary.setPaymentDate(rs.getDate("payment_date"));
                salary.setStatus(rs.getString("status"));
                salary.setTotalSalary(rs.getBigDecimal("total_salary"));
                salary.setNote(rs.getString("note"));
                // Nếu bạn có các cột month_year/amount, set thêm
                salaries.add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }

    /** 
     * Process (chuyển status sang 'Processed') 
     */
    public boolean processSalary(int salaryId) {
        String sql = "UPDATE salary SET status = 'Processed' WHERE salary_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 
     * Pay (chuyển status sang 'Paid') 
     */
    public boolean paySalary(int salaryId) {
        String sql = "UPDATE salary SET status = 'Paid' WHERE salary_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy lương theo employee_id. (Không liên quan xóa mềm, nhưng sẽ chỉ tìm record chưa bị xóa nếu muốn)
     */
    public BigDecimal getSalaryByEmployeeId(int employeeId) {
        String query = "SELECT total_salary FROM salary WHERE employee_id = ? AND status <> 'Cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total_salary");
            } else {
                throw new RuntimeException("Không tìm thấy lương (hoặc đã bị hủy) cho nhân viên ID: " + employeeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn lương từ cơ sở dữ liệu.");
        }
    }

    /**
     * Lấy danh sách bản ghi lương (Object[]) kèm tên nhân viên, có phân trang + tìm kiếm,
     * và loại trừ những salary đã bị hủy (status = 'Cancelled').
     */
   /**
     * Lấy danh sách Salary kèm tên nhân viên, phân trang + tìm kiếm + lọc trạng thái.
     *
     * @param offset         số bản ghi bỏ qua
     * @param limit          số bản ghi lấy về
     * @param searchKeyword  từ khóa tìm theo tên nhân viên
     * @param statusFilter   giá trị status muốn lọc (Pending, Processed, Paid, Cancelled hoặc "All")
     */
    public List<Object[]> getSalariesWithEmployeeNameRaw(int offset, int limit, String searchKeyword, String statusFilter) {
        List<Object[]> list = new ArrayList<>();

        // Xây dựng câu SQL với phần điều kiện status nếu cần
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ")
          .append("  s.salary_id, ")
          .append("  s.employee_id, ")
          .append("  s.basic_salary, ")
          .append("  s.payment_period, ")
          .append("  s.payment_date, ")
          .append("  s.status, ")
          .append("  s.total_salary, ")
          .append("  s.note, ")
          .append("  e.first_name || ' ' || e.last_name AS employee_name ")
          .append("FROM salary s ")
          .append("JOIN employee e ON s.employee_id = e.employee_id ")
          .append("WHERE LOWER(e.first_name || ' ' || e.last_name) LIKE ? ");

        // Nếu statusFilter != "All", thêm điều kiện
        if (!"All".equalsIgnoreCase(statusFilter)) {
            sb.append("  AND s.status = ? ");
        }

        sb.append("ORDER BY s.salary_id ASC ")
          .append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sb.toString())) {

            int paramIndex = 1;
            stmt.setString(paramIndex++, "%" + searchKeyword.toLowerCase() + "%");

            if (!"All".equalsIgnoreCase(statusFilter)) {
                stmt.setString(paramIndex++, statusFilter);
            }

            stmt.setInt(paramIndex++, offset);
            stmt.setInt(paramIndex, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getInt("salary_id");
                row[1] = rs.getInt("employee_id");
                row[2] = rs.getBigDecimal("basic_salary");
                row[3] = rs.getString("payment_period");
                row[4] = rs.getDate("payment_date");
                row[5] = rs.getString("status");
                row[6] = rs.getBigDecimal("total_salary");
                row[7] = rs.getString("note");
                row[8] = rs.getString("employee_name");
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số Salary thoả điều kiện tìm kiếm + lọc trạng thái.
     */
    public int countSalariesWithEmployeeNameRaw(String searchKeyword, String statusFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) ")
          .append("FROM salary s ")
          .append("JOIN employee e ON s.employee_id = e.employee_id ")
          .append("WHERE LOWER(e.first_name || ' ' || e.last_name) LIKE ? ");

        if (!"All".equalsIgnoreCase(statusFilter)) {
            sb.append("  AND s.status = ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sb.toString())) {

            int paramIndex = 1;
            stmt.setString(paramIndex++, "%" + searchKeyword.toLowerCase() + "%");

            if (!"All".equalsIgnoreCase(statusFilter)) {
                stmt.setString(paramIndex, statusFilter);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
