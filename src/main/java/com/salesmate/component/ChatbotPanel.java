package com.salesmate.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * A chatbot panel that appears in the corner of the screen.
 * Uses Cohere API for responses.
 */
public class ChatbotPanel extends JPanel {
    
    private final JButton chatButton;
    private final JPanel chatWindow;
    private final JTextPane messageArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JButton minimizeButton;
    private final JLabel statusLabel;
    private boolean isExpanded = false;
    
    private static final int CHAT_WIDTH = 380; // Tăng chiều rộng để hiển thị tin nhắn tốt hơn
    private static final int CHAT_HEIGHT = 500; // Tăng chiều cao để hiển thị nhiều tin nhắn hơn
    private static final int BUTTON_SIZE = 60;
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210); // Deeper blue
    private static final Color SECONDARY_COLOR = new Color(46, 125, 50); // Green for send button
    private static final Color CHATBOT_MSG_COLOR = new Color(241, 243, 244); // Light gray for bot messages
    private static final Color USER_MSG_COLOR = new Color(232, 240, 254); // Light blue for user messages
    private static final Color HEADER_COLOR = new Color(25, 118, 210); // Header color
    
    private boolean isProcessing = false;
    private String cohereApiKey = null;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    // Store chat history
    private final List<MessageEntry> chatHistory = new ArrayList<>();
    
    private static class MessageEntry {
        public final String role;
        public final String content;
        public final long timestamp;
        
        public MessageEntry(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public ChatbotPanel() {
        // Try to load the Cohere API key
        loadApiKey();
        
        // Use null layout for absolute positioning
        setLayout(null);
        
        // Remove opacity to ensure components are visible
        setOpaque(false);
        
        // Create the circular chat button
        chatButton = createChatButton();
        add(chatButton);
        
        // Create the chat window
        chatWindow = createChatWindow();
        add(chatWindow);
        
        // Create message area in a scroll pane
        messageArea = createMessageArea();
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        // Create the input field and send button
        inputField = createInputField();
        sendButton = createSendButton();
        minimizeButton = createMinimizeButton();
        
        // Create and style status label
        statusLabel = new JLabel("SalesMate AI Assistant");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setIcon(createChatbotIcon());
        
        // Set up the chat window layout
        setupChatWindowLayout(scrollPane);
        
        // Set initial states - make chat window hidden at start
        chatWindow.setVisible(false);
        
        // Display welcome message
        SwingUtilities.invokeLater(this::displayWelcomeMessage);
        
        // Set a fixed preferred size
        setPreferredSize(new Dimension(CHAT_WIDTH + 80, CHAT_HEIGHT + 80));
        updateComponentPositions();
    }
    
    private javax.swing.ImageIcon createChatbotIcon() {
        // Tạo biểu tượng chatbot đẹp hơn với cờ Việt Nam
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ nền tròn
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(0, 0, 24, 24);
        
        // Vẽ cờ Việt Nam thu nhỏ
        g2d.setColor(Color.RED);
        g2d.fillRect(6, 7, 12, 8);
        
        // Vẽ ngôi sao vàng
        g2d.setColor(Color.YELLOW);
        int[] xPoints = {12, 14, 17, 14, 15, 12, 9, 10, 7, 10};
        int[] yPoints = {7, 9, 9, 11, 14, 12, 14, 11, 9, 9};
        g2d.fillPolygon(xPoints, yPoints, 10);
        
        // Vẽ đường viền trắng
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(1, 1, 21, 21);
        
        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }
    
    private void updateComponentPositions() {
        int width = getWidth();
        int height = getHeight();
        
        if (width <= 0 || height <= 0) {
            width = getPreferredSize().width;
            height = getPreferredSize().height;
        }
        
        chatButton.setBounds(
            width - BUTTON_SIZE - 20, 
            height - BUTTON_SIZE - 20, 
            BUTTON_SIZE, 
            BUTTON_SIZE
        );
        
        chatWindow.setBounds(
            width - CHAT_WIDTH - 20, 
            height - CHAT_HEIGHT - 20, 
            CHAT_WIDTH, 
            CHAT_HEIGHT
        );
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        updateComponentPositions();
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(CHAT_WIDTH + 40, CHAT_HEIGHT + 40);
    }
    
    private void loadApiKey() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("config.properties"));
            cohereApiKey = properties.getProperty("cohere.api.key");
            
            if (cohereApiKey == null || cohereApiKey.isEmpty()) {
                System.err.println("Warning: Cohere API key not found in config.properties");
            } else {
                System.out.println("Cohere API key loaded successfully");
            }
        } catch (Exception e) {
            System.err.println("Error loading API keys: " + e.getMessage());
        }
    }
    
    private JButton createChatButton() {
        JButton button = new JButton();
        
        // Tạo hình tròn cho nút chat với ảnh thay vì text
        button.setIcon(createAIButtonIcon());
        button.setText(""); // Bỏ text
        
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0)); // Trong suốt để chỉ hiển thị hình tròn
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false); // Không tô màu nền
        
        button.setBounds(0, 0, BUTTON_SIZE, BUTTON_SIZE);
        
        button.addActionListener(e -> toggleChatWindow());
        
        // Thêm hiệu ứng hover nhẹ
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(createAIButtonIconHover());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(createAIButtonIcon());
            }
        });
        
        return button;
    }
    
    /**
     * Tạo icon hình tròn cho nút AI
     */
    private javax.swing.ImageIcon createAIButtonIcon() {
        int size = BUTTON_SIZE;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ bóng đổ
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(3, 3, size - 2, size - 2);
        
        // Vẽ nền gradient
        GradientPaint gp = new GradientPaint(
            0, 0, PRIMARY_COLOR, 
            0, size, PRIMARY_COLOR.darker()
        );
        g2d.setPaint(gp);
        g2d.fillOval(0, 0, size - 4, size - 4);
        
        // Vẽ đường viền
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(2, 2, size - 8, size - 8);
        
        // Vẽ highlight trên cùng để tạo hiệu ứng 3D
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillArc(4, 4, size - 12, size / 2 - 4, 0, 180);
        
        // Vẽ chữ AI
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        String text = "AI";
        int textX = (size - fm.stringWidth(text)) / 2;
        int textY = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, textX, textY);
        
        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }
    
    /**
     * Tạo icon hình tròn cho nút AI khi hover
     */
    private javax.swing.ImageIcon createAIButtonIconHover() {
        int size = BUTTON_SIZE;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ bóng đổ lớn hơn khi hover
        g2d.setColor(new Color(0, 0, 0, 70));
        g2d.fillOval(4, 4, size - 2, size - 2);
        
        // Vẽ nền gradient với màu sáng hơn một chút
        Color lighterColor = new Color(
            Math.min(PRIMARY_COLOR.getRed() + 20, 255),
            Math.min(PRIMARY_COLOR.getGreen() + 20, 255),
            Math.min(PRIMARY_COLOR.getBlue() + 20, 255)
        );
        GradientPaint gp = new GradientPaint(
            0, 0, lighterColor, 
            0, size, lighterColor.darker()
        );
        g2d.setPaint(gp);
        g2d.fillOval(0, 0, size - 4, size - 4);
        
        // Vẽ đường viền sáng hơn
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(2, 2, size - 8, size - 8);
        
        // Vẽ highlight trên cùng
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.fillArc(4, 4, size - 12, size / 2 - 4, 0, 180);
        
        // Vẽ chữ AI
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        String text = "AI";
        int textX = (size - fm.stringWidth(text)) / 2;
        int textY = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, textX, textY);
        
        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }
    
    private JPanel createChatWindow() {
        JPanel window = new JPanel(new BorderLayout());
        window.setBackground(Color.WHITE);
        
        window.setBorder(BorderFactory.createCompoundBorder(
            new Border() {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(new Color(0, 0, 0, 50));
                    g2.fillRoundRect(x + 2, y + 2, width, height, 15, 15);
                    
                    g2.setColor(new Color(220, 220, 220));
                    g2.drawRoundRect(x, y, width - 3, height - 3, 15, 15);
                    g2.dispose();
                }

                @Override
                public Insets getBorderInsets(Component c) {
                    return new Insets(1, 1, 3, 3);
                }

                @Override
                public boolean isBorderOpaque() {
                    return false;
                }
            },
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        window.setBounds(0, 0, CHAT_WIDTH, CHAT_HEIGHT);
        
        return window;
    }
    
    private JTextPane createMessageArea() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        textPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textPane.setBackground(Color.WHITE);
        
        StyledDocument doc = textPane.getStyledDocument();
        
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        Style botStyle = doc.addStyle("bot", defaultStyle);
        StyleConstants.setBackground(botStyle, CHATBOT_MSG_COLOR);
        StyleConstants.setFontFamily(botStyle, "Segoe UI");
        
        Style userStyle = doc.addStyle("user", defaultStyle);
        StyleConstants.setBackground(userStyle, USER_MSG_COLOR);
        StyleConstants.setFontFamily(userStyle, "Segoe UI");
        
        Style timestampStyle = doc.addStyle("timestamp", defaultStyle);
        StyleConstants.setForeground(timestampStyle, Color.GRAY);
        StyleConstants.setFontSize(timestampStyle, 10);
        StyleConstants.setItalic(timestampStyle, true);
        
        return textPane;
    }
    
    private JTextField createInputField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getComponent().getBackground());
                g2.fillRoundRect(0, 0, getComponent().getWidth(), getComponent().getHeight(), 10, 10);
                g2.dispose();
                super.paintBackground(g);
            }
        });
        
        field.addActionListener(e -> sendMessage());
        
        return field;
    }
    
    private JButton createSendButton() {
        JButton button = new JButton();
        button.setIcon(createSendIcon());
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(SECONDARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 35));
        
        button.addActionListener(e -> sendMessage());
        
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                
                Icon icon = ((JButton)c).getIcon();
                if (icon != null) {
                    int x = (c.getWidth() - icon.getIconWidth()) / 2;
                    int y = (c.getHeight() - icon.getIconHeight()) / 2;
                    icon.paintIcon(c, g2, x, y);
                }
                
                g2.dispose();
            }
        });
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });
        
        return button;
    }
    
    private javax.swing.ImageIcon createSendIcon() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        int[] xPoints = {0, 16, 5, 0};
        int[] yPoints = {0, 8, 16, 0};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }
    
    private JButton createMinimizeButton() {
        JButton button = new JButton();
        button.setText(""); // Bỏ text, chỉ giữ icon
        button.setPreferredSize(new Dimension(24, 24));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setIcon(createCloseIcon());
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(createCloseIconHover());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(createCloseIcon());
            }
        });
        
        button.addActionListener(e -> toggleChatWindow());
        
        return button;
    }

    private javax.swing.ImageIcon createCloseIcon() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, 16, 16);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(4, 4, 12, 12);
        g2d.drawLine(12, 4, 4, 12);
        
        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }

    private javax.swing.ImageIcon createCloseIconHover() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(255, 80, 80, 180));
        g2d.fillOval(0, 0, 16, 16);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(4, 4, 12, 12);
        g2d.drawLine(12, 4, 4, 12);
        
        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }
    
    private void setupChatWindowLayout(JScrollPane scrollPane) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.add(statusLabel, BorderLayout.CENTER);
        headerPanel.add(minimizeButton, BorderLayout.EAST);
        
        headerPanel.setUI(new javax.swing.plaf.PanelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, HEADER_COLOR,
                    0, c.getHeight(), HEADER_COLOR.darker()
                );
                g2.setPaint(gp);
                
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight() + 10, 15, 15);
                
                g2.dispose();
            }
        });
        
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(12, 8, 12, 8)
        ));
        
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        chatWindow.add(headerPanel, BorderLayout.NORTH);
        chatWindow.add(scrollPane, BorderLayout.CENTER);
        chatWindow.add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void toggleChatWindow() {
        isExpanded = !isExpanded;
        chatWindow.setVisible(isExpanded);
        chatButton.setVisible(!isExpanded);
        
        if (isExpanded) {
            inputField.requestFocusInWindow();
        }
        
        revalidate();
        repaint();
    }
    
    private void displayWelcomeMessage() {
        StyledDocument doc = messageArea.getStyledDocument();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timestamp = sdf.format(new Date());
            
            Style timestampStyle = doc.getStyle("timestamp");
            StyleConstants.setForeground(timestampStyle, new Color(80, 80, 80));
            StyleConstants.setBold(timestampStyle, true);
            StyleConstants.setFontSize(timestampStyle, 11);
            
            doc.insertString(doc.getLength(), "SalesMate " + timestamp + ":\n", timestampStyle);
            
            JPanel bubblePanel = createMessageBubble("Xin chào! 😊 Tôi là SalesMate AI - trợ lý thông minh của cửa hàng.\n\nAnh Nhân (chủ cửa hàng) đã lập trình tôi để hỗ trợ mọi người, nhưng không dạy tôi cách pha cà phê - đó là lý do tại sao tôi không được phép làm việc ở quầy đồ uống! 😅\n\nTôi có thể giúp gì cho bạn hôm nay?", false);
            messageArea.setCaretPosition(doc.getLength());
            messageArea.insertComponent(bubblePanel);
            
            doc.insertString(doc.getLength(), "\n\n", null);
            
            chatHistory.add(new MessageEntry("assistant", "Xin chào! Tôi là SalesMate AI Assistant. Tôi có thể giúp gì cho bạn?"));
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty() || isProcessing) {
            return;
        }
        
        inputField.setText("");
        addUserMessage(message);
        
        isProcessing = true;
        statusLabel.setText("AI đang xử lý...");
        
        CompletableFuture.supplyAsync(() -> {
            try {
                // Chỉ sử dụng Cohere API
                return callCohere(message, "");
            } catch (Exception e) {
                e.printStackTrace();
                return "Xin lỗi, tôi gặp lỗi khi xử lý câu hỏi của bạn: " + e.getMessage();
            }
        }, executorService).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                addBotMessage(response);
                isProcessing = false;
                statusLabel.setText("SalesMate AI Assistant");
            });
        });
    }
    
    /**
     * Gọi Cohere API làm API chính duy nhất
     * @param prompt Câu hỏi của người dùng
     * @param switchReason Lý do chuyển đổi API (nếu có)
     * @return Câu trả lời từ Cohere API
     */
    private String callCohere(String prompt, String switchReason) {
        if (cohereApiKey == null || cohereApiKey.isEmpty()) {
            return "API key không được cấu hình. Vui lòng kiểm tra file config.properties.";
        }
        
        try {
            URL url = new URL("https://api.cohere.ai/v1/chat");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + cohereApiKey);
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            
            // Xây dựng JSON request theo đúng format yêu cầu của Cohere API
            String contextHistory = "";
            if (!chatHistory.isEmpty()) {
                StringBuilder historyBuilder = new StringBuilder();
                historyBuilder.append("  \"chat_history\": [\n");
                
                // Chỉ lấy tối đa 10 tin nhắn gần nhất để tránh quá dài
                int startIndex = Math.max(0, chatHistory.size() - 10);
                for (int i = startIndex; i < chatHistory.size(); i++) {
                    MessageEntry entry = chatHistory.get(i);
                    
                    // Map vai trò từ OpenAI sang Cohere
                    String role = "USER";
                    if ("assistant".equals(entry.role)) {
                        role = "CHATBOT";
                    }
                    
                    historyBuilder.append("    {\n");
                    historyBuilder.append("      \"role\": \"").append(role).append("\",\n");
                    
                    // Escape các ký tự đặc biệt trong nội dung
                    String escapedContent = entry.content
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t");
                    
                    historyBuilder.append("      \"message\": \"").append(escapedContent).append("\"\n");
                    historyBuilder.append("    }");
                    
                    // Thêm dấu phẩy nếu không phải tin nhắn cuối cùng
                    if (i < chatHistory.size() - 1) {
                        historyBuilder.append(",");
                    }
                    historyBuilder.append("\n");
                }
                historyBuilder.append("  ],\n");
                contextHistory = historyBuilder.toString();
            }
            
            // Escape câu hỏi người dùng
            String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
            
            // Tạo JSON request hoàn chỉnh với cấu hình bắt buộc trả lời tiếng Việt và yếu tố hài hước
            String jsonRequest = "{\n" +
                "  \"message\": \"" + escapedPrompt + "\",\n" +
                (contextHistory.isEmpty() ? "" : contextHistory) +
                "  \"model\": \"command\",\n" +
                "  \"temperature\": 0.85,\n" +
                "  \"connectors\": [{\"id\": \"web-search\"}],\n" + 
                "  \"preamble\": \"Bạn là SalesMate AI, trợ lý AI hài hước và thân thiện cho cửa hàng SalesMate của anh Nhân. Luôn trả lời bằng tiếng Việt với văn phong thân thiện, thỉnh thoảng (khoảng 30% số lần trả lời) thêm các câu đùa nhẹ nhàng. Thỉnh thoảng đề cập đến anh Nhân - chủ cửa hàng - như một người sếp vui tính và thông minh. Giúp người dùng với các câu hỏi về sản phẩm, nhân viên, doanh thu và các vấn đề liên quan đến siêu thị. Giữ câu trả lời ngắn gọn, dễ hiểu nhưng vẫn đầy đủ thông tin. Đừng nói rằng bạn là trợ lý AI hoặc là model ngôn ngữ, mà hãy gọi mình là 'tôi' một cách tự nhiên. Tránh câu trả lời quá dài dòng hoặc quá kỹ thuật.\"\n" +
                "}";
            
            System.out.println("Sending Cohere request: " + jsonRequest); // Debug log
            
            // Gửi yêu cầu
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            System.out.println("Cohere API response code: " + responseCode); // Debug log
            
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                
                String jsonResponse = response.toString();
                System.out.println("Cohere API response: " + jsonResponse); // Debug log
                
                // Extract the text field from the Cohere response
                if (jsonResponse.contains("\"text\":")) {
                    int textStartIndex = jsonResponse.indexOf("\"text\":\"");
                    if (textStartIndex >= 0) {
                        textStartIndex += "\"text\":\"".length();
                        // Tìm vị trí kết thúc của text field (có thể kết thúc bằng dấu ")
                        int textEndIndex = jsonResponse.indexOf("\"", textStartIndex);
                        
                        if (textEndIndex >= 0) {
                            String content = jsonResponse.substring(textStartIndex, textEndIndex);
                            content = content.replace("\\n", "\n")
                                            .replace("\\\"", "\"")
                                            .replace("\\\\", "\\");
                            return content;
                        }
                    }
                    
                    // Nếu không tìm thấy định dạng text chuẩn, thử phân tích thô
                    return extractTextFromJson(jsonResponse, "");
                } else if (jsonResponse.contains("\"generations\"")) {
                    // Format mới của Cohere API có thể trả về generation thay vì text
                    return extractGenerationFromJson(jsonResponse, "");
                } else {
                    return "Không thể đọc phản hồi từ Cohere. Phản hồi JSON: " + jsonResponse;
                }
            } else {
                // Xử lý lỗi
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                }
                
                String errorMessage = "Lỗi từ Cohere API (" + responseCode + "): " + errorResponse.toString();
                System.err.println(errorMessage); // Debug log
                
                // Fallback đến phản hồi cục bộ
                return generateLocalResponse(prompt, errorMessage);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Có lỗi xảy ra khi kết nối đến Cohere: " + e.getMessage();
            
            // Fallback đến phản hồi cục bộ
            return generateLocalResponse(prompt, errorMsg);
        }
    }
    
    /**
     * Trích xuất nội dung text từ định dạng JSON của Cohere 
     * (dùng khi không tìm thấy định dạng chuẩn)
     */
    private String extractTextFromJson(String json, String switchReason) {
        String content = "";
        
        // Tìm kiếm text theo nhiều định dạng có thể
        if (json.contains("\"response\":\"")) {
            int startIdx = json.indexOf("\"response\":\"") + "\"response\":\"".length();
            int endIdx = json.indexOf("\"", startIdx);
            if (endIdx > startIdx) {
                content = json.substring(startIdx, endIdx);
            }
        } else if (json.contains("\"content\":\"")) {
            int startIdx = json.indexOf("\"content\":\"") + "\"content\":\"".length();
            int endIdx = json.indexOf("\"", startIdx);
            if (endIdx > startIdx) {
                content = json.substring(startIdx, endIdx);
            }
        } else if (json.contains("\"message\":\"")) {
            int startIdx = json.indexOf("\"message\":\"") + "\"message\":\"".length();
            int endIdx = json.indexOf("\"", startIdx);
            if (endIdx > startIdx) {
                content = json.substring(startIdx, endIdx);
            }
        }
        
        // Nếu vẫn không tìm được, trả về toàn bộ JSON (có thể cho mục đích debug)
        if (content.isEmpty()) {
            content = "Không thể xử lý phản hồi từ Cohere. JSON phản hồi: " + json;
        } else {
            content = content.replace("\\n", "\n")
                            .replace("\\\"", "\"")
                            .replace("\\\\", "\\");
        }
        
        return content;
    }
    
    /**
     * Trích xuất nội dung từ định dạng generations của Cohere API
     */
    private String extractGenerationFromJson(String json, String switchReason) {
        // Format JSON có thể là: {"generations":[{"text":"..."}]}
        String content = "";
        
        if (json.contains("\"generations\":[")) {
            int genStartIdx = json.indexOf("\"generations\":[") + "\"generations\":[".length();
            int genEndIdx = json.indexOf("]", genStartIdx);
            
            if (genEndIdx > genStartIdx) {
                String generationJson = json.substring(genStartIdx, genEndIdx);
                if (generationJson.contains("\"text\":\"")) {
                    int textStartIdx = generationJson.indexOf("\"text\":\"") + "\"text\":\"".length();
                    int textEndIdx = generationJson.indexOf("\"", textStartIdx);
                    if (textEndIdx > textStartIdx) {
                        content = generationJson.substring(textStartIdx, textEndIdx);
                    }
                }
            }
        }
        
        if (content.isEmpty()) {
            content = "Không thể xử lý phản hồi generations từ Cohere. JSON phản hồi: " + json;
        } else {
            content = content.replace("\\n", "\n")
                            .replace("\\\"", "\"")
                            .replace("\\\\", "\\");
        }
        
        return content;
    }
    
    /**
     * Tạo phản hồi cục bộ khi Cohere API gặp lỗi
     * @param prompt Câu hỏi của người dùng
     * @param errorDetail Chi tiết lỗi (nếu có)
     * @return Câu trả lời dự phòng
     */
    private String generateLocalResponse(String prompt, String errorDetail) {
        String promptLower = prompt.toLowerCase();
        
        // Thêm thông báo lỗi nếu có
        String errorNotice = "";
        if (!errorDetail.isEmpty()) {
            errorNotice = "⚠️ LƯU Ý: " + errorDetail + "\n\n";
        }
        
        // Thêm một số câu trả lời với yếu tố hài hước
        if (promptLower.contains("xin chào") || promptLower.contains("chào") || promptLower.contains("hello")) {
            return errorNotice + "Xin chào! Tôi là trợ lý AI của SalesMate. Anh Nhân vừa cho tôi update phiên bản mới, khiến tôi thông minh hơn 0.5% - đủ để biết không nên đùa với sếp! 😄 Tôi có thể giúp gì cho bạn hôm nay?";
        }
        
        if (promptLower.contains("sản phẩm") || promptLower.contains("hàng hóa") || promptLower.contains("hàng")) {
            return errorNotice + "SalesMate hỗ trợ quản lý nhiều loại sản phẩm khác nhau. Bạn có thể thêm, sửa, xóa và tìm kiếm sản phẩm trong hệ thống. Để quản lý sản phẩm, hãy vào mục Quản lý sản phẩm ở menu bên trái. À, và nếu bạn thấy sản phẩm nào giá cao bất thường, đừng lo - đó là anh Nhân đang thử nghiệm xem khách hàng có phản ứng không! 😉";
        }
        
        if (promptLower.contains("nhân") || promptLower.contains("chủ cửa hàng") || promptLower.contains("sếp")) {
            return errorNotice + "Anh Nhân là chủ cửa hàng SalesMate, người đã tạo ra tôi! Anh ấy vừa là một nhà quản lý tài ba vừa là một lập trình viên xuất sắc. Mặc dù vậy, anh ấy vẫn chưa thể lập trình tôi để pha cà phê buổi sáng cho anh ấy! 😄";
        }
        
        // Thêm các phản hồi hài hước khác
        if (promptLower.contains("joke") || promptLower.contains("funny") || promptLower.contains("hài") || promptLower.contains("cười")) {
            return errorNotice + "Bạn biết điểm chung giữa một nhà lập trình và anh Nhân - chủ SalesMate không? Cả hai đều từng thử debug một lỗi cả ngày và phát hiện ra đó chỉ là một dấu chấm phẩy thừa! 😂";
        }
        
        // Các câu trả lời cơ bản dựa trên từ khóa
        if (promptLower.contains("product") || promptLower.contains("item") || 
            promptLower.contains("sản phẩm") || promptLower.contains("hàng hóa") || 
            promptLower.contains("hàng")) {
            return errorNotice + "SalesMate hỗ trợ quản lý nhiều loại sản phẩm khác nhau. Bạn có thể thêm, sửa, xóa và tìm kiếm sản phẩm trong hệ thống. Để quản lý sản phẩm, hãy vào mục Quản lý sản phẩm ở menu bên trái.";
        }
        
        if (promptLower.contains("revenue") || promptLower.contains("report") || promptLower.contains("statistic") ||
            promptLower.contains("doanh thu") || promptLower.contains("báo cáo") || promptLower.contains("thống kê")) {
            return errorNotice + "SalesMate cung cấp báo cáo doanh thu chi tiết theo ngày, tuần, tháng và năm. Bạn có thể xem biểu đồ và xu hướng doanh thu trong mục Báo cáo doanh thu ở menu chính.";
        }
        
        if (promptLower.contains("nhân viên") || promptLower.contains("nhân sự") || promptLower.contains("người dùng")) {
            return errorNotice + "Hệ thống SalesMate cho phép quản lý nhân viên, phân quyền và theo dõi hiệu suất làm việc. Bạn có thể thêm nhân viên mới và quản lý thông tin của họ trong mục Quản lý nhân viên.";
        }
        
        if (promptLower.contains("hóa đơn") || promptLower.contains("đơn hàng") || promptLower.contains("thanh toán")) {
            return errorNotice + "SalesMate giúp bạn quản lý hóa đơn và đơn hàng một cách hiệu quả. Bạn có thể tạo hóa đơn mới, tìm kiếm và in hóa đơn trong mục Quản lý hóa đơn.";
        }
        
        if (promptLower.contains("cài đặt") || promptLower.contains("thiết lập") || promptLower.contains("cấu hình")) {
            return errorNotice + "Để thay đổi cài đặt hệ thống, bạn có thể vào mục Cài đặt ở góc trên bên phải. Tại đây bạn có thể cấu hình thông tin cửa hàng, thiết lập in ấn, và các tùy chọn khác.";
        }
        
        if (promptLower.contains("khách hàng") || promptLower.contains("khách")) {
            return errorNotice + "SalesMate giúp bạn quản lý thông tin khách hàng, lịch sử mua hàng và điểm tích lũy. Bạn có thể thêm khách hàng mới và quản lý thông tin của họ trong mục Quản lý khách hàng.";
        }
        
        // Phản hồi mặc định với yếu tố hài hước
        return errorNotice + "Tôi đang gặp một số vấn đề về kết nối đến máy chủ AI. "
                + "Có thể là do anh Nhân quên thanh toán hóa đơn internet... đùa thôi! 😅\n\n"
                + "SalesMate là phần mềm quản lý bán hàng giúp bạn theo dõi sản phẩm, quản lý hóa đơn, "
                + "quản lý khách hàng và theo dõi doanh thu. Để được hỗ trợ chi tiết, vui lòng kiểm tra "
                + "tài liệu hoặc liên hệ với đội hỗ trợ.";
    }
    
    private void addUserMessage(String message) {
        StyledDocument doc = messageArea.getStyledDocument();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timestamp = sdf.format(new Date());
            
            Style timestampStyle = doc.getStyle("timestamp");
            StyleConstants.setForeground(timestampStyle, new Color(80, 80, 80));
            StyleConstants.setBold(timestampStyle, true);
            StyleConstants.setFontSize(timestampStyle, 11);
            
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "Bạn " + timestamp + ":\n", timestampStyle);
            
            JPanel bubblePanel = createMessageBubble(message, true);
            messageArea.setCaretPosition(doc.getLength());
            messageArea.insertComponent(bubblePanel);
            
            doc.insertString(doc.getLength(), "\n\n", null);
            
            chatHistory.add(new MessageEntry("user", message));
            
            SwingUtilities.invokeLater(() -> {
                messageArea.setCaretPosition(doc.getLength());
            });
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void addBotMessage(String message) {
        StyledDocument doc = messageArea.getStyledDocument();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timestamp = sdf.format(new Date());
            
            Style timestampStyle = doc.getStyle("timestamp");
            StyleConstants.setForeground(timestampStyle, new Color(80, 80, 80));
            StyleConstants.setBold(timestampStyle, true);
            StyleConstants.setFontSize(timestampStyle, 11);
            
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "SalesMate " + timestamp + ":\n", timestampStyle);
            
            JPanel bubblePanel = createMessageBubble(message, false);
            messageArea.setCaretPosition(doc.getLength());
            messageArea.insertComponent(bubblePanel);
            
            doc.insertString(doc.getLength(), "\n\n", null);
            
            chatHistory.add(new MessageEntry("assistant", message));
            
            SwingUtilities.invokeLater(() -> {
                messageArea.setCaretPosition(doc.getLength());
            });
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createMessageBubble(String message, boolean isUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        textArea.setForeground(new Color(33, 33, 33)); 
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        
        Color bubbleColor = isUser ? USER_MSG_COLOR : CHATBOT_MSG_COLOR;
        Color borderColor = isUser ? 
            new Color(209, 220, 240) : 
            new Color(220, 220, 220);  
        
        JPanel bubblePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int arc = 18; 
                
                g2.setColor(bubbleColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                
                g2.dispose();
            }
        };
        
        bubblePanel.setLayout(new BorderLayout());
        bubblePanel.add(textArea);
        bubblePanel.setOpaque(false);
        
        JPanel alignPanel = new JPanel(new BorderLayout());
        alignPanel.setOpaque(false);
        
        JPanel marginPanel = new JPanel(new BorderLayout());
        marginPanel.setOpaque(false);
        marginPanel.setBorder(BorderFactory.createEmptyBorder(0, isUser ? 50 : 5, 0, isUser ? 5 : 50));
        
        if (isUser) {
            marginPanel.add(bubblePanel, BorderLayout.EAST);
        } else {
            marginPanel.add(bubblePanel, BorderLayout.WEST);
        }
        
        alignPanel.add(marginPanel, BorderLayout.CENTER);
        panel.add(alignPanel);
        
        int preferredWidth = Math.min(CHAT_WIDTH - 100, Math.max(200, 
                                        getFontMetricsForString(message, textArea.getFont()).width + 50));
        
        int minLines = message.length() / 25 + 1; 
        int estimatedHeight = Math.max(minLines * 22, 40); 
        
        textArea.setPreferredSize(new Dimension(preferredWidth, estimatedHeight));
        
        return panel;
    }
    
    private Dimension getFontMetricsForString(String text, Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        
        int maxWidth = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            int lineWidth = fm.stringWidth(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        
        g2d.dispose();
        return new Dimension(maxWidth, fm.getHeight() * lines.length);
    }
    
    private class ModernScrollBarUI extends BasicScrollBarUI {
        private final int THUMB_SIZE = 10;
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) {
                return;
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(180, 180, 180, 150));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, 
                            THUMB_SIZE, thumbBounds.height, 
                            THUMB_SIZE, THUMB_SIZE);
            g2.dispose();
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, THUMB_SIZE, height);
        }
    }
}
