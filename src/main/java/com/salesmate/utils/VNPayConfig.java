package com.salesmate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VNPayConfig {
    // VNPay configuration constants
    public static final String MERCHANT_ID;
    public static final String SECRET_KEY;
    public static final String API_URL;
    public static final String RETURN_URL;
    public static final String VERSION;
    public static final String COMMAND;
    public static final String ORDER_TYPE;
    public static final String LOCALE;
    public static final String CURRENCY_CODE;
    
    // Load configuration from properties file
    static {
        Properties props = new Properties();
        
        // **CẢI THIỆN**: Thử nhiều vị trí để tìm config.properties
        boolean configLoaded = false;
        String configSource = "";
        
        // 1. Thử load từ classpath (resources folder)
        try (InputStream input = VNPayConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                configLoaded = true;
                configSource = "classpath:/config.properties";
                System.out.println("Loaded config from classpath: /src/main/resources/config.properties");
            }
        } catch (IOException e) {
            System.out.println("Cannot load from classpath: " + e.getMessage());
        }
        
        // 2. Nếu không tìm thấy, thử load từ thư mục gốc dự án
        if (!configLoaded) {
            String[] possiblePaths = {
                "config.properties",                           // Thư mục gốc
                "src/main/resources/config.properties",       // Maven structure
                "../config.properties",                       // Parent directory
                "../../config.properties"                     // Grandparent directory
            };
            
            for (String path : possiblePaths) {
                File configFile = new File(path);
                if (configFile.exists() && configFile.isFile()) {
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        props.load(fis);
                        configLoaded = true;
                        configSource = configFile.getAbsolutePath();
                        System.out.println("Loaded config from file: " + configSource);
                        break;
                    } catch (IOException e) {
                        System.out.println("Cannot load from " + path + ": " + e.getMessage());
                    }
                }
            }
        }
        
        // 3. Nếu vẫn không tìm thấy, báo lỗi và sử dụng default values
        if (!configLoaded) {
            System.err.println("CẢNH BÁO: Không tìm thấy file config.properties!");
            System.err.println("Vui lòng đặt file config.properties vào một trong các vị trí:");
            System.err.println(" - src/main/resources/config.properties (khuyến nghị)");
            System.err.println(" - config.properties (thư mục gốc dự án)");
            System.err.println("🔧 Sử dụng cấu hình mặc định...");
        }
        
        // Load VNPay settings from properties với default values
        MERCHANT_ID = props.getProperty("vnpay.merchant_id", "").trim();
        SECRET_KEY = props.getProperty("vnpay.secret_key", "").trim();
        API_URL = props.getProperty("vnpay.api_url", "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html").trim();
        RETURN_URL = props.getProperty("vnpay.return_url", "http://localhost:8080/vnpay-return").trim();
        VERSION = props.getProperty("vnpay.version", "2.1.0").trim();
        COMMAND = props.getProperty("vnpay.command", "pay").trim();
        ORDER_TYPE = props.getProperty("vnpay.order_type", "other").trim();
        LOCALE = props.getProperty("vnpay.locale", "vn").trim();
        CURRENCY_CODE = props.getProperty("vnpay.currency_code", "VND").trim();
        
        // Validate required fields
        if (MERCHANT_ID.isEmpty() || SECRET_KEY.isEmpty()) {
            System.err.println("CẢNH BÁO NGHIÊM TRỌNG: VNPay MERCHANT_ID hoặc SECRET_KEY chưa được cấu hình!");
            System.err.println("MERCHANT_ID: " + (MERCHANT_ID.isEmpty() ? "CHƯA CẤU HÌNH" : "ĐÃ CẤU HÌNH"));
            System.err.println("SECRET_KEY: " + (SECRET_KEY.isEmpty() ? "CHƯA CẤU HÌNH" : "ĐÃ CẤU HÌNH"));
            System.err.println("VNPay sẽ không hoạt động nếu thiếu thông tin này!");
        }
        
        // Debug output
        System.out.println("=== VNPay Configuration Loaded ===");
        System.out.println("Config source: " + (configLoaded ? configSource : "DEFAULT VALUES"));
        System.out.println("MERCHANT_ID: " + (MERCHANT_ID.isEmpty() ? "NOT SET" : MERCHANT_ID));
        System.out.println("SECRET_KEY: " + (SECRET_KEY.isEmpty() ? "NOT SET" : "SET (hidden)"));
        System.out.println("API_URL: " + API_URL);
        System.out.println("RETURN_URL: " + RETURN_URL);
        System.out.println("Valid config: " + isConfigValid());
        System.out.println("==================================");
    }
    
    /**
     * Check if the configuration is valid
     * @return true if all required fields are set
     */
    public static boolean isConfigValid() {
        return !MERCHANT_ID.isEmpty() && !SECRET_KEY.isEmpty();
    }
    
    /**
     * In thông tin cấu hình để debug
     */
    public static void printConfig() {
        System.out.println("=== VNPay Configuration ===");
        System.out.println("MERCHANT_ID: " + MERCHANT_ID);
        System.out.println("SECRET_KEY: " + (SECRET_KEY.isEmpty() ? "NOT SET" : "*****"));
        System.out.println("API_URL: " + API_URL);
        System.out.println("RETURN_URL: " + RETURN_URL);
        System.out.println("VERSION: " + VERSION);
        System.out.println("COMMAND: " + COMMAND);
        System.out.println("ORDER_TYPE: " + ORDER_TYPE);
        System.out.println("LOCALE: " + LOCALE);
        System.out.println("CURRENCY_CODE: " + CURRENCY_CODE);
        System.out.println("Config Valid: " + isConfigValid());
        System.out.println("==========================");
    }
    
    /**
     * Tạo file config.properties mẫu
     */
    public static void createSampleConfig() {
        System.out.println("=== SAMPLE CONFIG.PROPERTIES ===");
        System.out.println("# VNPay payment gateway settings");
        System.out.println("vnpay.merchant_id=YOUR_MERCHANT_ID_HERE");
        System.out.println("vnpay.secret_key=YOUR_SECRET_KEY_HERE");
        System.out.println("vnpay.api_url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        System.out.println("vnpay.return_url=http://localhost:8080/vnpay-return");
        System.out.println("vnpay.version=2.1.0");
        System.out.println("vnpay.command=pay");
        System.out.println("vnpay.order_type=other");
        System.out.println("vnpay.locale=vn");
        System.out.println("vnpay.currency_code=VND");
        System.out.println("===============================");
    }
}
