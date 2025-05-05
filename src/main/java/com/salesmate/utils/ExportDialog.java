package com.salesmate.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ExportDialog extends JDialog {
    private boolean exportConfirmed = false;
    private final JTable table;
    private JCheckBox[] columnCheckboxes;
    private JCheckBox includeHeadersCheckbox;
    private JRadioButton xlsxRadio;
    private JRadioButton csvRadio;
    private JCheckBox openAfterExportCheckbox;
    private JComboBox<String> filterComboBox;

    public ExportDialog(Frame owner, JTable table) {
        super(owner, "Xuất file", true);
        this.table = table;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Main content panel with vertical BoxLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // File format selection with better styling
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.setBackground(Color.WHITE);
        JLabel formatLabel = new JLabel("Định dạng file:");
        formatLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formatPanel.add(formatLabel);

        xlsxRadio = new JRadioButton("XLSX", true);
        csvRadio = new JRadioButton("CSV");
        styleRadioButton(xlsxRadio);
        styleRadioButton(csvRadio);

        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(xlsxRadio);
        formatGroup.add(csvRadio);
        formatPanel.add(xlsxRadio);
        formatPanel.add(csvRadio);
        contentPanel.add(formatPanel);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        JLabel filterLabel = new JLabel("Lọc dữ liệu:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterPanel.add(filterLabel);

        // Column selection with better styling
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        columnPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Chọn cột để xuất",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        columnPanel.setBackground(Color.WHITE);

        // Selection buttons with better styling
        JPanel selectionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionButtonPanel.setBackground(Color.WHITE);

        JButton selectAllButton = createStyledButton("Chọn tất cả", new Color(0, 123, 255));
        JButton selectNoneButton = createStyledButton("Bỏ chọn tất cả", new Color(108, 117, 125));

        selectAllButton.addActionListener(e -> {
            for (JCheckBox checkbox : columnCheckboxes) {
                checkbox.setSelected(true);
            }
        });

        selectNoneButton.addActionListener(e -> {
            for (JCheckBox checkbox : columnCheckboxes) {
                checkbox.setSelected(false);
            }
        });

        selectionButtonPanel.add(selectAllButton);
        selectionButtonPanel.add(selectNoneButton);
        contentPanel.add(selectionButtonPanel);

        // Checkbox panel with better styling
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBackground(Color.WHITE);

        columnCheckboxes = new JCheckBox[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            columnCheckboxes[i] = new JCheckBox(table.getColumnName(i), true);
            styleCheckBox(columnCheckboxes[i]);
            checkboxPanel.add(columnCheckboxes[i]);
        }

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        columnPanel.add(scrollPane);
        contentPanel.add(columnPanel);

        // Options panel with better styling
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Tùy chọn",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        optionsPanel.setBackground(Color.WHITE);

        includeHeadersCheckbox = new JCheckBox("Bao gồm tiêu đề cột", true);
        openAfterExportCheckbox = new JCheckBox("Mở file sau khi xuất", false);
        styleCheckBox(includeHeadersCheckbox);
        styleCheckBox(openAfterExportCheckbox);

        optionsPanel.add(includeHeadersCheckbox);
        optionsPanel.add(openAfterExportCheckbox);
        contentPanel.add(optionsPanel);

        // Button panel with better styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton exportButton = createStyledButton("Xuất", new Color(40, 167, 69));
        JButton cancelButton = createStyledButton("Hủy", new Color(220, 53, 69));

        exportButton.addActionListener(e -> {
            if (validateSelection()) {
                exportConfirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
        setMinimumSize(new Dimension(400, 600));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Fix for button appearance - ensure these properties are set correctly
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Add mouse listener to maintain color during hover with better contrast
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Make darker but preserve color
                Color darker = new Color(
                    Math.max((int)(backgroundColor.getRed() * 0.85), 0),
                    Math.max((int)(backgroundColor.getGreen() * 0.85), 0),
                    Math.max((int)(backgroundColor.getBlue() * 0.85), 0)
                );
                button.setBackground(darker);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setBackground(Color.WHITE);
    }

    private void styleRadioButton(JRadioButton radio) {
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radio.setBackground(Color.WHITE);
    }

    private boolean validateSelection() {
        boolean anySelected = false;
        for (JCheckBox checkbox : columnCheckboxes) {
            if (checkbox.isSelected()) {
                anySelected = true;
                break;
            }
        }

        if (!anySelected) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn ít nhất một cột để xuất",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public File showSaveDialog() {
        JFileChooser fileChooser = new JFileChooser();
        String extension = isXLSX() ? "xlsx" : "csv";
        fileChooser.setSelectedFile(new File("export." + extension));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith("." + extension)) {
                file = new File(file.getParentFile(), file.getName() + "." + extension);
            }
            return file;
        }
        return null;
    }

    public boolean isExportConfirmed() {
        return exportConfirmed;
    }

    public List<Integer> getSelectedColumns() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < columnCheckboxes.length; i++) {
            if (columnCheckboxes[i].isSelected()) {
                selected.add(i);
            }
        }
        return selected;
    }

    public boolean includeHeaders() {
        return includeHeadersCheckbox.isSelected();
    }

    public boolean isXLSX() {
        return xlsxRadio.isSelected();
    }

    public boolean openAfterExport() {
        return openAfterExportCheckbox.isSelected();
    }

    public String getSelectedFilter() {
        return (String) filterComboBox.getSelectedItem();
    }
}
