package com.salesmate.component;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import com.salesmate.utils.BarcodeGenerator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.table.TableRowSorter;
import com.google.zxing.WriterException;

public class AdProductPanel extends javax.swing.JPanel {
    private ProductController productController;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JButton generateBarcodeButton;
    private JButton refreshButton;

    public AdProductPanel() {
        productController = new ProductController();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Create top panel for buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generateBarcodeButton = new JButton("Generate Barcode");
        refreshButton = new JButton("Refresh");
        
        generateBarcodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBarcodes();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProducts();
            }
        });
        
        topPanel.add(generateBarcodeButton);
        topPanel.add(refreshButton);

        // Create table model
        String[] columns = {"ID", "Name", "Price", "Quantity", "Barcode", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
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

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProducts() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get products from controller
        List<Product> products = productController.getAllProducts();

        // Add products to table
        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getBarcode(),
                product.getCategory()
            };
            tableModel.addRow(row);
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

        String message = String.format("Generated %d barcodes successfully.\nFailed to generate %d barcodes.", 
                                     successCount, failCount);
        JOptionPane.showMessageDialog(this, message, "Barcode Generation", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
}
