package com.salesmate.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class QRCodeGenerator {
    
    public static BufferedImage generateQRCode(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (bitMatrix.get(j, i)) {
                    graphics.fillRect(j, i, 1, 1);
                }
            }
        }
        
        return image;
    }
    
    public static ImageIcon createQRImageIcon(String text, int size) {
        try {
            BufferedImage qrImage = generateQRCode(text, size, size);
            return new ImageIcon(qrImage);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
