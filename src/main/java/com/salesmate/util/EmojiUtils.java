package com.salesmate.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.Arrays;

/**
 * Utility class for emoji handling in the application
 */
public class EmojiUtils {
    
    private static boolean isEmojiSupported = false;
    private static String bestEmojiFont = "Segoe UI Emoji"; // Default for Windows
    
    static {
        initEmojiSupport();
    }
    
    /**
     * Initializes emoji support by finding the best font for emoji
     */
    private static void initEmojiSupport() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            
            // List of fonts that are known to support emoji, in order of preference
            String[] emojiFonts = {
                "Segoe UI Emoji", // Windows
                "Apple Color Emoji", // macOS
                "Noto Color Emoji", // Linux
                "Noto Emoji", // Linux
                "Segoe UI Symbol", // Windows fallback
                "Arial Unicode MS", // Common fallback
                "Symbola" // Another fallback
            };
            
            // Find the first available emoji font
            for (String emojiFont : emojiFonts) {
                if (Arrays.asList(fontNames).contains(emojiFont)) {
                    bestEmojiFont = emojiFont;
                    isEmojiSupported = true;
                    System.out.println("Using emoji font: " + bestEmojiFont);
                    return;
                }
            }
            
            // If no specific emoji font is found, use system default
            bestEmojiFont = new JLabel().getFont().getFamily();
            System.out.println("No dedicated emoji font found. Using: " + bestEmojiFont);
        } catch (Exception e) {
            System.err.println("Error initializing emoji support: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the best font for emoji display
     * 
     * @param size Font size
     * @param style Font style
     * @return Font for emoji
     */
    public static Font getEmojiFontForUI(int size, int style) {
        return new Font(bestEmojiFont, style, size);
    }
    
    /**
     * Check if emoji is supported
     * 
     * @return True if emoji is supported
     */
    public static boolean isEmojiSupported() {
        return isEmojiSupported;
    }
    
    /**
     * Tests emoji rendering capability
     */
    public static void testEmojiSupport() {
        String testEmojis = "Test Emoji: 😊 😂 🎉 🚀 🌈";
        
        JLabel testLabel = new JLabel(testEmojis);
        testLabel.setFont(getEmojiFontForUI(14, Font.PLAIN));
        
        JOptionPane.showMessageDialog(null, testLabel, "Emoji Test", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Convert Unicode codes to actual emoji characters
     * 
     * @param unicodeNotation Unicode notation like \uD83D\uDE00
     * @return The actual emoji character
     */
    public static String unicodeToEmoji(String unicodeNotation) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < unicodeNotation.length(); i++) {
            if (unicodeNotation.charAt(i) == '\\' && i + 1 < unicodeNotation.length() && unicodeNotation.charAt(i + 1) == 'u') {
                try {
                    // Parse the next 4 characters as a hex string
                    String hex = unicodeNotation.substring(i + 2, i + 6);
                    result.append((char)Integer.parseInt(hex, 16));
                    i += 5; // Skip the \u and 4 hex digits
                } catch (Exception e) {
                    // In case of error, just add the original character
                    result.append(unicodeNotation.charAt(i));
                }
            } else {
                result.append(unicodeNotation.charAt(i));
            }
        }
        
        return result.toString();
    }
}
