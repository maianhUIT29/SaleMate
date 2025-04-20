package com.salesmate.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class BarcodeGenerator {

    public static String generateBarcode(String barcode, int productId) throws WriterException, IOException {
        // Thiết lập thư mục lưu trữ
        String resourcesDir = System.getProperty("user.dir") + "/src/main/resources";
        String barcodesDir = resourcesDir + "/barcodes";
        new File(barcodesDir).mkdirs();
        
        String fileName = "product_" + productId + ".png";
        String filePath = barcodesDir + File.separator + fileName;
        
        // Cài đặt cho mã vạch
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 2); // Khoảng trắng xung quanh mã vạch
        
        // Tạo barcode (Code 128 là định dạng phổ biến cho bán lẻ)
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(barcode, BarcodeFormat.CODE_128, 300, 100, hints);
        
        // Lưu mã vạch
        Path path = Paths.get(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        
        return "/barcodes/" + fileName;
    }
}