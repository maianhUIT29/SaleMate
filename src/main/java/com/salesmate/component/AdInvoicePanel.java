package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.salesmate.controller.DetailController;
import com.salesmate.controller.EmployeeController;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.ProductController;
import com.salesmate.controller.UserController;
import com.salesmate.model.Detail;
import com.salesmate.model.Employee;
import com.salesmate.model.Invoice;
import com.salesmate.model.Product;
import com.salesmate.model.User;
import static com.salesmate.utils.ColorPalette.BACKGROUND_COLOR;
import static com.salesmate.utils.ColorPalette.TEXT_COLOR;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExcelImporter;
import com.salesmate.utils.ExportDialog;
import com.toedter.calendar.JDateChooser;

public class AdInvoicePanel extends JPanel {
    private final InvoiceController controller = new InvoiceController();
    private final DetailController detailController = new DetailController();
    private final UserController userController = new UserController();
    private final ProductController productController = new ProductController();
    private final EmployeeController employeeController = new EmployeeController();
    private final DefaultTableModel tableModel;
    private final JTable invoiceTable;
    private JTextField searchField;
    private final JSpinner pageSpinner;
    private final JLabel totalPagesLabel;

    private Date filterFrom, filterTo;
    private String filterStatus = "All";
    private String currentSearch = "";
    private int currentPage = 1;
    private final int pageSize = 20;
    private int totalPages = 1;

    // Update color scheme to match Bootstrap colors
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255); // Bootstrap primary
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125); // Bootstrap secondary
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69); // Bootstrap success
    private static final Color DANGER_COLOR = new Color(220, 53, 69); // Bootstrap danger
    private static final Color WARNING_COLOR = new Color(255, 193, 7); // Bootstrap warning
    private static final Color INFO_COLOR = new Color(23, 162, 184); // Bootstrap info
    private static final Color LIGHT_COLOR = new Color(248, 249, 250); // Bootstrap light
    private static final Color DARK_COLOR = new Color(52, 58, 64); // Bootstrap dark
    private static final Color WHITE_COLOR = new Color(255, 255, 255); // White
    private static final Color BORDER_COLOR = new Color(222, 226, 230); // Bootstrap border
    private static final Color CARD_COLOR = new Color(255, 255, 255); // Card background color
    private static final Color ACCENT_COLOR = new Color(220, 53, 69); // Accent color for highlights
    private static final Color IMPORT_COLOR = new Color(50, 150, 50); // Import button color
    private static final Color EXPORT_COLOR = new Color(0, 150, 136); // Export button color
    private static final Color ADD_COLOR = new Color(0, 123, 255); // Add button color
    private static final Color HEADER_COLOR = new Color(33, 37, 41); // Header color
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125); // Secondary text color
    private static final Color LIGHT_TEXT = new Color(248, 249, 250); // Light text color

    public AdInvoicePanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with modern styling
        JLabel title = new JLabel("Quản lý hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(PRIMARY_COLOR);

        // Create a header panel with shadow effect
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.add(title, BorderLayout.WEST);
        headerPanel.add(header, BorderLayout.CENTER);

        // Toolbar buttons with icons - now with Add button first
        JButton addBtn = createIconButton("Thêm hóa đơn", ADD_COLOR, "/icons/add.png");
        JButton editBtn = createIconButton("Sửa", SECONDARY_COLOR, "/icons/edit.png");
        JButton deleteBtn = createIconButton("Xóa", ACCENT_COLOR, "/icons/delete.png");
        JButton exportBtn = createIconButton("Xuất Excel", EXPORT_COLOR, "/icons/export.png");
        JButton importBtn = createIconButton("Nhập Excel", IMPORT_COLOR, "/icons/import.png");
        JButton refreshBtn = createIconButton("Làm mới", new Color(55, 71, 79), "/icons/refresh.png");

        // Create button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(CARD_COLOR);
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(importBtn);
        btnPanel.add(refreshBtn);

        // Create filter panel (now separate from button panel)
        JPanel filterPanel = createFilterPanel();
        filterPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));

        // Main content panel containing buttons and filters stacked vertically
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.add(btnPanel);
        contentPanel.add(filterPanel);

        headerPanel.add(contentPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Initialize pagination spinner
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        JComponent editor = pageSpinner.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        spinnerEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSpinner.setPreferredSize(new Dimension(70, 38));

        totalPagesLabel = new JLabel(" / 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPagesLabel.setForeground(TEXT_COLOR);

        // Table setup with modern styling
        String[] cols = { "Mã hóa đơn", "Người lập hóa đơn", "Ngày", "Tổng tiền", "Trạng thái", "Chi tiết" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };

        invoiceTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                if (col == 4) { // Status column
                    String status = (String) getValueAt(row, col);
                    if ("Paid".equals(status)) {
                        comp.setForeground(SUCCESS_COLOR);
                    } else {
                        comp.setForeground(WARNING_COLOR);
                    }
                } else {
                    comp.setForeground(TEXT_COLOR);
                }
                return comp;
            }
        };

        invoiceTable.setRowHeight(46);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        invoiceTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Cho phép chọn nhiều dòng
        invoiceTable.setAutoCreateRowSorter(true);
        invoiceTable.setShowGrid(false);
        invoiceTable.setIntercellSpacing(new Dimension(0, 0));

        // Style table header and rows
        styleModernTable(invoiceTable);

        // Detail button column
        invoiceTable.getColumnModel().getColumn(5).setCellRenderer(new ModernButtonRenderer());
        invoiceTable.getColumnModel().getColumn(5).setCellEditor(new ModernButtonEditor(new JCheckBox()));

        // Create table panel with shadow border
        JScrollPane tableScroll = new JScrollPane(invoiceTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(CARD_COLOR);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(new CompoundBorder(
                new ShadowBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Pagination controls with modern styling
        JPanel paginationPanel = createPaginationPanel();
        add(paginationPanel, BorderLayout.SOUTH);

        // Add listeners
        addBtn.addActionListener(e -> showAddInvoiceDialog());
        deleteBtn.addActionListener(e -> deleteInvoice());
        refreshBtn.addActionListener(e -> loadInvoices());
        exportBtn.addActionListener(e -> exportToExcel());
        importBtn.addActionListener(e -> importFromExcel());

        // UI Manager settings to fix combo box rendering
        UIManager.put("ComboBox.background", WHITE_COLOR);
        UIManager.put("ComboBox.foreground", TEXT_COLOR);
        UIManager.put("ComboBox.selectionBackground", PRIMARY_COLOR);
        UIManager.put("ComboBox.selectionForeground", WHITE_COLOR);
        UIManager.put("ComboBox.buttonBackground", WHITE_COLOR);
        UIManager.put("ComboBox.buttonDarkShadow", BORDER_COLOR);
        UIManager.put("ComboBox.buttonHighlight", WHITE_COLOR);
        UIManager.put("ComboBox.buttonShadow", BORDER_COLOR);
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("ComboBox.rendererUseListColors", Boolean.FALSE);

        // Initial load
        loadInvoices();
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterPanel.setBackground(WHITE_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Date choosers with increased width
        JDateChooser fromDate = new JDateChooser();
        fromDate.setPreferredSize(new Dimension(180, 38)); // Increased from 140
        customizeDateChooser(fromDate);

        JDateChooser toDate = new JDateChooser();
        toDate.setPreferredSize(new Dimension(180, 38)); // Increased from 140
        customizeDateChooser(toDate);

        // Status filter styling
        JComboBox<String> statusFilter = new JComboBox<>(new String[] { "All", "Paid", "Unpaid" });
        statusFilter.setPreferredSize(new Dimension(140, 38)); // Increased from 120
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setBackground(WHITE_COLOR);
        ((JComponent) statusFilter.getRenderer()).setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Apply filter button
        JButton applyFilter = createStyledButton("Áp dụng", PRIMARY_COLOR);
        applyFilter.setPreferredSize(new Dimension(100, 38));

        // Add components with spacing
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Từ:"));
        filterPanel.add(fromDate);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(new JLabel("Đến:"));
        filterPanel.add(toDate);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(applyFilter);

        return filterPanel;
    }

    private JPanel createPaginationPanel() {
        JPanel paging = new JPanel();
        paging.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paging.setBackground(CARD_COLOR);
        paging.setBorder(new CompoundBorder(
                new ShadowBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Change pagination button colors and styling
        JButton prev = createGradientButton("« Trước", INFO_COLOR);
        JButton next = createGradientButton("Sau »", INFO_COLOR);

        // Make buttons more prominent
        prev.setPreferredSize(new Dimension(100, 35));
        next.setPreferredSize(new Dimension(100, 35));

        prev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageSpinner.setValue(currentPage);
                loadInvoices();
            }
        });

        next.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                pageSpinner.setValue(currentPage);
                loadInvoices();
            }
        });

        pageSpinner.addChangeListener(e -> {
            currentPage = (int) pageSpinner.getValue();
            loadInvoices();
        });

        JLabel pageLabel = new JLabel("Trang:");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pageLabel.setForeground(TEXT_COLOR);

        paging.add(prev);
        paging.add(pageLabel);
        paging.add(pageSpinner);
        paging.add(totalPagesLabel);
        paging.add(next);

        return paging;
    }

    private void loadInvoices() {
        tableModel.setRowCount(0);
        List<Invoice> all = controller.getAllInvoices();
        List<Invoice> filtered = all.stream().filter(inv -> {
            boolean m1 = String.valueOf(inv.getInvoiceId()).contains(currentSearch)
                    || inv.getPaymentStatus().toLowerCase().contains(currentSearch.toLowerCase());
            boolean m2 = filterStatus.equals("All") || inv.getPaymentStatus().equals(filterStatus);
            boolean m3 = true;
            if (filterFrom != null)
                m3 = !inv.getCreatedAt().before(filterFrom);
            if (m3 && filterTo != null)
                m3 = !inv.getCreatedAt().after(filterTo);
            return m1 && m2 && m3;
        }).collect(Collectors.toList());

        int total = filtered.size();
        totalPages = Math.max((int) Math.ceil((double) total / pageSize), 1);
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        for (Invoice inv : filtered.subList(start, end)) {
            // Lấy tên người lập tương ứng cho từng hóa đơn
            User u = userController.getUserById(inv.getUsersId());
            String username = (u != null ? u.getUsername() : "—");

            tableModel.addRow(new Object[] {
                    inv.getInvoiceId(),
                    username,
                    inv.getCreatedAt(),
                    inv.getTotal(),
                    inv.getPaymentStatus(),
                    "Xem chi tiết"
            });
        }

        ((TableRowSorter<?>) invoiceTable.getRowSorter())
                .setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    }

    private void populateDetailTable(DefaultTableModel model, List<Detail> details) {
        model.setRowCount(0);
        for (Detail detail : details) {
            String productName = productController.getProductNameById(detail.getProductId());
            if (productName == null)
                productName = "Sản phẩm không tồn tại";

            model.addRow(new Object[] {
                    detail.getDetailId(),
                    detail.getProductId(),
                    productName,
                    detail.getQuantity(),
                    detail.getPrice(),
                    detail.getTotal()
            });
        }
    }

    private JPanel createMainContentPanel(JTable detailTable, DefaultTableModel detailModel,
            List<Detail> originalDetails) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Tạo toolbar với enhanced styling
        JPanel toolbarPanel = createToolbar();

        // Tạo search panel với improved UX
        JPanel searchPanel = createSearchPanel(detailTable, detailModel, originalDetails);

        // Enhanced table container with better visual hierarchy
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // Table with enhanced scroll pane
        JScrollPane tableScrollPane = new JScrollPane(detailTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(LIGHT_COLOR);

        // Custom scroll bar styling for modern look
        tableScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(189, 195, 199);
                this.trackColor = new Color(248, 249, 250);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        // Enhanced table header with statistics
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setBackground(Color.WHITE);
        tableHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel tableTitle = new JLabel("📋 Danh sách sản phẩm trong hóa đơn");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(DARK_COLOR);

        // Statistics panel on the right
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        JLabel itemCountLabel = new JLabel("Tổng: " + detailModel.getRowCount() + " mặt hàng");
        itemCountLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        itemCountLabel.setForeground(TEXT_SECONDARY);
        itemCountLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(233, 236, 239), 12),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        // Store reference for updating item count
        tableContainer.putClientProperty("itemCountLabel", itemCountLabel);

        statsPanel.add(itemCountLabel);

        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(statsPanel, BorderLayout.EAST);

        tableContainer.add(tableHeaderPanel, BorderLayout.NORTH);
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);

        // Main content card with enhanced styling
        JPanel contentCard = new JPanel(new BorderLayout());
        contentCard.setBackground(Color.WHITE);
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // Top section with toolbar and search
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(toolbarPanel, BorderLayout.NORTH);
        topSection.add(searchPanel, BorderLayout.SOUTH);

        contentCard.add(topSection, BorderLayout.NORTH);
        contentCard.add(tableContainer, BorderLayout.CENTER);

        mainPanel.add(contentCard, BorderLayout.CENTER);

        // Add table model listener to update statistics
        detailModel.addTableModelListener(e -> {
            JLabel countLabel = (JLabel) tableContainer.getClientProperty("itemCountLabel");
            if (countLabel != null) {
                countLabel.setText("Tổng: " + detailModel.getRowCount() + " mặt hàng");
            }

            // Update search panel result count
            JPanel searchPanelRef = (JPanel) topSection.getComponent(1);
            JLabel resultLabel = (JLabel) searchPanelRef.getClientProperty("resultLabel");
            if (resultLabel != null && detailModel.getRowCount() == originalDetails.size()) {
                resultLabel.setText("Hiển thị: " + originalDetails.size() + " sản phẩm");
                resultLabel.setForeground(new Color(134, 142, 150));
            }
        });

        return mainPanel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(LIGHT_COLOR);
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Chi tiết sản phẩm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addBtn = createIconButton("Thêm", SUCCESS_COLOR, "/icons/add.png");
        JButton deleteBtn = createIconButton("Xóa", DANGER_COLOR, "/icons/delete.png");

        addBtn.putClientProperty("action", "add");
        deleteBtn.putClientProperty("action", "delete");

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);

        toolbar.add(titleLabel, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        // Store button references for event handling
        toolbar.putClientProperty("addBtn", addBtn);
        toolbar.putClientProperty("deleteBtn", deleteBtn);

        return toolbar;
    }

    private JPanel createSearchPanel(JTable table, DefaultTableModel model, List<Detail> originalDetails) {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Left side - Search components
        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchLeft.setOpaque(false);

        JLabel searchLabel = new JLabel("🔍 Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_SECONDARY);

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(52, 152, 219), 8), // Blue rounded border
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        searchField.setBackground(new Color(250, 253, 255)); // Very light blue background

        // Add placeholder-like behavior
        searchField.setText("Nhập mã sản phẩm hoặc tên sản phẩm...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Nhập mã sản phẩm hoặc tên sản phẩm...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Nhập mã sản phẩm hoặc tên sản phẩm...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchBtn = createGradientButton("Tìm kiếm", new Color(52, 152, 219));
        searchBtn.setPreferredSize(new Dimension(110, 38));
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton clearBtn = createGradientButton("Xóa bộ lọc", new Color(108, 117, 125));
        clearBtn.setPreferredSize(new Dimension(100, 38));
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Right side - Info panel
        JPanel searchRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchRight.setOpaque(false);

        JLabel resultLabel = new JLabel("Hiển thị: " + originalDetails.size() + " sản phẩm");
        resultLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        resultLabel.setForeground(new Color(134, 142, 150));

        // Store reference for updating result count
        searchPanel.putClientProperty("resultLabel", resultLabel);

        // Xử lý tìm kiếm với real-time feedback
        Runnable performSearchAction = () -> {
            String searchText = searchField.getText().trim();
            if (searchText.equals("Nhập mã sản phẩm hoặc tên sản phẩm...")) {
                searchText = "";
            }

            model.setRowCount(0);
            int foundCount = 0;

            if (searchText.isEmpty()) {
                populateDetailTable(model, originalDetails);
                foundCount = originalDetails.size();
            } else {
                String lowerSearchText = searchText.toLowerCase();

                for (Detail detail : originalDetails) {
                    String productName = productController.getProductNameById(detail.getProductId());
                    if (productName == null)
                        productName = "Sản phẩm không tồn tại";

                    // Enhanced search - search by ID or name
                    if (String.valueOf(detail.getProductId()).contains(lowerSearchText) ||
                            productName.toLowerCase().contains(lowerSearchText)) {

                        model.addRow(new Object[] {
                                detail.getDetailId(),
                                detail.getProductId(),
                                productName,
                                detail.getQuantity(),
                                detail.getPrice(),
                                detail.getTotal()
                        });
                        foundCount++;
                    }
                }
            }

            // Update result count with animation-like effect
            resultLabel.setText("Hiển thị: " + foundCount + " / " + originalDetails.size() + " sản phẩm");
            resultLabel.setForeground(foundCount == 0 ? WARNING_COLOR : new Color(134, 142, 150));
        };

        // Real-time search as user types
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                // Delay search to avoid too many operations
                javax.swing.Timer searchTimer = new javax.swing.Timer(300, event -> performSearchAction.run());
                searchTimer.setRepeats(false);
                searchTimer.start();
            }
        });

        searchBtn.addActionListener(e -> performSearchAction.run());

        clearBtn.addActionListener(e -> {
            searchField.setText("Nhập mã sản phẩm hoặc tên sản phẩm...");
            searchField.setForeground(Color.GRAY);
            populateDetailTable(model, originalDetails);
            resultLabel.setText("Hiển thị: " + originalDetails.size() + " sản phẩm");
            resultLabel.setForeground(new Color(134, 142, 150));
        });

        searchField.addActionListener(e -> searchBtn.doClick());

        searchLeft.add(searchLabel);
        searchLeft.add(searchField);
        searchLeft.add(searchBtn);
        searchLeft.add(clearBtn);

        searchRight.add(resultLabel);

        searchPanel.add(searchLeft, BorderLayout.WEST);
        searchPanel.add(searchRight, BorderLayout.EAST);

        return searchPanel;
    }

    private void performSearch(String searchText, DefaultTableModel model, List<Detail> originalDetails) {
        model.setRowCount(0);

        if (searchText == null || searchText.trim().isEmpty()) {
            populateDetailTable(model, originalDetails);
            return;
        }

        String lowerSearchText = searchText.toLowerCase().trim();

        for (Detail detail : originalDetails) {
            String productName = productController.getProductNameById(detail.getProductId());
            if (productName == null)
                productName = "Sản phẩm không tồn tại";

            // Tìm kiếm theo mã hoặc tên sản phẩm
            if (String.valueOf(detail.getProductId()).contains(lowerSearchText) ||
                    productName.toLowerCase().contains(lowerSearchText)) {

                model.addRow(new Object[] {
                        detail.getDetailId(),
                        detail.getProductId(),
                        productName,
                        detail.getQuantity(),
                        detail.getPrice(),
                        detail.getTotal()
                });
            }
        }
    }

    private JPanel createBottomPanel(Invoice invoice) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        // Panel tổng tiền
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setOpaque(false);

        JLabel totalLabel = new JLabel(
                "TỔNG CỘNG: " + NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(invoice.getTotal()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalPanel.add(totalLabel);

        // Panel nút hành động
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton saveBtn = createGradientButton("Lưu thay đổi", SUCCESS_COLOR);
        JButton cancelBtn = createGradientButton("Hủy", SECONDARY_COLOR);

        saveBtn.setPreferredSize(new Dimension(140, 40));
        cancelBtn.setPreferredSize(new Dimension(100, 40));

        saveBtn.putClientProperty("action", "save");
        cancelBtn.putClientProperty("action", "cancel");

        actionPanel.add(saveBtn);
        actionPanel.add(cancelBtn);

        bottomPanel.add(totalPanel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        // Store reference cho việc cập nhật tổng tiền
        bottomPanel.putClientProperty("totalLabel", totalLabel);

        return bottomPanel;
    }

    private void setupEditDialogEvents(JDialog dialog, JTable detailTable, DefaultTableModel detailModel,
            Invoice invoice, List<Detail> originalDetails, JPanel bottomPanel, int invoiceId) {

        // Lưu trữ các thay đổi
        List<Detail> toAdd = new ArrayList<>();
        List<Detail> toUpdate = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();

        // Get button references from components
        JPanel mainContent = (JPanel) dialog.getContentPane().getComponent(1); // CENTER component
        JPanel toolbarPanel = null;

        // Find toolbar panel that contains buttons
        for (Component comp : mainContent.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getClientProperty("addBtn") != null) {
                    toolbarPanel = panel;
                    break;
                }
            }
        }

        // Get button references
        JButton addBtn = null;
        JButton editBtn = null;
        JButton deleteBtn = null;
        JButton saveBtn = null;
        JButton cancelBtn = null;

        if (toolbarPanel != null) {
            addBtn = (JButton) toolbarPanel.getClientProperty("addBtn");
            editBtn = (JButton) toolbarPanel.getClientProperty("editBtn");
            deleteBtn = (JButton) toolbarPanel.getClientProperty("deleteBtn");
        }

        // Get save/cancel buttons from bottom panel
        for (Component comp : bottomPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel actionPanel = (JPanel) comp;
                for (Component button : actionPanel.getComponents()) {
                    if (button instanceof JButton) {
                        JButton btn = (JButton) button;
                        String action = (String) btn.getClientProperty("action");
                        if ("save".equals(action)) {
                            saveBtn = btn;
                        } else if ("cancel".equals(action)) {
                            cancelBtn = btn;
                        }
                    }
                }
            }
        }

        // Get status combo from header
        JPanel headerPanel = (JPanel) dialog.getContentPane().getComponent(0); // NORTH component
        JComboBox<String> statusCombo = (JComboBox<String>) headerPanel.getClientProperty("statusCombo");

        // Hàm tính lại tổng tiền
        Runnable recalcTotal = () -> {
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                BigDecimal rowTotal = (BigDecimal) detailModel.getValueAt(i, 5);
                if (rowTotal != null) {
                    total = total.add(rowTotal);
                }
            }
            invoice.setTotal(total);

            JLabel totalLabel = (JLabel) bottomPanel.getClientProperty("totalLabel");
            if (totalLabel != null) {
                totalLabel.setText(
                        "TỔNG CỘNG: " + NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(total));
            }
        };

        // === XỬ LÝ NÚT THÊM ===
        if (addBtn != null) {
            addBtn.addActionListener(e -> {
                JDialog dlg = new JDialog(dialog, "Thêm sản phẩm vào hóa đơn", true);
                dlg.setLayout(new BorderLayout());
                dlg.getContentPane().setBackground(BACKGROUND_COLOR);

                // Header panel
                JPanel dlgHeader = new JPanel();
                dlgHeader.setBackground(SUCCESS_COLOR);
                dlgHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                dlgHeader.setLayout(new BorderLayout());

                JLabel dlgTitle = new JLabel("Thêm sản phẩm vào hóa đơn #" + invoiceId);
                dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
                dlgTitle.setForeground(Color.WHITE);
                dlgHeader.add(dlgTitle, BorderLayout.CENTER);

                dlg.add(dlgHeader, BorderLayout.NORTH);

                // Form panel
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBackground(WHITE_COLOR);
                formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.weightx = 0;

                // Lấy danh sách sản phẩm từ controller
                List<Product> products = productController.getAllProducts();

                // Tạo combobox product
                JComboBox<String> productCombo = new JComboBox<>();
                for (Product p : products) {
                    productCombo.addItem(p.getProductId() + ": " + p.getProductName());
                }
                productCombo.setPreferredSize(new Dimension(0, 38));
                productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                // Product selection label
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel productLabel = createStyledLabel("Sản phẩm:");
                formPanel.add(productLabel, gbc);

                gbc.gridx = 1;
                gbc.weightx = 1.0;
                formPanel.add(productCombo, gbc);

                // Quantity spinner with styling
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 0;
                JLabel qtyLabel = createStyledLabel("Số lượng:");
                formPanel.add(qtyLabel, gbc);

                gbc.gridx = 1;
                gbc.weightx = 1.0;
                JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
                qtySpin.setPreferredSize(new Dimension(0, 38));
                JComponent spinnerEditor = qtySpin.getEditor();
                JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinnerEditor;
                editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
                formPanel.add(qtySpin, gbc);

                // Price field with styling
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.weightx = 0;
                JLabel priceLabel = createStyledLabel("Đơn giá:");
                formPanel.add(priceLabel, gbc);

                gbc.gridx = 1;
                gbc.weightx = 1.0;
                JTextField priceField = new JTextField(10);
                priceField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDER_COLOR, 8),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                priceField.setPreferredSize(new Dimension(0, 38));
                formPanel.add(priceField, gbc);

                // Total price (calculated) with styling
                gbc.gridx = 0;
                gbc.gridy = 3;
                JLabel totalFieldLabel = createStyledLabel("Thành tiền:");
                formPanel.add(totalFieldLabel, gbc);

                gbc.gridx = 1;
                JTextField totalItemField = new JTextField();
                totalItemField.setEditable(false);
                totalItemField.setFont(new Font("Segoe UI", Font.BOLD, 14));
                totalItemField.setBackground(new Color(245, 245, 245));
                totalItemField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(PRIMARY_COLOR, 8),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                totalItemField.setPreferredSize(new Dimension(0, 38));
                formPanel.add(totalItemField, gbc);

                dlg.add(formPanel, BorderLayout.CENTER);

                // Lắng nghe sự kiện thay đổi combobox
                productCombo.addActionListener(ev -> {
                    if (productCombo.getSelectedItem() != null) {
                        try {
                            String selected = (String) productCombo.getSelectedItem();
                            int pid = Integer.parseInt(selected.split(":")[0].trim());
                            Product product = productController.getProductById(pid);

                            if (product != null) {
                                priceField.setText(product.getPrice().toString());

                                // Update total
                                int qty = (int) qtySpin.getValue();
                                BigDecimal price = product.getPrice();
                                BigDecimal total = price.multiply(BigDecimal.valueOf(qty));
                                totalItemField.setText(total.toString());
                            }
                        } catch (Exception ex) {
                            priceField.setText("");
                            totalItemField.setText("");
                        }
                    }
                });

                // Điền dữ liệu ban đầu nếu có sản phẩm
                if (productCombo.getItemCount() > 0) {
                    productCombo.setSelectedIndex(0);
                }

                // Update total when quantity changes
                qtySpin.addChangeListener(ev -> {
                    try {
                        if (!priceField.getText().isEmpty()) {
                            BigDecimal price = new BigDecimal(priceField.getText());
                            int qty = (int) qtySpin.getValue();
                            BigDecimal total = price.multiply(BigDecimal.valueOf(qty));
                            totalItemField.setText(total.toString());
                        }
                    } catch (Exception ex) {
                        totalItemField.setText("");
                    }
                });

                // Update total when price changes
                priceField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        updateTotal(priceField, qtySpin, totalItemField);
                    }

                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        updateTotal(priceField, qtySpin, totalItemField);
                    }

                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        updateTotal(priceField, qtySpin, totalItemField);
                    }
                });

                // Action buttons panel
                JPanel actionBtn = new JPanel();
                actionBtn.setBackground(WHITE_COLOR);
                actionBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                actionBtn.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

                JButton saveItemBtn = createGradientButton("Thêm vào hóa đơn", SUCCESS_COLOR);
                JButton cancelItemBtn = createGradientButton("Hủy", ACCENT_COLOR);

                actionBtn.add(saveItemBtn);
                actionBtn.add(cancelItemBtn);

                dlg.add(actionBtn, BorderLayout.SOUTH);

                // Save button action
                saveItemBtn.addActionListener(ev -> {
                    try {
                        if (productCombo.getSelectedItem() == null) {
                            JOptionPane.showMessageDialog(dlg,
                                    "Vui lòng chọn sản phẩm trước",
                                    "Thiếu thông tin",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        String selected = (String) productCombo.getSelectedItem();
                        int pid = Integer.parseInt(selected.split(":")[0].trim());
                        String productName = selected.split(":")[1].trim();

                        int qty = (int) qtySpin.getValue();
                        BigDecimal price = new BigDecimal(priceField.getText().trim());
                        BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

                        // Add to model
                        detailModel.addRow(new Object[] {
                                0, // detailId = 0 for new items
                                pid,
                                productName,
                                qty,
                                price,
                                total
                        });

                        // Add to pending changes
                        Detail newDetail = new Detail(0, invoiceId, pid, qty, price, total);
                        toAdd.add(newDetail);

                        // Update invoice total
                        recalcTotal.run();
                        dlg.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dlg,
                                "Lỗi: " + ex.getMessage(),
                                "Lỗi khi thêm sản phẩm",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelItemBtn.addActionListener(ev -> dlg.dispose());

                dlg.setSize(500, 450);
                dlg.setLocationRelativeTo(dialog);
                dlg.setVisible(true);
            });

            // === XỬ LÝ NÚT SỬA ===
            if (editBtn != null) {
                editBtn.addActionListener(e -> {
                    int r = detailTable.getSelectedRow();
                    if (r < 0) {
                        JOptionPane.showMessageDialog(dialog,
                                "Vui lòng chọn sản phẩm cần sửa",
                                "Chưa chọn sản phẩm",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    int detailId = (int) detailModel.getValueAt(r, 0);
                    int oldPid = (int) detailModel.getValueAt(r, 1);
                    String productName = (String) detailModel.getValueAt(r, 2);
                    int oldQty = (int) detailModel.getValueAt(r, 3);
                    BigDecimal oldPrice = (BigDecimal) detailModel.getValueAt(r, 4);

                    JDialog dlg = new JDialog(dialog, "Sửa sản phẩm", true);
                    dlg.setLayout(new BorderLayout());
                    dlg.getContentPane().setBackground(BACKGROUND_COLOR);

                    // Header panel
                    JPanel dlgHeader = new JPanel();
                    dlgHeader.setBackground(INFO_COLOR);
                    dlgHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                    dlgHeader.setLayout(new BorderLayout());

                    JLabel dlgTitle = new JLabel("Sửa sản phẩm trong hóa đơn");
                    dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    dlgTitle.setForeground(Color.WHITE);
                    dlgHeader.add(dlgTitle, BorderLayout.CENTER);

                    // Form panel
                    JPanel formPanel = new JPanel(new GridBagLayout());
                    formPanel.setBackground(WHITE_COLOR);
                    formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.insets = new Insets(10, 10, 10, 10);

                    // Lấy danh sách sản phẩm từ controller
                    List<Product> products = productController.getAllProducts();

                    // Tạo combobox product với product hiện tại đã được chọn
                    JComboBox<String> productCombo = new JComboBox<>();
                    int selectedIndex = 0;
                    int count = 0;

                    for (Product p : products) {
                        String item = p.getProductId() + ": " + p.getProductName();
                        productCombo.addItem(item);

                        if (p.getProductId() == oldPid) {
                            selectedIndex = count;
                        }
                        count++;
                    }

                    // Set selected product
                    if (productCombo.getItemCount() > 0) {
                        productCombo.setSelectedIndex(selectedIndex);
                    }

                    productCombo.setPreferredSize(new Dimension(0, 38));
                    productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                    // Product selection
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.weightx = 0;
                    JLabel productIdLabel = createStyledLabel("Sản phẩm:");
                    formPanel.add(productIdLabel, gbc);

                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    formPanel.add(productCombo, gbc);

                    // Số lượng
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.weightx = 0;
                    JLabel qtyLabel = createStyledLabel("Số lượng:");
                    formPanel.add(qtyLabel, gbc);

                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(oldQty, 1, 9999, 1));
                    qtySpin.setPreferredSize(new Dimension(0, 38));
                    JComponent spinnerEditor = qtySpin.getEditor();
                    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinnerEditor;
                    editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    formPanel.add(qtySpin, gbc);

                    // Đơn giá
                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    gbc.weightx = 0;
                    JLabel priceLabel = createStyledLabel("Đơn giá:");
                    formPanel.add(priceLabel, gbc);

                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JTextField priceField = new JTextField(oldPrice.toString());
                    priceField.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(BORDER_COLOR, 8),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                    priceField.setPreferredSize(new Dimension(0, 38));
                    formPanel.add(priceField, gbc);

                    // Thành tiền
                    gbc.gridx = 0;
                    gbc.gridy = 3;
                    gbc.weightx = 0;
                    JLabel totalItemLabel = createStyledLabel("Thành tiền:");
                    formPanel.add(totalItemLabel, gbc);

                    gbc.gridx = 1;
                    gbc.weightx = 1.0;
                    JTextField totalField = new JTextField(oldPrice.multiply(BigDecimal.valueOf(oldQty)).toString());
                    totalField.setEditable(false);
                    totalField.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    totalField.setBackground(new Color(245, 245, 245));
                    totalField.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(PRIMARY_COLOR, 8),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                    totalField.setPreferredSize(new Dimension(0, 38));
                    formPanel.add(totalField, gbc);

                    dlg.add(formPanel, BorderLayout.CENTER);

                    // Lắng nghe sự kiện thay đổi combobox
                    productCombo.addActionListener(ev -> {
                        if (productCombo.getSelectedItem() != null) {
                            try {
                                String selected = (String) productCombo.getSelectedItem();
                                int pid = Integer.parseInt(selected.split(":")[0].trim());
                                Product product = productController.getProductById(pid);

                                if (product != null && oldPid != pid) {
                                    // Chỉ cập nhật giá khi chọn sản phẩm mới
                                    priceField.setText(product.getPrice().toString());
                                }

                                // Update total when quantity or price changes
                                qtySpin.addChangeListener(ev2 -> updateTotal(priceField, qtySpin, totalField));
                                priceField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                        updateTotal(priceField, qtySpin, totalField);
                                    }

                                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                        updateTotal(priceField, qtySpin, totalField);
                                    }

                                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                        updateTotal(priceField, qtySpin, totalField);
                                    }
                                });
                            } catch (Exception ex) {
                                priceField.setText("");
                            }
                        }
                    });

                    // Action buttons panel
                    JPanel actionBtn = new JPanel();
                    actionBtn.setBackground(WHITE_COLOR);
                    actionBtn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                    actionBtn.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

                    JButton saveItemBtn = createGradientButton("Lưu thay đổi", INFO_COLOR);
                    JButton cancelItemBtn = createGradientButton("Hủy", ACCENT_COLOR);

                    actionBtn.add(saveItemBtn);
                    actionBtn.add(cancelItemBtn);

                    dlg.add(actionBtn, BorderLayout.SOUTH);

                    // Save button action
                    saveItemBtn.addActionListener(ev -> {
                        try {
                            String selected = (String) productCombo.getSelectedItem();
                            int pid = Integer.parseInt(selected.split(":")[0].trim());
                            String newProductName = selected.split(":")[1].trim();

                            int qty = (int) qtySpin.getValue();
                            BigDecimal price = new BigDecimal(priceField.getText().trim());
                            BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

                            Detail d = new Detail(detailId, invoiceId, pid, qty, price, total);

                            if (detailId == 0) {
                                // Cập nhật trong danh sách toAdd
                                toAdd.removeIf(x -> x.getDetailId() == 0 && x.getProductId() == oldPid);
                                toAdd.add(d);
                            } else {
                                toUpdate.removeIf(x -> x.getDetailId() == detailId);
                                toUpdate.add(d);
                            }

                            // Cập nhật lại model
                            detailModel.setValueAt(pid, r, 1);
                            detailModel.setValueAt(newProductName, r, 2);
                            detailModel.setValueAt(qty, r, 3);
                            detailModel.setValueAt(price, r, 4);
                            detailModel.setValueAt(total, r, 5);

                            recalcTotal.run();
                            dlg.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dlg,
                                    "Dữ liệu không hợp lệ: " + ex.getMessage(),
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    cancelItemBtn.addActionListener(ev -> dlg.dispose());

                    dlg.setSize(500, 450);
                    dlg.setLocationRelativeTo(dialog);
                    dlg.setVisible(true);
                });
            }

            // === XỬ LÝ NÚT XÓA ===
            if (deleteBtn != null) {
                deleteBtn.addActionListener(e -> {
                    int r = detailTable.getSelectedRow();
                    if (r < 0) {
                        JOptionPane.showMessageDialog(dialog,
                                "Vui lòng chọn sản phẩm cần xóa",
                                "Chưa chọn sản phẩm",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    int detailId = (int) detailModel.getValueAt(r, 0);
                    String productName = (String) detailModel.getValueAt(r, 2);

                    // Xác nhận xóa sản phẩm
                    int choice = JOptionPane.showConfirmDialog(dialog,
                            "Bạn có chắc chắn muốn xóa sản phẩm:\n" + productName + " ?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        // Nếu detailId = 0 thì đây là sản phẩm mới thêm, chỉ cần xóa khỏi model
                        if (detailId == 0) {
                            detailModel.removeRow(r);
                            // Remove from toAdd list
                            int productId = (int) detailModel.getValueAt(r, 1);
                            toAdd.removeIf(d -> d.getProductId() == productId);
                        } else {
                            // Sản phẩm đã có trong DB, thêm vào danh sách xóa
                            toDelete.add(detailId);
                            detailModel.removeRow(r);
                            // Remove from toUpdate if exists
                            toUpdate.removeIf(d -> d.getDetailId() == detailId);
                        }
                        // Tính lại tổng tiền
                        recalcTotal.run();
                    }
                });
            }

            // === XỬ LÝ NÚT LƯU ===
            if (saveBtn != null) {
                saveBtn.addActionListener(e -> {
                    // Cập nhật trạng thái từ combobox
                    invoice.setPaymentStatus((String) statusCombo.getSelectedItem());

                    // Nếu xóa hết thì hiển thị xác nhận
                    if (detailModel.getRowCount() == 0) {
                        JDialog confirmEmptyDialog = new JDialog(dialog, "Cảnh báo", true);
                        confirmEmptyDialog.setLayout(new BorderLayout());
                        confirmEmptyDialog.getContentPane().setBackground(WHITE_COLOR);

                        JPanel confirmHeader = new JPanel();
                        confirmHeader.setBackground(WARNING_COLOR);
                        confirmHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                        confirmHeader.setLayout(new BorderLayout());

                        JLabel confirmTitle = new JLabel("Cảnh báo");
                        confirmTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
                        confirmTitle.setForeground(DARK_COLOR);
                        confirmHeader.add(confirmTitle, BorderLayout.CENTER);

                        confirmEmptyDialog.add(confirmHeader, BorderLayout.NORTH);

                        JPanel confirmContent = new JPanel();
                        confirmContent.setBackground(WHITE_COLOR);
                        confirmContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
                        confirmContent.setLayout(new BorderLayout());

                        JLabel confirmMessage = new JLabel(
                                "<html>Hóa đơn không có sản phẩm nào.<br>" +
                                        "Hóa đơn trống sẽ bị <b>xóa hoàn toàn</b>.<br><br>" +
                                        "Bạn có muốn tiếp tục?</html>");
                        confirmMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        confirmContent.add(confirmMessage, BorderLayout.CENTER);

                        confirmEmptyDialog.add(confirmContent, BorderLayout.CENTER);

                        JPanel confirmButtons = new JPanel();
                        confirmButtons.setBackground(WHITE_COLOR);
                        confirmButtons.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
                        confirmButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

                        JButton deleteButton = createGradientButton("Xóa hóa đơn", ACCENT_COLOR);
                        JButton cancelButton = createGradientButton("Hủy", SECONDARY_COLOR);

                        confirmButtons.add(deleteButton);
                        confirmButtons.add(cancelButton);

                        confirmEmptyDialog.add(confirmButtons, BorderLayout.SOUTH);

                        final boolean[] confirmed = { false };

                        deleteButton.addActionListener(ev -> {
                            confirmed[0] = true;
                            confirmEmptyDialog.dispose();
                        });

                        cancelButton.addActionListener(ev -> confirmEmptyDialog.dispose());

                        confirmEmptyDialog.setSize(400, 250);
                        confirmEmptyDialog.setLocationRelativeTo(dialog);
                        confirmEmptyDialog.setVisible(true);

                        if (confirmed[0]) {
                            controller.deleteInvoice(invoiceId);
                            JOptionPane.showMessageDialog(dialog,
                                    "Hóa đơn đã được xóa hoàn toàn!",
                                    "Hoàn tất",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadInvoices();
                            dialog.dispose();
                        }
                        return;
                    }

                    // Hiển thị dialog xác nhận lưu thay đổi
                    JDialog confirmDialog = new JDialog(dialog, "Xác nhận", true);
                    confirmDialog.setLayout(new BorderLayout());
                    confirmDialog.getContentPane().setBackground(WHITE_COLOR);

                    JPanel confirmHeader = new JPanel();
                    confirmHeader.setBackground(PRIMARY_COLOR);
                    confirmHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                    confirmHeader.setLayout(new BorderLayout());

                    JLabel confirmTitle = new JLabel("Xác nhận lưu thay đổi");
                    confirmTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    confirmTitle.setForeground(Color.WHITE);
                    confirmHeader.add(confirmTitle, BorderLayout.CENTER);

                    confirmDialog.add(confirmHeader, BorderLayout.NORTH);

                    JPanel confirmContent = new JPanel();
                    confirmContent.setBackground(WHITE_COLOR);
                    confirmContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
                    confirmContent.setLayout(new BorderLayout());

                    String changes = "";
                    if (!invoice.getPaymentStatus().equals(statusCombo.getSelectedItem()))
                        changes += "- Đổi trạng thái thành: " + statusCombo.getSelectedItem() + "<br>";
                    if (!toAdd.isEmpty())
                        changes += "- Thêm " + toAdd.size() + " sản phẩm mới<br>";
                    if (!toUpdate.isEmpty())
                        changes += "- Cập nhật " + toUpdate.size() + " sản phẩm<br";
                    if (!toDelete.isEmpty())
                        changes += "- Xóa " + toDelete.size() + " sản phẩm<br";

                    if (changes.isEmpty())
                        changes = "Không có thay đổi nào.";

                    JLabel confirmMessage = new JLabel("<html>Bạn có chắc chắn muốn lưu những thay đổi sau?<br><br>" +
                            changes + "</html>");
                    confirmMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    confirmContent.add(confirmMessage, BorderLayout.CENTER);

                    confirmDialog.add(confirmContent, BorderLayout.CENTER);

                    JPanel confirmButtons = new JPanel();
                    confirmButtons.setBackground(WHITE_COLOR);
                    confirmButtons.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                            BorderFactory.createEmptyBorder(15, 20, 15, 20)));
                    confirmButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

                    JButton saveConfirmBtn = createGradientButton("Lưu thay đổi", PRIMARY_COLOR);
                    JButton cancelConfirmBtn = createGradientButton("Hủy", SECONDARY_COLOR);

                    confirmButtons.add(saveConfirmBtn);
                    confirmButtons.add(cancelConfirmBtn);

                    confirmDialog.add(confirmButtons, BorderLayout.SOUTH);

                    final boolean[] confirmed = { false };

                    saveConfirmBtn.addActionListener(ev -> {
                        confirmed[0] = true;
                        confirmDialog.dispose();
                    });

                    cancelConfirmBtn.addActionListener(ev -> confirmDialog.dispose());

                    confirmDialog.setSize(450, 300);
                    confirmDialog.setLocationRelativeTo(dialog);
                    confirmDialog.setVisible(true);

                    // Xử lý lưu thay đổi
                    if (confirmed[0]) {
                        try {
                            // 1) Xóa chi tiết
                            for (int id : toDelete) {
                                detailController.deleteDetail(id);
                            }

                            // 2) Cập nhật chi tiết
                            for (Detail d : toUpdate) {
                                detailController.updateDetail(d);
                            }

                            // 3) Thêm chi tiết
                            for (Detail d : toAdd) {
                                detailController.addDetail(d);
                            }

                            // 4) Cập nhật tổng của hoá đơn và trạng thái
                            invoice.setPaymentStatus((String) statusCombo.getSelectedItem());
                            controller.updateInvoice(invoice);

                            // Show success message with animation
                            JDialog successDialog = new JDialog(dialog, "Thành công", true);
                            successDialog.setLayout(new BorderLayout());
                            successDialog.getContentPane().setBackground(WHITE_COLOR);

                            JPanel successHeader = new JPanel();
                            successHeader.setBackground(SUCCESS_COLOR);
                            successHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                            successHeader.setLayout(new BorderLayout());

                            JLabel successTitle = new JLabel("Thành công");
                            successTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
                            successTitle.setForeground(Color.WHITE);
                            successHeader.add(successTitle, BorderLayout.CENTER);

                            successDialog.add(successHeader, BorderLayout.NORTH);

                            JPanel successContent = new JPanel();
                            successContent.setBackground(WHITE_COLOR);
                            successContent.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
                            successContent.setLayout(new BorderLayout(0, 20));

                            JLabel successMessage = new JLabel(
                                    "<html><div style='text-align: center;'>Thay đổi đã được lưu thành công!<br>" +
                                            "Hóa đơn #" + invoiceId + " đã được cập nhật.</div></html>",
                                    JLabel.CENTER);
                            successMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                            JButton okButton = createGradientButton("OK", SUCCESS_COLOR);
                            okButton.setPreferredSize(new Dimension(100, 40));

                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                            buttonPanel.setBackground(WHITE_COLOR);
                            buttonPanel.add(okButton);

                            successContent.add(successMessage, BorderLayout.CENTER);
                            successContent.add(buttonPanel, BorderLayout.SOUTH);

                            successDialog.add(successContent, BorderLayout.CENTER);

                            okButton.addActionListener(ev -> {
                                successDialog.dispose();
                                dialog.dispose();
                                loadInvoices();
                            });

                            successDialog.setSize(350, 250);
                            successDialog.setLocationRelativeTo(dialog);
                            successDialog.setVisible(true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Lỗi khi lưu thay đổi: " + ex.getMessage(),
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }

            // Cancel button action
            if (cancelBtn != null) {
                cancelBtn.addActionListener(e -> dialog.dispose());
            }

            // Set dialog size and position
            dialog.setSize(900, 650);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    private void updateTotal(JTextField priceField, JSpinner qtySpin, JTextField totalField) {
        try {
            if (!priceField.getText().isEmpty()) {
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                int qty = (int) qtySpin.getValue();
                BigDecimal total = price.multiply(BigDecimal.valueOf(qty));
                totalField.setText(total.toString());
            }
        } catch (NumberFormatException e) {
            totalField.setText("");
        }
    }

    private void deleteInvoice() {
        // Get selected invoice
        int viewRow = invoiceTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xóa",
                    "Chưa chọn hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = invoiceTable.convertRowIndexToModel(viewRow);
        int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

        // Create modern confirmation dialog
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Xác nhận xóa", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.getContentPane().setBackground(WHITE_COLOR);

        // Header panel with warning icon
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setBackground(DANGER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        // Load warning icon with error handling
        try {
            ImageIcon warningIcon = new ImageIcon(getClass().getResource("/icons/warning.png"));
            if (warningIcon != null && warningIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                JLabel iconLabel = new JLabel(warningIcon);
                headerPanel.add(iconLabel);
            } else {
                // Fallback to text-only warning if icon fails to load
                JLabel fallbackLabel = new JLabel("⚠️");
                fallbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
                fallbackLabel.setForeground(Color.WHITE);
                headerPanel.add(fallbackLabel);
            }
        } catch (Exception e) {
            // Log error and continue without icon
            System.err.println("Error loading warning icon: " + e.getMessage());
            // Use text fallback
            JLabel fallbackLabel = new JLabel("⚠️");
            fallbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            fallbackLabel.setForeground(Color.WHITE);
            headerPanel.add(fallbackLabel);
        }

        JLabel titleLabel = new JLabel("Xác nhận xóa hóa đơn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(WHITE_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));
        contentPanel.setLayout(new BorderLayout(0, 15));

        JLabel msgLabel = new JLabel(
                "<html><body style='width: 300px'>" +
                        "<div style='text-align: center; font-size: 14px;'>" +
                        "Bạn có chắc chắn muốn xóa hóa đơn #" + invoiceId + "?<br><br>" +
                        "<span style='color: #dc3545; font-weight: bold;'>" +
                        "Lưu ý: Tất cả chi tiết của hóa đơn cũng sẽ bị xóa.<br>" +
                        "Hành động này không thể hoàn tác.</span>" +
                        "</div></body></html>",
                JLabel.CENTER);
        contentPanel.add(msgLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(WHITE_COLOR);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton deleteButton = createGradientButton("Xóa hóa đơn", DANGER_COLOR);
        JButton cancelButton = createGradientButton("Hủy", SECONDARY_COLOR);

        deleteButton.setPreferredSize(new Dimension(130, 40));
        cancelButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        confirmDialog.add(headerPanel, BorderLayout.NORTH);
        confirmDialog.add(contentPanel, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add button actions
        deleteButton.addActionListener(e -> {
            try {
                // Show loading state
                deleteButton.setEnabled(false);
                deleteButton.setText("Đang xóa...");

                // Perform deletion in a separate thread to avoid UI blocking
                SwingUtilities.invokeLater(() -> {
                    boolean success = false;
                    String errorMessage = "Không xác định được lỗi";

                    try {
                        success = controller.deleteInvoice(invoiceId);
                        if (!success) {
                            errorMessage = "Không thể xóa hóa đơn. Có thể hóa đơn đang được sử dụng hoặc không tồn tại.";
                        }
                    } catch (Exception ex) {
                        success = false;
                        errorMessage = "Lỗi khi xóa hóa đơn: " + ex.getMessage();
                        ex.printStackTrace();
                    }

                    // Reset button state
                    deleteButton.setEnabled(true);
                    deleteButton.setText("Xóa hóa đơn");

                    // Close dialog first
                    confirmDialog.dispose();

                    // Show result
                    if (success) {
                        showSuccessDialog("Xóa hóa đơn thành công!");
                        // Refresh the invoice list
                        loadInvoices();
                    } else {
                        showErrorDialog("Lỗi: " + errorMessage);
                    }
                });
            } catch (Exception ex) {
                // Reset button state in case of immediate error
                deleteButton.setEnabled(true);
                deleteButton.setText("Xóa hóa đơn");
                confirmDialog.dispose();
                showErrorDialog("Lỗi không mong đợi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> confirmDialog.dispose());

        // Show dialog
        confirmDialog.setSize(400, 300);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setVisible(true);
    }

    private void showSuccessDialog(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thành công", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(WHITE_COLOR);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(SUCCESS_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Thành công");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(WHITE_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(msgLabel);

        JButton okButton = createGradientButton("OK", SUCCESS_COLOR);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> dialog.dispose());

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lỗi", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(WHITE_COLOR);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(DANGER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Lỗi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(WHITE_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(msgLabel);

        JButton okButton = createGradientButton("OK", DANGER_COLOR);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> dialog.dispose());

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Custom UI Components

    // Simple border for panels - Bootstrap style (no shadows)
    private static class ShadowBorder extends LineBorder {
        public ShadowBorder() {
            super(BORDER_COLOR, 1, false);
        }
    }

    // Rounded border for text fields
    private static class RoundedBorder extends LineBorder {
        private int radius;

        public RoundedBorder(Color color, int radius) {
            super(color);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getLineColor());
            g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2d.dispose();
        }
    }

    // Modern button renderer for table
    class ModernButtonRenderer extends JButton implements TableCellRenderer {
        ModernButtonRenderer() {
            setOpaque(true);
            setText("Xem chi tiết");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(LIGHT_TEXT);
            setBackground(SECONDARY_COLOR);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                setBackground(SECONDARY_COLOR.darker());
            } else {
                setBackground(SECONDARY_COLOR);
            }
            return this;
        }
    }

    // Modern button editor for table
    class ModernButtonEditor extends DefaultCellEditor {
        private int currentRow;
        private final JButton btn;

        ModernButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            btn = new JButton("Xem chi tiết");
            btn.setOpaque(true);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(LIGHT_TEXT);
            btn.setBackground(SECONDARY_COLOR);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> fireEditingStopped());

            // Add hover effect
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(SECONDARY_COLOR.darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(SECONDARY_COLOR);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentRow = row;
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            int invoiceId = (int) tableModel.getValueAt(currentRow, 0);
            // Use proper reference to outer class method
            showDetailDialog(invoiceId);
            return "Xem chi tiết";
        }
    }

    // Method to show invoice detail dialog
    private void showDetailDialog(int invoiceId) {
        Invoice invoice = controller.getInvoiceById(invoiceId);
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Detail> details = detailController.getDetailsByInvoiceId(invoiceId);

        // Create modern detail dialog with larger size
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn #" + invoiceId,
                true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Modern header panel with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), 0, new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📄");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel titleLabel = new JLabel("Chi tiết hóa đơn #" + invoiceId);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Enhanced status badge with rounded corners
        JLabel statusLabel = new JLabel(invoice.getPaymentStatus()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setOpaque(false);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        if ("Paid".equals(invoice.getPaymentStatus())) {
            statusLabel.setBackground(SUCCESS_COLOR);
            statusLabel.setForeground(Color.WHITE);
        } else {
            statusLabel.setBackground(WARNING_COLOR);
            statusLabel.setForeground(DARK_COLOR);
        }

        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Enhanced invoice info panel with cards layout
        JPanel infoMainPanel = new JPanel(new BorderLayout());
        infoMainPanel.setBackground(BACKGROUND_COLOR);
        infoMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Get employee/user who created the invoice
        User user = userController.getUserById(invoice.getUsersId());
        String username = (user != null) ? user.getUsername() : "—";

        // Date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateStr = sdf.format(invoice.getCreatedAt());

        // Info cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setBackground(BACKGROUND_COLOR);

        // Create info cards
        JPanel userCard = createInfoCard("👤", "Người lập", username);
        JPanel dateCard = createInfoCard("📅", "Ngày lập", dateStr);
        JPanel totalCard = createInfoCard("💰", "Tổng tiền", invoice.getTotal().toString() + " VND");
        JPanel itemsCard = createInfoCard("📦", "Số sản phẩm", String.valueOf(details.size()) + " mặt hàng");

        cardsPanel.add(userCard);
        cardsPanel.add(dateCard);
        cardsPanel.add(totalCard);
        cardsPanel.add(itemsCard);

        infoMainPanel.add(cardsPanel, BorderLayout.CENTER);

        // Enhanced details table with modern styling
        String[] cols = { "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá (VND)", "Thành tiền (VND)" };
        DefaultTableModel detailModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable detailTable = new JTable(detailModel);
        styleModernTable(detailTable);
        detailTable.setRowHeight(45);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Enhanced table header styling
        JTableHeader header = detailTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(236, 240, 241));
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(0, 50));

        // Add data to table with formatting
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));
        for (Detail detail : details) {
            String productName = productController.getProductNameById(detail.getProductId());
            if (productName == null)
                productName = "Sản phẩm không tồn tại";

            detailModel.addRow(new Object[] {
                    "#" + detail.getProductId(),
                    productName,
                    detail.getQuantity(),
                    currencyFormat.format(detail.getPrice()),
                    currencyFormat.format(detail.getTotal())
            });
        }

        // Enhanced scroll pane with modern borders
        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                new ShadowBorder()));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Table container with title
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(BACKGROUND_COLOR);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        JLabel tableTitle = new JLabel("Danh sách sản phẩm");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        tableContainer.add(tableTitle, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Enhanced button panel with multiple actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Export button
        JButton exportButton = createGradientButton("Xuất Excel", new Color(39, 174, 96));
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportButton.setPreferredSize(new Dimension(140, 45));
        exportButton.addActionListener(e -> {
            // Export functionality for invoice details
            ExportDialog exportDialog = new ExportDialog((Frame) SwingUtilities.getWindowAncestor(this), detailTable);
            exportDialog.setVisible(true);

            if (exportDialog.isExportConfirmed()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Lưu chi tiết hóa đơn");
                fileChooser.setSelectedFile(new File("ChiTiet_HoaDon_" + invoiceId + ".xlsx"));

                int userSelection = fileChooser.showSaveDialog(dialog);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        if (exportDialog.isXLSX()) {
                            ExcelExporter.exportToExcel(detailTable, fileToSave,
                                    exportDialog.includeHeaders(), exportDialog.getSelectedColumns());
                        } else {
                            ExcelExporter.exportToCSV(detailTable, fileToSave,
                                    exportDialog.includeHeaders(), exportDialog.getSelectedColumns());
                        }
                        JOptionPane.showMessageDialog(dialog,
                                "Xuất file thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Lỗi khi xuất file: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Close button
        JButton closeButton = createGradientButton("Đóng", SECONDARY_COLOR);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setPreferredSize(new Dimension(120, 45));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.add(infoMainPanel, BorderLayout.NORTH);
        mainContent.add(tableContainer, BorderLayout.CENTER);
        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainContent, BorderLayout.CENTER);

        // Show dialog with larger, more modern size
        dialog.setSize(1100, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Helper method to create info cards
    private JPanel createInfoCard(String icon, String label, String value) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Subtle shadow effect
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(0, 100));

        // Icon panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconPanel.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconPanel.add(iconLabel);

        // Text panel
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);
        labelText.setHorizontalAlignment(JLabel.CENTER);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueText.setForeground(TEXT_COLOR);
        valueText.setHorizontalAlignment(JLabel.CENTER);

        textPanel.add(labelText, BorderLayout.NORTH);
        textPanel.add(valueText, BorderLayout.CENTER);

        card.add(iconPanel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // Helper methods

    private void customizeDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(180, 38)); // Increased width from 140 to 180
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.getJCalendar().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.getDateEditor().getUiComponent().setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Increase calendar button size
        for (Component c : dateChooser.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setPreferredSize(new Dimension(40, 38));
                btn.setBackground(PRIMARY_COLOR);
                btn.setForeground(WHITE_COLOR);
            }
        }

        // Style the text field
        dateChooser.getDateEditor().getUiComponent().setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDER_COLOR, 18),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void styleModernTable(JTable table) {
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(WHITE_COLOR);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR.darker()),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                return label;
            }
        });

        // Row styling
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);

                if (isSelected) {
                    label.setBackground(new Color(232, 240, 254)); // Light blue selection
                    label.setForeground(PRIMARY_COLOR);
                } else {
                    label.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(248, 249, 250));
                    label.setForeground(TEXT_COLOR);
                }

                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                // Customize alignment based on column
                if (column == 0 || column == 2) { // ID and Date columns
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 3) { // Amount column
                    label.setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    label.setHorizontalAlignment(JLabel.LEFT);
                }

                return label;
            }
        });
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(WHITE_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        return button;
    }

    private JButton createIconButton(String text, Color color, String iconPath) {
        JButton button = createStyledButton(text, color);

        try {
            ImageIcon icon = loadIcon(iconPath);
            if (icon != null) {
                button.setIcon(icon);
                button.setIconTextGap(8);
            }
        } catch (Exception e) {
            // If icon loading fails, just use text button
            System.err.println("Failed to load icon: " + iconPath);
        }

        return button;
    }

    private ImageIcon loadIcon(String path) {
        try {
            // Try to load from resources or create a placeholder
            return new ImageIcon(getClass().getResource(path));
        } catch (Exception e) {
            return null;
        }
    }

    private void showAddInvoiceDialog() {
        // Create the main dialog with increased size
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Thêm hóa đơn mới", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(CARD_COLOR);

        // Header panel with gradient color
        JPanel dialogHeaderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(41, 128, 185), // Blue
                        getWidth(), 0, new Color(52, 152, 219) // Lighter blue
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        dialogHeaderPanel.setLayout(new BorderLayout());
        dialogHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Tạo hóa đơn mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        dialogHeaderPanel.add(titleLabel, BorderLayout.WEST);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 0;

        // Get list of employees
        List<Employee> employees = employeeController.getAllEmployees();

        // Create employee selection combobox with consistent styling
        JComboBox<String> employeeCombo = new JComboBox<>();
        employeeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        employeeCombo.setPreferredSize(new Dimension(200, 40));
        employeeCombo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199)), // Lighter border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        employeeCombo.setFocusable(false);

        for (Employee emp : employees) {
            employeeCombo.addItem(emp.getEmployeeId() + ": " + emp.getFirstName() + " " + emp.getLastName());
        }

        if (employeeCombo.getItemCount() > 0) {
            employeeCombo.setSelectedIndex(0);
        }

        // Total amount field with consistent styling - Make it non-editable and
        // auto-calculated
        JTextField totalField = new JTextField(10);
        totalField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalField.setPreferredSize(new Dimension(200, 40));
        totalField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(46, 204, 113), 8), // Green border to indicate calculated field
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        totalField.setEditable(false); // Make it non-editable
        totalField.setBackground(new Color(245, 245, 245)); // Light gray background
        totalField.setText("0.00"); // Initialize with 0.00
        totalField.setHorizontalAlignment(JTextField.RIGHT); // Right-align for currency display

        // Payment status combo with consistent styling
        JComboBox<String> statusCombo = new JComboBox<>(new String[] { "Paid", "Unpaid" });
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusCombo.setPreferredSize(new Dimension(200, 40));
        statusCombo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199)), // Lighter border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        statusCombo.setFocusable(false);

        // Date chooser for invoice date with consistent styling
        JDateChooser dateChooser = new JDateChooser(new Date());
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateChooser.setPreferredSize(new Dimension(200, 40));
        // Style date editor
        JTextField dateTextField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Add form components
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel employeeLabel = createFormLabel("Nhân viên:");
        employeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        employeeLabel.setForeground(new Color(52, 73, 94)); // Dark blue
        formPanel.add(employeeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(employeeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel totalLabel = createFormLabel("Tổng tiền:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(46, 204, 113)); // Green color to indicate auto-calculation
        formPanel.add(totalLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(totalField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel statusLabel = createFormLabel("Trạng thái:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(new Color(52, 73, 94)); // Dark blue
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(statusCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel dateLabel = createFormLabel("Ngày tạo:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateLabel.setForeground(new Color(52, 73, 94)); // Dark blue
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(dateChooser, gbc);

        // Products panel (initially empty)
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBackground(CARD_COLOR);
        productsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(20, 0, 0, 0)));

        JLabel productsTitle = new JLabel("Sản phẩm");
        productsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productsTitle.setForeground(new Color(26, 188, 156)); // Turquoise

        JPanel productsTitlePanel = new JPanel(new BorderLayout());
        productsTitlePanel.setBackground(CARD_COLOR);
        productsTitlePanel.add(productsTitle, BorderLayout.WEST);

        JButton addProductBtn = createStyledButton("Thêm sản phẩm", new Color(46, 204, 113)); // Green
        addProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addProductBtn.setPreferredSize(new Dimension(150, 40));
        productsTitlePanel.add(addProductBtn, BorderLayout.EAST);

        productsPanel.add(productsTitlePanel, BorderLayout.NORTH);

        // Table for products
        String[] cols = { "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền", "Xóa" };
        DefaultTableModel productModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2 || col == 5; // Only quantity and delete button are editable
            }
        };

        JTable productTable = new JTable(productModel);
        productTable.setRowHeight(40);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        styleModernTable(productTable);

        // Add delete button column
        productTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Xóa"));
        ButtonEditor deleteButtonEditor = new ButtonEditor(new JCheckBox(), "Xóa");
        productTable.getColumnModel().getColumn(5).setCellEditor(deleteButtonEditor);

        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createEmptyBorder());
        productsPanel.add(productScroll, BorderLayout.CENTER);

        // List to keep track of details
        List<Detail> details = new ArrayList<>();

        // Set references for the button editor to update total when products are
        // deleted
        deleteButtonEditor.setReferences(details, totalField, productTable);

        // Add action to add product button
        addProductBtn.addActionListener(e -> {
            JDialog productDialog = new JDialog(dialog, "Thêm sản phẩm", true);
            productDialog.setLayout(new BorderLayout(10, 10));
            productDialog.getContentPane().setBackground(CARD_COLOR);
            productDialog.setMinimumSize(new Dimension(700, 550)); // Increased size for search

            // Header panel with gradient
            JPanel headerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(26, 188, 156), // Turquoise
                            getWidth(), 0, new Color(22, 160, 133) // Darker turquoise
                    );
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            headerPanel.setLayout(new BorderLayout());

            JLabel productTitleLabel = new JLabel("Thêm sản phẩm vào hóa đơn");
            productTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            productTitleLabel.setForeground(Color.WHITE);
            headerPanel.add(productTitleLabel, BorderLayout.WEST);

            // Search panel - NEW FEATURE
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            searchPanel.setBackground(CARD_COLOR);
            searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

            JLabel searchLabel = new JLabel("Tìm kiếm sản phẩm:");
            searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            searchLabel.setForeground(new Color(52, 73, 94));

            JTextField productSearchField = new JTextField(20);
            productSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            productSearchField.setPreferredSize(new Dimension(250, 35));
            productSearchField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(new Color(189, 195, 199), 8),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));

            JButton searchButton = createGradientButton("Tìm kiếm", new Color(52, 152, 219));
            searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            searchButton.setPreferredSize(new Dimension(100, 35));

            searchPanel.add(searchLabel);
            searchPanel.add(productSearchField);
            searchPanel.add(searchButton);

            // Main form panel
            JPanel productFormPanel = new JPanel(new GridBagLayout());
            productFormPanel.setBackground(CARD_COLOR);
            productFormPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

            GridBagConstraints pgbc = new GridBagConstraints();
            pgbc.fill = GridBagConstraints.HORIZONTAL;
            pgbc.insets = new Insets(10, 10, 10, 10);

            // Product selection dropdown (enhanced with search functionality)
            List<Product> allProducts = productController.getAllProducts();
            JComboBox<String> productCombo = new JComboBox<>();
            productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            productCombo.setPreferredSize(new Dimension(350, 40));
            productCombo.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            productCombo.setBackground(Color.WHITE);
            productCombo.setFocusable(false);

            // Store original products list for filtering
            List<Product> filteredProducts = new ArrayList<>(allProducts);

            // Method to update product combo based on search
            Runnable updateProductCombo = () -> {
                String searchText = productSearchField.getText().toLowerCase().trim();
                productCombo.removeAllItems();
                filteredProducts.clear();

                for (Product product : allProducts) {
                    if (searchText.isEmpty() ||
                            product.getProductName().toLowerCase().contains(searchText) ||
                            String.valueOf(product.getProductId()).contains(searchText) ||
                            (product.getBarcode() != null && product.getBarcode().toLowerCase().contains(searchText))) {

                        filteredProducts.add(product);
                        String stockInfo = product.getQuantity() > 0 ? " (Tồn: " + product.getQuantity() + ")"
                                : " (Hết hàng)";
                        productCombo.addItem(product.getProductId() + ": " + product.getProductName() + stockInfo);
                    }
                }

                if (productCombo.getItemCount() > 0) {
                    productCombo.setSelectedIndex(0);
                }
            };

            // Initialize combo with all products
            updateProductCombo.run();

            // Wire up search button functionality
            searchButton.addActionListener(ev -> {
                updateProductCombo.run();
            });

            // Add search functionality - real-time filtering
            productSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    updateProductCombo.run();
                }
            });

            // Product info fields with styling
            JTextField productNameField = new JTextField(20);
            productNameField.setEditable(false);
            productNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            productNameField.setBackground(new Color(245, 245, 245));
            productNameField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            productNameField.setPreferredSize(new Dimension(200, 40));

            // Price field
            JTextField priceField = new JTextField(10);
            priceField.setEditable(false);
            priceField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            priceField.setBackground(new Color(245, 245, 245));
            priceField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            priceField.setPreferredSize(new Dimension(200, 40));

            // Quantity spinner with validation - ENHANCED
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            quantitySpinner.setPreferredSize(new Dimension(200, 40));
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) quantitySpinner.getEditor();
            spinnerEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 16));
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);

            // Total field
            JTextField totalProductField = new JTextField(10);
            totalProductField.setEditable(false);
            totalProductField.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalProductField.setBackground(new Color(245, 245, 245));
            totalProductField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(new Color(26, 188, 156), 8), // Turquoise
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            totalProductField.setPreferredSize(new Dimension(200, 40));

            // Add components with labels
            pgbc.gridx = 0;
            pgbc.gridy = 0;
            JLabel productLabel = createStyledLabel("Chọn sản phẩm:");
            productLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            productLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(productLabel, pgbc);

            pgbc.gridx = 1;
            pgbc.weightx = 1.0;
            productFormPanel.add(productCombo, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 1;
            pgbc.weightx = 0;
            JLabel pnameLabel = createStyledLabel("Tên sản phẩm:");
            pnameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            pnameLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(pnameLabel, pgbc);

            pgbc.gridx = 1;
            pgbc.weightx = 1.0;
            productFormPanel.add(productNameField, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 2;
            pgbc.weightx = 0;
            JLabel priceLabel = createStyledLabel("Đơn giá:");
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            priceLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(priceLabel, pgbc);

            pgbc.gridx = 1;
            pgbc.weightx = 1.0;
            productFormPanel.add(priceField, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 3;
            pgbc.weightx = 0;
            JLabel qtyLabel = createStyledLabel("Số lượng:");
            qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            qtyLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(qtyLabel, pgbc);

            pgbc.gridx = 1;
            pgbc.weightx = 1.0;
            productFormPanel.add(quantitySpinner, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 4;
            pgbc.weightx = 0;
            JLabel totalLabel2 = createStyledLabel("Thành tiền:");
            totalLabel2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalLabel2.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(totalLabel2, pgbc);

            pgbc.gridx = 1;
            pgbc.gridy = 4;
            pgbc.weightx = 1.0;
            productFormPanel.add(totalProductField, pgbc);

            // Button panel with gradient buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(CARD_COLOR);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JButton addToInvoiceBtn = createGradientButton("Thêm vào hóa đơn", new Color(26, 188, 156)); // Turquoise
            addToInvoiceBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            addToInvoiceBtn.setPreferredSize(new Dimension(180, 45));

            JButton cancelProductBtn = createGradientButton("Hủy", new Color(231, 76, 60)); // Red
            cancelProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            cancelProductBtn.setPreferredSize(new Dimension(120, 45));

            buttonPanel.add(addToInvoiceBtn);
            buttonPanel.add(cancelProductBtn);

            // Add panels to dialog
            productDialog.add(headerPanel, BorderLayout.NORTH);

            // Create center panel that includes search and form
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.add(searchPanel, BorderLayout.NORTH);
            centerPanel.add(productFormPanel, BorderLayout.CENTER);

            productDialog.add(centerPanel, BorderLayout.CENTER);
            productDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Product selection change listener - auto-fill product details and update
            // stock validation
            productCombo.addActionListener(ev -> {
                if (productCombo.getSelectedItem() != null) {
                    try {
                        String selected = (String) productCombo.getSelectedItem();
                        int productId = Integer.parseInt(selected.split(":")[0].trim());
                        Product product = productController.getProductById(productId);

                        if (product != null) {
                            productNameField.setText(product.getProductName());
                            priceField.setText(product.getPrice().toString());

                            // Update quantity spinner maximum based on available stock
                            int availableStock = product.getQuantity();
                            SpinnerNumberModel spinnerModel = (SpinnerNumberModel) quantitySpinner.getModel();
                            if (availableStock > 0) {
                                spinnerModel.setMaximum(availableStock);
                                spinnerModel.setMinimum(1);
                                // Ensure the current value is valid, reset to 1 if needed
                                int currentValue = (Integer) quantitySpinner.getValue();
                                if (currentValue > availableStock || currentValue < 1) {
                                    quantitySpinner.setValue(1);
                                }
                            } else {
                                spinnerModel.setMaximum(0);
                                spinnerModel.setMinimum(0);
                                quantitySpinner.setValue(0);
                            }

                            updateProductTotal(priceField, quantitySpinner, totalProductField);
                        }
                    } catch (Exception ex) {
                        productNameField.setText("");
                        priceField.setText("");
                        totalProductField.setText("");
                        // Reset spinner to default state on error
                        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) quantitySpinner.getModel();
                        spinnerModel.setMaximum(999);
                        spinnerModel.setMinimum(1);
                        quantitySpinner.setValue(1);
                    }
                }
            });

            // Initialize with first product if available
            if (productCombo.getItemCount() > 0) {
                productCombo.setSelectedIndex(0);
                // Trigger the action listener to populate fields
                productCombo.getActionListeners()[0]
                        .actionPerformed(new java.awt.event.ActionEvent(productCombo, 0, ""));
            }

            // Update total when quantity changes with stock validation
            quantitySpinner.addChangeListener(ev -> {
                // Validate quantity against available stock
                try {
                    int requestedQuantity = (Integer) quantitySpinner.getValue();
                    if (productCombo.getSelectedItem() != null) {
                        String selected = (String) productCombo.getSelectedItem();
                        int productId = Integer.parseInt(selected.split(":")[0].trim());
                        Product product = productController.getProductById(productId);

                        if (product != null) {
                            // Check existing quantity in the invoice table to calculate available stock
                            int existingQuantityInTable = 0;
                            for (int row = 0; row < productModel.getRowCount(); row++) {
                                int existingProductId = (int) productModel.getValueAt(row, 0);
                                if (existingProductId == productId) {
                                    existingQuantityInTable = (int) productModel.getValueAt(row, 2);
                                    break;
                                }
                            }

                            int maxAllowedQuantity = product.getQuantity() - existingQuantityInTable;
                            if (requestedQuantity > maxAllowedQuantity && maxAllowedQuantity > 0) {
                                // Reset to maximum allowed quantity
                                quantitySpinner.setValue(maxAllowedQuantity);
                                JOptionPane.showMessageDialog(productDialog,
                                        "Số lượng yêu cầu (" + requestedQuantity + ") vượt quá số lượng có thể thêm ("
                                                + maxAllowedQuantity + ")\n" +
                                                "Tồn kho: " + product.getQuantity() + ", Đã có trong hóa đơn: "
                                                + existingQuantityInTable,
                                        "Vượt quá tồn kho",
                                        JOptionPane.WARNING_MESSAGE);
                            } else if (maxAllowedQuantity <= 0) {
                                quantitySpinner.setValue(0);
                                JOptionPane.showMessageDialog(productDialog,
                                        "Sản phẩm này đã được thêm hết vào hóa đơn hoặc hết hàng",
                                        "Không thể thêm",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Handle any parsing errors silently and reset to 1
                    quantitySpinner.setValue(1);
                }
                updateProductTotal(priceField, quantitySpinner, totalProductField);
            });

            // Add button action
            addToInvoiceBtn.addActionListener(ev -> {
                try {
                    if (productNameField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(productDialog,
                                "Vui lòng chọn sản phẩm trước",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Get product ID from selected combo item
                    String selected = (String) productCombo.getSelectedItem();
                    int productId = Integer.parseInt(selected.split(":")[0].trim());

                    // Ensure spinner value is committed before reading
                    try {
                        quantitySpinner.commitEdit();
                    } catch (java.text.ParseException pe) {
                        // If commit fails, the current value in the editor might be invalid
                        // Force the spinner to use its current valid value
                    }

                    int quantity = (Integer) quantitySpinner.getValue();

                    // Validate quantity is positive
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(productDialog,
                                "Số lượng phải lớn hơn 0",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    BigDecimal price = new BigDecimal(priceField.getText());
                    BigDecimal total = new BigDecimal(totalProductField.getText());

                    // Validate stock availability before adding
                    Product product = productController.getProductById(productId);
                    if (product == null) {
                        JOptionPane.showMessageDialog(productDialog,
                                "Không tìm thấy thông tin sản phẩm",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check if we have enough stock (considering existing quantity in the table)
                    int existingQuantityInTable = 0;
                    for (int row = 0; row < productModel.getRowCount(); row++) {
                        int existingProductId = (int) productModel.getValueAt(row, 0);
                        if (existingProductId == productId) {
                            existingQuantityInTable = (int) productModel.getValueAt(row, 2);
                            break;
                        }
                    }

                    int totalRequiredQuantity = existingQuantityInTable + quantity;
                    if (totalRequiredQuantity > product.getQuantity()) {
                        JOptionPane.showMessageDialog(productDialog,
                                "Không đủ hàng tồn kho!\n" +
                                        "Tồn kho hiện tại: " + product.getQuantity() + "\n" +
                                        "Đã có trong hóa đơn: " + existingQuantityInTable + "\n" +
                                        "Số lượng có thể thêm tối đa: "
                                        + (product.getQuantity() - existingQuantityInTable),
                                "Vượt quá tồn kho",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Check if product already exists in table
                    boolean productExists = false;
                    for (int row = 0; row < productModel.getRowCount(); row++) {
                        int existingProductId = (int) productModel.getValueAt(row, 0);
                        if (existingProductId == productId) {
                            // Update existing product quantity and total
                            int existingQuantity = (int) productModel.getValueAt(row, 2);
                            int newQuantity = existingQuantity + quantity;

                            BigDecimal newTotal = price.multiply(BigDecimal.valueOf(newQuantity));

                            // Update table
                            productModel.setValueAt(newQuantity, row, 2);
                            productModel.setValueAt(newTotal, row, 4);

                            // Update details list
                            for (Detail detail : details) {
                                if (detail.getProductId() == productId) {
                                    detail.setQuantity(newQuantity);
                                    detail.setTotal(newTotal);
                                    break;
                                }
                            }

                            productExists = true;
                            break;
                        }
                    }

                    if (!productExists) {
                        // Add new row if product doesn't exist
                        productModel.addRow(new Object[] {
                                productId,
                                productNameField.getText(),
                                quantity,
                                price,
                                total,
                                "Xóa"
                        });

                        // Add to details list
                        Detail detail = new Detail();
                        detail.setProductId(productId);
                        detail.setQuantity(quantity);
                        detail.setPrice(price);
                        detail.setTotal(total);
                        details.add(detail);
                    }

                    // Update invoice total
                    updateInvoiceTotal(details, totalField);

                    // Show success message with the product that was added/updated
                    JOptionPane.showMessageDialog(productDialog,
                            productExists ? "Đã cập nhật số lượng sản phẩm: " + productNameField.getText()
                                    : "Đã thêm sản phẩm: " + productNameField.getText(),
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    productDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(productDialog,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi khi thêm sản phẩm",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelProductBtn.addActionListener(ev -> productDialog.dispose());

            productDialog.pack();
            productDialog.setLocationRelativeTo(dialog);
            productDialog.setVisible(true);
        });

        // Bottom panel with buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(CARD_COLOR);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton saveBtn = createGradientButton("Lưu hóa đơn", new Color(41, 128, 185)); // Blue
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setPreferredSize(new Dimension(180, 45));

        JButton cancelBtn = createGradientButton("Hủy", new Color(231, 76, 60)); // Red
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBtn.setPreferredSize(new Dimension(120, 45));

        saveBtn.addActionListener(e -> {
            try {
                // Validate form - no need to check total field since it's auto-calculated
                if (details.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Vui lòng thêm ít nhất một sản phẩm vào hóa đơn",
                            "Hóa đơn trống",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Extract employee ID from combo selection
                String selectedEmployee = (String) employeeCombo.getSelectedItem();
                int employeeId = Integer.parseInt(selectedEmployee.split(":")[0].trim());

                // Create invoice
                Invoice invoice = new Invoice();
                invoice.setUsersId(employeeId);
                invoice.setTotal(new BigDecimal(totalField.getText()));
                invoice.setPaymentStatus((String) statusCombo.getSelectedItem());
                invoice.setCreatedAt(dateChooser.getDate());

                // Save invoice and get ID
                controller.saveInvoice(invoice);
                int invoiceId = invoice.getInvoiceId();

                // Add details with the invoice ID
                for (Detail detail : details) {
                    detail.setInvoiceId(invoiceId);
                    detailController.addDetail(detail);
                }

                // Success message with colorful dialog
                JDialog successDialog = new JDialog(dialog, "Thành công", true);
                successDialog.setLayout(new BorderLayout());

                JPanel successHeader = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(
                                0, 0, new Color(46, 204, 113), // Green
                                getWidth(), 0, new Color(39, 174, 96) // Darker green
                        );
                        g2d.setPaint(gp);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        g2d.dispose();
                    }
                };
                successHeader.setLayout(new BorderLayout());
                successHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

                JLabel successTitle = new JLabel("Thành công");
                successTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
                successTitle.setForeground(Color.WHITE);
                successHeader.add(successTitle, BorderLayout.CENTER);

                JPanel successContent = new JPanel();
                successContent.setBackground(Color.WHITE);
                successContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
                successContent.setLayout(new BorderLayout());

                JLabel successMessage = new JLabel(
                        "<html><div style='text-align: center;'>" +
                                "Đã tạo hóa đơn mới thành công!<br><br>" +
                                "Mã hóa đơn: <b>#" + invoiceId + "</b><br>" +
                                "Số sản phẩm: <b>" + details.size() + "</b><br>" +
                                "Tổng tiền: <b>" + totalField.getText() + " VND</b>" +
                                "</div></html>");
                successMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                successMessage.setHorizontalAlignment(JLabel.CENTER);

                JButton okButton = createGradientButton("Đóng", new Color(46, 204, 113));
                okButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
                okButton.setPreferredSize(new Dimension(120, 40));

                JPanel buttonWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonWrap.setBackground(Color.WHITE);
                buttonWrap.add(okButton);

                successContent.add(successMessage, BorderLayout.CENTER);
                successContent.add(buttonWrap, BorderLayout.SOUTH);

                successDialog.add(successHeader, BorderLayout.NORTH);
                successDialog.add(successContent, BorderLayout.CENTER);

                okButton.addActionListener(okEvent -> {
                    successDialog.dispose();
                    dialog.dispose();
                    loadInvoices();
                });

                successDialog.setSize(400, 300);
                successDialog.setLocationRelativeTo(dialog);
                successDialog.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi tạo hóa đơn: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        // Add all panels to dialog
        dialog.add(dialogHeaderPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(productsPanel, BorderLayout.CENTER);

        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        // Increase dialog size
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateProductTotal(JTextField priceField, JSpinner quantitySpinner, JTextField totalField) {
        try {
            // Ensure we have valid price text
            String priceText = priceField.getText().trim();
            if (priceText.isEmpty()) {
                totalField.setText("");
                return;
            }

            // Commit any pending edits to the spinner
            try {
                quantitySpinner.commitEdit();
            } catch (java.text.ParseException pe) {
                // If commit fails, use the spinner's current value
            }

            BigDecimal price = new BigDecimal(priceText);
            Integer quantityObj = (Integer) quantitySpinner.getValue();

            // Validate quantity is not null and positive
            if (quantityObj == null || quantityObj <= 0) {
                totalField.setText("0");
                return;
            }

            int quantity = quantityObj;
            BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
            totalField.setText(total.toString());

        } catch (NumberFormatException e) {
            totalField.setText("");
        } catch (Exception e) {
            // Log the error and clear the field
            System.err.println("Error updating product total: " + e.getMessage());
            totalField.setText("");
        }
    }

    private void updateInvoiceTotal(List<Detail> details, JTextField totalField) {
        BigDecimal total = BigDecimal.ZERO;
        for (Detail detail : details) {
            total = total.add(detail.getTotal());
        }
        totalField.setText(total.toString());
    }

    private void exportToExcel() {
        try {
            // Create file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu báo cáo hóa đơn");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));

            // Show save dialog
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Add .xlsx extension if not already there
                if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
                }

                // Get all invoices
                List<Invoice> invoices = controller.getAllInvoices();

                // Create Excel exporter
                ExcelExporter exporter = new ExcelExporter();
                exporter.exportToExcel(invoiceTable, fileToSave, true);

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "Xuất Excel thành công:\n" + fileToSave.getAbsolutePath(),
                        "Xuất Excel",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void importFromExcel() {
        try {
            // Create file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file Excel để nhập");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));

            // Show open dialog
            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();

                // Import from Excel
                ExcelImporter importer = new ExcelImporter();
                // Create a file object
                File excelFile = fileToOpen;

                // Validate the file
                if (!ExcelImporter.validateExcelFile(excelFile)) {
                    throw new IOException("Invalid Excel file format");
                }

                // Import data from Excel
                List<Object[]> importedData = ExcelImporter.importFromExcel(excelFile);

                // Process each row and create invoices
                int count = 0;
                for (Object[] row : importedData) {
                    try {
                        // Assuming the Excel has columns: UserId, Total, PaymentStatus, Date
                        // Create and populate the invoice
                        if (row.length >= 4) {
                            int userId = (row[0] instanceof Number) ? ((Number) row[0]).intValue()
                                    : Integer.parseInt(row[0].toString());
                            BigDecimal total = (row[1] instanceof BigDecimal) ? (BigDecimal) row[1]
                                    : new BigDecimal(row[1].toString());
                            String status = (row[2] != null) ? row[2].toString() : "Unpaid";
                            java.util.Date date = (row[3] instanceof java.util.Date) ? (java.util.Date) row[3]
                                    : new java.util.Date();

                            // Create invoice and save
                            Invoice invoice = new Invoice();
                            invoice.setUsersId(userId);
                            invoice.setTotal(total);
                            invoice.setPaymentStatus(status);
                            invoice.setCreatedAt(date);

                            controller.saveInvoice(invoice);
                            count++;
                        }
                    } catch (Exception e) {
                        System.err.println("Error importing row: " + e.getMessage());
                    }
                }

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "Đã nhập " + count + " hóa đơn từ Excel.",
                        "Nhập Excel thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refresh the table
                loadInvoices();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi nhập Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JButton createGradientButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add clean Bootstrap-style hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    // Create a slightly darker shade (85% of original brightness)
                    Color darker = new Color(
                            Math.max((int) (baseColor.getRed() * 0.85), 0),
                            Math.max((int) (baseColor.getGreen() * 0.85), 0),
                            Math.max((int) (baseColor.getBlue() * 0.85), 0));
                    button.setBackground(darker);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor);
                }
            }
        });

        return button;
    }

    // Button renderer for delete column
    class ButtonRenderer extends DefaultTableCellRenderer {
        private final String text;

        public ButtonRenderer(String text) {
            this.text = text;
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JButton button = new JButton(text);
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            if (isSelected) {
                button.setBackground(ACCENT_COLOR.darker());
            }
            return button;
        }
    }

    // Button editor for delete column
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String currentText;
        private boolean isPushed;
        private int row;
        private List<Detail> detailsList; // Reference to details list
        private JTextField totalFieldRef; // Reference to total field
        private JTable tableRef; // Reference to table

        public ButtonEditor(JCheckBox checkBox, String text) {
            super(checkBox);
            button = new JButton(text);
            button.setOpaque(true);
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            button.addActionListener(e -> fireEditingStopped());

            currentText = text;
        }

        // Method to set references to details list and total field
        public void setReferences(List<Detail> details, JTextField totalField, JTable table) {
            this.detailsList = details;
            this.totalFieldRef = totalField;
            this.tableRef = table;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentText = (value == null) ? "" : value.toString();
            button.setText(currentText);
            isPushed = true;
            this.row = row;
            this.tableRef = table; // Store table reference
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;

            // If this is a delete button in the Add Invoice dialog
            if (currentText.equals("Xóa") && tableRef != null) {
                DefaultTableModel model = (DefaultTableModel) tableRef.getModel();

                // Get product ID from the row being deleted
                int productId = (int) model.getValueAt(row, 0);

                // Remove the row from table
                model.removeRow(row);

                // Remove corresponding detail from the details list
                if (detailsList != null) {
                    detailsList.removeIf(detail -> detail.getProductId() == productId);

                    // Update total field if reference is available
                    if (totalFieldRef != null) {
                        updateInvoiceTotal(detailsList, totalFieldRef);
                    }
                }
            }

            return currentText;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
