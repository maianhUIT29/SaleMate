package com.salesmate.component;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.salesmate.model.Product;

public class ProductSelectionPanel extends javax.swing.JPanel {

    private List<Product> products;
    private CheckoutPanel checkoutPanel;

    public ProductSelectionPanel() {
        initComponents();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        System.out.println("Danh sach san pham da duoc cap nhat, so luong: " + products.size());
        displayProducts(); // Gọi phương thức hiển thị sản phẩm mỗi khi danh sách sản phẩm thay đổi
    }

    public void setCheckoutPanel(CheckoutPanel checkoutPanel) {
        this.checkoutPanel = checkoutPanel;
    }

    public void displayProducts() {
        if (products == null || products.isEmpty()) {
            return;
        }

        JPanel productContainer = new JPanel();
        productContainer.setLayout(new java.awt.GridBagLayout());
        productContainer.setBackground(java.awt.Color.WHITE);

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;

        int row = 0;
        int col = 0;
        for (Product product : products) {
            ProductCard productCard = new ProductCard();
            productCard.setPreferredSize(new java.awt.Dimension(220, 300));
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
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        // Thêm panel trống để đẩy các card lên trên
        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.weighty = 1.0;
        gbc.gridwidth = 3;
        productContainer.add(new JPanel(), gbc);

        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        removeAll();
        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
        revalidate();
        repaint();
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
