/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

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
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExportDialog;
import com.salesmate.utils.SessionManager;

public class CashierInvoicesPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(CashierInvoicesPanel.class.getName());
    private InvoiceController invoiceController;
    private DefaultTableModel tableModel;
    private List<Invoice> invoices;
    private static final int ROWS_PER_PAGE = 15; // Thay đổi số dòng mỗi trang
    private int currentPage = 1;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageInfoLabel;

    public CashierInvoicesPanel() {
        initComponents();

        if (!Beans.isDesignTime()) {
            setupComponents();
            loadUserInvoices();

            // Add action listeners
            btnRefresh.addActionListener(e -> loadUserInvoices());
        }
    }

    private void setupComponents() {
        setupTable();

        // Thay đổi layout chính
        setLayout(new BorderLayout());
        add(panelInvoiceTable, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        prevButton = new JButton("Trang trước");
        nextButton = new JButton("Trang sau");
        pageInfoLabel = new JLabel("Trang 1/1");

        // Style buttons
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        prevButton.setForeground(Color.WHITE);
        prevButton.setBackground(new Color(0, 123, 255));
        prevButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        prevButton.setFocusPainted(false);

        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBackground(new Color(0, 123, 255));
        nextButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        nextButton.setFocusPainted(false);

        pageInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pageInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Add pagination controls
        paginationPanel.add(prevButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextButton);

        // Add listeners
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                refreshTableData(invoices);
            }
        });

        nextButton.addActionListener(e -> {
            int totalPages = getTotalPages(invoices);
            if (currentPage < totalPages) {
                currentPage++;
                refreshTableData(invoices);
            }
        });

        // Add pagination panel to main panel  
        add(paginationPanel, BorderLayout.SOUTH);

        // Add export button
        JButton btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(new Color(40, 167, 69));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> handleExport());

        // Add top panel with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setBackground(Color.WHITE);
        rightButtons.add(btnExport);
        rightButtons.add(btnRefresh);

        topPanel.add(rightButtons, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void handleExport() {
        ExportDialog dialog = new ExportDialog((Frame) SwingUtilities.getWindowAncestor(this), tblInvoices);
        dialog.setVisible(true);

        if (dialog.isExportConfirmed()) {
            File file = dialog.showSaveDialog();
            if (file != null) {
                try {
                    List<Integer> selectedColumns = dialog.getSelectedColumns();
                    if (dialog.isXLSX()) {
                        ExcelExporter.exportToExcel(tblInvoices, file, 
                            dialog.includeHeaders(), selectedColumns);
                    } else {
                        ExcelExporter.exportToCSV(tblInvoices, file, 
                            dialog.includeHeaders(), selectedColumns);
                    }

                    if (dialog.openAfterExport()) {
                        ExcelExporter.openFile(file);
                    }

                    JOptionPane.showMessageDialog(this, 
                        "Xuất file thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Lỗi khi xuất file: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void setupTable() {
        invoiceController = new InvoiceController();

        // Setup table model
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Số HĐ", "Ngày tạo", "Tổng tiền", "Trạng thái", "Hành động"}
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

        // Giảm kích thước cột hành động
        tblInvoices.getColumnModel().getColumn(4).setMinWidth(70);
        tblInvoices.getColumnModel().getColumn(4).setMaxWidth(70);
        tblInvoices.getColumnModel().getColumn(4).setPreferredWidth(70);

        // Style cho các dòng xen kẽ
        tblInvoices.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(179, 229, 252)); // Light blue khi select
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255) : new Color(240, 240, 240));
                    c.setForeground(Color.BLACK);
                }

                // Căn chỉnh các cột
                if (column == 1 || column == 2 || column == 3) { // Ngày tạo, Tổng tiền, Trạng thái
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 2) { // Tổng tiền
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                }

                return c;
            }
        });

        // Add button column for actions
        TableColumn actionColumn = tblInvoices.getColumnModel().getColumn(4);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void loadUserInvoices() {
        // Lấy user hiện tại từ SessionManager
        User currentUser = SessionManager.getInstance().getLoggedInUser();
        if (currentUser == null) {
            return;
        }

        // Lấy danh sách hoá đơn
        invoices = invoiceController.getInvoicesByUserId(currentUser.getUsersId());

        refreshTableData(invoices);
    }

    private void refreshTableData(List<Invoice> invoicesToShow) {
        tableModel.setRowCount(0);

        if (invoicesToShow == null || invoicesToShow.isEmpty()) {
            return;
        }

        int start = (currentPage - 1) * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, invoicesToShow.size());
        List<Invoice> pageInvoices = invoicesToShow.subList(start, end);

        // Update pagination controls
        updatePaginationControls(invoicesToShow.size());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

        for (Invoice invoice : pageInvoices) {
            tableModel.addRow(new Object[]{
                "#" + invoice.getInvoiceId(),
                dateFormat.format(invoice.getCreatedAt()),
                currencyFormat.format(invoice.getTotal()),
                invoice.getPaymentStatus(),
                "Xem chi tiết"
            });
        }
    }

    private void updatePaginationControls(int totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / ROWS_PER_PAGE);
        pageInfoLabel.setText(String.format("Trang %d/%d", currentPage, totalPages));
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }

    private int getTotalPages(List<Invoice> items) {
        return (int) Math.ceil((double) items.size() / ROWS_PER_PAGE);
    }

    private void showInvoiceDetails(int invoiceId) {
        // Tăng kích thước dialog
        JDialog detailDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn #" + invoiceId);
        detailDialog.setModal(true);
        detailDialog.setSize(1000, 600); // Tăng kích thước dialog
        detailDialog.setLocationRelativeTo(null);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
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
        detailTable.setRowHeight(35);

        // Cấu hình độ rộng các cột
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Tên sản phẩm
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Số lượng
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Đơn giá
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Thành tiền

        // Style cho các dòng xen kẽ trong bảng chi tiết
        detailTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(179, 229, 252));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255) : new Color(240, 240, 240));
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        // Panel chứa nút ở dưới cùng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Nút xuất Excel
        JButton exportButton = new JButton("Xuất Excel");
        exportButton.setBackground(new Color(0, 123, 255));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportButton.addActionListener(e -> {
            ExportDialog dialog = new ExportDialog((Frame) SwingUtilities.getWindowAncestor(this), detailTable);
            dialog.setVisible(true);

            if (dialog.isExportConfirmed()) {
                File file = dialog.showSaveDialog();
                if (file != null) {
                    try {
                        List<Integer> selectedColumns = dialog.getSelectedColumns();
                        if (dialog.isXLSX()) {
                            ExcelExporter.exportToExcel(detailTable, file, 
                                dialog.includeHeaders(), selectedColumns);
                        } else {
                            ExcelExporter.exportToCSV(detailTable, file, 
                                dialog.includeHeaders(), selectedColumns);
                        }

                        if (dialog.openAfterExport()) {
                            ExcelExporter.openFile(file);
                        }

                        JOptionPane.showMessageDialog(detailDialog,
                            "Xuất file thành công!", 
                            "Thông báo", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(detailDialog,
                            "Lỗi khi xuất file: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Nút đóng
        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> detailDialog.dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);

        // Layout
        mainPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

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
        btnRefresh = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

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

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRefresh.setText("Refresh");

        btnPrint.setText("In danh sách");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelInvoiceTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblHeader)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelInvoiceTable, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JScrollPane panelInvoiceTable;
    private javax.swing.JTable tblInvoices;
    // End of variables declaration//GEN-END:variables
}
