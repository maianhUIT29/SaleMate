package com.salesmate.component;

import java.awt.GridLayout;
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
            System.out.println("Danh sach san pham rong, khong co san pham de hien thi.");
            return;
        }

        System.out.println("Bat dau hien thi danh sach san pham...");

        JPanel productContainer = new JPanel();
        productContainer.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns, 10px spacing

        for (Product product : products) {
            ProductCard productCard = new ProductCard();
            productCard.setProductDetails(product);
            productCard.setProductCardListener(selectedProduct -> {
                if (checkoutPanel != null) {
                    System.out.println("Adding product to checkout: " + selectedProduct.getProductName()); // Debugging
                    checkoutPanel.addProductToCheckout(selectedProduct);
                }
            });
            productContainer.add(productCard);
        }

        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.setLayout(new java.awt.BorderLayout());
        this.removeAll(); // Clear previous components
        this.add(scrollPane, java.awt.BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
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
