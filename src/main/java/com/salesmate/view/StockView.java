package com.salesmate.view;

import com.salesmate.component.StockInventoryPanel;
import com.salesmate.component.AccountInfoPanel; // Import AccountInfoPanel

import javax.swing.*;
import java.awt.*;

public class StockView extends JFrame {
    private JPanel sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private int userId; // Thêm biến userId

    public StockView(int userId) {
        this.userId = userId;
        setTitle("Quản lý kho");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(41, 128, 185));
        sidebar.setPreferredSize(new Dimension(200, 700));

        JButton btnInventory = createSidebarButton("Kiểm tra tồn kho", new Color(52, 152, 219));
        JButton btnExportAccount = createSidebarButton("Xuất tài khoản", new Color(241, 196, 15));
        JButton btnExportImport = createSidebarButton("Xuất nhập kho", new Color(46, 204, 113));
        JButton btnOther = createSidebarButton("Khác", new Color(155, 89, 182));

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(btnInventory);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(btnExportAccount);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(btnExportImport);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(btnOther);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Thêm các panel chức năng
        contentPanel.add(new StockInventoryPanel(), "inventory");
        contentPanel.add(new AccountInfoPanel(userId), "exportAccount"); // Thêm panel vào contentPanel
        contentPanel.add(createExportImportPanel(userId), "exportImport");
        contentPanel.add(new JPanel(), "other");         // Placeholder

        btnInventory.addActionListener(e -> cardLayout.show(contentPanel, "inventory"));
        btnExportAccount.addActionListener(e -> cardLayout.show(contentPanel, "exportAccount")); // Sự kiện nút "Xuất tài khoản"
        btnExportImport.addActionListener(e -> cardLayout.show(contentPanel, "exportImport"));
        btnOther.addActionListener(e -> cardLayout.show(contentPanel, "other"));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createExportImportPanel(int userId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Bảng lương
        JTable salaryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(salaryTable);

        // Lấy dữ liệu lương
        com.salesmate.controller.SalaryController salaryController = new com.salesmate.controller.SalaryController();
        Object[][] salaryData = salaryController.getSalaryInfo(userId);
        String[] columnNames = {"Kỳ lương", "Ngày thanh toán", "Lương cơ bản", "Tổng lương", "Trạng thái", "Ghi chú"};

        salaryTable.setModel(new javax.swing.table.DefaultTableModel(salaryData, columnNames));
        salaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaryTable.setRowHeight(25);

        // Nút xuất báo cáo
        JButton btnExportExcel = new JButton("Xuất báo cáo lương (Excel)");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExportExcel.setBackground(new Color(46, 204, 113));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setOpaque(true);
        btnExportExcel.setContentAreaFilled(true);
        btnExportExcel.setBorderPainted(false);

        btnExportExcel.addActionListener(e -> exportSalaryReportToExcel(userId));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnExportExcel);

        panel.add(new JLabel("Bảng lương của bạn", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void exportSalaryReportToExcel(int employeeId) {
        try {
            com.salesmate.controller.SalaryController salaryController = new com.salesmate.controller.SalaryController();
            Object[][] salaryData = salaryController.getSalaryInfo(employeeId);
            String[] headers = {"Kỳ lương", "Ngày thanh toán", "Lương cơ bản", "Tổng lương", "Trạng thái", "Ghi chú"};

            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Salary Report");

            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (int i = 0; i < salaryData.length; i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                for (int j = 0; j < salaryData[i].length; j++) {
                    if (salaryData[i][j] instanceof String)
                        row.createCell(j).setCellValue((String) salaryData[i][j]);
                    else if (salaryData[i][j] instanceof Double)
                        row.createCell(j).setCellValue((Double) salaryData[i][j]);
                }
            }

            // Cho phép người dùng chọn nơi lưu file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu báo cáo lương");
            fileChooser.setSelectedFile(new java.io.File("SalaryReport.xlsx"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                    workbook.write(fos);
                }
                workbook.close();
                javax.swing.JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath());
            } else {
                workbook.close();
                // Người dùng bấm Cancel, không lưu file
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo Excel!");
        }
    }
}
