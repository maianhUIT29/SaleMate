package com.salesmate.component;

import com.salesmate.model.Product;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.DetailController;
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
    private ProductSelectionPanel productSelectionPanel; // Thêm biến reference

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
        
            private String formatCurrency(BigDecimal amount) {
                java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                return currencyFormatter.format(amount);
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

        setupTable();

        // Add action listener for "Huỷ" button
        btnCancel.addActionListener(e -> clearTable());

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
        if (product.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this,
                "Sản phẩm đã hết hàng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ensure the product is added only once
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
                product.getPrice(),
                1,
                product.getBarcode(),
                product.getImage()
            );
            checkoutProduct.setMaxQuantity(product.getQuantity());
            checkoutProducts.put(product.getProductId(), checkoutProduct);
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
        lblTotalValue.setText(String.format("%,.0f VND", totalPrice)); // Format as integer with commas
    }

    private void adjustColumnWidths() {
        javax.swing.table.TableColumnModel columnModel = tblProduct.getColumnModel();
        int totalParts = 10; // 5 parts for "Tên sản phẩm", 1 part each for the others
        int tableWidth = tblProduct.getWidth();

        columnModel.getColumn(0).setPreferredWidth((tableWidth * 5) / totalParts); // "Tên sản phẩm"
        columnModel.getColumn(1).setPreferredWidth((tableWidth * 2) / totalParts); // "Giá"
        columnModel.getColumn(2).setPreferredWidth((tableWidth * 1) / totalParts); // "Số Lượng"
        columnModel.getColumn(3).setPreferredWidth((tableWidth * 2) / totalParts); // "Thành tiền"
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

    private JasperReport createDynamicReport() throws JRException {
        // Create a simple JasperDesign object
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("DynamicInvoice");
        jasperDesign.setPageWidth(595);
        jasperDesign.setPageHeight(842);
        jasperDesign.setColumnWidth(555);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(20);
        jasperDesign.setRightMargin(20);
        jasperDesign.setTopMargin(30);
        jasperDesign.setBottomMargin(30);

        // Create default style
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Sans_Normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName("DejaVu Sans");
        normalStyle.setFontSize(12f);
        normalStyle.setPdfFontName("Helvetica");
        jasperDesign.addStyle(normalStyle);

        // Create title style
        JRDesignStyle titleStyle = new JRDesignStyle();
        titleStyle.setName("Sans_Bold");
        titleStyle.setFontName("DejaVu Sans");
        titleStyle.setFontSize(18f);
        titleStyle.setBold(true);
        titleStyle.setPdfFontName("Helvetica-Bold");
        jasperDesign.addStyle(titleStyle);

        // Add fields
        String[] fieldNames = {"no", "productName", "price", "quantity", "total"};
        Class<?>[] fieldTypes = {Integer.class, String.class, String.class, Integer.class, String.class};
        
        for (int i = 0; i < fieldNames.length; i++) {
            JRDesignField field = new JRDesignField();
            field.setName(fieldNames[i]);
            field.setValueClass(fieldTypes[i]);
            jasperDesign.addField(field);
        }
        
        // Add parameters
        String[] paramNames = {"invoiceNo", "date", "cashierName", "subtotal", "tax", "totalAmount", "paymentMethod", "companyName"};
        for (String paramName : paramNames) {
            JRDesignParameter parameter = new JRDesignParameter();
            parameter.setName(paramName);
            parameter.setValueClass(String.class);
            jasperDesign.addParameter(parameter);
        }

        // Create title band with proper height to contain all elements
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(100); // Increased height to accommodate all elements
        
        // Main title
        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setX(0);
        titleText.setY(10);
        titleText.setWidth(jasperDesign.getColumnWidth());
        titleText.setHeight(30);
        titleText.setText("HÓA ĐƠN BÁN HÀNG");
        titleText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleText.setStyle(titleStyle);
        titleBand.addElement(titleText);

        // Add invoice number field in the title section
        JRDesignTextField invoiceNoField = new JRDesignTextField();
        invoiceNoField.setX(0);
        invoiceNoField.setY(50); // Adjusted Y position to fit in the band
        invoiceNoField.setWidth(jasperDesign.getColumnWidth());
        invoiceNoField.setHeight(25);
        
        JRDesignExpression invoiceNoExpr = new JRDesignExpression();
        invoiceNoExpr.setText("\"Mã hóa đơn: \" + $P{invoiceNo}");
        invoiceNoField.setExpression(invoiceNoExpr);
        invoiceNoField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleBand.addElement(invoiceNoField);

        jasperDesign.setTitle(titleBand);

        // Create page header for additional info
        JRDesignBand pageHeaderBand = new JRDesignBand();
        pageHeaderBand.setHeight(60); // Sufficient height for header content
        
        // Date info
        JRDesignStaticText dateLabel = new JRDesignStaticText();
        dateLabel.setX(0);
        dateLabel.setY(10);
        dateLabel.setWidth(100);
        dateLabel.setHeight(20);
        dateLabel.setText("Ngày:");
        dateLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        pageHeaderBand.addElement(dateLabel);
        
        JRDesignTextField dateField = new JRDesignTextField();
        dateField.setX(100);
        dateField.setY(10);
        dateField.setWidth(200);
        dateField.setHeight(20);
        JRDesignExpression dateExpr = new JRDesignExpression();
        dateExpr.setText("$P{date}");
        dateField.setExpression(dateExpr);
        pageHeaderBand.addElement(dateField);
        
        // Cashier info
        JRDesignStaticText cashierLabel = new JRDesignStaticText();
        cashierLabel.setX(350);
        cashierLabel.setY(10);
        cashierLabel.setWidth(80);
        cashierLabel.setHeight(20);
        cashierLabel.setText("Nhân viên:");
        cashierLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        pageHeaderBand.addElement(cashierLabel);
        
        JRDesignTextField cashierField = new JRDesignTextField();
        cashierField.setX(430);
        cashierField.setY(10);
        cashierField.setWidth(125);
        cashierField.setHeight(20);
        JRDesignExpression cashierExpr = new JRDesignExpression();
        cashierExpr.setText("$P{cashierName}");
        cashierField.setExpression(cashierExpr);
        pageHeaderBand.addElement(cashierField);
        
        // Payment method info
        JRDesignStaticText paymentLabel = new JRDesignStaticText();
        paymentLabel.setX(0);
        paymentLabel.setY(35);
        paymentLabel.setWidth(100);
        paymentLabel.setHeight(20);
        paymentLabel.setText("Thanh toán:");
        paymentLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        pageHeaderBand.addElement(paymentLabel);
        
        JRDesignTextField paymentField = new JRDesignTextField();
        paymentField.setX(100);
        paymentField.setY(35);
        paymentField.setWidth(200);
        paymentField.setHeight(20);
        JRDesignExpression paymentExpr = new JRDesignExpression();
        paymentExpr.setText("$P{paymentMethod}");
        paymentField.setExpression(paymentExpr);
        pageHeaderBand.addElement(paymentField);
        
        jasperDesign.setPageHeader(pageHeaderBand);

        // Create column headers
        JRDesignBand columnHeaderBand = new JRDesignBand();
        columnHeaderBand.setHeight(30);
        
        String[] columnTitles = {"STT", "Sản phẩm", "Đơn giá", "SL", "Thành tiền"};
        int[] columnWidths = {40, 200, 120, 50, 145};
        int xPos = 0;
        
        for (int i = 0; i < columnTitles.length; i++) {
            JRDesignStaticText header = new JRDesignStaticText();
            header.setX(xPos);
            header.setY(0);
            header.setWidth(columnWidths[i]);
            header.setHeight(25);
            header.setText(columnTitles[i]);
            header.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            header.setMode(ModeEnum.OPAQUE);
            header.setBackcolor(new Color(210, 210, 210));
            columnHeaderBand.addElement(header);
            xPos += columnWidths[i];
        }
        
        jasperDesign.setColumnHeader(columnHeaderBand);

        // Create detail band
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(30);
        
        xPos = 0;
        String[] fieldRefs = {"no", "productName", "price", "quantity", "total"};
        
        for (int i = 0; i < fieldRefs.length; i++) {
            JRDesignTextField textField = new JRDesignTextField();
            textField.setX(xPos);
            textField.setY(0);
            textField.setWidth(columnWidths[i]);
            textField.setHeight(25);
            
            JRDesignExpression expression = new JRDesignExpression();
            expression.setText("$F{" + fieldRefs[i] + "}");
            textField.setExpression(expression);
            
            if (i == 0 || i == 3) {
                textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            } else if (i == 2 || i == 4) {
                textField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
            }
            
            detailBand.addElement(textField);
            xPos += columnWidths[i];
        }
        
        ((JRDesignSection)jasperDesign.getDetailSection()).addBand(detailBand);

        // Create summary band for totals
        JRDesignBand summaryBand = new JRDesignBand();
        summaryBand.setHeight(120);
        
        // Subtotal
        JRDesignStaticText subtotalLabel = new JRDesignStaticText();
        subtotalLabel.setX(340);
        subtotalLabel.setY(10);
        subtotalLabel.setWidth(100);
        subtotalLabel.setHeight(20);
        subtotalLabel.setText("Tạm tính:");
        subtotalLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        summaryBand.addElement(subtotalLabel);
        
        JRDesignTextField subtotalField = new JRDesignTextField();
        subtotalField.setX(450);
        subtotalField.setY(10);
        subtotalField.setWidth(105);
        subtotalField.setHeight(20);
        JRDesignExpression subtotalExpr = new JRDesignExpression();
        subtotalExpr.setText("$P{subtotal}");
        subtotalField.setExpression(subtotalExpr);
        subtotalField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        summaryBand.addElement(subtotalField);
        
        // Tax
        JRDesignStaticText taxLabel = new JRDesignStaticText();
        taxLabel.setX(340);
        taxLabel.setY(35);
        taxLabel.setWidth(100);
        taxLabel.setHeight(20);
        taxLabel.setText("Thuế (10%):");
        taxLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        summaryBand.addElement(taxLabel);
        
        JRDesignTextField taxField = new JRDesignTextField();
        taxField.setX(450);
        taxField.setY(35);
        taxField.setWidth(105);
        taxField.setHeight(20);
        JRDesignExpression taxExpr = new JRDesignExpression();
        taxExpr.setText("$P{tax}");
        taxField.setExpression(taxExpr);
        taxField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        summaryBand.addElement(taxField);
        
        // Total
        JRDesignStaticText totalLabel = new JRDesignStaticText();
        totalLabel.setX(340);
        totalLabel.setY(60);
        totalLabel.setWidth(100);
        totalLabel.setHeight(25);
        totalLabel.setText("Tổng cộng:");
        totalLabel.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        totalLabel.setBold(true);
        summaryBand.addElement(totalLabel);
        
        JRDesignTextField totalField = new JRDesignTextField();
        totalField.setX(450);
        totalField.setY(60);
        totalField.setWidth(105);
        totalField.setHeight(25);
        JRDesignExpression totalExpr = new JRDesignExpression();
        totalExpr.setText("$P{totalAmount}");
        totalField.setExpression(totalExpr);
        totalField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        totalField.setBold(true);
        summaryBand.addElement(totalField);
        
        // Thank you message
        JRDesignStaticText thankYouText = new JRDesignStaticText();
        thankYouText.setX(0);
        thankYouText.setY(90);
        thankYouText.setWidth(jasperDesign.getColumnWidth());
        thankYouText.setHeight(20);
        thankYouText.setText("Cảm ơn quý khách đã mua hàng! Hẹn gặp lại.");
        thankYouText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        summaryBand.addElement(thankYouText);
        
        jasperDesign.setSummary(summaryBand);

        // Add cashier info in footer section - with proper height
        JRDesignBand pageFooterBand = new JRDesignBand();
        pageFooterBand.setHeight(30);
        
        JRDesignTextField footerCashierField = new JRDesignTextField();
        footerCashierField.setX(0);
        footerCashierField.setY(5);
        footerCashierField.setWidth(jasperDesign.getColumnWidth() / 2);
        footerCashierField.setHeight(20);
        
        JRDesignExpression footerCashierExpr = new JRDesignExpression();
        footerCashierExpr.setText("\"Nhân viên: \" + $P{cashierName}");
        footerCashierField.setExpression(footerCashierExpr);
        pageFooterBand.addElement(footerCashierField);
        
        // Add page number in footer
        JRDesignTextField pageField = new JRDesignTextField();
        pageField.setX(jasperDesign.getColumnWidth() / 2);
        pageField.setY(5);
        pageField.setWidth(jasperDesign.getColumnWidth() / 2);
        pageField.setHeight(20);
        
        JRDesignExpression pageExpr = new JRDesignExpression();
        pageExpr.setText("\"Trang \" + $V{PAGE_NUMBER}");
        pageField.setExpression(pageExpr);
        pageField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        pageFooterBand.addElement(pageField);
        
        jasperDesign.setPageFooter(pageFooterBand);
        
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private void exportToPDF() {
        try {
            // Generate a unique invoice number
            String invoiceNumber = generateInvoiceNumber();
            
            // Convert checkoutProducts to a list for JasperReports
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

            // Calculate tax (10% of subtotal) and grand total
            BigDecimal taxRate = new BigDecimal("0.1"); // 10% tax
            BigDecimal taxAmount = subtotal.multiply(taxRate);
            BigDecimal grandTotal = subtotal.add(taxAmount);
            
            // Get cashier information
            String cashierName = SessionManager.getInstance().getLoggedInUser().getUsername();
            
            // Create parameters map with additional invoice details
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceNo", invoiceNumber);
            parameters.put("date", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            parameters.put("cashierName", cashierName);
            parameters.put("subtotal", formatCurrency(subtotal));
            parameters.put("tax", formatCurrency(taxAmount));
            parameters.put("totalAmount", formatCurrency(grandTotal));
            parameters.put("paymentMethod", paymentMethodComboBox.getSelectedItem().toString());
            parameters.put("companyName", "SalesMate");
            parameters.put("companyAddress", "Trường Đại học Công nghệ Thông tin TP.HCM");
            parameters.put("companyPhone", "(+84) 28 1234 5678");
            parameters.put("companyEmail", "contact@salesmate.vn");
            parameters.put("thankYouMessage", "Cảm ơn quý khách đã mua hàng! Hẹn gặp lại.");
            
            // Create the data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            // Try to find or create a report template
            JasperReport jasperReport;
            try {
                // Try to load from resources first
                try (InputStream reportStream = getClass().getResourceAsStream("/reports/invoice_template.jrxml")) {
                    if (reportStream != null) {
                        jasperReport = JasperCompileManager.compileReport(reportStream);
                    } else {
                        // Try to load from file system
                        String templatePath = System.getProperty("user.dir") + "/src/main/resources/reports/invoice_template.jrxml";
                        File templateFile = new File(templatePath);
                        
                        if (templateFile.exists()) {
                            jasperReport = JasperCompileManager.compileReport(templatePath);
                        } else {
                            // Use our simple template as fallback
                            jasperReport = createDynamicReport();
                        }
                    }
                }
            } catch (Exception e) {
                // Last resort - create a dynamic report
                jasperReport = createDynamicReport();
            }
            
            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Create invoices directory if it doesn't exist
            String appDir = System.getProperty("user.dir");
            String invoicesDir = appDir + File.separator + "invoices";
            new File(invoicesDir).mkdirs();
            
            // Define output path
            String fileName = "Invoice_" + invoiceNumber.replace("#", "") + ".pdf";
            String outputPath = invoicesDir + File.separator + fileName;
            
            // Show preview before printing
            showPDFPreview(jasperPrint, outputPath);
            
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

    private String generateInvoiceNumber() {
        String dateFormat = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        String randomPart = String.format("%04d", new java.util.Random().nextInt(10000));
        return "#INV-" + dateFormat + "-" + randomPart;
    }

    private void showPDFPreview(JasperPrint jasperPrint, String outputPath) {
        try {
            // Create a preview frame
            JDialog previewDialog = new JDialog();
            previewDialog.setTitle("Xem trước hóa đơn");
            previewDialog.setSize(800, 600);
            previewDialog.setLocationRelativeTo(this);
            previewDialog.setModal(true);
            
            // Create panel to hold the preview
            JPanel previewPanel = new JPanel(new BorderLayout());
            
            // Use JasperViewer component for preview
            net.sf.jasperreports.swing.JRViewer viewer = new net.sf.jasperreports.swing.JRViewer(jasperPrint);
            previewPanel.add(viewer, BorderLayout.CENTER);
            
            // Create button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            
            JButton printButton = createStyledButton("In hóa đơn", new Color(33, 150, 243));
            JButton saveButton = createStyledButton("Lưu PDF", new Color(46, 125, 50));
            JButton cancelButton = createStyledButton("Đóng", new Color(108, 117, 125));
            
            // Print button action
            printButton.addActionListener(e -> {
                try {
                    JasperPrintManager.printReport(jasperPrint, true); // Show print dialog
                } catch (JRException ex) {
                    JOptionPane.showMessageDialog(previewDialog,
                        "Lỗi khi in: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Save button action
            saveButton.addActionListener(e -> {
                try {
                    JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                    JOptionPane.showMessageDialog(previewDialog,
                        "Đã lưu hóa đơn vào: " + outputPath,
                        "Lưu thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (JRException ex) {
                    JOptionPane.showMessageDialog(previewDialog,
                        "Lỗi khi lưu: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Cancel button action
            cancelButton.addActionListener(e -> previewDialog.dispose());
            
            buttonPanel.add(printButton);
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            previewPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            previewDialog.getContentPane().add(previewPanel);
            previewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            previewDialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi hiển thị xem trước: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            
            // If preview fails, try direct save
            try {
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                int option = JOptionPane.showConfirmDialog(
                    this,
                    "Đã lưu hóa đơn vào: " + outputPath + "\n\nBạn có muốn mở file không?",
                    "Lưu thành công",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(outputPath));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi lưu hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public void setProductSelectionPanel(ProductSelectionPanel panel) {
        this.productSelectionPanel = panel;
    }

    private void processPayment() {
        System.out.println("Bat dau xu ly thanh toan...");
        if (checkoutProducts.isEmpty()) {
            System.out.println("Khong co san pham nao trong gio de thanh toan.");
            JOptionPane.showMessageDialog(this, 
                "Khong co san pham nao de thanh toan!", 
                "Loi", 
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
                
                // Tạo map để lưu số lượng đã bán của mỗi sản phẩm
                Map<Integer, Integer> soldQuantities = new HashMap<>();
                
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
                    
                    // Lưu số lượng đã bán
                    soldQuantities.put(product.getProductId(), product.getQuantity());
                }
                
                if (detailsSuccess) {
                    // Cập nhật số lượng trong ProductSelectionPanel
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
            // Show error dialog with detailed message
            JOptionPane.showMessageDialog(this,
                "Có lỗi xảy ra trong quá trình thanh toán:\n" + e.getMessage(),
                "Lỗi thanh toán",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSuccessDialog(int invoiceId) {
        JDialog successDialog = new JDialog();
        // Lấy frame cha
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        successDialog.setTitle("Thanh toán thành công");
        successDialog.setModal(true);
        successDialog.setSize(480, 400);
        // Set vị trí dialog ra giữa frame cha
        successDialog.setLocationRelativeTo(parentFrame);
        successDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Panel chính với gradient background
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

        // Panel chứa icon và title
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
                g2d.setColor(new Color(46, 125, 50)); // Màu xanh lá đậm hơn
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

        // Title với font size lớn hơn và màu đậm hơn  
        JLabel titleLabel = new JLabel("Thanh toán thành công!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 33, 33));

        // Thêm padding cho topPanel
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
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

    private void setupTable() {
        // Tạo custom renderer cho bảng
        tblProduct.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new java.awt.Color(51, 153, 255)); // Màu nền xanh dương khi chọn
                    c.setForeground(java.awt.Color.WHITE); // Chữ màu trắng khi chọn
                } else {
                    c.setBackground(row % 2 == 0 ? 
                        new java.awt.Color(255, 255, 255) : // Dòng chẵn màu trắng
                        new java.awt.Color(240, 240, 240)); // Dòng lẻ màu xám nhạt
                    c.setForeground(java.awt.Color.BLACK); // Chữ màu đen khi không chọn
                }

                // Căn giữa cho cột số lượng
                if (column == 2) {
                    ((JLabel) c).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                }
                
                return c;
            }
        });

        // Thêm grid lines cho bảng
        tblProduct.setShowGrid(true);
        tblProduct.setGridColor(new java.awt.Color(211, 211, 211)); // Màu xám nhạt cho grid
        tblProduct.setIntercellSpacing(new java.awt.Dimension(1, 1)); // Khoảng cách giữa các cell

        // Style cho header
        javax.swing.table.JTableHeader header = tblProduct.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                label.setBackground(new java.awt.Color(51, 153, 255)); // Background xanh dương
                label.setForeground(java.awt.Color.WHITE); // Chữ màu trắng
                label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                label.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(211, 211, 211)),
                    javax.swing.BorderFactory.createEmptyBorder(8, 4, 8, 4)
                ));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Căn giữa text
                setOpaque(true); // Để hiện background
                
                return label;
            }
        });
        
        header.setOpaque(false);
        header.setBackground(new java.awt.Color(51, 153, 255));
        header.setForeground(java.awt.Color.WHITE);

        // Không cho phép di chuyển các cột
        header.setReorderingAllowed(false);

        // Add a listener to handle changes in the "Số Lượng" column
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 2) { // Số lượng column
                int productId = getProductIdFromRow(row);
                if (productId != -1) {
                    Product product = checkoutProducts.get(productId);
                    try {
                        int newQuantity = (int) tableModel.getValueAt(row, 2);
                        if (newQuantity <= 0) {
                            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
                        }
                        if (newQuantity > product.getMaxQuantity()) {
                            throw new IllegalArgumentException("Số lượng vượt quá tồn kho (" + product.getMaxQuantity() + ")");
                        }

                        product.setQuantity(newQuantity);
                        tableModel.setValueAt(
                            product.getPrice().multiply(new java.math.BigDecimal(newQuantity)),
                            row,
                            3
                        );
                        updateTotal();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Số lượng không hợp lệ: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                        // Khôi phục giá trị cũ
                        tableModel.setValueAt(product.getQuantity(), row, 2);
                    }
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = tblProduct.getSelectedRow();
                    int col = tblProduct.getSelectedColumn();
                    
                    if (col == 2) { // Cột số lượng
                        try {
                            String input = tblProduct.getValueAt(row, col).toString();
                            int newQuantity = Integer.parseInt(input);
                            
                            int productId = getProductIdFromRow(row);
                            if (productId != -1) {
                                Product product = checkoutProducts.get(productId);
                                
                                if (newQuantity <= 0) {
                                    throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
                                }
                                
                                if (newQuantity > product.getMaxQuantity()) {
                                    throw new IllegalArgumentException("Số lượng vượt quá tồn kho (" + product.getMaxQuantity() + ")");
                                }
                                
                                // Cập nhật số lượng mới
                                product.setQuantity(newQuantity);
                                // Cập nhật thành tiền
                                tableModel.setValueAt(
                                    product.getPrice().multiply(new java.math.BigDecimal(newQuantity)),
                                    row,
                                    3
                                );
                                updateTotal();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(CheckoutPanel.this,
                                "Vui lòng nhập số nguyên hợp lệ!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            // Khôi phục giá trị cũ
                            Product product = checkoutProducts.get(getProductIdFromRow(row));
                            tableModel.setValueAt(product.getQuantity(), row, 2);
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(CheckoutPanel.this,
                                ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            // Khôi phục giá trị cũ
                            Product product = checkoutProducts.get(getProductIdFromRow(row));
                            tableModel.setValueAt(product.getQuantity(), row, 2);
                        }
                    }
                }
            }
        });
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
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tên sản phẩm", "Giá", "Số Lượng", "Thành tiền"
            }
        ));
        tblProduct.setToolTipText("");
        tblProduct.setName(""); // NOI18N
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
        }

        btnPayment.setBackground(new java.awt.Color(40, 167, 69)); // Màu xanh lá cây
        btnPayment.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnPayment.setText("Thanh toán");
        btnPayment.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));

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
                        .addGap(162, 162, 162)
                        .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JDialog paymentDialog;
    private javax.swing.JComboBox<String> paymentMethodComboBox;
    private javax.swing.JTable tblProduct;
    // End of variables declaration//GEN-END:variables
}
