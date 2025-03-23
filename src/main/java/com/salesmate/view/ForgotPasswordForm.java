package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
import com.salesmate.controller.UserController;

public class ForgotPasswordForm extends JFrame {

    private JTextField emailField;
    private JButton submitButton;
    private JLabel statusLabel;
    private JDialog loadingDialog;
    private Timer loadingAnimationTimer;
    private int loadingDotCount = 0;

    public ForgotPasswordForm() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tăng kích thước của form
        setTitle("🔑 Quên Mật khẩu - SalesMate");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Căn chỉnh nhãn tiêu đề
        JLabel titleLabel = new JLabel("QUÊN MẬT KHẨU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleLabel.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Căn chỉnh nhãn email
        JLabel emailLabel = new JLabel("Nhập email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emailLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0; // Không cần mở rộng nhãn
        mainPanel.add(emailLabel, gbc);

        // Căn chỉnh trường nhập liệu email
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emailField.setPreferredSize(new Dimension(250, 40)); // Điều chỉnh chiều rộng ô input
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0; // Ô input có thể mở rộng chiều ngang
        mainPanel.add(emailField, gbc);

        // Thêm FlowLayout để căn giữa nút gửi
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Gửi");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        submitButton.setBackground(new Color(33, 150, 243));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.setPreferredSize(new Dimension(100, 40)); // Giới hạn chiều rộng nút
        buttonPanel.add(submitButton); // Thêm nút vào JPanel

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc); // Thêm JPanel chứa nút gửi vào GridBagLayout

        // Căn chỉnh nhãn trạng thái
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 3;
        mainPanel.add(statusLabel, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();

                // Kiểm tra nếu email hợp lệ
                if (email == null || email.isEmpty()) {
                    statusLabel.setText("Vui lòng nhập email!");
                    return;
                }

                showLoading();

                // Gọi UserController để reset mật khẩu
                UserController userController = new UserController();
                boolean isReset = userController.resetPassword(email);

                hideLoading();

                // Cập nhật trạng thái sau khi gửi email
                if (isReset) {
                    showToast("Đã gửi mật khẩu mới qua email!");
                    // Hiển thị toast trong 3 giây và chuyển hướng đến đăng nhập
                    Timer timer = new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new LoginForm();
                            dispose();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    showToast("Có lỗi xảy ra, vui lòng thử lại!");
                }
            }
        });

        add(mainPanel, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                new LoginForm();
            }
        });

        setVisible(true);
    }

    private void showLoading() {
        loadingDialog = new JDialog(this);
        loadingDialog.setUndecorated(true);
        loadingDialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("Đang xử lý", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        loadingDialog.add(label, BorderLayout.CENTER);

        loadingDialog.setSize(200, 50);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setVisible(true);

        loadingAnimationTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingDotCount = (loadingDotCount + 1) % 4;
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < loadingDotCount; i++) {
                    dots.append(".");
                }
                label.setText("Đang xử lý" + dots.toString());
            }
        });
        loadingAnimationTimer.start();
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dispose();
        }
        if (loadingAnimationTimer != null) {
            loadingAnimationTimer.stop();
        }
    }

    private void showToast(String message) {
        JDialog dialog = new JDialog(this);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        dialog.add(label, BorderLayout.CENTER);

        dialog.setSize(300, 50);
        dialog.setLocationRelativeTo(this);

        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color color1 = new Color(224, 255, 255);
            Color color2 = new Color(173, 216, 230);

            GradientPaint gradient = new GradientPaint(0, 0, color1, 0, height, color2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ForgotPasswordForm::new);
    }
}
