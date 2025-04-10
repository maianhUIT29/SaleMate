package com.salesmate.component;

import com.salesmate.model.Product;

import java.awt.GridLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ProductSelectionPanel extends javax.swing.JPanel {

    private List<Product> products;

    public ProductSelectionPanel() {
        initComponents();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        System.out.println("Danh sach san pham da duoc cap nhat, so luong: " + products.size());
        displayProducts(); // Gọi phương thức hiển thị sản phẩm mỗi khi danh sách sản phẩm thay đổi
    }

    private void displayProducts() {
        if (products == null || products.isEmpty()) {
            System.out.println("Danh sach san pham rong, khong co san pham de hien thi.");
            return;
        }

        System.out.println("Bat dau hien thi danh sach san pham...");

        // Tạo container chứa các ProductCard
        JPanel productContainer = new JPanel();
        int columns = 3;  // Số sản phẩm mỗi hàng
        int rows = (int) Math.ceil(products.size() / (double) columns); // Tính số dòng

        // Thiết lập Layout của productContainer để hiển thị các sản phẩm dưới dạng lưới
        productContainer.setLayout(new GridLayout(rows, columns, 10, 10)); // 10px khoảng cách giữa các sản phẩm
        System.out.println("GridLayout duoc thiet lap: " + rows + " dong, " + columns + " cot.");

        // Duyệt qua tất cả sản phẩm và thêm vào panel
        for (Product product : products) {
            System.out.println("Dang them san pham: " + product.getProductName());
            ProductCard productCard = new ProductCard();
            productCard.setProductDetails(product);  // Đặt thông tin sản phẩm cho card
            productCard.setPreferredSize(new java.awt.Dimension(220, 260));  // Giảm kích thước ProductCard
            productContainer.add(productCard);
        }

        // Thêm productContainer vào JScrollPane để đảm bảo có thể cuộn khi có nhiều sản phẩm
        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  // Không cuộn ngang
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);  // Cuộn dọc khi cần

        this.setLayout(new java.awt.BorderLayout());
        this.add(scrollPane, java.awt.BorderLayout.CENTER);  // Thêm JScrollPane vào panel chính

        // Cập nhật lại kích thước cho panel selection (chiếm 2/3 chiều rộng)
        this.setPreferredSize(new java.awt.Dimension(650, 500));  // Chiều rộng 2/3 và chiều cao thích hợp

        this.revalidate();
        this.repaint();
        System.out.println("Danh sach san pham da duoc hien thi.");
        enhanceUI();
    }

    public void enhanceUI() {
        setBackground(new java.awt.Color(245, 245, 245));  // Đặt màu nền dịu mắt cho panel chính
        setLayout(new java.awt.BorderLayout());  // Đảm bảo không gian hợp lý giữa các phần tử

        // Cải thiện các hiệu ứng cuộn và các thành phần khác
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cải thiện cuộn dọc

        // Tạo khoảng cách hợp lý giữa các sản phẩm và các thành phần trong container
        JPanel productContainer = new JPanel();
        int columns = 3;
        int rows = (int) Math.ceil(products.size() / (double) columns);
        productContainer.setLayout(new GridLayout(rows, columns, 20, 20));  // Tăng khoảng cách giữa các sản phẩm

        // Duyệt qua tất cả sản phẩm và thêm vào panel
        for (Product product : products) {
            ProductCard productCard = new ProductCard();
            productCard.setProductDetails(product);
            productCard.setPreferredSize(new java.awt.Dimension(220, 260));  // Giảm kích thước ProductCard
            productCard.enhanceUI();  // Thêm hiệu ứng và cải tiến UI cho ProductCard
            productContainer.add(productCard);
        }

        // Thêm container vào JScrollPane để đảm bảo cuộn dọc
        scrollPane.setViewportView(productContainer);

        this.add(scrollPane, java.awt.BorderLayout.CENTER);  // Thêm JScrollPane vào panel chính
        this.revalidate();
        this.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
