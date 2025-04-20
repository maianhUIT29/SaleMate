package com.salesmate.utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BarcodeScanner extends JPanel {
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private ScheduledExecutorService executor;
    private Consumer<Product> onProductScanned;
    private ProductController productController;
    private JLabel statusLabel;
    private final int SCAN_INTERVAL = 500; // ms
    private String lastScannedCode = "";
    private long lastScanTime = 0;
    private final long SCAN_COOLDOWN = 2000; // Prevent duplicate scans within 2 seconds

    static {
        // Cài đặt driver cho IP Camera
        Webcam.setDriver(new IpCamDriver());
    }

    public BarcodeScanner(Consumer<Product> onProductScanned) {
        this.onProductScanned = onProductScanned;
        this.productController = new ProductController();
        setLayout(new BorderLayout());
        
        // Load webcam host from config.properties
        Properties properties = new Properties();
        String defaultHost = "http://192.168.1.x:8080/video";
        String webcamHost;
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            webcamHost = properties.getProperty("webcam.host", defaultHost);
        } catch (IOException e) {
            webcamHost = defaultHost;
            e.printStackTrace();
        }
        
        // Panel điều khiển
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton connectButton = new JButton("Kết nối Camera");
        JTextField ipTextField = new JTextField(webcamHost, 20); // Use webcamHost as default value
        statusLabel = new JLabel("Trạng thái: Chưa kết nối");
        statusLabel.setForeground(Color.RED);
        
        connectButton.addActionListener(e -> {
            if (webcam != null && webcam.isOpen()) {
                stopScanner();
                statusLabel.setText("Trạng thái: Đã ngắt kết nối");
                statusLabel.setForeground(Color.RED);
                connectButton.setText("Kết nối Camera");
                return;
            }
            
            try {
                String ipAddress = ipTextField.getText().trim();
                if (!ipAddress.startsWith("http://")) {
                    ipAddress = "http://" + ipAddress;
                }
                if (!ipAddress.endsWith("/video")) {
                    ipAddress = ipAddress + "/video";
                }
                
                initCamera(ipAddress);
                connectButton.setText("Ngắt kết nối");
                statusLabel.setText("Trạng thái: Đang quét mã vạch...");
                statusLabel.setForeground(new Color(0, 128, 0));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Không thể kết nối đến camera: " + ex.getMessage(), 
                    "Lỗi kết nối", 
                    JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Trạng thái: Lỗi kết nối");
                statusLabel.setForeground(Color.RED);
            }
        });
        
        controlPanel.add(new JLabel("Địa chỉ IP Camera:"));
        controlPanel.add(ipTextField);
        controlPanel.add(connectButton);
        
        add(controlPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
        
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setPreferredSize(new java.awt.Dimension(640, 480)); // Specify java.awt.Dimension
        placeholderPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        placeholderPanel.add(new JLabel("Kết nối camera để bắt đầu quét mã vạch"));
        add(placeholderPanel, BorderLayout.CENTER);
    }
    
    private void initCamera(String ipCameraAddress) throws MalformedURLException {
        // Đăng ký IP Camera
        IpCamDeviceRegistry.register("IP Camera", ipCameraAddress, IpCamMode.PUSH);
        
        // Lấy webcam (IP Camera)
        webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();
        
        // Tạo webcam panel
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setDisplayDebugInfo(true);
        webcamPanel.setImageSizeDisplayed(true);
        webcamPanel.setMirrored(false);
        
        // Thêm webcam panel vào giao diện
        remove(getComponent(1)); // Xóa placeholder
        add(webcamPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        // Bắt đầu quét mã vạch
        startScanner();
    }
    
    private void startScanner() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::scanBarcode, 0, SCAN_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    private void stopScanner() {
        if (executor != null) {
            executor.shutdown();
        }
        
        if (webcam != null && webcam.isOpen()) {
            webcamPanel.stop();
            webcam.close();
            
            // Khôi phục placeholder
            remove(webcamPanel);
            
            JPanel placeholderPanel = new JPanel();
            placeholderPanel.setPreferredSize(new java.awt.Dimension(640, 480)); // Specify java.awt.Dimension
            placeholderPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            placeholderPanel.add(new JLabel("Kết nối camera để bắt đầu quét mã vạch"));
            add(placeholderPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }
    
    private void scanBarcode() {
        try {
            BufferedImage image = webcam.getImage();
            
            if (image == null) {
                return;
            }
            
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            MultiFormatReader reader = new MultiFormatReader();
            Result result = null;
            
            try {
                result = reader.decode(bitmap);
            } catch (NotFoundException e) {
                // Không tìm thấy mã vạch nào, bỏ qua
                return;
            }
            
            if (result != null) {
                String scannedCode = result.getText();
                long currentTime = System.currentTimeMillis();
                
                // Kiểm tra để tránh quét trùng lặp
                if (!scannedCode.equals(lastScannedCode) || 
                    (currentTime - lastScanTime) > SCAN_COOLDOWN) {
                    
                    lastScannedCode = scannedCode;
                    lastScanTime = currentTime;
                    
                    // Tìm sản phẩm theo mã vạch
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Đang tìm sản phẩm với mã: " + scannedCode);
                        Product product = productController.findProductByBarcode(scannedCode);
                        
                        if (product != null) {
                            statusLabel.setText("Đã quét: " + product.getProductName() + " - " + scannedCode);
                            onProductScanned.accept(product);
                        } else {
                            statusLabel.setText("Không tìm thấy sản phẩm với mã: " + scannedCode);
                        }
                    });
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Lỗi khi quét: " + e.getMessage());
        }
    }

    public void closeScanner() {
        stopScanner();
    }
}