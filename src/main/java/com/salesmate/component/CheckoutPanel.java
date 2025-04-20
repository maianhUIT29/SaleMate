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
            new String[]{"Tên sản phẩm", "Giá", "Số Lượng", "Thành tiền"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        
            private String formatCurrency(BigDecimal amount) {
                java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                return currencyFormatter.format(amount);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
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

        setupTable();

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
        tableModel.setRowCount(0);
        for (Product product : checkoutProducts.values()) {
            tableModel.addRow(new Object[]{
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getPrice().multiply(new java.math.BigDecimal(product.getQuantity()))
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
    }

    private void adjustColumnWidths() {
        javax.swing.table.TableColumnModel columnModel = tblProduct.getColumnModel();
        int totalParts = 10;
        int tableWidth = tblProduct.getWidth();

        columnModel.getColumn(0).setPreferredWidth((tableWidth * 5) / totalParts);
        columnModel.getColumn(1).setPreferredWidth((tableWidth * 2) / totalParts);
        columnModel.getColumn(2).setPreferredWidth((tableWidth * 1) / totalParts);
        columnModel.getColumn(3).setPreferredWidth((tableWidth * 2) / totalParts);
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
            
            showSaveSuccessDialog(outputPath, jasperPrint, invoiceId);
            
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

    private void showSaveSuccessDialog(String filePath, JasperPrint jasperPrint, int invoiceId) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Hoá đơn đã lưu");
        dialog.setModal(true);
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("<html><body><p style='font-size: 14px;'>Hoá đơn #" + invoiceId + " đã được lưu thành công tại:</p><p style='font-size: 12px; color: #666;'>" + filePath + "</p></body></html>");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        
        JButton openButton = createStyledButton("Mở file", new Color(33, 150, 243));
        JButton openFolderButton = createStyledButton("Mở thư mục", new Color(76, 175, 80));
        JButton printButton = createStyledButton("In hoá đơn", new Color(255, 152, 0));
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        
        openButton.addActionListener(e -> {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Không tìm thấy file: " + filePath,
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Không thể mở file: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        openFolderButton.addActionListener(e -> {
            try {
                File folder = new File(filePath).getParentFile();
                Desktop.getDesktop().open(folder);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Không thể mở thư mục: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        printButton.addActionListener(e -> {
            try {
                JasperPrintManager.printReport(jasperPrint, true);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Lỗi khi in: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(openButton);
        buttonPanel.add(openFolderButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
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

        // Prepare resources directory path
        String resourcesDir = System.getProperty("user.dir") + "/src/main/resources";
        String invoicesDir = resourcesDir + "/invoices";
        new File(invoicesDir).mkdirs();
        
        String fileName = "Invoice_" + invoiceId + ".pdf";
        String outputPath = invoicesDir + File.separator + fileName;

        // Button panel with 3 buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        // Print button - shows print dialog directly
        JButton printButton = createStyledButton("In hoá đơn", new Color(0, 123, 255));
        printButton.addActionListener(e -> {
            try {
                // Generate JasperPrint for preview
                JasperPrint jasperPrint = createInvoiceReport(invoiceId);
                
                // Show print preview
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

        // Save button - saves the file and shows confirmation
        JButton saveButton = createStyledButton("Lưu hoá đơn", new Color(40, 167, 69));
        saveButton.addActionListener(e -> {
            try {
                JasperPrint jasperPrint = createInvoiceReport(invoiceId);
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                
                successDialog.dispose();
                
                // Show dialog with Open and OK buttons
                Object[] options = {"Mở", "OK"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    "Hoá đơn đã được lưu thành công tại:\n" + outputPath,
                    "Lưu thành công",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]
                );
                
                if (choice == 0) { // Open button clicked
                    try {
                        Desktop.getDesktop().open(new File(outputPath));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Không thể mở file: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    successDialog,
                    "Lỗi khi lưu hoá đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Close button - simply closes the dialog
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

    /**
     * Shows a print preview dialog before printing
     * @param jasperPrint The JasperPrint object to preview
     */
    private void showPrintPreview(JasperPrint jasperPrint) {
        JDialog previewDialog = new JDialog();
        previewDialog.setTitle("Xem trước khi in");
        previewDialog.setModal(true);
        previewDialog.setSize(800, 600);
        previewDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create viewer component
        net.sf.jasperreports.swing.JRViewer viewer = new net.sf.jasperreports.swing.JRViewer(jasperPrint);
        mainPanel.add(viewer, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Print button
        JButton printButton = createStyledButton("In", new Color(0, 123, 255));
        printButton.addActionListener(e -> {
            try {
                JasperPrintManager.printReport(jasperPrint, true); // Show system print dialog
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(
                    previewDialog,
                    "Lỗi khi in: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        // Close button
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.addActionListener(e -> previewDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        previewDialog.add(mainPanel);
        
        previewDialog.setVisible(true);
    }

    private JasperPrint createInvoiceReport(int invoiceId) throws JRException {
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
        
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
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
        jasperDesign.setTopMargin(30);
        jasperDesign.setBottomMargin(30);

        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Sans_Normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName("DejaVu Sans");
        normalStyle.setFontSize(12f);
        normalStyle.setPdfFontName("Helvetica");
        jasperDesign.addStyle(normalStyle);

        JRDesignStyle titleStyle = new JRDesignStyle();
        titleStyle.setName("Sans_Bold");
        titleStyle.setFontName("DejaVu Sans");
        titleStyle.setFontSize(18f);
        titleStyle.setBold(true);
        titleStyle.setPdfFontName("Helvetica-Bold");
        jasperDesign.addStyle(titleStyle);

        String[] fieldNames = {"no", "productName", "price", "quantity", "total"};
        Class<?>[] fieldTypes = {Integer.class, String.class, String.class, Integer.class, String.class};
        
        for (int i = 0; i < fieldNames.length; i++) {
            JRDesignField field = new JRDesignField();
            field.setName(fieldNames[i]);
            field.setValueClass(fieldTypes[i]);
            jasperDesign.addField(field);
        }
        
        String[] paramNames = {"invoiceNo", "date", "cashierName", "subtotal", "tax", "totalAmount", "paymentMethod", "companyName"};
        for (String paramName : paramNames) {
            JRDesignParameter parameter = new JRDesignParameter();
            parameter.setName(paramName);
            parameter.setValueClass(String.class);
            jasperDesign.addParameter(parameter);
        }

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(100);
        
        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setX(0);
        titleText.setY(10);
        titleText.setWidth(jasperDesign.getColumnWidth());
        titleText.setHeight(30);
        titleText.setText("HÓA ĐƠN BÁN HÀNG");
        titleText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleText.setStyle(titleStyle);
        titleBand.addElement(titleText);

        JRDesignTextField invoiceNoField = new JRDesignTextField();
        invoiceNoField.setX(0);
        invoiceNoField.setY(50);
        invoiceNoField.setWidth(jasperDesign.getColumnWidth());
        invoiceNoField.setHeight(25);
        
        JRDesignExpression invoiceNoExpr = new JRDesignExpression();
        invoiceNoExpr.setText("\"Mã hóa đơn: \" + $P{invoiceNo}");
        invoiceNoField.setExpression(invoiceNoExpr);
        invoiceNoField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleBand.addElement(invoiceNoField);

        jasperDesign.setTitle(titleBand);

        JRDesignBand pageHeaderBand = new JRDesignBand();
        pageHeaderBand.setHeight(60);
        
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
            header.getLineBox().getPen().setLineWidth(1.0f);
            header.getLineBox().getPen().setLineColor(Color.BLACK);
            columnHeaderBand.addElement(header);
            xPos += columnWidths[i];
        }
        
        jasperDesign.setColumnHeader(columnHeaderBand);

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
            
            textField.getLineBox().getPen().setLineWidth(0.5f);
            textField.getLineBox().getPen().setLineColor(Color.BLACK);
            
            if (i == 0 || i == 3) {
                textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            } else if (i == 2 || i == 4) {
                textField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
            }
            
            detailBand.addElement(textField);
            xPos += columnWidths[i];
        }
        
        ((JRDesignSection)jasperDesign.getDetailSection()).addBand(detailBand);

        JRDesignBand summaryBand = new JRDesignBand();
        summaryBand.setHeight(120);
        
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
        
        JRDesignStaticText thankYouText = new JRDesignStaticText();
        thankYouText.setX(0);
        thankYouText.setY(90);
        thankYouText.setWidth(jasperDesign.getColumnWidth());
        thankYouText.setHeight(20);
        thankYouText.setText("Cảm ơn quý khách đã mua hàng! Hẹn gặp lại.");
        thankYouText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        summaryBand.addElement(thankYouText);
        
        jasperDesign.setSummary(summaryBand);

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

    public void setProductSelectionPanel(ProductSelectionPanel panel) {
        this.productSelectionPanel = panel;
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
        tblProduct.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new java.awt.Color(51, 153, 255));
                    c.setForeground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? 
                        new java.awt.Color(255, 255, 255) : 
                        new java.awt.Color(240, 240, 240));
                    c.setForeground(java.awt.Color.BLACK);
                }

                if (column == 2) {
                    ((JLabel) c).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                }
                
                return c;
            }
        });

        tblProduct.setShowGrid(true);
        tblProduct.setGridColor(new java.awt.Color(211, 211, 211));
        tblProduct.setIntercellSpacing(new java.awt.Dimension(1, 1));

        javax.swing.table.JTableHeader header = tblProduct.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                label.setBackground(new java.awt.Color(51, 153, 255));
                label.setForeground(java.awt.Color.WHITE);
                label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                label.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(211, 211, 211)),
                    javax.swing.BorderFactory.createEmptyBorder(8, 4, 8, 4)
                ));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                setOpaque(true);
                
                return label;
            }
        });
        
        header.setOpaque(false);
        header.setBackground(new java.awt.Color(51, 153, 255));
        header.setForeground(java.awt.Color.WHITE);

        header.setReorderingAllowed(false);

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 2) {
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
                        tableModel.setValueAt(product.getQuantity(), row, 2);
                    }
                }
            }
        });

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
                    
                    if (col == 2) {
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
                                
                                product.setQuantity(newQuantity);
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
                            Product product = checkoutProducts.get(getProductIdFromRow(row));
                            tableModel.setValueAt(product.getQuantity(), row, 2);
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(CheckoutPanel.this,
                                ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            Product product = checkoutProducts.get(getProductIdFromRow(row));
                            tableModel.setValueAt(product.getQuantity(), row, 2);
                        }
                    }
                }
            }
        });
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
        }

        btnPayment.setBackground(new java.awt.Color(0, 123, 255));
        btnPayment.setFont(new java.awt.Font("Arial", 1, 12));
        btnPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnPayment.setText("Thanh toán");

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
                                .addGap(252, 252, 252)
                                .addComponent(lblTotalValue, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
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
}
