
package com.salesmate.component;

import com.salesmate.model.Product;

import java.util.List;

public class CheckoutPanel extends javax.swing.JPanel {
    
   private List<Product> products;
   private double totalPrice;
    
    public CheckoutPanel() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paymentDialog = new javax.swing.JDialog();
        lblHeader = new javax.swing.JLabel();
        ProductListContainer = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        btnPayment = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        paymentMethodComboBox = new javax.swing.JComboBox<>();
        lblTotalValue = new javax.swing.JLabel();
        lblPaymentMethod = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        javax.swing.GroupLayout paymentDialogLayout = new javax.swing.GroupLayout(paymentDialog.getContentPane());
        paymentDialog.getContentPane().setLayout(paymentDialogLayout);
        paymentDialogLayout.setHorizontalGroup(
            paymentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );
        paymentDialogLayout.setVerticalGroup(
            paymentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setAutoscrolls(true);

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("SALEMATE - HOÁ ĐƠN BÁN HÀNG");
        lblHeader.setRequestFocusEnabled(false);

        tblProduct.setAutoCreateRowSorter(true);
        tblProduct.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tên sản phẩm", "Giá", "Số Lượng", "Thành tiền"
            }
        ));
        tblProduct.setToolTipText("");
        tblProduct.setName(""); // NOI18N
        ProductListContainer.setViewportView(tblProduct);
        if (tblProduct.getColumnModel().getColumnCount() > 0) {
            tblProduct.getColumnModel().getColumn(0).setMinWidth(80);
            tblProduct.getColumnModel().getColumn(0).setPreferredWidth(120);
            tblProduct.getColumnModel().getColumn(1).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(1).setPreferredWidth(10);
            tblProduct.getColumnModel().getColumn(2).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(2).setPreferredWidth(10);
            tblProduct.getColumnModel().getColumn(3).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(3).setPreferredWidth(10);
        }

        btnPayment.setBackground(new java.awt.Color(0, 123, 255));
        btnPayment.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnPayment.setText("Thanh toán");

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotal.setText("Tổng tiền :");

        paymentMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền mặt", "Chuyển khoản" }));
        paymentMethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentMethodComboBoxActionPerformed(evt);
            }
        });

        lblTotalValue.setText("VND");

        lblPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPaymentMethod.setText("Phương thức thanh toán");

        btnCancel.setBackground(new java.awt.Color(255, 0, 0));
        btnCancel.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("Huỷ");

        btnPrint.setBackground(new java.awt.Color(40, 167, 69));
        btnPrint.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setText("In hoá đơn");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(252, 252, 252)
                                .addComponent(lblTotalValue, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                            .addComponent(ProductListContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblPaymentMethod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(paymentMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProductListContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalValue, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaymentMethod, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(paymentMethodComboBox))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPayment, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void paymentMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentMethodComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paymentMethodComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ProductListContainer;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPayment;
    private javax.swing.JButton btnPrint;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JDialog paymentDialog;
    private javax.swing.JComboBox<String> paymentMethodComboBox;
    private javax.swing.JTable tblProduct;
    // End of variables declaration//GEN-END:variables
}
