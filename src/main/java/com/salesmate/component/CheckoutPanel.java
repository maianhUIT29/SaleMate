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
import com.salesmate.utils.VNPayConfig;

import javax.swing.*;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import com.salesmate.utils.VNPayUtil;

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

    public void setProductSelectionPanel(ProductSelectionPanel productSelectionPanel) {
        this.productSelectionPanel = productSelectionPanel;
        System.out.println("CheckoutPanel: ProductSelectionPanel reference set");
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
        try {
            String invoiceNumber = generateInvoiceNumber(invoiceId);
            
            List<Map<String, Object>> data = new ArrayList<>();
            int index = 1;
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDiscounts = BigDecimal.ZERO;
            
            for (Product product : checkoutProducts.values()) {
                Map<String, Object> row = new HashMap<>();
                row.put("no", String.valueOf(index++));
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
                
                row.put("quantity", String.valueOf(product.getQuantity()));
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
            
            JasperReport jasperReport = null;
            
            try {
                InputStream reportStream = getClass().getResourceAsStream("/reports/invoice_template.jrxml");
                if (reportStream != null) {
                    jasperReport = JasperCompileManager.compileReport(reportStream);
                    reportStream.close();
                } else {
                    String projectDir = System.getProperty("user.dir");
                    String templatePath = projectDir + "/src/main/resources/reports/invoice_template.jrxml";
                    File templateFile = new File(templatePath);
                    
                    if (templateFile.exists()) {
                        jasperReport = JasperCompileManager.compileReport(templatePath);
                    } else {
                        templatePath = projectDir + "/src/main/resources/templates/invoice_template.jrxml";
                        templateFile = new File(templatePath);
                        
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
            
            if (jasperReport == null) {
                throw new JRException("Could not create or load report template");
            }
            
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            jasperPrint.setName("Invoice_" + invoiceId);
            
            return jasperPrint;
        } catch (Exception e) {
            throw new JRException("Failed to create invoice report: " + e.getMessage(), e);
        }
    }

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

        String[] fieldNames = {"no", "productName", "price", "quantity", "total"};
        for (String fieldName : fieldNames) {
            JRDesignField field = new JRDesignField();
            field.setName(fieldName);
            field.setValueClass(java.lang.String.class);
            jasperDesign.addField(field);
        }

        String[] paramNames = {"invoiceNo", "date", "cashierName", "subtotal", "tax", 
                              "totalAmount", "paymentMethod", "companyName", 
                              "companyAddress", "companyPhone", "companyEmail", 
                              "thankYouMessage", "totalDiscount"};
                              
        for (String paramName : paramNames) {
            JRDesignParameter parameter = new JRDesignParameter();
            parameter.setName(paramName);
            parameter.setValueClass(java.lang.String.class);
            try {
                jasperDesign.addParameter(parameter);
            } catch (Exception e) {
                System.err.println("Could not add parameter '" + paramName + "': " + e.getMessage());
            }
        }

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

        JRDesignBand summaryBand = new JRDesignBand();
        summaryBand.setHeight(100);
        
        JRDesignStaticText subtotalLabel = new JRDesignStaticText();
        subtotalLabel.setX(350);
        subtotalLabel.setY(10);
        subtotalLabel.setWidth(100);
        subtotalLabel.setHeight(20);
        subtotalLabel.setText("Tạm tính:");
        subtotalLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        summaryBand.addElement(subtotalLabel);
        
        JRDesignTextField subtotalField = new JRDesignTextField();
        subtotalField.setX(455);
        subtotalField.setY(10);
        subtotalField.setWidth(100);
        subtotalField.setHeight(20);
        JRDesignExpression subtotalExpr = new JRDesignExpression();
        subtotalExpr.setText("$P{subtotal}");
        subtotalField.setExpression(subtotalExpr);
        subtotalField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        summaryBand.addElement(subtotalField);
        
        jasperDesign.setSummary(summaryBand);

        return JasperCompileManager.compileReport(jasperDesign);
    }

    private void processPayment() {
        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
        
        if (checkoutProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào giỏ hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("=== PROCESS PAYMENT DEBUG ===");
        System.out.println("Payment method selected: " + paymentMethod);
        System.out.println("Products in checkout: " + checkoutProducts.size());
        System.out.println("Total price: " + totalPrice);

        // Validate VNPay configuration if paying by bank transfer
        if ("Chuyển khoản".equals(paymentMethod)) {
            if (!VNPayConfig.isConfigValid()) {
                JOptionPane.showMessageDialog(this, 
                    "Cấu hình VNPay không hợp lệ. Vui lòng kiểm tra file config.properties",
                    "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Validate product quantities against current stock
        ProductController productController = new ProductController();
        for (Product cartProduct : checkoutProducts.values()) {
            Product currentStock = productController.getProductById(cartProduct.getProductId());
            if (currentStock == null || currentStock.getQuantity() < cartProduct.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                    "Số lượng sản phẩm '" + cartProduct.getProductName() + "' trong giỏ vượt quá tồn kho!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            // Create invoice
            Invoice invoice = new Invoice();
            invoice.setTotal(BigDecimal.valueOf(totalPrice));
            invoice.setCreatedAt(new Date());
            invoice.setPaymentStatus("Chuyển khoản".equals(paymentMethod) ? "Unpaid" : "Paid");
            
            // Get current user ID from session
            int userId = SessionManager.getInstance().getLoggedInUser().getUsersId();
            invoice.setUsersId(userId);

            // Save invoice to get ID
            invoiceController.saveInvoice(invoice);
            int invoiceId = invoice.getInvoiceId();
            String invoiceNumber = generateInvoiceNumber(invoiceId);
            System.out.println("Generated Invoice ID: " + invoiceId);

            // Save details and update product quantities
            for (Product product : checkoutProducts.values()) {
                // Create and save invoice detail
                Detail detail = new Detail();
                detail.setInvoiceId(invoiceId);
                detail.setProductId(product.getProductId());
                detail.setQuantity(product.getQuantity());
                detail.setPrice(product.getPrice());
                detail.setTotal(product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())));
                detailController.addDetail(detail);

                // Update product stock
                Product stockProduct = productController.getProductById(product.getProductId());
                if (stockProduct != null) {
                    stockProduct.setQuantity(stockProduct.getQuantity() - product.getQuantity());
                    productController.updateProduct(stockProduct);
                }
            }

            // Handle payment based on method
            if ("Tiền mặt".equals(paymentMethod)) {
                // Show success dialog with invoice details - RESTORED ORIGINAL BEHAVIOR
                showSuccessDialog(invoiceId);
                clearTable(); // Clear cart after successful payment
            } else if ("Chuyển khoản".equals(paymentMethod)) {
                // Show VNPay QR Dialog
                VNPayQRDialog vnPayDialog = new VNPayQRDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    invoiceNumber,
                    invoice.getTotal(),
                    "Thanh toan don hang " + invoiceNumber,
                    invoiceId
                );
                vnPayDialog.setVisible(true);
                
                // If VNPay payment successful
                if (vnPayDialog.isPaymentCompleted()) {
                    // Update status to Paid
                    invoice.setPaymentStatus("Paid");
                    invoiceController.updateInvoice(invoice);
                    clearTable(); // Clear cart after successful payment
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi xử lý thanh toán: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        System.out.println("=== PROCESS PAYMENT END ===");
    }

    private void showSuccessDialog(int invoiceId) {
        JDialog successDialog = new JDialog();
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        successDialog.setTitle("Thanh toán thành công");
        successDialog.setModal(true);
        successDialog.setSize(480, 400);
        successDialog.setLocationRelativeTo(parentFrame);
        successDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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

        JPanel topPanel = new JPanel(new BorderLayout(15, 10));
        topPanel.setOpaque(false);
        
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

        JLabel titleLabel = new JLabel("Thanh toán thành công!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 33, 33));

        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));

        String invoiceNumber = generateInvoiceNumber(invoiceId);
        String[][] details = {
            {"Mã hoá đơn:", invoiceNumber},
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

        String resourcesDir = System.getProperty("user.dir") + "/src/main/resources";
        String invoicesDir = resourcesDir + "/invoices";
        new File(invoicesDir).mkdirs();
        
        String fileName = "Invoice_" + invoiceId + ".pdf";
        String outputPath = invoicesDir + File.separator + fileName;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton printButton = createStyledButton("In hoá đơn", new Color(0, 123, 255));
        printButton.addActionListener(e -> {
            try {
                JasperPrint jasperPrint = createInvoiceReport(invoiceId);
                showPrintPreview(jasperPrint);
                successDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    successDialog,
                    "Lỗi khi chuẩn bị hoá đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton saveButton = createStyledButton("Lưu hoá đơn", new Color(40, 167, 69));
        saveButton.addActionListener(e -> {
            try {
                JasperPrint jasperPrint = createInvoiceReport(invoiceId);
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                
                successDialog.dispose();
                
                showSaveSuccessDialog(outputPath, jasperPrint, invoiceId);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    successDialog,
                    "Lỗi khi lưu hoá đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.addActionListener(e -> successDialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        successDialog.add(mainPanel);
        successDialog.setVisible(true);
    }

    private void showSaveSuccessDialog(String filePath, JasperPrint jasperPrint, int invoiceId) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Xuất PDF thành công");
        dialog.setModal(true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel messageLabel = new JLabel("<html><center>Đã xuất hóa đơn thành công!<br>Đường dẫn: " + filePath + "</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton openFileButton = new JButton("Mở file");
        openFileButton.addActionListener(e -> {
            try {
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Không thể mở file: " + ex.getMessage());
            }
        });
        
        JButton printButton = new JButton("In hóa đơn");
        printButton.addActionListener(e -> {
            dialog.dispose();
            showPrintPreview(jasperPrint); // Use the parameter jasperPrint directly
        });
        
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(openFileButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showPrintPreview(JasperPrint jasperPrint) {
        JDialog previewDialog = new JDialog();
        previewDialog.setTitle("Xem trước khi in");
        previewDialog.setModal(true);
        previewDialog.setSize(800, 600);
        previewDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        net.sf.jasperreports.swing.JRViewer viewer = new net.sf.jasperreports.swing.JRViewer(jasperPrint);
        mainPanel.add(viewer, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton printButton = createStyledButton("In", new Color(0, 123, 255));
        printButton.addActionListener(e -> {
            try {
                JasperPrintManager.printReport(jasperPrint, true);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(
                    previewDialog,
                    "Lỗi khi in: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        JButton saveButton = createStyledButton("Lưu PDF", new Color(40, 167, 69));
        saveButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Lưu hoá đơn");
                fileChooser.setSelectedFile(new File("Invoice_" + jasperPrint.getName() + ".pdf"));
                
                if (fileChooser.showSaveDialog(previewDialog) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".pdf")) {
                        file = new File(file.getAbsolutePath() + ".pdf");
                    }
                    
                    JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
                    
                    JOptionPane.showMessageDialog(
                        previewDialog,
                        "Hoá đơn đã được lưu thành công tại:\n" + file.getAbsolutePath(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(
                    previewDialog,
                    "Lỗi khi lưu PDF: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.addActionListener(e -> previewDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        previewDialog.add(mainPanel);
        
        previewDialog.setVisible(true);
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
                "Tên sản phẩm", "Giá gốc", "Giá bán", "SL", "Thành tiền"
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

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 30));
        
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();
                
                Color fillColor = backgroundColor;
                if (model.isPressed()) {
                    fillColor = backgroundColor.darker();
                } else if (model.isRollover()) {
                    fillColor = new Color(
                        Math.min(backgroundColor.getRed() + 15, 255),
                        Math.min(backgroundColor.getGreen() + 15, 255),
                        Math.min(backgroundColor.getBlue() + 15, 255)
                    );
                }
                
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        return button;
    }
}
