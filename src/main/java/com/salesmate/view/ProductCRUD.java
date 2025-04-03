package com.salesmate.view;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ProductCRUD {
    private ProductController controller;
    private JTable productTable;
    private JButton btnNew;
    private JButton btnUpdate;
    private JButton btnDelete;

    public ProductCRUD(JTable productTable, JButton btnNew, JButton btnUpdate, JButton btnDelete) {
        this.productTable = productTable;
        this.btnNew = btnNew;
        this.btnUpdate = btnUpdate;
        this.btnDelete = btnDelete;

        controller = new ProductController();

        initListeners();
        loadProductTable();
    }

    private void initListeners() {
        btnNew.addActionListener(e -> handleNew());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
    }

    // Tải lại bảng sản phẩm
    private void loadProductTable() {
        List<Product> products = controller.getAllProducts();
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);

        for (Product p : products) {
            model.addRow(new Object[] {
                p.getProductId(), p.getProductName(), p.getPrice(),
                p.getQuantity(), p.getBarcode(), p.getImage()
            });
        }
    }

    // Tạo một dòng mới cho người dùng nhập liệu
    private void handleNew() {
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.addRow(new Object[] {null, "", BigDecimal.ZERO, 0, "", ""});
    }

    // Lưu sản phẩm mới hoặc cập nhật sản phẩm đã chọn
    private void handleUpdate() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Chọn một dòng để sửa.");
            return;
        }

        try {
            Integer id = productTable.getValueAt(row, 0) != null ? (Integer) productTable.getValueAt(row, 0) : null;
            String name = (String) productTable.getValueAt(row, 1);
            BigDecimal price = new BigDecimal(productTable.getValueAt(row, 2).toString());
            int quantity = Integer.parseInt(productTable.getValueAt(row, 3).toString());
            String barcode = (String) productTable.getValueAt(row, 4);
            String image = (String) productTable.getValueAt(row, 5);

            // Kiểm tra ràng buộc
            if (name.isEmpty() || barcode.isEmpty() || image.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Các trường không được để trống.");
                return;
            }

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(null, "Giá sản phẩm phải lớn hơn 0.");
                return;
            }

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0.");
                return;
            }

            Product p = new Product(id != null ? id : 0, name, price, quantity, barcode, image);

            boolean success = (id == null) ? controller.addProduct(p) : controller.updateProduct(p);
            JOptionPane.showMessageDialog(null, success ? "Cập nhật thành công" : "Lỗi khi cập nhật sản phẩm");
            loadProductTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Dữ liệu không hợp lệ: " + ex.getMessage());
        }
    }

    // Xóa sản phẩm đã chọn
    private void handleDelete() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Chọn một dòng để xóa.");
            return;
        }

        int productId = (Integer) productTable.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(null,
                "Xóa sản phẩm ID=" + productId + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteProduct(productId);
            JOptionPane.showMessageDialog(null, success ? "Xóa thành công" : "Lỗi khi xóa sản phẩm");
            loadProductTable();
        }
    }
}

