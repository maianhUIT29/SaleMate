package com.salesmate.view;

import com.formdev.flatlaf.FlatLightLaf;
import com.salesmate.controller.UserController;
import com.salesmate.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private UserController userController;

    public LoginForm() {
        // Áp dụng giao diện đẹp bằng FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        userController = new UserController();
        setTitle("🔑 Đăng nhập - SalesMate");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Tạo panel chính với Gradient Background
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Tiêu đề căn giữa
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.BLACK); // Màu chữ

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10)); // Khoảng cách

        // Ô nhập tên đăng nhập
        JLabel usernameLabel = new JLabel("🔒 Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 30));

        // Ô nhập mật khẩu
        JLabel passwordLabel = new JLabel("🔑 Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));

        // Nút đăng nhập
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thông báo trạng thái
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Layout căn giữa
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1, 10, 10));
        inputPanel.setOpaque(false); // Để nền gradient hiển thị
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);

        add(mainPanel, BorderLayout.CENTER);

        // Xử lý sự kiện khi nhấn "Đăng nhập"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        setVisible(true);
    }

    // Hàm xử lý đăng nhập
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = userController.login(username, password);
        if (user != null) {
            statusLabel.setText("✅ Đăng nhập thành công!");
            JOptionPane.showMessageDialog(this, "Chào " + user.getUsername() + "!");
            dispose(); // Đóng cửa sổ login
        } else {
            statusLabel.setText("❌ Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    // Lớp JPanel custom để vẽ background gradient
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color color1 = new Color(173, 216, 230); // Màu xanh nhạt
            Color color2 = new Color(224, 255, 255); // Màu xanh pastel

            GradientPaint gradient = new GradientPaint(0, 0, color1, 0, height, color2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
