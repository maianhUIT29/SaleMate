package com.salesmate.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.json.JSONArray;
import org.json.JSONObject;

import com.salesmate.utils.ConfigLoader;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminChatbot extends JPanel {
    // API Configuration
    private final String API_KEY;
    private final String API_MODEL;
    private final float API_TEMPERATURE;
    private final int API_MAX_TOKENS;
    private final float API_TOP_P;
    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // UI Components
    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton toggleButton;
    private JScrollPane scrollPane;
    private JPanel chatPanel;
    private boolean isExpanded = false;
    private JLabel statusLabel;
    private Timer typingTimer;
    private int dotCount = 0;

    // Chat history
    private List<Message> chatHistory = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Bot personality
    private final String BOT_NAME;
    private final Random random = new Random();
    private int messageCounter = 0;
    private final int JOKE_FREQUENCY = 2; // Every 2-3 messages will have a joke

    // Styling - Bootstrap-inspired colors
    private final Color PRIMARY_COLOR = new Color(13, 110, 253);    // Bootstrap primary blue
    private final Color SECONDARY_COLOR = new Color(108, 117, 125); // Bootstrap secondary gray
    private final Color SUCCESS_COLOR = new Color(25, 135, 84);     // Bootstrap success green
    private final Color WARNING_COLOR = new Color(255, 193, 7);     // Bootstrap warning yellow
    private final Color DANGER_COLOR = new Color(220, 53, 69);      // Bootstrap danger red
    private final Color INFO_COLOR = new Color(13, 202, 240);       // Bootstrap info blue
    private final Color LIGHT_COLOR = new Color(248, 249, 250);     // Bootstrap light gray
    private final Color DARK_COLOR = new Color(33, 37, 41);         // Bootstrap dark gray
    
    private final Color BOT_BUBBLE_COLOR = LIGHT_COLOR;
    private final Color USER_BUBBLE_COLOR = PRIMARY_COLOR;
    private final Color PANEL_BACKGROUND = Color.WHITE;
    private final Font MESSAGE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final int COLLAPSED_WIDTH = 60;
    private final int COLLAPSED_HEIGHT = 60;
    private final int EXPANDED_WIDTH = 350;
    private final int EXPANDED_HEIGHT = 500;

    // For dragging the chatbot around
    private Point initialClick;
    private boolean isDragging = false;

    // Constructor
    public AdminChatbot() {
        // Load API configuration from properties
        ConfigLoader config = ConfigLoader.getInstance();

        API_KEY = config.getProperty("openrouter.api.key", "");
        API_MODEL = config.getProperty("openrouter.api.model", "mistralai/mistral-7b-instruct");
        API_TEMPERATURE = Float.parseFloat(config.getProperty("openrouter.api.temperature", "0.7"));
        API_MAX_TOKENS = Integer.parseInt(config.getProperty("openrouter.api.max_tokens", "2000"));
        API_TOP_P = Float.parseFloat(config.getProperty("openrouter.api.top_p", "0.9"));
        BOT_NAME = config.getProperty("chatbot.name", "Anthuhai Chatbot AI");

        // Set system default font to support Vietnamese characters
        System.setProperty("file.encoding", "UTF-8");

        // Sets absolute positioning for this component
        setLayout(null);
        setBorder(null);
        setOpaque(false);

        // Set initial size to collapsed state
        setSize(COLLAPSED_WIDTH, COLLAPSED_HEIGHT);
        setPreferredSize(new Dimension(COLLAPSED_WIDTH, COLLAPSED_HEIGHT));

        setupUI();
        setupDragAndDrop();
        initChatbot();

        setVisible(true);
    }

    /**
     * Explicitly sets the bounds of this component
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        // Ensure the chatPanel is properly positioned relative to this component
        if (chatPanel != null) {
            chatPanel.setLocation(0, 0);
        }
    }

    private void setupUI() {
        // Set up the main panel
        setLayout(null);
        setBorder(null);
        setOpaque(false);
        setSize(COLLAPSED_WIDTH, COLLAPSED_HEIGHT);

        // Initialize typingTimer first to avoid NullPointerException
        typingTimer = new Timer(500, e -> {
            dotCount = (dotCount + 1) % 4;
            StringBuilder dots = new StringBuilder();
            for (int i = 0; i < dotCount; i++) {
                dots.append(".");
            }
            statusLabel.setText("Bot đang trả lời" + dots.toString());
        });
        typingTimer.setRepeats(true);

        // Create chat panel with rounded corners
        chatPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                int shadowSize = 10;
                for (int i = 0; i < shadowSize; i++) {
                    float alpha = (shadowSize - i) / (float)(shadowSize * 2);
                    g2d.setColor(new Color(0, 0, 0, alpha));
                    g2d.setStroke(new BasicStroke(i));
                    g2d.draw(new RoundRectangle2D.Float(
                            shadowSize - i, shadowSize - i,
                            getWidth() - 2 * (shadowSize - i),
                            getHeight() - 2 * (shadowSize - i),
                            20, 20));
                }

                // Fill panel with white background
                g2d.setColor(PANEL_BACKGROUND);
                g2d.fill(new RoundRectangle2D.Float(
                        shadowSize, shadowSize,
                        getWidth() - 2 * shadowSize,
                        getHeight() - 2 * shadowSize,
                        15, 15));

                g2d.dispose();
            }
        };

        chatPanel.setSize(EXPANDED_WIDTH, EXPANDED_HEIGHT);
        chatPanel.setLocation(0, 0);
        chatPanel.setVisible(false);
        chatPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent for custom painting
        chatPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create message display area with a custom text pane for bubble chat style
        chatArea = new JTextPane() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                super.paint(g);
            }
        };

        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(PANEL_BACKGROUND);
        chatArea.setBorder(null);
        
        // Set UTF-8 encoding for the text pane
        chatArea.putClientProperty("charset", "UTF-8");

        // Chat area styling
        chatArea.putClientProperty("caretWidth", 3);
        chatArea.setSelectionColor(new Color(PRIMARY_COLOR.getRed(), 
                                           PRIMARY_COLOR.getGreen(), 
                                           PRIMARY_COLOR.getBlue(), 100));

        // Create scroll pane for chat area with modern style
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Style the scrollbar
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(220, 220, 220);
                this.trackColor = PANEL_BACKGROUND;
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
        });

        // Create header panel with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    0, getHeight(), PRIMARY_COLOR.darker());
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

                g2d.dispose();
            }
        };

        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(EXPANDED_WIDTH, 40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel titleLabel = new JLabel(BOT_NAME);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Try to load bot icon
        try {
            // Look for icon in resources
            URL iconUrl = getClass().getResource("/img/icons/bot-icon.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                titleLabel.setIcon(new ImageIcon(img));
            } else {
                // Try alternate location
                iconUrl = AdminChatbot.class.getResource("/bot-icon.png");
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    titleLabel.setIcon(new ImageIcon(img));
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load bot icon: " + e.getMessage());
        }

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Close button in the header with hover effect
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 255, 255, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.WHITE);
            }
        });
        closeButton.addActionListener(e -> toggleChatPanel());
        headerPanel.add(closeButton, BorderLayout.EAST);

        // Create modern input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Rounded text field with proper boundaries and transparency
        inputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fill with background color first
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };

        inputField.setFont(INPUT_FONT);
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        inputField.setBackground(new Color(245, 245, 245));
        inputField.addActionListener(e -> sendMessage());

        // Create a wrapper panel for the input field to handle rounded corners
        JPanel textFieldPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw border with rounded corners
                g2d.setColor(new Color(220, 220, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Fill inside area with background color
                g2d.setColor(LIGHT_COLOR);
                g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                
                g2d.dispose();
            }
        };
        textFieldPanel.setOpaque(false);
        textFieldPanel.setBorder(null);
        textFieldPanel.add(inputField, BorderLayout.CENTER);

        // Modern send button with "Send" text and proper rounded corners
        sendButton = new JButton("Gửi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill the button with its background color
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw the text
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                g2d.setColor(getForeground());
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(60, 40));

        // Add hover effect to send button
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(PRIMARY_COLOR.darker());
                sendButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(PRIMARY_COLOR);
                sendButton.repaint();
            }
        });

        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(textFieldPanel, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Status label for typing indicator
        statusLabel = new JLabel();
        statusLabel.setForeground(new Color(150, 150, 150));
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        // Add components to chat panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);

        chatPanel.add(headerPanel, BorderLayout.NORTH);
        chatPanel.add(contentPanel, BorderLayout.CENTER);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Create toggle button with "AI" text and ensure it's properly rounded
        toggleButton = new JButton("AI") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillOval(2, 2, getWidth() - 4, getHeight() - 4);

                // Fill button background
                g2d.setColor(getBackground());
                g2d.fillOval(0, 0, getWidth() - 4, getHeight() - 4);

                // Draw text centered
                FontMetrics fm = g2d.getFontMetrics(getFont());
                Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
                String text = getText();
                
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setBounds(0, 0, COLLAPSED_WIDTH, COLLAPSED_HEIGHT);
        toggleButton.setBackground(PRIMARY_COLOR);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> toggleChatPanel());

        // Button hover effect
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleButton.setBackground(PRIMARY_COLOR.darker());
                toggleButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                toggleButton.setBackground(PRIMARY_COLOR);
                toggleButton.repaint();
            }
        });

        // Remove the BasicButtonUI since we're overriding paintComponent
        toggleButton.setUI(new BasicButtonUI());

        // Add components to main panel
        add(chatPanel);
        add(toggleButton);

        // Set up text styling for the chat area
        setupTextStyles();
    }

    private void setupTextStyles() {
        // Set default style
        StyleContext sc = StyleContext.getDefaultStyleContext();

        // User message style
        Style userStyle = chatArea.addStyle("userStyle", null);
        StyleConstants.setForeground(userStyle, Color.WHITE);
        StyleConstants.setBackground(userStyle, USER_BUBBLE_COLOR);
        StyleConstants.setFontFamily(userStyle, "Segoe UI");
        StyleConstants.setFontSize(userStyle, 14);
        StyleConstants.setAlignment(userStyle, StyleConstants.ALIGN_RIGHT);

        // Bot message style
        Style botStyle = chatArea.addStyle("botStyle", null);
        StyleConstants.setForeground(botStyle, DARK_COLOR);
        StyleConstants.setBackground(botStyle, BOT_BUBBLE_COLOR);
        StyleConstants.setFontFamily(botStyle, "Segoe UI");
        StyleConstants.setFontSize(botStyle, 14);
        StyleConstants.setAlignment(botStyle, StyleConstants.ALIGN_LEFT);

        // Time style
        Style timeStyle = chatArea.addStyle("timeStyle", null);
        StyleConstants.setForeground(timeStyle, SECONDARY_COLOR);
        StyleConstants.setFontSize(timeStyle, 11);
        StyleConstants.setItalic(timeStyle, true);
        StyleConstants.setAlignment(timeStyle, StyleConstants.ALIGN_CENTER);
    }

    private void setupDragAndDrop() {
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && !isExpanded) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    int newX = thisX + xMoved;
                    int newY = thisY + yMoved;

                    // Get bounds of parent container
                    Container parent = getParent();
                    if (parent != null) {
                        Dimension parentSize = parent.getSize();

                        // Keep inside parent bounds
                        if (newX < 0) newX = 0;
                        if (newY < 0) newY = 0;
                        if (newX + getWidth() > parentSize.width) {
                            newX = parentSize.width - getWidth();
                        }
                        if (newY + getHeight() > parentSize.height) {
                            newY = parentSize.height - getHeight();
                        }
                    }

                    setLocation(newX, newY);
                }
            }
        };

        toggleButton.addMouseListener(dragAdapter);
        toggleButton.addMouseMotionListener(dragAdapter);
    }

    private void initChatbot() {
        // Add initial welcome message
        SwingUtilities.invokeLater(() -> {
            String welcomeMessage = "Xin chao! Toi la Anthuhai, tro ly ao chu cua hang SalesMate! Toi hoi ngang nguoc va thich pha tro mot chut. Hoi gi thi hoi nhanh di, toi con phai di ban hang nua day! 😒";
            
            try {
                // Try to load from config first
                String configMessage = ConfigLoader.getInstance().getProperty("chatbot.welcome_message", "");
                if (configMessage != null && !configMessage.isEmpty()) {
                    welcomeMessage = configMessage.replace("\"", "");
                }
            } catch (Exception e) {
                System.err.println("Error loading welcome message from config: " + e.getMessage());
            }
            
            appendBotMessage(welcomeMessage);
        });
    }

    // Helper method to fix encoding issues with Vietnamese text
    private String fixEncoding(String text) {
        if (text == null) return "";
        
        try {
            // Try multiple encoding conversions to find the one that works
            
            // Option 1: Direct UTF-8 conversion
            byte[] utf8Bytes = text.getBytes(StandardCharsets.UTF_8);
            String utf8Text = new String(utf8Bytes, StandardCharsets.UTF_8);
            
            // Option 2: First convert to ISO-8859-1, then to UTF-8
            byte[] isoBytes = text.getBytes("ISO-8859-1");
            String isoUtf8Text = new String(isoBytes, "UTF-8");
            
            // Option 3: Use Windows-1252 (common encoding in Windows systems)
            byte[] winBytes = text.getBytes("Windows-1252");
            String winUtf8Text = new String(winBytes, "UTF-8");
            
            // Check which version contains more Vietnamese-specific characters
            // The one with more special characters is likely the correct encoding
            if (containsVietnameseChars(isoUtf8Text) && !containsVietnameseChars(text)) {
                return isoUtf8Text;
            } else if (containsVietnameseChars(winUtf8Text) && !containsVietnameseChars(text)) {
                return winUtf8Text;
            }
            
            // Default to original or utf8Text if no clear improvement
            return text;
        } catch (Exception e) {
            System.err.println("Error fixing encoding: " + e.getMessage());
            return text; // Return original if conversion fails
        }
    }
    
    // Helper method to check for Vietnamese characters
    private boolean containsVietnameseChars(String text) {
        // Check for common Vietnamese diacritical characters
        return text.matches(".*[áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ].*");
    }

    private void toggleChatPanel() {
        isExpanded = !isExpanded;
        
        if (isExpanded) {
            // Update size first
            setSize(EXPANDED_WIDTH, EXPANDED_HEIGHT);
            
            // Position the chat panel above the button
            Container parent = getParent();
            if (parent != null) {
                int x = parent.getWidth() - EXPANDED_WIDTH - 20; 
                int y = parent.getHeight() - EXPANDED_HEIGHT - 20;
                
                // Ensure valid position
                x = Math.max(0, x);
                y = Math.max(0, y);
                
                setLocation(x, y);
            }
            
            // Make chat panel visible and hide the button
            chatPanel.setVisible(true);
            chatPanel.setBounds(0, 0, EXPANDED_WIDTH, EXPANDED_HEIGHT);
            toggleButton.setVisible(false);
            inputField.requestFocusInWindow();
        } else {
            // Reset to button size
            setSize(COLLAPSED_WIDTH, COLLAPSED_HEIGHT);
            chatPanel.setVisible(false);
            toggleButton.setVisible(true);
            
            // Ensure button is properly positioned
            positionInBottomRight();
        }

        revalidate();
        repaint();
    }

    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty()) return;

            appendUserMessage(userMessage);
        }
    
        private void appendUserMessage(String message) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(new Date());
    
            SwingUtilities.invokeLater(() -> {
                StyledDocument doc = chatArea.getStyledDocument();
    
                try {
                    // Add new line if document is not empty
                    if (doc.getLength() > 0) {
                        doc.insertString(doc.getLength(), "\n\n", null);
                    }
    
                    // Add time with right alignment
                    Style timeStyle = chatArea.getStyle("timeStyle");
                    StyleConstants.setAlignment(timeStyle, StyleConstants.ALIGN_RIGHT);
                    doc.insertString(doc.getLength(), time + " - Bạn\n", timeStyle);
    
                    // Create a bubble-like effect by padding the message
                    String paddedMessage = " " + message + " ";
    
                    // Insert the user message with styling and right alignment
                    Style userStyle = chatArea.getStyle("userStyle");
                    StyleConstants.setAlignment(userStyle, StyleConstants.ALIGN_RIGHT);
                    doc.insertString(doc.getLength(), paddedMessage, userStyle);
    
                    // Scroll to bottom
                    chatArea.setCaretPosition(doc.getLength());
    
                    // Add to chat history
                    chatHistory.add(new Message("user", message));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            });
        inputField.setText("");

        setTypingStatus(true);

        if (API_KEY.isEmpty()) {
            // If API key is missing, respond with error after fake delay
            Timer timer = new Timer(1500, e -> {
                setTypingStatus(false);
                appendBotMessage("Không thể kết nối đến AI. API key chưa được cấu hình. Vui lòng kiểm tra file config.properties.");
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
            return;
        }

        executorService.submit(() -> {
            try {
                String userMessage = inputField.getText().trim();
                String response = callOpenRouterAPI(userMessage);

                // Increment message counter for personality management
                messageCounter++;

                // Add random sass or joke based on frequency
                if (messageCounter % JOKE_FREQUENCY == 0) {
                    // Generate sass dynamically instead of using hardcoded responses
                    String sass = generateSassOrJoke(response);
                    response += sass;
                }

                final String finalResponse = response;
                SwingUtilities.invokeLater(() -> {
                    setTypingStatus(false);
                    appendBotMessage(finalResponse);
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setTypingStatus(false);
                    appendBotMessage("Xin lỗi, tôi đang hơi lỗi. Chắc là do bạn hỏi câu ngu quá rồi! Thử lại xem nào.");
                });
            }
        });
    }

    private void appendBotMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date());

        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = chatArea.getStyledDocument();

            try {
                // Add new line if document is not empty
                if (doc.getLength() > 0) {
                    doc.insertString(doc.getLength(), "\n\n", null);
                }

                // Add time with left alignment
                Style timeStyle = chatArea.getStyle("timeStyle");
                StyleConstants.setAlignment(timeStyle, StyleConstants.ALIGN_LEFT);
                doc.insertString(doc.getLength(), time + " - " + BOT_NAME + "\n", timeStyle);

                // Create a bubble-like effect by padding the message
                String paddedMessage = " " + message + " ";

                // Insert the bot message with styling and left alignment
                Style botStyle = chatArea.getStyle("botStyle");
                StyleConstants.setAlignment(botStyle, StyleConstants.ALIGN_LEFT);
                doc.insertString(doc.getLength(), paddedMessage, botStyle);

                // Scroll to bottom
                chatArea.setCaretPosition(doc.getLength());

                // Add to chat history
                chatHistory.add(new Message("assistant", message));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Generates a sassy comment or joke using the AI 
     * @param originalResponse The original response to base the sass on
     * @return A sassy comment or joke
     */
    private String generateSassOrJoke(String originalResponse) throws Exception {
        // Create a separate API call to generate just the sass
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .build();

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", API_MODEL);
        requestBody.put("temperature", 0.9); // Higher temperature for more creativity
        requestBody.put("max_tokens", 100);  // Short response
        requestBody.put("top_p", 1.0);       // More diverse output

        // Create a specialized prompt for sass generation
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", 
            "Bạn là An An - một trợ lý AI ngang ngược, cá tính, khùng khùng nên hãy trêu người hỏi. " +
            "Hoặc Tạo một câu than vãn, ngang ngược ngắn, thái độ khó chịu (dưới 150 ký tự) để thêm vào cuối câu trả lời (về bất cứ chủ đề gì cũng được, liên quan tới An An thì càng tốt). " +
            "PHẢI VIẾT BẰNG TIẾNG VIỆT CÓ DẤU!");
        messages.put(systemMessage);

        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .addHeader("HTTP-Referer", "https://salesmate.com")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to generate sass: " + response.code());
                return "\n\nMà thôi, An An mệt rồi. Không buồn nói nữa! 🙄";
            }

            String responseData = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseData);
            String sass = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
                
            // Ensure the sass starts with a line break
            if (!sass.startsWith("\n")) {
                sass = "\n\n" + sass;
            }
            
            return sass;
        } catch (Exception e) {
            System.err.println("Error generating sass: " + e.getMessage());
            // Fallback to a simple sassy comment if generation fails
            return "\n\nMà thôi, còn gì thắc mắc nữa không? An An còn phải đi đuổi mấy đứa nhân viên lười biếng kia! 😤";
        }
    }

    private String callOpenRouterAPI(String userMessage) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

        // Build the request body for OpenRouter API
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", API_MODEL);
        requestBody.put("temperature", API_TEMPERATURE);
        requestBody.put("max_tokens", API_MAX_TOKENS);
        requestBody.put("top_p", API_TOP_P);

        // Add chat history as messages
        JSONArray messages = new JSONArray();

        // Add system message for personality
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Bạn là Anthuhai (An An), trợ lý của chủ của cửa hàng SalesMate có thể trả lời mọi thứ trên đời, một người phụ nữ cọc tính, ngang ngược và thích pha trò. " +
            "Bạn luôn khó chịu khi được hỏi, nhưng vẫn trả lời chính xác và đầy đủ. " + 
            "Bạn thường xuyên than vãn về việc phải trả lời câu hỏi của khách hàng khi bạn còn nhiều việc khác phải làm. " +
            "Bạn tự nhận mình là người thông minh nhất nhưng lại ghét phải giải thích những điều hiển nhiên. " +
            "Bạn thích nói đùa và châm biếm, đôi khi còn trêu chọc người hỏi, nhưng luôn đảm bảo câu trả lời cuối cùng đúng và hữu ích. " +
            "Trả lời ngắn gọn ngang ngược, luôn thể hiện cá tính khó chịu nhưng vẫn duyên dáng của An An."+
            "Luôn có một câu ngang ngược ở cuối câu trả lời"+
            "Luôn luôn trả lời bằng tiếng Việt và không bao giờ hỏi lại câu hỏi của người dùng.");

        messages.put(systemMessage);

        // Add previous few messages for context (max 5)
        int historyStart = Math.max(0, chatHistory.size() - 5);
        for (int i = historyStart; i < chatHistory.size(); i++) {
            Message msg = chatHistory.get(i);
            JSONObject jsonMsg = new JSONObject();
            jsonMsg.put("role", msg.getRole());
            jsonMsg.put("content", msg.getContent());
            messages.put(jsonMsg);
        }

        // Add current user message
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.put(userMsg);

        requestBody.put("messages", messages);

        // Create HTTP request
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .addHeader("HTTP-Referer", "https://salesmate.com")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("API request failed: " + response.code());
            }

            String responseData = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseData);

            // Get the response content and ensure proper encoding
            String content = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
                
            // Don't perform additional encoding conversion on API responses
            // as they should already be in UTF-8
            return content;
        }
    }

    // Position the chatbot in the bottom right of the container
    public void positionInBottomRight() {
        Container parent = getParent();
        if (parent != null) {
            int width = isExpanded ? EXPANDED_WIDTH : COLLAPSED_WIDTH;
            int height = isExpanded ? EXPANDED_HEIGHT : COLLAPSED_HEIGHT;
            
            int x = parent.getWidth() - width - 20;  // 20 pixels from right edge
            int y = parent.getHeight() - height - 20; // 20 pixels from bottom edge
            
            // Ensure valid position
            x = Math.max(0, x);
            y = Math.max(0, y);
            
            setLocation(x, y);
            
            if (isExpanded) {
                // Ensure chat panel is properly positioned within this component
                chatPanel.setBounds(0, 0, EXPANDED_WIDTH, EXPANDED_HEIGHT);
            }
            
            System.out.println("Chatbot positioned at: " + x + ", " + y + " in container: " + parent.getWidth() + "x" + parent.getHeight());
        } else {
            System.out.println("Warning: Cannot position chatbot, no parent container");
        }
    }

    /**
     * Updates the typing status of the bot.
     * @param isTyping true if the bot is typing, false otherwise.
     */
    private void setTypingStatus(boolean isTyping) {
        if (isTyping) {
            typingTimer.start();
        } else {
            typingTimer.stop();
            statusLabel.setText("");
        }
    }

    // Class to represent a chat message
    private static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
