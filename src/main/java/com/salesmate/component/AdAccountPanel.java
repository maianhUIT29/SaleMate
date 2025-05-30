package com.salesmate.component;

import com.salesmate.controller.UserController;
import com.salesmate.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdAccountPanel extends JPanel {
    private final UserController userController;
    private final DefaultTableModel tableModel;
    private final JTable userTable;
    private final JTextField searchField;
    private final JComboBox<String> roleFilter;
    private final JButton addButton, editButton, deleteButton, refreshButton;
    private final JSpinner pageSpinner;
    private final JLabel totalPagesLabel;
    private int currentPage = 1;
    private int totalPages = 1;
    private final int pageSize = 20;

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_TEXT = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);

    public AdAccountPanel() {
        userController = new UserController();

        // Header
        JLabel titleLabel = new JLabel("Quản lý tài khoản");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Buttons
        addButton = createStyledButton("Thêm", PRIMARY_COLOR);
        editButton = createStyledButton("Sửa", SECONDARY_COLOR);
        deleteButton = createStyledButton("Kích hoạt/Ngừng", ACCENT_COLOR);
        refreshButton = createStyledButton("Làm mới", new Color(52, 73, 94));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Search and filter
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        roleFilter = new JComboBox<>();
        roleFilter.addItem("Tất cả");
        roleFilter.addItem("Manager");
        roleFilter.addItem("Warehouse");
        roleFilter.addItem("Sales");
        roleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Vai trò:"));
        filterPanel.add(roleFilter);

        JPanel topContainer = new JPanel(new BorderLayout(10, 0));
        topContainer.setBackground(BACKGROUND_COLOR);
        topContainer.setBorder(new EmptyBorder(0, 0, 15, 0));
        topContainer.add(buttonPanel, BorderLayout.WEST);
        topContainer.add(filterPanel, BorderLayout.EAST);

        // Table setup
        String[] cols = {"ID", "Tên đăng nhập", "Email", "Vai trò", "Trạng thái", "Ngày tạo"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowSorter(new TableRowSorter<>(tableModel));
        styleHeader(userTable);
        styleRows(userTable);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Pagination controls
        JButton prevButton = createStyledButton("« Trước", SECONDARY_COLOR);
        JButton nextButton = createStyledButton("Tiếp »", SECONDARY_COLOR);
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        totalPagesLabel = new JLabel("/ 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPagesLabel.setForeground(TEXT_COLOR);
        JLabel pageLabel = new JLabel("Trang:");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pageLabel.setForeground(TEXT_COLOR);

        JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pagingPanel.setBackground(BACKGROUND_COLOR);
        pagingPanel.add(prevButton);
        pagingPanel.add(pageLabel);
        pagingPanel.add(pageSpinner);
        pagingPanel.add(totalPagesLabel);
        pagingPanel.add(nextButton);

        // Combine header and topContainer
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BACKGROUND_COLOR);
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(topContainer, BorderLayout.SOUTH);

        // Layout
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(pagingPanel, BorderLayout.SOUTH);

        // Listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> toggleUserStatus());
        refreshButton.addActionListener(e -> loadUsers());

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { resetPageAndLoad(); }
            public void removeUpdate(DocumentEvent e) { resetPageAndLoad(); }
            public void changedUpdate(DocumentEvent e) { resetPageAndLoad(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        roleFilter.addActionListener(e -> resetPageAndLoad());
        prevButton.addActionListener(e -> { if (currentPage > 1) pageSpinner.setValue(--currentPage); });
        nextButton.addActionListener(e -> { if (currentPage < totalPages) pageSpinner.setValue(++currentPage); });
        pageSpinner.addChangeListener(e -> {
            currentPage = (int) pageSpinner.getValue();
            loadUsers();
        });

        // Initial load
        loadUsers();
    }

    private void resetPageAndLoad() {
        currentPage = 1;
        pageSpinner.setValue(1);
        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText().trim().toLowerCase();
        String role = (String) roleFilter.getSelectedItem();

        // Full filtered list
        List<User> filtered = userController.getAllUsers().stream()
                .filter(u -> {
                    String name = u.getUsername()!=null?u.getUsername().toLowerCase():"";
                    boolean matchKey = keyword.isEmpty() || name.contains(keyword);
                    boolean matchRole = role.equals("Tất cả") || u.getRole().equals(role);
                    return matchKey && matchRole;
                })
                .collect(Collectors.toList());

        // Calculate pagination
        int total = filtered.size();
        totalPages = Math.max((int) Math.ceil((double) total / pageSize), 1);
        totalPagesLabel.setText("/ " + totalPages);
        ((SpinnerNumberModel) pageSpinner.getModel()).setMaximum(totalPages);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<User> pageList = filtered.subList(start, end);

        for (User u : pageList) {
            tableModel.addRow(new Object[]{
                    u.getUsersId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
                    u.getStatus(),
                    u.getCreatedAt()
            });
        }
    }

    private void toggleUserStatus() {
        int r = userTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn người dùng");
            return;
        }
        int id = (int) tableModel.getValueAt(r, 0);
        User u = userController.getUserById(id);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy");
            return;
        }
        String newStatus = u.getStatus().equals("Active") ? "Inactive" : "Active";
        u.setStatus(newStatus);
        if (userController.updateUser(u)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thành " + newStatus);
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm người dùng", true);
        dlg.setLayout(new GridLayout(0,2,5,5));
        JTextField userField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Manager","Warehouse","Sales"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active","Inactive"});

        dlg.add(new JLabel("Tên đăng nhập:")); dlg.add(userField);
        dlg.add(new JLabel("Email:")); dlg.add(emailField);
        dlg.add(new JLabel("Mật khẩu:")); dlg.add(passField);
        dlg.add(new JLabel("Vai trò:")); dlg.add(roleBox);
        dlg.add(new JLabel("Trạng thái:")); dlg.add(statusBox);

        JButton save = new JButton("Lưu");
        save.addActionListener(evt -> {
            User u = new User();
            u.setUsername(userField.getText().trim());
            u.setEmail(emailField.getText().trim());
            u.setPassword(new String(passField.getPassword()));
            u.setRole((String) roleBox.getSelectedItem());
            u.setStatus((String) statusBox.getSelectedItem());
            if (userController.addUser(u)) {
                JOptionPane.showMessageDialog(dlg, "Thêm thành công");
                dlg.dispose(); resetPageAndLoad();
            } else {
                JOptionPane.showMessageDialog(dlg, "Lỗi thêm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.add(save);
        JButton cancel = new JButton("Hủy"); cancel.addActionListener(e -> dlg.dispose()); dlg.add(cancel);
        dlg.pack(); dlg.setLocationRelativeTo(this); dlg.setVisible(true);
    }

    private void showEditDialog() {
        int r = userTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn người dùng");
            return;
        }
        int id = (int) tableModel.getValueAt(r,0);
        User u = userController.getUserById(id);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy");
            return;
        }
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa người dùng", true);
        dlg.setLayout(new GridLayout(0,2,5,5));
        JTextField userField = new JTextField(u.getUsername());
        JTextField emailField = new JTextField(u.getEmail());
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Manager","Warehouse","Sales"}); roleBox.setSelectedItem(u.getRole());
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active","Inactive"}); statusBox.setSelectedItem(u.getStatus());

        dlg.add(new JLabel("Tên đăng nhập:")); dlg.add(userField);
        dlg.add(new JLabel("Email:")); dlg.add(emailField);
        dlg.add(new JLabel("Vai trò:")); dlg.add(roleBox);
        dlg.add(new JLabel("Trạng thái:")); dlg.add(statusBox);

        JButton saveBtn = new JButton("Lưu");
        saveBtn.addActionListener(evt -> {
            u.setUsername(userField.getText().trim());
            u.setEmail(emailField.getText().trim());
            u.setRole((String) roleBox.getSelectedItem());
            u.setStatus((String) statusBox.getSelectedItem());
            if (userController.updateUser(u)) {
                JOptionPane.showMessageDialog(dlg, "Cập nhật thành công");
                dlg.dispose(); resetPageAndLoad();
            } else {
                JOptionPane.showMessageDialog(dlg, "Lỗi cập nhật", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.add(saveBtn);
        JButton cancelBtn = new JButton("Hủy"); cancelBtn.addActionListener(e -> dlg.dispose()); dlg.add(cancelBtn);
        dlg.pack(); dlg.setLocationRelativeTo(this); dlg.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) g.setColor(bg.darker());
                else if (getModel().isRollover()) g.setColor(bg.brighter());
                else g.setColor(bg);
                g.fillRect(0,0,getWidth(),getHeight()); super.paintComponent(g);
            }
        };
        btn.setForeground(LIGHT_TEXT);
        btn.setFont(new Font("Segoe UI",Font.BOLD,13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleHeader(JTable table) {
        JTableHeader h = table.getTableHeader();
        h.setReorderingAllowed(false);
        h.setResizingAllowed(false);
        h.setPreferredSize(new Dimension(h.getWidth(),35));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBackground(PRIMARY_COLOR);
                lbl.setForeground(LIGHT_TEXT);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setOpaque(true);
                return lbl;
            }
        });
    }

    private void styleRows(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (!s) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                return comp;
            }
        });
    }
}
