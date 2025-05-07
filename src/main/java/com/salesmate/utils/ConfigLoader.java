package com.salesmate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Singleton class to load configuration from properties files
 * with proper UTF-8 support for Vietnamese characters
 */
public class ConfigLoader {
    private static ConfigLoader instance;
    private Properties properties;
    private final String CONFIG_FILE = "config.properties";
    
    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }
    
    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }
    
    private void loadProperties() {
        try {
            // Look for config file in different locations
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                // Try in application directory
                configFile = new File(System.getProperty("user.dir") + File.separator + CONFIG_FILE);
            }
            
            if (configFile.exists()) {
                // Use InputStreamReader with UTF-8 encoding to properly read Vietnamese characters
                try (InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                    properties.load(reader);
                    System.out.println("Configuration loaded successfully from: " + configFile.getAbsolutePath());
                    
                    // Debug: Print loaded values to verify encoding
                    if (properties.containsKey("chatbot.welcome_message")) {
                        String welcome = properties.getProperty("chatbot.welcome_message");
                        System.out.println("Loaded welcome message: " + welcome);
                    }
                }
            } else {
                System.out.println("Configuration file not found. Using default values.");
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }
    
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            // Custom default values for the chatbot
            if ("chatbot.name".equals(key)) {
                return "An An";
            }
            if ("chatbot.welcome_message".equals(key)) {
                return "Xin chao! Toi la An An, chu cua hang SalesMate! Toi hoi ngang nguoc va thich pha tro mot chut. Hoi gi thi hoi nhanh di, toi con phai di ban hang nua day! 😒";
            }
            return defaultValue;
        }
        
        // Try to fix encoding issues with Vietnamese text
        try {
            // Check if the text might be wrongly encoded
            if (containsEncodingIssues(value)) {
                // Try ISO-8859-1 to UTF-8 conversion (common fix)
                byte[] bytes = value.getBytes("ISO-8859-1");
                String utf8Value = new String(bytes, StandardCharsets.UTF_8);
                
                // If the conversion seems to have worked
                if (!containsEncodingIssues(utf8Value)) {
                    return utf8Value;
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Error fixing encoding for property " + key + ": " + e.getMessage());
        }
        
        return value;
    }
    
    private boolean containsEncodingIssues(String text) {
        // Check for common signs of encoding issues (like weird characters)
        return text.contains("Ä") || text.contains("Ã") || text.contains("Æ") || 
               text.contains("â€") || text.contains("Ã¡") || text.contains("Ã ");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
