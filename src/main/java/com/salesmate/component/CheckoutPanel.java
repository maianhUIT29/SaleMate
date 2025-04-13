package com.salesmate.component;

import com.salesmate.model.Product;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.DetailController;
import com.salesmate.model.Invoice;
import com.salesmate.model.Detail;

import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.util.ArrayList;

import com.salesmate.utils.SessionManager;

import javax.swing.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

public class CheckoutPanel extends javax.swing.JPanel {
    
    private List<Product> products;
    private double totalPrice;
    private Map<Integer, Product> checkoutProducts = new HashMap<>();
    private DefaultTableModel tableModel;
    private InvoiceController invoiceController;
    private DetailController detailController;

    public CheckoutPanel() {
        initComponents();

        invoiceController = new InvoiceController();
        detailController = new DetailController();

        // Reinitialize the table model to avoid conflicts
        tableModel = new DefaultTableModel(
            new Object[][]{}, // Start with an empty table
            new String[]{"Tên sản phẩm", "Giá", "Số Lượng", "Thành tiền"} // Correct column headers
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing of the "Số Lượng" column (index 2)
                return column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Ensure the "Số Lượng" column is treated as Integer
                if (columnIndex == 2) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        tblProduct.setModel(tableModel); // Set the reinitialized table model
        tblProduct.setAutoCreateRowSorter(true); // Enable sorting

        // Adjust column widths after the table is rendered
        tblProduct.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustColumnWidths();
            }
        });

        // Add a listener to handle changes in the "Số Lượng" column
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 2) { // If the "Số Lượng" column is edited
                int productId = getProductIdFromRow(row);
                if (productId != -1) {
                    Product product = checkoutProducts.get(productId);
                    int newQuantity = (int) tableModel.getValueAt(row, 2);
                    product.setQuantity(newQuantity);

                    // Update the "Thành tiền" column
                    tableModel.setValueAt(
                        product.getPrice().multiply(new java.math.BigDecimal(newQuantity)),
                        row,
                        3
                    );

                    // Update the total price
                    updateTotal();
                }
            }
        });

        // Add a key listener to handle row deletion with the Delete key
        tblProduct.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int selectedRow = tblProduct.getSelectedRow();
                    if (selectedRow != -1) {
                        int productId = getProductIdFromRow(selectedRow);
                        if (productId != -1) {
                            checkoutProducts.remove(productId);
                            tableModel.removeRow(selectedRow);
                            updateTotal();
                        }
                    }
                }
            }
        });

        // Add action listener for "Huỷ" button
        btnCancel.addActionListener(e -> clearTable());

        // Add action listener for "In" button
        btnPrint.addActionListener(e -> exportToPDF());

        // Add action listener for payment button
        btnPayment.addActionListener(e -> processPayment());
    }

    private int getProductIdFromRow(int row) {
        String productName = (String) tableModel.getValueAt(row, 0);
        for (Product product : checkoutProducts.values()) {
            if (product.getProductName().equals(productName)) {
                return product.getProductId();
            }
        }
        return -1; // Product not found
    }

    public void addProductToCheckout(Product product) {
        if (checkoutProducts.containsKey(product.getProductId())) {
            Product existingProduct = checkoutProducts.get(product.getProductId());
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
        } else {
            checkoutProducts.put(product.getProductId(), new Product(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                1, // Initial quantity
                product.getBarcode(),
                product.getImage()
            ));
        }
        refreshCheckoutTable();
        updateTotal();
    }

    private void refreshCheckoutTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Product product : checkoutProducts.values()) {
            tableModel.addRow(new Object[]{
                product.getProductName(), // Product Name
                product.getPrice(),       // Price
                product.getQuantity(),    // Quantity
                product.getPrice().multiply(new java.math.BigDecimal(product.getQuantity())) // Total
            });
        }
        tblProduct.revalidate();
        tblProduct.repaint();
    }

    private void updateTotal() {
        totalPrice = checkoutProducts.values().stream()
            .map(product -> product.getPrice().multiply(new java.math.BigDecimal(product.getQuantity())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            .doubleValue();
        lblTotalValue.setText(String.format("%.2f VND", totalPrice));
    }

    private void adjustColumnWidths() {
        javax.swing.table.TableColumnModel columnModel = tblProduct.getColumnModel();
        int totalParts = 10; // 7 parts for "Tên sản phẩm", 1 part each for the others
        int tableWidth = tblProduct.getWidth();

        columnModel.getColumn(0).setPreferredWidth((tableWidth * 7) / totalParts); // "Tên sản phẩm"
        columnModel.getColumn(1).setPreferredWidth((tableWidth * 1) / totalParts); // "Giá"
        columnModel.getColumn(2).setPreferredWidth((tableWidth * 1) / totalParts); // "Số Lượng"
        columnModel.getColumn(3).setPreferredWidth((tableWidth * 1) / totalParts); // "Thành tiền"
    }

    private void clearTable() {
        tableModel.setRowCount(0); // Clear all rows
        checkoutProducts.clear(); // Clear the product map
        updateTotal(); // Reset the total
    }

    private void showPrintPreview() {
        javax.swing.JDialog printPreviewDialog = new javax.swing.JDialog();
        printPreviewDialog.setTitle("Print Preview");
        printPreviewDialog.setModal(true);
        printPreviewDialog.setSize(600, 400);
        printPreviewDialog.setLocationRelativeTo(this);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        javax.swing.JTable previewTable = new javax.swing.JTable(tableModel);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(previewTable);

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
        javax.swing.JButton btnPrintNow = new javax.swing.JButton("In");
        javax.swing.JButton btnCancelPreview = new javax.swing.JButton("Huỷ");

        btnPrintNow.addActionListener(e -> {
            System.out.println("Printing...");
            printPreviewDialog.dispose();
        });

        btnCancelPreview.addActionListener(e -> printPreviewDialog.dispose());

        buttonPanel.add(btnPrintNow);
        buttonPanel.add(btnCancelPreview);

        panel.add(scrollPane, java.awt.BorderLayout.CENTER);
        panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        printPreviewDialog.add(panel);
        printPreviewDialog.setVisible(true);
    }

    private void exportToPDF() {
        try {
            // Convert checkoutProducts to a list for JasperReports
            List<Map<String, Object>> data = new ArrayList<>();
            int index = 1;
            for (Product product : checkoutProducts.values()) {
                Map<String, Object> row = new HashMap<>();
                row.put("no", index++);
                row.put("productName", product.getProductName());
                row.put("price", product.getPrice());
                row.put("quantity", product.getQuantity());
                row.put("total", product.getPrice().multiply(new BigDecimal(product.getQuantity())));
                data.add(row);
            }

            // Create parameters map
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceNo", "INV" + System.currentTimeMillis());
            parameters.put("date", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            parameters.put("cashierName", SessionManager.getInstance().getLoggedInUser().getUsername());
            parameters.put("totalAmount", String.format("%,.0f VNĐ", totalPrice));
            parameters.put("paymentMethod", paymentMethodComboBox.getSelectedItem().toString());
            
            // Create the data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            try (InputStream reportStream = getClass().getResourceAsStream("/reports/invoice_template.jrxml")) {
                if (reportStream == null) {
                    throw new FileNotFoundException("Could not find invoice_template.jrxml");
                }
                
                // Compile the report
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
                
                // Fill the report
                JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, 
                    parameters, 
                    dataSource
                );
                
                // Export to PDF
                String fileName = "Invoice_" + System.currentTimeMillis() + ".pdf";
                String outputPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + fileName;
                
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                
                // Show success message with file location
                int option = JOptionPane.showConfirmDialog(
                    this,
                    "PDF đã được xuất thành công!\nVị trí: " + outputPath + "\n\nBạn có muốn mở file không?",
                    "Xuất PDF thành công",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Open the PDF if user chooses Yes
                if (option == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(outputPath));
                }
                
            } catch (FileNotFoundException e) {
                throw new Exception("Template file not found: " + e.getMessage());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi xuất PDF: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void processPayment() {
        if (checkoutProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có sản phẩm nào để thanh toán!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create new Invoice
            Invoice invoice = new Invoice();
            invoice.setUsersId(SessionManager.getInstance().getLoggedInUser().getUsersId());
            invoice.setTotal(new BigDecimal(totalPrice));
            invoice.setCreatedAt(new Date());
            invoice.setPaymentStatus("Paid"); // Set explicitly to "Paid"
            
            try {
                invoiceController.saveInvoice(invoice);
                int invoiceId = invoice.getInvoiceId(); // Assuming the ID is set in the invoice object after saving
                boolean detailsSuccess = true;
                
                // Create invoice details
                for (Product product : checkoutProducts.values()) {
                    Detail detail = new Detail();
                    detail.setInvoiceId(invoiceId);
                    detail.setProductId(product.getProductId());
                    detail.setQuantity(product.getQuantity());
                    detail.setPrice(product.getPrice());
                    detail.setTotal(product.getPrice().multiply(new BigDecimal(product.getQuantity())));
                    
                    if (!detailController.addDetail(detail)) {
                        detailsSuccess = false;
                        break;
                    }
                }
                
                if (detailsSuccess) {
                    showSuccessDialog(invoiceId);
                    clearTable();
                } else {
                    throw new Exception("Failed to save invoice details");
                }

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                    "Validation error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving invoice: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Show error dialog with detailed message
            JOptionPane.showMessageDialog(this,
                "Có lỗi xảy ra trong quá trình thanh toán:\n" + e.getMessage(),
                "Lỗi thanh toán",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSuccessDialog(int invoiceId) {
        JDialog successDialog = new JDialog();
        successDialog.setTitle("Thanh toán thành công");
        successDialog.setModal(true);
        successDialog.setSize(480, 400);
        successDialog.setLocationRelativeTo(this);
        successDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(240, 248, 255),
                    0, h, new Color(255, 255, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Top panel with icon and message
        JPanel topPanel = new JPanel(new BorderLayout(15, 10));
        topPanel.setOpaque(false);

        // Custom checkmark icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(new Color(46, 125, 50));
                g2d.drawArc(5, 5, 40, 40, 0, 360);
                g2d.drawPolyline(
                    new int[]{15, 25, 35},
                    new int[]{25, 35, 15},
                    3
                );
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(50, 50);
            }
        };

        // Success message
        JLabel titleLabel = new JLabel("Thanh toán thành công!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Center panel with invoice details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));

        // Add invoice details with consistent styling
        String[][] details = {
            {"Mã hoá đơn:", "#" + invoiceId},
            {"Tổng tiền:", String.format("%,.0f VNĐ", totalPrice)},
            {"Phương thức:", paymentMethodComboBox.getSelectedItem().toString()},
            {"Thời gian:", new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date())},
            {"Nhân viên:", SessionManager.getInstance().getLoggedInUser().getUsername()}
        };

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 14);
        Color textColor = new Color(51, 51, 51);

        for (String[] detail : details) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setOpaque(false);
            
            JLabel label = new JLabel(detail[0]);
            label.setFont(labelFont);
            label.setForeground(textColor);
            
            JLabel value = new JLabel(detail[1]);
            value.setFont(valueFont);
            value.setForeground(textColor);
            
            rowPanel.add(label, BorderLayout.WEST);
            rowPanel.add(value, BorderLayout.EAST);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            detailsPanel.add(rowPanel);
            detailsPanel.add(Box.createVerticalStrut(10));
        }

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        // Print button
        JButton printButton = createStyledButton("In hoá đơn", new Color(33, 150, 243));
        printButton.addActionListener(e -> {
            exportToPDF();
            successDialog.dispose();
        });

        // Close button
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.addActionListener(e -> successDialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        // Add all components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        successDialog.add(mainPanel);
        successDialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paymentDialog = new javax.swing.JDialog();
        lblHeader = new javax.swing.JLabel();
        ProductListContainer = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        btnPayment = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        paymentMethodComboBox = new javax.swing.JComboBox<>();
        lblTotalValue = new javax.swing.JLabel();
        lblPaymentMethod = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        javax.swing.GroupLayout paymentDialogLayout = new javax.swing.GroupLayout(paymentDialog.getContentPane());
        paymentDialog.getContentPane().setLayout(paymentDialogLayout);
        paymentDialogLayout.setHorizontalGroup(
            paymentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );
        paymentDialogLayout.setVerticalGroup(
            paymentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setAutoscrolls(true);

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("SALEMATE - HOÁ ĐƠN BÁN HÀNG");
        lblHeader.setRequestFocusEnabled(false);

        tblProduct.setAutoCreateRowSorter(true);
        tblProduct.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Tên sản phẩm", "Giá", "Số Lượng", "Thành tiền"
            }
        ));

        ProductListContainer.setViewportView(tblProduct);

        // Set column widths proportionally
        javax.swing.table.TableColumnModel columnModel = tblProduct.getColumnModel();
        int totalParts = 10; // 7 parts for "Tên sản phẩm", 1 part each for the others
        int tableWidth = tblProduct.getPreferredSize().width;

        columnModel.getColumn(0).setPreferredWidth((tableWidth * 7) / totalParts); // "Tên sản phẩm"
        columnModel.getColumn(1).setPreferredWidth((tableWidth * 1) / totalParts); // "Giá"
        columnModel.getColumn(2).setPreferredWidth((tableWidth * 1) / totalParts); // "Số Lượng"
        columnModel.getColumn(3).setPreferredWidth((tableWidth * 1) / totalParts); // "Thành tiền"

        btnPayment.setBackground(new java.awt.Color(0, 123, 255));
        btnPayment.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnPayment.setText("Thanh toán");

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotal.setText("Tổng tiền :");

        paymentMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền mặt", "Chuyển khoản" }));
        paymentMethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentMethodComboBoxActionPerformed(evt);
            }
        });

        lblTotalValue.setText("VND");

        lblPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPaymentMethod.setText("Phương thức thanh toán");

        btnCancel.setBackground(new java.awt.Color(255, 0, 0));
        btnCancel.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("Huỷ");

        btnPrint.setBackground(new java.awt.Color(40, 167, 69));
        btnPrint.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setText("In hoá đơn");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(252, 252, 252)
                                .addComponent(lblTotalValue, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                            .addComponent(ProductListContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblPaymentMethod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(paymentMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProductListContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalValue, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaymentMethod, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(paymentMethodComboBox))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPayment, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void paymentMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentMethodComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paymentMethodComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ProductListContainer;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPayment;
    private javax.swing.JButton btnPrint;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JDialog paymentDialog;
    private javax.swing.JComboBox<String> paymentMethodComboBox;
    private javax.swing.JTable tblProduct;
    // End of variables declaration//GEN-END:variables
}
