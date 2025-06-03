package com.salesmate.component;

import com.salesmate.controller.SalaryController;
import com.salesmate.controller.SalaryDetailController;
import com.salesmate.model.SalaryDetail;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExcelImporter;
import com.salesmate.utils.ExportDialog;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AdSalaryPanel extends JPanel {
    private final SalaryController salaryController;
    private final SalaryDetailController detailController;
    private final DefaultTableModel tableModel;
    private final JTable salaryTable;
    private final JTextField searchField;
    private final JComboBox<String> statusCombo;      // Combo lọc trạng thái
    private final JSpinner pageSpinner;
    private final JLabel totalPagesLabel;

    private int currentPage = 1;
    private final int pageSize = 20;
    private int totalPages = 1;
    private String currentSearch = "";
    private String currentStatusFilter = "All";

    // Màu sắc chủ đạo
    private static final Color PRIMARY_COLOR    = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR  = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR     = new Color(231, 76, 60);
    private static final Color EXPORT_COLOR     = new Color(155, 89, 182);
    private static final Color IMPORT_COLOR     = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR       = new Color(44, 62, 80);
    private static final Color LIGHT_TEXT       = new Color(255, 255, 255);
    private static final Color BORDER_COLOR     = new Color(189, 195, 199);

    public AdSalaryPanel() {
        this.salaryController = new SalaryController();
        this.detailController = new SalaryDetailController();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== HEADER =====
        JLabel titleLabel = new JLabel("Quản Lý Lương");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // ===== BUTTONS, SEARCH & STATUS FILTER =====
        JButton addButton     = createStyledButton("Thêm", PRIMARY_COLOR);
        JButton deleteButton  = createStyledButton("Xóa", ACCENT_COLOR);
        JButton processButton = createStyledButton("Process", SECONDARY_COLOR);
        JButton payButton     = createStyledButton("Pay", SECONDARY_COLOR.darker());
        JButton refreshButton = createStyledButton("Làm Mới", new Color(52, 73, 94));
        JButton exportButton  = createStyledButton("Xuất Excel", EXPORT_COLOR);
        JButton importButton  = createStyledButton("Nhập Excel", IMPORT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(processButton);
        buttonPanel.add(payButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(refreshButton);

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(180, 30));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // Tạo ComboBox lọc trạng thái
        String[] statuses = { "All", "Pending", "Processed", "Paid", "Cancelled" };
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusCombo.setPreferredSize(new Dimension(120, 30));

        JLabel searchLabel = new JLabel("Tìm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_COLOR);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(statusLabel);
        filterPanel.add(statusCombo);

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(BACKGROUND_COLOR);
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.SOUTH);

        // ===== TABLE SETUP =====
        String[] columns = {
            "ID", "Tên nhân viên", "Basic Salary", "Payment Period",
            "Total Salary", "Status", "Payment Date", "Note", "Chi tiết"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 8;  // chỉ cột “Chi tiết” cho phép nhấn nút
            }
        };
        salaryTable = new JTable(tableModel) {
            // Vẽ màu chữ cho cột Status
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (column == 5) { // cột Status
                    String status = (String) getValueAt(row, column);
                    if ("Paid".equals(status)) {
                        comp.setForeground(new Color(40, 167, 69)); // success
                    } else if ("Processed".equals(status)) {
                        comp.setForeground(new Color(23, 162, 184)); // info
                    } else if ("Cancelled".equals(status)) {
                        comp.setForeground(new Color(220, 53, 69)); // danger
                    } else {
                        comp.setForeground(new Color(255, 193, 7)); // warning
                    }
                } else {
                    comp.setForeground(TEXT_COLOR);
                }
                return comp;
            }
        };
        salaryTable.setRowHeight(36);
        salaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salaryTable.setRowSorter(new TableRowSorter<>(tableModel));
        salaryTable.setShowGrid(true);
        salaryTable.setGridColor(new Color(220, 220, 220));
        salaryTable.setSelectionBackground(new Color(232, 234, 246));
        salaryTable.setSelectionForeground(TEXT_COLOR);

        styleHeader(salaryTable);
        styleRows(salaryTable);

        // Thêm cột “Chi tiết” với nút
        salaryTable.getColumnModel().getColumn(8).setCellRenderer(new DetailButtonRenderer());
        salaryTable.getColumnModel().getColumn(8).setCellEditor(new DetailButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // ===== PAGINATION =====
        JButton prevButton = createStyledButton("« Trước", SECONDARY_COLOR);
        JButton nextButton = createStyledButton("Sau »", SECONDARY_COLOR);
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        totalPagesLabel = new JLabel(" / 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPagesLabel.setForeground(TEXT_COLOR);
        JLabel pageInfoLabel = new JLabel("Trang:");
        pageInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pageInfoLabel.setForeground(TEXT_COLOR);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paginationPanel.setBackground(BACKGROUND_COLOR);
        paginationPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        paginationPanel.add(prevButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(pageSpinner);
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(nextButton);

        // ===== MAIN LAYOUT =====
        setLayout(new BorderLayout());
        add(northContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS =====

        // Nút thêm Salary (giữ nguyên logic cũ hoặc tùy bạn triển khai)
        addButton.addActionListener(e -> showAddSalaryDialog());

        // Nút xóa mềm (đổi status = 'Cancelled')
        deleteButton.addActionListener(e -> {
            int row = salaryTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn một bản ghi để xóa");
                return;
            }
            int modelRow = salaryTable.convertRowIndexToModel(row);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận hủy Salary ID = " + id + " ?", "Xóa mềm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = salaryController.deleteSalary(id);
                JOptionPane.showMessageDialog(this,
                    ok ? "Đã hủy (Cancelled)" : "Lỗi khi hủy",
                    ok ? "Thành công" : "Lỗi",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                loadSalaryPage();
            }
        });

        // Nút Process
        processButton.addActionListener(e -> {
            int row = salaryTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn một bản ghi để Process");
                return;
            }
            int modelRow = salaryTable.convertRowIndexToModel(row);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            boolean ok = salaryController.processSalary(id);
            JOptionPane.showMessageDialog(this,
                ok ? "Đã chuyển sang ‘Processed’" : "Lỗi khi Process",
                ok ? "Thành công" : "Lỗi",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadSalaryPage();
        });

        // Nút Pay
        payButton.addActionListener(e -> {
            int row = salaryTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn một bản ghi để Pay");
                return;
            }
            int modelRow = salaryTable.convertRowIndexToModel(row);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            boolean ok = salaryController.paySalary(id);
            JOptionPane.showMessageDialog(this,
                ok ? "Đã chuyển sang ‘Paid’" : "Lỗi khi Pay",
                ok ? "Thành công" : "Lỗi",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadSalaryPage();
        });

        // Nút Làm Mới (xóa filter, reset trang và load lại)
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            statusCombo.setSelectedItem("All");
            currentSearch = "";
            currentStatusFilter = "All";
            currentPage = 1;
            pageSpinner.setValue(1);
            loadSalaryPage();
        });

        // Xuất Excel
        exportButton.addActionListener(e -> exportToExcel());

        // Nhập Excel
        importButton.addActionListener(e -> importFromExcel());

        // Pagination: nút “Trước”
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageSpinner.setValue(currentPage);
                loadSalaryPage();
            }
        });

        // Pagination: nút “Sau”
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                pageSpinner.setValue(currentPage);
                loadSalaryPage();
            }
        });

        // Khi thay đổi số trang qua spinner
        pageSpinner.addChangeListener(e -> {
            int v = (Integer) pageSpinner.getValue();
            if (v >= 1 && v <= totalPages) {
                currentPage = v;
                loadSalaryPage();
            }
        });

        // Khi gõ tìm kiếm theo tên nhân viên
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                currentSearch = searchField.getText().trim().toLowerCase();
                currentPage = 1;
                pageSpinner.setValue(1);
                loadSalaryPage();
            }
        });

        // Khi thay đổi lựa chọn trạng thái
        statusCombo.addActionListener(e -> {
            currentStatusFilter = (String) statusCombo.getSelectedItem();
            currentPage = 1;
            pageSpinner.setValue(1);
            loadSalaryPage();
        });

        // Initial load
        loadSalaryPage();
    }

    /**
     * Tải dữ liệu lên table, phân trang + tìm kiếm + lọc trạng thái.
     */
    private void loadSalaryPage() {
        int offset = (currentPage - 1) * pageSize;
        String keyword = currentSearch;
        String statusFilter = currentStatusFilter;

        // Gọi controller (DAO) để lấy danh sách Object[] đã kèm tên nhân viên
        List<Object[]> salaries =
            salaryController.getSalariesWithEmployeeNameRaw(offset, pageSize, keyword, statusFilter);

        // Đổ dữ liệu lên tableModel
        tableModel.setRowCount(0);
        for (Object[] row : salaries) {
            tableModel.addRow(new Object[]{
                row[0], // salaryId
                row[8], // employeeName
                row[2], // basicSalary
                row[3], // paymentPeriod
                row[6], // totalSalary
                row[5], // status
                row[4], // paymentDate
                row[7], // note
                "Chi tiết"
            });
        }

        // Tính tổng số bản ghi
        int totalRecords = salaryController.countSalariesWithEmployeeNameRaw(keyword, statusFilter);
        totalPages = Math.max((int) Math.ceil((double) totalRecords / pageSize), 1);

        // Cập nhật spinner và label tổng trang
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);
    }

    /**
     * Hiển thị dialog thêm lương mới (giữ nguyên như cũ).
     */
    private void showAddSalaryDialog() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Thêm lương mới", true
        );
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // 1. Employee ID
        JTextField empIdField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Mã nhân viên:"), gbc);
        gbc.gridx = 1;
        form.add(empIdField, gbc);

        // 2. Basic Salary
        JTextField basicField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Basic Salary:"), gbc);
        gbc.gridx = 1;
        form.add(basicField, gbc);

        // 3. Payment Period (YYYY-MM)
        JTextField periodField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Payment Period (YYYY-MM):"), gbc);
        gbc.gridx = 1;
        form.add(periodField, gbc);

        // 4. Total Salary
        JTextField totalField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Total Salary:"), gbc);
        gbc.gridx = 1;
        form.add(totalField, gbc);

        // 5. Note
        JTextField noteField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Note:"), gbc);
        gbc.gridx = 1;
        form.add(noteField, gbc);

        // 6. Payment Date (JDateChooser)
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Payment Date:"), gbc);
        gbc.gridx = 1;
        form.add(dateChooser, gbc);

        // Buttons “Lưu” / “Hủy”
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton save   = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");

        save.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdField.getText().trim());
                BigDecimal basicSalary = new BigDecimal(basicField.getText().trim());
                String period = periodField.getText().trim();
                BigDecimal totalSalary = new BigDecimal(totalField.getText().trim());
                Date payDate = dateChooser.getDate();
                if (payDate == null) {
                    JOptionPane.showMessageDialog(dialog, "Chọn Payment Date", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String note = noteField.getText().trim();

                boolean ok = salaryController.addSalary(
                    empId,
                    basicSalary,
                    period,
                    payDate,
                    totalSalary,
                    note
                );
                JOptionPane.showMessageDialog(dialog,
                    ok ? "Thêm thành công" : "Lỗi thêm",
                    ok ? "Thành công" : "Lỗi",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
                );
                if (ok) {
                    dialog.dispose();
                    loadSalaryPage();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID hoặc số tiền không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dialog.dispose());

        btnP.add(save);
        btnP.add(cancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnP, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Xuất bảng ra Excel hoặc CSV (giữ nguyên logic cũ).
     */
    private void exportToExcel() {
        ExportDialog dlg = new ExportDialog((Frame) SwingUtilities.getWindowAncestor(this), salaryTable);
        dlg.setVisible(true);
        if (dlg.isExportConfirmed()) {
            File f = dlg.showSaveDialog();
            if (f != null) {
                try {
                    if (dlg.isXLSX()) {
                        ExcelExporter.exportToExcel(salaryTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
                    } else {
                        ExcelExporter.exportToCSV(salaryTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
                    }
                    if (dlg.openAfterExport()) {
                        ExcelExporter.openFile(f);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Lỗi xuất: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Nhập từ file Excel, ánh xạ cột giống AdUserPanel (giữ nguyên logic cũ).
     */
    private void importFromExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                if (!ExcelImporter.validateExcelFile(f)) {
                    JOptionPane.showMessageDialog(this,
                        "Excel không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JDialog mapDlg = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Ánh xạ cột", true
                );
                JPanel mapP = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                String[] fields = {
                    "Employee ID", "Basic Salary", "Payment Period",
                    "Total Salary", "Payment Date", "Status", "Note"
                };
                String[] hdr = ExcelImporter.getColumnHeaders(f);
                @SuppressWarnings("unchecked")
                JComboBox<String>[] cmb = new JComboBox[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    gbc.gridx = 0; gbc.gridy = i;
                    mapP.add(new JLabel(fields[i] + ":"), gbc);
                    gbc.gridx = 1;
                    cmb[i] = new JComboBox<>(hdr);
                    mapP.add(cmb[i], gbc);
                }
                JButton imp = new JButton("Nhập");
                JButton cn  = new JButton("Hủy");
                imp.addActionListener(e -> {
                    mapDlg.dispose();
                    performImport(f, cmb);
                });
                cn.addActionListener(e -> mapDlg.dispose());
                JPanel bp = new JPanel();
                bp.add(imp);
                bp.add(cn);

                mapDlg.setLayout(new BorderLayout());
                mapDlg.add(mapP, BorderLayout.CENTER);
                mapDlg.add(bp, BorderLayout.SOUTH);
                mapDlg.pack();
                mapDlg.setLocationRelativeTo(this);
                mapDlg.setVisible(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi import: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performImport(File file, JComboBox<String>[] combos) {
        try {
            List<Object[]> data = ExcelImporter.importFromExcel(file);
            int success = 0, fail = 0;
            StringBuilder log = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                Object[] row = data.get(i);
                try {
                    int empId = Integer.parseInt(row[combos[0].getSelectedIndex()].toString());
                    BigDecimal basicSalary = new BigDecimal(row[combos[1].getSelectedIndex()].toString());
                    String period = row[combos[2].getSelectedIndex()].toString();
                    BigDecimal totalSalary = new BigDecimal(row[combos[3].getSelectedIndex()].toString());
                    java.util.Date dt = (java.util.Date) row[combos[4].getSelectedIndex()];
                    String status = row[combos[5].getSelectedIndex()].toString();
                    String note = row[combos[6].getSelectedIndex()].toString();

                    boolean ok = salaryController.addSalary(
                        empId, basicSalary, period, dt, totalSalary, note
                    );
                    if (ok) {
                        success++;
                    } else {
                        fail++;
                        log.append("Dòng ").append(i + 2).append(": Lỗi lưu\n");
                    }
                } catch (Exception ex) {
                    fail++;
                    log.append("Dòng ").append(i + 2).append(": ").append(ex.getMessage()).append("\n");
                }
            }
            String msg = "Nhập thành công " + success + " bản ghi."
                       + (fail > 0 ? "\nKhông thể nhập " + fail + " bản ghi.\n" + log : "");
            JOptionPane.showMessageDialog(this, msg);
            loadSalaryPage();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi import: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== Renderer & Editor cho cột “Chi tiết” ====================

    class DetailButtonRenderer extends JButton implements TableCellRenderer {
        public DetailButtonRenderer() {
            setText("Chi tiết");
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(LIGHT_TEXT);
            setBackground(SECONDARY_COLOR);
            setBorderPainted(false);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (isSelected) {
                setBackground(SECONDARY_COLOR.darker());
            } else {
                setBackground(SECONDARY_COLOR);
            }
            return this;
        }
    }

    class DetailButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public DetailButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Chi tiết");
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(LIGHT_TEXT);
            button.setBackground(SECONDARY_COLOR);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            button.addActionListener(e -> fireEditingStopped());
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(SECONDARY_COLOR.darker());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(SECONDARY_COLOR);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            int modelRow = salaryTable.convertRowIndexToModel(currentRow);
            int salaryId = (int) tableModel.getValueAt(modelRow, 0);
List<SalaryDetail> details = detailController.getDetailsBySalaryId(salaryId);
showDetailDialog(salaryId, details);

           
       

            return "Chi tiết";
        }
    }

    // ==================== Dialog xem chi tiết ====================
private void showDetailDialog(int salaryId, List<SalaryDetail> details) {
    JDialog dlg = new JDialog(
        (Frame) SwingUtilities.getWindowAncestor(this),
        "Chi tiết lương #" + salaryId, true
    );
    dlg.setLayout(new BorderLayout());
    dlg.getContentPane().setBackground(BACKGROUND_COLOR);

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    if (details != null && !details.isEmpty()) {
        String[] cols = {
            "ID", "Component", "Type", "Calculation",
            "Base", "Value", "Amount", "Taxable", "Ghi chú"
        };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (SalaryDetail d : details) {
            model.addRow(new Object[]{
                d.getSalaryDetailId(),
                d.getComponentName(),
                d.getComponentType(),
                d.getCalculationType(),
                d.getCalculationBase(),
                d.getValue(),
                d.getAmount(),
                d.getIsTaxable() == 1 ? "Có" : "Không",
                d.getNote()
            });
        }

        JTable tbl = new JTable(model);
        tbl.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(tbl);
        panel.add(scroll, BorderLayout.CENTER);
    } else {
        JLabel notFound = new JLabel("Không tìm thấy chi tiết nào cho salary_id = " + salaryId);
        notFound.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notFound.setForeground(Color.RED);
        panel.add(notFound, BorderLayout.CENTER);
    }

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton closeBtn = createStyledButton("Đóng", ACCENT_COLOR);
    closeBtn.setPreferredSize(new Dimension(100, 35));
    closeBtn.addActionListener(e -> dlg.dispose());
    bottom.add(closeBtn);

    dlg.add(panel, BorderLayout.CENTER);
    dlg.add(bottom, BorderLayout.SOUTH);
    dlg.setSize(800, 400);
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
}

    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    private JLabel createInfoValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    /**
     * Nút phong cách, giữ nguyên logic cũ.
     */
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(bg.brighter());
                } else {
                    g.setColor(bg);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setForeground(LIGHT_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleHeader(JTable table) {
        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(h.getWidth(), 45));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(new Color(25, 79, 115));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return lbl;
            }
        });
    }

    private void styleRows(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (!s) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                int[] centerCols = { 0, 2, 4, 5 };
                boolean shouldCenter = false;
                for (int col : centerCols) {
                    if (c == col) { shouldCenter = true; break; }
                }
                setHorizontalAlignment(shouldCenter ? JLabel.CENTER : JLabel.LEFT);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }
}
