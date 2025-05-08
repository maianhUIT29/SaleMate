package com.salesmate.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.salesmate.configs.DBConnection;
import com.salesmate.controller.DetailController;
import com.salesmate.model.ChartDataModel;
import com.salesmate.model.Detail;
import com.salesmate.model.Invoice;

public class InvoiceDAO {

    private DetailDAO detailDAO = new DetailDAO();
    private DetailController detailController = new DetailController();
    private ProductDAO productDAO = new ProductDAO();

    // Tạo invoice mới
    public boolean createInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoice (users_id, total_amount, payment_status) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getUsersId());
            pstmt.setBigDecimal(2, invoice.getTotal());
            pstmt.setString(3, invoice.getPaymentStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả hóa đơn
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (Connection connection = DBConnection.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    // Read invoice by ID
    public Invoice getInvoiceById(int id) {
        String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status")); // Fixed: payment_status instead of status
                return invoice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoice SET users_id = ?, total_amount = ?, payment_status = ? WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getUsersId());
            pstmt.setBigDecimal(2, invoice.getTotal());
            pstmt.setString(3, invoice.getPaymentStatus());
            pstmt.setInt(4, invoice.getInvoiceId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete
    public boolean deleteInvoice(int id) {
        String sql = "DELETE FROM invoice WHERE invoice_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get invoices by user ID
    public List<Invoice> getInvoicesByUserId(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE users_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    // Get invoices from the last 7 days
    public List<Invoice> getInvoicesLast7Days() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE created_at >= CURRENT_DATE - 7";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setUsersId(rs.getInt("users_id"));
                invoice.setTotal(rs.getBigDecimal("total_amount"));
                invoice.setCreatedAt(rs.getDate("created_at"));
                invoice.setPaymentStatus(rs.getString("payment_status"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    /* Gets invoices from the last N days */
     
    public List<Invoice> getInvoicesLastNDays(int days) {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT invoice_id, users_id, total_amount, created_at FROM invoice " +
                       "WHERE created_at >= SYSDATE - ? " +
                       "ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, days);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("invoice_id"));
                    invoice.setUsersId(rs.getInt("users_id"));
                    invoice.setTotal(rs.getBigDecimal("total_amount"));
                    invoice.setCreatedAt(rs.getTimestamp("created_at"));
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching invoices from last " + days + " days: " + e.getMessage());
            e.printStackTrace();
        }
        
        return invoices;
    }

    public int saveInvoice(Invoice invoice) {
        // Validate payment status
        if (invoice.getPaymentStatus() == null
                || (!invoice.getPaymentStatus().equals("Paid") && !invoice.getPaymentStatus().equals("Unpaid"))) {
            throw new IllegalArgumentException("Payment status must be either 'Paid' or 'Unpaid'");
        }

        // Validate total amount
        if (invoice.getTotal() == null || invoice.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }

        String insertSql = "INSERT INTO invoice (users_id, total_amount, created_at, payment_status) "
                + "VALUES (?, ?, ?, ?)";

        // Sau đó lấy ID vừa được tạo
        String getIdSql = "SELECT invoice_seq.CURRVAL FROM dual";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement insertStmt = connection.prepareStatement(insertSql); PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)) {

            // Set các tham số cho câu insert
            insertStmt.setInt(1, invoice.getUsersId());
            insertStmt.setBigDecimal(2, invoice.getTotal());
            insertStmt.setDate(3, new java.sql.Date(invoice.getCreatedAt().getTime()));
            insertStmt.setString(4, invoice.getPaymentStatus());

            // Thực hiện insert
            insertStmt.executeUpdate();

            // Lấy ID vừa được tạo
            ResultSet rs = getIdStmt.executeQuery();
            if (rs.next()) {
                int newId = rs.getInt(1);
                invoice.setInvoiceId(newId);
                System.out.println("Invoice saved successfully with ID: " + newId);
                return newId;
            }

            throw new SQLException("Failed to get generated ID");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save invoice: " + e.getMessage());
        }
    }

    // Đếm số lượng hóa đơn
    public int countInvoices() {
        String sql = "SELECT COUNT(*) FROM invoice";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Trả về số lượng hóa đơn
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Nếu có lỗi hoặc không tìm thấy dữ liệu, trả về 0
    }

    public List<ChartDataModel> getDailyRevenue() {
        String sql = 
            "SELECT TRUNC(created_at) AS date, " +
            "       NVL(SUM(total_amount), 0) AS total_revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND created_at >= TRUNC(SYSDATE, 'MM') " +
            "  AND created_at < ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1) " +
            "GROUP BY TRUNC(created_at) " +
            "ORDER BY date";

        List<ChartDataModel> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date date = rs.getDate("date");
                BigDecimal revenue = rs.getBigDecimal("total_revenue");
                list.add(new ChartDataModel(date, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* Tổng doanh thu tháng hiện tại.z */
    public BigDecimal getRevenueForCurrentMonth() {
        String sql =
            "SELECT NVL(SUM(total_amount),0) AS total_revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND created_at >= TRUNC(SYSDATE, 'MM') " +
            "  AND created_at <  ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }


    /*Tính doanh thu theo tháng*/
    public BigDecimal getRevenueForMonth(int month) {
        String sql =
            "SELECT NVL(SUM(total_amount),0) AS total_revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND EXTRACT(MONTH FROM created_at) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /*Tính doanh thu theo năm*/
    public BigDecimal getRevenueForYear(int year) {
        String sql =
            "SELECT NVL(SUM(total_amount),0) AS total_revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND EXTRACT(YEAR FROM created_at) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /*Tính doanh thu theo quý*/
    public BigDecimal getRevenueForQuarter(int quarter) {
        String sql =
            "SELECT NVL(SUM(total_amount),0) AS total_revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND EXTRACT(QUARTER FROM created_at) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quarter);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /*Tính doanh thu theo tuần*/
    public BigDecimal getRevenueForWeek(int week) {
        String sql =
            "SELECT NVL(SUM(total_amount),0) AS total_revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND EXTRACT(WEEK FROM created_at) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, week);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets today's total revenue
     */
    public BigDecimal getTodayRevenue() {
        try {
            String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM invoice WHERE TRUNC(created_at) = TRUNC(SYSDATE) AND payment_status = 'Paid'";
            Object result = getSingleResult(sql);
            return result != null ? (BigDecimal) result : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }


    // Helper method to get single result
    private Object getSingleResult(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to get result list
    private List<Object[]> getResultList(String sql) {
        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get revenue by year
    public List<ChartDataModel> getYearlyRevenue() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT EXTRACT(YEAR FROM created_at) as year, " +
            "       NVL(SUM(total_amount), 0) AS revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "GROUP BY EXTRACT(YEAR FROM created_at) " +
            "ORDER BY year";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String year = String.valueOf(rs.getInt("year"));
                BigDecimal revenue = rs.getBigDecimal("revenue");
                result.add(new ChartDataModel(year, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get revenue by month for a specific year
    public List<ChartDataModel> getMonthlyRevenueByYear(int year) {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT EXTRACT(MONTH FROM created_at) as month, " +
            "       NVL(SUM(total_amount), 0) AS revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND EXTRACT(YEAR FROM created_at) = ? " +
            "GROUP BY EXTRACT(MONTH FROM created_at) " +
            "ORDER BY month";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String month = String.valueOf(rs.getInt("month"));
                BigDecimal revenue = rs.getBigDecimal("revenue");
                result.add(new ChartDataModel(month, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get revenue by week for current month
    public List<ChartDataModel> getWeeklyRevenueForCurrentMonth() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT TO_CHAR(created_at, 'IW') as week, " +
            "       NVL(SUM(total_amount), 0) AS revenue " +
            "FROM invoice " +
            "WHERE payment_status = 'Paid' " +
            "  AND created_at >= TRUNC(SYSDATE, 'MM') " +
            "  AND created_at < ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1) " +
            "GROUP BY TO_CHAR(created_at, 'IW') " +
            "ORDER BY week";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String week = "Tuần " + rs.getString("week");
                BigDecimal revenue = rs.getBigDecimal("revenue");
                result.add(new ChartDataModel(week, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Top customers by revenue
    public List<Map<String, Object>> getTopCustomersByRevenue(int topN) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT users_id, SUM(total_amount) AS total_revenue " +
                     "FROM invoice WHERE payment_status = 'Paid' " +
                     "GROUP BY users_id ORDER BY total_revenue DESC FETCH FIRST ? ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topN);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("users_id", rs.getInt("users_id"));
                row.put("total_revenue", rs.getBigDecimal("total_revenue"));
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Top invoices by value
    public List<Map<String, Object>> getTopInvoices(int n) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT i.invoice_id, u.username, i.total_amount, i.created_at FROM invoice i JOIN users u ON i.users_id = u.users_id WHERE i.payment_status = 'Paid' ORDER BY i.total_amount DESC FETCH FIRST ? ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("invoice_id", rs.getInt("invoice_id"));
                    map.put("username", rs.getString("username"));
                    map.put("total_amount", rs.getBigDecimal("total_amount"));
                    map.put("created_at", rs.getDate("created_at"));
                    result.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Invoice status ratio
    public List<ChartDataModel> getInvoiceStatusRatio() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT payment_status, COUNT(*) AS count FROM invoice GROUP BY payment_status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new ChartDataModel(rs.getString("payment_status"), BigDecimal.valueOf(rs.getInt("count"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get available years for filtering
    public List<Integer> getAvailableYears() {
        List<Integer> years = new ArrayList<>();
        String sql = 
            "SELECT DISTINCT EXTRACT(YEAR FROM created_at) as year " +
            "FROM invoice " +
            "ORDER BY year DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return years;
    }

    // Top employees by revenue (tổng doanh thu do nhân viên lập hóa đơn)
    public List<Map<String, Object>> getTopEmployeesByRevenue(int topN) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT u.users_id, u.username, SUM(i.total_amount) AS total_revenue " +
                     "FROM invoice i JOIN users u ON i.users_id = u.users_id " +
                     "WHERE i.payment_status = 'Paid' " +
                     "GROUP BY u.users_id, u.username " +
                     "ORDER BY total_revenue DESC FETCH FIRST ? ROWS ONLY";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topN);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("users_id", rs.getInt("users_id"));
                row.put("username", rs.getString("username"));
                row.put("total_revenue", rs.getBigDecimal("total_revenue"));
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Tổng doanh thu tất cả hóa đơn
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT NVL(SUM(total_amount),0) AS total_revenue FROM invoice WHERE payment_status = 'Paid'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // Doanh thu theo tuần cho cả năm
    public List<ChartDataModel> getWeeklyRevenueByYear(int year) {
        List<ChartDataModel> result = new ArrayList<>();
        // Lấy số tuần tối đa trong năm (ISO)
        int maxWeek = 53;
        // Truy vấn doanh thu từng tuần
        String sql = "SELECT TO_CHAR(created_at, 'IW') as week, NVL(SUM(total_amount), 0) AS revenue " +
                     "FROM invoice WHERE payment_status = 'Paid' AND EXTRACT(YEAR FROM created_at) = ? " +
                     "GROUP BY TO_CHAR(created_at, 'IW')";
        Map<Integer, BigDecimal> weekRevenue = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int week = Integer.parseInt(rs.getString("week"));
                BigDecimal revenue = rs.getBigDecimal("revenue");
                weekRevenue.put(week, revenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Đảm bảo đủ tất cả các tuần (1-53)
        for (int w = 1; w <= maxWeek; w++) {
            BigDecimal revenue = weekRevenue.getOrDefault(w, BigDecimal.ZERO);
            result.add(new ChartDataModel("Tuần " + w, revenue));
        }
        return result;
    }

    public List<Detail> getInvoiceDetails(int invoiceId) {
        return detailDAO.getDetailsByInvoiceId(invoiceId);
    }

}