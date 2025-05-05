package com.salesmate.component;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;

public class ProductSelectionPanel extends javax.swing.JPanel {

    private List<Product> products;
    private List<Product> filteredProducts;
    private CheckoutPanel checkoutPanel;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox<String> priceFilter;
    private javax.swing.JComboBox<String> quantityFilter;
    private javax.swing.JComboBox<String> categoryFilter;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel filterPanel;
    private int currentPage = 1;
    private int productsPerPage = 30; // 6 cột x 5 dòng
    private javax.swing.JPanel paginationPanel;
    private static final int MAX_PAGE_BUTTONS = 5; // Số nút trang tối đa hiển thị
    private boolean isLoading = false; // Add flag to track loading state
    private ProductController productController; // Add this field

    public ProductSelectionPanel() {
        initComponents();
        setLayout(new java.awt.BorderLayout());
        // chỉ chạy khi thực sự chạy chương trình
        if (!java.beans.Beans.isDesignTime()) {
            productController = new ProductController(); // Initialize ProductController
            setupFilterPanel();
            // Remove the automatic loading animation from constructor
            // The animation will only show when setProducts is called
        }
    }

    private int spinnerAngle = 0;

    private void showLoadingAnimation() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel loadingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Vẽ nền mờ đen
                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Toast background với shadow 
                int width = 350;  // Tăng width
                int height = 150; // Tăng height
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;

                // Vẽ shadow cho khung loading
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(x + 5, y + 5, width, height, 20, 20);

                // Vẽ background khung loading
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y, width, height, 20, 20);

                // Vẽ spinner với animation
                int spinnerSize = 40;
                int spinnerX = x + (width - spinnerSize) / 2;
                int spinnerY = y + height / 2 - 30;

                g2d.setColor(new Color(0, 123, 255));
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                for (int i = 0; i < 12; i++) {
                    float scale = (float) ((12 - i) % 12) / 12.0f;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scale));

                    double angle = Math.toRadians(spinnerAngle + i * 30);
                    int x1 = spinnerX + spinnerSize / 2 + (int) (spinnerSize / 3 * Math.cos(angle));
                    int y1 = spinnerY + spinnerSize / 2 + (int) (spinnerSize / 3 * Math.sin(angle));
                    int x2 = spinnerX + spinnerSize / 2 + (int) (spinnerSize / 2 * Math.cos(angle));
                    int y2 = spinnerY + spinnerSize / 2 + (int) (spinnerSize / 2 * Math.sin(angle));

                    g2d.drawLine(x1, y1, x2, y2);
                }

                // Text với font lớn và căn giữa
                String text = "Đang tải dữ liệu...";
                g2d.setColor(new Color(33, 33, 33));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));

                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (width - fm.stringWidth(text)) / 2;
                int textY = y + height / 2 + 35;

                g2d.setColor(new Color(33, 33, 33, 240));
                g2d.drawString(text, textX, textY);

                g2d.dispose();
            }
        };
        loadingPanel.setOpaque(false);

        mainPanel.add(loadingPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Animation spinner
        Timer spinnerTimer = new Timer(50, e -> {
            spinnerAngle = (spinnerAngle + 30) % 360;
            loadingPanel.repaint();
        });
        spinnerTimer.start();

        // Tự động ẩn loading sau 2 giây và hiển thị dữ liệu nếu có
        Timer removeTimer = new Timer(2000, e -> {
            remove(mainPanel);
            spinnerTimer.stop();
            revalidate();
            repaint();
            showSuccessToast();
            
            // Reset loading flag when animation is done
            isLoading = false;
            
            // Tự động hiển thị dữ liệu nếu có
            if (products != null && !products.isEmpty()) {
                displayFilteredProducts(products);
            }
        });
        removeTimer.setRepeats(false);
        removeTimer.start();
    }

    private void showSuccessToast() {
        JPanel successPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ background cho toast
                int width = 200;
                int height = 60;
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;

                g2d.setColor(new Color(46, 125, 50, 220));
                g2d.fillRoundRect(x, y, width, height, 20, 20);

                // Vẽ text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2d.drawString("Tải dữ liệu thành công!", x + 30, y + height / 2 + 5);

                g2d.dispose();
            }
        };
        successPanel.setOpaque(false);

        JPanel overlayPanel = new JPanel(new BorderLayout());
        overlayPanel.setOpaque(false);
        overlayPanel.add(successPanel, BorderLayout.CENTER);

        add(overlayPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        // Remove success toast after delay
        Timer removeTimer = new Timer(1500, e -> {
            remove(overlayPanel);
            revalidate();
            repaint();
        });
        removeTimer.setRepeats(false);
        removeTimer.start();
    }

    public void setProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        this.products = products;
        this.filteredProducts = new ArrayList<>(products);
        currentPage = 1;
        
        // Calculate price ranges and populate category list
        updateFilterOptions(products);
        
        // Nếu không đang ở design time và chưa hiển thị loading, thì hiển thị loading animation
        if (!java.beans.Beans.isDesignTime() && !isLoading) {
            isLoading = true; // Set loading flag to prevent multiple animations
            showLoadingAnimation(); // Loading animation sẽ tự động hiển thị dữ liệu sau khi hoàn thành
        } else if (java.beans.Beans.isDesignTime()) {
            displayFilteredProducts(products); // Hiển thị trực tiếp khi ở design time
        }
    }

    // New method to calculate price ranges and categories
    private void updateFilterOptions(List<Product> products) {
        // Sort products by price to determine price ranges
        List<Product> sortedByPrice = new ArrayList<>(products);
        Collections.sort(sortedByPrice, Comparator.comparing(Product::getPrice));
        
        // Get minimum and maximum prices
        BigDecimal minPrice = sortedByPrice.get(0).getPrice();
        BigDecimal maxPrice = sortedByPrice.get(sortedByPrice.size() - 1).getPrice();
        
        // Create dynamic price ranges
        String[] priceRanges;
        if (maxPrice.compareTo(new BigDecimal(1000000)) > 0) {
            priceRanges = new String[]{
                "Tất cả",
                "Dưới 100,000đ",
                "100,000đ - 500,000đ",
                "500,000đ - 1,000,000đ",
                "1,000,000đ - 5,000,000đ",
                "Trên 5,000,000đ"
            };
        } else if (maxPrice.compareTo(new BigDecimal(500000)) > 0) {
            priceRanges = new String[]{
                "Tất cả",
                "Dưới 50,000đ",
                "50,000đ - 200,000đ",
                "200,000đ - 500,000đ",
                "Trên 500,000đ"
            };
        } else {
            priceRanges = new String[]{
                "Tất cả",
                "Dưới 20,000đ",
                "20,000đ - 50,000đ",
                "50,000đ - 100,000đ",
                "Trên 100,000đ"
            };
        }
        
        // Update price filter combobox
        if (priceFilter != null) {
            priceFilter.setModel(new DefaultComboBoxModel<>(priceRanges));
        }
        
        // Load categories from database instead of extracting from products
        loadCategoriesFromDatabase();
    }
    
    private void loadCategoriesFromDatabase() {
        try {
            // Clear any existing debug output
            System.out.println("Loading categories from database...");
            
            // Get categories directly using the ProductController
            List<String> categoriesList = productController.getProductCategories();
            
            // Debug print the categories
            System.out.println("Retrieved " + (categoriesList != null ? categoriesList.size() : 0) + " categories");
            if (categoriesList != null) {
                for (String cat : categoriesList) {
                    System.out.println(" - Category: " + cat);
                }
            }
            
            // Ensure we have valid data
            if (categoriesList == null || categoriesList.isEmpty()) {
                System.out.println("No categories found, using default 'All' only");
                categoriesList = new ArrayList<>();
                categoriesList.add("Tất cả");
            }
            
            // Convert the list to an array for the combobox and ensure it's correctly populated
            String[] categories = categoriesList.toArray(new String[0]);
            
            // Update the category filter combobox on the EDT
            SwingUtilities.invokeLater(() -> {
                if (categoryFilter != null) {
                    categoryFilter.setModel(new DefaultComboBoxModel<>(categories));
                    System.out.println("Category filter updated with " + categories.length + " items");
                    categoryFilter.setSelectedIndex(0); // Ensure "Tất cả" is selected
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error loading categories: " + e.getMessage());
            e.printStackTrace();
            
            // Use a simple default model as fallback
            SwingUtilities.invokeLater(() -> {
                if (categoryFilter != null) {
                    categoryFilter.setModel(new DefaultComboBoxModel<>(new String[]{"Tất cả"}));
                }
            });
        }
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
        priceFilter = new javax.swing.JComboBox<>(new String[]{"Tất cả"});
        priceFilter.setPreferredSize(new java.awt.Dimension(150, 30));

        // Style cho quantity filter
        quantityFilter = new javax.swing.JComboBox<>(new String[]{
            "Tất cả",
            "Hết hàng",
            "Sắp hết (< 10)",
            "Còn hàng (≥ 10)"
        });
        quantityFilter.setPreferredSize(new java.awt.Dimension(120, 30));

        // Thêm category filter - Initialize empty, will be populated from database
        categoryFilter = new javax.swing.JComboBox<>(new String[]{"Tất cả"});
        categoryFilter.setPreferredSize(new java.awt.Dimension(150, 30)); // Increased width for category names
        
        // Load categories immediately
        loadCategoriesFromDatabase();

        // Tạo nút reset
        resetButton = new javax.swing.JButton("Refresh");
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
            if (!"Tất cả".equals(priceFilter.getSelectedItem())) {
                applyFilters();
            } else if (searchField.getText().isEmpty() && "Tất cả số lượng".equals(quantityFilter.getSelectedItem()) && "Tất cả danh mục".equals(categoryFilter.getSelectedItem())) {
                displayFilteredProducts(products);
            }
        });

        categoryFilter.addActionListener(e -> {
            if (!"Tất cả".equals(categoryFilter.getSelectedItem())) {
                applyFilters();
            } else if (searchField.getText().isEmpty()
                    && "Tất cả".equals(priceFilter.getSelectedItem())
                    && "Tất cả số lượng".equals(quantityFilter.getSelectedItem())) {
                displayFilteredProducts(products);
            }
        });

        resetButton.addActionListener(e -> {
            // Enhanced reset button functionality - load fresh data from database
            System.out.println("Reset button clicked, reloading fresh product data");
            loadProductData(); // Use the dedicated method that shows animation
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

        // Lọc theo danh mục - với xử lý đặc biệt cho "Không có danh mục"
        if (!"Tất cả".equals(category)) {
            if ("Không có danh mục".equals(category)) {
                filteredProducts.removeIf(p -> p.getCategory() != null && !p.getCategory().trim().isEmpty());
            } else {
                filteredProducts.removeIf(p -> !matchesCategory(p, category));
            }
        }

        // Lọc theo giá
        if (!"Tất cả".equals(priceRange)) {
            filteredProducts.removeIf(p -> !matchesPriceRange(p.getPrice().doubleValue(), priceRange));
        }

        // Lọc theo số lượng
        if (!"Tất cả".equals(quantityRange)) {
            filteredProducts.removeIf(p -> !matchesQuantityRange(p.getQuantity(), quantityRange));
        }

        // Hiển thị kết quả
        displayFilteredProducts(filteredProducts);
    }

    private boolean matchesPriceRange(double price, String range) {
        if (range.startsWith("Dưới")) {
            String priceStr = range.substring(5, range.length() - 1).replace(",", "");
            return price < Double.parseDouble(priceStr);
        } else if (range.startsWith("Trên")) {
            String priceStr = range.substring(5, range.length() - 1).replace(",", "");
            return price > Double.parseDouble(priceStr);
        } else if (range.contains("-")) {
            String[] parts = range.split("-");
            String lowerStr = parts[0].trim().replace("đ", "").replace(",", "");
            String upperStr = parts[1].trim().replace("đ", "").replace(",", "");
            double lower = Double.parseDouble(lowerStr);
            double upper = Double.parseDouble(upperStr);
            return price >= lower && price <= upper;
        }
        return true;
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
        // "Tất cả" is a special case that matches everything
        if ("Tất cả".equals(category)) {
            return true;
        }
        
        // Get the product category, handle null case
        String productCategory = p.getCategory();
        if (productCategory == null || productCategory.trim().isEmpty()) {
            // For "Không có danh mục" filter special case
            return "Không có danh mục".equals(category);
        }
        
        // Trim and compare case-insensitively
        return category.trim().equalsIgnoreCase(productCategory.trim());
    }

    private void displayFilteredProducts(List<Product> filteredProducts) {
        if (filteredProducts == null || filteredProducts.isEmpty()) {
            return;
        }

        this.filteredProducts = filteredProducts;

        // Tạo main container với BorderLayout
        JPanel mainContainer = new JPanel(new java.awt.BorderLayout());
        mainContainer.setBackground(new java.awt.Color(245, 245, 245));

        // Tạo product container với GridBagLayout
        JPanel productContainer = new JPanel();
        productContainer.setLayout(new java.awt.GridBagLayout());
        productContainer.setBackground(new java.awt.Color(245, 245, 245));
        productContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;

        // Changed from 6 to 5 products per row
        int numColumns = 5;
        int spacing = 16;
        int availableWidth = getWidth() - (spacing * (numColumns + 1));
        int cardWidth = availableWidth / numColumns;
        int cardHeight = (int) (cardWidth * 1.4);

        int totalProducts = filteredProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);
        int startIndex = (currentPage - 1) * productsPerPage;
        int endIndex = Math.min(startIndex + productsPerPage, totalProducts);

        List<Product> currentPageProducts = filteredProducts.subList(startIndex, endIndex);

        int row = 0;
        int col = 0;

        for (Product product : currentPageProducts) {
            ProductCard productCard = new ProductCard();
            productCard.setPreferredSize(new java.awt.Dimension(cardWidth, cardHeight));
            productCard.setProductDetails(product);

            // Set the listener only once
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

        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(java.awt.Color.WHITE);

        paginationPanel = new javax.swing.JPanel();
        paginationPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 5));
        paginationPanel.setBackground(java.awt.Color.WHITE);
        paginationPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(230, 230, 230)));
        paginationPanel.setPreferredSize(new java.awt.Dimension(getWidth(), 50));

        // Add "First Page" button
        javax.swing.JButton firstButton = new javax.swing.JButton("|<");
        firstButton.setOpaque(true);
        firstButton.setFocusPainted(false);
        firstButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        firstButton.setEnabled(currentPage > 1);
        firstButton.addActionListener(e -> {
            currentPage = 1;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(firstButton);

        // Previous button
        javax.swing.JButton prevButton = new javax.swing.JButton("<<");
        prevButton.setOpaque(true);
        prevButton.setFocusPainted(false);
        prevButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        prevButton.setEnabled(currentPage > 1);
        prevButton.addActionListener(e -> {
            currentPage--;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(prevButton);

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + MAX_PAGE_BUTTONS - 1);
        startPage = Math.max(1, endPage - MAX_PAGE_BUTTONS + 1);

        for (int i = startPage; i <= endPage; i++) {
            addPageButton(i, filteredProducts);
        }

        // Next button
        javax.swing.JButton nextButton = new javax.swing.JButton(">>");
        nextButton.setOpaque(true);
        nextButton.setFocusPainted(false);
        nextButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        nextButton.setEnabled(currentPage < totalPages);
        nextButton.addActionListener(e -> {
            currentPage++;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(nextButton);

        // Add "Last Page" button
        javax.swing.JButton lastButton = new javax.swing.JButton(">|");
        lastButton.setOpaque(true);
        lastButton.setFocusPainted(false);
        lastButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        lastButton.setEnabled(currentPage < totalPages);
        lastButton.addActionListener(e -> {
            currentPage = totalPages;
            displayFilteredProducts(filteredProducts);
        });
        paginationPanel.add(lastButton);

        mainContainer.add(scrollPane, java.awt.BorderLayout.CENTER);
        mainContainer.add(paginationPanel, java.awt.BorderLayout.SOUTH);

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
        pageButton.setOpaque(true);
        pageButton.setFocusPainted(false);
        pageButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
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
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (!(window instanceof JFrame)) {
            // If not in a JFrame, show simple message instead
            System.out.println("Cannot show toast - parent window is not a JFrame");
            return;
        }
        JFrame parentFrame = (JFrame) window;

        // Reset filters
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        priceFilter.setSelectedIndex(0);
        quantityFilter.setSelectedIndex(0);
        currentPage = 1;

        // Load fresh data from database instead of showing the old list
        loadProductData();
    }

    /**
     * Updates product quantities after a sale and refreshes the UI
     * @param soldQuantities Map of product IDs to quantities sold
     */
    public void updateProductQuantities(Map<Integer, Integer> soldQuantities) {
        if (products == null) {
            System.out.println("ProductSelectionPanel: Cannot update quantities - products list is null");
            return;
        }

        System.out.println("ProductSelectionPanel: Updating quantities for " + soldQuantities.size() + " products");
        
        boolean anyUpdated = false;
        
        // Update quantities in the local product list
        for (Product product : products) {
            if (soldQuantities.containsKey(product.getProductId())) {
                int soldQty = soldQuantities.get(product.getProductId());
                int newQuantity = product.getQuantity() - soldQty;
                System.out.println("Updating product ID: " + product.getProductId() + 
                                   " from quantity " + product.getQuantity() + 
                                   " to " + newQuantity);
                product.setQuantity(newQuantity);
                anyUpdated = true;
            }
        }
        
        // Also update filtered products if they exist
        if (filteredProducts != null) {
            for (Product product : filteredProducts) {
                if (soldQuantities.containsKey(product.getProductId())) {
                    int soldQty = soldQuantities.get(product.getProductId());
                    int newQuantity = product.getQuantity() - soldQty;
                    product.setQuantity(newQuantity);
                }
            }
        }
        
        // If any products were updated, refresh the display
        if (anyUpdated) {
            System.out.println("ProductSelectionPanel: Products updated, refreshing display");
            refreshProducts();
        }
    }

    /**
     * Updates a specific product card with the new quantity
     * @param productId ID of the product to update
     * @param newQuantity New quantity to display
     * @return true if the card was found and updated, false otherwise
     */
    public boolean updateSpecificProductCard(int productId, int newQuantity) {
        System.out.println("ProductSelectionPanel: Updating card for product ID " + productId + 
                         " with new quantity " + newQuantity);
        
        // First update the product in our data models
        boolean productUpdated = false;
        
        // Update in main products list
        if (products != null) {
            for (Product p : products) {
                if (p.getProductId() == productId) {
                    System.out.println("Found product in main list, updating quantity from " + 
                                     p.getQuantity() + " to " + newQuantity);
                    p.setQuantity(newQuantity);
                    productUpdated = true;
                    break;
                }
            }
        }
        
        // Update in filtered products list if exists
        if (filteredProducts != null) {
            for (Product p : filteredProducts) {
                if (p.getProductId() == productId) {
                    System.out.println("Found product in filtered list, updating quantity from " + 
                                     p.getQuantity() + " to " + newQuantity);
                    p.setQuantity(newQuantity);
                    break;
                }
            }
        }
        
        // Now find and update the visual card component
        try {
            // Find the container that holds all product cards
            Component mainContainer = null;
            for (int i = 0; i < getComponentCount(); i++) {
                Component comp = getComponent(i);
                if (comp instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) comp;
                    if (scrollPane.getViewport().getView() instanceof JPanel) {
                        mainContainer = scrollPane.getViewport().getView();
                        break;
                    }
                } else if (comp instanceof JPanel && 
                          (comp.getName() != null && comp.getName().contains("main"))) {
                    mainContainer = comp;
                    break;
                }
            }
            
            if (mainContainer == null) {
                System.out.println("Could not find main container for product cards");
                return refreshProducts(); // Fall back to full refresh
            }
            
            // Search for the ProductCard with matching ID
            boolean cardFound = false;
            if (mainContainer instanceof JPanel) {
                JPanel container = (JPanel) mainContainer;
                for (int i = 0; i < container.getComponentCount(); i++) {
                    Component comp = container.getComponent(i);
                    if (comp instanceof ProductCard) {
                        ProductCard card = (ProductCard) comp;
                        Product cardProduct = card.getProduct();
                        
                        if (cardProduct != null && cardProduct.getProductId() == productId) {
                            // Update the card UI
                            System.out.println("Found card for product ID " + productId + 
                                             ", updating quantity to " + newQuantity);
                            card.updateQuantity(newQuantity);
                            cardFound = true;
                            break;
                        }
                    }
                }
            }
            
            if (!cardFound) {
                System.out.println("Product card for ID " + productId + " not found in view");
            }
            
            return productUpdated || cardFound;
            
        } catch (Exception e) {
            System.err.println("Error updating product card: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Refreshes the product display with the latest data
     * This method can be called after updateProductQuantities to show quantity changes
     * @return true if refresh succeeded, false otherwise
     */
    public boolean refreshProducts() {
        System.out.println("ProductSelectionPanel: Performing full refresh of product display");
        
        try {
            if (filteredProducts != null) {
                System.out.println("ProductSelectionPanel: Refreshing filtered products view with " + 
                                  filteredProducts.size() + " products");
                // Refresh the current filtered view
                displayFilteredProducts(filteredProducts);
                return true;
            } else if (products != null) {
                System.out.println("ProductSelectionPanel: Refreshing with all products view with " + 
                                 products.size() + " products");
                // If no filtered products, refresh with all products
                displayFilteredProducts(products);
                return true;
            } else {
                System.out.println("ProductSelectionPanel: Cannot refresh - no product data available");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error during refresh: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads product data from the database and refreshes the UI
     * This method can be called after a successful checkout to reload products
     */
    public void loadProductData() {
        try {
            System.out.println("ProductSelectionPanel: Loading fresh product data from database");
            // Show loading animation
            isLoading = true;
            showLoadingAnimation();
            
            // Load products in a background thread to avoid UI freezing
            SwingUtilities.invokeLater(() -> {
                try {
                    if (productController == null) {
                        productController = new ProductController();
                    }
                    
                    List<Product> freshProducts = productController.getAllProducts();
                    System.out.println("ProductSelectionPanel: Successfully loaded " + 
                                     (freshProducts != null ? freshProducts.size() : 0) + " products");
                    
                    // Update the product lists
                    products = freshProducts;
                    
                    // Reset filtered products to show all products
                    filteredProducts = new ArrayList<>(products);
                    
                    // If filters were applied, reapply them to the new data
                    if (searchField != null && !searchField.getText().isEmpty()) {
                        applyFilters(); // Apply existing filters to new data
                    } else {
                        // The loading animation callback will display products when ready
                        currentPage = 1; // Reset to first page
                    }
                    
                    System.out.println("ProductSelectionPanel: UI will be refreshed with new data");
                } catch (Exception e) {
                    System.err.println("Error reloading products: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error in loadProductData: " + e.getMessage());
            e.printStackTrace();
            isLoading = false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createLineBorder(null));
        setForeground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(600, 500));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void drawSpinner(Graphics2D g2d, int x, int y) {
        int size = 20;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));

        for (int i = 0; i < 12; i++) {
            float scale = (float) ((12 - i) % 12) / 12.0f;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scale));

            double angle = Math.toRadians(spinnerAngle + i * 30);
            int x1 = x + size / 2 + (int) (size / 3 * Math.cos(angle));
            int y1 = y + (int) (size / 3 * Math.sin(angle));
            int x2 = x + size / 2 + (int) (size / 2 * Math.cos(angle));
            int y2 = y + (int) (size / 2 * Math.sin(angle));

            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
