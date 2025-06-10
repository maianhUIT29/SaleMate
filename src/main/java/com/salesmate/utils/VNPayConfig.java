package com.salesmate.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VNPayConfig {
    private static Properties props;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        props = new Properties();
        try (InputStream input = VNPayConfig.class.getResourceAsStream("/config.properties")) {
            if (input != null) {
                props.load(input);
                System.out.println("VNPay config loaded successfully");
            } else {
                System.err.println("Config file not found");
            }
        } catch (IOException e) {
            System.err.println("Cannot load VNPay config: " + e.getMessage());
        }
    }
    
    public static String getMerchantId() {
        return props.getProperty("vnpay.merchant_id");
    }
    
    public static String getSecretKey() {
        return props.getProperty("vnpay.secret_key");
    }
    
    public static String getApiUrl() {
        return props.getProperty("vnpay.api_url");
    }
    
    public static String getReturnUrl() {
        return props.getProperty("vnpay.return_url");
    }
    
    public static String getVersion() {
        return props.getProperty("vnpay.version");
    }
    
    public static String getCommand() {
        return props.getProperty("vnpay.command");
    }
    
    public static String getOrderType() {
        return props.getProperty("vnpay.order_type");
    }
    
    public static String getLocale() {
        return props.getProperty("vnpay.locale");
    }
    
    public static String getCurrencyCode() {
        return props.getProperty("vnpay.currency_code");
    }
    
    public static boolean isConfigValid() {
        return getMerchantId() != null && !getMerchantId().isEmpty() &&
               getSecretKey() != null && !getSecretKey().isEmpty() &&
               getApiUrl() != null && !getApiUrl().isEmpty();
    }
}
