package com.salesmate.component;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockInventoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ProductController productController = new ProductController();
    private JTextField txtSearch;

    public StockInventoryPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Kiểm tra tồn kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(41, 128, 185));
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));

        // Panel chứa các nút chức năng ở trên bảng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        JButton btnUpdate = new JButton("Cập nhật sản phẩm");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnUpdate.setBackground(new Color(241, 196, 15));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setOpaque(true);
        btnUpdate.setContentAreaFilled(true);
        btnUpdate.setBorderPainted(false);

        JButton btnImportExcel = new JButton("Nhập Excel");
        btnImportExcel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnImportExcel.setBackground(new Color(46, 204, 113));
        btnImportExcel.setForeground(Color.WHITE);
        btnImportExcel.setOpaque(true);
        btnImportExcel.setContentAreaFilled(true);
        btnImportExcel.setBorderPainted(false);

        JButton btnExportReport = new JButton("Xuất báo cáo");
        btnExportReport.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnExportReport.setBackground(new Color(155, 89, 182));
        btnExportReport.setForeground(Color.WHITE);
        btnExportReport.setOpaque(true);
        btnExportReport.setContentAreaFilled(true);
        btnExportReport.setBorderPainted(false);

        // Tạo nút Thêm sản phẩm
        JButton btnAddProduct = new JButton("Thêm sản phẩm");
        btnAddProduct.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAddProduct.setBackground(new Color(46, 204, 113));
        btnAddProduct.setForeground(Color.WHITE);
        btnAddProduct.setOpaque(true);
        btnAddProduct.setContentAreaFilled(true);
        btnAddProduct.setBorderPainted(false);

        // Tạo nút Xóa sản phẩm
        JButton btnDeleteProduct = new JButton("Xóa sản phẩm");
        btnDeleteProduct.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDeleteProduct.setBackground(new Color(46, 204, 113));
        btnDeleteProduct.setForeground(Color.WHITE);
        btnDeleteProduct.setOpaque(true);
        btnDeleteProduct.setContentAreaFilled(true);
        btnDeleteProduct.setBorderPainted(false);

        // Tạo nút Làm mới
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRefresh.setBackground(new Color(52, 152, 219)); // Màu xanh giống nút Tìm kiếm
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setOpaque(true);
        btnRefresh.setContentAreaFilled(true);
        btnRefresh.setBorderPainted(false);

        // Chừa chỗ cho các nút khác sau này
        buttonPanel.add(btnUpdate);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Khoảng cách
        buttonPanel.add(btnImportExcel);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Khoảng cách
        buttonPanel.add(btnExportReport);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Khoảng cách
        buttonPanel.add(btnAddProduct);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnDeleteProduct);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnRefresh);
        buttonPanel.add(Box.createHorizontalStrut(10));

        // Khi khởi tạo tableModel (chỉ cần làm 1 lần, thường ở constructor)
        String[] columns = {"Mã SP", "Tên sản phẩm", "Danh mục", "Mã vạch", "Giá", "Số lượng còn", "Số lượng đã bán"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setForeground(Color.BLACK);      // Chữ màu đen
        table.setBackground(Color.WHITE);      // Nền trắng

        // Border cho JTable (nếu muốn)
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setToolTipText("Nhập tên sản phẩm...");

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setOpaque(true);
        btnSearch.setContentAreaFilled(true);
        btnSearch.setBorderPainted(false);

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.BEFORE_FIRST_LINE); // Nút nằm trên bảng
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        // Load all data on start
        loadInventoryData(null);

        // Search action
        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());

        // Xử lý cập nhật sản phẩm
        btnUpdate.addActionListener(e -> showUpdateDialog());

        // Xử lý nhập Excel
        btnImportExcel.addActionListener(e -> importFromExcel());
        btnExportReport.addActionListener(e -> exportReport());

        // Xử lý sự kiện Thêm sản phẩm
        btnAddProduct.addActionListener(e -> {
            JTextField txtName = new JTextField();
            JTextField txtPrice = new JTextField();
            JTextField txtBarcode = new JTextField();
            JTextField txtCategory = new JTextField();
            JTextField txtImage = new JTextField();
            JTextArea txtDesc = new JTextArea(3, 20);
            JTextField txtQuantity = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Tên sản phẩm:"));
            panel.add(txtName);
            panel.add(new JLabel("Giá bán:"));
            panel.add(txtPrice);
            panel.add(new JLabel("Mã vạch:"));
            panel.add(txtBarcode);
            panel.add(new JLabel("Danh mục:"));
            panel.add(txtCategory);
            panel.add(new JLabel("Hình ảnh (đường dẫn):"));
            panel.add(txtImage);
            panel.add(new JLabel("Mô tả:"));
            panel.add(new JScrollPane(txtDesc));
            panel.add(new JLabel("Số lượng:"));
            panel.add(txtQuantity);

            int result = JOptionPane.showConfirmDialog(this, panel, "Thêm sản phẩm mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    Product product = new Product();
                    product.setProductName(txtName.getText().trim());
                    product.setPrice(new java.math.BigDecimal(txtPrice.getText().trim()));
                    product.setBarcode(txtBarcode.getText().trim());
                    product.setCategory(txtCategory.getText().trim());
                    product.setImage(txtImage.getText().trim());
                    product.setDescription(txtDesc.getText().trim());
                    product.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
                    if (productController.addProduct(product)) {
                        JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        doSearch();
                    } else {
                        JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xử lý sự kiện Xóa sản phẩm
        btnDeleteProduct.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (productController.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    doSearch();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xử lý sự kiện nút Làm mới
        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); // Xóa nội dung tìm kiếm
            loadInventoryData(null); // Tải lại toàn bộ dữ liệu
        });
    }

    private void showUpdateDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = productController.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtName = new JTextField(product.getProductName());
        JTextField txtPrice = new JTextField(product.getPrice() != null ? product.getPrice().toString() : "");
        JTextField txtBarcode = new JTextField(product.getBarcode() != null ? product.getBarcode() : "");
        JTextField txtCategory = new JTextField(product.getCategory() != null ? product.getCategory() : "");
        JTextField txtImage = new JTextField(product.getImage() != null ? product.getImage() : "");
        JTextArea txtDesc = new JTextArea(product.getDescription() != null ? product.getDescription() : "", 3, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Tên sản phẩm:"));
        panel.add(txtName);
        panel.add(new JLabel("Giá bán:"));
        panel.add(txtPrice);
        panel.add(new JLabel("Mã vạch:"));
        panel.add(txtBarcode);
        panel.add(new JLabel("Danh mục:"));
        panel.add(txtCategory);
        panel.add(new JLabel("Hình ảnh (đường dẫn):"));
        panel.add(txtImage);
        panel.add(new JLabel("Mô tả:"));
        panel.add(new JScrollPane(txtDesc));

        int result = JOptionPane.showConfirmDialog(this, panel, "Cập nhật sản phẩm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                product.setProductName(txtName.getText().trim());
                product.setPrice(new java.math.BigDecimal(txtPrice.getText().trim()));
                product.setBarcode(txtBarcode.getText().trim());
                product.setCategory(txtCategory.getText().trim());
                product.setImage(txtImage.getText().trim());
                product.setDescription(txtDesc.getText().trim());
                if (productController.updateProduct(product)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    doSearch();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doSearch() {
        String keyword = txtSearch.getText().trim();
        loadInventoryData(keyword);
    }

    private void loadInventoryData(String keyword) {
        tableModel.setRowCount(0);
        List<Product> products;
        if (keyword == null || keyword.isEmpty()) {
            products = productController.getAllProducts();
        } else {
            products = productController.searchProductsByName(keyword);
        }
        for (Product p : products) {
            int sold = productController.getSoldQuantity(p.getProductId());
            tableModel.addRow(new Object[]{
                p.getProductId(),         // Mã SP
                p.getProductName(),       // Tên sản phẩm
                p.getCategory(),          // Danh mục
                p.getBarcode(),           // Mã vạch
                p.getPrice(),             // Giá
                p.getQuantity(),          // Số lượng còn
                sold                      // Số lượng đã bán
            });
        }
    }

    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                List<Product> products = com.salesmate.utils.ExcelImporter.importProducts(file);
                int success = 0, fail = 0, updated = 0;
                for (Product p : products) {
                    // Kiểm tra sản phẩm đã tồn tại chưa (theo barcode)
                    Product existing = productController.getProductByBarcode(p.getBarcode());
                    if (existing != null) {
                        // Nếu đã có, cập nhật số lượng tồn kho
                        int newQuantity = existing.getQuantity() + p.getQuantity();
                        existing.setQuantity(newQuantity);
                        if (productController.updateProduct(existing)) {
                            updated++;
                        } else {
                            fail++;
                        }
                    } else {
                        // Nếu chưa có, thêm mới
                        if (productController.addProduct(p)) {
                            success++;
                        } else {
                            fail++;
                        }
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "Thêm mới: " + success + " sản phẩm\n"
                      + "Cập nhật tồn kho: " + updated + " sản phẩm\n"
                      + (fail > 0 ? "Không thành công: " + fail + " sản phẩm" : ""),
                        "Kết quả nhập Excel",
                        fail > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
                doSearch();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi nhập Excel: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo sản phẩm đã nhập");
        fileChooser.setSelectedFile(new java.io.File("bao_cao_san_pham_nhap.xlsx"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Sản phẩm đã nhập");
                // Header
                org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
                String[] headers = {"Mã SP", "Tên sản phẩm", "Danh mục", "Mã vạch", "Số lượng còn", "Giá bán"};
                for (int i = 0; i < headers.length; i++) {
                    header.createCell(i).setCellValue(headers[i]);
                }
                // Data
                List<Product> products = productController.getAllProducts();
                for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(p.getProductId());
                    row.createCell(1).setCellValue(p.getProductName());
                    row.createCell(2).setCellValue(p.getCategory());
                    row.createCell(3).setCellValue(p.getBarcode());
                    row.createCell(4).setCellValue(p.getQuantity());
                    row.createCell(5).setCellValue(p.getPrice() != null ? p.getPrice().doubleValue() : 0);
                }
                // Auto size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                    workbook.write(fos);
                }
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
