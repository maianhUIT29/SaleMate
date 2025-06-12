package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.salesmate.controller.EmployeeController;
import com.salesmate.model.Employee;
import com.toedter.calendar.JDateChooser;

/**
 * Class chứa các dialog hiện đại cho form quản lý người dùng
 */
public class UserDialogs {
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    /**
     * Hiển thị dialog thêm người dùng với giao diện hiện đại
     * @param parent Component cha
     * @param employeeController Controller quản lý nhân viên
     * @return true nếu thêm thành công
     */
    public static boolean showModernAddDialog(JPanel parent, EmployeeController employeeController) {
        // Tạo dialog modal với giao diện hiện đại
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Thêm nhân viên", true);
        dialog.setLayout(new BorderLayout());
        
        // Panel tiêu đề với màu nổi bật
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Thêm nhân viên mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Form panel với GridBagLayout và màu nền đẹp
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Style cho label và input
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        
        // 1. Họ
        JTextField fnField = createStyledField(20);
        fnField.setFont(inputFont);
        
        JLabel fnLabel = new JLabel("Họ:");
        fnLabel.setFont(labelFont);
        fnLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(fnLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(fnField, gbc);
        
        // 2. Tên
        JTextField lnField = createStyledField(20);
        lnField.setFont(inputFont);
        
        JLabel lnLabel = new JLabel("Tên:");
        lnLabel.setFont(labelFont);
        lnLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lnLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(lnField, gbc);
        
        // 3. Ngày sinh
        JDateChooser bdField = createStyledDateChooser(null);
        bdField.setFont(inputFont);
        
        JLabel bdLabel = new JLabel("Ngày sinh:");
        bdLabel.setFont(labelFont);
        bdLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(bdLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(bdField, gbc);
        
        // 4. Ngày vào làm
        JDateChooser hdField = createStyledDateChooser(null);
        hdField.setFont(inputFont);
        
        JLabel hdLabel = new JLabel("Ngày vào làm:");
        hdLabel.setFont(labelFont);
        hdLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(hdLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(hdField, gbc);
        
        // 5. SĐT
        JTextField phoneField = createStyledField(20);
        phoneField.setFont(inputFont);
        
        JLabel phoneLabel = new JLabel("SĐT:");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(phoneField, gbc);
        
        // 6. Địa chỉ
        JTextField addrField = createStyledField(20);
        addrField.setFont(inputFont);
        
        JLabel addrLabel = new JLabel("Địa chỉ:");
        addrLabel.setFont(labelFont);
        addrLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formPanel.add(addrLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(addrField, gbc);
        
        // 7. Người liên hệ
        JTextField ecField = createStyledField(20);
        ecField.setFont(inputFont);
        
        JLabel ecLabel = new JLabel("Người liên hệ:");
        ecLabel.setFont(labelFont);
        ecLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formPanel.add(ecLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(ecField, gbc);
        
        // 8. SĐT liên hệ
        JTextField epField = createStyledField(20);
        epField.setFont(inputFont);
        
        JLabel epLabel = new JLabel("SĐT liên hệ:");
        epLabel.setFont(labelFont);
        epLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        formPanel.add(epLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(epField, gbc);
        
        // 9. Chức vụ
        JComboBox<String> roleField = createStyledComboBox(new String[]{"Warehouse", "Sales", "Manager"});
        roleField.setFont(inputFont);
        
        JLabel roleLabel = new JLabel("Chức vụ:");
        roleLabel.setFont(labelFont);
        roleLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0.3;
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(roleField, gbc);
        
        // Panel nút điều khiển
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        btnPanel.setBackground(Color.WHITE);
        
        JButton saveBtn = createStyledButton("Lưu", PRIMARY_COLOR);
        JButton cancelBtn = createStyledButton("Hủy", new Color(108, 117, 125));
        
        // Biến theo dõi kết quả thêm
        final boolean[] result = {false};
        
        // Sự kiện lưu dữ liệu
        saveBtn.addActionListener(e -> {
            try {
                Employee emp = new Employee();
                emp.setFirstName(fnField.getText());
                emp.setLastName(lnField.getText());
                emp.setBirthDate(bdField.getDate());
                emp.setHireDate(hdField.getDate());
                emp.setPhone(phoneField.getText());
                emp.setAddress(addrField.getText());
                emp.setEmergencyContact(ecField.getText());
                emp.setEmergencyPhone(epField.getText());
                emp.setRole((String) roleField.getSelectedItem());
                
                if (employeeController.addEmployee(emp)) {
                    JOptionPane.showMessageDialog(dialog, "Thêm thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    result[0] = true;
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi thêm nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Kiểm tra lại dữ liệu nhập vào", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        // Hiệu ứng hover cho các nút
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                saveBtn.setBackground(PRIMARY_COLOR.darker()); 
            }
            
            @Override
            public void mouseExited(MouseEvent e) { 
                saveBtn.setBackground(PRIMARY_COLOR); 
            }
        });
        
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                cancelBtn.setBackground(new Color(90, 90, 90)); 
            }
            
            @Override
            public void mouseExited(MouseEvent e) { 
                cancelBtn.setBackground(new Color(108, 117, 125)); 
            }
        });
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        
        // Hoàn thiện dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(new Dimension(450, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    /**
     * Hiển thị dialog chỉnh sửa nhân viên với giao diện hiện đại
     * @param parent Component cha
     * @param employeeController Controller quản lý nhân viên
     * @param empId ID của nhân viên cần chỉnh sửa
     * @return true nếu cập nhật thành công
     */
    public static boolean showModernEditDialog(JPanel parent, EmployeeController employeeController, int empId) {
        // Lấy thông tin nhân viên
        Employee emp = employeeController.getEmployeeById(empId);
        if (emp == null) {
            JOptionPane.showMessageDialog(parent, "Không tìm thấy thông tin nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Tạo dialog modal với giao diện hiện đại
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Cập nhật thông tin nhân viên", true);
        dialog.setLayout(new BorderLayout());
        
        // Panel tiêu đề với màu nổi bật
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Cập nhật thông tin nhân viên");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Form panel với GridBagLayout và màu nền đẹp
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Style cho label và input
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        
        // 1. Họ
        JTextField fnField = createStyledField(20);
        fnField.setText(emp.getFirstName());
        fnField.setFont(inputFont);
        
        JLabel fnLabel = new JLabel("Họ:");
        fnLabel.setFont(labelFont);
        fnLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(fnLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(fnField, gbc);
        
        // 2. Tên
        JTextField lnField = createStyledField(20);
        lnField.setText(emp.getLastName());
        lnField.setFont(inputFont);
        
        JLabel lnLabel = new JLabel("Tên:");
        lnLabel.setFont(labelFont);
        lnLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lnLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(lnField, gbc);
        
        // 3. Ngày sinh
        JDateChooser bdField = createStyledDateChooser(emp.getBirthDate());
        bdField.setFont(inputFont);
        
        JLabel bdLabel = new JLabel("Ngày sinh:");
        bdLabel.setFont(labelFont);
        bdLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(bdLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(bdField, gbc);
        
        // 4. Ngày vào làm
        JDateChooser hdField = createStyledDateChooser(emp.getHireDate());
        hdField.setFont(inputFont);
        
        JLabel hdLabel = new JLabel("Ngày vào làm:");
        hdLabel.setFont(labelFont);
        hdLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(hdLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(hdField, gbc);
        
        // 5. SĐT
        JTextField phoneField = createStyledField(20);
        phoneField.setText(emp.getPhone());
        phoneField.setFont(inputFont);
        
        JLabel phoneLabel = new JLabel("SĐT:");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(phoneField, gbc);
        
        // 6. Địa chỉ
        JTextField addrField = createStyledField(20);
        addrField.setText(emp.getAddress());
        addrField.setFont(inputFont);
        
        JLabel addrLabel = new JLabel("Địa chỉ:");
        addrLabel.setFont(labelFont);
        addrLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formPanel.add(addrLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(addrField, gbc);
        
        // 7. Người liên hệ
        JTextField ecField = createStyledField(20);
        ecField.setText(emp.getEmergencyContact());
        ecField.setFont(inputFont);
        
        JLabel ecLabel = new JLabel("Người liên hệ:");
        ecLabel.setFont(labelFont);
        ecLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        formPanel.add(ecLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(ecField, gbc);
        
        // 8. SĐT liên hệ
        JTextField epField = createStyledField(20);
        epField.setText(emp.getEmergencyPhone());
        epField.setFont(inputFont);
        
        JLabel epLabel = new JLabel("SĐT liên hệ:");
        epLabel.setFont(labelFont);
        epLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        formPanel.add(epLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(epField, gbc);
        
        // 9. Chức vụ
        JComboBox<String> roleField = createStyledComboBox(new String[]{"Warehouse", "Sales", "Manager"});
        roleField.setSelectedItem(emp.getRole());
        roleField.setFont(inputFont);
        
        JLabel roleLabel = new JLabel("Chức vụ:");
        roleLabel.setFont(labelFont);
        roleLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0.3;
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(roleField, gbc);
        
        // Panel nút điều khiển
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        btnPanel.setBackground(Color.WHITE);
        
        JButton saveBtn = createStyledButton("Lưu", PRIMARY_COLOR);
        JButton cancelBtn = createStyledButton("Hủy", new Color(108, 117, 125));
        
        // Biến theo dõi kết quả cập nhật
        final boolean[] result = {false};
        
        // Sự kiện lưu dữ liệu
        saveBtn.addActionListener(e -> {
            try {
                emp.setFirstName(fnField.getText());
                emp.setLastName(lnField.getText());
                emp.setBirthDate(bdField.getDate());
                emp.setHireDate(hdField.getDate());
                emp.setPhone(phoneField.getText());
                emp.setAddress(addrField.getText());
                emp.setEmergencyContact(ecField.getText());
                emp.setEmergencyPhone(epField.getText());
                emp.setRole((String) roleField.getSelectedItem());
                
                if (employeeController.updateEmployee(emp)) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    result[0] = true;
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Kiểm tra lại dữ liệu nhập vào", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        // Hiệu ứng hover cho các nút
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                saveBtn.setBackground(PRIMARY_COLOR.darker()); 
            }
            
            @Override
            public void mouseExited(MouseEvent e) { 
                saveBtn.setBackground(PRIMARY_COLOR); 
            }
        });
        
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                cancelBtn.setBackground(new Color(90, 90, 90)); 
            }
            
            @Override
            public void mouseExited(MouseEvent e) { 
                cancelBtn.setBackground(new Color(108, 117, 125)); 
            }
        });
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        
        // Hoàn thiện dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(new Dimension(450, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    /**
     * Tạo JTextField có style hiện đại với viền và bo góc
     */
    private static JTextField createStyledField(int columns) {
        JTextField field = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof javax.swing.plaf.UIResource) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        // Hiệu ứng focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * Tạo ComboBox có style hiện đại
     */
    private static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        combo.setBackground(Color.WHITE);
        return combo;
    }
    
    /**
     * Tạo JDateChooser với style hiện đại
     */
    private static JDateChooser createStyledDateChooser(java.util.Date date) {
        JDateChooser dateChooser = new JDateChooser(date);
        dateChooser.setDateFormatString("yyyy-MM-dd");
        
        // Cài đặt style cho phần text field bên trong JDateChooser
        JTextField textField = (JTextField) dateChooser.getDateEditor().getUiComponent();
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        return dateChooser;
    }
    
    /**
     * Tạo JButton với style hiện đại
     */
    private static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
}
