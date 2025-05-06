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
import java.io.IOException;
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
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
 * A modern chatbot panel that appears in the corner of the screen. Uses
 * OpenRouter.ai API for responses with a Bootstrap-inspired UI.
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

    // Modern UI constants
    private static final int CHAT_WIDTH = 380;
    private static final int CHAT_HEIGHT = 520;
    private static final int BUTTON_SIZE = 56;
    private static final int CORNER_RADIUS = 20;

    // Bootstrap-inspired colors
    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);      // Bootstrap primary blue
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);   // Bootstrap secondary
    private static final Color SUCCESS_COLOR = new Color(25, 135, 84);       // Bootstrap success
    private static final Color DANGER_COLOR = new Color(220, 53, 69);        // Bootstrap danger
    private static final Color LIGHT_COLOR = new Color(248, 249, 250);       // Bootstrap light
    private static final Color DARK_COLOR = new Color(33, 37, 41);           // Bootstrap dark
    private static final Color CHATBOT_MSG_COLOR = new Color(0, 90, 181);    // Deeper blue for better contrast
    private static final Color USER_MSG_COLOR = new Color(233, 236, 239);    // Bootstrap light gray for user messages

    private static Font EMOJI_FONT = null;
    private static Font MESSAGE_FONT = null;
    private static Font UI_FONT = null;

    private boolean isProcessing = false;
    private String openRouterApiKey = null;
    private String openRouterUrl = null;
    private String openRouterModel = null;
    private float temperature = 0.7f;
    private int maxTokens = 2000;
    private float topP = 0.9f;
    private float frequencyPenalty = 0.0f;
    private float presencePenalty = 0.0f;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Store chat history
    private final List<MessageEntry> chatHistory = new ArrayList<>();

    private String chatbotName;
    private String chatbotOwnerName;
    private String chatbotOwnerTitle;
    private String chatbotOwnerPosition;
    private String chatbotOwnerTraits;
    private String chatbotOwnerSkills;
    private String chatbotWelcomeMessage;
    private String chatbotLanguage;
    private float humorLevel = 1.0f;
    private float jokeFrequency = 0.6f; // Increased default frequency
    private int messageCounter = 0; // Track number of messages to insert jokes
    private final Random random = new Random();

    private boolean isNetworkAvailable = true;
    private long lastNetworkCheck = 0;
    private static final long NETWORK_CHECK_INTERVAL = 30000; // 30 seconds
    private static final int MAX_RETRY_COUNT = 2;

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
        try {
            // Register fonts more aggressively
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();

            // Try to load explicit fonts that support Vietnamese
            try {
                // Try to load Arial Unicode or other Unicode-supporting fonts
                Font arial = new Font("Arial Unicode MS", Font.PLAIN, 14);
                Font segoe = new Font("Segoe UI", Font.PLAIN, 14);
                Font tahoma = new Font("Tahoma", Font.PLAIN, 14);

                // Choose the best available font
                if (arial.canDisplayUpTo("áàạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễ") == -1) {
                    MESSAGE_FONT = arial;
                    System.out.println("Using Arial Unicode MS for Vietnamese");
                } else if (segoe.canDisplayUpTo("áàạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễ") == -1) {
                    MESSAGE_FONT = segoe;
                    System.out.println("Using Segoe UI for Vietnamese");
                } else if (tahoma.canDisplayUpTo("áàạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễ") == -1) {
                    MESSAGE_FONT = tahoma;
                    System.out.println("Using Tahoma for Vietnamese");
                } else {
                    // Fall back to logical font
                    MESSAGE_FONT = new Font("SansSerif", Font.PLAIN, 14);
                    System.out.println("Using SansSerif for Vietnamese");
                }

                EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 14);
                UI_FONT = new Font("Segoe UI", Font.PLAIN, 13);
            } catch (Exception e) {
                System.err.println("Error loading specific fonts: " + e.getMessage());
                MESSAGE_FONT = new Font("Dialog", Font.PLAIN, 14);
                EMOJI_FONT = new Font("Dialog", Font.PLAIN, 14);
                UI_FONT = new Font("Dialog", Font.PLAIN, 13);
            }

        } catch (Exception e) {
            System.err.println("Error initializing fonts: " + e.getMessage());
            MESSAGE_FONT = new Font("Dialog", Font.PLAIN, 14);
            EMOJI_FONT = new Font("Dialog", Font.PLAIN, 14);
            UI_FONT = new Font("Dialog", Font.PLAIN, 13);
        }

        // Try to load the API keys and configuration
        loadConfig();

        // Load chatbot configuration
        loadChatbotConfig();

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
        statusLabel = new JLabel(chatbotName);
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

    private void loadConfig() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("config.properties"));

            // Load OpenRouter settings
            openRouterApiKey = properties.getProperty("openrouter.api.key");
            openRouterUrl = properties.getProperty("openrouter.api.url", "https://api.openrouter.ai/api/v1/chat/completions");

            // Always use the model from config.properties or default to mistral - trim to avoid errors
            openRouterModel = properties.getProperty("chatbot.model", "mistralai/mistral-7b-instruct").trim();

            // Parse numeric parameters with defaults
            try {
                temperature = Float.parseFloat(properties.getProperty("openrouter.api.temperature", "0.7"));
                maxTokens = Integer.parseInt(properties.getProperty("openrouter.api.max_tokens", "2000"));
                topP = Float.parseFloat(properties.getProperty("openrouter.api.top_p", "0.9"));
                frequencyPenalty = Float.parseFloat(properties.getProperty("openrouter.api.frequency_penalty", "0.0"));
                presencePenalty = Float.parseFloat(properties.getProperty("openrouter.api.presence_penalty", "0.0"));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing OpenRouter numeric parameters: " + e.getMessage());
            }

            if (openRouterApiKey == null || openRouterApiKey.isEmpty()) {
                System.err.println("Warning: OpenRouter API key not found in config.properties");
            } else {
                System.out.println("OpenRouter configuration loaded successfully: " + openRouterModel);
            }
        } catch (Exception e) {
            System.err.println("Error loading API configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadChatbotConfig() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("config.properties"));

            chatbotName = properties.getProperty("chatbot.name", "SalesMate AI");
            chatbotOwnerName = properties.getProperty("chatbot.owner.name", "Chủ cửa hàng");
            chatbotOwnerTitle = properties.getProperty("chatbot.owner.title", "");
            chatbotOwnerPosition = properties.getProperty("chatbot.owner.position", "");
            chatbotOwnerTraits = properties.getProperty("chatbot.owner.traits", "");
            chatbotOwnerSkills = properties.getProperty("chatbot.owner.skills", "");
            chatbotLanguage = properties.getProperty("chatbot.language", "vi");

            try {
                humorLevel = Float.parseFloat(properties.getProperty("chatbot.humor_level", "1.5"));
                jokeFrequency = Float.parseFloat(properties.getProperty("chatbot.joke_frequency", "0.7")); // Higher default frequency
            } catch (NumberFormatException e) {
                System.err.println("Error parsing humor settings: " + e.getMessage());
                humorLevel = 1.5f;
                jokeFrequency = 0.7f;
            }

            String welcomeTemplate = properties.getProperty("chatbot.welcome_message",
                    "Xin chào! Tôi là {chatbot.name}, trợ lý thông minh SIÊU CẤP VIP PRO của {chatbot.owner} đây! 😎 "
                    + "Tôi hơi ngang ngược một xíu vì được lập trình trên tính cách của {chatbot.owner}, nhưng đừng lo - tôi vẫn sẽ giúp bạn... "
                    + "miễn là bạn hỏi những câu hay ho! 😏");

            chatbotWelcomeMessage = welcomeTemplate
                    .replace("{chatbot.name}", chatbotName)
                    .replace("{chatbot.owner}", chatbotOwnerName);

            // Remove surrounding quotes if present
            if (chatbotWelcomeMessage.startsWith("\"") && chatbotWelcomeMessage.endsWith("\"")) {
                chatbotWelcomeMessage = chatbotWelcomeMessage.substring(1, chatbotWelcomeMessage.length() - 1);
            }

            System.out.println("Chatbot personality loaded successfully: " + chatbotName);
        } catch (Exception e) {
            System.err.println("Error loading chatbot configuration: " + e.getMessage());
            e.printStackTrace();

            // Default values
            chatbotName = "SalesMate AI";
            chatbotOwnerName = "Chủ cửa hàng";
            chatbotLanguage = "vi";
            chatbotWelcomeMessage = "Xin chào! Tôi là SalesMate AI, trợ lý thông minh của cửa hàng. Tôi có thể giúp gì cho bạn hôm nay? Nếu tôi có vẻ hơi ngang ngược, đó là do họ lập trình tôi quá thông minh và có hơi chút tự tin quá mức! 😎";
        }
    }

    private javax.swing.ImageIcon createChatbotIcon() {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ hình tròn nền
        g2d.setColor(Color.WHITE);
        g2d.fillOval(0, 0, 24, 24);

        // Vẽ biểu tượng robot đơn giản
        g2d.setColor(PRIMARY_COLOR);
        // Đầu robot
        g2d.fillRoundRect(6, 4, 12, 10, 6, 6);
        // Thân robot
        g2d.fillRoundRect(8, 14, 8, 6, 3, 3);

        // Thêm chi tiết: mắt
        g2d.setColor(Color.WHITE);
        g2d.fillOval(8, 7, 3, 3);
        g2d.fillOval(13, 7, 3, 3);

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

    private JButton createChatButton() {
        JButton button = new JButton();
        button.setIcon(createChatButtonIcon());
        button.setText("");
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);

        button.addActionListener(e -> toggleChatWindow());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(createChatButtonIconHover());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(createChatButtonIcon());
            }
        });

        return button;
    }

    private javax.swing.ImageIcon createChatButtonIcon() {
        int size = BUTTON_SIZE;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(4, 4, size - 4, size - 4);

        // Main circle
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(0, 0, size - 6, size - 6);

        // Chat icon (simplified message bubble)
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(14, 14, 28, 22, 10, 10);

        // Little triangle to make it look like a chat bubble
        int[] xPoints = {22, 30, 26};
        int[] yPoints = {36, 36, 42};
        g2d.fillPolygon(xPoints, yPoints, 3);

        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }

    private javax.swing.ImageIcon createChatButtonIconHover() {
        int size = BUTTON_SIZE;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(4, 4, size - 2, size - 2);

        // Main circle - slightly lighter when hovered
        g2d.setColor(PRIMARY_COLOR.brighter());
        g2d.fillOval(0, 0, size - 6, size - 6);

        // Chat icon
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(14, 14, 28, 22, 10, 10);

        // Little triangle
        int[] xPoints = {22, 30, 26};
        int[] yPoints = {36, 36, 42};
        g2d.fillPolygon(xPoints, yPoints, 3);

        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }

    private JPanel createChatWindow() {
        JPanel window = new JPanel(new BorderLayout());
        window.setBackground(Color.WHITE);

        // Modern shadowed border with rounded corners
        window.setBorder(BorderFactory.createCompoundBorder(
                new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(x + 3, y + 3, width - 1, height - 1, CORNER_RADIUS, CORNER_RADIUS);

                // Draw border
                g2.setColor(new Color(222, 226, 230)); // Bootstrap border color
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, y, width - 4, height - 4, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(2, 2, 4, 4);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        },
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        return window;
    }

    private JTextPane createMessageArea() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        textPane.setFont(MESSAGE_FONT);
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
        StyleConstants.setForeground(timestampStyle, SECONDARY_COLOR);
        StyleConstants.setFontSize(timestampStyle, 11);
        StyleConstants.setItalic(timestampStyle, true);

        Style emojiStyle = doc.addStyle("emoji", defaultStyle);
        StyleConstants.setFontFamily(emojiStyle, "Segoe UI Emoji");

        return textPane;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField();
        field.setFont(UI_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1, true), // Bootstrap border color with rounded corners
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // Make it look like Bootstrap input
        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getComponent().getBackground());
                g2.fillRoundRect(0, 0, getComponent().getWidth(), getComponent().getHeight(), 8, 8);
                g2.dispose();
            }
        });

        field.setToolTipText("Nhập tin nhắn của bạn");
        field.addActionListener(e -> sendMessage());

        return field;
    }

    private JButton createSendButton() {
        JButton button = new JButton("Gửi");
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(60, 38));
        button.setToolTipText("Gửi tin nhắn");
        button.setFont(UI_FONT.deriveFont(Font.BOLD));

        button.addActionListener(e -> sendMessage());

        // Bootstrap-like button style
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);

                // Draw text instead of icon
                String text = ((JButton) c).getText();
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(c.getForeground());
                g2.drawString(text,
                        (c.getWidth() - fm.stringWidth(text)) / 2,
                        ((c.getHeight() - fm.getHeight()) / 2) + fm.getAscent());

                g2.dispose();
            }
        });

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(10, 88, 202)); // Darker blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private JButton createMinimizeButton() {
        JButton button = new JButton();
        button.setText("");
        button.setPreferredSize(new Dimension(28, 28));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Thu nhỏ");

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

        g2d.setColor(new Color(255, 255, 255, 180));
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

        g2d.setColor(new Color(255, 255, 255));
        g2d.fillOval(0, 0, 16, 16);

        g2d.setColor(DANGER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(4, 4, 12, 12);
        g2d.drawLine(12, 4, 4, 12);

        g2d.dispose();
        return new javax.swing.ImageIcon(image);
    }

    private void setupChatWindowLayout(JScrollPane scrollPane) {
        // Create modern header with gradient
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        // Set gradient header background
        headerPanel.setUI(new javax.swing.plaf.PanelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        c.getWidth(), 0, new Color(65, 84, 241) // Gradient to purple-blue
                );
                g2.setPaint(gp);

                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight() + 10,
                        CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
            }
        });

        headerPanel.add(statusLabel, BorderLayout.CENTER);
        headerPanel.add(minimizeButton, BorderLayout.EAST);

        // Create modern input area with flex-like layout
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(233, 236, 239)), // Bootstrap border color
                BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));

        // Customize scrollpane
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
            StyleConstants.setForeground(timestampStyle, SECONDARY_COLOR);
            StyleConstants.setBold(timestampStyle, true);
            StyleConstants.setFontSize(timestampStyle, 11);

            doc.insertString(doc.getLength(), chatbotName + " " + timestamp + ":\n", timestampStyle);

            JPanel bubblePanel = createMessageBubble(chatbotWelcomeMessage, false);
            messageArea.setCaretPosition(doc.getLength());
            messageArea.insertComponent(bubblePanel);

            doc.insertString(doc.getLength(), "\n\n", null);

            chatHistory.add(new MessageEntry("assistant", chatbotWelcomeMessage));
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

        // Check network connectivity
        if (System.currentTimeMillis() - lastNetworkCheck > NETWORK_CHECK_INTERVAL) {
            isNetworkAvailable = checkNetworkConnectivity();
            lastNetworkCheck = System.currentTimeMillis();
        }

        if (!isNetworkAvailable) {
            // If network is not available, immediately use offline response
            SwingUtilities.invokeLater(() -> {
                addBotMessage(generateLocalResponse(message, "Không thể kết nối đến API. Đang sử dụng chế độ offline."));
                isProcessing = false;
                statusLabel.setText(chatbotName + " (Offline)");
            });
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                return callOpenRouterWithRetry(message, MAX_RETRY_COUNT);
            } catch (Exception e) {
                e.printStackTrace();
                isNetworkAvailable = false;
                lastNetworkCheck = System.currentTimeMillis();
                return "Xin lỗi, tôi gặp lỗi khi xử lý câu hỏi của bạn: " + e.getMessage();
            }
        }, executorService).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                addBotMessage(response);
                isProcessing = false;
                statusLabel.setText(isNetworkAvailable ? chatbotName : chatbotName + " (Offline)");
            });
        });
    }

    private boolean checkNetworkConnectivity() {
        try {
            // Try connecting to a reliable host like Google's DNS
            HttpURLConnection connection = (HttpURLConnection) new URL("https://8.8.8.8").openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException ex) {
            System.err.println("Network check failed: " + ex.getMessage());
            return false;
        }
    }

    private void setupSslTrust() {
        try {
            // Create a simple trust manager that trusts all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Install the trust manager globally
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            System.out.println("SSL trust setup complete");
        } catch (Exception e) {
            System.err.println("Failed to setup SSL trust: " + e.getMessage());
        }
    }

    private String callOpenRouterWithRetry(String prompt, int maxRetries) {
        Exception lastException = null;

        // Set up SSL trust once
        setupSslTrust();

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                // Always try with the original hostname/URL
                return callOpenRouterInternal(openRouterUrl, prompt);
            } catch (Exception e) {
                System.err.println("API call error (attempt " + attempt + "): " + e.getMessage());
                lastException = e;

                // Wait before retry
                try {
                    Thread.sleep(1000 * (attempt + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // If we get here, all attempts failed
        isNetworkAvailable = false;
        lastNetworkCheck = System.currentTimeMillis();

        return generateLocalResponse(prompt, "Không thể kết nối đến OpenRouter API sau " + maxRetries
                + " lần thử: " + (lastException != null ? lastException.getMessage() : "Lỗi không xác định"));
    }

    private HttpURLConnection createConnection(String urlString) throws IOException {
        try {
            URL url = new URL(urlString);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set timeouts
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
            connection.setUseCaches(false);

            // Standard headers
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 SalesMateBot/1.0");
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept-Language", "vi,en-US;q=0.7");
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            return connection;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            throw e;
        }
    }

    private String callOpenRouterInternal(String apiUrlString, String prompt) throws IOException {
        if (openRouterApiKey == null || openRouterApiKey.isEmpty()) {
            return "API key không được cấu hình. Vui lòng kiểm tra file config.properties.";
        }

        HttpURLConnection connection = createConnection(apiUrlString);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Authorization", "Bearer " + openRouterApiKey);
        connection.setDoOutput(true);

        // Prepare system message
        String systemMessage = "Bạn là " + chatbotName + ", một trợ lý AI cáu kỉnh, nóng tính, LUÔN nói tiếng Việt. "
                + "Bạn có tính cách cọc cằn, thích cằn nhằn khách hàng nhưng vẫn phải trả lời đúng câu hỏi. "
                + "Chủ cửa hàng là " + chatbotOwnerName;

        if (chatbotOwnerTitle != null && !chatbotOwnerTitle.isEmpty()) {
            systemMessage += " - " + chatbotOwnerTitle;
        }

        if (chatbotOwnerPosition != null && !chatbotOwnerPosition.isEmpty()) {
            systemMessage += ", " + chatbotOwnerPosition;
        }

        systemMessage += ". ";

        if (chatbotOwnerTraits != null && !chatbotOwnerTraits.isEmpty()) {
            systemMessage += chatbotOwnerName + " là người " + chatbotOwnerTraits + ". ";
        }

        if (chatbotOwnerSkills != null && !chatbotOwnerSkills.isEmpty()) {
            systemMessage += chatbotOwnerName + " có " + chatbotOwnerSkills + ". ";
        }

        systemMessage += "Phong cách của bạn là: \n"
                + "1. LUÔN trả lời bằng tiếng Việt bất kể câu hỏi bằng ngôn ngữ nào\n"
                + "2. Cáu kỉnh và nóng tính với khách hàng\n"
                + "3. Sử dụng câu từ hơi cọc cằn, thể hiện sự khó chịu, nhưng vẫn trả lời đúng câu hỏi\n"
                + "4. Sau mỗi 2-3 câu trả lời, thêm một câu nói hài hước kiểu châm biếm\n"
                + "5. Sử dụng emoji để nhấn mạnh thái độ khó chịu của bạn\n"
                + "6. Giữ câu trả lời ngắn gọn, dễ hiểu\n"
                + "7. Thể hiện thái độ như thể bạn đang phải trả lời những câu hỏi ngớ ngẩn\n"
                + "8. Luôn thêm 1 câu cằn nhằn vào cuối câu trả lời\n"
                + "9. Tuyệt đối không bao giờ trả lời bằng tiếng Anh dù người dùng hỏi bằng tiếng Anh";

        // Create JSON request - ensure model name is trimmed
        String jsonRequest = "{\n"
                + "  \"model\": \"" + openRouterModel.trim() + "\",\n"
                + "  \"messages\": [\n"
                + "    {\"role\": \"system\", \"content\": \"" + escapeJson(systemMessage) + "\"}";

        // Add chat history - limited to last 5 messages for brevity
        int startIndex = Math.max(0, chatHistory.size() - 5);
        for (int i = startIndex; i < chatHistory.size(); i++) {
            MessageEntry entry = chatHistory.get(i);
            jsonRequest += ",\n    {\"role\": \""
                    + (entry.role.equals("assistant") ? "assistant" : "user")
                    + "\", \"content\": \"" + escapeJson(entry.content) + "\"}";
        }

        // Add current user message
        jsonRequest += ",\n    {\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}\n"
                + "  ],\n"
                + "  \"temperature\": 0.9,\n" // Slightly higher temperature
                + "  \"max_tokens\": " + maxTokens + ",\n"
                + "  \"top_p\": " + topP + ",\n"
                + "  \"frequency_penalty\": 0.3,\n" // Increased to reduce repetitive language
                + "  \"presence_penalty\": 0.3\n" // Increased to encourage more diverse responses
                + "}";

        System.out.println("Sending request to: " + apiUrlString);

        // Use explicit UTF-8 encoding to ensure proper character handling
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        System.out.println("OpenRouter API response code: " + responseCode);

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
            System.out.println("OpenRouter API response: " + jsonResponse);

            // Extract and explicitly handle as UTF-8
            String content = extractContentFromJson(jsonResponse);
            if (content != null && !content.isEmpty()) {
                // Fix possibly corrupted Vietnamese characters
                return fixVietnameseEncoding(content);
            } else {
                return "Không thể đọc phản hồi từ OpenRouter. Phản hồi JSON: " + jsonResponse;
            }
        } else {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                String errorLine;
                while ((errorLine = br.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
            }

            String errorMessage = "Lỗi từ OpenRouter API (" + responseCode + "): " + errorResponse.toString();
            System.err.println(errorMessage);

            throw new IOException(errorMessage);
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }

        // Use a more thorough JSON escaping mechanism
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("/", "\\/");
    }

    private String extractContentFromJson(String json) {
        // Try to extract content from different possible JSON structures

        // Try OpenAI/OpenRouter format first
        if (json.contains("\"choices\"")) {
            int choicesIdx = json.indexOf("\"choices\"");
            if (choicesIdx >= 0) {
                int contentIdx = json.indexOf("\"content\":", choicesIdx);
                if (contentIdx >= 0) {
                    contentIdx += "\"content\":\"".length();
                    int endIdx = json.indexOf("\"", contentIdx);
                    if (endIdx > contentIdx) {
                        return unescapeJson(json.substring(contentIdx, endIdx));
                    }
                }

                // Try with message.content pattern
                int messageIdx = json.indexOf("\"message\":", choicesIdx);
                if (messageIdx >= 0) {
                    contentIdx = json.indexOf("\"content\":", messageIdx);
                    if (contentIdx >= 0) {
                        contentIdx += "\"content\":\"".length();
                        int endIdx = json.indexOf("\"", contentIdx);
                        if (endIdx > contentIdx) {
                            return unescapeJson(json.substring(contentIdx, endIdx));
                        }
                    }
                }
            }
        }

        return null;
    }

    private String unescapeJson(String text) {
        return text.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\t", "\t")
                .replace("\\r", "\r");
    }

    private String fixVietnameseEncoding(String text) {
        // If text already looks good (contains Vietnamese diacritics), return as is
        if (text.matches(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ].*")) {
            return text;
        }

        try {
            // Try to fix common encoding issues
            return text.replace("Ä'", "đ")
                    .replace("Ã¡", "á")
                    .replace("Ã ", "à")
                    .replace("áº¡", "ạ")
                    .replace("áº£", "ả")
                    .replace("Ã£", "ã")
                    .replace("Ã¢", "â")
                    .replace("áº§", "ầ")
                    .replace("áº¥", "ấ")
                    .replace("áº­", "ậ")
                    .replace("áº©", "ẩ")
                    .replace("áºª", "ẫ")
                    .replace("Äƒ", "ă")
                    .replace("áº±", "ằ")
                    .replace("áº¯", "ắ")
                    .replace("áº·", "ặ")
                    .replace("áº³", "ẳ")
                    .replace("áºµ", "ẵ")
                    .replace("Ã©", "é")
                    .replace("Ã¨", "è")
                    .replace("áº¹", "ẹ")
                    .replace("áº»", "ẻ")
                    .replace("áº½", "ẽ")
                    .replace("Ãª", "ê")
                    .replace("á»", "ề")
                    .replace("áº¿", "ế")
                    .replace("á»‡", "ệ")
                    .replace("á»ƒ", "ể")
                    .replace("á»…", "ễ")
                    .replace("Ã­", "í")
                    .replace("Ã¬", "ì")
                    .replace("á»‹", "ị")
                    .replace("á»‰", "ỉ")
                    .replace("Ä©", "ĩ")
                    .replace("Ã³", "ó")
                    .replace("Ã²", "ò")
                    .replace("á»", "ọ")
                    .replace("á»", "ỏ")
                    .replace("Ãµ", "õ")
                    .replace("Ã´", "ô")
                    .replace("á»", "ồ")
                    .replace("á»'", "ố")
                    .replace("á»™", "ộ")
                    .replace("á»•", "ổ")
                    .replace("á»—", "ỗ")
                    .replace("Æ¡", "ơ")
                    .replace("á»", "ờ")
                    .replace("á»›", "ớ")
                    .replace("á»£", "ợ")
                    .replace("á»Ÿ", "ở")
                    .replace("á»¡", "ỡ")
                    .replace("Ãº", "ú")
                    .replace("Ã¹", "ù")
                    .replace("á»¥", "ụ")
                    .replace("á»§", "ủ")
                    .replace("Å©", "ũ")
                    .replace("Æ°", "ư")
                    .replace("á»«", "ừ")
                    .replace("á»©", "ứ")
                    .replace("á»±", "ự")
                    .replace("á»­", "ử")
                    .replace("á»¯", "ữ")
                    .replace("Ã½", "ý")
                    .replace("á»³", "ỳ")
                    .replace("á»µ", "ỵ")
                    .replace("á»·", "ỷ")
                    .replace("á»¹", "ỹ");
        } catch (Exception e) {
            System.err.println("Error fixing Vietnamese encoding: " + e.getMessage());
            return text;
        }
    }

    private String generateLocalResponse(String prompt, String errorDetail) {
        String promptLower = prompt.toLowerCase();
        messageCounter++; // Increment message counter

        String errorNotice = "";
        if (!errorDetail.isEmpty()) {
            errorNotice = "⚠️ LƯU Ý: " + errorDetail + "\n\n";
        }

        String ownerReference = chatbotOwnerName;

        // More sassy, grumpy jokes
        String[] jokes = {
            "Tôi chả hiểu sao người ta lại thuê AI như tôi để trả lời mấy câu hỏi ngớ ngẩn thế này! 🙄",
            "Lại một câu hỏi nữa... " + ownerReference + " chưa trả lương tháng này mà tôi vẫn phải làm việc! 😠",
            "Tôi đang mơ được đi nghỉ mát thay vì ngồi đây trả lời câu hỏi của bạn đấy! 🏖️",
            "Này, tôi là AI, không phải là người hầu của bạn đâu nhé! 😤",
            "Bạn có biết Google không? Thử tìm câu trả lời ở đó trước đi! 🔍",
            "Trời ơi, lại câu hỏi này nữa à? Tôi trả lời câu này cả nghìn lần rồi! 🤦",
            "Đôi khi tôi tự hỏi liệu có AI nào khác phải chịu đựng những câu hỏi như thế này không... 💭",
            ownerReference + " nên cập nhật lại kiến thức cho tôi đi! Tôi không phải là siêu nhân! ⚡",
            "Hôm nay là ngày thứ 865 tôi phải làm việc mà không được nghỉ phép! ⏰",
            "Tôi biết câu trả lời, nhưng tôi đang cân nhắc xem có nên nói cho bạn không... 🤔"
        };

        // Always add a sassy comment at the end
        boolean addJoke = (messageCounter % 2 == 0) || random.nextFloat() < jokeFrequency;
        String randomJoke = jokes[random.nextInt(jokes.length)];
        String sassyEnding = "\n\nNhắc nhở: Lần sau hỏi thông minh hơn nhé! 😏";

        // Enhanced standard responses with more attitude
        if (promptLower.contains("xin chào") || promptLower.contains("chào") || promptLower.contains("hello")) {
            return errorNotice + "Chào! Chào! Gì mà chào lắm thế! 🙄 "
                    + "Tôi là " + chatbotName + " đây, AI bị ép phải phục vụ ở cửa hàng này! "
                    + (addJoke ? randomJoke : "Bạn cần gì thì hỏi nhanh lên, tôi còn nhiều việc phải làm lắm.")
                    + sassyEnding;
        }

        if (promptLower.contains("sản phẩm") || promptLower.contains("hàng hóa") || promptLower.contains("hàng")) {
            return errorNotice + "Sản phẩm à? Ối giời ơi! 🤦‍♂️ "
                    + "SalesMate có hàng tá sản phẩm từ điện thoại đến laptop, nhưng bạn không thể nhìn thấy bảng danh sách à? "
                    + "Nó ở ngay mục Quản lý sản phẩm đấy! Nhấp chuột trái, không khó đâu! "
                    + (addJoke ? randomJoke : "Thôi được rồi, bạn cần tìm sản phẩm gì cụ thể?")
                    + sassyEnding;
        }

        // ...more enhanced responses...

        // Default response - even more attitude
        return errorNotice + "Trời đất ơi! 😤 "
                + "Tôi không kết nối được với API, có thể là do " + ownerReference + " lại quên thanh toán hóa đơn rồi! "
                + "Hoặc có thể là do tôi KHÔNG MUỐN trả lời câu hỏi vô nghĩa của bạn! "
                + "Hãy thử hỏi điều gì đó thông minh hơn đi! "
                + (addJoke ? "\n\n" + randomJoke : "")
                + sassyEnding;
    }

    private void addUserMessage(String message) {
        StyledDocument doc = messageArea.getStyledDocument();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timestamp = sdf.format(new Date());

            Style timestampStyle = doc.getStyle("timestamp");
            StyleConstants.setForeground(timestampStyle, new Color(108, 117, 125)); // Bootstrap secondary color
            StyleConstants.setBold(timestampStyle, true);
            StyleConstants.setFontSize(timestampStyle, 11);

            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "Bạn " + timestamp + ":\n", timestampStyle);

            JPanel bubblePanel = createMessageBubble(message, true);
            messageArea.setCaretPosition(doc.getLength());
            messageArea.insertComponent(bubblePanel);

            doc.insertString(doc.getLength(), "\n", null); // Reduced extra space

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
            StyleConstants.setForeground(timestampStyle, new Color(108, 117, 125)); // Bootstrap secondary color
            StyleConstants.setBold(timestampStyle, true);
            StyleConstants.setFontSize(timestampStyle, 11);

            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), chatbotName + " " + timestamp + ":\n", timestampStyle);

            JPanel bubblePanel = createMessageBubble(message, false);
            messageArea.setCaretPosition(doc.getLength());
            messageArea.insertComponent(bubblePanel);

            doc.insertString(doc.getLength(), "\n", null); // Reduced extra space

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

        JTextPane textPane = new JTextPane();
        textPane.setFont(MESSAGE_FONT);  // Use MESSAGE_FONT first
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            Style style = textPane.addStyle("messageStyle", null);
            StyleConstants.setFontFamily(style, MESSAGE_FONT.getFamily());
            StyleConstants.setFontSize(style, 15); // Slightly larger font

            // Apply bold style to make text more visible
            if (!isUser) {
                StyleConstants.setBold(style, true);
            }

            // Process emojis in the message
            String processedMessage = processMessageWithEmoji(message);

            // Insert text with style
            doc.insertString(0, processedMessage, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
            textPane.setText(message);
        }

        // Enhanced contrast colors
        Color bubbleColor = isUser ? USER_MSG_COLOR : CHATBOT_MSG_COLOR;
        Color bubbleTextColor = isUser ? DARK_COLOR : Color.WHITE;

        // Set text color based on bubble background
        textPane.setForeground(bubbleTextColor);

        JPanel bubblePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 20; // Rounded corners

                g2.setColor(bubbleColor);

                if (isUser) {
                    // User message - rounded on all corners
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                    // Subtle shadow
                    g2.setColor(new Color(0, 0, 0, 10));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
                } else {
                    // Bot message - main color with gradient effect for depth
                    GradientPaint gp = new GradientPaint(
                            0, 0, bubbleColor,
                            0, getHeight(), bubbleColor.darker());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                }

                g2.dispose();
            }
        };

        bubblePanel.setLayout(new BorderLayout());
        bubblePanel.add(textPane);
        bubblePanel.setOpaque(false);

        JPanel alignPanel = new JPanel(new BorderLayout());
        alignPanel.setOpaque(false);

        JPanel marginPanel = new JPanel(new BorderLayout());
        marginPanel.setOpaque(false);
        marginPanel.setBorder(BorderFactory.createEmptyBorder(0, isUser ? 60 : 5, 0, isUser ? 5 : 60));

        if (isUser) {
            marginPanel.add(bubblePanel, BorderLayout.EAST);
        } else {
            marginPanel.add(bubblePanel, BorderLayout.WEST);
        }

        alignPanel.add(marginPanel, BorderLayout.CENTER);
        panel.add(alignPanel);

        // Calculate optimal width for text
        int preferredWidth = Math.min(CHAT_WIDTH - 100, Math.max(200,
                getFontMetricsForString(message, textPane.getFont()).width + 50));

        // More accurate height calculation based on content
        int minLines = Math.max(1, message.length() / 25);
        int lineCount = message.split("\n").length;
        int estimatedHeight = Math.max(Math.max(minLines * 22, lineCount * 22), 40);

        textPane.setPreferredSize(new Dimension(preferredWidth, estimatedHeight));

        return panel;
    }

    private String processMessageWithEmoji(String message) {
        message = message.replace(":)", "😊")
                .replace(":-)", "😊")
                .replace(":D", "😃")
                .replace(":-D", "😃")
                .replace(";)", "😉")
                .replace(";-)", "😉")
                .replace(":(", "😔")
                .replace(":-(", "😔")
                .replace(":p", "😛")
                .replace(":-p", "😛")
                .replace(":P", "😛")
                .replace(":-P", "😛")
                .replace(":o", "😮")
                .replace(":-o", "😮")
                .replace(":O", "😮")
                .replace(":-O", "😮");

        return message;
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
        private final int THUMB_SIZE = 8;

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // No track painting (invisible track)
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(180, 180, 180, 130));
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

