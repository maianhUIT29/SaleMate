package com.salesmate.component;

import com.salesmate.model.Product;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.DetailController;
import com.salesmate.controller.ProductController;
import com.salesmate.model.Invoice;
import com.salesmate.model.Detail;

import javax.swing.table.DefaultTableModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.File;
import java.util.ArrayList;

import com.salesmate.utils.SessionManager;
import javax.swing.*;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;

public class CheckoutPanel extends javax.swing.JPanel {

    private String formatCurrency(java.math.BigDecimal amount) {
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }
    
    private List<Product> products;
    private double totalPrice;
    private Map<Integer, Product> checkoutProducts = new HashMap<>();
    private DefaultTableModel tableModel;
    private InvoiceController invoiceController;
    private DetailController detailController;
    private ProductSelectionPanel productSelectionPanel;

    public CheckoutPanel() {
        initComponents();

        invoiceController = new InvoiceController();
        detailController = new DetailController();

        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Tên sản phẩm", "Giá gốc", "Giá giảm", "SL", "Thành tiền"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        
            private String formatCurrency(BigDecimal amount) {
                java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                return currencyFormatter.format(amount);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        tblProduct.setModel(tableModel);
        tblProduct.setAutoCreateRowSorter(true);

        tblProduct.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustColumnWidths();
            }
        });

        // Setup table cell editing and updating
        tblProduct.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 3) { // Quantity column
                    int productId = getProductIdFromRow(row);
                    if (productId != -1) {
                        Product product = checkoutProducts.get(productId);
                        int newQuantity = (Integer) tableModel.getValueAt(row, column);
                        if (newQuantity <= 0) {
                            checkoutProducts.remove(productId);
                            refreshCheckoutTable();
                        } else if (newQuantity <= product.getMaxQuantity()) {
                            product.setQuantity(newQuantity);
                            refreshCheckoutTable();
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                "Số lượng vượt quá tồn kho!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            product.setQuantity(product.getMaxQuantity());
                            refreshCheckoutTable();
                        }
                        updateTotal();
                    }
                }
            }
        });

        btnCancel.addActionListener(e -> clearTable());
        btnPayment.addActionListener(e -> processPayment());
    }

    private int getProductIdFromRow(int row) {
        String productName = (String) tableModel.getValueAt(row, 0);
        for (Product product : checkoutProducts.values()) {
            if (product.getProductName().equals(productName)) {
                return product.getProductId();
            }
        }
        return -1;
    }

    public void addProductToCheckout(Product product) {
        if (product.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this,
                "Sản phẩm đã hết hàng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (checkoutProducts.containsKey(product.getProductId())) {
            Product existingProduct = checkoutProducts.get(product.getProductId());
            if (existingProduct.getQuantity() >= product.getMaxQuantity()) {
                JOptionPane.showMessageDialog(this,
                    "Số lượng đã đạt giới hạn tồn kho!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
        } else {
            Product checkoutProduct = new Product(
                product.getProductId(),
                product.getProductName(),
                product.getOriginalPrice() != null ? product.getOriginalPrice() : product.getPrice(),
                1,
                product.getBarcode(),
                product.getImage()
            );
            checkoutProduct.setMaxQuantity(product.getQuantity());
            checkoutProduct.setPrice(product.getPrice());
            checkoutProduct.setOriginalPrice(product.getOriginalPrice());
            checkoutProduct.setDiscountPercent(product.getDiscountPercent());
            checkoutProducts.put(product.getProductId(), checkoutProduct);
        }
        refreshCheckoutTable();
        updateTotal();
    }

    private void refreshCheckoutTable() {
        tableModel.setRowCount(0);
        for (Product product : checkoutProducts.values()) {
            boolean hasDiscount = product.getOriginalPrice() != null && 
                product.getDiscountPercent() > 0 &&
                product.getOriginalPrice().compareTo(product.getPrice()) > 0;
            
            BigDecimal originalPrice = hasDiscount ? product.getOriginalPrice() : null;
            BigDecimal currentPrice = product.getPrice();
            
            tableModel.addRow(new Object[]{
                product.getProductName(),
                hasDiscount ? originalPrice : null,
                currentPrice, 
                product.getQuantity(),
                currentPrice.multiply(new BigDecimal(product.getQuantity()))
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
        
        lblTotalValue.setText(String.format("%,.0f VND", totalPrice));
        
        lblTotalValue.setMinimumSize(new Dimension(150, lblTotalValue.getHeight()));
        lblTotalValue.setPreferredSize(new Dimension(150, lblTotalValue.getHeight()));
    }

    private void adjustColumnWidths() {
        javax.swing.table.TableColumnModel columnModel = tblProduct.getColumnModel();
        int totalParts = 12;
        int tableWidth = tblProduct.getWidth();

        columnModel.getColumn(0).setPreferredWidth((tableWidth * 4) / totalParts);
        columnModel.getColumn(1).setPreferredWidth((tableWidth * 2) / totalParts);
        columnModel.getColumn(2).setPreferredWidth((tableWidth * 2) / totalParts);
        columnModel.getColumn(3).setPreferredWidth((tableWidth * 1) / totalParts);
        columnModel.getColumn(4).setPreferredWidth((tableWidth * 3) / totalParts);
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        checkoutProducts.clear();
        updateTotal();
    }

    private String generateInvoiceNumber(int invoiceId) {
        return "#INV-" + invoiceId;
    }

    private void exportToPDF(int invoiceId) {
        try {
            String invoiceNumber = generateInvoiceNumber(invoiceId);
            
            List<Map<String, Object>> data = new ArrayList<>();
            int index = 1;
            BigDecimal subtotal = BigDecimal.ZERO;
            
            for (Product product : checkoutProducts.values()) {
                Map<String, Object> row = new HashMap<>();
                row.put("no", index++);
                row.put("productName", product.getProductName());
                row.put("price", formatCurrency(product.getPrice()));
                row.put("quantity", product.getQuantity());
                BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                row.put("total", formatCurrency(itemTotal));
                subtotal = subtotal.add(itemTotal);
                data.add(row);
            }

            BigDecimal taxRate = new BigDecimal("0.1");
            BigDecimal taxAmount = subtotal.multiply(taxRate);
            BigDecimal grandTotal = subtotal.add(taxAmount);
            
            String cashierName = SessionManager.getInstance().getLoggedInUser().getUsername();
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceNo", invoiceNumber);
            parameters.put("date", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            parameters.put("cashierName", cashierName);
            parameters.put("subtotal", formatCurrency(subtotal));
            parameters.put("tax", formatCurrency(taxAmount));
            parameters.put("totalAmount", formatCurrency(grandTotal));
            parameters.put("paymentMethod", paymentMethodComboBox.getSelectedItem().toString());
            parameters.put("companyName", "SalesMate - Phần mềm quản lý bán hàng");
            parameters.put("companyAddress", "Trường Đại học Công nghệ Thông tin TP.HCM");
            parameters.put("companyPhone", "(+84) 28 1234 5678");
            parameters.put("companyEmail", "contact@salesmate.vn");
            parameters.put("thankYouMessage", "Cảm ơn quý khách đã mua hàng! Hẹn gặp lại.");
            
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            JasperReport jasperReport;
            try {
                try (InputStream reportStream = getClass().getResourceAsStream("/reports/invoice_template.jrxml")) {
                    if (reportStream != null) {
                        jasperReport = JasperCompileManager.compileReport(reportStream);
                    } else {
                        String templatePath = System.getProperty("user.dir") + "/src/main/resources/reports/invoice_template.jrxml";
                        File templateFile = new File(templatePath);
                        
                        if (templateFile.exists()) {
                            jasperReport = JasperCompileManager.compileReport(templatePath);
                        } else {
                            jasperReport = createDynamicReport();
                        }
                    }
                }
            } catch (Exception e) {
                jasperReport = createDynamicReport();
            }
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            String resourcesDir = System.getProperty("user.dir") + "/src/main/resources";
            String invoicesDir = resourcesDir + "/invoices";
            new File(invoicesDir).mkdirs();
            
            String fileName = "Invoice_" + invoiceId + ".pdf";
            String outputPath = invoicesDir + File.separator + fileName;
            
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            
            showSuccessDialog(invoiceId);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi xuất hóa đơn: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JasperPrint createInvoiceReport(int invoiceId) throws JRException {
        String invoiceNumber = generateInvoiceNumber(invoiceId);
        
        List<Map<String, Object>> data = new ArrayList<>();
        int index = 1;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscounts = BigDecimal.ZERO;
        
        for (Product product : checkoutProducts.values()) {
            Map<String, Object> row = new HashMap<>();
            row.put("no", index++);
            row.put("productName", product.getProductName());
            
            boolean hasDiscount = product.getOriginalPrice() != null && 
                                product.getDiscountPercent() > 0;
            
            if (hasDiscount) {
                row.put("price", formatCurrency(product.getPrice()) + " (giảm " + 
                       (int)product.getDiscountPercent() + "%)");
                
                BigDecimal singleItemDiscount = product.getOriginalPrice().subtract(product.getPrice());
                BigDecimal totalItemDiscount = singleItemDiscount.multiply(new BigDecimal(product.getQuantity()));
                totalDiscounts = totalDiscounts.add(totalItemDiscount);
            } else {
                row.put("price", formatCurrency(product.getPrice()));
            }
            
            row.put("quantity", product.getQuantity());
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
            row.put("total", formatCurrency(itemTotal));
            subtotal = subtotal.add(itemTotal);
            data.add(row);
        }

        BigDecimal taxRate = new BigDecimal("0.1");
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal grandTotal = subtotal.add(taxAmount);
        
        String cashierName = SessionManager.getInstance().getLoggedInUser().getUsername();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceNo", invoiceNumber);
        parameters.put("date", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        parameters.put("cashierName", cashierName);
        parameters.put("subtotal", formatCurrency(subtotal));
        parameters.put("tax", formatCurrency(taxAmount));
        parameters.put("totalAmount", formatCurrency(grandTotal));
        parameters.put("paymentMethod", paymentMethodComboBox.getSelectedItem().toString());
        parameters.put("companyName", "SalesMate - Phần mềm quản lý bán hàng");
        parameters.put("companyAddress", "Trường Đại học Công nghệ Thông tin TP.HCM");
        parameters.put("companyPhone", "(+84) 28 1234 5678");
        parameters.put("companyEmail", "contact@salesmate.vn");
        parameters.put("thankYouMessage", "Cảm ơn quý khách đã mua hàng! Hẹn gặp lại.");
        
        if (totalDiscounts.compareTo(BigDecimal.ZERO) > 0) {
            parameters.put("totalDiscount", formatCurrency(totalDiscounts));
        } else {
            parameters.put("totalDiscount", "0đ");
        }
        
        JasperReport jasperReport;
        try {
            try (InputStream reportStream = getClass().getResourceAsStream("/reports/invoice_template.jrxml")) {
                if (reportStream != null) {
                    jasperReport = JasperCompileManager.compileReport(reportStream);
                } else {
                    String templatePath = System.getProperty("user.dir") + "/src/main/resources/reports/invoice_template.jrxml";
                    File templateFile = new File(templatePath);
                    
                    if (templateFile.exists()) {
                        jasperReport = JasperCompileManager.compileReport(templatePath);
                    } else {
                        jasperReport = createDynamicReport();
                    }
                }
            }
        } catch (Exception e) {
            jasperReport = createDynamicReport();
        }
        
        return JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(data));
    }

    private void processPayment() {
        System.out.println("Bat dau xu ly thanh toan...");
        if (checkoutProducts.isEmpty()) {
            System.out.println("Khong co san pham nao trong gio de thanh toan.");
            JOptionPane.showMessageDialog(this, 
                "Không có sản phẩm nào để thanh toán!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Invoice invoice = new Invoice();
            invoice.setUsersId(SessionManager.getInstance().getLoggedInUser().getUsersId());
            invoice.setTotal(new BigDecimal(totalPrice));
            invoice.setCreatedAt(new Date());
            invoice.setPaymentStatus("Paid");
            
            try {
                invoiceController.saveInvoice(invoice);
                int invoiceId = invoice.getInvoiceId();
                boolean detailsSuccess = true;
                
                Map<Integer, Integer> soldQuantities = new HashMap<>();
                
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
                    
                    soldQuantities.put(product.getProductId(), product.getQuantity());
                }
                
                if (detailsSuccess) {
                    ProductController productController = new ProductController();
                    boolean allUpdatesSuccessful = true;
                    
                    for (Map.Entry<Integer, Integer> entry : soldQuantities.entrySet()) {
                        int productId = entry.getKey();
                        int soldQuantity = entry.getValue();
                        if (!productController.updateProductQuantity(productId, soldQuantity)) {
                            allUpdatesSuccessful = false;
                            System.out.println("Failed to update quantity for product ID: " + productId);
                        }
                    }
                    
                    if (!allUpdatesSuccessful) {
                        System.out.println("Some product quantities were not updated successfully");
                    }
                    
                    if (productSelectionPanel != null) {
                        productSelectionPanel.updateProductQuantities(soldQuantities);
                    }
                    
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
            JOptionPane.showMessageDialog(this,
                "Có lỗi xảy ra trong quá trình thanh toán:\n" + e.getMessage(),
                "Lỗi thanh toán",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
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

        setAutoscrolls(true);

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("SALEMATE - HOÁ ĐƠN BÁN HÀNG");
        lblHeader.setRequestFocusEnabled(false);

        tblProduct.setAutoCreateRowSorter(true);
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tên sản phẩm", "Giá gốc", "Giá giảm", "SL", "Thành tiền"
            }
        ));
        tblProduct.setToolTipText("");
        tblProduct.setName("");
        ProductListContainer.setViewportView(tblProduct);
        if (tblProduct.getColumnModel().getColumnCount() > 0) {
            tblProduct.getColumnModel().getColumn(0).setMinWidth(80);
            tblProduct.getColumnModel().getColumn(0).setPreferredWidth(120);
            tblProduct.getColumnModel().getColumn(1).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(1).setPreferredWidth(10);
            tblProduct.getColumnModel().getColumn(2).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(2).setPreferredWidth(10);
            tblProduct.getColumnModel().getColumn(3).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(3).setPreferredWidth(10);
            tblProduct.getColumnModel().getColumn(4).setMinWidth(10);
            tblProduct.getColumnModel().getColumn(4).setPreferredWidth(10);
        }

        btnPayment.setBackground(new java.awt.Color(0, 123, 255));
        btnPayment.setFont(new java.awt.Font("Arial", 1, 12));
        btnPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnPayment.setText("Thanh toán");
        btnPayment.setOpaque(true);
        btnPayment.setContentAreaFilled(true);
        btnPayment.setBorderPainted(true);
        btnPayment.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblTotal.setText("Tổng tiền :");

        paymentMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền mặt", "Chuyển khoản" }));
        paymentMethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentMethodComboBoxActionPerformed(evt);
            }
        });

        lblTotalValue.setText("VND");

        lblPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPaymentMethod.setText("Phương thức thanh toán");

        btnCancel.setBackground(new java.awt.Color(255, 0, 0));
        btnCancel.setFont(new java.awt.Font("Arial", 1, 12));
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("Huỷ");
        btnCancel.setOpaque(true);
        btnCancel.setContentAreaFilled(true);
        btnCancel.setBorderPainted(true);
        btnCancel.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 156, Short.MAX_VALUE)
                                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalValue, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addGap(142, 142, 142))
                            .addComponent(ProductListContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPaymentMethod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(paymentMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10))))
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
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap())
        );
    }

    private void paymentMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void showSuccessDialog(int invoiceId) {
        String message = "Thanh toán thành công!\nMã hóa đơn: " + generateInvoiceNumber(invoiceId);
        int option = JOptionPane.showConfirmDialog(
            this,
            message + "\nBạn có muốn xuất hóa đơn PDF không?",
            "Thành công",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                exportToPDF(invoiceId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi xuất PDF: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private javax.swing.JScrollPane ProductListContainer;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPayment;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JDialog paymentDialog;
    private javax.swing.JComboBox<String> paymentMethodComboBox;
    private javax.swing.JTable tblProduct;

    private JasperReport createDynamicReport() throws JRException {
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("DynamicInvoice");
        jasperDesign.setPageWidth(595);
        jasperDesign.setPageHeight(842);
        jasperDesign.setColumnWidth(555);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(20);
        jasperDesign.setRightMargin(20);
        jasperDesign.setTopMargin(20);
        jasperDesign.setBottomMargin(20);

        // Create fields
        String[] fieldNames = {"no", "productName", "price", "quantity", "total"};
        for (String fieldName : fieldNames) {
            JRDesignField field = new JRDesignField();
            field.setName(fieldName);
            field.setValueClass(String.class);
            jasperDesign.addField(field);
        }

        // Create title band
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(50);
        
        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setX(0);
        titleText.setY(10);
        titleText.setWidth(555);
        titleText.setHeight(30);
        titleText.setText("HÓA ĐƠN BÁN HÀNG");
        titleText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleText.setFontSize(16f);
        titleText.setBold(true);
        
        titleBand.addElement(titleText);
        jasperDesign.setTitle(titleBand);

        // Create column header band
        JRDesignBand columnHeader = new JRDesignBand();
        columnHeader.setHeight(30);
        
        String[] headers = {"STT", "Tên sản phẩm", "Đơn giá", "SL", "Thành tiền"};
        int[] widths = {50, 200, 100, 50, 155};
        int xPos = 0;
        
        for (int i = 0; i < headers.length; i++) {
            JRDesignStaticText header = new JRDesignStaticText();
            header.setX(xPos);
            header.setY(0);
            header.setWidth(widths[i]);
            header.setHeight(30);
            header.setText(headers[i]);
            header.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            header.setBold(true);
            columnHeader.addElement(header);
            xPos += widths[i];
        }
        
        jasperDesign.setColumnHeader(columnHeader);

        // Create detail band
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(30);
        
        xPos = 0;
        String[] fields = {"no", "productName", "price", "quantity", "total"};
        
        for (int i = 0; i < fields.length; i++) {
            JRDesignTextField textField = new JRDesignTextField();
            textField.setX(xPos);
            textField.setY(0);
            textField.setWidth(widths[i]);
            textField.setHeight(30);
            
            JRDesignExpression expression = new JRDesignExpression();
            expression.setText("$F{" + fields[i] + "}");
            textField.setExpression(expression);
            
            textField.setHorizontalTextAlign(i == 1 ? HorizontalTextAlignEnum.LEFT : HorizontalTextAlignEnum.CENTER);
            
            detailBand.addElement(textField);
            xPos += widths[i];
        }
        
        ((JRDesignSection)jasperDesign.getDetailSection()).addBand(detailBand);

        return JasperCompileManager.compileReport(jasperDesign);
    }
}
