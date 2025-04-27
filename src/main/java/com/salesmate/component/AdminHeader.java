package com.salesmate.component;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import javax.swing.JPopupMenu;

public class AdminHeader extends javax.swing.JPanel {
private JPopupMenu notiMenu;
    private JPopupMenu accountMenu;
   
    public AdminHeader() {
        initComponents();
         // 2. Instantiation popup menus và gắn panel 
        notiMenu = new JPopupMenu();
        notiMenu.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY));
        notiMenu.add(new AdNotificationPopup());

        accountMenu = new JPopupMenu();
        accountMenu.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY));
        accountMenu.add(new AdAccountPopup());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblHeader = new javax.swing.JLabel();
        tbtnNotification = new javax.swing.JToggleButton();
        tbtnAdminAccount = new javax.swing.JToggleButton();

        lblHeader.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblHeader.setText("SALEMATE");

        buttonGroup1.add(tbtnNotification);
        tbtnNotification.setForeground(new java.awt.Color(255, 255, 255));
        tbtnNotification.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons/ic_notification.png"))); // NOI18N
        tbtnNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnNotificationActionPerformed(evt);
            }
        });

        buttonGroup1.add(tbtnAdminAccount);
        tbtnAdminAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons/ic_admin.png"))); // NOI18N
        tbtnAdminAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnAdminAccountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tbtnNotification)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtnAdminAccount))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHeader)
                        .addComponent(tbtnNotification))
                    .addComponent(tbtnAdminAccount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tbtnNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbtnNotificationActionPerformed
        // TODO add your handling code here:
         if (tbtnNotification.isSelected()) {
            // ensure account popup đóng trước
            accountMenu.setVisible(false);
            // show notification popup ngay dưới nút
            notiMenu.show(tbtnNotification, 0, tbtnNotification.getHeight());
        } else {
            // ấn lại để đóng
            notiMenu.setVisible(false);
        }
    }//GEN-LAST:event_tbtnNotificationActionPerformed

    private void tbtnAdminAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbtnAdminAccountActionPerformed
        // TODO add your handling code here:
          if (tbtnAdminAccount.isSelected()) {
            notiMenu.setVisible(false);
            accountMenu.show(tbtnAdminAccount, 0, tbtnAdminAccount.getHeight());
        } else {
            accountMenu.setVisible(false);
        }
    }//GEN-LAST:event_tbtnAdminAccountActionPerformed

   @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ gradient background cho header
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 123, 255), // Bắt đầu màu xanh dương
                0, getHeight(), new Color(0, 102, 204) // Kết thúc màu xanh đậm
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Vẽ gradient nền
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JToggleButton tbtnAdminAccount;
    private javax.swing.JToggleButton tbtnNotification;
    // End of variables declaration//GEN-END:variables
}
