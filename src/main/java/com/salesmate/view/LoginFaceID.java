package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        webcam = WebcamConfig.configureWebcam();
        webcam.open();

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(new Dimension(400, 300));

        JDialog cameraDialog = new JDialog(this, "Chụp ảnh", true);
        cameraDialog.setLayout(new BorderLayout());
        cameraDialog.add(webcamPanel, BorderLayout.CENTER);
        cameraDialog.setSize(400, 350);
        cameraDialog.setLocationRelativeTo(this);

        JButton captureButton = new JButton("Chụp ảnh");
        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage capturedImage = webcam.getImage();
                try {
                    // Tạo thư mục img/login nếu chưa tồn tại
                    File outputDir = new File("/src/main/resources/img/login");
                    if (!outputDir.exists()) {
                        outputDir.mkdirs();
                    }
                    // Lưu ảnh chụp vào thư mục img/login
                    File outputfile = new File(outputDir, "captured.jpg");

                    // Ghi ảnh chụp vào file captured.jpg
                    ImageIO.write(capturedImage, "jpg", outputfile);
                    if (compareFace(outputfile)) {
                        JOptionPane.showMessageDialog(cameraDialog, "Đăng nhập thành công!");
                        dispose();
                        new CashierPanel(); // Mở trang quản lý nếu đăng nhập thành công
                    } else {
                        JOptionPane.showMessageDialog(cameraDialog, "Đăng nhập thất bại!");
                        new LoginForm(); // Reopen LoginForm when login fails
                        dispose();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                cameraDialog.dispose();
            }
        });

        JButton closeButton = new JButton("Tắt Webcam");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                webcam.close();
                cameraDialog.dispose();
                new LoginForm(); // Reopen LoginForm when webcam is closed
                dispose();
            }
        });

        JPanel panel = new JPanel();
        panel.add(captureButton);
        panel.add(closeButton);
        cameraDialog.add(panel, BorderLayout.SOUTH);

        cameraDialog.setVisible(true);
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
