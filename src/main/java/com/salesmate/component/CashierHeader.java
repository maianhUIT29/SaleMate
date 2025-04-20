package com.salesmate.component;

import org.apache.batik.swing.JSVGCanvas;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.net.URL;
import com.salesmate.model.User;
import com.salesmate.utils.ColorPalette;
import com.salesmate.utils.SessionManager;
import com.salesmate.view.LoginForm;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import java.beans.Beans;

public class CashierHeader extends javax.swing.JPanel {

    public CashierHeader() {
        initComponents();

        // chỉ chạy khi thực sự chạy chương trình
        if (!Beans.isDesignTime()) {
            createDropdown();
            loadUserData(); // Gọi ngay sau khi init xong UI
        }
    }

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

    public void loadUserData() {
        User user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {

            // Hiển thị avatar
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                try {
                    // Đảm bảo rằng đường dẫn avatar chính xác
                    ImageIcon icon = new ImageIcon(getClass().getResource("/img/avt/" + user.getAvatar()));
                    Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(new ImageIcon(img)))); // Làm tròn avatar ngay từ đầu
                    lblAvatar.setText(""); // Không hiển thị text
                } catch (Exception e) {
                    System.out.println("Lỗi load avatar: " + e.getMessage());
                    // Nếu không tìm thấy avatar, sử dụng ảnh mặc định
                    lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(new ImageIcon(getClass().getResource("/img/icons/ic_default_avt.png")))));
                }
            }
            
            // Cải thiện giao diện
            enhanceUI();
        }
    }

    private void enhanceUI() {
        // 1. Cải thiện avatar (viền tròn và bỏ màu nền)
        lblAvatar.setPreferredSize(new Dimension(40, 40)); // Đảm bảo kích thước chính xác
        lblAvatar.setOpaque(false); // Không có nền cho avatar

        // Làm tròn avatar ngay từ đầu
        lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(lblAvatar.getIcon())));

        // Thêm hiệu ứng hover cho avatar
        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAvatar.setBackground(new Color(230, 230, 230)); // Đổi màu nền khi hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAvatar.setBackground(null); // Trở lại trạng thái không có nền khi hover ra
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Hiển thị dropdown khi click vào avatar
                createDropdown();
            }
        });    
    }

    private void createDropdown() {
        // Tạo một JPopupMenu (dropdown menu)
        JPopupMenu popupMenu = new JPopupMenu();

        // Tạo các mục cho menu
        JMenuItem homeMenuItem = new JMenuItem("Trang chủ");
        homeMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Dùng font của Bootstrap
        homeMenuItem.setBackground(new Color(245, 245, 245)); // Nền sáng
        homeMenuItem.setForeground(Color.BLACK); // Màu chữ đen
        homeMenuItem.addActionListener(e -> {
            // Hành động cho Trang chủ
            System.out.println("Trang chủ clicked");
        });

        JMenuItem salaryMenuItem = new JMenuItem("Lương");
        salaryMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaryMenuItem.setBackground(new Color(245, 245, 245));
        salaryMenuItem.setForeground(Color.BLACK);
        salaryMenuItem.addActionListener(e -> {
            // Hành động cho Lương
            System.out.println("Lương clicked");
        });

        JMenuItem accountMenuItem = new JMenuItem("Tài khoản");
        accountMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountMenuItem.setBackground(new Color(245, 245, 245));
        accountMenuItem.setForeground(Color.BLACK);
        accountMenuItem.addActionListener(e -> {
            // Hành động cho Tài khoản
            System.out.println("Tài khoản clicked");
        });

        JMenuItem logoutMenuItem = new JMenuItem("Đăng xuất");
        logoutMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutMenuItem.setBackground(new Color(245, 245, 245));
        logoutMenuItem.setForeground(Color.BLACK);
        logoutMenuItem.addActionListener(e -> {
            // Hành động cho Đăng xuất
            System.out.println("Đăng xuất clicked");

            // Đăng xuất và quay lại màn hình đăng nhập (hoặc thực hiện hành động cần thiết)
            SessionManager.getInstance().logout();  // Đăng xuất người dùng

            // Mở cửa sổ đăng nhập (LoginForm) sau khi đăng xuất
            showLoginForm();  // Hàm này sẽ mở cửa sổ đăng nhập
        });

        // Thêm các mục vào dropdown menu
        popupMenu.add(homeMenuItem);
        popupMenu.add(salaryMenuItem);
        popupMenu.add(accountMenuItem);
        popupMenu.add(logoutMenuItem);

        // Đảm bảo rằng popup menu có viền
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        popupMenu.setBackground(new Color(255, 255, 255)); // Màu nền trắng cho menu

        // Đảm bảo dropdown menu xuất hiện khi bấm vào lblAvatar (Avatar Button)
        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Hiển thị menu ngay dưới lblAvatar và canh giữa dropdown với avatar
                int x = -(popupMenu.getWidth() - lblAvatar.getWidth() / 2) - 60; // Dịch chuyển sang trái thêm 20
                popupMenu.show(lblAvatar, x, lblAvatar.getHeight());
            }
        });
    }

    private void showLoginForm() {
        // Đóng cửa sổ hiện tại nếu cần thiết (tùy theo cách ứng dụng của bạn tổ chức)
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            currentFrame.dispose();  // Đóng cửa sổ hiện tại (CashierPanel)
        }

        // Mở cửa sổ LoginForm
        LoginForm loginForm = new LoginForm();  // Khởi tạo cửa sổ LoginForm
        loginForm.setVisible(true);  // Hiển thị cửa sổ đăng nhập
    }

    // Phương thức để làm tròn avatar
    private Image getRoundedAvatar(Icon avatarIcon) {
        ImageIcon icon = (ImageIcon) avatarIcon;
        Image image = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(0, 0, image.getWidth(null), image.getHeight(null)); // Vẽ hình tròn
        g2d.setClip(new Ellipse2D.Float(0, 0, image.getWidth(null), image.getHeight(null))); // Cắt hình tròn
        g2d.drawImage(image, 0, 0, null); // Vẽ ảnh vào hình tròn
        g2d.dispose();
        return bufferedImage; // Trả về ảnh đã làm tròn
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        home_btn = new javax.swing.JMenuItem();
        salary_btn = new javax.swing.JMenuItem();
        account_btn = new javax.swing.JMenuItem();
        logout_btn = new javax.swing.JMenuItem();
        lblHeaderTitle = new javax.swing.JLabel();
        lblAvatar = new javax.swing.JLabel();

        home_btn.setText("Trang chủ");
        home_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_btnActionPerformed(evt);
            }
        });
        jPopupMenu1.add(home_btn);

        salary_btn.setText("Lương");
        jPopupMenu1.add(salary_btn);

        account_btn.setText("Tài khoản");
        jPopupMenu1.add(account_btn);

        logout_btn.setText("Đăng xuất");
        jPopupMenu1.add(logout_btn);

        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblHeaderTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeaderTitle.setText("SALEMATE");

        lblAvatar.setText("Avatar");
        lblAvatar.setPreferredSize(new java.awt.Dimension(34, 34));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(420, 420, 420)
                .addComponent(lblHeaderTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
                .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAvatar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblHeaderTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void home_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_home_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_home_btnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem account_btn;
    private javax.swing.JMenuItem home_btn;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JMenuItem logout_btn;
    private javax.swing.JMenuItem salary_btn;
    // End of variables declaration//GEN-END:variables
}
