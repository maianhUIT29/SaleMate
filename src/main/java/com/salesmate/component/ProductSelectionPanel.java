package com.salesmate.component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.salesmate.model.Product;

public class ProductSelectionPanel extends javax.swing.JPanel {

    private List<Product> products;
    private List<Product> filteredProducts;
    private CheckoutPanel checkoutPanel;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox<String> priceFilter;
    private javax.swing.JComboBox<String> quantityFilter;
    private javax.swing.JComboBox<String> categoryFilter; // Thêm biến thành viên mới
    private javax.swing.JButton resetButton; // Thêm biến thành viên mới
    private javax.swing.JPanel filterPanel;

    // Thêm các biến thành viên mới
    private int currentPage = 1;
    private int productsPerPage = 30; // 6 cột x 5 dòng
    private javax.swing.JPanel paginationPanel;
    private static final int MAX_PAGE_BUTTONS = 5; // Số nút trang tối đa hiển thị

    public ProductSelectionPanel() {
        initComponents();
        setLayout(new java.awt.BorderLayout());
        setupFilterPanel();
        // Bỏ gọi displayProducts() ở đây vì chưa có data
    }

    public void setProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        this.products = products;
        this.filteredProducts = new ArrayList<>(products);
        currentPage = 1; // Reset về trang đầu tiên

        // Reset các filter về trạng thái mặc định
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        priceFilter.setSelectedIndex(0);
        quantityFilter.setSelectedIndex(0);

        // Hiển thị toàn bộ sản phẩm
        displayFilteredProducts(this.products);
    }

    public void setCheckoutPanel(CheckoutPanel checkoutPanel) {
        this.checkoutPanel = checkoutPanel;
    }

    private void setupFilterPanel() {
        // Tạo filter panel với màu nền và kích thước cố định
        filterPanel = new javax.swing.JPanel();
        filterPanel.setBackground(new java.awt.Color(255, 255, 255));
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(230, 230, 230)),
                javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setPreferredSize(new java.awt.Dimension(getWidth(), 60));
        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        // Style cho search field
        searchField = new javax.swing.JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm sản phẩm...");
        searchField.setPreferredSize(new java.awt.Dimension(200, 30));
        searchField.setMinimumSize(new java.awt.Dimension(200, 30));
        searchField.setMaximumSize(new java.awt.Dimension(200, 30));

        // Style cho price filter
        priceFilter = new javax.swing.JComboBox<>(new String[]{
            "Tất cả",
            "Dưới 100,000đ",
            "100,000đ - 500,000đ",
            "500,000đ - 1,000,000đ",
            "Trên 1,000,000đ"
        });
        priceFilter.setPreferredSize(new java.awt.Dimension(150, 30));

        // Style cho quantity filter
        quantityFilter = new javax.swing.JComboBox<>(new String[]{
            "Tất cả",
            "Hết hàng",
            "Sắp hết (< 10)",
            "Còn hàng (≥ 10)"
        });
        quantityFilter.setPreferredSize(new java.awt.Dimension(120, 30));

        // Thêm category filter
        categoryFilter = new javax.swing.JComboBox<>(new String[]{
            "Tất cả",
            "Đồ uống",
            "Thức ăn",
            "Điện thoại",
            "Máy tính"
        });
        categoryFilter.setPreferredSize(new java.awt.Dimension(120, 30));
        
        // Tạo nút reset
        resetButton = new javax.swing.JButton("Đặt lại");
        resetButton.setPreferredSize(new java.awt.Dimension(80, 30));
        resetButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        resetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Style cho labels
        javax.swing.JLabel searchLabel = new javax.swing.JLabel("Tìm kiếm:");
        javax.swing.JLabel priceLabel = new javax.swing.JLabel("Giá:");
        javax.swing.JLabel quantityLabel = new javax.swing.JLabel("Số lượng:");
        javax.swing.JLabel categoryLabel = new javax.swing.JLabel("Danh mục:");
        java.awt.Font labelFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12);
        searchLabel.setFont(labelFont);
        priceLabel.setFont(labelFont);
        quantityLabel.setFont(labelFont);
        categoryLabel.setFont(labelFont);

        // Thêm các components vào filter panel theo thứ tự mới
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(categoryLabel);
        filterPanel.add(categoryFilter);
        filterPanel.add(priceLabel);
        filterPanel.add(priceFilter);
        filterPanel.add(quantityLabel);
        filterPanel.add(quantityFilter);
        filterPanel.add(resetButton);

        // Thêm listeners chỉ khi người dùng chủ động thay đổi filter
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                if (!searchField.getText().isEmpty()) {
                    applyFilters();
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (!searchField.getText().isEmpty()) {
                    applyFilters();
                }
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (searchField.getText().isEmpty()) {
                    displayFilteredProducts(products);
                } else {
                    applyFilters();
                }
            }
        });

        quantityFilter.addActionListener(e -> {
            if (!"Tất cả số lượng".equals(quantityFilter.getSelectedItem())) {
                applyFilters();
            } else if (searchField.getText().isEmpty() && "Tất cả giá".equals(priceFilter.getSelectedItem()) && "Tất cả danh mục".equals(categoryFilter.getSelectedItem())) {
                displayFilteredProducts(products);
            }
        });

        priceFilter.addActionListener(e -> {
            if (!"Tất cả giá".equals(priceFilter.getSelectedItem())) {
                applyFilters();
            } else if (searchField.getText().isEmpty() && "Tất cả số lượng".equals(quantityFilter.getSelectedItem()) && "Tất cả danh mục".equals(categoryFilter.getSelectedItem())) {
                displayFilteredProducts(products);
            }
        });

        categoryFilter.addActionListener(e -> {
            if (!"Tất cả danh mục".equals(categoryFilter.getSelectedItem())) {
                applyFilters();
            } else if (searchField.getText().isEmpty() && 
                     "Tất cả giá".equals(priceFilter.getSelectedItem()) && 
                     "Tất cả số lượng".equals(quantityFilter.getSelectedItem())) {
                displayFilteredProducts(products);
            }
        });

        resetButton.addActionListener(e -> {
            resetFilters();
        });

        add(filterPanel, java.awt.BorderLayout.NORTH);
    }

    private void applyFilters() {
        if (products == null) {
            return;
        }

        filteredProducts = new ArrayList<>(products);
        String searchText = searchField.getText().toLowerCase().trim();
        String category = (String) categoryFilter.getSelectedItem();
        String priceRange = (String) priceFilter.getSelectedItem();
        String quantityRange = (String) quantityFilter.getSelectedItem();

        // Lọc theo tên
        if (!searchText.isEmpty()) {
            filteredProducts.removeIf(p -> !p.getProductName().toLowerCase().contains(searchText));
        }

        // Lọc theo danh mục
        if (!"Tất cả danh mục".equals(category)) {
            filteredProducts.removeIf(p -> !matchesCategory(p, category));
        }

        // Lọc theo giá
        if (!"Tất cả giá".equals(priceRange)) {
            filteredProducts.removeIf(p -> !matchesPriceRange(p.getPrice().doubleValue(), priceRange));
        }

        // Lọc theo số lượng
        if (!"Tất cả số lượng".equals(quantityRange)) {
            filteredProducts.removeIf(p -> !matchesQuantityRange(p.getQuantity(), quantityRange));
        }

        // Hiển thị kết quả
        displayFilteredProducts(filteredProducts);
    }

    private boolean matchesPriceRange(double price, String range) {
        switch (range) {
            case "Dưới 100,000đ":
                return price < 100000;
            case "100,000đ - 500,000đ":
                return price >= 100000 && price <= 500000;
            case "500,000đ - 1,000,000đ":
                return price > 500000 && price <= 1000000;
            case "Trên 1,000,000đ":
                return price > 1000000;
            default:
                return true;
        }
    }

    private boolean matchesQuantityRange(int quantity, String range) {
        switch (range) {
            case "Hết hàng":
                return quantity == 0;
            case "Sắp hết (< 10)":
                return quantity > 0 && quantity < 10;
            case "Còn hàng (≥ 10)":
                return quantity >= 10;
            default:
                return true;
        }
    }

    private boolean matchesCategory(Product p, String category) {
        // TODO: Implement category matching logic when you have category in Product model
        return true; // Placeholder return
    }

    private void displayFilteredProducts(List<Product> filteredProducts) {
        if (filteredProducts == null || filteredProducts.isEmpty()) {
            System.out.println("Không có sản phẩm để hiển thị");
            return;
        }

        System.out.println("Đang hiển thị " + filteredProducts.size() + " sản phẩm");

        // Tạo main container với BorderLayout
        JPanel mainContainer = new JPanel(new java.awt.BorderLayout());
        mainContainer.setBackground(new java.awt.Color(245, 245, 245));

        // Tạo product container với GridBagLayout như cũ
        JPanel productContainer = new JPanel();
        productContainer.setLayout(new java.awt.GridBagLayout());
        productContainer.setBackground(new java.awt.Color(245, 245, 245));
        productContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Giảm padding container

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8); // Tăng spacing giữa các card
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;

        // Tính toán số cột và kích thước card với spacing mới
        int numColumns = 6;
        int spacing = 16; // Khoảng cách giữa các card
        int availableWidth = getWidth() - (spacing * (numColumns + 1)); // Trừ đi tổng spacing
        int cardWidth = availableWidth / numColumns;
        int cardHeight = (int)(cardWidth * 1.4);

        // Tính toán phân trang
        int totalProducts = filteredProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);
        int startIndex = (currentPage - 1) * productsPerPage;
        int endIndex = Math.min(startIndex + productsPerPage, totalProducts);

        // Lấy sản phẩm cho trang hiện tại
        List<Product> currentPageProducts = filteredProducts.subList(startIndex, endIndex);

        int row = 0;
        int col = 0;

        for (Product product : currentPageProducts) {
            ProductCard productCard = new ProductCard();
            productCard.setPreferredSize(new java.awt.Dimension(cardWidth, cardHeight));
            productCard.setProductDetails(product);
            productCard.setProductCardListener(selectedProduct -> {
                if (checkoutPanel != null) {
                    checkoutPanel.addProductToCheckout(selectedProduct);
                }
            });

            gbc.gridx = col;
            gbc.gridy = row;
            productContainer.add(productCard, gbc);

            col++;
            if (col >= numColumns) {
                col = 0;
                row++;
            }
        }

        // Đưa product container vào scroll pane
        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(java.awt.Color.WHITE);

        // Tạo panel phân trang với border phía trên
        paginationPanel = new javax.swing.JPanel();
        paginationPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 5));
        paginationPanel.setBackground(java.awt.Color.WHITE);
        paginationPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(230, 230, 230)));
        paginationPanel.setPreferredSize(new java.awt.Dimension(getWidth(), 50));

        // Thêm nút Previous
        javax.swing.JButton prevButton = new javax.swing.JButton("<<");
        prevButton.setEnabled(currentPage > 1);
        prevButton.addActionListener(e -> {
            currentPage--;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(prevButton);

        // Tính toán range của các nút trang
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + MAX_PAGE_BUTTONS - 1);
        startPage = Math.max(1, endPage - MAX_PAGE_BUTTONS + 1);

        // Thêm nút trang đầu tiên nếu cần
        if (startPage > 1) {
            addPageButton(1, filteredProducts);
            if (startPage > 2) {
                JLabel dots = new JLabel("...");
                dots.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                paginationPanel.add(dots);
            }
        }

        // Thêm các nút trang chính
        for (int i = startPage; i <= endPage; i++) {
            addPageButton(i, filteredProducts);
        }

        // Thêm nút trang cuối cùng nếu cần
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                JLabel dots = new JLabel("...");
                dots.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                paginationPanel.add(dots);
            }
            addPageButton(totalPages, filteredProducts);
        }

        // Thêm nút Next
        javax.swing.JButton nextButton = new javax.swing.JButton(">>");
        nextButton.setEnabled(currentPage < totalPages);
        nextButton.addActionListener(e -> {
            currentPage++;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(nextButton);

        // Thêm các components vào main container
        mainContainer.add(scrollPane, java.awt.BorderLayout.CENTER);
        mainContainer.add(paginationPanel, java.awt.BorderLayout.SOUTH);

        // Cập nhật UI
        if (getComponentCount() > 1) {
            remove(1);
        }
        add(mainContainer, java.awt.BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // Thêm phương thức để thêm nút trang
    private void addPageButton(int pageNum, List<Product> filteredProducts) {
        javax.swing.JButton pageButton = new javax.swing.JButton(String.valueOf(pageNum));
        if (pageNum == currentPage) {
            pageButton.setBackground(new java.awt.Color(46, 125, 50));
            pageButton.setForeground(java.awt.Color.WHITE);
        }
        pageButton.addActionListener(e -> {
            currentPage = pageNum;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(pageButton);
    }

    private void resetFilters() {
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        priceFilter.setSelectedIndex(0);
        quantityFilter.setSelectedIndex(0);
        currentPage = 1; // Reset về trang đầu tiên khi reset filter
        if (products != null) {
            displayFilteredProducts(products);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setForeground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(600, 500));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 601, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 500, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
