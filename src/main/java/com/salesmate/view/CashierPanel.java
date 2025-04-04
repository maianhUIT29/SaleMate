/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.salesmate.view;

import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import com.salesmate.component.ProductCard;
import java.awt.GridLayout;
import java.util.List;
import java.math.BigDecimal;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

/**
 *
 * @author Nhan
 */
public class CashierPanel extends javax.swing.JFrame {

    private ProductController productController;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private javax.swing.JPanel checkoutContainer;
    private javax.swing.JLabel totalPriceLabel;
    private List<Product> allProducts; // Store all products for search functionality
    private Map<Product, JPanel> checkoutItems; // Map to track products and their checkout panels
    private JTable checkoutTable;
    private DefaultTableModel tableModel;

    /**
     * Creates new form CashierPanel
     */
    public CashierPanel() {
        initComponents();
        
        // Initialize ProductController
        productController = new ProductController();
        
        // Load product list into the ProductSelectionContainer
        loadProductList();
        setupCheckoutSection();
        setupSearchFunctionality();
        setupPaymentConfirmationDialogs();
        checkoutItems = new HashMap<>();
    }

    private void loadProductList() {
        System.out.println("Loading product list...");

        allProducts = productController.getAllProducts();
        if (allProducts == null || allProducts.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.println("Number of products found: " + allProducts.size());
        displayProducts(allProducts);
    }

    private void displayProducts(List<Product> products) {
        javax.swing.JPanel productContainer = new javax.swing.JPanel();
        productContainer.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns, adjustable rows

        for (Product product : products) {
            ProductCard productCard = new ProductCard(this);
            productCard.setProductDetails(product);
            productContainer.add(productCard);
        }

        jScrollPane2.setViewportView(productContainer);
        jScrollPane2.revalidate();
        jScrollPane2.repaint();
    }

    private void setupCheckoutSection() {
        // Initialize table model with columns
        tableModel = new DefaultTableModel(new Object[]{"Product Name", "Price", "Quantity", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing only for the quantity column
                return column == 2;
            }
        };

        // Initialize table
        checkoutTable = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                // Set the "Actions" column to display buttons
                return column == 3 ? JButton.class : super.getColumnClass(column);
            }
        };
        checkoutTable.setRowHeight(30);
        checkoutTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Product Name
        checkoutTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Price
        checkoutTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Quantity
        checkoutTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Actions

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(checkoutTable);
        jScrollPane1.setViewportView(scrollPane);

        // Add total price label at the bottom
        totalPriceLabel = new JLabel("Total Price: 0");
        totalPriceLabel.setHorizontalAlignment(JLabel.RIGHT);
        jPanel1.add(totalPriceLabel);
    }

    private void setupSearchFunctionality() {
        SearchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = SearchInput.getText().toLowerCase();
                List<Product> filteredProducts = allProducts.stream()
                        .filter(product -> product.getProductName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
                displayProducts(filteredProducts);
            }
        });
    }

    public void addToCheckout(Product product) {
        // Check if the product already exists in the table
        boolean productExists = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(product.getProductName())) {
                // Increment quantity
                int currentQuantity = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                tableModel.setValueAt(currentQuantity + 1, i, 2);
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            // Add new product to the table
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Remove the row from the table
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 3) == deleteButton) {
                            tableModel.removeRow(i);
                            updateTotalPrice();
                            break;
                        }
                    }
                }
            });

            tableModel.addRow(new Object[]{product.getProductName(), product.getPrice(), 1, deleteButton});
        }

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPrice = BigDecimal.ZERO;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            BigDecimal price = (BigDecimal) tableModel.getValueAt(i, 1);
            int quantity = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        totalPriceLabel.setText("Total Price: " + totalPrice.toString());
    }

    private void setupPaymentConfirmationDialogs() {
        jButton1.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to proceed with cash payment?",
                    "Confirm Payment",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Payment successful!");
                clearCheckout();
            }
        });

        jButton2.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to proceed with bank transfer?",
                    "Confirm Payment",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Payment successful!");
                clearCheckout();
            }
        });
    }

    private void clearCheckout() {
        // Clear the checkout table or container
        tableModel.setRowCount(0); // Assuming tableModel is used for the checkout table
        updateTotalPrice(); // Update the total price to reflect the cleared checkout
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        HeaderTitle = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        CheckoutTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        PaymentMethodTitle = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        SearchInput = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        HeaderTitle.setFont(new java.awt.Font(".VnCentury SchoolbookH", 3, 24)); // NOI18N
        HeaderTitle.setText("SALEMATE");
        HeaderTitle.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(388, 388, 388)
                .addComponent(HeaderTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(HeaderTitle)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        CheckoutTitle.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        CheckoutTitle.setText("THÔNG TIN HOÁ ĐƠN");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setText("Tiền mặt");

        jButton2.setText("Chuyển khoản");

        PaymentMethodTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        PaymentMethodTitle.setText("Phương thức thanh toán");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(111, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addGap(14, 14, 14))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(PaymentMethodTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PaymentMethodTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(CheckoutTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(CheckoutTitle)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        SearchInput.setText("Nhập tên sản phẩm cần tìm");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(SearchInput, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addGap(193, 193, 193))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 920, 620);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CashierPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CashierPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CashierPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CashierPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CashierPanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CheckoutTitle;
    private javax.swing.JTextField HeaderTitle;
    private javax.swing.JLabel PaymentMethodTitle;
    private javax.swing.JTextField SearchInput;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
