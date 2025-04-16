package com.salesmate.component;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Toast extends JDialog {
    private static final int DISPLAY_TIME = 2000;
    private static Toast instance;
    private JPanel contentPane;
    private JLabel messageLabel;
    private Timer fadeTimer;
    private Timer spinnerTimer;
    private float opacity = 1.0f;
    private int spinnerAngle = 0;
    private boolean isLoading = false;

    private Toast(JFrame parent) {
        super(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(parent);
        
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                
                // Draw rounded rectangle background
                g2d.setColor(new Color(60, 60, 60, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw spinner if loading
                if (isLoading) {
                    int size = 20;
                    int x = 25; // Margin from left
                    int y = getHeight() / 2 - size / 2;
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new java.awt.BasicStroke(2));
                    
                    for (int i = 0; i < 12; i++) {
                        float scale = (float) ((12 - i) % 12) / 12.0f;
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scale * opacity));
                        
                        double angle = Math.toRadians(spinnerAngle + i * 30);
                        int x1 = x + size/2 + (int)(size/3 * Math.cos(angle));
                        int y1 = y + size/2 + (int)(size/3 * Math.sin(angle));
                        int x2 = x + size/2 + (int)(size/2 * Math.cos(angle));
                        int y2 = y + size/2 + (int)(size/2 * Math.sin(angle));
                        
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        contentPane.setLayout(new BorderLayout());
        contentPane.setOpaque(false);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 60, 15, 25));
        
        messageLabel = new JLabel();
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        contentPane.add(messageLabel, BorderLayout.CENTER);
        setContentPane(contentPane);
        
        spinnerTimer = new Timer(50, e -> {
            spinnerAngle = (spinnerAngle + 30) % 360;
            repaint();
        });
        
        fadeTimer = new Timer(50, e -> {
            opacity -= 0.1f;
            if (opacity <= 0) {
                dispose();
                ((Timer)e.getSource()).stop();
                if (spinnerTimer.isRunning()) {
                    spinnerTimer.stop();
                }
            }
            repaint();
        });
    }

    public static void showToast(JFrame parent, String message, String type) {
        if (instance != null && instance.isVisible()) {
            instance.dispose();
        }
        
        instance = new Toast(parent);
        instance.messageLabel.setText(message);
        instance.isLoading = type.equalsIgnoreCase("loading");
        
        // Set colors based on type
        Color bgColor;
        switch (type.toLowerCase()) {
            case "success":
                bgColor = new Color(46, 125, 50, 220);
                break;
            case "error":
                bgColor = new Color(198, 40, 40, 220);
                break;
            case "loading":
                bgColor = new Color(60, 60, 60, 220);
                break;
            default:
                bgColor = new Color(60, 60, 60, 220);
        }
        
        instance.contentPane.setBackground(bgColor);
        
        instance.pack();
        instance.setLocationRelativeTo(parent);
        instance.setVisible(true);
        
        if (instance.isLoading) {
            instance.spinnerTimer.start();
        } else {
            // Start fade out timer after display time for non-loading toasts
            Timer displayTimer = new Timer(DISPLAY_TIME, e -> {
                instance.fadeTimer.start();
                ((Timer)e.getSource()).stop();
            });
            displayTimer.setRepeats(false);
            displayTimer.start();
        }
    }
    
    public static void hideToast() {
        if (instance != null && instance.isVisible()) {
            instance.fadeTimer.start();
        }
    }
}
