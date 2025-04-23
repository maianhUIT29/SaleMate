package com.salesmate.component;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryManagementPanel extends JPanel {
    private ProductController productController;

    public InventoryManagementPanel() {
        productController = new ProductController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        List<Product> inventoryProducts = productController.getInventoryForecast();
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên sản phẩm", "Số lượng", "Giá", "Dự báo tồn kho"}, 0);

        for (Product product : inventoryProducts) {
            String forecast = product.getQuantity() > 10 ? "Đủ hàng" : "Cần nhập thêm";

            model.addRow(new Object[]{
                    product.getProductId(),
                    product.getProductName(),
                    product.getQuantity(),
                    product.getPrice(),
                    forecast
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}