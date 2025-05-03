package com.salesmate.component;

import java.awt.Image;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import com.salesmate.model.Product;
import com.salesmate.model.Promotion;
import com.salesmate.model.PromotionDetail;
import com.salesmate.controller.PromotionController;

public class ProductCard extends javax.swing.JPanel {

    private Product product;
    private BigDecimal originalPrice; // To track original price when there's a discount
    private double discountPercent = 0; // Store discount percentage
    private BigDecimal discountAmount = BigDecimal.ZERO; // Store discount amount
    private String discountType = ""; // "PERCENT" or "AMOUNT"
    private PromotionDetail promotionDetail; // Store associated promotion detail

    public interface ProductCardListener {
        void onProductSelected(Product product);
    }

    private ProductCardListener listener;
    private boolean isMouseListenerAdded = false; // Track if the listener is already added

    public ProductCard() {
        initComponents();
        setPreferredSize(new java.awt.Dimension(160, 220)); // Default size
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
    }

    public void setProductCardListener(ProductCardListener listener) {
        if (this.listener == null) { // Prevent setting the listener multiple times
            this.listener = listener;
            if (!isMouseListenerAdded) { // Add the mouse listener only once
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        if (ProductCard.this.listener != null && product != null) {
                            ProductCard.this.listener.onProductSelected(product);
                        }
                    }
                });
                isMouseListenerAdded = true;
            }
        }
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ProductCardContainer = new javax.swing.JPanel();
        panelImageContainer = new javax.swing.JPanel();
        lblProductImage = new javax.swing.JLabel();
        panelProductDetail = new javax.swing.JPanel();
        lblProductNameValue = new javax.swing.JLabel();
        lblProductQuantityKey = new javax.swing.JLabel();
        lblProductQuantityValue = new javax.swing.JLabel();
        lblProductPriceKey = new javax.swing.JLabel();
        lblProductPriceValue = new javax.swing.JLabel();
        lblProductCategory = new javax.swing.JLabel(); // New label for category
        lblDiscountBadge = new javax.swing.JLabel(); // Discount badge for left corner
        lblDiscountedPrice = new javax.swing.JLabel(); // Discounted price for right corner

        setPreferredSize(new java.awt.Dimension(160, 220));
        setLayout(new java.awt.BorderLayout());

        ProductCardContainer.setPreferredSize(new java.awt.Dimension(160, 220));

        lblDiscountBadge.setFont(new java.awt.Font("Segoe UI", 1, 10));
        lblDiscountBadge.setForeground(new Color(255, 255, 255));
        lblDiscountBadge.setBackground(new Color(220, 53, 69));
        lblDiscountBadge.setOpaque(true);
        lblDiscountBadge.setHorizontalAlignment(SwingConstants.CENTER);
        lblDiscountBadge.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        lblDiscountBadge.setText("-10%");
        lblDiscountBadge.setVisible(false); // Hide by default

        lblDiscountedPrice.setFont(new java.awt.Font("Segoe UI", 1, 10));
        lblDiscountedPrice.setForeground(new Color(255, 255, 255));
        lblDiscountedPrice.setBackground(new Color(46, 125, 50)); // Green background
        lblDiscountedPrice.setOpaque(true);
        lblDiscountedPrice.setHorizontalAlignment(SwingConstants.CENTER);
        lblDiscountedPrice.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        lblDiscountedPrice.setText("-10,000đ");
        lblDiscountedPrice.setVisible(false); // Hide by default

        javax.swing.GroupLayout panelImageContainerLayout = new javax.swing.GroupLayout(panelImageContainer);
        panelImageContainer.setLayout(panelImageContainerLayout);
        panelImageContainerLayout.setHorizontalGroup(
            panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(0, 0, 0))
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(lblDiscountBadge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDiscountedPrice)
                .addGap(5, 5, 5))
        );
        panelImageContainerLayout.setVerticalGroup(
            panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDiscountBadge)
                    .addComponent(lblDiscountedPrice))
                .addGap(5, 5, 5)
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelProductDetail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductNameValue.setFont(new java.awt.Font("Times New Roman", 1, 10)); // NOI18N
        lblProductNameValue.setText("Chưa rõ");

        lblProductQuantityKey.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblProductQuantityKey.setText("Còn lại");

        lblProductQuantityValue.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblProductQuantityValue.setText("0");

        lblProductPriceKey.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblProductPriceKey.setText("Giá tiền");

        lblProductPriceValue.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblProductPriceValue.setText("0");

        lblProductCategory.setFont(new java.awt.Font("Segoe UI", 0, 10));
        lblProductCategory.setText("Danh mục");

        javax.swing.GroupLayout panelProductDetailLayout = new javax.swing.GroupLayout(panelProductDetail);
        panelProductDetail.setLayout(panelProductDetailLayout);
        panelProductDetailLayout.setHorizontalGroup(
            panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProductDetailLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProductDetailLayout.createSequentialGroup()
                        .addComponent(lblProductNameValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelProductDetailLayout.createSequentialGroup()
                        .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProductPriceKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblProductQuantityKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblProductCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblProductQuantityValue, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                            .addComponent(lblProductPriceValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4))))
        );
        panelProductDetailLayout.setVerticalGroup(
            panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProductDetailLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(lblProductNameValue, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductQuantityKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProductQuantityValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductPriceKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProductPriceValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout ProductCardContainerLayout = new javax.swing.GroupLayout(ProductCardContainer);
        ProductCardContainer.setLayout(ProductCardContainerLayout);
        ProductCardContainerLayout.setHorizontalGroup(
            ProductCardContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductCardContainerLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(ProductCardContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelImageContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelProductDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        ProductCardContainerLayout.setVerticalGroup(
            ProductCardContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductCardContainerLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(panelImageContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelProductDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        add(ProductCardContainer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void setProductDetails(Product product) {
        this.product = product;
        this.product.setMaxQuantity(product.getQuantity()); // Save max quantity when setting product
        this.originalPrice = product.getPrice(); // Store original price
        
        // Check for discount - this would check the promotion database in real code
        applyDiscountIfAvailable(product);
        
        // Check product quantity
        if (product.getQuantity() <= 0) {
            this.setEnabled(false);
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            // Style to show out of stock
            lblProductQuantityValue.setForeground(new java.awt.Color(255, 0, 0));
            lblProductQuantityValue.setText("Hết hàng");
        }

        // Display product name
        lblProductNameValue.setText(product.getProductName());

        // Display category
        if (product.getCategory() != null && !product.getCategory().isEmpty()) {
            lblProductCategory.setText(product.getCategory());
            lblProductCategory.setVisible(true);
        } else {
            lblProductCategory.setVisible(false);
        }

        // Display quantity and price
        lblProductQuantityValue.setText(String.valueOf(product.getQuantity()));
        
        // Display price - always show original price (not the discounted one)
        lblProductPriceValue.setText(formatPrice(originalPrice));
        
        // Load product image if available
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                URL imageUrl = getClass().getResource("/img/product/" + product.getImage());
                if (imageUrl != null) {
                    ImageIcon icon = new ImageIcon(imageUrl);
                    Image img = icon.getImage();
                    Image newImg = img.getScaledInstance(
                            lblProductImage.getWidth(), 
                            lblProductImage.getHeight(), 
                            Image.SCALE_SMOOTH);
                    lblProductImage.setIcon(new ImageIcon(newImg));
                } else {
                    lblProductImage.setIcon(null);
                    lblProductImage.setText("No Image");
                }
            } catch (Exception e) {
                System.err.println("Error loading product image: " + e.getMessage());
                lblProductImage.setIcon(null);
                lblProductImage.setText("Error");
            }
        } else {
            lblProductImage.setIcon(null);
            lblProductImage.setText("No Image");
        }

        panelImageContainer.revalidate();  // Update UI of the panel
        panelImageContainer.repaint();     // Redraw the interface changes

        applyModernStyle(); // Apply style after updating product info
    }

    private void applyDiscountIfAvailable(Product product) {
        // First check if we have a real promotion in the database
        PromotionController promotionController = new PromotionController();
        promotionDetail = promotionController.getActivePromotionForProduct(product.getProductId());
        
        if (promotionDetail != null) {
            // We have a real promotion from database
            originalPrice = product.getPrice(); // Store original price before discount
            discountType = promotionDetail.getDiscountType();
            
            if ("PERCENT".equals(discountType)) {
                // Percentage discount
                discountPercent = promotionDetail.getDiscountValue().doubleValue();
                
                // Calculate discount amount based on percentage
                discountAmount = originalPrice.multiply(
                    BigDecimal.valueOf(discountPercent / 100.0)
                ).setScale(2, RoundingMode.HALF_UP);
                
                // Check if there's a maximum discount amount
                if (promotionDetail.getMaxDiscountAmount() != null && 
                    discountAmount.compareTo(promotionDetail.getMaxDiscountAmount()) > 0) {
                    discountAmount = promotionDetail.getMaxDiscountAmount();
                    // Recalculate effective percentage for display
                    discountPercent = discountAmount.multiply(new BigDecimal("100"))
                        .divide(originalPrice, 2, RoundingMode.HALF_UP)
                        .doubleValue();
                }
                
                // Update product price with the discounted price
                BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
                product.setPrice(discountedPrice);
                product.setOriginalPrice(originalPrice);
                product.setDiscountPercent(discountPercent);
                
                // Show discount badge with percentage
                lblDiscountBadge.setText("-" + (int)discountPercent + "%");
                lblDiscountBadge.setVisible(true);
                
            } else if ("AMOUNT".equals(discountType)) {
                // Fixed amount discount
                discountAmount = promotionDetail.getDiscountValue();
                
                // Make sure discount doesn't exceed the product's price
                if (discountAmount.compareTo(originalPrice) >= 0) {
                    discountAmount = originalPrice.multiply(new BigDecimal("0.9")); // Maximum 90% discount
                }
                
                // Calculate effective discount percentage for internal use
                discountPercent = discountAmount.multiply(new BigDecimal("100"))
                    .divide(originalPrice, 2, RoundingMode.HALF_UP)
                    .doubleValue();
                
                // Update product price with the discounted price
                BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
                product.setPrice(discountedPrice);
                product.setOriginalPrice(originalPrice);
                product.setDiscountPercent(discountPercent);
                
                // Format the discount amount as Vietnamese currency
                java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                String formattedDiscount = "-" + formatter.format(discountAmount) + "đ";
                
                // Show discount badge with amount
                lblDiscountBadge.setText(formattedDiscount);
                lblDiscountBadge.setVisible(true);
            }
        } else {
            // For demo purposes only - when no database promotions found
            // In production, you might want to remove this to only show real promotions
            if (Math.random() < 0.3) {
                // Randomly choose between percentage discount and fixed amount discount for demo
                boolean isPercentageDiscount = Math.random() < 0.7; // 70% chance of percentage discount
                
                if (isPercentageDiscount) {
                    discountType = "PERCENT";
                    // Generate a random discount between 5% and 30%
                    discountPercent = 5 + Math.random() * 25;
                    discountPercent = Math.round(discountPercent); // Round to whole number
                    
                    // Store original price in product before applying discount
                    originalPrice = product.getPrice();
                    product.setOriginalPrice(originalPrice);
                    
                    // Calculate discount amount
                    discountAmount = originalPrice.multiply(
                        BigDecimal.valueOf(discountPercent / 100.0)
                    ).setScale(2, RoundingMode.HALF_UP);
                    
                    // Apply discount to product price
                    BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
                    product.setPrice(discountedPrice);
                    
                    // Save discount percent to product
                    product.setDiscountPercent(discountPercent);
                    
                    // Show discount badge on left with percentage
                    lblDiscountBadge.setText("-" + (int)discountPercent + "%");
                    lblDiscountBadge.setVisible(true);
                } else {
                    discountType = "AMOUNT";
                    // Fixed amount discount - between 5,000đ and 50,000đ (in steps of 1,000đ)
                    int discountValue = (int)(5000 + Math.random() * 45000);
                    discountValue = Math.round(discountValue / 1000) * 1000; // Round to nearest 1000đ
                    
                    discountAmount = new BigDecimal(discountValue);
                    originalPrice = product.getPrice();
                    product.setOriginalPrice(originalPrice);
                    
                    // Make sure discount doesn't exceed 50% of the price
                    if (discountAmount.compareTo(originalPrice.multiply(new BigDecimal("0.5"))) > 0) {
                        discountAmount = originalPrice.multiply(new BigDecimal("0.5"));
                    }
                    
                    // Apply discount to product price
                    BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
                    product.setPrice(discountedPrice);
                    
                    // Calculate percent for internal use
                    discountPercent = discountAmount.multiply(new BigDecimal("100"))
                        .divide(originalPrice, 2, RoundingMode.HALF_UP)
                        .doubleValue();
                    product.setDiscountPercent(discountPercent);
                    
                    // Format the discount amount as Vietnamese currency (simplified)
                    java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                    String formattedDiscount = "-" + formatter.format(discountAmount) + "đ";
                    
                    // Show fixed discount badge
                    lblDiscountBadge.setText(formattedDiscount);
                    lblDiscountBadge.setVisible(true);
                }
            } else {
                // No discount
                discountPercent = 0;
                discountAmount = BigDecimal.ZERO;
                discountType = "";
                product.setDiscountPercent(0);
                product.setOriginalPrice(null);
                lblDiscountBadge.setVisible(false);
            }
        }
    }

    /**
     * Format price with Vietnamese currency
     */
    private String formatPrice(BigDecimal price) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(price) + "đ";
    }

    /**
     * Format price with Vietnamese currency in a shorter format
     */
    private String formatShortPrice(BigDecimal price) {
        if (price.compareTo(new BigDecimal(1000000)) >= 0) {
            // Convert to millions
            BigDecimal millions = price.divide(new BigDecimal(1000000), 1, RoundingMode.HALF_UP);
            return millions + "tr";
        } else if (price.compareTo(new BigDecimal(1000)) >= 0) {
            // Convert to thousands
            BigDecimal thousands = price.divide(new BigDecimal(1000), 0, RoundingMode.HALF_UP);
            return thousands + "k";
        } else {
            return price.intValue() + "đ";
        }
    }

    public void enhanceUI() {
        // Thay đổi màu nền của ProductCardContainer và các thành phần khác
        setBackground(new java.awt.Color(245, 245, 245));  // Màu nền xám nhạt cho card

        // Thay đổi màu viền của card
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 2));  // Viền mỏng và nhẹ nhàng

        // Thay đổi màu nền cho panel chứa hình ảnh
        panelImageContainer.setBackground(new java.awt.Color(240, 240, 240));  // Màu nền nhẹ cho panel hình ảnh

        // Thay đổi màu chữ cho các thông tin sản phẩm
        lblProductNameValue.setForeground(new java.awt.Color(40, 40, 40));  // Màu chữ đậm, dễ đọc
        lblProductQuantityKey.setForeground(new java.awt.Color(100, 100, 100));  // Màu chữ xám cho các label
        lblProductPriceKey.setForeground(new java.awt.Color(100, 100, 100));  // Màu chữ xám cho các label
        lblProductQuantityValue.setForeground(new java.awt.Color(80, 80, 80));  // Màu chữ cho giá trị
        lblProductPriceValue.setForeground(new java.awt.Color(80, 80, 80));  // Màu chữ cho giá trị

        // Thêm hiệu ứng hover cho card (di chuột qua card sẽ thay đổi màu nền)
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new java.awt.Color(230, 230, 230));  // Màu nền khi hover
                setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(150, 150, 150), 2));  // Đổi màu viền khi hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new java.awt.Color(245, 245, 245));  // Màu nền trở lại khi không hover
                setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 2));  // Viền trở lại màu cũ
            }
        });
    }

    // Thêm các thuộc tính màu sắc
    private static final java.awt.Color CARD_BACKGROUND = new java.awt.Color(255, 255, 255);
    private static final java.awt.Color CARD_BORDER = new java.awt.Color(230, 230, 230);
    private static final java.awt.Color HOVER_BACKGROUND = new java.awt.Color(249, 249, 249);
    private static final java.awt.Color HOVER_BORDER = new java.awt.Color(200, 200, 200);
    private static final java.awt.Color PRICE_COLOR = new java.awt.Color(46, 125, 50);
    private static final java.awt.Color NAME_COLOR = new java.awt.Color(33, 33, 33);
    private static final int BORDER_RADIUS = 10;

    public void applyModernStyle() {
        setBackground(CARD_BACKGROUND);
        setBorder(new javax.swing.border.LineBorder(CARD_BORDER, 1, true)); // Chỉ giữ lại border, bỏ padding

        // Style cho container
        ProductCardContainer.setBackground(CARD_BACKGROUND);
        ProductCardContainer.setBorder(null);

        // Style cho panel hình ảnh
        panelImageContainer.setBackground(CARD_BACKGROUND);
        panelImageContainer.setBorder(new javax.swing.border.LineBorder(CARD_BORDER, 1, true));

        // Style cho panel chi tiết
        panelProductDetail.setBackground(CARD_BACKGROUND);
        panelProductDetail.setBorder(null);

        // Đặt tỷ lệ cho panel ảnh và chi tiết
        java.awt.Dimension parentSize = getPreferredSize();
        int imageHeight = (int)(parentSize.height * 0.6);
        panelImageContainer.setPreferredSize(new java.awt.Dimension(
            parentSize.width,  // Bỏ trừ padding
            imageHeight
        ));
        
        // Panel chi tiết
        panelProductDetail.setPreferredSize(new java.awt.Dimension(
            parentSize.width, // Bỏ trừ padding
            parentSize.height - imageHeight
        ));

        // Style cho tên sản phẩm
        lblProductNameValue.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        lblProductNameValue.setForeground(NAME_COLOR);
        lblProductNameValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        
        // Giới hạn và xử lý tên sản phẩm
        String name = lblProductNameValue.getText();
        if (name.length() > 25) {
            // Truncate and add ellipsis
            lblProductNameValue.setText(name.substring(0, 22) + "...");
        }

        // Style cho các label khác
        styleLabel(lblProductQuantityKey, "Segoe UI", java.awt.Font.PLAIN, 11);
        styleLabel(lblProductQuantityValue, "Segoe UI", java.awt.Font.BOLD, 11);
        styleLabel(lblProductPriceKey, "Segoe UI", java.awt.Font.PLAIN, 11);
        lblProductPriceValue.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblProductPriceValue.setForeground(PRICE_COLOR);

        // Style for discount badges
        // Left side percentage badge
        lblDiscountBadge.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        lblDiscountBadge.setBackground(new java.awt.Color(220, 53, 69)); // Red background
        lblDiscountBadge.setForeground(java.awt.Color.WHITE);
        lblDiscountBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 5, 2, 5),
            BorderFactory.createLineBorder(new Color(180, 30, 45), 1)
        ));
        
        // Right side fixed discount badge
        lblDiscountedPrice.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        lblDiscountedPrice.setBackground(new java.awt.Color(46, 125, 50)); // Green background
        lblDiscountedPrice.setForeground(java.awt.Color.WHITE);
        lblDiscountedPrice.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 5, 2, 5),
            BorderFactory.createLineBorder(new Color(25, 100, 30), 1)
        ));

        // Cập nhật các giá trị maximum size
        setMaximumSize(getPreferredSize());
        ProductCardContainer.setMaximumSize(getPreferredSize());

        addHoverEffect();
    }

    private void styleLabel(JLabel label, String fontName, int fontStyle, int fontSize) {
        label.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        label.setForeground(new java.awt.Color(102, 102, 102));
    }

    private void addHoverEffect() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isEnabled()) { // Chỉ thay đổi style khi sản phẩm còn hàng
                    setBackground(HOVER_BACKGROUND);
                    setBorder(new javax.swing.border.LineBorder(HOVER_BORDER, 1, true));
                    setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isEnabled()) {
                    setBackground(CARD_BACKGROUND);
                    setBorder(new javax.swing.border.LineBorder(CARD_BORDER, 1, true));
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ProductCardContainer;
    private javax.swing.JLabel lblProductImage;
    private javax.swing.JLabel lblProductNameValue;
    private javax.swing.JLabel lblProductPriceKey;
    private javax.swing.JLabel lblProductPriceValue;
    private javax.swing.JLabel lblProductQuantityKey;
    private javax.swing.JLabel lblProductQuantityValue;
    private javax.swing.JLabel lblProductCategory; 
    private javax.swing.JLabel lblDiscountBadge;
    private javax.swing.JLabel lblDiscountedPrice; // Label for discounted price in right corner
    private javax.swing.JPanel panelImageContainer;
    private javax.swing.JPanel panelProductDetail;
    // End of variables declaration//GEN-END:variables
}
