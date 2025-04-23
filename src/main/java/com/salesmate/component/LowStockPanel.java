package com.salesmate.component;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LowStockPanel extends JPanel {
    private ProductController productController;

    public LowStockPanel() {
        productController = new ProductController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        List<Product> lowStockProducts = productController.getLowStockProducts();
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên sản phẩm", "Số lượng", "Giá"}, 0);

        if (lowStockProducts.isEmpty()) {
            model.addRow(new Object[]{"Không có sản phẩm", "", "", ""});
        } else {
            for (Product product : lowStockProducts) {
                model.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getQuantity(),
                        product.getPrice()
                });
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}