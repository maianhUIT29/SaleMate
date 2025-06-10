package com.salesmate.component;

import com.salesmate.utils.QRCodeGenerator;
import com.salesmate.utils.VNPayUtil;
import com.salesmate.utils.PaymentResult;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

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
    private Timer loadingAnimationTimer;
    private Timer spinnerTimer;
    private int timeRemaining = 900; // 15 minutes in seconds
    private int spinnerAngle = 0;
    private String vnpayUrl;
    
    public VNPayQRDialog(Frame parent, String orderId, BigDecimal amount, String orderInfo) {
        super(parent, "Thanh toán VNPay", true);
        this.orderId = orderId;
        this.amount = amount;
        this.orderInfo = orderInfo;
        
        initializeDialog();
        showLoadingAnimation();
        
        // Create VNPay URL in background after a short delay to show loading effect
        Timer urlCreationTimer = new Timer(1500, e -> {
            createVNPayURL();
            ((Timer) e.getSource()).stop();
        });
        urlCreationTimer.setRepeats(false);
        urlCreationTimer.start();
    }
    
    private void initializeDialog() {
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Set layout
        setLayout(new BorderLayout());
        
        // Create and add components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        // Apply custom styling
        getContentPane().setBackground(LIGHT_COLOR);
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
        actionPanel.add(copyUrlButton, gbc);
        
        gbc.gridx = 1;
        openBrowserButton = createStyledButton("Mở trình duyệt", PRIMARY_COLOR);
        openBrowserButton.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        openBrowserButton.setEnabled(false); // Disable until URL is ready
        actionPanel.add(openBrowserButton, gbc);
        
        // Second row - Refresh button (center)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        refreshButton = createStyledButton("Kiểm tra thanh toán", WARNING_COLOR);
        refreshButton.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        refreshButton.setEnabled(false); // Disable until URL is ready
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
        
        // Cancel button
        cancelButton = createStyledButton("Hủy thanh toán", DANGER_COLOR);
        cancelButton.setPreferredSize(new Dimension(180, BUTTON_HEIGHT));
        
        footerPanel.add(completeButton);
        footerPanel.add(cancelButton);
        
        return footerPanel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = backgroundColor.darker();
                } else if (getModel().isRollover() && isEnabled()) {
                    bgColor = backgroundColor.brighter();
                } else {
                    bgColor = isEnabled() ? backgroundColor : new Color(200, 200, 200);
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE_COLOR);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add action listeners
        if (text.contains("Sao chép")) {
            button.addActionListener(e -> copyUrlToClipboard());
        } else if (text.contains("Mở trình duyệt")) {
            button.addActionListener(e -> openInBrowser());
        } else if (text.contains("Kiểm tra")) {
            button.addActionListener(e -> refreshPaymentStatus());
        } else if (text.contains("Hoàn thành")) {
            button.addActionListener(e -> completePayment());
        } else if (text.contains("Hủy")) {
            button.addActionListener(e -> cancelPayment());
        }
        
        return button;
    }
    
    private void showLoadingAnimation() {
        // Set initial loading state
        qrLabel.setText("<html><div style='text-align: center;'>" +
                       "<div style='font-size: 16px; color: #6c757d; margin-top: 60px;'>Đang tạo mã QR thanh toán...</div>" +
                       "<div style='font-size: 14px; color: #6c757d; margin-top: 15px;'>Vui lòng đợi trong giây lát</div>" +
                       "</html>");
        
        // Start spinner animation
        spinnerTimer = new Timer(50, e -> {
            spinnerAngle = (spinnerAngle + 6) % 360;
            qrLabel.repaint();
        });
        spinnerTimer.start();
        
        // Update status
        statusLabel.setText("Đang kết nối với VNPay...");
        statusLabel.setForeground(INFO_COLOR);
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
    
    private void createVNPayURL() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Stop spinner animation
                if (spinnerTimer != null) {
                    spinnerTimer.stop();
                }
                
                // Show processing status
                statusLabel.setText("Đang tạo liên kết thanh toán...");
                statusLabel.setForeground(WARNING_COLOR);
                
                // Generate VNPay URL
                vnpayUrl = VNPayUtil.createPaymentUrl(orderId, amount.longValue(), orderInfo);
                
                if (vnpayUrl != null && !vnpayUrl.isEmpty()) {
                    // Generate QR code
                    BufferedImage qrImage = QRCodeGenerator.generateQRCode(vnpayUrl, QR_SIZE - 20, QR_SIZE - 20);
                    qrLabel.setIcon(new ImageIcon(qrImage));
                    qrLabel.setText("");
                    
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
                    
                    // Show success animation
                    showReadyAnimation();
                    
                } else {
                    throw new Exception("Không thể tạo URL VNPay");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Lỗi tạo mã QR: " + e.getMessage());
            }
        });
    }
    
    private void showReadyAnimation() {
        // Briefly highlight the QR code area to show it's ready
        Timer highlightTimer = new Timer(100, null);
        final int[] flashCount = {0};
        
        highlightTimer.addActionListener(e -> {
            flashCount[0]++;
            if (flashCount[0] % 2 == 0) {
                qrLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS_COLOR, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            } else {
                qrLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
            
            if (flashCount[0] >= 6) {
                highlightTimer.stop();
                qrLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        highlightTimer.start();
    }
    
    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                updateTimerDisplay();
                
                if (timeRemaining <= 0) {
                    countdownTimer.stop();
                    showTimeout();
                }
            }
        });
        countdownTimer.start();
    }
    
    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Thời gian còn lại: %02d:%02d", minutes, seconds));
        
        // Update progress bar
        progressBar.setValue(timeRemaining);
        
        // Change color based on remaining time
        if (timeRemaining <= 120) { // Last 2 minutes
            timerLabel.setForeground(DANGER_COLOR);
            progressBar.setForeground(DANGER_COLOR);
        } else if (timeRemaining <= 300) { // Last 5 minutes
            timerLabel.setForeground(WARNING_COLOR.darker());
            progressBar.setForeground(WARNING_COLOR);
        }
    }
    
    private void copyUrlToClipboard() {
        try {
            if (vnpayUrl != null) {
                StringSelection selection = new StringSelection(vnpayUrl);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                
                // Show temporary success message with animation
                String originalText = copyUrlButton.getText();
                copyUrlButton.setText("Đã sao chép!");
                copyUrlButton.setEnabled(false);
                
                Timer resetTimer = new Timer(2000, e -> {
                    copyUrlButton.setText(originalText);
                    copyUrlButton.setEnabled(true);
                });
                resetTimer.setRepeats(false);
                resetTimer.start();
            }
        } catch (Exception e) {
            showError("Không thể sao chép URL: " + e.getMessage());
        }
    }
    
    private void openInBrowser() {
        try {
            if (vnpayUrl != null) {
                Desktop.getDesktop().browse(java.net.URI.create(vnpayUrl));
                
                // Show feedback animation
                String originalText = openBrowserButton.getText();
                openBrowserButton.setText("🌐 Đã mở!");
                
                Timer resetTimer = new Timer(2000, e -> {
                    openBrowserButton.setText(originalText);
                });
                resetTimer.setRepeats(false);
                resetTimer.start();
            }
        } catch (Exception e) {
            showError("Không thể mở trình duyệt: " + e.getMessage());
        }
    }
    
    private void refreshPaymentStatus() {
        // Show checking animation
        statusLabel.setText("🔄 Đang kiểm tra trạng thái thanh toán...");
        statusLabel.setForeground(INFO_COLOR);
        
        String originalText = refreshButton.getText();
        refreshButton.setEnabled(false);
        refreshButton.setText("🔄 Đang kiểm tra...");
        
        // Simulate checking process
        Timer checkTimer = new Timer(1500, e -> {
            statusLabel.setText("Quét mã QR hoặc nhấn nút để thanh toán");
            statusLabel.setForeground(SUCCESS_COLOR);
            refreshButton.setText(originalText);
            refreshButton.setEnabled(true);
        });
        checkTimer.setRepeats(false);
        checkTimer.start();
    }
    
    private void completePayment() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        paymentCompleted = true;
        
        // Show success animation
        showSuccessAnimation();
        
        // Update UI to show success
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
        
        // Auto close after 3 seconds
        Timer closeTimer = new Timer(3000, e -> dispose());
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    private void showSuccessAnimation() {
        // Flash the QR area with success color
        Timer successFlash = new Timer(150, null);
        final int[] flashCount = {0};
        
        successFlash.addActionListener(e -> {
            flashCount[0]++;
            if (flashCount[0] % 2 == 0) {
                qrLabel.setBackground(SUCCESS_COLOR.brighter());
            } else {
                qrLabel.setBackground(WHITE_COLOR);
            }
            
            if (flashCount[0] >= 8) {
                successFlash.stop();
                qrLabel.setBackground(WHITE_COLOR);
            }
        });
        
        successFlash.start();
    }
    
    private void cancelPayment() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn hủy thanh toán?",
            "Xác nhận hủy",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            paymentCompleted = false;
            dispose();
        } else if (countdownTimer != null && timeRemaining > 0) {
            // Resume timer if cancelled the cancellation
            countdownTimer.start();
        }
    }
    
    private void showTimeout() {
        statusLabel.setText("⏰ Hết thời gian thanh toán");
        statusLabel.setForeground(DANGER_COLOR);
        timerLabel.setText("Đã hết thời gian");
        
        // Disable all action buttons
        copyUrlButton.setEnabled(false);
        openBrowserButton.setEnabled(false);
        refreshButton.setEnabled(false);
        completeButton.setEnabled(false);
        
        // Show timeout animation
        Timer timeoutFlash = new Timer(200, null);
        final int[] flashCount = {0};
        
        timeoutFlash.addActionListener(e -> {
            flashCount[0]++;
            if (flashCount[0] % 2 == 0) {
                qrLabel.setBackground(DANGER_COLOR.brighter());
            } else {
                qrLabel.setBackground(WHITE_COLOR);
            }
            
            if (flashCount[0] >= 6) {
                timeoutFlash.stop();
                qrLabel.setBackground(WHITE_COLOR);
            }
        });
        
        timeoutFlash.start();
        
        JOptionPane.showMessageDialog(
            this,
            "Thời gian thanh toán đã hết.\nVui lòng thực hiện lại giao dịch.",
            "Hết thời gian",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(DANGER_COLOR);
        
        // Stop all timers
        if (spinnerTimer != null) spinnerTimer.stop();
        if (countdownTimer != null) countdownTimer.stop();
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
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
    
    // Thêm method để xử lý callback từ VNPay
    public void handleVNPayCallback(String queryString) {
        try {
            // Lấy parameters từ query string
            Map<String, String> vnpayData = VNPayUtil.getVNPayResponseParameters(queryString);
            
            // Validate chữ ký
            boolean valid = VNPayUtil.validateSignature(vnpayData);
            
            if (!valid) {
                // Chữ ký sai
                VNPayUtil.showError("Sai chữ ký VNPAY. Vui lòng kiểm tra lại cấu hình.");
                setPaymentCompleted(false);
            } else {
                // Xử lý kết quả thanh toán
                PaymentResult result = VNPayUtil.processPaymentResult(vnpayData);
                
                if (result.isSuccess()) {
                    VNPayUtil.showSuccess("Thanh toán thành công!\nMã giao dịch: " + result.getTransactionId());
                    setPaymentCompleted(true);
                } else {
                    VNPayUtil.showError("Thanh toán thất bại: " + result.getMessage());
                    setPaymentCompleted(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            VNPayUtil.showError("Lỗi xử lý callback VNPay: " + e.getMessage());
            setPaymentCompleted(false);
        }
    }
    
    @Override
    public void dispose() {
        // Stop all timers before disposing
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        if (spinnerTimer != null) {
            spinnerTimer.stop();
        }
        super.dispose();
    }
}
