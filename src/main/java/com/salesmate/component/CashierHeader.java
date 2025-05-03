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

    private final int AVATAR_SIZE = 32; // Reduced from 32 to 24 pixels

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
            try {
                // Đảm bảo luôn có một icon, bằng cách set default icon trước
                ImageIcon defaultIcon = null;
                try {
                    URL defaultUrl = getClass().getResource("/img/icons/ic_default_avt.png");
                    if (defaultUrl != null) {
                        ImageIcon tempIcon = new ImageIcon(defaultUrl);
                        // Resize icon to match AVATAR_SIZE
                        Image image = tempIcon.getImage().getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
                        defaultIcon = new ImageIcon(image);
                    } else {
                        // Nếu không tìm thấy default icon, tạo một BufferedImage trống
                        BufferedImage emptyImage = createDefaultAvatar(user);
                        defaultIcon = new ImageIcon(emptyImage);
                    }
                } catch (Exception e) {
                    System.out.println("Không thể tải ảnh mặc định: " + e.getMessage());
                    // Tạo một BufferedImage trống nếu không thể tải ảnh mặc định
                    BufferedImage emptyImage = createDefaultAvatar(user);
                    defaultIcon = new ImageIcon(emptyImage);
                }
                
                // Cố gắng tải avatar từ user, nếu có
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    URL imageUrl = getClass().getResource("/img/avt/" + user.getAvatar());
                    if (imageUrl != null) {
                        ImageIcon icon = new ImageIcon(imageUrl);
                        Image img = icon.getImage().getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
                        lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(new ImageIcon(img))));
                        lblAvatar.setText(""); // Không hiển thị text
                    } else {
                        // Nếu không tìm thấy avatar, sử dụng ảnh mặc định đã tạo
                        lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(defaultIcon)));
                        lblAvatar.setText(""); // Không hiển thị text
                    }
                } else {
                    // Nếu user không có avatar, sử dụng ảnh mặc định đã tạo
                    lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(defaultIcon)));
                    lblAvatar.setText(""); // Không hiển thị text
                }
            } catch (Exception e) {
                System.out.println("Lỗi load avatar: " + e.getMessage());
                e.printStackTrace();
                // Tạo một avatar trống nếu xảy ra lỗi
                BufferedImage emptyImage = createDefaultAvatar(user);
                lblAvatar.setIcon(new ImageIcon(emptyImage));
                lblAvatar.setText(""); // Không hiển thị text
            }
            
            // Cải thiện giao diện
            enhanceUI();
        }
    }

    private BufferedImage createDefaultAvatar(User user) {
        BufferedImage emptyImage = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = emptyImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(100, 180, 255),
            AVATAR_SIZE, AVATAR_SIZE, new Color(80, 150, 230)
        );
        g2d.setPaint(gradient);
        g2d.fillOval(0, 0, AVATAR_SIZE, AVATAR_SIZE);
        
        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(1, 1, AVATAR_SIZE-3, AVATAR_SIZE-3);
        
        // Add user's initial if available
        if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
            String initial = user.getUsername().substring(0, 1).toUpperCase();
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, AVATAR_SIZE/2));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(initial);
            int textHeight = fm.getHeight();
            g2d.drawString(initial, (AVATAR_SIZE - textWidth) / 2, 
                          (AVATAR_SIZE + textHeight/3) / 2);
        }
        
        g2d.dispose();
        return emptyImage;
    }

    private void enhanceUI() {
        // 1. Cải thiện avatar (viền tròn và bỏ màu nền)
        lblAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        lblAvatar.setSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        lblAvatar.setMinimumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        lblAvatar.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        lblAvatar.setOpaque(false); // Không có nền cho avatar

        // Make sure the avatar is sized correctly and visible
        if (lblAvatar.getIcon() != null) {
            int iconWidth = lblAvatar.getIcon().getIconWidth();
            int iconHeight = lblAvatar.getIcon().getIconHeight();
            
            if (iconWidth != AVATAR_SIZE || iconHeight != AVATAR_SIZE) {
                // Resize the icon if needed
                Image img = ((ImageIcon)lblAvatar.getIcon()).getImage();
                Image newImg = img.getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(getRoundedAvatar(new ImageIcon(newImg))));
            }
        }

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

        // Tạo các mục cho menu với style rõ ràng
        JMenuItem homeMenuItem = createMenuItem("Trang chủ");
        JMenuItem salaryMenuItem = createMenuItem("Lương");
        JMenuItem accountMenuItem = createMenuItem("Tài khoản");
        JMenuItem logoutMenuItem = createMenuItem("Đăng xuất");

        // Add action listeners
        homeMenuItem.addActionListener(e -> {
            System.out.println("Trang chủ clicked");
        });

        salaryMenuItem.addActionListener(e -> {
            System.out.println("Lương clicked");
        });

        accountMenuItem.addActionListener(e -> {
            System.out.println("Tài khoản clicked");
        });

        logoutMenuItem.addActionListener(e -> {
            System.out.println("Đăng xuất clicked");
            SessionManager.getInstance().logout();
            showLoginForm();
        });

        // Thêm các mục vào dropdown menu
        popupMenu.add(homeMenuItem);
        popupMenu.add(salaryMenuItem);
        popupMenu.add(accountMenuItem);
        popupMenu.addSeparator(); // Thêm một đường phân cách trước mục đăng xuất
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

    // Helper method to create properly styled menu items
    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBackground(new Color(245, 245, 245));
        item.setForeground(new Color(33, 37, 41));
        
        // Make sure opacity is set correctly
        item.setOpaque(true);
        
        // Add hover effect
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(13, 110, 253));  // Bootstrap primary color
                item.setForeground(Color.WHITE);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(245, 245, 245));
                item.setForeground(new Color(33, 37, 41));
            }
        });
        
        return item;
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

    // Modified method to properly handle rounded avatar with consistent size
    private Image getRoundedAvatar(Icon avatarIcon) {
        Image image = ((ImageIcon) avatarIcon).getImage();
        BufferedImage bufferedImage = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        
        // Enable antialiasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Make a round clipping region
        g2d.setClip(new Ellipse2D.Float(0, 0, AVATAR_SIZE, AVATAR_SIZE));
        
        // Draw the image scaled to fit
        g2d.drawImage(image, 0, 0, AVATAR_SIZE, AVATAR_SIZE, null);
        
        // Add a nice border
        g2d.setClip(null);
        g2d.setColor(new Color(255, 255, 255, 100)); // Semi-transparent white
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(0, 0, AVATAR_SIZE-1, AVATAR_SIZE-1);
        
        g2d.dispose();
        return bufferedImage;
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
                        .addGap(8, 8, 8))))
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
