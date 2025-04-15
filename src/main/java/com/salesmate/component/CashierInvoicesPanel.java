/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.salesmate.controller.DetailController;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.ProductController;
import com.salesmate.model.Detail;
import com.salesmate.model.Invoice;
import com.salesmate.model.Product;
import com.salesmate.model.User;
import com.salesmate.utils.SessionManager;

/**
 *
 * @author Nhan
 */
public class CashierInvoicesPanel extends javax.swing.JPanel {
    private InvoiceController invoiceController;
    private DefaultTableModel tableModel;
    private List<Invoice> invoices;

    public CashierInvoicesPanel() {
        initComponents();
        setupTable();
        loadUserInvoices();
        
        // Add action listeners
        btnRefresh.addActionListener(e -> loadUserInvoices());
        cbbMonth.addActionListener(e -> filterInvoices());
        txtYear.addActionListener(e -> filterInvoices());
    }

    private void setupTable() {
        invoiceController = new InvoiceController();
        
        // Setup table model
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Số HĐ", "Ngày tạo", "Tổng tiền", "Trạng thái", "Hành động"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Chỉ cho phép edit cột hành động
            }
        };
        
        tblInvoices.setModel(tableModel);
        
        // Style table
        tblInvoices.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblInvoices.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblInvoices.setRowHeight(30);
        tblInvoices.setSelectionBackground(new Color(232, 241, 249));
        tblInvoices.setSelectionForeground(Color.BLACK);
        
        // Add button column for actions
        TableColumn actionColumn = tblInvoices.getColumnModel().getColumn(4);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void loadUserInvoices() {
        // Lấy user hiện tại từ SessionManager
        User currentUser = SessionManager.getInstance().getLoggedInUser();
        if (currentUser == null) return;

        // Lấy danh sách hoá đơn
        invoices = invoiceController.getInvoicesByUserId(currentUser.getUsersId());
        
        refreshTableData(invoices);
    }

    private void refreshTableData(List<Invoice> invoicesToShow) {
        tableModel.setRowCount(0); // Clear existing rows
        
        if (invoicesToShow == null || invoicesToShow.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có hoá đơn nào!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

        for (Invoice invoice : invoicesToShow) {
            tableModel.addRow(new Object[]{
                "#" + invoice.getInvoiceId(),
                dateFormat.format(invoice.getCreatedAt()),
                currencyFormat.format(invoice.getTotal()),
                invoice.getPaymentStatus(),
                "Xem chi tiết"
            });
        }
    }

    private void filterInvoices() {
        if (invoices == null || invoices.isEmpty()) return;

        String selectedMonth = (String) cbbMonth.getSelectedItem();
        String yearText = txtYear.getText().trim();

        List<Invoice> filteredInvoices = new ArrayList<>(invoices);

        // Filter by month if selected
        if (!"Tất cả tháng".equals(selectedMonth)) {
            int month = cbbMonth.getSelectedIndex(); // 0 is "Tất cả tháng"
            filteredInvoices.removeIf(invoice -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(invoice.getCreatedAt());
                return cal.get(Calendar.MONTH) + 1 != month;
            });
        }

        // Filter by year if entered
        if (!yearText.isEmpty() && !yearText.equals("Nhập năm")) {
            try {
                int year = Integer.parseInt(yearText);
                filteredInvoices.removeIf(invoice -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(invoice.getCreatedAt());
                    return cal.get(Calendar.YEAR) != year;
                });
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Năm không hợp lệ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        refreshTableData(filteredInvoices);
    }

    private void showInvoiceDetails(int invoiceId) {
        // Tạo dialog
        JDialog detailDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn #" + invoiceId);
        detailDialog.setModal(true);
        detailDialog.setSize(600, 400);
        detailDialog.setLocationRelativeTo(null);

        // Panel chính với gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(240, 248, 255),
                    0, h, new Color(255, 255, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Lấy thông tin chi tiết hóa đơn
        DetailController detailController = new DetailController();
        List<Detail> details = detailController.getDetailsByInvoiceId(invoiceId);

        // Tạo table model cho chi tiết hóa đơn
        String[] columnNames = {"STT", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Thêm dữ liệu vào table
        ProductController productController = new ProductController();
        int stt = 1;
        double total = 0;
        for (Detail detail : details) {
            Product product = productController.getProductById(detail.getProductId());
            if (product != null) {
                model.addRow(new Object[]{
                    stt++,
                    product.getProductName(),
                    detail.getQuantity(),
                    String.format("%,d", detail.getPrice().intValue()),
                    String.format("%,d", detail.getTotal().intValue())
                });
                total += detail.getTotal().doubleValue();
            }
        }

        // Tạo và style table
        JTable detailTable = new JTable(model);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.setRowHeight(30);
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTable.getTableHeader().setBackground(new Color(240, 240, 240));
        detailTable.setShowGrid(true);
        detailTable.setGridColor(new Color(230, 230, 230));
        
        // Căn chỉnh cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        detailTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        detailTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Số lượng
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        detailTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Đơn giá
        detailTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Thành tiền

        // Thêm table vào scroll pane
        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Panel hiển thị tổng tiền
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        JLabel totalLabel = new JLabel("Tổng tiền: " + String.format("%,d VNĐ", (int)total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPanel.add(totalLabel);

        // Thêm components vào panel chính
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);

        detailDialog.add(mainPanel);
        detailDialog.setVisible(true);
    }

    // Custom Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(0, 123, 255));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setPreferredSize(new Dimension(80, 25)); // Giảm kích thước button
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Chi tiết");
            return this;
        }
    }

    // Custom Button Editor
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get invoice ID from the first column
                int row = tblInvoices.getSelectedRow();
                String invoiceIdStr = (String) tblInvoices.getValueAt(row, 0);
                int invoiceId = Integer.parseInt(invoiceIdStr.substring(1)); // Remove '#' prefix
                
                // Show invoice details in a dialog
                showInvoiceDetails(invoiceId);
            }
            isPushed = false;
            return label;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelInvoiceTable = new javax.swing.JScrollPane();
        tblInvoices = new javax.swing.JTable();
        lblHeader = new javax.swing.JLabel();
        cbbMonth = new javax.swing.JComboBox<>();
        txtYear = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();

        tblInvoices.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Số Hoá Đơn", "Ngày tạo", "Tổng tiền", "Trạng thái", "Hành động"
            }
        ));
        panelInvoiceTable.setViewportView(tblInvoices);

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("DANH SÁCH HOÁ ĐƠN ĐÃ LẬP ");

        cbbMonth.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbbMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12" }));

        txtYear.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtYear.setText("Nhập năm");

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRefresh.setText("Refresh");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelInvoiceTable, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblHeader)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(417, 417, 417)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(cbbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRefresh))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(panelInvoiceTable, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<String> cbbMonth;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JScrollPane panelInvoiceTable;
    private javax.swing.JTable tblInvoices;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables
}
