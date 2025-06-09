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
    
    // Modern vibrant color scheme
    private static final Color PRIMARY_COLOR = new Color(102, 51, 153);       // Deep Purple
    private static final Color SECONDARY_COLOR = new Color(0, 123, 255);      // Bright Blue
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);        // Green
    private static final Color WARNING_COLOR = new Color(255, 193, 7);        // Amber
    private static final Color DANGER_COLOR = new Color(220, 53, 69);         // Red
    private static final Color INFO_COLOR = new Color(23, 162, 184);          // Cyan
    private static final Color LIGHT_COLOR = new Color(248, 249, 250);        // Light Gray
    private static final Color DARK_COLOR = new Color(52, 58, 64);            // Dark Gray
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 247);   // Light Blue Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color ACCENT_COLOR = new Color(255, 87, 34);         // Deep Orange
    private static final Color PURPLE_LIGHT = new Color(155, 89, 182);        // Light Purple
    
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
        setBackground(BACKGROUND_COLOR);
        
        // Main container with gradient background
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 242, 247), 
                                                   getWidth(), getHeight(), new Color(225, 235, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Title with modern gradient styling
        JPanel titlePanel = createTitlePanel();
        
        // Content panel with modern card design
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        contentPanel.setOpaque(false);
        
        // Left panel - User Account Info
        JPanel userPanel = createUserInfoPanel();
        
        // Right panel - Employee Info
        JPanel employeePanel = createEmployeeInfoPanel();
        
        contentPanel.add(userPanel);
        contentPanel.add(employeePanel);
        
        // Bottom panel with refresh button
        JPanel bottomPanel = createBottomPanel();
        
        mainContainer.add(titlePanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Quản Lý Tài Khoản Nhân Viên", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Cập nhật thông tin cá nhân và tài khoản của bạn", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        panel.setLayout(new BorderLayout());
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = createModernCard(SECONDARY_COLOR);
        
        // Header with gradient background
        JPanel headerPanel = createSectionHeader("👤 Thông Tin Tài Khoản", SECONDARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createModernLabel("🏷️ Tên đăng nhập:", SECONDARY_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtUsername = createModernTextField(SECONDARY_COLOR);
        formPanel.add(txtUsername, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("📧 Email:", SECONDARY_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmail = createModernTextField(SECONDARY_COLOR);
        formPanel.add(txtEmail, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("🎭 Vai trò:", SECONDARY_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbRole = createModernComboBox(new String[]{"Manager", "Warehouse", "Sales"}, SECONDARY_COLOR);
        cbRole.setEnabled(false);
        formPanel.add(cbRole, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("📊 Trạng thái:", SECONDARY_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbStatus = createModernComboBox(new String[]{"Active", "Inactive"}, SUCCESS_COLOR);
        cbStatus.setEnabled(false);
        formPanel.add(cbStatus, gbc);
        
        // Created At
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("📅 Ngày tạo:", SECONDARY_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblCreatedAt = createInfoLabel();
        formPanel.add(lblCreatedAt, gbc);
        
        // Password change section
        JPanel passwordPanel = createPasswordChangePanel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(25, 0, 0, 0);
        formPanel.add(passwordPanel, gbc);
        
        // Account buttons
        JPanel accountButtonsPanel = createAccountButtonsPanel();
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(accountButtonsPanel, gbc);
        
        panel.setLayout(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createPasswordChangePanel() {
        JPanel panel = createModernCard(WARNING_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING_COLOR, 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        JPanel headerPanel = createSectionHeader("🔐 Đổi Mật Khẩu", WARNING_COLOR);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createModernLabel("🔑 Mật khẩu hiện tại:", WARNING_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCurrentPassword = createModernPasswordField(WARNING_COLOR);
        formPanel.add(txtCurrentPassword, gbc);
        
        // New Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("🆕 Mật khẩu mới:", WARNING_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNewPassword = createModernPasswordField(WARNING_COLOR);
        formPanel.add(txtNewPassword, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("✅ Xác nhận mật khẩu:", WARNING_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtConfirmPassword = createModernPasswordField(WARNING_COLOR);
        formPanel.add(txtConfirmPassword, gbc);
        
        // Change password button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 0, 0);
        btnChangePassword = createModernButton("🔐 Đổi Mật Khẩu", WARNING_COLOR);
        formPanel.add(btnChangePassword, gbc);
        
        panel.setLayout(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createEmployeeInfoPanel() {
        JPanel panel = createModernCard(SUCCESS_COLOR);
        
        // Header with gradient background
        JPanel headerPanel = createSectionHeader("👨‍💼 Thông Tin Nhân Viên", SUCCESS_COLOR);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createModernLabel("👤 Họ:", SUCCESS_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFirstName = createModernTextField(SUCCESS_COLOR);
        formPanel.add(txtFirstName, gbc);
        
        // Last Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("📝 Tên:", SUCCESS_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtLastName = createModernTextField(SUCCESS_COLOR);
        formPanel.add(txtLastName, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("📱 Số điện thoại:", SUCCESS_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPhone = createModernTextField(SUCCESS_COLOR);
        formPanel.add(txtPhone, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createModernLabel("🏠 Địa chỉ:", SUCCESS_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtAddress = createModernTextArea(SUCCESS_COLOR);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        scrollAddress.setPreferredSize(new Dimension(250, 80));
        scrollAddress.setBorder(BorderFactory.createLineBorder(SUCCESS_COLOR, 2));
        formPanel.add(scrollAddress, gbc);
        
        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createModernLabel("🚨 Liên hệ khẩn cấp:", ACCENT_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmergencyContact = createModernTextField(ACCENT_COLOR);
        formPanel.add(txtEmergencyContact, gbc);
        
        // Emergency Phone
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("☎️ SĐT khẩn cấp:", ACCENT_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmergencyPhone = createModernTextField(ACCENT_COLOR);
        formPanel.add(txtEmergencyPhone, gbc);
        
        // Birth Date (read-only)
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("🎂 Ngày sinh:", INFO_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblBirthDate = createInfoLabel();
        formPanel.add(lblBirthDate, gbc);
        
        // Hire Date (read-only)
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("🗓️ Ngày tuyển dụng:", INFO_COLOR), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblHireDate = createInfoLabel();
        formPanel.add(lblHireDate, gbc);
        
        // Employee Role (read-only)
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createModernLabel("💼 Chức vụ:", PURPLE_LIGHT), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblEmployeeRole = createInfoLabel();
        formPanel.add(lblEmployeeRole, gbc);
        
        // Employee buttons
        JPanel employeeButtonsPanel = createEmployeeButtonsPanel();
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(employeeButtonsPanel, gbc);
        
        panel.setLayout(new BorderLayout());
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAccountButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        
        btnUpdateAccount = createModernButton("✏️ Cập Nhật", INFO_COLOR);
        btnSaveAccount = createModernButton("💾 Lưu", SUCCESS_COLOR);
        btnCancelAccount = createModernButton("❌ Hủy", DANGER_COLOR);
        
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
        panel.setOpaque(false);
        
        btnUpdateEmployee = createModernButton("✏️ Cập Nhật", INFO_COLOR);
        btnSaveEmployee = createModernButton("💾 Lưu", SUCCESS_COLOR);
        btnCancelEmployee = createModernButton("❌ Hủy", DANGER_COLOR);
        
        // Initially only show update button
        btnSaveEmployee.setVisible(false);
        btnCancelEmployee.setVisible(false);
        
        panel.add(btnUpdateEmployee);
        panel.add(btnSaveEmployee);
        panel.add(btnCancelEmployee);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panel.setOpaque(false);
        
        btnRefresh = createModernButton("🔄 Làm Mới Dữ Liệu", PRIMARY_COLOR);
        btnRefresh.setPreferredSize(new Dimension(200, 50));
        
        panel.add(btnRefresh);
        return panel;
    }
    
    // Helper methods for creating modern UI components
    private JPanel createModernCard(Color borderColor) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 3),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Add shadow effect
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedSoftBevelBorder(),
            BorderFactory.createLineBorder(borderColor, 2)
        ));
        
        return panel;
    }
    
    private JPanel createSectionHeader(String text, Color color) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, color, getWidth(), getHeight(), color.brighter());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        
        panel.add(label);
        return panel;
    }
    
    private JLabel createModernLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }
    
    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_SECONDARY);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        label.setOpaque(true);
        label.setBackground(LIGHT_COLOR);
        return label;
    }
    
    private JTextField createModernTextField(Color borderColor) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(280, 40));
        field.setBackground(Color.WHITE);
        return field;
    }
    
    private JPasswordField createModernPasswordField(Color borderColor) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(250, 40));
        field.setBackground(Color.WHITE);
        return field;
    }
    
    private JTextArea createModernTextArea(Color borderColor) {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        area.setBackground(Color.WHITE);
        return area;
    }
    
    private JComboBox<String> createModernComboBox(String[] items, Color borderColor) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        combo.setPreferredSize(new Dimension(280, 40));
        combo.setBackground(Color.WHITE);
        return combo;
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color1, color2;
                if (getModel().isPressed()) {
                    color1 = bgColor.darker();
                    color2 = bgColor.darker().darker();
                } else if (getModel().isRollover()) {
                    color1 = bgColor.brighter();
                    color2 = bgColor;
                } else {
                    color1 = bgColor;
                    color2 = bgColor.darker();
                }
                
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Add inner shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });
        
        return button;
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
            originalUser = SessionManager.getInstance().getLoggedInUser();
        }
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
                originalEmployee = employeeController.getEmployeeByUserId(originalUser.getUsersId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void saveAccountChanges() {
        try {
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng đăng nhập.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update user info
            currentUser.setUsername(txtUsername.getText().trim());
            currentUser.setEmail(txtEmail.getText().trim());
            
            boolean userUpdated = userController.updateUser(currentUser);
            
            if (userUpdated) {
                SessionManager.getInstance().setLoggedInUser(currentUser);
                JOptionPane.showMessageDialog(this, "✅ Cập nhật thông tin tài khoản thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                toggleAccountEdit(false);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra khi cập nhật thông tin tài khoản.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi cập nhật thông tin: " + e.getMessage(), 
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
                employee.setFirstName(txtFirstName.getText().trim());
                employee.setLastName(txtLastName.getText().trim());
                employee.setPhone(txtPhone.getText().trim());
                employee.setAddress(txtAddress.getText().trim());
                employee.setEmergencyContact(txtEmergencyContact.getText().trim());
                employee.setEmergencyPhone(txtEmergencyPhone.getText().trim());
                
                boolean employeeUpdated = employeeController.updateEmployee(employee);
                
                if (employeeUpdated) {
                    JOptionPane.showMessageDialog(this, "✅ Cập nhật thông tin nhân viên thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    toggleEmployeeEdit(false);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra khi cập nhật thông tin nhân viên.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi cập nhật thông tin nhân viên: " + e.getMessage(), 
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
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng điền đầy đủ thông tin mật khẩu.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "⚠️ Mật khẩu mới và xác nhận mật khẩu không khớp.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "⚠️ Mật khẩu mới phải có ít nhất 6 ký tự.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User currentUser = SessionManager.getInstance().getLoggedInUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "❌ Không tìm thấy thông tin người dùng đăng nhập.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = userController.resetPassword(currentUser.getEmail(), currentPassword, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "🎉 Đổi mật khẩu thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear password fields
                txtCurrentPassword.setText("");
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Mật khẩu hiện tại không đúng hoặc có lỗi xảy ra.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi đổi mật khẩu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
