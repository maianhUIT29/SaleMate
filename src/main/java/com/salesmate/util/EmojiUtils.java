package com.salesmate.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for finding and using emoji-compatible fonts
 */
public class EmojiUtils {
    
    // Danh sách các font có hỗ trợ emoji tốt theo thứ tự ưu tiên
    private static final String[] EMOJI_FONT_NAMES = {
        "Segoe UI Emoji",   // Windows
        "Apple Color Emoji", // macOS
        "Noto Color Emoji",  // Linux
        "Noto Emoji",
        "Segoe UI Symbol",
        "Symbola",
        "DejaVu Sans"
    };
    
    /**
     * Tìm font phù hợp nhất cho việc hiển thị emoji trên UI
     */
    public static Font getEmojiFontForUI(float size, int style) {
        Font bestFont = null;
        
        try {
            // Danh sách các font có sẵn trong hệ thống
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            List<Font> availableFonts = new ArrayList<>(Arrays.asList(ge.getAllFonts()));
            
            // Thêm các font có sẵn trong JRE
            availableFonts.add(new Font(Font.SANS_SERIF, style, (int)size));
            availableFonts.add(new Font(Font.DIALOG, style, (int)size));
            
            // Tìm font emoji phù hợp đầu tiên
            for (String fontName : EMOJI_FONT_NAMES) {
                for (Font font : availableFonts) {
                    String name = font.getFontName().toLowerCase();
                    if (name.contains(fontName.toLowerCase())) {
                        bestFont = font.deriveFont(style, size);
                        System.out.println("Found emoji font: " + font.getFontName());
                        break;
                    }
                }
                if (bestFont != null) break;
            }
            
            // Nếu không tìm thấy font emoji nào, thử đăng ký font từ hệ thống
            if (bestFont == null) {
                for (String fontName : EMOJI_FONT_NAMES) {
                    try {
                        bestFont = new Font(fontName, style, (int)size);
                        if (!bestFont.getFontName().equals(fontName)) {
                            bestFont = null; // Font không khớp với tên, JRE đã thay thế bằng font fallback
                        } else {
                            System.out.println("Registered emoji font: " + fontName);
                            break;
                        }
                    } catch (Exception e) {
                        // Font không tồn tại, tiếp tục tìm
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding emoji font: " + e.getMessage());
        }
        
        // Fallback về font Dialog nếu không tìm thấy font emoji nào
        if (bestFont == null) {
            bestFont = new Font(Font.DIALOG, style, (int)size);
            System.out.println("Using fallback font: " + bestFont.getFontName());
        }
        
        return bestFont;
    }
    
    /**
     * Kiểm tra xem một ký tự có phải là emoji hay không
     */
    public static boolean isEmoji(char c) {
        return c == 0x1F60A || // 😊 - smiling face with smiling eyes
               c == 0x1F603 || // 😃 - smiling face with open mouth
               c == 0x1F609 || // 😉 - winking face
               c == 0x1F614 || // 😔 - pensive face
               c == 0x1F61B || // 😛 - face with stuck-out tongue
               c == 0x1F62E || // 😮 - face with open mouth
               (c >= 0x1F600 && c <= 0x1F64F) || // Emoticons
               (c >= 0x1F300 && c <= 0x1F5FF) || // Misc Symbols and Pictographs
               (c >= 0x1F680 && c <= 0x1F6FF) || // Transport and Map
               (c >= 0x2600 && c <= 0x26FF) ||   // Misc symbols
               (c >= 0x2700 && c <= 0x27BF) ||   // Dingbats
               (c >= 0xFE00 && c <= 0xFE0F) ||   // Variation Selectors
               (c >= 0x1F900 && c <= 0x1F9FF) || // Supplemental Symbols and Pictographs
               (c >= 0x1F1E6 && c <= 0x1F1FF);   // Flags
    }
    
    /**
     * Kiểm tra xem một chuỗi có chứa emoji không
     */
    public static boolean containsEmoji(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (isEmoji(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
