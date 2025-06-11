package com.salesmate.test;

import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.salesmate.component.VNPayQRDialog;

/**
 * Test integration của VNPay QR Dialog với Callback Server
 */
public class VNPayQRDialogIntegrationTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set up look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Create test frame
                JFrame testFrame = new JFrame("VNPay Integration Test");
                testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                testFrame.setSize(400, 300);
                testFrame.setLocationRelativeTo(null);
                
                // Create test button
                JButton testButton = new JButton("Test VNPay QR Dialog with Callback Server");
                testButton.setFont(testButton.getFont().deriveFont(14f));
                testButton.addActionListener(e -> {
                    testVNPayQRDialog(testFrame);
                });
                
                testFrame.add(testButton);
                testFrame.setVisible(true);
                
                System.out.println("=== VNPay QR Dialog Integration Test Started ===");
                System.out.println("Click the button to test VNPay QR Dialog with Callback Server");
                
            } catch (Exception e) {
                System.err.println("Error setting up test: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private static void testVNPayQRDialog(JFrame parent) {
        try {
            System.out.println("\n=== Testing VNPay QR Dialog with Callback Server ===");
            
            // Test parameters
            String orderId = "TEST_ORDER_" + System.currentTimeMillis();
            BigDecimal amount = new BigDecimal("100000"); // 100,000 VND
            String orderInfo = "Test payment integration with callback server";
            int invoiceId = 12345;
            
            System.out.println("Creating VNPayQRDialog with:");
            System.out.println("- Order ID: " + orderId);
            System.out.println("- Amount: " + amount);
            System.out.println("- Order Info: " + orderInfo);
            System.out.println("- Invoice ID: " + invoiceId);
            
            // Create VNPay QR Dialog
            VNPayQRDialog dialog = new VNPayQRDialog(parent, orderId, amount, orderInfo, invoiceId);
            
            // Show dialog
            dialog.setVisible(true);
            
            // Log result after dialog closes
            if (dialog.isPaymentCompleted()) {
                System.out.println("✅ Payment completed successfully!");
            } else {
                System.out.println("❌ Payment was cancelled or failed");
            }
            
        } catch (Exception e) {
            System.err.println("Error testing VNPay QR Dialog: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(parent, 
                "Error testing VNPay QR Dialog: " + e.getMessage(), 
                "Test Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
