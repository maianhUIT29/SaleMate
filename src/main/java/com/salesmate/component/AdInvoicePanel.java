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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.swing.table.TableColumnModel;
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
    private final InvoiceController controller       = new InvoiceController();
    private final DetailController detailController = new DetailController();
    private final UserController    userController  = new UserController();
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
    private static final Color PRIMARY_COLOR    = new Color(0, 123, 255);    // Bootstrap primary
    private static final Color SECONDARY_COLOR  = new Color(108, 117, 125);  // Bootstrap secondary  
    private static final Color SUCCESS_COLOR    = new Color(40, 167, 69);    // Bootstrap success
    private static final Color DANGER_COLOR     = new Color(220, 53, 69);    // Bootstrap danger
    private static final Color WARNING_COLOR    = new Color(255, 193, 7);    // Bootstrap warning
    private static final Color INFO_COLOR       = new Color(23, 162, 184);   // Bootstrap info
    private static final Color LIGHT_COLOR      = new Color(248, 249, 250);  // Bootstrap light
    private static final Color DARK_COLOR       = new Color(52, 58, 64);     // Bootstrap dark
    private static final Color WHITE_COLOR      = new Color(255, 255, 255);  // White
    private static final Color BORDER_COLOR     = new Color(222, 226, 230);  // Bootstrap border
    private static final Color CARD_COLOR       = new Color(255, 255, 255);  // Card background color
    private static final Color ACCENT_COLOR     = new Color(220, 53, 69);    // Accent color for highlights
    private static final Color IMPORT_COLOR     = new Color(50, 150, 50);    // Import button color
    private static final Color EXPORT_COLOR     = new Color(0, 150, 136);    // Export button color
    private static final Color ADD_COLOR        = new Color(0, 123, 255);    // Add button color
    private static final Color HEADER_COLOR     = new Color(33, 37, 41);     // Header color
    private static final Color TEXT_SECONDARY   = new Color(108, 117, 125);  // Secondary text color
    private static final Color LIGHT_TEXT       = new Color(248, 249, 250);  // Light text color

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
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.add(title, BorderLayout.WEST);
        headerPanel.add(header, BorderLayout.CENTER);

        // Toolbar buttons with icons - now with Add button first
        JButton addBtn     = createIconButton("Thêm hóa đơn", ADD_COLOR, "/icons/add.png");
        JButton editBtn    = createIconButton("Sửa", SECONDARY_COLOR, "/icons/edit.png");
        JButton deleteBtn  = createIconButton("Xóa", ACCENT_COLOR, "/icons/delete.png");
        JButton exportBtn  = createIconButton("Xuất Excel", EXPORT_COLOR, "/icons/export.png");
        JButton importBtn  = createIconButton("Nhập Excel", IMPORT_COLOR, "/icons/import.png");
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
            BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));

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
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        spinnerEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSpinner.setPreferredSize(new Dimension(70, 38));
        
        totalPagesLabel = new JLabel(" / 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPagesLabel.setForeground(TEXT_COLOR);

        // Table setup with modern styling
        String[] cols = {"Mã hóa đơn", "Người lập hóa đơn", "Ngày", "Tổng tiền", "Trạng thái", "Chi tiết"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };
        
        invoiceTable = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
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
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);

        // Pagination controls with modern styling
        JPanel paginationPanel = createPaginationPanel();
        add(paginationPanel, BorderLayout.SOUTH);

        // Add listeners
        addBtn.addActionListener(e -> showAddInvoiceDialog());
        editBtn.addActionListener(e -> showEditDialog());
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
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Status filter styling
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Paid", "Unpaid"});
        statusFilter.setPreferredSize(new Dimension(120, 38));
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setBackground(WHITE_COLOR);
        ((JComponent) statusFilter.getRenderer()).setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        // Date choosers styling
        JDateChooser fromDate = new JDateChooser();
        fromDate.setPreferredSize(new Dimension(140, 38));
        customizeDateChooser(fromDate);
        
        JDateChooser toDate = new JDateChooser();
        toDate.setPreferredSize(new Dimension(140, 38));
        customizeDateChooser(toDate);

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
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
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
            if (filterFrom != null) m3 = !inv.getCreatedAt().before(filterFrom);
            if (m3 && filterTo != null) m3 = !inv.getCreatedAt().after(filterTo);
            return m1 && m2 && m3;
        }).collect(Collectors.toList());

        int total = filtered.size();
        totalPages = Math.max((int)Math.ceil((double)total / pageSize), 1);
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

       int start = (currentPage - 1) * pageSize;
int end   = Math.min(start + pageSize, total);

for (Invoice inv : filtered.subList(start, end)) {
    // Lấy tên người lập tương ứng cho từng hóa đơn
    User u = userController.getUserById(inv.getUsersId());
    String username = (u != null ? u.getUsername() : "—");

    tableModel.addRow(new Object[]{
        inv.getInvoiceId(),
        username,
        inv.getCreatedAt(),
        inv.getTotal(),
        inv.getPaymentStatus(),
        "Xem chi tiết"
    });
}


        ((TableRowSorter<?>)invoiceTable.getRowSorter())
            .setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    }
private void showEditDialog() {
    // 1. Lấy dòng được chọn
    int viewRow = invoiceTable.getSelectedRow();
    if (viewRow < 0) {
        JOptionPane.showMessageDialog(this, "Chọn hóa đơn để sửa", "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    int modelRow = invoiceTable.convertRowIndexToModel(viewRow);
    int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

    // 2. Lấy đối tượng Invoice và danh sách Detail từ DB
    Invoice invoice = controller.getInvoiceById(invoiceId);
    List<Detail> originalDetails = detailController.getDetailsByInvoiceId(invoiceId);

    // 3. Tạo dialog chính với kiểu dáng hiện đại
    JDialog dialog = new JDialog(
        (Frame) SwingUtilities.getWindowAncestor(this),
        "Sửa hóa đơn", true
    );
    dialog.setLayout(new BorderLayout());
    dialog.getContentPane().setBackground(BACKGROUND_COLOR);
    dialog.setMinimumSize(new Dimension(800, 600));

    // 4. Header với gradient đẹp
    JPanel headerPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                0, 0, PRIMARY_COLOR,
                getWidth(), 0, PRIMARY_COLOR.darker()
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    };
    headerPanel.setLayout(new BorderLayout());
    headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
    headerPanel.setPreferredSize(new Dimension(0, 70));

    JLabel titleLabel = new JLabel("Sửa hóa đơn #" + invoiceId);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titleLabel.setForeground(Color.WHITE);
    headerPanel.add(titleLabel, BorderLayout.WEST);
    
    // Thêm badge trạng thái
    JLabel statusBadge = new JLabel(invoice.getPaymentStatus());
    statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
    statusBadge.setOpaque(true);
    statusBadge.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
    
    if ("Paid".equals(invoice.getPaymentStatus())) {
        statusBadge.setBackground(SUCCESS_COLOR);
        statusBadge.setForeground(WHITE_COLOR);
    } else {
        statusBadge.setBackground(WARNING_COLOR);
        statusBadge.setForeground(DARK_COLOR);
    }
    
    JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    headerRight.setOpaque(false);
    headerRight.add(statusBadge);
    headerPanel.add(headerRight, BorderLayout.EAST);
    
    // 5. Tạo model cho bảng chi tiết
    String[] cols = {"detailId", "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
    DefaultTableModel detailModel = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    
    JTable detailTable = new JTable(detailModel);
    styleModernTable(detailTable);
    detailTable.setRowHeight(40);
    detailTable.setSelectionBackground(new Color(232, 240, 254));
    detailTable.setSelectionForeground(PRIMARY_COLOR);
    
    // Ẩn cột detailId
    detailTable.removeColumn(detailTable.getColumnModel().getColumn(0));
    
    // Set column widths
    detailTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Mã SP
    detailTable.getColumnModel().getColumn(1).setPreferredWidth(250);  // Tên SP
    detailTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Số lượng
    detailTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Đơn giá
    detailTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Thành tiền
    
    // Thêm dữ liệu cùng với tên sản phẩm
    for (Detail d : originalDetails) {
        String productName = productController.getProductNameById(d.getProductId());
        if (productName == null) productName = "Sản phẩm không tồn tại";
        
        detailModel.addRow(new Object[]{
            d.getDetailId(), 
            d.getProductId(), 
            productName,
            d.getQuantity(),
            d.getPrice(), 
            d.getTotal()
        });
    }
    
    // 6. Danh sách tạm để chứa thay đổi
    List<Detail> toAdd = new ArrayList<>();
    List<Detail> toUpdate = new ArrayList<>();
    List<Integer> toDelete = new ArrayList<>();
    
    // 7. Tạo bảng trong panel với viền đẹp
    JScrollPane scrollPane = new JScrollPane(detailTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setBackground(CARD_COLOR);
    
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.setBackground(CARD_COLOR);
    tablePanel.setBorder(new CompoundBorder(
        new ShadowBorder(),
        BorderFactory.createEmptyBorder(0, 0, 0, 0)
    ));
    tablePanel.add(scrollPane, BorderLayout.CENTER);
    
    // 8. Panel tổng tiền với styling hiện đại
    JPanel totalPanel = new JPanel(new BorderLayout());
    totalPanel.setBackground(WHITE_COLOR);
    totalPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
        BorderFactory.createEmptyBorder(20, 25, 20, 25)
    ));
    
    JLabel totalLabel = new JLabel("TỔNG CỘNG: " + invoice.getTotal() + " VND");
    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    totalLabel.setForeground(PRIMARY_COLOR);
    totalPanel.add(totalLabel, BorderLayout.WEST);
    
    // 9. Panel chứa nút Lưu/Hủy
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    actionPanel.setBackground(WHITE_COLOR);
    
    JButton saveBtn = createGradientButton("Lưu thay đổi", PRIMARY_COLOR);
    JButton cancelBtn = createGradientButton("Hủy", ACCENT_COLOR);
    saveBtn.setPreferredSize(new Dimension(130, 40));
    cancelBtn.setPreferredSize(new Dimension(100, 40));
    
    actionPanel.add(saveBtn);
    actionPanel.add(cancelBtn);
    totalPanel.add(actionPanel, BorderLayout.EAST);
    
    // 10. Panel chứa các nút chức năng cho sản phẩm
    JPanel toolbarPanel = new JPanel();
    toolbarPanel.setBackground(CARD_COLOR);
    toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
    ));
    toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
    
    JLabel productsLabel = new JLabel("Chi tiết sản phẩm");
    productsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    productsLabel.setForeground(PRIMARY_COLOR);
    
    JButton addBtn = createIconButton("Thêm sản phẩm", SUCCESS_COLOR, "/icons/add.png");
    JButton editBtn = createIconButton("Sửa", INFO_COLOR, "/icons/edit.png");
    JButton deleteBtn = createIconButton("Xóa", ACCENT_COLOR, "/icons/delete.png");
    
    toolbarPanel.add(productsLabel);
    toolbarPanel.add(Box.createHorizontalGlue());
    toolbarPanel.add(addBtn);
    toolbarPanel.add(Box.createRigidArea(new Dimension(8, 0)));
    toolbarPanel.add(editBtn);
    toolbarPanel.add(Box.createRigidArea(new Dimension(8, 0)));
    toolbarPanel.add(deleteBtn);
    
    // 11. Panel chứa bảng và toolbar
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBackground(BACKGROUND_COLOR);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JPanel cardPanel = new JPanel(new BorderLayout());
    cardPanel.setBackground(CARD_COLOR);
    cardPanel.add(toolbarPanel, BorderLayout.NORTH);
    cardPanel.add(tablePanel, BorderLayout.CENTER);
    cardPanel.add(totalPanel, BorderLayout.SOUTH);
    
    contentPanel.add(cardPanel, BorderLayout.CENTER);
    
    // 12. Thêm các panel vào dialog
    dialog.add(headerPanel, BorderLayout.NORTH);
    dialog.add(contentPanel, BorderLayout.CENTER);
    
    // 13. Hàm tính lại tổng tiền và cập nhật label
    Runnable recalcTotal = () -> {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            sum = sum.add((BigDecimal) detailModel.getValueAt(i, 4));
        }
        invoice.setTotal(sum);
        totalLabel.setText("TỔNG CỘNG: " + sum + " VND");
    };

    // === XỬ LÝ NÚT THÊM ===
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
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel productLabel = createStyledLabel("Sản phẩm:");
        formPanel.add(productLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(productCombo, gbc);
        
        // Quantity spinner with styling
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel qtyLabel = createStyledLabel("Số lượng:");
        formPanel.add(qtyLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        qtySpin.setPreferredSize(new Dimension(0, 38));
        JComponent spinnerEditor = qtySpin.getEditor();
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinnerEditor;
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(qtySpin, gbc);
        
        // Price field with styling
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel priceLabel = createStyledLabel("Đơn giá:");
        formPanel.add(priceLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField priceField = new JTextField(10);
        priceField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_COLOR, 8),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        priceField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(priceField, gbc);
        
        // Total price (calculated) with styling
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel totalFieldLabel = createStyledLabel("Thành tiền:");
        formPanel.add(totalFieldLabel, gbc);
        
        gbc.gridx = 1;
        JTextField totalItemField = new JTextField();
        totalItemField.setEditable(false);
        totalItemField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalItemField.setBackground(new Color(245, 245, 245));
        totalItemField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(PRIMARY_COLOR, 8),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotal(priceField, qtySpin, totalItemField); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotal(priceField, qtySpin, totalItemField); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotal(priceField, qtySpin, totalItemField); }
        });
        
        // Action buttons panel
        JPanel actionBtn = new JPanel();
        actionBtn.setBackground(WHITE_COLOR);
        actionBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
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
                detailModel.addRow(new Object[]{
                    0,  // detailId = 0 for new items
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
        
        dlg.add(dlgHeader, BorderLayout.NORTH);
        
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
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel productIdLabel = createStyledLabel("Sản phẩm:");
        formPanel.add(productIdLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(productCombo, gbc);
        
        // Số lượng
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel qtyLabel = createStyledLabel("Số lượng:");
        formPanel.add(qtyLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(oldQty, 1, 9999, 1));
        qtySpin.setPreferredSize(new Dimension(0, 38));
        JComponent spinnerEditor = qtySpin.getEditor();
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinnerEditor;
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(qtySpin, gbc);
        
        // Đơn giá
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel priceLabel = createStyledLabel("Đơn giá:");
        formPanel.add(priceLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField priceField = new JTextField(oldPrice.toString());
        priceField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_COLOR, 8),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        priceField.setPreferredSize(new Dimension(0, 38));
        formPanel.add(priceField, gbc);
        
        // Thành tiền
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel totalItemLabel = createStyledLabel("Thành tiền:");
        formPanel.add(totalItemLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField totalField = new JTextField(oldPrice.multiply(BigDecimal.valueOf(oldQty)).toString());
        totalField.setEditable(false);
        totalField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalField.setBackground(new Color(245, 245, 245));
        totalField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(PRIMARY_COLOR, 8),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
                    
                    // Update total
                    updateTotal(priceField, qtySpin, totalField);
                } catch (Exception ex) {
                    // Xử lý lỗi
                }
            }
        });
        
        // Update total when quantity or price changes
        qtySpin.addChangeListener(ev -> updateTotal(priceField, qtySpin, totalField));
        priceField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotal(priceField, qtySpin, totalField); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotal(priceField, qtySpin, totalField); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotal(priceField, qtySpin, totalField); }
        });
        
        // Action buttons panel
        JPanel actionBtn = new JPanel();
        actionBtn.setBackground(WHITE_COLOR);
        actionBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
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

    // === XỬ LÝ NÚT XÓA ===
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
        
        // Hiển thị dialog xác nhận với UI đẹp hơn
        JDialog confirmDialog = new JDialog(dialog, "Xác nhận xóa", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.getContentPane().setBackground(WHITE_COLOR);
        
        JPanel confirmHeader = new JPanel();
        confirmHeader.setBackground(ACCENT_COLOR);
        confirmHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        confirmHeader.setLayout(new BorderLayout());
        
        JLabel confirmTitle = new JLabel("Xác nhận xóa sản phẩm");
        confirmTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        confirmTitle.setForeground(Color.WHITE);
        confirmHeader.add(confirmTitle, BorderLayout.CENTER);
        
        confirmDialog.add(confirmHeader, BorderLayout.NORTH);
        
        JPanel confirmContent = new JPanel();
        confirmContent.setBackground(WHITE_COLOR);
        confirmContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        confirmContent.setLayout(new BorderLayout());
        
        JLabel confirmMessage = new JLabel("<html>Bạn có chắc chắn muốn xóa sản phẩm<br><b>" + 
                                         productName + "</b> khỏi hóa đơn?</html>");
        confirmMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmContent.add(confirmMessage, BorderLayout.CENTER);
        
        confirmDialog.add(confirmContent, BorderLayout.CENTER);
        
        JPanel confirmButtons = new JPanel();
        confirmButtons.setBackground(WHITE_COLOR);
        confirmButtons.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        confirmButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton yesButton = createGradientButton("Có, xóa sản phẩm", ACCENT_COLOR);
        JButton noButton = createGradientButton("Không, giữ lại", SECONDARY_COLOR);
        
        confirmButtons.add(yesButton);
        confirmButtons.add(noButton);
        
        confirmDialog.add(confirmButtons, BorderLayout.SOUTH);
        
        final boolean[] confirmed = {false};
        
        yesButton.addActionListener(ev -> {
            confirmed[0] = true;
            confirmDialog.dispose();
        });
        
        noButton.addActionListener(ev -> confirmDialog.dispose());
        
        confirmDialog.setSize(400, 250);
        confirmDialog.setLocationRelativeTo(dialog);
        confirmDialog.setVisible(true);
        
        // Sau khi dialog đóng, kiểm tra kết quả
        if (confirmed[0]) {
            if (detailId == 0) {
                toAdd.removeIf(d -> d.getDetailId() == 0 && 
                                  (int)detailModel.getValueAt(r,1) == d.getProductId());
            } else {
                toDelete.add(detailId);
                toUpdate.removeIf(d -> d.getDetailId() == detailId);
            }
            
            detailModel.removeRow(r);
            recalcTotal.run();
        }
    });

    // === XỬ LÝ NÚT LƯU ===
    saveBtn.addActionListener(e -> {
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
                "Bạn có muốn tiếp tục?</html>"
            );
            confirmMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            confirmContent.add(confirmMessage, BorderLayout.CENTER);
            
            confirmEmptyDialog.add(confirmContent, BorderLayout.CENTER);
            
            JPanel confirmButtons = new JPanel();
            confirmButtons.setBackground(WHITE_COLOR);
            confirmButtons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            confirmButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            
            JButton deleteButton = createGradientButton("Xóa hóa đơn", ACCENT_COLOR);
            JButton cancelButton = createGradientButton("Hủy", SECONDARY_COLOR);
            
            confirmButtons.add(deleteButton);
            confirmButtons.add(cancelButton);
            
            confirmEmptyDialog.add(confirmButtons, BorderLayout.SOUTH);
            
            final boolean[] confirmed = {false};
            
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
        if (!toAdd.isEmpty()) changes += "- Thêm " + toAdd.size() + " sản phẩm mới<br>";
        if (!toUpdate.isEmpty()) changes += "- Cập nhật " + toUpdate.size() + " sản phẩm<br>";
        if (!toDelete.isEmpty()) changes += "- Xóa " + toDelete.size() + " sản phẩm<br>";
        
        if (changes.isEmpty()) changes = "Không có thay đổi nào.";
        
        JLabel confirmMessage = new JLabel("<html>Bạn có chắc chắn muốn lưu những thay đổi sau?<br><br>" + 
                                         changes + "</html>");
        confirmMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmContent.add(confirmMessage, BorderLayout.CENTER);
        
        confirmDialog.add(confirmContent, BorderLayout.CENTER);
        
        JPanel confirmButtons = new JPanel();
        confirmButtons.setBackground(WHITE_COLOR);
        confirmButtons.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        confirmButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton saveConfirmBtn = createGradientButton("Lưu thay đổi", PRIMARY_COLOR);
        JButton cancelConfirmBtn = createGradientButton("Hủy", SECONDARY_COLOR);
        
        confirmButtons.add(saveConfirmBtn);
        confirmButtons.add(cancelConfirmBtn);
        
        confirmDialog.add(confirmButtons, BorderLayout.SOUTH);
        
        final boolean[] confirmed = {false};
        
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
                
                // 4) Cập nhật tổng của hoá đơn
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
                    "Hóa đơn #" + invoiceId + " đã được cập nhật.</div></html>", JLabel.CENTER
                );
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

    // Cancel button action
    cancelBtn.addActionListener(e -> dialog.dispose());

    // Set dialog size and position
    dialog.setSize(900, 650);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
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
    // 1) Lấy dòng được chọn trong view
    int viewRow = invoiceTable.getSelectedRow();
    if (viewRow < 0) {
        JOptionPane.showMessageDialog(this, "Chọn hóa đơn để xóa");
        return;
    }

    // 2) Chuyển thành chỉ số model (vì có sort/filter)
    int modelRow = invoiceTable.convertRowIndexToModel(viewRow);
    int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

    // 3) Hỏi xác nhận
    int choice = JOptionPane.showConfirmDialog(
        this,
        "Xóa hóa đơn #" + invoiceId + " và tất cả chi tiết của nó?",
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
    );
    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    // 4) Gọi controller (đã tự động xóa chi tiết rồi)
    boolean success = controller.deleteInvoice(invoiceId);

    // 5) Thông báo và làm mới
    if (success) {
        JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
        loadInvoices();
    } else {
        JOptionPane.showMessageDialog(
            this,
            "Xóa hóa đơn thất bại. Vui lòng thử lại.",
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }
}





    private void exportToExcel() {
        ExportDialog dlg = new ExportDialog((Frame)SwingUtilities.getWindowAncestor(this), invoiceTable);
        dlg.setVisible(true);
        if (!dlg.isExportConfirmed()) return;
        File f = dlg.showSaveDialog();
        if (f == null) return;
        try {
            if (dlg.isXLSX())
                ExcelExporter.exportToExcel(invoiceTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
            else
                ExcelExporter.exportToCSV(invoiceTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
            if (dlg.openAfterExport()) ExcelExporter.openFile(f);
            JOptionPane.showMessageDialog(this, "Xuất thành công");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importFromExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        try {
            if (!ExcelImporter.validateExcelFile(f)) {
                JOptionPane.showMessageDialog(this, "File không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // TODO: implement import mapping if needed
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi import: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetailDialog(int invoiceId) {
        // Get the invoice and details
        Invoice invoice = controller.getInvoiceById(invoiceId);
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Không thể tìm thấy hóa đơn", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Detail> details = detailController.getDetailsByInvoiceId(invoiceId);
        
        // Get the user (employee) name
        User user = userController.getUserById(invoice.getUsersId());
        String employeeName = user != null ? user.getUsername() : "—";
        
        // Create an elegant dialog
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
                                  "Chi tiết hóa đơn #" + invoiceId, true);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create a modern header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Chi tiết hóa đơn #" + invoiceId);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
        titleLabel.setForeground(WHITE_COLOR);
        
        // Style status badge
        JLabel statusBadge = new JLabel(invoice.getPaymentStatus());
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        
        if ("Paid".equals(invoice.getPaymentStatus())) {
            statusBadge.setBackground(SUCCESS_COLOR);
            statusBadge.setForeground(WHITE_COLOR);
        } else {
            statusBadge.setBackground(WARNING_COLOR);
            statusBadge.setForeground(DARK_COLOR);
        }

        // Format the date nicely
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = dateFormat.format(invoice.getCreatedAt());
        
        // Info panel under the header
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        infoPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add employee info
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel employeeTitle = new JLabel("Nhân viên:");
        employeeTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeTitle.setForeground(TEXT_SECONDARY);
        infoPanel.add(employeeTitle, gbc);
        
        gbc.gridx = 1;
        JLabel employeeValue = new JLabel(employeeName);
        employeeValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(employeeValue, gbc);
        
        // Add date info
        gbc.gridx = 2;
        gbc.insets = new Insets(5, 30, 5, 10); // Add more space between columns
        JLabel dateTitle = new JLabel("Ngày lập:");
        dateTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dateTitle.setForeground(TEXT_SECONDARY);
        infoPanel.add(dateTitle, gbc);
        
        gbc.gridx = 3;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel dateValue = new JLabel(formattedDate);
        dateValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(dateValue, gbc);
        
        // Add total amount info
        gbc.gridx = 4;
        gbc.insets = new Insets(5, 30, 5, 10);
        JLabel totalTitle = new JLabel("Tổng tiền:");
        totalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalTitle.setForeground(TEXT_SECONDARY);
        infoPanel.add(totalTitle, gbc);
        
        gbc.gridx = 5;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel totalValue = new JLabel(invoice.getTotal().toString() + " VND");
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValue.setForeground(PRIMARY_COLOR);
        infoPanel.add(totalValue, gbc);
        
        // Add status badge to header
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setBackground(PRIMARY_COLOR);
        headerRight.add(statusBadge);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        
        // Create table with details - modern styling
        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable detailTable = new JTable(m);
        styleModernTable(detailTable);
        detailTable.setRowHeight(40);
        
        // Populate table with details including product names
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Detail d : details) {
            String productName = productController.getProductNameById(d.getProductId());
            if (productName == null) productName = "Sản phẩm không tồn tại";
            
            m.addRow(new Object[]{
                d.getProductId(),
                productName,
                d.getQuantity(),
                d.getPrice() + " VND",
                d.getTotal() + " VND"
            });
            
            totalAmount = totalAmount.add(d.getTotal());
        }
        
        // Set preferred column widths
        TableColumnModel columnModel = detailTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);   // Mã SP
        columnModel.getColumn(1).setPreferredWidth(250);  // Tên SP
        columnModel.getColumn(2).setPreferredWidth(80);   // Số lượng
        columnModel.getColumn(3).setPreferredWidth(120);  // Đơn giá
        columnModel.getColumn(4).setPreferredWidth(120);  // Thành tiền
        
        // Table in a scroll pane with elegant border
        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create footer with total
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(CARD_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        footerPanel.setLayout(new BorderLayout());
        
        JLabel grandTotalLabel = new JLabel("TỔNG CỘNG: " + totalAmount + " VND");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        grandTotalLabel.setForeground(PRIMARY_COLOR);
        
        JButton closeButton = createStyledButton("Đóng", SECONDARY_COLOR);
        closeButton.setPreferredSize(new Dimension(100, 40));
        closeButton.addActionListener(e -> dlg.dispose());
        
        footerPanel.add(grandTotalLabel, BorderLayout.WEST);
        footerPanel.add(closeButton, BorderLayout.EAST);
        
        // Add all components to dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(new CompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        cardPanel.add(tablePanel, BorderLayout.CENTER);
        cardPanel.add(footerPanel, BorderLayout.SOUTH);
        
        contentPanel.add(cardPanel, BorderLayout.CENTER);
        
        dlg.add(mainPanel, BorderLayout.NORTH);
        dlg.add(contentPanel, BorderLayout.CENTER);
        
        dlg.setSize(800, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // Custom UI Components
    
    // Shadow Border for panels
    private static class ShadowBorder extends LineBorder {
        public ShadowBorder() {
            super(BORDER_COLOR, 1, true);
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            super.paintBorder(c, g, x, y, width, height);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.drawRoundRect(x, y, width-1, height-1, 1, 1);
            g2d.dispose();
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
            g2d.draw(new RoundRectangle2D.Double(x, y, width-1, height-1, radius, radius));
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
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return btn;
        }
        
        @Override
        public Object getCellEditorValue() {
            int invoiceId = (int) tableModel.getValueAt(currentRow, 0);
            showDetailDialog(invoiceId);
            return "Xem chi tiết";
        }
    }
    
    // Helper methods
    
    private void customizeDateChooser(JDateChooser dateChooser) {
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            )
        );
    }
    
    private void styleModernTable(JTable table) {
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(WHITE_COLOR);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR.darker()),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                return label;
            }
        });
        
        // Row styling
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
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
        
        // Get list of employees
        List<Employee> employees = employeeController.getAllEmployees();
        
        // Create employee selection combobox with consistent styling
        JComboBox<String> employeeCombo = new JComboBox<>();
        employeeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        employeeCombo.setPreferredSize(new Dimension(200, 40));
        employeeCombo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199)), // Lighter border
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        employeeCombo.setFocusable(false);
        
        for (Employee emp : employees) {
            employeeCombo.addItem(emp.getEmployeeId() + ": " + emp.getFirstName() + " " + emp.getLastName());
        }
        
        if (employeeCombo.getItemCount() > 0) {
            employeeCombo.setSelectedIndex(0);
        }
        
        // Total amount field with consistent styling
        JTextField totalField = new JTextField(10);
        totalField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        totalField.setPreferredSize(new Dimension(200, 40));
        totalField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Payment status combo with consistent styling
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Paid", "Unpaid"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusCombo.setPreferredSize(new Dimension(200, 40));
        statusCombo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199)), // Lighter border
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
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
        totalLabel.setForeground(new Color(52, 73, 94)); // Dark blue
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
            BorderFactory.createEmptyBorder(20, 0, 0, 0)
        ));
        
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
        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền", "Xóa"};
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
        productTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), "Xóa"));
        
        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createEmptyBorder());
        productsPanel.add(productScroll, BorderLayout.CENTER);
        
        // List to keep track of details
        List<Detail> details = new ArrayList<>();
        
        // Add action to add product button
        addProductBtn.addActionListener(e -> {
            JDialog productDialog = new JDialog(dialog, "Thêm sản phẩm", true);
            productDialog.setLayout(new BorderLayout(10, 10));
            productDialog.getContentPane().setBackground(CARD_COLOR);
            productDialog.setMinimumSize(new Dimension(600, 450)); // Increase dialog size
            
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

            // Main form panel
            JPanel productFormPanel = new JPanel(new GridBagLayout());
            productFormPanel.setBackground(CARD_COLOR);
            productFormPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            
            GridBagConstraints pgbc = new GridBagConstraints();
            pgbc.fill = GridBagConstraints.HORIZONTAL;
            pgbc.insets = new Insets(10, 10, 10, 10);
            
            // Product search field with better styling
            JTextField productIdField = new JTextField(10);
            productIdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            productIdField.setPreferredSize(new Dimension(150, 40));
            productIdField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            // Search button
            JButton searchBtn = createStyledButton("Tìm", new Color(52, 152, 219)); // Blue
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            searchBtn.setPreferredSize(new Dimension(100, 40));
            
            // Product info fields with styling
            JTextField productNameField = new JTextField(20);
            productNameField.setEditable(false);
            productNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            productNameField.setBackground(new Color(245, 245, 245));
            productNameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            productNameField.setPreferredSize(new Dimension(200, 40));

            // Price field
            JTextField priceField = new JTextField(10);
            priceField.setEditable(false);
            priceField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            priceField.setBackground(new Color(245, 245, 245));
            priceField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(189, 195, 199), 8), // Lighter border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            priceField.setPreferredSize(new Dimension(200, 40));

            // Quantity spinner with modern look
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            totalProductField.setPreferredSize(new Dimension(200, 40));

            // Add components with labels
            pgbc.gridx = 0; pgbc.gridy = 0;
            JLabel pidLabel = createStyledLabel("Mã sản phẩm:");
            pidLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            pidLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(pidLabel, pgbc);
            
            JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
            searchPanel.setBackground(CARD_COLOR);
            searchPanel.add(productIdField, BorderLayout.CENTER);
            searchPanel.add(searchBtn, BorderLayout.EAST);
            
            pgbc.gridx = 1; pgbc.weightx = 1.0;
            productFormPanel.add(searchPanel, pgbc);
            
            pgbc.gridx = 0; pgbc.gridy = 1; pgbc.weightx = 0;
            JLabel pnameLabel = createStyledLabel("Tên sản phẩm:");
            pnameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            pnameLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(pnameLabel, pgbc);
            
            pgbc.gridx = 1; pgbc.weightx = 1.0;
            productFormPanel.add(productNameField, pgbc);
            
            pgbc.gridx = 0; pgbc.gridy = 2; pgbc.weightx = 0;
            JLabel priceLabel = createStyledLabel("Đơn giá:");
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            priceLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(priceLabel, pgbc);
            
            pgbc.gridx = 1; pgbc.weightx = 1.0;
            productFormPanel.add(priceField, pgbc);
            
            pgbc.gridx = 0; pgbc.gridy = 3; pgbc.weightx = 0;
            JLabel qtyLabel = createStyledLabel("Số lượng:");
            qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            qtyLabel.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(qtyLabel, pgbc);
            
            pgbc.gridx = 1; pgbc.weightx = 1.0;
            productFormPanel.add(quantitySpinner, pgbc);
            
            pgbc.gridx = 0; pgbc.gridy = 4; pgbc.weightx = 0;
            JLabel totalLabel2 = createStyledLabel("Thành tiền:");
            totalLabel2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalLabel2.setForeground(new Color(52, 73, 94)); // Dark blue
            productFormPanel.add(totalLabel2, pgbc);
            
            pgbc.gridx = 1; pgbc.weightx = 1.0;
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
            productDialog.add(productFormPanel, BorderLayout.CENTER);
            productDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Search button action
            searchBtn.addActionListener(ev -> {
                try {
                    int productId = Integer.parseInt(productIdField.getText().trim());
                    Product product = productController.getProductById(productId);
                    
                    if (product != null) {
                        productNameField.setText(product.getProductName());
                        priceField.setText(product.getPrice().toString());
                        updateProductTotal(priceField, quantitySpinner, totalProductField);
                    } else {
                        JOptionPane.showMessageDialog(productDialog, 
                            "Không tìm thấy sản phẩm với mã " + productId,
                            "Không tìm thấy sản phẩm",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(productDialog,
                        "Vui lòng nhập đúng mã sản phẩm",
                        "Lỗi định dạng",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Update total when quantity changes
            quantitySpinner.addChangeListener(ev -> 
                updateProductTotal(priceField, quantitySpinner, totalProductField));
            
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
                    
                    int productId = Integer.parseInt(productIdField.getText().trim());
                    int quantity = (Integer) quantitySpinner.getValue();
                    BigDecimal price = new BigDecimal(priceField.getText());
                    BigDecimal total = new BigDecimal(totalProductField.getText());
                    
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
                        productExists ? 
                            "Đã cập nhật số lượng sản phẩm: " + productNameField.getText() :
                            "Đã thêm sản phẩm: " + productNameField.getText(),
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
                // Validate form
                if (totalField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tổng tiền");
                    return;
                }
                
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
                    "</div></html>"
                );
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
            BigDecimal price = new BigDecimal(priceField.getText());
            int quantity = (Integer) quantitySpinner.getValue();
            BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
            totalField.setText(total.toString());
        } catch (NumberFormatException e) {
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
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient
                GradientPaint gp = new GradientPaint(
                    0, 0, baseColor,
                    0, getHeight(),
                    baseColor.darker()
                );
                
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Add subtle highlight
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 8, 8);
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add rainbow effect on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
                button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            }
            
            @Override 
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentText = (value == null) ? "" : value.toString();
            button.setText(currentText);
            isPushed = true;
            this.row = row;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            
            // If this is a delete button in the Add Invoice dialog
            if (currentText.equals("Xóa")) {
                DefaultTableModel model = (DefaultTableModel) ((JTable) this.getComponent().getParent()).getModel();
                model.removeRow(row);
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
