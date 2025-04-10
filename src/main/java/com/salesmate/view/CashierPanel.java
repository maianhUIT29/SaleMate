package com.salesmate.view;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import java.util.List;

public class CashierPanel extends javax.swing.JFrame {

    private ProductController productController;

    public CashierPanel() {
        initComponents();  // Đừng thay đổi mã này, vì IDE đã tự động khởi tạo các thành phần UI
        loadProductList();  // Gọi phương thức tải sản phẩm
        // Đảm bảo rằng ProductSelectionPanel tự động kích thước phù hợp.
        productSelectionPanel.setPreferredSize(new java.awt.Dimension(750, 500));  // Bạn có thể điều chỉnh kích thước nếu cần

    }

    private void loadProductList() {
        productController = new ProductController();
        List<Product> products = productController.getAllProducts();  // Lấy sản phẩm từ controller
        productSelectionPanel.setProducts(products);  // Truyền danh sách sản phẩm vào ProductSelectionPanel
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        cashierHeader = new com.salesmate.component.CashierHeader();
        tpCashier = new javax.swing.JTabbedPane();
        panelSaleContainer = new javax.swing.JPanel();
        PanelSale = new javax.swing.JPanel();
        checkoutCashier = new com.salesmate.component.CheckoutPanel();
        productSelectionPanel = new com.salesmate.component.ProductSelectionPanel();
        panelAccountContainer = new javax.swing.JPanel();
        panelSalaryContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tpCashier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tpCashier.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        panelSaleContainer.setLayout(new java.awt.BorderLayout());

        checkoutCashier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout checkoutCashierLayout = new javax.swing.GroupLayout(checkoutCashier);
        checkoutCashier.setLayout(checkoutCashierLayout);
        checkoutCashierLayout.setHorizontalGroup(
            checkoutCashierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 304, Short.MAX_VALUE)
        );
        checkoutCashierLayout.setVerticalGroup(
            checkoutCashierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelSaleLayout = new javax.swing.GroupLayout(PanelSale);
        PanelSale.setLayout(PanelSaleLayout);
        PanelSaleLayout.setHorizontalGroup(
            PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSaleLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(productSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(checkoutCashier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        PanelSaleLayout.setVerticalGroup(
            PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSaleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .addComponent(checkoutCashier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        panelSaleContainer.add(PanelSale, java.awt.BorderLayout.CENTER);

        tpCashier.addTab("Bán hàng", panelSaleContainer);

        javax.swing.GroupLayout panelAccountContainerLayout = new javax.swing.GroupLayout(panelAccountContainer);
        panelAccountContainer.setLayout(panelAccountContainerLayout);
        panelAccountContainerLayout.setHorizontalGroup(
            panelAccountContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 957, Short.MAX_VALUE)
        );
        panelAccountContainerLayout.setVerticalGroup(
            panelAccountContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 485, Short.MAX_VALUE)
        );

        tpCashier.addTab("Tài khoản", panelAccountContainer);

        panelSalaryContainer.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout panelSalaryContainerLayout = new javax.swing.GroupLayout(panelSalaryContainer);
        panelSalaryContainer.setLayout(panelSalaryContainerLayout);
        panelSalaryContainerLayout.setHorizontalGroup(
            panelSalaryContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 955, Short.MAX_VALUE)
        );
        panelSalaryContainerLayout.setVerticalGroup(
            panelSalaryContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 483, Short.MAX_VALUE)
        );

        tpCashier.addTab("Lương", panelSalaryContainer);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cashierHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tpCashier))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cashierHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpCashier))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 985, 598);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new RunnableImpl());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelSale;
    private com.salesmate.component.CashierHeader cashierHeader;
    private com.salesmate.component.CheckoutPanel checkoutCashier;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panelAccountContainer;
    private javax.swing.JPanel panelSalaryContainer;
    private javax.swing.JPanel panelSaleContainer;
    private com.salesmate.component.ProductSelectionPanel productSelectionPanel;
    private javax.swing.JTabbedPane tpCashier;
    // End of variables declaration//GEN-END:variables

    private static class RunnableImpl implements Runnable {

        public RunnableImpl() {
        }

        @Override
        public void run() {
            new CashierPanel().setVisible(true);
        }
    }
}
