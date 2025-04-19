package com.salesmate.view;

import java.util.List;

import javax.swing.JFrame;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;

public class CashierView extends javax.swing.JFrame {

    private ProductController productController;

    public CashierView() {
        initComponents();
        loadProductList();
        productSelectionPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        // Link panels
        productSelectionPanel.setCheckoutPanel(checkoutPanel2);
        checkoutPanel2.setProductSelectionPanel(productSelectionPanel);
    }

    private void loadProductList() {
        productController = new ProductController();
        List<Product> products = productController.getAllProducts();
        if (products != null && !products.isEmpty()) {
            productSelectionPanel.setProducts(products);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cashierAccount1 = new com.salesmate.component.CashierAccount();
        cashierAccount2 = new com.salesmate.component.CashierAccount();
        jPanel2 = new javax.swing.JPanel();
        cashierHeader = new com.salesmate.component.CashierHeader();
        tpCashier = new javax.swing.JTabbedPane();
        panelSaleContainer = new javax.swing.JPanel();
        PanelSale = new javax.swing.JPanel();
        productSelectionPanel = new com.salesmate.component.ProductSelectionPanel();
        checkoutPanel2 = new com.salesmate.component.CheckoutPanel();
        panelAccountContainer = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cashierRecentInvoice1 = new com.salesmate.component.CashierRecentInvoice();
        panelSalaryContainer = new javax.swing.JPanel();
        cashierInvoicesPanel2 = new com.salesmate.component.CashierInvoicesPanel();
        jPanel3 = new javax.swing.JPanel();
        cashierAccount3 = new com.salesmate.component.CashierAccount();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));

        tpCashier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tpCashier.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        panelSaleContainer.setLayout(new java.awt.BorderLayout());

        productSelectionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout PanelSaleLayout = new javax.swing.GroupLayout(PanelSale);
        PanelSale.setLayout(PanelSaleLayout);
        PanelSaleLayout.setHorizontalGroup(
            PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSaleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(productSelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 548, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(checkoutPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        PanelSaleLayout.setVerticalGroup(
            PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSaleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productSelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(checkoutPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        panelSaleContainer.add(PanelSale, java.awt.BorderLayout.CENTER);

        tpCashier.addTab("Bán hàng", panelSaleContainer);

        panelAccountContainer.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(cashierRecentInvoice1, javax.swing.GroupLayout.DEFAULT_SIZE, 918, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(cashierRecentInvoice1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addGap(17, 17, 17))
        );

        panelAccountContainer.add(jPanel1, java.awt.BorderLayout.CENTER);

        tpCashier.addTab("Hoá Đơn Gần Đây", panelAccountContainer);

        panelSalaryContainer.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        cashierInvoicesPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout panelSalaryContainerLayout = new javax.swing.GroupLayout(panelSalaryContainer);
        panelSalaryContainer.setLayout(panelSalaryContainerLayout);
        panelSalaryContainerLayout.setHorizontalGroup(
            panelSalaryContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSalaryContainerLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(cashierInvoicesPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
                .addGap(55, 55, 55))
        );
        panelSalaryContainerLayout.setVerticalGroup(
            panelSalaryContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalaryContainerLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(cashierInvoicesPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );

        tpCashier.addTab("Lịch sử bán hàng", panelSalaryContainer);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cashierAccount3, javax.swing.GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cashierAccount3, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpCashier.addTab("Tài Khoản", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 969, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 481, Short.MAX_VALUE)
        );

        tpCashier.addTab("Lương", jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cashierHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tpCashier)
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

        setBounds(0, 0, 985, 589);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new RunnableImpl());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelSale;
    private com.salesmate.component.CashierAccount cashierAccount1;
    private com.salesmate.component.CashierAccount cashierAccount2;
    private com.salesmate.component.CashierAccount cashierAccount3;
    private com.salesmate.component.CashierHeader cashierHeader;
    private com.salesmate.component.CashierInvoicesPanel cashierInvoicesPanel2;
    private com.salesmate.component.CashierRecentInvoice cashierRecentInvoice1;
    private com.salesmate.component.CheckoutPanel checkoutPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
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
            new CashierView().setVisible(true);
        }
    }
}
