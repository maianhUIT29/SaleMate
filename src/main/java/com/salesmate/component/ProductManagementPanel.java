package com.salesmate.component;

import com.salesmate.model.Product;
import com.salesmate.controller.ProductController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private ProductController productController;

    public ProductManagementPanel() {
        productController = new ProductController();
        initComponents();
        loadProducts(); // Tải dữ liệu ngay khi khởi tạo
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterComboBox = new JComboBox<>(new String[]{"Tất cả", "Còn hàng (≥ 10)", "Sắp hết (< 10)", "Hết hàng (0)"});
        filterComboBox.addActionListener(e -> applyFilter());
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        filterPanel.add(filterComboBox);
        add(filterPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Tên sản phẩm", "Giá", "Số lượng", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // CRUD Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");

        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> editProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        List<Product> products = productController.getAllProducts();
        updateTable(products);
    }

    private void applyFilter() {
        String filter = (String) filterComboBox.getSelectedItem();
        List<Product> products = productController.getAllProducts();

        if ("Còn hàng (≥ 10)".equals(filter)) {
            products.removeIf(p -> p.getQuantity() < 10);
        } else if ("Sắp hết (< 10)".equals(filter)) {
            products.removeIf(p -> p.getQuantity() >= 10 || p.getQuantity() == 0);
        } else if ("Hết hàng (0)".equals(filter)) {
            products.removeIf(p -> p.getQuantity() > 0);
        }

        updateTable(products);
    }

    private void updateTable(List<Product> products) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                String status = product.getQuantity() == 0 ? "Hết hàng"
                        : product.getQuantity() < 10 ? "Sắp hết" : "Còn hàng";
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getQuantity(),
                        status
                });
            }
        }
    }

    private void addProduct() {
        // Hiển thị form thêm sản phẩm
        JOptionPane.showMessageDialog(this, "Chức năng thêm sản phẩm!");
    }

    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!");
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        // Hiển thị form sửa sản phẩm
        JOptionPane.showMessageDialog(this, "Chức năng sửa sản phẩm với ID: " + productId);
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productController.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại!");
            }
        }
    }
}