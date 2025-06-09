package com.salesmate.component;

import com.salesmate.controller.SalaryController;
import com.salesmate.controller.UserController;
import com.salesmate.model.User;

import javax.swing.*;
import java.awt.*;

public class AccountInfoPanel extends JPanel {
    public AccountInfoPanel(int userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Xem thông tin tài khoản
        JPanel viewInfoPanel = createViewInfoPanel(userId);
        tabbedPane.addTab("Xem thông tin", viewInfoPanel);

        // Tab 2: Cập nhật thông tin cá nhân
        JPanel updateInfoPanel = createUpdateInfoPanel(userId);
        tabbedPane.addTab("Cập nhật thông tin", updateInfoPanel);

        // Tab 3: Đổi mật khẩu
        JPanel changePasswordPanel = createChangePasswordPanel(userId);
        tabbedPane.addTab("Đổi mật khẩu", changePasswordPanel);

        // Tab 4: Cập nhật ảnh đại diện
        JPanel updateAvatarPanel = createUpdateAvatarPanel(userId);
        tabbedPane.addTab("Cập nhật ảnh đại diện", updateAvatarPanel);

        // Tab 5: Thông tin lương
        JPanel salaryInfoPanel = createSalaryInfoPanel(userId);
        tabbedPane.addTab("Xem thông tin lương", salaryInfoPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createViewInfoPanel(int userId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        UserController userController = new UserController();
        User user = userController.getUserById(userId);

        JLabel lblTitle = new JLabel("Thông tin tài khoản");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));

        JLabel lblName = new JLabel("Tên đăng nhập: " + (user != null ? user.getUsername() : ""));
        JLabel lblEmail = new JLabel("Email: " + (user != null ? user.getEmail() : ""));
        JLabel lblRole = new JLabel("Vai trò: " + (user != null ? user.getRole() : ""));
        JLabel lblJoinDate = new JLabel("Ngày gia nhập: " + (user != null && user.getCreatedAt() != null
            ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(user.getCreatedAt()) : ""));

        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblJoinDate.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(lblTitle, gbc);
        gbc.gridy++;
        panel.add(lblName, gbc);
        gbc.gridy++;
        panel.add(lblEmail, gbc);
        gbc.gridy++;
        panel.add(lblRole, gbc);
        gbc.gridy++;
        panel.add(lblJoinDate, gbc);

        return panel;
    }

    private JPanel createUpdateInfoPanel(int userId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        UserController userController = new UserController();
        User user = userController.getUserById(userId);

        JLabel lblTitle = new JLabel("Cập nhật thông tin cá nhân");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));

        JTextField txtEmail = new JTextField(user != null ? user.getEmail() : "", 20);

        JButton btnUpdate = new JButton("Cập nhật thông tin");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnUpdate.setBackground(new Color(46, 204, 113));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setOpaque(true);
        btnUpdate.setContentAreaFilled(true);
        btnUpdate.setBorderPainted(false);

        btnUpdate.addActionListener(e -> {
            String newEmail = txtEmail.getText().trim();
            if (user != null) {
                user.setEmail(newEmail);
                boolean ok = userController.updateUserInfo(user);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(lblTitle, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        panel.add(txtEmail, gbc);
        gbc.gridy++;
        panel.add(btnUpdate, gbc);

        return panel;
    }

    private JPanel createChangePasswordPanel(int userId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        UserController userController = new UserController();

        JLabel lblTitle = new JLabel("Đổi mật khẩu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));

        JPasswordField txtOldPassword = new JPasswordField(20);
        JPasswordField txtNewPassword = new JPasswordField(20);
        JPasswordField txtConfirmPassword = new JPasswordField(20);

        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        btnChangePassword.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnChangePassword.setBackground(new Color(46, 204, 113));
        btnChangePassword.setForeground(Color.WHITE);
        btnChangePassword.setOpaque(true);
        btnChangePassword.setContentAreaFilled(true);
        btnChangePassword.setBorderPainted(false);

        btnChangePassword.addActionListener(e -> {
            String oldPassword = new String(txtOldPassword.getPassword()).trim();
            String newPassword = new String(txtNewPassword.getPassword()).trim();
            String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new UserController().getUserById(userId); // Lấy thông tin user từ userId
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email không hợp lệ hoặc không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Debug email và mật khẩu cũ
            System.out.println("Email: " + user.getEmail());
            System.out.println("Mật khẩu cũ: " + oldPassword);
            System.out.println("Mật khẩu mới: " + newPassword);

            boolean ok = new UserController().resetPassword(user.getEmail(), oldPassword, newPassword);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại! Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(lblTitle, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Mật khẩu cũ:"), gbc);
        gbc.gridy++;
        panel.add(txtOldPassword, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridy++;
        panel.add(txtNewPassword, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Xác nhận mật khẩu mới:"), gbc);
        gbc.gridy++;
        panel.add(txtConfirmPassword, gbc);
        gbc.gridy++;
        panel.add(btnChangePassword, gbc);

        return panel;
    }

    private JPanel createUpdateAvatarPanel(int userId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        UserController userController = new UserController();
        User user = userController.getUserById(userId);

        JLabel lblTitle = new JLabel("Cập nhật ảnh đại diện");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));

        JLabel lblAvatar = new JLabel("Ảnh hiện tại:");
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        ImageIcon avatarIcon = new ImageIcon(user != null && user.getAvatar() != null ? user.getAvatar() : "default-avatar.png");
        JLabel avatarPreview = new JLabel(avatarIcon);

        JButton btnChooseFile = new JButton("Chọn ảnh");
        btnChooseFile.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnChooseFile.setBackground(new Color(52, 152, 219)); // Màu xanh giống nút nhập Excel
        btnChooseFile.setForeground(Color.WHITE);
        btnChooseFile.setOpaque(true);
        btnChooseFile.setContentAreaFilled(true);
        btnChooseFile.setBorderPainted(false);

        JButton btnUpdateAvatar = new JButton("Cập nhật ảnh");
        btnUpdateAvatar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnUpdateAvatar.setBackground(new Color(52, 152, 219)); // Màu xanh giống nút nhập Excel
        btnUpdateAvatar.setForeground(Color.WHITE);
        btnUpdateAvatar.setOpaque(true);
        btnUpdateAvatar.setContentAreaFilled(true);
        btnUpdateAvatar.setBorderPainted(false);

        final String[] selectedFilePath = {null}; // Biến lưu đường dẫn ảnh được chọn

        btnChooseFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFilePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                avatarPreview.setIcon(new ImageIcon(selectedFilePath[0]));
            }
        });

        btnUpdateAvatar.addActionListener(e -> {
            if (selectedFilePath[0] != null) {
                boolean ok = userController.updateAvatar(userId, selectedFilePath[0]);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(lblTitle, gbc);
        gbc.gridy++;
        panel.add(lblAvatar, gbc);
        gbc.gridy++;
        panel.add(avatarPreview, gbc);
        gbc.gridy++;
        panel.add(btnChooseFile, gbc);
        gbc.gridy++;
        panel.add(btnUpdateAvatar, gbc);

        return panel;
    }

    private JPanel createSalaryInfoPanel(int employeeId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thông tin lương");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JTable salaryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(salaryTable);

        // Lấy dữ liệu lương từ database
        SalaryController salaryController = new SalaryController();
        Object[][] salaryData = salaryController.getSalaryInfo(employeeId);
        String[] columnNames = {"Kỳ lương", "Ngày thanh toán", "Lương cơ bản", "Tổng lương", "Trạng thái", "Ghi chú"};

        salaryTable.setModel(new javax.swing.table.DefaultTableModel(salaryData, columnNames));
        salaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaryTable.setRowHeight(25);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}