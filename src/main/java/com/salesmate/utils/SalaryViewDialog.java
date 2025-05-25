package com.salesmate.utils;

import com.salesmate.component.Toast;
import com.salesmate.configs.DBConnection;
import com.salesmate.utils.DebugLogger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalaryViewDialog extends JDialog {
    private static final int LOW_STOCK_THRESHOLD = 10; // Ngưỡng số lượng thấp
    private JFrame parent;
    private Toast toast;
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private int currentUserId;
    private String userRole;
    private NumberFormat currencyFormatter;

    public SalaryViewDialog(JFrame parent, int currentUserId, String userRole) {
        super(parent, "Salary Information", true);
        this.parent = parent;
        this.currentUserId = currentUserId;
        this.userRole = userRole;
        this.toast = new Toast(parent);
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        initComponents();
        loadSalaryData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);
        setLocationRelativeTo(parent);

        // Table to display salary information
        String[] columns = {"Period", "Basic Salary", "Bonus", "Commission", "Other", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        salaryTable = new JTable(tableModel);
        salaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        salaryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(231, 76, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Status label
        JLabel statusLabel = new JLabel("Salary Information for " + ("Warehouse".equals(userRole) ? "Current User" : "Warehouse Staff"));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);
    }

    private void loadSalaryData() {
        List<Object[]> salaryData = fetchSalaryData();
        if (salaryData.isEmpty()) {
            toast.showMessage("No salary data available.");
            dispose(); // Close dialog if no data
            return;
        }

        // Populate table
        for (Object[] row : salaryData) {
            tableModel.addRow(row);
        }
    }

    private List<Object[]> fetchSalaryData() {
        List<Object[]> salaryList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        String query;

        // Query for Manager (all warehouse staff) or Warehouse (current user only)
        if ("Manager".equals(userRole)) {
            query = "SELECT s.salary_id, s.period_start, s.period_end, s.total_amount, " +
                    "e.first_name, e.last_name, sd.salary_type, sd.amount " +
                    "FROM SALARY s " +
                    "JOIN EMPLOYEE e ON s.employee_id = e.employee_id " +
                    "JOIN USERS u ON u.employee_id = e.employee_id " +
                    "LEFT JOIN SALARY_DETAIL sd ON s.salary_id = sd.salary_id " +
                    "WHERE u.role = 'Warehouse' " +
                    "ORDER BY s.period_start DESC";
        } else {
            query = "SELECT s.salary_id, s.period_start, s.period_end, s.total_amount, " +
                    "e.first_name, e.last_name, sd.salary_type, sd.amount " +
                    "FROM SALARY s " +
                    "JOIN EMPLOYEE e ON s.employee_id = e.employee_id " +
                    "JOIN USERS u ON u.employee_id = e.employee_id " +
                    "LEFT JOIN SALARY_DETAIL sd ON s.salary_id = sd.salary_id " +
                    "WHERE u.users_id = ? " +
                    "ORDER BY s.period_start DESC";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if ("Warehouse".equals(userRole)) {
                pstmt.setInt(1, currentUserId);
            }

            ResultSet rs = pstmt.executeQuery();
            Map<Integer, Map<String, Object>> salaryMap = new HashMap<>();

            while (rs.next()) {
                int salaryId = rs.getInt("salary_id");
                salaryMap.computeIfAbsent(salaryId, k -> new HashMap<>());

                Map<String, Object> salaryInfo = salaryMap.get(salaryId);
                salaryInfo.putIfAbsent("period_start", rs.getDate("period_start"));
                salaryInfo.putIfAbsent("period_end", rs.getDate("period_end"));
                salaryInfo.putIfAbsent("total_amount", rs.getBigDecimal("total_amount"));
                salaryInfo.putIfAbsent("employee_name", rs.getString("first_name") + " " + rs.getString("last_name"));

                String salaryType = rs.getString("salary_type");
                if (salaryType != null) {
                    salaryInfo.put(salaryType.toLowerCase(), rs.getBigDecimal("amount"));
                }
            }

            // Format data for table
            for (Map<String, Object> salaryInfo : salaryMap.values()) {
                String period = dateFormat.format(salaryInfo.get("period_start")) + " - " +
                                dateFormat.format(salaryInfo.get("period_end"));
                String basic = currencyFormatter.format(salaryInfo.getOrDefault("basic", 0.0));
                String bonus = currencyFormatter.format(salaryInfo.getOrDefault("bonus", 0.0));
                String commission = currencyFormatter.format(salaryInfo.getOrDefault("commission", 0.0));
                String other = currencyFormatter.format(salaryInfo.getOrDefault("other", 0.0));
                String total = currencyFormatter.format(salaryInfo.get("total_amount"));

                Object[] row = new Object[] {
                    period,
                    basic,
                    bonus,
                    commission,
                    other,
                    total
                };
                salaryList.add(row);
            }
        } catch (SQLException e) {
            DebugLogger.logError("Failed to fetch salary data: " + e.getMessage());
            toast.showMessage("Error fetching salary data.", Toast.ERROR);
        }
        return salaryList;
    }

    public static void showSalaryDialog(JFrame parent, int currentUserId, String userRole) {
        if (!"Manager".equals(userRole) && !"Warehouse".equals(userRole)) {
            return; // Only show for Manager or Warehouse roles
        }
        SalaryViewDialog dialog = new SalaryViewDialog(parent, currentUserId, userRole);
        dialog.setVisible(true);
    }
}