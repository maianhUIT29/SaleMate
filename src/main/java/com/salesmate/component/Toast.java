package com.salesmate.component;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
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
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

                // Vẽ shadow
                float[] dist = {0.0f, 0.4f};
                Color[] colors = {new Color(0,0,0,50), new Color(0,0,0,0)};
                RadialGradientPaint gp = new RadialGradientPaint(
                    getWidth()/2.0f, getHeight()/2.0f, 
                    getWidth()/2.0f,
                    dist, colors
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(-5, -5, getWidth()+10, getHeight()+10, 25, 25);
                
                // Vẽ background chính với gradient
                GradientPaint gradient;
                if (isLoading) {
                    gradient = new GradientPaint(
                        0, 0, new Color(44, 62, 80),
                        0, getHeight(), new Color(52, 73, 94)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(46, 125, 50, 245),
                        0, getHeight(), new Color(27, 94, 32, 245)
                    );
                }
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Vẽ spinner nếu đang loading
                if (isLoading) {
                    int size = 24;
                    int x = 30;
                    int y = getHeight()/2 - size/2;
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
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
                } else {
                    // Vẽ icon check mark khi thành công
                    int size = 24;
                    int x = 30;
                    int y = getHeight()/2 - size/2;
                    
                    g2d.setColor(Color.WHITE); 
                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawPolyline(
                        new int[]{x, x + size/2, x + size},
                        new int[]{y + size/2, y + size, y},
                        3
                    );
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        contentPane.setLayout(new BorderLayout());
        contentPane.setOpaque(false);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 70, 20, 30));
        
        messageLabel = new JLabel();
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
        
        instance.pack();
        
        // Căn giữa toast theo frame cha
        int x = parent.getX() + (parent.getWidth() - instance.getWidth()) / 2;
        int y = parent.getY() + (parent.getHeight() - instance.getHeight()) / 2;
        instance.setLocation(x, y);
        
        instance.setVisible(true);
        
        if (instance.isLoading) {
            instance.spinnerTimer.start();
        } else {
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
