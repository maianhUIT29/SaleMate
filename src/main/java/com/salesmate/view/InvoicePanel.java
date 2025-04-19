package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.salesmate.controller.InvoiceController;
import com.salesmate.model.Invoice;

public class InvoicePanel extends JPanel {
    private InvoiceController invoiceController;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JTextField idField, userIdField, totalField, statusField;
    private JButton addButton, updateButton, deleteButton, refreshButton;

    public InvoicePanel() {
        invoiceController = new InvoiceController();
        setLayout(new BorderLayout());
        
        // Main Panel
        setLayout(new GroupLayout(this));
        GroupLayout layout = (GroupLayout) getLayout();
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GroupLayout(formPanel));
        GroupLayout formLayout = (GroupLayout) formPanel.getLayout();
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        
        JLabel idLabel = new JLabel("ID:");
        JLabel userIdLabel = new JLabel("User ID:");
        JLabel totalLabel = new JLabel("Tổng tiền:");
        JLabel statusLabel = new JLabel("Trạng thái:");
        
        idField = new JTextField(20);
        userIdField = new JTextField(20);
        totalField = new JTextField(20);
        statusField = new JTextField(20);
        
        // Form Panel Layout
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(idLabel)
                    .addComponent(userIdLabel)
                    .addComponent(totalLabel)
                    .addComponent(statusLabel))
                .addGap(10)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(idField)
                    .addComponent(userIdField)
                    .addComponent(totalField)
                    .addComponent(statusField))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        
        formLayout.setVerticalGroup(
            formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idField))
                .addGap(5)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(userIdLabel)
                    .addComponent(userIdField))
                .addGap(5)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLabel)
                    .addComponent(totalField))
                .addGap(5)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusField))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xóa");
        refreshButton = new JButton("Làm mới");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table
        String[] columnNames = {"ID", "User ID", "Tổng tiền", "Ngày tạo", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        
        // Main Panel Layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(formPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        // Add action listeners
        addButton.addActionListener(e -> addInvoice());
        updateButton.addActionListener(e -> updateInvoice());
        deleteButton.addActionListener(e -> deleteInvoice());
        refreshButton.addActionListener(e -> refreshTable());
        
        // Initial load
        refreshTable();
    }
    
    private void addInvoice() {
        try {
            Invoice invoice = new Invoice();
            invoice.setUsersId(Integer.parseInt(userIdField.getText()));
            invoice.setTotal(new BigDecimal(totalField.getText()));
            invoice.setPaymentStatus(statusField.getText());
            
            if (invoiceController.addInvoice(invoice)) {
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
                clearFields();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thất bại!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private void updateInvoice() {
        try {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần cập nhật!");
                return;
            }
            
            Invoice invoice = new Invoice();
            invoice.setInvoiceId(Integer.parseInt(idField.getText()));
            invoice.setUsersId(Integer.parseInt(userIdField.getText()));
            invoice.setTotal(new BigDecimal(totalField.getText()));
            invoice.setPaymentStatus(statusField.getText());
            
            if (invoiceController.updateInvoice(invoice)) {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!");
                clearFields();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thất bại!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private void deleteInvoice() {
        try {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!");
                return;
            }
            
            int invoiceId = Integer.parseInt(idField.getText());
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa hóa đơn này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (invoiceController.deleteInvoice(invoiceId)) {
                    JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
                    clearFields();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại!");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Invoice> invoices = invoiceController.getAllInvoices();
        if (invoices != null) {
            for (Invoice invoice : invoices) {
                Object[] row = {
                    invoice.getInvoiceId(),
                    invoice.getUsersId(),
                    invoice.getTotal(),
                    invoice.getCreatedAt(),
                    invoice.getPaymentStatus()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void clearFields() {
        idField.setText("");
        userIdField.setText("");
        totalField.setText("");
        statusField.setText("");
    }
} 