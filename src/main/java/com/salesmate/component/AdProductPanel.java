package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import com.google.zxing.WriterException;
import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import com.salesmate.utils.BarcodeGenerator;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExcelImporter;
import com.salesmate.utils.ExportDialog;

public class AdProductPanel extends javax.swing.JPanel {
    private ProductController productController;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton generateBarcodeButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JButton importButton;
    private JTextField searchField;
    private JSpinner pageSpinner;
    private JLabel totalPagesLabel;
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;
    private String currentSearch = "";
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color LIGHT_TEXT = new Color(255, 255, 255);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(189, 195, 199);

    public AdProductPanel() {
        productController = new ProductController();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create top panel with a title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Quản lý sản phẩm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create top panel for buttons and filters
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Left panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        // Create styled buttons
        addButton = createStyledButton("Thêm sản phẩm", PRIMARY_COLOR, LIGHT_TEXT);
        editButton = createStyledButton("Sửa", SECONDARY_COLOR, LIGHT_TEXT);
        deleteButton = createStyledButton("Xóa", ACCENT_COLOR, LIGHT_TEXT);
        generateBarcodeButton = createStyledButton("Tạo mã vạch", new Color(46, 204, 113), LIGHT_TEXT);
        refreshButton = createStyledButton("Làm mới", new Color(52, 73, 94), LIGHT_TEXT);
        exportButton = createStyledButton("Xuất Excel", new Color(155, 89, 182), LIGHT_TEXT);
        importButton = createStyledButton("Nhập Excel", new Color(243, 156, 18), LIGHT_TEXT);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(generateBarcodeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        
        // Right panel for filters (only search field now)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        // Styled search field
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"ID", "Tên sản phẩm", "Giá", "Số lượng", "Mã vạch", "Danh mục", "Hình ảnh"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and style table
        productTable = new JTable(tableModel);
        productTable.setRowHeight(40); // Make rows taller
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Larger font
        productTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        productTable.setRowSorter(new TableRowSorter<>(tableModel));
        productTable.setShowGrid(true);
        productTable.setGridColor(new Color(220, 220, 220));
        productTable.setSelectionBackground(new Color(232, 234, 246));
        productTable.setSelectionForeground(TEXT_COLOR);
        productTable.setFillsViewportHeight(true);
        
        // Style table header with a different color than the buttons
        JTableHeader header = productTable.getTableHeader();
        
        // Use a deeper blue color for headers to distinguish from buttons
        final Color HEADER_COLOR = new Color(25, 79, 115);
        
        // Make header taller
        header.setPreferredSize(new Dimension(header.getWidth(), 45)); // Increased from default
        
        // Important: Set custom header renderer with the new color
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setBackground(HEADER_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                label.setHorizontalAlignment(JLabel.CENTER);
                // Add more vertical padding
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5) // Increased padding
                ));
                label.setOpaque(true);
                return label;
            }
        });

        // Alternate row colors and center align some columns
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, false, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                
                // Center align ID, Price, Quantity columns
                setHorizontalAlignment(column == 0 || column == 2 || column == 3 ? 
                                      SwingConstants.CENTER : SwingConstants.LEFT);
                
                // Add padding
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                
                return c;
            }
        });
        
        // Set column widths (increase for better visibility)
        productTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(260); // Tên sản phẩm
        productTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Giá
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Số lượng
        productTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Mã vạch
        productTable.getColumnModel().getColumn(5).setPreferredWidth(160); // Danh mục
        productTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Hình ảnh

        // Add table to scroll pane with styling
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 500)); // Make table bigger

        // Create styled pagination panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paginationPanel.setBackground(BACKGROUND_COLOR);
        paginationPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton prevButton = createStyledButton("« Trước", SECONDARY_COLOR, LIGHT_TEXT);
        JButton nextButton = createStyledButton("Sau »", SECONDARY_COLOR, LIGHT_TEXT);
        
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        pageSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pageSpinner.setPreferredSize(new Dimension(60, 35));
        
        totalPagesLabel = new JLabel(" / 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPagesLabel.setForeground(TEXT_COLOR);
        
        JLabel pageInfoLabel = new JLabel("Trang:");
        pageInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pageInfoLabel.setForeground(TEXT_COLOR);
        
        paginationPanel.add(prevButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(pageSpinner);
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(nextButton);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
        
        // Move header to top
        remove(headerPanel);
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(BACKGROUND_COLOR);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(topPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Add action listeners
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteSelectedProducts());
        generateBarcodeButton.addActionListener(e -> generateBarcodes());
        refreshButton.addActionListener(e -> loadProducts());
        exportButton.addActionListener(e -> exportToExcel());
        importButton.addActionListener(e -> importFromExcel());
        
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageSpinner.setValue(currentPage);
                loadProducts();
            }
        });
        
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                pageSpinner.setValue(currentPage);
                loadProducts();
            }
        });
        
        pageSpinner.addChangeListener(e -> {
            currentPage = (Integer) pageSpinner.getValue();
            loadProducts();
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            
            private void filterProducts() {
                currentSearch = searchField.getText();
                currentPage = 1;
                pageSpinner.setValue(1);
                loadProducts();
            }
        });
    }

    // Helper method to create consistently styled buttons
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(bgColor.brighter());
                } else {
                    g.setColor(bgColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productController.getProducts(currentPage, pageSize, null, currentSearch);
        int totalProducts = productController.countProducts(null, currentSearch);
        totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        if (totalPages < 1) totalPages = 1;
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getBarcode(),
                product.getCategory(),
                product.getImage()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm mới", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Thêm sản phẩm mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Form panel with improved styling
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Create styled text fields
        JTextField nameField = createStyledTextField("");
        JTextField priceField = createStyledTextField("");
        JTextField quantityField = createStyledTextField("");
        JTextField barcodeField = createStyledTextField("");
        JTextField categoryField = createStyledTextField("");
        JTextField imageField = createStyledTextField("");
        
        // Create styled labels
        JLabel nameLabel = createStyledLabel("Tên sản phẩm:");
        JLabel priceLabel = createStyledLabel("Giá:");
        JLabel quantityLabel = createStyledLabel("Số lượng:");
        JLabel barcodeLabel = createStyledLabel("Mã vạch:");
        JLabel categoryLabel = createStyledLabel("Danh mục:");
        JLabel imageLabel = createStyledLabel("Hình ảnh:");
        
        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(priceLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3; 
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(quantityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(barcodeLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(barcodeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(categoryLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(categoryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1;
        formPanel.add(imageLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        formPanel.add(imageField, gbc);
        
        // Button panel with styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton saveButton = createStyledButton("Lưu", PRIMARY_COLOR, LIGHT_TEXT);
        JButton cancelButton = createStyledButton("Hủy", new Color(189, 195, 199), TEXT_COLOR);
        
        saveButton.addActionListener(e -> {
            try {
                Product product = new Product();
                product.setProductName(nameField.getText());
                product.setPrice(new BigDecimal(priceField.getText()));
                product.setQuantity(Integer.parseInt(quantityField.getText()));
                product.setBarcode(barcodeField.getText());
                product.setCategory(categoryField.getText());
                product.setImage(imageField.getText());
                
                if (productController.addProduct(product)) {
                    JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm sản phẩm!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đúng định dạng số!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
            return;
        }
        
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = productController.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa sản phẩm", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Cập nhật thông tin sản phẩm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Form panel with improved styling
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Create styled text fields
        JTextField nameField = createStyledTextField(product.getProductName());
        JTextField priceField = createStyledTextField(product.getPrice().toString());
        JTextField quantityField = createStyledTextField(String.valueOf(product.getQuantity()));
        JTextField barcodeField = createStyledTextField(product.getBarcode());
        JTextField categoryField = createStyledTextField(product.getCategory());
        JTextField imageField = createStyledTextField(product.getImage());
        
        // Create styled labels
        JLabel nameLabel = createStyledLabel("Tên sản phẩm:");
        JLabel priceLabel = createStyledLabel("Giá:");
        JLabel quantityLabel = createStyledLabel("Số lượng:");
        JLabel barcodeLabel = createStyledLabel("Mã vạch:");
        JLabel categoryLabel = createStyledLabel("Danh mục:");
        JLabel imageLabel = createStyledLabel("Hình ảnh:");
        
        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(priceLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3; 
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(quantityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(barcodeLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(barcodeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(categoryLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(categoryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1;
        formPanel.add(imageLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        formPanel.add(imageField, gbc);
        
        // Button panel with styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton saveButton = createStyledButton("Lưu thay đổi", PRIMARY_COLOR, LIGHT_TEXT);
        JButton cancelButton = createStyledButton("Hủy", new Color(189, 195, 199), TEXT_COLOR);
        
        saveButton.addActionListener(e -> {
            try {
                product.setProductName(nameField.getText());
                product.setPrice(new BigDecimal(priceField.getText()));
                product.setQuantity(Integer.parseInt(quantityField.getText()));
                product.setBarcode(barcodeField.getText());
                product.setCategory(categoryField.getText());
                product.setImage(imageField.getText());
                
                if (productController.updateProduct(product)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật sản phẩm thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật sản phẩm!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đúng định dạng số!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Helper method to create styled text fields
    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return textField;
    }

    // Helper method to create styled labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void deleteSelectedProducts() {
        int[] rows = productTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm để xóa",
                "Chưa chọn sản phẩm",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create a more attractive confirmation dialog
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                          "Xác nhận xóa", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.getContentPane().setBackground(Color.WHITE);
        // Set larger initial size
        confirmDialog.setMinimumSize(new Dimension(480, 250));
        confirmDialog.setResizable(false);
        // Add a nice drop shadow border
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        confirmDialog.setContentPane(contentPane);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ACCENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Xác nhận xóa sản phẩm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel msgLabel = new JLabel("<html>Bạn có chắc chắn muốn xóa <b>" + rows.length + 
                                    "</b> sản phẩm đã chọn?<br>Thao tác này không thể hoàn tác.</html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(msgLabel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JButton deleteBtn = createStyledButton("Xóa", ACCENT_COLOR, LIGHT_TEXT);
        JButton cancelBtn = createStyledButton("Hủy", SECONDARY_COLOR, LIGHT_TEXT);

        buttonPanel.add(deleteBtn);
        buttonPanel.add(cancelBtn);

        // Add panels to dialog
        confirmDialog.add(headerPanel, BorderLayout.NORTH);
        confirmDialog.add(contentPanel, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Handle delete action
        deleteBtn.addActionListener(e -> {
            int[] productIds = new int[rows.length];
            for (int i = 0; i < rows.length; i++) {
                int modelRow = productTable.convertRowIndexToModel(rows[i]);
                productIds[i] = (int) tableModel.getValueAt(modelRow, 0); // Assuming ID is in first column
            }

            if (productController.deleteMultipleProducts(productIds)) {
                // Success message
                JOptionPane.showMessageDialog(this,
                    "Đã xóa " + rows.length + " sản phẩm thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra khi xóa sản phẩm.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            confirmDialog.dispose();
        });

        cancelBtn.addActionListener(e -> confirmDialog.dispose());

        // Show dialog
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setVisible(true);
    }

    private void generateBarcodes() {
        List<Product> products = productController.getAllProducts();
        int successCount = 0;
        int failCount = 0;

        for (Product product : products) {
            if (product.getBarcode() != null && !product.getBarcode().isEmpty()) {
                try {
                    BarcodeGenerator.generateBarcode(product.getBarcode(), product.getProductId());
                    successCount++;
                } catch (WriterException | IOException e) {
                    failCount++;
                    e.printStackTrace();
                }
            }
        }

        String message = String.format("Đã tạo %d mã vạch thành công.\nKhông thể tạo %d mã vạch.", 
                                     successCount, failCount);
        JOptionPane.showMessageDialog(this, message, "Tạo mã vạch", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToExcel() {
        ExportDialog exportDialog = new ExportDialog((Frame) SwingUtilities.getWindowAncestor(this), productTable);
        exportDialog.setVisible(true);
        
        if (exportDialog.isExportConfirmed()) {
            File file = exportDialog.showSaveDialog();
            if (file != null) {
                try {
                    if (exportDialog.isXLSX()) {
                        ExcelExporter.exportToExcel(productTable, file, exportDialog.includeHeaders(), exportDialog.getSelectedColumns());
                    } else {
                        ExcelExporter.exportToCSV(productTable, file, exportDialog.includeHeaders(), exportDialog.getSelectedColumns());
                    }
                    
                    if (exportDialog.openAfterExport()) {
                        ExcelExporter.openFile(file);
                    }
                    
                    JOptionPane.showMessageDialog(this, "Xuất file thành công!");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + e.getMessage(), 
                                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                if (!ExcelImporter.validateExcelFile(file)) {
                    JOptionPane.showMessageDialog(this, 
                        "File Excel không hợp lệ. Vui lòng kiểm tra lại định dạng file.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get column headers from Excel file
                String[] headers = ExcelImporter.getColumnHeaders(file);
                if (headers == null || headers.length == 0) {
                    JOptionPane.showMessageDialog(this,
                        "Không thể đọc tiêu đề cột từ file Excel.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create column mapping dialog
                JDialog mappingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ánh xạ cột", true);
                mappingDialog.setLayout(new BorderLayout());
                
                JPanel mappingPanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Create comboboxes for each required field
                String[] requiredFields = {"Tên sản phẩm", "Giá", "Số lượng", "Mã vạch", "Danh mục", "Hình ảnh"};
                JComboBox<String>[] fieldMappings = new JComboBox[requiredFields.length];
                
                for (int i = 0; i < requiredFields.length; i++) {
                    gbc.gridx = 0;
                    gbc.gridy = i;
                    mappingPanel.add(new JLabel(requiredFields[i] + ":"), gbc);
                    
                    gbc.gridx = 1;
                    fieldMappings[i] = new JComboBox<>(headers);
                    mappingPanel.add(fieldMappings[i], gbc);
                }

                JButton importButton = new JButton("Nhập");
                JButton cancelButton = new JButton("Hủy");

                importButton.addActionListener(e -> {
                    mappingDialog.dispose();
                    performImport(file, fieldMappings);
                });

                cancelButton.addActionListener(e -> mappingDialog.dispose());

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(importButton);
                buttonPanel.add(cancelButton);

                mappingDialog.add(mappingPanel, BorderLayout.CENTER);
                mappingDialog.add(buttonPanel, BorderLayout.SOUTH);
                mappingDialog.pack();
                mappingDialog.setLocationRelativeTo(this);
                mappingDialog.setVisible(true);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi đọc file Excel: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performImport(File file, JComboBox<String>[] fieldMappings) {
        try {
            List<Object[]> data = ExcelImporter.importFromExcel(file);
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorLog = new StringBuilder();

            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Object[] row = data.get(rowIndex);
                try {
                    Product product = new Product();
                    
                    // Map Excel columns to product fields
                    String productName = getStringValue(row, fieldMappings[0].getSelectedIndex());
                    BigDecimal price = getBigDecimalValue(row, fieldMappings[1].getSelectedIndex());
                    int quantity = getIntValue(row, fieldMappings[2].getSelectedIndex());
                    String barcode = getStringValue(row, fieldMappings[3].getSelectedIndex());
                    String category = getStringValue(row, fieldMappings[4].getSelectedIndex());
                    String image = getStringValue(row, fieldMappings[5].getSelectedIndex());

                    // Validate required fields
                    if (productName == null || productName.trim().isEmpty()) {
                        throw new IllegalArgumentException("Tên sản phẩm không được để trống");
                    }
                    if (price.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Giá không được âm");
                    }
                    if (quantity < 0) {
                        throw new IllegalArgumentException("Số lượng không được âm");
                    }

                    product.setProductName(productName);
                    product.setPrice(price);
                    product.setQuantity(quantity);
                    product.setBarcode(barcode);
                    product.setCategory(category);
                    product.setImage(image);

                    if (productController.addProduct(product)) {
                        successCount++;
                    } else {
                        failCount++;
                        errorLog.append(String.format("Dòng %d: Không thể thêm sản phẩm\n", rowIndex + 2));
                    }
                } catch (Exception e) {
                    failCount++;
                    errorLog.append(String.format("Dòng %d: %s\n", rowIndex + 2, e.getMessage()));
                }
            }

            // Show import results
            StringBuilder message = new StringBuilder();
            message.append(String.format("Đã nhập %d sản phẩm thành công.\n", successCount));
            if (failCount > 0) {
                message.append(String.format("Không thể nhập %d sản phẩm.\n\n", failCount));
                message.append("Chi tiết lỗi:\n");
                message.append(errorLog.toString());
            }

            JOptionPane.showMessageDialog(this, message.toString(), 
                "Kết quả nhập Excel", 
                failCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
            
            loadProducts();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi đọc file Excel: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getStringValue(Object[] row, int columnIndex) {
        if (columnIndex < 0 || columnIndex >= row.length || row[columnIndex] == null) {
            return "";
        }
        return row[columnIndex].toString().trim();
    }

    private BigDecimal getBigDecimalValue(Object[] row, int columnIndex) {
        try {
            if (columnIndex < 0 || columnIndex >= row.length || row[columnIndex] == null) {
                return BigDecimal.ZERO;
            }
            String value = row[columnIndex].toString().trim();
            return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private int getIntValue(Object[] row, int columnIndex) {
        try {
            if (columnIndex < 0 || columnIndex >= row.length || row[columnIndex] == null) {
                return 0;
            }
            String value = row[columnIndex].toString().trim();
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
