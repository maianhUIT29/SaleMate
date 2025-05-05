package com.salesmate.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.salesmate.controller.DetailController;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.ProductController;
import com.salesmate.controller.UserController;
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
    private UserController userController;
    private DefaultTableModel tableModel;
    private List<Invoice> invoices;
    private static final int ROWS_PER_PAGE = 15; // Thay đổi số dòng mỗi trang
    private int currentPage = 1;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageInfoLabel;
    private javax.swing.JComboBox<String> filterComboBox;

    public CashierInvoicesPanel() {
        initComponents();

        if (!Beans.isDesignTime()) {
            setupComponents();
            loadInvoicesBasedOnFilter();
            btnRefresh.addActionListener(e -> {
                currentPage = 1;
                loadInvoicesBasedOnFilter();
            });
            btnPrint.addActionListener(e -> printInvoicesList());
        }
    }

    private void setupComponents() {
        try {
            setupTable();

            // Thay đổi layout chính
            setLayout(new BorderLayout());
            add(panelInvoiceTable, BorderLayout.CENTER);

            JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            paginationPanel.setBackground(Color.WHITE);
            paginationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            // Create pagination buttons with special styling for pagination
            prevButton = createPaginationButton("Trang trước", new Color(0, 123, 255));
            nextButton = createPaginationButton("Trang sau", new Color(0, 123, 255));
            pageInfoLabel = new JLabel("Trang 1/1");
            
            // Style page info label
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

            // Create filter combo box with additional 30-day option
            filterComboBox = new javax.swing.JComboBox<>(new String[]{
                "Hoá đơn do tôi tạo", 
                "Hoá đơn 7 ngày gần đây",
                "Hoá đơn 30 ngày gần đây"
            });
            filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            filterComboBox.setPreferredSize(new Dimension(200, 35));
            filterComboBox.setBackground(Color.WHITE);
            
            // Style the combo box with a simple border
            filterComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            
            // Add listener to filter combo box
            filterComboBox.addActionListener(e -> {
                currentPage = 1; // Reset to first page when filter changes
                loadInvoicesBasedOnFilter();
            });

            // Style refresh button
            btnRefresh = createStyledButton("Làm mới", new Color(0, 123, 255));
            btnRefresh.setPreferredSize(new Dimension(120, 35));
            
            // Style print button
            btnPrint = createStyledButton("In danh sách", new Color(108, 117, 125));
            btnPrint.setPreferredSize(new Dimension(150, 35));

            // Create export button
            JButton btnExport = createStyledButton("Xuất Excel", new Color(40, 167, 69));
            btnExport.setPreferredSize(new Dimension(150, 35));
            btnExport.addActionListener(e -> handleExport());

            // Add top panel with filter and buttons
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(Color.WHITE);
            topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            // Left panel for header and filter
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
            leftPanel.setBackground(Color.WHITE);
            
            // Add filter label
            JLabel filterLabel = new JLabel("Lọc theo:");
            filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            leftPanel.add(filterLabel);
            leftPanel.add(filterComboBox);

            // Right panel for buttons
            JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            rightButtons.setBackground(Color.WHITE);
            rightButtons.add(btnPrint);
            rightButtons.add(btnExport);
            rightButtons.add(btnRefresh);

            topPanel.add(leftPanel, BorderLayout.WEST);
            topPanel.add(rightButtons, BorderLayout.EAST);

            // Add header to top of container
            JPanel headerContainer = new JPanel(new BorderLayout());
            headerContainer.setBackground(Color.WHITE);
            
            // Apply modern style to header label
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblHeader.setForeground(new Color(33, 37, 41));
            lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
            lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            
            headerContainer.add(lblHeader, BorderLayout.CENTER);
            
            JPanel mainTopPanel = new JPanel(new BorderLayout());
            mainTopPanel.add(headerContainer, BorderLayout.NORTH);
            mainTopPanel.add(topPanel, BorderLayout.SOUTH);
            
            add(mainTopPanel, BorderLayout.NORTH);
        } catch (Exception ex) {
            // If anything goes wrong, show an error and create a simple fallback UI
            System.err.println("Error setting up components: " + ex.getMessage());
            ex.printStackTrace();
            
            setLayout(new BorderLayout());
            
            JLabel errorLabel = new JLabel("Không thể tải dữ liệu. Vui lòng thử lại sau.");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
            
            JButton retryButton = new JButton("Làm mới");
            retryButton.addActionListener(e -> {
                removeAll();
                setupComponents();
                loadInvoicesBasedOnFilter();
                revalidate();
                repaint();
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(retryButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void loadInvoicesBasedOnFilter() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        
        if (selectedFilter.equals("Hoá đơn 7 ngày gần đây")) {
            loadRecentInvoices(7);
        } else if (selectedFilter.equals("Hoá đơn 30 ngày gần đây")) {
            loadRecentInvoices(30);
        } else {
            loadUserInvoices();
        }
    }

    private void loadRecentInvoices(int days) {
        try {
            // Show loading indicator first
            showLoadingMessage("Đang tải dữ liệu hóa đơn gần đây...");
            
            // Create a new thread to perform the loading
            SwingUtilities.invokeLater(() -> {
                try {
                    // Ensure the controller is initialized
                    if (invoiceController == null) {
                        invoiceController = new InvoiceController();
                    }
                    
                    System.out.println("CashierInvoicesPanel: Loading invoices from the last " + days + " days");
                    // Get recent invoices based on days parameter
                    List<Invoice> loadedInvoices;
                    if (days == 7) {
                        loadedInvoices = invoiceController.getInvoicesLast7Days();
                        System.out.println("Using existing method for 7 days");
                    } else {
                        loadedInvoices = invoiceController.getInvoicesLastNDays(days);
                        System.out.println("Using new method for " + days + " days");
                    }
                    
                    // Store the loaded invoices
                    invoices = loadedInvoices;
                    
                    // Update UI title
                    lblHeader.setText("DANH SÁCH HOÁ ĐƠN " + days + " NGÀY GẦN ĐÂY");
                    
                    System.out.println("CashierInvoicesPanel: Loaded " + 
                        (invoices != null ? invoices.size() : 0) + " recent invoices");
                    
                    if (invoices == null || invoices.isEmpty()) {
                        showEmptyMessage("Không có hóa đơn nào trong " + days + " ngày gần đây");
                    } else {
                        // Remove loading panel and refresh the data
                        refreshTableData(invoices);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading recent invoices: " + ex.getMessage());
                    ex.printStackTrace();
                    showErrorMessage("Lỗi khi tải dữ liệu: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in loadRecentInvoices: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Lỗi: " + e.getMessage());
        }
    }

    private void loadUserInvoices() {
        try {
            // Show loading indicator first
            showLoadingMessage("Đang tải danh sách hóa đơn của bạn...");
            
            // Perform the loading in a separate thread
            SwingUtilities.invokeLater(() -> {
                try {
                    // Get current user from session
                    User currentUser = SessionManager.getInstance().getLoggedInUser();
                    if (currentUser == null) {
                        showErrorMessage("Không thể xác định người dùng hiện tại");
                        return;
                    }

                    // Ensure controller is initialized
                    if (invoiceController == null) {
                        invoiceController = new InvoiceController();
                    }
                    
                    System.out.println("CashierInvoicesPanel: Loading invoices for user ID: " + 
                        currentUser.getUsersId());
                    
                    // Get user's invoices
                    invoices = invoiceController.getInvoicesByUserId(currentUser.getUsersId());
                    
                    // Update UI title
                    lblHeader.setText("DANH SÁCH HOÁ ĐƠN DO TÔI LẬP");
                    
                    System.out.println("CashierInvoicesPanel: Loaded " + 
                        (invoices != null ? invoices.size() : 0) + " user invoices");
                    
                    if (invoices == null || invoices.isEmpty()) {
                        showEmptyMessage("Bạn chưa có hóa đơn nào");
                    } else {
                        refreshTableData(invoices);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading user invoices: " + ex.getMessage());
                    ex.printStackTrace();
                    showErrorMessage("Lỗi khi tải dữ liệu: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error in loadUserInvoices: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Lỗi: " + e.getMessage());
        }
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
                new String[]{"Số HĐ", "Ngày tạo", "Người tạo", "Tổng tiền", "Trạng thái", "Hành động"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cho phép edit cột hành động
            }
        };

        tblInvoices.setModel(tableModel);

        // Style table
        tblInvoices.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblInvoices.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblInvoices.getTableHeader().setBackground(new Color(240, 240, 240));
        tblInvoices.getTableHeader().setForeground(new Color(33, 37, 41));
        tblInvoices.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));
        tblInvoices.setRowHeight(35);
        tblInvoices.setGridColor(new Color(242, 242, 242));
        tblInvoices.setSelectionBackground(new Color(232, 241, 249));
        tblInvoices.setSelectionForeground(Color.BLACK);
        tblInvoices.setShowVerticalLines(false);

        // Giảm kích thước cột hành động
        TableColumn actionColumn = tblInvoices.getColumnModel().getColumn(5);
        actionColumn.setMinWidth(100);
        actionColumn.setMaxWidth(100);
        actionColumn.setPreferredWidth(100);

        // Style cho các dòng xen kẽ
        tblInvoices.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(207, 226, 243)); // Light blue khi select
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255) : new Color(249, 249, 249));
                    c.setForeground(Color.BLACK);
                }

                // Căn chỉnh các cột
                if (column == 0) { // Số hoá đơn - căn giữa
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 1) { // Ngày tạo - căn giữa
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 2) { // Người tạo - căn giữa
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 3) { // Tổng tiền - căn phải
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                } else if (column == 4) { // Trạng thái - căn giữa
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    
                    // Style for payment status
                    if (value != null) {
                        String status = value.toString();
                        if (status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Đã thanh toán")) {
                            c.setForeground(new Color(40, 167, 69)); // Green for paid
                        } else if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Chờ thanh toán")) {
                            c.setForeground(new Color(255, 193, 7)); // Yellow for pending
                        } else if (status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Đã huỷ")) {
                            c.setForeground(new Color(220, 53, 69)); // Red for cancelled
                        }
                    }
                }

                // Add more padding to cells
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                
                return c;
            }
        });

        // Add button column for actions
        TableColumn actionCol = tblInvoices.getColumnModel().getColumn(5);
        actionCol.setCellRenderer(new ButtonRenderer());
        actionCol.setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void refreshTableData(List<Invoice> invoicesToShow) {
        // Stop loading timer if exists
        Timer timer = (Timer) panelInvoiceTable.getClientProperty("loadingTimer");
        if (timer != null) {
            timer.stop();
            panelInvoiceTable.putClientProperty("loadingTimer", null);
        }
        
        // Clear existing rows
        tableModel.setRowCount(0);

        if (invoicesToShow == null || invoicesToShow.isEmpty()) {
            showEmptyMessage("Không có dữ liệu hóa đơn");
            return;
        }

        // Calculate pagination
        int start = (currentPage - 1) * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, invoicesToShow.size());
        
        // Ensure start index is valid
        if (start >= invoicesToShow.size()) {
            currentPage = 1;
            start = 0;
            end = Math.min(ROWS_PER_PAGE, invoicesToShow.size());
        }
        
        List<Invoice> pageInvoices = invoicesToShow.subList(start, end);

        // Update pagination controls
        updatePaginationControls(invoicesToShow.size());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

        // Initialize UserController if needed
        if (userController == null) {
            userController = new UserController();
        }
        
        System.out.println("CashierInvoicesPanel: Showing invoices " + (start + 1) + " to " + end + 
                         " of " + invoicesToShow.size());

        for (Invoice invoice : pageInvoices) {
            try {
                // Get creator name from the users table
                String creatorName = "Unknown";
                User creator = userController.getUserById(invoice.getUsersId());
                if (creator != null) {
                    creatorName = creator.getUsername();
                }
                
                String formattedTotal;
                try {
                    formattedTotal = currencyFormat.format(invoice.getTotal());
                } catch (Exception ex) {
                    formattedTotal = "0 VNĐ";
                    System.err.println("Error formatting invoice total: " + ex.getMessage());
                }
                
                tableModel.addRow(new Object[]{
                    "#" + invoice.getInvoiceId(),
                    invoice.getCreatedAt() != null ? dateFormat.format(invoice.getCreatedAt()) : "N/A",
                    creatorName,
                    formattedTotal,
                    "Đã thanh toán",
                    "Chi tiết"
                });
            } catch (Exception e) {
                System.err.println("Error adding invoice to table: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Show the table
        panelInvoiceTable.setViewportView(tblInvoices);
        revalidate();
        repaint();
    }

    private void updatePaginationControls(int totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / ROWS_PER_PAGE);
        pageInfoLabel.setText(String.format("Trang %d/%d", currentPage, totalPages));
        
        // Update button states and styles
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
        
        // Adjust button appearance based on enabled state
        if (!prevButton.isEnabled()) {
            prevButton.setBackground(new Color(200, 200, 200)); // Lighter gray when disabled
            prevButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            prevButton.setForeground(Color.WHITE); // Keep text white for disabled state
        } else {
            prevButton.setBackground(new Color(0, 123, 255)); // Restore color when enabled
            prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            prevButton.setForeground(Color.WHITE); // Ensure text is white
        }
        
        if (!nextButton.isEnabled()) {
            nextButton.setBackground(new Color(200, 200, 200)); // Lighter gray when disabled
            nextButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            nextButton.setForeground(Color.WHITE); // Keep text white for disabled state
        } else {
            nextButton.setBackground(new Color(0, 123, 255)); // Restore color when enabled
            nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            nextButton.setForeground(Color.WHITE); // Ensure text is white
        }
    }

    private int getTotalPages(List<Invoice> items) {
        if (items == null || items.isEmpty()) return 0;
        return (int) Math.ceil((double) items.size() / ROWS_PER_PAGE);
    }

    private void showLoadingMessage(String message) {
        // Clear current table
        tableModel.setRowCount(0);
        
        // Create loading panel
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Color.WHITE);
        
        JLabel loadingLabel = new JLabel(message, SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loadingPanel.add(loadingLabel, BorderLayout.CENTER);
        
        // Create a "spinner" or activity indicator
        JPanel spinnerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(centerX, centerY) - 5;
                
                g2d.setColor(new Color(0, 123, 255));
                g2d.setStroke(new BasicStroke(3.0f));
                
                // Get current time in milliseconds for rotation
                long now = System.currentTimeMillis() % 3600000;
                float angle = (now % 2000) / 2000f * 360f;
                
                // Draw arc
                g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 
                           (int)angle, 270);
            }
        };
        spinnerPanel.setPreferredSize(new Dimension(50, 50));
        spinnerPanel.setBackground(Color.WHITE);
        
        // Add spinner above loading label
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(spinnerPanel, BorderLayout.CENTER);
        centerPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        loadingPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(loadingPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Replace the current view
        panelInvoiceTable.setViewportView(scrollPane);
        
        // Start animation timer
        Timer timer = new Timer(50, e -> spinnerPanel.repaint());
        timer.start();
        
        // Store timer as client property so it can be stopped later
        panelInvoiceTable.putClientProperty("loadingTimer", timer);
    }

    private void showEmptyMessage(String message) {
        // Stop loading timer if exists
        Timer timer = (Timer) panelInvoiceTable.getClientProperty("loadingTimer");
        if (timer != null) {
            timer.stop();
            panelInvoiceTable.putClientProperty("loadingTimer", null);
        }
        
        // Create empty state panel
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setBackground(Color.WHITE);
        
        JLabel emptyLabel = new JLabel(message, SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emptyLabel.setForeground(new Color(108, 117, 125));
        
        // Create icon for empty state
        JLabel iconLabel = new JLabel();
        try {
            // Try to load an icon
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/icons/empty-box.png"));
            if (icon != null) {
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
                iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            // Just use text if icon fails
            iconLabel.setText("📃");
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setForeground(new Color(200, 200, 200));
        }
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(iconLabel, BorderLayout.CENTER);
        centerPanel.add(emptyLabel, BorderLayout.SOUTH);
        
        emptyPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(emptyPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Replace the current view
        panelInvoiceTable.setViewportView(scrollPane);
        
        // Disable pagination buttons
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        pageInfoLabel.setText("Trang 0/0");
    }

    private void showErrorMessage(String message) {
        // Stop loading timer if exists
        Timer timer = (Timer) panelInvoiceTable.getClientProperty("loadingTimer");
        if (timer != null) {
            timer.stop();
            panelInvoiceTable.putClientProperty("loadingTimer", null);
        }
        
        // Create error panel
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Color.WHITE);
        
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setForeground(new Color(220, 53, 69));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(new Color(220, 53, 69));
        
        JButton retryButton = createStyledButton("Thử lại", new Color(0, 123, 255));
        retryButton.addActionListener(e -> loadInvoicesBasedOnFilter());
        
        centerPanel.add(iconLabel, BorderLayout.NORTH);
        centerPanel.add(errorLabel, BorderLayout.CENTER);
        centerPanel.add(retryButton, BorderLayout.SOUTH);
        
        errorPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(errorPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Replace the current view
        panelInvoiceTable.setViewportView(scrollPane);
        
        // Disable pagination buttons
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        pageInfoLabel.setText("Trang 0/0");
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

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Get invoice information
        Invoice invoice = invoiceController.getInvoiceById(invoiceId);
        User creator = null;
        
        if (invoice != null) {
            if (userController == null) {
                userController = new UserController();
            }
            creator = userController.getUserById(invoice.getUsersId());
        }
        
        // Create title with invoice ID and creator info
        JLabel titleLabel = new JLabel("Chi tiết hóa đơn #" + invoiceId);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Add creator info panel to the right
        if (creator != null) {
            JPanel creatorInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            creatorInfoPanel.setBackground(Color.WHITE);
            
            JLabel creatorLabel = new JLabel("Người tạo: " + creator.getUsername());
            creatorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            creatorLabel.setForeground(new Color(108, 117, 125));
            
            creatorInfoPanel.add(creatorLabel);
            headerPanel.add(creatorInfoPanel, BorderLayout.EAST);
        }
        
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
        detailTable.setGridColor(new Color(242, 242, 242));
        detailTable.setShowVerticalLines(false);
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTable.getTableHeader().setBackground(new Color(240, 240, 240));
        detailTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));

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
                    c.setBackground(new Color(207, 226, 243));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255) : new Color(249, 249, 249));
                    c.setForeground(Color.BLACK);
                }

                // Align quantity, price and total to right
                if (column == 2 || column == 3 || column == 4) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
                } else if (column == 0) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER); // Center STT column
                }

                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)));
        
        JLabel totalLabel = new JLabel("Tổng tiền: " + String.format("%,d VNĐ", (int)total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPanel.add(totalLabel);

        // Panel chứa nút ở dưới cùng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Nút xuất Excel
        JButton exportButton = createStyledButton("Xuất Excel", new Color(0, 123, 255));
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
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> detailDialog.dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);

        // Bottom panel combines total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        detailDialog.add(mainPanel);
        detailDialog.setVisible(true);
    }
    
    // Helper method specifically for pagination buttons with more padding and rounded corners
    private JButton createPaginationButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); // Ensure text is white for better contrast
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add extra padding for pagination buttons
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        // Add hover effect with proper text color maintenance
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Create a slightly darker shade (85% of original brightness)
                Color darker = new Color(
                    Math.max((int)(bgColor.getRed() * 0.85), 0),
                    Math.max((int)(bgColor.getGreen() * 0.85), 0),
                    Math.max((int)(bgColor.getBlue() * 0.85), 0)
                );
                button.setBackground(darker);
                button.setForeground(Color.WHITE); // Keep text white on hover
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(Color.WHITE); // Reset to white
            }
            
            // Enhanced look when pressed
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Even darker when pressed (70% of original)
                Color darker = new Color(
                    Math.max((int)(bgColor.getRed() * 0.7), 0),
                    Math.max((int)(bgColor.getGreen() * 0.7), 0),
                    Math.max((int)(bgColor.getBlue() * 0.7), 0)
                );
                button.setBackground(darker);
                button.setForeground(Color.WHITE); // Maintain white text
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Go back to hover state
                Color darker = new Color(
                    Math.max((int)(bgColor.getRed() * 0.85), 0),
                    Math.max((int)(bgColor.getGreen() * 0.85), 0),
                    Math.max((int)(bgColor.getBlue() * 0.85), 0)
                );
                button.setBackground(darker);
                button.setForeground(Color.WHITE); // Maintain white text
            }
        });
        
        return button;
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); // Ensure text is white for better contrast
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Improved hover effect with better visual appearance
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Create a slightly darker shade (85% of original brightness)
                Color darker = new Color(
                    Math.max((int)(bgColor.getRed() * 0.85), 0), 
                    Math.max((int)(bgColor.getGreen() * 0.85), 0), 
                    Math.max((int)(bgColor.getBlue() * 0.85), 0)
                );
                button.setBackground(darker);
                button.setForeground(Color.WHITE); // Keep text white on hover
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(Color.WHITE); // Reset to white
            }
        });
        
        return button;
    }
    
    // Add print functionality
    private void printInvoicesList() {
        try {
            // Check if there are invoices to print
            if (invoices == null || invoices.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không có hóa đơn nào để in!",
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a print-ready version of the table
            JTable printTable = new JTable(tblInvoices.getModel());
            
            // Set up print options
            boolean complete = printTable.print(
                JTable.PrintMode.FIT_WIDTH,           // Print mode
                new java.text.MessageFormat(lblHeader.getText()), // Header
                new java.text.MessageFormat("Trang {0}"),         // Footer
                true,                                 // Show print dialog
                null,                                 // Print request attributes
                true,                                 // Interactive
                null                                  // Print service
            );
            
            if (complete) {
                JOptionPane.showMessageDialog(this, 
                    "In danh sách hóa đơn thành công!",
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "In bị hủy hoặc gặp lỗi!",
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi in danh sách: " + e.getMessage(),
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom Button Renderer with modern style
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(23, 162, 184));
            setForeground(Color.WHITE); // Ensure text color is white for visibility
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Chi tiết");
            return this;
        }
    }

    // Custom Button Editor with modern style
    class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(23, 162, 184));
            button.setForeground(Color.WHITE); // Ensure text color is white for visibility
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(true);
            
            // Improved hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    Color baseColor = new Color(23, 162, 184);
                    // Create a slightly darker shade (85% of original brightness)
                    Color darker = new Color(
                        Math.max((int)(baseColor.getRed() * 0.85), 0),
                        Math.max((int)(baseColor.getGreen() * 0.85), 0),
                        Math.max((int)(baseColor.getBlue() * 0.85), 0)
                    );
                    button.setBackground(darker);
                    button.setForeground(Color.WHITE); // Maintain text color
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(23, 162, 184));
                    button.setForeground(Color.WHITE); // Reset text color
                }
            });
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
                try {
                    // Get invoice ID from the first column
                    int row = tblInvoices.getSelectedRow();
                    if (row >= 0) {
                        String invoiceIdStr = (String) tblInvoices.getValueAt(row, 0);
                        if (invoiceIdStr != null && invoiceIdStr.startsWith("#")) {
                            int invoiceId = Integer.parseInt(invoiceIdStr.substring(1)); // Remove '#' prefix
                            // Show invoice details in a dialog
                            showInvoiceDetails(invoiceId);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        CashierInvoicesPanel.this,
                        "Lỗi khi hiển thị chi tiết hóa đơn: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
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
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Số Hoá Đơn", "Title 2", "Ngày tạo", "Tổng tiền", "Trạng thái", "Hành động"
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

    // Add this method at the end of your class to ensure NetBeans can still work with the UI
    private void restoreNetBeansCompatibility() {
        // This empty method is a marker for NetBeans to recognize this class as a UI component
        // Don't modify the code between the <editor-fold> tags above
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JScrollPane panelInvoiceTable;
    private javax.swing.JTable tblInvoices;
    // End of variables declaration//GEN-END:variables
}