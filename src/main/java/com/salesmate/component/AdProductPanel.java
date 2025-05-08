package com.salesmate.component;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import com.salesmate.utils.BarcodeGenerator;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExportDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;
import javax.swing.table.TableRowSorter;
import com.google.zxing.WriterException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.salesmate.utils.ExcelImporter;

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
    private JComboBox<String> categoryFilter;
    private JTextField searchField;
    private JSpinner pageSpinner;
    private JLabel totalPagesLabel;
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;
    private String currentCategory = "Tất cả";
    private String currentSearch = "";

    public AdProductPanel() {
        productController = new ProductController();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Create top panel for buttons and filters
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Left panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Thêm sản phẩm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        generateBarcodeButton = new JButton("Tạo mã vạch");
        refreshButton = new JButton("Làm mới");
        exportButton = new JButton("Xuất Excel");
        importButton = new JButton("Nhập Excel");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(generateBarcodeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        
        // Right panel for filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryFilter = new JComboBox<>(new String[]{"Tất cả", "Nước giặt", "Dầu gội", "Nước xả", "Sữa tắm", "Không có danh mục"});
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        filterPanel.add(new JLabel("Tìm kiếm:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Danh mục:"));
        filterPanel.add(categoryFilter);
        
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

        // Create table
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowSorter(new TableRowSorter<>(tableModel));
        
        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Price
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
        productTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Barcode
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Category
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Image

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // Create pagination panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("Trước");
        JButton nextButton = new JButton("Sau");
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        totalPagesLabel = new JLabel(" / 1");
        
        paginationPanel.add(prevButton);
        paginationPanel.add(pageSpinner);
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(nextButton);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
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
        
        categoryFilter.addActionListener(e -> {
            currentCategory = (String) categoryFilter.getSelectedItem();
            currentPage = 1;
            pageSpinner.setValue(1);
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

    private void loadProducts() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get products from controller with pagination and filters
        List<Product> products = productController.getProducts(currentPage, pageSize, currentCategory, currentSearch);
        int totalProducts = productController.countProducts(currentCategory, currentSearch);
        totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        // Update pagination controls
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

        // Add products to table
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
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form fields
        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField barcodeField = new JTextField(20);
        JTextField imageField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Mã vạch:"), gbc);
        gbc.gridx = 1;
        formPanel.add(barcodeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Hình ảnh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(imageField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
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
                    JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thành công!");
                    dialog.dispose();
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
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
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form fields
        JTextField nameField = new JTextField(product.getProductName(), 20);
        JTextField priceField = new JTextField(product.getPrice().toString(), 20);
        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()), 20);
        JTextField barcodeField = new JTextField(product.getBarcode(), 20);
        JTextField imageField = new JTextField(product.getImage(), 20);
        JTextField categoryField = new JTextField(product.getCategory(), 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Mã vạch:"), gbc);
        gbc.gridx = 1;
        formPanel.add(barcodeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Hình ảnh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(imageField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        saveButton.addActionListener(e -> {
            try {
                product.setProductName(nameField.getText());
                product.setPrice(new BigDecimal(priceField.getText()));
                product.setQuantity(Integer.parseInt(quantityField.getText()));
                product.setBarcode(barcodeField.getText());
                product.setCategory(categoryField.getText());
                product.setImage(imageField.getText());
                
                if (productController.updateProduct(product)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật sản phẩm thành công!");
                    dialog.dispose();
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
            return;
        }
        
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sản phẩm này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (productController.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
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
