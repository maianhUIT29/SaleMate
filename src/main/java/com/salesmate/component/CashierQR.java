package com.salesmate.component;

import com.salesmate.utils.QRCodeGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CashierQR extends JPanel {
    private JLabel qrLabel;
    private JTextField urlField;
    private JButton generateButton;
    
    public CashierQR() {
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        qrLabel.setPreferredSize(new Dimension(300, 300));
        
        urlField = new JTextField(30);
        urlField.setText("https://sandbox.vnpayment.vn");
        generateButton = new JButton("Tạo QR Code");
        
        setDefaultQR();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("URL/Text:"));
        topPanel.add(urlField);
        topPanel.add(generateButton);
        
        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.add(qrLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        generateButton.addActionListener(e -> generateQR());
        urlField.addActionListener(e -> generateQR());
    }
    
    private void generateQR() {
        String text = urlField.getText().trim();
        if (!text.isEmpty()) {
            try {
                BufferedImage qrImage = QRCodeGenerator.generateQRCode(text, 280, 280);
                qrLabel.setIcon(new ImageIcon(qrImage));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Không thể tạo QR Code: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setDefaultQR() {
        try {
            BufferedImage qrImage = QRCodeGenerator.generateQRCode("SalesMate POS System - VNPay Integration", 280, 280);
            qrLabel.setIcon(new ImageIcon(qrImage));
        } catch (Exception e) {
            qrLabel.setText("QR Code");
        }
    }
    
    public void setQRContent(String content) {
        urlField.setText(content);
        generateQR();
    }
}
