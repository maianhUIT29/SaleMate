package com.salesmate.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    
    public ExportDialog(Frame owner, JTable table) {
        super(owner, "Xuất file", true);
        this.table = table;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Main content panel with vertical BoxLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // File format selection
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(new JLabel("Định dạng file:"));
        xlsxRadio = new JRadioButton("XLSX", true);
        csvRadio = new JRadioButton("CSV");
        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(xlsxRadio);
        formatGroup.add(csvRadio);
        formatPanel.add(xlsxRadio);
        formatPanel.add(csvRadio);
        contentPanel.add(formatPanel);
        
        // Column selection
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        columnPanel.setBorder(BorderFactory.createTitledBorder("Chọn cột để xuất"));
        
        // Create scrollable column selection
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        
        columnCheckboxes = new JCheckBox[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            columnCheckboxes[i] = new JCheckBox(table.getColumnName(i), true);
            checkboxPanel.add(columnCheckboxes[i]);
        }
        
        // Add select all/none buttons
        JPanel selectionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllButton = new JButton("Chọn tất cả");
        JButton selectNoneButton = new JButton("Bỏ chọn tất cả");
        
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

        // Add scrollable checkbox panel
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        columnPanel.add(scrollPane);
        contentPanel.add(columnPanel);
        
        // Options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Tùy chọn"));
        
        includeHeadersCheckbox = new JCheckBox("Bao gồm tiêu đề cột", true);
        openAfterExportCheckbox = new JCheckBox("Mở file sau khi xuất", false);
        
        optionsPanel.add(includeHeadersCheckbox);
        optionsPanel.add(openAfterExportCheckbox);
        contentPanel.add(optionsPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Xuất");
        JButton cancelButton = new JButton("Hủy");
        
        exportButton.addActionListener(e -> {
            if (validateSelection()) {
                exportConfirmed = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog properties
        pack();
        setLocationRelativeTo(getOwner());
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
}
