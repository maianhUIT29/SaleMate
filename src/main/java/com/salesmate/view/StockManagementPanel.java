package com.salesmate.view;

import com.salesmate.dao.StockManagementDAO;
import com.salesmate.model.Product;
import com.salesmate.utils.ColorPalette;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockManagementPanel extends JPanel {
    private StockManagementDAO stockManagementDAO;
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JLabel warningLabel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    public StockManagementPanel() {
        stockManagementDAO = new StockManagementDAO();
        initializeUI();
        loadStockData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorPalette.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Title
        JLabel titleLabel = new JLabel("Quản lý tồn kho");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Warning label
        warningLabel = new JLabel();
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(warningLabel, BorderLayout.CENTER);

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterProducts());

        // Filter combo box
        String[] filterOptions = {"Tất cả", "Sắp hết hàng", "Còn hàng"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        filterComboBox.addActionListener(e -> filterProducts());

        // Search button
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(ColorPalette.PRIMARY);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.addActionListener(e -> filterProducts());

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Lọc:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);

        // Table setup
        String[] columnNames = {"Mã SP", "Tên sản phẩm", "Số lượng tồn", "Giá", "Mã vạch", "Thao tác"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        stockTable = new JTable(tableModel);
        stockTable.setRowHeight(35);
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        stockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        stockTable.setBackground(Color.WHITE);
        stockTable.setGridColor(Color.LIGHT_GRAY);
        stockTable.setSelectionBackground(ColorPalette.SECONDARY);
        stockTable.setSelectionForeground(Color.WHITE);

        // Custom renderer for table cells
        stockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int quantity = Integer.parseInt(table.getValueAt(row, 2).toString());
                if (quantity <= 10) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else if (quantity <= 20) {
                    c.setBackground(Color.YELLOW);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(isSelected ? ColorPalette.SECONDARY : Color.WHITE);
                    c.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                }
                return c;
            }
        });

        // Update button renderer
        stockTable.getColumnModel().getColumn(5).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("Cập nhật");
            button.setBackground(ColorPalette.PRIMARY);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                int productId = (int) table.getValueAt(row, 0);
                String productName = (String) table.getValueAt(row, 1);
                int currentQuantity = (int) table.getValueAt(row, 2);
                
                String newQuantityStr = JOptionPane.showInputDialog(
                    this,
                    "Nhập số lượng mới cho sản phẩm " + productName + " (Hiện tại: " + currentQuantity + "):",
                    "Cập nhật số lượng",
                    JOptionPane.PLAIN_MESSAGE
                );
                
                if (newQuantityStr != null && !newQuantityStr.isEmpty()) {
                    try {
                        int newQuantity = Integer.parseInt(newQuantityStr);
                        if (stockManagementDAO.updateStockQuantity(productId, newQuantity)) {
                            JOptionPane.showMessageDialog(
                                this,
                                "Cập nhật số lượng thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            loadStockData();
                        } else {
                            JOptionPane.showMessageDialog(
                                this,
                                "Cập nhật số lượng thất bại!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Vui lòng nhập số hợp lệ!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });
            return button;
        });

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(ColorPalette.PRIMARY);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadStockData());

        buttonPanel.add(refreshButton);

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase();
        String filterOption = (String) filterComboBox.getSelectedItem();
        
        tableModel.setRowCount(0);
        List<Product> products = stockManagementDAO.getAllProducts();
        
        for (Product product : products) {
            boolean matchesSearch = product.getProductName().toLowerCase().contains(searchText) ||
                                  product.getBarcode().toLowerCase().contains(searchText);
            
            boolean matchesFilter = true;
            if (filterOption.equals("Sắp hết hàng")) {
                matchesFilter = product.getQuantity() <= 20;
            } else if (filterOption.equals("Còn hàng")) {
                matchesFilter = product.getQuantity() > 20;
            }
            
            if (matchesSearch && matchesFilter) {
                Object[] row = {
                    product.getProductId(),
                    product.getProductName(),
                    product.getQuantity(),
                    product.getPrice(),
                    product.getBarcode(),
                    "Cập nhật"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void loadStockData() {
        tableModel.setRowCount(0);
        List<Product> products = stockManagementDAO.getAllProducts();
        List<Product> lowStockProducts = stockManagementDAO.getLowStockProducts();

        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getQuantity(),
                product.getPrice(),
                product.getBarcode(),
                "Cập nhật"
            };
            tableModel.addRow(row);
        }

        if (!lowStockProducts.isEmpty()) {
            warningLabel.setText("Cảnh báo: Có " + lowStockProducts.size() + " sản phẩm sắp hết hàng!");
        } else {
            warningLabel.setText("");
        }
    }
} 