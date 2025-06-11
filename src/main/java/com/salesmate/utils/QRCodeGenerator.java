package com.salesmate.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator {
    
    /**
     * Tạo QR Code từ text với kích thước tùy chỉnh
     */
    public static BufferedImage generateQRCode(String text, int width, int height) throws WriterException {
        if (text == null || text.trim().isEmpty()) {
            return createErrorQRCode(width, height, "Nội dung QR rỗng");
        }
        
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            // Cấu hình hints cho QR code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            // Tạo BitMatrix
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            
            // Chuyển đổi thành BufferedImage với màu sắc đẹp hơn
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = qrImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền trắng
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            
            // Vẽ QR code với màu xanh dương đậm
            graphics.setColor(new Color(0, 51, 102)); // Dark blue
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }
            
            graphics.dispose();
            return qrImage;
            
        } catch (Exception e) {
            System.err.println("Lỗi tạo QR Code: " + e.getMessage());
            e.printStackTrace();
            return createErrorQRCode(width, height, "Lỗi tạo QR: " + e.getMessage());
        }
    }
    
    /**
     * Tạo QR Code với kích thước mặc định
     */
    public static BufferedImage generateQRCode(String text) throws WriterException {
        return generateQRCode(text, 300, 300);
    }
    
    /**
     * Tạo QR Code lỗi khi không thể tạo QR code thực
     */
    private static BufferedImage createErrorQRCode(int width, int height, String errorMessage) {
        BufferedImage errorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = errorImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Nền đỏ nhạt
        g2d.setColor(new Color(255, 240, 240));
        g2d.fillRect(0, 0, width, height);
        
        // Viền đỏ
        g2d.setColor(new Color(220, 53, 69));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(2, 2, width - 4, height - 4);
        
        // Icon X lớn ở giữa
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int centerX = width / 2;
        int centerY = height / 2;
        int size = Math.min(width, height) / 4;
        
        g2d.drawLine(centerX - size, centerY - size, centerX + size, centerY + size);
        g2d.drawLine(centerX + size, centerY - size, centerX - size, centerY + size);
        
        // Text lỗi
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(new Color(150, 40, 50));
        
        FontMetrics fm = g2d.getFontMetrics();
        String[] lines = {"QR Code", "Không khả dụng"};
        
        int y = centerY + size + 20;
        for (String line : lines) {
            int textWidth = fm.stringWidth(line);
            int x = (width - textWidth) / 2;
            g2d.drawString(line, x, y);
            y += fm.getHeight();
        }
        
        g2d.dispose();
        return errorImage;
    }
    
    /**
     * Kiểm tra xem có thể tạo QR code từ text hay không
     */
    public static boolean canGenerateQRCode(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 100, 100);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
