package com.salesmate.view;

import com.formdev.flatlaf.FlatLightLaf;
import com.salesmate.controller.UserController;
import com.salesmate.model.User;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, cameraLoginButton, forgotPasswordButton;
    private JLabel statusLabel;
    private UserController userController;
    private Webcam webcam;
    private WebcamPanel webcamPanel;

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

        // Icon người dùng (Chỉnh kích thước phù hợp)
        JLabel avatarLabel = new JLabel();
        URL imageURL = getClass().getResource("/img/avatar.png");
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("⚠️ Không tìm thấy ảnh avatar.png");
        }
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(avatarLabel, gbc);

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 30, 30));
        gbc.gridy = 1;
        mainPanel.add(titleLabel, gbc);

        // Nhãn và field email với icon trong ô input
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Tô màu đậm các nhãn input
        emailLabel.setHorizontalAlignment(SwingConstants.LEFT);  // Đặt nhãn hoàn toàn bên trái
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 30));  // Tăng kích thước ô input
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230), 1, true),
                BorderFactory.createEmptyBorder(5, 30, 5, 10)));  // Thêm không gian để đặt icon
        JLabel emailIcon = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/img/ic_email.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        emailIcon.setBounds(5, 5, 20, 20);
        emailField.setLayout(null);
        emailField.add(emailIcon);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Mật khẩu với icon trong ô input
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Tô màu đậm các nhãn input
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);  // Đặt nhãn hoàn toàn bên trái
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 30));  // Tăng kích thước ô input
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230), 1, true),
                BorderFactory.createEmptyBorder(5, 30, 5, 10)));  // Thêm không gian để đặt icon
        JLabel passwordIcon = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/img/ic_password.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        passwordIcon.setBounds(5, 5, 20, 20);
        passwordField.setLayout(null);
        passwordField.add(passwordIcon);
        JButton togglePasswordButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/ic_eye.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        togglePasswordButton.setBounds(270, 5, 20, 20);
        togglePasswordButton.setBorder(null);
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        passwordField.add(togglePasswordButton);
        togglePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (passwordField.getEchoChar() == '\u2022') {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('\u2022');
                }
            }
        });
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Nút đăng nhập
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        // Nút đăng nhập bằng camera
        cameraLoginButton = new JButton("Đăng nhập bằng Camera");
        cameraLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cameraLoginButton.setBackground(new Color(33, 150, 243));
        cameraLoginButton.setForeground(Color.WHITE);
        cameraLoginButton.setFocusPainted(false);
        cameraLoginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        mainPanel.add(cameraLoginButton, gbc);

        // Thêm chữ "Hoặc"
        JLabel orLabel = new JLabel("Hoặc", SwingConstants.CENTER);
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 6;
        mainPanel.add(orLabel, gbc);

        // Nút quên mật khẩu
        forgotPasswordButton = new JButton("Quên mật khẩu?");
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPasswordButton.setBackground(Color.LIGHT_GRAY);
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setFocusPainted(false);
        gbc.gridy = 7;
        mainPanel.add(forgotPasswordButton, gbc);

        // Trạng thái
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 8;
        mainPanel.add(statusLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        cameraLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCamera();
            }
        });

        setVisible(true);
    }

    // Mở webcam để chụp ảnh
    private void openCamera() {
        webcam = Webcam.getDefault();
        webcam.open();

        // Create a panel to display the camera feed
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(new Dimension(400, 300));

        // Create a dialog to show the webcam feed
        JDialog cameraDialog = new JDialog(this, "Chụp ảnh", true);
        cameraDialog.setLayout(new BorderLayout());
        cameraDialog.add(webcamPanel, BorderLayout.CENTER);
        cameraDialog.setSize(400, 350);
        cameraDialog.setLocationRelativeTo(this);

        // Allow capturing a snapshot from the webcam
        JButton captureButton = new JButton("Chụp ảnh");
        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Capture the image
                Image image = webcam.getImage();
                // You can now save the image or process it further here (e.g., for facial recognition)
                JOptionPane.showMessageDialog(cameraDialog, "Đã chụp ảnh!");
                cameraDialog.dispose();  // Close the camera dialog
            }
        });

        // Button to close the webcam
        JButton closeButton = new JButton("Tắt Webcam");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                webcam.close();
                cameraDialog.dispose();  // Close the camera dialog
            }
        });

        JPanel panel = new JPanel();
        panel.add(captureButton);
        panel.add(closeButton);
        cameraDialog.add(panel, BorderLayout.SOUTH);

        cameraDialog.setVisible(true);  // Ensure the dialog is visible after adding components
    }

    // Hàm xử lý đăng nhập thông thường
    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        User user = userController.login(email, password);
        if (user != null) {
            statusLabel.setText("✅ Đăng nhập thành công!");
            JOptionPane.showMessageDialog(this, "Chào " + user.getUsername() + "!");
            dispose();
        } else {
            statusLabel.setText("❌ Sai email hoặc mật khẩu!");
        }
    }

    // Mở form quên mật khẩu
    private void showForgotPasswordForm() {
        new ForgotPasswordForm();
        dispose();
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
