package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import com.salesmate.controller.EmployeeController;
import com.salesmate.model.Employee;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExcelImporter;
import com.salesmate.utils.ExportDialog;
import com.toedter.calendar.JDateChooser;

public class AdUserPanel extends JPanel {
    private final EmployeeController employeeController;
    private final DefaultTableModel tableModel;
    private final JTable userTable;
    private JTextField searchField;
    private JSpinner pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
    private JLabel totalPagesLabel = new JLabel(" / 1");
    private int currentPage = 1;
    private final int pageSize = 20;
    private int totalPages = 1;
    private String currentSearch = "";

    // Enhanced modern color scheme
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);       // Deeper blue
    private static final Color SECONDARY_COLOR = new Color(66, 165, 245);     // Lighter blue
    private static final Color ACCENT_COLOR = new Color(239, 83, 80);         // Modern red
    private static final Color EXPORT_COLOR = new Color(126, 87, 194);        // Purple
    private static final Color IMPORT_COLOR = new Color(255, 167, 38);        // Orange
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);   // Light gray
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);    // White for cards
    private static final Color TEXT_COLOR = new Color(33, 33, 33);            // Near black
    private static final Color LIGHT_TEXT = new Color(255, 255, 255);         // White text
    private static final Color BORDER_COLOR = new Color(224, 224, 224);       // Light border
    private static final Color TABLE_ALTERNATE_COLOR = new Color(248, 249, 250); // Table alternating rows
    private static final Color TABLE_HEADER_BG = new Color(33, 97, 140);      // Table header

    public AdUserPanel() {
        this.employeeController = new EmployeeController();

        // Set up panel with card-like appearance
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create card panel with shadow effect
        JPanel cardPanel = createCardPanel();
        cardPanel.setLayout(new BorderLayout(0, 15));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with modern styling
        JLabel titleLabel = new JLabel("Quản lý nhân viên");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Buttons with enhanced styling
        JButton addButton = createModernButton("Thêm nhân viên", PRIMARY_COLOR, "\u002B"); // Plus sign
        JButton editButton = createModernButton("Sửa", SECONDARY_COLOR, "\u270E");  // Pencil icon
        JButton deleteButton = createModernButton("Xóa", ACCENT_COLOR, "\u2716");   // X icon
        JButton refreshButton = createModernButton("Làm mới", new Color(52, 73, 94), "\u21BB"); // Refresh icon
        JButton exportButton = createModernButton("Xuất Excel", EXPORT_COLOR, "\u2193"); // Down arrow
        JButton importButton = createModernButton("Nhập Excel", IMPORT_COLOR, "\u2191"); // Up arrow

        // Button panel with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Spacer
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Spacer
        buttonPanel.add(refreshButton);

        // Modern search field
        JPanel searchPanel = createModernSearchPanel();
        
        // Top panel organization with proper spacing
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setBackground(CARD_BACKGROUND);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        JPanel northContainer = new JPanel(new BorderLayout(0, 15));
        northContainer.setBackground(CARD_BACKGROUND);
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.SOUTH);

        // Table setup with improved styling
        String[] columns = {"ID", "Họ", "Tên", "Ngày sinh", "Ngày vào làm",
                           "SĐT", "Địa chỉ", "Người liên hệ", "SĐT liên hệ", "Chức vụ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override 
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        userTable = new JTable(tableModel);
        userTable.setRowHeight(45); // Taller rows
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowSorter(new TableRowSorter<>(tableModel));
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(235, 235, 235));
        userTable.setSelectionBackground(new Color(214, 234, 248));
        userTable.setSelectionForeground(TEXT_COLOR);
        userTable.setIntercellSpacing(new Dimension(5, 5)); // Add space between cells
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        styleHeader(userTable);
        styleRows(userTable);
        
        // Create a better-looking scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Enhanced pagination with modern controls
        JPanel paginationPanel = createModernPaginationPanel();

        // Add components to the card panel
        cardPanel.add(northContainer, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        cardPanel.add(paginationPanel, BorderLayout.SOUTH);
        
        // Add the card panel to the main panel
        add(cardPanel, BorderLayout.CENTER);

        // Listeners
        addButton.addActionListener(e->showAddDialog());
        editButton.addActionListener(e->showEditDialog());
        deleteButton.addActionListener(e->deleteEmployee());
        refreshButton.addActionListener(e->loadEmployees());
        exportButton.addActionListener(e->exportToExcel());
        importButton.addActionListener(e->importFromExcel());
        
        // Initial load
        loadEmployees();
    }
    
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint white background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                
                // Create shadow effect
                int shadowSize = 4;
                for (int i = 0; i < shadowSize; i++) {
                    int alpha = 20 - (i * 4);
                    if (alpha < 0) alpha = 0;
                    g2d.setColor(new Color(0, 0, 0, alpha));
                    g2d.draw(new RoundRectangle2D.Double(i, i, getWidth() - (i * 2) - 1, getHeight() - (i * 2) - 1, 12, 12));
                }
                
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBackground(CARD_BACKGROUND);
        return panel;
    }
    
    private JPanel createModernSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(CARD_BACKGROUND);
        
        // Search icon
        JLabel searchIcon = new JLabel("\uD83D\uDD0D"); // Unicode magnifying glass
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchIcon.setHorizontalAlignment(SwingConstants.CENTER);
        searchIcon.setForeground(new Color(120, 120, 120));
        searchIcon.setPreferredSize(new Dimension(35, 35));
        
        // Search label
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        
        // Search field with rounded borders
        searchField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(250, 38));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 35, 5, 10));
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){
                currentSearch = searchField.getText();
                currentPage = 1;
                pageSpinner.setValue(1);
                loadEmployees();
            }
        });
        
        // Panel to hold search field and icon
        JPanel textFieldPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 240, 240));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.setColor(new Color(210, 210, 210));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
            }
        };
        textFieldPanel.setOpaque(false);
        textFieldPanel.add(searchField, BorderLayout.CENTER);
        textFieldPanel.add(searchIcon, BorderLayout.WEST);
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);  
        searchPanel.add(textFieldPanel, BorderLayout.EAST);
        
        return searchPanel;
    }
    
    private JPanel createModernPaginationPanel() {
        // Create modern pagination buttons with icons
        JButton prevButton = createModernButton("« Trước", SECONDARY_COLOR, null);
        JButton nextButton = createModernButton("Sau »", SECONDARY_COLOR, null);
        
        // Spinner with better styling
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        pageSpinner.setPreferredSize(new Dimension(70, 30));
        pageSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Spinner.arrowButtonSize", new Dimension(20, 15));
        UIManager.put("Spinner.arrowButtonInsets", new Insets(2, 2, 2, 2));
        
        // Labels with better styling
        totalPagesLabel = new JLabel(" / 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPagesLabel.setForeground(TEXT_COLOR);
        
        JLabel pageInfoLabel = new JLabel("Trang:");
        pageInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pageInfoLabel.setForeground(TEXT_COLOR);
        
        // Modern pagination panel
        JPanel paginationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Add subtle gradient at the top
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(235, 235, 235, 100),
                    0, 10, CARD_BACKGROUND);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), 10);
                
                g2d.dispose();
            }
        };
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(CARD_BACKGROUND);
        paginationPanel.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        // Add components to panel
        paginationPanel.add(prevButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(pageSpinner);
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(nextButton);
        
        // Pagination button listeners
        prevButton.addActionListener(e -> {
            if(currentPage > 1) {
                currentPage--;
                pageSpinner.setValue(currentPage);
                loadEmployees();
            }
        });
        
        nextButton.addActionListener(e -> {
            if(currentPage < totalPages) {
                currentPage++;
                pageSpinner.setValue(currentPage);
                loadEmployees();
            }
        });
        
        pageSpinner.addChangeListener(e -> {
            currentPage = (Integer) pageSpinner.getValue();
            loadEmployees();
        });
        
        return paginationPanel;
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> list = employeeController.getEmployee(currentPage, pageSize, currentSearch);
        int total = employeeController.countEmployee(currentSearch);
        totalPages = Math.max((int) Math.ceil((double) total / pageSize), 1);
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

        for (Employee e : list) {
            tableModel.addRow(new Object[]{
                e.getEmployeeId(),
                e.getFirstName(),
                e.getLastName(),
                e.getBirthDate(),
                e.getHireDate(),
                e.getPhone(),
                e.getAddress(),
                e.getEmergencyContact(),
                e.getEmergencyPhone(),
                e.getRole()
            });
        }
    }
    
    private JButton createModernButton(String text, Color bgColor, String icon) {
        JButton btn = new JButton();
        
        // Format button text with icon if provided
        if (icon != null && !icon.isEmpty()) {
            btn.setText(icon + " " + text);
        } else {
            btn.setText(text);
        }
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(LIGHT_TEXT);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(1, 1, 1, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Custom painting for button
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                // Choose button color based on state
                Color buttonColor = bgColor;
                if (model.isPressed()) {
                    buttonColor = bgColor.darker();
                } else if (model.isRollover()) {
                    buttonColor = bgColor.brighter();
                }
                
                // Fill button background with rounded corners
                g2d.setColor(buttonColor);
                g2d.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), 10, 10));
                
                // Add subtle gradient effect
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 50),
                    0, c.getHeight(), new Color(255, 255, 255, 0)
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight() / 2, 10, 10));
                
                // Add subtle shadow when not pressed
                if (!model.isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fill(new RoundRectangle2D.Double(0, c.getHeight() - 3, c.getWidth(), 3, 5, 5));
                }
                
                g2d.dispose();
                super.paint(g, c);
            }
        });
        
        // Hover effects
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
        });
        
        return btn;
    }

    private void showAddDialog() {
        JDialog dialog = createStyledDialog("Thêm nhân viên");
        JPanel content = createFormPanel(null);
        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            showStyledMessage("Vui lòng chọn nhân viên", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Employee emp = employeeController.getEmployeeById(id);
        if (emp == null) {
            showStyledMessage("Không tìm thấy nhân viên", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = createStyledDialog("Sửa nhân viên");
        JPanel content = createFormPanel(emp);
        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JDialog createStyledDialog(String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Add styled title bar with icon
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(LIGHT_TEXT);
        
        // Add icon based on dialog type
        String iconText = title.contains("Thêm") ? "➕ " : "✏️ ";
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(LIGHT_TEXT);
        
        JPanel titleContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleContent.setBackground(PRIMARY_COLOR);
        titleContent.add(iconLabel);
        titleContent.add(titleLabel);
        
        titleBar.add(titleContent, BorderLayout.WEST);
        dialog.add(titleBar, BorderLayout.NORTH);
        
        // Add a close button
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(LIGHT_TEXT);
        closeButton.setBackground(null);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dialog.dispose());
        
        titleBar.add(closeButton, BorderLayout.EAST);
        
        // Make the dialog more visually appealing
        dialog.setUndecorated(true); // Remove standard window decorations
    
        // Use a JPanel with shadow effect as the content pane
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Set the new content pane
        dialog.setContentPane(contentPanel);
        dialog.add(titleBar, BorderLayout.NORTH);
        
        // Make the dialog draggable
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                dialog.setLocation(
                    dialog.getLocation().x + e.getX(),
                    dialog.getLocation().y + e.getY()
                );
            }
            
            public void mouseReleased(MouseEvent e) {
                dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                dialog.setLocation(
                    dialog.getLocation().x + e.getX() - titleBar.getWidth() / 2,
                    dialog.getLocation().y + e.getY() - titleBar.getHeight() / 2
                );
            }
        });
        
        return dialog;
    }

    private JPanel createFormPanel(Employee employee) {
        boolean isNewEmployee = employee == null;
        
        // Create main panel with shadow border
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint white background with rounded corners
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        mainPanel.setBackground(CARD_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Create title and description for the form
        JLabel formTitle = new JLabel(isNewEmployee ? "Thông tin nhân viên mới" : "Cập nhật thông tin nhân viên");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(PRIMARY_COLOR);
        
        JLabel formDescription = new JLabel(isNewEmployee ? 
                "Vui lòng điền đầy đủ thông tin nhân viên" : 
                "Chỉnh sửa thông tin của nhân viên");
        formDescription.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        formDescription.setForeground(new Color(120, 120, 120));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(CARD_BACKGROUND);
        titlePanel.add(formTitle);
        titlePanel.add(formDescription);
        
        // Use a tabbed form layout with two columns for better organization
        JPanel personalInfoPanel = new JPanel(new GridBagLayout());
        personalInfoPanel.setBackground(CARD_BACKGROUND);
        
        JPanel contactInfoPanel = new JPanel(new GridBagLayout());
        contactInfoPanel.setBackground(CARD_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Custom field styling
        UIManager.put("TextField.caretForeground", PRIMARY_COLOR);
        UIManager.put("ComboBox.selectionBackground", PRIMARY_COLOR);
        
        // Create styled form fields with icons
        JTextField fn = createStyledTextField(employee != null ? employee.getFirstName() : "", "👤");
        JTextField ln = createStyledTextField(employee != null ? employee.getLastName() : "", "👤");
        JDateChooser bd = createStyledDateChooser(employee != null ? employee.getBirthDate() : null);
        JDateChooser hd = createStyledDateChooser(employee != null ? employee.getHireDate() : null);
        JTextField phone = createStyledTextField(employee != null ? employee.getPhone() : "", "📱");
        JTextField addr = createStyledTextField(employee != null ? employee.getAddress() : "", "🏠");
        JTextField ec = createStyledTextField(employee != null ? employee.getEmergencyContact() : "", "👥");
        JTextField ep = createStyledTextField(employee != null ? employee.getEmergencyPhone() : "", "📞");
        JComboBox<String> role = createStyledComboBox(new String[]{"Warehouse", "Sales", "Manager"});
        
        if (employee != null) {
            role.setSelectedItem(employee.getRole());
        }
        
        // Add field validation
        fn.putClientProperty("FieldName", "Họ");
        ln.putClientProperty("FieldName", "Tên");
        phone.putClientProperty("FieldName", "SĐT");
        
        DocumentListener validationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateField(e); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateField(e); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateField(e); }
            
            private void validateField(DocumentEvent e) {
                JTextField field = (JTextField) e.getDocument().getProperty("owner");
                if (field != null) {
                    if (field.getText().trim().isEmpty()) {
                        field.setBackground(new Color(255, 243, 243));
                        field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(255, 153, 153), 1, true),
                            BorderFactory.createEmptyBorder(8, 30, 8, 10)
                        ));
                    } else {
                        field.setBackground(Color.WHITE);
                        field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                            BorderFactory.createEmptyBorder(8, 30, 8, 10)
                        ));
                    }
                }
            }
        };
        
        fn.getDocument().addDocumentListener(validationListener);
        fn.getDocument().putProperty("owner", fn);
        
        ln.getDocument().addDocumentListener(validationListener);
        ln.getDocument().putProperty("owner", ln);
        
        phone.getDocument().addDocumentListener(validationListener);
        phone.getDocument().putProperty("owner", phone);
        
        // Personal Information Section
        JLabel personalInfoLabel = new JLabel("Thông tin cá nhân");
        personalInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        personalInfoLabel.setForeground(PRIMARY_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        personalInfoPanel.add(personalInfoLabel, gbc);
        gbc.gridwidth = 1;
        
        addFormRow(personalInfoPanel, "Họ:", fn, gbc, 1);
        addFormRow(personalInfoPanel, "Tên:", ln, gbc, 2);
        addFormRow(personalInfoPanel, "Ngày sinh:", bd, gbc, 3);
        addFormRow(personalInfoPanel, "Ngày vào làm:", hd, gbc, 4);
        addFormRow(personalInfoPanel, "Chức vụ:", role, gbc, 5);
        
        // Contact Information Section
        JLabel contactInfoLabel = new JLabel("Thông tin liên hệ");
        contactInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contactInfoLabel.setForeground(PRIMARY_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contactInfoPanel.add(contactInfoLabel, gbc);
        gbc.gridwidth = 1;
        
        addFormRow(contactInfoPanel, "Số điện thoại:", phone, gbc, 1);
        addFormRow(contactInfoPanel, "Địa chỉ:", addr, gbc, 2);
        addFormRow(contactInfoPanel, "Người liên hệ:", ec, gbc, 3);
        addFormRow(contactInfoPanel, "SĐT liên hệ:", ep, gbc, 4);
        
        // Create a panel to hold both sections side by side
        JPanel formSectionsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        formSectionsPanel.setBackground(CARD_BACKGROUND);
        formSectionsPanel.add(personalInfoPanel);
        formSectionsPanel.add(contactInfoPanel);
        
        // Create separator
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setBackground(CARD_BACKGROUND);
        
        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setBackground(CARD_BACKGROUND);
        
        JButton saveBtn = createActionButton("Lưu", PRIMARY_COLOR, "✓");
        JButton cancelBtn = createActionButton("Hủy", new Color(158, 158, 158), "✕");
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        
        // Button actions
        final Employee finalEmp = employee;
        saveBtn.addActionListener(e -> {
            // Check required fields
            if (fn.getText().trim().isEmpty() || ln.getText().trim().isEmpty() || phone.getText().trim().isEmpty()) {
                showStyledMessage("Vui lòng điền đầy đủ thông tin bắt buộc", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Employee emp = finalEmp != null ? finalEmp : new Employee();
                emp.setFirstName(fn.getText());
                emp.setLastName(ln.getText());
                emp.setBirthDate(bd.getDate());
                emp.setHireDate(hd.getDate());
                emp.setPhone(phone.getText());
                emp.setAddress(addr.getText());
                emp.setEmergencyContact(ec.getText());
                emp.setEmergencyPhone(ep.getText());
                emp.setRole((String) role.getSelectedItem());
                
                boolean success = false;
                if (finalEmp != null) {
                    success = employeeController.updateEmployee(emp);
                } else {
                    success = employeeController.addEmployee(emp);
                }
                
                if (success) {
                    showStyledMessage(finalEmp != null ? "Cập nhật thành công" : "Thêm thành công", 
                                    JOptionPane.INFORMATION_MESSAGE);
                    loadEmployees();
                    ((JDialog) SwingUtilities.getWindowAncestor(mainPanel)).dispose();
                } else {
                    showStyledMessage(finalEmp != null ? "Lỗi cập nhật" : "Lỗi thêm", 
                                    JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                showStyledMessage("Kiểm tra lại dữ liệu đã nhập", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelBtn.addActionListener(e -> {
            ((JDialog) SwingUtilities.getWindowAncestor(mainPanel)).dispose();
        });
        
        // Assemble panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formSectionsPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CARD_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        bottomPanel.add(separator, BorderLayout.NORTH);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JButton createActionButton(String text, Color color, String icon) {
        JButton button = new JButton();
        if (icon != null) {
            button.setText(icon + " " + text);
        } else {
            button.setText(text);
        }
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(LIGHT_TEXT);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Custom rounded button with hover effects
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                int width = c.getWidth();
                int height = c.getHeight();
                
                // Button background
                Color bg = color;
                if (model.isPressed()) {
                    bg = color.darker();
                } else if (model.isRollover()) {
                    bg = color.brighter();
                }
                
                // Draw rounded rectangle
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, width, height, 10, 10));
                
                // Add subtle gradient
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 50),
                    0, height, new Color(255, 255, 255, 0)
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, width, height/2, 10, 10));
                
                g2.dispose();
                
                super.paint(g, c);
            }
            
            @Override
            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                // Custom pressed state
            }
        });
        
        // Apply dimensions
        button.setPreferredSize(new Dimension(120, 40));
        
        return button;
    }

    private void addFormRow(JPanel form, String labelText, Component field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.1;
        
        // Required field indicator
        boolean isRequired = labelText.equals("Họ:") || labelText.equals("Tên:") || labelText.equals("Số điện thoại:");
        String displayLabel = isRequired ? labelText + " *" : labelText;
        
        JLabel label = new JLabel(displayLabel);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(isRequired ? new Color(220, 53, 69) : TEXT_COLOR);
        form.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.9;
        form.add(field, gbc);
    }

    private JTextField createStyledTextField(String text, String icon) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 30, 8, 10)
        ));
        
        // Add icon to the left side of the text field
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            iconLabel.setPreferredSize(new Dimension(30, 30));
            iconLabel.setForeground(new Color(120, 120, 120));
            
            wrapper.add(iconLabel, BorderLayout.WEST);
        }
        
        wrapper.add(field, BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(250, 38));
        wrapper.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        
        // Add focus highlight
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                wrapper.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                wrapper.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
            }
        });
        
        return field;
    }

    private JDateChooser createStyledDateChooser(java.util.Date date) {
        JDateChooser chooser = new JDateChooser(date);
        chooser.setDateFormatString("yyyy-MM-dd");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Style the date chooser components
        for (Component comp : chooser.getComponents()) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                field.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            }
        }
        
        // Add calendar icon
        JButton calendarButton = chooser.getCalendarButton();
        if (calendarButton != null) {
            calendarButton.setText("📅");
            calendarButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            calendarButton.setBorderPainted(false);
            calendarButton.setFocusPainted(false);
            calendarButton.setContentAreaFilled(false);
            calendarButton.setBackground(null);
            calendarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        
        chooser.setPreferredSize(new Dimension(250, 38));
        chooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        
        return chooser;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(250, 38));
        
        // Style the dropdown
        comboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
                
                if (isSelected) {
                    label.setBackground(PRIMARY_COLOR);
                    label.setForeground(Color.WHITE);
                }
                
                return label;
            }
        });
        
        // Style the combobox
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Add focus highlight
        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
        
        return comboBox;
    }

    private void deleteEmployee() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            showStyledMessage("Chọn nhân viên", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        
        int confirm = showStyledConfirmDialog("Xác nhận xóa nhân viên này?", "Xóa nhân viên");
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (employeeController.deleteEmployee(id)) {
                showStyledMessage("Xóa thành công", JOptionPane.INFORMATION_MESSAGE);
                loadEmployees();
            } else {
                showStyledMessage("Lỗi xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showStyledMessage(String message, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog("Thông báo");
        dialog.setVisible(true);
    }
    
    private int showStyledConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(
            this, 
            message, 
            title, 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );
    }

    private void exportToExcel() {
        ExportDialog dlg = new ExportDialog((Frame)SwingUtilities.getWindowAncestor(this), userTable);
        dlg.setVisible(true);
        if(dlg.isExportConfirmed()) {
            File f = dlg.showSaveDialog();
            if(f != null) {
                try {
                    if(dlg.isXLSX())
                        ExcelExporter.exportToExcel(userTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
                    else
                        ExcelExporter.exportToCSV(userTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
                    
                    if(dlg.openAfterExport())
                        ExcelExporter.openFile(f);
                        
                    showStyledMessage("Xuất thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch(IOException ex) {
                    showStyledMessage("Lỗi xuất: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void importFromExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                if(!ExcelImporter.validateExcelFile(f)) {
                    showStyledMessage("Excel không hợp lệ", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String[] hdr = ExcelImporter.getColumnHeaders(f);
                JDialog mapDlg = createStyledDialog("Ánh xạ cột");
                
                JPanel content = new JPanel(new BorderLayout(0, 15));
                content.setBackground(CARD_BACKGROUND);
                content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JPanel mapP = new JPanel(new GridBagLayout());
                mapP.setBackground(CARD_BACKGROUND);
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                
                String[] fields = {"Họ", "Tên", "Ngày sinh", "Ngày vào làm", "SĐT", 
                                  "Địa chỉ", "Người liên hệ", "SĐT liên hệ", "Chức vụ"};
                JComboBox<String>[] cmb = new JComboBox[fields.length];
                
                for(int i = 0; i < fields.length; i++) {
                    JLabel fieldLabel = new JLabel(fields[i] + ":");
                    fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    fieldLabel.setForeground(TEXT_COLOR);
                    
                    cmb[i] = createStyledComboBox(hdr);
                    
                    gbc.gridx = 0;
                    gbc.gridy = i;
                    gbc.weightx = 0.3;
                    mapP.add(fieldLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.weightx = 0.7;
                    mapP.add(cmb[i], gbc);
                }
                
                JButton imp = createModernButton("Nhập", PRIMARY_COLOR, null);
                JButton cn = createModernButton("Hủy", new Color(158, 158, 158), null);
                
                imp.addActionListener(e -> {
                    mapDlg.dispose();
                    performImport(f, cmb);
                });
                
                cn.addActionListener(e -> mapDlg.dispose());
                
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                btnPanel.setBackground(CARD_BACKGROUND);
                btnPanel.add(imp);
                btnPanel.add(cn);
                
                content.add(mapP, BorderLayout.CENTER);
                content.add(btnPanel, BorderLayout.SOUTH);
                
                mapDlg.add(content, BorderLayout.CENTER);
                mapDlg.pack();
                mapDlg.setLocationRelativeTo(this);
                mapDlg.setVisible(true);
            } catch(IOException ex) {
                showStyledMessage("Lỗi import: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performImport(File file, JComboBox<String>[] combos) {
        try {
            List<Object[]> data = ExcelImporter.importFromExcel(file);
            int success = 0, failed = 0;
            StringBuilder log = new StringBuilder();
            
            for(int i = 0; i < data.size(); i++) {
                Object[] row = data.get(i);
                try {
                    Employee e = new Employee();
                    e.setFirstName(row[combos[0].getSelectedIndex()].toString());
                    e.setLastName(row[combos[1].getSelectedIndex()].toString());
                    e.setBirthDate(new java.sql.Date(((java.util.Date)row[combos[2].getSelectedIndex()]).getTime()));
                    e.setHireDate(new java.sql.Date(((java.util.Date)row[combos[3].getSelectedIndex()]).getTime()));
                    e.setPhone(row[combos[4].getSelectedIndex()].toString());
                    e.setAddress(row[combos[5].getSelectedIndex()].toString());
                    e.setEmergencyContact(row[combos[6].getSelectedIndex()].toString());
                    e.setEmergencyPhone(row[combos[7].getSelectedIndex()].toString());
                    e.setRole(row[combos[8].getSelectedIndex()].toString());
                    
                    if(employeeController.addEmployee(e)) {
                        success++;
                    } else {
                        failed++;
                        log.append("Dòng " + (i+2) + ": Lỗi lưu\n");
                    }
                } catch(Exception ex) {
                    failed++;
                    log.append("Dòng " + (i+2) + ": " + ex.getMessage() + "\n");
                }
            }
            
            String msg = "Nhập thành công " + success + " nhân viên." 
                      + (failed > 0 ? "\nKhông thể nhập " + failed + " nhân viên.\n" + log.toString() : "");
            
            showStyledMessage(msg, JOptionPane.INFORMATION_MESSAGE);
            loadEmployees();
        } catch(IOException ex) {
            showStyledMessage("Lỗi import: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                          boolean isSelected, boolean hasFocus, 
                                                          int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                // Styling for header
                label.setBackground(TABLE_HEADER_BG);
                label.setForeground(LIGHT_TEXT);
                label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                
                // Add border and padding
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                
                return label;
            }
        });
    }

    private void styleRows(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                         boolean isSelected, boolean hasFocus, 
                                                         int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                // Zebra striping
                if (!isSelected) {
                    Color bg = (row % 2 == 0) ? Color.WHITE : TABLE_ALTERNATE_COLOR;
                    label.setBackground(bg);
                }
                
                // Alignment based on column type
                if (column == 0 || column == 3 || column == 4) {
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    label.setHorizontalAlignment(JLabel.LEFT);
                }
                
                // Add padding
                label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
                
                return label;
            }
        });
    }
}
