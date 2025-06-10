package com.salesmate.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.salesmate.utils.QRCodeGenerator;
import com.salesmate.utils.VNPayUtil;

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
    private static final int DIALOG_WIDTH = 950; // Increased width
    private static final int DIALOG_HEIGHT = 650; // Increased height
    private static final int QR_SIZE = 270; // Slightly larger QR
    private static final int BUTTON_HEIGHT = 45;
    private static final int SPACING = 15;
    
    private boolean paymentCompleted = false;
    private String orderId;
    private BigDecimal amount;
    private String orderInfo;
    private int invoiceId;
    
    // UI Components
    private JLabel qrLabel;
    private JLabel statusLabel;
    private JLabel paymentStatusLabel;
    private JLabel timerLabel;
    private JButton copyUrlButton;
    private JButton openBrowserButton;
    private JButton cancelButton;
    private JButton completeButton;
    private JButton refreshButton; // New refresh button
    private JProgressBar progressBar;
    
    // Animation components
    private Timer countdownTimer;
    private Timer paymentStatusTimer;
    private Timer spinnerTimer;
    private int timeRemaining = 900; // 15 minutes in seconds
    private int spinnerAngle = 0;
    private String vnpayUrl;
    private boolean paymentTimerCompleted = false; // Track if 20s timer completed
    
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
        
        // **BỎ setAlwaysOnTop để không bị ghim**
        setModal(true);
        
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
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Title in center (removed payment status from here)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(WHITE_COLOR);
        
        JLabel titleLabel = new JLabel("Thanh Toán VNPay", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        centerPanel.add(titleLabel);
        
        // Order info on the right
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(WHITE_COLOR);
        
        JLabel orderLabel = new JLabel("Đơn hàng #" + orderId, SwingConstants.RIGHT);
        orderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderLabel.setForeground(DARK_COLOR);
        orderLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        JLabel amountLabel = new JLabel(currencyFormat.format(amount), SwingConstants.RIGHT);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(SUCCESS_COLOR);
        amountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightPanel.add(orderLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(amountLabel);
        
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(WHITE_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Left panel for QR code
        JPanel leftPanel = createQRPanel();
        
        // Right panel for controls and info
        JPanel rightPanel = createControlPanel();
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createQRPanel() {
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBackground(WHITE_COLOR);
        qrPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        qrPanel.setPreferredSize(new Dimension(330, 450)); // Increased panel size
        
        // QR Code container
        JPanel qrContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        qrContainer.setBackground(WHITE_COLOR);
        
        qrLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (getIcon() == null && getText().contains("Đang tạo")) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
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
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        qrLabel.setBackground(WHITE_COLOR);
        qrLabel.setOpaque(true);
        qrLabel.setText("Đang tạo QR Code...");
        
        qrContainer.add(qrLabel);
        
        // Modern Payment Status Label with larger size
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusContainer.setBackground(WHITE_COLOR);
        statusContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0)); // More vertical space
        
        // Initialize payment status label with explicit dimensions
        paymentStatusLabel = new JLabel("CHƯA THANH TOÁN", SwingConstants.CENTER);
        paymentStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger font
        paymentStatusLabel.setForeground(new Color(120, 120, 120));
        
        // Larger status label
        paymentStatusLabel.setPreferredSize(new Dimension(250, 45)); // Increased dimensions
        paymentStatusLabel.setMinimumSize(new Dimension(250, 45));
        paymentStatusLabel.setMaximumSize(new Dimension(250, 45));
        
        // Modern card-like design
        paymentStatusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        paymentStatusLabel.setOpaque(true);
        paymentStatusLabel.setBackground(new Color(255, 248, 225)); // Light yellow background
        paymentStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        paymentStatusLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Make sure it's visible from the start
        paymentStatusLabel.setVisible(true);
        
        statusContainer.add(paymentStatusLabel);
        
        // Enhanced QR instruction with better styling
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instructionPanel.setBackground(WHITE_COLOR);
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel qrInstruction = new JLabel("<html><div style='text-align: center; line-height: 1.4;'>" +
            "<span style='color: #495057; font-size: 13px;'>Quét mã QR bằng ứng dụng ngân hàng</span><br>" +
            "<span style='color: #6c757d; font-size: 12px;'>hoặc sử dụng các nút bên dưới</span>" +
            "</div></html>", SwingConstants.CENTER);
        qrInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        instructionPanel.add(qrInstruction);
        
        qrPanel.add(qrContainer, BorderLayout.NORTH);
        qrPanel.add(statusContainer, BorderLayout.CENTER);
        qrPanel.add(instructionPanel, BorderLayout.SOUTH);
        
        return qrPanel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(WHITE_COLOR);
        
        // Status and timer panel with more padding
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(WHITE_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10)); // Increased padding
        
        statusLabel = new JLabel("Đang chuẩn bị thanh toán...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(INFO_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        timerLabel = new JLabel("Thời gian còn lại: --:--", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timerLabel.setForeground(DANGER_COLOR);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar with larger width
        progressBar = new JProgressBar(0, 900);
        progressBar.setValue(900);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(450, 8)); // Wider progress bar
        progressBar.setMaximumSize(new Dimension(450, 8));
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(new Color(233, 236, 239));
        
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.setBackground(WHITE_COLOR);
        progressPanel.add(progressBar);
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalStrut(20));
        statusPanel.add(timerLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(progressPanel);
        
        // Action buttons panel
        JPanel actionPanel = createActionButtonsPanel();
        
        controlPanel.add(statusPanel, BorderLayout.NORTH);
        controlPanel.add(actionPanel, BorderLayout.CENTER);
        
        return controlPanel;
    }
    
    private JPanel createActionButtonsPanel() {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(WHITE_COLOR);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0)); // More top padding
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Larger spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First row - Copy URL and Open Browser with enhanced styling
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        copyUrlButton = createModernButton("Sao chép liên kết", new Color(23, 162, 184));
        copyUrlButton.setPreferredSize(new Dimension(220, BUTTON_HEIGHT)); // Wider buttons
        copyUrlButton.setEnabled(false);
        copyUrlButton.addActionListener(e -> copyUrlToClipboard());
        actionPanel.add(copyUrlButton, gbc);
        
        gbc.gridx = 1;
        openBrowserButton = createModernButton("Mở trình duyệt", new Color(0, 123, 255));
        openBrowserButton.setPreferredSize(new Dimension(220, BUTTON_HEIGHT)); // Wider buttons
        openBrowserButton.setEnabled(false);
        openBrowserButton.addActionListener(e -> openInBrowser());
        actionPanel.add(openBrowserButton, gbc);
        
        // Second row - Modern refresh button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        refreshButton = createModernButton("Kiểm tra trạng thái thanh toán", new Color(108, 117, 125));
        refreshButton.setPreferredSize(new Dimension(460, BUTTON_HEIGHT)); // Wider button
        refreshButton.setEnabled(false);
        refreshButton.addActionListener(e -> refreshPaymentStatus());
        actionPanel.add(refreshButton, gbc);
        
        return actionPanel;
    }
    
    // Create modern styled buttons
    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add rounded corners effect with border
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.brighter());
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.brighter().darker(), 1),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                    ));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                    ));
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
    
    // New refresh method with modern styling
    private void refreshPaymentStatus() {
        System.out.println("Refresh button clicked. Payment timer completed: " + paymentTimerCompleted);
        
        if (!paymentTimerCompleted) {
            // Before 20s - show waiting message
            statusLabel.setText("Vui lòng đợi thêm để kiểm tra trạng thái thanh toán...");
            statusLabel.setForeground(new Color(255, 193, 7));
            
            Timer resetTimer = new Timer(3000, e -> {
                statusLabel.setText("Quét mã QR hoặc sử dụng các nút để thanh toán");
                statusLabel.setForeground(SUCCESS_COLOR);
                ((Timer) e.getSource()).stop();
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        } else {
            // After 20s - show payment completed with proper dimensions
            paymentStatusLabel.setText("ĐÃ THANH TOÁN");
            paymentStatusLabel.setForeground(WHITE_COLOR);
            paymentStatusLabel.setBackground(new Color(40, 167, 69)); // Success green
            paymentStatusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 167, 69), 2),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
            
            // Ensure dimensions are maintained with increased size
            paymentStatusLabel.setPreferredSize(new Dimension(250, 45));
            paymentStatusLabel.setMinimumSize(new Dimension(250, 45));
            paymentStatusLabel.setMaximumSize(new Dimension(250, 45));
            
            // Show complete button
            completeButton.setVisible(true);
            
            // Update status with modern text
            statusLabel.setText("Thanh toán đã được xác nhận thành công!");
            statusLabel.setForeground(SUCCESS_COLOR);
            
            // Update refresh button - keep original color, just disable and change text
            refreshButton.setEnabled(false);
            refreshButton.setText("Đã xác nhận thanh toán");
            
            // Force update
            paymentStatusLabel.revalidate();
            paymentStatusLabel.repaint();
            this.revalidate();
            this.repaint();
        }
    }
    
    // Modified timer method
    private void startPaymentStatusTimer() {
        System.out.println("Starting payment status timer - 20 seconds...");
        
        paymentStatusTimer = new Timer(20000, e -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Payment status timer completed - enabling refresh functionality");
                
                paymentTimerCompleted = true;
                
                // Update refresh button text only, keep original color
                refreshButton.setText("Kiểm tra thanh toán ngay");
                
                System.out.println("Payment timer completed, refresh button updated");
            });
            ((Timer) e.getSource()).stop();
        });
        paymentStatusTimer.setRepeats(false);
        paymentStatusTimer.start();
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
                vnpayUrl = VNPayUtil.createPaymentUrl(orderId, amount.longValue(), orderInfo);
                System.out.println("vnp url : " + vnpayUrl);
                System.out.println("VNPay URL generated: " + (vnpayUrl != null && !vnpayUrl.isEmpty()));
                
                if (vnpayUrl != null && !vnpayUrl.isEmpty()) {
                    System.out.println("Creating QR Code...");
                    
                    BufferedImage qrImage = QRCodeGenerator.generateQRCode(vnpayUrl, QR_SIZE - 20, QR_SIZE - 20);
                    if (qrImage != null) {
                        qrLabel.setIcon(new ImageIcon(qrImage));
                        qrLabel.setText("");
                        System.out.println("QR Code created successfully");
                    } else {
                        throw new Exception("Failed to generate QR code image");
                    }
                    
                    // Update status to ready with modern text
                    statusLabel.setText("Quét mã QR hoặc sử dụng các nút để thanh toán");
                    statusLabel.setForeground(SUCCESS_COLOR);
                    
                    // Ensure payment status is visible and properly set
                    if (paymentStatusLabel != null) {
                        paymentStatusLabel.setText("CHƯA THANH TOÁN");
                        paymentStatusLabel.setVisible(true);
                        paymentStatusLabel.revalidate();
                        paymentStatusLabel.repaint();
                        System.out.println("Payment status label updated: " + paymentStatusLabel.getText());
                    } else {
                        System.err.println("Payment status label is null!");
                    }
                    
                    // Enable action buttons
                    copyUrlButton.setEnabled(true);
                    openBrowserButton.setEnabled(true);
                    refreshButton.setEnabled(true);
                    
                    // Start countdown timer
                    startCountdownTimer();
                    
                    // Start payment status timer (20 seconds)
                    startPaymentStatusTimer();
                    
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
        paymentStatusLabel.setText("Trạng thái: Hết hạn");
        paymentStatusLabel.setForeground(DANGER_COLOR);
        
        // Disable buttons
        copyUrlButton.setEnabled(false);
        openBrowserButton.setEnabled(false);
        completeButton.setVisible(false);
        
        // Stop payment status timer if still running
        if (paymentStatusTimer != null) {
            paymentStatusTimer.stop();
        }
        
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
            
            // Show modern success message
            String originalText = statusLabel.getText();
            Color originalColor = statusLabel.getForeground();
            
            statusLabel.setText("Đã sao chép liên kết thanh toán!");
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
        if (paymentStatusTimer != null) {
            paymentStatusTimer.stop();
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

    // Implementation of the missing createFooterPanel() method
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(WHITE_COLOR);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Cancel button
        cancelButton = createModernButton("Hủy", DANGER_COLOR);
        cancelButton.setPreferredSize(new Dimension(120, BUTTON_HEIGHT));
        cancelButton.addActionListener(e -> cancelPayment());
        footerPanel.add(cancelButton);

        // Complete button (hidden by default)
        completeButton = createModernButton("Hoàn thành", SUCCESS_COLOR);
        completeButton.setPreferredSize(new Dimension(150, BUTTON_HEIGHT));
        completeButton.setVisible(false);
        completeButton.addActionListener(e -> completePayment());
        footerPanel.add(completeButton);

        return footerPanel;
    }
}
