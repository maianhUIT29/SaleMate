package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GradientPaint;
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
import com.salesmate.utils.UIHelper;

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
            UIHelper.removeFocusIndicators(); // Apply our focus removers to FlatLightLaf
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("🔑 Quên Mật khẩu - SalesMate");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("QUÊN MẬT KHẨU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Description text
        JLabel descLabel = new JLabel("<html>Vui lòng nhập email của bạn để nhận mật khẩu mới</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(descLabel, gbc);

        // Email field
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(300, 40));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        emailField.putClientProperty("JTextField.placeholderText", "Nhập email của bạn");
        gbc.gridy = 2;
        mainPanel.add(emailField, gbc);

        // Submit button
        submitButton = new JButton("Gửi yêu cầu");
        submitButton.setPreferredSize(new Dimension(300, 45));
        submitButton.setBackground(new Color(0, 123, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBorder(null);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3;
        mainPanel.add(submitButton, gbc);

        // Back to login link
        JButton backButton = new JButton("Quay lại đăng nhập");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(new Color(0, 123, 255));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        gbc.gridy = 4;
        mainPanel.add(backButton, gbc);

        add(mainPanel);

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

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                new LoginForm();
            }
        });

        // Apply no-focus styling to all components
        UIHelper.removeFocusFromAll(this);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIHelper.setupLookAndFeel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ForgotPasswordForm();
        });
    }
}
