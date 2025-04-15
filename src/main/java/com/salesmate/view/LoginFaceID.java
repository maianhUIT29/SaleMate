package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.salesmate.configs.OpenCVConfig;
import com.salesmate.configs.WebcamConfig;
import com.salesmate.controller.UserController;
import com.salesmate.model.User;
import com.salesmate.utils.SessionManager;

public class LoginFaceID extends JFrame {

    private UserController userController;
    private Webcam webcam;

    public LoginFaceID() {
        userController = new UserController();
        setTitle("🔑 Đăng nhập bằng FaceID - SalesMate");
        setSize(800, 600); // Tăng kích thước cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        webcam = WebcamConfig.configureWebcam();
        webcam.open();

        // Panel chính với gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                Color color1 = new Color(224, 255, 255);
                Color color2 = new Color(173, 216, 230);
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Title với icon
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP BẰNG FACE ID");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Vui lòng nhìn thẳng vào camera");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        // Camera panel với viền và padding
        JPanel cameraContainer = new JPanel(new BorderLayout());
        cameraContainer.setOpaque(false);
        cameraContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(new Dimension(640, 480));
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setMirrored(true);
        cameraContainer.add(webcamPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Styled buttons
        JButton captureButton = createStyledButton("Chụp ảnh", new Color(0, 123, 255));
        JButton backButton = createStyledButton("Quay lại", new Color(108, 117, 125));

        // Add action listeners
        captureButton.addActionListener(e -> {
            try {
                BufferedImage capturedImage = webcam.getImage();
                File outputDir = new File("src/main/resources/img/login");
                outputDir.mkdirs();
                File outputfile = new File(outputDir, "captured.jpg");
                ImageIO.write(capturedImage, "jpg", outputfile);
                
                if (compareFace(outputfile)) {
                    showSuccessDialog();
                } else {
                    showErrorDialog();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                showErrorDialog();
            }
        });

        backButton.addActionListener(e -> {
            webcam.close();
            dispose();
            new LoginForm().setVisible(true);
        });

        buttonPanel.add(captureButton);
        buttonPanel.add(backButton);

        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cameraContainer, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 45));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void showSuccessDialog() {
        JOptionPane.showOptionDialog(this,
            "Đăng nhập thành công!",
            "Thành công",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"OK"},
            "OK");
        dispose();
        new CashierPanel().setVisible(true);
    }

    private void showErrorDialog() {
        JOptionPane.showOptionDialog(this,
            "Không thể xác thực khuôn mặt. Vui lòng thử lại!",
            "Lỗi",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new Object[]{"OK"},
            "OK");
        dispose();
        new LoginForm().setVisible(true);
    }

    // So sánh ảnh chụp với ảnh trong thư mục faceid
    private boolean compareFace(File capturedFile) {
        Mat capturedImage = OpenCVConfig.loadImage(capturedFile.getAbsolutePath());
        Rect[] capturedFaces = OpenCVConfig.detectFaces(capturedImage);

        // Lấy danh sách các file ảnh trong thư mục faceid
        File faceIdDir = new File(getClass().getResource("C/src/main/resources/img/faceid").getFile());
        File[] faceIdFiles = faceIdDir.listFiles();

        if (faceIdFiles != null) {
            for (File faceIdFile : faceIdFiles) {
                Mat faceIdImage = OpenCVConfig.loadImage(faceIdFile.getAbsolutePath());
                Rect[] faceIdFaces = OpenCVConfig.detectFaces(faceIdImage);

                if (capturedFaces.length > 0 && faceIdFaces.length > 0) {
                    Mat capturedFace = new Mat(capturedImage, capturedFaces[0]);
                    Mat faceIdFace = new Mat(faceIdImage, faceIdFaces[0]);

                    // Resize faces to the same size for comparison
                    Imgproc.resize(capturedFace, capturedFace, new Size(100, 100));
                    Imgproc.resize(faceIdFace, faceIdFace, new Size(100, 100));

                    MatOfByte capturedFaceBytes = new MatOfByte();
                    MatOfByte faceIdFaceBytes = new MatOfByte();

                    Imgcodecs.imencode(".jpg", capturedFace, capturedFaceBytes);
                    Imgcodecs.imencode(".jpg", faceIdFace, faceIdFaceBytes);

                    if (Arrays.equals(capturedFaceBytes.toArray(), faceIdFaceBytes.toArray())) {
                        String avatarName = faceIdFile.getName();
                        User user = userController.getUserByAvatar(avatarName);
                        if (user != null) {
                            SessionManager.getInstance().setLoggedInUser(user);
                            return true; // Đăng nhập thành công nếu khuôn mặt khớp
                        }
                    }
                }
            }
        }
        return false; // Nếu không có khuôn mặt khớp
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFaceID::new);
    }
}
