package com.salesmate.component;
import com.salesmate.utils.SessionManager;
import com.salesmate.view.LoginForm;
import java.awt.Window;
import javax.swing.SwingUtilities;
import com.salesmate.view.AdminView;

public class AdAccountPopup extends javax.swing.JPanel {
   private AdminView parentView; // Đối tượng AdminView
    /**
     * Creates new form AdAccount
     */
    public AdAccountPopup() {
        initComponents();
         
    }
 public void setParentView(AdminView parentView) {
        this.parentView = parentView;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tbtnChangePassword = new javax.swing.JToggleButton();
        tbtnLogout = new javax.swing.JToggleButton();

        setLayout(new java.awt.GridLayout(2, 0));

        buttonGroup1.add(tbtnChangePassword);
        tbtnChangePassword.setText("Đổi mật khẩu");
        add(tbtnChangePassword);

        buttonGroup1.add(tbtnLogout);
        tbtnLogout.setText("Đăng xuất");
        tbtnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnLogoutActionPerformed(evt);
            }
        });
        add(tbtnLogout);
    }// </editor-fold>//GEN-END:initComponents

    private void tbtnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbtnLogoutActionPerformed
        // TODO add your handling code here:
   // 1) Xóa session
    SessionManager.getInstance().logout();
    // 2) Mở LoginForm
    new LoginForm().setVisible(true);
    // 3) Đóng AdminView
    Window window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
        window.dispose();
    }
    }//GEN-LAST:event_tbtnLogoutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JToggleButton tbtnChangePassword;
    private javax.swing.JToggleButton tbtnLogout;
    // End of variables declaration//GEN-END:variables
}
