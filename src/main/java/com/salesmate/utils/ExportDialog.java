package com.salesmate.utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExportDialog extends JDialog {
    private boolean exportConfirmed = false;
    private JTextField fileNameField;
    private JComboBox<String> formatComboBox;
    private JCheckBox includeHeadersCheckBox;
    private JCheckBox openAfterExportCheckBox;

    public ExportDialog(Frame parent) {
        super(parent, "Xuất file Excel", true);
        initComponents();
    }

    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Filename
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Tên file:"), gbc);
        
        String defaultFileName = "Invoice_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileNameField = new JTextField(defaultFileName);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(fileNameField, gbc);

        // Format selection
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Định dạng:"), gbc);

        formatComboBox = new JComboBox<>(new String[]{"Excel (.xlsx)", "CSV (.csv)"});
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(formatComboBox, gbc);

        // Options
        includeHeadersCheckBox = new JCheckBox("Bao gồm tiêu đề cột", true);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(includeHeadersCheckBox, gbc);

        openAfterExportCheckBox = new JCheckBox("Mở file sau khi xuất", true);
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(openAfterExportCheckBox, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Xuất");
        JButton cancelButton = new JButton("Huỷ");

        exportButton.addActionListener(e -> {
            if (fileNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên file!");
                return;
            }
            exportConfirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isExportConfirmed() {
        return exportConfirmed;
    }

    public String getFileName() {
        return fileNameField.getText().trim();
    }

    public boolean isXLSX() {
        return formatComboBox.getSelectedIndex() == 0;
    }

    public boolean includeHeaders() {
        return includeHeadersCheckBox.isSelected();
    }

    public boolean openAfterExport() {
        return openAfterExportCheckBox.isSelected();
    }

    public File showSaveDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(getFileName() + (isXLSX() ? ".xlsx" : ".csv")));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}
