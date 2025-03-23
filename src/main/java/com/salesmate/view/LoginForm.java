package com.salesmate.view;

import com.formdev.flatlaf.FlatLightLaf;
import com.salesmate.controller.UserController;
import com.salesmate.model.User;
import com.salesmate.utils.SessionManager;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.salesmate.utils.ColorPalette;

public class LoginForm extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, cameraLoginButton, forgotPasswordButton;
    private UserController userController;

    public LoginForm() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        userController = new UserController();
        setTitle("🔑 Đăng nhập - SalesMate");
        setSize(450, 600);  // Mở rộng form đăng nhập
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Avatar icon
        JLabel avatarLabel = new JLabel();
        URL imageURL = getClass().getClassLoader().getResource("img/avatar.png");
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("⚠️ Không tìm thấy ảnh avatar.png");
        }
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(avatarLabel, gbc);

        // Title label
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 30, 30));
        gbc.gridy = 1;
        mainPanel.add(titleLabel, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 30));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Login button
        Dimension buttonSize = new Dimension(200, 40); // Button size
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(buttonSize);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(loginButton, gbc);

        // Camera login button
        cameraLoginButton = new JButton("FaceID");
        cameraLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cameraLoginButton.setBackground(new Color(33, 150, 243));
        cameraLoginButton.setForeground(Color.WHITE);
        cameraLoginButton.setPreferredSize(buttonSize);
        cameraLoginButton.setFocusPainted(false);
        cameraLoginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        mainPanel.add(cameraLoginButton, gbc);

        // "Hoặc" label
        JLabel orLabel = new JLabel("Hoặc", SwingConstants.CENTER);
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 6;
        mainPanel.add(orLabel, gbc);

        // Forgot password button
        forgotPasswordButton = new JButton("Quên mật khẩu?");
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPasswordButton.setBackground(ColorPalette.INFO);
        forgotPasswordButton.setForeground(ColorPalette.WHITE);
        forgotPasswordButton.setPreferredSize(buttonSize);
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setBorder(BorderFactory.createLineBorder(ColorPalette.INFO, 1));
        gbc.gridy = 7;
        mainPanel.add(forgotPasswordButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Action listeners
        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ForgotPasswordForm();
                dispose();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        cameraLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFaceID();
                dispose();
            }
        });

        setVisible(true);
    }

    // Hàm xử lý đăng nhập thông thường
    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        User user = userController.login(email, password);
        if (user != null) {
            // Lưu thông tin đăng nhập vào SessionManager
            SessionManager.getInstance().setLoggedInUser(user);
            dispose();
            if ("Sales Staff".equals(user.getRole())) {
                new CashierPanel().setVisible(true); // Ensure the view is visible
            } else if ("Store Manager".equals(user.getRole())) {
                new AdminView().setVisible(true); // Ensure the view is visible
            }
        } else {
            // Hiển thị toast thông báo lỗi trong 3 giây
            showToast("Email hoặc mật khẩu không chính xác!");
        }
    }

    // Hàm hiển thị toast thông báo lỗi
    private void showToast(String message) {
        final JDialog toast = new JDialog(this, "Thông báo", true);
        toast.setSize(300, 60);
        toast.setLocationRelativeTo(this);
        toast.setUndecorated(true);
        toast.setBackground(new Color(0, 0, 0, 0)); // Transparent background

        JPanel toastPanel = new JPanel(new BorderLayout());
        toastPanel.setBackground(new Color(255, 0, 0, 180));  // Red background for error

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setOpaque(false);

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(255, 0, 0, 180));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toast.dispose();
            }
        });

        toastPanel.add(label, BorderLayout.CENTER);
        toastPanel.add(closeButton, BorderLayout.EAST);
        toast.add(toastPanel);
        toast.setVisible(true);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.dispose();  // Close the toast after 3 seconds
            }
        }, 3000);
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
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
