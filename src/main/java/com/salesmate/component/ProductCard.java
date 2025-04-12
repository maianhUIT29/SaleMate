package com.salesmate.component;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.salesmate.model.Product;
import java.awt.Image;

public class ProductCard extends javax.swing.JPanel {

    private Product product;

    public ProductCard() {
        initComponents();
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

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panelImageContainerLayout = new javax.swing.GroupLayout(panelImageContainer);
        panelImageContainer.setLayout(panelImageContainerLayout);
        panelImageContainerLayout.setHorizontalGroup(
            panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelImageContainerLayout.setVerticalGroup(
            panelImageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelProductDetail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductNameValue.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        lblProductNameValue.setText("Chưa rõ");

        lblProductQuantityKey.setText("Còn lại");

        lblProductQuantityValue.setText("0");

        lblProductPriceKey.setText("Giá tiền");

        lblProductPriceValue.setText("0");

        javax.swing.GroupLayout panelProductDetailLayout = new javax.swing.GroupLayout(panelProductDetail);
        panelProductDetail.setLayout(panelProductDetailLayout);
        panelProductDetailLayout.setHorizontalGroup(
            panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProductDetailLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductQuantityKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProductPriceKey)
                    .addComponent(lblProductNameValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblProductQuantityValue, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(lblProductPriceValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panelProductDetailLayout.setVerticalGroup(
            panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProductDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductNameValue, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductQuantityKey, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(lblProductQuantityValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProductDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductPriceKey)
                    .addComponent(lblProductPriceValue, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout ProductCardContainerLayout = new javax.swing.GroupLayout(ProductCardContainer);
        ProductCardContainer.setLayout(ProductCardContainerLayout);
        ProductCardContainerLayout.setHorizontalGroup(
            ProductCardContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductCardContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProductCardContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelImageContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelProductDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ProductCardContainerLayout.setVerticalGroup(
            ProductCardContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProductCardContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelImageContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelProductDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                System.out.println("Tải ảnh từ: /img/product/" + product.getImage());

                String imagePath = "/img/product/" + product.getImage(); // Đảm bảo đây là đường dẫn đúng tới ảnh
                URL imageUrl = getClass().getResource(imagePath); // Tải URL ảnh

                if (imageUrl != null) {
                    ImageIcon originalImage = new ImageIcon(imageUrl); // Chuyển đổi URL thành ImageIcon

                    // Lấy kích thước của panelImageContainer để resize ảnh phù hợp
                    int targetWidth = panelImageContainer.getWidth();  // Lấy chiều rộng của panel
                    int targetHeight = panelImageContainer.getHeight();  // Lấy chiều cao của panel

                    // Nếu chiều cao và chiều rộng của panel chưa được xác định, mặc định chiều cao là 200px
                    if (targetWidth == 0 || targetHeight == 0) {
                        targetWidth = 260;  // Mặc định chiều rộng
                        targetHeight = 150;  // Mặc định chiều cao
                    }

                    // Resize ảnh theo kích thước của panel
                    Image resizedImage = originalImage.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

                    // Tạo lại ImageIcon từ ảnh đã resize
                    ImageIcon resizedImageIcon = new ImageIcon(resizedImage);

                    // Đặt layout và kích thước cho panel chứa ảnh
                    panelImageContainer.setLayout(new java.awt.BorderLayout()); // Sử dụng BorderLayout

                    // Thêm JLabel chứa ảnh đã resize vào panel
                    JLabel imageLabel = new JLabel(resizedImageIcon); // Tạo JLabel cho ảnh đã resize
                    panelImageContainer.removeAll();  // Xóa các thành phần cũ trong panel
                    panelImageContainer.add(imageLabel, java.awt.BorderLayout.CENTER);  // Thêm ảnh vào panel

                    // Đảm bảo ảnh không bị tràn và các thành phần khác vẫn hiển thị đúng
                    imageLabel.setPreferredSize(new java.awt.Dimension(targetWidth, targetHeight));  // Đảm bảo ảnh có kích thước phù hợp

                    System.out.println("Ảnh tải thành công và đã resize.");
                } else {
                    // Nếu không tìm thấy ảnh, hiển thị thông báo mặc định
                    panelImageContainer.removeAll();
                    panelImageContainer.add(new JLabel("No Image Available"));
                    System.out.println("Ảnh không tìm thấy tại đường dẫn: " + imagePath);
                }
            } catch (Exception e) {
                System.out.println("Error loading image: " + e.getMessage());
                panelImageContainer.removeAll();
                panelImageContainer.add(new JLabel("No Image"));
            }
        } else {
            panelImageContainer.removeAll();
            panelImageContainer.add(new JLabel("No Image")); // Nếu không có ảnh, hiển thị thông báo
            System.out.println("Không có ảnh cho sản phẩm.");
        }

        panelImageContainer.revalidate();  // Cập nhật lại UI của panel
        panelImageContainer.repaint(); // Vẽ lại các thay đổi trên giao diện
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
