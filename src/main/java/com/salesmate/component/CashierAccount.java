/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesmate.component;

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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.salesmate.controller.EmployeeController;
import com.salesmate.controller.UserController;
import com.salesmate.model.Employee;
import com.salesmate.model.User;
import com.salesmate.utils.SessionManager;

public class CashierAccount extends JPanel {
    
    // Bootstrap color scheme
    private static final Color PRIMARY = new Color(13, 110, 253);        // Bootstrap Primary
    private static final Color SECONDARY = new Color(108, 117, 125);     // Bootstrap Secondary
    private static final Color SUCCESS = new Color(25, 135, 84);         // Bootstrap Success
    private static final Color DANGER = new Color(220, 53, 69);          // Bootstrap Danger
    private static final Color WARNING = new Color(255, 193, 7);         // Bootstrap Warning
    private static final Color INFO = new Color(13, 202, 240);           // Bootstrap Info
    private static final Color LIGHT = new Color(248, 249, 250);         // Bootstrap Light
    private static final Color DARK = new Color(33, 37, 41);             // Bootstrap Dark
    private static final Color WHITE = new Color(255, 255, 255);         // Bootstrap White
    private static final Color LIGHT_GRAY = new Color(222, 226, 230);    // Bootstrap Gray-300
    
    private UserController userController;
    private EmployeeController employeeController;
    
    // User info components
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbStatus;
    private JLabel lblCreatedAt;
    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    
    // Employee info components
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtPhone;
    private JTextArea txtAddress;
    private JTextField txtEmergencyContact;
    private JTextField txtEmergencyPhone;
    private JLabel lblBirthDate;
    private JLabel lblHireDate;
    private JLabel lblEmployeeRole;
    
    // Account buttons
    private JButton btnUpdateAccount;
    private JButton btnSaveAccount;
    private JButton btnCancelAccount;
    
    // Employee buttons
    private JButton btnUpdateEmployee;
    private JButton btnSaveEmployee;
    private JButton btnCancelEmployee;
    
    // Password button
    private JButton btnChangePassword;
    private JButton btnRefresh;
    
    // Edit states
    private boolean isEditingAccount = false;
    private boolean isEditingEmployee = false;
    
    // Backup data for cancel functionality
    private User originalUser;
    private Employee originalEmployee;
    
    public CashierAccount() {
        initializeControllers();
        initComponents();
        loadUserData();
        setupEventHandlers();
    }
    
    private void initializeControllers() {
        userController = new UserController();
        employeeController = new EmployeeController();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(LIGHT);
        
        // Main scrollable container
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, LIGHT, 
                                                   getWidth(), getHeight(), new Color(240, 245, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Title panel with fixed refresh button
        JPanel titlePanel = createTitlePanel();
        
        // Content panel with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        contentPanel.setOpaque(false);
        
        // Left panel - User Account Info
        JPanel userInfoPanel = createUserInfoPanel();
        
        // Right panel - Employee Info
        JPanel employeeInfoPanel = createEmployeeInfoPanel();
        
        contentPanel.add(userInfoPanel);
        contentPanel.add(employeeInfoPanel);
        
        mainContainer.add(titlePanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        // Wrap in scroll pane
        JScrollPane mainScrollPane = new JScrollPane(mainContainer);
        mainScrollPane.setBorder(null);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(mainScrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Main title section
        JPanel titleSection = new JPanel(new BorderLayout());
        titleSection.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản Lý Tài Khoản Nhân Viên", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY);
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Cập nhật thông tin cá nhân và tài khoản của bạn", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        titleSection.add(titleLabel, BorderLayout.CENTER);
        titleSection.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Fixed refresh button panel
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshPanel.setOpaque(false);
        
        btnRefresh = createBootstrapButton("Làm Mới Dữ Liệu", PRIMARY);
        refreshPanel.add(btnRefresh);
        
        panel.add(titleSection, BorderLayout.CENTER);
        panel.add(refreshPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = createBootstrapCard();
        
        // Header
        JPanel headerPanel = createSectionHeader("Thông Tin Tài Khoản", PRIMARY);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createBootstrapLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtUsername = createBootstrapTextField();
        formPanel.add(txtUsername, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmail = createBootstrapTextField();
        formPanel.add(txtEmail, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Vai trò:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbRole = createBootstrapComboBox(new String[]{"Manager", "Warehouse", "Sales"});
        cbRole.setEnabled(false);
        formPanel.add(cbRole, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbStatus = createBootstrapComboBox(new String[]{"Active", "Inactive"});
        cbStatus.setEnabled(false);
        formPanel.add(cbStatus, gbc);
        
        // Created At
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Ngày tạo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblCreatedAt = createInfoLabel();
        formPanel.add(lblCreatedAt, gbc);
        
        // Password change section
        JPanel passwordPanel = createPasswordChangePanel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(25, 0, 0, 0);
        formPanel.add(passwordPanel, gbc);
        
        // Account buttons panel
        JPanel accountButtonsPanel = createAccountButtonsPanel();
        
        panel.setLayout(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(accountButtonsPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createPasswordChangePanel() {
        JPanel panel = createBootstrapCard();
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING, 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        JPanel headerPanel = createSectionHeader("Đổi Mật Khẩu", WARNING);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createBootstrapLabel("Mật khẩu hiện tại:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCurrentPassword = createBootstrapPasswordField();
        formPanel.add(txtCurrentPassword, gbc);
        
        // New Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNewPassword = createBootstrapPasswordField();
        formPanel.add(txtNewPassword, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtConfirmPassword = createBootstrapPasswordField();
        formPanel.add(txtConfirmPassword, gbc);
        
        // Change Password Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(15, 0, 0, 0);
        btnChangePassword = createBootstrapButton("Đổi Mật Khẩu", WARNING);
        formPanel.add(btnChangePassword, gbc);
        
        panel.setLayout(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createEmployeeInfoPanel() {
        JPanel panel = createBootstrapCard();
        
        // Header
        JPanel headerPanel = createSectionHeader("Thông Tin Nhân Viên", SUCCESS);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createBootstrapLabel("Họ:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFirstName = createBootstrapTextField();
        formPanel.add(txtFirstName, gbc);
        
        // Last Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Tên:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtLastName = createBootstrapTextField();
        formPanel.add(txtLastName, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPhone = createBootstrapTextField();
        formPanel.add(txtPhone, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createBootstrapLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtAddress = createBootstrapTextArea();
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        scrollAddress.setPreferredSize(new Dimension(250, 80));
        formPanel.add(scrollAddress, gbc);
        
        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createBootstrapLabel("Liên hệ khẩn cấp:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmergencyContact = createBootstrapTextField();
        formPanel.add(txtEmergencyContact, gbc);
        
        // Emergency Phone
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createBootstrapLabel("SĐT khẩn cấp:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmergencyPhone = createBootstrapTextField();
        formPanel.add(txtEmergencyPhone, gbc);
        
        // Birth Date (read-only)
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(createBootstrapLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblBirthDate = createInfoLabel();
        formPanel.add(lblBirthDate, gbc);
        
        // Hire Date (read-only)
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(createBootstrapLabel("Ngày tuyển dụng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblHireDate = createInfoLabel();
        formPanel.add(lblHireDate, gbc);
        
        // Employee Role (read-only)
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(createBootstrapLabel("Chức vụ:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblEmployeeRole = createInfoLabel();
        formPanel.add(lblEmployeeRole, gbc);
        
        // Employee buttons panel
        JPanel employeeButtonsPanel = createEmployeeButtonsPanel();
        
        panel.setLayout(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(employeeButtonsPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createAccountButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Initialize account buttons
        btnUpdateAccount = createBootstrapButton("Cập Nhật Tài Khoản", INFO);
        btnSaveAccount = createBootstrapButton("Lưu Tài Khoản", SUCCESS);
        btnCancelAccount = createBootstrapButton("Hủy", DANGER);
        
        // Initially only show update button
        btnSaveAccount.setVisible(false);
        btnCancelAccount.setVisible(false);
        
        panel.add(btnUpdateAccount);
        panel.add(btnSaveAccount);
        panel.add(btnCancelAccount);
        
        return panel;
    }
    
    private JPanel createEmployeeButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Initialize employee buttons
        btnUpdateEmployee = createBootstrapButton("Cập Nhật Nhân Viên", INFO);
        btnSaveEmployee = createBootstrapButton("Lưu Nhân Viên", SUCCESS);
        btnCancelEmployee = createBootstrapButton("Hủy", DANGER);
        
        // Initially only show update button
        btnSaveEmployee.setVisible(false);
        btnCancelEmployee.setVisible(false);
        
        panel.add(btnUpdateEmployee);
        panel.add(btnSaveEmployee);
        panel.add(btnCancelEmployee);
        
        return panel;
    }
    
    private JPanel createSectionHeader(String text, Color color) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(WHITE);
        
        panel.add(label);
        return panel;
    }
    
    private JLabel createBootstrapLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK);
        return label;
    }
    
    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(SECONDARY);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        label.setOpaque(true);
        label.setBackground(LIGHT);
        return label;
    }
    
    private JTextField createBootstrapTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(280, 40));
        field.setBackground(WHITE);
        return field;
    }
    
    private JPasswordField createBootstrapPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(250, 40));
        field.setBackground(WHITE);
        return field;
    }
    
    private JTextArea createBootstrapTextArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        area.setBackground(WHITE);
        return area;
    }
    
    private JButton createBootstrapButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = color.darker();
                } else if (getModel().isRollover()) {
                    bgColor = color.brighter();
                } else {
                    bgColor = color;
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JComboBox<String> createBootstrapComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        combo.setPreferredSize(new Dimension(280, 40));
        combo.setBackground(WHITE);
        return combo;
    }
    
    private JPanel createBootstrapCard() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        return panel;
    }
    
    private void setupEventHandlers() {
        btnUpdateAccount.addActionListener(e -> toggleAccountEdit(true));
        btnSaveAccount.addActionListener(e -> saveAccountChanges());
        btnCancelAccount.addActionListener(e -> cancelAccountChanges());
        
        btnUpdateEmployee.addActionListener(e -> toggleEmployeeEdit(true));
        btnSaveEmployee.addActionListener(e -> saveEmployeeChanges());
        btnCancelEmployee.addActionListener(e -> cancelEmployeeChanges());
        
        btnChangePassword.addActionListener(e -> changePassword());
        btnRefresh.addActionListener(e -> loadUserData());
    }
    
    private void toggleAccountEdit(boolean editing) {
        isEditingAccount = editing;
        txtUsername.setEnabled(editing);
        txtEmail.setEnabled(editing);
        
        btnUpdateAccount.setVisible(!editing);
        btnSaveAccount.setVisible(editing);
        btnCancelAccount.setVisible(editing);
        
        if (editing) {
            // Backup original data
            originalUser = new User();
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser != null) {
                originalUser.setUsername(currentUser.getUsername());
                originalUser.setEmail(currentUser.getEmail());
                originalUser.setUsersId(currentUser.getUsersId());
                originalUser.setRole(currentUser.getRole());
                originalUser.setStatus(currentUser.getStatus());
                originalUser.setCreatedAt(currentUser.getCreatedAt());
            }
        }
        
        // Update panel
        revalidate();
        repaint();
    }
    
    private void toggleEmployeeEdit(boolean editing) {
        isEditingEmployee = editing;
        txtFirstName.setEnabled(editing);
        txtLastName.setEnabled(editing);
        txtPhone.setEnabled(editing);
        txtAddress.setEnabled(editing);
        txtEmergencyContact.setEnabled(editing);
        txtEmergencyPhone.setEnabled(editing);
        
        btnUpdateEmployee.setVisible(!editing);
        btnSaveEmployee.setVisible(editing);
        btnCancelEmployee.setVisible(editing);
        
        if (editing) {
            // Backup original data
            try {
                User currentUser = SessionManager.getInstance().getLoggedInUser();
                if (currentUser != null) {
                    originalEmployee = employeeController.getEmployeeByUserId(currentUser.getUsersId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Update panel
        revalidate();
        repaint();
    }
    
    private void saveAccountChanges() {
        try {
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng đăng nhập.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate input
            String username = txtUsername.getText().trim();
            String email = txtEmail.getText().trim();
            
            if (username.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Update user info
            currentUser.setUsername(username);
            currentUser.setEmail(email);
            
            boolean userUpdated = userController.updateUser(currentUser);
            
            if (userUpdated) {
                SessionManager.getInstance().setLoggedInUser(currentUser);
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin tài khoản thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                toggleAccountEdit(false);
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật thông tin tài khoản.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelAccountChanges() {
        if (originalUser != null) {
            populateUserFields(originalUser);
        }
        toggleAccountEdit(false);
    }
    
    private void saveEmployeeChanges() {
        try {
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng đăng nhập.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Employee employee = employeeController.getEmployeeByUserId(currentUser.getUsersId());
            if (employee != null) {
                // Validate input
                String firstName = txtFirstName.getText().trim();
                String lastName = txtLastName.getText().trim();
                String phone = txtPhone.getText().trim();
                String address = txtAddress.getText().trim();
                
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng điền họ tên đầy đủ.", 
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setPhone(phone);
                employee.setAddress(address);
                employee.setEmergencyContact(txtEmergencyContact.getText().trim());
                employee.setEmergencyPhone(txtEmergencyPhone.getText().trim());
                
                boolean employeeUpdated = employeeController.updateEmployee(employee);
                
                if (employeeUpdated) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    toggleEmployeeEdit(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật thông tin nhân viên.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin nhân viên: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelEmployeeChanges() {
        if (originalEmployee != null) {
            populateEmployeeFields(originalEmployee);
        }
        toggleEmployeeEdit(false);
    }
    
    private void loadUserData() {
        try {
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng đăng nhập.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Load user data
            populateUserFields(currentUser);
            
            // Load employee data
            Employee employee = employeeController.getEmployeeByUserId(currentUser.getUsersId());
            if (employee != null) {
                populateEmployeeFields(employee);
            } else {
                clearEmployeeFields();
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên liên kết với tài khoản này.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Reset edit states
            toggleAccountEdit(false);
            toggleEmployeeEdit(false);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateUserFields(User user) {
        txtUsername.setText(user.getUsername());
        txtEmail.setText(user.getEmail());
        cbRole.setSelectedItem(user.getRole());
        cbStatus.setSelectedItem(user.getStatus());
        
        if (user.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            lblCreatedAt.setText(sdf.format(user.getCreatedAt()));
        }
    }
    
    private void populateEmployeeFields(Employee employee) {
        txtFirstName.setText(employee.getFirstName());
        txtLastName.setText(employee.getLastName());
        txtPhone.setText(employee.getPhone());
        txtAddress.setText(employee.getAddress());
        txtEmergencyContact.setText(employee.getEmergencyContact());
        txtEmergencyPhone.setText(employee.getEmergencyPhone());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (employee.getBirthDate() != null) {
            lblBirthDate.setText(sdf.format(employee.getBirthDate()));
        }
        if (employee.getHireDate() != null) {
            lblHireDate.setText(sdf.format(employee.getHireDate()));
        }
        
        lblEmployeeRole.setText(employee.getRole());
    }
    
    private void clearEmployeeFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtEmergencyContact.setText("");
        txtEmergencyPhone.setText("");
        lblBirthDate.setText("");
        lblHireDate.setText("");
        lblEmployeeRole.setText("");
    }
    
    private void changePassword() {
        try {
            String currentPassword = new String(txtCurrentPassword.getPassword());
            String newPassword = new String(txtNewPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin mật khẩu.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận mật khẩu không khớp.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng đăng nhập.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = userController.resetPassword(currentUser.getEmail(), currentPassword, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear password fields
                txtCurrentPassword.setText("");
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng hoặc có lỗi xảy ra.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi đổi mật khẩu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
