package com.salesmate.component;

import com.salesmate.utils.QRCodeGenerator;
import com.salesmate.utils.VNPayUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class VNPayQRDialog extends JDialog {
    
    // Color scheme constants
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color INFO_COLOR = new Color(23, 162, 184);
    private static final Color LIGHT_COLOR = new Color(248, 249, 250);
    private static final Color DARK_COLOR = new Color(52, 58, 64);
    private static final Color WHITE_COLOR = new Color(255, 255, 255);
    
    // Size constants
    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 700;
    private static final int QR_SIZE = 280;
    private static final int BUTTON_HEIGHT = 45;
    private static final int SPACING = 15;
    
    private boolean paymentCompleted = false;
    private String orderId;
    private BigDecimal amount;
    private String orderInfo;
    private int invoiceId; // Thêm invoiceId
    
    // UI Components
    private JLabel qrLabel;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JButton copyUrlButton;
    private JButton openBrowserButton;
    private JButton refreshButton;
    private JButton cancelButton;
    private JButton completeButton;
    private JProgressBar progressBar;
    
    // Animation components
    private Timer countdownTimer;
    private Timer spinnerTimer;
    private int timeRemaining = 900; // 15 minutes in seconds
    private int spinnerAngle = 0;
    private String vnpayUrl;
    
    public VNPayQRDialog(Frame parent, String orderId, BigDecimal amount, String orderInfo, int invoiceId) {
        super(parent, "Thanh toán VNPay", true);
        
        System.out.println("=== VNPAY QR DIALOG CONSTRUCTOR ===");
        System.out.println("Parent: " + (parent != null ? "Found" : "NULL"));
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount: " + amount);
        System.out.println("Order Info: " + orderInfo);
        System.out.println("Invoice ID: " + invoiceId);
        
        this.orderId = orderId;
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.invoiceId = invoiceId;
        
        try {
            initializeDialog();
            System.out.println("Dialog initialized successfully");
            
            showLoadingAnimation();
            System.out.println("Loading animation started");
            
            // Create VNPay URL in background after a short delay to show loading effect
            Timer urlCreationTimer = new Timer(1500, e -> {
                System.out.println("Creating VNPay URL...");
                createVNPayURL();
                ((Timer) e.getSource()).stop();
            });
            urlCreationTimer.setRepeats(false);
            urlCreationTimer.start();
            
            System.out.println("VNPayQRDialog constructor completed successfully");
        } catch (Exception e) {
            System.err.println("Error in VNPayQRDialog constructor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create VNPayQRDialog", e);
        }
    }
    
    private void initializeDialog() {
        System.out.println("Initializing dialog components...");
        
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // **FORCE MODAL AND ALWAYS ON TOP**
        setModal(true);
        setAlwaysOnTop(true);
        
        // Set layout
        setLayout(new BorderLayout());
        
        // Create and add components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        // Apply custom styling
        getContentPane().setBackground(LIGHT_COLOR);
        
        System.out.println("Dialog components initialized");
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(WHITE_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Thanh Toán VNPay", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Order info
        JLabel orderLabel = new JLabel("Đơn hàng #" + orderId, SwingConstants.CENTER);
        orderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        orderLabel.setForeground(DARK_COLOR);
        orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Amount
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        JLabel amountLabel = new JLabel(currencyFormat.format(amount), SwingConstants.CENTER);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(SUCCESS_COLOR);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(orderLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(amountLabel);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(WHITE_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // QR Code panel
        JPanel qrPanel = createQRPanel();
        
        // Status panel
        JPanel statusPanel = createStatusPanel();
        
        // Action buttons panel
        JPanel actionPanel = createActionButtonsPanel();
        
        mainPanel.add(qrPanel, BorderLayout.NORTH);
        mainPanel.add(statusPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createQRPanel() {
        JPanel qrPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        qrPanel.setBackground(WHITE_COLOR);
        qrPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // QR Code placeholder
        qrLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // If no icon is set, draw loading animation
                if (getIcon() == null && getText().contains("Đang tạo")) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw spinning loader
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2 - 20;
                    
                    drawSpinner(g2d, centerX - 15, centerY - 15);
                }
            }
        };
        
        qrLabel.setPreferredSize(new Dimension(QR_SIZE, QR_SIZE));
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setVerticalAlignment(SwingConstants.CENTER);
        qrLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        qrLabel.setBackground(WHITE_COLOR);
        qrLabel.setOpaque(true);
        qrLabel.setText("Đang tạo QR Code...");
        
        qrPanel.add(qrLabel);
        return qrPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(WHITE_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Status label
        statusLabel = new JLabel("Đang chuẩn bị thanh toán...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(INFO_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Timer label
        timerLabel = new JLabel("Thời gian còn lại: --:--", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timerLabel.setForeground(DANGER_COLOR);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        progressBar = new JProgressBar(0, 900); // 15 minutes
        progressBar.setValue(900);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(400, 8));
        progressBar.setMaximumSize(new Dimension(400, 8));
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(new Color(233, 236, 239));
        
        // Center the progress bar
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.setBackground(WHITE_COLOR);
        progressPanel.add(progressBar);
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(timerLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(progressPanel);
        
        return statusPanel;
    }
    
    private JPanel createActionButtonsPanel() {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(WHITE_COLOR);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First row - Copy URL and Open Browser buttons
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        copyUrlButton = createStyledButton("Sao chép URL", INFO_COLOR);
        copyUrlButton.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        copyUrlButton.setEnabled(false); // Disable until URL is ready
        copyUrlButton.addActionListener(e -> copyUrlToClipboard());
        actionPanel.add(copyUrlButton, gbc);
        
        gbc.gridx = 1;
        openBrowserButton = createStyledButton("Mở trình duyệt", PRIMARY_COLOR);
        openBrowserButton.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        openBrowserButton.setEnabled(false); // Disable until URL is ready
        openBrowserButton.addActionListener(e -> openInBrowser());
        actionPanel.add(openBrowserButton, gbc);
        
        // Second row - Payment Status button (center)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        refreshButton = createStyledButton("Trạng thái thanh toán", WARNING_COLOR);
        refreshButton.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        refreshButton.setEnabled(false); // Disable until URL is ready
        refreshButton.addActionListener(e -> checkPaymentStatus());
        actionPanel.add(refreshButton, gbc);
        
        return actionPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING, 0));
        footerPanel.setBackground(LIGHT_COLOR);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Complete payment button (hidden initially)
        completeButton = createStyledButton("Hoàn thành thanh toán", SUCCESS_COLOR);
        completeButton.setPreferredSize(new Dimension(220, BUTTON_HEIGHT));
        completeButton.setVisible(false);
        completeButton.addActionListener(e -> completePayment());
        
        // Cancel button
        cancelButton = createStyledButton("Hủy thanh toán", DANGER_COLOR);
        cancelButton.setPreferredSize(new Dimension(180, BUTTON_HEIGHT));
        cancelButton.addActionListener(e -> cancelPayment());
        
        footerPanel.add(completeButton);
        footerPanel.add(cancelButton);
        
        return footerPanel;
    }
    
    private void createVNPayURL() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("=== CREATE VNPAY URL START ===");
                
                // Stop spinner animation
                if (spinnerTimer != null) {
                    spinnerTimer.stop();
                }
                
                // Show processing status
                statusLabel.setText("Đang tạo liên kết thanh toán...");
                statusLabel.setForeground(WARNING_COLOR);
                
                // **THÊM VALIDATION VÀ DEBUG**
                if (orderId == null || orderId.isEmpty()) {
                    throw new Exception("Order ID is null or empty");
                }
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new Exception("Amount is null or zero");
                }
                if (orderInfo == null || orderInfo.isEmpty()) {
                    throw new Exception("Order info is null or empty");
                }
                
                System.out.println("Creating VNPay URL with:");
                System.out.println("- Order ID: " + orderId);
                System.out.println("- Amount: " + amount.longValue());
                System.out.println("- Order Info: " + orderInfo);
                
                // Generate VNPay URL
                vnpayUrl = VNPayUtil.createPaymentUrl(orderId, amount.longValue(), orderInfo) ;
                System.out.println("vnp url : " +  vnpayUrl);
                System.out.println("VNPay URL generated: " + (vnpayUrl != null && !vnpayUrl.isEmpty()));
                
                if (vnpayUrl != null && !vnpayUrl.isEmpty()) {
                    System.out.println("Creating QR Code...");
                    
                    // **🎯 QUAN TRỌNG: TẠO QR CODE TỪ URL VNPAY** 
                    BufferedImage qrImage = QRCodeGenerator.generateQRCode(vnpayUrl, QR_SIZE - 20, QR_SIZE - 20);
                    if (qrImage != null) {
                        qrLabel.setIcon(new ImageIcon(qrImage));
                        qrLabel.setText("");
                        System.out.println("QR Code created successfully");
                    } else {
                        throw new Exception("Failed to generate QR code image");
                    }
                    
                    // Update status to ready
                    statusLabel.setText("Quét mã QR hoặc nhấn nút để thanh toán");
                    statusLabel.setForeground(SUCCESS_COLOR);
                    
                    // Enable action buttons
                    copyUrlButton.setEnabled(true);
                    openBrowserButton.setEnabled(true);
                    refreshButton.setEnabled(true);
                    
                    // Show complete button for testing
                    completeButton.setVisible(true);
                    
                    // Start countdown timer
                    startCountdownTimer();
                    
                    System.out.println("VNPay URL creation completed successfully");
                    
                } else {
                    throw new Exception("VNPay URL is null or empty");
                }
            } catch (Exception e) {
                System.err.println("Error in createVNPayURL: " + e.getMessage());
                e.printStackTrace();
                showError("Lỗi tạo mã QR: " + e.getMessage());
            }
        });
    }
    
    private void showLoadingAnimation() {
        qrLabel.setText("Đang tạo QR Code...");
        qrLabel.setIcon(null);
        
        // Start spinner animation
        spinnerTimer = new Timer(100, e -> {
            spinnerAngle = (spinnerAngle + 30) % 360;
            qrLabel.repaint();
        });
        spinnerTimer.start();
    }
    
    private void drawSpinner(Graphics2D g2d, int x, int y) {
        int size = 30;
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        for (int i = 0; i < 12; i++) {
            float alpha = (float) (1.0 - (i * 0.08));
            g2d.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 
                                 (int) (alpha * 255)));
            
            double angle = Math.toRadians(spinnerAngle + i * 30);
            int x1 = x + size / 2 + (int) (size / 3 * Math.cos(angle));
            int y1 = y + size / 2 + (int) (size / 3 * Math.sin(angle));
            int x2 = x + size / 2 + (int) (size / 2.2 * Math.cos(angle));
            int y2 = y + size / 2 + (int) (size / 2.2 * Math.sin(angle));
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            if (timeRemaining <= 0) {
                ((Timer) e.getSource()).stop();
                showTimeout();
            } else {
                updateTimerDisplay();
                progressBar.setValue(timeRemaining);
            }
        });
        countdownTimer.start();
    }
    
    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Thời gian còn lại: %02d:%02d", minutes, seconds));
    }
    
    private void showTimeout() {
        statusLabel.setText("⏰ Hết thời gian thanh toán");
        statusLabel.setForeground(DANGER_COLOR);
        
        // Disable buttons
        copyUrlButton.setEnabled(false);
        openBrowserButton.setEnabled(false);
        refreshButton.setEnabled(false);
        completeButton.setVisible(false);
        
        cancelButton.setText("Đóng");
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(DANGER_COLOR);
        
        qrLabel.setIcon(null);
        qrLabel.setText("<html><div style='text-align: center;'>Lỗi tạo QR Code<br>" + message + "</div></html>");
        
        if (spinnerTimer != null) {
            spinnerTimer.stop();
        }
    }
    
    private void copyUrlToClipboard() {
        if (vnpayUrl != null) {
            StringSelection selection = new StringSelection(vnpayUrl);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            
            // Show temporary success message
            String originalText = statusLabel.getText();
            Color originalColor = statusLabel.getForeground();
            
            statusLabel.setText("📋 Đã sao chép URL!");
            statusLabel.setForeground(SUCCESS_COLOR);
            
            Timer resetTimer = new Timer(2000, e -> {
                statusLabel.setText(originalText);
                statusLabel.setForeground(originalColor);
                ((Timer) e.getSource()).stop();
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }
    
    private void openInBrowser() {
        if (vnpayUrl != null) {
            try {
                Desktop.getDesktop().browse(java.net.URI.create(vnpayUrl));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Không thể mở trình duyệt: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void checkPaymentStatus() {
        // Simulate payment status check
        JOptionPane.showMessageDialog(this, 
            "Tính năng kiểm tra trạng thái thanh toán sẽ được cập nhật trong phiên bản sau.",
            "Trạng thái thanh toán", JOptionPane.INFORMATION_MESSAGE);
    }
      private void completePayment() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        paymentCompleted = true;
        
        statusLabel.setText("🎉 Thanh toán thành công!");
        statusLabel.setForeground(SUCCESS_COLOR);
        timerLabel.setText("Hoàn thành");
        progressBar.setValue(progressBar.getMaximum());
        progressBar.setForeground(SUCCESS_COLOR);
        
        // Disable all buttons except close
        copyUrlButton.setEnabled(false);
        openBrowserButton.setEnabled(false);
        refreshButton.setEnabled(false);
        completeButton.setEnabled(false);
        cancelButton.setText("Đóng");

        // Hiển thị toast thông báo thành công
        Toast.showToast((JFrame)getOwner(), "Thanh toán đơn hàng #" + orderId + " thành công!", "success");
        
        // Auto close after 2 seconds
        Timer closeTimer = new Timer(2000, e -> dispose());
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    private void cancelPayment() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        if (spinnerTimer != null) {
            spinnerTimer.stop();
        }
        
        paymentCompleted = false;
        dispose();
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });
        
        return button;
    }
    
    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }
    
    public void setPaymentCompleted(boolean completed) {
        this.paymentCompleted = completed;
        if (completed) {
            completePayment();
        }
    }

    private void showSuccessDialog(int invoiceId) {
        // Create a new dialog for success message
        JDialog successDialog = new JDialog(this, "Thanh toán thành công", true);
        successDialog.setSize(400, 300);
        successDialog.setLocationRelativeTo(this);
        successDialog.setResizable(false);
        
        // Panel for content
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Success icon
        JLabel iconLabel = new JLabel(new ImageIcon("path/to/success_icon.png"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        
        // Success message
        JLabel messageLabel = new JLabel("Thanh toán của bạn đã được thực hiện thành công!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(SUCCESS_COLOR);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);
        
        // Invoice ID
        JLabel invoiceLabel = new JLabel("Mã hóa đơn: " + invoiceId, SwingConstants.CENTER);
        invoiceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        invoiceLabel.setForeground(DARK_COLOR);
        invoiceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(invoiceLabel);
        
        // Close button
        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> successDialog.dispose());
        panel.add(closeButton);
        
        successDialog.add(panel);
        successDialog.setVisible(true);
    }
}
