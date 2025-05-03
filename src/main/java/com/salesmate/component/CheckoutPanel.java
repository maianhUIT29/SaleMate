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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;

import net.sf.jasperreports.swing.JRViewer;

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
            new String[]{"Tên sản phẩm", "Giá gốc", "Giá b", "SL", "Thành tiền"}
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
        try {
            System.out.println("Creating invoice report for ID: " + invoiceId);
            System.out.println("Total products in checkout: " + checkoutProducts.size());
            
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
                InputStream reportStream = null;
                try {
                    reportStream = getClass().getResourceAsStream("/reports/invoice_template.jrxml");
                    if (reportStream != null) {
                        System.out.println("Found report template in resources");
                        jasperReport = JasperCompileManager.compileReport(reportStream);
                    } else {
                        System.out.println("Could not find report template in resources, trying file system...");
                        String templatePath = System.getProperty("user.dir") + "/src/main/resources/reports/invoice_template.jrxml";
                        File templateFile = new File(templatePath);
                        
                        if (templateFile.exists()) {
                            System.out.println("Found template at: " + templatePath);
                            jasperReport = JasperCompileManager.compileReport(templatePath);
                        } else {
                            System.out.println("Template not found in file system, using dynamic report");
                            jasperReport = createDynamicReport();
                        }
                    }
                } finally {
                    if (reportStream != null) {
                        try {
                            reportStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error compiling report: " + e.getMessage());
                e.printStackTrace();
                jasperReport = createDynamicReport();
            }
            
            return JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(data));
        } catch (Exception e) {
            System.err.println("Error creating invoice report: " + e.getMessage());
            e.printStackTrace();
            throw new JRException("Failed to create invoice report", e);
        }
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

    private void showSuccessDialog(int invoiceId) {
        JDialog successDialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(SwingUtilities.getWindowAncestor(this)), 
            "Thanh toán thành công", 
            true
        );
        
        successDialog.setSize(500, 400);
        successDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        successDialog.setLayout(new BorderLayout());
        
        String invoiceNumber = generateInvoiceNumber(invoiceId);
        String cashierName = SessionManager.getInstance().getLoggedInUser().getUsername();
        String paymentMethod = paymentMethodComboBox.getSelectedItem().toString();
        String totalAmountStr = lblTotalValue.getText();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        BufferedImage successIcon = createSuccessIcon(100);
        JLabel iconLabel = new JLabel(new ImageIcon(successIcon));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        
        JLabel successLabel = new JLabel("Thanh toán thành công!");
        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        successLabel.setForeground(new Color(40, 167, 69));
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(iconLabel, BorderLayout.CENTER);
        
        JPanel detailsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(245, 245, 245));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(new Color(230, 230, 230));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                g2d.dispose();
            }
        };
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        detailsPanel.setLayout(new GridLayout(5, 2, 10, 15));
        
        addStyledDetailRow(detailsPanel, "Mã hóa đơn:", invoiceNumber);
        addStyledDetailRow(detailsPanel, "Người lập hóa đơn:", cashierName);
        addStyledDetailRow(detailsPanel, "Ngày lập:", currentDate);
        addStyledDetailRow(detailsPanel, "Phương thức thanh toán:", paymentMethod);
        addStyledDetailRow(detailsPanel, "Tổng tiền:", totalAmountStr);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        
        JButton printButton = createStyledButton("Xem và In", new Color(0, 123, 255));
        printButton.addActionListener(e -> {
            try {
                JasperPrint jasperPrint = createInvoiceReport(invoiceId);
                showPrintPreview(jasperPrint, invoiceId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    successDialog,
                    "Lỗi khi tạo hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.addActionListener(e -> successDialog.dispose());
        
        buttonsPanel.add(printButton);
        buttonsPanel.add(closeButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(successLabel, BorderLayout.CENTER);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        successDialog.add(contentPanel);
        successDialog.pack();
        successDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        successDialog.setVisible(true);
    }

    private BufferedImage createSuccessIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(40, 167, 69));
        g2d.fillOval(0, 0, size, size);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(size/10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int[] xPoints = {size/4, size/2, size*3/4};
        int[] yPoints = {size/2, size*3/4, size/3};
        
        g2d.drawPolyline(xPoints, yPoints, 3);
        
        g2d.dispose();
        return image;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(darkenColor(bgColor, 0.85f));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private Color darkenColor(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2] * factor);
    }

    private void addStyledDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(60, 60, 60));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(50, 50, 50));
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    private void showPrintPreview(JasperPrint jasperPrint, int invoiceId) {
        JDialog previewDialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(SwingUtilities.getWindowAncestor(this)), 
            "Xem trước hóa đơn", 
            true
        );
        
        previewDialog.setSize(800, 600);
        previewDialog.setLocationRelativeTo(null);
        previewDialog.setLayout(new BorderLayout());
        
        try {
            JRViewer viewer = new JRViewer(jasperPrint);
            viewer.setOpaque(true);
            viewer.setVisible(true);
            // Set zoom level to fit the page instead of using setFitPage
            viewer.setZoomRatio(0.75f);
            
            previewDialog.add(viewer, BorderLayout.CENTER);
            
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
            buttonsPanel.setBackground(Color.WHITE);
            
            JButton printButton = createStyledButton("In hóa đơn", new Color(0, 123, 255));
            printButton.addActionListener(e -> {
                try {
                    String resourcesDir = System.getProperty("user.dir") + "/src/main/resources";
                    String invoicesDir = resourcesDir + "/invoices";
                    File invoicesDirFile = new File(invoicesDir);
                    
                    if (!invoicesDirFile.exists()) {
                        if (!invoicesDirFile.mkdirs()) {
                            throw new IOException("Không thể tạo thư mục " + invoicesDir);
                        }
                    }
                    
                    String fileName = "Invoice_" + invoiceId + ".pdf";
                    String outputPath = invoicesDir + File.separator + fileName;
                    
                    try {
                        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                        
                        JOptionPane.showMessageDialog(
                            previewDialog,
                            "Hóa đơn đã được lưu tại: " + outputPath,
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        try {
                            File pdfFile = new File(outputPath);
                            if (pdfFile.exists()) {
                                Desktop.getDesktop().open(pdfFile);
                            }
                        } catch (Exception ex) {
                            System.err.println("Could not open PDF file: " + ex.getMessage());
                        }
                    } catch (JRException jrEx) {
                        throw new IOException("Lỗi khi xuất hóa đơn: " + jrEx.getMessage(), jrEx);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        previewDialog,
                        "Lỗi khi lưu hóa đơn: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            
            JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
            closeButton.addActionListener(e -> previewDialog.dispose());
            
            buttonsPanel.add(printButton);
            buttonsPanel.add(closeButton);
            previewDialog.add(buttonsPanel, BorderLayout.SOUTH);
            
            previewDialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Lỗi khi hiển thị xem trước: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
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
