package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
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
import com.salesmate.dao.UserDAO;
import com.salesmate.model.Detail;
import com.salesmate.model.Invoice;
import com.salesmate.model.Product;
import com.salesmate.model.User;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExportDialog;

public class CashierRecentInvoice extends javax.swing.JPanel {
    private InvoiceController invoiceController;
    private UserDAO userDAO;
    private DefaultTableModel tableModel;
    private List<Invoice> invoices;

    private static final int ROWS_PER_PAGE = 15; // Thay đổi số dòng mỗi trang
    private int currentPage = 1;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageInfoLabel;

    public CashierRecentInvoice() {
        initComponents();
        if (!java.beans.Beans.isDesignTime()) {
            setupComponents();
            loadRecentInvoices();
        }
        
        // Add action listeners
        btnRefresh.addActionListener(e -> loadRecentInvoices());
        comboBoxMonth.addActionListener(e -> refreshTableData(filterInvoices()));
        txtYear.addActionListener(e -> refreshTableData(filterInvoices()));
    }
    
    private void setupComponents() {
        invoiceController = new InvoiceController();
        userDAO = new UserDAO();
        
        // Setup table model
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Số HĐ", "Ngày tạo", "Người tạo", "Tổng tiền", "Trạng thái", "Hành động"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cho phép edit cột hành động
            }
        };
        
        tblDataInvoiceRecent.setModel(tableModel);
        
        // Style table
        tblDataInvoiceRecent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblDataInvoiceRecent.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblDataInvoiceRecent.setRowHeight(30);
        tblDataInvoiceRecent.setSelectionBackground(new Color(232, 241, 249));
        tblDataInvoiceRecent.setSelectionForeground(Color.BLACK);

        // Giảm kích thước cột hành động
        tblDataInvoiceRecent.getColumnModel().getColumn(5).setMinWidth(70);
        tblDataInvoiceRecent.getColumnModel().getColumn(5).setMaxWidth(70);
        tblDataInvoiceRecent.getColumnModel().getColumn(5).setPreferredWidth(70);

        // Style cho các dòng xen kẽ
        tblDataInvoiceRecent.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                if (column == 1) { // Ngày tạo
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 3) { // Tổng tiền
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                } else if (column == 4) { // Trạng thái
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    
                    // Set màu cho trạng thái
                    if ("Paid".equals(value)) {
                        c.setForeground(new Color(0, 128, 0)); // Green for paid
                    } else if ("Unpaid".equals(value)) {
                        c.setForeground(new Color(204, 0, 0)); // Red for unpaid
                    }
                }
                
                return c;
            }
        });

        // Add button column for actions
        TableColumn actionColumn = tblDataInvoiceRecent.getColumnModel().getColumn(5);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        // Add pagination panel
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
                refreshTableData(filterInvoices());
            }
        });

        nextButton.addActionListener(e -> {
            int totalPages = getTotalPages(invoices);
            if (currentPage < totalPages) {
                currentPage++;
                refreshTableData(filterInvoices());
            }
        });

        // Thêm phân trang vào layout chính
        setLayout(new BorderLayout());
        add(ScrollPaneInvoice, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // Add export button
        JButton btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(new Color(40, 167, 69));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> handleExport());
        
        // Add filter months and year
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        comboBoxMonth.setPreferredSize(new Dimension(150, 25));
        comboBoxMonth.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        txtYear.setPreferredSize(new Dimension(80, 25)); 
        txtYear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        filterPanel.add(comboBoxMonth);
        filterPanel.add(txtYear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setBackground(Color.WHITE);
        rightButtons.add(btnExport);
        rightButtons.add(btnRefresh);
        
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(rightButtons, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }

    private void handleExport() {
        ExportDialog dialog = new ExportDialog((Frame) SwingUtilities.getWindowAncestor(this), tblDataInvoiceRecent);
        dialog.setVisible(true);
        
        if (dialog.isExportConfirmed()) {
            File file = dialog.showSaveDialog();
            if (file != null) {
                try {
                    List<Integer> selectedColumns = dialog.getSelectedColumns();
                    if (dialog.isXLSX()) {
                        ExcelExporter.exportToExcel(tblDataInvoiceRecent, file, 
                            dialog.includeHeaders(), selectedColumns);
                    } else {
                        ExcelExporter.exportToCSV(tblDataInvoiceRecent, file, 
                            dialog.includeHeaders(), selectedColumns);
                    }
                    
                    if (dialog.openAfterExport()) {
                        ExcelExporter.openFile(file);
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Xuất file thành công!", 
                        "Thông báo", 
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

    private void loadRecentInvoices() {
        // Get 7 days of recent invoices
        invoices = invoiceController.getInvoicesLast7Days();
        refreshTableData(invoices);
    }
    
    private void refreshTableData(List<Invoice> invoicesToShow) {
        tableModel.setRowCount(0); // Clear existing rows
        
        if (invoicesToShow == null || invoicesToShow.isEmpty()) {
            return; // Just clear table and return - no message needed
        }

        int start = (currentPage - 1) * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, invoicesToShow.size());
        List<Invoice> pageInvoices = invoicesToShow.subList(start, end);

        // Update pagination controls
        updatePaginationControls(invoicesToShow.size());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

        for (Invoice invoice : pageInvoices) {
            // Lấy thông tin người tạo hoá đơn
            User creator = userDAO.getUserById(invoice.getUsersId());
            String creatorName = creator != null ? creator.getUsername() : "Không xác định";
            
            tableModel.addRow(new Object[]{
                "#" + invoice.getInvoiceId(),
                dateFormat.format(invoice.getCreatedAt()),
                creatorName,
                currencyFormat.format(invoice.getTotal()),
                invoice.getPaymentStatus(),
                "Chi tiết"
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

    private List<Invoice> filterInvoices() {
        if (invoices == null || invoices.isEmpty()) return new ArrayList<>();

        String selectedMonth = (String) comboBoxMonth.getSelectedItem();
        String yearText = txtYear.getText().trim();

        List<Invoice> filteredInvoices = new ArrayList<>(invoices);

        // Filter by month if selected 
        if (!"Tất cả các tháng".equals(selectedMonth)) {
            int month = comboBoxMonth.getSelectedIndex(); // 0 is "Tất cả các tháng"
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

        return filteredInvoices;
    }

    private void showInvoiceDetails(int invoiceId) {
        // Lấy thông tin chi tiết hóa đơn
        Invoice invoice = invoiceController.getInvoiceById(invoiceId);
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy thông tin người lập hóa đơn
        User invoiceCreator = userDAO.getUserById(invoice.getUsersId());
        
        // Tạo dialog hiển thị chi tiết
        JDialog detailDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn #" + invoiceId);
        detailDialog.setModal(true);
        detailDialog.setSize(1000, 650); // Tăng kích thước dialog
        detailDialog.setLocationRelativeTo(null);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel thông tin hóa đơn và người lập
        JPanel infoPanel = new JPanel(new BorderLayout(15, 5));
        infoPanel.setBackground(Color.WHITE);
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("CHI TIẾT HÓA ĐƠN #" + invoiceId);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Panel thông tin hóa đơn
        JPanel invoiceInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        invoiceInfoPanel.setBackground(Color.WHITE);
        
        // Format ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        // Thông tin hóa đơn
        JLabel invoiceDateLabel = new JLabel("Ngày lập: " + dateFormat.format(invoice.getCreatedAt()));
        invoiceDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel invoiceStatusLabel = new JLabel("Trạng thái: " + invoice.getPaymentStatus());
        invoiceStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if ("Paid".equals(invoice.getPaymentStatus())) {
            invoiceStatusLabel.setForeground(new Color(0, 128, 0)); // Green color for Paid
        } else {
            invoiceStatusLabel.setForeground(new Color(204, 0, 0)); // Red color for Unpaid
        }
        
        // Thông tin người lập
        JLabel creatorLabel = new JLabel("Người lập: " + 
                (invoiceCreator != null ? invoiceCreator.getUsername() : "Không xác định"));
        creatorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Thêm các thành phần vào panel thông tin
        invoiceInfoPanel.add(invoiceDateLabel);
        invoiceInfoPanel.add(invoiceStatusLabel);
        invoiceInfoPanel.add(creatorLabel);
        
        // Thêm tiêu đề và thông tin vào panel info
        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(invoiceInfoPanel, BorderLayout.CENTER);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

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
                    String.format("%,d VNĐ", detail.getPrice().intValue()),
                    String.format("%,d VNĐ", detail.getTotal().intValue())
                });
                total += detail.getTotal().doubleValue();
            }
        }

        // Tạo và style table
        JTable detailTable = new JTable(model);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.setRowHeight(35);
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
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
                
                // Căn giữa cho cột STT và Số lượng
                if (column == 0 || column == 2) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                }
                // Căn phải cho cột đơn giá và thành tiền
                else if (column == 3 || column == 4) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                }
                
                return c;
            }
        });
        
        // Panel tổng tiền
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        
        JLabel totalLabel = new JLabel("TỔNG TIỀN: " + String.format("%,d VNĐ", invoice.getTotal().intValue()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(204, 0, 0)); // Red color for total
        
        totalPanel.add(totalLabel);

        // Panel chứa nút ở dưới cùng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Nút xuất Excel thay thế nút in
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
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm padding cho total panel để không bị che bởi button panel
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

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
                int row = tblDataInvoiceRecent.getSelectedRow();
                String invoiceIdStr = (String) tblDataInvoiceRecent.getValueAt(row, 0);
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

        ScrollPaneInvoice = new javax.swing.JScrollPane();
        tblDataInvoiceRecent = new javax.swing.JTable();
        lblHeader = new javax.swing.JLabel();
        comboBoxMonth = new javax.swing.JComboBox<>();
        txtYear = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        tblDataInvoiceRecent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Số Hoá Đơn", "Ngày Tạo", "Người Tạo", "Tổng Tiền", "Trạng Thái", "Hành động"
            }
        ));
        ScrollPaneInvoice.setViewportView(tblDataInvoiceRecent);

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblHeader.setText("DANH SÁCH HOÁ ĐƠN GẦN ĐÂY");

        comboBoxMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả các tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12" }));

        txtYear.setText("Nhập năm");

        btnRefresh.setText("Refresh");

        btnExport.setText("Xuất Excel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                        .addGap(361, 361, 361))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(comboBoxMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(300, 300, 300)
                        .addComponent(btnExport)
                        .addGap(18, 18, 18) 
                        .addComponent(btnRefresh))
                    .addComponent(ScrollPaneInvoice, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblHeader)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExport)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScrollPaneInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrollPaneInvoice;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnExport;
    private javax.swing.JComboBox<String> comboBoxMonth;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JTable tblDataInvoiceRecent;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables
}
