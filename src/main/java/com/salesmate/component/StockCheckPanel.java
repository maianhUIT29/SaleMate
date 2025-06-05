package com.salesmate.component;

import com.salesmate.dao.ProductDAO;
import com.salesmate.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

public class StockCheckPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private ProductDAO productDAO = new ProductDAO();
    private JTextField searchField;

    public StockCheckPanel() {
        setLayout(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm tên sản phẩm:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{"ID", "Tên sản phẩm", "Tồn kho", "Đã bán"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Thêm nút cập nhật sản phẩm
        JButton updateButton = new JButton("Cập nhật sản phẩm");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> showUpdateProductDialog());

        // Load all data initially
        loadStockData("");

        // Add search listener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String keyword = searchField.getText().trim();
                loadStockData(keyword);
            }
        });

        // Kiểm tra sản phẩm sắp hết khi mở panel
        notifyLowStockProducts();
    }

    private void loadStockData(String keyword) {
        model.setRowCount(0);
        List<Product> products = productDAO.searchProductsByName(keyword);
        for (Product p : products) {
            int sold = productDAO.getSoldQuantity(p.getProductId());
            model.addRow(new Object[]{
                p.getProductId(),
                p.getProductName(),
                p.getQuantity(),
                sold
            });
        }
    }

    private void notifyLowStockProducts() {
        int threshold = 10; // hoặc cho phép cấu hình
        List<Product> lowStock = productDAO.getLowStockProducts(threshold);
        if (!lowStock.isEmpty()) {
            StringBuilder msg = new StringBuilder("Các sản phẩm sắp hết hàng:\n");
            for (Product p : lowStock) {
                msg.append("- ").append(p.getProductName()).append(" (Còn lại: ").append(p.getQuantity()).append(")\n");
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "Cảnh báo tồn kho thấp", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showUpdateProductDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int productId = (int) model.getValueAt(selectedRow, 0);
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(product.getProductName());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        //JTextField descField = new JTextField(product.getDescription());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên sản phẩm:"));
        panel.add(nameField);
        panel.add(new JLabel("Giá bán:"));
        panel.add(priceField);
        panel.add(new JLabel("Mô tả:"));
        //panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Cập nhật sản phẩm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                product.setProductName(nameField.getText().trim());
                product.setPrice(BigDecimal.valueOf(Double.parseDouble(priceField.getText().trim())));
                //product.setDescription(descField.getText().trim());
                boolean updated = productDAO.updateProductInfo(product);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadStockData(searchField.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
