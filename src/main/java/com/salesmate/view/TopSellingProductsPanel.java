package com.salesmate.view;

import com.salesmate.dao.ProductDAO;
import com.salesmate.model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
//import java.awt.*;
import java.util.List;

public class TopSellingProductsPanel extends JPanel {
    private ProductDAO productDAO;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JSpinner limitSpinner;
    private JButton refreshButton;

    public TopSellingProductsPanel() {
        productDAO = new ProductDAO();
        
        // Main Panel
        setLayout(new GroupLayout(this));
        GroupLayout layout = (GroupLayout) getLayout();
        
        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GroupLayout(controlPanel));
        GroupLayout controlLayout = (GroupLayout) controlPanel.getLayout();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel limitLabel = new JLabel("Số lượng sản phẩm hiển thị:");
        limitSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        refreshButton = new JButton("Làm mới");
        
        // Control Panel Layout
        controlLayout.setHorizontalGroup(
            controlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(controlLayout.createSequentialGroup()
                .addComponent(limitLabel)
                .addGap(10)
                .addComponent(limitSpinner, 100, 100, 100)
                .addGap(10)
                .addComponent(refreshButton)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        
        controlLayout.setVerticalGroup(
            controlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(controlLayout.createSequentialGroup()
                .addGroup(controlLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(limitLabel)
                    .addComponent(limitSpinner)
                    .addComponent(refreshButton))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        
        // Table
        String[] columnNames = {"ID", "Tên sản phẩm", "Giá", "Số lượng", "Mã vạch"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Main Panel Layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        // Add action listener
        refreshButton.addActionListener(e -> refreshTable());
        
        // Initial load
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        int limit = (Integer) limitSpinner.getValue();
        List<Product> products = productDAO.getTopSellingProducts(limit);
        
        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getBarcode()
            };
            tableModel.addRow(row);
        }
    }
    
    // Phương thức để tạo frame từ panel (nếu cần)
    public static JFrame createFrame() {
        JFrame frame = new JFrame("Sản phẩm bán chạy nhất");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new TopSellingProductsPanel());
        return frame;
    }
    
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TopSellingProductsPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        SwingUtilities.invokeLater(() -> {
            createFrame().setVisible(true);
        });
    }
}