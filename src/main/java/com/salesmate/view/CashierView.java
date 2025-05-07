package com.salesmate.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import com.salesmate.utils.UIHelper;

public class CashierView extends javax.swing.JFrame {

    private ProductController productController;

    public CashierView() {
        try {
            System.out.println("Initializing CashierView...");
            
            // Use our custom look and feel helper
            UIHelper.setupLookAndFeel();
            
            initComponents();
            productSelectionPanel.setPreferredSize(new java.awt.Dimension(700, 500));
            
            // Link panels with proper connection in both directions
            System.out.println("CashierView: Setting up bidirectional connection between panels");
            productSelectionPanel.setCheckoutPanel(checkoutPanel2);
            checkoutPanel2.setProductSelectionPanel(productSelectionPanel);
            System.out.println("CashierView: Panel connection established");
            
            // Add window listener to maximize window after it becomes visible
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    System.out.println("CashierView window opened, maximizing...");
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    validate();
                    repaint();
                }
            });
            
            // Make sure window is visible before loading products
            setVisible(true);
            
            // Apply no-focus styling to all components
            UIHelper.removeFocusFromAll(this);
            
            // Load products after UI is set up
            SwingUtilities.invokeLater(this::loadProductList);
            
            System.out.println("CashierView initialization complete");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in CashierView constructor: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                    "Lỗi khởi tạo giao diện bán hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductList() {
        try {
            System.out.println("Loading product list...");
            productController = new ProductController();
            List<Product> products = productController.getAllProducts();
            if (products != null && !products.isEmpty()) {
                System.out.println("Found " + products.size() + " products");
                // Set products will trigger a single loading animation in ProductSelectionPanel
                productSelectionPanel.setProducts(products);
            } else {
                System.out.println("No products found");
                // Show an error message if no products are found
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy sản phẩm nào trong cơ sở dữ liệu.",
                    "Thông báo",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading products: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CashierViewContainer = new javax.swing.JPanel();
        cashierHeader = new com.salesmate.component.CashierHeader();
        tpCashier = new javax.swing.JTabbedPane();
        panelSaleContainer = new javax.swing.JPanel();
        PanelSale = new javax.swing.JPanel();
        productSelectionPanel = new com.salesmate.component.ProductSelectionPanel();
        checkoutPanel2 = new com.salesmate.component.CheckoutPanel();
        panelInvoicesContainer = new javax.swing.JPanel();
        cashierInvoicesPanel2 = new com.salesmate.component.CashierInvoicesPanel();
        panelAccountContainer = new javax.swing.JPanel();
        cashierAccount3 = new com.salesmate.component.CashierAccount();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));

        tpCashier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tpCashier.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        panelSaleContainer.setLayout(new java.awt.BorderLayout());

        productSelectionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        checkoutPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout PanelSaleLayout = new javax.swing.GroupLayout(PanelSale);
        PanelSale.setLayout(PanelSaleLayout);
        PanelSaleLayout.setHorizontalGroup(
            PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSaleLayout.createSequentialGroup()
                .addComponent(productSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkoutPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelSaleLayout.setVerticalGroup(
            PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelSaleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PanelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(productSelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(checkoutPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelSaleContainer.add(PanelSale, java.awt.BorderLayout.CENTER);

        tpCashier.addTab("Bán hàng", panelSaleContainer);

        panelInvoicesContainer.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        cashierInvoicesPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout panelInvoicesContainerLayout = new javax.swing.GroupLayout(panelInvoicesContainer);
        panelInvoicesContainer.setLayout(panelInvoicesContainerLayout);
        panelInvoicesContainerLayout.setHorizontalGroup(
            panelInvoicesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInvoicesContainerLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(cashierInvoicesPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
                .addGap(27, 27, 27))
        );
        panelInvoicesContainerLayout.setVerticalGroup(
            panelInvoicesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInvoicesContainerLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(cashierInvoicesPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );

        tpCashier.addTab("Lịch sử bán hàng", panelInvoicesContainer);

        javax.swing.GroupLayout panelAccountContainerLayout = new javax.swing.GroupLayout(panelAccountContainer);
        panelAccountContainer.setLayout(panelAccountContainerLayout);
        panelAccountContainerLayout.setHorizontalGroup(
            panelAccountContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAccountContainerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cashierAccount3, javax.swing.GroupLayout.PREFERRED_SIZE, 929, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        panelAccountContainerLayout.setVerticalGroup(
            panelAccountContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccountContainerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cashierAccount3, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpCashier.addTab("Tài Khoản", panelAccountContainer);

        javax.swing.GroupLayout CashierViewContainerLayout = new javax.swing.GroupLayout(CashierViewContainer);
        CashierViewContainer.setLayout(CashierViewContainerLayout);
        CashierViewContainerLayout.setHorizontalGroup(
            CashierViewContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashierViewContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cashierHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tpCashier)
        );
        CashierViewContainerLayout.setVerticalGroup(
            CashierViewContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashierViewContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cashierHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpCashier))
        );

        getContentPane().add(CashierViewContainer, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 985, 589);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            System.out.println("Starting CashierView application...");
            UIHelper.setupLookAndFeel();
            
            java.awt.EventQueue.invokeLater(() -> {
                try {
                    CashierView view = new CashierView();
                    System.out.println("CashierView created successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error creating CashierView: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, 
                            "Lỗi khởi tạo CashierView: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error in main: " + e.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CashierViewContainer;
    private javax.swing.JPanel PanelSale;
    private com.salesmate.component.CashierAccount cashierAccount3;
    private com.salesmate.component.CashierHeader cashierHeader;
    private com.salesmate.component.CashierInvoicesPanel cashierInvoicesPanel2;
    private com.salesmate.component.CheckoutPanel checkoutPanel2;
    private javax.swing.JPanel panelAccountContainer;
    private javax.swing.JPanel panelInvoicesContainer;
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
