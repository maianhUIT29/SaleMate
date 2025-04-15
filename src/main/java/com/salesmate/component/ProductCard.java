package com.salesmate.component;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.salesmate.model.Product;

public class ProductCard extends javax.swing.JPanel {

    private Product product;

    public interface ProductCardListener {
        void onProductSelected(Product product);
    }

    private ProductCardListener listener;

    public void setProductCardListener(ProductCardListener listener) {
        this.listener = listener;
    }

    public ProductCard() {
        initComponents();
        setPreferredSize(new java.awt.Dimension(160, 220)); // Kích thước mặc định
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (listener != null && product != null) {
                    System.out.println("Product clicked: " + product.getProductName()); // Debugging
                    listener.onProductSelected(product);
                }
            }
        });
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

        setPreferredSize(new java.awt.Dimension(160, 220));
        setLayout(new java.awt.BorderLayout());

        ProductCardContainer.setPreferredSize(new java.awt.Dimension(160, 220));

        javax.swing.GroupLayout panelImageContainerLayout = new javax.swing.GroupLayout(panelImageContainer);
        panelImageContainer.setLayout(panelImageContainerLayout);
        panelImageContainerLayout.setHorizontalGroup(
            panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        panelImageContainerLayout.setVerticalGroup(
            panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
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
                            .addComponent(lblProductQuantityKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        // Hiển thị tên sản phẩm
        lblProductNameValue.setText(product.getProductName());

        // Hiển thị số lượng và giá
        lblProductQuantityValue.setText(String.valueOf(product.getQuantity()));
        lblProductPriceValue.setText(product.getPrice().toString());

        // Tải hình ảnh sản phẩm nếu có
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                String imagePath = "/img/product/" + product.getImage();
                URL imageUrl = getClass().getResource(imagePath);

                if (imageUrl != null) {
                    ImageIcon originalIcon = new ImageIcon(imageUrl);
                    Image originalImage = originalIcon.getImage();
                    
                    // Tạo ImageIcon mới với kích thước tự động điều chỉnh
                    ImageIcon scaledIcon = new ImageIcon(originalImage) {
                        @Override
                        public int getIconWidth() {
                            return panelImageContainer.getWidth() - 10;
                        }
                        
                        @Override
                        public int getIconHeight() {
                            return panelImageContainer.getHeight() - 10;
                        }
                        
                        @Override
                        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
                            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                                               java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            
                            int width = getIconWidth();
                            int height = getIconHeight();
                            
                            // Tính toán tỷ lệ khung hình
                            double imageRatio = (double) originalImage.getWidth(c) / originalImage.getHeight(c);
                            double containerRatio = (double) width / height;
                            
                            int finalWidth, finalHeight;
                            if (imageRatio > containerRatio) {
                                finalWidth = width;
                                finalHeight = (int) (width / imageRatio);
                            } else {
                                finalHeight = height;
                                finalWidth = (int) (height * imageRatio);
                            }
                            
                            // Vẽ ảnh với kích thước đã tính toán và căn giữa
                            int dx = (width - finalWidth) / 2;
                            int dy = (height - finalHeight) / 2;
                            g2d.drawImage(originalImage, dx, dy, finalWidth, finalHeight, c);
                            g2d.dispose();
                        }
                    };
                    
                    lblProductImage.setIcon(scaledIcon);
                } else {
                    lblProductImage.setIcon(null);
                    lblProductImage.setText("No Image Available");
                }
            } catch (Exception e) {
                lblProductImage.setIcon(null);
                lblProductImage.setText("Error Loading Image");
            }
        } else {
            lblProductImage.setIcon(null);
            lblProductImage.setText("No Image");
        }

        panelImageContainer.revalidate();  // Cập nhật lại UI của panel
        panelImageContainer.repaint(); // Vẽ lại các thay đổi trên giao diện

        applyModernStyle(); // Áp dụng style mới sau khi cập nhật thông tin sản phẩm
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
            lblProductNameValue.setText("<html>" + name + "</html>"); // Cho phép wrap text
            lblProductNameValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        }

        // Style cho các label khác
        styleLabel(lblProductQuantityKey, "Segoe UI", java.awt.Font.PLAIN, 11);
        styleLabel(lblProductQuantityValue, "Segoe UI", java.awt.Font.BOLD, 11);
        styleLabel(lblProductPriceKey, "Segoe UI", java.awt.Font.PLAIN, 11);
        lblProductPriceValue.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblProductPriceValue.setForeground(PRICE_COLOR);
        
        // Format giá tiền với đơn vị VNĐ
        try {
            double price = Double.parseDouble(lblProductPriceValue.getText());
            lblProductPriceValue.setText(String.format("%,dđ", (int)price));
        } catch (NumberFormatException e) {
            // Giữ nguyên text nếu không parse được
        }

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
                setBackground(HOVER_BACKGROUND);
                setBorder(new javax.swing.border.LineBorder(HOVER_BORDER, 1, true));
                setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(CARD_BACKGROUND);
                setBorder(new javax.swing.border.LineBorder(CARD_BORDER, 1, true));
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
    private javax.swing.JPanel panelImageContainer;
    private javax.swing.JPanel panelProductDetail;
    // End of variables declaration//GEN-END:variables
}
