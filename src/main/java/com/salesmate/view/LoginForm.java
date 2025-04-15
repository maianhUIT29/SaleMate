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
    private JLabel togglePasswordLabel;
    private boolean isPasswordVisible = false;
    private UserController userController;

    public LoginForm() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        userController = new UserController();
        setTitle("SalesMate");
        setSize(400, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(null); // Sử dụng absolute positioning
        mainPanel.setBorder(BorderFactory.createEmptyBorder());

        // Logo section - căn giữa trên cùng
        JLabel avatarLabel = new JLabel();
        URL imageURL = getClass().getClassLoader().getResource("img/avatar.png");
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledImage));
        }
        avatarLabel.setBounds(150, 30, 100, 100);
        mainPanel.add(avatarLabel);

        // Title 
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 150, 400, 30);
        mainPanel.add(titleLabel);

        // Email section
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setBounds(50, 220, 300, 20);
        mainPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 245, 300, 40);
        styleTextField(emailField, "Nhập email của bạn");
        mainPanel.add(emailField);

        // Password section
        JLabel passwordLabel = new JLabel("Mật khẩu"); 
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setBounds(50, 300, 300, 20);
        mainPanel.add(passwordLabel);

        mainPanel.add(createPasswordPanel());

        // Buttons với vị trí mới
        loginButton = createStyledButton("Đăng nhập", new Color(0, 123, 255));
        loginButton.setBounds(50, 385, 300, 45); // Di chuyển lên 15px
        mainPanel.add(loginButton);

        cameraLoginButton = createStyledButton("FaceID", new Color(40, 167, 69));
        cameraLoginButton.setBounds(50, 440, 300, 45); // Di chuyển lên 20px
        mainPanel.add(cameraLoginButton);

        forgotPasswordButton = createLinkButton("Quên mật khẩu?");
        forgotPasswordButton.setBounds(50, 495, 300, 30); // Di chuyển lên 25px
        mainPanel.add(forgotPasswordButton);

        add(mainPanel);

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

        cameraLoginButton.addActionListener(e -> {
            dispose(); // Đóng form login hiện tại
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginFaceID(); // Mở form FaceID login mới
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, 
                        "Không thể khởi động camera: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    new LoginForm(); // Nếu có lỗi thì quay lại form login
                }
            });
        });

        setVisible(true);
    }

    private void styleTextField(JComponent field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        if (field instanceof JTextField) {
            ((JTextField) field).putClientProperty("JTextField.placeholderText", placeholder);
        }
        field.setBackground(Color.WHITE);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(new Color(0, 123, 255));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(0, 86, 179));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(0, 123, 255));
            }
        });
        
        return button;
    }

    private JPanel createPasswordPanel() {
        JPanel passwordPanel = new JPanel(null);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBounds(50, 325, 300, 40);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setBounds(0, 0, 300, 40);
        passwordField.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu của bạn");
        styleTextField(passwordField, "Nhập mật khẩu của bạn"); // Thêm placeholder vào đây

        passwordPanel.add(passwordField);
        return passwordPanel;
    }

    // Hàm xử lý đăng nhập thông thường
    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Validate email format
        if (!isValidEmail(email)) {
            showToast("Email không hợp lệ!");
            return;
        }

        // Validate password không được trống
        if (password.trim().isEmpty()) {
            showToast("Vui lòng nhập mật khẩu!");
            return;
        }

        User user = userController.login(email, password);
        if (user != null) {
            // Lưu thông tin đăng nhập vào SessionManager
            SessionManager.getInstance().setLoggedInUser(user);
            dispose();
            if ("Sales Staff".equals(user.getRole())) {
                new CashierView().setVisible(true); // Ensure the view is visible
            } else if ("Store Manager".equals(user.getRole())) {
                new AdminView().setVisible(true); // Ensure the view is visible
            }
        } else {
            // Hiển thị toast thông báo lỗi trong 3 giây
            showToast("Email hoặc mật khẩu không chính xác!");
        }
    }

    // Thêm hàm kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches(emailRegex);
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
